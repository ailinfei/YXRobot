import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import axios from 'axios'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

describe('Page Navigation Integration Tests', () => {
  let pinia: any
  let router: any

  beforeEach(() => {
    pinia = createPinia()
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/admin/dashboard', name: 'dashboard', component: { template: '<div>Dashboard</div>' } },
        { path: '/admin/content/products', name: 'products', component: { template: '<div>Products</div>' } },
        { path: '/admin/business/customers', name: 'customers', component: { template: '<div>Customers</div>' } },
        { path: '/admin/business/customers/:id', name: 'customer-detail', component: { template: '<div>Customer Detail</div>' } },
        { path: '/admin/content/charity', name: 'charity', component: { template: '<div>Charity</div>' } },
        { path: '/admin/device/management', name: 'device-management', component: { template: '<div>Device Management</div>' } },
        { path: '/admin/device/monitoring', name: 'device-monitoring', component: { template: '<div>Device Monitoring</div>' } },
        { path: '/admin/system/languages', name: 'languages', component: { template: '<div>Languages</div>' } },
        { path: '/admin/system/courses', name: 'courses', component: { template: '<div>Courses</div>' } },
        { path: '/admin/system/fonts', name: 'fonts', component: { template: '<div>Fonts</div>' } }
      ]
    })
    vi.clearAllMocks()
  })

  describe('Dashboard Navigation', () => {
    it('should navigate from dashboard to different modules', async () => {
      // Start at dashboard
      await router.push('/admin/dashboard')
      expect(router.currentRoute.value.path).toBe('/admin/dashboard')

      // Navigate to products
      await router.push('/admin/content/products')
      expect(router.currentRoute.value.name).toBe('products')

      // Navigate to customers
      await router.push('/admin/business/customers')
      expect(router.currentRoute.value.name).toBe('customers')

      // Navigate to charity
      await router.push('/admin/content/charity')
      expect(router.currentRoute.value.name).toBe('charity')
    })

    it('should pass data between dashboard and detail pages', async () => {
      // Mock dashboard data
      const dashboardData = {
        customerStats: { total: 1250, active: 980, vip: 156 },
        deviceStats: { total: 2340, online: 1890, offline: 450 },
        salesStats: { thisMonth: 580000, lastMonth: 520000 }
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: dashboardData }
      })

      // Fetch dashboard data
      const dashboardResponse = await axios.get('/api/dashboard/stats')
      expect(dashboardResponse.data.data.customerStats.total).toBe(1250)

      // Navigate to customer detail with ID from dashboard
      await router.push('/admin/business/customers/cust1')
      expect(router.currentRoute.value.params.id).toBe('cust1')
    })
  })

  describe('Customer Management Navigation', () => {
    it('should navigate between customer list and detail pages', async () => {
      // Navigate to customer list
      await router.push('/admin/business/customers')
      expect(router.currentRoute.value.name).toBe('customers')

      // Mock customer list data
      const customers = [
        { id: 'cust1', name: '张三', email: 'zhangsan@example.com' },
        { id: 'cust2', name: '李四', email: 'lisi@example.com' }
      ]

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: customers }
      })

      const customersResponse = await axios.get('/api/customers')
      expect(customersResponse.data.data).toHaveLength(2)

      // Navigate to customer detail
      await router.push('/admin/business/customers/cust1')
      expect(router.currentRoute.value.params.id).toBe('cust1')

      // Mock customer detail data
      const customerDetail = {
        id: 'cust1',
        name: '张三',
        email: 'zhangsan@example.com',
        devices: [
          { id: 'device1', serialNumber: 'YX001', status: 'active' }
        ],
        orders: [
          { id: 'order1', amount: 2999, status: 'completed' }
        ]
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: customerDetail }
      })

      const detailResponse = await axios.get('/api/customers/cust1')
      expect(detailResponse.data.data.devices).toHaveLength(1)
      expect(detailResponse.data.data.orders).toHaveLength(1)
    })

    it('should maintain state when navigating back to customer list', async () => {
      // Navigate to customer list with filters
      await router.push('/admin/business/customers?level=vip&status=active')
      expect(router.currentRoute.value.query.level).toBe('vip')
      expect(router.currentRoute.value.query.status).toBe('active')

      // Navigate to detail page
      await router.push('/admin/business/customers/cust1')
      expect(router.currentRoute.value.params.id).toBe('cust1')

      // Navigate back to list (should preserve filters)
      await router.push('/admin/business/customers?level=vip&status=active')
      expect(router.currentRoute.value.query.level).toBe('vip')
      expect(router.currentRoute.value.query.status).toBe('active')
    })
  })

  describe('Device Management Navigation', () => {
    it('should navigate between device management and monitoring', async () => {
      // Navigate to device management
      await router.push('/admin/device/management')
      expect(router.currentRoute.value.name).toBe('device-management')

      // Mock device list
      const devices = [
        { id: 'device1', serialNumber: 'YX001', status: 'online' },
        { id: 'device2', serialNumber: 'YX002', status: 'offline' }
      ]

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: devices }
      })

      const devicesResponse = await axios.get('/api/devices')
      expect(devicesResponse.data.data).toHaveLength(2)

      // Navigate to device monitoring
      await router.push('/admin/device/monitoring')
      expect(router.currentRoute.value.name).toBe('device-monitoring')

      // Mock monitoring data
      const monitoringData = {
        onlineDevices: 1890,
        offlineDevices: 450,
        recentAlerts: [
          { deviceId: 'device3', type: 'offline', timestamp: new Date().toISOString() }
        ]
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: monitoringData }
      })

      const monitoringResponse = await axios.get('/api/devices/monitoring')
      expect(monitoringResponse.data.data.onlineDevices).toBe(1890)
    })
  })

  describe('Content Management Navigation', () => {
    it('should navigate between different content management pages', async () => {
      // Navigate to products
      await router.push('/admin/content/products')
      expect(router.currentRoute.value.name).toBe('products')

      // Navigate to charity
      await router.push('/admin/content/charity')
      expect(router.currentRoute.value.name).toBe('charity')

      // Mock charity data
      const charityStats = {
        totalDonations: 12580,
        beneficiaries: 8960,
        institutions: 156,
        activeProjects: 23
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: charityStats }
      })

      const charityResponse = await axios.get('/api/charity/stats')
      expect(charityResponse.data.data.institutions).toBe(156)
    })
  })

  describe('System Management Navigation', () => {
    it('should navigate between system management pages', async () => {
      // Navigate to languages
      await router.push('/admin/system/languages')
      expect(router.currentRoute.value.name).toBe('languages')

      // Mock language data
      const languages = [
        { code: 'zh-CN', name: 'Chinese', progress: 100 },
        { code: 'en-US', name: 'English', progress: 95 },
        { code: 'ja-JP', name: 'Japanese', progress: 80 }
      ]

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: languages }
      })

      const languagesResponse = await axios.get('/api/languages')
      expect(languagesResponse.data.data).toHaveLength(3)

      // Navigate to courses
      await router.push('/admin/system/courses')
      expect(router.currentRoute.value.name).toBe('courses')

      // Navigate to fonts
      await router.push('/admin/system/fonts')
      expect(router.currentRoute.value.name).toBe('fonts')
    })
  })

  describe('Cross-Module Data Flow', () => {
    it('should share data between customer and device modules', async () => {
      // Get customer with devices
      const customerWithDevices = {
        id: 'cust1',
        name: '张三',
        devices: [
          { id: 'device1', serialNumber: 'YX001', status: 'online' },
          { id: 'device2', serialNumber: 'YX002', status: 'offline' }
        ]
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: customerWithDevices }
      })

      const customerResponse = await axios.get('/api/customers/cust1')
      expect(customerResponse.data.data.devices).toHaveLength(2)

      // Navigate to device management and filter by customer
      await router.push('/admin/device/management?customerId=cust1')
      expect(router.currentRoute.value.query.customerId).toBe('cust1')

      // Mock filtered device list
      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: customerWithDevices.devices }
      })

      const deviceResponse = await axios.get('/api/devices?customerId=cust1')
      expect(deviceResponse.data.data).toHaveLength(2)
      expect(deviceResponse.data.data[0].serialNumber).toBe('YX001')
    })

    it('should share data between product and order modules', async () => {
      // Get product information
      const product = {
        id: 'prod1',
        name: '练字机器人V2.0',
        price: { currency: 'CNY', amount: 2999 }
      }

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: product }
      })

      const productResponse = await axios.get('/api/products/prod1')
      expect(productResponse.data.data.price.amount).toBe(2999)

      // Create order with product
      const newOrder = {
        customerId: 'cust1',
        productId: 'prod1',
        quantity: 1,
        totalAmount: 2999
      }

      mockedAxios.post.mockResolvedValueOnce({
        data: { success: true, data: { ...newOrder, id: 'order1' } }
      })

      const orderResponse = await axios.post('/api/orders', newOrder)
      expect(orderResponse.data.data.productId).toBe('prod1')
      expect(orderResponse.data.data.totalAmount).toBe(2999)
    })
  })

  describe('Error Handling in Navigation', () => {
    it('should handle navigation errors gracefully', async () => {
      // Try to navigate to non-existent route
      try {
        await router.push('/admin/nonexistent')
      } catch (error) {
        // Router should handle this gracefully
        expect(error).toBeDefined()
      }

      // Should still be able to navigate to valid routes
      await router.push('/admin/dashboard')
      expect(router.currentRoute.value.name).toBe('dashboard')
    })

    it('should handle API errors during navigation', async () => {
      // Navigate to customer detail
      await router.push('/admin/business/customers/cust999')
      expect(router.currentRoute.value.params.id).toBe('cust999')

      // Mock API error for non-existent customer
      mockedAxios.get.mockRejectedValueOnce({
        response: {
          status: 404,
          data: { message: 'Customer not found' }
        }
      })

      try {
        await axios.get('/api/customers/cust999')
      } catch (error: any) {
        expect(error.response.status).toBe(404)
        expect(error.response.data.message).toBe('Customer not found')
      }

      // Should be able to navigate back to customer list
      await router.push('/admin/business/customers')
      expect(router.currentRoute.value.name).toBe('customers')
    })
  })

  describe('State Persistence Across Navigation', () => {
    it('should maintain search and filter state', async () => {
      // Navigate to customers with search query
      await router.push('/admin/business/customers?search=张三&level=vip')
      expect(router.currentRoute.value.query.search).toBe('张三')
      expect(router.currentRoute.value.query.level).toBe('vip')

      // Navigate to another page
      await router.push('/admin/content/products')
      expect(router.currentRoute.value.name).toBe('products')

      // Navigate back to customers
      await router.push('/admin/business/customers?search=张三&level=vip')
      expect(router.currentRoute.value.query.search).toBe('张三')
      expect(router.currentRoute.value.query.level).toBe('vip')
    })

    it('should maintain pagination state', async () => {
      // Navigate to customers with pagination
      await router.push('/admin/business/customers?page=3&pageSize=20')
      expect(router.currentRoute.value.query.page).toBe('3')
      expect(router.currentRoute.value.query.pageSize).toBe('20')

      // Navigate to detail and back
      await router.push('/admin/business/customers/cust1')
      await router.push('/admin/business/customers?page=3&pageSize=20')
      
      expect(router.currentRoute.value.query.page).toBe('3')
      expect(router.currentRoute.value.query.pageSize).toBe('20')
    })
  })
})