/**
 * Mock数据拦截器
 * 拦截API请求并返回Mock数据
 */

import type { AxiosInstance } from 'axios'
import { mockPlatformLinkAPI } from '@/api/mock'
import { mockCharityAPI } from '@/api/mock/charity'

// Mock配置
const MOCK_CONFIG = {
  enabled: import.meta.env.VITE_USE_MOCK === 'true' || import.meta.env.NODE_ENV === 'development',
  delay: 200
}

// Mock路由映射
const mockRoutes = new Map([
  // 平台链接管理
  ['GET:/api/platform-links', mockPlatformLinkAPI.getPlatformLinks],
  ['GET:/api/platform-stats', mockPlatformLinkAPI.getPlatformLinkStats],
  ['GET:/api/platform-stats/regions', mockPlatformLinkAPI.getRegionConfigs],
  ['GET:/api/platform-links/:id', mockPlatformLinkAPI.getPlatformLinkDetail],
  ['POST:/api/platform-links', mockPlatformLinkAPI.createPlatformLink],
  ['PUT:/api/platform-links/:id', mockPlatformLinkAPI.updatePlatformLink],
  ['DELETE:/api/platform-links/:id', mockPlatformLinkAPI.deletePlatformLink],
  ['PUT:/api/platform-links/batch', mockPlatformLinkAPI.batchUpdatePlatformLinks],
  ['DELETE:/api/platform-links/batch', mockPlatformLinkAPI.batchDeletePlatformLinks],
  ['POST:/api/platform-links/:id/validate', mockPlatformLinkAPI.validatePlatformLink],
  ['POST:/api/platform-links/batch-validate', mockPlatformLinkAPI.batchValidatePlatformLinks],
  ['PATCH:/api/platform-links/:id/toggle', mockPlatformLinkAPI.togglePlatformLink],
  ['GET:/api/platform-links/:id/analytics', mockPlatformLinkAPI.getLinkAnalytics],
  
  // 公益慈善管理
  ['GET:/api/admin/charity/stats', mockCharityAPI.getCharityStats],
  ['GET:/api/admin/charity/enhanced-stats', mockCharityAPI.getCharityStats],
  ['GET:/api/admin/charity/projects', mockCharityAPI.getCharityProjects],
  ['GET:/api/admin/charity/institutions', mockCharityAPI.getCharityInstitutions],
  ['GET:/api/admin/charity/activities', mockCharityAPI.getCharityActivities],
  ['GET:/api/admin/charity/chart-data', mockCharityAPI.getCharityStats],
  ['GET:/api/admin/charity/success-stories', () => ({ code: 200, data: [], message: 'success' })],
  ['POST:/api/admin/charity/projects', mockCharityAPI.createCharityProject],
  ['PUT:/api/admin/charity/projects/:id', mockCharityAPI.updateCharityProject],
  ['DELETE:/api/admin/charity/projects/:id', mockCharityAPI.deleteCharityProject],
  ['POST:/api/admin/charity/institutions', () => ({ code: 200, data: {}, message: 'success' })],
  ['PUT:/api/admin/charity/institutions/:id', () => ({ code: 200, data: {}, message: 'success' })],
  ['DELETE:/api/admin/charity/institutions/:id', () => ({ code: 200, data: null, message: 'success' })],
  ['POST:/api/admin/charity/activities', () => ({ code: 200, data: {}, message: 'success' })],
  ['PUT:/api/admin/charity/activities/:id', () => ({ code: 200, data: {}, message: 'success' })],
  ['DELETE:/api/admin/charity/activities/:id', () => ({ code: 200, data: null, message: 'success' })],
  ['POST:/api/admin/charity/refresh-stats', mockCharityAPI.getCharityStats],
  ['PUT:/api/admin/charity/stats', mockCharityAPI.getCharityStats],
])

// 路径参数匹配
const matchRoute = (method: string, url: string): string | null => {
  const key = `${method}:${url.split('?')[0]}`
  
  // 直接匹配
  if (mockRoutes.has(key)) {
    return key
  }
  
  // 参数匹配
  for (const [routeKey] of mockRoutes) {
    const [routeMethod, routePath] = routeKey.split(':')
    if (routeMethod !== method) continue
    
    const urlWithoutQuery = url.split('?')[0]
    const routeRegex = routePath.replace(/:([^/]+)/g, '([^/]+)')
    const regex = new RegExp(`^${routeRegex}$`)
    
    if (regex.test(urlWithoutQuery)) {
      return routeKey
    }
  }
  
  return null
}

// 提取路径参数
const extractParams = (routePath: string, actualPath: string): Record<string, string> => {
  const params: Record<string, string> = {}
  const routeParts = routePath.split('/')
  const actualParts = actualPath.split('/')
  
  for (let i = 0; i < routeParts.length; i++) {
    const routePart = routeParts[i]
    const actualPart = actualParts[i]
    
    if (routePart?.startsWith(':') && actualPart) {
      const paramName = routePart.slice(1)
      params[paramName] = actualPart
    }
  }
  
  return params
}

// 安装Mock拦截器
export const setupMockInterceptor = (apiClient: AxiosInstance) => {
  if (!MOCK_CONFIG.enabled) {
    console.log('🔧 Mock拦截器已禁用')
    return
  }

  console.log('🚀 Mock拦截器已启用')
  console.log('📊 已注册Mock路由数量:', mockRoutes.size)

  // 请求拦截器
  apiClient.interceptors.request.use(
    async (config) => {
      const method = config.method?.toUpperCase() || 'GET'
      const url = config.url || ''
      
      // 移除baseURL前缀
      const cleanUrl = url.replace(config.baseURL || '', '')
      
      const matchedRoute = matchRoute(method, cleanUrl)
      
      if (matchedRoute) {
        console.log('🎯 Mock拦截:', method, cleanUrl)
        
        const mockHandler = mockRoutes.get(matchedRoute)
        if (mockHandler) {
          try {
            // 提取路径参数
            const [, routePath] = matchedRoute.split(':')
            const pathParams = extractParams(routePath, cleanUrl.split('?')[0])
            
            // 合并参数
            const allParams = {
              ...config.params,
              ...pathParams,
              ...config.data
            }
            
            // 模拟网络延迟
            await new Promise(resolve => setTimeout(resolve, MOCK_CONFIG.delay))
            
            // 调用Mock处理函数
            const mockResponse = await mockHandler(allParams)
            
            // 创建模拟的axios响应对象
            const response = {
              data: mockResponse,
              status: 200,
              statusText: 'OK',
              headers: {
                'content-type': 'application/json'
              },
              config,
              request: {}
            }
            
            // 使用自定义adapter直接返回Mock响应
            config.adapter = () => Promise.resolve(response)
            return config
          } catch (error) {
            console.error('Mock处理错误:', error)
            throw error
          }
        }
      }
      
      return config
    },
    (error) => {
      console.error('请求拦截器错误:', error)
      return Promise.reject(error)
    }
  )
}

// 获取Mock配置信息
export const getMockInfo = () => {
  return {
    enabled: MOCK_CONFIG.enabled,
    routeCount: mockRoutes.size,
    delay: MOCK_CONFIG.delay,
    routes: Array.from(mockRoutes.keys()).sort()
  }
}