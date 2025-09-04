/**
 * 文件上传服务
 * 为设备管理模块提供文件上传功能支持
 * 任务23：文件上传系统实现（基础框架）
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { ref, readonly } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadFile, UploadRawFile } from 'element-plus'

// 文件上传类型枚举
export enum FileUploadType {
  DEVICE_IMAGE = 'device_image',
  DEVICE_DOCUMENT = 'device_document',
  FIRMWARE_FILE = 'firmware_file',
  DEVICE_MANUAL = 'device_manual',
  DEVICE_CERTIFICATE = 'device_certificate'
}

// 文件上传配置接口
export interface FileUploadConfig {
  type: FileUploadType
  maxSize: number // MB
  allowedTypes: string[]
  uploadUrl: string
}

// 文件上传响应接口
export interface FileUploadResponse {
  code: number
  message: string
  data: {
    fileId: string
    fileName: string
    fileUrl: string
    fileSize: number
    uploadTime: string
  }
}

// 文件上传进度接口
export interface FileUploadProgress {
  percent: number
  loaded: number
  total: number
}

// 文件上传配置
const uploadConfigs: Record<FileUploadType, FileUploadConfig> = {
  [FileUploadType.DEVICE_IMAGE]: {
    type: FileUploadType.DEVICE_IMAGE,
    maxSize: 5, // 5MB
    allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
    uploadUrl: '/api/admin/devices/upload/image'
  },
  [FileUploadType.DEVICE_DOCUMENT]: {
    type: FileUploadType.DEVICE_DOCUMENT,
    maxSize: 10, // 10MB
    allowedTypes: ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'],
    uploadUrl: '/api/admin/devices/upload/document'
  },
  [FileUploadType.FIRMWARE_FILE]: {
    type: FileUploadType.FIRMWARE_FILE,
    maxSize: 50, // 50MB
    allowedTypes: ['application/octet-stream', 'application/zip', 'application/x-tar'],
    uploadUrl: '/api/admin/devices/upload/firmware'
  },
  [FileUploadType.DEVICE_MANUAL]: {
    type: FileUploadType.DEVICE_MANUAL,
    maxSize: 20, // 20MB
    allowedTypes: ['application/pdf'],
    uploadUrl: '/api/admin/devices/upload/manual'
  },
  [FileUploadType.DEVICE_CERTIFICATE]: {
    type: FileUploadType.DEVICE_CERTIFICATE,
    maxSize: 5, // 5MB
    allowedTypes: ['application/pdf', 'image/jpeg', 'image/png'],
    uploadUrl: '/api/admin/devices/upload/certificate'
  }
}

/**
 * 文件上传服务类
 */
export class FileUploadService {
  /**
   * 验证文件类型和大小
   */
  static validateFile(file: UploadRawFile, type: FileUploadType): boolean {
    const config = uploadConfigs[type]

    // 检查文件类型
    if (!config.allowedTypes.includes(file.type)) {
      ElMessage.error(`不支持的文件类型：${file.type}`)
      return false
    }

    // 检查文件大小
    const fileSizeMB = file.size / 1024 / 1024
    if (fileSizeMB > config.maxSize) {
      ElMessage.error(`文件大小不能超过 ${config.maxSize}MB`)
      return false
    }

    return true
  }

