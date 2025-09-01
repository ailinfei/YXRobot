/**
 * 公益项目管理API服务
 * 提供公益项目相关的API接口调用
 */

import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'
import type {
  CharityStats,
  CharityProject,
  CharityInstitution,
  CharityActivity
} from './mock/charity'

// 分页参数接口
interface PaginationParams {
  page?: number
  size?: number
  keyword?: string
  status?: string
  type?: string
}

// 分页响应接口
interface PaginatedResponse<T> {
  list: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 公益统计数据扩展接口
export interface EnhancedCharityStats extends CharityStats {
  // 增长趋势数据
  trends: {
    beneficiariesTrend: number[]
    institutionsTrend: number[]
    projectsTrend: number[]
    fundingTrend: number[]
  }
  // 月度对比数据
  monthlyComparison: {
    beneficiariesChange: number
    institutionsChange: number
    projectsChange: number
    fundingChange: number
  }
  // 实时更新时间
  lastUpdated: string
}

// 成功案例接口
export interface SuccessStory {
  id: number
  title: string
  location: string
  description: string
  impact: string
  testimonial: string
  image: string
  date: string
}

// 图表数据接口
export interface CharityChartData {
  projectStatusData: Array<{ name: string; value: number; color: string }>
  fundingTrendData: {
    months: string[]
    raisedData: number[]
    donatedData: number[]
  }
  regionDistributionData: Array<{ name: string; value: number; color: string }>
  volunteerActivityData: {
    months: string[]
    volunteerData: number[]
    activityData: number[]
  }
  beneficiaryDistribution: Array<{
    category: string
    count: number
    percentage: number
  }>
  institutionTypeDistribution: Array<{
    type: string
    count: number
    percentage: number
  }>
}

/**
 * 公益项目API服务类
 */
export class CharityAPI {

  /**
   * 获取公益统计数据
   */
  static async getCharityStats(): Promise<ApiResponse<CharityStats>> {
    return request.get('/api/admin/charity/stats')
  }

  /**
   * 获取增强的公益统计数据（包含趋势和对比数据）
   */
  static async getEnhancedCharityStats(): Promise<ApiResponse<EnhancedCharityStats>> {
    return request.get('/api/admin/charity/enhanced-stats')
  }

  /**
   * 获取公益项目列表
   */
  static async getCharityProjects(params?: PaginationParams): Promise<ApiResponse<PaginatedResponse<CharityProject>>> {
    return request.get('/api/admin/charity/projects', params)
  }

  /**
   * 获取合作机构列表
   */
  static async getCharityInstitutions(params?: PaginationParams): Promise<ApiResponse<PaginatedResponse<CharityInstitution>>> {
    return request.get('/api/admin/charity/institutions', params)
  }

  /**
   * 获取公益活动列表
   */
  static async getCharityActivities(params?: PaginationParams): Promise<ApiResponse<PaginatedResponse<CharityActivity>>> {
    return request.get('/api/admin/charity/activities', params)
  }

  /**
   * 获取图表数据
   */
  static async getCharityChartData(): Promise<ApiResponse<CharityChartData>> {
    return request.get('/api/admin/charity/chart-data')
  }

  /**
   * 获取成功案例
   */
  static async getSuccessStories(): Promise<ApiResponse<SuccessStory[]>> {
    return request.get('/api/admin/charity/success-stories')
  }

  /**
   * 创建公益项目
   */
  static async createCharityProject(data: Partial<CharityProject>): Promise<ApiResponse<CharityProject>> {
    return request.post('/api/admin/charity/projects', data)
  }

  /**
   * 更新公益项目
   */
  static async updateCharityProject(id: number, data: Partial<CharityProject>): Promise<ApiResponse<CharityProject>> {
    return request.put(`/api/admin/charity/projects/${id}`, data)
  }

  /**
   * 删除公益项目
   */
  static async deleteCharityProject(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/api/admin/charity/projects/${id}`)
  }

  /**
   * 创建合作机构
   */
  static async createCharityInstitution(data: Partial<CharityInstitution>): Promise<ApiResponse<CharityInstitution>> {
    return request.post('/api/admin/charity/institutions', data)
  }

  /**
   * 更新合作机构
   */
  static async updateCharityInstitution(id: number, data: Partial<CharityInstitution>): Promise<ApiResponse<CharityInstitution>> {
    return request.put(`/api/admin/charity/institutions/${id}`, data)
  }

  /**
   * 删除合作机构
   */
  static async deleteCharityInstitution(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/api/admin/charity/institutions/${id}`)
  }

  /**
   * 创建公益活动
   */
  static async createCharityActivity(data: Partial<CharityActivity>): Promise<ApiResponse<CharityActivity>> {
    return request.post('/api/admin/charity/activities', data)
  }

  /**
   * 更新公益活动
   */
  static async updateCharityActivity(id: number, data: Partial<CharityActivity>): Promise<ApiResponse<CharityActivity>> {
    return request.put(`/api/admin/charity/activities/${id}`, data)
  }

  /**
   * 删除公益活动
   */
  static async deleteCharityActivity(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/api/admin/charity/activities/${id}`)
  }

  /**
   * 实时刷新统计数据
   */
  static async refreshCharityStats(): Promise<ApiResponse<EnhancedCharityStats>> {
    return request.post('/api/admin/charity/refresh-stats')
  }

  /**
   * 导出公益数据
   */
  static async exportCharityData(type: 'projects' | 'institutions' | 'activities' | 'all'): Promise<Blob> {
    const response = await request.get(`/api/admin/charity/export/${type}`, undefined, {
      responseType: 'blob'
    })
    return response.data
  }

  /**
   * 更新公益统计数据
   */
  static async updateCharityStats(statsData: any): Promise<ApiResponse<CharityStats>> {
    return request.put('/api/admin/charity/stats', statsData)
  }
}

// 导出默认实例
export default CharityAPI