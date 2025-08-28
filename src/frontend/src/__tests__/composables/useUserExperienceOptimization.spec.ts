import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { defineComponent } from 'vue'
import { useUserExperienceOptimization } from '@/composables/useUserExperienceOptimization'
import { ElNotification, ElMessage } from 'element-plus'

// Mock Element Plus
vi.mock('element-plus', () => ({
  ElNotification: vi.fn(),
  ElMessage: {
    info: vi.fn(),
    success: vi.fn(),
    warning: vi.fn(),
    error: vi.fn()
  }
}))

// Mock performance API
global.performance = {
  ...global.performance,
  now: vi.fn(() => Date.now()),
  memory: {
    usedJSHeapSize: 1000000,
    jsHeapSizeLimit: 10000000
  }
} as any

describe('useUserExperienceOptimization', () => {
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

  const TestComponent = defineComponent({
    setup() {
      return useUserExperienceOptimization()
    },
    template: '<div>Test Component</div>'
  })

  describe('加载状态管理', () => {
    it('应该正确管理加载状态', () => {
      wrapper = mount(TestComponent)
      
      expect(wrapper.vm.isLoading).toBe(false)
      expect(wrapper.vm.loadingText).toBe('')
      
      wrapper.vm.startLoading('测试加载')
      expect(wrapper.vm.isLoading).toBe(true)
      expect(wrapper.vm.loadingText).toBe('测试加载')
      
      wrapper.vm.stopLoading()
      expect(wrapper.vm.isLoading).toBe(false)
      expect(wrapper.vm.loadingText).toBe('')
    })
  })

  describe('错误处理', () => {
    it('应该显示错误通知', () => {
      wrapper = mount(TestComponent)
      
      wrapper.vm.showError('测试错误', '详细信息')
      
      expect(wrapper.vm.errorMessages).toContain('测试错误')
      expect(wrapper.vm.hasErrors).toBe(true)
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          title: '操作失败',
          message: '测试错误',
          type: 'error'
        })
      )
    })

    it('应该显示成功通知', () => {
      wrapper = mount(TestComponent)
      
      wrapper.vm.showSuccess('操作成功', '详细描述')
      
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          title: '操作成功',
          message: '操作成功',
          type: 'success'
        })
      )
    })

    it('应该显示警告通知', () => {
      wrapper = mount(TestComponent)
      const mockAction = vi.fn()
      
      wrapper.vm.showWarning('警告信息', mockAction)
      
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          title: '注意',
          message: '警告信息',
          type: 'warning',
          onClick: mockAction
        })
      )
    })

    it('应该能够清除错误', () => {
      wrapper = mount(TestComponent)
      
      wrapper.vm.showError('错误1')
      wrapper.vm.showError('错误2')
      expect(wrapper.vm.errorMessages.length).toBe(2)
      
      wrapper.vm.clearErrors()
      expect(wrapper.vm.errorMessages.length).toBe(0)
      expect(wrapper.vm.hasErrors).toBe(false)
    })
  })

  describe('快捷键管理', () => {
    it('应该能够注册和注销快捷键', () => {
      wrapper = mount(TestComponent)
      const mockCallback = vi.fn()
      
      wrapper.vm.registerShortcut('ctrl+s', mockCallback, '保存')
      expect(wrapper.vm.shortcuts['ctrl+s']).toBe(mockCallback)
      
      wrapper.vm.unregisterShortcut('ctrl+s')
      expect(wrapper.vm.shortcuts['ctrl+s']).toBeUndefined()
    })

    it('应该正确处理键盘事件', () => {
      wrapper = mount(TestComponent)
      const mockCallback = vi.fn()
      
      wrapper.vm.registerShortcut('ctrl+s', mockCallback)
      
      // 模拟键盘事件
      const event = new KeyboardEvent('keydown', {
        key: 's',
        ctrlKey: true
      })
      
      document.dispatchEvent(event)
      
      expect(mockCallback).toHaveBeenCalled()
    })
  })

  describe('性能监控', () => {
    it('应该测量操作性能', async () => {
      wrapper = mount(TestComponent)
      
      const mockOperation = vi.fn().mockResolvedValue('result')
      const measuredFn = wrapper.vm.measurePerformance('test', mockOperation)
      
      await measuredFn()
      
      expect(mockOperation).toHaveBeenCalled()
      expect(wrapper.vm.performanceMetrics.renderTime).toBeGreaterThanOrEqual(0)
    })

    it('应该在操作耗时过长时显示警告', async () => {
      wrapper = mount(TestComponent)
      
      const slowOperation = vi.fn().mockImplementation(() => {
        return new Promise(resolve => setTimeout(resolve, 1500))
      })
      
      const measuredFn = wrapper.vm.measurePerformance('slow-test', slowOperation)
      
      await measuredFn()
      vi.advanceTimersByTime(1500)
      
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          type: 'warning',
          message: expect.stringContaining('操作耗时较长')
        })
      )
    })
  })

  describe('工具函数', () => {
    it('防抖函数应该正确工作', () => {
      wrapper = mount(TestComponent)
      const mockFn = vi.fn()
      const debouncedFn = wrapper.vm.debounce(mockFn, 100)
      
      // 快速调用多次
      debouncedFn()
      debouncedFn()
      debouncedFn()
      
      expect(mockFn).not.toHaveBeenCalled()
      
      // 等待防抖时间
      vi.advanceTimersByTime(100)
      
      expect(mockFn).toHaveBeenCalledTimes(1)
    })

    it('节流函数应该正确工作', () => {
      wrapper = mount(TestComponent)
      const mockFn = vi.fn()
      const throttledFn = wrapper.vm.throttle(mockFn, 100)
      
      // 快速调用多次
      throttledFn()
      throttledFn()
      throttledFn()
      
      expect(mockFn).toHaveBeenCalledTimes(1)
      
      // 等待节流时间
      vi.advanceTimersByTime(100)
      
      throttledFn()
      expect(mockFn).toHaveBeenCalledTimes(2)
    })

    it('重试机制应该正确工作', async () => {
      wrapper = mount(TestComponent)
      let attempts = 0
      
      const failingOperation = vi.fn().mockImplementation(() => {
        attempts++
        if (attempts < 3) {
          throw new Error('操作失败')
        }
        return 'success'
      })
      
      const result = await wrapper.vm.retryOperation(failingOperation, 3, 100)
      
      expect(result).toBe('success')
      expect(failingOperation).toHaveBeenCalledTimes(3)
    })

    it('重试机制应该在达到最大次数后抛出错误', async () => {
      wrapper = mount(TestComponent)
      
      const alwaysFailingOperation = vi.fn().mockRejectedValue(new Error('始终失败'))
      
      await expect(
        wrapper.vm.retryOperation(alwaysFailingOperation, 2, 100)
      ).rejects.toThrow('始终失败')
      
      expect(alwaysFailingOperation).toHaveBeenCalledTimes(2)
    })
  })

  describe('用户交互', () => {
    it('应该显示提示信息', () => {
      wrapper = mount(TestComponent)
      
      wrapper.vm.showTip('这是一个提示', 'target-element')
      
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          title: '提示',
          message: '这是一个提示',
          type: 'info',
          customClass: 'tip-for-target-element'
        })
      )
    })

    it('应该处理操作确认', async () => {
      wrapper = mount(TestComponent)
      
      // 模拟用户点击确认
      const mockNotification = vi.mocked(ElNotification)
      mockNotification.mockImplementation((options: any) => {
        // 模拟点击确认
        setTimeout(() => options.onClick(), 0)
        return {} as any
      })
      
      const result = await wrapper.vm.confirmAction('确认删除吗？')
      
      expect(result).toBe(true)
      expect(ElNotification).toHaveBeenCalledWith(
        expect.objectContaining({
          title: '确认操作',
          message: expect.stringContaining('确认删除吗？')
        })
      )
    })
  })

  describe('响应式状态', () => {
    it('应该正确计算响应式状态', () => {
      wrapper = mount(TestComponent)
      
      // 测试hasErrors计算属性
      expect(wrapper.vm.hasErrors).toBe(false)
      
      wrapper.vm.showError('测试错误')
      expect(wrapper.vm.hasErrors).toBe(true)
      
      // 测试isResponsive计算属性
      wrapper.vm.performanceMetrics.interactionDelay = 50
      expect(wrapper.vm.isResponsive).toBe(true)
      
      wrapper.vm.performanceMetrics.interactionDelay = 150
      expect(wrapper.vm.isResponsive).toBe(false)
    })
  })
})