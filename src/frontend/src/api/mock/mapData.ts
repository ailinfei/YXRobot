/**
 * 地图数据Mock服务
 * 提供销售、客户、设备、租赁分布的模拟数据
 */

import type { MapDataPoint, MapDataParams, MapConfig } from '@/api/mapData'

// 世界主要国家/地区坐标数据
const WORLD_COORDINATES: Record<string, [number, number]> = {
  '中国': [104.195397, 35.86166],
  '美国': [-95.712891, 37.09024],
  '日本': [138.252924, 36.204824],
  '韩国': [127.766922, 35.907757],
  '德国': [10.451526, 51.165691],
  '英国': [-3.435973, 55.378051],
  '法国': [2.213749, 46.227638],
  '加拿大': [-106.346771, 56.130366],
  '澳大利亚': [133.775136, -25.274398],
  '巴西': [-51.92528, -14.235004],
  '印度': [78.96288, 20.593684],
  '俄罗斯': [105.318756, 61.52401],
  '意大利': [12.56738, 41.87194],
  '西班牙': [-3.74922, 40.463667],
  '荷兰': [5.291266, 52.132633],
  '瑞士': [8.227512, 46.818188],
  '瑞典': [18.643501, 60.128161],
  '挪威': [8.468946, 60.472024],
  '新加坡': [103.819836, 1.352083],
  '泰国': [100.992541, 15.870032],
  '马来西亚': [101.975766, 4.210484],
  '印度尼西亚': [113.921327, -0.789275],
  '菲律宾': [121.774017, 12.879721],
  '越南': [108.277199, 14.058324],
  '墨西哥': [-102.552784, 23.634501],
  '阿根廷': [-63.616672, -38.416097],
  '智利': [-71.542969, -35.675147],
  '南非': [22.937506, -30.559482],
  '埃及': [30.802498, 26.820553],
  '土耳其': [35.243322, 38.963745],
  '沙特阿拉伯': [45.079162, 23.885942],
  '阿联酋': [53.847818, 23.424076],
  '以色列': [34.851612, 31.046051]
}

export class MapDataMockService {
  // 生成销售地图数据
  static generateSalesMapData(): MapDataPoint[] {
    const salesData = [
      { name: '中国', value: 2580000, region: 'Asia' },
      { name: '美国', value: 1200000, region: 'North America' },
      { name: '日本', value: 890000, region: 'Asia' },
      { name: '韩国', value: 650000, region: 'Asia' },
      { name: '德国', value: 580000, region: 'Europe' },
      { name: '英国', value: 420000, region: 'Europe' },
      { name: '法国', value: 380000, region: 'Europe' },
      { name: '加拿大', value: 320000, region: 'North America' },
      { name: '澳大利亚', value: 280000, region: 'Oceania' },
      { name: '巴西', value: 250000, region: 'South America' },
      { name: '印度', value: 220000, region: 'Asia' },
      { name: '俄罗斯', value: 180000, region: 'Europe' },
      { name: '意大利', value: 160000, region: 'Europe' },
      { name: '西班牙', value: 140000, region: 'Europe' },
      { name: '荷兰', value: 120000, region: 'Europe' },
      { name: '瑞士', value: 100000, region: 'Europe' },
      { name: '瑞典', value: 85000, region: 'Europe' },
      { name: '挪威', value: 75000, region: 'Europe' },
      { name: '新加坡', value: 95000, region: 'Asia' },
      { name: '泰国', value: 65000, region: 'Asia' }
    ]

    return salesData.map(item => ({
      ...item,
      coordinates: WORLD_COORDINATES[item.name],
      extra: {
        region: item.region,
        currency: 'CNY',
        growth: Math.round((Math.random() - 0.5) * 40) // -20% to +20%
      }
    }))
  }

  // 生成客户地图数据
  static generateCustomerMapData(): MapDataPoint[] {
    const customerData = [
      { name: '中国', value: 15600, region: 'Asia' },
      { name: '美国', value: 8900, region: 'North America' },
      { name: '日本', value: 6700, region: 'Asia' },
      { name: '韩国', value: 4500, region: 'Asia' },
      { name: '德国', value: 3800, region: 'Europe' },
      { name: '英国', value: 2900, region: 'Europe' },
      { name: '法国', value: 2600, region: 'Europe' },
      { name: '加拿大', value: 2200, region: 'North America' },
      { name: '澳大利亚', value: 1800, region: 'Oceania' },
      { name: '巴西', value: 1500, region: 'South America' },
      { name: '印度', value: 1200, region: 'Asia' },
      { name: '俄罗斯', value: 980, region: 'Europe' },
      { name: '意大利', value: 850, region: 'Europe' },
      { name: '西班牙', value: 720, region: 'Europe' },
      { name: '荷兰', value: 650, region: 'Europe' },
      { name: '瑞士', value: 580, region: 'Europe' },
      { name: '瑞典', value: 520, region: 'Europe' },
      { name: '挪威', value: 480, region: 'Europe' },
      { name: '新加坡', value: 620, region: 'Asia' },
      { name: '泰国', value: 420, region: 'Asia' }
    ]

    return customerData.map(item => ({
      ...item,
      coordinates: WORLD_COORDINATES[item.name],
      extra: {
        region: item.region,
        avgOrderValue: Math.round(item.value * 150 + Math.random() * 500),
        activeRate: Math.round(60 + Math.random() * 35) // 60-95%
      }
    }))
  }

