/**
 * API错误处理工具
 * 提供统一的API错误处理和用户友好的错误提示
 * 
 * @author Kiro
 * @date 2025-01-28
 */

import { ElMessage, ElNotification } from 'element-plus'
import type { ApiError } from '@/types/rental'

/**
 * 错误类型枚举
 */
export enum ErrorType {
  NETWORK = 'NETWORK',
  TIMEOUT = 'TIMEOUT',
  UNAUTHORIZED = 'UNAUTHORIZED',
  FORBIDDEN = 'FORBIDDEN',
  NOT_FOUND = 'NOT_FOUND',
  VALIDATION = 'VALIDATION',
  SERVER = 'SERVER',
  UNKNOWN = 'UNKNOWN'
}

/**
 * 错误处理配置
 */
interface ErrorHandlerConfig {
  showMessage?: boolean
  showNotification?: boolean
  logError?: boolean
  redirectOnAuth?: boolean
}

/**
 * 默认错误处理配置
 */
const defaultConfig: ErrorHandlerConfig = {
  showMessage: true,
  showNotification: false,
  logError: true,
  redirectOnAuth: true
}

/**
 * API错误处理器类
 */
export class ApiErrorHandler {
  private static instance: ApiErrorHandler
  private config: ErrorHandlerConfig

  private constructor(config: ErrorHandlerConfig = defaultConfig) {
    this.config = { ...defaultConfig, ...config }
  }

  /**
   * 获取单例实例
   */
  public static getInstance(config?: ErrorHandlerConfig): ApiErrorHandler {
    if (!ApiErrorHandler.instance) {
      ApiErrorHandler.instance = new ApiErrorHandler(config)
    }
    return ApiErrorHandler.instance
  }

  /**
   * 处理API错误
   */
  public handleError(error: any, config?: ErrorHandlerConfig): ApiError {
    const finalConfig = { ...this.config, ...config }
    const apiError = this.formatError(error)

    if (finalConfig.logError) {
      this.logError(apiError, error)
    }

    if (finalConfig.showMessage || finalConfig.showNotification) {
      this.showErrorMessage(apiError, finalConfig)
    }

    if (finalConfig.redirectOnAuth && this.isAuthError(apiError)) {
      this.handleAuthError(apiError)
    }

    return apiError
  }

  /**
   * 格式化错误信息
   */
  private formatError(error: any): ApiError {
    // 网络错误
    if (!error.response) {
      return {
        code: 0,
        message: '网络连接失败，请检查网络设置',
        details: error.message,
        timestamp: Date.now()
      }
    }

    // HTTP错误
    const { status, data } = error.response
    
    return {
      code: status,
      message: this.getErrorMessage(status, data),
      details: data?.details || error.message,
      validationErrors: data?.validationErrors,
      timestamp: Date.now()
    }
  }

  /**
   * 获取用户友好的错误消息
   */
  private getErrorMessage(status: number, data: any): string {
    // 优先使用服务器返回的错误消息
    if (data?.message) {
      return data.message
    }

    // 根据状态码返回默认消息
    switch (status) {
      case 400:
        return '请求参数错误'
      case 401:
        return '登录已过期，请重新登录'
      case 403:
        return '权限不足，无法访问该资源'
      case 404:
        return '请求的资源不存在'
      case 408:
        return '请求超时，请稍后重试'
      case 422:
        return '数据验证失败'
      case 429:
        return '请求过于频繁，请稍后重试'
      case 500:
        return '服务器内部错误'
      case 502:
        return '网关错误'
      case 503:
        return '服务暂时不可用'
      case 504:
        return '网关超时'
      default:
        return `请求失败 (${status})`
    }
  }

  /**
   * 显示错误消息
   */
  private showErrorMessage(apiError: ApiError, config: ErrorHandlerConfig) {
    const message = apiError.message

    if (config.showNotification) {
      ElNotification({
        title: '错误',
        message,
        type: 'error',
        duration: 5000
      })
    } else if (config.showMessage) {
      ElMessage({
        message,
        type: 'error',
        duration: 3000
      })
    }
  }

  /**
   * 记录错误日志
   */
  private logError(apiError: ApiError, originalError: any) {
    console.group(`🚨 API错误 [${apiError.code}]`)
    console.error('错误消息:', apiError.message)
    console.error('错误详情:', apiError.details)
    console.error('时间戳:', new Date(apiError.timestamp).toLocaleString())
    
    if (apiError.validationErrors) {
      console.error('验证错误:', apiError.validationErrors)
    }
    
    console.error('原始错误:', originalError)
    console.groupEnd()
  }

  /**
   * 判断是否为认证错误
   */
  private isAuthError(apiError: ApiError): boolean {
    return apiError.code === 401
  }

  /**
   * 处理认证错误
   */
  private handleAuthError(apiError: ApiError) {
    // 清除本地存储的认证信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 延迟跳转，让用户看到错误消息
    setTimeout(() => {
      window.location.href = '/login'
    }, 1500)
  }

  /**
   * 获取错误类型
   */
  public getErrorType(apiError: ApiError): ErrorType {
    if (apiError.code === 0) {
      return ErrorType.NETWORK
    }
    
    switch (apiError.code) {
      case 401:
        return ErrorType.UNAUTHORIZED
      case 403:
        return ErrorType.FORBIDDEN
      case 404:
        return ErrorType.NOT_FOUND
      case 408:
      case 504:
        return ErrorType.TIMEOUT
      case 422:
        return ErrorType.VALIDATION
      case 500:
      case 502:
      case 503:
        return ErrorType.SERVER
      default:
        return ErrorType.UNKNOWN
    }
  }

  /**
   * 判断错误是否可重试
   */
  public isRetryableError(apiError: ApiError): boolean {
    const retryableCodes = [0, 408, 429, 500, 502, 503, 504]
    return retryableCodes.includes(apiError.code)
  }

  /**
   * 更新配置
   */
  public updateConfig(config: Partial<ErrorHandlerConfig>) {
    this.config = { ...this.config, ...config }
  }
}

// 导出单例实例
export const apiErrorHandler = ApiErrorHandler.getInstance()

/**
 * 快捷错误处理函数
 */
export function handleApiError(error: any, config?: ErrorHandlerConfig): ApiError {
  return apiErrorHandler.handleError(error, config)
}

/**
 * 静默错误处理（不显示消息）
 */
export function handleApiErrorSilently(error: any): ApiError {
  return apiErrorHandler.handleError(error, {
    showMessage: false,
    showNotification: false
  })
}

/**
 * 验证错误格式化
 */
export function formatValidationErrors(errors: any[]): string {
  if (!errors || !Array.isArray(errors)) {
    return ''
  }
  
  return errors.map(error => error.message || error).join('; ')
}