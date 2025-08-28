/**
 * 地图数据API测试工具
 * 用于验证地图数据API的功能
 */

import { mapDataAPI } from '@/api/mapData'

export class MapDataTestService {
  // 测试所有地图数据API
  static async testAllMapDataAPIs() {
    console.log('🧪 开始测试地图数据API...')
    
    try {
      // 测试销售数据
      console.log('📊 测试销售数据API...')
      const salesData = await mapDataAPI.getSalesMapData()
      console.log('✅ 销售数据:', salesData.data?.length, '条记录')
      
      // 测试客户数据
      console.log('👥 测试客户数据API...')
      const customerData = await mapDataAPI.getCustomerMapData()
      console.log('✅ 客户数据:', customerData.data?.length, '条记录')
      
      // 测试设备数据
      console.log('🤖 测试设备数据API...')
      const deviceData = await mapDataAPI.getDeviceMapData()
      console.log('✅ 设备数据:', deviceData.data?.length, '条记录')
      
      // 测试租赁数据
      console.log('🏠 测试租赁数据API...')
      const rentalData = await mapDataAPI.getRentalMapData()
      console.log('✅ 租赁数据:', rentalData.data?.length, '条记录')
      
      // 测试地图配置
      console.log('🗺️ 测试地图配置API...')
      const worldConfig = await mapDataAPI.getMapConfig('world')
      const chinaConfig = await mapDataAPI.getMapConfig('china')
      console.log('✅ 世界地图配置:', worldConfig.data?.title)
      console.log('✅ 中国地图配置:', chinaConfig.data?.title)
      
      console.log('🎉 所有地图数据API测试通过！')
      
      return {
        success: true,
        results: {
          sales: salesData.data,
          customers: customerData.data,
          devices: deviceData.data,
          rentals: rentalData.data,
          worldConfig: worldConfig.data,
          chinaConfig: chinaConfig.data
        }
      }
      
    } catch (error) {
      console.error('❌ 地图数据API测试失败:', error)
      return {
        success: false,
        error
      }
    }
  }
  
  // 测试数据格式验证
  static validateMapDataFormat(data: any[]) {
    if (!Array.isArray(data)) {
      throw new Error('地图数据必须是数组格式')
    }
    
    for (const item of data) {
      if (!item.name || typeof item.name !== 'string') {
        throw new Error('地图数据项必须包含name字段')
      }
      
      if (item.value === undefined || (typeof item.value !== 'number' && !Array.isArray(item.value))) {
        throw new Error('地图数据项必须包含value字段')
      }
      
      if (item.coordinates && (!Array.isArray(item.coordinates) || item.coordinates.length !== 2)) {
        throw new Error('coordinates字段必须是包含两个数字的数组')
      }
    }
    
    return true
  }
  
  // 生成数据统计报告
  static generateDataReport(results: any) {
    const report = {
      timestamp: new Date().toISOString(),
      summary: {
        totalDataPoints: 0,
        dataTypes: 0,
        regions: new Set(),
        valueRanges: {}
      },
      details: {}
    }
    
    // 统计各类型数据
    Object.keys(results).forEach(key => {
      if (key.endsWith('Config')) return
      
      const data = results[key]
      if (Array.isArray(data)) {
        report.summary.totalDataPoints += data.length
        report.summary.dataTypes += 1
        
        // 统计地区
        data.forEach((item: any) => {
          if (item.name) {
            report.summary.regions.add(item.name)
          }
        })
        
        // 统计数值范围
        const values = data.map((item: any) => 
          typeof item.value === 'number' ? item.value : item.value[0]
        )
        report.summary.valueRanges[key] = {
          min: Math.min(...values),
          max: Math.max(...values),
          avg: Math.round(values.reduce((a, b) => a + b, 0) / values.length)
        }
        
        report.details[key] = {
          count: data.length,
          sampleData: data.slice(0, 3) // 前3条数据作为样本
        }
      }
    })
    
    report.summary.regions = Array.from(report.summary.regions)
    
    return report
  }
}

// 在开发环境下自动运行测试
if (import.meta.env.DEV) {
  // 延迟执行，确保其他模块已加载
  setTimeout(() => {
    MapDataTestService.testAllMapDataAPIs().then(result => {
      if (result.success) {
        const report = MapDataTestService.generateDataReport(result.results)
        console.log('📋 地图数据统计报告:', report)
      }
    })
  }, 2000)
}