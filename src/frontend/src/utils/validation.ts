/**
 * 类型安全的验证工具函数
 * 提供与后端验证规则一致的前端验证
 * 
 * @author Kiro
 * @date 2025-01-28
 */

import type {
  RentalRecordFormData,
  RentalDeviceFormData,
  RentalCustomerFormData,
  ValidationError,
  DeviceStatus,
  RentalStatus,
  CustomerType,
  MaintenanceStatus
} from '@/types/rental'

// ==================== 基础验证函数 ====================

/**
 * 验证必填字段
 */
export const validateRequired = (value: any, fieldName: string): ValidationError | null => {
  if (value === null || value === undefined || value === '') {
    return {
      field: fieldName,
      message: `${fieldName}不能为空`,
      code: 'REQUIRED'
    }
  }
  return null
}

/**
 * 验证字符串长度
 */
export const validateStringLength = (
  value: string,
  fieldName: string,
  min?: number,
  max?: number
): ValidationError | null => {
  if (!value) return null
  
  if (min && value.length < min) {
    return {
      field: fieldName,
      message: `${fieldName}长度不能少于${min}个字符`,
      code: 'MIN_LENGTH'
    }
  }
  
  if (max && value.length > max) {
    return {
      field: fieldName,
      message: `${fieldName}长度不能超过${max}个字符`,
      code: 'MAX_LENGTH'
    }
  }
  
  return null
}

/**
 * 验证数值范围
 */
export const validateNumberRange = (
  value: number,
  fieldName: string,
  min?: number,
  max?: number
): ValidationError | null => {
  if (value === null || value === undefined) return null
  
  if (min !== undefined && value < min) {
    return {
      field: fieldName,
      message: `${fieldName}不能小于${min}`,
      code: 'MIN_VALUE'
    }
  }
  
  if (max !== undefined && value > max) {
    return {
      field: fieldName,
      message: `${fieldName}不能大于${max}`,
      code: 'MAX_VALUE'
    }
  }
  
  return null
}

/**
 * 验证邮箱格式
 */
export const validateEmail = (email: string, fieldName: string = '邮箱'): ValidationError | null => {
  if (!email) return null
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) {
    return {
      field: fieldName,
      message: `${fieldName}格式不正确`,
      code: 'INVALID_EMAIL'
    }
  }
  
  return null
}

/**
 * 验证手机号格式
 */
export const validatePhone = (phone: string, fieldName: string = '手机号'): ValidationError | null => {
  if (!phone) return null
  
  const phoneRegex = /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/
  if (!phoneRegex.test(phone)) {
    return {
      field: fieldName,
      message: `${fieldName}格式不正确`,
      code: 'INVALID_PHONE'
    }
  }
  
  return null
}

/**
 * 验证日期格式
 */
export const validateDate = (date: string, fieldName: string): ValidationError | null => {
  if (!date) return null
  
  const dateRegex = /^\d{4}-\d{2}-\d{2}$/
  if (!dateRegex.test(date)) {
    return {
      field: fieldName,
      message: `${fieldName}格式不正确，应为YYYY-MM-DD`,
      code: 'INVALID_DATE'
    }
  }
  
  const parsedDate = new Date(date)
  if (isNaN(parsedDate.getTime())) {
    return {
      field: fieldName,
      message: `${fieldName}不是有效日期`,
      code: 'INVALID_DATE'
    }
  }
  
  return null
}

/**
 * 验证金额格式
 */
export const validateAmount = (
  amount: number,
  fieldName: string,
  min: number = 0,
  max: number = 999999.99
): ValidationError | null => {
  if (amount === null || amount === undefined) return null
  
  if (amount < min) {
    return {
      field: fieldName,
      message: `${fieldName}不能小于${min}`,
      code: 'MIN_AMOUNT'
    }
  }
  
  if (amount > max) {
    return {
      field: fieldName,
      message: `${fieldName}不能超过${max}`,
      code: 'MAX_AMOUNT'
    }
  }
  
  // 验证小数位数
  const decimalPlaces = (amount.toString().split('.')[1] || '').length
  if (decimalPlaces > 2) {
    return {
      field: fieldName,
      message: `${fieldName}最多保留2位小数`,
      code: 'INVALID_DECIMAL'
    }
  }
  
  return null
}

