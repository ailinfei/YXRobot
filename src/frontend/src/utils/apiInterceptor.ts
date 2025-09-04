/**
 * API拦截器 - 自动监控API性能
 * 任务24：性能优化和监控
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { recordApiPerformance } from '@/services/performanceMonitor'

// API请求信息接口
interface ApiRequestInfo {
  url: string
  method: string
  startTime: number
  requestSize?: number
}

// 请求映射表
const requestMap = new Map<string, ApiRequestInfo>()

/**
 * 生成请求唯一标识
 */
function generateRequestId(): string {
  return `${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

/**
 * 计算请求体大小
 */
function calculateRequestSize(data: any): number {
  if (!data) return 0
  
  try {
    if (typeof data === 'string') {
      return new Blob([data]).size
    }
    if (data instanceof FormData) {
      // FormData大小估算
      let size = 0
      for (const [key, value] of data.entries()) {
        size += key.length
        if (typeof value === 'string') {
          size += value.length
        } else if (value instanceof File) {
          size += value.size
        }
      }
      return size
    }
    return new Blob([JSON.stringify(data)]).size
  } catch {
    return 0
  }
}

/**
 * 计算响应体大小
 */
function calculateResponseSize(response: Response): number {
  const contentLength = response.headers.get('content-length')
  if (contentLength) {
    return parseInt(contentLength, 10)
  }
  
  // 如果没有content-length头，尝试从其他方式估算
  return 0
}

/**
 * 设置Fetch拦截器
 */
export function setupFetchInterceptor() {
  if (typeof window === 'undefined') return
  
  const originalFetch = window.fetch
  
  window.fetch = async function(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
    const requestId = generateRequestId()
    const url = typeof input === 'string' ? input : input instanceof URL ? input.href : input.url
    const method = init?.method || 'GET'
    const startTime = performance.now()
    
    // 记录请求信息
    const requestInfo: ApiRequestInfo = {
      url,
      method,
      startTime,
      requestSize: calculateRequestSize(init?.body)
    }
    requestMap.set(requestId, requestInfo)
    
    try {
      const response = await originalFetch(input, init)
      const endTime = performance.now()
      const responseTime = Math.round(endTime - startTime)
      
      // 记录性能指标
      recordApiPerformance(
        url,
        method,
        response.status,
        responseTime,
        requestInfo.requestSize,
        calculateResponseSize(response)
      )
      
      // 清理请求映射
      requestMap.delete(requestId)
      
      return response
    } catch (error) {
      const endTime = performance.now()
      const responseTime = Math.round(endTime - startTime)
      
      // 记录失败的请求
      recordApiPerformance(
        url,
        method,
        0, // 网络错误状态码
        responseTime,
        requestInfo.requestSize
      )
      
      // 清理请求映射
      requestMap.delete(requestId)
      
      throw error
    }
  }
}

/**
 * 设置XMLHttpRequest拦截器
 */
export function setupXHRInterceptor() {
  if (typeof window === 'undefined') return
  
  const originalXHROpen = XMLHttpRequest.prototype.open
  const originalXHRSend = XMLHttpRequest.prototype.send
  
  XMLHttpRequest.prototype.open = function(method: string, url: string | URL, ...args: any[]) {
    this._performanceData = {
      method,
      url: typeof url === 'string' ? url : url.href,
      startTime: 0
    }
    
    return originalXHROpen.apply(this, [method, url, ...args])
  }
  
  XMLHttpRequest.prototype.send = function(body?: Document | XMLHttpRequestBodyInit | null) {
    if (this._performanceData) {
      this._performanceData.startTime = performance.now()
      this._performanceData.requestSize = calculateRequestSize(body)
      
      // 监听请求完成
      const originalOnReadyStateChange = this.onreadystatechange
      this.onreadystatechange = function(event) {
        if (this.readyState === 4) {
          const endTime = performance.now()
          const responseTime = Math.round(endTime - this._performanceData.startTime)
          
          // 记录性能指标
          recordApiPerformance(
            this._performanceData.url,
            this._performanceData.method,
            this.status,
            responseTime,
            this._performanceData.requestSize,
            this.getResponseHeader('content-length') ? 
              parseInt(this.getResponseHeader('content-length')!, 10) : 0
          )
        }
        
        if (originalOnReadyStateChange) {
          return originalOnReadyStateChange.apply(this, [event])
        }
      }
    }
    
    return originalXHRSend.apply(this, [body])
  }
}

/**
 * 初始化所有拦截器
 */
export function initializeApiInterceptors() {
  setupFetchInterceptor()
  setupXHRInterceptor()
  
  console.debug('API performance interceptors initialized')
}

// 扩展XMLHttpRequest类型
declare global {
  interface XMLHttpRequest {
    _performanceData?: {
      method: string
      url: string
      startTime: number
      requestSize?: number
    }
  }
}