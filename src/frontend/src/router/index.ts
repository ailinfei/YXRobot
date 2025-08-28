import { createRouter, createWebHistory } from 'vue-router'
import { lazyRoute } from '@/utils/lazyLoading'
import { newsRoutes, newsRouteGuard } from './news'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/admin/dashboard'
    },
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: lazyRoute(() => import('@/views/admin/Login.vue'))
    },
    {
      path: '/admin',
      component: lazyRoute(() => import('@/components/admin/AdminLayout.vue')),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: lazyRoute(() => import('@/views/admin/Dashboard.vue')),
          meta: { title: '数据看板' }
        },
        // 内容管理
        {
          path: 'content/products',
          name: 'ContentProducts',
          component: lazyRoute(() => import('@/views/admin/content/Products.vue')),
          meta: { title: '产品管理' }
        },
        {
          path: 'content/charity',
          name: 'ContentCharity',
          component: lazyRoute(() => import('@/views/admin/content/Charity.vue')),
          meta: { title: '公益项目管理' }
        },
        {
          path: 'content/platforms',
          name: 'ContentPlatforms',
          component: lazyRoute(() => import('@/views/admin/content/Platforms.vue')),
          meta: { title: '平台链接管理' }
        },
        // 新闻管理路由
        {
          path: 'content/news',
          name: 'ContentNews',
          component: lazyRoute(() => import('@/views/admin/content/NewsManagement.vue')),
          meta: {
            title: '新闻管理',
            requiresAuth: true,
            permissions: ['news:view']
          }
        },
        // 业务管理
        {
          path: 'business/sales',
          name: 'BusinessSales',
          component: lazyRoute(() => import('@/views/admin/business/Sales.vue')),
          meta: { title: '销售数据' }
        },
        {
          path: 'business/rental',
          name: 'BusinessRental',
          component: lazyRoute(() => import('@/views/admin/business/RentalAnalytics.vue')),
          meta: { title: '租赁数据分析' }
        },
        {
          path: 'business/customers',
          name: 'BusinessCustomers',
          component: lazyRoute(() => import('@/views/admin/business/CustomerManagement.vue')),
          meta: { title: '客户管理' }
        },
        {
          path: 'business/customers/:id',
          name: 'CustomerDetail',
          component: lazyRoute(() => import('@/views/admin/business/CustomerDetail.vue')),
          meta: { title: '客户详情' }
        },
        {
          path: 'business/orders',
          name: 'OrderManagement',
          component: lazyRoute(() => import('@/views/admin/Orders.vue')),
          meta: { title: '订单管理' }
        },
        // 设备管理
        {
          path: 'device/management',
          name: 'DeviceManagement',
          component: lazyRoute(() => import('@/views/admin/device/DeviceManagement.vue')),
          meta: { title: '设备管理' }
        },
        {
          path: 'device/monitoring',
          name: 'DeviceMonitoring',
          component: lazyRoute(() => import('@/views/admin/device/DeviceMonitoring.vue')),
          meta: { title: '设备监控' }
        },
        {
          path: 'device/firmware',
          name: 'FirmwareManagement',
          component: lazyRoute(() => import('@/views/admin/device/FirmwareManagement.vue')),
          meta: { title: '固件管理' }
        },
        // 系统管理
        {
          path: 'system/courses',
          name: 'SystemCourses',
          component: lazyRoute(() => import('@/views/admin/system/CourseManagement.vue')),
          meta: { title: '课程管理' }
        },
        {
          path: 'system/fonts',
          name: 'SystemFonts',
          component: lazyRoute(() => import('@/views/admin/system/FontPackageManagement.vue')),
          meta: { title: 'AI字体包管理' }
        },
        {
          path: 'system/languages',
          name: 'SystemLanguages',
          component: lazyRoute(() => import('@/views/admin/system/LanguageManagement.vue')),
          meta: { title: '多语言管理' }
        }
      ]
    },
    {
      path: '/website',
      component: lazyRoute(() => import('@/components/website/WebsiteLayout.vue')),
      children: [
        {
          path: '',
          redirect: '/website/home'
        },
        {
          path: 'home',
          name: 'Home',
          component: lazyRoute(() => import('@/views/website/Home.vue'))
        },
        {
          path: 'products',
          name: 'WebsiteProducts',
          component: lazyRoute(() => import('@/views/website/Products.vue'))
        },
        {
          path: 'charity',
          name: 'Charity',
          component: lazyRoute(() => import('@/views/website/Charity.vue'))
        },
        // 前台新闻路由 - 使用独立的路由配置
        ...newsRoutes.filter(route => route.path.startsWith('/website/news')).map(route => ({
          ...route,
          path: route.path.replace('/website/', '')
        })),
        {
          path: 'support',
          name: 'Support',
          component: lazyRoute(() => import('@/views/website/Support.vue'))
        },
        {
          path: 'contact',
          name: 'Contact',
          component: lazyRoute(() => import('@/views/website/Contact.vue'))
        }
      ]
    },
    // 测试页面路由
    {
      path: '/test',
      children: [
        {
          path: 'orders',
          name: 'OrderTest',
          component: lazyRoute(() => import('@/views/test/OrderTest.vue')),
          meta: { title: '订单功能测试' }
        },
        {
          path: 'simple-orders',
          name: 'SimpleOrderTest',
          component: lazyRoute(() => import('@/views/test/SimpleOrderTest.vue')),
          meta: { title: '简单订单测试' }
        },
        {
          path: 'api',
          name: 'ApiTest',
          component: lazyRoute(() => import('@/views/test/ApiTest.vue')),
          meta: { title: 'API测试' }
        }
      ]
    }
  ],
})

// 路由守卫 - 权限管理
router.beforeEach((to, from, next) => {
  // 新闻相关路由的权限检查
  if (to.path.includes('/news')) {
    let guardHandled = false
    const guardNext = (route?: any) => {
      guardHandled = true
      if (route) {
        next(route)
      } else {
        next()
      }
    }

    newsRouteGuard(to, from, guardNext)
    if (guardHandled) return
  }
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    const userInfo = localStorage.getItem('userInfo')

    if (!token || !userInfo) {
      // 未登录，跳转到登录页
      console.log('用户未登录，跳转到登录页')
      next('/admin/login')
      return
    }

    try {
      // 验证用户信息
      const user = JSON.parse(userInfo)
      if (!user.permissions || !user.permissions.includes('admin:all')) {
        // 权限不足
        console.warn('权限不足，跳转到登录页')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        next('/admin/login')
        return
      }

      // 验证token是否过期（简单的模拟验证）
      if (token === 'mock-jwt-token') {
        // 模拟token验证通过
        console.log('用户认证通过')
      } else {
        // token无效
        console.warn('Token无效，跳转到登录页')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        next('/admin/login')
        return
      }
    } catch (error) {
      // 用户信息解析失败
      console.error('用户信息解析失败:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      next('/admin/login')
      return
    }
  }

  // 如果已登录用户访问登录页，重定向到仪表板
  if (to.path === '/admin/login') {
    const token = localStorage.getItem('token')
    if (token) {
      console.log('用户已登录，重定向到仪表板')
      next('/admin/dashboard')
      return
    }
  }

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 练字机器人管理后台`
  } else {
    document.title = '练字机器人管理后台'
  }

  next()
})

export default router
