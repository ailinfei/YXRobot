/**
 * 前端性能监控服务
 * 监控API响应时间、页面加载性能、用户交互等
 * 任务24：性能优化和监控
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

// 性能指标接口
export interface PerformanceMetric {
  name: string
  value: number
  timestamp: number
  category: 'api' | 'page' | 'user' | 'resource'
  tags?: Record<string, string>
}

// API性能指标
export interface ApiPerformanceMetric extends PerformanceMetric {
  category: 'api'
  url: string
  method: string
  status: number
  responseTime: number
  requestSize?: number
  responseSize?: number
}

// 页面性能指标
export interface PagePerformanceMetric extends PerformanceMetric {
  category: 'page'
  route: string
  loadTime: number
  domContentLoaded: number
  firstContentfulPaint?: number
  largestContentfulPaint?: number
}

// 用户交互指标
export interface UserInteractionMetric extends PerformanceMetric {
  category: 'user'
  action: string
  element: string
  duration?: number
}

// 资源加载指标
export interface ResourcePerformanceMetric extends PerformanceMetric {
  category: 'resource'
  resourceType: string
  resourceUrl: string
  loadTime: number
  size?: number
}

/**
 * 性能监控服务类
 */
export class PerformanceMonitorService {
  private static instance: PerformanceMonitorService
  private metrics: PerformanceMetric[] = []
  private observers: PerformanceObserver[] = []
  private isEnabled = true
  private reportInterval = 30000 // 30秒上报一次
  private maxMetrics = 1000 // 最大缓存指标数量
  
  private constructor() {
    this.initializeObservers()
    this.startReporting()
  }
  
  static getInstance(): PerformanceMonitorService {
    if (!this.instance) {
      this.instance = new PerformanceMonitorService()
    }
    return this.instance
  }
  
