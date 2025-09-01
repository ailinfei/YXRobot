/**
 * 租赁数据相关API
 * 提供租赁业务数据分析和管理功能
 */

import apiClient from './config'
import type { ApiResponse, PageResponse } from './types'
import { mockRentalAPI } from './mock/rental'
import type {
  RentalStats,
  RentalTrendData,
  DeviceUtilizationData,
  RentalRecord,
  RentalDevice,
  RentalCustomer,
  RentalRevenueAnalysis,
  DeviceStatusStats,
  TodayStats,
  DeviceQueryParams,
  RentalRecordQueryParams,
  TrendQueryParams,
  RevenueAnalysisParams,
  RentalRecordFormData,
  RentalDeviceFormData,
  RentalCustomerFormData,
  ExportParams
} from '@/types/rental'

// 开发环境使用Mock数据，生产环境使用真实API
const isDevelopment = import.meta.env.DEV && import.meta.env.VITE_USE_MOCK !== 'false'

// 获取租赁业务统计数据
export const getRentalStats = (): Promise<ApiResponse<RentalStats>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalStats()
  }
  return apiClient.get('/api/rental/stats')
}

// 获取租赁趋势数据
export const getRentalTrendData = (params: TrendQueryParams): Promise<ApiResponse<RentalTrendData[]>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalTrendData(params)
  }
  return apiClient.get('/api/rental/trend', { params })
}

// 获取设备利用率数据
export const getDeviceUtilizationData = (params?: DeviceQueryParams): Promise<ApiResponse<DeviceUtilizationData[]>> => {
  if (isDevelopment) {
    return mockRentalAPI.getDeviceUtilizationData(params)
  }
  return apiClient.get('/api/rental/device-utilization', { params })
}

// 获取租赁订单列表
export const getRentalOrders = (params: RentalRecordQueryParams): Promise<ApiResponse<PageResponse<RentalRecord>>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalOrders(params)
  }
  return apiClient.get('/api/rental/orders', { params })
}

// 获取租赁收入分析数据
export const getRentalRevenueAnalysis = (params: RevenueAnalysisParams): Promise<ApiResponse<RentalRevenueAnalysis>> => {
  if (isDevelopment) {
    return mockRentalAPI.getRentalRevenueAnalysis(params)
  }
  return apiClient.get('/api/rental/revenue-analysis', { params })
}

// 获取租赁订单详情
export const getRentalOrderDetail = (orderId: number): Promise<ApiResponse<RentalRecord>> => {
  if (isDevelopment) {
    // Mock implementation for development
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalRecord,
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
export const createRentalOrder = (orderData: RentalRecordFormData): Promise<ApiResponse<RentalRecord>> => {
  if (isDevelopment) {
    return mockRentalAPI.createRentalOrder(orderData)
  }
  return apiClient.post('/api/rental/orders', orderData)
}

// 更新租赁订单
export const updateRentalOrder = (orderId: number, orderData: Partial<RentalRecordFormData>): Promise<ApiResponse<RentalRecord>> => {
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

// 获取今日统计数据
export const getTodayStats = (): Promise<ApiResponse<TodayStats>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        revenue: 45680,
        orders: 23,
        activeDevices: 198,
        avgUtilization: 78.5
      },
      timestamp: Date.now()
    })
  }
  return apiClient.get('/api/rental/today-stats')
}

// 获取设备状态统计
export const getDeviceStatusStats = (): Promise<ApiResponse<DeviceStatusStats>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        active: 198,
        idle: 67,
        maintenance: 20
      },
      timestamp: Date.now()
    })
  }
  return apiClient.get('/api/rental/device-status-stats')
}

// 获取TOP设备排行
export const getTopDevices = (limit: number = 5): Promise<ApiResponse<DeviceUtilizationData[]>> => {
  if (isDevelopment) {
    return mockRentalAPI.getDeviceUtilizationData({}).then(res => ({
      ...res,
      data: res.data.slice(0, limit)
    }))
  }
  return apiClient.get('/api/rental/top-devices', { params: { limit } })
}

// 导出租赁数据报表
export const exportRentalReport = (params: ExportParams): Promise<Blob> => {
  return apiClient.get('/api/rental/export', {
    params,
    responseType: 'blob'
  })
}

// ==================== 设备管理API ====================

// 获取设备列表
export const getRentalDevices = (params?: DeviceQueryParams): Promise<ApiResponse<PageResponse<RentalDevice>>> => {
  if (isDevelopment) {
    // Mock implementation
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        list: [],
        total: 0,
        page: params?.page || 1,
        pageSize: params?.pageSize || 20,
        totalPages: 0
      },
      timestamp: Date.now()
    })
  }
  return apiClient.get('/api/rental/devices', { params })
}

// 获取设备详情
export const getRentalDeviceDetail = (deviceId: number): Promise<ApiResponse<RentalDevice>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalDevice,
      timestamp: Date.now()
    })
  }
  return apiClient.get(`/api/rental/devices/${deviceId}`)
}

// 创建设备
export const createRentalDevice = (deviceData: RentalDeviceFormData): Promise<ApiResponse<RentalDevice>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalDevice,
      timestamp: Date.now()
    })
  }
  return apiClient.post('/api/rental/devices', deviceData)
}

// 更新设备
export const updateRentalDevice = (deviceId: number, deviceData: Partial<RentalDeviceFormData>): Promise<ApiResponse<RentalDevice>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalDevice,
      timestamp: Date.now()
    })
  }
  return apiClient.put(`/api/rental/devices/${deviceId}`, deviceData)
}

// 删除设备
export const deleteRentalDevice = (deviceId: number): Promise<ApiResponse<void>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: undefined,
      timestamp: Date.now()
    })
  }
  return apiClient.delete(`/api/rental/devices/${deviceId}`)
}

// ==================== 客户管理API ====================

// 获取客户列表
export const getRentalCustomers = (params?: {
  page?: number
  pageSize?: number
  keyword?: string
  customerType?: string
  region?: string
}): Promise<ApiResponse<PageResponse<RentalCustomer>>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        list: [],
        total: 0,
        page: params?.page || 1,
        pageSize: params?.pageSize || 20,
        totalPages: 0
      },
      timestamp: Date.now()
    })
  }
  return apiClient.get('/api/rental/customers', { params })
}

// 获取客户详情
export const getRentalCustomerDetail = (customerId: number): Promise<ApiResponse<RentalCustomer>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalCustomer,
      timestamp: Date.now()
    })
  }
  return apiClient.get(`/api/rental/customers/${customerId}`)
}

// 创建客户
export const createRentalCustomer = (customerData: RentalCustomerFormData): Promise<ApiResponse<RentalCustomer>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalCustomer,
      timestamp: Date.now()
    })
  }
  return apiClient.post('/api/rental/customers', customerData)
}

// 更新客户
export const updateRentalCustomer = (customerId: number, customerData: Partial<RentalCustomerFormData>): Promise<ApiResponse<RentalCustomer>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {} as RentalCustomer,
      timestamp: Date.now()
    })
  }
  return apiClient.put(`/api/rental/customers/${customerId}`, customerData)
}

// 删除客户
export const deleteRentalCustomer = (customerId: number): Promise<ApiResponse<void>> => {
  if (isDevelopment) {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: undefined,
      timestamp: Date.now()
    })
  }
  return apiClient.delete(`/api/rental/customers/${customerId}`)
}