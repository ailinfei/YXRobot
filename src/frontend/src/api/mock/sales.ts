/**
 * 销售数据Mock API
 * 提供销售相关的模拟数据，用于前端开发和测试
 */

export interface SalesStats {
  totalRevenue: number
  monthlyRevenue: number
  revenueGrowth: number
  totalOrders: number
  monthlyOrders: number
  orderGrowth: number
  avgOrderValue: number
  avgOrderGrowth: number
  conversionRate: number
  conversionGrowth: number
}

export interface SalesOrder {
  id: number
  orderNumber: string
  customerName: string
  customerPhone: string
  productModel: string
  quantity: number
  unitPrice: number
  totalAmount: number
  platform: string
  region: string
  city: string
  status: 'pending' | 'paid' | 'shipped' | 'delivered' | 'cancelled'
  orderDate: string
  shippedDate?: string
  deliveredDate?: string
  createdAt: string
}

export interface RegionSalesData {
  region: string
  country: string
  totalRevenue: number
  totalOrders: number
  avgOrderValue: number
  growth: number
  marketShare: number
}

export interface ChannelSalesData {
  channel: string
  platform: string
  totalRevenue: number
  totalOrders: number
  conversionRate: number
  growth: number
  color: string
}

export interface SalesTrendData {
  date: string
  revenue: number
  orders: number
  customers: number
  avgOrderValue: number
}

export interface ProductSalesRanking {
  productModel: string
  productName: string
  totalRevenue: number
  totalOrders: number
  avgPrice: number
  growth: number
  marketShare: number
}

// 生成随机数据的工具函数
const randomBetween = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

const randomFloat = (min: number, max: number, decimals: number = 2): number => {
  return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
}

const generateRandomDate = (daysAgo: number): string => {
  const date = new Date()
  date.setDate(date.getDate() - randomBetween(0, daysAgo))
  return date.toISOString().slice(0, 10)
}

// Mock数据生成器
export class SalesMockService {
  private static customerNames = [
    '张伟', '王芳', '李娜', '刘强', '陈静', '杨洋', '赵敏', '黄磊',
    '周杰', '吴亮', '徐丽', '孙勇', '马超', '朱琳', '胡斌', '郭敏',
    '何东', '高峰', '林雪', '罗军', '梁华', '宋丽', '唐伟', '韩雪',
    '冯强', '曹敏', '彭亮', '董华', '袁丽', '邓伟', '许静', '傅勇'
  ]

  private static productModels = [
    { model: 'YX-Robot-Pro', name: '专业版练字机器人', basePrice: 12999 },
    { model: 'YX-Robot-Standard', name: '标准版练字机器人', basePrice: 8999 },
    { model: 'YX-Robot-Lite', name: '轻量版练字机器人', basePrice: 5999 },
    { model: 'YX-Robot-Education', name: '教育版练字机器人', basePrice: 9999 },
    { model: 'YX-Robot-Home', name: '家用版练字机器人', basePrice: 6999 }
  ]

  private static platforms = [
    '淘宝', '京东', '天猫', 'Amazon', '拼多多', '苏宁易购', '国美在线', '唯品会'
  ]

  private static regions = [
    { region: '华北地区', cities: ['北京', '天津', '石家庄', '太原', '呼和浩特'] },
    { region: '华东地区', cities: ['上海', '南京', '杭州', '合肥', '福州', '南昌', '济南'] },
    { region: '华南地区', cities: ['广州', '深圳', '南宁', '海口'] },
    { region: '华中地区', cities: ['武汉', '长沙', '郑州'] },
    { region: '西南地区', cities: ['重庆', '成都', '贵阳', '昆明', '拉萨'] },
    { region: '西北地区', cities: ['西安', '兰州', '西宁', '银川', '乌鲁木齐'] },
    { region: '东北地区', cities: ['沈阳', '长春', '哈尔滨'] }
  ]

  private static channels = [
    { channel: '线上直销', platform: '官方商城', color: '#6366f1' },
    { channel: '电商平台', platform: '淘宝天猫', color: '#f59e0b' },
    { channel: '电商平台', platform: '京东', color: '#ef4444' },
    { channel: '电商平台', platform: 'Amazon', color: '#10b981' },
    { channel: '线下门店', platform: '直营店', color: '#8b5cf6' },
    { channel: '代理商', platform: '区域代理', color: '#06b6d4' },
    { channel: '教育渠道', platform: '学校采购', color: '#84cc16' },
    { channel: '企业采购', platform: '批量采购', color: '#f97316' }
  ]

