<template>
  <div class="route-test">
    <div class="page-header">
      <h2>路由测试页面</h2>
      <p>测试订单管理路由和导航功能</p>
    </div>

    <div class="test-sections">
      <!-- 路由信息 -->
      <div class="test-section">
        <h3>当前路由信息</h3>
        <div class="info-card">
          <div class="info-item">
            <label>路由路径:</label>
            <span>{{ $route.path }}</span>
          </div>
          <div class="info-item">
            <label>路由名称:</label>
            <span>{{ $route.name }}</span>
          </div>
          <div class="info-item">
            <label>页面标题:</label>
            <span>{{ $route.meta.title }}</span>
          </div>
          <div class="info-item">
            <label>需要认证:</label>
            <span>{{ $route.meta.requiresAuth ? '是' : '否' }}</span>
          </div>
          <div class="info-item">
            <label>权限要求:</label>
            <span>{{ $route.meta.permissions ? $route.meta.permissions.join(', ') : '无' }}</span>
          </div>
        </div>
      </div>

      <!-- 导航测试 -->
      <div class="test-section">
        <h3>导航测试</h3>
        <div class="nav-buttons">
          <el-button 
            type="primary" 
            @click="navigateTo('/admin/business/orders')"
          >
            跳转到订单管理
          </el-button>
          <el-button 
            type="success" 
            @click="navigateTo('/admin/business/customers')"
          >
            跳转到客户管理
          </el-button>
          <el-button 
            type="info" 
            @click="navigateTo('/admin/business/sales')"
          >
            跳转到销售数据
          </el-button>
          <el-button 
            type="warning" 
            @click="navigateTo('/admin/dashboard')"
          >
            跳转到数据看板
          </el-button>
        </div>
      </div>

      <!-- 面包屑测试 -->
      <div class="test-section">
        <h3>面包屑导航测试</h3>
        <div class="breadcrumb-demo">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>
              <router-link to="/admin/dashboard">控制台</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item>业务管理</el-breadcrumb-item>
            <el-breadcrumb-item>订单管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <!-- 权限测试 -->
      <div class="test-section">
        <h3>权限测试</h3>
        <div class="permission-info">
          <div class="info-item">
            <label>当前用户:</label>
            <span>{{ userInfo.name || '未登录' }}</span>
          </div>
          <div class="info-item">
            <label>用户权限:</label>
            <div class="permissions-list">
              <el-tag 
                v-for="permission in userInfo.permissions" 
                :key="permission"
                size="small"
                style="margin: 2px;"
              >
                {{ permission }}
              </el-tag>
            </div>
          </div>
          <div class="info-item">
            <label>订单管理权限:</label>
            <el-tag 
              :type="hasOrderPermission ? 'success' : 'danger'"
              size="small"
            >
              {{ hasOrderPermission ? '有权限' : '无权限' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 路由历史 -->
      <div class="test-section">
        <h3>路由历史</h3>
        <div class="route-history">
          <div 
            v-for="(route, index) in routeHistory" 
            :key="index"
            class="history-item"
          >
            <span class="history-time">{{ route.time }}</span>
            <span class="history-path">{{ route.path }}</span>
            <span class="history-title">{{ route.title }}</span>
          </div>
        </div>
      </div>

      <!-- 测试结果 -->
      <div class="test-section">
        <h3>测试结果</h3>
        <div class="test-results">
          <div class="result-item" :class="{ success: testResults.routeConfig }">
            <el-icon><Check v-if="testResults.routeConfig" /><Close v-else /></el-icon>
            <span>路由配置正确</span>
          </div>
          <div class="result-item" :class="{ success: testResults.navigation }">
            <el-icon><Check v-if="testResults.navigation" /><Close v-else /></el-icon>
            <span>导航功能正常</span>
          </div>
          <div class="result-item" :class="{ success: testResults.permissions }">
            <el-icon><Check v-if="testResults.permissions" /><Close v-else /></el-icon>
            <span>权限检查正确</span>
          </div>
          <div class="result-item" :class="{ success: testResults.breadcrumb }">
            <el-icon><Check v-if="testResults.breadcrumb" /><Close v-else /></el-icon>
            <span>面包屑导航正确</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '测试用户',
  permissions: ['admin:all', 'business:order:manage', 'business:manage']
})

