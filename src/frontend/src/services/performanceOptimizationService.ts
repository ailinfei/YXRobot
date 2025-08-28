import { ref, nextTick } from 'vue'

export interface PerformanceMetrics {
  renderTime: number
  interactionDelay: number
  memoryUsage: number
  bundleSize: number
  networkLatency: number
  fps: number
}

export interface OptimizationConfig {
  enableVirtualScrolling: boolean
  enableLazyLoading: boolean
  enableImageOptimization: boolean
  enableCaching: boolean
  maxCacheSize: number
  debounceDelay: number
  throttleLimit: number
}

export class PerformanceOptimizationService {
  private metrics = ref<PerformanceMetrics>({
    renderTime: 0,
    interactionDelay: 0,
    memoryUsage: 0,
    bundleSize: 0,
    networkLatency: 0,
    fps: 60
  })

  private config: OptimizationConfig = {
    enableVirtualScrolling: true,
    enableLazyLoading: true,
    enableImageOptimization: true,
    enableCaching: true,
    maxCacheSize: 50 * 1024 * 1024, // 50MB
    debounceDelay: 300,
    throttleLimit: 100
  }

  private cache = new Map<string, any>()
  private imageCache = new Map<string, HTMLImageElement>()
  private performanceObserver?: PerformanceObserver

  constructor() {
    this.initializePerformanceMonitoring()
  }

  /**
   * 初始化性能监控
   */
  private initializePerformanceMonitoring(): void {
    // 监控渲染性能
    if ('PerformanceObserver' in window) {
      this.performanceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach((entry) => {
          if (entry.entryType === 'measure') {
            this.metrics.value.renderTime = entry.duration
          }
          if (entry.entryType === 'navigation') {
            const navEntry = entry as PerformanceNavigationTiming
            this.metrics.value.networkLatency = navEntry.responseEnd - navEntry.requestStart
          }
        })
      })

