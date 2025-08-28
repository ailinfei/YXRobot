import { defineAsyncComponent, AsyncComponentLoader } from 'vue'
import { ElLoading } from 'element-plus'

/**
 * 创建懒加载组件的工厂函数
 * @param loader 组件加载器
 * @param options 配置选项
 */
export function createLazyComponent(
  loader: AsyncComponentLoader,
  options: {
    loadingComponent?: any
    errorComponent?: any
    delay?: number
    timeout?: number
    suspensible?: boolean
  } = {}
) {
  return defineAsyncComponent({
    loader,
    loadingComponent: options.loadingComponent || {
      template: `
        <div class="lazy-loading">
          <el-skeleton :rows="5" animated />
        </div>
      `
    },
    errorComponent: options.errorComponent || {
      template: `
        <div class="lazy-error">
          <el-result
            icon="error"
            title="组件加载失败"
            sub-title="请刷新页面重试"
          >
            <template #extra>
              <el-button type="primary" @click="window.location.reload()">
                刷新页面
              </el-button>
            </template>
          </el-result>
        </div>
      `
    },
    delay: options.delay || 200,
    timeout: options.timeout || 30000,
    suspensible: options.suspensible || false
  })
}

/**
 * 预加载组件
 * @param componentLoader 组件加载器
 */
export function preloadComponent(componentLoader: AsyncComponentLoader) {
  return componentLoader()
}

/**
 * 批量预加载组件
 * @param loaders 组件加载器数组
 */
export async function preloadComponents(loaders: AsyncComponentLoader[]) {
  const promises = loaders.map(loader => loader())
  return Promise.all(promises)
}

/**
 * 路由级别的懒加载
 * @param importFn 动态导入函数
 */
export function lazyRoute(importFn: () => Promise<any>) {
  return () => {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '页面加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })

    return importFn()
      .then(module => {
        loadingInstance.close()
        return module
      })
      .catch(error => {
        loadingInstance.close()
        console.error('Route loading failed:', error)
        throw error
      })
  }
}

/**
 * 组件级别的懒加载装饰器
 */
export function LazyComponent(options?: {
  preload?: boolean
  priority?: 'high' | 'low'
}) {
  return function (target: any) {
    const originalLoader = target.loader || target

    if (options?.preload) {
      // 在空闲时间预加载
      if ('requestIdleCallback' in window) {
        requestIdleCallback(() => {
          originalLoader()
        })
      } else {
        setTimeout(() => {
          originalLoader()
        }, 1000)
      }
    }

    return createLazyComponent(originalLoader, {
      suspensible: options?.priority === 'high'
    })
  }
}

/**
 * 智能预加载策略
 */
export class SmartPreloader {
  private preloadedComponents = new Set<string>()
  private preloadQueue: Array<{ name: string; loader: AsyncComponentLoader; priority: number }> = []

  /**
   * 添加预加载任务
   */
  addPreloadTask(name: string, loader: AsyncComponentLoader, priority = 0) {
    if (this.preloadedComponents.has(name)) {
      return
    }

    this.preloadQueue.push({ name, loader, priority })
    this.preloadQueue.sort((a, b) => b.priority - a.priority)
  }

  /**
   * 执行预加载
   */
  async executePreload(maxConcurrent = 3) {
    const executing: Promise<any>[] = []

    while (this.preloadQueue.length > 0 || executing.length > 0) {
      // 启动新的预加载任务
      while (executing.length < maxConcurrent && this.preloadQueue.length > 0) {
        const task = this.preloadQueue.shift()!
        const promise = this.preloadComponent(task.name, task.loader)
        executing.push(promise)
      }

      // 等待至少一个任务完成
      if (executing.length > 0) {
        await Promise.race(executing)
        // 移除已完成的任务
        for (let i = executing.length - 1; i >= 0; i--) {
          if (await Promise.race([executing[i], Promise.resolve('pending')]) !== 'pending') {
            executing.splice(i, 1)
          }
        }
      }
    }
  }

  private async preloadComponent(name: string, loader: AsyncComponentLoader) {
    try {
      await loader()
      this.preloadedComponents.add(name)
      console.log(`Component ${name} preloaded successfully`)
    } catch (error) {
      console.warn(`Failed to preload component ${name}:`, error)
    }
  }

  /**
   * 基于用户行为的智能预加载
   */
  setupIntelligentPreloading() {
    // 鼠标悬停预加载
    document.addEventListener('mouseover', (event) => {
      const target = event.target as HTMLElement
      const routerLink = target.closest('[data-preload]')
      if (routerLink) {
        const componentName = routerLink.getAttribute('data-preload')
        if (componentName && !this.preloadedComponents.has(componentName)) {
          // 延迟预加载，避免频繁触发
          setTimeout(() => {
            this.triggerPreload(componentName)
          }, 100)
        }
      }
    })

    // 视口内预加载
    if ('IntersectionObserver' in window) {
      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const componentName = entry.target.getAttribute('data-preload')
            if (componentName && !this.preloadedComponents.has(componentName)) {
              this.triggerPreload(componentName)
            }
          }
        })
      }, {
        rootMargin: '50px'
      })

      // 观察所有带有 data-preload 属性的元素
      document.querySelectorAll('[data-preload]').forEach(el => {
        observer.observe(el)
      })
    }
  }

  private triggerPreload(componentName: string) {
    // 这里需要根据实际的组件映射来实现
    console.log(`Triggering preload for component: ${componentName}`)
  }
}

// 全局预加载器实例
export const smartPreloader = new SmartPreloader()

// 初始化智能预加载
export function initializeSmartPreloading() {
  smartPreloader.setupIntelligentPreloading()
}