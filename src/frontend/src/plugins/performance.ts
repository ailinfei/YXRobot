/**
 * 性能优化插件初始化
 */

import { App } from 'vue'
import { 
  initializePerformanceMonitoring,
  performanceMonitor,
  taskScheduler,
  lazyImageLoader,
  resourcePreloader
} from '@/utils/performance'
import { 
  initializeCacheCleanup,
  memoryCache,
  requestCache,
  localStorageCache
} from '@/utils/cache'
import { 
  initializeSmartPreloading,
  smartPreloader
} from '@/utils/lazyLoading'
import { PERFORMANCE_CONFIG } from '@/config/performance'

/**
 * 性能优化插件
 */
export default {
  install(app: App) {
    // 1. 初始化性能监控
    if (PERFORMANCE_CONFIG.performanceMonitor.enabled) {
      initializePerformanceMonitoring()
      console.log('✅ Performance monitoring initialized')
    }

    // 2. 初始化缓存系统
    initializeCacheCleanup()
    console.log('✅ Cache system initialized')

    // 3. 初始化智能预加载
    if (PERFORMANCE_CONFIG.lazyLoading.route.prefetchOnHover || 
        PERFORMANCE_CONFIG.lazyLoading.route.prefetchOnVisible) {
      initializeSmartPreloading()
      console.log('✅ Smart preloading initialized')
    }

    // 4. 预加载关键资源
    if (PERFORMANCE_CONFIG.preload.critical.length > 0) {
      preloadCriticalResources()
    }

    // 5. 设置内存监控
    if (PERFORMANCE_CONFIG.memoryOptimization.monitoring.enabled) {
      setupMemoryMonitoring()
    }

    // 6. 注册全局性能工具
    app.config.globalProperties.$performance = {
      monitor: performanceMonitor,
      cache: {
        memory: memoryCache,
        request: requestCache,
        localStorage: localStorageCache
      },
      scheduler: taskScheduler,
      imageLoader: lazyImageLoader,
      preloader: resourcePreloader,
      smartPreloader
    }

    // 7. 添加性能指令
    addPerformanceDirectives(app)

    // 8. 设置错误边界
    setupErrorBoundary(app)

    console.log('🚀 Performance optimization plugin installed')
  }
}

/**
 * 预加载关键资源
 */
async function preloadCriticalResources() {
  const { critical } = PERFORMANCE_CONFIG.preload
  
  try {
    // 预加载CSS
    const cssResources = critical.filter(url => url.endsWith('.css'))
    await Promise.all(cssResources.map(url => resourcePreloader.preloadCSS(url)))
    
    // 预加载图片
    const imageResources = critical.filter(url => 
      /\.(jpg|jpeg|png|gif|svg|webp)$/i.test(url)
    )
    await resourcePreloader.preloadImages(imageResources)
    
    console.log('✅ Critical resources preloaded')
  } catch (error) {
    console.warn('⚠️ Failed to preload some critical resources:', error)
  }
}

/**
 * 设置内存监控
 */
function setupMemoryMonitoring() {
  const { threshold, interval } = PERFORMANCE_CONFIG.memoryOptimization.monitoring
  
  setInterval(() => {
    if ('memory' in performance) {
      const memory = (performance as any).memory
      const usedMB = memory.usedJSHeapSize / 1024 / 1024
      
      if (usedMB > threshold) {
        console.warn(`⚠️ High memory usage detected: ${usedMB.toFixed(2)}MB`)
        
        // 触发垃圾回收建议
        if ('gc' in window) {
          (window as any).gc()
        }
        
        // 清理缓存
        memoryCache.clear()
        localStorageCache.cleanup()
      }
    }
  }, interval)
}

/**
 * 添加性能相关指令
 */
function addPerformanceDirectives(app: App) {
  // v-lazy 图片懒加载指令
  app.directive('lazy', {
    mounted(el: HTMLImageElement, binding) {
      if (binding.value) {
        el.dataset.src = binding.value
        el.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMSIgaGVpZ2h0PSIxIiB2aWV3Qm94PSIwIDAgMSAxIiBmaWxsPSJub25lIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxyZWN0IHdpZHRoPSIxIiBoZWlnaHQ9IjEiIGZpbGw9IiNGNUY1RjUiLz48L3N2Zz4='
        lazyImageLoader.observe(el)
      }
    },
    unmounted(el: HTMLImageElement) {
      lazyImageLoader.unobserve(el)
    }
  })

  // v-preload 预加载指令
  app.directive('preload', {
    mounted(el: HTMLElement, binding) {
      if (binding.value) {
        el.dataset.preload = binding.value
      }
    }
  })

  // v-debounce 防抖指令
  app.directive('debounce', {
    mounted(el: HTMLElement, binding) {
      const { value, arg } = binding
      const delay = parseInt(arg || '300')
      
      let timer: NodeJS.Timeout
      const handler = (event: Event) => {
        clearTimeout(timer)
        timer = setTimeout(() => {
          value(event)
        }, delay)
      }
      
      el.addEventListener('input', handler)
      el._debounceHandler = handler
    },
    unmounted(el: HTMLElement & { _debounceHandler?: Function }) {
      if (el._debounceHandler) {
        el.removeEventListener('input', el._debounceHandler as EventListener)
        delete el._debounceHandler
      }
    }
  })

  // v-throttle 节流指令
  app.directive('throttle', {
    mounted(el: HTMLElement, binding) {
      const { value, arg } = binding
      const delay = parseInt(arg || '100')
      
      let timer: NodeJS.Timeout | null = null
      const handler = (event: Event) => {
        if (!timer) {
          timer = setTimeout(() => {
            value(event)
            timer = null
          }, delay)
        }
      }
      
      el.addEventListener('scroll', handler)
      el._throttleHandler = handler
    },
    unmounted(el: HTMLElement & { _throttleHandler?: Function }) {
      if (el._throttleHandler) {
        el.removeEventListener('scroll', el._throttleHandler as EventListener)
        delete el._throttleHandler
      }
    }
  })
}

