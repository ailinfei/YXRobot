/**
 * 设备管理模块 API 调用服务
 * 任务21：实现前端API调用服务
 * 
 * 基于ManagedDevice模块的完整API接口实现
 * 与后端ManagedDeviceController完全对应
 */

import request from '@/utils/request'
import type { 
  Device, 
  DeviceStats, 
  DeviceLog,
  CreateDeviceData, 
  UpdateDeviceData, 
  DeviceQueryParams,
  DeviceListResponse,
  MaintenanceRecord
} from '@/types/device'
import type { ApiResponse, BatchOperationResponse } from '@/types/api'

// 设备管理API接口 - 完全匹配后端ManagedDeviceController
export const managedDeviceAPI = {
  /**
   * 获取设备列表
   * 对应后端: GET /api/admin/devices
   */
  async getDevices(params: DeviceQueryParams = {}): Promise<DeviceListResponse> {
    try {
      const response = await request.get<ApiResponse<DeviceListResponse>>('/api/admin/devices', { 
        params: {
          page: params.page || 1,
          pageSize: params.pageSize || 20,
          keyword: params.keyword || '',
          status: params.status || '',
          model: params.model || '',
          customerId: params.customerId || '',
          sortBy: params.sortBy || 'createdAt',
          sortOrder: params.sortOrder || 'desc',
          startDate: params.dateRange?.[0] || '',
          endDate: params.dateRange?.[1] || ''
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
   * 获取设备详情
   * 对应后端: GET /api/admin/devices/{id}
   */
  async getDeviceById(id: string | number): Promise<Device> {
    try {
      const response = await request.get<ApiResponse<Device>>(`/api/admin/devices/${id}`)
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备详情失败')
      }
    } catch (error) {
      console.error('获取设备详情失败:', error)
      throw error
    }
  },

  /**
   * 根据序列号获取设备
   * 对应后端: GET /api/admin/devices/serial/{serialNumber}
   */
  async getDeviceBySerialNumber(serialNumber: string): Promise<Device> {
    try {
      const response = await request.get<ApiResponse<Device>>(`/api/admin/devices/serial/${serialNumber}`)
      
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
   * 创建设备
   * 对应后端: POST /api/admin/devices
   */
  async createDevice(deviceData: CreateDeviceData): Promise<Device> {
    try {
      const response = await request.post<ApiResponse<Device>>('/api/admin/devices', {
        serialNumber: deviceData.serialNumber,
        model: deviceData.model,
        customerId: deviceData.customerId,
        firmwareVersion: deviceData.firmwareVersion || '1.0.0',
        specifications: deviceData.specifications,
        configuration: deviceData.configuration,
        location: deviceData.location,
        notes: deviceData.notes
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '创建设备失败')
      }
    } catch (error) {
      console.error('创建设备失败:', error)
      throw error
    }
  },

  /**
   * 更新设备
   * 对应后端: PUT /api/admin/devices/{id}
   */
  async updateDevice(id: string | number, deviceData: UpdateDeviceData): Promise<Device> {
    try {
      const response = await request.put<ApiResponse<Device>>(`/api/admin/devices/${id}`, deviceData)
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '更新设备失败')
      }
    } catch (error) {
      console.error('更新设备失败:', error)
      throw error
    }
  },

  /**
   * 删除设备
   * 对应后端: DELETE /api/admin/devices/{id}
   */
  async deleteDevice(id: string | number): Promise<void> {
    try {
      const response = await request.delete<ApiResponse<void>>(`/api/admin/devices/${id}`)
      
      if (response.code !== 200) {
        throw new Error(response.message || '删除设备失败')
      }
    } catch (error) {
      console.error('删除设备失败:', error)
      throw error
    }
  },

  /**
   * 更新设备状态
   * 对应后端: PATCH /api/admin/devices/{id}/status
   */
  async updateDeviceStatus(
    id: string | number, 
    status: Device['status']
  ): Promise<{ id: string; status: string; updatedAt: string }> {
    try {
      const response = await request.patch<ApiResponse<{ id: string; status: string; updatedAt: string }>>(
        `/api/admin/devices/${id}/status`, 
        { status }
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '更新设备状态失败')
      }
    } catch (error) {
      console.error('更新设备状态失败:', error)
      throw error
    }
  },

  /**
   * 重启设备
   * 对应后端: POST /api/admin/devices/{id}/reboot
   */
  async rebootDevice(id: string | number): Promise<{ message: string }> {
    try {
      const response = await request.post<ApiResponse<{ message: string }>>(
        `/api/admin/devices/${id}/reboot`
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '重启设备失败')
      }
    } catch (error) {
      console.error('重启设备失败:', error)
      throw error
    }
  },

  /**
   * 激活设备
   * 对应后端: POST /api/admin/devices/{id}/activate
   */
  async activateDevice(id: string | number): Promise<{ id: string; status: string; activatedAt: string }> {
    try {
      const response = await request.post<ApiResponse<{ id: string; status: string; activatedAt: string }>>(
        `/api/admin/devices/${id}/activate`
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '激活设备失败')
      }
    } catch (error) {
      console.error('激活设备失败:', error)
      throw error
    }
  },

  /**
   * 推送固件
   * 对应后端: POST /api/admin/devices/{id}/firmware
   */
  async pushFirmware(
    id: string | number, 
    version?: string
  ): Promise<{ message: string; version: string }> {
    try {
      const response = await request.post<ApiResponse<{ message: string; version: string }>>(
        `/api/admin/devices/${id}/firmware`, 
        { version }
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '推送固件失败')
      }
    } catch (error) {
      console.error('推送固件失败:', error)
      throw error
    }
  },

  /**
   * 获取设备日志
   * 对应后端: GET /api/admin/devices/{id}/logs
   */
  async getDeviceLogs(
    id: string | number, 
    params?: { 
      page?: number
      pageSize?: number
      level?: string
      category?: string
      dateRange?: [string, string]
    }
  ): Promise<{
    list: DeviceLog[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get<ApiResponse<{
        list: DeviceLog[]
        total: number
        page: number
        pageSize: number
      }>>(`/api/admin/devices/${id}/logs`, { 
        params: {
          page: params?.page || 1,
          pageSize: params?.pageSize || 20,
          level: params?.level || '',
          category: params?.category || '',
          startDate: params?.dateRange?.[0] || '',
          endDate: params?.dateRange?.[1] || ''
        }
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备日志失败')
      }
    } catch (error) {
      console.error('获取设备日志失败:', error)
      throw error
    }
  },

  /**
   * 获取设备统计数据
   * 对应后端: GET /api/admin/devices/stats
   */
  async getDeviceStats(params?: {
    startDate?: string
    endDate?: string
  }): Promise<DeviceStats> {
    try {
      const response = await request.get<ApiResponse<DeviceStats>>('/api/admin/devices/stats', { 
        params: {
          startDate: params?.startDate || '',
          endDate: params?.endDate || ''
        }
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取设备统计失败')
      }
    } catch (error) {
      console.error('获取设备统计失败:', error)
      throw error
    }
  },

  /**
   * 获取客户选项
   * 对应后端: GET /api/admin/customers/options
   */
  async getCustomerOptions(): Promise<Array<{ id: string; name: string }>> {
    try {
      const response = await request.get<ApiResponse<Array<{ id: string; name: string }>>>(
        '/api/admin/customers/options'
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取客户选项失败')
      }
    } catch (error) {
      console.error('获取客户选项失败:', error)
      throw error
    }
  },

  /**
   * 批量推送固件
   * 对应后端: POST /api/admin/devices/batch/firmware
   */
  async batchPushFirmware(
    deviceIds: (string | number)[], 
    version?: string
  ): Promise<BatchOperationResponse['data']> {
    try {
      const response = await request.post<BatchOperationResponse>(
        '/api/admin/devices/batch/firmware', 
        { 
          deviceIds: deviceIds.map(id => String(id)), 
          version 
        }
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '批量推送固件失败')
      }
    } catch (error) {
      console.error('批量推送固件失败:', error)
      throw error
    }
  },

  /**
   * 批量重启设备
   * 对应后端: POST /api/admin/devices/batch/reboot
   */
  async batchRebootDevices(deviceIds: (string | number)[]): Promise<BatchOperationResponse['data']> {
    try {
      const response = await request.post<BatchOperationResponse>(
        '/api/admin/devices/batch/reboot', 
        { 
          deviceIds: deviceIds.map(id => String(id))
        }
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '批量重启设备失败')
      }
    } catch (error) {
      console.error('批量重启设备失败:', error)
      throw error
    }
  },

  /**
   * 批量删除设备
   * 对应后端: DELETE /api/admin/devices/batch
   */
  async batchDeleteDevices(deviceIds: (string | number)[]): Promise<BatchOperationResponse['data']> {
    try {
      const response = await request.delete<BatchOperationResponse>(
        '/api/admin/devices/batch', 
        { 
          data: { 
            deviceIds: deviceIds.map(id => String(id))
          }
        }
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '批量删除设备失败')
      }
    } catch (error) {
      console.error('批量删除设备失败:', error)
      throw error
    }
  },

  /**
   * 导出设备数据
   * 对应后端: GET /api/admin/devices/export
   */
  async exportDevices(params: DeviceQueryParams): Promise<{ 
    downloadUrl: string
    filename: string 
    fileSize?: number
    expiresAt?: string
  }> {
    try {
      const response = await request.get<ApiResponse<{ 
        downloadUrl: string
        filename: string 
        fileSize?: number
        expiresAt?: string
      }>>('/api/admin/devices/export', { 
        params: {
          keyword: params.keyword || '',
          status: params.status || '',
          model: params.model || '',
          customerId: params.customerId || '',
          startDate: params.dateRange?.[0] || '',
          endDate: params.dateRange?.[1] || ''
        }
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '导出设备数据失败')
      }
    } catch (error) {
      console.error('导出设备数据失败:', error)
      throw error
    }
  },

  /**
   * 添加维护记录
   * 对应后端: POST /api/admin/devices/{id}/maintenance
   */
  async addMaintenanceRecord(
    deviceId: string | number,
    maintenanceData: {
      type: MaintenanceRecord['type']
      description: string
      technician: string
      cost?: number
      parts?: string[]
      notes?: string
    }
  ): Promise<MaintenanceRecord> {
    try {
      const response = await request.post<ApiResponse<MaintenanceRecord>>(
        `/api/admin/devices/${deviceId}/maintenance`,
        maintenanceData
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '添加维护记录失败')
      }
    } catch (error) {
      console.error('添加维护记录失败:', error)
      throw error
    }
  },

  /**
   * 更新维护记录
   * 对应后端: PUT /api/admin/devices/{deviceId}/maintenance/{id}
   */
  async updateMaintenanceRecord(
    deviceId: string | number,
    maintenanceId: string | number,
    maintenanceData: Partial<MaintenanceRecord>
  ): Promise<MaintenanceRecord> {
    try {
      const response = await request.put<ApiResponse<MaintenanceRecord>>(
        `/api/admin/devices/${deviceId}/maintenance/${maintenanceId}`,
        maintenanceData
      )
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '更新维护记录失败')
      }
    } catch (error) {
      console.error('更新维护记录失败:', error)
      throw error
    }
  },

  /**
   * 获取维护记录列表
   * 对应后端: GET /api/admin/devices/{id}/maintenance
   */
  async getMaintenanceRecords(
    deviceId: string | number,
    params?: {
      page?: number
      pageSize?: number
      type?: string
      status?: string
    }
  ): Promise<{
    list: MaintenanceRecord[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get<ApiResponse<{
        list: MaintenanceRecord[]
        total: number
        page: number
        pageSize: number
      }>>(`/api/admin/devices/${deviceId}/maintenance`, { 
        params: {
          page: params?.page || 1,
          pageSize: params?.pageSize || 20,
          type: params?.type || '',
          status: params?.status || ''
        }
      })
      
      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '获取维护记录失败')
      }
    } catch (error) {
      console.error('获取维护记录失败:', error)
      throw error
    }
  }
}

// 默认导出
export default managedDeviceAPI

// 命名导出（保持兼容性）
export { managedDeviceAPI as deviceAPI }