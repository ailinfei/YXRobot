/**
 * 租赁数据Mock服务
 * 提供租赁业务的模拟数据，用于前端开发和测试
 */

import type {
  RentalStats,
  RentalTrendData,
  DeviceUtilizationData,
  RentalOrder,
  RentalRevenueAnalysis
} from '../rental'

// 生成随机数据的工具函数
const randomBetween = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

const randomFloat = (min: number, max: number, decimals: number = 2): number => {
  return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
}

const generateDateRange = (days: number): string[] => {
  const dates: string[] = []
  const today = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(date.toISOString().slice(0, 10))
  }
  
  return dates
}

// Mock数据生成器
export class RentalMockService {
  // 生成租赁统计数据
  static generateRentalStats(): RentalStats {
    const totalDevices = randomBetween(450, 650) // 增加设备数量
    const activeDevices = Math.round(totalDevices * randomFloat(0.75, 0.92)) // 提高活跃比例
    const utilizationRate = randomFloat(78, 94) // 提高利用率
    const totalOrders = randomBetween(3200, 4800) // 增加订单数量
    const averagePeriod = randomBetween(32, 58) // 增加平均租期
    
    // 基于设备数量和利用率计算合理的收入
    const dailyRevenuePerDevice = randomFloat(220, 380) // 提高日收入
    const totalRevenue = Math.round(activeDevices * utilizationRate / 100 * 365 * dailyRevenuePerDevice)
    
    return {
      totalRentalRevenue: totalRevenue,
      totalRentalDevices: totalDevices,
      activeRentalDevices: activeDevices,
      deviceUtilizationRate: utilizationRate,
      averageRentalPeriod: averagePeriod,
      totalRentalOrders: totalOrders,
      revenueGrowthRate: randomFloat(15, 35), // 提高增长率
      deviceGrowthRate: randomFloat(18, 42) // 提高设备增长率
    }
  }

  // 生成租赁趋势数据
  static generateRentalTrendData(_period: string, days: number = 30): RentalTrendData[] {
    const dates = generateDateRange(days)
    
    return dates.map((date, index) => {
      // 基于日期和周期生成更真实的趋势数据
      const dayOfWeek = new Date(date).getDay()
      const isWeekend = dayOfWeek === 0 || dayOfWeek === 6
      
      // 提高基础值，让数据更丰富
      let baseRevenue = 45000 // 提高基础收入
      let baseOrders = 85 // 提高基础订单数
      let baseDevices = 180 // 提高基础设备数
      let baseUtilization = 82 // 提高基础利用率
      
      // 周末调整（租赁业务周末通常较低）
      if (isWeekend) {
        baseRevenue *= 0.75
        baseOrders *= 0.65
        baseUtilization *= 0.85
      }
      
      // 添加更明显的增长趋势
      const trendFactor = 1 + (index / dates.length) * 0.45
      baseRevenue *= trendFactor
      baseOrders *= trendFactor
      baseDevices *= trendFactor
      
      // 添加季节性波动（更明显的波动）
      const seasonalFactor = 1 + Math.sin((index / dates.length) * Math.PI * 2) * 0.25
      
      // 添加月度周期性（商业租赁通常月初月末较活跃）
      const monthlyFactor = 1 + Math.sin((index / dates.length) * Math.PI * 8) * 0.15
      
      // 添加随机波动
      const randomFactor = randomFloat(0.85, 1.25)
      
      return {
        date,
        revenue: Math.round(baseRevenue * seasonalFactor * monthlyFactor * randomFactor),
        orderCount: Math.round(baseOrders * seasonalFactor * monthlyFactor * randomFactor),
        deviceCount: Math.round(baseDevices * trendFactor * randomFloat(0.95, 1.05)),
        utilizationRate: Math.round(Math.min(98, Math.max(65, baseUtilization * seasonalFactor * randomFloat(0.92, 1.08))))
      }
    })
  }