// 路由历史
const routeHistory = ref<Array<{ time: string; path: string; title: string }>>([])

// 测试结果
const testResults = ref({
  routeConfig: false,
  navigation: false,
  permissions: false,
  breadcrumb: false
})

// 检查订单管理权限
const hasOrderPermission = computed(() => {
  const requiredPermissions = ['business:order:manage', 'business:manage', 'admin:all']
  return requiredPermissions.some(permission => 
    userInfo.value.permissions.includes(permission)
  )
})

// 导航到指定路径
const navigateTo = async (path: string) => {
  try {
    await router.push(path)
    ElMessage.success(`成功跳转到 ${path}`)
    testResults.value.navigation = true
  } catch (error) {
    ElMessage.error(`跳转失败: ${error}`)
    testResults.value.navigation = false
  }
}

// 记录路由历史
const recordRoute = () => {
  const now = new Date()
  const timeString = now.toLocaleTimeString()
  
  routeHistory.value.unshift({
    time: timeString,
    path: route.path,
    title: route.meta.title as string || '未知'
  })
  
  // 只保留最近10条记录
  if (routeHistory.value.length > 10) {
    routeHistory.value = routeHistory.value.slice(0, 10)
  }
}

// 监听路由变化
watch(route, () => {
  recordRoute()
}, { immediate: true })

onMounted(() => {
  console.log('路由测试页面已加载')
  console.log('当前路由:', route)
})
</script>

<style lang="scss" scoped>
.route-test {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
  
  h2 {
    color: #262626;
    margin-bottom: 8px;
  }
  
  p {
    color: #8c8c8c;
    font-size: 14px;
  }
}

.test-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  
  h3 {
    color: #262626;
    margin-bottom: 16px;
    font-size: 18px;
    border-bottom: 2px solid #1890ff;
    padding-bottom: 8px;
  }
}

.nav-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.info-card,
.permission-info {
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    label {
      font-weight: 500;
      color: #262626;
      min-width: 120px;
      margin-right: 12px;
    }
    
    span {
      color: #8c8c8c;
      
      &.success {
        color: #52c41a;
        font-weight: 500;
      }
      
      &.error {
        color: #ff4d4f;
        font-weight: 500;
      }
    }
  }
}

.breadcrumb-demo {
  :deep(.el-breadcrumb) {
    font-size: 14px;
    
    .el-breadcrumb__item {
      .el-breadcrumb__inner {
        color: #8c8c8c;
        
        &.is-link {
          color: #1890ff;
          
          &:hover {
            color: #40a9ff;
          }
        }
      }
      
      &:last-child .el-breadcrumb__inner {
        color: #bfbfbf;
      }
    }
  }
}

.route-history {
  max-height: 300px;
  overflow-y: auto;
  
  .history-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    border-radius: 4px;
    margin-bottom: 4px;
    background: #fafafa;
    
    .history-time {
      color: #bfbfbf;
      font-size: 12px;
      min-width: 80px;
      margin-right: 12px;
    }
    
    .history-path {
      color: #1890ff;
      font-family: monospace;
      margin-right: 12px;
      flex: 1;
    }
    
    .history-title {
      color: #8c8c8c;
      font-size: 12px;
    }
  }
}

.test-results {
  .result-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    padding: 8px 12px;
    border-radius: 4px;
    background: #fafafa;
    
    .el-icon {
      margin-right: 8px;
      font-size: 16px;
    }
    
    span {
      color: #8c8c8c;
    }
    
    &.success {
      background: #f6ffed;
      
      .el-icon {
        color: #52c41a;
      }
      
      span {
        color: #262626;
      }
    }
    
    &:not(.success) {
      background: #fff2f0;
      
      .el-icon {
        color: #ff4d4f;
      }
      
      span {
        color: #262626;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .route-test {
    padding: 16px;
  }
  
  .test-section {
    padding: 16px;
  }
  
  .nav-buttons {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
  
  .info-card,
  .permission-info {
    .info-item {
      flex-direction: column;
      align-items: flex-start;
      
      label {
        min-width: auto;
        margin-right: 0;
        margin-bottom: 4px;
      }
    }
  }
}
</style>
