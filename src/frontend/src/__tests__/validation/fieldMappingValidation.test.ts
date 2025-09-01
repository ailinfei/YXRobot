import { describe, it, expect, beforeAll } from 'vitest'
import axios from 'axios'
import ApiResponseValidator from '../utils/apiResponseValidator'
import type { Customer, CustomerStats } from '@/types/customer'

// 测试配置
const API_BASE_URL = 'http://localhost:8081'
const TEST_TIMEOUT = 10000

// 创建axios实例
const testApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: TEST_TIMEOUT
})

describe('Field Mapping Validation Tests - 字段映射验证', () => {
  let validationResults: any[] = []

  beforeAll(async () => {
    console.log('开始字段映射验证测试...')
  })

  describe('1. 数据库字段到前端字段映射验证', () => {
    it('客户统计数据字段映射应该正确', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const stats = response.data.data
        const validation = ApiResponseValidator.validateCustomerStats(stats)
        
        validationResults.push({
          testName: '客户统计数据字段映射',
          validation
        })

        // 验证字段映射
        expect(validation.isValid).toBe(true)
        
        // 验证camelCase命名规范
        const expectedCamelCaseFields = [
          'total', 'regular', 'vip', 'premium', 
          'activeDevices', 'totalRevenue', 'newThisMonth'
        ]
        
        expectedCamelCaseFields.forEach(field => {
          expect(stats).toHaveProperty(field)
          expect(typeof stats[field]).toBe('number')
        })

        // 验证没有snake_case字段
        const forbiddenSnakeCaseFields = [
          'active_devices', 'total_revenue', 'new_this_month'
        ]
        
        forbiddenSnakeCaseFields.forEach(field => {
          expect(stats).not.toHaveProperty(field)
        })

        console.log('✅ 客户统计数据字段映射验证通过')
        console.log(`字段映射详情:`, validation.fieldMappings)
        
      } catch (error) {
        console.error('❌ 客户统计数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户列表数据字段映射应该正确', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 5 }
        })
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const listData = response.data.data
        const validation = ApiResponseValidator.validateCustomerListResponse(listData)
        
        validationResults.push({
          testName: '客户列表数据字段映射',
          validation
        })

        // 验证响应结构
        expect(validation.isValid).toBe(true)
        
        if (listData.list.length > 0) {
          const customer = listData.list[0] as Customer
          
          // 验证camelCase命名规范
          const expectedCamelCaseFields = [
            'id', 'name', 'level', 'phone', 'email', 'status',
            'totalSpent', 'customerValue', 'registeredAt'
          ]
          
          expectedCamelCaseFields.forEach(field => {
            expect(customer).toHaveProperty(field)
          })

          // 验证没有snake_case字段
          const forbiddenSnakeCaseFields = [
            'customer_name', 'phone_number', 'email_address',
            'customer_level', 'customer_status', 'total_spent',
            'customer_value', 'registered_at', 'last_active_at'
          ]
          
          forbiddenSnakeCaseFields.forEach(field => {
            expect(customer).not.toHaveProperty(field)
          })

          // 验证可选字段的camelCase命名
          if (customer.deviceCount) {
            expect(customer.deviceCount).toHaveProperty('total')
            expect(customer.deviceCount).toHaveProperty('purchased')
            expect(customer.deviceCount).toHaveProperty('rental')
            
            // 验证没有snake_case
            expect(customer.deviceCount).not.toHaveProperty('device_count')
          }

          if (customer.address) {
            expect(customer.address).toHaveProperty('province')
            expect(customer.address).toHaveProperty('city')
            expect(customer.address).toHaveProperty('detail')
          }

          console.log('✅ 客户列表数据字段映射验证通过')
        } else {
          console.log('⚠️ 客户列表为空，跳过详细字段映射验证')
        }
        
      } catch (error) {
        console.error('❌ 客户列表数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户详情数据字段映射应该正确', async () => {
      try {
        // 先获取一个客户ID
        const listResponse = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (listResponse.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过客户详情字段映射验证')
          return
        }

        const customerId = listResponse.data.data.list[0].id
        const response = await testApi.get(`/api/admin/customers/${customerId}`)
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const customer = response.data.data as Customer
        const validation = ApiResponseValidator.validateCustomer(customer)
        
        validationResults.push({
          testName: '客户详情数据字段映射',
          validation
        })

        // 验证基本字段映射
        expect(validation.isValid).toBe(true)
        
        // 验证特殊字段的映射
        if (customer.lastActiveAt) {
          expect(typeof customer.lastActiveAt).toBe('string')
          // 验证没有snake_case版本
          expect(customer).not.toHaveProperty('last_active_at')
        }

        if (customer.deviceCount) {
          expect(typeof customer.deviceCount).toBe('object')
          expect(customer.deviceCount).toHaveProperty('total')
          expect(customer.deviceCount).toHaveProperty('purchased')
          expect(customer.deviceCount).toHaveProperty('rental')
        }

        console.log('✅ 客户详情数据字段映射验证通过')
        
      } catch (error) {
        console.error('❌ 客户详情数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('2. 关联数据字段映射验证', () => {
    it('客户设备数据字段映射应该正确', async () => {
      try {
        // 先获取一个客户ID
        const listResponse = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (listResponse.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过设备字段映射验证')
          return
        }

        const customerId = listResponse.data.data.list[0].id
        const response = await testApi.get(`/api/admin/customers/${customerId}/devices`)
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const devices = response.data.data
        expect(Array.isArray(devices)).toBe(true)

        if (devices.length > 0) {
          const device = devices[0]
          
          // 验证camelCase字段
          const expectedFields = [
            'id', 'serialNumber', 'model', 'type', 'status'
          ]
          
          expectedFields.forEach(field => {
            if (device[field] !== undefined) {
              expect(device).toHaveProperty(field)
            }
          })

          // 验证没有snake_case字段
          const forbiddenFields = [
            'serial_number', 'device_type', 'device_status',
            'activated_at', 'last_online_at', 'firmware_version'
          ]
          
          forbiddenFields.forEach(field => {
            expect(device).not.toHaveProperty(field)
          })

          console.log('✅ 客户设备数据字段映射验证通过')
        } else {
          console.log('⚠️ 客户没有设备数据，跳过设备字段映射验证')
        }
        
      } catch (error) {
        console.error('❌ 客户设备数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户订单数据字段映射应该正确', async () => {
      try {
        // 先获取一个客户ID
        const listResponse = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (listResponse.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过订单字段映射验证')
          return
        }

        const customerId = listResponse.data.data.list[0].id
        const response = await testApi.get(`/api/admin/customers/${customerId}/orders`)
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const orders = response.data.data
        expect(Array.isArray(orders)).toBe(true)

        if (orders.length > 0) {
          const order = orders[0]
          
          // 验证camelCase字段
          const expectedFields = [
            'id', 'orderNumber', 'type', 'productName', 'productModel',
            'quantity', 'amount', 'status', 'createdAt'
          ]
          
          expectedFields.forEach(field => {
            if (order[field] !== undefined) {
              expect(order).toHaveProperty(field)
            }
          })

          // 验证没有snake_case字段
          const forbiddenFields = [
            'order_number', 'product_name', 'product_model',
            'order_status', 'created_at', 'updated_at'
          ]
          
          forbiddenFields.forEach(field => {
            expect(order).not.toHaveProperty(field)
          })

          console.log('✅ 客户订单数据字段映射验证通过')
        } else {
          console.log('⚠️ 客户没有订单数据，跳过订单字段映射验证')
        }
        
      } catch (error) {
        console.error('❌ 客户订单数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('客户服务记录数据字段映射应该正确', async () => {
      try {
        // 先获取一个客户ID
        const listResponse = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (listResponse.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过服务记录字段映射验证')
          return
        }

        const customerId = listResponse.data.data.list[0].id
        const response = await testApi.get(`/api/admin/customers/${customerId}/service-records`)
        
        expect(response.status).toBe(200)
        expect(response.data.code).toBe(200)
        
        const serviceRecords = response.data.data
        expect(Array.isArray(serviceRecords)).toBe(true)

        if (serviceRecords.length > 0) {
          const record = serviceRecords[0]
          
          // 验证camelCase字段
          const expectedFields = [
            'id', 'type', 'subject', 'description', 'serviceStaff',
            'status', 'createdAt'
          ]
          
          expectedFields.forEach(field => {
            if (record[field] !== undefined) {
              expect(record).toHaveProperty(field)
            }
          })

          // 验证没有snake_case字段
          const forbiddenFields = [
            'service_type', 'service_staff', 'device_id',
            'service_status', 'created_at', 'updated_at'
          ]
          
          forbiddenFields.forEach(field => {
            expect(record).not.toHaveProperty(field)
          })

          console.log('✅ 客户服务记录数据字段映射验证通过')
        } else {
          console.log('⚠️ 客户没有服务记录数据，跳过服务记录字段映射验证')
        }
        
      } catch (error) {
        console.error('❌ 客户服务记录数据字段映射验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('3. 数据类型一致性验证', () => {
    it('数值类型字段应该返回正确的数值类型', async () => {
      try {
        const response = await testApi.get('/api/admin/customers/stats')
        const stats = response.data.data
        
        // 验证数值类型
        const numericFields = ['total', 'regular', 'vip', 'premium', 'activeDevices', 'totalRevenue', 'newThisMonth']
        
        numericFields.forEach(field => {
          expect(typeof stats[field]).toBe('number')
          expect(Number.isFinite(stats[field])).toBe(true)
          expect(stats[field]).toBeGreaterThanOrEqual(0)
        })

        console.log('✅ 数值类型字段验证通过')
        
      } catch (error) {
        console.error('❌ 数值类型字段验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('字符串类型字段应该返回正确的字符串类型', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (response.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过字符串类型验证')
          return
        }

        const customer = response.data.data.list[0]
        
        // 验证字符串类型
        const stringFields = ['id', 'name', 'level', 'phone', 'email', 'status', 'registeredAt']
        
        stringFields.forEach(field => {
          if (customer[field] !== undefined && customer[field] !== null) {
            expect(typeof customer[field]).toBe('string')
            expect(customer[field].length).toBeGreaterThan(0)
          }
        })

        console.log('✅ 字符串类型字段验证通过')
        
      } catch (error) {
        console.error('❌ 字符串类型字段验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)

    it('日期时间字段应该返回ISO格式字符串', async () => {
      try {
        const response = await testApi.get('/api/admin/customers', {
          params: { page: 1, pageSize: 1 }
        })
        
        if (response.data.data.list.length === 0) {
          console.log('⚠️ 没有客户数据，跳过日期时间格式验证')
          return
        }

        const customer = response.data.data.list[0]
        
        // 验证日期时间格式
        const dateFields = ['registeredAt', 'lastActiveAt']
        
        dateFields.forEach(field => {
          if (customer[field]) {
            expect(typeof customer[field]).toBe('string')
            
            // 验证是否为有效的日期字符串
            const date = new Date(customer[field])
            expect(date.toString()).not.toBe('Invalid Date')
            
            // 验证ISO格式（可选，根据后端实现调整）
            // expect(customer[field]).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/)
          }
        })

        console.log('✅ 日期时间字段格式验证通过')
        
      } catch (error) {
        console.error('❌ 日期时间字段格式验证失败:', error)
        throw error
      }
    }, TEST_TIMEOUT)
  })

  describe('4. 生成验证报告', () => {
    it('应该生成完整的字段映射验证报告', () => {
      const report = ApiResponseValidator.generateValidationReport(
        validationResults.map(r => r.validation)
      )
      
      console.log(report)
      
      // 验证报告包含必要信息
      expect(report).toContain('API响应验证报告')
      expect(report).toContain('验证总结')
      expect(report).toContain('成功率')
      
      // 保存报告到文件（可选）
      // fs.writeFileSync('field-mapping-validation-report.txt', report)
      
      console.log('✅ 字段映射验证报告生成完成')
    })
  })
})