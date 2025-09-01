import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import axios from 'axios'
import type { Customer, CustomerStats, CreateCustomerData, UpdateCustomerData } from '@/types/customer'

// 集成测试配置
const API_BASE_URL = 'http://localhost:8081'
const TEST_TIMEOUT = 10000

// 创建axios实例用于测试
const testApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: TEST_TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 测试数据
let testCustomerId: string | null = null
const testCustomerData: CreateCustomerData = {
  name: '测试客户_集成测试',
  level: 'regular',
  phone: '13800138999',
  email: 'test.integration@example.com',
  company: '测试公司',
  status: 'active',
  address: {
    province: '北京市',
    city: '北京市',
    detail: '朝阳区测试街道999号'
  },
  tags: ['集成测试', '自动化测试'],
  notes: '这是集成测试创建的客户数据'
}

describe('Customer API Integration Tests - 前后端对接验证', () => {
  beforeAll(async () => {
    console.log('开始客户API集成测试...')
    console.log(`API Base URL: ${API_BASE_URL}`)
  })

  afterAll(async () => {
    // 清理测试数据
    if (testCustomerId) {
      try {
        await testApi.delete(`/api/admin/customers/${testCustomerId}`)
        console.log(`清理测试客户数据: ${testCustomerId}`)
      } catch (error) {
        console.warn('清理测试数据失败:', error)
      }
    }
  })

  describe('1. API接口完整性验证', () => {
    it('应该能够访问客户统计API', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const stats = response.data.data as CustomerStats
        expect(stats).toHaveProperty('total')
        expect(stats).toHaveProperty('regular')
        expect(stats).toHaveProperty('vip')
        expect(stats).toHaveProperty('premium')
        expect(stats).toHaveProperty('activeDevices')
        expect(stats).toHaveProperty('totalRevenue')
        
        console.log('✅ 客户统计API测试通过')
      } catch (error) {
        console.error('❌ 客户统计API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够访问客户列表API', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: {
            page: 1,
            pageSize: 10
          }
        })
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const data = response.data.data
        expect(data).toHaveProperty('list')
        expect(data).toHaveProperty('total')
        expect(data).toHaveProperty('page')
        expect(data).toHaveProperty('pageSize')
        expect(Array.isArray(data.list)).toBe(true)
        
        console.log('✅ 客户列表API测试通过')
      } catch (error) {
        console.error('❌ 客户列表API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够访问筛选选项API', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/filter-options')
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const options = response.data.data
        expect(options).toHaveProperty('levels')
        expect(options).toHaveProperty('regions')
        expect(options).toHaveProperty('industries')
        
        console.log('✅ 筛选选项API测试通过')
      } catch (error) {
        console.error('❌ 筛选选项API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('2. 数据格式匹配验证', () => {
    it('客户统计数据格式应该与前端接口匹配', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        const stats = response.data.data as CustomerStats
        
        // 验证数据类型
        expect(typeof stats.total).toBe('number')
        expect(typeof stats.regular).toBe('number')
        expect(typeof stats.vip).toBe('number')
        expect(typeof stats.premium).toBe('number')
        expect(typeof stats.activeDevices).toBe('number')
        expect(typeof stats.totalRevenue).toBe('number')
        
        // 验证数据合理性
        expect(stats.total).toBeGreaterThanOrEqual(0)
        expect(stats.regular + stats.vip + stats.premium).toBeLessThanOrEqual(stats.total)
        expect(stats.totalRevenue).toBeGreaterThanOrEqual(0)
        
        console.log('✅ 客户统计数据格式验证通过')
        console.log(`统计数据: 总客户${stats.total}, VIP${stats.vip}, 总收入${stats.totalRevenue}`)
      } catch (error) {
        console.error('❌ 客户统计数据格式验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户列表数据格式应该与前端接口匹配', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 5 }
        })
        
        const data = response.data.data
        const customers = data.list as Customer[]
        
        if (customers.length > 0) {
          const customer = customers[0]
          
          // 验证必需字段
          expect(customer).toHaveProperty('id')
          expect(customer).toHaveProperty('name')
          expect(customer).toHaveProperty('level')
          expect(customer).toHaveProperty('phone')
          expect(customer).toHaveProperty('email')
          expect(customer).toHaveProperty('status')
          expect(customer).toHaveProperty('totalSpent')
          expect(customer).toHaveProperty('customerValue')
          expect(customer).toHaveProperty('registeredAt')
          
          // 验证数据类型
          expect(typeof customer.id).toBe('string')
          expect(typeof customer.name).toBe('string')
          expect(typeof customer.totalSpent).toBe('number')
          expect(typeof customer.customerValue).toBe('number')
          
          // 验证枚举值
          expect(['regular', 'vip', 'premium']).toContain(customer.level)
          expect(['active', 'inactive', 'suspended']).toContain(customer.status)
          
          console.log('✅ 客户列表数据格式验证通过')
        } else {
          console.log('⚠️ 客户列表为空，跳过数据格式验证')
        }
      } catch (error) {
        console.error('❌ 客户列表数据格式验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('3. 客户CRUD操作验证', () => {
    it('应该能够创建客户', async () => {
      try {
        const response = await testApi.post('/api/admin/customers', testCustomerData)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const createdCustomer = response.data.data as Customer
        testCustomerId = createdCustomer.id
        
        expect(createdCustomer.name).toBe(testCustomerData.name)
        expect(createdCustomer.phone).toBe(testCustomerData.phone)
        expect(createdCustomer.email).toBe(testCustomerData.email)
        expect(createdCustomer.level).toBe(testCustomerData.level)
        expect(createdCustomer.status).toBe(testCustomerData.status)
        
        console.log('✅ 客户创建测试通过')
        console.log(`创建的客户ID: ${testCustomerId}`)
      } catch (error) {
        console.error('❌ 客户创建测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够获取客户详情', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}`)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const customer = response.data.data as Customer
        expect(customer.id).toBe(testCustomerId)
        expect(customer.name).toBe(testCustomerData.name)
        
        console.log('✅ 客户详情查询测试通过')
      } catch (error) {
        console.error('❌ 客户详情查询测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够更新客户', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      try {
        const updateData: UpdateCustomerData = {
          name: '测试客户_已更新',
          level: 'vip',
          notes: '客户已升级为VIP'
        }

        const response = await testApi.put(`/api/admin/customers/${testCustomerId}`, updateData)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        
        const updatedCustomer = response.data.data as Customer
        expect(updatedCustomer.name).toBe(updateData.name)
        expect(updatedCustomer.level).toBe(updateData.level)
        expect(updatedCustomer.notes).toBe(updateData.notes)
        
        console.log('✅ 客户更新测试通过')
      } catch (error) {
        console.error('❌ 客户更新测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('4. 搜索和筛选功能验证', () => {
    it('应该支持关键词搜索', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: {
            keyword: '测试',
            page: 1,
            pageSize: 10
          }
        })
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        
        const data = response.data.data
        expect(data).toHaveProperty('list')
        expect(Array.isArray(data.list)).toBe(true)
        
        console.log('✅ 关键词搜索测试通过')
        console.log(`搜索结果数量: ${data.list.length}`)
      } catch (error) {
        console.error('❌ 关键词搜索测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该支持等级筛选', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: {
            level: 'vip',
            page: 1,
            pageSize: 10
          }
        })
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        
        const data = response.data.data
        const customers = data.list as Customer[]
        
        // 验证筛选结果
        customers.forEach(customer => {
          expect(customer.level).toBe('vip')
        })
        
        console.log('✅ 等级筛选测试通过')
        console.log(`VIP客户数量: ${customers.length}`)
      } catch (error) {
        console.error('❌ 等级筛选测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该支持状态筛选', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: {
            status: 'active',
            page: 1,
            pageSize: 10
          }
        })
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        
        const data = response.data.data
        const customers = data.list as Customer[]
        
        // 验证筛选结果
        customers.forEach(customer => {
          expect(customer.status).toBe('active')
        })
        
        console.log('✅ 状态筛选测试通过')
        console.log(`活跃客户数量: ${customers.length}`)
      } catch (error) {
        console.error('❌ 状态筛选测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('5. 客户关联数据验证', () => {
    it('应该能够获取客户设备列表', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/devices`)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        expect(Array.isArray(response.data.data)).toBe(true)
        
        console.log('✅ 客户设备列表API测试通过')
        console.log(`设备数量: ${response.data.data.length}`)
      } catch (error) {
        console.error('❌ 客户设备列表API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够获取客户订单列表', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/orders`)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        expect(Array.isArray(response.data.data)).toBe(true)
        
        console.log('✅ 客户订单列表API测试通过')
        console.log(`订单数量: ${response.data.data.length}`)
      } catch (error) {
        console.error('❌ 客户订单列表API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该能够获取客户服务记录', async () => {
      if (!testCustomerId) {
        throw new Error('测试客户ID不存在，请先运行创建客户测试')
      }

      try {
        const response = await testApi.get(`/api/admin/customers/${testCustomerId}/service-records`)
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 200)
        expect(response.data).toHaveProperty('data')
        expect(Array.isArray(response.data.data)).toBe(true)
        
        console.log('✅ 客户服务记录API测试通过')
        console.log(`服务记录数量: ${response.data.data.length}`)
      } catch (error) {
        console.error('❌ 客户服务记录API测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('6. 错误处理验证', () => {
    it('应该正确处理不存在的客户ID', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/999999')
        
        expect(response.status).toBe(200)
        expect(response.data).toHaveProperty('code', 404)
        expect(response.data).toHaveProperty('message')
        
        console.log('✅ 不存在客户ID错误处理测试通过')
      } catch (error) {
        // 如果是404错误，也是正确的处理方式
        if (error.response && error.response.status === 404) {
          console.log('✅ 不存在客户ID错误处理测试通过（HTTP 404）')
        } else {
          console.error('❌ 不存在客户ID错误处理测试失败:', error)
          throw error
        }
      }
    }, TEST_TIMEOUT)

    it('应该正确处理无效的查询参数', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: {
            page: -1,
            pageSize: 0
          }
        })
        
        // 应该返回错误或者使用默认值
        expect(response.status).toBe(200)
        
        if (response.data.code !== 200) {
          expect(response.data).toHaveProperty('message')
          console.log('✅ 无效查询参数错误处理测试通过（返回错误）')
        } else {
          // 如果使用默认值，验证数据结构
          expect(response.data.data).toHaveProperty('list')
          console.log('✅ 无效查询参数错误处理测试通过（使用默认值）')
        }
      } catch (error) {
        console.error('❌ 无效查询参数错误处理测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('应该正确处理创建客户时的数据验证错误', async () => {
      try {
        const invalidData = {
          name: '', // 空名称
          level: 'invalid_level', // 无效等级
          phone: '123', // 无效电话
          email: 'invalid_email', // 无效邮箱
          status: 'active'
        }

        const response = await testApi.post('/api/admin/customers', invalidData)
        
        expect(response.status).toBe(200)
        expect(response.data.code).not.toBe(200) // 应该返回错误码
        expect(response.data).toHaveProperty('message')
        
        console.log('✅ 数据验证错误处理测试通过')
      } catch (error) {
        // 如果是400错误，也是正确的处理方式
        if (error.response && error.response.status === 400) {
          console.log('✅ 数据验证错误处理测试通过（HTTP 400）')
        } else {
          console.error('❌ 数据验证错误处理测试失败:', error)
          throw error
        }
      }
    }, TEST_TIMEOUT)
  })

  describe('7. 性能要求验证', () => {
    it('客户列表API响应时间应该小于2秒', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 20 }
        })
        
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(responseTime).toBeLessThan(2000)
        
        console.log(`✅ 客户列表API性能测试通过，响应时间: ${responseTime}ms`)
      } catch (error) {
        console.error('❌ 客户列表API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户统计API响应时间应该小于1秒', async () => {
      const startTime = Date.now()
      
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        
        const responseTime = Date.now() - startTime
        
        expect(response.status).toBe(200)
        expect(responseTime).toBeLessThan(1000)
        
        console.log(`✅ 客户统计API性能测试通过，响应时间: ${responseTime}ms`)
      } catch (error) {
        console.error('❌ 客户统计API性能测试失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })
})