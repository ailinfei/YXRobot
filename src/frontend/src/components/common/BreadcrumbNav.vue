<template>
  <div class="breadcrumb-nav">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>
        <router-link to="/admin/dashboard" class="breadcrumb-link">
          <el-icon><House /></el-icon>
          控制台
        </router-link>
      </el-breadcrumb-item>
      
      <el-breadcrumb-item v-if="breadcrumb.parent">
        <span class="breadcrumb-parent">{{ breadcrumb.parent }}</span>
      </el-breadcrumb-item>
      
      <el-breadcrumb-item v-if="breadcrumb.current">
        <span class="breadcrumb-current">{{ breadcrumb.current }}</span>
      </el-breadcrumb-item>
    </el-breadcrumb>
    
    <!-- 页面标题 -->
    <h1 class="page-title">{{ pageTitle }}</h1>
    
    <!-- 页面描述 -->
    <p v-if="pageDescription" class="page-description">{{ pageDescription }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { House } from '@element-plus/icons-vue'

interface Props {
  title?: string
  description?: string
  customBreadcrumb?: {
    parent?: string
    current?: string
  }
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  description: '',
  customBreadcrumb: undefined
})

const route = useRoute()

// 菜单项配置 - 与AdminLayout保持一致
const menuItems = [
  {
    path: '/admin/dashboard',
    title: '数据看板'
  },
  {
    path: '/admin/content',
    title: '官网管理',
    children: [
      { path: '/admin/content/products', title: '产品管理' },
      { path: '/admin/content/charity', title: '公益项目管理' },
      { path: '/admin/content/platforms', title: '平台链接管理' },
      { path: '/admin/content/news', title: '新闻管理' }
    ]
  },
  {
    path: '/admin/business',
    title: '业务管理',
    children: [
      { path: '/admin/business/sales', title: '销售数据' },
      { path: '/admin/business/rental-analytics', title: '租赁数据分析' },
      { path: '/admin/business/customers', title: '客户管理' },
      { path: '/admin/business/orders', title: '订单管理' }
    ]
  },
  {
    path: '/admin/device',
    title: '设备管理',
    children: [
      { path: '/admin/device/management', title: '设备管理' },
      { path: '/admin/device/monitoring', title: '设备监控' },
      { path: '/admin/device/firmware', title: '固件管理' }
    ]
  },
  {
    path: '/admin/system',
    title: '课程管理',
    children: [
      { path: '/admin/system/courses', title: '课程管理' },
      { path: '/admin/system/fonts', title: 'AI字体包管理' },
      { path: '/admin/system/languages', title: '多语言管理' }
    ]
  }
]

// 面包屑导航
const breadcrumb = computed(() => {
  // 如果有自定义面包屑，使用自定义的
  if (props.customBreadcrumb) {
    return props.customBreadcrumb
  }

  // 查找当前路径对应的面包屑
  for (const item of menuItems) {
    if (item.children) {
      const child = item.children.find(child => child.path === route.path)
      if (child) {
        return {
          parent: item.title,
          current: child.title
        }
      }
    }
  }
  
  // 如果是主菜单项
  const currentItem = menuItems.find(item => item.path === route.path)
  if (currentItem) {
    return {
      parent: null,
      current: currentItem.title
    }
  }
  
  // 特殊路由处理
  if (route.path.startsWith('/test/')) {
    return {
      parent: '测试工具',
      current: route.meta.title as string || '测试页面'
    }
  }
  
  return {
    parent: null,
    current: route.meta.title as string || '未知页面'
  }
})

// 页面标题
const pageTitle = computed(() => {
  if (props.title) {
    return props.title
  }
  
  return breadcrumb.value.current || route.meta.title as string || '控制面板'
})

// 页面描述
const pageDescription = computed(() => {
  if (props.description) {
    return props.description
  }
  
  // 根据路由提供默认描述
  const descriptions: Record<string, string> = {
    '/admin/dashboard': '查看系统整体运营数据和关键指标',
    '/admin/business/orders': '管理销售订单和租赁订单，跟踪订单状态',
    '/admin/business/customers': '管理客户信息和设备关联关系',
    '/admin/business/sales': '查看销售数据分析和趋势报告',
    '/admin/business/rental-analytics': '查看租赁业务数据和分析报告',
    '/admin/content/products': '管理产品信息、图片和规格参数',
    '/admin/content/news': '管理新闻文章和发布状态',
    '/test/order-api': '测试订单相关API接口功能',
    '/test/route-config': '验证路由配置和导航功能'
  }
  
  return descriptions[route.path] || ''
})
</script>

<style lang="scss" scoped>
.breadcrumb-nav {
  margin-bottom: 24px;
  
  .page-breadcrumb {
    margin-bottom: 12px;
    
    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: var(--text-secondary);
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 4px;
        
        &.is-link {
          color: var(--primary-color);
          transition: color 0.2s ease;
          
          &:hover {
            color: var(--primary-dark);
          }
        }
      }
      
      &:last-child .el-breadcrumb__inner {
        color: var(--text-light);
        font-weight: 500;
      }
    }
  }
  
  .breadcrumb-link {
    color: var(--primary-color);
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: color 0.2s ease;
    
    &:hover {
      color: var(--primary-dark);
    }
  }
  
  .breadcrumb-parent {
    color: var(--text-secondary);
  }
  
  .breadcrumb-current {
    color: var(--text-light);
    font-weight: 500;
  }
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
    line-height: 1.2;
  }
  
  .page-description {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.4;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .breadcrumb-nav {
    margin-bottom: 16px;
    
    .page-title {
      font-size: 20px;
    }
    
    .page-description {
      font-size: 13px;
    }
  }
}
</style>