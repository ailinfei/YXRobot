/**
 * 固件更新服务
 * 负责设备通信、更新指令发送、状态同步等功能
 */

import type {
  FirmwareUpdateTask,
  DeviceUpdateProgress,
  DeviceFirmwareStatus,
  UpdateProgressStatus,
  DeviceStatusChangeType
} from '@/types/firmware'
import { DeviceFirmwareStatusManager } from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'

// 更新指令类型
export interface UpdateCommand {
  taskId: string
  deviceId: string
  targetVersion: string
  firmwareUrl: string
  checksum: string
  priority: 'low' | 'normal' | 'high'
  retryCount: number
  timeout: number
}

// 设备响应类型
export interface DeviceResponse {
  deviceId: string
  taskId: string
  status: 'acknowledged' | 'rejected' | 'error'
  message?: string
  errorCode?: string
}

// 进度更新类型
export interface ProgressUpdate {
  deviceId: string
  taskId: string
  status: UpdateProgressStatus
  progress: number
  currentStep: string
  errorMessage?: string
  timestamp: string
}

// 更新结果类型
export interface UpdateResult {
  deviceId: string
  taskId: string
  success: boolean
  fromVersion: string
  toVersion: string
  duration: number
  errorMessage?: string
  timestamp: string
}

/**
 * 固件更新服务类
 */
export class FirmwareUpdateService {
  private static instance: FirmwareUpdateService
  private activeUpdates = new Map<string, UpdateCommand>()
  private progressCallbacks = new Map<string, (update: ProgressUpdate) => void>()
  private resultCallbacks = new Map<string, (result: UpdateResult) => void>()
  private retryQueue: UpdateCommand[] = []
  private maxRetries = 3
  private retryDelay = 5000 // 5秒

  private constructor() {
    this.startProgressSimulation()
  }

  public static getInstance(): FirmwareUpdateService {
    if (!FirmwareUpdateService.instance) {
      FirmwareUpdateService.instance = new FirmwareUpdateService()
    }
    return FirmwareUpdateService.instance
  }

  /**
   * 发送更新指令到设备
   */
  public async sendUpdateCommand(command: UpdateCommand): Promise<DeviceResponse> {
    try {
      // 验证更新指令
      const validation = this.validateUpdateCommand(command)
      if (!validation.valid) {
        return {
          deviceId: command.deviceId,
          taskId: command.taskId,
          status: 'rejected',
          message: validation.reason
        }
      }

      // 模拟网络延迟
      await this.delay(Math.random() * 1000 + 500)

      // 模拟设备响应（90%成功率）
      const success = Math.random() > 0.1

      if (success) {
        // 记录活跃更新
        this.activeUpdates.set(command.deviceId, command)

        // 开始模拟更新进度
        this.simulateUpdateProgress(command)

        return {
          deviceId: command.deviceId,
          taskId: command.taskId,
          status: 'acknowledged',
          message: '设备已接收更新指令'
        }
      } else {
        // 模拟失败情况
        const errorCodes = ['NETWORK_ERROR', 'DEVICE_BUSY', 'INSUFFICIENT_STORAGE', 'INVALID_VERSION']
        const errorCode = errorCodes[Math.floor(Math.random() * errorCodes.length)]
        
        return {
          deviceId: command.deviceId,
          taskId: command.taskId,
          status: 'error',
          errorCode,
          message: this.getErrorMessage(errorCode)
        }
      }
    } catch (error) {
      return {
        deviceId: command.deviceId,
        taskId: command.taskId,
        status: 'error',
        message: '发送更新指令失败'
      }
    }
  }

  /**
   * 批量发送更新指令
   */
  public async sendBatchUpdateCommands(commands: UpdateCommand[]): Promise<DeviceResponse[]> {
    const responses: DeviceResponse[] = []
    
    // 并发发送指令，但限制并发数量
    const batchSize = 5
    for (let i = 0; i < commands.length; i += batchSize) {
      const batch = commands.slice(i, i + batchSize)
      const batchPromises = batch.map(command => this.sendUpdateCommand(command))
      const batchResponses = await Promise.all(batchPromises)
      responses.push(...batchResponses)
      
      // 批次间延迟，避免设备过载
      if (i + batchSize < commands.length) {
        await this.delay(1000)
      }
    }

    return responses
  }

