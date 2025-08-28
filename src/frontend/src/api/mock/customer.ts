import type { Customer, CustomerDevice, CustomerOrder, ServiceRecord, CustomerStats } from '@/types/customer'

// Mock客户设备数据
export const mockCustomerDevices: CustomerDevice[] = [
  {
    id: '1',
    serialNumber: 'YX-EDU-001001',
    model: 'YX-EDU-2024',
    type: 'purchased',
    status: 'active',
    activatedAt: '2024-01-15T08:00:00Z',
    lastOnlineAt: '2024-02-20T14:30:00Z',
    firmwareVersion: '2.1.0',
    healthScore: 95,
    usageStats: {
      totalUsageHours: 156,
      dailyAverageUsage: 2.5,
      lastUsedAt: '2024-02-20T14:30:00Z',
      coursesCompleted: 12,
      charactersWritten: 2580,
      learningProgress: 78
    },
    purchaseDate: '2024-01-10T00:00:00Z',
    warrantyEndDate: '2027-01-10T00:00:00Z'
  },
  {
    id: '2',
    serialNumber: 'YX-EDU-001002',
    model: 'YX-EDU-2024',
    type: 'rental',
    status: 'active',
    activatedAt: '2024-02-01T10:00:00Z',
    lastOnlineAt: '2024-02-20T16:45:00Z',
    firmwareVersion: '2.1.0',
    healthScore: 88,
    usageStats: {
      totalUsageHours: 45,
      dailyAverageUsage: 2.2,
      lastUsedAt: '2024-02-20T16:45:00Z',
      coursesCompleted: 3,
      charactersWritten: 680,
      learningProgress: 25
    },
    rentalStartDate: '2024-02-01T00:00:00Z',
    rentalEndDate: '2024-05-01T00:00:00Z'
  }
]

// Mock客户订单数据
export const mockCustomerOrders: CustomerOrder[] = [
  {
    id: '1',
    orderNumber: 'ORD-2024-001001',
    type: 'purchase',
    status: 'completed',
    totalAmount: 2999,
    currency: 'CNY',
    items: [
      {
        id: '1',
        productId: '1',
        productName: '教育版练字机器人',
        productModel: 'YX-EDU-2024',
        quantity: 1,
        unitPrice: 2999,
        totalPrice: 2999
      }
    ],
    createdAt: '2024-01-10T08:00:00Z',
    updatedAt: '2024-01-15T10:00:00Z'
  },
  {
    id: '2',
    orderNumber: 'ORD-2024-001002',
    type: 'rental',
    status: 'active',
    totalAmount: 299,
    currency: 'CNY',
    items: [
      {
        id: '2',
        productId: '1',
        productName: '教育版练字机器人',
        productModel: 'YX-EDU-2024',
        quantity: 1,
        unitPrice: 299,
        totalPrice: 299
      }
    ],
    createdAt: '2024-02-01T08:00:00Z',
    updatedAt: '2024-02-01T10:00:00Z'
  }
]

// Mock服务记录数据
export const mockServiceRecords: ServiceRecord[] = [
  {
    id: '1',
    type: 'consultation',
    title: '使用咨询',
    description: '客户咨询设备使用方法和课程设置',
    status: 'completed',
    priority: 'medium',
    assignedTo: '客服小王',
    createdAt: '2024-01-20T09:00:00Z',
    updatedAt: '2024-01-20T10:30:00Z',
    resolvedAt: '2024-01-20T10:30:00Z'
  },
  {
    id: '2',
    type: 'maintenance',
    title: '设备维护',
    description: '定期维护检查，更新固件版本',
    status: 'completed',
    priority: 'low',
    assignedTo: '技术小李',
    createdAt: '2024-02-10T14:00:00Z',
    updatedAt: '2024-02-10T16:00:00Z',
    resolvedAt: '2024-02-10T16:00:00Z'
  }
]