  /**
   * 生成增强的设备利用率数据
   * 包含信号强度、性能评分、地区分布、维护状态等详细信息
   * @returns {DeviceUtilizationData[]} 设备利用率数据数组，按利用率降序排列
   */
  static generateDeviceUtilizationData(): DeviceUtilizationData[] {
    const deviceModels = ['YX-Robot-Pro', 'YX-Robot-Standard', 'YX-Robot-Lite', 'YX-Robot-Mini']
    const regions = ['华北', '华东', '华南', '华中', '西南', '西北', '东北', '华西']
    const devices: DeviceUtilizationData[] = []

    // 生成更多设备数据，确保有足够的样本（增加到300台）
    for (let i = 1; i <= 300; i++) {
      const totalAvailableDays = 365
      const model = deviceModels[randomBetween(0, deviceModels.length - 1)]
      const region = regions[randomBetween(0, regions.length - 1)]
      
      // 根据设备型号调整利用率分布
      let baseUtilization = 0.7
      let basePerformanceScore = 75
      let baseSignalStrength = 70
      
      switch (model) {
        case 'YX-Robot-Pro':
          baseUtilization = 0.85 // Pro版本利用率更高
          basePerformanceScore = 85
          baseSignalStrength = 85
          break
        case 'YX-Robot-Standard':
          baseUtilization = 0.75
          basePerformanceScore = 78
          baseSignalStrength = 75
          break
        case 'YX-Robot-Lite':
          baseUtilization = 0.65
          basePerformanceScore = 70
          baseSignalStrength = 68
          break
        case 'YX-Robot-Mini':
          baseUtilization = 0.55
          basePerformanceScore = 65
          baseSignalStrength = 60
          break
      }
      
      // 根据地区调整信号强度（发达地区信号更好）
      const regionSignalBonus = ['华东', '华南', '华北'].includes(region) ? 10 : 
                               ['华中', '西南'].includes(region) ? 5 : 0
      
      // 添加随机波动
      const utilizationVariation = randomFloat(-0.2, 0.2)
      const finalUtilization = Math.max(0.2, Math.min(0.95, baseUtilization + utilizationVariation))
      const totalRentalDays = Math.round(totalAvailableDays * finalUtilization)
      const utilizationRate = Math.round((totalRentalDays / totalAvailableDays) * 100)
      
      // 性能评分与利用率负相关（高利用率设备磨损更多）
      const performanceScore = Math.round(Math.max(30, Math.min(100, 
        basePerformanceScore - (utilizationRate - 50) * 0.3 + randomFloat(-10, 10)
      )))
      
      // 信号强度计算
      const signalStrength = Math.round(Math.max(20, Math.min(100, 
        baseSignalStrength + regionSignalBonus + randomFloat(-15, 15)
      )))
      
      // 根据利用率和性能评分决定设备状态
      let status: 'active' | 'idle' | 'maintenance' | 'renting'
      if (utilizationRate > 85) {
        // 高利用率设备更可能在租赁中或需要维护
        const rand = Math.random()
        if (rand > 0.6) status = 'renting'
        else if (rand > 0.3) status = 'active'
        else if (rand > 0.1) status = 'idle'
        else status = 'maintenance'
      } else if (utilizationRate > 60) {
        const rand = Math.random()
        if (rand > 0.5) status = 'active'
        else if (rand > 0.2) status = 'idle'
        else if (rand > 0.05) status = 'renting'
        else status = 'maintenance'
      } else {
        const rand = Math.random()
        if (rand > 0.6) status = 'idle'
        else if (rand > 0.3) status = 'active'
        else if (rand > 0.1) status = 'maintenance'
        else status = 'renting'
      }
      
      // 维护状态基于性能评分
      let maintenanceStatus: 'normal' | 'warning' | 'urgent'
      if (performanceScore >= 80) maintenanceStatus = 'normal'
      else if (performanceScore >= 60) maintenanceStatus = 'warning'
      else maintenanceStatus = 'urgent'
      
      // 故障风险预测（基于利用率、性能评分和使用时间）
      const predictedFailureRisk = Math.round(Math.max(0, Math.min(100,
        (100 - performanceScore) * 0.6 + utilizationRate * 0.3 + randomFloat(-10, 10)
      )))
      
      // 网络类型分布（发达地区5G比例更高）
      let networkType: '4G' | '5G' | 'WiFi' | 'Ethernet'
      if (['华东', '华南', '华北'].includes(region)) {
        const rand = Math.random()
        if (rand > 0.6) networkType = '5G'
        else if (rand > 0.3) networkType = 'WiFi'
        else if (rand > 0.1) networkType = '4G'
        else networkType = 'Ethernet'
      } else {
        const rand = Math.random()
        if (rand > 0.7) networkType = '4G'
        else if (rand > 0.4) networkType = 'WiFi'
        else if (rand > 0.2) networkType = '5G'
        else networkType = 'Ethernet'
      }
      
      // 维护时间计算
      const lastMaintenanceDate = new Date(Date.now() - randomBetween(7, 90) * 24 * 60 * 60 * 1000)
      const nextMaintenanceDate = new Date(lastMaintenanceDate.getTime() + 
        (maintenanceStatus === 'urgent' ? 7 : maintenanceStatus === 'warning' ? 30 : 90) * 24 * 60 * 60 * 1000)
      
      devices.push({
        deviceId: `YX-${String(i).padStart(4, '0')}`,
        deviceModel: model,
        utilizationRate,
        totalRentalDays,
        totalAvailableDays,
        currentStatus: status,
        lastRentalDate: utilizationRate > 20 ? 
          new Date(Date.now() - randomBetween(1, 60) * 24 * 60 * 60 * 1000).toISOString().slice(0, 10) : 
          undefined,
        // 新增字段
        signalStrength: status === 'active' || status === 'renting' ? signalStrength : undefined,
        performanceScore,
        region,
        maintenanceStatus,
        predictedFailureRisk,
        networkType: status === 'active' || status === 'renting' ? networkType : undefined,
        lastMaintenanceDate: lastMaintenanceDate.toISOString().slice(0, 10),
        nextMaintenanceDate: nextMaintenanceDate.toISOString().slice(0, 10)
      })
    }

    return devices.sort((a, b) => b.utilizationRate - a.utilizationRate)
  }

