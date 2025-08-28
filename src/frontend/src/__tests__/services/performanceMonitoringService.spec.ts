import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { PerformanceMonitoringService } from '@/services/performanceMonitoringService'

// Mock performance API
global.performance = {
  ...global.performance,
  now: vi.fn(() => Date.now()),
  mark: vi.fn(),
  measure: vi.fn(),
  memory: {
    usedJSHeapSize: 1000000,
    jsHeapSizeLimit: 10000000
  }
} as any

// Mock PerformanceObserver
global.PerformanceObserver = vi.fn().mockImplementation((callback) => ({
  observe: vi.fn(),
  disconnect: vi.fn()
}))

describe('PerformanceMonitoringService', () => {
  let service: PerformanceMonitoringService
  
  beforeEach(() => {
    vi.clearAllMocks()
    service = new PerformanceMonitoringService()
  })
  
  afterEach(() => {
    service.stopMonitoring()
  })

  describe('基本功能', () => {
    it('应该能够启动和停止监控', () => {
      expect(service['isMonitoring']).toBe(false)
      
      service.startMonitoring()
      expect(service['isMonitoring']).toBe(true)
      
      service.stopMonitoring()
      expect(service['isMonitoring']).toBe(false)
    })

    it('应该能够获取当前性能指标', () => {
      const metrics = service.getCurrentMetrics()
      
      expect(metrics).toHaveProperty('renderTime')
      expect(metrics).toHaveProperty('interactionDelay')
      expect(metrics).toHaveProperty('memoryUsage')
      expect(metrics).toHaveProperty('networkLatency')
      expect(metrics).toHaveProperty('fps')
      expect(metrics).toHaveProperty('bundleSize')
    })

    it('应该能够重置性能指标', () => {
      // 修改一些指标
      service['metrics'].renderTime = 100
      service['metrics'].memoryUsage = 50
      
      service.resetMetrics()
      
      const metrics = service.getCurrentMetrics()
      expect(metrics.renderTime).toBe(0)
      expect(metrics.memoryUsage).toBe(0)
    })
  })

  describe('性能测量', () => {
    it('应该能够测量渲染时间', () => {
      const mockRenderFn = vi.fn()
      
      service.measureRenderTime('TestComponent', mockRenderFn)
      
      expect(mockRenderFn).toHaveBeenCalled()
      expect(performance.mark).toHaveBeenCalledWith('TestComponent-render-start')
      expect(performance.mark).toHaveBeenCalledWith('TestComponent-render-end')
    })

    it('应该能够测量交互延迟', () => {
      const mockInteractionFn = vi.fn()
      
      service.measureInteractionDelay('click', mockInteractionFn)
      
      expect(mockInteractionFn).toHaveBeenCalled()
    })
  })

  describe('性能报告生成', () => {
    it('应该能够生成性能报告', () => {
      const report = service.generateReport()
      
      expect(report).toHaveProperty('timestamp')
      expect(report).toHaveProperty('metrics')
      expect(report).toHaveProperty('recommendations')
      expect(report).toHaveProperty('issues')
      
      expect(Array.isArray(report.recommendations)).toBe(true)
      expect(Array.isArray(report.issues)).toBe(true)
    })

    it('应该能够分析性能问题', () => {
      // 设置一些问题指标
      service['metrics'].memoryUsage = 85 // 高内存使用
      service['metrics'].renderTime = 60 // 长渲染时间
      service['metrics'].fps = 25 // 低帧率
      
      const report = service.generateReport()
      
      expect(report.issues.length).toBeGreaterThan(0)
      expect(report.issues.some(issue => issue.type === 'memory')).toBe(true)
      expect(report.issues.some(issue => issue.type === 'render')).toBe(true)
    })

    it('应该为良好性能生成正面建议', () => {
      // 设置良好的性能指标
      service['metrics'].memoryUsage = 30
      service['metrics'].renderTime = 10
      service['metrics'].fps = 60
      service['metrics'].interactionDelay = 20
      service['metrics'].networkLatency = 100
      
      const report = service.generateReport()
      
      expect(report.issues.length).toBe(0)
      expect(report.recommendations).toContain('性能表现良好，继续保持')
    })
  })

  describe('性能问题分析', () => {
    it('应该正确识别内存问题', () => {
      service['metrics'].memoryUsage = 85
      
      const issues = service['analyzePerformanceIssues']()
      const memoryIssue = issues.find(issue => issue.type === 'memory')
      
      expect(memoryIssue).toBeDefined()
      expect(memoryIssue?.severity).toBe('high')
    })

    it('应该正确识别渲染问题', () => {
      service['metrics'].renderTime = 60
      
      const issues = service['analyzePerformanceIssues']()
      const renderIssue = issues.find(issue => issue.type === 'render')
      
      expect(renderIssue).toBeDefined()
      expect(renderIssue?.severity).toBe('high')
    })

    it('应该正确识别网络问题', () => {
      service['metrics'].networkLatency = 2500
      
      const issues = service['analyzePerformanceIssues']()
      const networkIssue = issues.find(issue => issue.type === 'network')
      
      expect(networkIssue).toBeDefined()
      expect(networkIssue?.severity).toBe('high')
    })

    it('应该正确识别交互问题', () => {
      service['metrics'].interactionDelay = 250
      
      const issues = service['analyzePerformanceIssues']()
      const interactionIssue = issues.find(issue => issue.type === 'interaction')
      
      expect(interactionIssue).toBeDefined()
      expect(interactionIssue?.severity).toBe('high')
    })
  })

  describe('数据导出', () => {
    it('应该能够导出性能数据', () => {
      const exportedData = service.exportPerformanceData()
      
      expect(typeof exportedData).toBe('string')
      
      const parsedData = JSON.parse(exportedData)
      expect(parsedData).toHaveProperty('timestamp')
      expect(parsedData).toHaveProperty('metrics')
      expect(parsedData).toHaveProperty('recommendations')
      expect(parsedData).toHaveProperty('issues')
    })
  })

  describe('配置管理', () => {
    it('应该能够设置性能阈值', () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      
      const thresholds = {
        renderTime: 20,
        memoryUsage: 70,
        fps: 45
      }
      
      service.setPerformanceThresholds(thresholds)
      
      expect(consoleSpy).toHaveBeenCalledWith('性能阈值已更新:', thresholds)
      
      consoleSpy.mockRestore()
    })
  })

  describe('边界情况', () => {
    it('应该处理重复启动监控', () => {
      service.startMonitoring()
      expect(service['isMonitoring']).toBe(true)
      
      // 重复启动应该不会有问题
      service.startMonitoring()
      expect(service['isMonitoring']).toBe(true)
    })

    it('应该处理重复停止监控', () => {
      service.startMonitoring()
      service.stopMonitoring()
      expect(service['isMonitoring']).toBe(false)
      
      // 重复停止应该不会有问题
      service.stopMonitoring()
      expect(service['isMonitoring']).toBe(false)
    })

    it('应该处理缺少性能API的情况', () => {
      // 临时移除PerformanceObserver
      const originalPO = global.PerformanceObserver
      delete (global as any).PerformanceObserver
      
      expect(() => {
        service.startMonitoring()
      }).not.toThrow()
      
      // 恢复PerformanceObserver
      global.PerformanceObserver = originalPO
    })
  })
})