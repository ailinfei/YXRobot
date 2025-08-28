import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import { ElMessage } from 'element-plus'

// Import router configuration
import router from '@/router'

// Mock Element Plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  },
  ElLoading: {
    service: vi.fn(() => ({
      close: vi.fn()
    }))
  }
}))

describe('Navigation Integration Tests', () => {
  let pinia: any
  let testRouter: any

  beforeEach(() => {
    pinia = createPinia()
    testRouter = createRouter({
      history: createWebHistory(),
      routes: router.getRoutes()
    })
  })

  it('should navigate to admin dashboard', async () => {
    await testRouter.push('/admin')
    expect(testRouter.currentRoute.value.path).toBe('/admin/dashboard')
  })

  it('should navigate to product management', async () => {
    await testRouter.push('/admin/content/products')
    expect(testRouter.currentRoute.value.path).toBe('/admin/content/products')
  })

  it('should navigate to customer management', async () => {
    await testRouter.push('/admin/business/customers')
    expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers')
  })

  it('should navigate to charity management', async () => {
    await testRouter.push('/admin/content/charity')
    expect(testRouter.currentRoute.value.path).toBe('/admin/content/charity')
  })

  it('should navigate to course management', async () => {
    await testRouter.push('/admin/system/courses')
    expect(testRouter.currentRoute.value.path).toBe('/admin/system/courses')
  })

  it('should navigate to sales data', async () => {
    await testRouter.push('/admin/business/sales')
    expect(testRouter.currentRoute.value.path).toBe('/admin/business/sales')
  })

  it('should navigate to language management', async () => {
    await testRouter.push('/admin/system/languages')
    expect(testRouter.currentRoute.value.path).toBe('/admin/system/languages')
  })

  it('should handle invalid routes', async () => {
    await testRouter.push('/invalid-route')
    // Should redirect to 404 or default route
    expect(testRouter.currentRoute.value.path).toBe('/invalid-route')
  })
})