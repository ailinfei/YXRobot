/**
 * 集成测试配置文件
 * 用于配置API测试的各种参数和环境设置
 */

// API配置
export const API_CONFIG = {
  baseURL: process.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  retryAttempts: 3,
  retryDelay: 1000
}

// 性能要求配置
export const PERFORMANCE_REQUIREMENTS = {
  // 查询API性能要求 (毫秒)
  customerStats: 1000,      // 客户统计API < 1秒
  customerList: 2000,       // 客户列表API < 2秒
  customerDetail: 1500,     // 客户详情API < 1.5秒
  customerSearch: 1000,     // 客户搜索API < 1秒
  
  // CRUD操作性能要求 (毫秒)
  customerCreate: 3000,     // 客户创建API < 3秒
  customerUpdate: 2000,     // 客户更新API < 2秒
  customerDelete: 1000,     // 客户删除API < 1秒
  
  // 关联数据API性能要求 (毫秒)
  customerDevices: 2000,    // 客户设备API < 2秒
  customerOrders: 2000,     // 客户订单API < 2秒
  customerServiceRecords: 2000, // 客户服务记录API < 2秒
  
  // 批量操作性能要求 (毫秒)
  batchOperation: 5000,     // 批量操作API < 5秒
  dataExport: 10000,        // 数据导出API < 10秒
  
  // 并发性能要求
  concurrentRequests: 10,   // 支持10个并发请求
  concurrentMultiplier: 1.5 // 并发时允许1.5倍响应时间
}

// 测试数据配置
export const TEST_DATA = {
  // 测试客户数据
  customer: {
    name: '集成测试客户',
    level: 'regular',
    phone: '13800138999',
    email: 'integration.test@example.com',
    company: '测试公司',
    status: 'active',
    address: {
      province: '北京市',
      city: '北京市',
      detail: '朝阳区测试街道999号'
    },
    tags: ['集成测试', '自动化测试'],
    notes: '这是集成测试创建的客户数据'
  },
  
  // 无效测试数据
  invalidCustomer: {
    name: '', // 空名称
    level: 'invalid_level', // 无效等级
    phone: '123', // 无效电话
    email: 'invalid_email', // 无效邮箱
    status: 'active'
  },
  
  // 更新数据
  updateCustomer: {
    name: '集成测试客户_已更新',
    level: 'vip',
    notes: '客户已升级为VIP'
  }
}

// 验证规则配置
export const VALIDATION_RULES = {
  // 必需字段验证
  requiredFields: {
    customer: ['id', 'name', 'level', 'phone', 'email', 'status', 'totalSpent', 'customerValue', 'registeredAt'],
    customerStats: ['total', 'regular', 'vip', 'premium', 'activeDevices', 'totalRevenue', 'newThisMonth'],
    customerListResponse: ['list', 'total', 'page', 'pageSize']
  },
  
  // 字段类型验证
  fieldTypes: {
    customer: {
      id: 'string',
      name: 'string',
      level: 'string',
      phone: 'string',
      email: 'string',
      status: 'string',
      totalSpent: 'number',
      customerValue: 'number',
      registeredAt: 'string'
    },
    customerStats: {
      total: 'number',
      regular: 'number',
      vip: 'number',
      premium: 'number',
      activeDevices: 'number',
      totalRevenue: 'number',
      newThisMonth: 'number'
    }
  },
  
  // 枚举值验证
  enumValues: {
    customerLevel: ['regular', 'vip', 'premium'],
    customerStatus: ['active', 'inactive', 'suspended'],
    deviceType: ['purchased', 'rental'],
    deviceStatus: ['pending', 'active', 'offline', 'maintenance', 'retired'],
    orderType: ['sales', 'rental'],
    orderStatus: ['pending', 'processing', 'completed', 'cancelled'],
    serviceType: ['maintenance', 'upgrade', 'consultation', 'complaint'],
    serviceStatus: ['in_progress', 'completed', 'cancelled']
  },
  
  // 格式验证
  formats: {
    email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    phone: /^1[3-9]\d{9}$/,
    dateTime: /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/
  }
}

// 测试环境配置
export const TEST_ENVIRONMENT = {
  // 健康检查配置
  healthCheck: {
    timeout: 30000,
    interval: 2000,
    maxRetries: 15
  },
  
  // 数据清理配置
  cleanup: {
    enabled: true,
    retryAttempts: 3,
    retryDelay: 1000
  },
  
  // 日志配置
  logging: {
    level: 'info', // 'debug', 'info', 'warn', 'error'
    enableConsole: true,
    enableFile: false,
    filePrefix: 'integration-test'
  },
  
  // 报告配置
  reporting: {
    generateReport: true,
    reportFormat: 'console', // 'console', 'json', 'html'
    saveToFile: false,
    reportPath: './test-reports'
  }
}

// 错误处理配置
export const ERROR_HANDLING = {
  // 预期错误码
  expectedErrorCodes: {
    notFound: [404],
    badRequest: [400],
    unauthorized: [401],
    forbidden: [403],
    serverError: [500, 502, 503]
  },
  
  // 重试配置
  retry: {
    maxAttempts: 3,
    delay: 1000,
    backoffMultiplier: 2,
    retryableErrors: ['ECONNRESET', 'ETIMEDOUT', 'ENOTFOUND']
  }
}

// 导出默认配置
export default {
  API_CONFIG,
  PERFORMANCE_REQUIREMENTS,
  TEST_DATA,
  VALIDATION_RULES,
  TEST_ENVIRONMENT,
  ERROR_HANDLING
}