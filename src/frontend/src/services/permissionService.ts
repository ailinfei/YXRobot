/**
 * 权限管理服务
 * 管理用户权限验证和菜单权限控制
 */

import authService from './authService'

export interface Permission {
  code: string
  name: string
  description: string
  category: string
}

export interface MenuPermission {
  path: string
  permissions: string[]
  roles?: string[]
}

class PermissionService {
  // 系统权限定义
  private readonly PERMISSIONS: Permission[] = [
    // 数据看板权限
    { code: 'dashboard:view', name: '查看数据看板', description: '查看系统数据看板和统计信息', category: 'dashboard' },
    
    // 内容管理权限
    { code: 'content:view', name: '查看内容', description: '查看内容管理相关页面', category: 'content' },
    { code: 'content:manage', name: '管理内容', description: '管理产品、公益项目、平台链接等内容', category: 'content' },
    { code: 'content:product:manage', name: '管理产品', description: '管理产品信息和媒体文件', category: 'content' },
    { code: 'content:charity:manage', name: '管理公益项目', description: '管理公益项目数据和内容', category: 'content' },
    { code: 'content:platform:manage', name: '管理平台链接', description: '管理电商和租赁平台链接', category: 'content' },
    
    // 业务管理权限
    { code: 'business:view', name: '查看业务数据', description: '查看销售、租赁、客户等业务数据', category: 'business' },
    { code: 'business:manage', name: '管理业务数据', description: '管理销售、租赁、客户等业务数据', category: 'business' },
    { code: 'business:sales:manage', name: '管理销售数据', description: '管理销售订单和数据分析', category: 'business' },
    { code: 'business:rental:manage', name: '管理租赁数据', description: '管理租赁订单和数据分析', category: 'business' },
    { code: 'business:customer:manage', name: '管理客户数据', description: '管理客户信息和设备关联', category: 'business' },
    
    // 系统管理权限
    { code: 'system:view', name: '查看系统设置', description: '查看系统管理相关页面', category: 'system' },
    { code: 'system:manage', name: '管理系统设置', description: '管理系统配置和设置', category: 'system' },
    { code: 'system:course:manage', name: '管理课程', description: '管理课程内容和字库资源', category: 'system' },
    { code: 'system:font:manage', name: '管理字体包', description: '管理AI字体包生成和分发', category: 'system' },
    
    // 超级管理员权限
    { code: 'admin:all', name: '超级管理员', description: '拥有系统所有权限', category: 'admin' }
  ]

  // 菜单权限映射
  private readonly MENU_PERMISSIONS: MenuPermission[] = [
    // 数据看板
    { path: '/admin/dashboard', permissions: ['dashboard:view', 'admin:all'] },
    
    // 内容管理
    { path: '/admin/content/products', permissions: ['content:product:manage', 'content:manage', 'admin:all'] },
    { path: '/admin/content/charity', permissions: ['content:charity:manage', 'content:manage', 'admin:all'] },
    { path: '/admin/content/platforms', permissions: ['content:platform:manage', 'content:manage', 'admin:all'] },
    
    // 业务管理
    { path: '/admin/business/sales', permissions: ['business:sales:manage', 'business:manage', 'admin:all'] },
    { path: '/admin/business/rental-analytics', permissions: ['business:rental:manage', 'business:manage', 'admin:all'] },
    { path: '/admin/business/customers', permissions: ['business:customer:manage', 'business:manage', 'admin:all'] },
    
    // 系统管理
    { path: '/admin/system/courses', permissions: ['system:course:manage', 'system:manage', 'admin:all'] },
    { path: '/admin/system/fonts', permissions: ['system:font:manage', 'system:manage', 'admin:all'] }
  ]

  /**
   * 检查用户是否有指定权限
   */
  hasPermission(permission: string): boolean {
    return authService.hasPermission(permission)
  }

  /**
   * 检查用户是否有多个权限中的任意一个
   */
  hasAnyPermission(permissions: string[]): boolean {
    return permissions.some(permission => this.hasPermission(permission))
  }

  /**
   * 检查用户是否有所有指定权限
   */
  hasAllPermissions(permissions: string[]): boolean {
    return permissions.every(permission => this.hasPermission(permission))
  }

  /**
   * 检查用户是否可以访问指定路径
   */
  canAccessPath(path: string): boolean {
    const menuPermission = this.MENU_PERMISSIONS.find(mp => mp.path === path)
    
    if (!menuPermission) {
      // 如果没有配置权限要求，默认允许访问
      return true
    }

    // 检查是否有任意一个所需权限
    return this.hasAnyPermission(menuPermission.permissions)
  }

  /**
   * 获取用户可访问的菜单项
   */
  getAccessibleMenuItems(menuItems: any[]): any[] {
    return menuItems.filter(item => {
      // 检查主菜单项权限
      if (item.path && !this.canAccessPath(item.path)) {
        return false
      }

      // 如果有子菜单，过滤子菜单项
      if (item.children) {
        item.children = item.children.filter((child: any) => 
          this.canAccessPath(child.path)
        )
        
        // 如果所有子菜单都被过滤掉，则隐藏父菜单
        return item.children.length > 0
      }

      return true
    })
  }

  /**
   * 获取所有权限定义
   */
  getAllPermissions(): Permission[] {
    return [...this.PERMISSIONS]
  }

  /**
   * 根据分类获取权限
   */
  getPermissionsByCategory(category: string): Permission[] {
    return this.PERMISSIONS.filter(p => p.category === category)
  }

  /**
   * 获取用户当前权限
   */
  getCurrentUserPermissions(): string[] {
    const user = authService.getCurrentUser()
    return user?.permissions || []
  }

  /**
   * 检查是否为超级管理员
   */
  isSuperAdmin(): boolean {
    return this.hasPermission('admin:all')
  }

  /**
   * 获取权限描述
   */
  getPermissionDescription(permissionCode: string): string {
    const permission = this.PERMISSIONS.find(p => p.code === permissionCode)
    return permission?.description || '未知权限'
  }

  /**
   * 验证权限代码是否有效
   */
  isValidPermission(permissionCode: string): boolean {
    return this.PERMISSIONS.some(p => p.code === permissionCode)
  }

  /**
   * 获取菜单权限要求
   */
  getMenuPermissionRequirements(path: string): string[] {
    const menuPermission = this.MENU_PERMISSIONS.find(mp => mp.path === path)
    return menuPermission?.permissions || []
  }
}

// 创建单例实例
export const permissionService = new PermissionService()

// 默认导出
export default permissionService