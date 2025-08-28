import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { useProgressMonitoring } from '@/composables/useProgressMonitoring'
import type { AnomalyAlert } from '@/types/fontPackage'

describe('useProgressMonitoring', () => {
  let progressMonitoring: ReturnType<typeof useProgressMonitoring>
  
  beforeEach(() => {
    vi.useFakeTimers()
    progressMonitoring = useProgressMonitoring()
  })
  
  afterEach(() => {
    progressMonitoring.stopMonitoring()
    vi.useRealTimers()
  })
  
  describe('初始状态', () => {
    it('应该有正确的初始状态', () => {
      expect(progressMonitoring.isMonitoring.value).toBe(false)
      expect(progressMonitoring.currentPackageId.value).toBe(null)
      expect(progressMonitoring.overallProgress.percentage).toBe(0)
      expect(progressMonitoring.overallProgress.currentPhase.name).toBe('准备中')
      expect(progressMonitoring.anomalies.value).toEqual([])
      expect(progressMonitoring.healthStatus.status).toBe('healthy')
    })
  })
  
  describe('监控控制', () => {
    it('应该能够开始监控', () => {
      progressMonitoring.startMonitoring(1)
      
      expect(progressMonitoring.isMonitoring.value).toBe(true)
      expect(progressMonitoring.currentPackageId.value).toBe(1)
    })
    
    it('应该能够停止监控', () => {
      progressMonitoring.startMonitoring(1)
      progressMonitoring.stopMonitoring()
      
      expect(progressMonitoring.isMonitoring.value).toBe(false)
      expect(progressMonitoring.currentPackageId.value).toBe(null)
    })
    
    it('应该在开始新监控时停止之前的监控', () => {
      progressMonitoring.startMonitoring(1)
      expect(progressMonitoring.currentPackageId.value).toBe(1)
      
      progressMonitoring.startMonitoring(2)
      expect(progressMonitoring.currentPackageId.value).toBe(2)
    })
    
    it('应该在重复监控同一个包时不重新开始', () => {
      progressMonitoring.startMonitoring(1)
      const firstStart = progressMonitoring.isMonitoring.value
      
      progressMonitoring.startMonitoring(1)
      expect(progressMonitoring.isMonitoring.value).toBe(firstStart)
      expect(progressMonitoring.currentPackageId.value).toBe(1)
    })
  })
  
  describe('进度更新', () => {
    it('应该能够更新总体进度', () => {
      const updateData = {
        percentage: 50,
        currentCharacter: '测'
      }
      
      progressMonitoring.updateOverallProgress(updateData)
      
      expect(progressMonitoring.overallProgress.percentage).toBe(50)
      expect(progressMonitoring.overallProgress.currentCharacter).toBe('测')
    })
    
    it('应该能够更新字符进度', () => {
      progressMonitoring.updateCharacterProgress('测', {
        status: 'training',
        progress: 75,
        quality: 85
      })
      
      expect(progressMonitoring.characterProgress['测']).toEqual({
        status: 'training',
        progress: 75,
        quality: 85,
        issues: []
      })
    })
    
    it('应该能够更新性能指标', () => {
      const metrics = {
        trainingSpeed: 20,
        memoryUsage: 70,
        gpuUtilization: 90
      }
      
      progressMonitoring.updatePerformanceMetrics(metrics)
      
      expect(progressMonitoring.performanceMetrics.trainingSpeed).toBe(20)
      expect(progressMonitoring.performanceMetrics.memoryUsage).toBe(70)
      expect(progressMonitoring.performanceMetrics.gpuUtilization).toBe(90)
    })
  })
  
  describe('异常管理', () => {
    it('应该能够添加异常告警', () => {
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '训练速度低于预期',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      progressMonitoring.addAnomaly(anomaly)
      
      expect(progressMonitoring.anomalies.value).toContain(anomaly)
      expect(progressMonitoring.healthStatus.status).toBe('warning')
    })
    
    it('应该能够解决异常告警', () => {
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '训练速度低于预期',
        timestamp: '2024-01-01T12:00:00.000Z',
        resolved: false
      }
      
      progressMonitoring.addAnomaly(anomaly)
      const resolved = progressMonitoring.resolveAnomaly(anomaly.timestamp)
      
      expect(resolved).toBeTruthy()
      expect(resolved?.resolved).toBe(true)
      expect(progressMonitoring.anomalies.value).not.toContain(anomaly)
      expect(progressMonitoring.healthStatus.status).toBe('healthy')
    })
    
    it('应该根据异常严重程度更新健康状态', () => {
      // 添加高严重程度异常
      const highSeverityAnomaly: AnomalyAlert = {
        type: 'error',
        severity: 'high',
        message: '训练失败',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      progressMonitoring.addAnomaly(highSeverityAnomaly)
      expect(progressMonitoring.healthStatus.status).toBe('error')
      
      // 解决高严重程度异常，添加中等严重程度异常
      progressMonitoring.resolveAnomaly(highSeverityAnomaly.timestamp)
      
      const mediumSeverityAnomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '性能警告',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      progressMonitoring.addAnomaly(mediumSeverityAnomaly)
      expect(progressMonitoring.healthStatus.status).toBe('warning')
      
      // 解决所有异常
      progressMonitoring.resolveAnomaly(mediumSeverityAnomaly.timestamp)
      expect(progressMonitoring.healthStatus.status).toBe('healthy')
    })
  })
  
  describe('进度快照', () => {
    it('应该能够获取进度快照', () => {
      progressMonitoring.updateOverallProgress({ percentage: 50 })
      progressMonitoring.updatePerformanceMetrics({ trainingSpeed: 20 })
      
      const snapshot = progressMonitoring.getProgressSnapshot()
      
      expect(snapshot).toHaveProperty('timestamp')
      expect(snapshot).toHaveProperty('progress')
      expect(snapshot).toHaveProperty('metrics')
      expect(snapshot).toHaveProperty('health')
      expect(snapshot.progress.percentage).toBe(50)
      expect(snapshot.metrics.trainingSpeed).toBe(20)
    })
  })
  
  describe('数据重置', () => {
    it('应该能够重置所有进度数据', () => {
      // 设置一些数据
      progressMonitoring.updateOverallProgress({ percentage: 50 })
      progressMonitoring.updateCharacterProgress('测', { progress: 75 })
      progressMonitoring.updatePerformanceMetrics({ trainingSpeed: 20 })
      progressMonitoring.addAnomaly({
        type: 'performance',
        severity: 'medium',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      })
      
      // 重置数据
      progressMonitoring.resetProgressData()
      
      // 验证重置结果
      expect(progressMonitoring.overallProgress.percentage).toBe(0)
      expect(Object.keys(progressMonitoring.characterProgress)).toHaveLength(0)
      expect(progressMonitoring.performanceMetrics.trainingSpeed).toBe(0)
      expect(progressMonitoring.anomalies.value).toHaveLength(0)
      expect(progressMonitoring.healthStatus.status).toBe('healthy')
    })
  })
  
  describe('事件监听', () => {
    it('应该能够添加和移除进度监听器', () => {
      const listener = vi.fn()
      const removeListener = progressMonitoring.onProgressUpdate(listener)
      
      progressMonitoring.updateOverallProgress({ percentage: 50 })
      expect(listener).toHaveBeenCalledWith(progressMonitoring.overallProgress)
      
      removeListener()
      progressMonitoring.updateOverallProgress({ percentage: 75 })
      expect(listener).toHaveBeenCalledTimes(1)
    })
    
    it('应该能够添加和移除异常监听器', () => {
      const listener = vi.fn()
      const removeListener = progressMonitoring.onAnomalyDetected(listener)
      
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'medium',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      progressMonitoring.addAnomaly(anomaly)
      expect(listener).toHaveBeenCalledWith(anomaly)
      
      removeListener()
      progressMonitoring.addAnomaly({
        ...anomaly,
        timestamp: new Date().toISOString()
      })
      expect(listener).toHaveBeenCalledTimes(1)
    })
    
    it('应该能够添加和移除健康状态监听器', () => {
      const listener = vi.fn()
      const removeListener = progressMonitoring.onHealthStatusChange(listener)
      
      const anomaly: AnomalyAlert = {
        type: 'performance',
        severity: 'high',
        message: '测试异常',
        timestamp: new Date().toISOString(),
        resolved: false
      }
      
      progressMonitoring.addAnomaly(anomaly)
      expect(listener).toHaveBeenCalledWith(progressMonitoring.healthStatus)
      
      removeListener()
      progressMonitoring.resolveAnomaly(anomaly.timestamp)
      expect(listener).toHaveBeenCalledTimes(1)
    })
  })
  
  describe('模拟数据更新', () => {
    it('应该在开始监控时启动模拟数据更新', () => {
      progressMonitoring.startMonitoring(1)
      
      // 初始状态
      expect(progressMonitoring.overallProgress.charactersTotal).toBe(10)
      expect(Object.keys(progressMonitoring.characterProgress)).toHaveLength(10)
      
      // 推进时间，触发数据更新
      vi.advanceTimersByTime(2000)
      
      // 验证数据有更新
      expect(progressMonitoring.overallProgress.percentage).toBeGreaterThan(0)
    })
    
    it('应该在停止监控时停止模拟数据更新', () => {
      progressMonitoring.startMonitoring(1)
      const initialPercentage = progressMonitoring.overallProgress.percentage
      
      progressMonitoring.stopMonitoring()
      
      // 推进时间
      vi.advanceTimersByTime(5000)
      
      // 验证数据没有继续更新
      expect(progressMonitoring.overallProgress.percentage).toBe(initialPercentage)
    })
  })
  
  describe('错误处理', () => {
    it('应该处理监听器执行错误', () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      const faultyListener = vi.fn(() => {
        throw new Error('监听器错误')
      })
      
      progressMonitoring.onProgressUpdate(faultyListener)
      progressMonitoring.updateOverallProgress({ percentage: 50 })
      
      expect(consoleSpy).toHaveBeenCalledWith('进度监听器执行错误:', expect.any(Error))
      
      consoleSpy.mockRestore()
    })
  })
})