  /**
   * 上传文件
   */
  static async uploadFile(
    file: UploadRawFile,
    type: FileUploadType,
    deviceId?: string,
    onProgress?: (progress: FileUploadProgress) => void
  ): Promise<FileUploadResponse> {
    const config = uploadConfigs[type]

    // 验证文件
    if (!this.validateFile(file, type)) {
      throw new Error('文件验证失败')
    }

    // 创建FormData
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    if (deviceId) {
      formData.append('deviceId', deviceId)
    }

    // 创建XMLHttpRequest以支持进度回调
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest()

      // 监听上传进度
      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable && onProgress) {
          const progress: FileUploadProgress = {
            percent: Math.round((event.loaded / event.total) * 100),
            loaded: event.loaded,
            total: event.total
          }
          onProgress(progress)
        }
      })

      // 监听上传完成
      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          try {
            const response: FileUploadResponse = JSON.parse(xhr.responseText)
            if (response.code === 200) {
              resolve(response)
            } else {
              reject(new Error(response.message || '上传失败'))
            }
          } catch (error) {
            reject(new Error('响应解析失败'))
          }
        } else {
          reject(new Error(`上传失败：HTTP ${xhr.status}`))
        }
      })

      // 监听上传错误
      xhr.addEventListener('error', () => {
        reject(new Error('网络错误'))
      })

      // 发送请求
      xhr.open('POST', config.uploadUrl)

      // 添加认证头
      const token = localStorage.getItem('token')
      if (token) {
        xhr.setRequestHeader('Authorization', `Bearer ${token}`)
      }

      xhr.send(formData)
    })
  }

  /**
   * 批量上传文件
   */
  static async uploadFiles(
    files: UploadRawFile[],
    type: FileUploadType,
    deviceId?: string,
    onProgress?: (fileIndex: number, progress: FileUploadProgress) => void
  ): Promise<FileUploadResponse[]> {
    const results: FileUploadResponse[] = []

    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      try {
        const result = await this.uploadFile(
          file,
          type,
          deviceId,
          (progress) => onProgress?.(i, progress)
        )
        results.push(result)
      } catch (error) {
        console.error(`文件 ${file.name} 上传失败:`, error)
        throw error
      }
    }

    return results
  }

  /**
   * 删除文件
   */
  static async deleteFile(fileId: string): Promise<void> {
    try {
      const response = await fetch(`/api/admin/files/${fileId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) {
        throw new Error(`删除失败：HTTP ${response.status}`)
      }

      const result = await response.json()
      if (result.code !== 200) {
        throw new Error(result.message || '删除失败')
      }
    } catch (error) {
      console.error('删除文件失败:', error)
      throw error
    }
  }

  /**
   * 获取文件访问URL
   */
  static getFileUrl(fileId: string): string {
    return `/api/admin/files/${fileId}/download`
  }

  /**
   * 获取文件预览URL
   */
  static getPreviewUrl(fileId: string): string {
    return `/api/admin/files/${fileId}/preview`
  }

  /**
   * 检查文件是否为图片
   */
  static isImageFile(file: UploadRawFile | UploadFile): boolean {
    const imageTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']

    // UploadRawFile 有 type 属性，UploadFile 有 raw 属性包含原始文件
    if ('type' in file) {
      return imageTypes.includes(file.type || '')
    } else if (file.raw) {
      return imageTypes.includes(file.raw.type || '')
    }

    return false
  }

  /**
   * 格式化文件大小
   */
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B'

    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  /**
   * 获取文件扩展名
   */
  static getFileExtension(fileName: string): string {
    return fileName.slice((fileName.lastIndexOf('.') - 1 >>> 0) + 2)
  }

  /**
   * 生成安全的文件名
   */
  static generateSafeFileName(originalName: string): string {
    const timestamp = Date.now()
    const extension = this.getFileExtension(originalName)
    const baseName = originalName.replace(/\.[^/.]+$/, '').replace(/[^a-zA-Z0-9]/g, '_')
    return `${baseName}_${timestamp}.${extension}`
  }
}

/**
 * 文件上传组合式函数
 */
export function useFileUpload(type: FileUploadType, deviceId?: string) {
  const uploading = ref(false)
  const uploadProgress = ref(0)
  const uploadedFiles = ref<FileUploadResponse[]>([])

  const uploadFile = async (file: UploadRawFile) => {
    uploading.value = true
    uploadProgress.value = 0

    try {
      const result = await FileUploadService.uploadFile(
        file,
        type,
        deviceId,
        (progress) => {
          uploadProgress.value = progress.percent
        }
      )

      uploadedFiles.value.push(result)
      ElMessage.success('文件上传成功')
      return result
    } catch (error) {
      ElMessage.error(`文件上传失败: ${error}`)
      throw error
    } finally {
      uploading.value = false
      uploadProgress.value = 0
    }
  }

  const deleteFile = async (fileId: string) => {
    try {
      await FileUploadService.deleteFile(fileId)
      uploadedFiles.value = uploadedFiles.value.filter(file => file.data.fileId !== fileId)
      ElMessage.success('文件删除成功')
    } catch (error) {
      ElMessage.error(`文件删除失败: ${error}`)
      throw error
    }
  }

  return {
    uploading: readonly(uploading),
    uploadProgress: readonly(uploadProgress),
    uploadedFiles: readonly(uploadedFiles),
    uploadFile,
    deleteFile
  }
}

// 导出默认实例
export default FileUploadService