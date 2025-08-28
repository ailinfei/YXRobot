import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElMessage, ElEmpty, ElDialog } from 'element-plus'
import FontPackageProgressIntegration from '@/components/fontPackage/FontPackageProgressIntegration.vue'

// Mock子组件
vi.mock('@/components/fontPackage/VisualProgressMonitor.vue', () => ({
  default: {
    name: 'VisualProgressMonitor',
    template: '<div class="mock-visual-progress-monitor">Mock VisualProgressMonitor</div>',
    props: ['packageId', 'autoStart'],
    emits: ['monitoring-started', 'monitoring-stopped', 'anomaly-resolved']
  }
}))

vi.mock('@/components/fontPackage/FontPackageWizard.vue', () => ({
  default: {
    name: 'FontPackageWizard',
    template: '<div class="mock-font-package-wizard">Mock FontPackageWizard</div>',
    props: ['modelValue'],
    emits: ['complete', 'cancel']
  }
}))

// Mock Element Plus icons
vi.mock('@element-plus/icons-vue', () => ({
  VideoPlay: { name: 'VideoPlay' },
  VideoPause: { name: 'VideoPause' },
  Refresh: { name: 'Refresh' },
  Plus: { name: 'Plus' }
}))

// Mock i18n
const mockT = vi.fn((key: string) => key)

const globalMocks = {
  $t: mockT
}

