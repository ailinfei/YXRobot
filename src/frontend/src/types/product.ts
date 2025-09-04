// 产品管理相关类型定义 - 匹配后端实体类

// 产品图片接口 - 匹配后端ProductImage实体
export interface ProductImage {
  id: string
  url: string
  alt: string
  order: number
  type: 'main' | 'gallery' | 'detail'
  size?: number
  width?: number
  height?: number
  format?: string
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

// 主要产品接口 - 完全匹配后端Product实体
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
  
  // 库存信息
  stock?: number
  minStock?: number
  maxStock?: number
  stockStatus?: 'in_stock' | 'low_stock' | 'out_of_stock'
  
  // 分类信息
  categoryId?: string
  categoryName?: string
  tags?: string[]
  
  // SEO信息
  seoTitle?: string
  seoDescription?: string
  seoKeywords?: string[]
  
  // 销售信息
  salesCount?: number
  viewCount?: number
  rating?: number
  reviewCount?: number
  
  // 时间字段
  created_at?: string
  updated_at?: string
  createdAt?: string
  updatedAt?: string
  publishedAt?: string
  
  // 创建者信息
  createdBy?: string
  updatedBy?: string
}

// 产品创建数据接口 - 匹配后端CreateProductRequest
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
  
  // 库存信息
  stock?: number
  minStock?: number
  maxStock?: number
  
  // 分类信息
  categoryId?: string
  tags?: string[]
  
  // SEO信息
  seoTitle?: string
  seoDescription?: string
  seoKeywords?: string[]
  
  // 其他信息
  coverImageUrl?: string
  weight?: number
  dimensions?: {
    length: number
    width: number
    height: number
  }
}

export interface UpdateProductData extends Partial<CreateProductData> {
  status?: 'draft' | 'published' | 'archived'
}

// 产品查询参数接口 - 匹配后端ProductQueryParams
export interface ProductQueryParams {
  page?: number
  size?: number
  pageSize?: number
  status?: string
  keyword?: string
  categoryId?: string
  minPrice?: number
  maxPrice?: number
  stockStatus?: 'in_stock' | 'low_stock' | 'out_of_stock'
  sortBy?: 'name' | 'price' | 'createdAt' | 'salesCount' | 'rating'
  sortOrder?: 'ASC' | 'DESC'
  tags?: string
  featured?: boolean
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

// 产品统计接口 - 匹配后端ProductStats
export interface ProductStats {
  total: number
  published: number
  draft: number
  archived: number
  lowStock: number
  outOfStock: number
  totalValue: number
  averagePrice: number
}

// 产品列表响应接口
export interface ProductListResponse {
  list: Product[]
  total: number
  stats?: ProductStats
}

// 产品搜索建议接口
export interface ProductSearchSuggestion {
  id: string
  name: string
  model: string
  price: number
  image?: string
  stock?: number
  status: string
}

// 产品分类接口
export interface ProductCategory {
  id: string
  name: string
  description?: string
  parentId?: string
  level: number
  sort: number
  status: 'active' | 'inactive'
  productCount?: number
  children?: ProductCategory[]
}

// 产品筛选选项接口
export interface ProductFilterOptions {
  categories: Array<{ label: string; value: string; count?: number }>
  priceRanges: Array<{ label: string; min: number; max: number; count?: number }>
  statuses: Array<{ label: string; value: string; count?: number }>
  stockStatuses: Array<{ label: string; value: string; count?: number }>
  tags: Array<{ label: string; value: string; count?: number }>
}

// 产品批量操作接口
export interface ProductBatchOperation {
  productIds: (string | number)[]
  operation: 'delete' | 'updateStatus' | 'updateCategory' | 'updatePrice' | 'addTags' | 'removeTags'
  data?: {
    status?: string
    categoryId?: string
    priceMultiplier?: number
    tags?: string[]
  }
}

// 产品导出参数接口
export interface ProductExportParams extends ProductQueryParams {
  format?: 'excel' | 'csv'
  fields?: string[]
  includeImages?: boolean
  includeSpecs?: boolean
}

// 产品库存更新接口
export interface ProductStockUpdate {
  productId: string
  quantity: number
  operation: 'add' | 'subtract' | 'set'
  reason?: string
  operator?: string
}

// 产品价格历史接口
export interface ProductPriceHistory {
  id: string
  productId: string
  oldPrice: number
  newPrice: number
  changeReason: string
  changedBy: string
  changedAt: string
}

// 产品评价接口
export interface ProductReview {
  id: string
  productId: string
  customerId: string
  customerName: string
  rating: number
  title?: string
  content: string
  images?: string[]
  status: 'pending' | 'approved' | 'rejected'
  createdAt: string
  updatedAt?: string
}

// 产品规格模板接口
export interface ProductSpecTemplate {
  id: string
  name: string
  categoryId?: string
  specifications: Array<{
    name: string
    type: 'text' | 'number' | 'select' | 'boolean'
    required: boolean
    options?: string[]
    unit?: string
    defaultValue?: any
  }>
}

// 产品变体接口（如不同颜色、尺寸等）
export interface ProductVariant {
  id: string
  productId: string
  name: string
  sku: string
  price: number
  stock: number
  attributes: Record<string, string> // 如 { color: 'red', size: 'L' }
  images?: ProductImage[]
  status: 'active' | 'inactive'
}

// 产品组合接口（套装产品）
export interface ProductBundle {
  id: string
  name: string
  description: string
  products: Array<{
    productId: string
    quantity: number
    discount?: number
  }>
  totalPrice: number
  discountPrice: number
  savings: number
  status: 'active' | 'inactive'
}

// 产品推荐接口
export interface ProductRecommendation {
  id: string
  name: string
  reason: string
  score: number
  image?: string
  price: number
  originalPrice?: number
}

// 产品详情扩展接口
export interface ProductDetailExtended extends Product {
  category?: ProductCategory
  variants?: ProductVariant[]
  bundles?: ProductBundle[]
  reviews?: ProductReview[]
  recommendations?: ProductRecommendation[]
  priceHistory?: ProductPriceHistory[]
  stockHistory?: Array<{
    date: string
    quantity: number
    operation: string
    reason?: string
  }>
}