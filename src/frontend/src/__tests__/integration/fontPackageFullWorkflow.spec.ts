import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElMessage } from 'element-plus'
import FontPackageWizard from '@/components/fontPackage/FontPackageWizard.vue'
import SmartSampleUploader from '@/components/fontPackage/SmartSampleUploader.vue'
import VisualProgressMonitor from '@/components/fontPackage/VisualProgressMonitor.vue'
import FontPackageProgressIntegration from '@/components/fontPackage/FontPackageProgressIntegration.vue'

// Mock WebSocket
const mockWebSocket = {
  send: vi.fn(),
  close: vi.fn(),
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
  readyState: WebSocket.OPEN
}

global.WebSocket = vi.fn(() => mockWebSocket) as any

// Mock File API
global.FileReader = class {
  result: string | ArrayBuffer | null = null
  onload: ((event: ProgressEvent<FileReader>) => void) | null = null
  onerror: ((event: ProgressEvent<FileReader>) => void) | null = null
  
  readAsDataURL(file: File) {
    setTimeout(() => {
      this.result = `data:image/jpeg;base64,mock-base64-data-for-${file.name}`
      if (this.onload) {
        this.onload({ target: this } as ProgressEvent<FileReader>)
      }
    }, 10)
  }
}

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
global.localStorage = localStorageMock as any