describe('FontPackageProgressIntegration', () => {
  let wrapper: any
  
  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()
  })
  
  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
    vi.useRealTimers()
  })
  
  const createWrapper = (props = {}) => {
    return mount(FontPackageProgressIntegration, {
      props,
      global: {
        mocks: globalMocks,
        components: {
          ElEmpty,
          ElDialog
        }
      }
    })
  }
  
  describe('组件渲染', () => {
    it('应该正确渲染基本结构', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.font-package-progress-integration').exists()).toBe(true)
      expect(wrapper.find('.integration-header').exists()).toBe(true)
      expect(wrapper.find('.package-list').exists()).toBe(true)
    })
    
    it('应该显示正确的标题', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('h2').text()).toBe('字体包训练进度监控')
    })
    
    it('应该在没有选择字体包时显示空状态', () => {
      wrapper = createWrapper()
      
      const emptyState = wrapper.find('.empty-state')
      expect(emptyState.exists()).toBe(true)
      
      const emptyComponent = wrapper.findComponent(ElEmpty)
      expect(emptyComponent.exists()).toBe(true)
    })
  })
  
  describe('字体包列表', () => {
    it('应该在挂载后加载字体包列表', async () => {
      wrapper = createWrapper()
      
      // 等待异步加载完成
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.fontPackages.length).toBeGreaterThan(0)
    })
    
    it('应该显示字体包卡片', async () => {
      wrapper = createWrapper()
      
      // 等待数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const packageCards = wrapper.findAll('.package-card')
      expect(packageCards.length).toBeGreaterThan(0)
    })
    
    it('应该能够选择字体包', async () => {
      wrapper = createWrapper()
      
      // 等待数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const firstCard = wrapper.find('.package-card')
      await firstCard.trigger('click')
      
      expect(wrapper.vm.selectedPackage).toBeTruthy()
      expect(firstCard.classes()).toContain('active')
    })
    
    it('应该显示字体包的状态和进度', async () => {
      wrapper = createWrapper()
      
      // 等待数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const packageCard = wrapper.find('.package-card')
      expect(packageCard.find('.package-meta').exists()).toBe(true)
      
      // 检查是否有进度条（对于训练中的包）
      const progressElement = packageCard.find('.package-progress')
      if (progressElement.exists()) {
        expect(progressElement.find('.progress-text').exists()).toBe(true)
      }
    })
  })
  
  describe('进度监控', () => {
    it('应该在选择字体包后显示监控区域', async () => {
      wrapper = createWrapper()
      
      // 等待数据加载并选择字体包
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const firstCard = wrapper.find('.package-card')
      await firstCard.trigger('click')
      
      expect(wrapper.find('.progress-monitor-section').exists()).toBe(true)
      expect(wrapper.findComponent({ name: 'VisualProgressMonitor' }).exists()).toBe(true)
    })
    
    it('应该能够开始监控', async () => {
      wrapper = createWrapper()
      
      // 选择字体包
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const firstCard = wrapper.find('.package-card')
      await firstCard.trigger('click')
      
      // 点击开始监控按钮
      const startButton = wrapper.find('button')
      if (startButton.exists() && startButton.text().includes('开始监控')) {
        await startButton.trigger('click')
        
        // 等待异步操作完成
        vi.advanceTimersByTime(1000)
        await wrapper.vm.$nextTick()
        
        expect(wrapper.vm.isMonitoring).toBe(true)
      }
    })
    
    it('应该能够停止监控', async () => {
      wrapper = createWrapper()
      
      // 选择字体包并开始监控
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      const firstCard = wrapper.find('.package-card')
      await firstCard.trigger('click')
      
      // 手动设置监控状态
      wrapper.vm.isMonitoring = true
      await wrapper.vm.$nextTick()
      
      // 点击停止监控按钮
      const stopButton = wrapper.find('button')
      if (stopButton.exists() && stopButton.text().includes('停止监控')) {
        await stopButton.trigger('click')
        
        // 等待异步操作完成
        vi.advanceTimersByTime(500)
        await wrapper.vm.$nextTick()
        
        expect(wrapper.vm.isMonitoring).toBe(false)
      }
    })
  })
  
  describe('字体包创建', () => {
    it('应该能够打开创建对话框', async () => {
      wrapper = createWrapper()
      
      const createButton = wrapper.find('button')
      if (createButton.exists() && createButton.text().includes('创建新字体包')) {
        await createButton.trigger('click')
        
        expect(wrapper.vm.showCreateDialog).toBe(true)
      }
    })
    
    it('应该在字体包创建完成后更新列表', async () => {
      wrapper = createWrapper()
      
      const initialCount = wrapper.vm.fontPackages.length
      
      // 模拟字体包创建完成
      const mockData = {
        basicInfo: {
          name: '测试字体包',
          description: '测试描述',
          version: 'v1.0.0',
          fontType: 'kaishu',
          difficulty: 3,
          tags: ['测试']
        },
        sampleUpload: {
          targetCharacters: ['一', '二', '三']
        }
      }
      
      wrapper.vm.handlePackageCreated(mockData)
      
      expect(wrapper.vm.fontPackages.length).toBe(initialCount + 1)
      expect(wrapper.vm.selectedPackage).toBeTruthy()
      expect(wrapper.vm.selectedPackage.name).toBe('测试字体包')
    })
  })
  
  describe('状态管理', () => {
    it('应该正确获取状态类型', () => {
      wrapper = createWrapper()
      
      expect(wrapper.vm.getStatusType('draft')).toBe('info')
      expect(wrapper.vm.getStatusType('training')).toBe('warning')
      expect(wrapper.vm.getStatusType('completed')).toBe('success')
      expect(wrapper.vm.getStatusType('failed')).toBe('danger')
    })
    
    it('应该正确获取状态文本', () => {
      wrapper = createWrapper()
      
      expect(wrapper.vm.getStatusText('draft')).toBe('草稿')
      expect(wrapper.vm.getStatusText('training')).toBe('训练中')
      expect(wrapper.vm.getStatusText('completed')).toBe('已完成')
      expect(wrapper.vm.getStatusText('failed')).toBe('失败')
    })
  })
  
  describe('事件处理', () => {
    it('应该处理监控开始事件', () => {
      wrapper = createWrapper()
      
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      
      wrapper.vm.handleMonitoringStarted(1)
      
      expect(consoleSpy).toHaveBeenCalledWith('开始监控字体包 1')
      
      consoleSpy.mockRestore()
    })
    
    it('应该处理监控停止事件', () => {
      wrapper = createWrapper()
      
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      
      wrapper.vm.handleMonitoringStopped()
      
      expect(consoleSpy).toHaveBeenCalledWith('停止监控')
      
      consoleSpy.mockRestore()
    })
    
    it('应该处理异常解决事件', () => {
      wrapper = createWrapper()
      
      const mockAnomaly = {
        type: 'performance',
        severity: 'medium',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      // Mock ElMessage
      const messageSpy = vi.spyOn(ElMessage, 'success').mockImplementation(() => {})
      
      wrapper.vm.handleAnomalyResolved(mockAnomaly)
      
      expect(messageSpy).toHaveBeenCalledWith('异常已解决: 测试异常')
      
      messageSpy.mockRestore()
    })
  })
  
  describe('数据刷新', () => {
    it('应该能够刷新字体包列表', async () => {
      wrapper = createWrapper()
      
      const refreshSpy = vi.spyOn(wrapper.vm, 'loadFontPackages')
      
      await wrapper.vm.refreshPackageList()
      
      expect(refreshSpy).toHaveBeenCalled()
    })
  })
  
  describe('自动监控', () => {
    it('应该在选择训练中的字体包时自动开始监控', async () => {
      wrapper = createWrapper()
      
      // 等待数据加载
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(100)
      await wrapper.vm.$nextTick()
      
      // 找到训练中的字体包
      const trainingPackage = wrapper.vm.fontPackages.find((pkg: any) => pkg.status === 'training')
      
      if (trainingPackage) {
        const startMonitoringSpy = vi.spyOn(wrapper.vm, 'startMonitoring')
        
        wrapper.vm.selectPackage(trainingPackage)
        
        expect(startMonitoringSpy).toHaveBeenCalled()
      }
    })
  })
})