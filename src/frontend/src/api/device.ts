/**
 * 通用设备 API 调用服务
 * 用于其他模块的设备相关功能
 * 
 * 注意：ManagedDevice 模块请使用 managedDevice.ts
 */

import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 基础设备信息接口
export interface BasicDevice {
  id: string
  serialNumber: string
  model: string
  status: 'active' | 'inactive' | 'maintenance' | 'offline'
  name?: string
  location?: string
}

// 通用设备API接口 - 用于其他模块
export const deviceAPI = {
  /**
   * 获取基础设备列表
   * 用于其他模块的设备选择器等场景
   */
  async getBasicDevices(params?: {
    keyword?: string
    status?: string
    limit?: number
  }): Promise<BasicDevice[]> {
    try {
      const response = await request.get<ApiResponse<BasicDevice[]>>('/api/devices/basic', { 
        params: {
          keyword: params?.keyword || '',
          status: params?.status || '',
          limit: params?.limit || 50
        }
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备列表失败')
      }
    } catch (error) {
      console.error('获取设备列表失败:', error)
      throw error
    }
  },

  /**
   * 获取设备基础信息
   * 用于其他模块需要设备基础信息的场景
   */
  async getBasicDeviceInfo(id: string | number): Promise<BasicDevice> {
    try {
      const response = await request.get<ApiResponse<BasicDevice>>(`/api/devices/basic/${id}`)
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备信息失败')
      }
    } catch (error) {
      console.error('获取设备信息失败:', error)
      throw error
    }
  },

  /**
   * 根据序列号获取设备基础信息
   * 用于其他模块通过序列号查找设备
   */
  async getBasicDeviceBySerialNumber(serialNumber: string): Promise<BasicDevice> {
    try {
      const response = await request.get<ApiResponse<BasicDevice>>(`/api/devices/basic/serial/${serialNumber}`)
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备信息失败')
      }
    } catch (error) {
      console.error('根据序列号获取设备失败:', error)
      throw error
    }
  },

  /**
   * 检查设备状态
   * 用于其他模块检查设备是否可用
   */
  async checkDeviceStatus(id: string | number): Promise<{ 
    id: string
    status: BasicDevice['status']
    isOnline: boolean
    lastSeen?: string
  }> {
    try {
      const response = await request.get<ApiResponse<{ 
        id: string
        status: BasicDevice['status']
        isOnline: boolean
        lastSeen?: string
      }>>(`/api/devices/basic/${id}/status`)
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '检查设备状态失败')
      }
    } catch (error) {
      console.error('检查设备状态失败:', error)
      throw error
    }
  }
}

// 默认导出
export default deviceAPI