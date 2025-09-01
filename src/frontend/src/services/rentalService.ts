/**
 * 租赁业务API服务类
 * 提供完整的租赁业务API调用服务，包含错误处理和重试机制
 * 
 * @author Kiro
 * @date 2025-01-28
 */

import { 
  getRentalStats,
  getRentalTrendData,
  getDeviceUtilizationData,
  getRentalRevenueAnalysis,
  getTodayStats,
  getDeviceStatusStats,
  getTopDevices,
  getRentalOrders,
  getRentalDevices,
  getRentalCustomers,
  createRentalOrder,
  updateRentalOrder,
  deleteRentalOrder,
  createRentalDevice,
  updateRentalDevice,
  deleteRentalDevice,
  createRentalCustomer,
  updateRentalCustomer,
  deleteRentalCustomer,
  exportRentalReport
} from '@/api/rental'

import type {
  RentalStats,
  RentalTrendData,
  DeviceUtilizationData,
  RentalRevenueAnalysis,
  TodayStats,
  DeviceStatusStats,
  RentalRecord,
  RentalDevice,
  RentalCustomer,
  DeviceQueryParams,
  RentalRecordQueryParams,
  TrendQueryParams,
  RevenueAnalysisParams,
  RentalRecordFormData,
  RentalDeviceFormData,
  RentalCustomerFormData,
  ExportParams,
  ApiError
} from '@/types/rental'

import type { ApiResponse, PageResponse } from '@/types'

/**
 * 租赁API服务类
 * 提供统一的API调用接口和错误处理
 */
export class RentalService {
  private static instance: RentalService
  private retryCount = 3
  private retryDelay = 1000

  private constructor() {}

  /**
   * 获取单例实例
   */
  public static getInstance(): RentalService {
    if (!RentalService.instance) {
      RentalService.instance = new RentalService()
    }
    return RentalService.instance
  }

