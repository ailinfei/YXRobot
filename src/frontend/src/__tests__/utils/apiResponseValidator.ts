/**
 * API响应验证工具
 * 用于验证后端API响应格式与前端TypeScript接口的匹配性
 */

import type { Customer, CustomerStats, CreateCustomerData, UpdateCustomerData } from '@/types/customer'

// 验证结果接口
export interface ValidationResult {
  isValid: boolean
  errors: string[]
  warnings: string[]
  fieldMappings: FieldMapping[]
}

export interface FieldMapping {
  field: string
  expected: string
  actual: string
  isMatch: boolean
}

// 字段类型验证器
export class ApiResponseValidator {
  
  /**
   * 验证客户统计数据格式
   */
  static validateCustomerStats(data: any): ValidationResult {
    const result: ValidationResult = {
      isValid: true,
      errors: [],
      warnings: [],
      fieldMappings: []
    }

    const expectedFields = {
      total: 'number',
      regular: 'number',
      vip: 'number',
      premium: 'number',
      activeDevices: 'number',
      totalRevenue: 'number',
      newThisMonth: 'number'
    }

    // 验证必需字段
    for (const [field, expectedType] of Object.entries(expectedFields)) {
      const actualType = typeof data[field]
      const isMatch = actualType === expectedType

      result.fieldMappings.push({
        field,
        expected: expectedType,
        actual: actualType,
        isMatch
      })

      if (!isMatch) {
        if (data[field] === undefined) {
          result.errors.push(`缺少必需字段: ${field}`)
        } else {
          result.errors.push(`字段类型不匹配: ${field} 期望 ${expectedType}, 实际 ${actualType}`)
        }
        result.isValid = false
      }
    }

    // 验证数据合理性
    if (typeof data.total === 'number' && data.total < 0) {
      result.warnings.push('总客户数不应为负数')
    }

    if (typeof data.totalRevenue === 'number' && data.totalRevenue < 0) {
      result.warnings.push('总收入不应为负数')
    }

    if (typeof data.regular === 'number' && typeof data.vip === 'number' && 
        typeof data.premium === 'number' && typeof data.total === 'number') {
      const sum = data.regular + data.vip + data.premium
      if (sum > data.total) {
        result.warnings.push(`等级客户数量总和(${sum})超过总客户数(${data.total})`)
      }
    }

    return result
  }

