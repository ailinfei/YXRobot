/**
 * 字体包训练进度监控服务
 * 基于WebSocket实现实时进度更新
 */

import { getWebSocketManager } from '@/utils/websocket'
import type { 
  ProgressData, 
  CharacterProgressMap, 
  PerformanceMetrics, 
  AnomalyAlert, 
  SystemHealth,
  ProgressSnapshot
} from '@/types/fontPackage'

// 进度监控消息类型
export interface ProgressMessage {
  type: 'progress_update' | 'character_progress' | 'performance_metrics' | 'anomaly_alert' | 'health_status'
  packageId: number
  data: any
  timestamp: string
}

// 进度更新消息
export interface ProgressUpdateMessage extends ProgressMessage {
  type: 'progress_update'
  data: ProgressData
}

// 字符进度消息
export interface CharacterProgressMessage extends ProgressMessage {
  type: 'character_progress'
  data: {
    character: string
    progress: CharacterProgressMap[string]
  }
}

// 性能指标消息
export interface PerformanceMetricsMessage extends ProgressMessage {
  type: 'performance_metrics'
  data: PerformanceMetrics
}

// 异常告警消息
export interface AnomalyAlertMessage extends ProgressMessage {
  type: 'anomaly_alert'
  data: AnomalyAlert
}

// 健康状态消息
export interface HealthStatusMessage extends ProgressMessage {
  type: 'health_status'
  data: SystemHealth
}

// 订阅配置
export interface SubscriptionConfig {
  packageId: number
  includeCharacterProgress?: boolean
  includePerformanceMetrics?: boolean
  includeAnomalyAlerts?: boolean
  includeHealthStatus?: boolean
  updateInterval?: number
}

/**
 * 字体包进度监控服务类
 */
export class FontPackageProgressService {
  private wsManager = getWebSocketManager()
  private subscriptions = new Map<number, SubscriptionConfig>()
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 1000
  private heartbeatInterval = 30000
  private heartbeatTimer: NodeJS.Timeout | null = null
  
  // 事件监听器
  private progressListeners = new Map<number, Set<(data: ProgressData) => void>>()
  private characterProgressListeners = new Map<number, Set<(character: string, progress: CharacterProgressMap[string]) => void>>()
  private performanceListeners = new Map<number, Set<(metrics: PerformanceMetrics) => void>>()
  private anomalyListeners = new Map<number, Set<(anomaly: AnomalyAlert) => void>>()
  private healthListeners = new Map<number, Set<(health: SystemHealth) => void>>()
  private connectionListeners = new Set<(connected: boolean) => void>()
  
  constructor() {
    this.setupWebSocketListeners()
  }
  
  /**
   * 设置WebSocket监听器
   */
  private setupWebSocketListeners(): void {
    // 监听连接状态变化
    this.wsManager.addStatusListener((status) => {
      const isConnected = status === 'connected'
      this.connectionListeners.forEach(listener => {
        try {
          listener(isConnected)
        } catch (error) {
          console.error('连接状态监听器执行错误:', error)
        }
      })
      
      if (isConnected) {
        this.reconnectAttempts = 0
        this.startHeartbeat()
        this.resubscribeAll()
      } else {
        this.stopHeartbeat()
        if (status === 'disconnected') {
          this.scheduleReconnect()
        }
      }
    })
    
    // 监听进度更新消息
    this.wsManager.addEventListener('font_package_progress', (message: ProgressMessage) => {
      this.handleProgressMessage(message)
    })
    
    // 监听心跳响应
    this.wsManager.addEventListener('progress_pong', () => {
      // 心跳响应，保持连接活跃
    })
  }
  
  /**
   * 处理进度消息
   */
  private handleProgressMessage(message: ProgressMessage): void {
    const { type, packageId, data } = message
    
    try {
      switch (type) {
        case 'progress_update':
          this.notifyProgressListeners(packageId, data as ProgressData)
          break
          
        case 'character_progress':
          const charData = data as CharacterProgressMessage['data']
          this.notifyCharacterProgressListeners(packageId, charData.character, charData.progress)
          break
          
        case 'performance_metrics':
          this.notifyPerformanceListeners(packageId, data as PerformanceMetrics)
          break
          
        case 'anomaly_alert':
          this.notifyAnomalyListeners(packageId, data as AnomalyAlert)
          break
          
        case 'health_status':
          this.notifyHealthListeners(packageId, data as SystemHealth)
          break
          
        default:
          console.warn('未知的进度消息类型:', type)
      }
    } catch (error) {
      console.error('处理进度消息失败:', error)
    }
  }
  
