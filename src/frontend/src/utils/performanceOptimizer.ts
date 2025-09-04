/**
 * 性能优化工具类
 * 提供各种性能优化方法和工具
 * 任务24：性能优化和监控
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { ref, nextTick } from 'vue'
import { recordUserInteraction, recordDeviceOperation } from '@/services/performanceMonitor'

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
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

/**
 * 延迟执行函数
 */
export function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * 批量处理函数
 */
export class BatchProcessor<T> {
  private batch: T[] = []
  private timer: NodeJS.Timeout | null = null
  
  constructor(
    private processor: (items: T[]) => Promise<void> | void,
    private batchSize = 10,
    private delay = 100
  ) {}
  
  add(item: T) {
    this.batch.push(item)
    
    if (this.batch.length >= this.batchSize) {
      this.flush()
    } else if (!this.timer) {
      this.timer = setTimeout(() => this.flush(), this.delay)
    }
  }
  
  async flush() {
    if (this.timer) {
      clearTimeout(this.timer)
      this.timer = null
    }
    
    if (this.batch.length > 0) {
      const items = [...this.batch]
      this.batch = []
      await this.processor(items)
    }
  }
}

/**
 * 虚拟滚动优化
 */
export function useVirtualScroll<T>(
  items: T[],
  itemHeight: number,
  containerHeight: number,
  buffer = 5
) {
  const scrollTop = ref(0)
  
  const visibleRange = computed(() => {
    const start = Math.max(0, Math.floor(scrollTop.value / itemHeight) - buffer)
    const end = Math.min(
      items.length,
      Math.ceil((scrollTop.value + containerHeight) / itemHeight) + buffer
    )
    return { start, end }
  })
  
  const visibleItems = computed(() => {
    const { start, end } = visibleRange.value
    return items.slice(start, end).map((item, index) => ({
      item,
      index: start + index
    }))
  })
  
  const totalHeight = computed(() => items.length * itemHeight)
  
  const offsetY = computed(() => visibleRange.value.start * itemHeight)
  
  const handleScroll = (event: Event) => {
    const target = event.target as HTMLElement
    scrollTop.value = target.scrollTop
  }
  
  return {
    visibleItems,
    totalHeight,
    offsetY,
    handleScroll
  }
}

/**
 * 图片懒加载
 */
export function useLazyLoad() {
  const observer = ref<IntersectionObserver | null>(null)
  
  const initObserver = () => {
    if (typeof window === 'undefined') return
    
    observer.value = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const img = entry.target as HTMLImageElement
            const src = img.dataset.src
            if (src) {
              img.src = src
              img.removeAttribute('data-src')
              observer.value?.unobserve(img)
            }
          }
        })
      },
      {
        rootMargin: '50px'
      }
    )
  }
  
  const observe = (element: HTMLElement) => {
    if (observer.value) {
      observer.value.observe(element)
    }
  }
  
  const unobserve = (element: HTMLElement) => {
    if (observer.value) {
      observer.value.unobserve(element)
    }
  }
  
  const destroy = () => {
    if (observer.value) {
      observer.value.disconnect()
      observer.value = null
    }
  }
  
  return {
    initObserver,
    observe,
    unobserve,
    destroy
  }
}

/**
 * 内存优化 - 大数据处理
 */
export class MemoryOptimizer {
  private static cache = new Map<string, any>()
  private static maxCacheSize = 100
  
  /**
   * 缓存数据
   */
  static setCache(key: string, value: any, ttl = 300000) { // 5分钟TTL
    if (this.cache.size >= this.maxCacheSize) {
      // 删除最旧的缓存项
      const firstKey = this.cache.keys().next().value
      this.cache.delete(firstKey)
    }
    
    this.cache.set(key, {
      value,
      expiry: Date.now() + ttl
    })
  }
  
  /**
   * 获取缓存数据
   */
  static getCache(key: string): any {
    const cached = this.cache.get(key)
    if (!cached) return null
    
    if (Date.now() > cached.expiry) {
      this.cache.delete(key)
      return null
    }
    
    return cached.value
  }
  
  /**
   * 清理过期缓存
   */
  static cleanExpiredCache() {
    const now = Date.now()
    for (const [key, cached] of this.cache.entries()) {
      if (now > cached.expiry) {
        this.cache.delete(key)
      }
    }
  }
  
  /**
   * 清空所有缓存
   */
  static clearCache() {
    this.cache.clear()
  }
}

/**
 * 设备管理性能优化器
 */
