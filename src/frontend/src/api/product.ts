import { get, post, put, del } from '@/utils/request'
import type {
  Product,
  CreateProductData,
  UpdateProductData,
  ProductQueryParams,
  ProductImage,
  ApiResponse,
  PageResult
} from '@/types/product'

// 产品管理API
export const productApi = {
  // 获取产品列表
  async getProducts(params?: ProductQueryParams) {
    console.log('🔗 API调用: /api/admin/products', params)
    const result = await get<ApiResponse<PageResult<Product>>>('/api/admin/products', params)
    console.log('🔗 API响应:', result)
    return result
  },

  // 获取产品详情
  async getProduct(id: string) {
    return get<ApiResponse<Product>>(`/api/admin/products/${id}`)
  },

  // 创建产品（JSON格式）
  async createProduct(data: CreateProductData) {
    return post<ApiResponse<Product>>('/api/admin/products', data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 创建产品（支持文件上传）
  async createProductWithFiles(data: CreateProductData) {
    const formData = new FormData()

    // 添加基本字段
    formData.append('name', data.name)
    formData.append('model', data.model)
    formData.append('price', data.price.toString())
    formData.append('status', data.status)

    if (data.description !== undefined) {
      formData.append('description', data.description)
    }

    // 添加封面文件
    if (data.cover_files && data.cover_files.length > 0) {
      data.cover_files.forEach((file) => {
        formData.append('coverFiles', file)
      })
    }

    return post<ApiResponse<Product>>('/api/admin/products', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 更新产品（JSON格式）
  async updateProduct(id: string, data: UpdateProductData) {
    return put<ApiResponse<Product>>(`/api/admin/products/${id}`, data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 更新产品（支持文件上传）
  async updateProductWithFiles(id: string, data: UpdateProductData, keepExistingCover: boolean = true) {
    const formData = new FormData()

    // 添加基本字段（只有当值存在时才添加）
    if (data.name !== undefined) {
      formData.append('name', data.name)
    }
    if (data.model !== undefined) {
      formData.append('model', data.model)
    }
    if (data.price !== undefined) {
      formData.append('price', data.price.toString())
    }
    if (data.status !== undefined) {
      formData.append('status', data.status)
    }
    formData.append('keepExistingCover', keepExistingCover.toString())

    if (data.description !== undefined) {
      formData.append('description', data.description)
    }

    // 添加封面文件
    if (data.cover_files && data.cover_files.length > 0) {
      data.cover_files.forEach((file) => {
        formData.append('coverFiles', file)
      })
    }

    return put<ApiResponse<Product>>(`/api/admin/products/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除产品
  async deleteProduct(id: string) {
    return del<ApiResponse<void>>(`/api/admin/products/${id}`)
  },

  // 上传产品图片
  async uploadProductImage(productId: string, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', productId)

    return post<ProductImage>('/api/admin/products/upload-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 更新图片顺序
  async updateImageOrder(productId: string, imageOrders: Array<{ id: string; order: number }>) {
    return put(`/api/admin/products/${productId}/image-order`, { imageOrders })
  },

  // 设置主图
  async setMainImage(productId: string, imageId: string) {
    return put(`/api/admin/products/${productId}/main-image`, { imageId })
  },

  // 删除产品图片
  async deleteProductImage(productId: string, imageId: string) {
    return del(`/api/admin/products/${productId}/images/${imageId}`)
  },

  // 发布产品到官网
  async publishProduct(id: string) {
    return post(`/api/admin/products/${id}/publish`)
  },

  // 预览产品
  async previewProduct(id: string) {
    return get<Product>(`/api/admin/products/${id}/preview`)
  },

  // 获取产品媒体列表
  async getProductMedia(productId: string) {
    return get<ApiResponse<Array<any>>>(`/api/admin/products/${productId}/media`)
  },

  // 上传产品媒体
  async uploadProductMedia(productId: string, mediaType: string, sortOrder: number, mediaFile: File) {
    const formData = new FormData()
    formData.append('mediaType', mediaType)
    formData.append('sortOrder', sortOrder.toString())
    formData.append('mediaFile', mediaFile)

    return post<ApiResponse<any>>(`/api/admin/products/${productId}/media`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 更新产品媒体信息
  async updateProductMedia(mediaId: string, sortOrder: number) {
    return put<ApiResponse<any>>(`/api/admin/products/media/${mediaId}`, {
      sortOrder
    })
  },

  // 删除产品媒体
  async deleteProductMedia(mediaId: string) {
    return del<ApiResponse<void>>(`/api/admin/products/media/${mediaId}`)
  },

  // 创建产品（使用已上传的文件URL）
  async createProductWithUrls(data: CreateProductData & { cover_image_url?: string }) {
    return post<ApiResponse<Product>>('/api/admin/products/with-urls', data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 更新产品（使用已上传的文件URL）
  async updateProductWithUrls(id: string, data: UpdateProductData & { cover_image_url?: string }) {
    return put<ApiResponse<Product>>(`/api/admin/products/${id}/with-urls`, data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  }
}