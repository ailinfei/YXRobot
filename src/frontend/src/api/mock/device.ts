import type { 
  Device, 
  DeviceStats, 
  DeviceLog,
  MaintenanceRecord,
  CreateDeviceData, 
  UpdateDeviceData, 
  DeviceQueryParams 
} from '@/types/device'

// Mock设备数据生成器
export class DeviceMockService {
  private static deviceModels = ['YX-EDU-2024', 'YX-HOME-2024', 'YX-PRO-2024']
  private static customerNames = [
    '张明华', '李晓红', '王建国', '陈志强', '刘美丽', '赵建华', '孙小明', '周雅琴',
    '吴大伟', '郑小红', '马建国', '朱丽娜', '胡志明', '林小芳', '黄大明'
  ]
  private static technicians = [
    '技术员A', '技术员B', '技术员C', '维修师傅', '工程师小王', '工程师小李'
  ]

  /**
   * 生成设备数据
   */
  static generateDevices(count: number = 100): Device[] {
    const devices: Device[] = []
    
    const statusWeights = {
      'online': 0.60,
      'offline': 0.25,
      'error': 0.10,
      'maintenance': 0.05
    }

    for (let i = 1; i <= count; i++) {
      const model = this.deviceModels[Math.floor(Math.random() * this.deviceModels.length)]
      const status = this.weightedRandomSelect(statusWeights) as Device['status']
      const customerName = this.customerNames[Math.floor(Math.random() * this.customerNames.length)]
      
      const createdAt = this.generateRandomDate(365)
      const activatedAt = status !== 'offline' ? this.generateRandomDate(300) : undefined
      const lastOnlineAt = status === 'online' ? 
        this.generateRandomDate(1) : 
        (status === 'offline' ? this.generateRandomDate(30) : this.generateRandomDate(7))

      const device: Device = {
        id: i.toString(),
        serialNumber: `${model.split('-')[1]}-${String(i).padStart(6, '0')}`,
        model,
        status,
        firmwareVersion: this.generateFirmwareVersion(),
        customerId: Math.floor(Math.random() * 50 + 1).toString(),
        customerName,
        customerPhone: `138****${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`,
        lastOnlineAt,
        activatedAt,
        createdAt,
        updatedAt: this.generateRandomDate(30),
        
        specifications: this.generateSpecifications(model),
        usageStats: this.generateUsageStats(status, activatedAt),
        maintenanceRecords: this.generateMaintenanceRecords(i.toString(), status),
        configuration: this.generateConfiguration(),
        location: Math.random() > 0.3 ? this.generateLocation() : undefined,
        notes: Math.random() > 0.7 ? this.generateNotes() : undefined
      }

      devices.push(device)
    }

    return devices.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  /**
   * 生成设备统计数据
   */
  static generateDeviceStats(devices: Device[]): DeviceStats {
    const stats: DeviceStats = {
      total: devices.length,
      online: 0,
      offline: 0,
      error: 0,
      maintenance: 0
    }

    devices.forEach(device => {
      stats[device.status]++
    })

    return stats
  }

  /**
   * 生成设备日志
   */
  static generateDeviceLogs(deviceId: string, count: number = 50): DeviceLog[] {
    const logs: DeviceLog[] = []
    const levels: DeviceLog['level'][] = ['info', 'warning', 'error', 'debug']
    const categories: DeviceLog['category'][] = ['system', 'user', 'network', 'hardware', 'software']
    
    const messages = {
      info: [
        '设备启动成功',
        '用户登录',
        '课程下载完成',
        '系统自检通过',
        '网络连接正常'
      ],
      warning: [
        '电池电量低',
        '网络连接不稳定',
        '存储空间不足',
        '温度过高',
        '固件版本过旧'
      ],
      error: [
        '网络连接失败',
        '硬件故障',
        '系统崩溃',
        '文件损坏',
        '传感器异常'
      ],
      debug: [
        '调试模式启动',
        '性能监控数据',
        '内存使用情况',
        'CPU使用率',
        '网络延迟测试'
      ]
    }

    for (let i = 1; i <= count; i++) {
      const level = levels[Math.floor(Math.random() * levels.length)]
      const category = categories[Math.floor(Math.random() * categories.length)]
      const messageList = messages[level]
      const message = messageList[Math.floor(Math.random() * messageList.length)]

      logs.push({
        id: i.toString(),
        deviceId,
        timestamp: this.generateRandomDate(30),
        level,
        category,
        message,
        details: Math.random() > 0.5 ? this.generateLogDetails(level, category) : undefined
      })
    }

    return logs.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
  }

  /**
   * 应用筛选条件
   */
  static applyFilters(devices: Device[], params: DeviceQueryParams): Device[] {
    let filtered = [...devices]

    // 关键词搜索
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filtered = filtered.filter(device => 
        device.serialNumber.toLowerCase().includes(keyword) ||
        device.customerName.toLowerCase().includes(keyword) ||
        device.customerPhone.includes(keyword) ||
        device.model.toLowerCase().includes(keyword)
      )
    }

    // 状态筛选
    if (params.status) {
      filtered = filtered.filter(device => device.status === params.status)
    }

    // 型号筛选
    if (params.model) {
      filtered = filtered.filter(device => device.model === params.model)
    }

    // 客户筛选
    if (params.customerId) {
      filtered = filtered.filter(device => device.customerId === params.customerId)
    }

    // 日期范围筛选
    if (params.dateRange && params.dateRange.length === 2) {
      const [startDate, endDate] = params.dateRange
      filtered = filtered.filter(device => {
        const deviceDate = new Date(device.createdAt)
        return deviceDate >= new Date(startDate) && deviceDate <= new Date(endDate)
      })
    }

    return filtered
  }

