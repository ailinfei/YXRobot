<!--
  设备路由验证测试页面
  用于验证设备管理路由配置的完整性和功能性
-->
<template>
  <div class="device-route-validation">
    <div class="validation-header">
      <h2>设备管理路由验证</h2>
      <p>验证设备管理页面的路由配置、导航菜单和面包屑功能是否正常工作</p>
    </div>

    <!-- 路由配置验证 -->
    <div class="validation-section">
      <h3>路由配置验证</h3>
      <div class="validation-results">
        <div class="result-item">
          <el-icon :class="routeExists ? 'success' : 'error'">
            <component :is="routeExists ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
          </el-icon>
          <span>设备管理路由 (/admin/device/management): </span>
          <el-tag :type="routeExists ? 'success' : 'danger'">
            {{ routeExists ? '已配置' : '未配置' }}
          </el-tag>
        </div>
        
        <div class="result-item">
          <el-icon :class="componentExists ? 'success' : 'error'">
            <component :is="componentExists ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
          </el-icon>
          <span>DeviceManagement组件: </span>
          <el-tag :type="componentExists ? 'success' : 'danger'">
            {{ componentExists ? '已加载' : '未加载' }}
          </el-tag>
        </div>

        <div class="result-item">
          <el-icon :class="navigationConfigured ? 'success' : 'error'">
            <component :is="navigationConfigured ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
          </el-icon>
          <span>导航菜单配置: </span>
          <el-tag :type="navigationConfigured ? 'success' : 'danger'">
            {{ navigationConfigured ? '已配置' : '未配置' }}
          </el-tag>
        </div>

        <div class="result-item">
          <el-icon :class="breadcrumbConfigured ? 'success' : 'error'">
            <component :is="breadcrumbConfigured ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
          </el-icon>
          <span>面包屑导航配置: </span>
          <el-tag :type="breadcrumbConfigured ? 'success' : 'danger'">
            {{ breadcrumbConfigured ? '已配置' : '未配置' }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 路由跳转测试 -->
    <div class="validation-section">
      <h3>路由跳转测试</h3>
      <div class="test-buttons">
        <el-button type="primary" @click="testDeviceManagementRoute">
          测试设备管理页面跳转
        </el-button>
        <el-button type="success" @click="testDeviceMonitoringRoute">
          测试设备监控页面跳转
        </el-button>
        <el-button type="warning" @click="testFirmwareManagementRoute">
          测试固件管理页面跳转
        </el-button>
        <el-button @click="testBackToDashboard">
          返回控制台
        </el-button>
      </div>
    </div>

    <!-- 当前路由状态 -->
    <div class="validation-section">
      <h3>当前路由状态</h3>
      <div class="route-status">
        <div class="status-item">
          <label>当前路径:</label>
          <code>{{ currentRoute.path }}</code>
        </div>
        <div class="status-item">
          <label>路由名称:</label>
          <code>{{ currentRoute.name }}</code>
        </div>
        <div class="status-item">
          <label>页面标题:</label>
          <code>{{ currentRoute.meta?.title }}</code>
        </div>
        <div class="status-item">
          <label>查询参数:</label>
          <code>{{ JSON.stringify(currentRoute.query) }}</code>
        </div>
      </div>
    </div>

    <!-- 面包屑测试 -->
    <div class="validation-section">
      <h3>面包屑导航测试</h3>
      <div class="breadcrumb-tests">
        <div v-for="testPath in testPaths" :key="testPath" class="breadcrumb-test">
          <div class="test-path">{{ testPath }}</div>
          <div class="breadcrumb-result">
            <span 
              v-for="(item, index) in getBreadcrumbForPath(testPath)" 
              :key="index"
              class="breadcrumb-item"
            >
              {{ item.title }}
              <span v-if="index < getBreadcrumbForPath(testPath).length - 1" class="separator">></span>
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 权限验证测试 -->
    <div class="validation-section">
      <h3>权限验证测试</h3>
      <div class="permission-tests">
        <div class="permission-item">
          <label>设备管理权限:</label>
          <el-tag :type="hasDeviceManagePermission ? 'success' : 'danger'">
            {{ hasDeviceManagePermission ? '有权限' : '无权限' }}
          </el-tag>
        </div>
        <div class="permission-item">
          <label>设备监控权限:</label>
          <el-tag :type="hasDeviceMonitorPermission ? 'success' : 'danger'">
            {{ hasDeviceMonitorPermission ? '有权限' : '无权限' }}
          </el-tag>
        </div>
        <div class="permission-item">
          <label>固件管理权限:</label>
          <el-tag :type="hasFirmwareManagePermission ? 'success' : 'danger'">
            {{ hasFirmwareManagePermission ? '有权限' : '无权限' }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 测试结果汇总 -->
    <div class="validation-section">
      <h3>验证结果汇总</h3>
      <div class="summary-card" :class="allTestsPassed ? 'success' : 'error'">
        <div class="summary-icon">
          <el-icon>
            <component :is="allTestsPassed ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
          </el-icon>
        </div>
        <div class="summary-content">
          <h4>{{ allTestsPassed ? '所有测试通过' : '存在配置问题' }}</h4>
          <p>{{ summaryMessage }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  CircleCheckFilled, 
  CircleCloseFilled,
  Plus,
  Monitor,
  Tools,
  Setting
} from '@element-plus/icons-vue'
import { getBreadcrumb, mainNavigationConfig, hasMenuPermission } from '@/config/navigation'

const router = useRouter()
const route = useRoute()

// 响应式数据
const currentRoute = computed(() => route)

// 测试路径
const testPaths = [
  '/admin/device/management',
  '/admin/device/monitoring',
  '/admin/device/firmware'
]

// 模拟用户权限
const userPermissions = ref([
  'admin:all',
  'device:view',
  'device:manage',
  'device:monitor',
  'firmware:manage'
])

// 验证结果
const routeExists = ref(false)
const componentExists = ref(false)
const navigationConfigured = ref(false)
const breadcrumbConfigured = ref(false)

// 权限检查
const hasDeviceManagePermission = computed(() => {
  return userPermissions.value.includes('device:manage') || userPermissions.value.includes('admin:all')
})

const hasDeviceMonitorPermission = computed(() => {
  return userPermissions.value.includes('device:monitor') || userPermissions.value.includes('admin:all')
})

const hasFirmwareManagePermission = computed(() => {
  return userPermissions.value.includes('firmware:manage') || userPermissions.value.includes('admin:all')
})

// 总体测试结果
const allTestsPassed = computed(() => {
  return routeExists.value && 
         componentExists.value && 
         navigationConfigured.value && 
         breadcrumbConfigured.value
})

const summaryMessage = computed(() => {
  if (allTestsPassed.value) {
    return '设备管理路由配置完整，所有功能正常工作。可以正常访问 /admin/device/management 页面。'
  } else {
    const issues = []
    if (!routeExists.value) issues.push('路由配置')
    if (!componentExists.value) issues.push('组件加载')
    if (!navigationConfigured.value) issues.push('导航菜单')
    if (!breadcrumbConfigured.value) issues.push('面包屑导航')
    return `发现问题：${issues.join('、')}需要检查和修复。`
  }
})

// 路由跳转方法
const testDeviceManagementRoute = () => {
  router.push('/admin/device/management')
  ElMessage.success('跳转到设备管理页面')
}

const testDeviceMonitoringRoute = () => {
  router.push('/admin/device/monitoring')
  ElMessage.success('跳转到设备监控页面')
}

const testFirmwareManagementRoute = () => {
  router.push('/admin/device/firmware')
  ElMessage.success('跳转到固件管理页面')
}

const testBackToDashboard = () => {
  router.push('/admin/dashboard')
  ElMessage.success('返回控制台')
}

// 获取面包屑
const getBreadcrumbForPath = (path: string) => {
  return getBreadcrumb(path, 'admin')
}

// 验证配置
const validateConfigurations = () => {
  // 验证路由是否存在
  const routes = router.getRoutes()
  const deviceManagementRoute = routes.find(route => 
    route.path === '/admin/device/management' || 
    route.name === 'DeviceManagement'
  )
  routeExists.value = !!deviceManagementRoute

  // 验证组件是否存在（通过路由配置检查）
  componentExists.value = !!deviceManagementRoute?.component

  // 验证导航配置
  const deviceMenu = mainNavigationConfig.find(item => item.path === '/admin/device')
  navigationConfigured.value = !!(deviceMenu && 
    deviceMenu.children && 
    deviceMenu.children.some(child => child.path === '/admin/device/management'))

  // 验证面包屑配置
  const breadcrumb = getBreadcrumbForPath('/admin/device/management')
  breadcrumbConfigured.value = breadcrumb.length > 0
}

onMounted(() => {
  validateConfigurations()
})
</script>

<style lang="scss" scoped>
.device-route-validation {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .validation-header {
    text-align: center;
    margin-bottom: 32px;

    h2 {
      color: #303133;
      margin-bottom: 8px;
    }

    p {
      color: #606266;
      font-size: 14px;
    }
  }

  .validation-section {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h3 {
      color: #303133;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid #e6f7ff;
    }

    .validation-results {
      .result-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        gap: 8px;

        .el-icon {
          &.success {
            color: #67c23a;
          }
          &.error {
            color: #f56c6c;
          }
        }

        span {
          color: #303133;
          font-weight: 500;
        }
      }
    }

    .test-buttons {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }

    .route-status {
      .status-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        gap: 12px;

        label {
          font-weight: 500;
          color: #606266;
          min-width: 100px;
        }

        code {
          background: #f5f7fa;
          padding: 4px 8px;
          border-radius: 4px;
          font-family: 'Courier New', monospace;
          color: #303133;
        }
      }
    }

    .breadcrumb-tests {
      .breadcrumb-test {
        margin-bottom: 16px;
        padding: 12px;
        border: 1px solid #e4e7ed;
        border-radius: 6px;

        .test-path {
          font-weight: 500;
          color: #606266;
          margin-bottom: 8px;
          font-family: 'Courier New', monospace;
        }

        .breadcrumb-result {
          display: flex;
          align-items: center;
          gap: 8px;

          .breadcrumb-item {
            color: #303133;
          }

          .separator {
            color: #909399;
          }
        }
      }
    }

    .permission-tests {
      .permission-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        gap: 12px;

        label {
          font-weight: 500;
          color: #606266;
          min-width: 120px;
        }
      }
    }

    .summary-card {
      display: flex;
      align-items: center;
      padding: 20px;
      border-radius: 8px;
      gap: 16px;

      &.success {
        background: #f0f9ff;
        border: 1px solid #67c23a;
      }

      &.error {
        background: #fef0f0;
        border: 1px solid #f56c6c;
      }

      .summary-icon {
        font-size: 32px;

        .el-icon {
          color: inherit;
        }
      }

      .summary-content {
        h4 {
          margin: 0 0 8px 0;
          color: #303133;
        }

        p {
          margin: 0;
          color: #606266;
          line-height: 1.5;
        }
      }

      &.success {
        .summary-icon .el-icon {
          color: #67c23a;
        }
      }

      &.error {
        .summary-icon .el-icon {
          color: #f56c6c;
        }
      }
    }
  }
}
</style>