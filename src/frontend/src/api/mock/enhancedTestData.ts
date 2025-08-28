/**
 * 增强测试数据集
 * 包含边界情况、异常场景和压力测试数据
 */

import { mockTestSuite } from './test'

// 边界测试数据配置
export const EDGE_CASE_CONFIG = {
  // 极限数据量测试
  stress: {
    customers: 10000,
    orders: 50000,
    devices: 5000,
    courses: 1000,
    characters: 50000
  },
  
  // 空数据测试
  empty: {
    customers: 0,
    orders: 0,
    devices: 0,
    courses: 0
  },
  
  // 异常数据测试
  anomaly: {
    invalidIds: [-1, 0, 999999, 'invalid', null, undefined],
    invalidDates: ['invalid-date', '2024-13-32', '2023-02-30'],
    invalidNumbers: [-999999, 0, Infinity, NaN, 'not-a-number'],
    invalidStrings: ['', '   ', null, undefined, '<script>alert("xss")</script>']
  }
}

// 性能测试数据生成器
export class PerformanceTestDataGenerator {
  
  // 生成大量客户数据用于性能测试
  static generateStressTestCustomers(count: number = 10000) {
    console.log(`🔥 生成 ${count} 个客户数据用于压力测试...`)
    
    const customers = []
    const batchSize = 1000
    
    for (let i = 0; i < count; i += batchSize) {
      const batch = []
      const currentBatchSize = Math.min(batchSize, count - i)
      
      for (let j = 0; j < currentBatchSize; j++) {
        const customerId = i + j + 1
        batch.push({
          id: customerId,
          name: `压力测试客户_${customerId}`,
          email: `stress_test_${customerId}@example.com`,
          phone: `1380000${String(customerId).padStart(4, '0')}`,
          address: `压力测试地址 ${customerId} 号`,
          registrationDate: new Date(2020 + Math.floor(Math.random() * 4), 
                                   Math.floor(Math.random() * 12), 
                                   Math.floor(Math.random() * 28) + 1).toISOString(),
          status: Math.random() > 0.1 ? 'active' : 'inactive',
          totalOrders: Math.floor(Math.random() * 100),
          totalSpent: Math.floor(Math.random() * 100000),
          deviceCount: Math.floor(Math.random() * 10),
          lastLoginDate: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
          tags: [`tag_${Math.floor(Math.random() * 10)}`, `category_${Math.floor(Math.random() * 5)}`],
          notes: `压力测试客户备注 ${customerId}`
        })
      }
      
      customers.push(...batch)
      
      // 显示进度
      if (i % 5000 === 0) {
        console.log(`  已生成 ${i + currentBatchSize}/${count} 个客户数据`)
      }
    }
    
    console.log(`✅ 压力测试客户数据生成完成: ${customers.length} 个`)
    return customers
  }
  
  // 生成大量订单数据
  static generateStressTestOrders(count: number = 50000) {
    console.log(`🔥 生成 ${count} 个订单数据用于压力测试...`)
    
    const orders = []
    const batchSize = 2000
    
    for (let i = 0; i < count; i += batchSize) {
      const batch = []
      const currentBatchSize = Math.min(batchSize, count - i)
      
      for (let j = 0; j < currentBatchSize; j++) {
        const orderId = i + j + 1
        const orderDate = new Date(2020 + Math.floor(Math.random() * 4), 
                                 Math.floor(Math.random() * 12), 
                                 Math.floor(Math.random() * 28) + 1)
        
        batch.push({
          id: orderId,
          orderNumber: `ST${String(orderId).padStart(8, '0')}`,
          customerId: Math.floor(Math.random() * 10000) + 1,
          productId: Math.floor(Math.random() * 10) + 1,
          quantity: Math.floor(Math.random() * 5) + 1,
          unitPrice: Math.floor(Math.random() * 5000) + 1000,
          totalAmount: 0, // 将在后面计算
          orderDate: orderDate.toISOString(),
          status: ['pending', 'processing', 'shipped', 'delivered', 'cancelled'][Math.floor(Math.random() * 5)],
          paymentStatus: ['pending', 'paid', 'failed', 'refunded'][Math.floor(Math.random() * 4)],
          shippingAddress: `压力测试配送地址 ${orderId}`,
          trackingNumber: Math.random() > 0.3 ? `TN${String(orderId).padStart(10, '0')}` : null,
          notes: Math.random() > 0.7 ? `压力测试订单备注 ${orderId}` : null
        })
        
        // 计算总金额
        const order = batch[batch.length - 1]
        order.totalAmount = order.quantity * order.unitPrice
      }
      
      orders.push(...batch)
      
      // 显示进度
      if (i % 10000 === 0) {
        console.log(`  已生成 ${i + currentBatchSize}/${count} 个订单数据`)
      }
    }
    
    console.log(`✅ 压力测试订单数据生成完成: ${orders.length} 个`)
    return orders
  }
  
