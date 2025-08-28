import type { Product, ProductImage, ProductFeature } from '@/types/product'

// Mock产品图片数据
export const mockProductImages: ProductImage[] = [
  {
    id: '1',
    url: 'https://images.unsplash.com/photo-1518717758536-85ae29035b6d?w=800&h=600&fit=crop',
    alt: '练字机器人主图',
    order: 1,
    type: 'main'
  },
  {
    id: '2',
    url: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&h=600&fit=crop',
    alt: '产品侧面图',
    order: 2,
    type: 'gallery'
  },
  {
    id: '3',
    url: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=800&h=600&fit=crop',
    alt: '产品细节图',
    order: 3,
    type: 'gallery'
  },
  {
    id: '4',
    url: 'https://images.unsplash.com/photo-1518717758536-85ae29035b6d?w=800&h=600&fit=crop',
    alt: '使用场景图',
    order: 4,
    type: 'detail'
  }
]

// Mock产品功能特性
export const mockProductFeatures: ProductFeature[] = [
  {
    title: 'AI智能识别',
    description: '采用先进的人工智能技术，精准识别笔画轨迹，实时纠正书写错误，提供个性化指导建议。',
    icon: 'Cpu'
  },
  {
    title: '数据分析系统',
    description: '全面记录学习数据，生成详细的学习报告，帮助教师了解学生进度，制定针对性教学计划。',
    icon: 'DataAnalysis'
  },
  {
    title: '教育专用设计',
    description: '专为教育机构设计，支持多人同时使用，配备教师管理端，方便课堂教学和学生管理。',
    icon: 'School'
  },
  {
    title: '智能教学模式',
    description: '内置多种教学模式，从基础笔画到复杂汉字，循序渐进，适应不同年龄段学生的学习需求。',
    icon: 'Lightning'
  },
  {
    title: '护眼显示技术',
    description: '采用电子墨水屏技术，无蓝光伤害，长时间使用不疲劳，保护学生视力健康。',
    icon: 'Monitor'
  }
]

// Mock产品数据
export const mockProduct: Product = {
  id: '1',
  name: '教育版练字机器人',
  model: 'YX-EDU-2024',
  description: '专为教育机构设计的智能练字机器人，集成AI识别技术，提供个性化书法教学解决方案。',
  badge: '教育专版',
  features: [
    'AI智能笔迹识别',
    '实时书写指导',
    '多人协作学习',
    '学习进度跟踪',
    '个性化课程推荐'
  ],
  productFeatures: mockProductFeatures,
  specifications: {
    dimensions: '420mm × 320mm × 180mm',
    weight: '2.8kg',
    display: '10.1英寸电子墨水屏',
    connectivity: 'Wi-Fi 6, 蓝牙 5.0, USB-C',
    teaching: '手把手教学，实时纠错',
    recognition: 'AI深度学习算法',
    courses: '内置3000+汉字课程',
    management: '云端管理平台'
  },
  price: 2999,
  originalPrice: 3999,
  prices: [
    {
      currency: 'CNY',
      amount: 2999,
      originalAmount: 3999,
      discount: 25
    },
    {
      currency: 'USD',
      amount: 420,
      originalAmount: 560,
      discount: 25
    }
  ],
  images: mockProductImages,
  highlights: [
    'AI智能识别技术，准确率达99.5%',
    '支持50人同时在线学习',
    '内置专业书法教学课程',
    '实时学习数据分析',
    '护眼电子墨水屏设计'
  ],
  status: 'published',
  createdAt: '2024-01-15T08:00:00Z',
  updatedAt: '2024-02-20T10:30:00Z'
}

// Mock产品列表数据
export const mockProducts: Product[] = [
  mockProduct,
  {
    ...mockProduct,
    id: '2',
    name: '家庭版练字机器人',
    model: 'YX-HOME-2024',
    description: '适合家庭使用的智能练字机器人，轻便易用，让孩子在家也能享受专业的书法指导。',
    badge: '家庭首选',
    price: 1999,
    originalPrice: 2499,
    status: 'published'
  },
  {
    ...mockProduct,
    id: '3',
    name: '专业版练字机器人',
    model: 'YX-PRO-2024',
    description: '面向专业书法培训机构的高端产品，配备更多专业功能和高精度识别系统。',
    badge: '专业版',
    price: 4999,
    originalPrice: 5999,
    status: 'draft'
  }
]

