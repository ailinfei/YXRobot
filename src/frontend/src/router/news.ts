/**
 * 新闻管理路由配置
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { RouteRecordRaw } from 'vue-router'
import { lazyRoute } from '@/utils/lazyLoading'

/**
 * 新闻管理相关路由
 */
export const newsRoutes: RouteRecordRaw[] = [
  // 管理后台新闻路由
  {
    path: '/admin/content/news',
    name: 'ContentNews',
    component: lazyRoute(() => import('@/views/admin/content/NewsManagement.vue')),
    meta: { 
      title: '新闻管理',
      requiresAuth: true,
      permissions: ['news:view'],
      breadcrumb: [
        { title: '控制台', path: '/admin/dashboard' },
        { title: '官网管理', path: '/admin/content' },
        { title: '新闻管理', path: '/admin/content/news' }
      ]
    }
  },
  // 注意：编辑和创建功能已集成在NewsManagement.vue中，暂时移除独立的编辑器路由

  // 前台网站新闻路由 - 暂时移除，专注于管理后台功能
]

/**
 * 新闻管理导航菜单配置
 */
export const newsMenuItems = [
  {
    path: '/admin/content/news',
    title: '新闻列表',
    icon: 'Document',
    permissions: ['news:view']
  },
  // 其他菜单项暂时移除，专注于核心新闻管理功能
]

/**
 * 新闻权限配置
 */
export const newsPermissions = {
  // 基础权限
  VIEW: 'news:view',
  CREATE: 'news:create',
  EDIT: 'news:edit',
  DELETE: 'news:delete',
  
  // 状态管理权限
  PUBLISH: 'news:publish',
  OFFLINE: 'news:offline',
  
  // 分类管理权限
  CATEGORY_VIEW: 'news:category:view',
  CATEGORY_MANAGE: 'news:category:manage',
  
  // 标签管理权限
  TAG_VIEW: 'news:tag:view',
  TAG_MANAGE: 'news:tag:manage',
  
  // 统计权限
  STATS: 'news:stats',
  
  // 文件上传权限
  UPLOAD: 'news:upload'
} as const

/**
 * 路由守卫 - 新闻权限检查
 */
export const newsRouteGuard = (to: any, from: any, next: any) => {
  // 检查新闻相关权限
  const requiredPermissions = to.meta?.permissions || []
  
  if (requiredPermissions.length > 0) {
    // 这里应该调用权限服务检查用户权限
    // const hasPermission = permissionService.hasPermissions(requiredPermissions)
    // if (!hasPermission) {
    //   next('/admin/403') // 跳转到无权限页面
    //   return
    // }
  }
  
  next()
}

/**
 * 动态面包屑生成
 */
export const generateNewsBreadcrumb = (route: any, newsTitle?: string) => {
  const baseBreadcrumb = route.meta?.breadcrumb || []
  
  // 如果是新闻详情页面，添加新闻标题
  if (route.name === 'NewsDetail' || route.name === 'NewsEdit') {
    if (newsTitle) {
      return [
        ...baseBreadcrumb.slice(0, -1),
        { title: newsTitle, path: '' }
      ]
    }
  }
  
  return baseBreadcrumb
}