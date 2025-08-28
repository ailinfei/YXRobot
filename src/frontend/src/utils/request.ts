/**
 * API请求工具函数
 * 提供统一错误处理、请求缓存、重试机制
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

// 扩展Axios请求配置类型
declare module 'axios' {
  interface InternalAxiosRequestConfig {
    metadata?: {
      requestId?: string
      [key: string]: any
    }
  }
}

// 请求配置接口
export interface RequestConfig extends AxiosRequestConfig {
  showLoading?: boolean
  showError?: boolean
  cache?: boolean
  cacheTime?: number
  retry?: number
  retryDelay?: number
}

// 响应数据接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: number
}

// 缓存项接口
interface CacheItem {
  data: any
  timestamp: number
  expiry: number
}

// 请求缓存
const requestCache = new Map<string, CacheItem>()

// 正在进行的请求
const pendingRequests = new Map<string, Promise<any>>()

// 创建axios实例
const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/',
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // 请求拦截器
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      // 添加认证token
      const token = localStorage.getItem('token')
      if (token) {
        config.headers = config.headers || {}
        config.headers.Authorization = `Bearer ${token}`
      }

      // 添加请求ID用于缓存和去重
      const requestId = generateRequestId(config)
      config.metadata = { requestId }

      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  instance.interceptors.response.use(
    (response: AxiosResponse<ApiResponse>) => {
      const { data } = response

      // 统一处理业务错误
      if (data.code !== 200 && data.code !== 0) {
        const error = new Error(data.message || '请求失败')
          ; (error as any).code = data.code
        return Promise.reject(error)
      }

      return data
    },
    (error: AxiosError) => {
      // 处理HTTP错误
      handleHttpError(error)
      return Promise.reject(error)
    }
  )

  return instance
}

// 创建请求实例
const request = createAxiosInstance()

/**
 * 生成请求ID
 */
function generateRequestId(config: AxiosRequestConfig | InternalAxiosRequestConfig): string {
  const { method, url, params, data } = config
  return `${method}_${url}_${JSON.stringify(params)}_${JSON.stringify(data)}`
}

/**
 * 处理HTTP错误
 */
function handleHttpError(error: AxiosError) {
  const { response, message } = error
  let errorMessage = '请求失败'

  if (response) {
    const { status, data } = response
    switch (status) {
      case 400:
        errorMessage = '请求参数错误'
        break
      case 401:
        errorMessage = '未授权，请重新登录'
        // 清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/admin/login'
        break
      case 403:
        errorMessage = '权限不足'
        break
      case 404:
        errorMessage = '请求的资源不存在'
        break
      case 500:
        errorMessage = '服务器内部错误'
        break
      case 502:
        errorMessage = '网关错误'
        break
      case 503:
        errorMessage = '服务不可用'
        break
      default:
        errorMessage = (data as any)?.message || `请求失败 (${status})`
    }
  } else if (message.includes('timeout')) {
    errorMessage = '请求超时'
  } else if (message.includes('Network Error')) {
    errorMessage = '网络连接失败'
  }

  console.error('API Error:', errorMessage, error)
  return errorMessage
}

/**
 * 获取缓存数据
 */
function getCacheData(key: string): any | null {
  const item = requestCache.get(key)
  if (!item) return null

  const now = Date.now()
  if (now > item.expiry) {
    requestCache.delete(key)
    return null
  }

  return item.data
}

/**
 * 设置缓存数据
 */
function setCacheData(key: string, data: any, cacheTime: number) {
  const now = Date.now()
  requestCache.set(key, {
    data,
    timestamp: now,
    expiry: now + cacheTime
  })
}

/**
 * 清除缓存
 */
export function clearCache(pattern?: string) {
  if (pattern) {
    const regex = new RegExp(pattern)
    for (const [key] of requestCache) {
      if (regex.test(key)) {
        requestCache.delete(key)
      }
    }
  } else {
    requestCache.clear()
  }
}

/**
 * 通用请求方法
 */
