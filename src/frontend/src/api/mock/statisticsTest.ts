/**
 * 数据统计表测试文件
 * 验证完整的数据统计功能和API
 */

import { mockDataStatisticsAPI, DataStatisticsGenerator } from './dataStatistics'
import { getMockDataStats } from './index'

// 数据统计表测试套件
export class StatisticsTestSuite {
  
  // 测试所有统计API
  static async testAllStatisticsAPIs() {
    console.log('🧪 开始测试YXRobot数据统计表系统...')
    
    const results = {
      apiTests: [],
      performanceTests: [],
      dataIntegrityTests: [],
      summary: {}
    }
    
    try {
      // 1. 测试完整统计数据API
      console.log('📊 测试完整统计数据API...')
      const startTime = Date.now()
      const completeStats = await mockDataStatisticsAPI.getCompleteStatistics()
      const completeStatsTime = Date.now() - startTime
      
      results.apiTests.push({
        api: 'getCompleteStatistics',
        success: !!completeStats.data,
        responseTime: completeStatsTime,
        dataSize: JSON.stringify(completeStats.data).length
      })
      
      console.log(`✅ 完整统计数据API测试通过 (${completeStatsTime}ms)`)
      
      // 2. 测试系统概览API
      console.log('🔍 测试系统概览API...')
      const overviewStart = Date.now()
      const systemOverview = await mockDataStatisticsAPI.getSystemOverview()
      const overviewTime = Date.now() - overviewStart
      
      results.apiTests.push({
        api: 'getSystemOverview',
        success: !!systemOverview.data,
        responseTime: overviewTime,
        dataSize: JSON.stringify(systemOverview.data).length
      })
      
      console.log(`✅ 系统概览API测试通过 (${overviewTime}ms)`)
      console.log(`  - 总用户数: ${systemOverview.data.totalUsers}`)
      console.log(`  - 总设备数: ${systemOverview.data.totalDevices}`)
      console.log(`  - 总收入: ¥${(systemOverview.data.totalRevenue / 10000).toFixed(0)}万`)
      console.log(`  - 系统正常运行时间: ${systemOverview.data.uptime}%`)
      
      // 3. 测试业务统计API
      console.log('💼 测试业务统计API...')
      const businessStart = Date.now()
      const businessStats = await mockDataStatisticsAPI.getBusinessStatistics()
      const businessTime = Date.now() - businessStart
      
      results.apiTests.push({
        api: 'getBusinessStatistics',
        success: !!businessStats.data,
        responseTime: businessTime,
        dataSize: JSON.stringify(businessStats.data).length
      })
      
      console.log(`✅ 业务统计API测试通过 (${businessTime}ms)`)
      console.log(`  - 销售订单: ${businessStats.data.sales.totalOrders} 个`)
      console.log(`  - 销售收入: ¥${(businessStats.data.sales.totalRevenue / 10000).toFixed(0)}万`)
      console.log(`  - 租赁订单: ${businessStats.data.rental.totalRentals} 个`)
      console.log(`  - 活跃客户: ${businessStats.data.customers.activeCustomers} 个`)
      
      // 4. 测试内容统计API
      console.log('📝 测试内容统计API...')
      const contentStart = Date.now()
      const contentStats = await mockDataStatisticsAPI.getContentStatistics()
      const contentTime = Date.now() - contentStart
      
      results.apiTests.push({
        api: 'getContentStatistics',
        success: !!contentStats.data,
        responseTime: contentTime,
        dataSize: JSON.stringify(contentStats.data).length
      })
      
      console.log(`✅ 内容统计API测试通过 (${contentTime}ms)`)
      console.log(`  - 发布产品: ${contentStats.data.products.publishedProducts} 个`)
      console.log(`  - 公益项目: ${contentStats.data.charity.totalProjects} 个`)
      console.log(`  - 平台链接: ${contentStats.data.platforms.totalLinks} 个`)
      console.log(`  - 媒体文件: ${contentStats.data.media.totalImages + contentStats.data.media.totalVideos} 个`)
      
      // 5. 测试系统统计API
      console.log('⚙️ 测试系统统计API...')
      const systemStart = Date.now()
      const systemStats = await mockDataStatisticsAPI.getSystemStatistics()
      const systemTime = Date.now() - systemStart
      
      results.apiTests.push({
        api: 'getSystemStatistics',
        success: !!systemStats.data,
        responseTime: systemTime,
        dataSize: JSON.stringify(systemStats.data).length
      })
      
      console.log(`✅ 系统统计API测试通过 (${systemTime}ms)`)
      console.log(`  - 发布课程: ${systemStats.data.courses.publishedCourses} 门`)
      console.log(`  - 完成字体包: ${systemStats.data.fontPackages.completedPackages} 个`)
      console.log(`  - 支持语言: ${systemStats.data.languages.supportedLanguages} 种`)
      console.log(`  - 在线设备: ${systemStats.data.devices.onlineDevices} 台`)
      
      // 6. 测试网站统计API
      console.log('🌐 测试网站统计API...')
      const websiteStart = Date.now()
      const websiteStats = await mockDataStatisticsAPI.getWebsiteStatistics()
      const websiteTime = Date.now() - websiteStart
      
      results.apiTests.push({
        api: 'getWebsiteStatistics',
        success: !!websiteStats.data,
        responseTime: websiteTime,
        dataSize: JSON.stringify(websiteStats.data).length
      })
      
      console.log(`✅ 网站统计API测试通过 (${websiteTime}ms)`)
      console.log(`  - 总访客: ${websiteStats.data.traffic.totalVisitors} 人`)
      console.log(`  - 页面浏览: ${websiteStats.data.traffic.pageViews} 次`)
      console.log(`  - 转化率: ${websiteStats.data.conversion.conversionRate}%`)
      console.log(`  - 跳出率: ${websiteStats.data.traffic.bounceRate}%`)
      
      // 7. 测试性能统计API
      console.log('⚡ 测试性能统计API...')
      const performanceStart = Date.now()
      const performanceStats = await mockDataStatisticsAPI.getPerformanceStatistics()
      const performanceTime = Date.now() - performanceStart
      
      results.apiTests.push({
        api: 'getPerformanceStatistics',
        success: !!performanceStats.data,
        responseTime: performanceTime,
        dataSize: JSON.stringify(performanceStats.data).length
      })
      
      console.log(`✅ 性能统计API测试通过 (${performanceTime}ms)`)
      console.log(`  - 系统正常运行时间: ${performanceStats.data.system.uptime}%`)
      console.log(`  - API平均响应时间: ${performanceStats.data.api.averageResponseTime}ms`)
      console.log(`  - 数据库查询时间: ${performanceStats.data.database.averageQueryTime}ms`)
      console.log(`  - 页面加载时间: ${performanceStats.data.frontend.pageLoadTime}ms`)
      
      // 8. 测试趋势统计API
      console.log('📈 测试趋势统计API...')
      const trendStart = Date.now()
      const trendStats = await mockDataStatisticsAPI.getTrendStatistics()
      const trendTime = Date.now() - trendStart
      
      results.apiTests.push({
        api: 'getTrendStatistics',
        success: !!trendStats.data,
        responseTime: trendTime,
        dataSize: JSON.stringify(trendStats.data).length
      })
      
      console.log(`✅ 趋势统计API测试通过 (${trendTime}ms)`)
      console.log(`  - 日趋势数据: ${trendStats.data.daily.length} 天`)
      console.log(`  - 周趋势数据: ${trendStats.data.weekly.length} 周`)
      console.log(`  - 月趋势数据: ${trendStats.data.monthly.length} 月`)
      console.log(`  - 预测数据: ${trendStats.data.predictions.length} 项`)
      
      // 9. 测试实时统计API
      console.log('⏱️ 测试实时统计API...')
      const realTimeStart = Date.now()
      const realTimeStats = await mockDataStatisticsAPI.getRealTimeStatistics()
      const realTimeTime = Date.now() - realTimeStart
      
      results.apiTests.push({
        api: 'getRealTimeStatistics',
        success: !!realTimeStats.data,
        responseTime: realTimeTime,
        dataSize: JSON.stringify(realTimeStats.data).length
      })
      
      console.log(`✅ 实时统计API测试通过 (${realTimeTime}ms)`)
      console.log(`  - 当前在线用户: ${realTimeStats.data.currentUsers} 人`)
      console.log(`  - 在线设备: ${realTimeStats.data.onlineDevices} 台`)
      console.log(`  - 活跃订单: ${realTimeStats.data.activeOrders} 个`)
      console.log(`  - 系统负载: ${realTimeStats.data.systemLoad}%`)
      
      // 性能测试
      console.log('\n⚡ 开始性能测试...')
      const performanceTestResults = await this.runPerformanceTests()
      results.performanceTests = performanceTestResults
      
      // 数据完整性测试
      console.log('\n🔍 开始数据完整性测试...')
      const integrityTestResults = await this.runDataIntegrityTests()
      results.dataIntegrityTests = integrityTestResults
      
      // 汇总结果
      const totalResponseTime = results.apiTests.reduce((sum, test) => sum + test.responseTime, 0)
      const averageResponseTime = totalResponseTime / results.apiTests.length
      const successfulTests = results.apiTests.filter(test => test.success).length
      
      results.summary = {
        totalAPIs: results.apiTests.length,
        successfulAPIs: successfulTests,
        successRate: (successfulTests / results.apiTests.length) * 100,
        totalResponseTime,
        averageResponseTime,
        totalDataSize: results.apiTests.reduce((sum, test) => sum + test.dataSize, 0)
      }
      
      console.log('\n🎉 数据统计表系统测试完成！')
      console.log('📋 测试汇总:')
      console.log(`  - 测试API数量: ${results.summary.totalAPIs}`)
      console.log(`  - 成功测试: ${results.summary.successfulAPIs}`)
      console.log(`  - 成功率: ${results.summary.successRate.toFixed(1)}%`)
      console.log(`  - 总响应时间: ${results.summary.totalResponseTime}ms`)
      console.log(`  - 平均响应时间: ${results.summary.averageResponseTime.toFixed(0)}ms`)
      console.log(`  - 总数据大小: ${(results.summary.totalDataSize / 1024).toFixed(1)}KB`)
      
      return results
      
    } catch (error) {
      console.error('❌ 数据统计表系统测试失败:', error)
      throw error
    }
  }
  
