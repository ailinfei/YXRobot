/**
 * 订单API使用示例
 * 任务19：实现前端API调用服务 - 使用示例和最佳实践
 */

import { orderApi, orderStatusApi, orderSearchApi, orderExtensionApi, orderApiWithRetry } from './order'
import type {
    CreateOrderRequest,
    UpdateOrderRequest,
    OrderQueryParams
} from '@/types/order'

/**
 * 订单列表查询示例
 */
export async function getOrderListExample() {
    try {
        // 基础查询
        const basicResponse = await orderApi.getOrders()
        console.log('基础订单列表:', basicResponse.data)

        // 带参数查询
        const params: OrderQueryParams = {
            page: 1,
            pageSize: 10,
            keyword: '客户名称',
            type: 'sales',
            status: 'pending',
            dateRange: ['2024-01-01', '2024-12-31']
        }

        const filteredResponse = await orderApi.getOrders(params)
        console.log('筛选后的订单列表:', filteredResponse.data)

        return filteredResponse.data
    } catch (error) {
        console.error('获取订单列表失败:', error)
        throw error
    }
}

/**
 * 订单创建示例
 */
export async function createOrderExample() {
    try {
        const orderData: CreateOrderRequest = {
            type: 'sales',
            customerName: '张三',
            customerPhone: '13800138000',
            customerEmail: 'zhangsan@example.com',
            deliveryAddress: '北京市朝阳区xxx街道xxx号',
            items: [
                {
                    productId: 'prod-001',
                    productName: '练字机器人 Pro',
                    quantity: 1,
                    unitPrice: 2999,
                    totalPrice: 2999,
                    notes: '客户指定颜色：白色'
                }
            ],
            subtotal: 2999,
            shippingFee: 50,
            discount: 100,
            totalAmount: 2949,
            currency: 'CNY',
            expectedDeliveryDate: '2024-02-01',
            salesPerson: '李销售',
            notes: '客户要求尽快发货'
        }

        const response = await orderApi.createOrder(orderData)
        console.log('创建的订单:', response.data)

        return response.data
    } catch (error) {
        console.error('创建订单失败:', error)
        throw error
    }
}

/**
 * 订单更新示例
 */
export async function updateOrderExample(orderId: string) {
    try {
        const updateData: UpdateOrderRequest = {
            customerPhone: '13900139000',
            deliveryAddress: '更新后的收货地址',
            notes: '客户要求修改收货地址'
        }

        const response = await orderApi.updateOrder(orderId, updateData)
        console.log('更新后的订单:', response.data)

        return response.data
    } catch (error) {
        console.error('更新订单失败:', error)
        throw error
    }
}

/**
 * 订单状态管理示例
 */
export async function orderStatusManagementExample(orderId: string) {
    try {
        // 单个订单状态更新
        await orderStatusApi.updateOrderStatus(orderId, 'confirmed', '订单已确认，准备发货')
        console.log('订单状态已更新为已确认')

        // 批量状态更新
        const orderIds = ['order-001', 'order-002', 'order-003']
        await orderStatusApi.batchUpdateOrderStatus(orderIds, 'shipped', '批量发货')
        console.log('批量更新订单状态完成')

    } catch (error) {
        console.error('订单状态管理失败:', error)
        throw error
    }
}

/**
 * 订单搜索示例
 */
export async function orderSearchExample() {
    try {
        // 搜索建议
        const suggestions = await orderSearchApi.searchOrderSuggestions('张三', 5)
        console.log('搜索建议:', suggestions.data)

        // 获取筛选选项
        const filterOptions = await orderSearchApi.getOrderFilterOptions()
        console.log('筛选选项:', filterOptions.data)

        // 高级搜索
        const searchParams: OrderQueryParams = {
            keyword: '张三',
            type: 'sales',
            status: 'completed',
            dateRange: ['2024-01-01', '2024-12-31']
        }

        const searchResults = await orderSearchApi.advancedSearchOrders(searchParams)
        console.log('高级搜索结果:', searchResults.data)

    } catch (error) {
        console.error('订单搜索失败:', error)
        throw error
    }
}

/**
 * 订单统计示例
 */
