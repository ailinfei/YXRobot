import { axiosInstance as request } from '@/utils/request'
import type { 
  Order, 
  CreateOrderRequest,
  UpdateOrderRequest,
  OrderQueryParams,
  OrderStats,
  OrderListResponse,
  UpdateOrderStatusRequest,
  BatchUpdateOrderStatusRequest,
  BatchDeleteOrdersRequest,
  ExportOrdersRequest,
  OrderSearchSuggestion,
  OrderFilterOptions,
  OrderDetailExtended,
  ApiResponse,
  PaginatedResponse
} from '@/types/order'

/**
 * 订单管理API服务
 * 任务19：实现前端API调用服务
 * 包含完整的CRUD操作、批量操作、搜索筛选等功能
 */

// 核心订单CRUD操作API
export const orderApi = {
  /**
   * 获取订单列表 - 支持分页、搜索、筛选
   * @param params 查询参数
   * @returns 订单列表响应
   */
  async getOrders(params?: OrderQueryParams): Promise<ApiResponse<OrderListResponse>> {
    try {
      const response = await request.get<ApiResponse<OrderListResponse>>('/api/admin/orders', { params })
      return response
    } catch (error) {
      console.error('获取订单列表失败:', error)
      throw error
    }
  },

  /**
   * 获取订单详情
   * @param id 订单ID
   * @returns 订单详情
   */
  async getOrder(id: string): Promise<ApiResponse<Order>> {
    try {
      const response = await request.get<ApiResponse<Order>>(`/api/admin/orders/${id}`)
      return response
    } catch (error) {
      console.error(`获取订单详情失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 创建订单
   * @param data 订单创建数据
   * @returns 创建的订单
   */
  async createOrder(data: CreateOrderRequest): Promise<ApiResponse<Order>> {
    try {
      const response = await request.post<ApiResponse<Order>>('/api/admin/orders', data)
      return response
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    }
  },

  /**
   * 更新订单
   * @param id 订单ID
   * @param data 订单更新数据
   * @returns 更新后的订单
   */
  async updateOrder(id: string, data: UpdateOrderRequest): Promise<ApiResponse<Order>> {
    try {
      const response = await request.put<ApiResponse<Order>>(`/api/admin/orders/${id}`, data)
      return response
    } catch (error) {
      console.error(`更新订单失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 删除订单
   * @param id 订单ID
   * @returns 删除结果
   */
  async deleteOrder(id: string): Promise<ApiResponse<void>> {
    try {
      const response = await request.delete<ApiResponse<void>>(`/api/admin/orders/${id}`)
      return response
    } catch (error) {
      console.error(`删除订单失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 获取订单统计数据
   * @returns 订单统计信息
   */
  async getOrderStats(): Promise<ApiResponse<OrderStats>> {
    try {
      const response = await request.get<ApiResponse<OrderStats>>('/api/admin/orders/stats')
      return response
    } catch (error) {
      console.error('获取订单统计失败:', error)
      throw error
    }
  }
}

// 订单状态管理API
export const orderStatusApi = {
  /**
   * 更新订单状态
   * @param id 订单ID
   * @param status 新状态
   * @param notes 备注
   * @returns 更新结果
   */
  async updateOrderStatus(id: string, status: Order['status'], notes?: string): Promise<ApiResponse<void>> {
    try {
      const data: UpdateOrderStatusRequest = { status, notes }
      const response = await request.patch<ApiResponse<void>>(`/api/admin/orders/${id}/status`, data)
      return response
    } catch (error) {
      console.error(`更新订单状态失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 批量更新订单状态
   * @param orderIds 订单ID列表
   * @param status 新状态
   * @param notes 备注
   * @returns 批量更新结果
   */
  async batchUpdateOrderStatus(orderIds: string[], status: Order['status'], notes?: string): Promise<ApiResponse<void>> {
    try {
      const data: BatchUpdateOrderStatusRequest = { orderIds, status, notes }
      const response = await request.patch<ApiResponse<void>>('/api/admin/orders/batch/status', data)
      return response
    } catch (error) {
      console.error('批量更新订单状态失败:', error)
      throw error
    }
  },

  /**
   * 批量删除订单
   * @param orderIds 订单ID列表
   * @param reason 删除原因
   * @returns 批量删除结果
   */
  async batchDeleteOrders(orderIds: string[], reason?: string): Promise<ApiResponse<void>> {
    try {
      const data: BatchDeleteOrdersRequest = { orderIds, reason }
      const response = await request.delete<ApiResponse<void>>('/api/admin/orders/batch', { data })
      return response
    } catch (error) {
      console.error('批量删除订单失败:', error)
      throw error
    }
  }
}

// 订单搜索和筛选API
export const orderSearchApi = {
  /**
   * 搜索订单建议
   * @param keyword 搜索关键词
   * @param limit 返回数量限制
   * @returns 搜索建议列表
   */
  async searchOrderSuggestions(keyword: string, limit: number = 10): Promise<ApiResponse<OrderSearchSuggestion[]>> {
    try {
      const response = await request.get<ApiResponse<OrderSearchSuggestion[]>>('/api/admin/orders/search-suggestions', {
        params: { keyword, limit }
      })
      return response
    } catch (error) {
      console.error('搜索订单建议失败:', error)
      throw error
    }
  },

  /**
   * 获取订单筛选选项
   * @returns 筛选选项
   */
  async getOrderFilterOptions(): Promise<ApiResponse<OrderFilterOptions>> {
    try {
      const response = await request.get<ApiResponse<OrderFilterOptions>>('/api/admin/orders/filter-options')
      return response
    } catch (error) {
      console.error('获取订单筛选选项失败:', error)
      throw error
    }
  },

  /**
   * 高级搜索订单
   * @param params 搜索参数
   * @returns 搜索结果
   */
  async advancedSearchOrders(params: OrderQueryParams): Promise<ApiResponse<PaginatedResponse<Order>>> {
    try {
      const response = await request.post<ApiResponse<PaginatedResponse<Order>>>('/api/admin/orders/advanced-search', params)
      return response
    } catch (error) {
      console.error('高级搜索订单失败:', error)
      throw error
    }
  }
}

// 订单扩展功能API
export const orderExtensionApi = {
  /**
   * 获取订单详情（扩展信息）
   * @param id 订单ID
   * @returns 订单详情扩展信息
   */
  async getOrderDetailExtended(id: string): Promise<ApiResponse<OrderDetailExtended>> {
    try {
      const response = await request.get<ApiResponse<OrderDetailExtended>>(`/api/admin/orders/${id}/detail-extended`)
      return response
    } catch (error) {
      console.error(`获取订单扩展详情失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 导出订单数据
   * @param params 导出参数
   * @returns 导出文件信息
   */
  async exportOrders(params?: ExportOrdersRequest): Promise<Blob> {
    try {
      const response = await request.get('/api/admin/orders/export', { 
        params,
        responseType: 'blob'
      })
      return response as Blob
    } catch (error) {
      console.error('导出订单数据失败:', error)
      throw error
    }
  },

  /**
   * 获取订单物流信息
   * @param id 订单ID
   * @returns 物流信息
   */
  async getOrderTracking(id: string): Promise<ApiResponse<any>> {
    try {
      const response = await request.get<ApiResponse<any>>(`/api/admin/orders/${id}/tracking`)
      return response
    } catch (error) {
      console.error(`获取订单物流信息失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 更新订单物流信息
   * @param id 订单ID
   * @param trackingNumber 运单号
   * @param company 物流公司
   * @returns 更新结果
   */
  async updateOrderTracking(id: string, trackingNumber: string, company?: string): Promise<ApiResponse<void>> {
    try {
      const data = { trackingNumber, company }
      const response = await request.put<ApiResponse<void>>(`/api/admin/orders/${id}/tracking`, data)
      return response
    } catch (error) {
      console.error(`更新订单物流信息失败 (ID: ${id}):`, error)
      throw error
    }
  },

  /**
   * 获取订单操作日志
   * @param id 订单ID
   * @returns 操作日志列表
   */
  async getOrderLogs(id: string): Promise<ApiResponse<any[]>> {
    try {
      const response = await request.get<ApiResponse<any[]>>(`/api/admin/orders/${id}/logs`)
      return response
    } catch (error) {
      console.error(`获取订单操作日志失败 (ID: ${id}):`, error)
      throw error
    }
  }
}

// 订单验证和辅助API
export const orderValidationApi = {
  /**
   * 验证订单数据
   * @param data 订单数据
   * @returns 验证结果
   */
  async validateOrder(data: Partial<CreateOrderRequest>): Promise<ApiResponse<any>> {
    try {
      const response = await request.post<ApiResponse<any>>('/api/admin/orders/validate', data)
      return response
    } catch (error) {
      console.error('验证订单数据失败:', error)
      throw error
    }
  },

  /**
   * 计算订单金额
   * @param data 订单数据
   * @returns 计算结果
   */
  async calculateOrderAmount(data: Partial<CreateOrderRequest>): Promise<ApiResponse<any>> {
    try {
      const response = await request.post<ApiResponse<any>>('/api/admin/orders/calculate', data)
      return response
    } catch (error) {
      console.error('计算订单金额失败:', error)
      throw error
    }
  },

  /**
   * 获取订单预览
   * @param data 订单数据
   * @returns 订单预览
   */
  async previewOrder(data: CreateOrderRequest): Promise<ApiResponse<any>> {
    try {
      const response = await request.post<ApiResponse<any>>('/api/admin/orders/preview', data)
      return response
    } catch (error) {
      console.error('获取订单预览失败:', error)
      throw error
    }
  }
}

// 错误处理和重试机制
class OrderApiError extends Error {
  constructor(
    message: string,
    public code?: number,
    public details?: any
  ) {
    super(message)
    this.name = 'OrderApiError'
  }
}

/**
 * API调用重试装饰器
 * @param retries 重试次数
 * @param delay 重试延迟（毫秒）
 */
function withRetry<T extends (...args: any[]) => Promise<any>>(
  fn: T,
  retries: number = 3,
  delay: number = 1000
): T {
  return (async (...args: Parameters<T>) => {
    let lastError: Error
    
    for (let i = 0; i <= retries; i++) {
      try {
        return await fn(...args)
      } catch (error) {
        lastError = error as Error
        
        // 如果是最后一次重试，直接抛出错误
        if (i === retries) {
          throw new OrderApiError(
            `API调用失败，已重试${retries}次: ${lastError.message}`,
            (error as any)?.response?.status,
            error
          )
        }
        
        // 等待后重试
        await new Promise(resolve => setTimeout(resolve, delay * (i + 1)))
        console.warn(`API调用失败，正在进行第${i + 1}次重试...`, error)
      }
    }
    
    throw lastError!
  }) as T
}

// 带重试机制的API方法
export const orderApiWithRetry = {
  getOrders: withRetry(orderApi.getOrders),
  getOrder: withRetry(orderApi.getOrder),
  createOrder: withRetry(orderApi.createOrder),
  updateOrder: withRetry(orderApi.updateOrder),
  deleteOrder: withRetry(orderApi.deleteOrder),
  getOrderStats: withRetry(orderApi.getOrderStats),
  updateOrderStatus: withRetry(orderStatusApi.updateOrderStatus),
  batchUpdateOrderStatus: withRetry(orderStatusApi.batchUpdateOrderStatus),
  batchDeleteOrders: withRetry(orderStatusApi.batchDeleteOrders)
}

// 统一导出所有API
export const allOrderApis = {
  ...orderApi,
  ...orderStatusApi,
  ...orderSearchApi,
  ...orderExtensionApi,
  ...orderValidationApi
}

// 默认导出主要的订单API
export default orderApi

// 导出错误类型
export { OrderApiError }