  // 生成大量设备数据
  static generateStressTestDevices(count: number = 5000) {
    console.log(`🔥 生成 ${count} 个设备数据用于压力测试...`)
    
    const devices = []
    const deviceTypes = ['YX-Pro-2024', 'YX-Standard-2024', 'YX-Mini-2024', 'YX-Education-2024']
    const statuses = ['online', 'offline', 'maintenance', 'error']
    
    for (let i = 0; i < count; i++) {
      const deviceId = i + 1
      const installDate = new Date(2020 + Math.floor(Math.random() * 4), 
                                 Math.floor(Math.random() * 12), 
                                 Math.floor(Math.random() * 28) + 1)
      
      devices.push({
        id: deviceId,
        serialNumber: `YX${String(deviceId).padStart(8, '0')}`,
        model: deviceTypes[Math.floor(Math.random() * deviceTypes.length)],
        customerId: Math.floor(Math.random() * 10000) + 1,
        status: statuses[Math.floor(Math.random() * statuses.length)],
        location: `压力测试位置 ${deviceId}`,
        installDate: installDate.toISOString(),
        lastMaintenanceDate: new Date(installDate.getTime() + Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
        firmwareVersion: `v${Math.floor(Math.random() * 5) + 1}.${Math.floor(Math.random() * 10)}.${Math.floor(Math.random() * 100)}`,
        batteryLevel: Math.floor(Math.random() * 101),
        usageHours: Math.floor(Math.random() * 10000),
        errorCount: Math.floor(Math.random() * 50),
        lastErrorDate: Math.random() > 0.7 ? new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString() : null,
        networkSignal: Math.floor(Math.random() * 101),
        temperature: Math.floor(Math.random() * 40) + 10,
        humidity: Math.floor(Math.random() * 80) + 20
      })
      
      // 显示进度
      if (i % 1000 === 0 && i > 0) {
        console.log(`  已生成 ${i}/${count} 个设备数据`)
      }
    }
    
    console.log(`✅ 压力测试设备数据生成完成: ${devices.length} 个`)
    return devices
  }
}

// 异常情况测试数据生成器
export class AnomalyTestDataGenerator {
  
  // 生成包含异常值的客户数据
  static generateAnomalyCustomers() {
    return [
      // 正常数据
      {
        id: 1,
        name: '正常客户',
        email: 'normal@example.com',
        phone: '13800138000',
        status: 'active'
      },
      // 边界值数据
      {
        id: 2,
        name: '', // 空名称
        email: 'empty-name@example.com',
        phone: '13800138001',
        status: 'active'
      },
      {
        id: 3,
        name: 'A'.repeat(1000), // 超长名称
        email: 'long-name@example.com',
        phone: '13800138002',
        status: 'active'
      },
      // 异常数据
      {
        id: 4,
        name: null, // null值
        email: 'null-name@example.com',
        phone: '13800138003',
        status: 'active'
      },
      {
        id: 5,
        name: undefined, // undefined值
        email: 'undefined-name@example.com',
        phone: '13800138004',
        status: 'active'
      },
      // 特殊字符数据
      {
        id: 6,
        name: '<script>alert("XSS")</script>',
        email: 'xss-test@example.com',
        phone: '13800138005',
        status: 'active'
      },
      {
        id: 7,
        name: '测试客户 & 特殊字符 < > " \' \\',
        email: 'special-chars@example.com',
        phone: '13800138006',
        status: 'active'
      },
      // Unicode字符数据
      {
        id: 8,
        name: '🤖 机器人客户 🎨',
        email: 'emoji-customer@example.com',
        phone: '13800138007',
        status: 'active'
      },
      {
        id: 9,
        name: '한국어 고객', // 韩文
        email: 'korean-customer@example.com',
        phone: '13800138008',
        status: 'active'
      },
      {
        id: 10,
        name: '日本語のお客様', // 日文
        email: 'japanese-customer@example.com',
        phone: '13800138009',
        status: 'active'
      }
    ]
  }
  
