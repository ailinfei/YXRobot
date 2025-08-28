/**
 * 缓存管理工具
 */

// 内存缓存接口
interface CacheItem<T> {
  data: T
  timestamp: number
  ttl: number
  hits: number
}

// 缓存配置
interface CacheConfig {
  maxSize?: number
  defaultTTL?: number
  cleanupInterval?: number
}

/**
 * 内存缓存类
 */
export class MemoryCache<T = any> {
  private cache = new Map<string, CacheItem<T>>()
  private maxSize: number
  private defaultTTL: number
  private cleanupTimer: NodeJS.Timeout | null = null

  constructor(config: CacheConfig = {}) {
    this.maxSize = config.maxSize || 100
    this.defaultTTL = config.defaultTTL || 5 * 60 * 1000 // 5分钟
    
    // 定期清理过期缓存
    if (config.cleanupInterval !== 0) {
      this.cleanupTimer = setInterval(() => {
        this.cleanup()
      }, config.cleanupInterval || 60 * 1000) // 1分钟
    }
  }

  /**
   * 设置缓存
   */
  set(key: string, data: T, ttl?: number): void {
    // 如果缓存已满，删除最少使用的项
    if (this.cache.size >= this.maxSize) {
      this.evictLRU()
    }

    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl: ttl || this.defaultTTL,
      hits: 0
    })
  }

  /**
   * 获取缓存
   */
  get(key: string): T | null {
    const item = this.cache.get(key)
    
    if (!item) {
      return null
    }

    // 检查是否过期
    if (Date.now() - item.timestamp > item.ttl) {
      this.cache.delete(key)
      return null
    }

    // 更新访问次数
    item.hits++
    return item.data
  }

  /**
   * 检查缓存是否存在且未过期
   */
  has(key: string): boolean {
    return this.get(key) !== null
  }

  /**
   * 删除缓存
   */
  delete(key: string): boolean {
    return this.cache.delete(key)
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    this.cache.clear()
  }

  /**
   * 获取缓存统计信息
   */
  getStats() {
    const items = Array.from(this.cache.values())
    return {
      size: this.cache.size,
      maxSize: this.maxSize,
      totalHits: items.reduce((sum, item) => sum + item.hits, 0),
      averageAge: items.length > 0 
        ? items.reduce((sum, item) => sum + (Date.now() - item.timestamp), 0) / items.length
        : 0
    }
  }

  /**
   * 清理过期缓存
   */
  private cleanup(): void {
    const now = Date.now()
    for (const [key, item] of this.cache.entries()) {
      if (now - item.timestamp > item.ttl) {
        this.cache.delete(key)
      }
    }
  }

  /**
   * LRU淘汰策略
   */
  private evictLRU(): void {
    let lruKey = ''
    let lruHits = Infinity
    let oldestTime = Infinity

    for (const [key, item] of this.cache.entries()) {
      if (item.hits < lruHits || (item.hits === lruHits && item.timestamp < oldestTime)) {
        lruKey = key
        lruHits = item.hits
        oldestTime = item.timestamp
      }
    }

    if (lruKey) {
      this.cache.delete(lruKey)
    }
  }

  /**
   * 销毁缓存
   */
  destroy(): void {
    if (this.cleanupTimer) {
      clearInterval(this.cleanupTimer)
      this.cleanupTimer = null
    }
    this.clear()
  }
}

/**
 * API响应缓存装饰器
 */
export function CacheResponse(ttl?: number, keyGenerator?: (...args: any[]) => string) {
  return function (target: any, propertyName: string, descriptor: PropertyDescriptor) {
    const method = descriptor.value
    const cache = new MemoryCache()

    descriptor.value = async function (...args: any[]) {
      const cacheKey = keyGenerator ? keyGenerator(...args) : JSON.stringify(args)
      
      // 尝试从缓存获取
      const cachedResult = cache.get(cacheKey)
      if (cachedResult !== null) {
        console.log(`Cache hit for ${propertyName}:${cacheKey}`)
        return cachedResult
      }

      // 执行原方法
      try {
        const result = await method.apply(this, args)
        cache.set(cacheKey, result, ttl)
        console.log(`Cache set for ${propertyName}:${cacheKey}`)
        return result
      } catch (error) {
        console.error(`Method ${propertyName} failed:`, error)
        throw error
      }
    }

    return descriptor
  }
}

/**
 * 请求缓存管理器
 */
