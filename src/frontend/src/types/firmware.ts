// 固件管理相关类型定义

// 固件状态枚举
export type FirmwareStatus = 'draft' | 'testing' | 'stable' | 'deprecated'

// 设备更新状态枚举
export type DeviceUpdateStatus = 'up-to-date' | 'update-available' | 'updating' | 'failed'

// 设备固件状态变更类型
export type DeviceStatusChangeType = 'version_check' | 'update_start' | 'update_progress' | 'update_complete' | 'update_failed' | 'manual_refresh'

// 更新任务状态枚举
export type UpdateTaskStatus = 'pending' | 'running' | 'completed' | 'failed' | 'cancelled'

// 设备更新进度状态枚举
export type UpdateProgressStatus = 'pending' | 'downloading' | 'installing' | 'rebooting' | 'completed' | 'failed'

// 固件版本接口
export interface FirmwareVersion {
  id: string
  version: string
  deviceModel: string[]
  releaseDate: string
  fileSize: number
  fileName: string
  filePath: string
  checksum: string
  description: string
  changelog: string[]
  compatibility: string[]
  knownIssues: string[]
  downloadCount: number
  status: FirmwareStatus
  createdBy: string
  createdAt: string
  updatedAt: string
}

// 设备固件状态接口
export interface DeviceFirmwareStatus {
  deviceId: string
  deviceSerialNumber: string
  deviceModel: string
  deviceName: string
  currentVersion: string
  latestVersion: string
  updateAvailable: boolean
  lastUpdateCheck: string
  updateStatus: DeviceUpdateStatus
  lastUpdateTime?: string
  updateHistory: FirmwareUpdateRecord[]
  // 扩展字段
  isOnline: boolean
  signalStrength?: number
  location?: string
  lastHeartbeat: string
  updateProgress?: number
  errorCode?: string
  errorMessage?: string
  canUpdate: boolean
  updateRestrictions?: string[]
}

// 设备固件状态变更记录接口
export interface DeviceStatusChangeRecord {
  id: string
  deviceId: string
  changeType: DeviceStatusChangeType
  fromStatus: DeviceUpdateStatus
  toStatus: DeviceUpdateStatus
  timestamp: string
  reason?: string
  metadata?: Record<string, any>
  triggeredBy: string
}

// 固件更新任务接口
export interface FirmwareUpdateTask {
  id: string
  name: string
  targetVersion: string
  deviceIds: string[]
  scheduledTime?: string
  status: UpdateTaskStatus
  progress: {
    total: number
    completed: number
    failed: number
    inProgress: number
  }
  createdBy: string
  createdAt: string
  startedAt?: string
  completedAt?: string
  devices: DeviceUpdateProgress[]
}

// 设备更新进度接口
export interface DeviceUpdateProgress {
  deviceId: string
  deviceSerialNumber: string
  status: UpdateProgressStatus
  progress: number
  currentStep: string
  errorMessage?: string
  startTime?: string
  endTime?: string
  retryCount: number
}

// 固件更新记录接口
export interface FirmwareUpdateRecord {
  id: string
  deviceId: string
  fromVersion: string
  toVersion: string
  updateTime: string
  status: 'success' | 'failed'
  errorMessage?: string
  duration: number
  updatedBy: string
}

// 固件上传数据接口
export interface FirmwareUploadData {
  file: File
  version: string
  deviceModel: string[]
  description: string
  changelog: string[]
  compatibility: string[]
  knownIssues: string[]
}

// API请求参数接口
export interface FirmwareQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: FirmwareStatus
  deviceModel?: string
  dateRange?: [string, string]
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface DeviceFirmwareQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  updateStatus?: DeviceUpdateStatus
  deviceModel?: string
  hasUpdate?: boolean
}

export interface UpdateTaskQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: UpdateTaskStatus
  dateRange?: [string, string]
}

// API响应接口
export interface FirmwareListResponse {
  list: FirmwareVersion[]
  total: number
  page: number
  pageSize: number
}

export interface DeviceFirmwareListResponse {
  list: DeviceFirmwareStatus[]
  total: number
  page: number
  pageSize: number
  stats: {
    total: number
    upToDate: number
    updateAvailable: number
    updating: number
    failed: number
  }
}

export interface UpdateTaskListResponse {
  list: FirmwareUpdateTask[]
  total: number
  page: number
  pageSize: number
}

// 固件统计数据接口
export interface FirmwareStats {
  totalVersions: number
  stableVersions: number
  testingVersions: number
  draftVersions: number
  deprecatedVersions: number
  totalDownloads: number
  deviceDistribution: Record<string, number>
}

