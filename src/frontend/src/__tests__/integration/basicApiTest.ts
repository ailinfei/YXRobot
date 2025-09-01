/**
 * 基础API集成测试
 * 用于验证前后端API对接的基本功能
 */

import axios from 'axios'
import { checkBackendHealth, showBackendStatus } from '../utils/healthCheck.js'

const API_BASE_URL = 'http://localhost:8081'

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

interface TestResult {
  name: string
  passed: boolean
  message: string
  responseTime?: number
  data?: any
}

/**
 * 执行基础API测试
 */
export async function runBasicApiTests(): Promise<TestResult[]> {
  const results: TestResult[] = []
  
  console.log('🚀 开始基础API集成测试')
  console.log('=' .repeat(50))
  
  // 1. 检查后端服务健康状态
  console.log('\n📋 1. 后端服务健康检查')
  const healthResult = await checkBackendHealth()
  
  if (!healthResult.isHealthy) {
    results.push({
      name: '后端服务健康检查',
      passed: false,
      message: healthResult.message
    })
    
    console.log('❌ 后端服务不可用，跳过其他测试')
    return results
  }
  
  results.push({
    name: '后端服务健康检查',
    passed: true,
    message: healthResult.message,
    responseTime: healthResult.responseTime
  })
  
  // 2. 测试客户统计API
  console.log('\n📋 2. 客户统计API测试')
  try {
    const startTime = Date.now()
    const response = await api.get('/api/admin/customers/stats')
    const responseTime = Date.now() - startTime
    
    if (response.status === 200 && response.data.code === 200) {
      results.push({
        name: '客户统计API',
        passed: true,
        message: 'API响应正常',
        responseTime,
        data: response.data.data
      })
      console.log(`✅ 客户统计API测试通过 (${responseTime}ms)`)
      console.log(`📊 统计数据:`, response.data.data)
    } else {
      results.push({
        name: '客户统计API',
        passed: false,
        message: `API响应异常: ${response.data.message || 'Unknown error'}`,
        responseTime
      })
      console.log(`❌ 客户统计API测试失败`)
    }
  } catch (error: any) {
    results.push({
      name: '客户统计API',
      passed: false,
      message: `请求失败: ${error.message}`
    })
    console.log(`❌ 客户统计API测试失败: ${error.message}`)
  }
  
  // 3. 测试客户列表API
  console.log('\n📋 3. 客户列表API测试')
  try {
    const startTime = Date.now()
    const response = await api.get('/api/admin/customers', {
      params: { page: 1, pageSize: 5 }
    })
    const responseTime = Date.now() - startTime
    
    if (response.status === 200 && response.data.code === 200) {
      const data = response.data.data
      results.push({
        name: '客户列表API',
        passed: true,
        message: `API响应正常，返回${data.list.length}条记录`,
        responseTime,
        data: {
          total: data.total,
          listLength: data.list.length,
          page: data.page,
          pageSize: data.pageSize
        }
      })
      console.log(`✅ 客户列表API测试通过 (${responseTime}ms)`)
      console.log(`📋 列表数据: 总数${data.total}, 当前页${data.list.length}条`)
    } else {
      results.push({
        name: '客户列表API',
        passed: false,
        message: `API响应异常: ${response.data.message || 'Unknown error'}`,
        responseTime
      })
      console.log(`❌ 客户列表API测试失败`)
    }
  } catch (error: any) {
    results.push({
      name: '客户列表API',
      passed: false,
      message: `请求失败: ${error.message}`
    })
    console.log(`❌ 客户列表API测试失败: ${error.message}`)
  }
  
  // 4. 测试筛选选项API
  console.log('\n📋 4. 筛选选项API测试')
  try {
    const startTime = Date.now()
    const response = await api.get('/api/admin/customers/filter-options')
    const responseTime = Date.now() - startTime
    
    if (response.status === 200 && response.data.code === 200) {
      results.push({
        name: '筛选选项API',
        passed: true,
        message: 'API响应正常',
        responseTime,
        data: response.data.data
      })
      console.log(`✅ 筛选选项API测试通过 (${responseTime}ms)`)
      console.log(`🔧 筛选选项:`, response.data.data)
    } else {
      results.push({
        name: '筛选选项API',
        passed: false,
        message: `API响应异常: ${response.data.message || 'Unknown error'}`,
        responseTime
      })
      console.log(`❌ 筛选选项API测试失败`)
    }
  } catch (error: any) {
    results.push({
      name: '筛选选项API',
      passed: false,
      message: `请求失败: ${error.message}`
    })
    console.log(`❌ 筛选选项API测试失败: ${error.message}`)
  }
  
  // 5. 测试搜索功能
  console.log('\n📋 5. 搜索功能测试')
  try {
    const startTime = Date.now()
    const response = await api.get('/api/admin/customers', {
      params: { keyword: '测试', page: 1, pageSize: 5 }
    })
    const responseTime = Date.now() - startTime
    
    if (response.status === 200 && response.data.code === 200) {
      const data = response.data.data
      results.push({
        name: '搜索功能',
        passed: true,
        message: `搜索功能正常，找到${data.list.length}条匹配记录`,
        responseTime,
        data: {
          keyword: '测试',
          matchCount: data.list.length,
          total: data.total
        }
      })
      console.log(`✅ 搜索功能测试通过 (${responseTime}ms)`)
      console.log(`🔍 搜索结果: 关键词"测试"匹配${data.list.length}条记录`)
    } else {
      results.push({
        name: '搜索功能',
        passed: false,
        message: `搜索功能异常: ${response.data.message || 'Unknown error'}`,
        responseTime
      })
      console.log(`❌ 搜索功能测试失败`)
    }
  } catch (error: any) {
    results.push({
      name: '搜索功能',
      passed: false,
      message: `请求失败: ${error.message}`
    })
    console.log(`❌ 搜索功能测试失败: ${error.message}`)
  }
  
  return results
}

