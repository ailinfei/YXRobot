<template>
  <div class="route-config-test">
    <div class="test-header">
      <h2>路由配置验证</h2>
      <p>验证订单管理相关路由配置是否正确</p>
      <div class="test-actions">
        <el-button type="primary" @click="runValidation">
          <el-icon><Refresh /></el-icon>
          重新验证
        </el-button>
        <el-button type="success" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 验证结果概览 -->
    <div class="validation-summary">
      <div class="summary-card" :class="{ success: validationResult.isValid, error: !validationResult.isValid }">
        <div class="summary-icon">
          <el-icon v-if="validationResult.isValid"><SuccessFilled /></el-icon>
          <el-icon v-else><WarningFilled /></el-icon>
        </div>
        <div class="summary-content">
          <h3>{{ validationResult.isValid ? '验证通过' : '验证失败' }}</h3>
          <p>{{ validationResult.isValid ? '所有路由配置正确' : '发现配置问题' }}</p>
        </div>
      </div>

      <div class="summary-stats">
        <div class="stat-item">
          <span class="stat-value">{{ validationResult.summary.totalRoutes }}</span>
          <span class="stat-label">总路由数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ validationResult.summary.validRoutes }}</span>
          <span class="stat-label">有效路由</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ validationResult.summary.routesWithAuth }}</span>
          <span class="stat-label">需要认证</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ validationResult.summary.routesWithPermissions }}</span>
          <span class="stat-label">需要权限</span>
        </div>
      </div>
    </div>

    <!-- 订单路由专项检查 -->
    <div class="test-section">
      <h3>订单管理路由检查</h3>
      <div class="order-route-check">
        <div class="check-item" :class="{ success: orderValidation.hasOrderManagement, error: !orderValidation.hasOrderManagement }">
          <el-icon><component :is="orderValidation.hasOrderManagement ? 'SuccessFilled' : 'CloseBold'" /></el-icon>
          <span>订单管理页面路由 (/admin/business/orders)</span>
        </div>
        <div class="check-item" :class="{ success: orderValidation.hasOrderTest, error: !orderValidation.hasOrderTest }">
          <el-icon><component :is="orderValidation.hasOrderTest ? 'SuccessFilled' : 'CloseBold'" /></el-icon>
          <span>订单API测试路由 (/test/order-api)</span>
        </div>
      </div>

      <!-- 订单路由详细配置 -->
      <div v-if="orderValidation.orderRouteConfig" class="route-details">
        <h4>订单管理路由配置</h4>
        <div class="config-table">
          <div class="config-row">
            <span class="config-label">路径:</span>
            <span class="config-value">{{ orderValidation.orderRouteConfig.path }}</span>
          </div>
          <div class="config-row">
            <span class="config-label">名称:</span>
            <span class="config-value">{{ orderValidation.orderRouteConfig.name || '未设置' }}</span>
          </div>
          <div class="config-row">
            <span class="config-label">标题:</span>
            <span class="config-value">{{ orderValidation.orderRouteConfig.meta?.title || '未设置' }}</span>
          </div>
          <div class="config-row">
            <span class="config-label">需要认证:</span>
            <span class="config-value" :class="{ success: orderValidation.orderRouteConfig.meta?.requiresAuth }">
              {{ orderValidation.orderRouteConfig.meta?.requiresAuth ? '是' : '否' }}
            </span>
          </div>
          <div class="config-row">
            <span class="config-label">权限要求:</span>
            <span class="config-value">
              {{ orderValidation.orderRouteConfig.meta?.permissions?.join(', ') || '无' }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误和警告 -->
    <div v-if="validationResult.errors.length > 0" class="test-section">
      <h3>配置错误</h3>
      <div class="error-list">
        <div v-for="(error, index) in validationResult.errors" :key="index" class="error-item">
          <el-icon><WarningFilled /></el-icon>
          <span>{{ error }}</span>
        </div>
      </div>
    </div>

    <div v-if="validationResult.warnings.length > 0" class="test-section">
      <h3>配置警告</h3>
      <div class="warning-list">
        <div v-for="(warning, index) in validationResult.warnings" :key="index" class="warning-item">
          <el-icon><Warning /></el-icon>
          <span>{{ warning }}</span>
        </div>
      </div>
    </div>

    <!-- 订单路由问题 -->
    <div v-if="orderValidation.issues.length > 0" class="test-section">
      <h3>订单路由问题</h3>
      <div class="issue-list">
        <div v-for="(issue, index) in orderValidation.issues" :key="index" class="issue-item">
          <el-icon><InfoFilled /></el-icon>
          <span>{{ issue }}</span>
        </div>
      </div>
    </div>

    <!-- 路由信息表格 -->
    <div class="test-section">
      <h3>路由配置详情</h3>
      <el-table :data="routeTableData" style="width: 100%" max-height="400">
        <el-table-column prop="path" label="路径" width="200" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="title" label="标题" width="120" />
        <el-table-column prop="component" label="组件" width="150" />
        <el-table-column prop="requiresAuth" label="需要认证" width="100">
          <template #default="{ row }">
            <el-tag :type="row.requiresAuth ? 'success' : 'info'" size="small">
              {{ row.requiresAuth ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissions" label="权限要求" min-width="200">
          <template #default="{ row }">
            <el-tag 
              v-for="permission in row.permissions" 
              :key="permission" 
              size="small" 
              style="margin-right: 4px;"
            >
              {{ permission }}
            </el-tag>
            <span v-if="!row.permissions || row.permissions.length === 0" class="text-muted">无</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 路由测试 -->
    <div class="test-section">
      <h3>路由跳转测试</h3>
      <div class="route-test-buttons">
        <el-button 
          v-for="testRoute in testRoutes" 
          :key="testRoute.path"
          :type="testRoute.type"
          @click="testNavigation(testRoute.path)"
        >
          {{ testRoute.label }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  SuccessFilled,
  WarningFilled,
  Warning,
  InfoFilled,
  CloseBold
} from '@element-plus/icons-vue'
import { routeValidationUtils, type RouteValidationResult } from '@/utils/routeValidator'
import type { RouteRecordRaw } from 'vue-router'

const router = useRouter()

// 验证结果
const validationResult = ref<RouteValidationResult>({
  isValid: true,
  errors: [],
  warnings: [],
  summary: {
    totalRoutes: 0,
    validRoutes: 0,
    invalidRoutes: 0,
    routesWithAuth: 0,
    routesWithPermissions: 0
  }
})

// 订单路由验证结果
const orderValidation = ref({
  hasOrderManagement: false,
  hasOrderTest: false,
  orderRouteConfig: null as RouteRecordRaw | null,
  issues: [] as string[]
})

// 测试路由列表
const testRoutes = [
  { path: '/admin/business/orders', label: '订单管理', type: 'primary' },
  { path: '/admin/dashboard', label: '数据看板', type: 'success' },
  { path: '/admin/business/customers', label: '客户管理', type: 'info' },
  { path: '/test/order-api', label: 'API测试', type: 'warning' }
]

// 路由表格数据
const routeTableData = computed(() => {
  const routes = router.getRoutes()
  return routes
    .filter(route => 
      route.path.includes('/admin/business') || 
      route.path.includes('/test') ||
      route.path === '/admin/dashboard'
    )
    .map(route => ({
      path: route.path,
      name: route.name as string || '',
      title: route.meta?.title || '',
      component: getComponentName(route.components?.default),
      requiresAuth: route.meta?.requiresAuth || false,
      permissions: route.meta?.permissions || []
    }))
})

// 获取组件名称
function getComponentName(component: any): string {
  if (!component) return '未定义'
  if (typeof component === 'function') return '懒加载组件'
  return component.name || '未知组件'
}

// 运行验证
const runValidation = () => {
  const routes = router.getRoutes() as RouteRecordRaw[]
  
  // 运行通用路由验证
  validationResult.value = routeValidationUtils.validator.validateRoutes(routes)
  
  // 运行订单路由专项验证
  orderValidation.value = routeValidationUtils.validateOrderRoutes(routes)
  
  ElMessage.success('路由验证完成')
}

// 测试路由跳转
const testNavigation = async (path: string) => {
  try {
    await router.push(path)
    ElMessage.success(`成功跳转到 ${path}`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    ElMessage.error(`跳转失败: ${path}`)
  }
}

// 导出验证报告
const exportReport = () => {
  const report = {
    timestamp: new Date().toISOString(),
    validation: validationResult.value,
    orderValidation: orderValidation.value,
    routes: routeTableData.value
  }
  
  const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `route-validation-report-${Date.now()}.json`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success('验证报告已导出')
}

onMounted(() => {
  runValidation()
})
</script>

<style lang="scss" scoped>
.route-config-test {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.test-header {
  margin-bottom: 32px;
  text-align: center;
  
  h2 {
    color: var(--text-primary);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 14px;
    margin-bottom: 16px;
  }
  
  .test-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
}

.validation-summary {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  
  .summary-card {
    flex: 1;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: var(--shadow-sm);
    
    &.success {
      border-left: 4px solid var(--success-color);
    }
    
    &.error {
      border-left: 4px solid var(--error-color);
    }
    
    .summary-icon {
      font-size: 32px;
      
      .success & {
        color: var(--success-color);
      }
      
      .error & {
        color: var(--error-color);
      }
    }
    
    .summary-content {
      h3 {
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      p {
        color: var(--text-secondary);
        font-size: 14px;
      }
    }
  }
  
  .summary-stats {
    display: flex;
    gap: 16px;
    
    .stat-item {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 20px;
      text-align: center;
      box-shadow: var(--shadow-sm);
      min-width: 100px;
      
      .stat-value {
        display: block;
        font-size: 24px;
        font-weight: 600;
        color: var(--primary-color);
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
  }
}

.test-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
  
  h3 {
    color: var(--text-primary);
    margin-bottom: 16px;
    font-size: 18px;
    border-bottom: 2px solid var(--primary-color);
    padding-bottom: 8px;
  }
  
  h4 {
    color: var(--text-primary);
    margin: 16px 0 12px 0;
    font-size: 16px;
  }
}

.order-route-check {
  .check-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    padding: 8px 12px;
    border-radius: var(--radius-md);
    background: var(--bg-secondary);
    
    &.success {
      color: var(--success-color);
      background: var(--success-light);
    }
    
    &.error {
      color: var(--error-color);
      background: var(--error-light);
    }
  }
}

.route-details {
  margin-top: 16px;
  
  .config-table {
    .config-row {
      display: flex;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid var(--border-color);
      
      &:last-child {
        border-bottom: none;
      }
      
      .config-label {
        font-weight: 500;
        color: var(--text-primary);
        min-width: 100px;
        margin-right: 12px;
      }
      
      .config-value {
        color: var(--text-secondary);
        
        &.success {
          color: var(--success-color);
          font-weight: 500;
        }
      }
    }
  }
}

.error-list,
.warning-list,
.issue-list {
  .error-item,
  .warning-item,
  .issue-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 8px;
    padding: 8px 12px;
    border-radius: var(--radius-md);
    
    .el-icon {
      margin-top: 2px;
      flex-shrink: 0;
    }
  }
  
  .error-item {
    background: var(--error-light);
    color: var(--error-color);
  }
  
  .warning-item {
    background: var(--warning-light);
    color: var(--warning-color);
  }
  
  .issue-item {
    background: var(--info-light);
    color: var(--info-color);
  }
}

.route-test-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.text-muted {
  color: var(--text-light);
  font-style: italic;
}

// 响应式设计
@media (max-width: 768px) {
  .route-config-test {
    padding: 16px;
  }
  
  .validation-summary {
    flex-direction: column;
    
    .summary-stats {
      justify-content: center;
    }
  }
  
  .test-section {
    padding: 16px;
  }
  
  .route-test-buttons {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>