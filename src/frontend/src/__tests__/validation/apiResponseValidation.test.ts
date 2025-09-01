import { describe, it, expect } from 'vitest'
import type { 
  Customer, 
  CustomerStats, 
  CustomerDevice, 
  CustomerOrder, 
  CustomerServiceRecord,
  Address,
  DeviceUsageStats,
  ServiceAttachment
} from '@/types/customer'

describe('API Response Format Validation', () => {
  describe('Customer接口验证', () => {
    it('Customer对象应该包含所有必需字段', () => {
      const customer: Customer = {
        id: '1',
        name: '张三',
        level: 'vip',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        totalSpent: 5000,
        customerValue: 8.5,
        registeredAt: '2024-01-01T00:00:00Z'
      }

      // 验证必需字段存在
      expect(customer.id).toBeDefined()
      expect(customer.name).toBeDefined()
      expect(customer.level).toBeDefined()
      expect(customer.phone).toBeDefined()
      expect(customer.email).toBeDefined()
      expect(customer.status).toBeDefined()
      expect(customer.totalSpent).toBeDefined()
      expect(customer.customerValue).toBeDefined()
      expect(customer.registeredAt).toBeDefined()

      // 验证字段类型
      expect(typeof customer.id).toBe('string')
      expect(typeof customer.name).toBe('string')
      expect(typeof customer.level).toBe('string')
      expect(typeof customer.phone).toBe('string')
      expect(typeof customer.email).toBe('string')
      expect(typeof customer.status).toBe('string')
      expect(typeof customer.totalSpent).toBe('number')
      expect(typeof customer.customerValue).toBe('number')
      expect(typeof customer.registeredAt).toBe('string')

      // 验证枚举值
      expect(['regular', 'vip', 'premium']).toContain(customer.level)
      expect(['active', 'inactive', 'suspended']).toContain(customer.status)
    })

    it('Customer对象应该支持可选字段', () => {
      const customerWithOptionalFields: Customer = {
        id: '1',
        name: '张三',
        level: 'vip',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        company: '测试公司',
        address: {
          province: '北京市',
          city: '北京市',
          detail: '朝阳区测试街道123号'
        },
        tags: ['重要客户', 'VIP'],
        notes: '这是一个重要客户',
        avatar: 'https://example.com/avatar.jpg',
        deviceCount: {
          total: 3,
          purchased: 2,
          rental: 1
        },
        totalSpent: 5000,
        customerValue: 8.5,
        devices: [],
        orders: [],
        serviceRecords: [],
        registeredAt: '2024-01-01T00:00:00Z',
        lastActiveAt: '2024-01-15T10:30:00Z'
      }

      // 验证可选字段类型
      expect(typeof customerWithOptionalFields.company).toBe('string')
      expect(typeof customerWithOptionalFields.address).toBe('object')
      expect(Array.isArray(customerWithOptionalFields.tags)).toBe(true)
      expect(typeof customerWithOptionalFields.notes).toBe('string')
      expect(typeof customerWithOptionalFields.avatar).toBe('string')
      expect(typeof customerWithOptionalFields.deviceCount).toBe('object')
      expect(Array.isArray(customerWithOptionalFields.devices)).toBe(true)
      expect(Array.isArray(customerWithOptionalFields.orders)).toBe(true)
      expect(Array.isArray(customerWithOptionalFields.serviceRecords)).toBe(true)
      expect(typeof customerWithOptionalFields.lastActiveAt).toBe('string')
    })
  })

  describe('CustomerStats接口验证', () => {
    it('CustomerStats对象应该包含所有统计字段', () => {
      const stats: CustomerStats = {
        total: 156,
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      // 验证所有字段存在
      expect(stats.total).toBeDefined()
      expect(stats.regular).toBeDefined()
      expect(stats.vip).toBeDefined()
      expect(stats.premium).toBeDefined()
      expect(stats.activeDevices).toBeDefined()
      expect(stats.totalRevenue).toBeDefined()
      expect(stats.newThisMonth).toBeDefined()

      // 验证字段类型
      expect(typeof stats.total).toBe('number')
      expect(typeof stats.regular).toBe('number')
      expect(typeof stats.vip).toBe('number')
      expect(typeof stats.premium).toBe('number')
      expect(typeof stats.activeDevices).toBe('number')
      expect(typeof stats.totalRevenue).toBe('number')
      expect(typeof stats.newThisMonth).toBe('number')

      // 验证数值合理性
      expect(stats.total).toBeGreaterThanOrEqual(0)
      expect(stats.regular + stats.vip + stats.premium).toBeLessThanOrEqual(stats.total)
      expect(stats.activeDevices).toBeGreaterThanOrEqual(0)
      expect(stats.totalRevenue).toBeGreaterThanOrEqual(0)
      expect(stats.newThisMonth).toBeGreaterThanOrEqual(0)
    })
  })

  describe('Address接口验证', () => {
    it('Address对象应该包含必需的地址字段', () => {
      const address: Address = {
        province: '北京市',
        city: '北京市',
        detail: '朝阳区测试街道123号'
      }

      // 验证必需字段
      expect(address.province).toBeDefined()
      expect(address.city).toBeDefined()
      expect(address.detail).toBeDefined()

      // 验证字段类型
      expect(typeof address.province).toBe('string')
      expect(typeof address.city).toBe('string')
      expect(typeof address.detail).toBe('string')

      // 验证字段不为空
      expect(address.province.trim()).not.toBe('')
      expect(address.city.trim()).not.toBe('')
      expect(address.detail.trim()).not.toBe('')
    })
  })

  describe('CustomerDevice接口验证', () => {
    it('CustomerDevice对象应该包含设备信息', () => {
      const device: CustomerDevice = {
        id: '1',
        serialNumber: 'YX001',
        model: 'YXRobot-Pro',
        type: 'purchased',
        status: 'active',
        firmwareVersion: '1.2.3',
        healthScore: 95
      }

      // 验证必需字段
      expect(device.id).toBeDefined()
      expect(device.serialNumber).toBeDefined()
      expect(device.model).toBeDefined()
      expect(device.type).toBeDefined()
      expect(device.status).toBeDefined()
      expect(device.firmwareVersion).toBeDefined()
      expect(device.healthScore).toBeDefined()

      // 验证字段类型
      expect(typeof device.id).toBe('string')
      expect(typeof device.serialNumber).toBe('string')
      expect(typeof device.model).toBe('string')
      expect(typeof device.type).toBe('string')
      expect(typeof device.status).toBe('string')
      expect(typeof device.firmwareVersion).toBe('string')
      expect(typeof device.healthScore).toBe('number')

      // 验证枚举值
      expect(['purchased', 'rental']).toContain(device.type)
      expect(['pending', 'active', 'offline', 'maintenance', 'retired']).toContain(device.status)

      // 验证健康评分范围
      expect(device.healthScore).toBeGreaterThanOrEqual(0)
      expect(device.healthScore).toBeLessThanOrEqual(100)
    })

    it('CustomerDevice应该支持使用统计信息', () => {
      const deviceWithStats: CustomerDevice = {
        id: '1',
        serialNumber: 'YX001',
        model: 'YXRobot-Pro',
        type: 'purchased',
        status: 'active',
        firmwareVersion: '1.2.3',
        healthScore: 95,
        usageStats: {
          totalUsageHours: 120,
          dailyAverageUsage: 4.5,
          lastUsedAt: '2024-01-15T10:30:00Z',
          coursesCompleted: 25,
          charactersWritten: 15000
        }
      }

      const usageStats = deviceWithStats.usageStats as DeviceUsageStats

      // 验证使用统计字段
      expect(usageStats.totalUsageHours).toBeDefined()
      expect(usageStats.dailyAverageUsage).toBeDefined()
      expect(usageStats.lastUsedAt).toBeDefined()
      expect(usageStats.coursesCompleted).toBeDefined()
      expect(usageStats.charactersWritten).toBeDefined()

      // 验证字段类型
      expect(typeof usageStats.totalUsageHours).toBe('number')
      expect(typeof usageStats.dailyAverageUsage).toBe('number')
      expect(typeof usageStats.lastUsedAt).toBe('string')
      expect(typeof usageStats.coursesCompleted).toBe('number')
      expect(typeof usageStats.charactersWritten).toBe('number')
    })
  })

  describe('CustomerOrder接口验证', () => {
    it('CustomerOrder对象应该包含订单信息', () => {
      const order: CustomerOrder = {
        id: '1',
        orderNumber: 'ORD001',
        type: 'sales',
        productName: 'YXRobot-Pro',
        productModel: 'YX-001',
        quantity: 1,
        amount: 2999,
        status: 'completed',
        createdAt: '2024-01-01T00:00:00Z'
      }

      // 验证必需字段
      expect(order.id).toBeDefined()
      expect(order.orderNumber).toBeDefined()
      expect(order.type).toBeDefined()
      expect(order.productName).toBeDefined()
      expect(order.productModel).toBeDefined()
      expect(order.quantity).toBeDefined()
      expect(order.amount).toBeDefined()
      expect(order.status).toBeDefined()
      expect(order.createdAt).toBeDefined()

      // 验证字段类型
      expect(typeof order.id).toBe('string')
      expect(typeof order.orderNumber).toBe('string')
      expect(typeof order.type).toBe('string')
      expect(typeof order.productName).toBe('string')
      expect(typeof order.productModel).toBe('string')
      expect(typeof order.quantity).toBe('number')
      expect(typeof order.amount).toBe('number')
      expect(typeof order.status).toBe('string')
      expect(typeof order.createdAt).toBe('string')

      // 验证枚举值
      expect(['sales', 'rental']).toContain(order.type)
      expect(['pending', 'processing', 'completed', 'cancelled']).toContain(order.status)

      // 验证数值合理性
      expect(order.quantity).toBeGreaterThan(0)
      expect(order.amount).toBeGreaterThan(0)
    })

    it('CustomerOrder应该支持租赁订单字段', () => {
      const rentalOrder: CustomerOrder = {
        id: '2',
        orderNumber: 'ORD002',
        type: 'rental',
        productName: 'YXRobot-Pro',
        productModel: 'YX-001',
        quantity: 1,
        amount: 299,
        status: 'processing',
        createdAt: '2024-01-01T00:00:00Z',
        rentalDays: 30,
        rentalStartDate: '2024-01-01',
        rentalEndDate: '2024-01-31',
        notes: '租赁期30天'
      }

      // 验证租赁特有字段
      expect(rentalOrder.rentalDays).toBeDefined()
      expect(rentalOrder.rentalStartDate).toBeDefined()
      expect(rentalOrder.rentalEndDate).toBeDefined()
      expect(rentalOrder.notes).toBeDefined()

      // 验证字段类型
      expect(typeof rentalOrder.rentalDays).toBe('number')
      expect(typeof rentalOrder.rentalStartDate).toBe('string')
      expect(typeof rentalOrder.rentalEndDate).toBe('string')
      expect(typeof rentalOrder.notes).toBe('string')

      // 验证租赁天数合理性
      expect(rentalOrder.rentalDays).toBeGreaterThan(0)
    })
  })

  describe('CustomerServiceRecord接口验证', () => {
    it('CustomerServiceRecord对象应该包含服务记录信息', () => {
      const serviceRecord: CustomerServiceRecord = {
        id: '1',
        type: 'maintenance',
        subject: '设备维护',
        description: '定期维护检查',
        serviceStaff: '技术员A',
        status: 'completed',
        createdAt: '2024-01-01T00:00:00Z'
      }

      // 验证必需字段
      expect(serviceRecord.id).toBeDefined()
      expect(serviceRecord.type).toBeDefined()
      expect(serviceRecord.subject).toBeDefined()
      expect(serviceRecord.description).toBeDefined()
      expect(serviceRecord.serviceStaff).toBeDefined()
      expect(serviceRecord.status).toBeDefined()
      expect(serviceRecord.createdAt).toBeDefined()

      // 验证字段类型
      expect(typeof serviceRecord.id).toBe('string')
      expect(typeof serviceRecord.type).toBe('string')
      expect(typeof serviceRecord.subject).toBe('string')
      expect(typeof serviceRecord.description).toBe('string')
      expect(typeof serviceRecord.serviceStaff).toBe('string')
      expect(typeof serviceRecord.status).toBe('string')
      expect(typeof serviceRecord.createdAt).toBe('string')

      // 验证枚举值
      expect(['maintenance', 'upgrade', 'consultation', 'complaint']).toContain(serviceRecord.type)
      expect(['in_progress', 'completed', 'cancelled']).toContain(serviceRecord.status)
    })

    it('CustomerServiceRecord应该支持附件信息', () => {
      const serviceRecordWithAttachments: CustomerServiceRecord = {
        id: '1',
        type: 'maintenance',
        subject: '设备维护',
        description: '定期维护检查',
        serviceStaff: '技术员A',
        status: 'completed',
        cost: 200,
        attachments: [
          {
            id: '1',
            name: '维护报告.pdf',
            url: 'https://example.com/report.pdf',
            size: 1024000,
            type: 'application/pdf'
          }
        ],
        createdAt: '2024-01-01T00:00:00Z'
      }

      // 验证可选字段
      expect(serviceRecordWithAttachments.cost).toBeDefined()
      expect(serviceRecordWithAttachments.attachments).toBeDefined()

      // 验证字段类型
      expect(typeof serviceRecordWithAttachments.cost).toBe('number')
      expect(Array.isArray(serviceRecordWithAttachments.attachments)).toBe(true)

      // 验证附件结构
      const attachment = serviceRecordWithAttachments.attachments![0] as ServiceAttachment
      expect(attachment.id).toBeDefined()
      expect(attachment.name).toBeDefined()
      expect(attachment.url).toBeDefined()
      expect(attachment.size).toBeDefined()
      expect(attachment.type).toBeDefined()

      expect(typeof attachment.id).toBe('string')
      expect(typeof attachment.name).toBe('string')
      expect(typeof attachment.url).toBe('string')
      expect(typeof attachment.size).toBe('number')
      expect(typeof attachment.type).toBe('string')
    })
  })

  describe('API响应结构验证', () => {
    it('客户列表API响应应该符合预期格式', () => {
      const apiResponse = {
        data: {
          list: [
            {
              id: '1',
              name: '张三',
              level: 'vip',
              phone: '13800138001',
              email: 'zhangsan@example.com',
              status: 'active',
              totalSpent: 5000,
              customerValue: 8.5,
              registeredAt: '2024-01-01T00:00:00Z'
            }
          ] as Customer[],
          total: 1,
          page: 1,
          pageSize: 20
        }
      }

      // 验证响应结构
      expect(apiResponse.data).toBeDefined()
      expect(apiResponse.data.list).toBeDefined()
      expect(apiResponse.data.total).toBeDefined()
      expect(apiResponse.data.page).toBeDefined()
      expect(apiResponse.data.pageSize).toBeDefined()

      // 验证字段类型
      expect(Array.isArray(apiResponse.data.list)).toBe(true)
      expect(typeof apiResponse.data.total).toBe('number')
      expect(typeof apiResponse.data.page).toBe('number')
      expect(typeof apiResponse.data.pageSize).toBe('number')

      // 验证分页信息合理性
      expect(apiResponse.data.total).toBeGreaterThanOrEqual(0)
      expect(apiResponse.data.page).toBeGreaterThan(0)
      expect(apiResponse.data.pageSize).toBeGreaterThan(0)
      expect(apiResponse.data.list.length).toBeLessThanOrEqual(apiResponse.data.pageSize)
    })

    it('统一错误响应格式验证', () => {
      const errorResponse = {
        code: 400,
        message: '参数错误',
        data: null
      }

      // 验证错误响应结构
      expect(errorResponse.code).toBeDefined()
      expect(errorResponse.message).toBeDefined()
      expect(errorResponse.data).toBeDefined()

      // 验证字段类型
      expect(typeof errorResponse.code).toBe('number')
      expect(typeof errorResponse.message).toBe('string')

      // 验证错误码范围
      expect(errorResponse.code).toBeGreaterThanOrEqual(400)
      expect(errorResponse.code).toBeLessThan(600)
    })
  })

  describe('日期时间格式验证', () => {
    it('应该使用ISO 8601格式的日期时间', () => {
      const customer: Customer = {
        id: '1',
        name: '张三',
        level: 'vip',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        totalSpent: 5000,
        customerValue: 8.5,
        registeredAt: '2024-01-01T00:00:00Z',
        lastActiveAt: '2024-01-15T10:30:00Z'
      }

      // 验证日期时间格式
      const isoDateRegex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{3})?Z?$/
      expect(isoDateRegex.test(customer.registeredAt)).toBe(true)
      
      if (customer.lastActiveAt) {
        expect(isoDateRegex.test(customer.lastActiveAt)).toBe(true)
      }

      // 验证日期可以被正确解析
      expect(new Date(customer.registeredAt).getTime()).not.toBeNaN()
      if (customer.lastActiveAt) {
        expect(new Date(customer.lastActiveAt).getTime()).not.toBeNaN()
      }
    })
  })
})