  /**
   * 初始化性能观察器
   */
  private initializeObservers() {
    if (typeof window === 'undefined') return
    
    // 监听导航性能
    if ('PerformanceObserver' in window) {
      try {
        const navigationObserver = new PerformanceObserver((list) => {
          for (const entry of list.getEntries()) {
            if (entry.entryType === 'navigation') {
              this.recordPagePerformance(entry as PerformanceNavigationTiming)
            }
          }
        })
        navigationObserver.observe({ entryTypes: ['navigation'] })
        this.observers.push(navigationObserver)
      } catch (error) {
        console.warn('Navigation performance observer not supported:', error)
      }
      
      // 监听资源加载性能
      try {
        const resourceObserver = new PerformanceObserver((list) => {
          for (const entry of list.getEntries()) {
            if (entry.entryType === 'resource') {
              this.recordResourcePerformance(entry as PerformanceResourceTiming)
            }
          }
        })
        resourceObserver.observe({ entryTypes: ['resource'] })
        this.observers.push(resourceObserver)
      } catch (error) {
        console.warn('Resource performance observer not supported:', error)
      }
      
      // 监听LCP (Largest Contentful Paint)
      try {
        const lcpObserver = new PerformanceObserver((list) => {
          for (const entry of list.getEntries()) {
            this.recordMetric({
              name: 'largest_contentful_paint',
              value: entry.startTime,
              timestamp: Date.now(),
              category: 'page',
              tags: { route: window.location.pathname }
            })
          }
        })
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] })
        this.observers.push(lcpObserver)
      } catch (error) {
        console.warn('LCP observer not supported:', error)
      }
    }
  }
  
  /**
   * 记录API性能指标
   */
  recordApiPerformance(
    url: string,
    method: string,
    status: number,
    responseTime: number,
    requestSize?: number,
    responseSize?: number
  ) {
    if (!this.isEnabled) return
    
    const metric: ApiPerformanceMetric = {
      name: 'api_response_time',
      value: responseTime,
      timestamp: Date.now(),
      category: 'api',
      url,
      method,
      status,
      responseTime,
      requestSize,
      responseSize,
      tags: {
        endpoint: this.extractEndpoint(url),
        status_code: status.toString(),
        method: method.toUpperCase()
      }
    }
    
    this.recordMetric(metric)
    
    // 记录慢请求
    if (responseTime > 2000) {
      this.recordSlowRequest(metric)
    }
  }
  
  /**
   * 记录页面性能指标
   */
  private recordPagePerformance(entry: PerformanceNavigationTiming) {
    const metric: PagePerformanceMetric = {
      name: 'page_load_time',
      value: entry.loadEventEnd - entry.navigationStart,
      timestamp: Date.now(),
      category: 'page',
      route: window.location.pathname,
      loadTime: entry.loadEventEnd - entry.navigationStart,
      domContentLoaded: entry.domContentLoadedEventEnd - entry.navigationStart,
      tags: {
        route: window.location.pathname,
        navigation_type: this.getNavigationType(entry.type)
      }
    }
    
    this.recordMetric(metric)
  }
  
  /**
   * 记录资源加载性能
   */
  private recordResourcePerformance(entry: PerformanceResourceTiming) {
    // 只监控关键资源
    if (this.isKeyResource(entry.name)) {
      const metric: ResourcePerformanceMetric = {
        name: 'resource_load_time',
        value: entry.responseEnd - entry.startTime,
        timestamp: Date.now(),
        category: 'resource',
        resourceType: this.getResourceType(entry.name),
        resourceUrl: entry.name,
        loadTime: entry.responseEnd - entry.startTime,
        size: entry.transferSize,
        tags: {
          resource_type: this.getResourceType(entry.name),
          cache_status: entry.transferSize === 0 ? 'cached' : 'network'
        }
      }
      
      this.recordMetric(metric)
    }
  }
  
  /**
   * 记录用户交互性能
   */
  recordUserInteraction(action: string, element: string, duration?: number) {
    if (!this.isEnabled) return
    
    const metric: UserInteractionMetric = {
      name: 'user_interaction',
      value: duration || 0,
      timestamp: Date.now(),
      category: 'user',
      action,
      element,
      duration,
      tags: {
        action,
        element,
        route: window.location.pathname
      }
    }
    
    this.recordMetric(metric)
  }
  
  /**
   * 记录设备管理特定指标
   */
  recordDeviceManagementMetric(operation: string, duration: number, deviceCount?: number) {
    const metric: PerformanceMetric = {
      name: 'device_management_operation',
      value: duration,
      timestamp: Date.now(),
      category: 'user',
      tags: {
        operation,
        device_count: deviceCount?.toString(),
        route: '/admin/device/management'
      }
    }
    
    this.recordMetric(metric)
  }
  
  /**
   * 记录通用指标
   */
  private recordMetric(metric: PerformanceMetric) {
    this.metrics.push(metric)
    
    // 限制缓存大小
    if (this.metrics.length > this.maxMetrics) {
      this.metrics = this.metrics.slice(-this.maxMetrics)
    }
    
    // 实时告警检查
    this.checkAlerts(metric)
  }
  
  /**
   * 记录慢请求
   */
  private recordSlowRequest(metric: ApiPerformanceMetric) {
    console.warn('Slow API request detected:', {
      url: metric.url,
      method: metric.method,
      responseTime: metric.responseTime,
      timestamp: new Date(metric.timestamp).toISOString()
    })
    
    // 可以发送到监控系统
    this.sendAlert('slow_request', metric)
  }
  
  /**
   * 检查性能告警
   */
  private checkAlerts(metric: PerformanceMetric) {
    const thresholds = {
      api_response_time: 2000,
      page_load_time: 3000,
      resource_load_time: 1000
    }
    
    const threshold = thresholds[metric.name as keyof typeof thresholds]
    if (threshold && metric.value > threshold) {
      this.sendAlert('performance_threshold_exceeded', metric)
    }
  }
  
  /**
   * 发送告警
   */
  private sendAlert(type: string, metric: PerformanceMetric) {
    // 这里可以集成实际的告警系统
    console.warn(`Performance Alert [${type}]:`, metric)
  }
  
  /**
   * 开始定期上报
   */
  private startReporting() {
    setInterval(() => {
      this.reportMetrics()
    }, this.reportInterval)
  }
  
  /**
   * 上报性能指标
   */
  private async reportMetrics() {
    if (this.metrics.length === 0) return
    
    try {
      // 准备上报数据
      const reportData = {
        timestamp: Date.now(),
        userAgent: navigator.userAgent,
        url: window.location.href,
        metrics: [...this.metrics]
      }
      
      // 发送到后端
      await this.sendMetricsToBackend(reportData)
      
      // 清空已上报的指标
      this.metrics = []
    } catch (error) {
      console.error('Failed to report metrics:', error)
    }
  }
  
  /**
   * 发送指标到后端
   */
  private async sendMetricsToBackend(data: any) {
    try {
      const response = await fetch('/api/admin/performance/metrics', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(data)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
    } catch (error) {
      // 静默失败，不影响用户体验
      console.debug('Metrics reporting failed:', error)
    }
  }
  
  /**
   * 获取性能统计
   */
  getPerformanceStats() {
    const stats = {
      totalMetrics: this.metrics.length,
      apiMetrics: this.metrics.filter(m => m.category === 'api').length,
      pageMetrics: this.metrics.filter(m => m.category === 'page').length,
      userMetrics: this.metrics.filter(m => m.category === 'user').length,
      resourceMetrics: this.metrics.filter(m => m.category === 'resource').length,
      averageApiResponseTime: this.calculateAverageApiResponseTime(),
      slowRequestsCount: this.getSlowRequestsCount()
    }
    
    return stats
  }
  
  /**
   * 计算平均API响应时间
   */
  private calculateAverageApiResponseTime(): number {
    const apiMetrics = this.metrics.filter(m => m.category === 'api') as ApiPerformanceMetric[]
    if (apiMetrics.length === 0) return 0
    
    const totalTime = apiMetrics.reduce((sum, metric) => sum + metric.responseTime, 0)
    return Math.round(totalTime / apiMetrics.length)
  }
  
  /**
   * 获取慢请求数量
   */
  private getSlowRequestsCount(): number {
    return this.metrics.filter(m => 
      m.category === 'api' && (m as ApiPerformanceMetric).responseTime > 2000
    ).length
  }
  
  /**
   * 工具方法
   */
  private extractEndpoint(url: string): string {
    try {
      const urlObj = new URL(url, window.location.origin)
      return urlObj.pathname.replace(/\/\d+/g, '/:id') // 替换ID为占位符
    } catch {
      return url
    }
  }
  
  private getNavigationType(type: number): string {
    const types = ['navigate', 'reload', 'back_forward', 'prerender']
    return types[type] || 'unknown'
  }
  
  private isKeyResource(url: string): boolean {
    const keyPatterns = [
      /\.js$/,
      /\.css$/,
      /\.vue$/,
      /\/api\//,
      /\.(png|jpg|jpeg|gif|webp|svg)$/
    ]
    
    return keyPatterns.some(pattern => pattern.test(url))
  }
  
  private getResourceType(url: string): string {
    if (url.includes('/api/')) return 'api'
    if (/\.(js|ts)$/.test(url)) return 'script'
    if (/\.css$/.test(url)) return 'stylesheet'
    if (/\.(png|jpg|jpeg|gif|webp|svg)$/.test(url)) return 'image'
    if (/\.(woff|woff2|ttf|eot)$/.test(url)) return 'font'
    return 'other'
  }
  
  /**
   * 启用/禁用监控
   */
  setEnabled(enabled: boolean) {
    this.isEnabled = enabled
  }
  
  /**
   * 清理资源
   */
  destroy() {
    this.observers.forEach(observer => observer.disconnect())
    this.observers = []
    this.metrics = []
  }
}

// 创建全局实例
export const performanceMonitor = PerformanceMonitorService.getInstance()

// 导出便捷方法
export const recordApiPerformance = (
  url: string,
  method: string,
  status: number,
  responseTime: number,
  requestSize?: number,
  responseSize?: number
) => {
  performanceMonitor.recordApiPerformance(url, method, status, responseTime, requestSize, responseSize)
}

export const recordUserInteraction = (action: string, element: string, duration?: number) => {
  performanceMonitor.recordUserInteraction(action, element, duration)
}

export const recordDeviceOperation = (operation: string, duration: number, deviceCount?: number) => {
  performanceMonitor.recordDeviceManagementMetric(operation, duration, deviceCount)
}

export const getPerformanceStats = () => {
  return performanceMonitor.getPerformanceStats()
}