export async function orderStatsExample() {
    try {
        const stats = await orderApi.getOrderStats()
        console.log('订单统计数据:', stats.data)

        // 可以根据统计数据更新前端图表
        const {
            total,
            pending,
            processing,
            completed,
            totalRevenue,
            averageOrderValue
        } = stats.data

        console.log(`总订单数: ${total}`)
        console.log(`待处理: ${pending}, 处理中: ${processing}, 已完成: ${completed}`)
        console.log(`总收入: ¥${totalRevenue.toLocaleString()}`)
        console.log(`平均订单价值: ¥${averageOrderValue.toFixed(2)}`)

        return stats.data
    } catch (error) {
        console.error('获取订单统计失败:', error)
        throw error
    }
}

/**
 * 订单导出示例
 */
export async function exportOrdersExample() {
    try {
        const exportParams = {
            format: 'excel' as const,
            dateRange: ['2024-01-01', '2024-12-31'] as [string, string],
            status: 'completed' as const,
            includeItems: true,
            includeCustomerInfo: true
        }

        const blob = await orderExtensionApi.exportOrders(exportParams)

        // 创建下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `订单数据_${new Date().toISOString().split('T')[0]}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        console.log('订单导出完成')
    } catch (error) {
        console.error('导出订单失败:', error)
        throw error
    }
}

/**
 * 带重试机制的API调用示例
 */
export async function apiWithRetryExample() {
    try {
        // 使用带重试机制的API
        const response = await orderApiWithRetry.getOrders({
            page: 1,
            pageSize: 10
        })

        console.log('带重试机制的API调用成功:', response.data)
        return response.data
    } catch (error) {
        console.error('即使重试也失败了:', error)
        throw error
    }
}

/**
 * 错误处理示例
 */
export async function errorHandlingExample() {
    try {
        // 尝试获取不存在的订单
        await orderApi.getOrder('non-existent-id')
    } catch (error: any) {
        if (error.response?.status === 404) {
            console.log('订单不存在，显示友好提示')
            // 可以显示空状态页面或提示用户
        } else if (error.response?.status >= 500) {
            console.log('服务器错误，稍后重试')
            // 可以显示重试按钮
        } else {
            console.log('其他错误:', error.message)
        }
    }
}

/**
 * 组合API调用示例 - 获取订单完整信息
 */
export async function getCompleteOrderInfoExample(orderId: string) {
    try {
        // 并行获取订单基本信息和扩展信息
        const [orderResponse, extendedResponse, logsResponse] = await Promise.all([
            orderApi.getOrder(orderId),
            orderExtensionApi.getOrderDetailExtended(orderId),
            orderExtensionApi.getOrderLogs(orderId)
        ])

        const completeOrderInfo = {
            basic: orderResponse.data,
            extended: extendedResponse.data,
            logs: logsResponse.data
        }

        console.log('完整订单信息:', completeOrderInfo)
        return completeOrderInfo
    } catch (error) {
        console.error('获取完整订单信息失败:', error)
        throw error
    }
}

/**
 * 订单工作流示例 - 从创建到完成的完整流程
 */
export async function orderWorkflowExample() {
    try {
        // 1. 创建订单
        const newOrder = await createOrderExample()
        const orderId = String(newOrder.id)

        // 2. 确认订单
        await orderStatusApi.updateOrderStatus(orderId, 'confirmed', '订单已确认')

        // 3. 处理订单
        await orderStatusApi.updateOrderStatus(orderId, 'processing', '开始处理订单')

        // 4. 发货
        await orderStatusApi.updateOrderStatus(orderId, 'shipped', '订单已发货')

        // 5. 更新物流信息
        await orderExtensionApi.updateOrderTracking(orderId, 'SF1234567890', '顺丰速运')

        // 6. 完成订单
        await orderStatusApi.updateOrderStatus(orderId, 'completed', '订单已完成')

        console.log('订单工作流完成:', orderId)
        return orderId
    } catch (error) {
        console.error('订单工作流执行失败:', error)
        throw error
    }
}

// 导出所有示例函数
export const orderApiExamples = {
    getOrderListExample,
    createOrderExample,
    updateOrderExample,
    orderStatusManagementExample,
    orderSearchExample,
    orderStatsExample,
    exportOrdersExample,
    apiWithRetryExample,
    errorHandlingExample,
    getCompleteOrderInfoExample,
    orderWorkflowExample
}