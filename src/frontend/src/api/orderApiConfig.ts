/**
 * 订单API配置和拦截器
 * 任务19：实现前端API调用服务 - 错误处理和重试机制
 */

import { AxiosError, AxiosResponse } from 'axios'
import { ElMessage, ElNotification } from 'element-plus'

// API错误类型定义
export interface ApiErrorResponse {
  code: number
  message: string
  errorCode?: string
  errorDetails?: any
  timestamp?: string
}

// 订单API错误码枚举
export enum OrderApiErrorCode {
  // 订单相关错误
  ORDER_NOT_FOUND = 'ORDER_NOT_FOUND',
  ORDER_STATUS_INVALID = 'ORDER_STATUS_INVALID',
  ORDER_CANNOT_BE_MODIFIED = 'ORDER_CANNOT_BE_MODIFIED',
  ORDER_ALREADY_EXISTS = 'ORDER_ALREADY_EXISTS',
  
  // 客户相关错误
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  CUSTOMER_INVALID = 'CUSTOMER_INVALID',
  
  // 产品相关错误
  PRODUCT_NOT_FOUND = 'PRODUCT_NOT_FOUND',
  PRODUCT_OUT_OF_STOCK = 'PRODUCT_OUT_OF_STOCK',
  PRODUCT_PRICE_CHANGED = 'PRODUCT_PRICE_CHANGED',
  
  // 业务逻辑错误
  INSUFFICIENT_INVENTORY = 'INSUFFICIENT_INVENTORY',
  INVALID_ORDER_AMOUNT = 'INVALID_ORDER_AMOUNT',
  PAYMENT_FAILED = 'PAYMENT_FAILED',
  SHIPPING_ADDRESS_INVALID = 'SHIPPING_ADDRESS_INVALID',
  
  // 权限错误
  PERMISSION_DENIED = 'PERMISSION_DENIED',
  OPERATION_NOT_ALLOWED = 'OPERATION_NOT_ALLOWED',
  
  // 系统错误
  SYSTEM_ERROR = 'SYSTEM_ERROR',
  DATABASE_ERROR = 'DATABASE_ERROR',
  NETWORK_ERROR = 'NETWORK_ERROR'
}

// 错误消息映射
const ERROR_MESSAGES: Record<string, string> = {
  [OrderApiErrorCode.ORDER_NOT_FOUND]: '订单不存在',
  [OrderApiErrorCode.ORDER_STATUS_INVALID]: '订单状态无效',
  [OrderApiErrorCode.ORDER_CANNOT_BE_MODIFIED]: '订单无法修改',
  [OrderApiErrorCode.ORDER_ALREADY_EXISTS]: '订单已存在',
  
  [OrderApiErrorCode.CUSTOMER_NOT_FOUND]: '客户不存在',
  [OrderApiErrorCode.CUSTOMER_INVALID]: '客户信息无效',
  
  [OrderApiErrorCode.PRODUCT_NOT_FOUND]: '产品不存在',
  [OrderApiErrorCode.PRODUCT_OUT_OF_STOCK]: '产品库存不足',
  [OrderApiErrorCode.PRODUCT_PRICE_CHANGED]: '产品价格已变更',
  
  [OrderApiErrorCode.INSUFFICIENT_INVENTORY]: '库存不足',
  [OrderApiErrorCode.INVALID_ORDER_AMOUNT]: '订单金额无效',
  [OrderApiErrorCode.PAYMENT_FAILED]: '支付失败',
  [OrderApiErrorCode.SHIPPING_ADDRESS_INVALID]: '收货地址无效',
  
  [OrderApiErrorCode.PERMISSION_DENIED]: '权限不足',
  [OrderApiErrorCode.OPERATION_NOT_ALLOWED]: '操作不被允许',
  
  [OrderApiErrorCode.SYSTEM_ERROR]: '系统错误',
  [OrderApiErrorCode.DATABASE_ERROR]: '数据库错误',
  [OrderApiErrorCode.NETWORK_ERROR]: '网络错误'
}

// 获取友好的错误消息
export function getFriendlyErrorMessage(errorCode: string, defaultMessage?: string): string {
  return ERROR_MESSAGES[errorCode] || defaultMessage || '未知错误'
}

// 错误处理器
export class OrderApiErrorHandler {
  /**
   * 处理API错误
   * @param error 错误对象
   * @param showNotification 是否显示通知
   */
  static handleError(error: AxiosError<ApiErrorResponse>, showNotification: boolean = true): void {
    const response = error.response
    const errorData = response?.data
    
    let errorMessage = '操作失败'
    let errorTitle = '错误'
    
    if (errorData) {
      errorMessage = getFriendlyErrorMessage(errorData.errorCode || '', errorData.message)
      
      // 根据错误类型设置不同的标题和处理方式
      switch (response?.status) {
        case 400:
          errorTitle = '请求错误'
          break
        case 401:
          errorTitle = '认证失败'
          // 可以在这里处理登录跳转
          break
        case 403:
          errorTitle = '权限不足'
          break
        case 404:
          errorTitle = '资源不存在'
          break
        case 409:
          errorTitle = '数据冲突'
          break
        case 422:
          errorTitle = '数据验证失败'
          break
        case 500:
          errorTitle = '服务器错误'
          break
        default:
          errorTitle = '网络错误'
      }
    } else if (error.code === 'NETWORK_ERROR' || error.message.includes('Network Error')) {
      errorMessage = '网络连接失败，请检查网络设置'
      errorTitle = '网络错误'
    } else if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      errorMessage = '请求超时，请稍后重试'
      errorTitle = '请求超时'
    }
    
