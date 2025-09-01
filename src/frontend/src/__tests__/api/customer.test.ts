import { describe, it, expect, beforeEach, vi } from 'vitest'
import { customerApi } from '@/api/customer'
import type { Customer, CustomerStats, CreateCustomerData, UpdateCustomerData } from '@/types/customer'

// Mock request utility
vi.mock('@/utils/request', () => ({
  request: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

import { request } from '@/utils/request'

describe('Customer API Integration Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('客户列表API测试', () => {
    it('应该能够获取客户列表', async () => {
      const mockResponse = {
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

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      const result = await customerApi.getCustomers({
        page: 1,
        pageSize: 20,
        keyword: '张三'
      })

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers', {
        params: {
          page: 1,
          pageSize: 20,
          keyword: '张三'
        }
      })
      expect(result.data.list).toHaveLength(1)
      expect(result.data.list[0].name).toBe('张三')
    })

    it('应该支持分页参数', async () => {
      const mockResponse = {
        data: {
          list: [],
          total: 0,
          page: 2,
          pageSize: 10
        }
      }

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      await customerApi.getCustomers({
        page: 2,
        pageSize: 10
      })

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers', {
        params: {
          page: 2,
          pageSize: 10
        }
      })
    })

    it('应该支持搜索和筛选参数', async () => {
      const mockResponse = {
        data: {
          list: [],
          total: 0,
          page: 1,
          pageSize: 20
        }
      }

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      await customerApi.getCustomers({
        keyword: '测试',
        level: 'vip',
        status: 'active',
        deviceType: 'purchased',
        region: '北京'
      })

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers', {
        params: {
          keyword: '测试',
          level: 'vip',
          status: 'active',
          deviceType: 'purchased',
          region: '北京'
        }
      })
    })
  })

  describe('客户详情API测试', () => {
    it('应该能够获取客户详情', async () => {
      const mockCustomer: Customer = {
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
        totalSpent: 5000,
        customerValue: 8.5,
        deviceCount: {
          total: 3,
          purchased: 2,
          rental: 1
        },
        registeredAt: '2024-01-01T00:00:00Z',
        lastActiveAt: '2024-01-15T10:30:00Z'
      }

      vi.mocked(request.get).mockResolvedValue(mockCustomer)

      const result = await customerApi.getCustomer('1')

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers/1')
      expect(result.name).toBe('张三')
      expect(result.level).toBe('vip')
      expect(result.deviceCount?.total).toBe(3)
    })
  })

  describe('客户统计API测试', () => {
    it('应该能够获取客户统计数据', async () => {
      const mockStats: CustomerStats = {
        total: 156,
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      vi.mocked(request.get).mockResolvedValue(mockStats)

      const result = await customerApi.getCustomerStats()

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers/stats')
      expect(result.total).toBe(156)
      expect(result.vip).toBe(30)
      expect(result.totalRevenue).toBe(125000)
    })
  })

  describe('客户CRUD操作API测试', () => {
    it('应该能够创建客户', async () => {
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

      const mockResponse: Customer = {
        id: '2',
        ...createData,
        totalSpent: 0,
        customerValue: 5.0,
        registeredAt: '2024-01-16T00:00:00Z'
      }

      vi.mocked(request.post).mockResolvedValue(mockResponse)

      const result = await customerApi.createCustomer(createData)

      expect(request.post).toHaveBeenCalledWith('/api/admin/customers', createData)
      expect(result.id).toBe('2')
      expect(result.name).toBe('李四')
    })

    it('应该能够更新客户', async () => {
      const updateData: UpdateCustomerData = {
        name: '张三（已更新）',
        level: 'premium',
        notes: '升级为高级客户'
      }

      const mockResponse: Customer = {
        id: '1',
        name: '张三（已更新）',
        level: 'premium',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: 'active',
        notes: '升级为高级客户',
        totalSpent: 5000,
        customerValue: 9.0,
        registeredAt: '2024-01-01T00:00:00Z'
      }

      vi.mocked(request.put).mockResolvedValue(mockResponse)

      const result = await customerApi.updateCustomer('1', updateData)

      expect(request.put).toHaveBeenCalledWith('/api/admin/customers/1', updateData)
      expect(result.name).toBe('张三（已更新）')
      expect(result.level).toBe('premium')
    })

    it('应该能够删除客户', async () => {
      vi.mocked(request.delete).mockResolvedValue({})

      await customerApi.deleteCustomer('1')

      expect(request.delete).toHaveBeenCalledWith('/api/admin/customers/1')
    })
  })

  describe('客户关联数据API测试', () => {
    it('应该能够获取客户设备列表', async () => {
      const mockDevices = [
        {
          id: '1',
          serialNumber: 'YX001',
          model: 'YXRobot-Pro',
          type: 'purchased',
          status: 'active',
          activatedAt: '2024-01-01T00:00:00Z',
          firmwareVersion: '1.2.3',
          healthScore: 95
        }
      ]

      vi.mocked(request.get).mockResolvedValue(mockDevices)

      const result = await customerApi.getCustomerDevices('1')

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers/1/devices')
      expect(result).toHaveLength(1)
    })

    it('应该能够获取客户订单列表', async () => {
      const mockOrders = [
        {
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
      ]

      vi.mocked(request.get).mockResolvedValue(mockOrders)

      const result = await customerApi.getCustomerOrders('1')

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers/1/orders')
      expect(result).toHaveLength(1)
    })

    it('应该能够获取客户服务记录', async () => {
      const mockServiceRecords = [
        {
          id: '1',
          type: 'maintenance',
          subject: '设备维护',
          description: '定期维护检查',
          serviceStaff: '技术员A',
          status: 'completed',
          createdAt: '2024-01-01T00:00:00Z'
        }
      ]

      vi.mocked(request.get).mockResolvedValue(mockServiceRecords)

      const result = await customerApi.getCustomerServiceRecords('1')

      expect(request.get).toHaveBeenCalledWith('/api/admin/customers/1/service-records')
      expect(result).toHaveLength(1)
    })
  })

  describe('错误处理测试', () => {
    it('应该正确处理网络错误', async () => {
      const networkError = new Error('Network Error')
      vi.mocked(request.get).mockRejectedValue(networkError)

      await expect(customerApi.getCustomers()).rejects.toThrow('Network Error')
    })

    it('应该正确处理API错误响应', async () => {
      const apiError = {
        response: {
          status: 400,
          data: {
            code: 400,
            message: '参数错误'
          }
        }
      }
      vi.mocked(request.post).mockRejectedValue(apiError)

      await expect(customerApi.createCustomer({
        name: '',
        level: 'regular',
        phone: '',
        email: '',
        status: 'active'
      })).rejects.toEqual(apiError)
    })
  })

  describe('数据格式验证测试', () => {
    it('客户列表响应应该匹配前端接口', async () => {
      const mockResponse = {
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
          ],
          total: 1,
          page: 1,
          pageSize: 20
        }
      }

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      const result = await customerApi.getCustomers()

      // 验证响应结构
      expect(result.data).toHaveProperty('list')
      expect(result.data).toHaveProperty('total')
      expect(result.data).toHaveProperty('page')
      expect(result.data).toHaveProperty('pageSize')

      // 验证客户对象结构
      const customer = result.data.list[0]
      expect(customer).toHaveProperty('id')
      expect(customer).toHaveProperty('name')
      expect(customer).toHaveProperty('level')
      expect(customer).toHaveProperty('phone')
      expect(customer).toHaveProperty('email')
      expect(customer).toHaveProperty('status')
      expect(customer).toHaveProperty('totalSpent')
      expect(customer).toHaveProperty('customerValue')
      expect(customer).toHaveProperty('registeredAt')

      // 验证数据类型
      expect(typeof customer.id).toBe('string')
      expect(typeof customer.name).toBe('string')
      expect(typeof customer.totalSpent).toBe('number')
      expect(typeof customer.customerValue).toBe('number')
    })

    it('客户统计响应应该匹配前端接口', async () => {
      const mockStats = {
        total: 156,
        regular: 120,
        vip: 30,
        premium: 6,
        activeDevices: 89,
        totalRevenue: 125000,
        newThisMonth: 12
      }

      vi.mocked(request.get).mockResolvedValue(mockStats)

      const result = await customerApi.getCustomerStats()

      // 验证统计对象结构
      expect(result).toHaveProperty('total')
      expect(result).toHaveProperty('regular')
      expect(result).toHaveProperty('vip')
      expect(result).toHaveProperty('premium')
      expect(result).toHaveProperty('activeDevices')
      expect(result).toHaveProperty('totalRevenue')
      expect(result).toHaveProperty('newThisMonth')

      // 验证数据类型
      expect(typeof result.total).toBe('number')
      expect(typeof result.regular).toBe('number')
      expect(typeof result.vip).toBe('number')
      expect(typeof result.premium).toBe('number')
      expect(typeof result.activeDevices).toBe('number')
      expect(typeof result.totalRevenue).toBe('number')
      expect(typeof result.newThisMonth).toBe('number')
    })
  })
})