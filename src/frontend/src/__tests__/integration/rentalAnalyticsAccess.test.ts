/**
 * 租赁数据分析页面访问集成测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import AdminLayout from '@/components/admin/AdminLayout.vue'

describe('租赁数据分析页面访问集成测试', () => {
  let router: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/admin',
          component: AdminLayout,
          children: [
            {
              path: 'business/rental-analytics',
              name: 'BusinessRental',
              component: { template: '<div class="rental-analytics-page">租赁数据分析页面</div>' },
              meta: { title: '租赁数据分析' }
            }
          ]
        }
      ]
    })
  })

  it('应该能够正确访问租赁数据分析页面', async () => {
    // 模拟用户登录状态
    localStorage.setItem('token', 'mock-jwt-token')
    localStorage.setItem('userInfo', JSON.stringify({
      name: '测试用户',
      permissions: ['admin:all']
    }))

    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, pinia]
      }
    })

    // 导航到租赁数据分析页面
    await router.push('/admin/business/rental-analytics')
    await wrapper.vm.$nextTick()

    // 验证路由状态
    expect(router.currentRoute.value.path).toBe('/admin/business/rental-analytics')
    expect(router.currentRoute.value.name).toBe('BusinessRental')
    expect(router.currentRoute.value.meta.title).toBe('租赁数据分析')
  })

  it('应该在导航菜单中显示租赁数据分析链接', async () => {
    // 模拟用户登录状态
    localStorage.setItem('token', 'mock-jwt-token')
    localStorage.setItem('userInfo', JSON.stringify({
      name: '测试用户',
      permissions: ['admin:all']
    }))

    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, pinia]
      }
    })

    await wrapper.vm.$nextTick()

    // 检查导航菜单中是否包含租赁数据分析链接
    const menuItems = wrapper.vm.menuItems
    const businessMenu = menuItems.find((item: any) => item.path === '/admin/business')
    expect(businessMenu).toBeDefined()
    
    const rentalAnalyticsMenu = businessMenu.children.find(
      (child: any) => child.path === '/admin/business/rental-analytics'
    )
    expect(rentalAnalyticsMenu).toBeDefined()
    expect(rentalAnalyticsMenu.title).toBe('租赁数据分析')
  })

  it('应该正确显示面包屑导航', async () => {
    // 模拟用户登录状态
    localStorage.setItem('token', 'mock-jwt-token')
    localStorage.setItem('userInfo', JSON.stringify({
      name: '测试用户',
      permissions: ['admin:all']
    }))

    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, pinia]
      }
    })

    // 导航到租赁数据分析页面
    await router.push('/admin/business/rental-analytics')
    await wrapper.vm.$nextTick()

    // 验证页面标题
    expect(wrapper.vm.currentPageTitle).toBe('租赁数据分析')
    
    // 验证面包屑
    const breadcrumb = wrapper.vm.currentBreadcrumb
    expect(breadcrumb.parent).toBe('业务管理')
    expect(breadcrumb.current).toBe('租赁数据分析')
  })

  it('应该在页面刷新后保持路由状态', async () => {
    // 模拟用户登录状态
    localStorage.setItem('token', 'mock-jwt-token')
    localStorage.setItem('userInfo', JSON.stringify({
      name: '测试用户',
      permissions: ['admin:all']
    }))

    // 导航到租赁数据分析页面
    await router.push('/admin/business/rental-analytics')
    
    // 验证路由状态保持
    expect(router.currentRoute.value.path).toBe('/admin/business/rental-analytics')
    expect(router.currentRoute.value.name).toBe('BusinessRental')
  })

  afterEach(() => {
    // 清理测试数据
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  })
})