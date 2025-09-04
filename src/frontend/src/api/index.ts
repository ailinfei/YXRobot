/**
 * API统一导出文件
 * 任务19：实现前端API调用服务 - 统一API导出
 */

// 订单相关API
export {
  orderApi,
  orderStatusApi,
  orderSearchApi,
  orderExtensionApi,
  orderValidationApi,
  orderApiWithRetry,
  allOrderApis,
  OrderApiError
} from './order'

// 客户相关API
export { customerApi } from './customer'

// 产品相关API
export { productApi } from './product'

// API配置和错误处理
export {
  orderApiConfig,
  OrderApiErrorHandler,
  OrderApiErrorCode,
  getFriendlyErrorMessage,
  LoadingManager,
  ApiPerformanceMonitor
} from './orderApiConfig'

// API使用示例
export { orderApiExamples } from './orderApiExamples'

// API工具函数
export {
  orderApiUtils,
  OrderQueryBuilder,
  OrderValidator,
  OrderFormatter,
  OrderStatsCalculator,
  OrderDataConverter,
  OrderCacheManager
} from './orderApiUtils'

// API测试工具
export {
  orderApiTest,
  OrderApiTestSuite,
  OrderApiPerformanceTest,
  orderApiTestSuite
} from './orderApiTest'

// 默认导出主要API
export default {
  order: orderApi,
  customer: customerApi,
  product: productApi
}