/**
 * 订单API测试工具
 * 任务19：实现前端API调用服务 - API测试和验证
 */

import { orderApi, orderStatusApi, orderSearchApi, orderExtensionApi } from './order'
import { OrderQueryBuilder, OrderValidator, OrderFormatter } from './orderApiUtils'
import type { CreateOrderRequest, OrderQueryParams } from '@/types/order'

/**
 * API测试结果接口
 */
interface TestResult {
  name: string
  success: boolean
  message: string
  duration: number
  data?: any
  error?: any
}

/**
 * 订单API测试套件
 */
export class OrderApiTestSuite {
  private results: TestResult[] = []

  /**
   * 运行单个测试
   */
  private async runTest(name: string, testFn: () => Promise<any>): Promise<TestResult> {
    const startTime = Date.now()
    
    try {
      const data = await testFn()
      const duration = Date.now() - startTime
      
      const result: TestResult = {
        name,
        success: true,
        message: '测试通过',
        duration,
        data
      }
      
      this.results.push(result)
      console.log(`✅ ${name} - ${duration}ms`)
      return result
    } catch (error) {
      const duration = Date.now() - startTime
      
      const result: TestResult = {
        name,
        success: false,
        message: error instanceof Error ? error.message : '未知错误',
        duration,
        error
      }
      
      this.results.push(result)
      console.error(`❌ ${name} - ${duration}ms`, error)
      return result
    }
  }

  /**
   * 测试订单列表API
   */
  async testGetOrders(): Promise<TestResult> {
    return this.runTest('获取订单列表', async () => {
      const params: OrderQueryParams = {
        page: 1,
        pageSize: 5
      }
      
      const response = await orderApi.getOrders(params)
      
      if (!response.data) {
        throw new Error('响应数据为空')
      }
      
      if (!Array.isArray(response.data.list)) {
        throw new Error('订单列表格式错误')
      }
      
      return {
        total: response.data.total,
        count: response.data.list.length
      }
    })
  }

  /**
   * 测试订单统计API
   */
  async testGetOrderStats(): Promise<TestResult> {
    return this.runTest('获取订单统计', async () => {
      const response = await orderApi.getOrderStats()
      
      if (!response.data) {
        throw new Error('统计数据为空')
      }
      
      const stats = response.data
      const requiredFields = ['total', 'pending', 'processing', 'completed', 'totalRevenue']
      
      for (const field of requiredFields) {
        if (!(field in stats)) {
          throw new Error(`缺少必要字段: ${field}`)
        }
      }
      
      return stats
    })
  }

  /**
   * 测试订单创建API（模拟数据）
   */
  async testCreateOrder(): Promise<TestResult> {
    return this.runTest('创建订单（模拟）', async () => {
      const orderData: CreateOrderRequest = {
        type: 'sales',
        customerName: '测试客户',
        customerPhone: '13800138000',
        customerEmail: 'test@example.com',
        deliveryAddress: '测试地址',
        items: [
          {
            productId: 'test-product-1',
            productName: '测试产品',
            quantity: 1,
            unitPrice: 100,
            totalPrice: 100
          }
        ],
        subtotal: 100,
        shippingFee: 10,
        discount: 0,
        totalAmount: 110,
        currency: 'CNY',
        notes: 'API测试订单'
      }

      // 验证订单数据
      const validation = OrderValidator.validateCreateOrder(orderData)
      if (!validation.isValid) {
        throw new Error(`订单数据验证失败: ${validation.errors.join(', ')}`)
      }

      // 注意：这里只是验证数据格式，不实际创建订单
      return {
        validation: validation.isValid,
        orderData
      }
    })
  }

  /**
   * 测试订单搜索API
   */
  async testSearchOrders(): Promise<TestResult> {
    return this.runTest('搜索订单', async () => {
      const suggestions = await orderSearchApi.searchOrderSuggestions('测试', 5)
      
      if (!Array.isArray(suggestions.data)) {
        throw new Error('搜索结果格式错误')
      }
      
      return {
        count: suggestions.data.length
      }
    })
  }

  /**
   * 测试筛选选项API
   */
  async testGetFilterOptions(): Promise<TestResult> {
    return this.runTest('获取筛选选项', async () => {
      const options = await orderSearchApi.getOrderFilterOptions()
      
      if (!options.data) {
        throw new Error('筛选选项数据为空')
      }
      
      const requiredFields = ['types', 'statuses']
      for (const field of requiredFields) {
        if (!(field in options.data)) {
          throw new Error(`缺少必要字段: ${field}`)
        }
      }
      
      return options.data
    })
  }