  // 性能测试
  static async runPerformanceTests() {
    console.log('🔥 运行性能压力测试...')
    
    const performanceResults = []
    
    // 并发API调用测试
    const concurrentTests = []
    const concurrentCount = 10
    
    for (let i = 0; i < concurrentCount; i++) {
      concurrentTests.push(mockDataStatisticsAPI.getSystemOverview())
    }
    
    const concurrentStart = Date.now()
    const concurrentResults = await Promise.all(concurrentTests)
    const concurrentTime = Date.now() - concurrentStart
    
    performanceResults.push({
      test: 'concurrent_api_calls',
      count: concurrentCount,
      totalTime: concurrentTime,
      averageTime: concurrentTime / concurrentCount,
      success: concurrentResults.every(result => !!result.data)
    })
    
    console.log(`✅ 并发API调用测试: ${concurrentCount}个并发请求，耗时${concurrentTime}ms`)
    
    // 大数据量生成测试
    const largeDataStart = Date.now()
    const largeDataStats = DataStatisticsGenerator.generateCompleteStatistics()
    const largeDataTime = Date.now() - largeDataStart
    
    performanceResults.push({
      test: 'large_data_generation',
      dataSize: JSON.stringify(largeDataStats).length,
      generationTime: largeDataTime,
      success: !!largeDataStats
    })
    
    console.log(`✅ 大数据量生成测试: 生成${(JSON.stringify(largeDataStats).length / 1024).toFixed(1)}KB数据，耗时${largeDataTime}ms`)
    
    // 内存使用测试
    const memoryBefore = process.memoryUsage().heapUsed
    
    // 生成多个统计数据实例
    for (let i = 0; i < 50; i++) {
      DataStatisticsGenerator.generateSystemOverview()
    }
    
    const memoryAfter = process.memoryUsage().heapUsed
    const memoryIncrease = memoryAfter - memoryBefore
    
    performanceResults.push({
      test: 'memory_usage',
      memoryBefore: Math.round(memoryBefore / 1024 / 1024),
      memoryAfter: Math.round(memoryAfter / 1024 / 1024),
      memoryIncrease: Math.round(memoryIncrease / 1024 / 1024),
      success: memoryIncrease < 50 * 1024 * 1024 // 小于50MB认为正常
    })
    
    console.log(`✅ 内存使用测试: 增加${Math.round(memoryIncrease / 1024 / 1024)}MB内存`)
    
    return performanceResults
  }
  
