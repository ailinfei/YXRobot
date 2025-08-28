/**
 * 权限配置文件
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

/**
 * 新闻管理权限定义
 */
export const NEWS_PERMISSIONS = {
    // 基础权限
    VIEW: 'news:view',
    CREATE: 'news:create',
    EDIT: 'news:edit',
    DELETE: 'news:delete',

    // 状态管理权限
    PUBLISH: 'news:publish',
    OFFLINE: 'news:offline',
    BATCH_OPERATION: 'news:batch',

    // 分类管理权限
    CATEGORY_VIEW: 'news:category:view',
    CATEGORY_CREATE: 'news:category:create',
    CATEGORY_EDIT: 'news:category:edit',
    CATEGORY_DELETE: 'news:category:delete',
    CATEGORY_MANAGE: 'news:category:manage',

    // 标签管理权限
    TAG_VIEW: 'news:tag:view',
    TAG_CREATE: 'news:tag:create',
    TAG_EDIT: 'news:tag:edit',
    TAG_DELETE: 'news:tag:delete',
    TAG_MANAGE: 'news:tag:manage',

    // 统计权限
    STATS_VIEW: 'news:stats:view',
    STATS_EXPORT: 'news:stats:export',
    STATS: 'news:stats',

    // 文件上传权限
    UPLOAD_IMAGE: 'news:upload:image',
    UPLOAD_ATTACHMENT: 'news:upload:attachment',
    UPLOAD: 'news:upload',

    // 高级权限
    ADMIN: 'news:admin',
    ALL: 'news:*'
} as const

/**
 * 系统权限定义
 */
export const SYSTEM_PERMISSIONS = {
    // 仪表板权限
    DASHBOARD_VIEW: 'dashboard:view',

    // 内容管理权限
    CONTENT_VIEW: 'content:view',
    CONTENT_MANAGE: 'content:manage',

    // 产品管理权限
    PRODUCT_VIEW: 'product:view',
    PRODUCT_MANAGE: 'product:manage',

    // 公益项目权限
    CHARITY_VIEW: 'charity:view',
    CHARITY_MANAGE: 'charity:manage',

    // 平台链接权限
    PLATFORM_VIEW: 'platform:view',
    PLATFORM_MANAGE: 'platform:manage',

    // 业务管理权限
    BUSINESS_VIEW: 'business:view',
    SALES_VIEW: 'sales:view',
    RENTAL_VIEW: 'rental:view',
    CUSTOMER_VIEW: 'customer:view',
    ORDER_VIEW: 'order:view',

    // 设备管理权限
    DEVICE_VIEW: 'device:view',
    DEVICE_MANAGE: 'device:manage',
    DEVICE_MONITOR: 'device:monitor',
    FIRMWARE_MANAGE: 'firmware:manage',

    // 系统管理权限
    SYSTEM_VIEW: 'system:view',
    COURSE_MANAGE: 'course:manage',
    FONT_MANAGE: 'font:manage',
    LANGUAGE_MANAGE: 'language:manage',

    // 超级管理员权限
    ADMIN_ALL: 'admin:all'
} as const

/**
 * 权限组定义
 */
// 先定义基础权限组
const NEWS_ADMIN_PERMISSIONS = [
    NEWS_PERMISSIONS.VIEW,
    NEWS_PERMISSIONS.CREATE,
    NEWS_PERMISSIONS.EDIT,
    NEWS_PERMISSIONS.DELETE,
    NEWS_PERMISSIONS.PUBLISH,
    NEWS_PERMISSIONS.OFFLINE,
    NEWS_PERMISSIONS.BATCH_OPERATION,
    NEWS_PERMISSIONS.CATEGORY_MANAGE,
    NEWS_PERMISSIONS.TAG_MANAGE,
    NEWS_PERMISSIONS.STATS,
    NEWS_PERMISSIONS.UPLOAD
] as const

const NEWS_EDITOR_PERMISSIONS = [
    NEWS_PERMISSIONS.VIEW,
    NEWS_PERMISSIONS.CREATE,
    NEWS_PERMISSIONS.EDIT,
    NEWS_PERMISSIONS.CATEGORY_VIEW,
    NEWS_PERMISSIONS.TAG_VIEW,
    NEWS_PERMISSIONS.UPLOAD_IMAGE
] as const

const NEWS_REVIEWER_PERMISSIONS = [
    NEWS_PERMISSIONS.VIEW,
    NEWS_PERMISSIONS.PUBLISH,
    NEWS_PERMISSIONS.OFFLINE,
    NEWS_PERMISSIONS.CATEGORY_VIEW,
    NEWS_PERMISSIONS.TAG_VIEW
] as const

const CONTENT_ADMIN_PERMISSIONS = [
    SYSTEM_PERMISSIONS.CONTENT_VIEW,
    SYSTEM_PERMISSIONS.CONTENT_MANAGE,
    SYSTEM_PERMISSIONS.PRODUCT_MANAGE,
    SYSTEM_PERMISSIONS.CHARITY_MANAGE,
    SYSTEM_PERMISSIONS.PLATFORM_MANAGE,
    ...NEWS_ADMIN_PERMISSIONS
] as const

const SYSTEM_ADMIN_PERMISSIONS = [
    SYSTEM_PERMISSIONS.ADMIN_ALL
] as const

export const PERMISSION_GROUPS = {
    // 新闻管理员
    NEWS_ADMIN: NEWS_ADMIN_PERMISSIONS,

    // 新闻编辑
    NEWS_EDITOR: NEWS_EDITOR_PERMISSIONS,

    // 新闻审核员
    NEWS_REVIEWER: NEWS_REVIEWER_PERMISSIONS,

    // 内容管理员
    CONTENT_ADMIN: CONTENT_ADMIN_PERMISSIONS,

    // 系统管理员
    SYSTEM_ADMIN: SYSTEM_ADMIN_PERMISSIONS
} as const

