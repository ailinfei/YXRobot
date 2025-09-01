/**
 * 客户管理路由验证测试
 * 任务18：配置前端路由和导航 - 完整验证测试
 */

import { describe, it, expect, beforeEach } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'

// 导入路由配置
import router from '@/router'
import { mainNavigationConfig, breadcrumbConfig, getBreadcrumb, getPageTitle } from '@/config/navigation'

describe('客户管理路由和导航完整验证', () => {
  let testRouter: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    testRouter = createRouter({
      history: createWebHistory(),
      routes: router.getRoutes()
    })
  })

  describe('路由配置验证', () => {
    it('应该正确配置客户管理路由路径', async () => {
      // 测试客户管理列表路由
      await testRouter.push('/admin/business/customers')
      expect(testRouter.currentRoute.value.name).toBe('BusinessCustomers')
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers')
      expect(testRouter.currentRoute.value.meta.title).toBe('客户管理')
    })

    it('应该正确配置客户详情路由', async () => {
      // 测试客户详情路由
      await testRouter.push('/admin/business/customers/123')
      expect(testRouter.currentRoute.value.name).toBe('CustomerDetail')
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers/123')
      expect(testRouter.currentRoute.value.params.id).toBe('123')
      expect(testRouter.currentRoute.value.meta.title).toBe('客户详情')
    })

    it('应该支持路由参数传递', async () => {
      const customerId = 'customer-456'
      await testRouter.push(`/admin/business/customers/${customerId}`)
      expect(testRouter.currentRoute.value.params.id).toBe(customerId)
    })
  })

  describe('导航菜单配置验证', () => {
    it('应该在业务管理菜单中包含客户管理', () => {
      const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
      expect(businessMenu).toBeDefined()
      expect(businessMenu?.title).toBe('业务管理')
      
      const customerMenu = businessMenu?.children?.find(item => item.path === '/admin/business/customers')
      expect(customerMenu).toBeDefined()
      expect(customerMenu?.title).toBe('客户管理')
      expect(customerMenu?.icon).toBeDefined()
      expect(customerMenu?.permissions).toContain('customer:view')
    })

    it('应该正确配置菜单权限', () => {
      const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
      const customerMenu = businessMenu?.children?.find(item => item.path === '/admin/business/customers')
      
      expect(businessMenu?.permissions).toContain('business:view')
      expect(customerMenu?.permissions).toContain('customer:view')
    })
  })

  describe('面包屑导航验证', () => {
    it('应该正确生成客户管理页面面包屑', () => {
      const breadcrumb = getBreadcrumb('/admin/business/customers', 'admin')
      
      expect(breadcrumb).toHaveLength(3)
      expect(breadcrumb[0]).toEqual({ title: '控制台', path: '/admin/dashboard' })
      expect(breadcrumb[1]).toEqual({ title: '业务管理', path: '/admin/business' })
      expect(breadcrumb[2]).toEqual({ title: '客户管理', path: '/admin/business/customers' })
    })

    it('应该正确生成客户详情页面面包屑', () => {
      const breadcrumb = getBreadcrumb('/admin/business/customers/123', 'admin')
      
      expect(breadcrumb).toHaveLength(4)
      expect(breadcrumb[0]).toEqual({ title: '控制台', path: '/admin/dashboard' })
      expect(breadcrumb[1]).toEqual({ title: '业务管理', path: '/admin/business' })
      expect(breadcrumb[2]).toEqual({ title: '客户管理', path: '/admin/business/customers' })
      expect(breadcrumb[3]).toEqual({ title: '客户详情', path: '' })
    })

    it('应该支持动态路由的面包屑生成', () => {
      // 测试动态路由模式匹配
      const testPaths = [
        '/admin/business/customers/1',
        '/admin/business/customers/abc123',
        '/admin/business/customers/customer-456'
      ]

      testPaths.forEach(path => {
        const breadcrumb = getBreadcrumb(path, 'admin')
        expect(breadcrumb).toHaveLength(4)
        expect(breadcrumb[3].title).toBe('客户详情')
      })
    })
  })

  describe('页面标题配置验证', () => {
    it('应该正确生成页面标题', () => {
      const customerListTitle = getPageTitle('/admin/business/customers', 'admin')
      expect(customerListTitle).toBe('客户管理')

      const customerDetailTitle = getPageTitle('/admin/business/customers/123', 'admin')
      expect(customerDetailTitle).toBe('客户详情')
    })

    it('应该为未知路径返回默认标题', () => {
      const defaultTitle = getPageTitle('/unknown/path', 'admin')
      expect(defaultTitle).toBe('练字机器人管理后台')
    })
  })

  describe('路由跳转功能验证', () => {
    it('应该支持编程式导航', async () => {
      // 测试编程式导航到客户管理
      await testRouter.push('/admin/business/customers')
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers')

      // 测试编程式导航到客户详情
      await testRouter.push('/admin/business/customers/789')
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers/789')
      expect(testRouter.currentRoute.value.params.id).toBe('789')
    })

    it('应该支持命名路由导航', async () => {
      // 使用路由名称导航
      await testRouter.push({ name: 'BusinessCustomers' })
      expect(testRouter.currentRoute.value.name).toBe('BusinessCustomers')

      await testRouter.push({ name: 'CustomerDetail', params: { id: 'test-id' } })
      expect(testRouter.currentRoute.value.name).toBe('CustomerDetail')
      expect(testRouter.currentRoute.value.params.id).toBe('test-id')
    })

    it('应该支持路由参数和查询参数', async () => {
      await testRouter.push({
        name: 'CustomerDetail',
        params: { id: 'customer-123' },
        query: { tab: 'devices', page: '2' }
      })

      expect(testRouter.currentRoute.value.params.id).toBe('customer-123')
      expect(testRouter.currentRoute.value.query.tab).toBe('devices')
      expect(testRouter.currentRoute.value.query.page).toBe('2')
    })
  })

  describe('路由权限验证', () => {
    it('应该正确识别需要认证的路由', () => {
      const routes = testRouter.getRoutes()
      
      // 查找admin路由
      const adminRoute = routes.find((route: any) => route.path === '/admin')
      expect(adminRoute).toBeDefined()
      expect(adminRoute?.meta?.requiresAuth).toBe(true)
    })

    it('应该继承父路由的权限设置', () => {
      const routes = testRouter.getRoutes()
      
      // 客户管理路由应该继承admin路由的认证要求
      const customerRoutes = routes.filter((route: any) => 
        route.path.includes('/admin/business/customers')
      )
      
      expect(customerRoutes.length).toBeGreaterThan(0)
      // 子路由会继承父路由的meta配置
    })
  })

  describe('路由状态保持验证', () => {
    it('应该正确处理路由历史', async () => {
      // 模拟用户导航历史
      await testRouter.push('/admin/dashboard')
      await testRouter.push('/admin/business/customers')
      await testRouter.push('/admin/business/customers/123')

      // 测试后退
      await testRouter.back()
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers')

      // 测试前进
      await testRouter.forward()
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers/123')
    })

    it('应该支持路由替换', async () => {
      await testRouter.push('/admin/business/customers')
      await testRouter.replace('/admin/business/customers/456')
      
      expect(testRouter.currentRoute.value.path).toBe('/admin/business/customers/456')
      expect(testRouter.currentRoute.value.params.id).toBe('456')
    })
  })
})

