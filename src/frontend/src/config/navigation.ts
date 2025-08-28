/**
 * 导航配置文件
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import {
    DataAnalysis,
    Document,
    ShoppingCart,
    User,
    Monitor,
    Tools,
    Setting,
    ChatDotRound,
    EditPen,
    Collection,
    PriceTag
} from '@element-plus/icons-vue'

/**
 * 主导航菜单配置
 */
export const mainNavigationConfig = [
    {
        path: '/admin/dashboard',
        title: '数据看板',
        icon: DataAnalysis,
        category: 'main',
        permissions: ['dashboard:view']
    },
    {
        path: '/admin/content',
        title: '官网管理',
        icon: Document,
        category: 'main',
        permissions: ['content:view'],
        children: [
            {
                path: '/admin/content/products',
                title: '产品管理',
                icon: ShoppingCart,
                permissions: ['product:view']
            },
            {
                path: '/admin/content/charity',
                title: '公益项目管理',
                icon: User,
                permissions: ['charity:view']
            },
            {
                path: '/admin/content/platforms',
                title: '平台链接管理',
                icon: Monitor,
                permissions: ['platform:view']
            },
            {
                path: '/admin/content/news',
                title: '新闻管理',
                icon: Document,
                permissions: ['news:view'],
                children: [
                    {
                        path: '/admin/content/news',
                        title: '新闻列表',
                        icon: Document,
                        permissions: ['news:view']
                    },
                    {
                        path: '/admin/content/news/create',
                        title: '创建新闻',
                        icon: EditPen,
                        permissions: ['news:create']
                    },
                    {
                        path: '/admin/content/news/categories',
                        title: '分类管理',
                        icon: Collection,
                        permissions: ['news:category:manage']
                    },
                    {
                        path: '/admin/content/news/tags',
                        title: '标签管理',
                        icon: PriceTag,
                        permissions: ['news:tag:manage']
                    },
                    {
                        path: '/admin/content/news/stats',
                        title: '统计分析',
                        icon: DataAnalysis,
                        permissions: ['news:stats']
                    }
                ]
            }
        ]
    },
    {
        path: '/admin/business',
        title: '业务管理',
        icon: ShoppingCart,
        category: 'main',
        permissions: ['business:view'],
        children: [
            {
                path: '/admin/business/sales',
                title: '销售数据',
                icon: DataAnalysis,
                permissions: ['sales:view']
            },
            {
                path: '/admin/business/rental',
                title: '租赁数据',
                icon: Monitor,
                permissions: ['rental:view']
            },
            {
                path: '/admin/business/customers',
                title: '客户管理',
                icon: User,
                permissions: ['customer:view']
            },
            {
                path: '/admin/business/orders',
                title: '订单管理',
                icon: Document,
                permissions: ['order:view']
            }
        ]
    },
    {
        path: '/admin/device',
        title: '设备管理',
        icon: Monitor,
        category: 'main',
        permissions: ['device:view'],
        children: [
            {
                path: '/admin/device/management',
                title: '设备管理',
                icon: Tools,
                permissions: ['device:manage']
            },
            {
                path: '/admin/device/monitoring',
                title: '设备监控',
                icon: DataAnalysis,
                permissions: ['device:monitor']
            },
            {
                path: '/admin/device/firmware',
                title: '固件管理',
                icon: Setting,
                permissions: ['firmware:manage']
            }
        ]
    },
    {
        path: '/admin/system',
        title: '课程管理',
        icon: Tools,
        category: 'main',
        permissions: ['system:view'],
        children: [
            {
                path: '/admin/system/courses',
                title: '课程管理',
                icon: Document,
                permissions: ['course:manage']
            },
            {
                path: '/admin/system/fonts',
                title: 'AI字体包管理',
                icon: Setting,
                permissions: ['font:manage']
            },
            {
                path: '/admin/system/languages',
                title: '多语言管理',
                icon: ChatDotRound,
                permissions: ['language:manage']
            }
        ]
    }
]

/**
 * 前台网站导航配置
 */
export const websiteNavigationConfig = [
    {
        path: '/website/home',
        title: '首页',
        icon: 'House'
    },
    {
        path: '/website/products',
        title: '产品中心',
        icon: 'ShoppingCart'
    },
    {
        path: '/website/charity',
        title: '公益项目',
        icon: 'User'
    },
    {
        path: '/website/news',
        title: '新闻中心',
        icon: 'Document',
        children: [
            {
                path: '/website/news',
                title: '全部新闻'
            },
            {
                path: '/website/news/category/1',
                title: '产品动态'
            },
            {
                path: '/website/news/category/2',
                title: '公司新闻'
            },
            {
                path: '/website/news/category/3',
                title: '行业资讯'
            }
        ]
    },
    {
        path: '/website/support',
        title: '技术支持',
        icon: 'Tools'
    },
    {
        path: '/website/contact',
        title: '联系我们',
        icon: 'Phone'
    }
]