// 固件版本比较结果接口
export interface FirmwareComparison {
  version1: FirmwareVersion
  version2: FirmwareVersion
  differences: {
    changelog: string[]
    compatibility: string[]
    knownIssues: string[]
    fileSize: number
  }
}

// 固件状态文本映射
export const FIRMWARE_STATUS_TEXT = {
  draft: '草稿',
  testing: '测试中',
  stable: '稳定版',
  deprecated: '已弃用'
}

// 设备更新状态文本映射
export const DEVICE_UPDATE_STATUS_TEXT = {
  'up-to-date': '最新版本',
  'update-available': '有可用更新',
  'updating': '更新中',
  'failed': '更新失败'
}

// 更新任务状态文本映射
export const UPDATE_TASK_STATUS_TEXT = {
  pending: '等待中',
  running: '执行中',
  completed: '已完成',
  failed: '失败',
  cancelled: '已取消'
}

// 更新进度状态文本映射
export const UPDATE_PROGRESS_STATUS_TEXT = {
  pending: '准备中',
  downloading: '下载中',
  installing: '安装中',
  rebooting: '重启中',
  completed: '完成',
  failed: '失败'
}

// 设备状态变更类型文本映射
export const DEVICE_STATUS_CHANGE_TYPE_TEXT = {
  'version_check': '版本检查',
  'update_start': '开始更新',
  'update_progress': '更新进度',
  'update_complete': '更新完成',
  'update_failed': '更新失败',
  'manual_refresh': '手动刷新'
}

// 工具函数
export const compareVersions = (version1: string, version2: string): number => {
  const v1Parts = version1.split('.').map(Number)
  const v2Parts = version2.split('.').map(Number)
  
  const maxLength = Math.max(v1Parts.length, v2Parts.length)
  
  for (let i = 0; i < maxLength; i++) {
    const v1Part = v1Parts[i] || 0
    const v2Part = v2Parts[i] || 0
    
    if (v1Part > v2Part) return 1
    if (v1Part < v2Part) return -1
  }
  
  return 0
}

