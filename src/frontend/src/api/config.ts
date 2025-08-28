/**
 * API配置文件
 * 统一管理API相关配置
 */

import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { setupMockInterceptor } from '@/utils/mockInterceptor'

// API基础配置
export const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  withCredentials: true
}

// 创建axios实例
const apiClient: AxiosInstance = axios.create(API_CONFIG)

// 请求拦截器
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加请求时间戳
    if (config.params) {
      config.params._t = Date.now()
    } else {
      config.params = { _t: Date.now() }
    }

    console.log('API请求:', config.method?.toUpperCase(), config.url, config.data || config.params)
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log('API响应:', response.config.url, response.status, response.data)

    // 统一处理响应数据格式
    if (response.data && typeof response.data === 'object') {
      return response.data
    }

    return response
  },
  (error) => {
    console.error('API响应错误:', error.response?.status, error.response?.data || error.message)

    // 统一错误处理
    if (error.response?.status === 401) {
      // 未授权，清除token并跳转登录
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      // 权限不足
      console.error('权限不足')
    } else if (error.response?.status >= 500) {
      // 服务器错误
      console.error('服务器错误')
    }

    return Promise.reject(error)
  }
)

// 设置Mock拦截器
setupMockInterceptor(apiClient)

export default apiClient