  // 生成包含异常值的订单数据
  static generateAnomalyOrders() {
    return [
      // 正常订单
      {
        id: 1,
        orderNumber: 'ORD001',
        customerId: 1,
        totalAmount: 1999.99,
        orderDate: '2024-01-15T10:30:00Z',
        status: 'completed'
      },
      // 零金额订单
      {
        id: 2,
        orderNumber: 'ORD002',
        customerId: 2,
        totalAmount: 0,
        orderDate: '2024-01-16T11:00:00Z',
        status: 'pending'
      },
      // 负金额订单（退款场景）
      {
        id: 3,
        orderNumber: 'ORD003',
        customerId: 3,
        totalAmount: -500.00,
        orderDate: '2024-01-17T12:00:00Z',
        status: 'refunded'
      },
      // 超大金额订单
      {
        id: 4,
        orderNumber: 'ORD004',
        customerId: 4,
        totalAmount: 999999999.99,
        orderDate: '2024-01-18T13:00:00Z',
        status: 'pending'
      },
      // 无效日期订单
      {
        id: 5,
        orderNumber: 'ORD005',
        customerId: 5,
        totalAmount: 1500.00,
        orderDate: 'invalid-date',
        status: 'error'
      },
      // 未来日期订单
      {
        id: 6,
        orderNumber: 'ORD006',
        customerId: 6,
        totalAmount: 2000.00,
        orderDate: '2030-12-31T23:59:59Z',
        status: 'scheduled'
      },
      // 无效客户ID订单
      {
        id: 7,
        orderNumber: 'ORD007',
        customerId: -1,
        totalAmount: 1200.00,
        orderDate: '2024-01-20T15:00:00Z',
        status: 'error'
      },
      // 空订单号
      {
        id: 8,
        orderNumber: '',
        customerId: 8,
        totalAmount: 800.00,
        orderDate: '2024-01-21T16:00:00Z',
        status: 'pending'
      },
      // 重复订单号
      {
        id: 9,
        orderNumber: 'ORD001', // 与第一个订单重复
        customerId: 9,
        totalAmount: 1500.00,
        orderDate: '2024-01-22T17:00:00Z',
        status: 'duplicate'
      },
      // 包含特殊字符的订单号
      {
        id: 10,
        orderNumber: 'ORD<script>alert("XSS")</script>',
        customerId: 10,
        totalAmount: 1000.00,
        orderDate: '2024-01-23T18:00:00Z',
        status: 'pending'
      }
    ]
  }
  
  // 生成包含异常值的设备数据
  static generateAnomalyDevices() {
    return [
      // 正常设备
      {
        id: 1,
        serialNumber: 'YX00000001',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: 85,
        temperature: 25
      },
      // 电池电量异常
      {
        id: 2,
        serialNumber: 'YX00000002',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: -10, // 负值
        temperature: 25
      },
      {
        id: 3,
        serialNumber: 'YX00000003',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: 150, // 超过100%
        temperature: 25
      },
      // 温度异常
      {
        id: 4,
        serialNumber: 'YX00000004',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: 75,
        temperature: -50 // 极低温度
      },
      {
        id: 5,
        serialNumber: 'YX00000005',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: 60,
        temperature: 200 // 极高温度
      },
      // 无效序列号
      {
        id: 6,
        serialNumber: '', // 空序列号
        model: 'YX-Pro-2024',
        status: 'error',
        batteryLevel: 50,
        temperature: 30
      },
      {
        id: 7,
        serialNumber: null, // null序列号
        model: 'YX-Pro-2024',
        status: 'error',
        batteryLevel: 40,
        temperature: 28
      },
      // 无效状态
      {
        id: 8,
        serialNumber: 'YX00000008',
        model: 'YX-Pro-2024',
        status: 'unknown-status',
        batteryLevel: 70,
        temperature: 26
      },
      // 无效型号
      {
        id: 9,
        serialNumber: 'YX00000009',
        model: '', // 空型号
        status: 'online',
        batteryLevel: 80,
        temperature: 24
      },
      // 包含特殊字符的序列号
      {
        id: 10,
        serialNumber: 'YX<script>alert("XSS")</script>',
        model: 'YX-Pro-2024',
        status: 'online',
        batteryLevel: 65,
        temperature: 27
      }
    ]
  }
}

// 边界测试用例生成器
export class BoundaryTestDataGenerator {
  