  // 生成销售统计数据
  static generateSalesStats(): SalesStats {
    const totalRevenue = randomBetween(8000000, 15000000)
    const monthlyRevenue = randomBetween(600000, 1200000)
    const totalOrders = randomBetween(3000, 8000)
    const monthlyOrders = randomBetween(200, 600)
    
    return {
      totalRevenue,
      monthlyRevenue,
      revenueGrowth: randomFloat(-5, 25),
      totalOrders,
      monthlyOrders,
      orderGrowth: randomFloat(-3, 20),
      avgOrderValue: Math.round(totalRevenue / totalOrders),
      avgOrderGrowth: randomFloat(-2, 15),
      conversionRate: randomFloat(2.5, 8.5),
      conversionGrowth: randomFloat(-1, 3)
    }
  }

  // 生成销售订单数据
  static generateSalesOrders(count: number = 500): SalesOrder[] {
    const orders: SalesOrder[] = []
    const statuses: ('pending' | 'paid' | 'shipped' | 'delivered' | 'cancelled')[] = 
      ['pending', 'paid', 'shipped', 'delivered', 'cancelled']

    for (let i = 1; i <= count; i++) {
      const product = this.productModels[randomBetween(0, this.productModels.length - 1)]
      const quantity = randomBetween(1, 5)
      const unitPrice = product.basePrice + randomBetween(-1000, 2000)
      const totalAmount = quantity * unitPrice
      const region = this.regions[randomBetween(0, this.regions.length - 1)]
      const city = region.cities[randomBetween(0, region.cities.length - 1)]
      const status = statuses[randomBetween(0, statuses.length - 1)]
      const orderDate = generateRandomDate(365)
      
      let shippedDate: string | undefined
      let deliveredDate: string | undefined
      
      if (['shipped', 'delivered'].includes(status)) {
        const orderDateObj = new Date(orderDate)
        shippedDate = new Date(orderDateObj.getTime() + randomBetween(1, 3) * 24 * 60 * 60 * 1000)
          .toISOString().slice(0, 10)
        
        if (status === 'delivered') {
          deliveredDate = new Date(new Date(shippedDate).getTime() + randomBetween(1, 7) * 24 * 60 * 60 * 1000)
            .toISOString().slice(0, 10)
        }
      }

      orders.push({
        id: i,
        orderNumber: `SO${new Date().getFullYear()}${String(i).padStart(6, '0')}`,
        customerName: this.customerNames[randomBetween(0, this.customerNames.length - 1)],
        customerPhone: `1${randomBetween(3, 9)}${String(randomBetween(10000000, 99999999))}`,
        productModel: product.model,
        quantity,
        unitPrice,
        totalAmount,
        platform: this.platforms[randomBetween(0, this.platforms.length - 1)],
        region: region.region,
        city,
        status,
        orderDate,
        shippedDate,
        deliveredDate,
        createdAt: generateRandomDate(400)
      })
    }

    return orders.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime())
  }

  // 生成地区销售数据
  static generateRegionSalesData(): RegionSalesData[] {
    const regionData: RegionSalesData[] = []
    
    const countries = [
      { region: '中国大陆', country: 'CN', share: 0.45 },
      { region: '美国', country: 'US', share: 0.20 },
      { region: '日本', country: 'JP', share: 0.15 },
      { region: '韩国', country: 'KR', share: 0.10 },
      { region: '新加坡', country: 'SG', share: 0.06 },
      { region: '其他地区', country: 'OTHER', share: 0.04 }
    ]
    
    const totalRevenue = randomBetween(10000000, 20000000)
    const totalOrders = randomBetween(5000, 10000)
    
    countries.forEach(country => {
      const revenue = Math.round(totalRevenue * country.share)
      const orders = Math.round(totalOrders * country.share)
      
      regionData.push({
        region: country.region,
        country: country.country,
        totalRevenue: revenue,
        totalOrders: orders,
        avgOrderValue: Math.round(revenue / orders),
        growth: randomFloat(-10, 30),
        marketShare: country.share * 100
      })
    })
    
    return regionData.sort((a, b) => b.totalRevenue - a.totalRevenue)
  }

  // 生成渠道销售数据
  static generateChannelSalesData(): ChannelSalesData[] {
    const channelData: ChannelSalesData[] = []
    const totalRevenue = randomBetween(8000000, 15000000)
    const totalOrders = randomBetween(3000, 8000)
    
    this.channels.forEach(channel => {
      const revenueShare = randomFloat(0.08, 0.25)
      const orderShare = randomFloat(0.08, 0.25)
      const revenue = Math.round(totalRevenue * revenueShare)
      const orders = Math.round(totalOrders * orderShare)
      
      channelData.push({
        channel: channel.channel,
        platform: channel.platform,
        totalRevenue: revenue,
        totalOrders: orders,
        conversionRate: randomFloat(2.0, 8.0),
        growth: randomFloat(-5, 25),
        color: channel.color
      })
    })
    
    return channelData.sort((a, b) => b.totalRevenue - a.totalRevenue)
  }

  // 生成销售趋势数据
  static generateSalesTrendData(days: number = 30): SalesTrendData[] {
    const trends: SalesTrendData[] = []
    const today = new Date()
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      
      const baseRevenue = randomBetween(20000, 50000)
      const baseOrders = randomBetween(10, 30)
      const baseCustomers = randomBetween(8, 25)
      
      // 添加周末效应（周末销售通常较低）
      const isWeekend = date.getDay() === 0 || date.getDay() === 6
      const weekendFactor = isWeekend ? 0.7 : 1.0
      
      const revenue = Math.round(baseRevenue * weekendFactor)
      const orders = Math.round(baseOrders * weekendFactor)
      const customers = Math.round(baseCustomers * weekendFactor)
      
      trends.push({
        date: date.toISOString().slice(0, 10),
        revenue,
        orders,
        customers,
        avgOrderValue: Math.round(revenue / orders)
      })
    }
    
    return trends
  }

  // 生成产品销售排行
  static generateProductSalesRanking(): ProductSalesRanking[] {
    const rankings: ProductSalesRanking[] = []
    const totalRevenue = randomBetween(10000000, 20000000)
    
    this.productModels.forEach((product, index) => {
      const share = randomFloat(0.15, 0.30)
      const revenue = Math.round(totalRevenue * share)
      const orders = Math.round(revenue / (product.basePrice + randomBetween(-500, 1000)))
      
      rankings.push({
        productModel: product.model,
        productName: product.name,
        totalRevenue: revenue,
        totalOrders: orders,
        avgPrice: Math.round(revenue / orders),
        growth: randomFloat(-8, 35),
        marketShare: share * 100
      })
    })
    
    return rankings.sort((a, b) => b.totalRevenue - a.totalRevenue)
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
export const mockSalesAPI = {
  // 获取销售统计数据
  async getSalesStats() {
    await mockDelay()
    return createMockResponse(SalesMockService.generateSalesStats())
  },

  // 获取销售订单列表
  async getSalesOrders(params: any) {
    await mockDelay()
    let data = SalesMockService.generateSalesOrders(800)
    
    // 应用筛选
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }
    
    if (params?.platform) {
      data = data.filter(item => item.platform === params.platform)
    }
    
    if (params?.region) {
      data = data.filter(item => item.region === params.region)
    }
    
    if (params?.productModel) {
      data = data.filter(item => item.productModel === params.productModel)
    }
    
    if (params?.keyword) {
      data = data.filter(item => 
        item.orderNumber.includes(params.keyword) ||
        item.customerName.includes(params.keyword) ||
        item.customerPhone.includes(params.keyword)
      )
    }
    
    if (params?.dateRange && params.dateRange.length === 2) {
      const [startDate, endDate] = params.dateRange
      data = data.filter(item => 
        item.orderDate >= startDate && item.orderDate <= endDate
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

  // 获取地区销售分布
  async getRegionSalesData() {
    await mockDelay()
    return createMockResponse(SalesMockService.generateRegionSalesData())
  },

  // 获取渠道销售数据
  async getChannelSalesData() {
    await mockDelay()
    return createMockResponse(SalesMockService.generateChannelSalesData())
  },

  // 获取销售趋势数据
  async getSalesTrendData(params: any) {
    await mockDelay()
    const days = params?.days || 30
    const data = SalesMockService.generateSalesTrendData(days)
    return createMockResponse(data)
  },

  // 获取产品销售排行
  async getProductSalesRanking() {
    await mockDelay()
    return createMockResponse(SalesMockService.generateProductSalesRanking())
  },

  // 导出销售报表
  async exportSalesReport(params: any) {
    await mockDelay(2000) // 模拟导出耗时
    return createMockResponse({
      downloadUrl: '/api/downloads/sales-report.xlsx',
      fileName: `销售报表_${new Date().toISOString().slice(0, 10)}.xlsx`,
      fileSize: '2.5MB'
    })
  }
}