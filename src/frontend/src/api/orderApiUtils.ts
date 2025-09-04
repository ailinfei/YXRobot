/**
 * 订单API工具函数
 * 任务19：实现前端API调用服务 - 工具函数和辅助方法
 */

import type { 
  Order, 
  OrderQueryParams, 
  CreateOrderRequest,
  OrderStats,
  OrderItem
} from '@/types/order'

/**
 * 订单查询参数构建器
 */
export class OrderQueryBuilder {
  private params: OrderQueryParams = {}

  /**
   * 设置分页参数
   */
  page(page: number, pageSize: number = 10): this {
    this.params.page = page
    this.params.pageSize = pageSize
    return this
  }

  /**
   * 设置搜索关键词
   */
  keyword(keyword: string): this {
    if (keyword.trim()) {
      this.params.keyword = keyword.trim()
    }
    return this
  }

  /**
   * 设置订单类型筛选
   */
  type(type: 'sales' | 'rental'): this {
    this.params.type = type
    return this
  }

  /**
   * 设置订单状态筛选
   */
  status(status: Order['status']): this {
    this.params.status = status
    return this
  }

  /**
   * 设置日期范围筛选
   */
  dateRange(startDate: string, endDate: string): this {
    this.params.dateRange = [startDate, endDate]
    return this
  }

  /**
   * 设置客户筛选
   */
  customer(customerId: string): this {
    this.params.customerId = customerId
    return this
  }

  /**
   * 设置销售人员筛选
   */
  salesPerson(salesPerson: string): this {
    this.params.salesPerson = salesPerson
    return this
  }

  /**
   * 构建查询参数
   */
  build(): OrderQueryParams {
    return { ...this.params }
  }

  /**
   * 重置参数
   */
  reset(): this {
    this.params = {}
    return this
  }
}

/**
 * 订单数据验证器
 */
export class OrderValidator {
  /**
   * 验证订单创建数据
   */
  static validateCreateOrder(data: CreateOrderRequest): { isValid: boolean; errors: string[] } {
    const errors: string[] = []

    // 验证客户信息
    if (!data.customerName?.trim()) {
      errors.push('客户姓名不能为空')
    }

    if (!data.customerPhone?.trim()) {
      errors.push('客户电话不能为空')
    } else if (!/^1[3-9]\d{9}$/.test(data.customerPhone)) {
      errors.push('客户电话格式不正确')
    }

    if (data.customerEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.customerEmail)) {
      errors.push('客户邮箱格式不正确')
    }

    if (!data.deliveryAddress?.trim()) {
      errors.push('收货地址不能为空')
    }

    // 验证商品信息
    if (!data.items || data.items.length === 0) {
      errors.push('订单商品不能为空')
    } else {
      data.items.forEach((item, index) => {
        if (!item.productId) {
          errors.push(`第${index + 1}个商品ID不能为空`)
        }
        if (!item.productName?.trim()) {
          errors.push(`第${index + 1}个商品名称不能为空`)
        }
        if (!item.quantity || item.quantity <= 0) {
          errors.push(`第${index + 1}个商品数量必须大于0`)
        }
        if (!item.unitPrice || item.unitPrice <= 0) {
          errors.push(`第${index + 1}个商品单价必须大于0`)
        }
        if (item.totalPrice !== item.quantity * item.unitPrice) {
          errors.push(`第${index + 1}个商品总价计算错误`)
        }
      })
    }

    // 验证金额信息
    if (!data.totalAmount || data.totalAmount <= 0) {
      errors.push('订单总金额必须大于0')
    }