  // 生成分页边界测试数据
  static generatePaginationBoundaryTests() {
    return [
      // 正常分页
      { page: 1, pageSize: 10, expectedValid: true },
      { page: 2, pageSize: 20, expectedValid: true },
      
      // 边界值
      { page: 0, pageSize: 10, expectedValid: false }, // 页码为0
      { page: -1, pageSize: 10, expectedValid: false }, // 负页码
      { page: 1, pageSize: 0, expectedValid: false }, // 页大小为0
      { page: 1, pageSize: -5, expectedValid: false }, // 负页大小
      
      // 极限值
      { page: 999999, pageSize: 10, expectedValid: true }, // 超大页码
      { page: 1, pageSize: 1000, expectedValid: true }, // 超大页大小
      { page: 1, pageSize: 10000, expectedValid: false }, // 过大页大小
      
      // 非数字值
      { page: 'invalid', pageSize: 10, expectedValid: false },
      { page: 1, pageSize: 'invalid', expectedValid: false },
      { page: null, pageSize: 10, expectedValid: false },
      { page: 1, pageSize: null, expectedValid: false },
      { page: undefined, pageSize: 10, expectedValid: false },
      { page: 1, pageSize: undefined, expectedValid: false }
    ]
  }
  
  // 生成日期范围边界测试数据
  static generateDateRangeBoundaryTests() {
    const now = new Date()
    const yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000)
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000)
    const farPast = new Date('1900-01-01')
    const farFuture = new Date('2100-12-31')
    
    return [
      // 正常日期范围
      { startDate: yesterday.toISOString(), endDate: now.toISOString(), expectedValid: true },
      
      // 边界情况
      { startDate: now.toISOString(), endDate: now.toISOString(), expectedValid: true }, // 同一天
      { startDate: now.toISOString(), endDate: yesterday.toISOString(), expectedValid: false }, // 开始日期晚于结束日期
      
      // 极限日期
      { startDate: farPast.toISOString(), endDate: farFuture.toISOString(), expectedValid: true },
      
      // 无效日期格式
      { startDate: 'invalid-date', endDate: now.toISOString(), expectedValid: false },
      { startDate: now.toISOString(), endDate: 'invalid-date', expectedValid: false },
      { startDate: '2024-13-32', endDate: now.toISOString(), expectedValid: false }, // 无效月日
      { startDate: '2024-02-30', endDate: now.toISOString(), expectedValid: false }, // 2月30日
      
      // null/undefined值
      { startDate: null, endDate: now.toISOString(), expectedValid: false },
      { startDate: now.toISOString(), endDate: null, expectedValid: false },
      { startDate: undefined, endDate: now.toISOString(), expectedValid: false },
      { startDate: now.toISOString(), endDate: undefined, expectedValid: false }
    ]
  }
  
  // 生成搜索关键词边界测试数据
  static generateSearchKeywordBoundaryTests() {
    return [
      // 正常搜索
      { keyword: '练字机器人', expectedValid: true },
      { keyword: 'YX-Pro', expectedValid: true },
      
      // 边界长度
      { keyword: '', expectedValid: true }, // 空搜索（返回所有结果）
      { keyword: 'A', expectedValid: true }, // 单字符
      { keyword: 'A'.repeat(100), expectedValid: true }, // 长搜索词
      { keyword: 'A'.repeat(1000), expectedValid: false }, // 过长搜索词
      
      // 特殊字符
      { keyword: '<script>alert("XSS")</script>', expectedValid: true }, // XSS测试
      { keyword: 'SELECT * FROM users', expectedValid: true }, // SQL注入测试
      { keyword: '../../etc/passwd', expectedValid: true }, // 路径遍历测试
      { keyword: '测试 & 特殊字符 < > " \' \\', expectedValid: true },
      
      // Unicode字符
      { keyword: '🤖 机器人 🎨', expectedValid: true },
      { keyword: '한국어 검색', expectedValid: true },
      { keyword: '日本語検索', expectedValid: true },
      
      // null/undefined值
      { keyword: null, expectedValid: false },
      { keyword: undefined, expectedValid: false },
      
      // 数字和符号
      { keyword: '12345', expectedValid: true },
      { keyword: '!@#$%^&*()', expectedValid: true },
      { keyword: '   ', expectedValid: true }, // 只有空格
      { keyword: '\n\t\r', expectedValid: true } // 换行符和制表符
    ]
  }
}