    // 显示错误通知
    if (showNotification) {
      if (response?.status && response.status >= 500) {
        // 服务器错误使用通知
        ElNotification({
          title: errorTitle,
          message: errorMessage,
          type: 'error',
          duration: 5000
        })
      } else {
        // 客户端错误使用消息
        ElMessage({
          message: errorMessage,
          type: 'error',
          duration: 3000
        })
      }
    }
    
    // 记录错误日志
    console.error('订单API错误:', {
      url: error.config?.url,
      method: error.config?.method,
      status: response?.status,
      errorCode: errorData?.errorCode,
      message: errorData?.message,
      details: errorData?.errorDetails
    })
  }
  
  /**
   * 处理成功响应
   * @param response 响应对象
   * @param showSuccessMessage 是否显示成功消息
   */
  static handleSuccess(response: AxiosResponse, showSuccessMessage: boolean = false): void {
    const data = response.data
    
    if (showSuccessMessage && data?.message) {
      ElMessage({
        message: data.message,
        type: 'success',
        duration: 2000
      })
    }
  }
}

// 请求重试配置
export interface RetryConfig {
  retries: number
  retryDelay: number
  retryCondition?: (error: AxiosError) => boolean
}

// 默认重试配置
export const DEFAULT_RETRY_CONFIG: RetryConfig = {
  retries: 3,
  retryDelay: 1000,
  retryCondition: (error: AxiosError) => {
    // 只对网络错误和5xx服务器错误进行重试
    return !error.response || (error.response.status >= 500 && error.response.status < 600)
  }
}

// 请求拦截器配置
export const requestInterceptor = {
  onFulfilled: (config: any) => {
    // 添加请求时间戳
    config.metadata = { startTime: Date.now() }
    
    // 添加通用请求头
    config.headers = {
      ...config.headers,
      'X-Requested-With': 'XMLHttpRequest',
      'X-Client-Version': '1.0.0'
    }
    
    return config
  },
  onRejected: (error: any) => {
    return Promise.reject(error)
  }
}

// 响应拦截器配置
export const responseInterceptor = {
  onFulfilled: (response: AxiosResponse) => {
    // 计算请求耗时
    const endTime = Date.now()
    const startTime = response.config.metadata?.startTime || endTime
    const duration = endTime - startTime
    
    // 记录慢请求
    if (duration > 3000) {
      console.warn(`慢请求警告: ${response.config.url} 耗时 ${duration}ms`)
    }
    
    // 处理业务错误
    const data = response.data
    if (data && data.code !== undefined && data.code !== 200) {
      const error = new Error(data.message || '业务处理失败') as any
      error.response = response
      error.isBusinessError = true
      throw error
    }
    
    return response
  },
  onRejected: (error: AxiosError<ApiErrorResponse>) => {
    // 处理API错误
    OrderApiErrorHandler.handleError(error)
    return Promise.reject(error)
  }
}

// 加载状态管理
export class LoadingManager {
  private static loadingCount = 0
  private static loadingInstance: any = null
  
  static show(text: string = '加载中...') {
    this.loadingCount++
    if (this.loadingCount === 1) {
      // 这里可以集成Element Plus的Loading组件
      console.log('显示加载状态:', text)
    }
  }
  
  static hide() {
    this.loadingCount = Math.max(0, this.loadingCount - 1)
    if (this.loadingCount === 0) {
      console.log('隐藏加载状态')
    }
  }
}

// API性能监控
export class ApiPerformanceMonitor {
  private static metrics: Map<string, number[]> = new Map()
  
  static recordRequest(url: string, duration: number) {
    if (!this.metrics.has(url)) {
      this.metrics.set(url, [])
    }
    
    const durations = this.metrics.get(url)!
    durations.push(duration)
    
    // 只保留最近100次请求的数据
    if (durations.length > 100) {
      durations.shift()
    }
  }
  
  static getAverageResponseTime(url: string): number {
    const durations = this.metrics.get(url)
    if (!durations || durations.length === 0) {
      return 0
    }
    
    const sum = durations.reduce((a, b) => a + b, 0)
    return sum / durations.length
  }
  
  static getSlowRequests(threshold: number = 2000): Array<{ url: string; avgTime: number }> {
    const slowRequests: Array<{ url: string; avgTime: number }> = []
    
    this.metrics.forEach((durations, url) => {
      const avgTime = this.getAverageResponseTime(url)
      if (avgTime > threshold) {
        slowRequests.push({ url, avgTime })
      }
    })
    
    return slowRequests.sort((a, b) => b.avgTime - a.avgTime)
  }
}

// 导出配置
export const orderApiConfig = {
  errorHandler: OrderApiErrorHandler,
  retryConfig: DEFAULT_RETRY_CONFIG,
  requestInterceptor,
  responseInterceptor,
  loadingManager: LoadingManager,
  performanceMonitor: ApiPerformanceMonitor
}