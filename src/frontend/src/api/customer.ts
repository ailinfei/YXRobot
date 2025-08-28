import { request } from '@/utils/request'
import type { 
  Customer, 
  CreateCustomerData, 
  UpdateCustomerData, 
  CustomerQueryParams,
  CustomerStats,
  ServiceRecord
} from '@/types/customer'

// 客户管理API
export const customerApi = {
  // 获取客户列表
  async getCustomers(params?: CustomerQueryParams) {
    return request.get<{
      data: Customer[]
      total: number
      page: number
      pageSize: number
    }>('/api/admin/customers', { params })
  },

  // 获取客户详情
  async getCustomer(id: string) {
    return request.get<Customer>(`/api/admin/customers/${id}`)
  },

  // 创建客户
  async createCustomer(data: CreateCustomerData) {
    return request.post<Customer>('/api/admin/customers', data)
  },

  // 更新客户
  async updateCustomer(id: string, data: UpdateCustomerData) {
    return request.put<Customer>(`/api/admin/customers/${id}`, data)
  },

  // 删除客户
  async deleteCustomer(id: string) {
    return request.delete(`/api/admin/customers/${id}`)
  },

  // 获取客户统计数据
  async getCustomerStats() {
    return request.get<CustomerStats>('/api/admin/customers/stats')
  },

  // 获取客户设备列表
  async getCustomerDevices(customerId: string) {
    return request.get(`/api/admin/customers/${customerId}/devices`)
  },

  // 获取客户订单列表
  async getCustomerOrders(customerId: string) {
    return request.get(`/api/admin/customers/${customerId}/orders`)
  },

  // 获取客户服务记录
  async getCustomerServiceRecords(customerId: string) {
    return request.get<ServiceRecord[]>(`/api/admin/customers/${customerId}/service-records`)
  },

  // 创建服务记录
  async createServiceRecord(customerId: string, data: Partial<ServiceRecord>) {
    return request.post<ServiceRecord>(`/api/admin/customers/${customerId}/service-records`, data)
  },

  // 更新客户等级
  async updateCustomerLevel(id: string, level: 'regular' | 'vip' | 'premium') {
    return request.put(`/api/admin/customers/${id}/level`, { level })
  },

  // 批量操作
  async batchUpdateCustomers(customerIds: string[], data: Partial<UpdateCustomerData>) {
    return request.put('/api/admin/customers/batch', { customerIds, data })
  },

  // 导出客户数据
  async exportCustomers(params?: CustomerQueryParams) {
    return request.get('/api/admin/customers/export', { 
      params,
      responseType: 'blob'
    })
  }
}