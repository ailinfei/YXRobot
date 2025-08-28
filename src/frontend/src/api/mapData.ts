/**
 * 地图数据API服务
 * 提供销售、客户、设备分布的地图可视化数据
 */

import { mockMapDataAPI } from '@/api/mock'

export interface MapDataPoint {
  name: string
  value: number | number[]
  coordinates?: [number, number]
  category?: string
  extra?: Record<string, any>
}

export interface MapDataParams {
  dateRange?: [string, string]
  region?: string
  metric?: string
  granularity?: 'country' | 'province' | 'city'
}

export interface MapConfig {
  mapType: 'world' | 'china'
  visualType: 'heatmap' | 'scatter' | 'bubble'
  colorScheme: string[]
  title: string
}

// 地图数据API - 使用Mock数据
export const mapDataAPI = {
  // 获取销售地区分布数据
  getSalesMapData(params?: MapDataParams) {
    return mockMapDataAPI.getSalesMapData(params)
  },

  // 获取客户地区分布数据
  getCustomerMapData(params?: MapDataParams) {
    return mockMapDataAPI.getCustomerMapData(params)
  },

  // 获取设备分布数据
  getDeviceMapData(params?: MapDataParams) {
    return mockMapDataAPI.getDeviceMapData(params)
  },

  // 获取租赁数据分布
  getRentalMapData(params?: MapDataParams) {
    return mockMapDataAPI.getRentalMapData(params)
  },

  // 获取地图配置
  getMapConfig(mapType: string) {
    return mockMapDataAPI.getMapConfig(mapType)
  }
}