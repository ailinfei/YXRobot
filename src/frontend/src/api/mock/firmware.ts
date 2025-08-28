// 固件管理Mock API

import type {
  FirmwareVersion,
  DeviceFirmwareStatus,
  FirmwareUpdateTask,
  FirmwareQueryParams,
  DeviceFirmwareQueryParams,
  UpdateTaskQueryParams,
  FirmwareListResponse,
  DeviceFirmwareListResponse,
  UpdateTaskListResponse,
  FirmwareStats,
  FirmwareUploadData,
  DeviceUpdateProgress
} from '@/types/firmware'

// Mock数据生成
const generateMockFirmwareVersions = (): FirmwareVersion[] => {
  return [
    {
      id: '1',
      version: '2.1.0',
      deviceModel: ['YX-EDU-2024', 'YX-HOME-2024'],
      releaseDate: '2024-01-15T00:00:00Z',
      fileSize: 52428800, // 50MB
      fileName: 'firmware_v2.1.0.bin',
      filePath: '/firmware/v2.1.0/firmware_v2.1.0.bin',
      checksum: 'sha256:a1b2c3d4e5f6...',
      description: '修复了字体渲染问题，提升了系统稳定性',
      changelog: [
        '修复字体渲染异常问题',
        '优化内存使用效率',
        '增强网络连接稳定性',
        '修复已知的安全漏洞'
      ],
      compatibility: ['YX-EDU-2024 v1.0+', 'YX-HOME-2024 v1.0+'],
      knownIssues: ['在某些网络环境下可能出现连接延迟'],
      downloadCount: 1250,
      status: 'stable',
      createdBy: 'admin',
      createdAt: '2024-01-10T00:00:00Z',
      updatedAt: '2024-01-15T00:00:00Z'
    },
    {
      id: '2',
      version: '2.0.5',
      deviceModel: ['YX-EDU-2024', 'YX-HOME-2024', 'YX-PRO-2024'],
      releaseDate: '2024-01-01T00:00:00Z',
      fileSize: 48234496, // 46MB
      fileName: 'firmware_v2.0.5.bin',
      filePath: '/firmware/v2.0.5/firmware_v2.0.5.bin',
      checksum: 'sha256:b2c3d4e5f6g7...',
      description: '稳定版本，推荐所有用户更新',
      changelog: [
        '提升书写识别准确度',
        '优化电池管理算法',
        '修复蓝牙连接问题',
        '增加新的练字模式'
      ],
      compatibility: ['所有设备型号'],
      knownIssues: [],
      downloadCount: 2340,
      status: 'stable',
      createdBy: 'admin',
      createdAt: '2023-12-25T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    {
      id: '3',
      version: '2.2.0-beta',
      deviceModel: ['YX-PRO-2024'],
      releaseDate: '2024-01-20T00:00:00Z',
      fileSize: 55574528, // 53MB
      fileName: 'firmware_v2.2.0-beta.bin',
      filePath: '/firmware/v2.2.0-beta/firmware_v2.2.0-beta.bin',
      checksum: 'sha256:c3d4e5f6g7h8...',
      description: '测试版本，包含AI智能纠错功能',
      changelog: [
        '新增AI智能纠错功能',
        '支持多种字体风格',
        '优化触控响应速度',
        '增加语音指导功能'
      ],
      compatibility: ['YX-PRO-2024 v2.0+'],
      knownIssues: [
        'AI功能可能消耗更多电量',
        '部分字体在小尺寸下显示不清晰'
      ],
      downloadCount: 156,
      status: 'testing',
      createdBy: 'developer',
      createdAt: '2024-01-18T00:00:00Z',
      updatedAt: '2024-01-20T00:00:00Z'
    }
  ]
}