  /**
   * 测试查询构建器
   */
  async testQueryBuilder(): Promise<TestResult> {
    return this.runTest('查询构建器', async () => {
      const builder = new OrderQueryBuilder()
      const params = builder
        .page(1, 10)
        .keyword('测试')
        .type('sales')
        .status('pending')
        .dateRange('2024-01-01', '2024-12-31')
        .build()

      const expectedFields = ['page', 'pageSize', 'keyword', 'type', 'status', 'dateRange']
      for (const field of expectedFields) {
        if (!(field in params)) {
          throw new Error(`查询参数缺少字段: ${field}`)
        }
      }

      return params
    })
  }

  /**
   * 测试数据格式化器
   */
  async testFormatter(): Promise<TestResult> {
    return this.runTest('数据格式化器', async () => {
      const tests = [
        {
          name: '状态格式化',
          result: OrderFormatter.formatStatus('pending'),
          expected: '待确认'
        },
        {
          name: '类型格式化',
          result: OrderFormatter.formatType('sales'),
          expected: '销售订单'
        },
        {
          name: '金额格式化',
          result: OrderFormatter.formatAmount(1234.56),
          expected: '¥1,235'
        }
      ]

      const results = tests.map(test => ({
        ...test,
        success: test.result.includes(test.expected.split('¥')[1] || test.expected)
      }))

      const failedTests = results.filter(r => !r.success)
      if (failedTests.length > 0) {
        throw new Error(`格式化测试失败: ${failedTests.map(t => t.name).join(', ')}`)
      }

      return results
    })
  }

  /**
   * 运行所有测试
   */
  async runAllTests(): Promise<TestResult[]> {
    console.log('🚀 开始运行订单API测试套件...')
    
    this.results = []
    
    // 运行所有测试
    await Promise.all([
      this.testGetOrders(),
      this.testGetOrderStats(),
      this.testCreateOrder(),
      this.testSearchOrders(),
      this.testGetFilterOptions(),
      this.testQueryBuilder(),
      this.testFormatter()
    ])

    // 输出测试结果摘要
    const successCount = this.results.filter(r => r.success).length
    const totalCount = this.results.length
    const totalDuration = this.results.reduce((sum, r) => sum + r.duration, 0)

    console.log(`\n📊 测试结果摘要:`)
    console.log(`✅ 成功: ${successCount}/${totalCount}`)
    console.log(`⏱️  总耗时: ${totalDuration}ms`)
    console.log(`📈 成功率: ${Math.round((successCount / totalCount) * 100)}%`)

    if (successCount < totalCount) {
      console.log(`\n❌ 失败的测试:`)
      this.results
        .filter(r => !r.success)
        .forEach(r => console.log(`  - ${r.name}: ${r.message}`))
    }

    return this.results
  }

  /**
   * 获取测试结果
   */
  getResults(): TestResult[] {
    return [...this.results]
  }

  /**
   * 清空测试结果
   */
  clearResults(): void {
    this.results = []
  }
}

/**
 * API性能测试
 */
export class OrderApiPerformanceTest {
  /**
   * 测试API响应时间
   */
  static async testResponseTime(apiCall: () => Promise<any>, iterations: number = 10): Promise<{
    average: number
    min: number
    max: number
    results: number[]
  }> {
    const results: number[] = []

    for (let i = 0; i < iterations; i++) {
      const startTime = Date.now()
      try {
        await apiCall()
        const duration = Date.now() - startTime
        results.push(duration)
      } catch (error) {
        console.warn(`性能测试第${i + 1}次调用失败:`, error)
        results.push(-1) // 标记失败
      }
    }

    const validResults = results.filter(r => r > 0)
    if (validResults.length === 0) {
      throw new Error('所有API调用都失败了')
    }

    return {
      average: validResults.reduce((sum, r) => sum + r, 0) / validResults.length,
      min: Math.min(...validResults),
      max: Math.max(...validResults),
      results: validResults
    }
  }

  /**
   * 测试并发请求
   */
  static async testConcurrency(apiCall: () => Promise<any>, concurrency: number = 5): Promise<{
    successCount: number
    failureCount: number
    averageTime: number
    results: Array<{ success: boolean; duration: number; error?: any }>
  }> {
    const promises = Array.from({ length: concurrency }, async () => {
      const startTime = Date.now()
      try {
        await apiCall()
        return {
          success: true,
          duration: Date.now() - startTime
        }
      } catch (error) {
        return {
          success: false,
          duration: Date.now() - startTime,
          error
        }
      }
    })

    const results = await Promise.all(promises)
    const successResults = results.filter(r => r.success)
    const failureResults = results.filter(r => !r.success)

    return {
      successCount: successResults.length,
      failureCount: failureResults.length,
      averageTime: successResults.length > 0 
        ? successResults.reduce((sum, r) => sum + r.duration, 0) / successResults.length 
        : 0,
      results
    }
  }
}

// 创建默认测试实例
export const orderApiTestSuite = new OrderApiTestSuite()

// 导出测试工具
export const orderApiTest = {
  TestSuite: OrderApiTestSuite,
  PerformanceTest: OrderApiPerformanceTest,
  defaultSuite: orderApiTestSuite
}