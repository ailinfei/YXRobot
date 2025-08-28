// 产品管理相关类型定义

export interface ProductImage {
  id: string
  url: string
  alt: string
  order: number
  type: 'main' | 'gallery' | 'detail'
}

export interface ProductSpec {
  category: string
  items: Array<{
    name: string
    value: string
    unit?: string
  }>
}

export interface ProductPrice {
  currency: string
  amount: number
  originalAmount?: number
  discount?: number
}

export interface ProductFeature {
  title: string
  description: string
  icon?: string
}

export interface Product {
  id: number | string
  name: string
  model: string
  description: string
  badge?: string
  features?: string[]
  productFeatures?: ProductFeature[]
  specifications?: Record<string, string>
  price: number
  originalPrice?: number
  prices?: ProductPrice[]
  images?: ProductImage[]
  highlights?: string[]
  cover_image_url?: string
  coverImageUrl?: string
  status: 'draft' | 'published' | 'archived'
  created_at?: string
  updated_at?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateProductData {
  name: string
  model: string
  description: string
  price: number
  status: 'draft' | 'published' | 'archived'
  cover_files?: File[]
  badge?: string
  features?: string[]
  productFeatures?: ProductFeature[]
  specifications?: Record<string, string>
  originalPrice?: number
  highlights?: string[]
}

export interface UpdateProductData extends Partial<CreateProductData> {
  status?: 'draft' | 'published' | 'archived'
}

export interface ProductQueryParams {
  page?: number
  size?: number
  pageSize?: number
  status?: string
  keyword?: string
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

export interface PageResult<T = any> {
  list: T[]
  total: number
  page: number
  size: number
  totalPages?: number
}

// 产品媒体类型
export interface ProductMedia {
  id: number
  product_id: number
  media_type: 'image' | 'video'
  media_url: string
  sort_order: number
}