/**
 * 设置错误边界
 */
function setupErrorBoundary(app: App) {
  app.config.errorHandler = (error, instance, info) => {
    console.error('Vue Error:', error)
    console.error('Component:', instance)
    console.error('Info:', info)
    
    // 记录性能影响
    performanceMonitor.mark('error-occurred')
    
    // 发送错误报告（可选）
    if (process.env.NODE_ENV === 'production') {
      // sendErrorReport(error, instance, info)
    }
  }

  // 捕获未处理的Promise错误
  window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled Promise Rejection:', event.reason)
    performanceMonitor.mark('promise-rejection')
  })

  // 捕获资源加载错误
  window.addEventListener('error', (event) => {
    if (event.target !== window) {
      console.error('Resource Load Error:', event.target)
      performanceMonitor.mark('resource-error')
    }
  }, true)
}

/**
 * 组件级性能优化混入
 */
export const performanceMixin = {
  created() {
    // 组件创建时标记
    const componentName = this.$options.name || 'Anonymous'
    performanceMonitor.mark(`${componentName}-created`)
  },
  
  mounted() {
    // 组件挂载时标记
    const componentName = this.$options.name || 'Anonymous'
    performanceMonitor.mark(`${componentName}-mounted`)
    
    // 测量组件加载时间
    const duration = performanceMonitor.measure(
      `${componentName}-load-time`,
      `${componentName}-created`,
      `${componentName}-mounted`
    )
    
    if (duration > 100) { // 超过100ms的组件加载时间
      console.warn(`⚠️ Slow component loading: ${componentName} took ${duration.toFixed(2)}ms`)
    }
  },
  
  beforeUnmount() {
    // 组件卸载前清理
    const componentName = this.$options.name || 'Anonymous'
    
    // 清理相关缓存
    if (this._cacheKeys) {
      this._cacheKeys.forEach((key: string) => {
        memoryCache.delete(key)
      })
    }
    
    // 清理定时器
    if (this._timers) {
      this._timers.forEach((timer: NodeJS.Timeout) => {
        clearTimeout(timer)
        clearInterval(timer)
      })
    }
    
    performanceMonitor.mark(`${componentName}-unmounted`)
  }
}

/**
 * 性能优化工具函数
 */
export const performanceUtils = {
  /**
   * 测量函数执行时间
   */
  measureFunction<T extends (...args: any[]) => any>(
    fn: T,
    name?: string
  ): T {
    return ((...args: any[]) => {
      const markName = name || fn.name || 'anonymous-function'
      const startMark = `${markName}-start`
      const endMark = `${markName}-end`
      
      performanceMonitor.mark(startMark)
      
      try {
        const result = fn(...args)
        
        if (result instanceof Promise) {
          return result.finally(() => {
            performanceMonitor.mark(endMark)
            performanceMonitor.measure(markName, startMark, endMark)
          })
        } else {
          performanceMonitor.mark(endMark)
          performanceMonitor.measure(markName, startMark, endMark)
          return result
        }
      } catch (error) {
        performanceMonitor.mark(endMark)
        performanceMonitor.measure(markName, startMark, endMark)
        throw error
      }
    }) as T
  },

  /**
   * 批量执行任务
   */
  batchExecute(tasks: Array<() => void>, batchSize = 5) {
    taskScheduler.addTasks(tasks)
  },

  /**
   * 延迟执行
   */
  defer(callback: () => void, delay = 0) {
    if (delay === 0) {
      if ('requestIdleCallback' in window) {
        requestIdleCallback(callback)
      } else {
        setTimeout(callback, 1)
      }
    } else {
      setTimeout(callback, delay)
    }
  },

  /**
   * 获取性能报告
   */
  getPerformanceReport() {
    return {
      metrics: performanceMonitor.getMetrics(),
      cacheStats: {
        memory: memoryCache.getStats(),
        localStorage: localStorageCache
      },
      memory: 'memory' in performance ? (performance as any).memory : null
    }
  }
}