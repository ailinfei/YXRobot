<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'mobile-open': mobileMenuOpen }">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">
            <el-icon><Edit /></el-icon>
          </div>
          <span>练字机器人</span>
        </div>
      </div>
      
      <div class="sidebar-menu">
        <template v-for="item in menuItems" :key="item.path">
          <!-- 有子菜单的项目 1111-->
          <div v-if="item.children" class="menu-group">
            <div 
              class="menu-item parent-item"
              :class="{ 
                active: isParentActive(item),
                expanded: expandedMenus.includes(item.path)
              }"
              @click="toggleMenu(item.path)"
            >
              <div class="menu-icon">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <span>{{ item.title }}</span>
              <el-icon class="expand-icon" :class="{ rotated: expandedMenus.includes(item.path) }">
                <ArrowDown />
              </el-icon>
            </div>
            
            <!-- 子菜单 -->
            <div class="submenu" v-show="expandedMenus.includes(item.path)">
              <router-link 
                v-for="child in item.children" 
                :key="child.path"
                :to="child.path" 
                class="menu-item submenu-item"
                :class="{ active: $route.path === child.path }"
              >
                <div class="menu-icon">
                  <el-icon><component :is="child.icon" /></el-icon>
                </div>
                <span>{{ child.title }}</span>
              </router-link>
            </div>
          </div>
          
          <!-- 没有子菜单的项目 -->
          <router-link 
            v-else
            :to="item.path" 
            class="menu-item"
            :class="{ active: $route.path === item.path }"
          >
            <div class="menu-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <span>{{ item.title }}</span>
          </router-link>
        </template>
      </div>
      
      <!-- 底部升级卡片 -->
      <div class="sidebar-footer">
        <div class="upgrade-card gradient-card">
       
          <h4>练字机器人Pro</h4>
          <p>让每个人写好汉字。</p>
      
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部栏 -->
      <div class="header">
        <div class="header-left">
          <el-button 
            class="mobile-menu-btn"
            @click="toggleMobileMenu"
            :icon="Menu"
            circle
          />
          <!-- 面包屑导航 -->
          <div class="breadcrumb-section">
            <h1 class="header-title">{{ currentPageTitle }}</h1>
            <el-breadcrumb separator="/" class="page-breadcrumb">
              <el-breadcrumb-item>
                <router-link to="/admin/dashboard">控制台</router-link>
              </el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentBreadcrumb.parent">
                {{ currentBreadcrumb.parent }}
              </el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentBreadcrumb.current">
                {{ currentBreadcrumb.current }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>
        
        <div class="header-actions">
          <!-- 搜索框 -->
          <el-input
            v-model="searchQuery"
            placeholder="全局搜索"
            class="search-input"
            :prefix-icon="Search"
            clearable
          />
          
          
          <!-- 用户头像 -->
          <el-dropdown @command="handleUserAction">
            <div class="user-avatar">
              <el-avatar 
                :src="userInfo.avatar"
                size="small"
              />
              <div class="user-info">
                <span class="user-name">{{ userInfo.name }}</span>
                <span class="user-role">{{ userInfo.role }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="account">账户管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="page-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Edit,
  Setting,
  DataAnalysis,
  ShoppingCart,
  User,
  Document,
  Monitor,
  Tools,
  Menu,
  Search,
  Bell,
  ArrowDown,
  Lightning,
  ChatDotRound
} from '@element-plus/icons-vue'
import authService from '@/services/authService'
import permissionService from '@/services/permissionService'

const route = useRoute()
const router = useRouter()
const mobileMenuOpen = ref(false)
const searchQuery = ref('')
const expandedMenus = ref<string[]>([])

// 展开/收起菜单
const toggleMenu = (path: string) => {
  const index = expandedMenus.value.indexOf(path)
  if (index > -1) {
    expandedMenus.value.splice(index, 1)
  } else {
    expandedMenus.value.push(path)
  }
}

// 检查父菜单是否激活
const isParentActive = (item: any) => {
  if (!item.children) return false
  return item.children.some((child: any) => route.path === child.path)
}

// 原始菜单项配置 - 按照任务要求重新组织
const originalMenuItems = [
  {
    path: '/admin/dashboard',
    title: '数据看板',
    icon: DataAnalysis,
    category: 'main'
  },
  {
    path: '/admin/content',
    title: '官网管理',
    icon: Document,
    category: 'main',
    children: [
      { path: '/admin/content/products', title: '产品管理', icon: ShoppingCart },
      { path: '/admin/content/charity', title: '公益项目管理', icon: User },
      { path: '/admin/content/platforms', title: '平台链接管理', icon: Monitor },
      { path: '/admin/content/news', title: '新闻管理', icon: Document }
    ]
  },
  {
    path: '/admin/business',
    title: '业务管理',
    icon: ShoppingCart,
    category: 'main',
    children: [
      { path: '/admin/business/sales', title: '销售数据', icon: DataAnalysis },
      { path: '/admin/business/rental', title: '租赁数据', icon: Monitor },
      { path: '/admin/business/customers', title: '客户管理', icon: User },
      { path: '/admin/business/orders', title: '订单管理', icon: Document }
    ]
  },
  {
    path: '/admin/device',
    title: '设备管理',
    icon: Monitor,
    category: 'main',
    children: [
      { path: '/admin/device/management', title: '设备管理', icon: Tools },
      { path: '/admin/device/monitoring', title: '设备监控', icon: DataAnalysis },
      { path: '/admin/device/firmware', title: '固件管理', icon: Setting }
    ]
  },
  {
    path: '/admin/system',
    title: '课程管理',
    icon: Tools,
    category: 'main',
    children: [
      { path: '/admin/system/courses', title: '课程管理', icon: Document },
      { path: '/admin/system/fonts', title: 'AI字体包管理', icon: Setting },
      { path: '/admin/system/languages', title: '多语言管理', icon: ChatDotRound }
    ]
  }
]

// 根据用户权限过滤菜单项
const menuItems = computed(() => {
  return permissionService.getAccessibleMenuItems(originalMenuItems)
})

// 当前页面标题和面包屑
const currentPageTitle = computed(() => {
  // 先查找子菜单项
  for (const item of originalMenuItems) {
    if (item.children) {
      const child = item.children.find(child => child.path === route.path)
      if (child) {
        return child.title
      }
    }
  }
  // 再查找主菜单项
  const currentItem = originalMenuItems.find(item => item.path === route.path)
  return currentItem?.title || '控制面板'
})

const currentBreadcrumb = computed(() => {
  // 查找当前路径对应的面包屑
  for (const item of originalMenuItems) {
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
  const currentItem = originalMenuItems.find(item => item.path === route.path)
  if (currentItem) {
    return {
      parent: null,
      current: currentItem.title
    }
  }
  
  return {
    parent: null,
    current: null
  }
})

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

// 用户登录状态管理
const userInfo = ref(authService.getCurrentUser() || {
  name: '管理员',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  role: '系统管理员'
})

// 处理用户操作
const handleUserAction = (command: string) => {
  switch (command) {
    case 'profile':
      console.log('打开个人设置')
      // 可以跳转到个人设置页面
      break
    case 'account':
      console.log('打开账户管理')
      // 可以跳转到账户管理页面
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理用户登出
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 使用认证服务登出
    authService.logout()
    ElMessage.success('已退出登录')
    
    // 跳转到登录页面
    router.push('/admin/login')
  } catch {
    // 用户取消登出
  }
}

// 组件挂载时的初始化
onMounted(() => {
  // 刷新用户最后活跃时间
  authService.refreshLastActiveTime()
  
  // 验证用户认证状态
  if (!authService.isAuthenticated()) {
    ElMessage.warning('登录状态已过期，请重新登录')
    router.push('/admin/login')
  }
})
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-secondary);
}

