import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { FontPackageProgressService, getFontPackageProgressService, useFontPackageProgress } from '@/services/fontPackageProgressService'
import type { ProgressData, AnomalyAlert } from '@/types/fontPackage'

// Mock WebSocket管理器
const mockWebSocketManager = {
  connect: vi.fn(),
  disconnect: vi.fn(),
  send: vi.fn(),
  getStatus: vi.fn(),
  addStatusListener: vi.fn(),
  addEventListener: vi.fn()
}

vi.mock('@/utils/websocket', () => ({
  getWebSocketManager: () => mockWebSocketManager
}))

describe('FontPackageProgressService', () => {
  let service: FontPackageProgressService
  
  beforeEach(() => {
    vi.clearAllMocks()
    service = new FontPackageProgressService()
    mockWebSocketManager.getStatus.mockReturnValue('disconnected')
  })
  
  afterEach(() => {
    service.disconnect()
  })
  
  describe('构造函数', () => {
    it('应该正确初始化服务', () => {
      expect(service).toBeInstanceOf(FontPackageProgressService)
      expect(mockWebSocketManager.addStatusListener).toHaveBeenCalled()
      expect(mockWebSocketManager.addEventListener).toHaveBeenCalledWith('font_package_progress', expect.any(Function))
    })
  })
  
  describe('订阅管理', () => {
    it('应该能够订阅字体包进度', async () => {
      mockWebSocketManager.connect.mockResolvedValue(undefined)
      mockWebSocketManager.getStatus.mockReturnValue('connected')
      mockWebSocketManager.send.mockReturnValue(true)
      
      const config = {
        packageId: 1,
        includeCharacterProgress: true,
        includePerformanceMetrics: true
      }
      
      await service.subscribe(config)
      
      expect(mockWebSocketManager.connect).toHaveBeenCalled()
      expect(mockWebSocketManager.send).toHaveBeenCalledWith({
        type: 'subscribe_font_package_progress',
        data: config,
        timestamp: expect.any(Number)
      })
      expect(service.isSubscribed(1)).toBe(true)
    })
    
    it('应该能够取消订阅字体包进度', async () => {
      // 先订阅
      mockWebSocketManager.connect.mockResolvedValue(undefined)
      mockWebSocketManager.getStatus.mockReturnValue('connected')
      mockWebSocketManager.send.mockReturnValue(true)
      
      await service.subscribe({ packageId: 1 })
      
      // 然后取消订阅
      await service.unsubscribe(1)
      
      expect(mockWebSocketManager.send).toHaveBeenCalledWith({
        type: 'unsubscribe_font_package_progress',
        data: { packageId: 1 },
        timestamp: expect.any(Number)
      })
      expect(service.isSubscribed(1)).toBe(false)
    })
    
    it('应该在WebSocket连接失败时抛出错误', async () => {
      mockWebSocketManager.connect.mockRejectedValue(new Error('连接失败'))
      
      await expect(service.subscribe({ packageId: 1 })).rejects.toThrow('连接失败')
    })
    
    it('应该在发送订阅消息失败时抛出错误', async () => {
      mockWebSocketManager.connect.mockResolvedValue(undefined)
      mockWebSocketManager.getStatus.mockReturnValue('connected')
      mockWebSocketManager.send.mockReturnValue(false)
      
      await expect(service.subscribe({ packageId: 1 })).rejects.toThrow('发送订阅消息失败')
    })
  })
  
  describe('连接管理', () => {
    it('应该正确报告连接状态', () => {
      mockWebSocketManager.getStatus.mockReturnValue('connected')
      expect(service.isConnected()).toBe(true)
      
      mockWebSocketManager.getStatus.mockReturnValue('disconnected')
      expect(service.isConnected()).toBe(false)
    })
    
    it('应该在连接状态变化时重新订阅', () => {
      const statusListener = mockWebSocketManager.addStatusListener.mock.calls[0][0]
      
      // 模拟订阅
      service['subscriptions'].set(1, { packageId: 1 })
      
      // 模拟连接恢复
      statusListener('connected')
      
      expect(mockWebSocketManager.send).toHaveBeenCalledWith({
        type: 'subscribe_font_package_progress',
        data: { packageId: 1 },
        timestamp: expect.any(Number)
      })
    })
  })
  
  describe('消息处理', () => {
    it('应该正确处理进度更新消息', () => {
      const progressListener = vi.fn()
      service.onProgressUpdate(1, progressListener)
      
      const progressMessage = {
        type: 'progress_update' as const,
        packageId: 1,
        data: {
          percentage: 50,
          currentPhase: {
            name: '训练中',
            description: '正在训练模型',
            status: 'running' as const,
            progress: 50
          },
          estimatedTimeRemaining: 300,
          charactersCompleted: 5,
          charactersTotal: 10,
          currentCharacter: '测'
        } as ProgressData,
        timestamp: new Date().toISOString()
      }
      
      // 模拟接收消息
      const messageHandler = mockWebSocketManager.addEventListener.mock.calls[0][1]
      messageHandler(progressMessage)
      
      expect(progressListener).toHaveBeenCalledWith(progressMessage.data)
    })
    
    it('应该正确处理异常告警消息', () => {
      const anomalyListener = vi.fn()
      service.onAnomalyAlert(1, anomalyListener)
      
      const anomalyMessage = {
        type: 'anomaly_alert' as const,
        packageId: 1,
        data: {
          type: 'performance' as const,
          severity: 'medium' as const,
          message: '训练速度低于预期',
          timestamp: new Date().toISOString(),
          resolved: false
        } as AnomalyAlert,
        timestamp: new Date().toISOString()
      }
      
      // 模拟接收消息
      const messageHandler = mockWebSocketManager.addEventListener.mock.calls[0][1]
      messageHandler(anomalyMessage)
      
      expect(anomalyListener).toHaveBeenCalledWith(anomalyMessage.data)
    })
    
    it('应该忽略未知类型的消息', () => {
      const consoleSpy = vi.spyOn(console, 'warn').mockImplementation(() => {})
      
      const unknownMessage = {
        type: 'unknown_type',
        packageId: 1,
        data: {},
        timestamp: new Date().toISOString()
      }
      
      // 模拟接收消息
      const messageHandler = mockWebSocketManager.addEventListener.mock.calls[0][1]
      messageHandler(unknownMessage)
      
      expect(consoleSpy).toHaveBeenCalledWith('未知的进度消息类型:', 'unknown_type')
      
      consoleSpy.mockRestore()
    })
  })
  
  describe('监听器管理', () => {
    it('应该能够添加和移除进度监听器', () => {
      const listener1 = vi.fn()
      const listener2 = vi.fn()
      
      const remove1 = service.onProgressUpdate(1, listener1)
      const remove2 = service.onProgressUpdate(1, listener2)
      
      // 模拟进度更新
      service['notifyProgressListeners'](1, {} as ProgressData)
      
      expect(listener1).toHaveBeenCalled()
      expect(listener2).toHaveBeenCalled()
      
      // 移除一个监听器
      remove1()
      
      listener1.mockClear()
      listener2.mockClear()
      
      service['notifyProgressListeners'](1, {} as ProgressData)
      
      expect(listener1).not.toHaveBeenCalled()
      expect(listener2).toHaveBeenCalled()
      
      // 移除所有监听器
      remove2()
      
      listener2.mockClear()
      
      service['notifyProgressListeners'](1, {} as ProgressData)
      
      expect(listener2).not.toHaveBeenCalled()
    })
    
    it('应该能够添加连接状态监听器', () => {
      const connectionListener = vi.fn()
      
      const removeListener = service.onConnectionChange(connectionListener)
      
      // 模拟连接状态变化
      const statusListener = mockWebSocketManager.addStatusListener.mock.calls[0][0]
      statusListener('connected')
      
      expect(connectionListener).toHaveBeenCalledWith(true)
      
      // 移除监听器
      removeListener()
      
      connectionListener.mockClear()
      statusListener('disconnected')
      
      expect(connectionListener).not.toHaveBeenCalled()
    })
  })
  
  describe('错误处理', () => {
    it('应该处理监听器执行错误', () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      const faultyListener = vi.fn(() => {
        throw new Error('监听器错误')
      })
      
      service.onProgressUpdate(1, faultyListener)
      
      // 模拟进度更新
      service['notifyProgressListeners'](1, {} as ProgressData)
      
      expect(consoleSpy).toHaveBeenCalledWith('进度监听器执行错误:', expect.any(Error))
      
      consoleSpy.mockRestore()
    })
    
    it('应该处理消息处理错误', () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      const invalidMessage = {
        type: 'progress_update',
        packageId: 1,
        data: null, // 无效数据
        timestamp: new Date().toISOString()
      }
      
      // 模拟接收消息
      const messageHandler = mockWebSocketManager.addEventListener.mock.calls[0][1]
      messageHandler(invalidMessage)
      
      expect(consoleSpy).toHaveBeenCalledWith('处理进度消息失败:', expect.any(Error))
      
      consoleSpy.mockRestore()
    })
  })
  
  describe('资源清理', () => {
    it('应该在断开连接时清理所有资源', () => {
      // 添加一些订阅和监听器
      service['subscriptions'].set(1, { packageId: 1 })
      service.onProgressUpdate(1, vi.fn())
      service.onConnectionChange(vi.fn())
      
      service.disconnect()
      
      expect(service.isSubscribed(1)).toBe(false)
      expect(mockWebSocketManager.disconnect).toHaveBeenCalled()
    })
  })
})