const generateMockDeviceFirmwareStatus = (): DeviceFirmwareStatus[] => {
  return [
    {
      deviceId: 'dev-001',
      deviceSerialNumber: 'EDU-001234',
      deviceModel: 'YX-EDU-2024',
      deviceName: '教学设备-001',
      currentVersion: '2.0.5',
      latestVersion: '2.1.0',
      updateAvailable: true,
      lastUpdateCheck: '2024-01-16T10:30:00Z',
      updateStatus: 'update-available',
      lastUpdateTime: '2024-01-01T15:20:00Z',
      updateHistory: [
        {
          id: 'update-001',
          deviceId: 'dev-001',
          fromVersion: '2.0.3',
          toVersion: '2.0.5',
          updateTime: '2024-01-01T15:20:00Z',
          status: 'success',
          duration: 180,
          updatedBy: 'admin'
        }
      ],
      // 扩展字段
      isOnline: true,
      signalStrength: 85,
      location: '教室A-101',
      lastHeartbeat: '2024-01-16T10:28:00Z',
      canUpdate: true,
      updateRestrictions: []
    },
    {
      deviceId: 'dev-002',
      deviceSerialNumber: 'HOME-005678',
      deviceModel: 'YX-HOME-2024',
      deviceName: '家用设备-002',
      currentVersion: '2.1.0',
      latestVersion: '2.1.0',
      updateAvailable: false,
      lastUpdateCheck: '2024-01-16T10:30:00Z',
      updateStatus: 'up-to-date',
      lastUpdateTime: '2024-01-15T09:45:00Z',
      updateHistory: [
        {
          id: 'update-002',
          deviceId: 'dev-002',
          fromVersion: '2.0.5',
          toVersion: '2.1.0',
          updateTime: '2024-01-15T09:45:00Z',
          status: 'success',
          duration: 210,
          updatedBy: 'admin'
        }
      ],
      // 扩展字段
      isOnline: true,
      signalStrength: 92,
      location: '客厅',
      lastHeartbeat: '2024-01-16T10:29:00Z',
      canUpdate: false,
      updateRestrictions: ['没有可用更新']
    },
    {
      deviceId: 'dev-003',
      deviceSerialNumber: 'PRO-009876',
      deviceModel: 'YX-PRO-2024',
      deviceName: '专业设备-003',
      currentVersion: '2.0.5',
      latestVersion: '2.1.0',
      updateAvailable: true,
      lastUpdateCheck: '2024-01-16T10:30:00Z',
      updateStatus: 'failed',
      lastUpdateTime: '2024-01-14T14:20:00Z',
      updateHistory: [
        {
          id: 'update-003',
          deviceId: 'dev-003',
          fromVersion: '2.0.5',
          toVersion: '2.1.0',
          updateTime: '2024-01-14T14:20:00Z',
          status: 'failed',
          errorMessage: '网络连接超时',
          duration: 45,
          updatedBy: 'admin'
        }
      ],
      // 扩展字段
      isOnline: false,
      signalStrength: 25,
      location: '办公室B-205',
      lastHeartbeat: '2024-01-16T09:15:00Z',
      updateProgress: 0,
      errorCode: 'NETWORK_TIMEOUT',
      errorMessage: '网络连接超时',
      canUpdate: false,
      updateRestrictions: ['设备离线', '信号强度不足（需要至少30%）']
    },
    {
      deviceId: 'dev-004',
      deviceSerialNumber: 'EDU-002468',
      deviceModel: 'YX-EDU-2024',
      deviceName: '教学设备-004',
      currentVersion: '2.0.3',
      latestVersion: '2.1.0',
      updateAvailable: true,
      lastUpdateCheck: '2024-01-16T10:30:00Z',
      updateStatus: 'updating',
      lastUpdateTime: '2024-01-10T11:30:00Z',
      updateHistory: [
        {
          id: 'update-004',
          deviceId: 'dev-004',
          fromVersion: '2.0.1',
          toVersion: '2.0.3',
          updateTime: '2024-01-10T11:30:00Z',
          status: 'success',
          duration: 195,
          updatedBy: 'admin'
        }
      ],
      // 扩展字段
      isOnline: true,
      signalStrength: 78,
      location: '教室C-302',
      lastHeartbeat: '2024-01-16T10:30:00Z',
      updateProgress: 45,
      canUpdate: false,
      updateRestrictions: ['设备正在更新中']
    },
    {
      deviceId: 'dev-005',
      deviceSerialNumber: 'HOME-001357',
      deviceModel: 'YX-HOME-2024',
      deviceName: '家用设备-005',
      currentVersion: '2.0.5',
      latestVersion: '2.1.0',
      updateAvailable: true,
      lastUpdateCheck: '2024-01-16T10:30:00Z',
      updateStatus: 'update-available',
      lastUpdateTime: '2024-01-05T16:45:00Z',
      updateHistory: [
        {
          id: 'update-005',
          deviceId: 'dev-005',
          fromVersion: '2.0.3',
          toVersion: '2.0.5',
          updateTime: '2024-01-05T16:45:00Z',
          status: 'success',
          duration: 165,
          updatedBy: 'user'
        }
      ],
      // 扩展字段
      isOnline: true,
      signalStrength: 65,
      location: '书房',
      lastHeartbeat: '2024-01-16T10:27:00Z',
      canUpdate: true,
      updateRestrictions: []
    }
  ]
}

