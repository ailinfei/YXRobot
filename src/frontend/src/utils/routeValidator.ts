/**
 * 路由配置验证工具
 * 任务20：配置前端路由和导航 - 路由验证工具
 */

import type { RouteRecordNormalized, RouteRecordRaw } from 'vue-router'

export interface RouteValidationResult {
  isValid: boolean
  errors: string[]
  warnings: string[]
  summary: {
    totalRoutes: number
    validRoutes: number
    invalidRoutes: number
    routesWithAuth: number
    routesWithPermissions: number
  }
}

export interface RouteInfo {
  path: string
  name: string
  title?: string
  requiresAuth: boolean
  permissions: string[]
  component?: string
  children?: RouteInfo[]
}

/**
 * 路由验证器类
 */
export class RouteValidator {
  private errors: string[] = []
  private warnings: string[] = []
  private routeCount = 0
  private validRouteCount = 0
  private authRouteCount = 0
  private permissionRouteCount = 0

  /**
   * 验证路由配置
   */
  validateRoutes(routes: RouteRecordRaw[]): RouteValidationResult {
    this.reset()
    
    for (const route of routes) {
      this.validateRoute(route)
    }

    return {
      isValid: this.errors.length === 0,
      errors: [...this.errors],
      warnings: [...this.warnings],
      summary: {
        totalRoutes: this.routeCount,
        validRoutes: this.validRouteCount,
        invalidRoutes: this.routeCount - this.validRouteCount,
        routesWithAuth: this.authRouteCount,
        routesWithPermissions: this.permissionRouteCount
      }
    }
  }

  /**
   * 验证单个路由
   */
  private validateRoute(route: RouteRecordRaw, parentPath = ''): void {
    this.routeCount++
    let isValid = true

    const fullPath = this.buildFullPath(parentPath, route.path)

    // 验证路径格式
    if (!this.validatePath(route.path, fullPath)) {
      isValid = false
    }

    // 验证路由名称
    if (!this.validateName(route.name, fullPath)) {
      isValid = false
    }

    // 验证组件
    if (!this.validateComponent(route.component, fullPath)) {
      isValid = false
    }

    // 验证元数据
    if (!this.validateMeta(route.meta, fullPath)) {
      isValid = false
    }

    // 验证子路由
    if (route.children) {
      for (const child of route.children) {
        this.validateRoute(child, fullPath)
      }
    }

    if (isValid) {
      this.validRouteCount++
    }

    // 统计认证和权限路由
    if (route.meta?.requiresAuth) {
      this.authRouteCount++
    }
    if (route.meta?.permissions) {
      this.permissionRouteCount++
    }
  }

  /**
   * 验证路径格式
   */
  private validatePath(path: string, fullPath: string): boolean {
    if (!path) {
      this.errors.push(`路由路径不能为空: ${fullPath}`)
      return false
    }

    // 检查路径格式
    if (path !== '/' && !path.startsWith('/') && !path.startsWith(':')) {
      this.warnings.push(`路径应该以 '/' 开头: ${fullPath}`)
    }

    // 检查路径中的特殊字符
    if (path.includes('//')) {
      this.errors.push(`路径包含双斜杠: ${fullPath}`)
      return false
    }

    // 检查路径长度
    if (fullPath.length > 200) {
      this.warnings.push(`路径过长 (${fullPath.length} 字符): ${fullPath}`)
    }

    return true
  }

  /**
   * 验证路由名称
   */
  private validateName(name: any, fullPath: string): boolean {
    if (!name) {
      this.warnings.push(`建议为路由设置名称: ${fullPath}`)
      return true
    }

    if (typeof name !== 'string') {
      this.errors.push(`路由名称必须是字符串: ${fullPath}`)
      return false
    }

    // 检查名称格式（建议使用 PascalCase）
    if (!/^[A-Z][a-zA-Z0-9]*$/.test(name)) {
      this.warnings.push(`建议路由名称使用 PascalCase 格式: ${name} (${fullPath})`)
    }

    return true
  }

