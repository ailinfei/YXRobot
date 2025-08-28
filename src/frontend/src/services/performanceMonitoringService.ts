export interface PerformanceReport {
  timestamp: string
  metrics: {
    renderTime: number
    interactionDelay: number
    memoryUsage: number
    networkLatency: number
    fps: number
    bundleSize: number
  }
  recommendations: string[]
  issues: PerformanceIssue[]
}

export interface PerformanceIssue {
  type: 'memory' | 'render' | 'network' | 'interaction'
  severity: 'low' | 'medium' | 'high' | 'critical'
  message: string
  suggestion: string
  component?: string
}

export class PerformanceMonitoringService {
  private metrics = {
    renderTime: 0,
    interactionDelay: 0,
    memoryUsage: 0,
    networkLatency: 0,
    fps: 60,
    bundleSize: 0
  }

  private observers: PerformanceObserver[] = []
  private fpsCounter = { frames: 0, lastTime: 0 }
  private isMonitoring = false

  /**
   * 开始性能监控
   */
  startMonitoring(): void {
    if (this.isMonitoring) return

    this.isMonitoring = true
    this.initializeObservers()
    this.startFPSMonitoring()
    this.startMemoryMonitoring()
    
    console.log('性能监控已启动')
  }

  /**
   * 停止性能监控
   */
  stopMonitoring(): void {
    if (!this.isMonitoring) return

    this.isMonitoring = false
    this.observers.forEach(observer => observer.disconnect())
    this.observers = []
    
    console.log('性能监控已停止')
  }

