<!--
  设备路由测试页面
  用于验证设备管理页面的路由配置和导航功能
-->
<template>
  <div class="device-route-test">
    <div class="test-header">
      <h2>设备管理路由测试</h2>
      <p>测试设备管理页面的路由配置、导航菜单和面包屑功能</p>
    </div>

    <!-- 路由测试区域 -->
    <div class="test-section">
      <h3>路由跳转测试</h3>
      <div class="test-buttons">
        <el-button type="primary" @click="navigateToDeviceManagement">
          跳转到设备管理页面
        </el-button>
        <el-button type="success" @click="navigateToDeviceMonitoring">
          跳转到设备监控页面
        </el-button>
        <el-button type="warning" @click="navigateToFirmwareManagement">
          跳转到固件管理页面
        </el-button>
      </div>
    </div>

    <!-- 当前路由信息 -->
    <div class="test-section">
      <h3>当前路由信息</h3>
      <div class="route-info">
        <div class="info-item">
          <label>当前路径:</label>
          <span>{{ currentRoute.path }}</span>
        </div>
        <div class="info-item">
          <label>路由名称:</label>
          <span>{{ currentRoute.name }}</span>
        </div>
        <div class="info-item">
          <label>页面标题:</label>
          <span>{{ currentRoute.meta?.title }}</span>
        </div>
      </div>
    </div>

    <!-- 面包屑测试 -->
    <div class="test-section">
      <h3>面包屑导航测试</h3>
      <div class="breadcrumb-test">
        <div v-for="path in testPaths" :key="path" class="breadcrumb-item">
          <label>{{ path }}:</label>
          <div class="breadcrumb-result">
            <span 
              v-for="(item, index) in getBreadcrumbForPath(path)" 
              :key="index"
              class="breadcrumb-segment"
            >
              {{ item.title }}
              <span v-if="index < getBreadcrumbForPath(path).length - 1" class="separator">></span>
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 导航菜单测试 -->
    <div class="test-section">
      <h3>导航菜单测试</h3>
      <div class="menu-test">
        <div class="menu-item" v-for="menuItem in deviceMenuItems" :key="menuItem.path">
          <div class="menu-title">
            <el-icon><component :is="menuItem.icon" /></el-icon>
            {{ menuItem.title }}
          </div>
          <div v-if="menuItem.children" class="menu-children">
            <div 
              v-for="child in menuItem.children" 
              :key="child.path"
              class="menu-child"
              @click="navigateToPath(child.path)"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              {{ child.title }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 权限测试 -->
    <div class="test-section">
      <h3>权限验证测试</h3>
      <div class="permission-test">
        <div class="permission-item">
          <label>当前用户权限:</label>
          <div class="permissions">
            <el-tag 
              v-for="permission in userPermissions" 
              :key="permission"
              size="small"
              type="info"
            >
              {{ permission }}
            </el-tag>
          </div>
        </div>
        <div class="permission-item">
          <label>设备管理权限检查:</label>
          <el-tag :type="hasDevicePermission ? 'success' : 'danger'">
            {{ hasDevicePermission ? '有权限' : '无权限' }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 测试结果 -->
    <div class="test-section">
      <h3>测试结果</h3>
      <div class="test-results">
        <div class="result-item">
          <label>路由配置:</label>
          <el-tag :type="routeConfigValid ? 'success' : 'danger'">
            {{ routeConfigValid ? '正常' : '异常' }}
          </el-tag>
        </div>
        <div class="result-item">
          <label>导航菜单:</label>
          <el-tag :type="menuConfigValid ? 'success' : 'danger'">
            {{ menuConfigValid ? '正常' : '异常' }}
          </el-tag>
        </div>
        <div class="result-item">
          <label>面包屑导航:</label>
          <el-tag :type="breadcrumbConfigValid ? 'success' : 'danger'">
            {{ breadcrumbConfigValid ? '正常' : '异常' }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Monitor, Tools, DataAnalysis, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getBreadcrumb, mainNavigationConfig, hasMenuPermission } from '@/config/navigation'

const router = useRouter()
const route = useRoute()

// 响应式数据
const currentRoute = computed(() => route)
const userPermissions = ref(['admin:all', 'device:view', 'device:manage', 'device:monitor', 'firmware:manage'])

// 测试路径
const testPaths = [
  '/admin/device/management',
  '/admin/device/monitoring', 
  '/admin/device/firmware'
]

// 设备相关菜单项
const deviceMenuItems = computed(() => {
  return mainNavigationConfig.filter(item => item.path === '/admin/device')
})

// 权限检查
const hasDevicePermission = computed(() => {
  const deviceMenu = deviceMenuItems.value[0]
  return deviceMenu ? hasMenuPermission(deviceMenu, userPermissions.value) : false
})

// 配置验证
const routeConfigValid = ref(false)
const menuConfigValid = ref(false)
const breadcrumbConfigValid = ref(false)

// 导航方法
const navigateToDeviceManagement = () => {
  router.push('/admin/device/management')
  ElMessage.success('跳转到设备管理页面')
}

const navigateToDeviceMonitoring = () => {
  router.push('/admin/device/monitoring')
  ElMessage.success('跳转到设备监控页面')
}

const navigateToFirmwareManagement = () => {
  router.push('/admin/device/firmware')
  ElMessage.success('跳转到固件管理页面')
}

const navigateToPath = (path: string) => {
  router.push(path)
  ElMessage.success(`跳转到 ${path}`)
}

// 获取面包屑
const getBreadcrumbForPath = (path: string) => {
  return getBreadcrumb(path, 'admin')
}

// 验证配置
const validateConfigurations = () => {
  // 验证路由配置
  const routes = router.getRoutes()
  const deviceRoutes = routes.filter(route => 
    route.path.includes('/admin/device')
  )
  routeConfigValid.value = deviceRoutes.length >= 3

  // 验证菜单配置
  menuConfigValid.value = deviceMenuItems.value.length > 0 && 
    deviceMenuItems.value[0].children && 
    deviceMenuItems.value[0].children.length >= 3

  // 验证面包屑配置
  const breadcrumbResults = testPaths.map(path => getBreadcrumbForPath(path))
  breadcrumbConfigValid.value = breadcrumbResults.every(breadcrumb => breadcrumb.length > 0)
}

onMounted(() => {
  validateConfigurations()
})
</script>

<style lang="scss" scoped>
.device-route-test {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .test-header {
    margin-bottom: 32px;
    text-align: center;

    h2 {
      color: #303133;
      margin-bottom: 8px;
    }

    p {
      color: #606266;
      font-size: 14px;
    }
  }

  .test-section {
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

    .test-buttons {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }

    .route-info {
      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        label {
          font-weight: 500;
          color: #606266;
          width: 120px;
        }

        span {
          color: #303133;
        }
      }
    }

    .breadcrumb-test {
      .breadcrumb-item {
        margin-bottom: 12px;

        label {
          font-weight: 500;
          color: #606266;
          display: block;
          margin-bottom: 4px;
        }

        .breadcrumb-result {
          display: flex;
          align-items: center;
          gap: 8px;

          .breadcrumb-segment {
            color: #303133;
          }

          .separator {
            color: #909399;
          }
        }
      }
    }

    .menu-test {
      .menu-item {
        border: 1px solid #e4e7ed;
        border-radius: 6px;
        padding: 16px;
        margin-bottom: 12px;

        .menu-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 12px;
        }

        .menu-children {
          padding-left: 24px;

          .menu-child {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.2s;

            &:hover {
              background-color: #f5f7fa;
            }
          }
        }
      }
    }

    .permission-test {
      .permission-item {
        margin-bottom: 16px;

        label {
          font-weight: 500;
          color: #606266;
          display: block;
          margin-bottom: 8px;
        }

        .permissions {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
      }
    }

    .test-results {
      .result-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        label {
          font-weight: 500;
          color: #606266;
          width: 120px;
        }
      }
    }
  }
}
</style>