const generateMockUpdateTasks = (): FirmwareUpdateTask[] => {
  return [
    {
      id: 'task-001',
      name: '批量更新到v2.1.0',
      targetVersion: '2.1.0',
      deviceIds: ['dev-001', 'dev-003', 'dev-005'],
      status: 'running',
      progress: {
        total: 3,
        completed: 1,
        failed: 0,
        inProgress: 2
      },
      createdBy: 'admin',
      createdAt: '2024-01-16T14:00:00Z',
      startedAt: '2024-01-16T14:05:00Z',
      devices: [
        {
          deviceId: 'dev-001',
          deviceSerialNumber: 'EDU-001234',
          status: 'downloading',
          progress: 65,
          currentStep: '下载固件文件',
          startTime: '2024-01-16T14:05:00Z',
          retryCount: 0
        },
        {
          deviceId: 'dev-003',
          deviceSerialNumber: 'PRO-009876',
          status: 'completed',
          progress: 100,
          currentStep: '更新完成',
          startTime: '2024-01-16T14:05:00Z',
          endTime: '2024-01-16T14:08:00Z',
          retryCount: 0
        },
        {
          deviceId: 'dev-005',
          deviceSerialNumber: 'HOME-001357',
          status: 'installing',
          progress: 85,
          currentStep: '安装固件',
          startTime: '2024-01-16T14:06:00Z',
          retryCount: 0
        }
      ]
    }
  ]
}

// Mock API类
export class MockFirmwareAPI {
  private firmwareVersions: FirmwareVersion[] = generateMockFirmwareVersions()
  private deviceFirmwareStatus: DeviceFirmwareStatus[] = generateMockDeviceFirmwareStatus()
  private updateTasks: FirmwareUpdateTask[] = generateMockUpdateTasks()

  // 获取固件版本列表
  async getFirmwareVersions(params: FirmwareQueryParams = {}): Promise<{ data: FirmwareListResponse }> {
    await this.delay(800)
    
    let filtered = [...this.firmwareVersions]
    
    // 搜索过滤
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filtered = filtered.filter(firmware =>
        firmware.version.toLowerCase().includes(keyword) ||
        firmware.description.toLowerCase().includes(keyword)
      )
    }
    
    // 状态过滤
    if (params.status) {
      filtered = filtered.filter(firmware => firmware.status === params.status)
    }
    
    // 设备型号过滤
    if (params.deviceModel) {
      filtered = filtered.filter(firmware => 
        firmware.deviceModel.includes(params.deviceModel!)
      )
    }
    
    // 排序
    if (params.sortBy) {
      filtered.sort((a, b) => {
        const aValue = a[params.sortBy as keyof FirmwareVersion]
        const bValue = b[params.sortBy as keyof FirmwareVersion]
        const order = params.sortOrder === 'desc' ? -1 : 1
        
        if (aValue < bValue) return -1 * order
        if (aValue > bValue) return 1 * order
        return 0
      })
    }
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const start = (page - 1) * pageSize
    const end = start + pageSize
    