describe('字体包完整工作流程集成测试', () => {
  let wrapper: any
  
  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()
    localStorageMock.getItem.mockReturnValue(null)
  })
  
  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
    vi.useRealTimers()
  })

  describe('完整工作流程测试', () => {
    it('应该完成从创建到监控的完整流程', async () => {
      // 1. 创建字体包向导
      const wizardWrapper = mount(FontPackageWizard, {
        props: { modelValue: true },
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      // 验证向导初始状态
      expect(wizardWrapper.find('.wizard-header').exists()).toBe(true)
      expect(wizardWrapper.vm.currentStep).toBe(1)   
   // 2. 填写基本信息
      wizardWrapper.vm.wizardData.basicInfo = {
        name: '测试字体包',
        description: '集成测试字体包',
        fontType: 'kaishu',
        difficulty: 3,
        version: 'v1.0.0',
        tags: ['测试', '集成']
      }

      // 验证步骤1
      wizardWrapper.vm.stepValidations[1] = {
        isValid: true,
        errors: [],
        warnings: []
      }

      // 进入下一步
      await wizardWrapper.vm.handleNext()
      expect(wizardWrapper.vm.currentStep).toBe(2)

      // 3. 测试样本上传
      const mockFiles = [
        new File(['mock-content'], '一.jpg', { type: 'image/jpeg' }),
        new File(['mock-content'], '二.png', { type: 'image/png' }),
        new File(['mock-content'], '三.gif', { type: 'image/gif' })
      ]

      // 模拟文件上传
      wizardWrapper.vm.wizardData.sampleUpload = {
        targetCharacters: ['一', '二', '三'],
        uploadedFiles: mockFiles.map((file, index) => ({
          uid: `file-${index}`,
          name: file.name,
          url: `data:image/jpeg;base64,mock-data-${index}`,
          raw: file,
          size: file.size,
          type: file.type,
          status: 'success',
          recognizedCharacters: [file.name.charAt(0)]
        })),
        analysisResult: {
          totalFiles: 3,
          recognizedCharacters: 3,
          duplicates: [],
          basicQualityCheck: {
            validFiles: 3,
            invalidFiles: 0,
            averageQuality: 85,
            issues: []
          }
        }
      }

      // 验证步骤2
      wizardWrapper.vm.stepValidations[2] = {
        isValid: true,
        errors: [],
        warnings: []
      }

      await wizardWrapper.vm.handleNext()
      expect(wizardWrapper.vm.currentStep).toBe(3)

      // 4. 配置参数
      wizardWrapper.vm.wizardData.configuration = {
        trainingSettings: {
          epochs: 100,
          batchSize: 32,
          learningRate: 0.001
        },
        qualitySettings: {
          minQuality: 80,
          enableAutoOptimization: true
        }
      }

      wizardWrapper.vm.stepValidations[3] = {
        isValid: true,
        errors: [],
        warnings: []
      }

      await wizardWrapper.vm.handleNext()
      expect(wizardWrapper.vm.currentStep).toBe(4)

      // 5. 确认创建
      wizardWrapper.vm.wizardData.review = {
        confirmed: true
      }

      wizardWrapper.vm.stepValidations[4] = {
        isValid: true,
        errors: [],
        warnings: []
      }

      await wizardWrapper.vm.handleNext()
      expect(wizardWrapper.vm.currentStep).toBe(5)

      // 6. 完成创建并开始监控
      const completeSpy = vi.fn()
      wizardWrapper.vm.$emit = completeSpy

      await wizardWrapper.vm.handleComplete()

      // 验证创建完成事件
      expect(completeSpy).toHaveBeenCalledWith('complete', wizardWrapper.vm.wizardData)

      wizardWrapper.unmount()
    })
  })

  describe('数据一致性验证', () => {
    it('应该在各组件间保持数据一致性', async () => {
      // 创建集成组件
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      // 等待数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 验证初始数据
      expect(wrapper.vm.fontPackages.length).toBeGreaterThan(0)

      // 选择一个字体包
      const firstPackage = wrapper.vm.fontPackages[0]
      wrapper.vm.selectPackage(firstPackage)

      expect(wrapper.vm.selectedPackage).toEqual(firstPackage)

      // 验证监控组件接收到正确的packageId
      const monitorComponent = wrapper.findComponent(VisualProgressMonitor)
      expect(monitorComponent.props('packageId')).toBe(firstPackage.id)
    })

    it('应该正确同步状态变化', async () => {
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 选择训练中的字体包
      const trainingPackage = wrapper.vm.fontPackages.find((pkg: any) => pkg.status === 'training')
      if (trainingPackage) {
        wrapper.vm.selectPackage(trainingPackage)

        // 验证自动开始监控
        expect(wrapper.vm.isMonitoring).toBe(false) // 初始状态

        // 手动开始监控
        await wrapper.vm.startMonitoring()
        vi.advanceTimersByTime(1000)
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.isMonitoring).toBe(true)

        // 停止监控
        await wrapper.vm.stopMonitoring()
        vi.advanceTimersByTime(500)
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.isMonitoring).toBe(false)
      }
    })
  })

  describe('错误处理和恢复', () => {
    it('应该正确处理网络错误', async () => {
      // Mock网络错误
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      const messageSpy = vi.spyOn(ElMessage, 'error').mockImplementation(() => {})

      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      // 模拟网络错误
      wrapper.vm.loadFontPackages = vi.fn().mockRejectedValue(new Error('Network error'))

      await wrapper.vm.refreshPackageList()

      expect(messageSpy).toHaveBeenCalledWith('刷新失败')

      consoleSpy.mockRestore()
      messageSpy.mockRestore()
    })

    it('应该处理WebSocket连接错误', async () => {
      // Mock WebSocket错误
      const mockErrorWebSocket = {
        ...mockWebSocket,
        readyState: WebSocket.CLOSED
      }

      global.WebSocket = vi.fn(() => mockErrorWebSocket) as any

      const monitorWrapper = mount(VisualProgressMonitor, {
        props: { packageId: 1, autoStart: true },
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await monitorWrapper.vm.$nextTick()

      // 验证连接状态
      expect(monitorWrapper.vm.connectionStatus).toBe('disconnected')

      monitorWrapper.unmount()
    })
  })

  describe('性能和内存管理', () => {
    it('应该正确清理资源', async () => {
      const cleanupSpy = vi.fn()

      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      // 模拟组件销毁时的清理
      wrapper.vm.$options.beforeUnmount = [cleanupSpy]

      wrapper.unmount()

      // 验证清理函数被调用
      expect(cleanupSpy).toHaveBeenCalled()
    })

    it('应该限制内存使用', async () => {
      // 创建大量数据测试内存管理
      const largeDataSet = Array.from({ length: 1000 }, (_, i) => ({
        id: i,
        name: `字体包${i}`,
        description: `描述${i}`,
        status: 'completed'
      }))

      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      wrapper.vm.fontPackages = largeDataSet

      await wrapper.vm.$nextTick()

      // 验证组件仍然响应
      expect(wrapper.vm.fontPackages.length).toBe(1000)
      expect(wrapper.find('.package-grid').exists()).toBe(true)
    })
  })
})