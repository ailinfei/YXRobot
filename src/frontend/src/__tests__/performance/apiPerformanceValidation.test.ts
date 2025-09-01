import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import axios from 'axios'

// 测试配置
const API_BASE_URL = 'http://localhost:8081'
const TEST_TIMEOUT = 15000

// 性能要求配置
const PERFORMANCE_REQUIREMENTS = {
  customerStats: 1000,      // 客户统计API < 1秒
  customerList: 2000,       // 客户列表API < 2秒
  customerDetail: 1500,     // 客户详情API < 1.5秒
  customerSearch: 1000,     // 客户搜索API < 1秒
  customerDevices: 2000,    // 客户设备API < 2秒
  customerOrders: 2000,     // 客户订单API < 2秒
  customerServiceRecords: 2000, // 客户服务记录API < 2秒
  customerCreate: 3000,     // 客户创建API < 3秒
  customerUpdate: 2000,     // 客户更新API < 2秒
  customerDelete: 1000      // 客户删除API < 1秒
}

// 创建axios实例
const testApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: TEST_TIMEOUT
})

// 性能测试结果
interface PerformanceResult {
  apiName: string
  responseTime: number
  requirement: number
  passed: boolean
  requestSize?: number
  responseSize?: number
}

const performanceResults: PerformanceResult[] = []

// 测试数据
let testCustomerId: string | null = null
const testCustomerData = {
  name: '性能测试客户',
  level: 'regular',
  phone: '13800138888',
  email: 'performance.test@example.com',
  status: 'active'
}

