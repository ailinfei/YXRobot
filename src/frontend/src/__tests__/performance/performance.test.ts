import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { 
  MemoryCache, 
  RequestCache, 
  LocalStorageCache,
  memoryCache,
  requestCache,
  localStorageCache
} from '@/utils/cache'
import { 
  debounce, 
  throttle, 
  TaskScheduler,
  LazyImageLoader,
  ResourcePreloader
} from '@/utils/performance'
import { createLazyComponent, SmartPreloader } from '@/utils/lazyLoading'

describe('Performance Optimizations', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('Memory Cache', () => {
    let cache: MemoryCache

    beforeEach(() => {
      cache = new MemoryCache({ maxSize: 3, defaultTTL: 1000 })
    })

    it('should store and retrieve data', () => {
      cache.set('key1', 'value1')
      expect(cache.get('key1')).toBe('value1')
    })

    it('should return null for non-existent keys', () => {
      expect(cache.get('nonexistent')).toBeNull()
    })

    it('should expire data after TTL', async () => {
      cache.set('key1', 'value1', 100)
      expect(cache.get('key1')).toBe('value1')
      
      await new Promise(resolve => setTimeout(resolve, 150))
      expect(cache.get('key1')).toBeNull()
    })

    it('should evict LRU items when cache is full', () => {
      cache.set('key1', 'value1')
      cache.set('key2', 'value2')
      cache.set('key3', 'value3')
      
      // Access key1 to make it more recently used
      cache.get('key1')
      
      // Add key4, should evict key2 (least recently used)
      cache.set('key4', 'value4')
      
      expect(cache.get('key1')).toBe('value1')
      expect(cache.get('key2')).toBeNull()
      expect(cache.get('key3')).toBe('value3')
      expect(cache.get('key4')).toBe('value4')
    })

    it('should provide cache statistics', () => {
      cache.set('key1', 'value1')
      cache.get('key1')
      cache.get('key1')
      
      const stats = cache.getStats()
      expect(stats.size).toBe(1)
      expect(stats.totalHits).toBe(2)
    })
  })

  describe('Request Cache', () => {
    let cache: RequestCache

    beforeEach(() => {
      cache = new RequestCache()
    })

    it('should cache request results', async () => {
      let callCount = 0
      const requestFn = () => {
        callCount++
        return Promise.resolve(`result-${callCount}`)
      }

      const result1 = await cache.cacheRequest('test-key', requestFn)
      const result2 = await cache.cacheRequest('test-key', requestFn)

      expect(result1).toBe('result-1')
      expect(result2).toBe('result-1') // Should return cached result
      expect(callCount).toBe(1) // Function should only be called once
    })

    it('should handle concurrent requests', async () => {
      let callCount = 0
      const requestFn = () => {
        callCount++
        return new Promise(resolve => {
          setTimeout(() => resolve(`result-${callCount}`), 100)
        })
      }

      const promises = [
        cache.cacheRequest('test-key', requestFn),
        cache.cacheRequest('test-key', requestFn),
        cache.cacheRequest('test-key', requestFn)
      ]

      const results = await Promise.all(promises)
      
      expect(results).toEqual(['result-1', 'result-1', 'result-1'])
      expect(callCount).toBe(1) // Function should only be called once
    })

    it('should clear cache by pattern', async () => {
      const requestFn = () => Promise.resolve('result')

      await cache.cacheRequest('user-1', requestFn)
      await cache.cacheRequest('user-2', requestFn)
      await cache.cacheRequest('product-1', requestFn)

      cache.clearPattern('user-.*')

      // User caches should be cleared, product cache should remain
      let callCount = 0
      const newRequestFn = () => {
        callCount++
        return Promise.resolve(`new-result-${callCount}`)
      }

      await cache.cacheRequest('user-1', newRequestFn)
      await cache.cacheRequest('product-1', newRequestFn)

      expect(callCount).toBe(1) // Only user-1 should call the new function
    })
  })

  describe('Local Storage Cache', () => {
    let cache: LocalStorageCache

    beforeEach(() => {
      cache = new LocalStorageCache('test_')
      cache.clear()
    })

    afterEach(() => {
      cache.clear()
    })

    it('should store and retrieve data from localStorage', () => {
      const testData = { name: 'test', value: 123 }
      cache.set('key1', testData)
      
      const retrieved = cache.get('key1')
      expect(retrieved).toEqual(testData)
    })

    it('should handle localStorage quota exceeded', () => {
      // Mock localStorage to throw quota exceeded error
      const originalSetItem = Storage.prototype.setItem
      Storage.prototype.setItem = vi.fn(() => {
        throw new Error('QuotaExceededError')
      })

      // Should not throw error
      expect(() => {
        cache.set('key1', 'value1')
      }).not.toThrow()

      Storage.prototype.setItem = originalSetItem
    })

    it('should clean up expired items', async () => {
      cache.set('key1', 'value1', 100)
      cache.set('key2', 'value2', 1000)
      
      await new Promise(resolve => setTimeout(resolve, 150))
      
      cache.cleanup()
      
      expect(cache.get('key1')).toBeNull()
      expect(cache.get('key2')).toBe('value2')
    })
  })

  describe('Debounce and Throttle', () => {
    it('should debounce function calls', async () => {
      let callCount = 0
      const fn = () => callCount++
      const debouncedFn = debounce(fn, 100)

      debouncedFn()
      debouncedFn()
      debouncedFn()

      expect(callCount).toBe(0)

      await new Promise(resolve => setTimeout(resolve, 150))
      expect(callCount).toBe(1)
    })

    it('should throttle function calls', async () => {
      let callCount = 0
      const fn = () => callCount++
      const throttledFn = throttle(fn, 100)

      throttledFn()
      throttledFn()
      throttledFn()

      expect(callCount).toBe(1)

      await new Promise(resolve => setTimeout(resolve, 150))
      
      throttledFn()
      expect(callCount).toBe(2)
    })
  })

  describe('Task Scheduler', () => {
    it('should execute tasks in batches', async () => {
      const scheduler = new TaskScheduler()
      const results: number[] = []

      const tasks = Array.from({ length: 10 }, (_, i) => () => {
        results.push(i)
      })

      scheduler.addTasks(tasks)

      // Wait for tasks to complete
      await new Promise(resolve => setTimeout(resolve, 100))

      expect(results).toHaveLength(10)
      expect(results).toEqual([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
    })

    it('should handle task errors gracefully', async () => {
      const scheduler = new TaskScheduler()
      const results: number[] = []

      const tasks = [
        () => results.push(1),
        () => { throw new Error('Task error') },
        () => results.push(3)
      ]

      scheduler.addTasks(tasks)

      await new Promise(resolve => setTimeout(resolve, 100))

      expect(results).toEqual([1, 3])
    })
  })

  describe('Lazy Image Loader', () => {
    it('should load images when they intersect', () => {
      // Mock IntersectionObserver
      const mockObserve = vi.fn()
      const mockUnobserve = vi.fn()
      const mockDisconnect = vi.fn()

      global.IntersectionObserver = vi.fn().mockImplementation((callback) => ({
        observe: mockObserve,
        unobserve: mockUnobserve,
        disconnect: mockDisconnect
      }))

      const loader = new LazyImageLoader()
      const img = document.createElement('img')
      img.dataset.src = 'test.jpg'

      loader.observe(img)

      expect(mockObserve).toHaveBeenCalledWith(img)
    })
  })

  describe('Resource Preloader', () => {
    it('should preload images', async () => {
      const preloader = new ResourcePreloader()
      
      // Mock Image constructor
      const mockImage = {
        onload: null as any,
        onerror: null as any,
        src: ''
      }

      global.Image = vi.fn().mockImplementation(() => mockImage)

      const preloadPromise = preloader.preloadImage('test.jpg')
      
      // Simulate image load
      mockImage.src = 'test.jpg'
      if (mockImage.onload) {
        mockImage.onload()
      }

      await expect(preloadPromise).resolves.toBeUndefined()
    })

    it('should handle image load errors', async () => {
      const preloader = new ResourcePreloader()
      
      const mockImage = {
        onload: null as any,
        onerror: null as any,
        src: ''
      }

      global.Image = vi.fn().mockImplementation(() => mockImage)

      const preloadPromise = preloader.preloadImage('invalid.jpg')
      
      // Simulate image error
      if (mockImage.onerror) {
        mockImage.onerror(new Error('Image load failed'))
      }

      await expect(preloadPromise).rejects.toThrow()
    })
  })

  describe('Smart Preloader', () => {
    it('should add and execute preload tasks', async () => {
      const preloader = new SmartPreloader()
      let loaded = false

      const mockLoader = () => {
        loaded = true
        return Promise.resolve()
      }

      preloader.addPreloadTask('test-component', mockLoader, 1)
      await preloader.executePreload(1)

      expect(loaded).toBe(true)
    })

    it('should respect priority order', async () => {
      const preloader = new SmartPreloader()
      const loadOrder: string[] = []

      const createLoader = (name: string) => () => {
        loadOrder.push(name)
        return Promise.resolve()
      }

      preloader.addPreloadTask('low-priority', createLoader('low'), 1)
      preloader.addPreloadTask('high-priority', createLoader('high'), 10)
      preloader.addPreloadTask('medium-priority', createLoader('medium'), 5)

      await preloader.executePreload(1)

      expect(loadOrder).toEqual(['high', 'medium', 'low'])
    })
  })

  describe('Global Cache Instances', () => {
    it('should provide global cache instances', () => {
      expect(memoryCache).toBeInstanceOf(MemoryCache)
      expect(requestCache).toBeInstanceOf(RequestCache)
      expect(localStorageCache).toBeInstanceOf(LocalStorageCache)
    })

    it('should allow setting and getting from global memory cache', () => {
      memoryCache.set('global-test', 'global-value')
      expect(memoryCache.get('global-test')).toBe('global-value')
    })
  })
})