// 综合测试数据管理器
export class EnhancedTestDataManager {
  
  // 运行所有边界测试
  static async runBoundaryTests() {
    console.log('🧪 开始运行边界测试...')
    
    const results = {
      pagination: [],
      dateRange: [],
      searchKeyword: [],
      anomalyData: []
    }
    
    try {
      // 分页边界测试
      console.log('📄 测试分页边界情况...')
      const paginationTests = BoundaryTestDataGenerator.generatePaginationBoundaryTests()
      for (const test of paginationTests) {
        try {
          // 这里应该调用实际的API进行测试
          const result = await this.testPaginationAPI(test)
          results.pagination.push({ ...test, result })
        } catch (error) {
          results.pagination.push({ ...test, result: 'error', error: error.message })
        }
      }
      
      // 日期范围边界测试
      console.log('📅 测试日期范围边界情况...')
      const dateRangeTests = BoundaryTestDataGenerator.generateDateRangeBoundaryTests()
      for (const test of dateRangeTests) {
        try {
          const result = await this.testDateRangeAPI(test)
          results.dateRange.push({ ...test, result })
        } catch (error) {
          results.dateRange.push({ ...test, result: 'error', error: error.message })
        }
      }
      
      // 搜索关键词边界测试
      console.log('🔍 测试搜索关键词边界情况...')
      const searchTests = BoundaryTestDataGenerator.generateSearchKeywordBoundaryTests()
      for (const test of searchTests) {
        try {
          const result = await this.testSearchAPI(test)
          results.searchKeyword.push({ ...test, result })
        } catch (error) {
          results.searchKeyword.push({ ...test, result: 'error', error: error.message })
        }
      }
      
      // 异常数据测试
      console.log('⚠️ 测试异常数据处理...')
      const anomalyCustomers = AnomalyTestDataGenerator.generateAnomalyCustomers()
      const anomalyOrders = AnomalyTestDataGenerator.generateAnomalyOrders()
      const anomalyDevices = AnomalyTestDataGenerator.generateAnomalyDevices()
      
      results.anomalyData = {
        customers: anomalyCustomers.length,
        orders: anomalyOrders.length,
        devices: anomalyDevices.length
      }
      
      console.log('✅ 边界测试完成')
      return results
      
    } catch (error) {
      console.error('❌ 边界测试失败:', error)
      throw error
    }
  }
  
  // 运行性能压力测试
  static async runStressTests() {
    console.log('🔥 开始运行性能压力测试...')
    
    const startTime = Date.now()
    
    try {
      // 生成大量测试数据
      const stressCustomers = PerformanceTestDataGenerator.generateStressTestCustomers(1000)
      const stressOrders = PerformanceTestDataGenerator.generateStressTestOrders(5000)
      const stressDevices = PerformanceTestDataGenerator.generateStressTestDevices(500)
      
      const dataGenerationTime = Date.now() - startTime
      
      // 测试API性能
      const apiTestStart = Date.now()
      
      // 模拟API调用测试
      const apiResults = await Promise.all([
        this.testAPIPerformance('customers', stressCustomers),
        this.testAPIPerformance('orders', stressOrders),
        this.testAPIPerformance('devices', stressDevices)
      ])
      
      const apiTestTime = Date.now() - apiTestStart
      const totalTime = Date.now() - startTime
      
      const results = {
        dataGeneration: {
          customers: stressCustomers.length,
          orders: stressOrders.length,
          devices: stressDevices.length,
          time: dataGenerationTime
        },
        apiPerformance: {
          results: apiResults,
          time: apiTestTime
        },
        totalTime,
        memoryUsage: Math.round(process.memoryUsage().heapUsed / 1024 / 1024)
      }
      
      console.log('✅ 性能压力测试完成')
      console.log(`📊 测试结果:`)
      console.log(`  - 数据生成时间: ${dataGenerationTime}ms`)
      console.log(`  - API测试时间: ${apiTestTime}ms`)
      console.log(`  - 总耗时: ${totalTime}ms`)
      console.log(`  - 内存使用: ${results.memoryUsage}MB`)
      
      return results
      
    } catch (error) {
      console.error('❌ 性能压力测试失败:', error)
      throw error
    }
  }
  