describe('getFontPackageProgressService', () => {
  it('应该返回单例实例', () => {
    const service1 = getFontPackageProgressService()
    const service2 = getFontPackageProgressService()
    
    expect(service1).toBe(service2)
    expect(service1).toBeInstanceOf(FontPackageProgressService)
  })
})

describe('useFontPackageProgress', () => {
  let progressComposable: ReturnType<typeof useFontPackageProgress>
  
  beforeEach(() => {
    vi.clearAllMocks()
    progressComposable = useFontPackageProgress()
    mockWebSocketManager.connect.mockResolvedValue(undefined)
    mockWebSocketManager.getStatus.mockReturnValue('connected')
    mockWebSocketManager.send.mockReturnValue(true)
  })
  
  it('应该提供监控控制方法', () => {
    expect(progressComposable.startMonitoring).toBeInstanceOf(Function)
    expect(progressComposable.stopMonitoring).toBeInstanceOf(Function)
    expect(progressComposable.isConnected).toBeInstanceOf(Function)
    expect(progressComposable.isSubscribed).toBeInstanceOf(Function)
  })
  
  it('应该能够开始监控', async () => {
    await progressComposable.startMonitoring(1)
    
    expect(mockWebSocketManager.connect).toHaveBeenCalled()
    expect(mockWebSocketManager.send).toHaveBeenCalledWith({
      type: 'subscribe_font_package_progress',
      data: {
        packageId: 1,
        includeCharacterProgress: true,
        includePerformanceMetrics: true,
        includeAnomalyAlerts: true,
        includeHealthStatus: true,
        updateInterval: 1000
      },
      timestamp: expect.any(Number)
    })
  })
  
  it('应该能够停止监控', async () => {
    await progressComposable.startMonitoring(1)
    await progressComposable.stopMonitoring(1)
    
    expect(mockWebSocketManager.send).toHaveBeenCalledWith({
      type: 'unsubscribe_font_package_progress',
      data: { packageId: 1 },
      timestamp: expect.any(Number)
    })
  })
  
  it('应该提供事件监听方法', () => {
    expect(progressComposable.onProgressUpdate).toBeInstanceOf(Function)
    expect(progressComposable.onCharacterProgress).toBeInstanceOf(Function)
    expect(progressComposable.onPerformanceMetrics).toBeInstanceOf(Function)
    expect(progressComposable.onAnomalyAlert).toBeInstanceOf(Function)
    expect(progressComposable.onHealthStatus).toBeInstanceOf(Function)
    expect(progressComposable.onConnectionChange).toBeInstanceOf(Function)
  })
  
  it('应该能够自定义订阅配置', async () => {
    const customConfig = {
      includeCharacterProgress: false,
      updateInterval: 2000
    }
    
    await progressComposable.startMonitoring(1, customConfig)
    
    expect(mockWebSocketManager.send).toHaveBeenCalledWith({
      type: 'subscribe_font_package_progress',
      data: {
        packageId: 1,
        includeCharacterProgress: false,
        includePerformanceMetrics: true,
        includeAnomalyAlerts: true,
        includeHealthStatus: true,
        updateInterval: 2000
      },
      timestamp: expect.any(Number)
    })
  })
})