      this.performanceObserver.observe({ entryTypes: ['measure', 'navigation'] })
    }

    // 监控内存使用
    this.monitorMemoryUsage()

    // 监控FPS
    this.monitorFPS()
  }

  /**
   * 监控内存使用
   */
  private monitorMemoryUsage(): void {
    if ('memory' in performance) {
      const updateMemoryUsage = () => {
        const memory = (performance as any).memory
        this.metrics.value.memoryUsage = (memory.usedJSHeapSize / memory.jsHeapSizeLimit) * 100
        
        // 内存使用过高时清理缓存
        if (this.metrics.value.memoryUsage > 80) {
          this.clearCache()
        }
      }

      setInterval(updateMemoryUsage, 5000)
      updateMemoryUsage()
    }
  }

  /**
   * 监控FPS
   */
  private monitorFPS(): void {
    let lastTime = performance.now()
    let frames = 0

    const measureFPS = (currentTime: number) => {
      frames++
      
      if (currentTime - lastTime >= 1000) {
        this.metrics.value.fps = Math.round((frames * 1000) / (currentTime - lastTime))
        frames = 0
        lastTime = currentTime
      }
      
      requestAnimationFrame(measureFPS)
    }

    requestAnimationFrame(measureFPS)
  }

  /**
   * 测量组件渲染时间
   */
  measureRenderTime<T>(componentName: string, renderFn: () => T): T {
    const startTime = performance.now()
    
    performance.mark(`${componentName}-render-start`)
    
    const result = renderFn()
    
    nextTick(() => {
      performance.mark(`${componentName}-render-end`)
      performance.measure(
        `${componentName}-render`,
        `${componentName}-render-start`,
        `${componentName}-render-end`
      )
      
      const endTime = performance.now()
      this.metrics.value.renderTime = endTime - startTime
    })
    
    return result
  }

  /**
   * 防抖函数
   */
  debounce<T extends (...args: any[]) => any>(
    func: T,
    delay?: number
  ): (...args: Parameters<T>) => void {
    let timeoutId: NodeJS.Timeout
    const actualDelay = delay || this.config.debounceDelay
    
    return (...args: Parameters<T>) => {
      clearTimeout(timeoutId)
      timeoutId = setTimeout(() => func(...args), actualDelay)
    }
  }

  /**
   * 节流函数
   */
  throttle<T extends (...args: any[]) => any>(
    func: T,
    limit?: number
  ): (...args: Parameters<T>) => void {
    let inThrottle: boolean
    const actualLimit = limit || this.config.throttleLimit
    
    return (...args: Parameters<T>) => {
      if (!inThrottle) {
        func(...args)
        inThrottle = true
        setTimeout(() => inThrottle = false, actualLimit)
      }
    }
  }

  /**
   * 缓存管理
   */
  setCache(key: string, value: any, ttl?: number): void {
    if (!this.config.enableCaching) return

    const item = {
      value,
      timestamp: Date.now(),
      ttl: ttl || 300000 // 默认5分钟
    }

    this.cache.set(key, item)
    this.cleanupCache()
  }

  getCache(key: string): any {
    if (!this.config.enableCaching) return null

    const item = this.cache.get(key)
    if (!item) return null

    // 检查是否过期
    if (Date.now() - item.timestamp > item.ttl) {
      this.cache.delete(key)
      return null
    }

    return item.value
  }

  /**
   * 清理过期缓存
   */
  private cleanupCache(): void {
    const now = Date.now()
    let totalSize = 0

    for (const [key, item] of this.cache.entries()) {
      // 删除过期项
      if (now - item.timestamp > item.ttl) {
        this.cache.delete(key)
        continue
      }

      // 估算大小
      totalSize += JSON.stringify(item).length
    }

    // 如果缓存过大，删除最旧的项
    if (totalSize > this.config.maxCacheSize) {
      const entries = Array.from(this.cache.entries())
      entries.sort((a, b) => a[1].timestamp - b[1].timestamp)
      
      const toDelete = Math.ceil(entries.length * 0.3) // 删除30%最旧的项
      for (let i = 0; i < toDelete; i++) {
        this.cache.delete(entries[i][0])
      }
    }
  }

  /**
   * 清空缓存
   */
  clearCache(): void {
    this.cache.clear()
    this.imageCache.clear()
  }

  /**
   * 图片懒加载
   */
  lazyLoadImage(src: string, placeholder?: string): Promise<HTMLImageElement> {
    if (!this.config.enableLazyLoading) {
      return this.loadImage(src)
    }

    // 检查缓存
    if (this.imageCache.has(src)) {
      return Promise.resolve(this.imageCache.get(src)!)
    }

    return new Promise((resolve, reject) => {
      const img = new Image()
      
      img.onload = () => {
        this.imageCache.set(src, img)
        resolve(img)
      }
      
      img.onerror = reject
      
      // 使用Intersection Observer实现懒加载
      if ('IntersectionObserver' in window) {
        const observer = new IntersectionObserver((entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              img.src = src
              observer.disconnect()
            }
          })
        })
        
        // 创建临时元素用于观察
        const tempElement = document.createElement('div')
        observer.observe(tempElement)
      } else {
        // 降级处理
        img.src = src
      }
    })
  }

  /**
   * 普通图片加载
   */
  private loadImage(src: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(img)
      img.onerror = reject
      img.src = src
    })
  }

  /**
   * 虚拟滚动实现
   */
  createVirtualScroller<T>(
    items: T[],
    itemHeight: number,
    containerHeight: number,
    renderItem: (item: T, index: number) => any
  ) {
    if (!this.config.enableVirtualScrolling) {
      return {
        visibleItems: items,
        scrollTop: 0,
        totalHeight: items.length * itemHeight
      }
    }

    const visibleCount = Math.ceil(containerHeight / itemHeight)
    const buffer = Math.ceil(visibleCount / 2)

    return {
      getVisibleItems: (scrollTop: number) => {
        const startIndex = Math.max(0, Math.floor(scrollTop / itemHeight) - buffer)
        const endIndex = Math.min(items.length, startIndex + visibleCount + buffer * 2)
        
        return {
          items: items.slice(startIndex, endIndex),
          startIndex,
          offsetY: startIndex * itemHeight
        }
      },
      totalHeight: items.length * itemHeight,
      itemHeight
    }
  }

  /**
   * 批量DOM操作优化
   */
  batchDOMUpdates(updates: (() => void)[]): Promise<void> {
    return new Promise((resolve) => {
      // 使用requestAnimationFrame批量执行DOM更新
      requestAnimationFrame(() => {
        updates.forEach(update => update())
        resolve()
      })
    })
  }

  /**
   * 预加载资源
   */
  preloadResources(urls: string[]): Promise<void[]> {
    const promises = urls.map(url => {
      if (url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
        return this.lazyLoadImage(url)
      } else {
        return fetch(url).then(response => response.blob())
      }
    })

    return Promise.all(promises)
  }

  /**
   * 获取性能指标
   */
  getMetrics(): PerformanceMetrics {
    return { ...this.metrics.value }
  }

  /**
   * 获取性能建议
   */
  getPerformanceRecommendations(): string[] {
    const recommendations: string[] = []
    const metrics = this.metrics.value

    if (metrics.renderTime > 16) {
      recommendations.push('渲染时间过长，考虑优化组件结构或使用虚拟滚动')
    }

    if (metrics.memoryUsage > 70) {
      recommendations.push('内存使用率较高，建议清理缓存或优化数据结构')
    }

    if (metrics.fps < 30) {
      recommendations.push('帧率较低，检查是否有性能密集型操作')
    }

    if (metrics.networkLatency > 1000) {
      recommendations.push('网络延迟较高，考虑使用CDN或优化资源大小')
    }

    if (recommendations.length === 0) {
      recommendations.push('性能表现良好')
    }

    return recommendations
  }

  /**
   * 更新配置
   */
  updateConfig(newConfig: Partial<OptimizationConfig>): void {
    this.config = { ...this.config, ...newConfig }
  }

  /**
   * 销毁服务
   */
  destroy(): void {
    if (this.performanceObserver) {
      this.performanceObserver.disconnect()
    }
    this.clearCache()
  }
}

// 创建全局实例
export const performanceOptimizer = new PerformanceOptimizationService()