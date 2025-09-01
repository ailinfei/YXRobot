/**
 * Mock数据拦截器
 * 在开发环境中拦截API请求并返回Mock数据
 * 
 * @author Kiro
 * @date 2025-01-28
 */

import type { AxiosInstance } from 'axios'

/**
 * 设置Mock拦截器
 */
export function setupMockInterceptor(apiClient: AxiosInstance) {
  // 只在开发环境且启用Mock时生效
  const isDevelopment = import.meta.env.DEV
  const useMock = import.meta.env.VITE_USE_MOCK !== 'false'
  
  if (!isDevelopment || !useMock) {
    return
  }
  
  console.log('🔧 Mock拦截器已启用')
  
  // 这里可以添加特定的Mock拦截逻辑
  // 目前主要依赖API文件中的isDevelopment判断
}

/**
 * Mock数据生成工具
 */
export class MockDataGenerator {
  /**
   * 生成随机数字
   */
  static randomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min
  }
  
  /**
   * 生成随机浮点数
   */
  static randomFloat(min: number, max: number, decimals: number = 2): number {
    return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
  }
  
  /**
   * 生成随机日期
   */
  static randomDate(start: Date, end: Date): string {
    const startTime = start.getTime()
    const endTime = end.getTime()
    const randomTime = startTime + Math.random() * (endTime - startTime)
    return new Date(randomTime).toISOString().slice(0, 10)
  }
  
  /**
   * 从数组中随机选择元素
   */
  static randomChoice<T>(array: T[]): T {
    return array[Math.floor(Math.random() * array.length)]
  }
  
  /**
   * 生成随机字符串
   */
  static randomString(length: number, chars: string = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'): string {
    let result = ''
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    return result
  }
  
  /**
   * 生成设备ID
   */
  static generateDeviceId(): string {
    return `YX-${String(this.randomNumber(1, 9999)).padStart(4, '0')}`
  }
  
  /**
   * 生成订单号
   */
  static generateOrderNumber(): string {
    const year = new Date().getFullYear()
    const random = String(this.randomNumber(100000, 999999))
    return `R${year}${random}`
  }
}

/**
 * API响应包装器
 */
export function createMockResponse<T>(data: T, code: number = 200, message: string = 'success') {
  return {
    code,
    message,
    data,
    timestamp: Date.now()
  }
}

/**
 * 分页响应包装器
 */
export function createMockPageResponse<T>(
  list: T[],
  page: number = 1,
  pageSize: number = 20,
  total?: number
) {
  const actualTotal = total ?? list.length
  const totalPages = Math.ceil(actualTotal / pageSize)
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const paginatedList = list.slice(start, end)
  
  return createMockResponse({
    list: paginatedList,
    total: actualTotal,
    page,
    pageSize,
    totalPages
  })
}

/**
 * 模拟API延迟
 */
export function mockDelay(ms: number = 500): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}