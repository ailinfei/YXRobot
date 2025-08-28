<template>
  <div class="customer-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
        <div class="customer-title">
          <el-avatar :src="customer?.avatar" :size="48" :icon="UserFilled" />
          <div class="title-info">
            <h2>{{ customer?.name }}</h2>
            <div class="customer-meta">
              <el-tag :type="getLevelTagType(customer?.level)" size="large">
                {{ getLevelText(customer?.level) }}
              </el-tag>
              <span class="customer-id">ID: {{ customer?.id }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="header-right">
        <el-button @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑客户
        </el-button>
        <el-button type="primary" @click="handleAddDevice">
          <el-icon><Plus /></el-icon>
          添加设备
        </el-button>
      </div>
    </div>

    <!-- 客户概览卡片 -->
    <div class="overview-cards">
      <div class="stat-card">
        <div class="stat-icon devices">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customer?.deviceCount?.total || 0 }}</div>
          <div class="stat-label">设备总数</div>
          <div class="stat-breakdown">
            购买{{ customer?.deviceCount?.purchased || 0 }} · 租赁{{ customer?.deviceCount?.rental || 0 }}
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ customer?.totalSpent?.toLocaleString() || 0 }}</div>
          <div class="stat-label">总消费金额</div>
          <div class="stat-breakdown">
            客户价值评分: {{ customer?.customerValue?.toFixed(1) || 0 }}
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon orders">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customer?.orders?.length || 0 }}</div>
          <div class="stat-label">订单数量</div>
          <div class="stat-breakdown">
            服务记录{{ customer?.serviceRecords?.length || 0 }}条
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon activity">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatRelativeTime(customer?.lastActiveAt) }}</div>
          <div class="stat-label">最后活跃</div>
          <div class="stat-breakdown">
            注册于{{ formatDate(customer?.registeredAt) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <el-tabs v-model="activeTab" type="border-card" class="detail-tabs">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <CustomerBasicInfo :customer="customer" @update="handleCustomerUpdate" />
      </el-tab-pane>
      
      <!-- 设备管理 -->
      <el-tab-pane label="设备管理" name="devices">
        <CustomerDeviceList 
          :customer-id="customerId" 
          :devices="customer?.devices || []"
          @refresh="loadCustomerDetail"
        />
      </el-tab-pane>
      
      <!-- 订单历史 -->
      <el-tab-pane label="订单历史" name="orders">
        <CustomerOrderHistory 
          :customer-id="customerId"
          :orders="customer?.orders || []"
        />
      </el-tab-pane>
      
      <!-- 服务记录 -->
      <el-tab-pane label="服务记录" name="service">
        <CustomerServiceRecords 
          :customer-id="customerId"
          :records="customer?.serviceRecords || []"
          @refresh="loadCustomerDetail"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  UserFilled,
  Edit,
  Plus,
  Monitor,
  Money,
  Document,
  Clock
} from '@element-plus/icons-vue'
import CustomerBasicInfo from '@/components/customer/CustomerBasicInfo.vue'
import CustomerDeviceList from '@/components/customer/CustomerDeviceList.vue'
import CustomerOrderHistory from '@/components/customer/CustomerOrderHistory.vue'
import CustomerServiceRecords from '@/components/customer/CustomerServiceRecords.vue'
import { mockCustomerAPI } from '@/api/mock'
import type { Customer } from '@/api/mock/customer'

const route = useRoute()
const router = useRouter()

// 响应式数据
const customerId = ref(route.params.id as string)
const customer = ref<Customer | null>(null)
const activeTab = ref('basic')
const loading = ref(false)

// 方法
const goBack = () => {
  router.back()
}

const loadCustomerDetail = async () => {
  loading.value = true
  try {
    const response = await mockCustomerAPI.getCustomerById(customerId.value)
    customer.value = response.data
  } catch (error) {
    console.error('加载客户详情失败:', error)
    ElMessage.error('加载客户详情失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  // 触发编辑客户信息
  const basicInfoComponent = document.querySelector('.customer-basic-info')
  if (basicInfoComponent) {
    const editButton = basicInfoComponent.querySelector('button')
    editButton?.click()
  }
}

const handleAddDevice = () => {
  // 切换到设备管理标签页并触发添加设备
  activeTab.value = 'devices'
  nextTick(() => {
    const deviceListComponent = document.querySelector('.customer-device-list')
    if (deviceListComponent) {
      const addButton = deviceListComponent.querySelector('button[type="primary"]')
      addButton?.click()
    }
  })
}

const handleCustomerUpdate = () => {
  loadCustomerDetail()
}

// 工具方法
const getLevelTagType = (level?: string) => {
  const types: Record<string, any> = {
    regular: '',
    vip: 'warning',
    premium: 'success'
  }
  return types[level || ''] || ''
}

const getLevelText = (level?: string) => {
  const texts: Record<string, string> = {
    regular: '普通客户',
    vip: 'VIP客户',
    premium: '高级客户'
  }
  return texts[level || ''] || level || ''
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatRelativeTime = (dateStr?: string) => {
  if (!dateStr) return ''
  const now = new Date()
  const date = new Date(dateStr)
  const diffMs = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '昨天'
  if (diffDays < 7) return `${diffDays}天前`
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}周前`
  return `${Math.floor(diffDays / 30)}月前`
}

onMounted(() => {
  loadCustomerDetail()
})
</script>

<style lang="scss" scoped>
.customer-detail {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .customer-title {
        display: flex;
        align-items: center;
        gap: 12px;

        .title-info {
          h2 {
            margin: 0 0 4px 0;
            color: #303133;
            font-size: 20px;
            font-weight: 600;
          }

          .customer-meta {
            display: flex;
            align-items: center;
            gap: 12px;

            .customer-id {
              color: #909399;
              font-size: 12px;
            }
          }
        }
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .overview-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;

        &.devices {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
        }

        &.revenue {
          background: linear-gradient(135deg, #67C23A, #85CE61);
          color: white;
        }

        &.orders {
          background: linear-gradient(135deg, #E6A23C, #ELBF73);
          color: white;
        }

        &.activity {
          background: linear-gradient(135deg, #F56C6C, #F78989);
          color: white;
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
          margin-bottom: 4px;
        }

        .stat-breakdown {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .detail-tabs {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 20px;
      background: #f8f9fa;
      border-radius: 8px 8px 0 0;
    }

    :deep(.el-tabs__content) {
      padding: 20px;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .customer-detail {
    .overview-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .customer-detail {
    padding: 16px;

    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .header-left {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
      }

      .header-right {
        justify-content: center;
      }
    }

    .overview-cards {
      grid-template-columns: 1fr;
      gap: 16px;

      .stat-card {
        padding: 16px;

        .stat-icon {
          width: 40px;
          height: 40px;
          font-size: 18px;
        }

        .stat-content {
          .stat-value {
            font-size: 20px;
          }
        }
      }
    }
  }
}
</style>