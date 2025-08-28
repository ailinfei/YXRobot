/**
 * 性能监控和优化工具
 */

// 性能指标接口
interface PerformanceMetrics {
  loadTime: number
  domContentLoaded: number
  firstPaint: number
  firstContentfulPaint: number
  largestContentfulPaint: number
  firstInputDelay: number
  cumulativeLayoutShift: number
}

// 性能监控类
export class PerformanceMonitor {
  private metrics: Partial<PerformanceMetrics> = {}
  private observers: PerformanceObserver[] = []

  constructor() {
    this.initializeObservers()
  }

  /**
   * 初始化性能观察器
   */
  private initializeObservers() {
    // 观察导航时间
    if ('PerformanceObserver' in window) {
      // LCP观察器
      try {
        const lcpObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          const lastEntry = entries[entries.length - 1] as any
          this.metrics.largestContentfulPaint = lastEntry.startTime
        })
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] })
        this.observers.push(lcpObserver)
      } catch (error) {
        console.warn('LCP observer not supported:', error)
      }

      // FID观察器
      try {
        const fidObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          entries.forEach((entry: any) => {
            this.metrics.firstInputDelay = entry.processingStart - entry.startTime
          })
        })
        fidObserver.observe({ entryTypes: ['first-input'] })
        this.observers.push(fidObserver)
      } catch (error) {
        console.warn('FID observer not supported:', error)
      }

      // CLS观察器
      try {
        const clsObserver = new PerformanceObserver((list) => {
          let clsValue = 0
          const entries = list.getEntries()
          entries.forEach((entry: any) => {
            if (!entry.hadRecentInput) {
              clsValue += entry.value
            }
          })
          this.metrics.cumulativeLayoutShift = clsValue
        })
        clsObserver.observe({ entryTypes: ['layout-shift'] })
        this.observers.push(clsObserver)
      } catch (error) {
        console.warn('CLS observer not supported:', error)
      }
    }

    // 页面加载完成后收集基础指标
    if (document.readyState === 'complete') {
      this.collectBasicMetrics()
    } else {
      window.addEventListener('load', () => {
        this.collectBasicMetrics()
      })
    }
  }

  /**
   * 收集基础性能指标
   */
  private collectBasicMetrics() {
    const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming
    const paint = performance.getEntriesByType('paint')

    if (navigation) {
      this.metrics.loadTime = navigation.loadEventEnd - navigation.navigationStart
      this.metrics.domContentLoaded = navigation.domContentLoadedEventEnd - navigation.navigationStart
    }

    paint.forEach((entry) => {
      if (entry.name === 'first-paint') {
        this.metrics.firstPaint = entry.startTime
      } else if (entry.name === 'first-contentful-paint') {
        this.metrics.firstContentfulPaint = entry.startTime
      }
    })
  }

  /**
   * 获取性能指标
   */
  getMetrics(): Partial<PerformanceMetrics> {
    return { ...this.metrics }
  }

  /**
   * 记录自定义性能标记
   */
  mark(name: string) {
    performance.mark(name)
  }

  /**
   * 测量两个标记之间的时间
   */
  measure(name: string, startMark: string, endMark?: string) {
    if (endMark) {
      performance.measure(name, startMark, endMark)
    } else {
      performance.measure(name, startMark)
    }
    
    const measures = performance.getEntriesByName(name, 'measure')
    return measures[measures.length - 1]?.duration || 0
  }

  /**
   * 销毁观察器
   */
  destroy() {
    this.observers.forEach(observer => observer.disconnect())
    this.observers = []
  }
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number,
  immediate = false
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null

  return function executedFunction(...args: Parameters<T>) {
    const later = () => {
      timeout = null
      if (!immediate) func(...args)
    }

    const callNow = immediate && !timeout
    
    if (timeout) clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    
    if (callNow) func(...args)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle: boolean
  
  return function executedFunction(...args: Parameters<T>) {
    if (!inThrottle) {
      func(...args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

/**
 * 空闲时执行任务
 */
export function runWhenIdle(callback: () => void, timeout = 5000) {
  if ('requestIdleCallback' in window) {
    requestIdleCallback(callback, { timeout })
  } else {
    setTimeout(callback, 1)
  }
}

/**
 * 批量执行任务
 */
export class TaskScheduler {
  private tasks: Array<() => void> = []
  private isRunning = false

  /**
   * 添加任务
   */
  addTask(task: () => void) {
    this.tasks.push(task)
    if (!this.isRunning) {
      this.runTasks()
    }
  }

  /**
   * 批量添加任务
   */
  addTasks(tasks: Array<() => void>) {
    this.tasks.push(...tasks)
    if (!this.isRunning) {
      this.runTasks()
    }
  }

  /**
   * 执行任务队列
   */
  private runTasks() {
    if (this.tasks.length === 0) {
      this.isRunning = false
      return
    }

    this.isRunning = true
    
    runWhenIdle(() => {
      const batchSize = Math.min(5, this.tasks.length) // 每批最多执行5个任务
      const batch = this.tasks.splice(0, batchSize)
      
      batch.forEach(task => {
        try {
          task()
        } catch (error) {
          console.error('Task execution failed:', error)
        }
      })

      // 继续执行剩余任务
      this.runTasks()
    })
  }

  /**
   * 清空任务队列
   */
  clear() {
    this.tasks = []
    this.isRunning = false
  }
}

/**
 * 图片懒加载
 */
export class LazyImageLoader {
  private observer: IntersectionObserver | null = null
  private images = new Set<HTMLImageElement>()

  constructor(options: IntersectionObserverInit = {}) {
    if ('IntersectionObserver' in window) {
      this.observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const img = entry.target as HTMLImageElement
            this.loadImage(img)
          }
        })
      }, {
        rootMargin: '50px',
        ...options
      })
    }
  }

  /**
   * 观察图片
   */
  observe(img: HTMLImageElement) {
    if (this.observer) {
      this.images.add(img)
      this.observer.observe(img)
    } else {
      // 不支持IntersectionObserver时直接加载
      this.loadImage(img)
    }
  }

  /**
   * 停止观察图片
   */
  unobserve(img: HTMLImageElement) {
    if (this.observer) {
      this.observer.unobserve(img)
      this.images.delete(img)
    }
  }

  /**
   * 加载图片
   */
  private loadImage(img: HTMLImageElement) {
    const src = img.dataset.src
    if (src) {
      img.src = src
      img.removeAttribute('data-src')
      
      if (this.observer) {
        this.observer.unobserve(img)
        this.images.delete(img)
      }
    }
  }

  /**
   * 销毁观察器
   */
  destroy() {
    if (this.observer) {
      this.observer.disconnect()
      this.observer = null
    }
    this.images.clear()
  }
}

