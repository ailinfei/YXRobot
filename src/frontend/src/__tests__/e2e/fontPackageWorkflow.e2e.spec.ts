import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import FontPackageProgressIntegration from '@/components/fontPackage/FontPackageProgressIntegration.vue'
import type { FontPackage } from '@/types/fontPackage'

// Mock全局依赖
global.WebSocket = vi.fn(() => ({
  send: vi.fn(),
  close: vi.fn(),
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
  readyState: WebSocket.OPEN
})) as any

global.FileReader = class {
  result: string | null = null
  onload: ((event: ProgressEvent<FileReader>) => void) | null = null

  readAsDataURL(file: File) {
    setTimeout(() => {
      this.result = `data:image/jpeg;base64,mock-${file.name}`
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
  removeItem: vi.fn()
}
global.localStorage = localStorageMock as any

describe('字体包工作流程端到端测试', () => {
  let wrapper: VueWrapper<any>

  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()

    // Mock ElMessage
    vi.spyOn(ElMessage, 'success').mockImplementation(() => { })
    vi.spyOn(ElMessage, 'error').mockImplementation(() => { })
    vi.spyOn(ElMessage, 'warning').mockImplementation(() => { })

    // Mock ElMessageBox
    vi.spyOn(ElMessageBox, 'confirm').mockResolvedValue('confirm')
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
    vi.useRealTimers()
    vi.restoreAllMocks()
  })

  describe('完整用户工作流程', () => {
    it('用户应该能够完成完整的字体包创建和监控流程', async () => {
      // 1. 初始化应用
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      // 等待初始数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 验证初始状态
      expect(wrapper.find('.font-package-progress-integration').exists()).toBe(true)
      expect(wrapper.vm.fontPackages.length).toBeGreaterThan(0)

      // 2. 用户点击创建新字体包
      const createButton = wrapper.find('button')
      if (createButton.exists() && createButton.text().includes('创建新字体包')) {
        await createButton.trigger('click')
        expect(wrapper.vm.showCreateDialog).toBe(true)
      }

      // 3. 模拟用户完成字体包创建
      const mockWizardData = {
        basicInfo: {
          name: 'E2E测试字体包',
          description: '端到端测试创建的字体包',
          fontType: 'kaishu',
          difficulty: 3,
          version: 'v1.0.0',
          tags: ['测试', 'E2E']
        },
        sampleUpload: {
          targetCharacters: ['测', '试', '字', '体'],
          uploadedFiles: [
            {
              uid: 'file-1',
              name: '测.jpg',
              url: 'data:image/jpeg;base64,mock-data',
              size: 1024,
              type: 'image/jpeg',
              status: 'success',
              recognizedCharacters: ['测']
            }
          ],
          analysisResult: {
            totalFiles: 1,
            recognizedCharacters: 1,
            duplicates: [],
            basicQualityCheck: {
              validFiles: 1,
              invalidFiles: 0,
              averageQuality: 90,
              issues: []
            }
          }
        },
        configuration: {
          trainingSettings: {
            epochs: 50,
            batchSize: 16
          },
          qualitySettings: {
            minQuality: 85
          }
        },
        review: {
          confirmed: true
        }
      }

      // 触发字体包创建完成
      wrapper.vm.handlePackageCreated(mockWizardData)

      // 验证新字体包被添加
      expect(wrapper.vm.fontPackages[0].name).toBe('E2E测试字体包')
      expect(wrapper.vm.selectedPackage).toBeTruthy()
      expect(wrapper.vm.selectedPackage.name).toBe('E2E测试字体包')

      // 4. 验证自动开始监控
      vi.advanceTimersByTime(1000)
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.isMonitoring).toBe(true)

      // 5. 用户查看进度监控
      const monitorSection = wrapper.find('.progress-monitor-section')
      expect(monitorSection.exists()).toBe(true)

      const visualMonitor = wrapper.findComponent({ name: 'VisualProgressMonitor' })
      expect(visualMonitor.exists()).toBe(true)
      expect(visualMonitor.props('packageId')).toBe(wrapper.vm.selectedPackage.id)

      // 6. 用户停止监控
      const stopButton = wrapper.find('button')
      if (stopButton.exists() && stopButton.text().includes('停止监控')) {
        await stopButton.trigger('click')

        vi.advanceTimersByTime(500)
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.isMonitoring).toBe(false)
      }

      // 7. 用户刷新列表
      const refreshButton = wrapper.find('button')
      if (refreshButton.exists() && refreshButton.text().includes('刷新')) {
        await refreshButton.trigger('click')

        vi.advanceTimersByTime(100)
        await wrapper.vm.$nextTick()

        expect(ElMessage.success).toHaveBeenCalledWith('列表已刷新')
      }
    })

    it('用户应该能够处理错误情况', async () => {
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 模拟监控启动失败
      wrapper.vm.startMonitoring = vi.fn().mockRejectedValue(new Error('监控启动失败'))

      // 选择一个字体包
      const firstPackage = wrapper.vm.fontPackages[0]
      wrapper.vm.selectPackage(firstPackage)

      // 尝试启动监控
      try {
        await wrapper.vm.startMonitoring()
      } catch (error) {
        expect(error.message).toBe('监控启动失败')
      }

      // 验证错误处理
      expect(ElMessage.error).toHaveBeenCalledWith('启动监控失败')
    })
  })

  describe('用户交互验证', () => {
    it('应该正确响应用户的所有交互', async () => {
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 测试字体包选择
      const packageCards = wrapper.findAll('.package-card')
      if (packageCards.length > 0) {
        await packageCards[0].trigger('click')
        expect(packageCards[0].classes()).toContain('active')
      }

      // 测试按钮状态变化
      const buttons = wrapper.findAll('button')
      buttons.forEach(button => {
        expect(button.attributes('disabled')).toBeUndefined()
      })

      // 测试加载状态
      wrapper.vm.isStartingMonitor = true
      await wrapper.vm.$nextTick()

      const startButton = wrapper.find('button[loading]')
      if (startButton.exists()) {
        expect(startButton.attributes('loading')).toBeDefined()
      }
    })

    it('应该提供适当的用户反馈', async () => {
      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 测试成功消息
      await wrapper.vm.refreshPackageList()
      expect(ElMessage.success).toHaveBeenCalledWith('列表已刷新')

      // 测试异常处理消息
      const mockAnomaly = {
        type: 'performance',
        severity: 'medium',
        message: '性能异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }

      wrapper.vm.handleAnomalyResolved(mockAnomaly)
      expect(ElMessage.success).toHaveBeenCalledWith('异常已解决: 性能异常')
    })
  })

  describe('数据持久化验证', () => {
    it('应该正确保存和恢复用户数据', async () => {
      // 模拟已保存的数据
      localStorageMock.getItem.mockReturnValue(JSON.stringify({
        selectedPackageId: 1,
        monitoringState: true,
        lastUpdate: new Date().toISOString()
      }))

      wrapper = mount(FontPackageProgressIntegration, {
        global: {
          mocks: { $t: (key: string) => key }
        }
      })

      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()

      // 验证数据恢复
      expect(localStorageMock.getItem).toHaveBeenCalled()

      // 模拟数据保存
      const newPackage: FontPackage = {
        id: 999,
        name: '新字体包',
        description: '测试描述',
        version: 'v1.0.0',
        fontType: 'kaishu',
        difficulty: 3,
        tags: [],
        status: 'draft',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        createdBy: 'test-user',
        targetCharacters: ['测', '试']
      }

      wrapper.vm.fontPackages.push(newPackage)
      wrapper.vm.selectPackage(newPackage)

      // 验证数据保存
      expect(localStorageMock.setItem).toHaveBeenCalled()
    })
  })
})