  // 私有辅助方法
  private static weightedRandomSelect(weights: Record<string, number>): string {
    const random = Math.random()
    let cumulative = 0
    for (const [key, weight] of Object.entries(weights)) {
      cumulative += weight
      if (random <= cumulative) return key
    }
    return Object.keys(weights)[0]
  }

  private static generateRandomDate(daysAgo: number): string {
    const date = new Date()
    date.setDate(date.getDate() - Math.floor(Math.random() * daysAgo))
    return date.toISOString()
  }

  private static generateFirmwareVersion(): string {
    const major = Math.floor(Math.random() * 3) + 1
    const minor = Math.floor(Math.random() * 10)
    const patch = Math.floor(Math.random() * 20)
    return `${major}.${minor}.${patch}`
  }

  private static generateSpecifications(model: string) {
    const specs = {
      'YX-EDU-2024': {
        cpu: 'ARM Cortex-A72 四核 1.5GHz',
        memory: '4GB LPDDR4',
        storage: '64GB eMMC',
        display: '10.1英寸 1920x1200 IPS',
        battery: '8000mAh 锂电池',
        connectivity: ['WiFi 6', 'Bluetooth 5.0', '4G LTE']
      },
      'YX-HOME-2024': {
        cpu: 'ARM Cortex-A55 四核 1.2GHz',
        memory: '2GB LPDDR4',
        storage: '32GB eMMC',
        display: '8英寸 1280x800 IPS',
        battery: '5000mAh 锂电池',
        connectivity: ['WiFi 5', 'Bluetooth 4.2']
      },
      'YX-PRO-2024': {
        cpu: 'ARM Cortex-A78 八核 2.0GHz',
        memory: '8GB LPDDR5',
        storage: '128GB UFS',
        display: '12.9英寸 2732x2048 OLED',
        battery: '12000mAh 锂电池',
        connectivity: ['WiFi 6E', 'Bluetooth 5.2', '5G', 'USB-C']
      }
    }
    return specs[model as keyof typeof specs] || specs['YX-HOME-2024']
  }

  private static generateUsageStats(status: Device['status'], activatedAt?: string) {
    if (!activatedAt || status === 'offline') {
      return {
        totalRuntime: 0,
        usageCount: 0,
        averageSessionTime: 0
      }
    }

    const daysSinceActivation = Math.floor(
      (Date.now() - new Date(activatedAt).getTime()) / (1000 * 60 * 60 * 24)
    )
    
    const usageCount = Math.floor(Math.random() * daysSinceActivation * 2)
    const totalRuntime = Math.floor(Math.random() * daysSinceActivation * 60)
    const averageSessionTime = usageCount > 0 ? Math.floor(totalRuntime / usageCount) : 0

    return {
      totalRuntime,
      usageCount,
      lastUsedAt: Math.random() > 0.3 ? this.generateRandomDate(7) : undefined,
      averageSessionTime
    }
  }

