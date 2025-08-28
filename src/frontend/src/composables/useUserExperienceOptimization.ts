import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'

/**
 * 用户体验优化 Composable
 * 提供界面响应速度优化、错误提示改进和操作快捷方式
 */
export function useUserExperienceOptimization() {
  // 响应式状态
  const isLoading = ref(false)
  const loadingText = ref('')
  const errorMessages = ref<string[]>([])
  const shortcuts = ref<Record<string, () => void>>({})

  // 性能监控
  const performanceMetrics = ref({
    renderTime: 0,
    interactionDelay: 0,
    memoryUsage: 0
  })

  // 计算属性
  const hasErrors = computed(() => errorMessages.value.length > 0)
  const isResponsive = computed(() => performanceMetrics.value.interactionDelay < 100)

  // 加载状态管理
  const startLoading = (text = '加载中...') => {
    isLoading.value = true
    loadingText.value = text
  }

  const stopLoading = () => {
    isLoading.value = false
    loadingText.value = ''
  }

  // 错误处理优化
  const showError = (message: string, details?: string) => {
    errorMessages.value.push(message)
    
    ElNotification({
      title: '操作失败',
      message: message,
      type: 'error',
      duration: 5000,
      showClose: true,
      onClick: () => {
        if (details) {
          ElMessage.info(details)
        }
      }
    })
  }

  const showSuccess = (message: string, description?: string) => {
    ElNotification({
      title: '操作成功',
      message: message,
      type: 'success',
      duration: 3000,
      showClose: true
    })

    if (description) {
      setTimeout(() => {
        ElMessage.success(description)
      }, 500)
    }
  }

  const showWarning = (message: string, action?: () => void) => {
    ElNotification({
      title: '注意',
      message: message,
      type: 'warning',
      duration: 4000,
      showClose: true,
      onClick: action
    })
  }

  const clearErrors = () => {
    errorMessages.value = []
  }

  // 快捷键管理
  const registerShortcut = (key: string, callback: () => void, description?: string) => {
    shortcuts.value[key] = callback
    
    // 显示快捷键提示
    if (description) {
      console.log(`快捷键已注册: ${key} - ${description}`)
    }
  }

  const unregisterShortcut = (key: string) => {
    delete shortcuts.value[key]
  }

  // 键盘事件处理
  const handleKeydown = (event: KeyboardEvent) => {
    const key = getKeyString(event)
    const shortcut = shortcuts.value[key]
    
    if (shortcut) {
      event.preventDefault()
      shortcut()
    }
  }

  // 获取按键字符串
  const getKeyString = (event: KeyboardEvent): string => {
    const parts: string[] = []
    
    if (event.ctrlKey) parts.push('ctrl')
    if (event.altKey) parts.push('alt')
    if (event.shiftKey) parts.push('shift')
    if (event.metaKey) parts.push('meta')
    
    parts.push(event.key.toLowerCase())
    
    return parts.join('+')
  }

  // 性能监控
  const measurePerformance = (operation: string, fn: () => Promise<void> | void) => {
    return async () => {
      const startTime = performance.now()
      
      try {
        await fn()
        
        const endTime = performance.now()
        const duration = endTime - startTime
        
        // 更新性能指标
        if (operation === 'render') {
          performanceMetrics.value.renderTime = duration
        } else if (operation === 'interaction') {
          performanceMetrics.value.interactionDelay = duration
        }
        
        // 性能警告
        if (duration > 1000) {
          showWarning(`${operation} 操作耗时较长 (${duration.toFixed(2)}ms)`)
        }
        
      } catch (error) {
        const endTime = performance.now()
        const duration = endTime - startTime
        
        showError(`${operation} 操作失败`, `耗时: ${duration.toFixed(2)}ms`)
        throw error
      }
    }
  }

  // 内存使用监控
  const monitorMemoryUsage = () => {
    if ('memory' in performance) {
      const memory = (performance as any).memory
      performanceMetrics.value.memoryUsage = memory.usedJSHeapSize / memory.jsHeapSizeLimit * 100
      
      if (performanceMetrics.value.memoryUsage > 80) {
        showWarning('内存使用率较高，建议刷新页面')
      }
    }
  }

  // 防抖函数
  const debounce = <T extends (...args: any[]) => any>(
    func: T,
    wait: number
  ): ((...args: Parameters<T>) => void) => {
    let timeout: NodeJS.Timeout
    
    return (...args: Parameters<T>) => {
      clearTimeout(timeout)
      timeout = setTimeout(() => func(...args), wait)
    }
  }

  // 节流函数
  const throttle = <T extends (...args: any[]) => any>(
    func: T,
    limit: number
  ): ((...args: Parameters<T>) => void) => {
    let inThrottle: boolean
    
    return (...args: Parameters<T>) => {
      if (!inThrottle) {
        func(...args)
        inThrottle = true
        setTimeout(() => inThrottle = false, limit)
      }
    }
  }

  // 智能重试机制
  const retryOperation = async <T>(
    operation: () => Promise<T>,
    maxRetries = 3,
    delay = 1000
  ): Promise<T> => {
    let lastError: Error
    
    for (let i = 0; i < maxRetries; i++) {
      try {
        return await operation()
      } catch (error) {
        lastError = error as Error
        
        if (i < maxRetries - 1) {
          showWarning(`操作失败，${delay / 1000}秒后重试 (${i + 1}/${maxRetries})`)
          await new Promise(resolve => setTimeout(resolve, delay))
          delay *= 2 // 指数退避
        }
      }
    }
    
    throw lastError!
  }

  // 用户引导提示
  const showTip = (message: string, target?: string) => {
    ElNotification({
      title: '提示',
      message: message,
      type: 'info',
      duration: 6000,
      showClose: true,
      customClass: target ? `tip-for-${target}` : undefined
    })
  }

  // 操作确认
  const confirmAction = (message: string, title = '确认操作'): Promise<boolean> => {
    return new Promise((resolve) => {
      ElNotification({
        title,
        message: `${message}\n点击确认继续，点击关闭取消`,
        type: 'warning',
        duration: 0,
        showClose: true,
        onClick: () => resolve(true),
        onClose: () => resolve(false)
      })
    })
  }

  // 生命周期管理
  onMounted(() => {
    // 注册全局键盘事件
    document.addEventListener('keydown', handleKeydown)
    
    // 注册常用快捷键
    registerShortcut('ctrl+s', () => {
      showTip('保存快捷键已触发')
    }, '保存')
    
    registerShortcut('ctrl+z', () => {
      showTip('撤销快捷键已触发')
    }, '撤销')
    
    registerShortcut('ctrl+y', () => {
      showTip('重做快捷键已触发')
    }, '重做')
    
    registerShortcut('escape', () => {
      clearErrors()
      showTip('已清除错误信息')
    }, '清除错误')
    
    // 开始性能监控
    const memoryMonitorInterval = setInterval(monitorMemoryUsage, 10000)
    
    // 清理函数
    onUnmounted(() => {
      clearInterval(memoryMonitorInterval)
    })
  })

  onUnmounted(() => {
    // 清理事件监听器
    document.removeEventListener('keydown', handleKeydown)
  })

  return {
    // 状态
    isLoading,
    loadingText,
    errorMessages,
    hasErrors,
    isResponsive,
    performanceMetrics,
    
    // 加载管理
    startLoading,
    stopLoading,
    
    // 消息提示
    showError,
    showSuccess,
    showWarning,
    showTip,
    clearErrors,
    
    // 快捷键
    registerShortcut,
    unregisterShortcut,
    
    // 性能优化
    measurePerformance,
    debounce,
    throttle,
    retryOperation,
    
    // 用户交互
    confirmAction
  }
}