async function makeRequest<T = any>(
  config: RequestConfig
): Promise<ApiResponse<T>> {
  const {
    showLoading = false,
    showError = true,
    cache = false,
    cacheTime = 5 * 60 * 1000, // 5分钟
    retry = 0,
    retryDelay = 1000,
    ...axiosConfig
  } = config

  const requestId = generateRequestId(axiosConfig)

  // 检查缓存
  if (cache && axiosConfig.method?.toLowerCase() === 'get') {
    const cachedData = getCacheData(requestId)
    if (cachedData) {
      return cachedData
    }
  }

  // 检查是否有相同的请求正在进行
  if (pendingRequests.has(requestId)) {
    return pendingRequests.get(requestId)!
  }

  // 显示加载状态
  let loadingInstance: any = null
  if (showLoading) {
    loadingInstance = ElLoading.service({
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }

  // 创建请求Promise
  const requestPromise = async (): Promise<ApiResponse<T>> => {
    let lastError: any

    for (let attempt = 0; attempt <= retry; attempt++) {
      try {
        const result = await request(axiosConfig)

        // 缓存GET请求的结果
        if (cache && axiosConfig.method?.toLowerCase() === 'get') {
          setCacheData(requestId, result, cacheTime)
        }

        return result
      } catch (error) {
        lastError = error

        // 如果不是最后一次尝试，等待后重试
        if (attempt < retry) {
          await new Promise(resolve => setTimeout(resolve, retryDelay * (attempt + 1)))
          continue
        }

        // 显示错误信息
        if (showError) {
          const errorMessage = handleHttpError(error as AxiosError)
          ElMessage.error(errorMessage)
        }

        throw error
      }
    }

    throw lastError
  }

  // 执行请求
  const promise = requestPromise()
  pendingRequests.set(requestId, promise)

  try {
    const result = await promise
    return result
  } finally {
    // 清理
    pendingRequests.delete(requestId)
    if (loadingInstance) {
      loadingInstance.close()
    }
  }
}

/**
 * GET请求
 */
export function get<T = any>(
  url: string,
  params?: any,
  config?: Omit<RequestConfig, 'method' | 'url' | 'params'>
): Promise<ApiResponse<T>> {
  return makeRequest<T>({
    method: 'GET',
    url,
    params,
    cache: true, // GET请求默认开启缓存
    ...config
  })
}

/**
 * POST请求
 */
export function post<T = any>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, 'method' | 'url' | 'data'>
): Promise<ApiResponse<T>> {
  return makeRequest<T>({
    method: 'POST',
    url,
    data,
    ...config
  })
}

/**
 * PUT请求
 */
export function put<T = any>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, 'method' | 'url' | 'data'>
): Promise<ApiResponse<T>> {
  return makeRequest<T>({
    method: 'PUT',
    url,
    data,
    ...config
  })
}

/**
 * DELETE请求
 */
export function del<T = any>(
  url: string,
  config?: Omit<RequestConfig, 'method' | 'url'>
): Promise<ApiResponse<T>> {
  return makeRequest<T>({
    method: 'DELETE',
    url,
    ...config
  })
}

/**
 * PATCH请求
 */
export function patch<T = any>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, 'method' | 'url' | 'data'>
): Promise<ApiResponse<T>> {
  return makeRequest<T>({
    method: 'PATCH',
    url,
    data,
    ...config
  })
}

/**
 * 文件上传
 */
export function upload<T = any>(
  url: string,
  file: File | FormData,
  config?: Omit<RequestConfig, 'method' | 'url' | 'data' | 'headers'>
): Promise<ApiResponse<T>> {
  const formData = file instanceof FormData ? file : new FormData()
  if (file instanceof File) {
    formData.append('file', file)
  }

  return makeRequest<T>({
    method: 'POST',
    url,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    showLoading: true,
    ...config
  })
}

/**
 * 批量请求
 */
export async function batchRequest<T = any>(
  requests: RequestConfig[],
  options: {
    concurrent?: number
    failFast?: boolean
  } = {}
): Promise<Array<ApiResponse<T> | Error>> {
  const { concurrent = 5, failFast = false } = options
  const results: Array<ApiResponse<T> | Error> = []

  // 分批处理请求
  for (let i = 0; i < requests.length; i += concurrent) {
    const batch = requests.slice(i, i + concurrent)
    const batchPromises = batch.map(async (config) => {
      try {
        return await makeRequest<T>(config)
      } catch (error) {
        if (failFast) {
          throw error
        }
        return error as Error
      }
    })

    const batchResults = await Promise.all(batchPromises)
    results.push(...batchResults)

    // 如果启用快速失败模式，检查是否有错误
    if (failFast && batchResults.some(result => result instanceof Error)) {
      break
    }
  }

  return results
}

/**
 * 请求重试装饰器
 */
export function withRetry<T extends (...args: any[]) => Promise<any>>(
  fn: T,
  maxRetries = 3,
  delay = 1000
): T {
  return (async (...args: any[]) => {
    let lastError: any

    for (let attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        return await fn(...args)
      } catch (error) {
        lastError = error

        if (attempt < maxRetries) {
          await new Promise(resolve => setTimeout(resolve, delay * (attempt + 1)))
          continue
        }
      }
    }

    throw lastError
  }) as T
}

/**
 * 请求防抖
 */
export function debounceRequest<T extends (...args: any[]) => Promise<any>>(
  fn: T,
  delay = 300
): T {
  let timeoutId: NodeJS.Timeout | null = null
  let lastPromise: Promise<any> | null = null

  return ((...args: any[]) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }

    if (lastPromise) {
      return lastPromise
    }

    lastPromise = new Promise((resolve, reject) => {
      timeoutId = setTimeout(async () => {
        try {
          const result = await fn(...args)
          resolve(result)
        } catch (error) {
          reject(error)
        } finally {
          lastPromise = null
          timeoutId = null
        }
      }, delay)
    })

    return lastPromise
  }) as T
}

// 导出axios实例供高级用法
export { request as axiosInstance }

// 默认导出
export default {
  get,
  post,
  put,
  delete: del,
  patch,
  upload,
  batchRequest,
  clearCache,
  withRetry,
  debounceRequest
}