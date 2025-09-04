/**
 * Vue性能监控插件
 * 自动监控Vue组件性能和用户交互
 * 任务24：性能优化和监控
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import type { App } from 'vue'
import { performanceMonitor, recordUserInteraction } from '@/services/performanceMonitor'
import { initializeApiInterceptors } from '@/utils/apiInterceptor'

// 插件选项接口
export interface PerformancePluginOptions {
  enabled?: boolean
  trackRouteChanges?: boolean
  trackUserInteractions?: boolean
  trackComponentLifecycle?: boolean
  reportInterval?: number
  maxMetrics?: number
}

// 默认选项
const defaultOptions: PerformancePluginOptions = {
  enabled: true,
  trackRouteChanges: true,
  trackUserInteractions: true,
  trackComponentLifecycle: false,
  reportInterval: 30000,
  maxMetrics: 1000
}

/**
 * 性能监控插件
 */
export const PerformancePlugin = {
  install(app: App, options: PerformancePluginOptions = {}) {
    const config = { ...defaultOptions, ...options }
    
    if (!config.enabled) {
      console.debug('Performance monitoring disabled')
      return
    }
    
    // 初始化API拦截器
    initializeApiInterceptors()
    
    // 全局属性
    app.config.globalProperties.$performance = performanceMonitor
    
    // 提供性能监控实例
    app.provide('performance', performanceMonitor)
    
    // 监控路由变化
    if (config.trackRouteChanges) {
      setupRouteTracking(app)
    }
    
    // 监控用户交互
    if (config.trackUserInteractions) {
      setupUserInteractionTracking(app)
    }
    
    // 监控组件生命周期
    if (config.trackComponentLifecycle) {
      setupComponentLifecycleTracking(app)
    }
    
    // 设置全局错误处理
    setupErrorTracking(app)
    
    console.debug('Performance monitoring plugin initialized', config)
  }
}

/**
 * 设置路由跟踪
 */
function setupRouteTracking(app: App) {
  let routeStartTime = 0
  
  // 监听路由变化
  app.mixin({
    beforeRouteEnter(to, from, next) {
      routeStartTime = performance.now()
      next()
    },
    
    mounted() {
      if (routeStartTime > 0 && this.$route) {
        const loadTime = performance.now() - routeStartTime
        recordUserInteraction('route_change', this.$route.path, loadTime)
        routeStartTime = 0
      }
    }
  })
}

/**
 * 设置用户交互跟踪
 */
function setupUserInteractionTracking(app: App) {
  // 全局指令：v-track
  app.directive('track', {
    mounted(el: HTMLElement, binding) {
      const { value, arg, modifiers } = binding
      const eventType = arg || 'click'
      const trackingData = typeof value === 'string' ? { action: value } : value
      
      const handler = (event: Event) => {
        const startTime = performance.now()
        
        // 记录交互
        recordUserInteraction(
          trackingData.action || eventType,
          trackingData.element || el.tagName.toLowerCase(),
          0
        )
        
        // 如果有回调函数，执行并记录耗时
        if (trackingData.callback) {
          Promise.resolve(trackingData.callback(event)).then(() => {
            const duration = performance.now() - startTime
            recordUserInteraction(
              `${trackingData.action || eventType}_callback`,
              trackingData.element || el.tagName.toLowerCase(),
              duration
            )
          })
        }
      }
      
      el.addEventListener(eventType, handler)
      
      // 保存处理器以便清理
      el._trackHandler = handler
      el._trackEventType = eventType
    },
    
    unmounted(el: HTMLElement) {
      if (el._trackHandler && el._trackEventType) {
        el.removeEventListener(el._trackEventType, el._trackHandler)
        delete el._trackHandler
        delete el._trackEventType
      }
    }
  })
  
  // 自动跟踪常见交互
  if (typeof window !== 'undefined') {
    // 跟踪点击事件
    document.addEventListener('click', (event) => {
      const target = event.target as HTMLElement
      if (target) {
        const tagName = target.tagName.toLowerCase()
        const className = target.className
        const id = target.id
        
        let elementIdentifier = tagName
        if (id) elementIdentifier += `#${id}`
        if (className) elementIdentifier += `.${className.split(' ')[0]}`
        
        recordUserInteraction('click', elementIdentifier)
      }
    }, { passive: true })
    
    // 跟踪表单提交
    document.addEventListener('submit', (event) => {
      const form = event.target as HTMLFormElement
      const formId = form.id || form.className || 'unknown'
      recordUserInteraction('form_submit', formId)
    }, { passive: true })
    
    // 跟踪页面可见性变化
    document.addEventListener('visibilitychange', () => {
      recordUserInteraction(
        'visibility_change',
        'document',
        document.hidden ? 1 : 0
      )
    })
  }
}

/**
 * 设置组件生命周期跟踪
 */
function setupComponentLifecycleTracking(app: App) {
  app.mixin({
    beforeCreate() {
      this._performanceStartTime = performance.now()
    },
    
    created() {
      if (this._performanceStartTime) {
        const duration = performance.now() - this._performanceStartTime
        recordUserInteraction(
          'component_created',
          this.$options.name || 'anonymous',
          duration
        )
      }
    },
    
    mounted() {
      if (this._performanceStartTime) {
        const duration = performance.now() - this._performanceStartTime
        recordUserInteraction(
          'component_mounted',
          this.$options.name || 'anonymous',
          duration
        )
      }
    },
    
    beforeUnmount() {
      recordUserInteraction(
        'component_unmount',
        this.$options.name || 'anonymous'
      )
    }
  })
}

/**
 * 设置错误跟踪
 */
function setupErrorTracking(app: App) {
  // Vue错误处理
  app.config.errorHandler = (error, instance, info) => {
    console.error('Vue Error:', error, info)
    
    recordUserInteraction(
      'vue_error',
      instance?.$options.name || 'unknown',
      0
    )
    
    // 可以发送错误到监控系统
    reportError('vue_error', error, {
      component: instance?.$options.name,
      info
    })
  }
  
  // 全局未捕获错误
  if (typeof window !== 'undefined') {
    window.addEventListener('error', (event) => {
      recordUserInteraction('js_error', 'global', 0)
      
      reportError('js_error', event.error, {
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno
      })
    })
    
    // Promise未捕获错误
    window.addEventListener('unhandledrejection', (event) => {
      recordUserInteraction('promise_rejection', 'global', 0)
      
      reportError('promise_rejection', event.reason)
    })
  }
}

/**
 * 报告错误到监控系统
 */
function reportError(type: string, error: any, context?: any) {
  try {
    const errorData = {
      type,
      message: error?.message || String(error),
      stack: error?.stack,
      context,
      timestamp: Date.now(),
      url: window.location.href,
      userAgent: navigator.userAgent
    }
    
    // 发送到后端监控系统
    fetch('/api/admin/performance/errors', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(errorData)
    }).catch(() => {
      // 静默失败
    })
  } catch {
    // 静默失败，避免错误循环
  }
}

// 扩展HTMLElement类型
declare global {
  interface HTMLElement {
    _trackHandler?: EventListener
    _trackEventType?: string
  }
}

// 扩展Vue实例类型
declare module '@vue/runtime-core' {
  interface ComponentInternalInstance {
    _performanceStartTime?: number
  }
  
  interface ComponentCustomProperties {
    $performance: typeof performanceMonitor
  }
}

export default PerformancePlugin