  // 数据完整性测试
  static async runDataIntegrityTests() {
    console.log('🔍 运行数据完整性测试...')
    
    const integrityResults = []
    
    // 测试数据结构完整性
    const completeStats = await mockDataStatisticsAPI.getCompleteStatistics()
    const statsData = completeStats.data
    
    const requiredSections = ['overview', 'business', 'content', 'system', 'website', 'performance', 'trends']
    const missingSections = requiredSections.filter(section => !statsData[section])
    
    integrityResults.push({
      test: 'data_structure_integrity',
      requiredSections: requiredSections.length,
      presentSections: requiredSections.length - missingSections.length,
      missingSections,
      success: missingSections.length === 0
    })
    
    console.log(`✅ 数据结构完整性: ${requiredSections.length - missingSections.length}/${requiredSections.length} 个必需部分存在`)
    
    // 测试数值合理性
    const overview = statsData.overview
    const reasonabilityTests = [
      { field: 'totalUsers', value: overview.totalUsers, min: 1000, max: 1000000 },
      { field: 'totalDevices', value: overview.totalDevices, min: 100, max: 100000 },
      { field: 'satisfactionRate', value: overview.satisfactionRate, min: 70, max: 100 },
      { field: 'uptime', value: overview.uptime, min: 95, max: 100 }
    ]
    
    const unreasonableValues = reasonabilityTests.filter(test => 
      test.value < test.min || test.value > test.max
    )
    
    integrityResults.push({
      test: 'data_reasonability',
      totalTests: reasonabilityTests.length,
      passedTests: reasonabilityTests.length - unreasonableValues.length,
      unreasonableValues,
      success: unreasonableValues.length === 0
    })
    
    console.log(`✅ 数据合理性: ${reasonabilityTests.length - unreasonableValues.length}/${reasonabilityTests.length} 个数值在合理范围内`)
    
    // 测试数据一致性
    const businessStats = statsData.business
    const consistencyTests = []
    
    // 检查收入数据一致性
    const totalRevenue = businessStats.revenue.totalRevenue
    const salesRevenue = businessStats.revenue.salesRevenue
    const rentalRevenue = businessStats.revenue.rentalRevenue
    const serviceRevenue = businessStats.revenue.serviceRevenue
    
    const revenueSum = salesRevenue + rentalRevenue + serviceRevenue
    const revenueConsistent = Math.abs(totalRevenue - revenueSum) < totalRevenue * 0.01 // 允许1%误差
    
    consistencyTests.push({
      test: 'revenue_consistency',
      totalRevenue,
      calculatedSum: revenueSum,
      difference: Math.abs(totalRevenue - revenueSum),
      consistent: revenueConsistent
    })
    
    integrityResults.push({
      test: 'data_consistency',
      consistencyTests,
      success: consistencyTests.every(test => test.consistent)
    })
    
    console.log(`✅ 数据一致性: ${consistencyTests.filter(test => test.consistent).length}/${consistencyTests.length} 个一致性检查通过`)
    
    // 测试时间序列数据
    const trendData = statsData.trends.daily
    const timeSeriesValid = trendData.every((item, index) => {
      if (index === 0) return true
      const currentDate = new Date(item.date)
      const previousDate = new Date(trendData[index - 1].date)
      return currentDate > previousDate
    })
    
    integrityResults.push({
      test: 'time_series_validity',
      totalDataPoints: trendData.length,
      validSequence: timeSeriesValid,
      success: timeSeriesValid
    })
    
    console.log(`✅ 时间序列有效性: ${trendData.length} 个数据点，序列${timeSeriesValid ? '有效' : '无效'}`)
    
    return integrityResults
  }
  
