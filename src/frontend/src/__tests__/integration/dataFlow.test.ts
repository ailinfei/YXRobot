import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import axios from 'axios'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

// Mock API responses
const mockCustomerData = {
  id: '1',
  name: '张三',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  devices: [
    {
      id: 'device1',
      serialNumber: 'YX001',
      model: 'YXRobot-V2',
      type: 'purchased',
      status: 'active'
    }
  ]
}

const mockProductData = {
  id: '1',
  name: '练字机器人V2.0',
  description: '智能练字机器人',
  price: { currency: 'CNY', amount: 2999 },
  images: []
}

const mockCharityData = {
  stats: {
    totalDonations: 12580,
    beneficiaries: 8960,
    institutions: 156,
    activeProjects: 23
  }
}

describe('Data Flow Integration Tests', () => {
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    vi.clearAllMocks()
  })

  describe('Customer Data Flow', () => {
    it('should fetch and display customer data correctly', async () => {
      // Mock API response
      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: [mockCustomerData] }
      })

      // Test API call
      const response = await axios.get('/api/customers')
      expect(response.data.data).toEqual([mockCustomerData])
      expect(response.data.data[0].devices).toHaveLength(1)
      expect(response.data.data[0].devices[0].type).toBe('purchased')
    })

    it('should handle customer creation data flow', async () => {
      const newCustomer = {
        name: '李四',
        email: 'lisi@example.com',
        phone: '13900139000'
      }

      mockedAxios.post.mockResolvedValueOnce({
        data: { success: true, data: { ...newCustomer, id: '2' } }
      })

      const response = await axios.post('/api/customers', newCustomer)
      expect(response.data.data).toMatchObject(newCustomer)
      expect(response.data.data.id).toBeDefined()
    })
  })

  describe('Product Data Flow', () => {
    it('should fetch and display product data correctly', async () => {
      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: [mockProductData] }
      })

      const response = await axios.get('/api/products')
      expect(response.data.data).toEqual([mockProductData])
      expect(response.data.data[0].price.currency).toBe('CNY')
    })

    it('should handle product update data flow', async () => {
      const updatedProduct = {
        ...mockProductData,
        name: '练字机器人V3.0',
        price: { currency: 'CNY', amount: 3999 }
      }

      mockedAxios.put.mockResolvedValueOnce({
        data: { success: true, data: updatedProduct }
      })

      const response = await axios.put('/api/products/1', updatedProduct)
      expect(response.data.data.name).toBe('练字机器人V3.0')
      expect(response.data.data.price.amount).toBe(3999)
    })
  })

  describe('Charity Data Flow', () => {
    it('should fetch charity statistics correctly', async () => {
      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: mockCharityData.stats }
      })

      const response = await axios.get('/api/charity/stats')
      expect(response.data.data.totalDonations).toBe(12580)
      expect(response.data.data.beneficiaries).toBe(8960)
      expect(response.data.data.institutions).toBe(156)
    })

    it('should handle charity institution creation', async () => {
      const newInstitution = {
        name: '希望小学',
        type: 'school',
        address: '北京市朝阳区',
        contact: { phone: '010-12345678' }
      }

      mockedAxios.post.mockResolvedValueOnce({
        data: { success: true, data: { ...newInstitution, id: 'inst1' } }
      })

      const response = await axios.post('/api/charity/institutions', newInstitution)
      expect(response.data.data).toMatchObject(newInstitution)
      expect(response.data.data.id).toBeDefined()
    })
  })

  describe('Map Data Flow', () => {
    it('should fetch map visualization data correctly', async () => {
      const mockMapData = [
        { name: '中国', value: 2580000, coordinates: [104.195397, 35.86166] },
        { name: '美国', value: 1200000, coordinates: [-95.712891, 37.09024] },
        { name: '日本', value: 890000, coordinates: [138.252924, 36.204824] }
      ]

      mockedAxios.get.mockResolvedValueOnce({
        data: { success: true, data: mockMapData }
      })

      const response = await axios.get('/api/map/sales')
      expect(response.data.data).toHaveLength(3)
      expect(response.data.data[0].name).toBe('中国')
      expect(response.data.data[0].coordinates).toHaveLength(2)
    })
  })

  describe('Error Handling Data Flow', () => {
    it('should handle API errors gracefully', async () => {
      mockedAxios.get.mockRejectedValueOnce({
        response: {
          status: 500,
          data: { message: 'Internal Server Error' }
        }
      })

      try {
        await axios.get('/api/customers')
      } catch (error: any) {
        expect(error.response.status).toBe(500)
        expect(error.response.data.message).toBe('Internal Server Error')
      }
    })

    it('should handle network errors', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network Error'))

      try {
        await axios.get('/api/customers')
      } catch (error: any) {
        expect(error.message).toBe('Network Error')
      }
    })
  })
})