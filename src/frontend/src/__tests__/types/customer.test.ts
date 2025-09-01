import { describe, it, expect } from 'vitest'
import type { Customer, CustomerStats, CreateCustomerData, UpdateCustomerData } from '@/types/customer'

describe('Customer Types', () => {
  describe('Customer interface', () => {
    it('should have all required fields', () => {
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

      expect(customer.id).toBe('1')
      expect(customer.name).toBe('张三')
      expect(customer.level).toBe('vip')
      expect(customer.phone).toBe('13800138001')
      expect(customer.email).toBe('zhangsan@example.com')
      expect(customer.status).toBe('active')
      expect(customer.totalSpent).toBe(5000)
      expect(customer.customerValue).toBe(8.5)
      expect(customer.registeredAt).toBe('2024-01-01T00:00:00Z')
    })

    it('should support optional fields', () => {
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
        company: '测试公司',
        address: {
          province: '北京市',
          city: '北京市',
          detail: '朝阳区测试街道123号'
        },
        tags: ['VIP', '重要客户'],
        notes: '这是一个重要客户',
        deviceCount: {
          total: 3,
          purchased: 2,
          rental: 1
        },
        lastActiveAt: '2024-01-15T10:30:00Z'
      }

      expect(customer.company).toBe('测试公司')
      expect(customer.address?.province).toBe('北京市')
      expect(customer.tags).toContain('VIP')
      expect(customer.deviceCount?.total).toBe(3)
    })
  })

  describe('CustomerStats interface', () => {
    it('should have all required statistical fields', () => {
      const stats: CustomerStats = {
        total: 156,
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      expect(stats.total).toBe(156)
      expect(stats.regular).toBe(120)
      expect(stats.vip).toBe(30)
      expect(stats.premium).toBe(6)
      expect(stats.activeDevices).toBe(89)
      expect(stats.totalRevenue).toBe(125000)
      expect(stats.newThisMonth).toBe(12)
    })
  })

  describe('CreateCustomerData interface', () => {
    it('should have all required fields for creating customer', () => {
      const createData: CreateCustomerData = {
        name: '李四',
        level: 'regular',
        phone: '13800138002',
        email: 'lisi@example.com',
        status: 'active'
      }

      expect(createData.name).toBe('李四')
      expect(createData.level).toBe('regular')
      expect(createData.phone).toBe('13800138002')
      expect(createData.email).toBe('lisi@example.com')
      expect(createData.status).toBe('active')
    })

    it('should support optional fields for creating customer', () => {
      const createData: CreateCustomerData = {
        name: '李四',
        level: 'regular',
        phone: '13800138002',
        email: 'lisi@example.com',
        status: 'active',
        company: '新公司',
        address: {
          province: '上海市',
          city: '上海市',
          detail: '浦东新区测试路456号'
        },
        tags: ['新客户'],
        notes: '通过推荐注册的新客户'
      }

      expect(createData.company).toBe('新公司')
      expect(createData.address?.city).toBe('上海市')
      expect(createData.tags).toContain('新客户')
      expect(createData.notes).toBe('通过推荐注册的新客户')
    })
  })

  describe('UpdateCustomerData interface', () => {
    it('should allow partial updates', () => {
      const updateData: UpdateCustomerData = {
        name: '张三（已更新）',
        level: 'premium',
        notes: '升级为高级客户'
      }

      expect(updateData.name).toBe('张三（已更新）')
      expect(updateData.level).toBe('premium')
      expect(updateData.notes).toBe('升级为高级客户')
    })

    it('should allow updating address', () => {
      const updateData: UpdateCustomerData = {
        address: {
          province: '广东省',
          city: '深圳市',
          detail: '南山区科技园'
        }
      }

      expect(updateData.address?.province).toBe('广东省')
      expect(updateData.address?.city).toBe('深圳市')
      expect(updateData.address?.detail).toBe('南山区科技园')
    })
  })

  describe('Type constraints', () => {
    it('should enforce customer level enum values', () => {
      // 这些应该是有效的等级值
      const validLevels: Array<'regular' | 'vip' | 'premium'> = ['regular', 'vip', 'premium']
      
      validLevels.forEach(level => {
        const customer: Partial<Customer> = { level }
        expect(['regular', 'vip', 'premium']).toContain(customer.level)
      })
    })

    it('should enforce customer status enum values', () => {
      // 这些应该是有效的状态值
      const validStatuses: Array<'active' | 'inactive' | 'suspended'> = ['active', 'inactive', 'suspended']
      
      validStatuses.forEach(status => {
        const customer: Partial<Customer> = { status }
        expect(['active', 'inactive', 'suspended']).toContain(customer.status)
      })
    })
  })
})