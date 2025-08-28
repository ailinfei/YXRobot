/**
 * WebSocket工具类
 * 用于实现设备状态实时监控
 */

import { ElMessage } from 'element-plus'

// WebSocket消息类型
export interface WebSocketMessage {
  type: string
  data: any
  timestamp: number
}

// 设备状态更新消息
export interface DeviceStatusMessage extends WebSocketMessage {
  type: 'device_status_update'
  data: {
    deviceId: string
    serialNumber: string
    status: 'active' | 'offline' | 'maintenance' | 'retired'
    lastOnlineAt: string
    healthScore: number
    usageStats?: {
      totalUsageHours: number
      dailyAverageUsage: number
      lastUsedAt: string
      coursesCompleted: number
      charactersWritten: number
      learningProgress: number
    }
  }
}

// WebSocket连接状态
export type WebSocketStatus = 'connecting' | 'connected' | 'disconnected' | 'error'

// 事件监听器类型
export type EventListener = (data: any) => void

// WebSocket配置
export interface WebSocketConfig {
  url: string
  protocols?: string[]
  reconnectInterval?: number
  maxReconnectAttempts?: number
  heartbeatInterval?: number
  showConnectionMessages?: boolean
}

/**
 * WebSocket管理类
 */
export class WebSocketManager {
  private ws: WebSocket | null = null
  private config: Required<WebSocketConfig>
  private status: WebSocketStatus = 'disconnected'
  private reconnectAttempts = 0
  private reconnectTimer: NodeJS.Timeout | null = null
  private heartbeatTimer: NodeJS.Timeout | null = null
  private eventListeners = new Map<string, Set<EventListener>>()
  private statusListeners = new Set<(status: WebSocketStatus) => void>()

  constructor(config: WebSocketConfig) {
    this.config = {
      protocols: [],
      reconnectInterval: 5000,
      maxReconnectAttempts: 10,
      heartbeatInterval: 30000,
      showConnectionMessages: true,
      ...config
    }
  }

  /**
   * 连接WebSocket
   */
  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        resolve()
        return
      }

      this.setStatus('connecting')
      
      try {
        this.ws = new WebSocket(this.config.url, this.config.protocols)
        
        this.ws.onopen = () => {
          this.setStatus('connected')
          this.reconnectAttempts = 0
          this.startHeartbeat()
          
          if (this.config.showConnectionMessages) {
            ElMessage.success('设备监控连接成功')
          }
          
          resolve()
        }

        this.ws.onmessage = (event) => {
          this.handleMessage(event)
        }

        this.ws.onclose = (event) => {
          this.setStatus('disconnected')
          this.stopHeartbeat()
          
          if (this.config.showConnectionMessages && !event.wasClean) {
            ElMessage.warning('设备监控连接断开')
          }
          
          // 自动重连
          if (this.reconnectAttempts < this.config.maxReconnectAttempts) {
            this.scheduleReconnect()
          }
        }

        this.ws.onerror = (error) => {
          this.setStatus('error')
          console.error('WebSocket错误:', error)
          
          if (this.config.showConnectionMessages) {
            ElMessage.error('设备监控连接失败')
          }
          
          reject(error)
        }
      } catch (error) {
        this.setStatus('error')
        reject(error)
      }
    })
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    
    this.stopHeartbeat()
    
    if (this.ws) {
      this.ws.close(1000, '主动断开连接')
      this.ws = null
    }
    
    this.setStatus('disconnected')
  }

  /**
   * 发送消息
   */
  send(message: WebSocketMessage): boolean {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket未连接，无法发送消息')
      return false
    }

    try {
      this.ws.send(JSON.stringify(message))
      return true
    } catch (error) {
      console.error('发送WebSocket消息失败:', error)
      return false
    }
  }

  /**
   * 订阅设备状态更新
   */
  subscribeDeviceStatus(deviceIds: string[]): boolean {
    return this.send({
      type: 'subscribe_device_status',
      data: { deviceIds },
      timestamp: Date.now()
    })
  }

  /**
   * 取消订阅设备状态
   */
  unsubscribeDeviceStatus(deviceIds: string[]): boolean {
    return this.send({
      type: 'unsubscribe_device_status',
      data: { deviceIds },
      timestamp: Date.now()
    })
  }

  /**
   * 添加事件监听器
   */
  addEventListener(event: string, listener: EventListener): void {
    if (!this.eventListeners.has(event)) {
      this.eventListeners.set(event, new Set())
    }
    this.eventListeners.get(event)!.add(listener)
  }

  /**
   * 移除事件监听器
   */
  removeEventListener(event: string, listener: EventListener): void {
    const listeners = this.eventListeners.get(event)
    if (listeners) {
      listeners.delete(listener)
      if (listeners.size === 0) {
        this.eventListeners.delete(event)
      }
    }
  }

  /**
   * 添加状态监听器
   */
  addStatusListener(listener: (status: WebSocketStatus) => void): void {
    this.statusListeners.add(listener)
  }

  /**
   * 移除状态监听器
   */
  removeStatusListener(listener: (status: WebSocketStatus) => void): void {
    this.statusListeners.delete(listener)
  }

  /**
   * 获取当前状态
   */
  getStatus(): WebSocketStatus {
    return this.status
  }

  /**
   * 处理接收到的消息
   */
  private handleMessage(event: MessageEvent): void {
    try {
      const message: WebSocketMessage = JSON.parse(event.data)
      
      // 处理心跳响应
      if (message.type === 'pong') {
        return
      }
      
      // 触发对应的事件监听器
      const listeners = this.eventListeners.get(message.type)
      if (listeners) {
        listeners.forEach(listener => {
          try {
            listener(message.data)
          } catch (error) {
            console.error('事件监听器执行错误:', error)
          }
        })
      }
      
      // 触发通用消息监听器
      const allListeners = this.eventListeners.get('*')
      if (allListeners) {
        allListeners.forEach(listener => {
          try {
            listener(message)
          } catch (error) {
            console.error('通用事件监听器执行错误:', error)
          }
        })
      }
    } catch (error) {
      console.error('解析WebSocket消息失败:', error)
    }
  }

  /**
   * 设置连接状态
   */
  private setStatus(status: WebSocketStatus): void {
    if (this.status !== status) {
      this.status = status
      this.statusListeners.forEach(listener => {
        try {
          listener(status)
        } catch (error) {
          console.error('状态监听器执行错误:', error)
        }
      })
    }
  }

  /**
   * 安排重连
   */
  private scheduleReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
    }

    this.reconnectAttempts++
    const delay = Math.min(
      this.config.reconnectInterval * Math.pow(2, this.reconnectAttempts - 1),
      30000 // 最大30秒
    )

    this.reconnectTimer = setTimeout(() => {
      console.log(`尝试重连WebSocket (${this.reconnectAttempts}/${this.config.maxReconnectAttempts})`)
      this.connect().catch(error => {
        console.error('重连失败:', error)
      })
    }, delay)
  }

  /**
   * 开始心跳
   */
  private startHeartbeat(): void {
    this.stopHeartbeat()
    
    this.heartbeatTimer = setInterval(() => {
      this.send({
        type: 'ping',
        data: {},
        timestamp: Date.now()
      })
    }, this.config.heartbeatInterval)
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
}

