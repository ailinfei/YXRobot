/**
 * Mock数据系统测试文件
 * 用于验证所有Mock服务是否正常工作
 */

import { 
  initializeMockData, 
  getMockDataStats,
  mockDashboardAPI,
  mockSalesAPI,
  mockRentalAPI,
  mockCustomerAPI,
  mockProductAPI,
  mockCharityAPI,
  mockPlatformLinkAPI
} from './index'

// 测试所有Mock API的基本功能
export async function testAllMockAPIs() {
  console.log('🧪 开始测试YXRobot管理后台Mock数据系统...')
  
  try {
    // 初始化Mock数据系统
    initializeMockData()
    
    // 获取数据统计
    const stats = getMockDataStats()
    console.log('📊 Mock数据统计:', stats)
    
    // 测试数据看板API
    console.log('\n🔍 测试数据看板API...')
    const dashboardStats = await mockDashboardAPI.getDashboardStats()
    const recentActivities = await mockDashboardAPI.getRecentActivities({ page: 1, pageSize: 5 })
    const trendData = await mockDashboardAPI.getTrendData({ days: 7 })
    const topPerformers = await mockDashboardAPI.getTopPerformers()
    
    console.log('✅ 数据看板API测试通过')
    console.log(`  - 统计数据: ${dashboardStats.data ? '✓' : '✗'}`)
    console.log(`  - 最近活动: ${recentActivities.data?.list?.length || 0} 条`)
    console.log(`  - 趋势数据: ${trendData.data?.length || 0} 天`)
    console.log(`  - 顶级表现者: ${topPerformers.data ? '✓' : '✗'}`)
    
    // 测试销售管理API
    console.log('\n🔍 测试销售管理API...')
    const salesStats = await mockSalesAPI.getSalesStats()
    const salesOrders = await mockSalesAPI.getSalesOrders({ page: 1, pageSize: 5 })
    
    console.log('✅ 销售管理API测试通过')
    console.log(`  - 销售统计: ${salesStats.data ? '✓' : '✗'}`)
    console.log(`  - 销售订单: ${salesOrders.data?.list?.length || 0} 条`)
    
    // 测试租赁管理API
    console.log('\n🔍 测试租赁管理API...')
    const rentalStats = await mockRentalAPI.getRentalStats()
    const rentalOrders = await mockRentalAPI.getRentalOrders({ page: 1, pageSize: 5 })
    
    console.log('✅ 租赁管理API测试通过')
    console.log(`  - 租赁统计: ${rentalStats.data ? '✓' : '✗'}`)
    console.log(`  - 租赁订单: ${rentalOrders.data?.list?.length || 0} 条`)
    
    // 测试客户管理API
    console.log('\n🔍 测试客户管理API...')
    const customers = await mockCustomerAPI.getCustomers({ page: 1, pageSize: 5 })
    
    console.log('✅ 客户管理API测试通过')
    console.log(`  - 客户列表: ${customers.data?.list?.length || 0} 条`)
    
    // 测试平台链接管理API
    console.log('\n🔍 测试平台链接管理API...')
    const platformLinks = await mockPlatformLinkAPI.getPlatformLinks({ page: 1, pageSize: 5 })
    const platformStats = await mockPlatformLinkAPI.getPlatformLinkStats()
    const regionConfigs = await mockPlatformLinkAPI.getRegionConfigs()
    
    console.log('✅ 平台链接管理API测试通过')
    console.log(`  - 平台链接: ${platformLinks.data?.list?.length || 0} 条`)
    console.log(`  - 统计数据: ${platformStats.data ? '✓' : '✗'}`)
    console.log(`  - 地区配置: ${regionConfigs.data?.length || 0} 个`)
    
    // 测试产品管理API
    console.log('\n🔍 测试产品管理API...')
    const products = await mockProductAPI.getProducts({ page: 1, pageSize: 5 })
    
    console.log('✅ 产品管理API测试通过')
    console.log(`  - 产品列表: ${products.data?.list?.length || 0} 条`)
    
    // 测试公益管理API
    console.log('\n🔍 测试公益管理API...')
    const charityStats = await mockCharityAPI.getCharityStats()
    const charityProjects = await mockCharityAPI.getCharityProjects({ page: 1, pageSize: 5 })
    
    console.log('✅ 公益管理API测试通过')
    console.log(`  - 公益统计: ${charityStats.data ? '✓' : '✗'}`)
    console.log(`  - 公益项目: ${charityProjects.data?.list?.length || 0} 条`)
    
    console.log('\n🎉 所有Mock API测试完成！')
    return true
    
  } catch (error) {
    console.error('❌ Mock API测试失败:', error)
    return false
  }
}

// 测试特定API模块
export async function testPlatformLinkAPI() {
  console.log('🧪 测试平台链接管理API...')
  
  try {
    // 测试获取平台链接列表
    const linksResponse = await mockPlatformLinkAPI.getPlatformLinks({
      page: 1,
      pageSize: 10
    })
    console.log('✅ 获取平台链接列表:', linksResponse.data?.list?.length || 0, '条')
    
    // 测试获取统计数据
    const statsResponse = await mockPlatformLinkAPI.getPlatformLinkStats()
    console.log('✅ 获取统计数据:', statsResponse.data ? '成功' : '失败')
    console.log('  - 总链接数:', statsResponse.data?.totalLinks || 0)
    console.log('  - 活跃链接:', statsResponse.data?.activeLinks || 0)
    console.log('  - 总点击量:', statsResponse.data?.totalClicks || 0)
    console.log('  - 转化率:', statsResponse.data?.conversionRate || 0, '%')
    
    // 测试获取地区配置
    const regionsResponse = await mockPlatformLinkAPI.getRegionConfigs()
    console.log('✅ 获取地区配置:', regionsResponse.data?.length || 0, '个地区')
    
    // 测试创建平台链接
    const newLinkData = {
      platformName: '测试平台',
      platformType: 'ecommerce' as const,
      linkUrl: 'https://test.com/product/123',
      region: '中国大陆',
      country: '中国',
      languageCode: 'zh-CN',
      isEnabled: true
    }
    
    const createResponse = await mockPlatformLinkAPI.createPlatformLink(newLinkData)
    console.log('✅ 创建平台链接:', createResponse.data ? '成功' : '失败')
    
    // 测试验证链接
    if (linksResponse.data?.list?.length > 0) {
      const firstLinkId = linksResponse.data.list[0].id
      const validateResponse = await mockPlatformLinkAPI.validatePlatformLink(firstLinkId)
      console.log('✅ 验证链接:', validateResponse.data ? '成功' : '失败')
    }
    
    console.log('🎉 平台链接管理API测试完成！')
    return true
    
  } catch (error) {
    console.error('❌ 平台链接管理API测试失败:', error)
    return false
  }
}

// 导出测试函数
export { testAllMockAPIs as default }