  private static generateMaintenanceRecords(deviceId: string, status: Device['status']): MaintenanceRecord[] {
    const records: MaintenanceRecord[] = []
    const recordCount = status === 'maintenance' ? Math.floor(Math.random() * 3) + 1 : Math.floor(Math.random() * 2)

    for (let i = 1; i <= recordCount; i++) {
      const types: MaintenanceRecord['type'][] = ['repair', 'upgrade', 'inspection', 'replacement']
      const type = types[Math.floor(Math.random() * types.length)]
      const technician = this.technicians[Math.floor(Math.random() * this.technicians.length)]
      
      records.push({
        id: `${deviceId}-${i}`,
        deviceId,
        type,
        description: this.generateMaintenanceDescription(type),
        technician,
        startTime: this.generateRandomDate(90),
        endTime: Math.random() > 0.2 ? this.generateRandomDate(85) : undefined,
        status: Math.random() > 0.1 ? 'completed' : 'in_progress',
        cost: Math.random() > 0.5 ? Math.floor(Math.random() * 1000) + 100 : undefined,
        parts: Math.random() > 0.6 ? this.generateParts() : undefined,
        notes: Math.random() > 0.7 ? '维护顺利完成' : undefined
      })
    }

    return records.sort((a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime())
  }

  private static generateConfiguration() {
    const languages = ['zh-CN', 'en-US', 'ja-JP', 'ko-KR']
    const timezones = ['Asia/Shanghai', 'Asia/Tokyo', 'Asia/Seoul', 'America/New_York']
    
    return {
      language: languages[Math.floor(Math.random() * languages.length)],
      timezone: timezones[Math.floor(Math.random() * timezones.length)],
      autoUpdate: Math.random() > 0.3,
      debugMode: Math.random() > 0.8,
      customSettings: {
        brightness: Math.floor(Math.random() * 100),
        volume: Math.floor(Math.random() * 100),
        sleepTimeout: Math.floor(Math.random() * 60) + 5
      }
    }
  }

  private static generateLocation() {
    const cities = [
      { name: '北京', lat: 39.9042, lng: 116.4074 },
      { name: '上海', lat: 31.2304, lng: 121.4737 },
      { name: '广州', lat: 23.1291, lng: 113.2644 },
      { name: '深圳', lat: 22.5431, lng: 114.0579 },
      { name: '杭州', lat: 30.2741, lng: 120.1551 }
    ]
    
    const city = cities[Math.floor(Math.random() * cities.length)]
    
    return {
      latitude: city.lat + (Math.random() - 0.5) * 0.1,
      longitude: city.lng + (Math.random() - 0.5) * 0.1,
      address: `${city.name}市某某区某某街道`,
      lastUpdated: this.generateRandomDate(30)
    }
  }

  private static generateNotes(): string {
    const notes = [
      '设备运行正常',
      '客户反馈良好',
      '需要定期维护',
      '教学效果显著',
      '使用频率较高',
      '偶有网络问题'
    ]
    return notes[Math.floor(Math.random() * notes.length)]
  }

  private static generateMaintenanceDescription(type: MaintenanceRecord['type']): string {
    const descriptions = {
      repair: ['屏幕维修', '电池更换', '主板维修', '外壳修复', '按键维修'],
      upgrade: ['固件升级', '系统更新', '硬件升级', '功能增强', '性能优化'],
      inspection: ['定期检查', '安全检测', '性能测试', '功能验证', '质量检查'],
      replacement: ['配件更换', '零件替换', '模块更新', '组件升级', '部件维修']
    }
    
    const list = descriptions[type]
    return list[Math.floor(Math.random() * list.length)]
  }

  private static generateParts(): string[] {
    const parts = ['屏幕', '电池', '主板', '外壳', '按键', '摄像头', '扬声器', '充电器']
    const count = Math.floor(Math.random() * 3) + 1
    const selectedParts: string[] = []
    
    for (let i = 0; i < count; i++) {
      const part = parts[Math.floor(Math.random() * parts.length)]
      if (!selectedParts.includes(part)) {
        selectedParts.push(part)
      }
    }
    
    return selectedParts
  }

  private static generateLogDetails(level: DeviceLog['level'], category: DeviceLog['category']) {
    const details: Record<string, any> = {}
    
    if (category === 'system') {
      details.cpu_usage = Math.floor(Math.random() * 100)
      details.memory_usage = Math.floor(Math.random() * 100)
      details.temperature = Math.floor(Math.random() * 40) + 20
    } else if (category === 'network') {
      details.signal_strength = Math.floor(Math.random() * 100)
      details.latency = Math.floor(Math.random() * 100) + 10
      details.bandwidth = Math.floor(Math.random() * 1000) + 100
    } else if (category === 'user') {
      details.session_duration = Math.floor(Math.random() * 3600)
      details.actions_count = Math.floor(Math.random() * 100)
    }
    
    return details
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Mock API响应格式
const createMockResponse = <T>(data: T, code: number = 200, message: string = 'success') => {
  return { code, message, data, timestamp: Date.now() }
}

// Mock设备管理API
export const mockDeviceAPI = {
  // 获取设备列表
  async getDevices(params: DeviceQueryParams = {}) {
    await mockDelay()
    
    const allDevices = DeviceMockService.generateDevices(100)
    let filteredDevices = DeviceMockService.applyFilters(allDevices, params)
    
    // 应用排序
    if (params.sortBy) {
      filteredDevices.sort((a, b) => {
        let aValue: any = a[params.sortBy as keyof Device]
        let bValue: any = b[params.sortBy as keyof Device]

        if (params.sortBy === 'createdAt' || params.sortBy === 'lastOnlineAt') {
          aValue = new Date(aValue || 0).getTime()
          bValue = new Date(bValue || 0).getTime()
        }

        if (typeof aValue === 'number' && typeof bValue === 'number') {
          return params.sortOrder === 'asc' ? aValue - bValue : bValue - aValue
        }

        if (typeof aValue === 'string' && typeof bValue === 'string') {
          return params.sortOrder === 'asc' ? 
            aValue.localeCompare(bValue) : 
            bValue.localeCompare(aValue)
        }

        return 0
      })
    }
    
    // 计算统计数据
    const stats = DeviceMockService.generateDeviceStats(filteredDevices)
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 20
    const total = filteredDevices.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = filteredDevices.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize),
      stats
    })
  },

