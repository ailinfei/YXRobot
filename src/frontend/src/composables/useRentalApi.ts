/**
 * 租赁API状态管理Composable
 * 提供响应式的API调用状态管理
 * 
 * @author Kiro
 * @date 2025-01-28
 */

import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { rentalService } from '@/services/rentalService'
import type {
  RentalStats,
  RentalTrendData,
  DeviceUtilizationData,
  RentalRevenueAnalysis,
  TodayStats,
  DeviceStatusStats,
  RentalRecord,
  RentalDevice,
  RentalCustomer,
  DeviceQueryParams,
  RentalRecordQueryParams,
  TrendQueryParams,
  RevenueAnalysisParams,
  ApiError
} from '@/types/rental'
import type { PageResponse } from '@/types'

/**
 * API加载状态接口
 */
interface ApiState {
  loading: boolean
  error: ApiError | null
  lastUpdated: number | null
}

/**
 * 租赁API状态管理Hook
 */
export function useRentalApi() {
  // ==================== 响应式状态 ====================
  
  // 统计数据状态
  const statsState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const todayStatsState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const deviceStatusStatsState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  // 图表数据状态
  const trendDataState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const deviceDataState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const revenueAnalysisState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  // 列表数据状态
  const ordersState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const devicesState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })
  
  const customersState = reactive<ApiState>({
    loading: false,
    error: null,
    lastUpdated: null
  })

  // ==================== 数据存储 ====================
  
  const rentalStats = ref<RentalStats | null>(null)
  const todayStats = ref<TodayStats | null>(null)
  const deviceStatusStats = ref<DeviceStatusStats | null>(null)
  const topDevices = ref<DeviceUtilizationData[]>([])
  
  const trendData = ref<RentalTrendData[]>([])
  const deviceData = ref<DeviceUtilizationData[]>([])
  const revenueAnalysis = ref<RentalRevenueAnalysis | null>(null)
  
  const orders = ref<PageResponse<RentalRecord> | null>(null)
  const devices = ref<PageResponse<RentalDevice> | null>(null)
  const customers = ref<PageResponse<RentalCustomer> | null>(null)

  // ==================== 计算属性 ====================
  
  const isAnyLoading = computed(() => {
    return statsState.loading || 
           todayStatsState.loading || 
           deviceStatusStatsState.loading ||
           trendDataState.loading || 
           deviceDataState.loading || 
           revenueAnalysisState.loading ||
           ordersState.loading || 
           devicesState.loading || 
           customersState.loading
  })
  
  const hasAnyError = computed(() => {
    return statsState.error || 
           todayStatsState.error || 
           deviceStatusStatsState.error ||
           trendDataState.error || 
           deviceDataState.error || 
           revenueAnalysisState.error ||
           ordersState.error || 
           devicesState.error || 
           customersState.error
  })

  // ==================== 工具函数 ====================
  
  /**
   * 通用API调用包装器
   */
  async function withApiCall<T>(
    state: ApiState,
    apiCall: () => Promise<T>,
    onSuccess?: (data: T) => void,
    showErrorMessage: boolean = true
  ): Promise<T | null> {
    state.loading = true
    state.error = null
    
    try {
      const data = await apiCall()
      state.lastUpdated = Date.now()
      onSuccess?.(data)
      return data
    } catch (error: any) {
      state.error = error
      if (showErrorMessage) {
        ElMessage.error(error.message || 'API调用失败')
      }
      console.error('API调用失败:', error)
      return null
    } finally {
      state.loading = false
    }
  }

  // ==================== 统计数据API ====================
  
  /**
   * 获取租赁统计数据
   */
  async function fetchRentalStats(showError: boolean = true) {
    return withApiCall(
      statsState,
      () => rentalService.getRentalStats(),
      (data) => { rentalStats.value = data },
      showError
    )
  }
  
  /**
   * 获取今日统计数据
   */
  async function fetchTodayStats(showError: boolean = true) {
    return withApiCall(
      todayStatsState,
      () => rentalService.getTodayStats(),
      (data) => { todayStats.value = data },
      showError
    )
  }
  
  /**
   * 获取设备状态统计
   */
  async function fetchDeviceStatusStats(showError: boolean = true) {
    return withApiCall(
      deviceStatusStatsState,
      () => rentalService.getDeviceStatusStats(),
      (data) => { deviceStatusStats.value = data },
      showError
    )
  }
  
  /**
   * 获取TOP设备排行
   */
  async function fetchTopDevices(limit: number = 5, showError: boolean = true) {
    return withApiCall(
      deviceStatusStatsState, // 复用状态
      () => rentalService.getTopDevices(limit),
      (data) => { topDevices.value = data },
      showError
    )
  }  
