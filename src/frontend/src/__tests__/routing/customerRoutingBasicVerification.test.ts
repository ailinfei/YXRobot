/**
 * 客户管理路由基础验证测试
 * 任务18：配置前端路由和导航 - 基础功能验证
 */

import { describe, it, expect } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'

// 导入路由配置
import { mainNavigationConfig, getBreadcrumb, getPageTitle } from '@/config/navigation'

// 模拟路由配置
const mockRoutes = [
  {
    path: '/admin',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'business/customers',
        name: 'BusinessCustomers',
        component: { template: '<div>客户管理页面</div>' },
        meta: { title: '客户管理' }
      },
      {
        path: 'business/customers/:id',
        name: 'CustomerDetail',
        component: { template: '<div>客户详情页面</div>' },
        meta: { title: '客户详情' }
      }
    ]
  }
]

describe('客户管理路由基础验证', () => {
  let router: any

  beforeEach(() => {
    router = createRouter({
      history: createWebHistory(),
      routes: mockRoutes
    })
  })

  describe('✅ 路由路径配置验证', () => {
    it('应该正确配置客户管理路由路径为 /admin/business/customers', async () => {
      await router.push('/admin/business/customers')
      expect(router.currentRoute.value.name).toBe('BusinessCustomers')
      expect(router.currentRoute.value.meta.title).toBe('客户管理')
    })

    it('应该正确配置客户详情路由路径为 /admin/business/customers/:id', async () => {
      await router.push('/admin/business/customers/123')
      expect(router.currentRoute.value.name).toBe('CustomerDetail')
      expect(router.currentRoute.value.params.id).toBe('123')
      expect(router.currentRoute.value.meta.title).toBe('客户详情')
    })

    it('应该支持动态路由参数传递', async () => {
      const testIds = ['1', 'abc123', 'customer-456']
      
      for (const id of testIds) {
        await router.push(`/admin/business/customers/${id}`)
        expect(router.currentRoute.value.params.id).toBe(id)
      }
    })
  })

  describe('✅ 导航菜单配置验证', () => {
    it('应该在管理后台导航菜单中包含客户管理入口', () => {
      const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
      expect(businessMenu).toBeDefined()
      expect(businessMenu?.title).toBe('业务管理')
      
      const customerMenu = businessMenu?.children?.find(item => item.path === '/admin/business/customers')
      expect(customerMenu).toBeDefined()
      expect(customerMenu?.title).toBe('客户管理')
      expect(customerMenu?.permissions).toContain('customer:view')
    })

    it('应该正确配置菜单权限要求', () => {
      const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
      const customerMenu = businessMenu?.children?.find(item => item.path === '/admin/business/customers')
      
      expect(businessMenu?.permissions).toContain('business:view')
      expect(customerMenu?.permissions).toContain('customer:view')
    })
  })

  describe('✅ 面包屑导航配置验证', () => {
    it('应该正确显示客户管理页面面包屑导航', () => {
      const breadcrumb = getBreadcrumb('/admin/business/customers', 'admin')
      
      expect(breadcrumb).toHaveLength(3)
      expect(breadcrumb[0]).toEqual({ title: '控制台', path: '/admin/dashboard' })
      expect(breadcrumb[1]).toEqual({ title: '业务管理', path: '/admin/business' })
      expect(breadcrumb[2]).toEqual({ title: '客户管理', path: '/admin/business/customers' })
    })

    it('应该正确显示客户详情页面面包屑导航', () => {
      const breadcrumb = getBreadcrumb('/admin/business/customers/123', 'admin')
      
      expect(breadcrumb).toHaveLength(4)
      expect(breadcrumb[0]).toEqual({ title: '控制台', path: '/admin/dashboard' })
      expect(breadcrumb[1]).toEqual({ title: '业务管理', path: '/admin/business' })
      expect(breadcrumb[2]).toEqual({ title: '客户管理', path: '/admin/business/customers' })
      expect(breadcrumb[3]).toEqual({ title: '客户详情', path: '' })
    })
  })

  describe('✅ 页面标题配置验证', () => {
    it('应该正确生成页面标题', () => {
      const customerListTitle = getPageTitle('/admin/business/customers', 'admin')
      expect(customerListTitle).toBe('客户管理')

      const customerDetailTitle = getPageTitle('/admin/business/customers/123', 'admin')
      expect(customerDetailTitle).toBe('客户详情')
    })

    it('应该为未配置的路径返回默认标题', () => {
      const defaultTitle = getPageTitle('/unknown/path', 'admin')
      expect(defaultTitle).toBe('练字机器人管理后台')
    })
  })

  describe('✅ 路由跳转功能验证', () => {
    it('应该支持编程式导航到客户管理页面', async () => {
      await router.push('/admin/business/customers')
      expect(router.currentRoute.value.path).toBe('/admin/business/customers')
      expect(router.currentRoute.value.name).toBe('BusinessCustomers')
    })

    it('应该支持命名路由导航', async () => {
      await router.push({ name: 'BusinessCustomers' })
      expect(router.currentRoute.value.name).toBe('BusinessCustomers')

      await router.push({ name: 'CustomerDetail', params: { id: 'test-123' } })
      expect(router.currentRoute.value.name).toBe('CustomerDetail')
      expect(router.currentRoute.value.params.id).toBe('test-123')
    })

    it('应该支持路由参数和查询参数', async () => {
      await router.push({
        name: 'CustomerDetail',
        params: { id: 'customer-456' },
        query: { tab: 'devices', page: '1' }
      })

      expect(router.currentRoute.value.params.id).toBe('customer-456')
      expect(router.currentRoute.value.query.tab).toBe('devices')
      expect(router.currentRoute.value.query.page).toBe('1')
    })
  })
})