/**
 * 资源预加载
 */
export class ResourcePreloader {
  private loadedResources = new Set<string>()

  /**
   * 预加载图片
   */
  preloadImage(src: string): Promise<void> {
    if (this.loadedResources.has(src)) {
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => {
        this.loadedResources.add(src)
        resolve()
      }
      img.onerror = reject
      img.src = src
    })
  }

  /**
   * 预加载多个图片
   */
  preloadImages(srcs: string[]): Promise<void[]> {
    return Promise.all(srcs.map(src => this.preloadImage(src)))
  }

  /**
   * 预加载CSS
   */
  preloadCSS(href: string): Promise<void> {
    if (this.loadedResources.has(href)) {
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const link = document.createElement('link')
      link.rel = 'stylesheet'
      link.href = href
      link.onload = () => {
        this.loadedResources.add(href)
        resolve()
      }
      link.onerror = reject
      document.head.appendChild(link)
    })
  }

  /**
   * 预加载JavaScript
   */
  preloadJS(src: string): Promise<void> {
    if (this.loadedResources.has(src)) {
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = src
      script.onload = () => {
        this.loadedResources.add(src)
        resolve()
      }
      script.onerror = reject
      document.head.appendChild(script)
    })
  }
}

// 全局实例
export const performanceMonitor = new PerformanceMonitor()
export const taskScheduler = new TaskScheduler()
export const lazyImageLoader = new LazyImageLoader()
export const resourcePreloader = new ResourcePreloader()

// 性能优化装饰器
export function PerformanceTrack(name?: string) {
  return function (target: any, propertyName: string, descriptor: PropertyDescriptor) {
    const method = descriptor.value
    const trackName = name || `${target.constructor.name}.${propertyName}`

    descriptor.value = async function (...args: any[]) {
      const startMark = `${trackName}-start`
      const endMark = `${trackName}-end`
      
      performanceMonitor.mark(startMark)
      
      try {
        const result = await method.apply(this, args)
        performanceMonitor.mark(endMark)
        const duration = performanceMonitor.measure(trackName, startMark, endMark)
        
        console.log(`Performance: ${trackName} took ${duration.toFixed(2)}ms`)
        return result
      } catch (error) {
        performanceMonitor.mark(endMark)
        performanceMonitor.measure(trackName, startMark, endMark)
        throw error
      }
    }

    return descriptor
  }
}

// 初始化性能监控
export function initializePerformanceMonitoring() {
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      performanceMonitor.mark('page-visible')
    } else {
      performanceMonitor.mark('page-hidden')
    }
  })

  // 监听页面卸载
  window.addEventListener('beforeunload', () => {
    // 发送性能数据到服务器
    const metrics = performanceMonitor.getMetrics()
    console.log('Performance metrics:', metrics)
    
    // 这里可以发送到分析服务
    // navigator.sendBeacon('/api/analytics/performance', JSON.stringify(metrics))
  })
}