  // 生成设备地图数据
  static generateDeviceMapData(): MapDataPoint[] {
    const deviceData = [
      { name: '中国', value: 45600, region: 'Asia' },
      { name: '美国', value: 23400, region: 'North America' },
      { name: '日本', value: 18900, region: 'Asia' },
      { name: '韩国', value: 12300, region: 'Asia' },
      { name: '德国', value: 9800, region: 'Europe' },
      { name: '英国', value: 7600, region: 'Europe' },
      { name: '法国', value: 6900, region: 'Europe' },
      { name: '加拿大', value: 5400, region: 'North America' },
      { name: '澳大利亚', value: 4200, region: 'Oceania' },
      { name: '巴西', value: 3600, region: 'South America' },
      { name: '印度', value: 2800, region: 'Asia' },
      { name: '俄罗斯', value: 2200, region: 'Europe' },
      { name: '意大利', value: 1900, region: 'Europe' },
      { name: '西班牙', value: 1600, region: 'Europe' },
      { name: '荷兰', value: 1400, region: 'Europe' },
      { name: '瑞士', value: 1200, region: 'Europe' },
      { name: '瑞典', value: 1100, region: 'Europe' },
      { name: '挪威', value: 950, region: 'Europe' },
      { name: '新加坡', value: 1350, region: 'Asia' },
      { name: '泰国', value: 890, region: 'Asia' }
    ]

    return deviceData.map(item => ({
      ...item,
      coordinates: WORLD_COORDINATES[item.name],
      extra: {
        region: item.region,
        onlineRate: Math.round(85 + Math.random() * 12), // 85-97%
        avgUsageHours: Math.round(4 + Math.random() * 6) // 4-10 hours
      }
    }))
  }

  // 生成租赁地图数据
  static generateRentalMapData(): MapDataPoint[] {
    const rentalData = [
      { name: '中国', value: 8900, region: 'Asia' },
      { name: '美国', value: 5600, region: 'North America' },
      { name: '日本', value: 4200, region: 'Asia' },
      { name: '韩国', value: 2800, region: 'Asia' },
      { name: '德国', value: 2400, region: 'Europe' },
      { name: '英国', value: 1900, region: 'Europe' },
      { name: '法国', value: 1700, region: 'Europe' },
      { name: '加拿大', value: 1400, region: 'North America' },
      { name: '澳大利亚', value: 1200, region: 'Oceania' },
      { name: '巴西', value: 980, region: 'South America' },
      { name: '印度', value: 750, region: 'Asia' },
      { name: '俄罗斯', value: 620, region: 'Europe' },
      { name: '意大利', value: 580, region: 'Europe' },
      { name: '西班牙', value: 520, region: 'Europe' },
      { name: '荷兰', value: 480, region: 'Europe' },
      { name: '瑞士', value: 420, region: 'Europe' },
      { name: '瑞典', value: 380, region: 'Europe' },
      { name: '挪威', value: 350, region: 'Europe' },
      { name: '新加坡', value: 450, region: 'Asia' },
      { name: '泰国', value: 320, region: 'Asia' }
    ]

    return rentalData.map(item => ({
      ...item,
      coordinates: WORLD_COORDINATES[item.name],
      extra: {
        region: item.region,
        avgRentalPeriod: Math.round(6 + Math.random() * 18), // 6-24 months
        renewalRate: Math.round(70 + Math.random() * 25) // 70-95%
      }
    }))
  }

  // 生成地图配置
  static generateMapConfig(mapType: string): MapConfig {
    const configs = {
      world: {
        mapType: 'world' as const,
        visualType: 'heatmap' as const,
        colorScheme: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'],
        title: '全球业务分布'
      },
      china: {
        mapType: 'china' as const,
        visualType: 'heatmap' as const,
        colorScheme: ['#f7fbff', '#deebf7', '#c6dbef', '#9ecae1', '#6baed6', '#4292c6', '#2171b5', '#08519c', '#08306b'],
        title: '中国业务分布'
      }
    }

    return configs[mapType as keyof typeof configs] || configs.world
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 300): Promise<void> => {
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

// 地图数据Mock API
export const mockMapDataAPI = {
  // 获取销售地区分布数据
  async getSalesMapData(params?: MapDataParams) {
    await mockDelay()
    const data = MapDataMockService.generateSalesMapData()
    
    // 根据参数过滤数据
    let filteredData = data
    if (params?.region) {
      filteredData = data.filter(item => item.extra?.region === params.region)
    }
    
    return createMockResponse(filteredData)
  },

  // 获取客户地区分布数据
  async getCustomerMapData(params?: MapDataParams) {
    await mockDelay()
    const data = MapDataMockService.generateCustomerMapData()
    
    let filteredData = data
    if (params?.region) {
      filteredData = data.filter(item => item.extra?.region === params.region)
    }
    
    return createMockResponse(filteredData)
  },

  // 获取设备分布数据
  async getDeviceMapData(params?: MapDataParams) {
    await mockDelay()
    const data = MapDataMockService.generateDeviceMapData()
    
    let filteredData = data
    if (params?.region) {
      filteredData = data.filter(item => item.extra?.region === params.region)
    }
    
    return createMockResponse(filteredData)
  },

  // 获取租赁数据分布
  async getRentalMapData(params?: MapDataParams) {
    await mockDelay()
    const data = MapDataMockService.generateRentalMapData()
    
    let filteredData = data
    if (params?.region) {
      filteredData = data.filter(item => item.extra?.region === params.region)
    }
    
    return createMockResponse(filteredData)
  },

  // 获取地图配置
  async getMapConfig(mapType: string) {
    await mockDelay(100)
    const config = MapDataMockService.generateMapConfig(mapType)
    return createMockResponse(config)
  }
}