  /**
   * 通用重试机制
   */
  private async withRetry<T>(
    apiCall: () => Promise<ApiResponse<T>>,
    retries: number = this.retryCount
  ): Promise<T> {
    try {
      const response = await apiCall()
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || 'API调用失败')
      }
    } catch (error: any) {
      if (retries > 0 && this.shouldRetry(error)) {
        console.warn(`API调用失败，${this.retryDelay}ms后重试，剩余重试次数: ${retries}`)
        await this.delay(this.retryDelay)
        return this.withRetry(apiCall, retries - 1)
      }
      throw this.formatError(error)
    }
  }

  /**
   * 判断是否应该重试
   */
  private shouldRetry(error: any): boolean {
    // 网络错误或服务器错误才重试
    return (
      error.code === 'NETWORK_ERROR' ||
      error.response?.status >= 500 ||
      error.code === 'TIMEOUT'
    )
  }

  /**
   * 延迟函数
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * 格式化错误信息
   */
  private formatError(error: any): ApiError {
    return {
      code: error.response?.status || error.code || 500,
      message: error.response?.data?.message || error.message || '未知错误',
      details: error.response?.data?.details || error.stack,
      timestamp: Date.now()
    }
  }

  // ==================== 统计数据API ====================

  /**
   * 获取租赁统计数据
   */
  async getRentalStats(): Promise<RentalStats> {
    return this.withRetry(() => getRentalStats())
  }

  /**
   * 获取今日统计数据
   */
  async getTodayStats(): Promise<TodayStats> {
    return this.withRetry(() => getTodayStats())
  }

  /**
   * 获取设备状态统计
   */
  async getDeviceStatusStats(): Promise<DeviceStatusStats> {
    return this.withRetry(() => getDeviceStatusStats())
  }

  /**
   * 获取TOP设备排行
   */
  async getTopDevices(limit: number = 5): Promise<DeviceUtilizationData[]> {
    return this.withRetry(() => getTopDevices(limit))
  }

  // ==================== 趋势和分析数据API ====================

  /**
   * 获取租赁趋势数据
   */
  async getRentalTrendData(params: TrendQueryParams): Promise<RentalTrendData[]> {
    return this.withRetry(() => getRentalTrendData(params))
  }

  /**
   * 获取设备利用率数据
   */
  async getDeviceUtilizationData(params?: DeviceQueryParams): Promise<DeviceUtilizationData[]> {
    return this.withRetry(() => getDeviceUtilizationData(params))
  }

  /**
   * 获取租赁收入分析数据
   */
  async getRentalRevenueAnalysis(params: RevenueAnalysisParams): Promise<RentalRevenueAnalysis> {
    return this.withRetry(() => getRentalRevenueAnalysis(params))
  }

  // ==================== 租赁记录管理API ====================

  /**
   * 获取租赁记录列表
   */
  async getRentalOrders(params: RentalRecordQueryParams): Promise<PageResponse<RentalRecord>> {
    return this.withRetry(() => getRentalOrders(params))
  }

  /**
   * 创建租赁记录
   */
  async createRentalOrder(data: RentalRecordFormData): Promise<RentalRecord> {
    return this.withRetry(() => createRentalOrder(data))
  }

  /**
   * 更新租赁记录
   */
  async updateRentalOrder(id: number, data: Partial<RentalRecordFormData>): Promise<RentalRecord> {
    return this.withRetry(() => updateRentalOrder(id, data))
  }

  /**
   * 删除租赁记录
   */
  async deleteRentalOrder(id: number): Promise<void> {
    return this.withRetry(() => deleteRentalOrder(id))
  } 
 // ==================== 设备管理API ====================

  /**
   * 获取设备列表
   */
  async getRentalDevices(params?: DeviceQueryParams): Promise<PageResponse<RentalDevice>> {
    return this.withRetry(() => getRentalDevices(params))
  }

  /**
   * 创建设备
   */
  async createRentalDevice(data: RentalDeviceFormData): Promise<RentalDevice> {
    return this.withRetry(() => createRentalDevice(data))
  }

  /**
   * 更新设备
   */
  async updateRentalDevice(id: number, data: Partial<RentalDeviceFormData>): Promise<RentalDevice> {
    return this.withRetry(() => updateRentalDevice(id, data))
  }

  /**
   * 删除设备
   */
  async deleteRentalDevice(id: number): Promise<void> {
    return this.withRetry(() => deleteRentalDevice(id))
  }

  // ==================== 客户管理API ====================

  /**
   * 获取客户列表
   */
  async getRentalCustomers(params?: {
    page?: number
    pageSize?: number
    keyword?: string
    customerType?: string
    region?: string
  }): Promise<PageResponse<RentalCustomer>> {
    return this.withRetry(() => getRentalCustomers(params))
  }

  /**
   * 创建客户
   */
  async createRentalCustomer(data: RentalCustomerFormData): Promise<RentalCustomer> {
    return this.withRetry(() => createRentalCustomer(data))
  }

  /**
   * 更新客户
   */
  async updateRentalCustomer(id: number, data: Partial<RentalCustomerFormData>): Promise<RentalCustomer> {
    return this.withRetry(() => updateRentalCustomer(id, data))
  }

  /**
   * 删除客户
   */
  async deleteRentalCustomer(id: number): Promise<void> {
    return this.withRetry(() => deleteRentalCustomer(id))
  }

  // ==================== 导出功能API ====================

  /**
   * 导出租赁报表
   */
  async exportRentalReport(params: ExportParams): Promise<Blob> {
    try {
      return await exportRentalReport(params)
    } catch (error: any) {
      throw this.formatError(error)
    }
  }

  // ==================== 批量操作API ====================

  /**
   * 批量获取数据（用于页面初始化）
   */
  async loadDashboardData(): Promise<{
    stats: RentalStats
    todayStats: TodayStats
    deviceStatusStats: DeviceStatusStats
    topDevices: DeviceUtilizationData[]
  }> {
    try {
      const [stats, todayStats, deviceStatusStats, topDevices] = await Promise.all([
        this.getRentalStats(),
        this.getTodayStats(),
        this.getDeviceStatusStats(),
        this.getTopDevices(5)
      ])

      return {
        stats,
        todayStats,
        deviceStatusStats,
        topDevices
      }
    } catch (error: any) {
      throw this.formatError(error)
    }
  }

  /**
   * 批量获取图表数据
   */
  async loadChartData(period: 'daily' | 'weekly' | 'monthly' | 'quarterly' | 'yearly' = 'monthly'): Promise<{
    trendData: RentalTrendData[]
    deviceData: DeviceUtilizationData[]
    revenueAnalysis: RentalRevenueAnalysis
  }> {
    try {
      const [trendData, deviceData, revenueAnalysis] = await Promise.all([
        this.getRentalTrendData({ period }),
        this.getDeviceUtilizationData(),
        this.getRentalRevenueAnalysis({ period: 'monthly' })
      ])

      return {
        trendData,
        deviceData,
        revenueAnalysis
      }
    } catch (error: any) {
      throw this.formatError(error)
    }
  }

  // ==================== 缓存管理 ====================

  private cache = new Map<string, { data: any; timestamp: number; ttl: number }>()
  private defaultTTL = 5 * 60 * 1000 // 5分钟缓存

  /**
   * 带缓存的API调用
   */
  async withCache<T>(
    key: string,
    apiCall: () => Promise<T>,
    ttl: number = this.defaultTTL
  ): Promise<T> {
    const cached = this.cache.get(key)
    const now = Date.now()

    if (cached && now - cached.timestamp < cached.ttl) {
      console.log(`使用缓存数据: ${key}`)
      return cached.data
    }

    try {
      const data = await apiCall()
      this.cache.set(key, { data, timestamp: now, ttl })
      console.log(`缓存数据: ${key}`)
      return data
    } catch (error) {
      // 如果有过期缓存，在API失败时使用
      if (cached) {
        console.warn(`API调用失败，使用过期缓存: ${key}`)
        return cached.data
      }
      throw error
    }
  }

  /**
   * 清除缓存
   */
  clearCache(key?: string): void {
    if (key) {
      this.cache.delete(key)
    } else {
      this.cache.clear()
    }
  }
}

// 导出单例实例
export const rentalService = RentalService.getInstance()