  /**
   * 订阅字体包进度更新
   */
  async subscribe(config: SubscriptionConfig): Promise<void> {
    try {
      // 确保WebSocket连接
      await this.ensureConnection()
      
      // 保存订阅配置
      this.subscriptions.set(config.packageId, config)
      
      // 发送订阅消息
      const success = this.wsManager.send({
        type: 'subscribe_font_package_progress',
        data: config,
        timestamp: Date.now()
      })
      
      if (!success) {
        throw new Error('发送订阅消息失败')
      }
      
      console.log(`已订阅字体包 ${config.packageId} 的进度更新`)
    } catch (error) {
      console.error('订阅进度更新失败:', error)
      throw error
    }
  }
  
  /**
   * 取消订阅字体包进度更新
   */
  async unsubscribe(packageId: number): Promise<void> {
    try {
      if (!this.subscriptions.has(packageId)) {
        return
      }
      
      // 发送取消订阅消息
      const success = this.wsManager.send({
        type: 'unsubscribe_font_package_progress',
        data: { packageId },
        timestamp: Date.now()
      })
      
      if (!success) {
        console.warn('发送取消订阅消息失败')
      }
      
      // 清理订阅配置和监听器
      this.subscriptions.delete(packageId)
      this.clearPackageListeners(packageId)
      
      console.log(`已取消订阅字体包 ${packageId} 的进度更新`)
    } catch (error) {
      console.error('取消订阅失败:', error)
      throw error
    }
  }
  
  /**
   * 确保WebSocket连接
   */
  private async ensureConnection(): Promise<void> {
    if (this.wsManager.getStatus() === 'connected') {
      return
    }
    
    try {
      await this.wsManager.connect()
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      throw error
    }
  }
  
  /**
   * 重新订阅所有包
   */
  private resubscribeAll(): void {
    this.subscriptions.forEach((config) => {
      this.wsManager.send({
        type: 'subscribe_font_package_progress',
        data: config,
        timestamp: Date.now()
      })
    })
  }
  