  /**
   * 取消设备更新
   */
  public async cancelDeviceUpdate(deviceId: string, taskId: string): Promise<boolean> {
    try {
      // 从活跃更新中移除
      this.activeUpdates.delete(deviceId)

      // 模拟发送取消指令
      await this.delay(500)

      // 触发取消状态更新
      const progressUpdate: ProgressUpdate = {
        deviceId,
        taskId,
        status: 'failed',
        progress: 0,
        currentStep: '更新已取消',
        errorMessage: '用户取消更新',
        timestamp: new Date().toISOString()
      }

      this.notifyProgress(progressUpdate)

      return true
    } catch (error) {
      console.error('取消设备更新失败:', error)
      return false
    }
  }

  /**
   * 重试失败的更新
   */
  public async retryDeviceUpdate(command: UpdateCommand): Promise<DeviceResponse> {
    // 增加重试次数
    const retryCommand = {
      ...command,
      retryCount: command.retryCount + 1
    }

    // 如果超过最大重试次数，直接返回失败
    if (retryCommand.retryCount > this.maxRetries) {
      return {
        deviceId: command.deviceId,
        taskId: command.taskId,
        status: 'rejected',
        message: `已达到最大重试次数 (${this.maxRetries})`
      }
    }

    // 延迟后重试
    await this.delay(this.retryDelay)

    return this.sendUpdateCommand(retryCommand)
  }

  /**
   * 注册进度回调
   */
  public onProgress(taskId: string, callback: (update: ProgressUpdate) => void): void {
    this.progressCallbacks.set(taskId, callback)
  }

  /**
   * 注册结果回调
   */
  public onResult(taskId: string, callback: (result: UpdateResult) => void): void {
    this.resultCallbacks.set(taskId, callback)
  }

  /**
   * 移除回调
   */
  public removeCallbacks(taskId: string): void {
    this.progressCallbacks.delete(taskId)
    this.resultCallbacks.delete(taskId)
  }

  /**
   * 获取设备更新状态
   */
  public getDeviceUpdateStatus(deviceId: string): UpdateCommand | null {
    return this.activeUpdates.get(deviceId) || null
  }

  /**
   * 获取所有活跃更新
   */
  public getActiveUpdates(): UpdateCommand[] {
    return Array.from(this.activeUpdates.values())
  }

  /**
   * 验证更新指令
   */
  private validateUpdateCommand(command: UpdateCommand): { valid: boolean; reason?: string } {
    if (!command.deviceId) {
      return { valid: false, reason: '设备ID不能为空' }
    }

    if (!command.taskId) {
      return { valid: false, reason: '任务ID不能为空' }
    }

    if (!command.targetVersion) {
      return { valid: false, reason: '目标版本不能为空' }
    }

    if (!command.firmwareUrl) {
      return { valid: false, reason: '固件下载地址不能为空' }
    }

    if (!command.checksum) {
      return { valid: false, reason: '固件校验和不能为空' }
    }

    if (command.timeout <= 0) {
      return { valid: false, reason: '超时时间必须大于0' }
    }

    return { valid: true }
  }

