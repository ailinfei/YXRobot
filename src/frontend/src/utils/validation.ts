/**
 * 表单验证工具函数
 * 提供统一的验证规则和错误提示格式化
 */

// 验证规则类型
export interface ValidationRule {
  required?: boolean
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (value: any) => boolean | string
  message?: string
}

// 验证结果类型
export interface ValidationResult {
  isValid: boolean
  errors: string[]
}

/**
 * 通用验证函数
 */
export function validateField(value: any, rules: ValidationRule[]): ValidationResult {
  const errors: string[] = []

  for (const rule of rules) {
    // 必填验证
    if (rule.required && (value === null || value === undefined || value === '')) {
      errors.push(rule.message || '此字段为必填项')
      continue
    }

    // 如果值为空且不是必填，跳过其他验证
    if (!rule.required && (value === null || value === undefined || value === '')) {
      continue
    }

    // 最小长度/值验证
    if (rule.min !== undefined) {
      if (typeof value === 'string' && value.length < rule.min) {
        errors.push(rule.message || `最少需要${rule.min}个字符`)
      } else if (typeof value === 'number' && value < rule.min) {
        errors.push(rule.message || `值不能小于${rule.min}`)
      }
    }

    // 最大长度/值验证
    if (rule.max !== undefined) {
      if (typeof value === 'string' && value.length > rule.max) {
        errors.push(rule.message || `最多允许${rule.max}个字符`)
      } else if (typeof value === 'number' && value > rule.max) {
        errors.push(rule.message || `值不能大于${rule.max}`)
      }
    }

    // 正则表达式验证
    if (rule.pattern && typeof value === 'string' && !rule.pattern.test(value)) {
      errors.push(rule.message || '格式不正确')
    }

    // 自定义验证器
    if (rule.validator) {
      const result = rule.validator(value)
      if (result !== true) {
        errors.push(typeof result === 'string' ? result : (rule.message || '验证失败'))
      }
    }
  }

  return {
    isValid: errors.length === 0,
    errors
  }
}

/**
 * 预定义验证规则
 */
export const ValidationRules = {
  // 邮箱验证
  email: {
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: '请输入有效的邮箱地址'
  },

  // 手机号验证（中国）
  phone: {
    pattern: /^1[3-9]\d{9}$/,
    message: '请输入有效的手机号码'
  },

  // 身份证号验证（中国）
  idCard: {
    pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
    message: '请输入有效的身份证号码'
  },

  // URL验证
  url: {
    pattern: /^https?:\/\/.+/,
    message: '请输入有效的URL地址'
  },

  // 密码强度验证
  password: {
    validator: (value: string) => {
      if (!value) return '密码不能为空'
      if (value.length < 8) return '密码长度至少8位'
      if (!/(?=.*[a-z])/.test(value)) return '密码必须包含小写字母'
      if (!/(?=.*[A-Z])/.test(value)) return '密码必须包含大写字母'
      if (!/(?=.*\d)/.test(value)) return '密码必须包含数字'
      return true
    }
  },

  // 用户名验证
  username: {
    pattern: /^[a-zA-Z0-9_]{3,20}$/,
    message: '用户名只能包含字母、数字和下划线，长度3-20位'
  },

  // 中文姓名验证
  chineseName: {
    pattern: /^[\u4e00-\u9fa5]{2,10}$/,
    message: '请输入2-10位中文姓名'
  },

  // 数字验证
  number: {
    validator: (value: any) => {
      return !isNaN(Number(value)) || '请输入有效的数字'
    }
  },

  // 正整数验证
  positiveInteger: {
    validator: (value: any) => {
      const num = Number(value)
      return (Number.isInteger(num) && num > 0) || '请输入正整数'
    }
  }
}

/**
 * 批量验证表单数据
 */
export function validateForm(
  data: Record<string, any>,
  rules: Record<string, ValidationRule[]>
): { isValid: boolean; errors: Record<string, string[]> } {
  const errors: Record<string, string[]> = {}
  let isValid = true

  for (const [field, fieldRules] of Object.entries(rules)) {
    const result = validateField(data[field], fieldRules)
    if (!result.isValid) {
      errors[field] = result.errors
      isValid = false
    }
  }

  return { isValid, errors }
}

/**
 * 格式化验证错误信息
 */
export function formatValidationErrors(errors: Record<string, string[]>): string[] {
  const formattedErrors: string[] = []
  
  for (const [field, fieldErrors] of Object.entries(errors)) {
    fieldErrors.forEach(error => {
      formattedErrors.push(`${field}: ${error}`)
    })
  }
  
  return formattedErrors
}

/**
 * 实时验证装饰器
 */
export function createValidator(rules: ValidationRule[]) {
  return {
    validate: (value: any) => validateField(value, rules),
    rules
  }
}

/**
 * 异步验证函数
 */
export async function validateAsync(
  value: any,
  asyncValidator: (value: any) => Promise<boolean | string>
): Promise<ValidationResult> {
  try {
    const result = await asyncValidator(value)
    return {
      isValid: result === true,
      errors: result === true ? [] : [typeof result === 'string' ? result : '验证失败']
    }
  } catch (error) {
    return {
      isValid: false,
      errors: ['验证过程中发生错误']
    }
  }
}

/**
 * 防抖验证
 */
export function debounceValidation(
  validator: (value: any) => ValidationResult,
  delay: number = 300
) {
  let timeoutId: NodeJS.Timeout | null = null
  
  return (value: any, callback: (result: ValidationResult) => void) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    
    timeoutId = setTimeout(() => {
      const result = validator(value)
      callback(result)
    }, delay)
  }
}