// ==================== 枚举验证函数 ====================

/**
 * 验证设备状态
 */
export const validateDeviceStatus = (status: string): boolean => {
  return Object.values(DeviceStatus).includes(status as DeviceStatus)
}

/**
 * 验证租赁状态
 */
export const validateRentalStatus = (status: string): boolean => {
  return Object.values(RentalStatus).includes(status as RentalStatus)
}

/**
 * 验证客户类型
 */
export const validateCustomerType = (type: string): boolean => {
  return Object.values(CustomerType).includes(type as CustomerType)
}

/**
 * 验证维护状态
 */
export const validateMaintenanceStatus = (status: string): boolean => {
  return Object.values(MaintenanceStatus).includes(status as MaintenanceStatus)
}

// ==================== 表单验证函数 ====================

/**
 * 验证租赁记录表单
 */
export const validateRentalRecordForm = (data: RentalRecordFormData): ValidationError[] => {
  const errors: ValidationError[] = []
  
  // 验证必填字段
  const requiredError1 = validateRequired(data.deviceId, '设备ID')
  if (requiredError1) errors.push(requiredError1)
  
  const requiredError2 = validateRequired(data.customerId, '客户ID')
  if (requiredError2) errors.push(requiredError2)
  
  const requiredError3 = validateRequired(data.rentalStartDate, '租赁开始日期')
  if (requiredError3) errors.push(requiredError3)
  
  const requiredError4 = validateRequired(data.plannedEndDate, '计划结束日期')
  if (requiredError4) errors.push(requiredError4)
  
  const requiredError5 = validateRequired(data.rentalPeriod, '租赁期间')
  if (requiredError5) errors.push(requiredError5)
  
  const requiredError6 = validateRequired(data.dailyRentalFee, '日租金')
  if (requiredError6) errors.push(requiredError6)
  
  // 验证日期格式
  const dateError1 = validateDate(data.rentalStartDate, '租赁开始日期')
  if (dateError1) errors.push(dateError1)
  
  const dateError2 = validateDate(data.plannedEndDate, '计划结束日期')
  if (dateError2) errors.push(dateError2)
  
  // 验证租赁期间
  const periodError = validateNumberRange(data.rentalPeriod, '租赁期间', 1, 365)
  if (periodError) errors.push(periodError)
  
  // 验证日租金
  const feeError = validateAmount(data.dailyRentalFee, '日租金', 0.01, 9999.99)
  if (feeError) errors.push(feeError)
  
  // 验证押金
  if (data.depositAmount !== undefined) {
    const depositError = validateAmount(data.depositAmount, '押金金额', 0, 99999.99)
    if (depositError) errors.push(depositError)
  }
  
  // 验证日期逻辑
  if (data.rentalStartDate && data.plannedEndDate) {
    const startDate = new Date(data.rentalStartDate)
    const endDate = new Date(data.plannedEndDate)
    
    if (startDate >= endDate) {
      errors.push({
        field: 'plannedEndDate',
        message: '计划结束日期必须晚于租赁开始日期',
        code: 'INVALID_DATE_RANGE'
      })
    }
  }
  
  return errors
}

/**
 * 验证设备表单
 */
