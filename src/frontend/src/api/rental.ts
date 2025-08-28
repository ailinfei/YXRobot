/**
 * 租赁数据相关API
 * 提供租赁业务数据分析和管理功能
 */

import apiClient from './config'
import type { ApiResponse, PageResponse } from './types'
import { mockRentalAPI } from './mock/rental'

// 开发环境使用Mock数据
const isDevelopment = import.meta.env.DEV

// 租赁业务统计数据接口
export interface RentalStats {
  totalRentalRevenue: number          // 总租赁收入
  totalRentalDevices: number          // 租赁设备总数
  activeRentalDevices: number         // 活跃租赁设备数
  deviceUtilizationRate: number       // 设备利用率
  averageRentalPeriod: number         // 平均租赁周期（天）
  totalRentalOrders: number           // 租赁订单总数
  revenueGrowthRate: number           // 收入增长率
  deviceGrowthRate: number            // 设备增长率
}

// 租赁趋势数据接口
export interface RentalTrendData {
  date: string
  revenue: number
  orderCount: number
  deviceCount: number
  utilizationRate: number
}

// 设备利用率数据接口
export interface DeviceUtilizationData {
  deviceId: string
  deviceModel: string
  utilizationRate: number
  totalRentalDays: number
  totalAvailableDays: number
  currentStatus: 'active' | 'idle' | 'maintenance' | 'renting'
  lastRentalDate?: string
  // 新增字段 - 设备增强信息
  signalStrength?: number              // 信号强度 (0-100)
  performanceScore?: number            // 性能评分 (0-100)
  region?: string                      // 所在地区
  maintenanceStatus?: 'normal' | 'warning' | 'urgent'  // 维护状态
  predictedFailureRisk?: number        // 预测故障风险 (0-100)
  networkType?: '4G' | '5G' | 'WiFi' | 'Ethernet'     // 网络类型
  lastMaintenanceDate?: string         // 最后维护时间
  nextMaintenanceDate?: string         // 下次维护时间
}

// 租赁订单接口
export interface RentalOrder {
  id: number
  orderNumber: string
  customerId: number
  customerName: string
  customerPhone: string
  deviceModel: string
  deviceId: string
  rentalPeriod: number                // 租赁天数
  dailyRate: number                   // 日租金
  totalAmount: number                 // 总金额
  platform: string                    // 租赁平台
  status: 'pending' | 'active' | 'expired' | 'returned' | 'cancelled'
  startDate: string                   // 租赁开始日期
  endDate: string                     // 租赁结束日期
  createdAt: string
  updatedAt: string
}

// 租赁收入分析数据接口
export interface RentalRevenueAnalysis {
  period: string
  totalRevenue: number
  orderCount: number
  averageOrderValue: number
  topPerformingDevices: Array<{
    deviceModel: string
    revenue: number
    orderCount: number
    utilizationRate: number
  }>
  regionDistribution: Array<{
    region: string
    revenue: number
    orderCount: number
    deviceCount: number
  }>
}

// 获取租赁业务统计数据
export const getRentalStats = (): Promise<ApiResponse<RentalStats>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalStats()
  }
  return apiClient.get('/api/rental/stats')
}

// 获取租赁趋势数据
export const getRentalTrendData = (params: {
  period: 'daily' | 'weekly' | 'monthly' | 'quarterly' | 'yearly'
  startDate?: string
  endDate?: string
}): Promise<ApiResponse<RentalTrendData[]>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalTrendData(params)
  }
  return apiClient.get('/api/rental/trend', { params })
}

// 获取设备利用率数据
export const getDeviceUtilizationData = (params?: {
  deviceModel?: string
  status?: string
  sortBy?: 'utilizationRate' | 'totalRentalDays' | 'lastRentalDate'
  sortOrder?: 'asc' | 'desc'
}): Promise<ApiResponse<DeviceUtilizationData[]>> => {
  if (isDevelopment) {
    return mockRentalAPI.getDeviceUtilizationData(params)
  }
  return apiClient.get('/api/rental/device-utilization', { params })
}

// 获取租赁订单列表
export const getRentalOrders = (params: {
  page?: number
  pageSize?: number
  status?: string
  customerId?: number
  deviceModel?: string
  platform?: string
  startDate?: string
  endDate?: string
  keyword?: string
}): Promise<ApiResponse<PageResponse<RentalOrder>>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalOrders(params)
  }
  return apiClient.get('/api/rental/orders', { params })
}

// 获取租赁收入分析数据
export const getRentalRevenueAnalysis = (params: {
  period: 'monthly' | 'quarterly' | 'yearly'
  year?: number
  quarter?: number
  month?: number
}): Promise<ApiResponse<RentalRevenueAnalysis>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalRevenueAnalysis(params)
  }
  return apiClient.get('/api/rental/revenue-analysis', { params })
}

// 获取租赁订单详情
export const getRentalOrderDetail = (orderId: number): Promise<ApiResponse<RentalOrder>> => {
  if (isDevelopment) {
    // Mock implementation for development
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalOrder,
      timestamp: Date.now()
    })
  }
  return apiClient.get(`/api/rental/orders/${orderId}`)
}

// 更新租赁订单状态
export const updateRentalOrderStatus = (orderId: number, status: string): Promise<ApiResponse<void>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: undefined,
      timestamp: Date.now()
    })
  }
  return apiClient.put(`/api/rental/orders/${orderId}/status`, { status })
}

// 创建租赁订单
export const createRentalOrder = (orderData: Partial<RentalOrder>): Promise<ApiResponse<RentalOrder>> => {
  if (isDevelopment) {
    return mockRentalAPI.createRentalOrder(orderData)
  }
  return apiClient.post('/api/rental/orders', orderData)
}

// 更新租赁订单
export const updateRentalOrder = (orderId: number, orderData: Partial<RentalOrder>): Promise<ApiResponse<RentalOrder>> => {
  if (isDevelopment) {
    return mockRentalAPI.updateRentalOrder(orderId, orderData)
  }
  return apiClient.put(`/api/rental/orders/${orderId}`, orderData)
}

// 删除租赁订单
export const deleteRentalOrder = (orderId: number): Promise<ApiResponse<void>> => {
  if (isDevelopment) {
    return mockRentalAPI.deleteRentalOrder(orderId)
  }
  return apiClient.delete(`/api/rental/orders/${orderId}`)
}

// 导出租赁数据报表
export const exportRentalReport = (params: {
  type: 'orders' | 'revenue' | 'utilization'
  format: 'excel' | 'pdf'
  startDate?: string
  endDate?: string
  filters?: Record<string, any>
}): Promise<Blob> => {
  return apiClient.get('/api/rental/export', {
    params,
    responseType: 'blob'
  })
}