// ==================== 图表数据API ====================
  
  /**
   * 获取租赁趋势数据
   */
  async function fetchTrendData(params: TrendQueryParams, showError: boolean = true) {
    return withApiCall(
      trendDataState,
      () => rentalService.getRentalTrendData(params),
      (data) => { trendData.value = data },
      showError
    )
  }
  
  /**
   * 获取设备利用率数据
   */
  async function fetchDeviceData(params?: DeviceQueryParams, showError: boolean = true) {
    return withApiCall(
      deviceDataState,
      () => rentalService.getDeviceUtilizationData(params),
      (data) => { deviceData.value = data },
      showError
    )
  }
  
  /**
   * 获取收入分析数据
   */
  async function fetchRevenueAnalysis(params: RevenueAnalysisParams, showError: boolean = true) {
    return withApiCall(
      revenueAnalysisState,
      () => rentalService.getRentalRevenueAnalysis(params),
      (data) => { revenueAnalysis.value = data },
      showError
    )
  }

  // ==================== 列表数据API ====================
  
  /**
   * 获取租赁订单列表
   */
  async function fetchOrders(params: RentalRecordQueryParams, showError: boolean = true) {
    return withApiCall(
      ordersState,
      () => rentalService.getRentalOrders(params),
      (data) => { orders.value = data },
      showError
    )
  }
  
  /**
   * 获取设备列表
   */
  async function fetchDevices(params?: DeviceQueryParams, showError: boolean = true) {
    return withApiCall(
      devicesState,
      () => rentalService.getRentalDevices(params),
      (data) => { devices.value = data },
      showError
    )
  }
  
  /**
   * 获取客户列表
   */
  async function fetchCustomers(params?: {
    page?: number
    pageSize?: number
    keyword?: string
    customerType?: string
    region?: string
  }, showError: boolean = true) {
    return withApiCall(
      customersState,
      () => rentalService.getRentalCustomers(params),
      (data) => { customers.value = data },
      showError
    )
  }

  // ==================== 批量操作 ====================
  
  /**
   * 加载仪表板数据
   */
  async function loadDashboardData() {
    try {
      const data = await rentalService.loadDashboardData()
      
      rentalStats.value = data.stats
      todayStats.value = data.todayStats
      deviceStatusStats.value = data.deviceStatusStats
      topDevices.value = data.topDevices
      
      // 更新状态
      statsState.lastUpdated = Date.now()
      todayStatsState.lastUpdated = Date.now()
      deviceStatusStatsState.lastUpdated = Date.now()
      
      return data
    } catch (error: any) {
      ElMessage.error('仪表板数据加载失败')
      console.error('仪表板数据加载失败:', error)
      return null
    }
  }
  
  /**
   * 加载图表数据
   */
  async function loadChartData(period: 'daily' | 'weekly' | 'monthly' | 'quarterly' | 'yearly' = 'monthly') {
    try {
      const data = await rentalService.loadChartData(period)
      
      trendData.value = data.trendData
      deviceData.value = data.deviceData
      revenueAnalysis.value = data.revenueAnalysis
      
      // 更新状态
      trendDataState.lastUpdated = Date.now()
      deviceDataState.lastUpdated = Date.now()
      revenueAnalysisState.lastUpdated = Date.now()
      
      return data
    } catch (error: any) {
      ElMessage.error('图表数据加载失败')
      console.error('图表数据加载失败:', error)
      return null
    }
  }

  // ==================== 缓存管理 ====================
  
  /**
   * 刷新所有数据
   */
  async function refreshAllData() {
    rentalService.clearCache()
    await Promise.all([
      loadDashboardData(),
      loadChartData()
    ])
  }
  
  /**
   * 清除错误状态
   */
  function clearErrors() {
    statsState.error = null
    todayStatsState.error = null
    deviceStatusStatsState.error = null
    trendDataState.error = null
    deviceDataState.error = null
    revenueAnalysisState.error = null
    ordersState.error = null
    devicesState.error = null
    customersState.error = null
  }

  // ==================== 返回接口 ====================
  
  return {
    // 状态
    statsState,
    todayStatsState,
    deviceStatusStatsState,
    trendDataState,
    deviceDataState,
    revenueAnalysisState,
    ordersState,
    devicesState,
    customersState,
    
    // 计算属性
    isAnyLoading,
    hasAnyError,
    
    // 数据
    rentalStats,
    todayStats,
    deviceStatusStats,
    topDevices,
    trendData,
    deviceData,
    revenueAnalysis,
    orders,
    devices,
    customers,
    
    // 方法
    fetchRentalStats,
    fetchTodayStats,
    fetchDeviceStatusStats,
    fetchTopDevices,
    fetchTrendData,
    fetchDeviceData,
    fetchRevenueAnalysis,
    fetchOrders,
    fetchDevices,
    fetchCustomers,
    loadDashboardData,
    loadChartData,
    refreshAllData,
    clearErrors
  }
}