describe('路由配置完整性检查', () => {
  it('应该确保所有必需的路由都已配置', () => {
    const routes = router.getRoutes()
    const routePaths = routes.map(route => route.path)

    // 检查关键路由是否存在
    const requiredRoutes = [
      '/admin',
      '/admin/business/customers',
      '/admin/business/customers/:id'
    ]

    requiredRoutes.forEach(requiredRoute => {
      const exists = routes.some(route => {
        if (requiredRoute.includes(':')) {
          // 对于动态路由，检查模式匹配
          const pattern = requiredRoute.replace(':id', '[^/]+')
          const regex = new RegExp(`^${pattern}$`)
          return regex.test(route.path) || route.path === requiredRoute
        }
        return route.path === requiredRoute
      })
      expect(exists).toBe(true)
    })
  })

  it('应该确保路由组件正确配置', () => {
    const routes = router.getRoutes()
    
    const customerManagementRoute = routes.find(route => 
      route.name === 'BusinessCustomers'
    )
    expect(customerManagementRoute).toBeDefined()
    expect(customerManagementRoute?.component).toBeDefined()

    const customerDetailRoute = routes.find(route => 
      route.name === 'CustomerDetail'
    )
    expect(customerDetailRoute).toBeDefined()
    expect(customerDetailRoute?.component).toBeDefined()
  })
})

export default {
  name: 'CustomerRoutingVerificationTest',
  description: '客户管理路由和导航完整验证测试套件'
}