// 产品Mock服务类
export class ProductMockService {
  // 生成产品列表
  static generateProducts(count: number = 10): Product[] {
    const products: Product[] = []

    for (let i = 0; i < count; i++) {
      const baseProduct = mockProducts[i % mockProducts.length]
      products.push({
        ...baseProduct,
        id: `product-${i + 1}`,
        name: `${baseProduct.name} ${i + 1}`,
        model: `${baseProduct.model}-${String(i + 1).padStart(3, '0')}`,
        createdAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
      })
    }

    return products
  }

  // 生成产品分类
  static generateProductCategories() {
    return [
      { id: '1', name: '教育版', description: '专为教育机构设计', count: 5 },
      { id: '2', name: '家庭版', description: '适合家庭使用', count: 3 },
      { id: '3', name: '专业版', description: '专业书法培训', count: 2 },
      { id: '4', name: '企业版', description: '企业培训专用', count: 1 }
    ]
  }

  // 生成产品评价
  static generateProductReviews(productId: string, count: number = 10) {
    const reviews = []
    const reviewTexts = [
      '产品质量很好，孩子很喜欢用来练字',
      'AI识别很准确，确实能帮助改善书写',
      '教学功能丰富，适合学校使用',
      '设计人性化，操作简单易懂',
      '性价比不错，推荐购买'
    ]

    for (let i = 0; i < count; i++) {
      reviews.push({
        id: `review-${i + 1}`,
        productId,
        userId: `user-${i + 1}`,
        userName: `用户${i + 1}`,
        rating: Math.floor(Math.random() * 2) + 4, // 4-5星
        content: reviewTexts[i % reviewTexts.length],
        createdAt: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toISOString()
      })
    }

    return reviews
  }
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

// 产品Mock API
export const mockProductAPI = {
  // 获取产品列表
  async getProducts(params?: { page?: number; pageSize?: number; keyword?: string }) {
    await mockDelay()
    const { page = 1, pageSize = 10, keyword = '' } = params || {}

    let products = ProductMockService.generateProducts(30)

    // 关键词筛选
    if (keyword) {
      products = products.filter(product =>
        product.name.includes(keyword) ||
        product.description.includes(keyword)
      )
    }

    // 分页
    const total = products.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = products.slice(start, end)

    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 获取产品详情
  async getProduct(id: string) {
    await mockDelay()
    const products = ProductMockService.generateProducts(30)
    const product = products.find(p => p.id === id)

    if (!product) {
      return createMockResponse(null, 404, '产品不存在')
    }

    return createMockResponse(product)
  },

  // 获取产品分类
  async getProductCategories() {
    await mockDelay()
    const categories = ProductMockService.generateProductCategories()
    return createMockResponse(categories)
  },

  // 获取产品评价
  async getProductReviews(productId: string, params?: { page?: number; pageSize?: number }) {
    await mockDelay()
    const { page = 1, pageSize = 10 } = params || {}

    const reviews = ProductMockService.generateProductReviews(productId, 50)

    // 分页
    const total = reviews.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = reviews.slice(start, end)

    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 创建产品
  async createProduct(productData: Partial<Product>) {
    await mockDelay()
    const newProduct = {
      ...mockProduct,
      ...productData,
      id: `product-${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }

    return createMockResponse(newProduct, 201, '产品创建成功')
  },

  // 更新产品
  async updateProduct(id: string, productData: Partial<Product>) {
    await mockDelay()
    const updatedProduct = {
      ...mockProduct,
      ...productData,
      id,
      updatedAt: new Date().toISOString()
    }

    return createMockResponse(updatedProduct, 200, '产品更新成功')
  },

  // 删除产品
  async deleteProduct(id: string) {
    await mockDelay()
    return createMockResponse(null, 200, '产品删除成功')
  }
}