/**
 * 销售数据模块 API 调用服务
 * 与后端SalesController接口对应
 */

import request from '@/utils/request'
import type {
  SalesRecord,
  Customer,
  SalesProduct,
  SalesStaff,
  SalesStats,
  SalesChartData,
  SalesRecordQuery,
  CustomerQuery,
  ProductQuery,
  StaffQuery,
  SalesRecordForm,
  ApiResponse,
  PageResponse,
  BatchOperation
} from '@/types/sales'

// 销售记录相关API
export const salesRecordApi = {
  // 获取销售记录列表
  getList(params: SalesRecordQuery): Promise<ApiResponse<PageResponse<SalesRecord>>> {
    return request.get('/api/sales/records', params)
  },

  // 获取销售记录详情
  getById(id: number): Promise<ApiResponse<SalesRecord>> {
    return request.get(`/api/sales/records/${id}`)
  },

  // 创建销售记录
  create(data: SalesRecordForm): Promise<ApiResponse<SalesRecord>> {
    return request.post('/api/sales/records', data)
  },

  // 更新销售记录
  update(id: number, data: SalesRecordForm): Promise<ApiResponse<SalesRecord>> {
    return request.put(`/api/sales/records/${id}`, data)
  },

  // 删除销售记录
  delete(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/api/sales/records/${id}`)
  },

  // 批量操作销售记录
  batchOperation(data: BatchOperation): Promise<ApiResponse<void>> {
    return request.post('/api/sales/records/batch', data)
  }
}

// 统计分析相关API
export const salesStatsApi = {
  // 获取销售统计数据
  getStats(params?: {
    startDate?: string
    endDate?: string
    statType?: string
  }): Promise<ApiResponse<SalesStats>> {
    return request.get('/api/sales/stats', params)
  },

  // 获取销售趋势数据
  getTrends(params?: {
    startDate?: string
    endDate?: string
    groupBy?: string
  }): Promise<ApiResponse<SalesChartData>> {
    return request.get('/api/sales/trends', params)
  },

  // 获取产品销售排行
  getProductRanking(params?: {
    startDate?: string
    endDate?: string
    limit?: number
  }): Promise<ApiResponse<any[]>> {
    return request.get('/api/sales/product-ranking', params)
  },

  // 获取销售人员业绩排行
  getStaffPerformance(params?: {
    startDate?: string
    endDate?: string
    limit?: number
  }): Promise<ApiResponse<any[]>> {
    return request.get('/api/sales/staff-performance', params)
  },

  // 获取实时统计数据
  getRealTimeStats(): Promise<ApiResponse<any>> {
    return request.get('/api/sales/realtime-stats')
  },

  // 获取今日统计数据
  getTodayStats(): Promise<ApiResponse<any>> {
    return request.get('/api/sales/today-stats')
  },

  // 获取本月统计数据
  getCurrentMonthStats(): Promise<ApiResponse<any>> {
    return request.get('/api/sales/current-month-stats')
  }
}

// 图表数据相关API
export const salesChartsApi = {
  // 获取销售分布图表数据
  getDistribution(params?: {
    type?: string
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<SalesChartData>> {
    return request.get('/api/sales/charts/distribution', params)
  },

  // 获取月度销售图表数据
  getMonthlyData(params?: {
    year?: number
    months?: number
  }): Promise<ApiResponse<SalesChartData>> {
    return request.get('/api/sales/charts/monthly', params)
  },

  // 获取销售漏斗图表数据
  getFunnelData(params?: {
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<SalesChartData>> {
    return request.get('/api/sales/charts/funnel', params)
  },

  // 获取销售趋势图表数据
  getTrendChartData(params?: {
    startDate?: string
    endDate?: string
    groupBy?: string
  }): Promise<ApiResponse<SalesChartData>> {
    return request.get('/api/sales/charts/trends', params)
  }
}

// 高级搜索和筛选相关API
export const salesSearchApi = {
  // 获取搜索建议
  getSearchSuggestions(keyword: string): Promise<ApiResponse<any[]>> {
    return request.get('/api/sales/search-suggestions', { keyword })
  },

  // 获取筛选选项
  getFilterOptions(): Promise<ApiResponse<{
    regions: any[]
    channels: any[]
    paymentMethods: any[]
    amountRanges: any[]
    statusOptions: any[]
    paymentStatusOptions: any[]
  }>> {
    return request.get('/api/sales/filter-options')
  },

  // 快速筛选 - 今日订单
  getTodayOrders(): Promise<ApiResponse<SalesRecord[]>> {
    return request.get('/api/sales/quick-filter/today')
  },

  // 快速筛选 - 本周订单
  getThisWeekOrders(): Promise<ApiResponse<SalesRecord[]>> {
    return request.get('/api/sales/quick-filter/this-week')
  },

  // 快速筛选 - 本月订单
  getThisMonthOrders(): Promise<ApiResponse<SalesRecord[]>> {
    return request.get('/api/sales/quick-filter/this-month')
  },

  // 快速筛选 - 待处理订单
  getPendingOrders(): Promise<ApiResponse<SalesRecord[]>> {
    return request.get('/api/sales/quick-filter/pending')
  },

  // 快速筛选 - 高价值订单
  getHighValueOrders(minAmount?: number): Promise<ApiResponse<SalesRecord[]>> {
    return request.get('/api/sales/quick-filter/high-value', 
      minAmount ? { minAmount } : undefined 
    )
  },

  // 优化的销售记录查询
  getRecordsOptimized(params: SalesRecordQuery): Promise<ApiResponse<PageResponse<SalesRecord>>> {
    return request.get('/api/sales/records-optimized', params)
  }
}

// 辅助功能相关API
export const salesAuxiliaryApi = {
  // 获取客户列表
  getCustomers(params: CustomerQuery): Promise<ApiResponse<PageResponse<Customer>>> {
    return request.get('/api/sales/customers', params)
  },

  // 获取活跃客户列表（用于下拉选择）
  getActiveCustomers(): Promise<ApiResponse<Customer[]>> {
    return request.get('/api/sales/customers/active')
  },

  // 获取产品列表
  getProducts(params: ProductQuery): Promise<ApiResponse<PageResponse<SalesProduct>>> {
    return request.get('/api/sales/products', params)
  },

  // 获取启用产品列表（用于下拉选择）
  getActiveProducts(): Promise<ApiResponse<SalesProduct[]>> {
    return request.get('/api/sales/products/active')
  },

  // 获取销售人员列表
  getStaff(params: StaffQuery): Promise<ApiResponse<PageResponse<SalesStaff>>> {
    return request.get('/api/sales/staff', params)
  },

  // 获取在职销售人员列表（用于下拉选择）
  getActiveStaff(): Promise<ApiResponse<SalesStaff[]>> {
    return request.get('/api/sales/staff/active')
  },

  // 批量操作
  batchOperation(data: BatchOperation & { entityType: string }): Promise<ApiResponse<void>> {
    return request.post('/api/sales/batch', data)
  }
}

// 导出所有API
export const salesApi = {
  records: salesRecordApi,
  stats: salesStatsApi,
  charts: salesChartsApi,
  search: salesSearchApi,
  auxiliary: salesAuxiliaryApi
}

export default salesApi