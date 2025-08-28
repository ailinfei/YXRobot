import { describe, it, expect, beforeEach, vi } from 'vitest'
import { fontPackageProgressService } from '@/services/fontPackageProgressService'
import { smartSampleAnalysis } from '@/services/smartSampleAnalysis'

// Mock WebSocket
const mockWebSocket = {
  send: vi.fn(),
  close: vi.fn(),
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
  readyState: WebSocket.OPEN
}

global.WebSocket = vi.fn(() => mockWebSocket) as any

describe('集成测试服务', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('服务间数据流转', () => {
    it('应该正确处理样本分析到进度监控的数据流', async () => {
      // 1. 模拟样本分析
      const mockFiles = [
        { name: '一.jpg', content: 'mock-content-1' },
        { name: '二.png', content: 'mock-content-2' },
        { name: '三.gif', content: 'mock-content-3' }
      ]

      const analysisResult = await smartSampleAnalysis.analyzeFiles(mockFiles as any)

      expect(analysisResult).toMatchObject({
        recognizedCharacters: expect.any(Object),
        duplicates: expect.any(Array),
        qualityReport: expect.any(Object)
      })

      // 2. 使用分析结果启动进度监控
      const packageId = 1
      const progressData = await fontPackageProgressService.startMonitoring(packageId)

      expect(progressData).toMatchObject({
        packageId,
        isActive: true,
        startTime: expect.any(String)
      })

      // 3. 验证数据一致性
      const snapshot = fontPackageProgressService.getProgressSnapshot(packageId)
      expect(snapshot).toBeDefined()
      expect(snapshot?.packageId).toBe(packageId)
    })

    it('应该正确处理错误传播', async () => {
      // 模拟分析失败
      const invalidFiles = [
        { name: 'invalid.txt', content: 'not-an-image' }
      ]

      await expect(
        smartSampleAnalysis.analyzeFiles(invalidFiles as any)
      ).rejects.toThrow()

      // 验证错误不会影响其他服务
      const packageId = 2
      const progressData = await fontPackageProgressService.startMonitoring(packageId)
      expect(progressData.isActive).toBe(true)
    })
  })

  describe('状态同步验证', () => {
    it('应该在多个监控实例间保持状态同步', async () => {
      const packageId1 = 1
      const packageId2 = 2

      // 启动多个监控
      await fontPackageProgressService.startMonitoring(packageId1)
      await fontPackageProgressService.startMonitoring(packageId2)

      // 验证状态独立性
      const snapshot1 = fontPackageProgressService.getProgressSnapshot(packageId1)
      const snapshot2 = fontPackageProgressService.getProgressSnapshot(packageId2)

      expect(snapshot1?.packageId).toBe(packageId1)
      expect(snapshot2?.packageId).toBe(packageId2)
      expect(snapshot1?.packageId).not.toBe(snapshot2?.packageId)

      // 停止一个监控，验证另一个不受影响
      await fontPackageProgressService.stopMonitoring(packageId1)

      const snapshot1After = fontPackageProgressService.getProgressSnapshot(packageId1)
      const snapshot2After = fontPackageProgressService.getProgressSnapshot(packageId2)

      expect(snapshot1After).toBeNull()
      expect(snapshot2After).toBeDefined()
    })

    it('应该正确处理并发操作', async () => {
      const packageId = 3

      // 并发启动和停止
      const operations = [
        fontPackageProgressService.startMonitoring(packageId),
        fontPackageProgressService.stopMonitoring(packageId),
        fontPackageProgressService.startMonitoring(packageId)
      ]

      await Promise.allSettled(operations)

      // 验证最终状态
      const finalSnapshot = fontPackageProgressService.getProgressSnapshot(packageId)
      expect(finalSnapshot).toBeDefined()
    })
  })

  describe('内存和资源管理', () => {
    it('应该正确清理资源', async () => {
      const packageIds = [1, 2, 3, 4, 5]

      // 启动多个监控
      for (const id of packageIds) {
        await fontPackageProgressService.startMonitoring(id)
      }

      // 验证所有监控都在运行
      for (const id of packageIds) {
        const snapshot = fontPackageProgressService.getProgressSnapshot(id)
        expect(snapshot).toBeDefined()
      }

      // 清理所有资源
      await fontPackageProgressService.cleanup()

      // 验证资源已清理
      for (const id of packageIds) {
        const snapshot = fontPackageProgressService.getProgressSnapshot(id)
        expect(snapshot).toBeNull()
      }
    })

    it('应该限制并发监控数量', async () => {
      const maxConcurrent = 10
      const packageIds = Array.from({ length: 15 }, (_, i) => i + 1)

      // 尝试启动超过限制的监控
      const results = await Promise.allSettled(
        packageIds.map(id => fontPackageProgressService.startMonitoring(id))
      )

      // 验证只有限制数量的监控成功启动
      const successCount = results.filter(r => r.status === 'fulfilled').length
      expect(successCount).toBeLessThanOrEqual(maxConcurrent)
    })
  })
})