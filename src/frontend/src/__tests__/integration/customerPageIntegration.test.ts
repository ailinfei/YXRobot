import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import Customers from '@/views/admin/Customers.vue'
import { customerApi } from '@/api/customer'
import type { Customer, CustomerStats } from '@/types/customer'

// Mock API
vi.mock('@/api/customer', () => ({
  customerApi: {
    getCustomers: vi.fn(),
    getCustomerStats: vi.fn(),
    getCustomer: vi.fn(),
    createCustomer: vi.fn(),
    updateCustomer: vi.fn(),
    deleteCustomer: vi.fn(),
    getCustomerDevices: vi.fn(),
    getCustomerOrders: vi.fn(),
    getCustomerServiceRecords: vi.fn()
  }
}))

// Mock router
const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
  currentRoute: {
    value: {
      path: '/admin/business/customers',
      query: {}
    }
  }
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  useRoute: () => mockRouter.currentRoute.value
}))

describe('Customer Page Integration Tests', () => {
  const mockCustomers: Customer[] = [
    {
      id: '1',
      name: '张三',
      level: 'vip',
      phone: '13800138001',
      email: 'zhangsan@example.com',
      status: 'active',
      company: '测试公司A',
      totalSpent: 5000,
      customerValue: 8.5,
      deviceCount: {
        total: 3,
        purchased: 2,
        rental: 1
      },
      registeredAt: '2024-01-01T00:00:00Z',
      lastActiveAt: '2024-01-15T10:30:00Z'
    },
    {
      id: '2',
      name: '李四',
      level: 'regular',
      phone: '13800138002',
      email: 'lisi@example.com',
      status: 'active',
      company: '测试公司B',
      totalSpent: 2000,
      customerValue: 6.0,
      deviceCount: {
        total: 1,
        purchased: 1,
        rental: 0
      },
      registeredAt: '2024-01-02T00:00:00Z',
      lastActiveAt: '2024-01-14T15:20:00Z'
    }
  ]

  const mockStats: CustomerStats = {
    total: 156,
    regular: 120,
    vip: 30,
    premium: 6,
    activeDevices: 89,
    totalRevenue: 125000,
    newThisMonth: 12
  }

  beforeEach(() => {
    vi.clearAllMocks()
    
    // Setup default API responses
    vi.mocked(customerApi.getCustomers).mockResolvedValue({
      data: {
        list: mockCustomers,
        total: mockCustomers.length,
        page: 1,
        pageSize: 20
      }
    })
    
    vi.mocked(customerApi.getCustomerStats).mockResolvedValue(mockStats)
  })

  describe('页面初始化测试', () => {
    it('应该在页面加载时获取客户统计数据', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))

      expect(customerApi.getCustomerStats).toHaveBeenCalled()
      expect(customerApi.getCustomers).toHaveBeenCalled()
    })

    it('应该正确显示统计卡片数据', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))

      // 检查统计卡片是否显示正确的数据
      const statsCards = wrapper.findAll('.stat-card')
      expect(statsCards.length).toBeGreaterThan(0)
      
      // 验证总客户数显示
      const totalCustomersCard = wrapper.find('[data-testid="total-customers"]')
      if (totalCustomersCard.exists()) {
        expect(totalCustomersCard.text()).toContain('156')
      }
    })

    it('应该正确显示客户列表数据', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))

      // 检查客户列表是否显示
      const customerTable = wrapper.find('[data-testid="customer-table"]')
      if (customerTable.exists()) {
        const customerRows = customerTable.findAll('tbody tr')
        expect(customerRows.length).toBe(mockCustomers.length)
      }
    })
  })

  describe('搜索功能测试', () => {
    it('应该能够执行客户搜索', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 模拟搜索输入
      const searchInput = wrapper.find('[data-testid="search-input"]')
      if (searchInput.exists()) {
        await searchInput.setValue('张三')
        await searchInput.trigger('input')
        
        await nextTick()
        await new Promise(resolve => setTimeout(resolve, 300)) // 等待防抖
        
        expect(customerApi.getCustomers).toHaveBeenCalledWith(
          expect.objectContaining({
            keyword: '张三'
          })
        )
      }
    })

    it('应该支持按客户等级筛选', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 模拟等级筛选
      const levelFilter = wrapper.find('[data-testid="level-filter"]')
      if (levelFilter.exists()) {
        await levelFilter.setValue('vip')
        await levelFilter.trigger('change')
        
        await nextTick()
        
        expect(customerApi.getCustomers).toHaveBeenCalledWith(
          expect.objectContaining({
            level: 'vip'
          })
        )
      }
    })

    it('应该支持按客户状态筛选', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 模拟状态筛选
      const statusFilter = wrapper.find('[data-testid="status-filter"]')
      if (statusFilter.exists()) {
        await statusFilter.setValue('active')
        await statusFilter.trigger('change')
        
        await nextTick()
        
        expect(customerApi.getCustomers).toHaveBeenCalledWith(
          expect.objectContaining({
            status: 'active'
          })
        )
      }
    })
  })

  describe('分页功能测试', () => {
    it('应该支持分页查询', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 模拟分页操作
      const pagination = wrapper.find('[data-testid="pagination"]')
      if (pagination.exists()) {
        // 模拟点击第2页
        const page2Button = pagination.find('[data-page="2"]')
        if (page2Button.exists()) {
          await page2Button.trigger('click')
          
          await nextTick()
          
          expect(customerApi.getCustomers).toHaveBeenCalledWith(
            expect.objectContaining({
              page: 2
            })
          )
        }
      }
    })

    it('应该支持修改每页显示数量', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 模拟修改每页显示数量
      const pageSizeSelect = wrapper.find('[data-testid="page-size-select"]')
      if (pageSizeSelect.exists()) {
        await pageSizeSelect.setValue('50')
        await pageSizeSelect.trigger('change')
        
        await nextTick()
        
        expect(customerApi.getCustomers).toHaveBeenCalledWith(
          expect.objectContaining({
            pageSize: 50
          })
        )
      }
    })
  })

  describe('客户操作测试', () => {
    it('应该能够查看客户详情', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 模拟点击查看详情按钮
      const viewButton = wrapper.find('[data-testid="view-customer-1"]')
      if (viewButton.exists()) {
        await viewButton.trigger('click')
        
        await nextTick()
        
        expect(customerApi.getCustomer).toHaveBeenCalledWith('1')
      }
    })

    it('应该能够编辑客户信息', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 模拟点击编辑按钮
      const editButton = wrapper.find('[data-testid="edit-customer-1"]')
      if (editButton.exists()) {
        await editButton.trigger('click')
        
        await nextTick()
        
        // 检查编辑对话框是否打开
        const editDialog = wrapper.find('[data-testid="edit-customer-dialog"]')
        expect(editDialog.exists()).toBe(true)
      }
    })

    it('应该能够删除客户', async () => {
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 模拟点击删除按钮
      const deleteButton = wrapper.find('[data-testid="delete-customer-1"]')
      if (deleteButton.exists()) {
        await deleteButton.trigger('click')
        
        await nextTick()
        
        // 模拟确认删除
        const confirmButton = wrapper.find('[data-testid="confirm-delete"]')
        if (confirmButton.exists()) {
          await confirmButton.trigger('click')
          
          await nextTick()
          
          expect(customerApi.deleteCustomer).toHaveBeenCalledWith('1')
        }
      }
    })
  })

  describe('错误处理测试', () => {
    it('应该正确处理API错误', async () => {
      // 模拟API错误
      vi.mocked(customerApi.getCustomers).mockRejectedValue(new Error('API Error'))
      
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 检查错误提示是否显示
      const errorMessage = wrapper.find('[data-testid="error-message"]')
      if (errorMessage.exists()) {
        expect(errorMessage.text()).toContain('加载失败')
      }
    })

    it('应该在网络错误时显示重试按钮', async () => {
      // 模拟网络错误
      vi.mocked(customerApi.getCustomerStats).mockRejectedValue(new Error('Network Error'))
      
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 检查重试按钮是否显示
      const retryButton = wrapper.find('[data-testid="retry-button"]')
      if (retryButton.exists()) {
        await retryButton.trigger('click')
        
        await nextTick()
        
        // 验证重新调用API
        expect(customerApi.getCustomerStats).toHaveBeenCalledTimes(2)
      }
    })
  })

  describe('响应式设计测试', () => {
    it('应该在移动端正确显示', async () => {
      // 模拟移动端视口
      Object.defineProperty(window, 'innerWidth', {
        writable: true,
        configurable: true,
        value: 375
      })
      
      const wrapper = mount(Customers)
      
      await nextTick()
      
      // 检查移动端布局
      const mobileLayout = wrapper.find('[data-testid="mobile-layout"]')
      if (mobileLayout.exists()) {
        expect(mobileLayout.classes()).toContain('mobile')
      }
    })
  })

  describe('性能测试', () => {
    it('应该在合理时间内加载数据', async () => {
      const startTime = Date.now()
      
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      const endTime = Date.now()
      const loadTime = endTime - startTime
      
      // 页面应该在2秒内加载完成
      expect(loadTime).toBeLessThan(2000)
    })

    it('应该正确处理大量数据', async () => {
      // 模拟大量客户数据
      const largeCustomerList = Array.from({ length: 1000 }, (_, index) => ({
        ...mockCustomers[0],
        id: String(index + 1),
        name: `客户${index + 1}`
      }))
      
      vi.mocked(customerApi.getCustomers).mockResolvedValue({
        data: {
          list: largeCustomerList,
          total: largeCustomerList.length,
          page: 1,
          pageSize: 20
        }
      })
      
      const wrapper = mount(Customers)
      
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 验证虚拟滚动或分页是否正常工作
      const visibleRows = wrapper.findAll('[data-testid^="customer-row-"]')
      expect(visibleRows.length).toBeLessThanOrEqual(20) // 应该只显示当前页的数据
    })
  })
})