import { ref, reactive, onUnmounted } from 'vue'
import { useFontPackageProgress } from '@/services/fontPackageProgressService'
import type { 
  ProgressData, 
  CharacterProgressMap, 
  PerformanceMetrics, 
  AnomalyAlert, 
  SystemHealth,
  ProgressSnapshot
} from '@/types/fontPackage'

/**
 * 进度监控 Composable
 * 提供字体包训练进度的实时监控功能
 */
export function useProgressMonitoring() {
  // WebSocket进度服务
  const progressService = useFontPackageProgress()
  
  // 监控状态
  const isMonitoring = ref(false)
  const currentPackageId = ref<number | null>(null)
  const isConnected = ref(false)
  
  // 进度数据
  const overallProgress = reactive<ProgressData>({
    percentage: 0,
    currentPhase: {
      name: '准备中',
      description: '正在初始化训练环境',
      status: 'pending',
      progress: 0
    },
    estimatedTimeRemaining: 0,
    charactersCompleted: 0,
    charactersTotal: 0,
    currentCharacter: ''
  })
  
  const characterProgress = reactive<CharacterProgressMap>({})
  
  const performanceMetrics = reactive<PerformanceMetrics>({
    trainingSpeed: 0,
    memoryUsage: 0,
    gpuUtilization: 0,
    estimatedCompletion: ''
  })
  
  const anomalies = ref<AnomalyAlert[]>([])
  
  const healthStatus = reactive<SystemHealth>({
    status: 'healthy',
    issues: [],
    lastCheck: new Date().toISOString()
  })
  
  // 更新监听器
  const progressListeners = new Set<(data: ProgressData) => void>()
  const anomalyListeners = new Set<(anomaly: AnomalyAlert) => void>()
  const healthListeners = new Set<(health: SystemHealth) => void>()
  
  // WebSocket监听器清理函数
  const cleanupFunctions = new Set<() => void>()
  
  /**
   * 开始监控指定字体包的训练进度
   */
  const startMonitoring = async (packageId: number) => {
    if (isMonitoring.value && currentPackageId.value === packageId) {
      return
    }
    
    // 停止之前的监控
    if (isMonitoring.value) {
      await stopMonitoring()
    }
    
    currentPackageId.value = packageId
    
    // 重置数据
    resetProgressData()
    
    try {
      // 启动WebSocket监控
      await progressService.startMonitoring(packageId)
      
      // 设置WebSocket监听器
      setupWebSocketListeners(packageId)
      
      isMonitoring.value = true
      console.log(`开始监控字体包 ${packageId} 的训练进度`)
      
      // 如果WebSocket不可用，使用模拟数据
      if (!progressService.isConnected()) {
        console.warn('WebSocket连接不可用，使用模拟数据')
        startMockDataUpdates()
      }
    } catch (error) {
      console.error('启动WebSocket监控失败，使用模拟数据:', error)
      isMonitoring.value = true
      startMockDataUpdates()
    }
  }
  
  /**
   * 停止监控
   */
  const stopMonitoring = async () => {
    if (!isMonitoring.value) {
      return
    }
    
    const packageId = currentPackageId.value
    
    isMonitoring.value = false
    currentPackageId.value = null
    
    // 清理WebSocket监听器
    cleanupWebSocketListeners()
    
    // 停止WebSocket监控
    if (packageId) {
      try {
        await progressService.stopMonitoring(packageId)
      } catch (error) {
        console.error('停止WebSocket监控失败:', error)
      }
    }
    
    // 停止模拟数据更新
    stopMockDataUpdates()
    
    console.log('停止进度监控')
  }
  
  /**
   * 更新总体进度
   */
  const updateOverallProgress = (data: Partial<ProgressData>) => {
    Object.assign(overallProgress, data)
    
    // 通知监听器
    progressListeners.forEach(listener => {
      try {
        listener(overallProgress)
      } catch (error) {
        console.error('进度监听器执行错误:', error)
      }
    })
  }
  
  /**
   * 更新字符进度
   */
  const updateCharacterProgress = (character: string, progress: Partial<CharacterProgressMap[string]>) => {
    if (!characterProgress[character]) {
      characterProgress[character] = {
        status: 'pending',
        progress: 0,
        quality: 0,
        issues: []
      }
    }
    
    Object.assign(characterProgress[character], progress)
  }
  
  /**
   * 更新性能指标
   */
  const updatePerformanceMetrics = (metrics: Partial<PerformanceMetrics>) => {
    Object.assign(performanceMetrics, metrics)
  }
  
  /**
   * 添加异常告警
   */
  const addAnomaly = (anomaly: AnomalyAlert) => {
    anomalies.value.push(anomaly)
    
    // 更新健康状态
    updateHealthStatus()
    
    // 通知监听器
    anomalyListeners.forEach(listener => {
      try {
        listener(anomaly)
      } catch (error) {
        console.error('异常监听器执行错误:', error)
      }
    })
  }
  
  /**
   * 解决异常告警
   */
  const resolveAnomaly = (timestamp: string) => {
    const index = anomalies.value.findIndex(a => a.timestamp === timestamp)
    if (index > -1) {
      const resolved = anomalies.value.splice(index, 1)[0]
      resolved.resolved = true
      
      // 更新健康状态
      updateHealthStatus()
      
      return resolved
    }
    return null
  }
  
  /**
   * 更新系统健康状态
   */
  const updateHealthStatus = () => {
    const activeAnomalies = anomalies.value.filter(a => !a.resolved)
    const highSeverityAnomalies = activeAnomalies.filter(a => a.severity === 'high')
    
    if (highSeverityAnomalies.length > 0) {
      healthStatus.status = 'error'
      healthStatus.issues = highSeverityAnomalies.map(a => a.message)
    } else if (activeAnomalies.length > 0) {
      healthStatus.status = 'warning'
      healthStatus.issues = activeAnomalies.map(a => a.message)
    } else {
      healthStatus.status = 'healthy'
      healthStatus.issues = []
    }
    
    healthStatus.lastCheck = new Date().toISOString()
    
    // 通知监听器
    healthListeners.forEach(listener => {
      try {
        listener(healthStatus)
      } catch (error) {
        console.error('健康状态监听器执行错误:', error)
      }
    })
  }
  
  /**
   * 获取进度快照
   */
  const getProgressSnapshot = (): ProgressSnapshot => {
    return {
      timestamp: new Date().toISOString(),
      progress: { ...overallProgress },
      metrics: { ...performanceMetrics },
      health: { ...healthStatus }
    }
  }
  
  /**
   * 重置进度数据
   */
  const resetProgressData = () => {
    // 重置总体进度
    Object.assign(overallProgress, {
      percentage: 0,
      currentPhase: {
        name: '准备中',
        description: '正在初始化训练环境',
        status: 'pending',
        progress: 0
      },
      estimatedTimeRemaining: 0,
      charactersCompleted: 0,
      charactersTotal: 0,
      currentCharacter: ''
    })
    
    // 清空字符进度
    Object.keys(characterProgress).forEach(key => {
      delete characterProgress[key]
    })
    
    // 重置性能指标
    Object.assign(performanceMetrics, {
      trainingSpeed: 0,
      memoryUsage: 0,
      gpuUtilization: 0,
      estimatedCompletion: ''
    })
    
    // 清空异常告警
    anomalies.value = []
    
    // 重置健康状态
    Object.assign(healthStatus, {
      status: 'healthy',
      issues: [],
      lastCheck: new Date().toISOString()
    })
  }
  
  /**
   * 添加进度监听器
   */
  const onProgressUpdate = (listener: (data: ProgressData) => void) => {
    progressListeners.add(listener)
    return () => progressListeners.delete(listener)
  }
  
  /**
   * 添加异常监听器
   */
  const onAnomalyDetected = (listener: (anomaly: AnomalyAlert) => void) => {
    anomalyListeners.add(listener)
    return () => anomalyListeners.delete(listener)
  }
  
  /**
   * 添加健康状态监听器
   */
  const onHealthStatusChange = (listener: (health: SystemHealth) => void) => {
    healthListeners.add(listener)
    return () => healthListeners.delete(listener)
  }
  
  /**
   * 设置WebSocket监听器
   */
  const setupWebSocketListeners = (packageId: number) => {
    // 监听连接状态变化
    const connectionCleanup = progressService.onConnectionChange((connected) => {
      isConnected.value = connected
      if (!connected && isMonitoring.value) {
        console.warn('WebSocket连接断开，切换到模拟数据')
        startMockDataUpdates()
      } else if (connected && isMonitoring.value) {
        console.log('WebSocket连接恢复，停止模拟数据')
        stopMockDataUpdates()
      }
    })
    cleanupFunctions.add(connectionCleanup)
    
    // 监听进度更新
    const progressCleanup = progressService.onProgressUpdate(packageId, (data) => {
      updateOverallProgress(data)
    })
    cleanupFunctions.add(progressCleanup)
    
    // 监听字符进度
    const characterCleanup = progressService.onCharacterProgress(packageId, (character, progress) => {
      updateCharacterProgress(character, progress)
    })
    cleanupFunctions.add(characterCleanup)
    
    // 监听性能指标
    const performanceCleanup = progressService.onPerformanceMetrics(packageId, (metrics) => {
      updatePerformanceMetrics(metrics)
    })
    cleanupFunctions.add(performanceCleanup)
    
    // 监听异常告警
    const anomalyCleanup = progressService.onAnomalyAlert(packageId, (anomaly) => {
      addAnomaly(anomaly)
    })
    cleanupFunctions.add(anomalyCleanup)
    
    // 监听健康状态
    const healthCleanup = progressService.onHealthStatus(packageId, (health) => {
      Object.assign(healthStatus, health)
      
      // 通知监听器
      healthListeners.forEach(listener => {
        try {
          listener(healthStatus)
        } catch (error) {
          console.error('健康状态监听器执行错误:', error)
        }
      })
    })
    cleanupFunctions.add(healthCleanup)
    
    // 更新连接状态
    isConnected.value = progressService.isConnected()
  }
  
  /**
   * 清理WebSocket监听器
   */
  const cleanupWebSocketListeners = () => {
    cleanupFunctions.forEach(cleanup => cleanup())
    cleanupFunctions.clear()
  }
  
  // 模拟数据更新（实际应该通过WebSocket接收）
  let mockTimer: NodeJS.Timeout | null = null
  
  const startMockDataUpdates = () => {
    // 初始化模拟数据
    overallProgress.charactersTotal = 10
    
    const characters = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
    characters.forEach(char => {
      characterProgress[char] = {
        status: 'pending',
        progress: 0,
        quality: 0,
        issues: []
      }
    })
    
    mockTimer = setInterval(() => {
      // 更新总体进度
      if (overallProgress.percentage < 100) {
        const increment = Math.random() * 2
        updateOverallProgress({
          percentage: Math.min(100, overallProgress.percentage + increment),
          charactersCompleted: Math.floor(
            (overallProgress.percentage / 100) * overallProgress.charactersTotal
          ),
          currentCharacter: characters[Math.floor(Math.random() * characters.length)]
        })
        
        // 更新阶段
        if (overallProgress.percentage > 80) {
          updateOverallProgress({
            currentPhase: {
              name: '质量检查',
              description: '正在进行生成结果的质量评估',
              status: 'running',
              progress: (overallProgress.percentage - 80) * 5
            }
          })
        } else if (overallProgress.percentage > 20) {
          updateOverallProgress({
            currentPhase: {
              name: '模型训练',
              description: '正在训练字体生成模型',
              status: 'running',
              progress: ((overallProgress.percentage - 20) / 60) * 100
            }
          })
        } else {
          updateOverallProgress({
            currentPhase: {
              name: '数据预处理',
              description: '正在处理训练样本数据',
              status: 'running',
              progress: (overallProgress.percentage / 20) * 100
            }
          })
        }
      }
      
      // 更新字符进度
      characters.forEach(char => {
        if (Math.random() > 0.7) {
          const currentProgress = characterProgress[char].progress
          const newProgress = Math.min(100, currentProgress + Math.random() * 10)
          
          let status = characterProgress[char].status
          if (newProgress === 100) {
            status = 'completed'
          } else if (newProgress > 0) {
            status = 'training'
          }
          
          updateCharacterProgress(char, {
            progress: newProgress,
            status,
            quality: Math.random() * 100
          })
        }
      })
      
      // 更新性能指标
      const speed = 15 + Math.random() * 10
      const memory = 60 + Math.random() * 20
      const gpu = 80 + Math.random() * 15
      
      const remainingTime = Math.max(0, 
        (100 - overallProgress.percentage) * 60 / speed
      )
      
      const completionTime = new Date(Date.now() + remainingTime * 1000)
      
      updatePerformanceMetrics({
        trainingSpeed: speed,
        memoryUsage: memory,
        gpuUtilization: gpu,
        estimatedCompletion: completionTime.toLocaleString()
      })
      
      updateOverallProgress({
        estimatedTimeRemaining: remainingTime
      })
      
      // 随机生成异常告警
      if (Math.random() > 0.98 && anomalies.value.length < 3) {
        const anomalyTypes = ['performance', 'quality', 'error'] as const
        const severities = ['low', 'medium', 'high'] as const
        const messages = [
          '训练速度低于预期',
          '内存使用率过高',
          'GPU利用率异常',
          '字符质量检查失败',
          '模型收敛速度缓慢',
          '数据预处理出现警告'
        ]
        
        addAnomaly({
          type: anomalyTypes[Math.floor(Math.random() * anomalyTypes.length)],
          severity: severities[Math.floor(Math.random() * severities.length)],
          message: messages[Math.floor(Math.random() * messages.length)],
          timestamp: new Date().toISOString(),
          resolved: false
        })
      }
    }, 2000)
  }
  
  const stopMockDataUpdates = () => {
    if (mockTimer) {
      clearInterval(mockTimer)
      mockTimer = null
    }
  }
  
  // 清理资源
  onUnmounted(() => {
    stopMonitoring()
    cleanupWebSocketListeners()
    progressListeners.clear()
    anomalyListeners.clear()
    healthListeners.clear()
  })
  
  return {
    // 状态
    isMonitoring,
    currentPackageId,
    isConnected,
    overallProgress,
    characterProgress,
    performanceMetrics,
    anomalies,
    healthStatus,
    
    // 方法
    startMonitoring,
    stopMonitoring,
    updateOverallProgress,
    updateCharacterProgress,
    updatePerformanceMetrics,
    addAnomaly,
    resolveAnomaly,
    getProgressSnapshot,
    resetProgressData,
    
    // 事件监听
    onProgressUpdate,
    onAnomalyDetected,
    onHealthStatusChange
  }
}