/**
 * 生成测试报告
 */
export function generateTestReport(results: TestResult[]): void {
  console.log('\n' + '=' .repeat(50))
  console.log('📊 API集成测试报告')
  console.log('=' .repeat(50))
  
  const passedTests = results.filter(r => r.passed)
  const failedTests = results.filter(r => !r.passed)
  
  console.log(`\n📈 测试总结:`)
  console.log(`总测试数: ${results.length}`)
  console.log(`通过测试: ${passedTests.length} ✅`)
  console.log(`失败测试: ${failedTests.length} ❌`)
  console.log(`通过率: ${Math.round((passedTests.length / results.length) * 100)}%`)
  
  if (passedTests.length > 0) {
    console.log(`\n✅ 通过的测试:`)
    passedTests.forEach(test => {
      const timeInfo = test.responseTime ? ` (${test.responseTime}ms)` : ''
      console.log(`  • ${test.name}: ${test.message}${timeInfo}`)
    })
  }
  
  if (failedTests.length > 0) {
    console.log(`\n❌ 失败的测试:`)
    failedTests.forEach(test => {
      console.log(`  • ${test.name}: ${test.message}`)
    })
    
    console.log(`\n🔧 解决建议:`)
    console.log(`1. 确保后端服务已启动: mvn spring-boot:run`)
    console.log(`2. 检查数据库连接是否正常`)
    console.log(`3. 验证API接口实现是否完整`)
    console.log(`4. 检查网络连接和防火墙设置`)
  }
  
  // 性能分析
  const responseTimes = results
    .filter(r => r.responseTime !== undefined)
    .map(r => r.responseTime!)
  
  if (responseTimes.length > 0) {
    const avgResponseTime = Math.round(responseTimes.reduce((a, b) => a + b, 0) / responseTimes.length)
    const maxResponseTime = Math.max(...responseTimes)
    const minResponseTime = Math.min(...responseTimes)
    
    console.log(`\n⚡ 性能分析:`)
    console.log(`平均响应时间: ${avgResponseTime}ms`)
    console.log(`最快响应时间: ${minResponseTime}ms`)
    console.log(`最慢响应时间: ${maxResponseTime}ms`)
    
    if (avgResponseTime > 2000) {
      console.log(`⚠️  平均响应时间较慢，建议优化API性能`)
    } else if (avgResponseTime < 500) {
      console.log(`🚀 API响应速度优秀`)
    } else {
      console.log(`👍 API响应速度良好`)
    }
  }
}

/**
 * 主函数 - 运行完整的API测试
 */
export async function main(): Promise<void> {
  try {
    // 显示后端服务状态
    await showBackendStatus()
    
    // 运行基础API测试
    const results = await runBasicApiTests()
    
    // 生成测试报告
    generateTestReport(results)
    
    // 根据测试结果设置退出码
    const failedCount = results.filter(r => !r.passed).length
    if (failedCount > 0) {
      console.log(`\n❌ 有 ${failedCount} 个测试失败`)
      process.exit(1)
    } else {
      console.log(`\n🎉 所有测试通过！前后端API对接成功`)
      process.exit(0)
    }
    
  } catch (error: any) {
    console.error('💥 测试执行失败:', error.message)
    process.exit(1)
  }
}

// 如果直接运行此文件
if (require.main === module) {
  main()
}