    // 验证租赁订单特有字段
    if (data.type === 'rental') {
      if (!data.rentalStartDate) {
        errors.push('租赁开始日期不能为空')
      }
      if (!data.rentalEndDate) {
        errors.push('租赁结束日期不能为空')
      }
      if (data.rentalStartDate && data.rentalEndDate) {
        const startDate = new Date(data.rentalStartDate)
        const endDate = new Date(data.rentalEndDate)
        if (startDate >= endDate) {
          errors.push('租赁结束日期必须晚于开始日期')
        }
      }
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  /**
   * 验证订单状态转换是否合法
   */
  static validateStatusTransition(currentStatus: Order['status'], newStatus: Order['status']): boolean {
    const validTransitions: Record<Order['status'], Order['status'][]> = {
      'pending': ['confirmed', 'cancelled'],
      'confirmed': ['processing', 'cancelled'],
      'processing': ['shipped', 'cancelled'],
      'shipped': ['delivered', 'cancelled'],
      'delivered': ['completed'],
      'completed': [],
      'cancelled': []
    }

    return validTransitions[currentStatus]?.includes(newStatus) || false
  }
}

/**
 * 订单数据格式化器
 */
export class OrderFormatter {
  /**
   * 格式化订单状态显示文本
   */
  static formatStatus(status: Order['status']): string {
    const statusMap: Record<Order['status'], string> = {
      'pending': '待确认',
      'confirmed': '已确认',
      'processing': '处理中',
      'shipped': '已发货',
      'delivered': '已送达',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    return statusMap[status] || status
  }

  /**
   * 格式化订单类型显示文本
   */
  static formatType(type: 'sales' | 'rental'): string {
    return type === 'sales' ? '销售订单' : '租赁订单'
  }

  /**
   * 格式化金额显示
   */
  static formatAmount(amount: number, currency: string = 'CNY'): string {
    const currencySymbol = currency === 'CNY' ? '¥' : '$'
    return `${currencySymbol}${amount.toLocaleString()}`
  }

  /**
   * 格式化日期显示
   */
  static formatDate(date: string | Date): string {
    const d = new Date(date)
    return d.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  /**
   * 格式化订单编号
   */
  static formatOrderNumber(orderNumber: string): string {
    // 如果订单编号太长，显示省略号
    if (orderNumber.length > 20) {
      return `${orderNumber.substring(0, 10)}...${orderNumber.substring(orderNumber.length - 6)}`
    }
    return orderNumber
  }

  /**
   * 格式化客户信息显示
   */
  static formatCustomerInfo(order: Order): string {
    const parts = [order.customerName]
    if (order.customerPhone) {
      parts.push(order.customerPhone)
    }
    return parts.join(' - ')
  }
}

/**
 * 订单统计计算器
 */
export class OrderStatsCalculator {
  /**
   * 计算订单完成率
   */
  static calculateCompletionRate(stats: OrderStats): number {
    if (stats.total === 0) return 0
    return Math.round((stats.completed / stats.total) * 100)
  }

  /**
   * 计算订单取消率
   */
  static calculateCancellationRate(stats: OrderStats): number {
    if (stats.total === 0) return 0
    return Math.round((stats.cancelled / stats.total) * 100)
  }

  /**
   * 计算处理中订单占比
   */
  static calculateProcessingRate(stats: OrderStats): number {
    if (stats.total === 0) return 0
    return Math.round((stats.processing / stats.total) * 100)
  }

  /**
   * 计算销售订单占比
   */
  static calculateSalesOrderRate(stats: OrderStats): number {
    if (stats.total === 0) return 0
    return Math.round((stats.salesOrders / stats.total) * 100)
  }

  /**
   * 计算租赁订单占比
   */
  static calculateRentalOrderRate(stats: OrderStats): number {
    if (stats.total === 0) return 0
    return Math.round((stats.rentalOrders / stats.total) * 100)
  }
}

/**
 * 订单数据转换器
 */
export class OrderDataConverter {
  /**
   * 将订单数据转换为表格显示格式
   */
  static toTableData(orders: Order[]): any[] {
    return orders.map(order => ({
      id: order.id,
      orderNumber: order.orderNumber,
      type: OrderFormatter.formatType(order.type),
      status: OrderFormatter.formatStatus(order.status),
      customerInfo: OrderFormatter.formatCustomerInfo(order),
      totalAmount: OrderFormatter.formatAmount(order.totalAmount),
      createdAt: OrderFormatter.formatDate(order.createdAt),
      itemCount: order.items?.length || 0,
      raw: order // 保留原始数据
    }))
  }

  /**
   * 将订单数据转换为导出格式
   */
  static toExportData(orders: Order[]): any[] {
    return orders.map(order => ({
      '订单编号': order.orderNumber,
      '订单类型': OrderFormatter.formatType(order.type),
      '订单状态': OrderFormatter.formatStatus(order.status),
      '客户姓名': order.customerName,
      '客户电话': order.customerPhone,
      '客户邮箱': order.customerEmail || '',
      '收货地址': order.deliveryAddress,
      '商品数量': order.items?.length || 0,
      '订单金额': order.totalAmount,
      '创建时间': OrderFormatter.formatDate(order.createdAt),
      '销售人员': order.salesPerson || '',
      '备注': order.notes || ''
    }))
  }

  /**
   * 将创建订单表单数据转换为API请求格式
   */
  static fromFormToCreateRequest(formData: any): CreateOrderRequest {
    return {
      type: formData.type,
      customerName: formData.customerName,
      customerPhone: formData.customerPhone,
      customerEmail: formData.customerEmail,
      deliveryAddress: formData.deliveryAddress,
      items: formData.items.map((item: any) => ({
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        totalPrice: item.totalPrice,
        notes: item.notes
      })),
      subtotal: formData.subtotal,
      shippingFee: formData.shippingFee || 0,
      discount: formData.discount || 0,
      totalAmount: formData.totalAmount,
      currency: formData.currency || 'CNY',
      expectedDeliveryDate: formData.expectedDeliveryDate,
      salesPerson: formData.salesPerson,
      notes: formData.notes,
      // 租赁相关字段
      rentalStartDate: formData.rentalStartDate,
      rentalEndDate: formData.rentalEndDate,
      rentalDays: formData.rentalDays,
      rentalNotes: formData.rentalNotes
    }
  }
}

/**
 * 订单缓存管理器
 */
export class OrderCacheManager {
  private static cache = new Map<string, { data: any; timestamp: number; ttl: number }>()

  /**
   * 设置缓存
   */
  static set(key: string, data: any, ttl: number = 5 * 60 * 1000): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl
    })
  }

  /**
   * 获取缓存
   */
  static get<T>(key: string): T | null {
    const item = this.cache.get(key)
    if (!item) return null

    const now = Date.now()
    if (now - item.timestamp > item.ttl) {
      this.cache.delete(key)
      return null
    }

    return item.data as T
  }

  /**
   * 删除缓存
   */
  static delete(key: string): void {
    this.cache.delete(key)
  }

  /**
   * 清空所有缓存
   */
  static clear(): void {
    this.cache.clear()
  }

  /**
   * 生成订单列表缓存键
   */
  static getOrderListCacheKey(params: OrderQueryParams): string {
    return `order_list_${JSON.stringify(params)}`
  }

  /**
   * 生成订单详情缓存键
   */
  static getOrderDetailCacheKey(id: string): string {
    return `order_detail_${id}`
  }

  /**
   * 生成订单统计缓存键
   */
  static getOrderStatsCacheKey(): string {
    return 'order_stats'
  }
}

// 导出所有工具类
export const orderApiUtils = {
  QueryBuilder: OrderQueryBuilder,
  Validator: OrderValidator,
  Formatter: OrderFormatter,
  StatsCalculator: OrderStatsCalculator,
  DataConverter: OrderDataConverter,
  CacheManager: OrderCacheManager
}