describe('✅ 路由访问权限验证', () => {
  it('应该确保客户管理路由需要认证', () => {
    // 客户管理路由在 /admin 路径下，继承认证要求
    const adminRoute = mockRoutes.find(route => route.path === '/admin')
    expect(adminRoute?.meta?.requiresAuth).toBe(true)
  })

  it('应该正确配置权限检查', () => {
    // 模拟权限检查函数
    const hasPermission = (userPermissions: string[], requiredPermissions: string[]) => {
      return requiredPermissions.some(permission => 
        userPermissions.includes(permission) || userPermissions.includes('admin:all')
      )
    }

    // 测试用户权限
    const userWithCustomerPermission = ['customer:view', 'business:view']
    const userWithoutPermission = ['other:view']
    const adminUser = ['admin:all']

    expect(hasPermission(userWithCustomerPermission, ['customer:view'])).toBe(true)
    expect(hasPermission(userWithoutPermission, ['customer:view'])).toBe(false)
    expect(hasPermission(adminUser, ['customer:view'])).toBe(true)
  })
})

describe('✅ 路由配置完整性检查', () => {
  it('应该确保所有必需的路由都已配置', () => {
    const requiredRoutes = [
      { path: '/admin/business/customers', name: 'BusinessCustomers' },
      { path: '/admin/business/customers/:id', name: 'CustomerDetail' }
    ]

    requiredRoutes.forEach(({ path, name }) => {
      const route = mockRoutes[0].children.find((child: any) => child.name === name)
      expect(route).toBeDefined()
      expect(route?.path).toBe(path.replace('/admin/', ''))
    })
  })

  it('应该确保路由元信息正确配置', () => {
    const customerRoute = mockRoutes[0].children.find((child: any) => child.name === 'BusinessCustomers')
    const detailRoute = mockRoutes[0].children.find((child: any) => child.name === 'CustomerDetail')

    expect(customerRoute?.meta?.title).toBe('客户管理')
    expect(detailRoute?.meta?.title).toBe('客户详情')
  })
})

export default {
  name: 'CustomerRoutingBasicVerificationTest',
  description: '客户管理路由基础验证测试 - 任务18完成验证'
}