export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export const formatDuration = (seconds: number): string => {
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分${seconds % 60}秒`
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60
  
  return `${hours}小时${minutes}分${remainingSeconds}秒`
}

// 设备固件状态管理类
export class DeviceFirmwareStatusManager {
  /**
   * 验证设备状态变更是否合法
   */
  static validateStatusChange(
    fromStatus: DeviceUpdateStatus,
    toStatus: DeviceUpdateStatus,
    changeType: DeviceStatusChangeType
  ): { valid: boolean; reason?: string } {
    // 定义合法的状态转换规则
    const validTransitions: Record<DeviceUpdateStatus, DeviceUpdateStatus[]> = {
      'up-to-date': ['update-available', 'updating'],
      'update-available': ['updating', 'up-to-date'],
      'updating': ['up-to-date', 'failed'],
      'failed': ['updating', 'update-available', 'up-to-date']
    }

    // 检查状态转换是否合法
    if (!validTransitions[fromStatus]?.includes(toStatus)) {
      return {
        valid: false,
        reason: `不允许从状态 "${fromStatus}" 转换到 "${toStatus}"`
      }
    }

    // 根据变更类型验证状态转换
    const typeValidations: Record<DeviceStatusChangeType, (from: DeviceUpdateStatus, to: DeviceUpdateStatus) => boolean> = {
      'version_check': (from, to) => to === 'update-available' || to === 'up-to-date',
      'update_start': (from, to) => to === 'updating',
      'update_progress': (from, to) => from === 'updating' && to === 'updating',
      'update_complete': (from, to) => from === 'updating' && to === 'up-to-date',
      'update_failed': (from, to) => from === 'updating' && to === 'failed',
      'manual_refresh': () => true // 手动刷新允许任何状态转换
    }

    if (!typeValidations[changeType]?.(fromStatus, toStatus)) {
      return {
        valid: false,
        reason: `变更类型 "${changeType}" 不支持从 "${fromStatus}" 到 "${toStatus}" 的转换`
      }
    }

    return { valid: true }
  }

  /**
   * 检查设备是否可以进行固件更新
   */
  static canDeviceUpdate(device: DeviceFirmwareStatus): { canUpdate: boolean; restrictions: string[] } {
    const restrictions: string[] = []

    // 检查设备是否在线
    if (!device.isOnline) {
      restrictions.push('设备离线')
    }

    // 检查设备是否正在更新
    if (device.updateStatus === 'updating') {
      restrictions.push('设备正在更新中')
    }



    // 检查信号强度（如果有）
    if (device.signalStrength !== undefined && device.signalStrength < 30) {
      restrictions.push('信号强度不足（需要至少30%）')
    }

    // 检查是否有可用更新
    if (!device.updateAvailable) {
      restrictions.push('没有可用更新')
    }

    // 检查设备型号兼容性
    if (device.updateRestrictions && device.updateRestrictions.length > 0) {
      restrictions.push(...device.updateRestrictions)
    }

    return {
      canUpdate: restrictions.length === 0,
      restrictions
    }
  }

  /**
   * 计算设备更新优先级
   */
  static calculateUpdatePriority(device: DeviceFirmwareStatus): number {
    let priority = 0

    // 基础优先级：有可用更新
    if (device.updateAvailable) {
      priority += 10
    }

    // 版本差距越大，优先级越高
    const versionDiff = compareVersions(device.latestVersion, device.currentVersion)
    priority += versionDiff * 5

    // 设备在线状态
    if (device.isOnline) {
      priority += 5
    }



    // 信号强度良好
    if (device.signalStrength && device.signalStrength > 70) {
      priority += 2
    }

    // 最近更新时间（越久未更新优先级越高）
    if (device.lastUpdateTime) {
      const daysSinceUpdate = Math.floor(
        (Date.now() - new Date(device.lastUpdateTime).getTime()) / (1000 * 60 * 60 * 24)
      )
      priority += Math.min(daysSinceUpdate / 10, 5) // 最多加5分
    }

    return Math.max(0, priority)
  }

  /**
   * 创建状态变更记录
   */
  static createStatusChangeRecord(
    deviceId: string,
    fromStatus: DeviceUpdateStatus,
    toStatus: DeviceUpdateStatus,
    changeType: DeviceStatusChangeType,
    triggeredBy: string,
    reason?: string,
    metadata?: Record<string, any>
  ): DeviceStatusChangeRecord {
    return {
      id: `change_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      deviceId,
      changeType,
      fromStatus,
      toStatus,
      timestamp: new Date().toISOString(),
      reason,
      metadata,
      triggeredBy
    }
  }

  /**
   * 更新设备固件状态
   */
  static updateDeviceStatus(
    device: DeviceFirmwareStatus,
    newStatus: DeviceUpdateStatus,
    changeType: DeviceStatusChangeType,
    triggeredBy: string,
    options?: {
      progress?: number
      errorCode?: string
      errorMessage?: string
      reason?: string
      metadata?: Record<string, any>
    }
  ): { success: boolean; updatedDevice?: DeviceFirmwareStatus; error?: string } {
    // 验证状态变更
    const validation = this.validateStatusChange(device.updateStatus, newStatus, changeType)
    if (!validation.valid) {
      return {
        success: false,
        error: validation.reason
      }
    }

    // 创建更新后的设备状态
    const updatedDevice: DeviceFirmwareStatus = {
      ...device,
      updateStatus: newStatus,
      updateProgress: options?.progress,
      errorCode: options?.errorCode,
      errorMessage: options?.errorMessage,
      lastUpdateCheck: new Date().toISOString()
    }

    // 根据新状态更新相关字段
    switch (newStatus) {
      case 'up-to-date':
        updatedDevice.updateAvailable = false
        updatedDevice.updateProgress = 100
        updatedDevice.errorCode = undefined
        updatedDevice.errorMessage = undefined
        if (changeType === 'update_complete') {
          updatedDevice.lastUpdateTime = new Date().toISOString()
          updatedDevice.currentVersion = device.latestVersion
        }
        break
      case 'update-available':
        updatedDevice.updateAvailable = true
        updatedDevice.updateProgress = 0
        break
      case 'updating':
        updatedDevice.updateProgress = options?.progress || 0
        break
      case 'failed':
        updatedDevice.updateProgress = 0
        break
    }

    // 重新计算是否可以更新
    const updateCheck = this.canDeviceUpdate(updatedDevice)
    updatedDevice.canUpdate = updateCheck.canUpdate
    updatedDevice.updateRestrictions = updateCheck.restrictions

    return {
      success: true,
      updatedDevice
    }
  }

  /**
   * 批量更新设备状态
   */
  static batchUpdateDeviceStatus(
    devices: DeviceFirmwareStatus[],
    newStatus: DeviceUpdateStatus,
    changeType: DeviceStatusChangeType,
    triggeredBy: string
  ): { 
    success: boolean
    updatedDevices: DeviceFirmwareStatus[]
    errors: Array<{ deviceId: string; error: string }>
  } {
    const updatedDevices: DeviceFirmwareStatus[] = []
    const errors: Array<{ deviceId: string; error: string }> = []

    for (const device of devices) {
      const result = this.updateDeviceStatus(device, newStatus, changeType, triggeredBy)
      if (result.success && result.updatedDevice) {
        updatedDevices.push(result.updatedDevice)
      } else {
        errors.push({
          deviceId: device.deviceId,
          error: result.error || '未知错误'
        })
      }
    }

    return {
      success: errors.length === 0,
      updatedDevices,
      errors
    }
  }
}