export class RequestCache {
  private cache = new MemoryCache<Promise<any>>()
  private pendingRequests = new Map<string, Promise<any>>()

  /**
   * 缓存请求结果
   */
  async cacheRequest<T>(
    key: string,
    requestFn: () => Promise<T>,
    ttl?: number
  ): Promise<T> {
    // 检查缓存
    const cached = this.cache.get(key)
    if (cached) {
      return cached
    }

    // 检查是否有相同的请求正在进行
    const pending = this.pendingRequests.get(key)
    if (pending) {
      return pending
    }

    // 执行请求
    const promise = requestFn()
    this.pendingRequests.set(key, promise)

    try {
      const result = await promise
      this.cache.set(key, Promise.resolve(result), ttl)
      return result
    } catch (error) {
      // 请求失败时不缓存
      throw error
    } finally {
      this.pendingRequests.delete(key)
    }
  }

  /**
   * 清除特定模式的缓存
   */
  clearPattern(pattern: string): void {
    const regex = new RegExp(pattern)
    for (const key of this.cache['cache'].keys()) {
      if (regex.test(key)) {
        this.cache.delete(key)
      }
    }
  }

  /**
   * 预热缓存
   */
  async warmup(requests: Array<{ key: string; requestFn: () => Promise<any>; ttl?: number }>) {
    const promises = requests.map(({ key, requestFn, ttl }) =>
      this.cacheRequest(key, requestFn, ttl).catch(error => {
        console.warn(`Warmup failed for ${key}:`, error)
      })
    )
    
    await Promise.allSettled(promises)
  }
}

/**
 * 本地存储缓存
 */
export class LocalStorageCache {
  private prefix: string

  constructor(prefix = 'app_cache_') {
    this.prefix = prefix
  }

  /**
   * 设置缓存
   */
  set(key: string, data: any, ttl?: number): void {
    const item = {
      data,
      timestamp: Date.now(),
      ttl: ttl || 24 * 60 * 60 * 1000 // 默认24小时
    }

    try {
      localStorage.setItem(this.prefix + key, JSON.stringify(item))
    } catch (error) {
      console.warn('LocalStorage cache set failed:', error)
      // 存储空间不足时清理旧缓存
      this.cleanup()
      try {
        localStorage.setItem(this.prefix + key, JSON.stringify(item))
      } catch (retryError) {
        console.error('LocalStorage cache set failed after cleanup:', retryError)
      }
    }
  }

  /**
   * 获取缓存
   */
  get<T>(key: string): T | null {
    try {
      const itemStr = localStorage.getItem(this.prefix + key)
      if (!itemStr) {
        return null
      }

      const item = JSON.parse(itemStr)
      
      // 检查是否过期
      if (Date.now() - item.timestamp > item.ttl) {
        this.delete(key)
        return null
      }

      return item.data
    } catch (error) {
      console.warn('LocalStorage cache get failed:', error)
      return null
    }
  }

  /**
   * 删除缓存
   */
  delete(key: string): void {
    localStorage.removeItem(this.prefix + key)
  }

  /**
   * 清理过期缓存
   */
  cleanup(): void {
    const now = Date.now()
    const keysToDelete: string[] = []

    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (key && key.startsWith(this.prefix)) {
        try {
          const itemStr = localStorage.getItem(key)
          if (itemStr) {
            const item = JSON.parse(itemStr)
            if (now - item.timestamp > item.ttl) {
              keysToDelete.push(key)
            }
          }
        } catch (error) {
          // 解析失败的项目也删除
          keysToDelete.push(key)
        }
      }
    }

    keysToDelete.forEach(key => localStorage.removeItem(key))
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    const keysToDelete: string[] = []
    
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (key && key.startsWith(this.prefix)) {
        keysToDelete.push(key)
      }
    }

    keysToDelete.forEach(key => localStorage.removeItem(key))
  }
}

// 全局缓存实例
export const memoryCache = new MemoryCache()
export const requestCache = new RequestCache()
export const localStorageCache = new LocalStorageCache()

// 初始化缓存清理
export function initializeCacheCleanup() {
  // 页面卸载时清理内存缓存
  window.addEventListener('beforeunload', () => {
    memoryCache.destroy()
  })

  // 定期清理本地存储缓存
  setInterval(() => {
    localStorageCache.cleanup()
  }, 60 * 60 * 1000) // 每小时清理一次
}