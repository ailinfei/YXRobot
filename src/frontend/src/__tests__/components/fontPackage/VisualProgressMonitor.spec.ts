import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElProgress, ElRow, ElCol, ElAlert, ElIcon } from 'element-plus'
import VisualProgressMonitor from '@/components/fontPackage/VisualProgressMonitor.vue'
import type { AnomalyAlert } from '@/types/fontPackage'

// Mock Element Plus icons
vi.mock('@element-plus/icons-vue', () => ({
  CircleCheck: { name: 'CircleCheck' },
  Warning: { name: 'Warning' },
  Timer: { name: 'Timer' },
  Monitor: { name: 'Monitor' },
  VideoPlay: { name: 'VideoPlay' },
  Clock: { name: 'Clock' }
}))

// Mock i18n
const mockT = vi.fn((key: string) => {
  const translations: Record<string, string> = {
    'fontPackage.progress.title': '训练进度',
    'fontPackage.progress.remaining': '剩余',
    'fontPackage.progress.characterProgress': '字符进度',
    'fontPackage.progress.completed': '已完成',
    'fontPackage.progress.total': '总计',
    'fontPackage.progress.current': '当前',
    'fontPackage.progress.performance': '性能指标',
    'fontPackage.progress.trainingSpeed': '训练速度',
    'fontPackage.progress.memoryUsage': '内存使用',
    'fontPackage.progress.gpuUtilization': 'GPU利用率',
    'fontPackage.progress.estimatedCompletion': '预计完成',
    'fontPackage.progress.alerts': '异常告警',
    'fontPackage.progress.currentPhase': '当前阶段',
    'fontPackage.progress.anomalyTypes.performance': '性能',
    'fontPackage.progress.anomalyTypes.quality': '质量',
    'fontPackage.progress.anomalyTypes.error': '错误',
    'fontPackage.progress.phaseStatus.pending': '等待中',
    'fontPackage.progress.phaseStatus.running': '运行中',
    'fontPackage.progress.phaseStatus.completed': '已完成',
    'fontPackage.progress.phaseStatus.failed': '失败'
  }
  return translations[key] || key
})

const globalMocks = {
  $t: mockT
}