// 固件更新任务管理类
export class FirmwareUpdateTaskManager {
  /**
   * 创建新的固件更新任务
   */
  static createUpdateTask(
    name: string,
    targetVersion: string,
    deviceIds: string[],
    createdBy: string,
    scheduledTime?: string
  ): FirmwareUpdateTask {
    const devices: DeviceUpdateProgress[] = deviceIds.map(deviceId => ({
      deviceId,
      deviceSerialNumber: '', // 需要从设备信息中获取
      status: 'pending',
      progress: 0,
      currentStep: '准备中',
      retryCount: 0
    }))

    return {
      id: `task_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`,
      name,
      targetVersion,
      deviceIds,
      scheduledTime,
      status: 'pending',
      progress: {
        total: deviceIds.length,
        completed: 0,
        failed: 0,
        inProgress: 0
      },
      createdBy,
      createdAt: new Date().toISOString(),
      devices
    }
  }

  /**
   * 更新任务状态
   */
  static updateTaskStatus(
    task: FirmwareUpdateTask,
    newStatus: UpdateTaskStatus,
    options?: {
      startedAt?: string
      completedAt?: string
      errorMessage?: string
    }
  ): FirmwareUpdateTask {
    const updatedTask: FirmwareUpdateTask = {
      ...task,
      status: newStatus
    }

    // 根据状态更新相关字段
    switch (newStatus) {
      case 'running':
        updatedTask.startedAt = options?.startedAt || new Date().toISOString()
        break
      case 'completed':
      case 'failed':
      case 'cancelled':
        updatedTask.completedAt = options?.completedAt || new Date().toISOString()
        break
    }

    return updatedTask
  }

  /**
   * 更新设备进度
   */
  static updateDeviceProgress(
    task: FirmwareUpdateTask,
    deviceId: string,
    progress: Partial<DeviceUpdateProgress>
  ): FirmwareUpdateTask {
    const updatedDevices = task.devices.map(device => {
      if (device.deviceId === deviceId) {
        return {
          ...device,
          ...progress,
          // 如果状态变为 completed 或 failed，设置结束时间
          endTime: (progress.status === 'completed' || progress.status === 'failed') 
            ? new Date().toISOString() 
            : device.endTime
        }
      }
      return device
    })

    // 重新计算任务整体进度
    const taskProgress = this.calculateTaskProgress(updatedDevices)

    // 根据设备状态更新任务状态
    let taskStatus = task.status
    if (taskProgress.completed + taskProgress.failed === taskProgress.total) {
      // 所有设备都完成了
      taskStatus = taskProgress.failed === 0 ? 'completed' : 'failed'
    } else if (taskProgress.inProgress > 0 && task.status === 'pending') {
      // 有设备开始更新，任务状态变为运行中
      taskStatus = 'running'
    }

    return {
      ...task,
      devices: updatedDevices,
      progress: taskProgress,
      status: taskStatus,
      completedAt: (taskStatus === 'completed' || taskStatus === 'failed') 
        ? new Date().toISOString() 
        : task.completedAt
    }
  }

  /**
   * 计算任务整体进度
   */
  static calculateTaskProgress(devices: DeviceUpdateProgress[]): {
    total: number
    completed: number
    failed: number
    inProgress: number
  } {
    const total = devices.length
    let completed = 0
    let failed = 0
    let inProgress = 0

    devices.forEach(device => {
      switch (device.status) {
        case 'completed':
          completed++
          break
        case 'failed':
          failed++
          break
        case 'downloading':
        case 'installing':
        case 'rebooting':
          inProgress++
          break
      }
    })

    return { total, completed, failed, inProgress }
  }

  /**
   * 获取任务完成百分比
   */
  static getTaskCompletionPercentage(task: FirmwareUpdateTask): number {
    if (task.progress.total === 0) return 0
    
    const completedAndFailed = task.progress.completed + task.progress.failed
    return Math.round((completedAndFailed / task.progress.total) * 100)
  }

  /**
   * 获取任务成功率
   */
  static getTaskSuccessRate(task: FirmwareUpdateTask): number {
    const completedAndFailed = task.progress.completed + task.progress.failed
    if (completedAndFailed === 0) return 0
    
    return Math.round((task.progress.completed / completedAndFailed) * 100)
  }