  /**
   * 验证客户数据格式
   */
  static validateCustomer(data: any): ValidationResult {
    const result: ValidationResult = {
      isValid: true,
      errors: [],
      warnings: [],
      fieldMappings: []
    }

    const requiredFields = {
      id: 'string',
      name: 'string',
      level: 'string',
      phone: 'string',
      email: 'string',
      status: 'string',
      totalSpent: 'number',
      customerValue: 'number',
      registeredAt: 'string'
    }

    const optionalFields = {
      company: 'string',
      address: 'object',
      tags: 'object', // array
      notes: 'string',
      avatar: 'string',
      deviceCount: 'object',
      lastActiveAt: 'string'
    }

    // 验证必需字段
    for (const [field, expectedType] of Object.entries(requiredFields)) {
      const actualType = typeof data[field]
      const isMatch = actualType === expectedType

      result.fieldMappings.push({
        field,
        expected: expectedType,
        actual: actualType,
        isMatch
      })

      if (!isMatch) {
        if (data[field] === undefined || data[field] === null) {
          result.errors.push(`缺少必需字段: ${field}`)
        } else {
          result.errors.push(`字段类型不匹配: ${field} 期望 ${expectedType}, 实际 ${actualType}`)
        }
        result.isValid = false
      }
    }

    // 验证可选字段
    for (const [field, expectedType] of Object.entries(optionalFields)) {
      if (data[field] !== undefined && data[field] !== null) {
        const actualType = typeof data[field]
        const isMatch = actualType === expectedType

        result.fieldMappings.push({
          field: `${field} (optional)`,
          expected: expectedType,
          actual: actualType,
          isMatch
        })

        if (!isMatch) {
          result.warnings.push(`可选字段类型不匹配: ${field} 期望 ${expectedType}, 实际 ${actualType}`)
        }
      }
    }

    // 验证枚举值
    if (data.level && !['regular', 'vip', 'premium'].includes(data.level)) {
      result.errors.push(`无效的客户等级: ${data.level}`)
      result.isValid = false
    }

    if (data.status && !['active', 'inactive', 'suspended'].includes(data.status)) {
      result.errors.push(`无效的客户状态: ${data.status}`)
      result.isValid = false
    }

    // 验证邮箱格式
    if (data.email && typeof data.email === 'string') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(data.email)) {
        result.warnings.push(`邮箱格式可能不正确: ${data.email}`)
      }
    }

    // 验证电话格式
    if (data.phone && typeof data.phone === 'string') {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(data.phone)) {
        result.warnings.push(`电话格式可能不正确: ${data.phone}`)
      }
    }

    // 验证地址对象结构
    if (data.address && typeof data.address === 'object') {
      const addressFields = ['province', 'city', 'detail']
      for (const field of addressFields) {
        if (!data.address[field] || typeof data.address[field] !== 'string') {
          result.warnings.push(`地址对象缺少或格式错误: ${field}`)
        }
      }
    }

    // 验证设备数量对象结构
    if (data.deviceCount && typeof data.deviceCount === 'object') {
      const deviceCountFields = ['total', 'purchased', 'rental']
      for (const field of deviceCountFields) {
        if (typeof data.deviceCount[field] !== 'number') {
          result.warnings.push(`设备数量对象字段类型错误: ${field}`)
        }
      }
    }

    return result
  }

  /**
   * 验证客户列表响应格式
   */
  static validateCustomerListResponse(data: any): ValidationResult {
    const result: ValidationResult = {
      isValid: true,
      errors: [],
      warnings: [],
      fieldMappings: []
    }

    // 验证响应结构
    const requiredFields = {
      list: 'object', // array
      total: 'number',
      page: 'number',
      pageSize: 'number'
    }

    for (const [field, expectedType] of Object.entries(requiredFields)) {
      const actualType = typeof data[field]
      const isMatch = actualType === expectedType

      result.fieldMappings.push({
        field,
        expected: expectedType,
        actual: actualType,
        isMatch
      })

      if (!isMatch) {
        result.errors.push(`响应结构字段类型不匹配: ${field} 期望 ${expectedType}, 实际 ${actualType}`)
        result.isValid = false
      }
    }

    // 验证list是否为数组
    if (!Array.isArray(data.list)) {
      result.errors.push('list字段必须是数组')
      result.isValid = false
    } else {
      // 验证数组中的每个客户对象
      data.list.forEach((customer: any, index: number) => {
        const customerValidation = this.validateCustomer(customer)
        if (!customerValidation.isValid) {
          result.errors.push(`客户列表第${index + 1}项验证失败: ${customerValidation.errors.join(', ')}`)
          result.isValid = false
        }
        result.warnings.push(...customerValidation.warnings.map(w => `客户列表第${index + 1}项: ${w}`))
      })
    }

    // 验证分页数据合理性
    if (typeof data.page === 'number' && data.page < 1) {
      result.warnings.push('页码应该从1开始')
    }

    if (typeof data.pageSize === 'number' && data.pageSize < 1) {
      result.warnings.push('页面大小应该大于0')
    }

    if (typeof data.total === 'number' && data.total < 0) {
      result.warnings.push('总数不应为负数')
    }

    if (Array.isArray(data.list) && typeof data.total === 'number') {
      if (data.list.length > data.total) {
        result.warnings.push(`当前页数据量(${data.list.length})超过总数(${data.total})`)
      }
    }

    return result
  }

  /**
   * 验证API响应的通用结构
   */
  static validateApiResponse(response: any): ValidationResult {
    const result: ValidationResult = {
      isValid: true,
      errors: [],
      warnings: [],
      fieldMappings: []
    }

    // 验证响应基本结构
    const requiredFields = {
      code: 'number',
      message: 'string',
      data: 'object' // 可以是null
    }

    for (const [field, expectedType] of Object.entries(requiredFields)) {
      const actualType = typeof response[field]
      const isMatch = actualType === expectedType || (field === 'data' && response[field] === null)

      result.fieldMappings.push({
        field,
        expected: expectedType,
        actual: response[field] === null ? 'null' : actualType,
        isMatch
      })

      if (!isMatch) {
        result.errors.push(`API响应结构字段类型不匹配: ${field} 期望 ${expectedType}, 实际 ${actualType}`)
        result.isValid = false
      }
    }

    // 验证状态码合理性
    if (typeof response.code === 'number') {
      if (response.code < 100 || response.code >= 600) {
        result.warnings.push(`状态码可能不合理: ${response.code}`)
      }
    }

    return result
  }

  /**
   * 生成验证报告
   */
  static generateValidationReport(validationResults: ValidationResult[]): string {
    let report = '\n📋 API响应验证报告\n'
    report += '=' .repeat(50) + '\n'

    let totalErrors = 0
    let totalWarnings = 0
    let totalFields = 0

    validationResults.forEach((result, index) => {
      report += `\n🧪 测试 ${index + 1}:\n`
      report += `状态: ${result.isValid ? '✅ 通过' : '❌ 失败'}\n`

      if (result.errors.length > 0) {
        report += `错误 (${result.errors.length}):\n`
        result.errors.forEach(error => {
          report += `  • ${error}\n`
        })
        totalErrors += result.errors.length
      }

      if (result.warnings.length > 0) {
        report += `警告 (${result.warnings.length}):\n`
        result.warnings.forEach(warning => {
          report += `  ⚠️ ${warning}\n`
        })
        totalWarnings += result.warnings.length
      }

      if (result.fieldMappings.length > 0) {
        const matchedFields = result.fieldMappings.filter(f => f.isMatch).length
        const totalFieldsInTest = result.fieldMappings.length
        report += `字段映射: ${matchedFields}/${totalFieldsInTest} 匹配\n`
        
        result.fieldMappings.forEach(mapping => {
          const status = mapping.isMatch ? '✅' : '❌'
          report += `  ${status} ${mapping.field}: ${mapping.expected} -> ${mapping.actual}\n`
        })
        
        totalFields += totalFieldsInTest
      }

      report += '\n'
    })

    // 总结
    report += '📊 验证总结:\n'
    report += '-' .repeat(30) + '\n'
    report += `总测试数: ${validationResults.length}\n`
    report += `通过测试: ${validationResults.filter(r => r.isValid).length}\n`
    report += `失败测试: ${validationResults.filter(r => !r.isValid).length}\n`
    report += `总错误数: ${totalErrors}\n`
    report += `总警告数: ${totalWarnings}\n`
    report += `总字段数: ${totalFields}\n`

    const successRate = validationResults.length > 0 
      ? (validationResults.filter(r => r.isValid).length / validationResults.length * 100).toFixed(1)
      : '0'
    report += `成功率: ${successRate}%\n`

    return report
  }
}

export default ApiResponseValidator