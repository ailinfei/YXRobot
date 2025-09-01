import { describe, it, expect } from 'vitest'
import ApiResponseValidator from './apiResponseValidator'

describe('ApiResponseValidator', () => {
  describe('validateCustomerStats', () => {
    it('should validate correct customer stats data', () => {
      const validStats = {
        total: 156,
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      const result = ApiResponseValidator.validateCustomerStats(validStats)
      
      expect(result.isValid).toBe(true)
      expect(result.errors).toHaveLength(0)
      expect(result.fieldMappings).toHaveLength(7)
      
      // 验证所有字段映射都匹配
      result.fieldMappings.forEach(mapping => {
        expect(mapping.isMatch).toBe(true)
      })
    })

    it('should detect missing required fields', () => {
      const invalidStats = {
        total: 156,
        regular: 120
        // 缺少其他必需字段
      }

      const result = ApiResponseValidator.validateCustomerStats(invalidStats)
      
      expect(result.isValid).toBe(false)
      expect(result.errors.length).toBeGreaterThan(0)
      expect(result.errors.some(error => error.includes('缺少必需字段'))).toBe(true)
    })

    it('should detect incorrect field types', () => {
      const invalidStats = {
        total: '156', // 应该是number
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      const result = ApiResponseValidator.validateCustomerStats(invalidStats)
      
      expect(result.isValid).toBe(false)
      expect(result.errors.some(error => error.includes('字段类型不匹配'))).toBe(true)
    })
  })

  describe('validateCustomer', () => {
    it('should validate correct customer data', () => {
      const validCustomer = {
        id: '1',
        name: '张三',
        level: 'vip',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        totalSpent: 5000,
        customerValue: 8.5,
        registeredAt: '2024-01-01T00:00:00Z'
      }

      const result = ApiResponseValidator.validateCustomer(validCustomer)
      
      expect(result.isValid).toBe(true)
      expect(result.errors).toHaveLength(0)
    })

    it('should detect invalid enum values', () => {
      const invalidCustomer = {
        id: '1',
        name: '张三',
        level: 'invalid_level', // 无效的等级
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        totalSpent: 5000,
        customerValue: 8.5,
        registeredAt: '2024-01-01T00:00:00Z'
      }

      const result = ApiResponseValidator.validateCustomer(invalidCustomer)
      
      expect(result.isValid).toBe(false)
      expect(result.errors.some(error => error.includes('无效的客户等级'))).toBe(true)
    })
  })

  describe('validateApiResponse', () => {
    it('should validate correct API response structure', () => {
      const validResponse = {
        code: 200,
        message: '查询成功',
        data: { test: 'data' }
      }

      const result = ApiResponseValidator.validateApiResponse(validResponse)
      
      expect(result.isValid).toBe(true)
      expect(result.errors).toHaveLength(0)
    })

    it('should detect missing required response fields', () => {
      const invalidResponse = {
        code: 200
        // 缺少message和data字段
      }

      const result = ApiResponseValidator.validateApiResponse(invalidResponse)
      
      expect(result.isValid).toBe(false)
      expect(result.errors.length).toBeGreaterThan(0)
    })
  })

  describe('generateValidationReport', () => {
    it('should generate a validation report', () => {
      const validationResults = [
        {
          isValid: true,
          errors: [],
          warnings: [],
          fieldMappings: [
            { field: 'test', expected: 'string', actual: 'string', isMatch: true }
          ]
        },
        {
          isValid: false,
          errors: ['测试错误'],
          warnings: ['测试警告'],
          fieldMappings: []
        }
      ]

      const report = ApiResponseValidator.generateValidationReport(validationResults)
      
      expect(report).toContain('API响应验证报告')
      expect(report).toContain('验证总结')
      expect(report).toContain('成功率')
      expect(report).toContain('测试错误')
      expect(report).toContain('测试警告')
    })
  })
})