/**
 * 角色权限映射
 */
export const ROLE_PERMISSIONS: Record<string, readonly string[]> = {
    'super_admin': [SYSTEM_PERMISSIONS.ADMIN_ALL],
    'admin': PERMISSION_GROUPS.CONTENT_ADMIN,
    'news_admin': PERMISSION_GROUPS.NEWS_ADMIN,
    'news_editor': PERMISSION_GROUPS.NEWS_EDITOR,
    'news_reviewer': PERMISSION_GROUPS.NEWS_REVIEWER,
    'viewer': [
        SYSTEM_PERMISSIONS.DASHBOARD_VIEW,
        SYSTEM_PERMISSIONS.CONTENT_VIEW,
        NEWS_PERMISSIONS.VIEW,
        NEWS_PERMISSIONS.CATEGORY_VIEW,
        NEWS_PERMISSIONS.TAG_VIEW
    ] as const
} as const

/**
 * 权限检查函数
 */
export class PermissionChecker {
    private userPermissions: string[] = []

    constructor(userPermissions: string[] = []) {
        this.userPermissions = userPermissions
    }

    /**
     * 设置用户权限
     */
    setPermissions(permissions: string[]) {
        this.userPermissions = permissions
    }

    /**
     * 检查单个权限
     */
    hasPermission(permission: string): boolean {
        // 超级管理员拥有所有权限
        if (this.userPermissions.includes(SYSTEM_PERMISSIONS.ADMIN_ALL)) {
            return true
        }

        // 检查具体权限
        return this.userPermissions.includes(permission)
    }

    /**
     * 检查多个权限（任一满足）
     */
    hasAnyPermission(permissions: string[]): boolean {
        return permissions.some(permission => this.hasPermission(permission))
    }

    /**
     * 检查多个权限（全部满足）
     */
    hasAllPermissions(permissions: string[]): boolean {
        return permissions.every(permission => this.hasPermission(permission))
    }

    /**
     * 检查新闻相关权限
     */
    canViewNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.VIEW)
    }

    canCreateNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.CREATE)
    }

    canEditNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.EDIT)
    }

    canDeleteNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.DELETE)
    }

    canPublishNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.PUBLISH)
    }

    canOfflineNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.OFFLINE)
    }

    canBatchOperateNews(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.BATCH_OPERATION)
    }

    canManageNewsCategory(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.CATEGORY_MANAGE)
    }

    canManageNewsTag(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.TAG_MANAGE)
    }

    canViewNewsStats(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.STATS)
    }

    canUploadNewsFile(): boolean {
        return this.hasPermission(NEWS_PERMISSIONS.UPLOAD)
    }
}

/**
 * 权限工具函数
 */
export const PermissionUtils = {
    /**
     * 根据角色获取权限列表
     */
    getPermissionsByRole(role: string): string[] {
        const permissions = ROLE_PERMISSIONS[role]
        return permissions ? [...permissions] : []
    },

    /**
     * 检查用户是否有访问路由的权限
     */
    canAccessRoute(route: any, userPermissions: string[]): boolean {
        const requiredPermissions = route.meta?.permissions || []
        if (requiredPermissions.length === 0) {
            return true
        }

        const checker = new PermissionChecker(userPermissions)
        return checker.hasAnyPermission(requiredPermissions)
    },

    /**
     * 过滤用户可访问的菜单项
     */
    filterAccessibleMenuItems(menuItems: any[], userPermissions: string[]): any[] {
        const checker = new PermissionChecker(userPermissions)

        return menuItems.filter(item => {
            // 检查当前菜单项权限
            if (item.permissions && item.permissions.length > 0) {
                if (!checker.hasAnyPermission(item.permissions)) {
                    return false
                }
            }

            // 递归过滤子菜单
            if (item.children && item.children.length > 0) {
                item.children = this.filterAccessibleMenuItems(item.children, userPermissions)
                return item.children.length > 0
            }

            return true
        })
    },

    /**
     * 获取权限描述
     */
    getPermissionDescription(permission: string): string {
        const descriptions: Record<string, string> = {
            [NEWS_PERMISSIONS.VIEW]: '查看新闻',
            [NEWS_PERMISSIONS.CREATE]: '创建新闻',
            [NEWS_PERMISSIONS.EDIT]: '编辑新闻',
            [NEWS_PERMISSIONS.DELETE]: '删除新闻',
            [NEWS_PERMISSIONS.PUBLISH]: '发布新闻',
            [NEWS_PERMISSIONS.OFFLINE]: '下线新闻',
            [NEWS_PERMISSIONS.BATCH_OPERATION]: '批量操作新闻',
            [NEWS_PERMISSIONS.CATEGORY_MANAGE]: '管理新闻分类',
            [NEWS_PERMISSIONS.TAG_MANAGE]: '管理新闻标签',
            [NEWS_PERMISSIONS.STATS]: '查看新闻统计',
            [NEWS_PERMISSIONS.UPLOAD]: '上传文件',
            [SYSTEM_PERMISSIONS.ADMIN_ALL]: '系统管理员权限'
        }

        return descriptions[permission] || permission
    }
}

/**
 * 创建权限检查器实例
 */
export const createPermissionChecker = (userPermissions: string[] = []) => {
    return new PermissionChecker(userPermissions)
}

/**
 * 默认权限检查器（需要在用户登录后更新权限）
 */
export const permissionChecker = new PermissionChecker()