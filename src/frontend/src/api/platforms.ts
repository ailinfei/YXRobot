/**
 * 平台链接管理API
 */

import apiClient from './config'
import type {
  ApiResponse,
  PageResponse,
  PlatformLink,
  PlatformLinkForm,
  LinkValidationResult,
  PlatformLinkStats,
  RegionConfig,
  BatchUpdateRequest
} from './types'

// 获取平台链接列表
export const getPlatformLinks = (params?: {
  page?: number
  pageSize?: number
  platformType?: 'ecommerce' | 'rental'
  region?: string
  languageCode?: string
  linkStatus?: 'active' | 'inactive' | 'checking' | 'error'
  isEnabled?: boolean
}): Promise<ApiResponse<PageResponse<PlatformLink>>> => {
  return apiClient.get('/api/platform-links', { params })
}

// 获取单个平台链接详情
export const getPlatformLink = (id: number): Promise<ApiResponse<PlatformLink>> => {
  return apiClient.get(`/api/platform-links/${id}`)
}

// 创建平台链接
export const createPlatformLink = (data: PlatformLinkForm): Promise<ApiResponse<PlatformLink>> => {
  return apiClient.post('/api/platform-links', data)
}

// 更新平台链接
export const updatePlatformLink = (id: number, data: Partial<PlatformLinkForm>): Promise<ApiResponse<PlatformLink>> => {
  return apiClient.put(`/api/platform-links/${id}`, data)
}

// 删除平台链接
export const deletePlatformLink = (id: number): Promise<ApiResponse<void>> => {
  return apiClient.delete(`/api/platform-links/${id}`)
}

// 批量删除平台链接
export const batchDeletePlatformLinks = (ids: number[]): Promise<ApiResponse<void>> => {
  return apiClient.delete('/api/platform-links/batch', { data: { ids } })
}

// 批量更新平台链接
export const batchUpdatePlatformLinks = (data: BatchUpdateRequest): Promise<ApiResponse<void>> => {
  return apiClient.put('/api/platform-links/batch', data)
}

// 验证链接有效性
export const validatePlatformLink = (id: number): Promise<ApiResponse<LinkValidationResult>> => {
  return apiClient.post(`/api/platform-links/${id}/validate`)
}

// 批量验证链接有效性
export const batchValidatePlatformLinks = (ids: number[]): Promise<ApiResponse<LinkValidationResult[]>> => {
  return apiClient.post('/api/platform-links/batch-validate', { ids })
}

// 获取平台链接统计数据
export const getPlatformLinkStats = (): Promise<ApiResponse<PlatformLinkStats>> => {
  return apiClient.get('/api/platform-stats')
}

// 获取区域配置
export const getRegionConfigs = (): Promise<ApiResponse<RegionConfig[]>> => {
  return apiClient.get('/api/platform-stats/regions')
}

// 启用/禁用平台链接
export const togglePlatformLink = (id: number, isEnabled: boolean): Promise<ApiResponse<PlatformLink>> => {
  return apiClient.patch(`/api/platform-links/${id}/toggle`, { isEnabled })
}

// 批量启用/禁用平台链接
export const batchTogglePlatformLinks = (ids: number[], isEnabled: boolean): Promise<ApiResponse<void>> => {
  return apiClient.patch('/api/platform-links/batch-toggle', { ids, isEnabled })
}