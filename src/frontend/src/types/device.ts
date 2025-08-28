// 设备相关类型定义

export interface Device {
  id: string
  serialNumber: string
  model: string
  status: 'online' | 'offline' | 'error' | 'maintenance'
  firmwareVersion: string
  customerId: string
  customerName: string
  customerPhone: string
  lastOnlineAt?: string
  activatedAt?: string
  createdAt: string
  updatedAt?: string
  
  // 技术参数
  specifications: {
    cpu: string
    memory: string
    storage: string
    display: string
    battery: string
    connectivity: string[]
  }
  
  // 使用统计
  usageStats: {
    totalRuntime: number // 总运行时间（分钟）
    usageCount: number // 使用次数
    lastUsedAt?: string
    averageSessionTime: number // 平均使用时长（分钟）
  }
  
  // 维护记录
  maintenanceRecords: MaintenanceRecord[]
  
  // 设备配置
  configuration: {
    language: string
    timezone: string
    autoUpdate: boolean
    debugMode: boolean
    customSettings: Record<string, any>
  }
  
  // 位置信息
  location?: {
    latitude: number
    longitude: number
    address: string
    lastUpdated: string
  }
  
  notes?: string
}

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

export interface DeviceStats {
  total: number
  online: number
  offline: number
  error: number
  maintenance: number
}

export interface DeviceLog {
  id: string
  deviceId: string
  timestamp: string
  level: 'info' | 'warning' | 'error' | 'debug'
  category: 'system' | 'user' | 'network' | 'hardware' | 'software'
  message: string
  details?: Record<string, any>
}

// API 请求/响应类型
export interface CreateDeviceData {
  serialNumber: string
  model: string
  customerId: string
  specifications: Device['specifications']
  configuration: Device['configuration']
  location?: Device['location']
  notes?: string
}

export interface UpdateDeviceData extends Partial<CreateDeviceData> {
  status?: Device['status']
  firmwareVersion?: string
}

export interface DeviceQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: Device['status']
  model?: string
  customerId?: string
  dateRange?: [string, string]
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface DeviceListResponse {
  list: Device[]
  total: number
  page: number
  pageSize: number
  stats: DeviceStats
}

// 设备状态文本映射
export const DEVICE_STATUS_TEXT = {
  online: '在线',
  offline: '离线',
  error: '故障',
  maintenance: '维护中'
}

// 设备型号文本映射
export const DEVICE_MODEL_TEXT = {
  'YX-EDU-2024': '教育版练字机器人',
  'YX-HOME-2024': '家庭版练字机器人',
  'YX-PRO-2024': '专业版练字机器人'
}

// 维护类型文本映射
export const MAINTENANCE_TYPE_TEXT = {
  repair: '维修',
  upgrade: '升级',
  inspection: '检查',
  replacement: '更换'
}