// Mock客户数据
export const mockCustomers: Customer[] = [
  {
    id: '1',
    name: '张明华',
    email: 'zhang.minghua@example.com',
    phone: '138****8888',
    address: {
      country: '中国',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detail: '建国路88号',
      zipCode: '100020'
    },
    level: 'vip',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
    devices: mockCustomerDevices,
    orders: mockCustomerOrders,
    serviceRecords: mockServiceRecords,
    registeredAt: '2024-01-10T08:00:00Z',
    lastActiveAt: '2024-02-20T16:45:00Z',
    totalSpent: 3298,
    deviceCount: {
      purchased: 1,
      rental: 1,
      total: 2
    },
    customerValue: 8.5,
    tags: ['教育机构', '长期客户', '推荐客户'],
    notes: '北京某小学校长，对产品很满意，有推荐其他学校的潜力'
  },
  {
    id: '2',
    name: '李晓红',
    email: 'li.xiaohong@example.com',
    phone: '139****6666',
    address: {
      country: '中国',
      province: '上海市',
      city: '上海市',
      district: '浦东新区',
      detail: '世纪大道1000号',
      zipCode: '200120'
    },
    level: 'premium',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&h=100&fit=crop&crop=face',
    devices: [
      {
        ...mockCustomerDevices[0],
        id: '3',
        serialNumber: 'YX-EDU-002001',
        type: 'purchased',
        purchaseDate: '2024-01-20T00:00:00Z'
      },
      {
        ...mockCustomerDevices[0],
        id: '4',
        serialNumber: 'YX-EDU-002002',
        type: 'purchased',
        purchaseDate: '2024-01-20T00:00:00Z'
      },
      {
        ...mockCustomerDevices[0],
        id: '5',
        serialNumber: 'YX-EDU-002003',
        type: 'purchased',
        purchaseDate: '2024-01-20T00:00:00Z'
      }
    ],
    orders: [
      {
        ...mockCustomerOrders[0],
        id: '3',
        orderNumber: 'ORD-2024-002001',
        totalAmount: 8997,
        items: [
          {
            ...mockCustomerOrders[0].items[0],
            quantity: 3,
            totalPrice: 8997
          }
        ]
      }
    ],
    serviceRecords: [],
    registeredAt: '2024-01-20T08:00:00Z',
    lastActiveAt: '2024-02-19T20:15:00Z',
    totalSpent: 8997,
    deviceCount: {
      purchased: 3,
      rental: 0,
      total: 3
    },
    customerValue: 9.2,
    tags: ['培训机构', '批量采购', 'VIP客户'],
    notes: '上海知名书法培训机构负责人，批量采购，服务要求较高'
  },
  {
    id: '3',
    name: '王建国',
    email: 'wang.jianguo@example.com',
    phone: '137****5555',
    address: {
      country: '中国',
      province: '广东省',
      city: '深圳市',
      district: '南山区',
      detail: '科技园南路15号',
      zipCode: '518000'
    },
    level: 'regular',
    devices: [
      {
        ...mockCustomerDevices[1],
        id: '6',
        serialNumber: 'YX-HOME-003001',
        model: 'YX-HOME-2024',
        type: 'purchased'
      }
    ],
    orders: [
      {
        ...mockCustomerOrders[0],
        id: '4',
        orderNumber: 'ORD-2024-003001',
        totalAmount: 1999,
        items: [
          {
            ...mockCustomerOrders[0].items[0],
            productName: '家庭版练字机器人',
            productModel: 'YX-HOME-2024',
            unitPrice: 1999,
            totalPrice: 1999
          }
        ]
      }
    ],
    serviceRecords: [],
    registeredAt: '2024-02-05T10:00:00Z',
    lastActiveAt: '2024-02-18T19:30:00Z',
    totalSpent: 1999,
    deviceCount: {
      purchased: 1,
      rental: 0,
      total: 1
    },
    customerValue: 6.8,
    tags: ['家庭用户', '新客户'],
    notes: '家庭用户，为孩子购买练字设备'
  }
]

// Mock客户统计数据
export const mockCustomerStats: CustomerStats = {
  total: 156,
  regular: 98,
  vip: 42,
  premium: 16,
  activeDevices: 234,
  totalRevenue: 486750,
  newThisMonth: 23
}

// 模拟API延迟
const mockDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Mock API响应格式
const createMockResponse = <T>(data: T, code: number = 200, message: string = 'success') => {
  return {
    code,
    message,
    data,
    timestamp: Date.now()
  }
}

// 客户管理Mock服务类
export class CustomerMockService {
  