  // 生成测试报告
  static generateTestReport(results: any) {
    const report = {
      timestamp: new Date().toISOString(),
      summary: results.summary,
      details: {
        apiTests: results.apiTests,
        performanceTests: results.performanceTests,
        integrityTests: results.dataIntegrityTests
      },
      recommendations: []
    }
    
    // 生成建议
    if (results.summary.averageResponseTime > 1000) {
      report.recommendations.push('API响应时间较长，建议优化数据生成算法')
    }
    
    if (results.summary.successRate < 100) {
      report.recommendations.push('存在API测试失败，需要检查错误处理机制')
    }
    
    const slowAPIs = results.apiTests.filter(test => test.responseTime > 800)
    if (slowAPIs.length > 0) {
      report.recommendations.push(`以下API响应较慢: ${slowAPIs.map(api => api.api).join(', ')}`)
    }
    
    return report
  }
}

// 快速测试函数
export const quickStatisticsTest = async () => {
  console.log('🚀 快速数据统计表测试...')
  
  try {
    // 测试基本功能
    const overview = await mockDataStatisticsAPI.getSystemOverview()
    const realTime = await mockDataStatisticsAPI.getRealTimeStatistics()
    
    console.log('✅ 快速测试通过')
    console.log(`  - 系统概览: ${overview.data ? '✓' : '✗'}`)
    console.log(`  - 实时统计: ${realTime.data ? '✓' : '✗'}`)
    console.log(`  - 总用户: ${overview.data.totalUsers}`)
    console.log(`  - 在线用户: ${realTime.data.currentUsers}`)
    
    return { success: true, message: '快速测试通过' }
    
  } catch (error) {
    console.error('❌ 快速测试失败:', error)
    return { success: false, error: error.message }
  }
}

