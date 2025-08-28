import { request } from '@/utils/request'
import type { 
  Order, 
  CreateOrderData, 
  UpdateOrderData, 
  OrderQueryParams,
  OrderStats,
  OrderListResponse
} from '@/types/order'

// 订单管理API
export const orderApi = {
  // 获取订单列表
  async getOrders(params?: OrderQueryParams) {
    return request.get<OrderListResponse>('/api/admin/orders', { params })
  },

  // 获取订单详情
  async getOrder(id: string) {
    return request.get<Order>(`/api/admin/orders/${id}`)
  },

  // 创建订单
  async createOrder(data: CreateOrderData) {
    return request.post<Order>('/api/admin/orders', data)
  },

  // 更新订单
  async updateOrder(id: string, data: UpdateOrderData) {
    return request.put<Order>(`/api/admin/orders/${id}`, data)
  },

  // 删除订单
  async deleteOrder(id: string) {
    return request.delete(`/api/admin/orders/${id}`)
  },

  // 获取订单统计数据
  async getOrderStats() {
    return request.get<OrderStats>('/api/admin/orders/stats')
  },

  // 更新订单状态
  async updateOrderStatus(id: string, status: Order['status']) {
    return request.patch(`/api/admin/orders/${id}/status`, { status })
  },

  // 批量更新订单状态
  async batchUpdateOrderStatus(orderIds: string[], status: Order['status']) {
    return request.patch('/api/admin/orders/batch/status', { orderIds, status })
  },

  // 批量删除订单
  async batchDeleteOrders(orderIds: string[]) {
    return request.delete('/api/admin/orders/batch', { data: { orderIds } })
  },

  // 导出订单数据
  async exportOrders(params?: OrderQueryParams) {
    return request.get('/api/admin/orders/export', { 
      params,
      responseType: 'blob'
    })
  },

  // 获取订单物流信息
  async getOrderTracking(id: string) {
    return request.get(`/api/admin/orders/${id}/tracking`)
  },

  // 更新订单物流信息
  async updateOrderTracking(id: string, trackingNumber: string) {
    return request.put(`/api/admin/orders/${id}/tracking`, { trackingNumber })
  }
}