  // 生成更多客户数据
  static generateMoreCustomers(): Customer[] {
    const additionalCustomers: Customer[] = []
    const names = ['陈志强', '刘美丽', '赵建华', '孙小明', '周雅琴', '吴大伟', '郑小红', '马建国', '朱丽娜', '胡志明']
    const provinces = ['北京市', '上海市', '广东省', '浙江省', '江苏省', '山东省', '河南省', '湖北省', '四川省', '福建省']
    const levels: ('regular' | 'vip' | 'premium')[] = ['regular', 'vip', 'premium']
    const tags = [
      ['家庭用户', '新客户'],
      ['教育机构', '长期客户'],
      ['培训机构', 'VIP客户'],
      ['个人用户', '推荐客户'],
      ['企业客户', '批量采购'],
      ['学校客户', '教育优惠'],
      ['社区中心', '公益合作']
    ]

    for (let i = 4; i <= 50; i++) {
      const level = levels[Math.floor(Math.random() * levels.length)]
      const province = provinces[Math.floor(Math.random() * provinces.length)]
      const purchasedCount = Math.floor(Math.random() * 5)
      const rentalCount = Math.floor(Math.random() * 3)
      const totalSpent = purchasedCount * 2999 + rentalCount * 299 * 3
      const customerTags = tags[Math.floor(Math.random() * tags.length)]

      additionalCustomers.push({
        id: i.toString(),
        name: names[Math.floor(Math.random() * names.length)],
        email: `customer${i}@example.com`,
        phone: `138****${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`,
        address: {
          country: '中国',
          province: province,
          city: province,
          district: '市中心区',
          detail: `街道${i}号`,
          zipCode: `${100000 + i}`
        },
        level: level,
        avatar: `https://images.unsplash.com/photo-${1472099645785 + i}?w=100&h=100&fit=crop&crop=face`,
        devices: [],
        orders: [],
        serviceRecords: [],
        registeredAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
        lastActiveAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
        totalSpent: totalSpent,
        deviceCount: {
          purchased: purchasedCount,
          rental: rentalCount,
          total: purchasedCount + rentalCount
        },
        customerValue: Math.random() * 10,
        tags: customerTags,
        notes: `客户${i}的备注信息`
      })
    }

    return [...mockCustomers, ...additionalCustomers]
  }

  // 应用筛选条件
  static applyFilters(customers: Customer[], params: any): Customer[] {
    let filtered = [...customers]

    // 关键词搜索
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filtered = filtered.filter(customer => 
        customer.name.toLowerCase().includes(keyword) ||
        customer.email.toLowerCase().includes(keyword) ||
        customer.phone.includes(keyword) ||
        customer.address.province.includes(keyword) ||
        customer.tags.some(tag => tag.includes(keyword))
      )
    }

    // 客户等级筛选
    if (params.level) {
      filtered = filtered.filter(customer => customer.level === params.level)
    }

    // 设备类型筛选
    if (params.deviceType) {
      if (params.deviceType === 'purchased') {
        filtered = filtered.filter(customer => customer.deviceCount.purchased > 0 && customer.deviceCount.rental === 0)
      } else if (params.deviceType === 'rental') {
        filtered = filtered.filter(customer => customer.deviceCount.rental > 0 && customer.deviceCount.purchased === 0)
      } else if (params.deviceType === 'mixed') {
        filtered = filtered.filter(customer => customer.deviceCount.purchased > 0 && customer.deviceCount.rental > 0)
      }
    }

    // 地区筛选
    if (params.region) {
      filtered = filtered.filter(customer => customer.address.province === params.region)
    }

    return filtered
  }

  // 应用排序
  static applySorting(customers: Customer[], sortBy?: string, sortOrder: 'asc' | 'desc' = 'desc'): Customer[] {
    if (!sortBy) return customers

    return customers.sort((a, b) => {
      let aValue: any, bValue: any

      switch (sortBy) {
        case 'name':
          aValue = a.name
          bValue = b.name
          break
        case 'totalSpent':
          aValue = a.totalSpent
          bValue = b.totalSpent
          break
        case 'deviceCount':
          aValue = a.deviceCount.total
          bValue = b.deviceCount.total
          break
        case 'customerValue':
          aValue = a.customerValue
          bValue = b.customerValue
          break
        case 'registeredAt':
          aValue = new Date(a.registeredAt).getTime()
          bValue = new Date(b.registeredAt).getTime()
          break
        default:
          return 0
      }

      if (sortOrder === 'asc') {
        return aValue > bValue ? 1 : -1
      } else {
        return aValue < bValue ? 1 : -1
      }
    })
  }
}