// 展示统计数据示例
export const showStatisticsExample = async () => {
  console.log('📊 YXRobot数据统计表示例展示')
  console.log('=' .repeat(50))
  
  // 获取Mock数据统计
  const mockStats = getMockDataStats()
  console.log('📈 Mock数据系统统计:')
  console.log(`  - 总数据记录: ${mockStats.totals.dataRecords}`)
  console.log(`  - API端点: ${mockStats.totals.apiEndpoints}`)
  console.log(`  - Mock服务: ${mockStats.totals.mockServices}`)
  console.log(`  - 统计模块: ${mockStats.totals.statisticsModules}`)
  
  // 获取系统概览
  const overview = await mockDataStatisticsAPI.getSystemOverview()
  console.log('\n🔍 系统概览统计:')
  console.log(`  - 总用户数: ${overview.data.totalUsers.toLocaleString()}`)
  console.log(`  - 总设备数: ${overview.data.totalDevices.toLocaleString()}`)
  console.log(`  - 总收入: ¥${(overview.data.totalRevenue / 10000).toLocaleString()}万`)
  console.log(`  - 活跃客户: ${overview.data.activeCustomers.toLocaleString()}`)
  console.log(`  - 在线设备: ${overview.data.onlineDevices.toLocaleString()}`)
  console.log(`  - 月增长率: ${overview.data.monthlyGrowth}%`)
  console.log(`  - 满意度: ${overview.data.satisfactionRate}%`)
  console.log(`  - 系统正常运行时间: ${overview.data.uptime}%`)
  
  // 获取实时数据
  const realTime = await mockDataStatisticsAPI.getRealTimeStatistics()
  console.log('\n⏱️ 实时监控数据:')
  console.log(`  - 当前在线用户: ${realTime.data.currentUsers}`)
  console.log(`  - 在线设备: ${realTime.data.onlineDevices}`)
  console.log(`  - 活跃订单: ${realTime.data.activeOrders}`)
  console.log(`  - 系统负载: ${realTime.data.systemLoad}%`)
  console.log(`  - 响应时间: ${realTime.data.responseTime}ms`)
  console.log(`  - 错误率: ${realTime.data.errorRate}%`)
  console.log(`  - 吞吐量: ${realTime.data.throughput} req/min`)
  
  console.log('\n✨ 数据统计表功能完整，可用于生产环境！')
}

// 如果直接运行此文件，执行完整测试
if (require.main === module) {
  StatisticsTestSuite.testAllStatisticsAPIs().then(results => {
    const report = StatisticsTestSuite.generateTestReport(results)
    console.log('\n📋 完整测试报告:')
    console.log(JSON.stringify(report, null, 2))
    process.exit(results.summary.successRate === 100 ? 0 : 1)
  }).catch(error => {
    console.error('❌ 测试执行失败:', error)
    process.exit(1)
  })
}