.sidebar {
  background: var(--primary-gradient);
  color: white;
  width: 240px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease;

  .sidebar-header {
    padding: 24px 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    .logo {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      
      .logo-icon {
        width: 32px;
        height: 32px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: var(--radius-md);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
      }
    }
  }
  
  .sidebar-menu {
    flex: 1;
    padding: 20px 0;
    
    .menu-group {
      .parent-item {
        cursor: pointer;
        position: relative;
        
        .expand-icon {
          margin-left: auto;
          transition: transform 0.3s ease;
          
          &.rotated {
            transform: rotate(180deg);
          }
        }
        
        &.expanded {
          background: rgba(255, 255, 255, 0.1);
        }
      }
      
      .submenu {
        background: rgba(0, 0, 0, 0.1);
        
        .submenu-item {
          padding-left: 52px;
          font-size: 14px;
          
          &:hover {
            background: rgba(255, 255, 255, 0.08);
          }
          
          &.active {
            background: rgba(255, 255, 255, 0.12);
            border-right: 3px solid white;
          }
        }
      }
    }
    
    .menu-item {
      display: flex;
      align-items: center;
      padding: 12px 20px;
      color: rgba(255, 255, 255, 0.8);
      text-decoration: none;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: white;
      }
      
      &.active {
        background: rgba(255, 255, 255, 0.15);
        color: white;
        border-right: 3px solid white;
      }
      
      .menu-icon {
        width: 20px;
        height: 20px;
        margin-right: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .sidebar-footer {
    padding: 20px;
    
    .upgrade-card {
      padding: 20px;
      border-radius: var(--radius-lg);
      text-align: center;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      
      .upgrade-icon {
        width: 40px;
        height: 40px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 12px;
        font-size: 20px;
      }
      
      h4 {
        font-size: 16px;
        margin-bottom: 8px;
        color: white;
      }
      
      p {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 16px;
        line-height: 1.4;
      }
      
      .upgrade-btn {
        width: 100%;
        background: white;
        color: var(--primary-color);
        border: none;
        
        &:hover {
          background: rgba(255, 255, 255, 0.9);
        }
      }
    }
  }
}

.main-content {
  margin-left: 240px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background: var(--bg-primary);
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .mobile-menu-btn {
      display: none;
    }
    
    .breadcrumb-section {
      .header-title {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 4px 0;
      }
      
      .page-breadcrumb {
        :deep(.el-breadcrumb__item) {
          .el-breadcrumb__inner {
            color: var(--text-secondary);
            font-size: 12px;
            
            &.is-link {
              color: var(--primary-color);
              
              &:hover {
                color: var(--primary-dark);
              }
            }
          }
          
          &:last-child .el-breadcrumb__inner {
            color: var(--text-light);
          }
        }
      }
    }
  }
  
  .header-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .search-input {
      width: 300px;
    }
    

    
    .notification-badge {
      .el-button {
        border: 1px solid var(--border-color);
        background: transparent;
        color: var(--text-secondary);
        
        &:hover {
          border-color: var(--primary-color);
          color: var(--primary-color);
        }
      }
    }
    
    .user-avatar {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 8px 12px;
      border-radius: var(--radius-md);
      transition: background-color 0.2s ease;
      
      &:hover {
        background: var(--bg-tertiary);
      }
      
      .user-info {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        
        .user-name {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
          line-height: 1.2;
        }
        
        .user-role {
          font-size: 12px;
          color: var(--text-secondary);
          line-height: 1.2;
        }
      }
    }
  }
}

.page-content {
  flex: 1;
  padding: 24px;
  background: var(--bg-secondary);
}

// 响应式设计
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    
    &.mobile-open {
      transform: translateX(0);
    }
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .header {
    .header-left {
      .mobile-menu-btn {
        display: block;
      }
    }
    
    .header-actions {
      .search-input {
        width: 200px;
      }
    }
  }
  
  .page-content {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .header-actions {
    .search-input {
      display: none;
    }
  }
}
</style>