  /**
   * 验证组件
   */
  private validateComponent(component: any, fullPath: string): boolean {
    if (!component) {
      // 对于有子路由的父路由，组件可以为空
      if (!fullPath.includes('children')) {
        this.warnings.push(`路由缺少组件: ${fullPath}`)
      }
      return true
    }

    // 检查是否是懒加载组件
    if (typeof component === 'function') {
      // 这是懒加载组件，无法进一步验证
      return true
    }

    return true
  }

  /**
   * 验证元数据
   */
  private validateMeta(meta: any, fullPath: string): boolean {
    if (!meta) {
      return true
    }

    let isValid = true

    // 验证标题
    if (meta.title && typeof meta.title !== 'string') {
      this.errors.push(`路由标题必须是字符串: ${fullPath}`)
      isValid = false
    }

    // 验证权限配置
    if (meta.permissions) {
      if (!Array.isArray(meta.permissions)) {
        this.errors.push(`权限配置必须是数组: ${fullPath}`)
        isValid = false
      } else {
        for (const permission of meta.permissions) {
          if (typeof permission !== 'string') {
            this.errors.push(`权限项必须是字符串: ${permission} (${fullPath})`)
            isValid = false
          }
        }
      }
    }

    // 验证认证要求
    if (meta.requiresAuth && typeof meta.requiresAuth !== 'boolean') {
      this.errors.push(`requiresAuth 必须是布尔值: ${fullPath}`)
      isValid = false
    }

    return isValid
  }

  /**
   * 构建完整路径
   */
  private buildFullPath(parentPath: string, currentPath: string): string {
    if (!parentPath) {
      return currentPath
    }

    if (currentPath.startsWith('/')) {
      return currentPath
    }

    return `${parentPath}/${currentPath}`.replace(/\/+/g, '/')
  }

  /**
   * 重置验证状态
   */
  private reset(): void {
    this.errors = []
    this.warnings = []
    this.routeCount = 0
    this.validRouteCount = 0
    this.authRouteCount = 0
    this.permissionRouteCount = 0
  }
}

/**
 * 提取路由信息
 */
export function extractRouteInfo(routes: RouteRecordRaw[]): RouteInfo[] {
  const result: RouteInfo[] = []

  for (const route of routes) {
    const info: RouteInfo = {
      path: route.path,
      name: route.name as string || '',
      component: getComponentName(route.component),
      title: route.meta?.title,
      requiresAuth: route.meta?.requiresAuth,
      permissions: route.meta?.permissions
    }

    if (route.children) {
      info.children = extractRouteInfo(route.children)
    }

    result.push(info)
  }

  return result
}

/**
 * 获取组件名称
 */
function getComponentName(component: any): string {
  if (!component) {
    return 'undefined'
  }

  if (typeof component === 'function') {
    return 'LazyComponent'
  }

  if (component.name) {
    return component.name
  }

  return 'UnknownComponent'
}

/**
 * 检查路由是否存在
 */
export function checkRouteExists(routes: RouteRecordRaw[], targetPath: string): boolean {
  for (const route of routes) {
    if (route.path === targetPath) {
      return true
    }

    if (route.children) {
      if (checkRouteExists(route.children, targetPath)) {
        return true
      }
    }
  }

  return false
}

/**
 * 查找路由配置
 */
export function findRoute(routes: RouteRecordRaw[], targetPath: string): RouteRecordRaw | null {
  for (const route of routes) {
    if (route.path === targetPath) {
      return route
    }

    if (route.children) {
      const found = findRoute(route.children, targetPath)
      if (found) {
        return found
      }
    }
  }

  return null
}

/**
 * 获取所有路由路径
 */
export function getAllRoutePaths(routes: RouteRecordRaw[], parentPath = ''): string[] {
  const paths: string[] = []

  for (const route of routes) {
    const fullPath = parentPath ? `${parentPath}/${route.path}`.replace(/\/+/g, '/') : route.path

    paths.push(fullPath)

    if (route.children) {
      paths.push(...getAllRoutePaths(route.children, fullPath))
    }
  }

  return paths
}

/**
 * 验证订单管理相关路由
 */