// 全局WebSocket实例
let globalWebSocketManager: WebSocketManager | null = null

/**
 * 获取全局WebSocket管理器
 */
export function getWebSocketManager(): WebSocketManager {
  if (!globalWebSocketManager) {
    const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws'
    globalWebSocketManager = new WebSocketManager({
      url: wsUrl,
      reconnectInterval: 5000,
      maxReconnectAttempts: 10,
      heartbeatInterval: 30000,
      showConnectionMessages: true
    })
  }
  return globalWebSocketManager
}

/**
 * 设备状态监控Composable
 */
export function useDeviceStatusMonitor() {
  const wsManager = getWebSocketManager()

  /**
   * 开始监控设备状态
   */
  const startMonitoring = async (deviceIds: string[]) => {
    try {
      await wsManager.connect()
      wsManager.subscribeDeviceStatus(deviceIds)
    } catch (error) {
      console.error('启动设备监控失败:', error)
      throw error
    }
  }

  /**
   * 停止监控设备状态
   */
  const stopMonitoring = (deviceIds: string[]) => {
    wsManager.unsubscribeDeviceStatus(deviceIds)
  }

  /**
   * 监听设备状态更新
   */
  const onDeviceStatusUpdate = (callback: (data: DeviceStatusMessage['data']) => void) => {
    wsManager.addEventListener('device_status_update', callback)
    return () => wsManager.removeEventListener('device_status_update', callback)
  }

  /**
   * 监听连接状态变化
   */
  const onStatusChange = (callback: (status: WebSocketStatus) => void) => {
    wsManager.addStatusListener(callback)
    return () => wsManager.removeStatusListener(callback)
  }

  /**
   * 获取连接状态
   */
  const getConnectionStatus = () => wsManager.getStatus()

  /**
   * 断开连接
   */
  const disconnect = () => wsManager.disconnect()

  return {
    startMonitoring,
    stopMonitoring,
    onDeviceStatusUpdate,
    onStatusChange,
    getConnectionStatus,
    disconnect
  }
}

export default WebSocketManager