describe('API Performance Validation Tests - API性能验证', () => {
  beforeAll(async () => {
    console.log('开始API性能验证测试...')
    console.log('性能要求:')
    Object.entries(PERFORMANCE_REQUIREMENTS).forEach(([api, time]) => {
      console.log(`  ${api}: < ${time}ms`)
    })
  })

  afterAll(async () => {
    // 清理测试数据
    if (testCustomerId) {
      try {
        await testApi.delete(`/api/admin/customers/${testCustomerId}`)
      } catch (error) {
        console.warn('清理测试数据失败:', error)
      }
    }

    // 输出性能测试总结
    console.log('\n📊 API性能测试总结:')
    console.log('=' .repeat(60))
    
    performanceResults.forEach(result => {
      const status = result.passed ? '✅' : '❌'
      const percentage = ((result.responseTime / result.requirement) * 100).toFixed(1)
      console.log(`${status} ${result.apiName}: ${result.responseTime}ms (${percentage}% of requirement)`)
    })

    const passedCount = performanceResults.filter(r => r.passed).length
    const totalCount = performanceResults.length
    const passRate = totalCount > 0 ? ((passedCount / totalCount) * 100).toFixed(1) : '0'
    
    console.log('-' .repeat(60))
    console.log(`总体通过率: ${passedCount}/${totalCount} (${passRate}%)`)
    
    if (passedCount === totalCount) {
      console.log('🎉 所有API性能测试通过！')
    } else {
      console.log('⚠️ 部分API性能测试未通过，需要优化')
    }
  })

  describe('1. 核心查询API性能测试', () => {
    it('客户统计API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerStats
        
        performanceResults.push({
          apiName: '客户统计API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerStats,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户统计API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户统计API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerStats,
          passed: false
        })
        console.error('❌ 客户统计API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户列表API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 20 }
        })
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerList
        
        performanceResults.push({
          apiName: '客户列表API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerList,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户列表API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户列表API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerList,
          passed: false
        })
        console.error('❌ 客户列表API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户搜索API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { 
            keyword: '测试',
            page: 1, 
            pageSize: 10 
          }
        })
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerSearch
        
        performanceResults.push({
          apiName: '客户搜索API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerSearch,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户搜索API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户搜索API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerSearch,
          passed: false
        })
        console.error('❌ 客户搜索API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('2. CRUD操作API性能测试', () => {
    it('客户创建API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.post('/api/admin/customers', testCustomerData)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        // 保存测试客户ID用于后续测试
        testCustomerId = response.data.data.id
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerCreate
        
        performanceResults.push({
          apiName: '客户创建API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerCreate,
          passed,
          requestSize: JSON.stringify(testCustomerData).length,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户创建API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户创建API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerCreate,
          passed: false
        })
        console.error('❌ 客户创建API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户详情API性能应该满足要求', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      const startTime = Date.now()
      
      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}`)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerDetail
        
        performanceResults.push({
          apiName: '客户详情API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerDetail,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户详情API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户详情API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerDetail,
          passed: false
        })
        console.error('❌ 客户详情API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户更新API性能应该满足要求', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      const updateData = {
        name: '性能测试客户_已更新',
        notes: '性能测试更新'
      }

      const startTime = Date.now()
      
      try {
        const response = await testApi.put(`/api/admin/customers/${testCustomerId}`, updateData)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerUpdate
        
        performanceResults.push({
          apiName: '客户更新API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerUpdate,
          passed,
          requestSize: JSON.stringify(updateData).length,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户更新API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户更新API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerUpdate,
          passed: false
        })
        console.error('❌ 客户更新API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('3. 关联数据API性能测试', () => {
    it('客户设备API性能应该满足要求', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      const startTime = Date.now()
      
      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/devices`)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerDevices
        
        performanceResults.push({
          apiName: '客户设备API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerDevices,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户设备API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户设备API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerDevices,
          passed: false
        })
        console.error('❌ 客户设备API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户订单API性能应该满足要求', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      const startTime = Date.now()
      
      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/orders`)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerOrders
        
        performanceResults.push({
          apiName: '客户订单API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerOrders,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户订单API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户订单API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerOrders,
          passed: false
        })
        console.error('❌ 客户订单API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户服务记录API性能应该满足要求', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      const startTime = Date.now()
      
      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/service-records`)
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerServiceRecords
        
        performanceResults.push({
          apiName: '客户服务记录API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerServiceRecords,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 客户服务记录API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '客户服务记录API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerServiceRecords,
          passed: false
        })
        console.error('❌ 客户服务记录API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('4. 大数据量性能测试', () => {
    it('大页面客户列表API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 100 } // 测试大页面
        })
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerList * 2 // 大数据量允许2倍时间
        
        performanceResults.push({
          apiName: '大页面客户列表API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerList * 2,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 大页面客户列表API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '大页面客户列表API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerList * 2,
          passed: false
        })
        console.error('❌ 大页面客户列表API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('复杂筛选查询API性能应该满足要求', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { 
            keyword: '测试',
            level: 'vip',
            status: 'active',
            page: 1, 
            pageSize: 50 
          }
        })
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const passed = responseTime < PERFORMANCE_REQUIREMENTS.customerSearch * 2 // 复杂查询允许2倍时间
        
        performanceResults.push({
          apiName: '复杂筛选查询API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerSearch * 2,
          passed,
          responseSize: JSON.stringify(response.data).length
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 复杂筛选查询API性能测试通过: ${responseTime}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: '复杂筛选查询API',
          responseTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerSearch * 2,
          passed: false
        })
        console.error('❌ 复杂筛选查询API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('5. 并发性能测试', () => {
    it('并发客户统计API请求性能应该满足要求', async () => {
      const concurrentRequests = 5
      const startTime = Date.now()
      
      try {
        const promises = Array(concurrentRequests).fill(null).map(() => 
          testApi.get('/api/admin/customers/stats')
        )
        
        const responses = await Promise.all(promises)
        const responseTime = Date.now() - startTime
        
        // 验证所有请求都成功
        responses.forEach(response => {
          expect(response.status).toBe(200)
          expect(response.data.code).toBe(200)
        })
        
        const averageTime = responseTime / concurrentRequests
        const passed = averageTime < PERFORMANCE_REQUIREMENTS.customerStats * 1.5 // 并发允许1.5倍时间
        
        performanceResults.push({
          apiName: `并发客户统计API (${concurrentRequests}个请求)`,
          responseTime: averageTime,
          requirement: PERFORMANCE_REQUIREMENTS.customerStats * 1.5,
          passed
        })
        
        expect(passed).toBe(true)
        console.log(`✅ 并发客户统计API性能测试通过: 平均${averageTime.toFixed(0)}ms`)
        
      } catch (error) {
        const responseTime = Date.now() - startTime
        performanceResults.push({
          apiName: `并发客户统计API (${concurrentRequests}个请求)`,
          responseTime: responseTime / concurrentRequests,
          requirement: PERFORMANCE_REQUIREMENTS.customerStats * 1.5,
          passed: false
        })
        console.error('❌ 并发客户统计API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })
})