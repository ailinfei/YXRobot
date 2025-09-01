/**
 * 客户管理模块 API 调用服务
 * 任务17：实现前端API调用服务
 * 与后端CustomerController接口对应
 */

import request from '@/utils/request'
import type { 
  Customer, 
  CustomerCreateRequest,
  CustomerUpdateRequest, 
  CustomerQueryRequest,
  CustomerStats,
  CustomerServiceRecord,
  CustomerDevice,
  CustomerOrder,
  PaginatedResponse,
  CustomerFilterOptions,
  CustomerSearchSuggestion,
  CustomerBatchOperation,
  CustomerExportParams,
  CustomerDetailExtended,
  AddDeviceData,
  AddServiceRecordData
} from '@/types/customer'

// 使用request工具的ApiResponse类型
import type { ApiResponse } from '@/utils/request'

// 客户基础CRUD操作API
export const customerApi = {
  // 获取客户列表 - 支持分页、搜索、筛选
  async getCustomers(params?: CustomerQueryRequest): Promise<ApiResponse<PaginatedResponse<Customer>>> {
    return request.get('/api/admin/customers', params)
  },

  // 获取客户详情
  async getCustomer(id: string | number): Promise<ApiResponse<Customer>> {
    return request.get(`/api/admin/customers/${id}`)
  },

  // 创建客户
  async createCustomer(data: CustomerCreateRequest): Promise<ApiResponse<Customer>> {
    return request.post('/api/admin/customers', data)
  },

  // 更新客户
  async updateCustomer(id: string | number, data: CustomerUpdateRequest): Promise<ApiResponse<Customer>> {
    return request.put(`/api/admin/customers/${id}`, data)
  },

  // 删除客户
  async deleteCustomer(id: string | number): Promise<ApiResponse<void>> {
    return request.delete(`/api/admin/customers/${id}`)
  },

  // 获取客户统计数据 - 支持前端统计卡片
  async getCustomerStats(): Promise<ApiResponse<CustomerStats>> {
    return request.get('/api/admin/customers/stats')
  }
}

// 客户关联数据API
export const customerRelationApi = {
  // 获取客户设备列表
  async getCustomerDevices(customerId: string | number, params?: { page?: number; pageSize?: number }): Promise<ApiResponse<PaginatedResponse<CustomerDevice>>> {
    return request.get(`/api/admin/customers/${customerId}/devices`, params)
  },

  // 获取客户订单列表
  async getCustomerOrders(customerId: string | number, params?: { page?: number; pageSize?: number }): Promise<ApiResponse<PaginatedResponse<CustomerOrder>>> {
    return request.get(`/api/admin/customers/${customerId}/orders`, params)
  },

  // 获取客户服务记录
  async getCustomerServiceRecords(customerId: string | number, params?: { page?: number; pageSize?: number }): Promise<ApiResponse<PaginatedResponse<CustomerServiceRecord>>> {
    return request.get(`/api/admin/customers/${customerId}/service-records`, params)
  },

  // 添加设备到客户
  async addDeviceToCustomer(customerId: string | number, data: AddDeviceData): Promise<ApiResponse<CustomerDevice>> {
    return request.post(`/api/admin/customers/${customerId}/devices`, data)
  },

  // 移除客户设备
  async removeDeviceFromCustomer(customerId: string | number, deviceId: string | number): Promise<ApiResponse<void>> {
    return request.delete(`/api/admin/customers/${customerId}/devices/${deviceId}`)
  },

  // 创建服务记录
  async createServiceRecord(customerId: string | number, data: AddServiceRecordData): Promise<ApiResponse<CustomerServiceRecord>> {
    return request.post(`/api/admin/customers/${customerId}/service-records`, data)
  }
}

// 客户管理操作API
export const customerManagementApi = {
  // 更新客户等级
  async updateCustomerLevel(id: string | number, level: 'REGULAR' | 'VIP' | 'PREMIUM'): Promise<ApiResponse<void>> {
    return request.put(`/api/admin/customers/${id}/level`, { level })
  },

  // 更新客户状态
  async updateCustomerStatus(id: string | number, status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'): Promise<ApiResponse<void>> {
    return request.put(`/api/admin/customers/${id}/status`, { status })
  },

  // 批量操作客户
  async batchOperation(operation: CustomerBatchOperation): Promise<ApiResponse<{ successCount: number; failureCount: number; errors?: string[] }>> {
    return request.post('/api/admin/customers/batch', operation)
  }
}