  // 获取设备详情
  async getDeviceById(id: string) {
    await mockDelay()
    const allDevices = DeviceMockService.generateDevices(100)
    const device = allDevices.find(d => d.id === id)
    
    if (!device) {
      return createMockResponse(null, 404, '设备不存在')
    }
    
    return createMockResponse(device)
  },

  // 创建设备
  async createDevice(deviceData: CreateDeviceData) {
    await mockDelay()
    
    const newDevice: Device = {
      id: Date.now().toString(),
      ...deviceData,
      status: 'offline',
      firmwareVersion: '1.0.0',
      customerName: '新客户',
      customerPhone: '138****0000',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      usageStats: {
        totalRuntime: 0,
        usageCount: 0,
        averageSessionTime: 0
      },
      maintenanceRecords: []
    }
    
    return createMockResponse(newDevice)
  },

  // 更新设备
  async updateDevice(id: string, deviceData: UpdateDeviceData) {
    await mockDelay()
    
    const updatedDevice = {
      id,
      ...deviceData,
      updatedAt: new Date().toISOString()
    }
    
    return createMockResponse(updatedDevice)
  },

  // 删除设备
  async deleteDevice(id: string) {
    await mockDelay()
    return createMockResponse(null)
  },

  // 更新设备状态
  async updateDeviceStatus(id: string, status: Device['status']) {
    await mockDelay()
    return createMockResponse({ 
      id,
      status,
      updatedAt: new Date().toISOString()
    })
  },

  // 重启设备
  async rebootDevice(id: string) {
    await mockDelay()
    return createMockResponse({ message: '重启指令已发送' })
  },

  // 激活设备
  async activateDevice(id: string) {
    await mockDelay()
    return createMockResponse({ 
      id,
      status: 'online',
      activatedAt: new Date().toISOString()
    })
  },

  // 推送固件
  async pushFirmware(id: string, version?: string) {
    await mockDelay()
    return createMockResponse({ 
      message: '固件推送已启动',
      version: version || '2.0.0'
    })
  },

  // 获取设备日志
  async getDeviceLogs(id: string, params?: { page?: number; pageSize?: number; level?: string }) {
    await mockDelay()
    const logs = DeviceMockService.generateDeviceLogs(id, 100)
    
    let filteredLogs = logs
    if (params?.level) {
      filteredLogs = logs.filter(log => log.level === params.level)
    }
    
    const page = params?.page || 1
    const pageSize = params?.pageSize || 50
    const total = filteredLogs.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = filteredLogs.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize
    })
  },

  // 获取客户选项
  async getCustomerOptions() {
    await mockDelay()
    const customers = DeviceMockService['customerNames'].map((name, index) => ({
      id: (index + 1).toString(),
      name
    }))
    return createMockResponse(customers)
  },

  // 批量操作
  async batchPushFirmware(deviceIds: string[]) {
    await mockDelay()
    return createMockResponse({ 
      updatedCount: deviceIds.length,
      message: '批量固件推送已启动'
    })
  },

  async batchRebootDevices(deviceIds: string[]) {
    await mockDelay()
    return createMockResponse({ 
      updatedCount: deviceIds.length,
      message: '批量重启指令已发送'
    })
  },

  async batchDeleteDevices(deviceIds: string[]) {
    await mockDelay()
    return createMockResponse({ 
      deletedCount: deviceIds.length
    })
  },

  // 导出设备数据
  async exportDevices(params: DeviceQueryParams) {
    await mockDelay()
    return createMockResponse({ 
      downloadUrl: '/api/devices/export/devices.xlsx',
      filename: `devices_${new Date().toISOString().split('T')[0]}.xlsx`
    })
  }
}