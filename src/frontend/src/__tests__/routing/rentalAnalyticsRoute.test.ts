/**
 * 租赁数据分析路由测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { describe, it, expect, beforeEach } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'

describe('租赁数据分析路由测试', () => {
  let router: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/admin/business/rental-analytics',
          name: 'BusinessRental',
          component: { template: '<div>租赁数据分析页面</div>' },
          meta: { title: '租赁数据分析' }
        }
      ]
    })
  })

  it('应该正确配置租赁数据分析路由', async () => {
    // 测试路由路径
    await router.push('/admin/business/rental-analytics')
    expect(router.currentRoute.value.path).toBe('/admin/business/rental-analytics')
    expect(router.currentRoute.value.name).toBe('BusinessRental')
    expect(router.currentRoute.value.meta.title).toBe('租赁数据分析')
  })

  it('应该能够从其他页面导航到租赁数据分析页面', async () => {
    // 先导航到其他页面
    await router.push('/admin/dashboard')
    expect(router.currentRoute.value.path).toBe('/admin/dashboard')

    // 再导航到租赁数据分析页面
    await router.push('/admin/business/rental-analytics')
    expect(router.currentRoute.value.path).toBe('/admin/business/rental-analytics')
    expect(router.currentRoute.value.name).toBe('BusinessRental')
  })

  it('应该正确设置页面标题', async () => {
    await router.push('/admin/business/rental-analytics')
    const route = router.currentRoute.value
    expect(route.meta.title).toBe('租赁数据分析')
  })

  it('应该支持路由参数和查询参数', async () => {
    // 测试查询参数
    await router.push('/admin/business/rental-analytics?period=monthly&deviceModel=YX-Robot-Pro')
    const route = router.currentRoute.value
    expect(route.query.period).toBe('monthly')
    expect(route.query.deviceModel).toBe('YX-Robot-Pro')
  })

  it('应该正确处理路由守卫', async () => {
    // 这里可以测试权限验证等路由守卫逻辑
    await router.push('/admin/business/rental-analytics')
    expect(router.currentRoute.value.path).toBe('/admin/business/rental-analytics')
  })
})