/**
 * 面包屑项目类型
 */
export interface BreadcrumbItem {
    title: string
    path: string
}

/**
 * 面包屑配置类型
 */
export interface BreadcrumbConfig {
    [key: string]: BreadcrumbItem[]
}

/**
 * 面包屑导航配置
 */
export const breadcrumbConfig: {
    admin: BreadcrumbConfig
    website: BreadcrumbConfig
} = {
    // 管理后台面包屑
    admin: {
        '/admin/dashboard': [
            { title: '控制台', path: '/admin/dashboard' }
        ],
        '/admin/content/news': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' }
        ],
        '/admin/content/news/create': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' },
            { title: '创建新闻', path: '/admin/content/news/create' }
        ],
        '/admin/content/news/edit/:id': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' },
            { title: '编辑新闻', path: '' }
        ],
        '/admin/content/news/categories': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' },
            { title: '分类管理', path: '/admin/content/news/categories' }
        ],
        '/admin/content/news/tags': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' },
            { title: '标签管理', path: '/admin/content/news/tags' }
        ],
        '/admin/content/news/stats': [
            { title: '控制台', path: '/admin/dashboard' },
            { title: '官网管理', path: '/admin/content' },
            { title: '新闻管理', path: '/admin/content/news' },
            { title: '统计分析', path: '/admin/content/news/stats' }
        ]
    },

    // 前台网站面包屑
    website: {
        '/website/home': [
            { title: '首页', path: '/website/home' }
        ],
        '/website/news': [
            { title: '首页', path: '/website/home' },
            { title: '新闻中心', path: '/website/news' }
        ],
        '/website/news/:id': [
            { title: '首页', path: '/website/home' },
            { title: '新闻中心', path: '/website/news' },
            { title: '新闻详情', path: '' }
        ],
        '/website/news/category/:categoryId': [
            { title: '首页', path: '/website/home' },
            { title: '新闻中心', path: '/website/news' },
            { title: '分类新闻', path: '' }
        ],
        '/website/news/tag/:tagId': [
            { title: '首页', path: '/website/home' },
            { title: '新闻中心', path: '/website/news' },
            { title: '标签新闻', path: '' }
        ]
    }
}

/**
 * 快捷操作配置
 */
export const quickActionsConfig = [
    {
        title: '创建新闻',
        path: '/admin/content/news/create',
        icon: 'EditPen',
        permissions: ['news:create'],
        description: '快速创建新闻文章'
    },
    {
        title: '新闻统计',
        path: '/admin/content/news/stats',
        icon: 'DataAnalysis',
        permissions: ['news:stats'],
        description: '查看新闻数据统计'
    },
    {
        title: '分类管理',
        path: '/admin/content/news/categories',
        icon: 'Collection',
        permissions: ['news:category:manage'],
        description: '管理新闻分类'
    },
    {
        title: '标签管理',
        path: '/admin/content/news/tags',
        icon: 'PriceTag',
        permissions: ['news:tag:manage'],
        description: '管理新闻标签'
    }
]

/**
 * 获取面包屑导航
 */
export const getBreadcrumb = (path: string, type: 'admin' | 'website' = 'admin'): BreadcrumbItem[] => {
    const config = breadcrumbConfig[type]

    // 精确匹配
    if (path in config) {
        return config[path]
    }

    // 动态路由匹配
    for (const [pattern, breadcrumb] of Object.entries(config)) {
        if (pattern.includes(':')) {
            const regex = new RegExp('^' + pattern.replace(/:[^/]+/g, '[^/]+') + '$')
            if (regex.test(path)) {
                return breadcrumb
            }
        }
    }

    return []
}

/**
 * 获取页面标题
 */
export const getPageTitle = (path: string, type: 'admin' | 'website' = 'admin') => {
    const breadcrumb = getBreadcrumb(path, type)
    if (breadcrumb.length > 0) {
        return breadcrumb[breadcrumb.length - 1].title
    }
    return '练字机器人管理后台'
}

/**
 * 检查菜单项权限
 */
export const hasMenuPermission = (menuItem: any, userPermissions: string[] = []) => {
    if (!menuItem.permissions || menuItem.permissions.length === 0) {
        return true
    }

    return menuItem.permissions.some((permission: string) =>
        userPermissions.includes(permission) || userPermissions.includes('admin:all')
    )
}

/**
 * 过滤有权限的菜单项
 */
export const filterMenuByPermissions = (menuItems: any[], userPermissions: string[] = []) => {
    return menuItems.filter(item => {
        if (!hasMenuPermission(item, userPermissions)) {
            return false
        }

        if (item.children) {
            item.children = filterMenuByPermissions(item.children, userPermissions)
            return item.children.length > 0
        }

        return true
    })
}