  /**
   * 初始化性能观察器
   */
  private initializeObservers(): void {
    if (!('PerformanceObserver' in window)) return

    // 监控导航性能
    try {
      const navigationObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach((entry) => {
          if (entry.entryType === 'navigation') {
            const navEntry = entry as PerformanceNavigationTiming
            this.metrics.networkLatency = navEntry.responseEnd - navEntry.requestStart
          }
        })
      })
      navigationObserver.observe({ entryTypes: ['navigation'] })
      this.observers.push(navigationObserver)
    } catch (error) {
      console.warn('无法监控导航性能:', error)
    }

    // 监控资源加载性能
    try {
      const resourceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach((entry) => {
          if (entry.entryType === 'resource') {
            const resourceEntry = entry as PerformanceResourceTiming
            if (resourceEntry.name.includes('.js') || resourceEntry.name.includes('.css')) {
              this.metrics.bundleSize += resourceEntry.transferSize || 0
            }
          }
        })
      })
      resourceObserver.observe({ entryTypes: ['resource'] })
      this.observers.push(resourceObserver)
    } catch (error) {
      console.warn('无法监控资源性能:', error)
    }

    // 监控长任务
    try {
      const longTaskObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach((entry) => {
          if (entry.entryType === 'longtask') {
            console.warn(`检测到长任务: ${entry.duration}ms`)
          }
        })
      })
      longTaskObserver.observe({ entryTypes: ['longtask'] })
      this.observers.push(longTaskObserver)
    } catch (error) {
      console.warn('无法监控长任务:', error)
    }

    // 监控用户交互
    try {
      const interactionObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach((entry) => {
          if (entry.entryType === 'event') {
            this.metrics.interactionDelay = entry.duration
          }
        })
      })
      interactionObserver.observe({ entryTypes: ['event'] })
      this.observers.push(interactionObserver)
    } catch (error) {
      console.warn('无法监控用户交互:', error)
    }
  }

  /**
   * 开始FPS监控
   */
  private startFPSMonitoring(): void {
    const measureFPS = (currentTime: number) => {
      if (!this.isMonitoring) return

      this.fpsCounter.frames++
      
      if (currentTime - this.fpsCounter.lastTime >= 1000) {
        this.metrics.fps = Math.round(
          (this.fpsCounter.frames * 1000) / (currentTime - this.fpsCounter.lastTime)
        )
        this.fpsCounter.frames = 0
        this.fpsCounter.lastTime = currentTime
      }
      
      requestAnimationFrame(measureFPS)
    }

    requestAnimationFrame(measureFPS)
  }

  /**
   * 开始内存监控
   */
  private startMemoryMonitoring(): void {
    if (!('memory' in performance)) return

    const updateMemoryUsage = () => {
      if (!this.isMonitoring) return

      const memory = (performance as any).memory
      this.metrics.memoryUsage = (memory.usedJSHeapSize / memory.jsHeapSizeLimit) * 100
      
      setTimeout(updateMemoryUsage, 5000)
    }

    updateMemoryUsage()
  }

  /**
   * 测量组件渲染时间
   */
  measureRenderTime(componentName: string, renderFn: () => void): void {
    const startTime = performance.now()
    
    performance.mark(`${componentName}-render-start`)
    renderFn()
    performance.mark(`${componentName}-render-end`)
    
    try {
      performance.measure(
        `${componentName}-render`,
        `${componentName}-render-start`,
        `${componentName}-render-end`
      )
      
      const endTime = performance.now()
      this.metrics.renderTime = endTime - startTime
      
      // 如果渲染时间过长，发出警告
      if (this.metrics.renderTime > 16) {
        console.warn(`${componentName} 渲染时间过长: ${this.metrics.renderTime}ms`)
      }
    } catch (error) {
      console.warn('无法测量渲染时间:', error)
    }
  }

  /**
   * 测量用户交互延迟
   */
  measureInteractionDelay(interactionName: string, interactionFn: () => void): void {
    const startTime = performance.now()
    
    interactionFn()
    
    requestAnimationFrame(() => {
      const endTime = performance.now()
      this.metrics.interactionDelay = endTime - startTime
      
      if (this.metrics.interactionDelay > 100) {
        console.warn(`${interactionName} 交互延迟过高: ${this.metrics.interactionDelay}ms`)
      }
    })
  }

  /**
   * 获取当前性能指标
   */
  getCurrentMetrics() {
    return { ...this.metrics }
  }

  /**
   * 生成性能报告
   */
  generateReport(): PerformanceReport {
    const issues = this.analyzePerformanceIssues()
    const recommendations = this.generateRecommendations(issues)

    return {
      timestamp: new Date().toISOString(),
      metrics: { ...this.metrics },
      recommendations,
      issues
    }
  }

  /**
   * 分析性能问题
   */
  private analyzePerformanceIssues(): PerformanceIssue[] {
    const issues: PerformanceIssue[] = []

    // 内存使用分析
    if (this.metrics.memoryUsage > 80) {
      issues.push({
        type: 'memory',
        severity: 'high',
        message: `内存使用率过高: ${this.metrics.memoryUsage.toFixed(1)}%`,
        suggestion: '考虑清理缓存、优化数据结构或减少同时加载的数据量'
      })
    } else if (this.metrics.memoryUsage > 60) {
      issues.push({
        type: 'memory',
        severity: 'medium',
        message: `内存使用率较高: ${this.metrics.memoryUsage.toFixed(1)}%`,
        suggestion: '监控内存使用情况，考虑优化数据管理'
      })
    }

    // 渲染性能分析
    if (this.metrics.renderTime > 50) {
      issues.push({
        type: 'render',
        severity: 'high',
        message: `渲染时间过长: ${this.metrics.renderTime.toFixed(1)}ms`,
        suggestion: '优化组件结构、使用虚拟滚动或减少DOM操作'
      })
    } else if (this.metrics.renderTime > 16) {
      issues.push({
        type: 'render',
        severity: 'medium',
        message: `渲染时间较长: ${this.metrics.renderTime.toFixed(1)}ms`,
        suggestion: '考虑优化渲染逻辑或使用性能优化技术'
      })
    }

    // 网络性能分析
    if (this.metrics.networkLatency > 2000) {
      issues.push({
        type: 'network',
        severity: 'high',
        message: `网络延迟过高: ${this.metrics.networkLatency.toFixed(1)}ms`,
        suggestion: '检查网络连接、使用CDN或优化资源大小'
      })
    } else if (this.metrics.networkLatency > 1000) {
      issues.push({
        type: 'network',
        severity: 'medium',
        message: `网络延迟较高: ${this.metrics.networkLatency.toFixed(1)}ms`,
        suggestion: '考虑优化网络请求或使用缓存策略'
      })
    }

    // 交互性能分析
    if (this.metrics.interactionDelay > 200) {
      issues.push({
        type: 'interaction',
        severity: 'high',
        message: `交互延迟过高: ${this.metrics.interactionDelay.toFixed(1)}ms`,
        suggestion: '优化事件处理逻辑、使用防抖节流或异步处理'
      })
    } else if (this.metrics.interactionDelay > 100) {
      issues.push({
        type: 'interaction',
        severity: 'medium',
        message: `交互延迟较高: ${this.metrics.interactionDelay.toFixed(1)}ms`,
        suggestion: '考虑优化用户交互响应速度'
      })
    }

    // FPS分析
    if (this.metrics.fps < 30) {
      issues.push({
        type: 'render',
        severity: 'high',
        message: `帧率过低: ${this.metrics.fps}fps`,
        suggestion: '检查是否有性能密集型操作、优化动画或减少重绘'
      })
    } else if (this.metrics.fps < 50) {
      issues.push({
        type: 'render',
        severity: 'medium',
        message: `帧率较低: ${this.metrics.fps}fps`,
        suggestion: '考虑优化动画性能或减少不必要的渲染'
      })
    }

    return issues
  }

  /**
   * 生成性能优化建议
   */
  private generateRecommendations(issues: PerformanceIssue[]): string[] {
    const recommendations: string[] = []

    if (issues.length === 0) {
      recommendations.push('性能表现良好，继续保持')
      return recommendations
    }

    // 根据问题类型生成建议
    const issueTypes = new Set(issues.map(issue => issue.type))

    if (issueTypes.has('memory')) {
      recommendations.push('实施内存管理策略：定期清理缓存、优化数据结构、使用对象池')
    }

    if (issueTypes.has('render')) {
      recommendations.push('优化渲染性能：使用虚拟滚动、减少DOM操作、优化CSS选择器')
    }

    if (issueTypes.has('network')) {
      recommendations.push('改善网络性能：使用CDN、启用压缩、优化资源大小')
    }

    if (issueTypes.has('interaction')) {
      recommendations.push('提升交互响应：使用防抖节流、异步处理、优化事件处理')
    }

    // 根据严重程度添加通用建议
    const highSeverityIssues = issues.filter(issue => issue.severity === 'high')
    if (highSeverityIssues.length > 0) {
      recommendations.push('发现严重性能问题，建议立即优化')
    }

    const criticalIssues = issues.filter(issue => issue.severity === 'critical')
    if (criticalIssues.length > 0) {
      recommendations.push('发现关键性能问题，需要紧急处理')
    }

    return recommendations
  }

  /**
   * 导出性能数据
   */
  exportPerformanceData(): string {
    const report = this.generateReport()
    return JSON.stringify(report, null, 2)
  }

  /**
   * 重置性能指标
   */
  resetMetrics(): void {
    this.metrics = {
      renderTime: 0,
      interactionDelay: 0,
      memoryUsage: 0,
      networkLatency: 0,
      fps: 60,
      bundleSize: 0
    }
  }

  /**
   * 设置性能阈值警告
   */
  setPerformanceThresholds(thresholds: {
    renderTime?: number
    interactionDelay?: number
    memoryUsage?: number
    networkLatency?: number
    fps?: number
  }): void {
    // 这里可以设置自定义的性能阈值
    console.log('性能阈值已更新:', thresholds)
  }
}

// 创建全局实例
export const performanceMonitor = new PerformanceMonitoringService()

// 在开发环境中自动启动监控
if (import.meta.env.DEV) {
  performanceMonitor.startMonitoring()
}