  /**
   * 检查任务是否可以启动
   */
  static canStartTask(task: FirmwareUpdateTask): { canStart: boolean; reason?: string } {
    if (task.status !== 'pending') {
      return {
        canStart: false,
        reason: `任务状态为 "${task.status}"，只有待执行的任务才能启动`
      }
    }

    if (task.devices.length === 0) {
      return {
        canStart: false,
        reason: '任务中没有设备'
      }
    }

    // 检查是否有定时任务且时间未到
    if (task.scheduledTime) {
      const scheduledTime = new Date(task.scheduledTime).getTime()
      const currentTime = Date.now()
      
      if (currentTime < scheduledTime) {
        return {
          canStart: false,
          reason: `定时任务，预定执行时间：${task.scheduledTime}`
        }
      }
    }

    return { canStart: true }
  }

  /**
   * 检查任务是否可以取消
   */
  static canCancelTask(task: FirmwareUpdateTask): { canCancel: boolean; reason?: string } {
    if (task.status === 'completed') {
      return {
        canCancel: false,
        reason: '任务已完成，无法取消'
      }
    }

    if (task.status === 'cancelled') {
      return {
        canCancel: false,
        reason: '任务已取消'
      }
    }

    return { canCancel: true }
  }

  /**
   * 取消任务
   */
  static cancelTask(task: FirmwareUpdateTask, cancelledBy: string): FirmwareUpdateTask {
    // 取消所有未完成的设备更新
    const updatedDevices = task.devices.map(device => {
      if (device.status === 'pending' || device.status === 'downloading' || 
          device.status === 'installing' || device.status === 'rebooting') {
        return {
          ...device,
          status: 'failed' as UpdateProgressStatus,
          errorMessage: '任务被取消',
          endTime: new Date().toISOString()
        }
      }
      return device
    })

    // 重新计算进度
    const taskProgress = this.calculateTaskProgress(updatedDevices)

    return {
      ...task,
      status: 'cancelled',
      devices: updatedDevices,
      progress: taskProgress,
      completedAt: new Date().toISOString()
    }
  }

  /**
   * 重试失败的设备
   */
  static retryFailedDevices(task: FirmwareUpdateTask): FirmwareUpdateTask {
    const updatedDevices = task.devices.map(device => {
      if (device.status === 'failed') {
        return {
          ...device,
          status: 'pending' as UpdateProgressStatus,
          progress: 0,
          currentStep: '准备重试',
          errorMessage: undefined,
          retryCount: device.retryCount + 1,
          endTime: undefined
        }
      }
      return device
    })

    // 重新计算进度
    const taskProgress = this.calculateTaskProgress(updatedDevices)

    // 如果任务已完成或失败，重新设置为运行中
    let taskStatus = task.status
    if (task.status === 'completed' || task.status === 'failed') {
      taskStatus = 'running'
    }

    return {
      ...task,
      devices: updatedDevices,
      progress: taskProgress,
      status: taskStatus,
      completedAt: undefined // 清除完成时间
    }
  }

  /**
   * 获取任务摘要信息
   */
  static getTaskSummary(task: FirmwareUpdateTask): {
    totalDevices: number
    completedDevices: number
    failedDevices: number
    inProgressDevices: number
    completionPercentage: number
    successRate: number
    duration?: number
    estimatedTimeRemaining?: number
  } {
    const summary = {
      totalDevices: task.progress.total,
      completedDevices: task.progress.completed,
      failedDevices: task.progress.failed,
      inProgressDevices: task.progress.inProgress,
      completionPercentage: this.getTaskCompletionPercentage(task),
      successRate: this.getTaskSuccessRate(task),
      duration: undefined as number | undefined,
      estimatedTimeRemaining: undefined as number | undefined
    }

    // 计算任务持续时间
    if (task.startedAt) {
      const startTime = new Date(task.startedAt).getTime()
      const endTime = task.completedAt ? new Date(task.completedAt).getTime() : Date.now()
      summary.duration = Math.floor((endTime - startTime) / 1000) // 秒
    }

    // 估算剩余时间（基于已完成设备的平均时间）
    if (task.startedAt && summary.completedDevices > 0 && summary.inProgressDevices > 0) {
      const startTime = new Date(task.startedAt).getTime()
      const currentTime = Date.now()
      const elapsedTime = (currentTime - startTime) / 1000 // 秒
      const avgTimePerDevice = elapsedTime / summary.completedDevices
      summary.estimatedTimeRemaining = Math.floor(avgTimePerDevice * summary.inProgressDevices)
    }

    return summary
  }
}