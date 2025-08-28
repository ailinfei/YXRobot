import { get } from '@/utils/request'
import type { Product, ProductQueryParams } from '@/types/product'

// 网站公开API
export const websiteApi = {
    // 获取公开产品列表
    async getProducts(params?: ProductQueryParams) {
        return get<{
            list: Product[]
            total: number
            page: number
            size: number
        }>('/api/website/v1/products', params)
    },

    // 获取产品详情
    async getProduct(id: string) {
        return get<Product>(`/api/website/v1/products/${id}`)
    },

    // 获取产品状态统计
    async getStatusStatistics() {
        return get<Array<{ status: string; count: number }>>('/api/website/v1/products/statistics/status')
    }
}