  // 生成租赁订单数据
  static generateRentalOrders(count: number = 200): RentalOrder[] {
    const deviceModels = ['YX-Robot-Pro', 'YX-Robot-Standard', 'YX-Robot-Lite']
    const platforms = ['租赁宝', '设备租', '智能租赁', '机器人租赁平台', '科技设备租']
    const statuses: ('pending' | 'active' | 'expired' | 'returned' | 'cancelled')[] = 
      ['pending', 'active', 'expired', 'returned', 'cancelled']
    const customerNames = [
      '张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十',
      '郑十一', '王十二', '冯十三', '陈十四', '褚十五', '卫十六'
    ]

    const orders: RentalOrder[] = []

    for (let i = 1; i <= count; i++) {
      const rentalPeriod = randomBetween(7, 90)
      const dailyRate = randomFloat(80, 300)
      const totalAmount = rentalPeriod * dailyRate
      const startDate = new Date(Date.now() - randomBetween(0, 180) * 24 * 60 * 60 * 1000)
      const endDate = new Date(startDate.getTime() + rentalPeriod * 24 * 60 * 60 * 1000)

      orders.push({
        id: i,
        orderNumber: `R${new Date().getFullYear()}${String(i).padStart(6, '0')}`,
        customerId: randomBetween(1, 100),
        customerName: customerNames[randomBetween(0, customerNames.length - 1)],
        customerPhone: `1${randomBetween(3, 9)}${String(randomBetween(10000000, 99999999))}`,
        deviceModel: deviceModels[randomBetween(0, deviceModels.length - 1)],
        deviceId: `YX-${String(randomBetween(1, 500)).padStart(4, '0')}`,
        rentalPeriod,
        dailyRate,
        totalAmount,
        platform: platforms[randomBetween(0, platforms.length - 1)],
        status: statuses[randomBetween(0, statuses.length - 1)],
        startDate: startDate.toISOString().slice(0, 10),
        endDate: endDate.toISOString().slice(0, 10),
        createdAt: new Date(startDate.getTime() - randomBetween(1, 7) * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date().toISOString()
      })
    }

    return orders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  // 生成租赁收入分析数据
  static generateRentalRevenueAnalysis(): RentalRevenueAnalysis {
    const deviceModels = ['YX-Robot-Pro', 'YX-Robot-Standard', 'YX-Robot-Lite', 'YX-Robot-Mini']
    const regions = ['华北', '华东', '华南', '华中', '西南', '西北', '东北', '华西']
    
    const totalRevenue = randomBetween(1800000, 2800000)
    const totalOrders = randomBetween(2200, 3500)
    const averageOrderValue = Math.round(totalRevenue / totalOrders)

    // 设备型号表现数据（按市场份额分配）
    const deviceShares = [0.35, 0.28, 0.22, 0.15] // Pro, Standard, Lite, Mini
    const topPerformingDevices = deviceModels.map((model, index) => {
      const share = deviceShares[index]
      const revenue = Math.round(totalRevenue * share * randomFloat(0.8, 1.2))
      const orderCount = Math.round(totalOrders * share * randomFloat(0.8, 1.2))
      
      // 不同型号的利用率差异
      let baseUtilization = 75
      switch (model) {
        case 'YX-Robot-Pro':
          baseUtilization = 85
          break
        case 'YX-Robot-Standard':
          baseUtilization = 78
          break
        case 'YX-Robot-Lite':
          baseUtilization = 72
          break
        case 'YX-Robot-Mini':
          baseUtilization = 65
          break
      }
      
      return {
        deviceModel: model,
        revenue,
        orderCount,
        utilizationRate: randomFloat(baseUtilization - 8, baseUtilization + 8)
      }
    })

    // 地区分布数据（按经济发达程度分配）
    const regionShares = [0.18, 0.22, 0.16, 0.12, 0.10, 0.08, 0.09, 0.05]
    const regionDistribution = regions.map((region, index) => {
      const share = regionShares[index]
      const revenue = Math.round(totalRevenue * share * randomFloat(0.8, 1.2))
      const orderCount = Math.round(totalOrders * share * randomFloat(0.8, 1.2))
      const deviceCount = Math.round(orderCount / randomBetween(8, 15)) // 每台设备平均订单数
      
      return {
        region,
        revenue,
        orderCount,
        deviceCount
      }
    })

    return {
      period: '2024年度',
      totalRevenue,
      orderCount: totalOrders,
      averageOrderValue,
      topPerformingDevices: topPerformingDevices.sort((a, b) => b.revenue - a.revenue),
      regionDistribution: regionDistribution.sort((a, b) => b.revenue - a.revenue)
    }
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Mock API响应格式
const createMockResponse = <T>(data: T, code: number = 200, message: string = 'success') => {
  return {
    code,
    message,
    data,
    timestamp: Date.now()
  }
}

// Mock API函数
export const mockRentalAPI = {
  // 获取租赁统计数据
  async getRentalStats() {
    await mockDelay()
    return createMockResponse(RentalMockService.generateRentalStats())
  },

  // 获取租赁趋势数据
  async getRentalTrendData(params: any) {
    await mockDelay()
    let days = 30
    
    switch (params?.period) {
      case 'daily':
        days = 7
        break
      case 'weekly':
        days = 28
        break
      case 'monthly':
        days = 365
        break
      case 'quarterly':
        days = 90
        break
      case 'yearly':
        days = 365
        break
    }
    
    return createMockResponse(RentalMockService.generateRentalTrendData(params?.period || 'monthly', days))
  },

  // 获取设备利用率数据
  async getDeviceUtilizationData(params: any) {
    await mockDelay()
    let data = RentalMockService.generateDeviceUtilizationData()
    
    // 应用筛选
    if (params?.deviceModel) {
      data = data.filter(item => item.deviceModel === params.deviceModel)
    }
    
    if (params?.status) {
      data = data.filter(item => item.currentStatus === params.status)
    }
    
    // 应用排序
    if (params?.sortBy) {
      data.sort((a, b) => {
        const aVal = a[params.sortBy as keyof DeviceUtilizationData]
        const bVal = b[params.sortBy as keyof DeviceUtilizationData]
        
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return params.sortOrder === 'asc' ? aVal - bVal : bVal - aVal
        }
        
        if (typeof aVal === 'string' && typeof bVal === 'string') {
          return params.sortOrder === 'asc' ? 
            aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
        }
        
        return 0
      })
    }
    
    return createMockResponse(data)
  },

  // 获取租赁订单列表
  async getRentalOrders(params: any) {
    await mockDelay()
    let data = RentalMockService.generateRentalOrders(100)
    
    // 应用筛选
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }
    
    if (params?.deviceModel) {
      data = data.filter(item => item.deviceModel === params.deviceModel)
    }
    
    if (params?.platform) {
      data = data.filter(item => item.platform.includes(params.platform))
    }
    
    if (params?.keyword) {
      data = data.filter(item => 
        item.orderNumber.includes(params.keyword) ||
        item.customerName.includes(params.keyword) ||
        item.customerPhone.includes(params.keyword)
      )
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 获取租赁收入分析数据
  async getRentalRevenueAnalysis(_params: any) {
    await mockDelay()
    return createMockResponse(RentalMockService.generateRentalRevenueAnalysis())
  },

  // 创建租赁订单
  async createRentalOrder(orderData: any) {
    await mockDelay()
    const newOrder = {
      id: Date.now(),
      orderNumber: `R${new Date().getFullYear()}${String(Date.now()).slice(-6)}`,
      ...orderData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(newOrder)
  },

  // 更新租赁订单
  async updateRentalOrder(orderId: number, orderData: any) {
    await mockDelay()
    const updatedOrder = {
      id: orderId,
      ...orderData,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedOrder)
  },

  // 删除租赁订单
  async deleteRentalOrder(_orderId: number) {
    await mockDelay()
    return createMockResponse(null)
  }
}