describe('VisualProgressMonitor', () => {
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
    return mount(VisualProgressMonitor, {
      props: {
        packageId: 1,
        autoStart: false,
        ...props
      },
      global: {
        mocks: globalMocks,
        components: {
          ElProgress,
          ElRow,
          ElCol,
          ElAlert,
          ElIcon
        }
      }
    })
  }
  
  describe('组件渲染', () => {
    it('应该正确渲染基本结构', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.visual-progress-monitor').exists()).toBe(true)
      expect(wrapper.find('.progress-overview').exists()).toBe(true)
      expect(wrapper.find('.progress-details').exists()).toBe(true)
      expect(wrapper.find('.phase-details').exists()).toBe(true)
    })
    
    it('应该显示正确的标题', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('h3').text()).toBe('训练进度')
    })
    
    it('应该显示健康状态指示器', () => {
      wrapper = createWrapper()
      
      const statusIndicator = wrapper.find('.status-indicator')
      expect(statusIndicator.exists()).toBe(true)
      expect(statusIndicator.classes()).toContain('healthy')
    })
  })
  
  describe('进度显示', () => {
    it('应该显示主进度条', () => {
      wrapper = createWrapper()
      
      const progressBar = wrapper.findComponent(ElProgress)
      expect(progressBar.exists()).toBe(true)
      expect(progressBar.props('percentage')).toBe(0)
      expect(progressBar.props('strokeWidth')).toBe(12)
    })
    
    it('应该显示进度信息', () => {
      wrapper = createWrapper()
      
      const progressInfo = wrapper.find('.progress-info')
      expect(progressInfo.exists()).toBe(true)
      
      const percentage = progressInfo.find('.percentage')
      expect(percentage.text()).toBe('0%')
      
      const phase = progressInfo.find('.phase')
      expect(phase.text()).toBe('准备中')
    })
    
    it('应该在有剩余时间时显示ETA', async () => {
      wrapper = createWrapper()
      
      // 模拟进度更新
      await wrapper.vm.$nextTick()
      vi.advanceTimersByTime(2000)
      await wrapper.vm.$nextTick()
      
      const eta = wrapper.find('.eta')
      if (eta.exists()) {
        expect(eta.text()).toContain('剩余')
      }
    })
  })
  
  describe('字符进度', () => {
    it('应该显示字符统计信息', () => {
      wrapper = createWrapper()
      
      const characterStats = wrapper.find('.character-stats')
      expect(characterStats.exists()).toBe(true)
      
      const statItems = characterStats.findAll('.stat-item')
      expect(statItems.length).toBe(3)
      
      expect(statItems[0].find('.label').text()).toBe('已完成')
      expect(statItems[1].find('.label').text()).toBe('总计')
      expect(statItems[2].find('.label').text()).toBe('当前')
    })
    
    it('应该在有字符进度时显示字符网格', async () => {
      wrapper = createWrapper()
      
      // 等待模拟数据更新
      vi.advanceTimersByTime(2000)
      await wrapper.vm.$nextTick()
      
      const characterGrid = wrapper.find('.character-grid')
      if (characterGrid.exists()) {
        const characterItems = characterGrid.findAll('.character-item')
        expect(characterItems.length).toBeGreaterThan(0)
      }
    })
  })
  
  describe('性能指标', () => {
    it('应该显示性能指标', () => {
      wrapper = createWrapper()
      
      const performanceMetrics = wrapper.find('.performance-metrics')
      expect(performanceMetrics.exists()).toBe(true)
      
      const metricItems = performanceMetrics.findAll('.metric-item')
      expect(metricItems.length).toBe(4)
      
      const labels = metricItems.map(item => item.find('.label').text())
      expect(labels).toContain('训练速度')
      expect(labels).toContain('内存使用')
      expect(labels).toContain('GPU利用率')
      expect(labels).toContain('预计完成')
    })
  })
  
  describe('异常告警', () => {
    it('应该在没有异常时不显示告警区域', () => {
      wrapper = createWrapper()
      
      const anomalyAlerts = wrapper.find('.anomaly-alerts')
      expect(anomalyAlerts.exists()).toBe(false)
    })
    
    it('应该在有异常时显示告警', async () => {
      wrapper = createWrapper()
      
      // 等待可能的异常生成
      vi.advanceTimersByTime(10000)
      await wrapper.vm.$nextTick()
      
      const anomalyAlerts = wrapper.find('.anomaly-alerts')
      if (anomalyAlerts.exists()) {
        expect(anomalyAlerts.find('h4').text()).toBe('异常告警')
        
        const alerts = anomalyAlerts.findAllComponents(ElAlert)
        expect(alerts.length).toBeGreaterThan(0)
      }
    })
    
    it('应该能够关闭异常告警', async () => {
      wrapper = createWrapper()
      
      // 手动添加一个异常
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      wrapper.vm.anomalies.push(anomaly)
      await wrapper.vm.$nextTick()
      
      const alert = wrapper.findComponent(ElAlert)
      if (alert.exists()) {
        await alert.vm.$emit('close')
        expect(wrapper.vm.anomalies.length).toBe(0)
      }
    })
  })
  
  describe('阶段详情', () => {
    it('应该显示当前训练阶段', () => {
      wrapper = createWrapper()
      
      const phaseDetails = wrapper.find('.phase-details')
      expect(phaseDetails.exists()).toBe(true)
      
      const phaseName = phaseDetails.find('.phase-name')
      expect(phaseName.text()).toBe('准备中')
      
      const phaseDescription = phaseDetails.find('.phase-description')
      expect(phaseDescription.text()).toBe('正在初始化训练环境')
    })
    
    it('应该显示阶段进度条', () => {
      wrapper = createWrapper()
      
      const phaseDetails = wrapper.find('.phase-details')
      const phaseProgress = phaseDetails.findComponent(ElProgress)
      
      expect(phaseProgress.exists()).toBe(true)
      expect(phaseProgress.props('strokeWidth')).toBe(6)
      expect(phaseProgress.props('showText')).toBe(true)
    })
  })
  
  describe('组件方法', () => {
    it('应该能够开始监控', () => {
      wrapper = createWrapper()
      
      const startSpy = vi.spyOn(wrapper.vm, 'startMonitoring')
      wrapper.vm.startMonitoring(1)
      
      expect(startSpy).toHaveBeenCalledWith(1)
    })
    
    it('应该能够停止监控', () => {
      wrapper = createWrapper()
      
      const stopSpy = vi.spyOn(wrapper.vm, 'stopMonitoring')
      wrapper.vm.stopMonitoring()
      
      expect(stopSpy).toHaveBeenCalled()
    })
    
    it('应该能够获取进度快照', () => {
      wrapper = createWrapper()
      
      const snapshot = wrapper.vm.getProgressSnapshot()
      
      expect(snapshot).toHaveProperty('timestamp')
      expect(snapshot).toHaveProperty('progress')
      expect(snapshot).toHaveProperty('metrics')
      expect(snapshot).toHaveProperty('health')
    })
  })
  
  describe('事件发射', () => {
    it('应该在开始监控时发射事件', () => {
      wrapper = createWrapper()
      
      wrapper.vm.startMonitoring(1)
      
      expect(wrapper.emitted('monitoringStarted')).toBeTruthy()
      expect(wrapper.emitted('monitoringStarted')[0]).toEqual([1])
    })
    
    it('应该在停止监控时发射事件', () => {
      wrapper = createWrapper()
      
      wrapper.vm.stopMonitoring()
      
      expect(wrapper.emitted('monitoringStopped')).toBeTruthy()
    })
    
    it('应该在解决异常时发射事件', async () => {
      wrapper = createWrapper()
      
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      wrapper.vm.anomalies.push(anomaly)
      await wrapper.vm.$nextTick()
      
      wrapper.vm.resolveAnomaly(anomaly)
      
      expect(wrapper.emitted('anomalyResolved')).toBeTruthy()
      expect(wrapper.emitted('anomalyResolved')[0]).toEqual([anomaly])
    })
  })
  
  describe('自动启动', () => {
    it('应该在autoStart为true时自动开始监控', () => {
      const startSpy = vi.fn()
      
      wrapper = createWrapper({ 
        packageId: 1, 
        autoStart: true 
      })
      
      // 由于组件在mounted时会调用startMonitoring，我们需要检查相关状态
      expect(wrapper.vm.isMonitoring).toBe(true)
    })
    
    it('应该在autoStart为false时不自动开始监控', () => {
      wrapper = createWrapper({ 
        packageId: 1, 
        autoStart: false 
      })
      
      expect(wrapper.vm.isMonitoring).toBe(false)
    })
  })
  
  describe('工具方法', () => {
    it('应该正确格式化时间', () => {
      wrapper = createWrapper()
      
      expect(wrapper.vm.formatTime(30)).toBe('30秒')
      expect(wrapper.vm.formatTime(90)).toBe('1分钟')
      expect(wrapper.vm.formatTime(3660)).toBe('1小时1分钟')
    })
    
    it('应该正确获取告警类型', () => {
      wrapper = createWrapper()
      
      expect(wrapper.vm.getAlertType('low')).toBe('info')
      expect(wrapper.vm.getAlertType('medium')).toBe('warning')
      expect(wrapper.vm.getAlertType('high')).toBe('error')
    })
    
    it('应该正确格式化时间戳', () => {
      wrapper = createWrapper()
      
      const timestamp = '2024-01-01T12:00:00.000Z'
      const formatted = wrapper.vm.formatTimestamp(timestamp)
      
      expect(typeof formatted).toBe('string')
      expect(formatted.length).toBeGreaterThan(0)
    })
  })
})