export const validateRentalDeviceForm = (data: RentalDeviceFormData): ValidationError[] => {
  const errors: ValidationError[] = []
  
  // 验证必填字段
  const requiredError1 = validateRequired(data.deviceId, '设备编号')
  if (requiredError1) errors.push(requiredError1)
  
  const requiredError2 = validateRequired(data.deviceModel, '设备型号')
  if (requiredError2) errors.push(requiredError2)
  
  const requiredError3 = validateRequired(data.deviceName, '设备名称')
  if (requiredError3) errors.push(requiredError3)
  
  const requiredError4 = validateRequired(data.deviceCategory, '设备类别')
  if (requiredError4) errors.push(requiredError4)
  
  const requiredError5 = validateRequired(data.dailyRentalPrice, '日租金价格')
  if (requiredError5) errors.push(requiredError5)
  
  // 验证字符串长度
  const lengthError1 = validateStringLength(data.deviceId, '设备编号', 1, 50)
  if (lengthError1) errors.push(lengthError1)
  
  const lengthError2 = validateStringLength(data.deviceModel, '设备型号', 1, 100)
  if (lengthError2) errors.push(lengthError2)
  
  const lengthError3 = validateStringLength(data.deviceName, '设备名称', 1, 200)
  if (lengthError3) errors.push(lengthError3)
  
  const lengthError4 = validateStringLength(data.deviceCategory, '设备类别', 1, 100)
  if (lengthError4) errors.push(lengthError4)
  
  // 验证价格
  const priceError = validateAmount(data.dailyRentalPrice, '日租金价格', 0.01, 9999.99)
  if (priceError) errors.push(priceError)
  
  if (data.purchasePrice !== undefined) {
    const purchasePriceError = validateAmount(data.purchasePrice, '采购价格', 0, 999999.99)
    if (purchasePriceError) errors.push(purchasePriceError)
  }
  
  // 验证日期
  if (data.purchaseDate) {
    const dateError = validateDate(data.purchaseDate, '采购日期')
    if (dateError) errors.push(dateError)
  }
  
  return errors
}

/**
 * 验证客户表单
 */
export const validateRentalCustomerForm = (data: RentalCustomerFormData): ValidationError[] => {
  const errors: ValidationError[] = []
  
  // 验证必填字段
  const requiredError1 = validateRequired(data.customerName, '客户名称')
  if (requiredError1) errors.push(requiredError1)
  
  const requiredError2 = validateRequired(data.customerType, '客户类型')
  if (requiredError2) errors.push(requiredError2)
  
  // 验证字符串长度
  const lengthError1 = validateStringLength(data.customerName, '客户名称', 1, 200)
  if (lengthError1) errors.push(lengthError1)
  
  if (data.contactPerson) {
    const lengthError2 = validateStringLength(data.contactPerson, '联系人', 1, 100)
    if (lengthError2) errors.push(lengthError2)
  }
  
  // 验证客户类型
  if (data.customerType && !validateCustomerType(data.customerType)) {
    errors.push({
      field: 'customerType',
      message: '客户类型不正确',
      code: 'INVALID_CUSTOMER_TYPE'
    })
  }
  
  // 验证手机号
  if (data.phone) {
    const phoneError = validatePhone(data.phone, '联系电话')
    if (phoneError) errors.push(phoneError)
  }
  
  // 验证邮箱
  if (data.email) {
    const emailError = validateEmail(data.email, '邮箱地址')
    if (emailError) errors.push(emailError)
  }
  
  return errors
}

// ==================== 工具函数 ====================

/**
 * 检查是否有验证错误
 */
export const hasValidationErrors = (errors: ValidationError[]): boolean => {
  return errors.length > 0
}

/**
 * 获取字段的第一个错误消息
 */
export const getFieldError = (errors: ValidationError[], fieldName: string): string | null => {
  const error = errors.find(e => e.field === fieldName)
  return error ? error.message : null
}

/**
 * 格式化验证错误消息
 */
export const formatValidationErrors = (errors: ValidationError[]): string => {
  return errors.map(error => error.message).join('; ')
}

/**
 * 创建Element Plus表单验证规则
 */
export const createFormRules = (validators: Record<string, (value: any) => ValidationError | null>) => {
  const rules: Record<string, any[]> = {}
  
  Object.keys(validators).forEach(field => {
    rules[field] = [
      {
        validator: (rule: any, value: any, callback: any) => {
          const error = validators[field](value)
          if (error) {
            callback(new Error(error.message))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  })
  
  return rules
}