export class DeviceManagementOptimizer {
  /**
   * 优化设备列表渲染
   */
  static optimizeDeviceList(devices: any[], pageSize = 20) {
    const startTime = performance.now()
    
    // 分页处理
    const totalPages = Math.ceil(devices.length / pageSize)
    const pages = []
    
    for (let i = 0; i < totalPages; i++) {
      const start = i * pageSize
      const end = start + pageSize
      pages.push(devices.slice(start, end))
    }
    
    const endTime = performance.now()
    recordDeviceOperation('list_optimization', endTime - startTime, devices.length)
    
    return pages
  }
  
  /**
   * 优化设备搜索
   */
  static optimizeDeviceSearch(devices: any[], keyword: string) {
    const startTime = performance.now()
    
    if (!keyword.trim()) {
      return devices
    }
    
    const lowerKeyword = keyword.toLowerCase()
    const results = devices.filter(device => 
      device.serialNumber?.toLowerCase().includes(lowerKeyword) ||
      device.customerName?.toLowerCase().includes(lowerKeyword) ||
      device.model?.toLowerCase().includes(lowerKeyword)
    )
    
    const endTime = performance.now()
    recordDeviceOperation('search_optimization', endTime - startTime, results.length)
    
    return results
  }
  
  /**
   * 优化批量操作
   */
  static async optimizeBatchOperation<T>(
    items: T[],
    operation: (item: T) => Promise<any>,
    batchSize = 5,
    delay = 100
  ) {
    const startTime = performance.now()
    const results = []
    
    for (let i = 0; i < items.length; i += batchSize) {
      const batch = items.slice(i, i + batchSize)
      const batchPromises = batch.map(operation)
      
      try {
        const batchResults = await Promise.all(batchPromises)
        results.push(...batchResults)
      } catch (error) {
        console.error('Batch operation failed:', error)
        throw error
      }
      
      // 添加延迟以避免过载
      if (i + batchSize < items.length) {
        await new Promise(resolve => setTimeout(resolve, delay))
      }
    }
    
    const endTime = performance.now()
    recordDeviceOperation('batch_optimization', endTime - startTime, items.length)
    
    return results
  }
}

/**
 * 性能监控装饰器
 */
export function performanceMonitor(operation: string) {
  return function (target: any, propertyName: string, descriptor: PropertyDescriptor) {
    const method = descriptor.value
    
    descriptor.value = async function (...args: any[]) {
      const startTime = performance.now()
      
      try {
        const result = await method.apply(this, args)
        const endTime = performance.now()
        
        recordUserInteraction(operation, propertyName, endTime - startTime)
        
        return result
      } catch (error) {
        const endTime = performance.now()
        recordUserInteraction(`${operation}_error`, propertyName, endTime - startTime)
        throw error
      }
    }
    
    return descriptor
  }
}

/**
 * 组合式函数 - 性能优化
 */
export function usePerformanceOptimization() {
  const isOptimizing = ref(false)
  const optimizationStats = ref({
    cacheHits: 0,
    cacheMisses: 0,
    optimizedOperations: 0
  })
  
  const startOptimization = () => {
    isOptimizing.value = true
  }
  
  const stopOptimization = () => {
    isOptimizing.value = false
  }
  
  const recordCacheHit = () => {
    optimizationStats.value.cacheHits++
  }
  
  const recordCacheMiss = () => {
    optimizationStats.value.cacheMisses++
  }
  
  const recordOptimizedOperation = () => {
    optimizationStats.value.optimizedOperations++
  }
  
  const getCacheHitRate = () => {
    const total = optimizationStats.value.cacheHits + optimizationStats.value.cacheMisses
    return total > 0 ? (optimizationStats.value.cacheHits / total * 100).toFixed(2) : '0'
  }
  
  return {
    isOptimizing: readonly(isOptimizing),
    optimizationStats: readonly(optimizationStats),
    startOptimization,
    stopOptimization,
    recordCacheHit,
    recordCacheMiss,
    recordOptimizedOperation,
    getCacheHitRate
  }
}

// 自动清理过期缓存
setInterval(() => {
  MemoryOptimizer.cleanExpiredCache()
}, 60000) // 每分钟清理一次

export default {
  debounce,
  throttle,
  delay,
  BatchProcessor,
  useVirtualScroll,
  useLazyLoad,
  MemoryOptimizer,
  DeviceManagementOptimizer,
  performanceMonitor,
  usePerformanceOptimization
}