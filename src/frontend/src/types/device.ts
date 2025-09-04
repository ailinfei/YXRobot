/**
 * 通用设备类型定义 - 供其他模块使用
 * 保持原有接口不变，确保向后兼容
 */

// 基础设备接口
export interface Device {
  id: string
  name: string
  type: string
  status: 'online' | 'offline' | 'error' | 'maintenance'
  lastSeen?: string
  createdAt?: string
  updatedAt?: string
}

// 设备状态枚举
export enum DeviceStatus {
  ONLINE = 'online',
  OFFLINE = 'offline',
  ERROR = 'error',
  MAINTENANCE = 'maintenance'
}

// 维护记录接口
export interface MaintenanceRecord {
  id: string
  deviceId: string
  type: 'repair' | 'upgrade' | 'inspection' | 'replacement'
  description: string
  technician: string
  startTime: string
  endTime?: string
  status: 'pending' | 'in_progress' | 'completed' | 'cancelled'
  cost?: number
  parts?: string[]
  notes?: string
}

// 设备统计接口
export interface DeviceStats {
  total: number
  online: number
  offline: number
  error: number
  maintenance: number
}

// 设备日志接口
export interface DeviceLog {
  id: string
  deviceId: string
  timestamp: string
  level: 'info' | 'warning' | 'error' | 'debug'
  category: 'system' | 'user' | 'network' | 'hardware' | 'software'
  message: string
  details?: Record<string, any>
}

// 设备查询参数
export interface DeviceQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
  type?: string
}

// 设备列表响应
export interface DeviceListResponse {
  list: Device[]
  total: number
  page: number
  pageSize: number
}

// API响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}