    return {
      data: {
        list: filtered.slice(start, end),
        total: filtered.length,
        page,
        pageSize
      }
    }
  }

  // 获取设备固件状态列表
  async getDeviceFirmwareStatus(params: DeviceFirmwareQueryParams = {}): Promise<{ data: DeviceFirmwareListResponse }> {
    await this.delay(600)
    
    let filtered = [...this.deviceFirmwareStatus]
    
    // 搜索过滤
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filtered = filtered.filter(device =>
        device.deviceSerialNumber.toLowerCase().includes(keyword) ||
        device.deviceName.toLowerCase().includes(keyword)
      )
    }
    
    // 更新状态过滤
    if (params.updateStatus) {
      filtered = filtered.filter(device => device.updateStatus === params.updateStatus)
    }
    
    // 是否有更新过滤
    if (params.hasUpdate !== undefined) {
      filtered = filtered.filter(device => device.updateAvailable === params.hasUpdate)
    }
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const start = (page - 1) * pageSize
    const end = start + pageSize
    
    // 统计数据
    const stats = {
      total: this.deviceFirmwareStatus.length,
      upToDate: this.deviceFirmwareStatus.filter(d => d.updateStatus === 'up-to-date').length,
      updateAvailable: this.deviceFirmwareStatus.filter(d => d.updateStatus === 'update-available').length,
      updating: this.deviceFirmwareStatus.filter(d => d.updateStatus === 'updating').length,
      failed: this.deviceFirmwareStatus.filter(d => d.updateStatus === 'failed').length
    }
    
    return {
      data: {
        list: filtered.slice(start, end),
        total: filtered.length,
        page,
        pageSize,
        stats
      }
    }
  }

  // 获取更新任务列表
  async getUpdateTasks(params: UpdateTaskQueryParams = {}): Promise<{ data: UpdateTaskListResponse }> {
    await this.delay(500)
    
    let filtered = [...this.updateTasks]
    
    // 搜索过滤
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filtered = filtered.filter(task =>
        task.name.toLowerCase().includes(keyword) ||
        task.targetVersion.toLowerCase().includes(keyword)
      )
    }
    
    // 状态过滤
    if (params.status) {
      filtered = filtered.filter(task => task.status === params.status)
    }
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const start = (page - 1) * pageSize
    const end = start + pageSize
    
    return {
      data: {
        list: filtered.slice(start, end),
        total: filtered.length,
        page,
        pageSize
      }
    }
  }

  // 上传固件
  async uploadFirmware(data: FirmwareUploadData): Promise<{ data: FirmwareVersion }> {
    await this.delay(2000) // 模拟上传��间
    
    const newFirmware: FirmwareVersion = {
      id: Date.now().toString(),
      version: data.version,
      deviceModel: data.deviceModel,
      releaseDate: new Date().toISOString(),
      fileSize: data.file.size,
      fileName: data.file.name,
      filePath: `/firmware/${data.version}/${data.file.name}`,
      checksum: `sha256:${Math.random().toString(36).substring(2)}`,
      description: data.description,
      changelog: data.changelog,
      compatibility: data.compatibility,
      knownIssues: data.knownIssues,
      downloadCount: 0,
      status: 'draft',
      createdBy: 'admin',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    this.firmwareVersions.unshift(newFirmware)
    
    return { data: newFirmware }
  }

  // 创建更新任务
  async createUpdateTask(data: {
    name: string
    targetVersion: string
    deviceIds: string[]
    scheduledTime?: string
  }): Promise<{ data: FirmwareUpdateTask }> {
    await this.delay(1000)
    
    const newTask: FirmwareUpdateTask = {
      id: Date.now().toString(),
      name: data.name,
      targetVersion: data.targetVersion,
      deviceIds: data.deviceIds,
      scheduledTime: data.scheduledTime,
      status: 'pending',
      progress: {
        total: data.deviceIds.length,
        completed: 0,
        failed: 0,
        inProgress: 0
      },
      createdBy: 'admin',
      createdAt: new Date().toISOString(),
      devices: data.deviceIds.map(deviceId => {
        const device = this.deviceFirmwareStatus.find(d => d.deviceId === deviceId)
        return {
          deviceId,
          deviceSerialNumber: device?.deviceSerialNumber || 'Unknown',
          status: 'pending',
          progress: 0,
          currentStep: '等待开始',
          retryCount: 0
        }
      })
    }
    
    this.updateTasks.unshift(newTask)
    
    return { data: newTask }
  }

  // 获取固件统计数据
  async getFirmwareStats(): Promise<{ data: FirmwareStats }> {
    await this.delay(400)
    
    const stats: FirmwareStats = {
      totalVersions: this.firmwareVersions.length,
      stableVersions: this.firmwareVersions.filter(f => f.status === 'stable').length,
      testingVersions: this.firmwareVersions.filter(f => f.status === 'testing').length,
      draftVersions: this.firmwareVersions.filter(f => f.status === 'draft').length,
      deprecatedVersions: this.firmwareVersions.filter(f => f.status === 'deprecated').length,
      totalDownloads: this.firmwareVersions.reduce((sum, f) => sum + f.downloadCount, 0),
      deviceDistribution: {
        'YX-EDU-2024': 45,
        'YX-HOME-2024': 32,
        'YX-PRO-2024': 23
      }
    }
    
    return { data: stats }
  }

  // 删除固件版本
  async deleteFirmware(id: string): Promise<{ data: { success: boolean } }> {
    await this.delay(500)
    
    const index = this.firmwareVersions.findIndex(f => f.id === id)
    if (index > -1) {
      this.firmwareVersions.splice(index, 1)
    }
    
    return { data: { success: true } }
  }

  // 取消更新任务
  async cancelUpdateTask(id: string): Promise<{ data: { success: boolean } }> {
    await this.delay(300)
    
    const task = this.updateTasks.find(t => t.id === id)
    if (task) {
      task.status = 'cancelled'
    }
    
    return { data: { success: true } }
  }

  // 重试设备更新
  async retryDeviceUpdate(taskId: string, deviceId: string): Promise<{ data: { success: boolean } }> {
    await this.delay(500)
    
    const task = this.updateTasks.find(t => t.id === taskId)
    if (task) {
      const device = task.devices.find(d => d.deviceId === deviceId)
      if (device) {
        device.status = 'pending'
        device.progress = 0
        device.currentStep = '准备重试'
        device.retryCount += 1
        device.errorMessage = undefined
      }
    }
    
    return { data: { success: true } }
  }

  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

// 导出单例实例
export const mockFirmwareAPI = new MockFirmwareAPI()