export function validateOrderRoutes(routes: RouteRecordRaw[]): {
  hasOrderManagement: boolean
  hasOrderTest: boolean
  orderRouteConfig: RouteRecordRaw | null
  issues: string[]
} {
  const issues: string[] = []
  
  // 检查订单管理路由
  const orderRoute = findRoute(routes, '/admin/business/orders')
  const hasOrderManagement = !!orderRoute
  
  if (!hasOrderManagement) {
    issues.push('缺少订单管理路由: /admin/business/orders')
  } else {
    // 验证订单管理路由配置
    if (!orderRoute?.name) {
      issues.push('订单管理路由缺少名称')
    }
    
    if (!orderRoute?.meta?.title) {
      issues.push('订单管理路由缺少标题')
    }
    
    if (!orderRoute?.meta?.requiresAuth) {
      issues.push('订单管理路由应该需要认证')
    }
    
    if (!orderRoute?.meta?.permissions) {
      issues.push('订单管理路由缺少权限配置')
    }
  }
  
  // 检查测试路由
  const testRoute = findRoute(routes, '/test/order-api')
  const hasOrderTest = !!testRoute
  
  if (!hasOrderTest) {
    issues.push('缺少订单API测试路由: /test/order-api')
  }

  return {
    hasOrderManagement,
    hasOrderTest,
    orderRouteConfig: orderRoute,
    issues
  }
}

// 创建默认验证器实例
export const routeValidator = new RouteValidator()

// 导出验证工具
export const routeValidationUtils = {
  validator: routeValidator,
  extractRouteInfo,
  checkRouteExists,
  findRoute,
  getAllRoutePaths,
  validateOrderRoutes
}

/**
 * 路由测试工具
 */
export class RouteTestUtils {
  /**
   * 测试路由导航
   */
  static async testNavigation(router: any, path: string): Promise<boolean> {
    try {
      await router.push(path)
      return true
    } catch (error) {
      console.error(`导航到 ${path} 失败:`, error)
      return false
    }
  }

  /**
   * 测试路由权限
   */
  static testRoutePermission(route: any, userPermissions: string[]): boolean {
    if (!route.meta?.permissions) {
      return true // 没有权限要求的路由默认允许访问
    }

    const requiredPermissions = route.meta.permissions as string[]
    return requiredPermissions.some(permission => 
      userPermissions.includes(permission)
    )
  }

  /**
   * 生成路由测试报告
   */
  static generateTestReport(validator: RouteValidator): string {
    const result = validator.validateAll()
    const routeInfo = validator.getAllRouteInfo()
    
    let report = '# 路由配置测试报告\n\n'
    
    // 验证结果摘要
    report += '## 验证结果摘要\n\n'
    report += `- 验证状态: ${result.isValid ? '✅ 通过' : '❌ 失败'}\n`
    report += `- 总路由数: ${result.summary.totalRoutes}\n`
    report += `- 受保护路由: ${result.summary.protectedRoutes}\n`
    report += `- 公开路由: ${result.summary.publicRoutes}\n`
    report += `- 有权限配置的路由: ${result.summary.routesWithPermissions}\n\n`
    
    // 错误信息
    if (result.errors.length > 0) {
      report += '## 错误信息\n\n'
      result.errors.forEach(error => {
        report += `- ❌ ${error}\n`
      })
      report += '\n'
    }
    
    // 警告信息
    if (result.warnings.length > 0) {
      report += '## 警告信息\n\n'
      result.warnings.forEach(warning => {
        report += `- ⚠️ ${warning}\n`
      })
      report += '\n'
    }
    
    // 业务管理路由详情
    const businessRoutes = validator.getBusinessRouteInfo()
    if (businessRoutes.length > 0) {
      report += '## 业务管理路由详情\n\n'
      businessRoutes.forEach(route => {
        report += `### ${route.title || route.name}\n`
        report += `- 路径: ${route.path}\n`
        report += `- 名称: ${route.name}\n`
        report += `- 需要认证: ${route.requiresAuth ? '是' : '否'}\n`
        report += `- 权限要求: ${route.permissions.join(', ') || '无'}\n\n`
      })
    }
    
    return report
  }
}

// 导出工具函数
export const routeUtils = {
  validator: RouteValidator,
  testUtils: RouteTestUtils
}