  /**
   * 模拟更新进度
   */
  private simulateUpdateProgress(command: UpdateCommand): void {
    const steps = [
      { status: 'pending' as UpdateProgressStatus, step: '准备更新', duration: 2000 },
      { status: 'downloading' as UpdateProgressStatus, step: '下载固件', duration: 15000 },
      { status: 'installing' as UpdateProgressStatus, step: '安装固件', duration: 10000 },
      { status: 'rebooting' as UpdateProgressStatus, step: '重启设备', duration: 5000 },
      { status: 'completed' as UpdateProgressStatus, step: '更新完成', duration: 1000 }
    ]

    let currentStepIndex = 0
    let currentProgress = 0

    const updateProgress = () => {
      if (currentStepIndex >= steps.length) {
        // 更新完成，发送结果
        const result: UpdateResult = {
          deviceId: command.deviceId,
          taskId: command.taskId,
          success: true,
          fromVersion: '2.0.5', // 模拟数据
          toVersion: command.targetVersion,
          duration: 32, // 模拟持续时间（秒）
          timestamp: new Date().toISOString()
        }

        this.notifyResult(result)
        this.activeUpdates.delete(command.deviceId)
        return
      }

      const currentStep = steps[currentStepIndex]
      const stepProgress = Math.min(100, currentProgress + Math.random() * 20)

      // 模拟失败情况（5%概率）
      if (Math.random() < 0.05 && currentStepIndex > 0) {
        const progressUpdate: ProgressUpdate = {
          deviceId: command.deviceId,
          taskId: command.taskId,
          status: 'failed',
          progress: stepProgress,
          currentStep: currentStep.step,
          errorMessage: '更新过程中发生错误',
          timestamp: new Date().toISOString()
        }

        this.notifyProgress(progressUpdate)

        const result: UpdateResult = {
          deviceId: command.deviceId,
          taskId: command.taskId,
          success: false,
          fromVersion: '2.0.5',
          toVersion: command.targetVersion,
          duration: Math.floor((Date.now() - Date.now()) / 1000),
          errorMessage: '更新过程中发生错误',
          timestamp: new Date().toISOString()
        }

        this.notifyResult(result)
        this.activeUpdates.delete(command.deviceId)
        return
      }

      const progressUpdate: ProgressUpdate = {
        deviceId: command.deviceId,
        taskId: command.taskId,
        status: currentStep.status,
        progress: stepProgress,
        currentStep: currentStep.step,
        timestamp: new Date().toISOString()
      }

      this.notifyProgress(progressUpdate)

      // 如果当前步骤完成，进入下一步
      if (stepProgress >= 100 || Math.random() > 0.3) {
        currentStepIndex++
        currentProgress = 0
      } else {
        currentProgress = stepProgress
      }

      // 继续下一次更新
      setTimeout(updateProgress, Math.random() * 2000 + 1000)
    }

    // 开始进度模拟
    setTimeout(updateProgress, 1000)
  }

  /**
   * 通知进度更新
   */
  private notifyProgress(update: ProgressUpdate): void {
    const callback = this.progressCallbacks.get(update.taskId)
    if (callback) {
      callback(update)
    }
  }

  /**
   * 通知更新结果
   */
  private notifyResult(result: UpdateResult): void {
    const callback = this.resultCallbacks.get(result.taskId)
    if (callback) {
      callback(result)
    }
  }

  /**
   * 开始进度模拟（用于演示）
   */
  private startProgressSimulation(): void {
    // 这里可以添加WebSocket连接或其他实时通信机制
    // 目前使用模拟数据
  }

  /**
   * 获取错误信息
   */
  private getErrorMessage(errorCode: string): string {
    const errorMessages: Record<string, string> = {
      'NETWORK_ERROR': '网络连接错误',
      'DEVICE_BUSY': '设备忙碌，请稍后重试',
      'INSUFFICIENT_STORAGE': '设备存储空间不足',
      'INVALID_VERSION': '固件版本不兼容',
      'CHECKSUM_MISMATCH': '固件校验失败',
      'TIMEOUT': '操作超时',
      'PERMISSION_DENIED': '权限不足',
      'DEVICE_OFFLINE': '设备离线'
    }

    return errorMessages[errorCode] || '未知错误'
  }

  /**
   * 延迟函数
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

/**
 * 固件更新管理器
 * 提供高级的更新管理功能
 */
export class FirmwareUpdateManager {
  private updateService: FirmwareUpdateService
  private activeTasks = new Map<string, FirmwareUpdateTask>()