// 客户搜索和筛选API
export const customerSearchApi = {

  // 搜索客户 - 支持关键词搜索
  async searchCustomers(keyword: string, limit: number = 10): Promise<ApiResponse<CustomerSearchSuggestion[]>> {
    return request.get('/api/admin/customers/search', { keyword, limit })
  },

  // 获取搜索建议
  async getSearchSuggestions(keyword: string, limit: number = 10): Promise<ApiResponse<CustomerSearchSuggestion[]>> {
    return request.get('/api/admin/customers/search-suggestions', { keyword, limit })
  },

  // 高级搜索 - 支持多条件搜索
  async advancedSearch(params: CustomerQueryRequest): Promise<ApiResponse<PaginatedResponse<Customer>>> {
    return request.post('/api/admin/customers/advanced-search', params)
  },

  // 获取筛选选项 - 支持前端筛选功能
  async getFilterOptions(): Promise<ApiResponse<CustomerFilterOptions>> {
    return request.get('/api/admin/customers/filter-options')
  },

  // 按等级获取客户
  async getCustomersByLevel(level: string, params?: CustomerQueryRequest): Promise<ApiResponse<PaginatedResponse<Customer>>> {
    return request.get(`/api/admin/customers/by-level/${level}`, params)
  },

  // 按地区获取客户
  async getCustomersByRegion(region: string, params?: CustomerQueryRequest): Promise<ApiResponse<PaginatedResponse<Customer>>> {
    return request.get(`/api/admin/customers/by-region/${region}`, params)
  },

  // 按标签获取客户
  async getCustomersByTags(tags: string, params?: CustomerQueryRequest): Promise<ApiResponse<PaginatedResponse<Customer>>> {
    return request.get('/api/admin/customers/by-tags', { tags, ...params })
  }
}

// 客户数据验证API
export const customerValidationApi = {

  // 验证客户信息
  async validateCustomer(data: Partial<CustomerCreateRequest>): Promise<ApiResponse<{ isValid: boolean; errors?: any[] }>> {
    return request.post('/api/admin/customers/validate', data)
  },

  // 检查客户名称是否存在
  async checkCustomerNameExists(name: string, excludeId?: string | number): Promise<ApiResponse<{ exists: boolean }>> {
    return request.get('/api/admin/customers/check-name', { name, excludeId })
  },

  // 检查电话号码是否存在
  async checkPhoneExists(phone: string, excludeId?: string | number): Promise<ApiResponse<{ exists: boolean }>> {
    return request.get('/api/admin/customers/check-phone', { phone, excludeId })
  },

  // 检查邮箱是否存在
  async checkEmailExists(email: string, excludeId?: string | number): Promise<ApiResponse<{ exists: boolean }>> {
    return request.get('/api/admin/customers/check-email', { email, excludeId })
  }
}

// 客户扩展功能API
export const customerExtensionApi = {
  // 获取客户详情（包含扩展信息）
  async getCustomerDetailExtended(id: string | number): Promise<ApiResponse<CustomerDetailExtended>> {
    return request.get(`/api/admin/customers/${id}/detail-extended`)
  },

  // 导出客户数据
  async exportCustomers(params: CustomerExportParams): Promise<ApiResponse<{ downloadUrl: string; fileName: string }>> {
    return request.post('/api/admin/customers/export', params)
  },

  // 获取客户活动日志
  async getCustomerActivityLog(customerId: string | number, params?: { page?: number; pageSize?: number }): Promise<ApiResponse<PaginatedResponse<any>>> {
    return request.get(`/api/admin/customers/${customerId}/activity-log`, params)
  },

  // 获取客户价值分析
  async getCustomerValueAnalysis(customerId: string | number): Promise<ApiResponse<any>> {
    return request.get(`/api/admin/customers/${customerId}/value-analysis`)
  },

  // 获取客户等级选项
  async getCustomerLevelOptions(): Promise<ApiResponse<Array<{ label: string; value: string }>>> {
    return request.get('/api/admin/customers/level-options')
  },

  // 获取地区选项
  async getRegionOptions(): Promise<ApiResponse<Array<{ label: string; value: string }>>> {
    return request.get('/api/admin/customers/region-options')
  },

  // 获取行业选项
  async getIndustryOptions(): Promise<ApiResponse<Array<{ label: string; value: string }>>> {
    return request.get('/api/admin/customers/industry-options')
  }
}

// 统一导出所有API
export const allCustomerApis = {
  ...customerApi,
  ...customerRelationApi,
  ...customerManagementApi,
  ...customerSearchApi,
  ...customerValidationApi,
  ...customerExtensionApi
}

// 默认导出主要的客户API
export default customerApi

// 兼容性导出
export { customerApi }