  // 模拟分页API测试
  private static async testPaginationAPI(test: any) {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 10))
    
    const { page, pageSize, expectedValid } = test
    
    // 验证分页参数
    if (typeof page !== 'number' || typeof pageSize !== 'number') {
      return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
    }
    
    if (page <= 0 || pageSize <= 0 || pageSize > 1000) {
      return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
    }
    
    return expectedValid ? 'expected_valid' : 'unexpected_valid'
  }
  
  // 模拟日期范围API测试
  private static async testDateRangeAPI(test: any) {
    await new Promise(resolve => setTimeout(resolve, 10))
    
    const { startDate, endDate, expectedValid } = test
    
    try {
      const start = new Date(startDate)
      const end = new Date(endDate)
      
      if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
      }
      
      if (start > end) {
        return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
      }
      
      return expectedValid ? 'expected_valid' : 'unexpected_valid'
      
    } catch (error) {
      return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
    }
  }
  
  // 模拟搜索API测试
  private static async testSearchAPI(test: any) {
    await new Promise(resolve => setTimeout(resolve, 10))
    
    const { keyword, expectedValid } = test
    
    if (keyword === null || keyword === undefined) {
      return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
    }
    
    if (typeof keyword === 'string' && keyword.length > 500) {
      return expectedValid ? 'unexpected_invalid' : 'expected_invalid'
    }
    
    return expectedValid ? 'expected_valid' : 'unexpected_valid'
  }
  
  // 模拟API性能测试
  private static async testAPIPerformance(type: string, data: any[]) {
    const startTime = Date.now()
    
    // 模拟批量处理
    const batchSize = 100
    const batches = Math.ceil(data.length / batchSize)
    
    for (let i = 0; i < batches; i++) {
      const batch = data.slice(i * batchSize, (i + 1) * batchSize)
      // 模拟API处理时间
      await new Promise(resolve => setTimeout(resolve, Math.random() * 50))
    }
    
    const endTime = Date.now()
    
    return {
      type,
      dataCount: data.length,
      batches,
      processingTime: endTime - startTime,
      averageTimePerRecord: (endTime - startTime) / data.length
    }
  }
}

// 导出测试套件
export const enhancedTestSuite = {
  // 性能测试
  performance: {
    generateStressCustomers: PerformanceTestDataGenerator.generateStressTestCustomers,
    generateStressOrders: PerformanceTestDataGenerator.generateStressTestOrders,
    generateStressDevices: PerformanceTestDataGenerator.generateStressTestDevices,
    runStressTests: EnhancedTestDataManager.runStressTests
  },
  
  // 异常测试
  anomaly: {
    generateAnomalyCustomers: AnomalyTestDataGenerator.generateAnomalyCustomers,
    generateAnomalyOrders: AnomalyTestDataGenerator.generateAnomalyOrders,
    generateAnomalyDevices: AnomalyTestDataGenerator.generateAnomalyDevices
  },
  
  // 边界测试
  boundary: {
    generatePaginationTests: BoundaryTestDataGenerator.generatePaginationBoundaryTests,
    generateDateRangeTests: BoundaryTestDataGenerator.generateDateRangeBoundaryTests,
    generateSearchKeywordTests: BoundaryTestDataGenerator.generateSearchKeywordBoundaryTests,
    runBoundaryTests: EnhancedTestDataManager.runBoundaryTests
  },
  
  // 综合测试
  comprehensive: {
    runAllTests: async () => {
      console.log('🚀 开始运行综合测试套件...')
      
      const results = {
        basicTests: await mockTestSuite.testAllAPIs(),
        boundaryTests: await EnhancedTestDataManager.runBoundaryTests(),
        stressTests: await EnhancedTestDataManager.runStressTests()
      }
      
      console.log('🎉 综合测试套件完成!')
      return results
    }
  }
}

// 如果直接运行此文件，执行综合测试
if (require.main === module) {
  enhancedTestSuite.comprehensive.runAllTests().then(results => {
    console.log('\n📋 综合测试结果:', JSON.stringify(results, null, 2))
    process.exit(0)
  }).catch(error => {
    console.error('❌ 综合测试失败:', error)
    process.exit(1)
  })
}