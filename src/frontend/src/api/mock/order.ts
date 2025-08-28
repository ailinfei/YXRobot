import type { Order, OrderQuery, OrderListResponse, OrderItem } from '@/types/order'

// Mock订单数据生成器
class MockOrderGenerator {
  private static orderCounter = 1000

  static generateOrderNumber(): string {
    const now = new Date()
    const timestamp = now.getTime().toString().slice(-8)
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
    return `ORD${timestamp}${random}`
  }

  static generateOrderItem(): OrderItem {
    const products = [
      { id: 'prod_001', name: '练字机器人 - 基础版', price: 2999 },
      { id: 'prod_002', name: '练字机器人 - 专业版', price: 4999 },
      { id: 'prod_003', name: '练字机器人 - 企业版', price: 7999 },
      { id: 'prod_004', name: '智能写字板', price: 599 },
      { id: 'prod_005', name: '专用墨水套装', price: 199 }
    ]
    
    const product = products[Math.floor(Math.random() * products.length)]
    const quantity = Math.floor(Math.random() * 5) + 1
    
    return {
      id: `item_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      productId: product.id,
      productName: product.name,
      quantity,
      unitPrice: product.price,
      totalPrice: product.price * quantity,
      notes: Math.random() > 0.7 ? '客户要求加急处理' : ''
    }
  }

  static generateOrder(): Order {
    const customers = [
      { name: '张三', phone: '13800138001', email: 'zhangsan@example.com', address: '北京市朝阳区建国路1号' },
      { name: '李四', phone: '13800138002', email: 'lisi@example.com', address: '上海市浦东新区陆家嘴路2号' },
      { name: '王五', phone: '13800138003', email: 'wangwu@example.com', address: '广州市天河区珠江路3号' },
      { name: '赵六', phone: '13800138004', email: 'zhaoliu@example.com', address: '深圳市南山区科技路4号' },
      { name: '钱七', phone: '13800138005', email: 'qianqi@example.com', address: '杭州市西湖区文三路5号' }
    ]
    
    const statuses: Order['status'][] = ['pending', 'confirmed', 'processing', 'shipped', 'delivered', 'completed', 'cancelled']
    const types: Order['type'][] = ['sales', 'rental']
    const paymentStatuses = ['pending', 'paid', 'failed', 'refunded']
    const salesPersons = ['张经理', '李经理', '王经理', '赵经理', '钱经理']
    
    const customer = customers[Math.floor(Math.random() * customers.length)]
    const type = types[Math.floor(Math.random() * types.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const itemCount = Math.floor(Math.random() * 3) + 1
    const items = Array.from({ length: itemCount }, () => this.generateOrderItem())
    
    const subtotal = items.reduce((sum, item) => sum + item.totalPrice, 0)
    const shippingFee = Math.floor(Math.random() * 100) + 50
    const discount = Math.floor(Math.random() * 200)
    const totalAmount = subtotal + shippingFee - discount
    
    const createdAt = new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
    
    const order: Order = {
      id: `order_${this.orderCounter++}`,
      orderNumber: this.generateOrderNumber(),
      type,
      status,
      customerId: `customer_${Math.floor(Math.random() * 1000)}`,
      customerName: customer.name,
      customerPhone: customer.phone,
      customerEmail: customer.email,
      deliveryAddress: customer.address,
      items,
      subtotal,
      shippingFee,
      discount,
      totalAmount,
      currency: 'CNY',
      paymentStatus: paymentStatuses[Math.floor(Math.random() * paymentStatuses.length)] as any,
      paymentMethod: Math.random() > 0.5 ? '微信支付' : '支付宝',
      paymentTime: status !== 'pending' ? createdAt : undefined,
      expectedDeliveryDate: new Date(Date.now() + Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      salesPerson: salesPersons[Math.floor(Math.random() * salesPersons.length)],
      notes: Math.random() > 0.6 ? '客户要求尽快发货，联系方式已确认' : '',
      createdAt,
      updatedAt: createdAt,
      createdBy: 'admin'
    }
    
    // 租赁订单特殊处理
    if (type === 'rental') {
      const startDate = new Date(Date.now() + Math.random() * 7 * 24 * 60 * 60 * 1000)
      const rentalDays = Math.floor(Math.random() * 30) + 7
      const endDate = new Date(startDate.getTime() + rentalDays * 24 * 60 * 60 * 1000)
      
      order.rentalStartDate = startDate.toISOString().split('T')[0]
      order.rentalEndDate = endDate.toISOString().split('T')[0]
      order.rentalDays = rentalDays
      order.rentalNotes = '租赁期间请妥善保管设备，如有损坏需要赔偿'
    }
    
    // 物流信息
    if (['shipped', 'delivered', 'completed'].includes(status)) {
      order.shippingInfo = {
        company: Math.random() > 0.5 ? '顺丰快递' : '京东物流',
        trackingNumber: `SF${Math.floor(Math.random() * 1000000000000)}`,
        shippedAt: new Date(new Date(createdAt).getTime() + 24 * 60 * 60 * 1000).toISOString(),
        deliveredAt: status === 'delivered' || status === 'completed' ? 
          new Date(new Date(createdAt).getTime() + 3 * 24 * 60 * 60 * 1000).toISOString() : undefined
      }
    }
    
    // 操作日志
    order.logs = [
      {
        id: 'log_1',
        action: '创建订单',
        operator: order.salesPerson || 'admin',
        notes: '订单创建成功',
        createdAt: order.createdAt
      }
    ]
    
    if (status !== 'pending') {
      order.logs.push({
        id: 'log_2',
        action: '确认订单',
        operator: order.salesPerson || 'admin',
        notes: '订单已确认，准备处理',
        createdAt: new Date(new Date(createdAt).getTime() + 2 * 60 * 60 * 1000).toISOString()
      })
    }
    
    if (['shipped', 'delivered', 'completed'].includes(status)) {
      order.logs.push({
        id: 'log_3',
        action: '发货',
        operator: '仓库管理员',
        notes: `已通过${order.shippingInfo?.company}发货`,
        createdAt: order.shippingInfo?.shippedAt || createdAt
      })
    }
    
    if (['delivered', 'completed'].includes(status)) {
      order.logs.push({
        id: 'log_4',
        action: '送达',
        operator: '系统',
        notes: '订单已送达客户',
        createdAt: order.shippingInfo?.deliveredAt || createdAt
      })
    }
    
    if (status === 'completed') {
      order.logs.push({
        id: 'log_5',
        action: '完成',
        operator: order.salesPerson || 'admin',
        notes: '订单已完成',
        createdAt: new Date(new Date(order.shippingInfo?.deliveredAt || createdAt).getTime() + 24 * 60 * 60 * 1000).toISOString()
      })
    }
    
    if (status === 'cancelled') {
      order.logs.push({
        id: 'log_cancel',
        action: '取消',
        operator: order.salesPerson || 'admin',
        notes: '订单已取消',
        createdAt: new Date(new Date(createdAt).getTime() + 30 * 60 * 1000).toISOString()
      })
    }
    
    return order
  }

  static generateOrders(count: number = 50): Order[] {
    return Array.from({ length: count }, () => this.generateOrder())
  }
}

// Mock订单API
export class MockOrderAPI {
  private static orders: Order[] = MockOrderGenerator.generateOrders(100)

  // 获取订单列表
  static async getOrders(query: OrderQuery = {}): Promise<{ data: OrderListResponse }> {
    await new Promise(resolve => setTimeout(resolve, 300)) // 模拟网络延迟
    
    let filteredOrders = [...this.orders]
    
    // 关键词搜索
    if (query.keyword) {
      const keyword = query.keyword.toLowerCase()
      filteredOrders = filteredOrders.filter(order =>
        order.orderNumber.toLowerCase().includes(keyword) ||
        order.customerName.toLowerCase().includes(keyword) ||
        order.customerPhone.includes(keyword)
      )
    }
    
    // 订单类型筛选
    if (query.type) {
      filteredOrders = filteredOrders.filter(order => order.type === query.type)
    }
    
    // 订单状态筛选
    if (query.status) {
      filteredOrders = filteredOrders.filter(order => order.status === query.status)
    }
    
    // 日期范围筛选
    if (query.dateRange) {
      const [startDate, endDate] = query.dateRange
      filteredOrders = filteredOrders.filter(order => {
        const orderDate = order.createdAt.split('T')[0]
        return orderDate >= startDate && orderDate <= endDate
      })
    }
    
    // 客户筛选
    if (query.customerId) {
      filteredOrders = filteredOrders.filter(order => order.customerId === query.customerId)
    }
    
    // 销售人员筛选
    if (query.salesPerson) {
      filteredOrders = filteredOrders.filter(order => order.salesPerson === query.salesPerson)
    }
    
    // 排序（按创建时间倒序）
    filteredOrders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    
    // 分页
    const page = query.page || 1
    const pageSize = query.pageSize || 10
    const startIndex = (page - 1) * pageSize
    const endIndex = startIndex + pageSize
    const paginatedOrders = filteredOrders.slice(startIndex, endIndex)
    
    // 统计数据
    const stats = {
      total: this.orders.length,
      pending: this.orders.filter(o => o.status === 'pending').length,
      processing: this.orders.filter(o => ['confirmed', 'processing', 'shipped'].includes(o.status)).length,
      completed: this.orders.filter(o => o.status === 'completed').length,
      cancelled: this.orders.filter(o => o.status === 'cancelled').length,
      totalRevenue: this.orders.filter(o => o.status === 'completed').reduce((sum, o) => sum + o.totalAmount, 0),
      averageOrderValue: this.orders.length > 0 ? this.orders.reduce((sum, o) => sum + o.totalAmount, 0) / this.orders.length : 0,
      salesOrders: this.orders.filter(o => o.type === 'sales').length,
      rentalOrders: this.orders.filter(o => o.type === 'rental').length
    }
    
    return {
      data: {
        list: paginatedOrders,
        total: filteredOrders.length,
        stats
      }
    }
  }

  // 获取订单详情
  static async getOrderById(id: string): Promise<{ data: Order }> {
    await new Promise(resolve => setTimeout(resolve, 200))
    
    const order = this.orders.find(o => o.id === id)
    if (!order) {
      throw new Error('订单不存在')
    }
    
    return { data: order }
  }

  // 创建订单
  static async createOrder(orderData: Partial<Order>): Promise<{ data: Order }> {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const newOrder: Order = {
      id: `order_${Date.now()}`,
      orderNumber: orderData.orderNumber || MockOrderGenerator.generateOrderNumber(),
      type: orderData.type || 'sales',
      status: 'pending',
      customerId: orderData.customerId || `customer_${Date.now()}`,
      customerName: orderData.customerName || '',
      customerPhone: orderData.customerPhone || '',
      customerEmail: orderData.customerEmail,
      deliveryAddress: orderData.deliveryAddress || '',
      items: orderData.items || [],
      subtotal: orderData.items?.reduce((sum, item) => sum + (item.totalPrice || 0), 0) || 0,
      shippingFee: orderData.shippingFee || 0,
      discount: orderData.discount || 0,
      totalAmount: orderData.totalAmount || 0,
      currency: 'CNY',
      paymentStatus: 'pending',
      expectedDeliveryDate: orderData.expectedDeliveryDate,
      salesPerson: orderData.salesPerson,
      notes: orderData.notes,
      rentalStartDate: orderData.rentalStartDate,
      rentalEndDate: orderData.rentalEndDate,
      rentalDays: orderData.rentalDays,
      rentalNotes: orderData.rentalNotes,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'admin',
      logs: [
        {
          id: 'log_1',
          action: '创建订单',
          operator: orderData.salesPerson || 'admin',
          notes: '订单创建成功',
          createdAt: new Date().toISOString()
        }
      ]
    }
    
    this.orders.unshift(newOrder)
    return { data: newOrder }
  }

  // 更新订单
  static async updateOrder(id: string, orderData: Partial<Order>): Promise<{ data: Order }> {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const index = this.orders.findIndex(o => o.id === id)
    if (index === -1) {
      throw new Error('订单不存在')
    }
    
    const updatedOrder = {
      ...this.orders[index],
      ...orderData,
      updatedAt: new Date().toISOString()
    }
    
    // 添加更新日志
    if (!updatedOrder.logs) updatedOrder.logs = []
    updatedOrder.logs.push({
      id: `log_${Date.now()}`,
      action: '更新订单',
      operator: 'admin',
      notes: '订单信息已更新',
      createdAt: new Date().toISOString()
    })
    
    this.orders[index] = updatedOrder
    return { data: updatedOrder }
  }

  // 更新订单状态
  static async updateOrderStatus(id: string, status: Order['status']): Promise<{ data: Order }> {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const index = this.orders.findIndex(o => o.id === id)
    if (index === -1) {
      throw new Error('订单不存在')
    }
    
    const order = this.orders[index]
    order.status = status
    order.updatedAt = new Date().toISOString()
    
    // 添加状态变更日志
    if (!order.logs) order.logs = []
    const statusTexts = {
      pending: '待确认',
      confirmed: '已确认',
      processing: '处理中',
      shipped: '已发货',
      delivered: '已送达',
      completed: '已完成',
      cancelled: '已取消'
    }
    
    order.logs.push({
      id: `log_${Date.now()}`,
      action: statusTexts[status],
      operator: 'admin',
      notes: `订单状态更新为：${statusTexts[status]}`,
      createdAt: new Date().toISOString()
    })
    
    // 根据状态更新相关信息
    if (status === 'shipped' && !order.shippingInfo) {
      order.shippingInfo = {
        company: '顺丰快递',
        trackingNumber: `SF${Math.floor(Math.random() * 1000000000000)}`,
        shippedAt: new Date().toISOString()
      }
    }
    
    if (status === 'delivered' && order.shippingInfo) {
      order.shippingInfo.deliveredAt = new Date().toISOString()
    }
    
    if (status === 'completed') {
      order.paymentStatus = 'paid'
      if (!order.paymentTime) {
        order.paymentTime = new Date().toISOString()
      }
    }
    
    return { data: order }
  }

  // 批量更新订单状态
  static async batchUpdateOrderStatus(ids: string[], status: Order['status']): Promise<{ data: Order[] }> {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const updatedOrders: Order[] = []
    
    for (const id of ids) {
      const result = await this.updateOrderStatus(id, status)
      updatedOrders.push(result.data)
    }
    
    return { data: updatedOrders }
  }

  // 删除订单
  static async deleteOrder(id: string): Promise<{ data: boolean }> {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const index = this.orders.findIndex(o => o.id === id)
    if (index === -1) {
      throw new Error('订单不存在')
    }
    
    this.orders.splice(index, 1)
    return { data: true }
  }

  // 批量删除订单
  static async batchDeleteOrders(ids: string[]): Promise<{ data: boolean }> {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    this.orders = this.orders.filter(order => !ids.includes(order.id))
    return { data: true }
  }

  // 导出订单
  static async exportOrders(query: OrderQuery = {}): Promise<{ data: boolean }> {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 这里可以实现实际的导出逻辑
    console.log('导出订单:', query)
    return { data: true }
  }
}

export const mockOrderAPI = MockOrderAPI