// Mock客户管理API
export const mockCustomerAPI = {
  // 获取客户列表
  async getCustomers(params: any = {}) {
    await mockDelay()
    
    const allCustomers = CustomerMockService.generateMoreCustomers()
    let filteredCustomers = CustomerMockService.applyFilters(allCustomers, params)
    
    // 应用排序
    filteredCustomers = CustomerMockService.applySorting(
      filteredCustomers, 
      params.sortBy, 
      params.sortOrder
    )
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const total = filteredCustomers.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = filteredCustomers.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 获取客户详情
  async getCustomerById(id: string) {
    await mockDelay()
    const allCustomers = CustomerMockService.generateMoreCustomers()
    const customer = allCustomers.find(c => c.id === id)
    
    if (!customer) {
      return createMockResponse(null, 404, '客户不存在')
    }
    
    return createMockResponse(customer)
  },

  // 获取客户设备列表
  async getCustomerDevices(customerId: string) {
    await mockDelay()
    const allCustomers = CustomerMockService.generateMoreCustomers()
    const customer = allCustomers.find(c => c.id === customerId)
    
    if (!customer) {
      return createMockResponse([], 404, '客户不存在')
    }
    
    return createMockResponse(customer.devices || [])
  },

  // 获取客户订单列表
  async getCustomerOrders(customerId: string) {
    await mockDelay()
    const allCustomers = CustomerMockService.generateMoreCustomers()
    const customer = allCustomers.find(c => c.id === customerId)
    
    if (!customer) {
      return createMockResponse([], 404, '客户不存在')
    }
    
    return createMockResponse(customer.orders || [])
  },

  // 获取客户服务记录
  async getCustomerServiceRecords(customerId: string) {
    await mockDelay()
    const allCustomers = CustomerMockService.generateMoreCustomers()
    const customer = allCustomers.find(c => c.id === customerId)
    
    if (!customer) {
      return createMockResponse([], 404, '客户不存在')
    }
    
    return createMockResponse(customer.serviceRecords || [])
  },

  // 获取客户统计数据
  async getCustomerStats() {
    await mockDelay()
    return createMockResponse(mockCustomerStats)
  },

  // 创建客户
  async createCustomer(customerData: any) {
    await mockDelay()
    const newCustomer: Customer = {
      id: Date.now().toString(),
      ...customerData,
      devices: [],
      orders: [],
      serviceRecords: [],
      registeredAt: new Date().toISOString(),
      lastActiveAt: new Date().toISOString(),
      totalSpent: 0,
      deviceCount: {
        purchased: 0,
        rental: 0,
        total: 0
      },
      customerValue: 0,
      tags: customerData.tags || [],
      level: customerData.level || 'regular'
    }
    
    return createMockResponse(newCustomer)
  },

  // 更新客户
  async updateCustomer(id: string, customerData: any) {
    await mockDelay()
    return createMockResponse({ id, ...customerData })
  },

  // 删除客户
  async deleteCustomer(id: string) {
    await mockDelay()
    return createMockResponse(null)
  },

  // 添加客户设备
  async addCustomerDevice(deviceData: any) {
    await mockDelay()
    const newDevice = {
      id: Date.now().toString(),
      ...deviceData,
      firmwareVersion: '2.1.0',
      healthScore: Math.floor(Math.random() * 20) + 80,
      usageStats: {
        totalUsageHours: 0,
        dailyAverageUsage: 0,
        lastUsedAt: new Date().toISOString(),
        coursesCompleted: 0,
        charactersWritten: 0
      },
      lastOnlineAt: deviceData.status === 'active' ? new Date().toISOString() : undefined
    }
    return createMockResponse(newDevice)
  },

  // 移除客户设备
  async removeCustomerDevice(customerId: string, deviceId: string) {
    await mockDelay()
    return createMockResponse(null)
  },

  // 添加客户服务记录
  async addCustomerServiceRecord(recordData: any) {
    await mockDelay()
    const newRecord = {
      id: Date.now().toString(),
      ...recordData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(newRecord)
  },

  // 更新客户服务记录
  async updateCustomerServiceRecord(customerId: string, recordId: string, updateData: any) {
    await mockDelay()
    const updatedRecord = {
      id: recordId,
      customerId,
      ...updateData,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedRecord)
  }
}