  constructor() {
    this.updateService = FirmwareUpdateService.getInstance()
  }

  /**
   * 执行固件更新任务
   */
  public async executeUpdateTask(
    task: FirmwareUpdateTask,
    onProgress?: (taskId: string, deviceId: string, progress: ProgressUpdate) => void,
    onComplete?: (taskId: string, results: UpdateResult[]) => void
  ): Promise<void> {
    try {
      this.activeTasks.set(task.id, task)

      // 注册进度回调
      if (onProgress) {
        this.updateService.onProgress(task.id, (update) => {
          onProgress(task.id, update.deviceId, update)
        })
      }

      // 收集更新结果
      const results: UpdateResult[] = []
      this.updateService.onResult(task.id, (result) => {
        results.push(result)
        
        // 如果所有设备都完成了，触发完成回调
        if (results.length === task.deviceIds.length && onComplete) {
          onComplete(task.id, results)
          this.cleanup(task.id)
        }
      })

      // 创建更新指令
      const commands: UpdateCommand[] = task.deviceIds.map(deviceId => ({
        taskId: task.id,
        deviceId,
        targetVersion: task.targetVersion,
        firmwareUrl: `/firmware/${task.targetVersion}/firmware.bin`,
        checksum: 'sha256:mock-checksum',
        priority: 'normal',
        retryCount: 0,
        timeout: 300000 // 5分钟超时
      }))

      // 批量发送更新指令
      const responses = await this.updateService.sendBatchUpdateCommands(commands)

      // 处理发送失败的设备
      responses.forEach(response => {
        if (response.status !== 'acknowledged') {
          const failedResult: UpdateResult = {
            deviceId: response.deviceId,
            taskId: response.taskId,
            success: false,
            fromVersion: 'unknown',
            toVersion: task.targetVersion,
            duration: 0,
            errorMessage: response.message,
            timestamp: new Date().toISOString()
          }
          results.push(failedResult)
        }
      })

    } catch (error) {
      console.error('执行更新任务失败:', error)
      this.cleanup(task.id)
      throw error
    }
  }

  /**
   * 取消更新任务
   */
  public async cancelUpdateTask(taskId: string): Promise<boolean> {
    const task = this.activeTasks.get(taskId)
    if (!task) {
      return false
    }

    try {
      // 取消所有设备的更新
      const cancelPromises = task.deviceIds.map(deviceId =>
        this.updateService.cancelDeviceUpdate(deviceId, taskId)
      )

      const results = await Promise.all(cancelPromises)
      const allCancelled = results.every(result => result)

      if (allCancelled) {
        this.cleanup(taskId)
      }

      return allCancelled
    } catch (error) {
      console.error('取消更新任务失败:', error)
      return false
    }
  }

  /**
   * 重试失败的设备
   */
  public async retryFailedDevices(taskId: string, failedDeviceIds: string[]): Promise<void> {
    const task = this.activeTasks.get(taskId)
    if (!task) {
      throw new Error('任务不存在')
    }

    const retryCommands: UpdateCommand[] = failedDeviceIds.map(deviceId => ({
      taskId,
      deviceId,
      targetVersion: task.targetVersion,
      firmwareUrl: `/firmware/${task.targetVersion}/firmware.bin`,
      checksum: 'sha256:mock-checksum',
      priority: 'high', // 重试时使用高优先级
      retryCount: 1,
      timeout: 300000
    }))

    await this.updateService.sendBatchUpdateCommands(retryCommands)
  }

  /**
   * 获取任务状态
   */
  public getTaskStatus(taskId: string): FirmwareUpdateTask | null {
    return this.activeTasks.get(taskId) || null
  }

  /**
   * 清理任务资源
   */
  private cleanup(taskId: string): void {
    this.activeTasks.delete(taskId)
    this.updateService.removeCallbacks(taskId)
  }
}

// 导出单例实例
export const firmwareUpdateService = FirmwareUpdateService.getInstance()
export const firmwareUpdateManager = new FirmwareUpdateManager()