  /**
   * 安排重连
   */
  private scheduleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('达到最大重连次数，停止重连')
      return
    }
    
    this.reconnectAttempts++
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), 30000)
    
    setTimeout(async () => {
      try {
        console.log(`尝试重连WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
        await this.wsManager.connect()
      } catch (error) {
        console.error('重连失败:', error)
      }
    }, delay)
  }
  
  /**
   * 开始心跳
   */
  private startHeartbeat(): void {
    this.stopHeartbeat()
    
    this.heartbeatTimer = setInterval(() => {
      this.wsManager.send({
        type: 'progress_ping',
        data: {},
        timestamp: Date.now()
      })
    }, this.heartbeatInterval)
  }
  
  /**
   * 停止心跳
   */
  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }
  
  /**
   * 清理指定包的监听器
   */
  private clearPackageListeners(packageId: number): void {
    this.progressListeners.delete(packageId)
    this.characterProgressListeners.delete(packageId)
    this.performanceListeners.delete(packageId)
    this.anomalyListeners.delete(packageId)
    this.healthListeners.delete(packageId)
  }
  
  /**
   * 通知进度监听器
   */
  private notifyProgressListeners(packageId: number, data: ProgressData): void {
    const listeners = this.progressListeners.get(packageId)
    if (listeners) {
      listeners.forEach(listener => {
        try {
          listener(data)
        } catch (error) {
          console.error('进度监听器执行错误:', error)
        }
      })
    }
  }
  
  /**
   * 通知字符进度监听器
   */
  private notifyCharacterProgressListeners(packageId: number, character: string, progress: CharacterProgressMap[string]): void {
    const listeners = this.characterProgressListeners.get(packageId)
    if (listeners) {
      listeners.forEach(listener => {
        try {
          listener(character, progress)
        } catch (error) {
          console.error('字符进度监听器执行错误:', error)
        }
      })
    }
  }
  
  /**
   * 通知性能监听器
   */
  private notifyPerformanceListeners(packageId: number, metrics: PerformanceMetrics): void {
    const listeners = this.performanceListeners.get(packageId)
    if (listeners) {
      listeners.forEach(listener => {
        try {
          listener(metrics)
        } catch (error) {
          console.error('性能监听器执行错误:', error)
        }
      })
    }
  }
  
  /**
   * 通知异常监听器
   */
  private notifyAnomalyListeners(packageId: number, anomaly: AnomalyAlert): void {
    const listeners = this.anomalyListeners.get(packageId)
    if (listeners) {
      listeners.forEach(listener => {
        try {
          listener(anomaly)
        } catch (error) {
          console.error('异常监听器执行错误:', error)
        }
      })
    }
  }
  
  /**
   * 通知健康状态监听器
   */
  private notifyHealthListeners(packageId: number, health: SystemHealth): void {
    const listeners = this.healthListeners.get(packageId)
    if (listeners) {
      listeners.forEach(listener => {
        try {
          listener(health)
        } catch (error) {
          console.error('健康状态监听器执行错误:', error)
        }
      })
    }
  }
  
  // 公共监听器管理方法
  
  /**
   * 添加进度更新监听器
   */
  onProgressUpdate(packageId: number, listener: (data: ProgressData) => void): () => void {
    if (!this.progressListeners.has(packageId)) {
      this.progressListeners.set(packageId, new Set())
    }
    this.progressListeners.get(packageId)!.add(listener)
    
    return () => {
      const listeners = this.progressListeners.get(packageId)
      if (listeners) {
        listeners.delete(listener)
        if (listeners.size === 0) {
          this.progressListeners.delete(packageId)
        }
      }
    }
  }
  
  /**
   * 添加字符进度监听器
   */
  onCharacterProgress(packageId: number, listener: (character: string, progress: CharacterProgressMap[string]) => void): () => void {
    if (!this.characterProgressListeners.has(packageId)) {
      this.characterProgressListeners.set(packageId, new Set())
    }
    this.characterProgressListeners.get(packageId)!.add(listener)
    
    return () => {
      const listeners = this.characterProgressListeners.get(packageId)
      if (listeners) {
        listeners.delete(listener)
        if (listeners.size === 0) {
          this.characterProgressListeners.delete(packageId)
        }
      }
    }
  }
  
  /**
   * 添加性能指标监听器
   */
  onPerformanceMetrics(packageId: number, listener: (metrics: PerformanceMetrics) => void): () => void {
    if (!this.performanceListeners.has(packageId)) {
      this.performanceListeners.set(packageId, new Set())
    }
    this.performanceListeners.get(packageId)!.add(listener)
    
    return () => {
      const listeners = this.performanceListeners.get(packageId)
      if (listeners) {
        listeners.delete(listener)
        if (listeners.size === 0) {
          this.performanceListeners.delete(packageId)
        }
      }
    }
  }
  
  /**
   * 添加异常告警监听器
   */
  onAnomalyAlert(packageId: number, listener: (anomaly: AnomalyAlert) => void): () => void {
    if (!this.anomalyListeners.has(packageId)) {
      this.anomalyListeners.set(packageId, new Set())
    }
    this.anomalyListeners.get(packageId)!.add(listener)
    
    return () => {
      const listeners = this.anomalyListeners.get(packageId)
      if (listeners) {
        listeners.delete(listener)
        if (listeners.size === 0) {
          this.anomalyListeners.delete(packageId)
        }
      }
    }
  }
  
  /**
   * 添加健康状态监听器
   */
  onHealthStatus(packageId: number, listener: (health: SystemHealth) => void): () => void {
    if (!this.healthListeners.has(packageId)) {
      this.healthListeners.set(packageId, new Set())
    }
    this.healthListeners.get(packageId)!.add(listener)
    
    return () => {
      const listeners = this.healthListeners.get(packageId)
      if (listeners) {
        listeners.delete(listener)
        if (listeners.size === 0) {
          this.healthListeners.delete(packageId)
        }
      }
    }
  }
  
  /**
   * 添加连接状态监听器
   */
  onConnectionChange(listener: (connected: boolean) => void): () => void {
    this.connectionListeners.add(listener)
    return () => this.connectionListeners.delete(listener)
  }
  
  /**
   * 获取连接状态
   */
  isConnected(): boolean {
    return this.wsManager.getStatus() === 'connected'
  }
  
  /**
   * 获取订阅状态
   */
  isSubscribed(packageId: number): boolean {
    return this.subscriptions.has(packageId)
  }
  
  /**
   * 断开连接并清理资源
   */
  disconnect(): void {
    this.stopHeartbeat()
    this.subscriptions.clear()
    this.progressListeners.clear()
    this.characterProgressListeners.clear()
    this.performanceListeners.clear()
    this.anomalyListeners.clear()
    this.healthListeners.clear()
    this.connectionListeners.clear()
    this.wsManager.disconnect()
  }
}

// 全局服务实例
let globalProgressService: FontPackageProgressService | null = null

/**
 * 获取全局字体包进度服务实例
 */
export function getFontPackageProgressService(): FontPackageProgressService {
  if (!globalProgressService) {
    globalProgressService = new FontPackageProgressService()
  }
  return globalProgressService
}

/**
 * 字体包进度监控 Composable
 */
export function useFontPackageProgress(packageId?: number) {
  const progressService = getFontPackageProgressService()
  
  /**
   * 开始监控指定字体包
   */
  const startMonitoring = async (id: number, config?: Partial<SubscriptionConfig>) => {
    const subscriptionConfig: SubscriptionConfig = {
      packageId: id,
      includeCharacterProgress: true,
      includePerformanceMetrics: true,
      includeAnomalyAlerts: true,
      includeHealthStatus: true,
      updateInterval: 1000,
      ...config
    }
    
    try {
      await progressService.subscribe(subscriptionConfig)
    } catch (error) {
      console.error('启动进度监控失败:', error)
      throw error
    }
  }
  
  /**
   * 停止监控指定字体包
   */
  const stopMonitoring = async (id: number) => {
    try {
      await progressService.unsubscribe(id)
    } catch (error) {
      console.error('停止进度监控失败:', error)
      throw error
    }
  }
  
  /**
   * 监听进度更新
   */
  const onProgressUpdate = (id: number, callback: (data: ProgressData) => void) => {
    return progressService.onProgressUpdate(id, callback)
  }
  
  /**
   * 监听字符进度
   */
  const onCharacterProgress = (id: number, callback: (character: string, progress: CharacterProgressMap[string]) => void) => {
    return progressService.onCharacterProgress(id, callback)
  }
  
  /**
   * 监听性能指标
   */
  const onPerformanceMetrics = (id: number, callback: (metrics: PerformanceMetrics) => void) => {
    return progressService.onPerformanceMetrics(id, callback)
  }
  
  /**
   * 监听异常告警
   */
  const onAnomalyAlert = (id: number, callback: (anomaly: AnomalyAlert) => void) => {
    return progressService.onAnomalyAlert(id, callback)
  }
  
  /**
   * 监听健康状态
   */
  const onHealthStatus = (id: number, callback: (health: SystemHealth) => void) => {
    return progressService.onHealthStatus(id, callback)
  }
  
  /**
   * 监听连接状态
   */
  const onConnectionChange = (callback: (connected: boolean) => void) => {
    return progressService.onConnectionChange(callback)
  }
  
  /**
   * 获取连接状态
   */
  const isConnected = () => progressService.isConnected()
  
  /**
   * 获取订阅状态
   */
  const isSubscribed = (id: number) => progressService.isSubscribed(id)
  
  return {
    startMonitoring,
    stopMonitoring,
    onProgressUpdate,
    onCharacterProgress,
    onPerformanceMetrics,
    onAnomalyAlert,
    onHealthStatus,
    onConnectionChange,
    isConnected,
    isSubscribed
  }
}

export default FontPackageProgressService