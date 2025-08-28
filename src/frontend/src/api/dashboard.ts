/**
 * 数据看板相关API
 */

import apiClient from './config'
import type { ApiResponse, DashboardStats, ChartData } from './types'

// 获取数据看板统计数据
export const getDashboardStats = (): Promise<ApiResponse<DashboardStats>> => {
  return apiClient.get('/api/dashboard/stats')
}

// 获取收入图表数据
export const getRevenueChartData = (period: string = '7d'): Promise<ApiResponse<ChartData[]>> => {
  return apiClient.get('/api/dashboard/revenue-chart', {
    params: { period }
  })
}

// 获取订单趋势数据
export const getOrderTrendData = (period: string = '7d'): Promise<ApiResponse<ChartData[]>> => {
  return apiClient.get('/api/dashboard/order-trend', {
    params: { period }
  })
}

// 获取客户地区分布数据
export const getCustomerRegionData = (): Promise<ApiResponse<ChartData[]>> => {
  return apiClient.get('/api/dashboard/customer-regions')
}

// 获取产品销售排行数据
export const getTopProductsData = (limit: number = 10): Promise<ApiResponse<ChartData[]>> => {
  return apiClient.get('/api/dashboard/top-products', {
    params: { limit }
  })
}

// 获取设备状态统计
export const getDeviceStatusStats = (): Promise<ApiResponse<Record<string, number>>> => {
  return apiClient.get('/api/dashboard/device-status')
}