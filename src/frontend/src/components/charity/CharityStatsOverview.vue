<template>
  <div class="charity-stats-overview">
    <!-- 统计卡片网格 -->
    <div class="stats-grid" v-loading="loading">
      <CharityStatsCard
        title="累计受益人群"
        :value="stats?.totalBeneficiaries || 0"
        unit="人"
        :change="stats?.monthlyComparison?.beneficiariesChange"
        theme="primary"
        icon="user"
        :show-trend="true"
        :trend-data="stats?.trends?.beneficiariesTrend || []"
        :animation-duration="2000"
      />
      
      <CharityStatsCard
        title="合作机构数量"
        :value="stats?.cooperatingInstitutions || 0"
        unit="家"
        :change="stats?.monthlyComparison?.institutionsChange"
        theme="success"
        icon="building"
        :show-trend="true"
        :trend-data="stats?.trends?.institutionsTrend || []"
        :animation-duration="2200"
      />
      
      <CharityStatsCard
        title="公益项目数量"
        :value="stats?.totalProjects || 0"
        unit="个"
        :change="stats?.monthlyComparison?.projectsChange"
        theme="info"
        icon="trophy"
        :show-trend="true"
        :trend-data="stats?.trends?.projectsTrend || []"
        :animation-duration="2400"
      />
      
      <CharityStatsCard
        title="筹集资金总额"
        :value="formatCurrency(stats?.totalRaised || 0)"
        unit="万元"
        :change="stats?.monthlyComparison?.fundingChange"
        theme="warning"
        icon="money"
        :show-trend="true"
        :trend-data="stats?.trends?.fundingTrend || []"
        :animation-duration="2600"
        :decimals="1"
      />
    </div>

    <!-- 详细统计信息 -->
    <div class="detailed-stats" v-if="stats">
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="stat-detail-card">
            <h4>项目执行情况</h4>
            <div class="stat-items">
              <div class="stat-item">
                <span class="label">进行中项目</span>
                <span class="value primary">{{ stats.activeProjects }}</span>
              </div>
              <div class="stat-item">
                <span class="label">已完成项目</span>
                <span class="value success">{{ stats.completedProjects }}</span>
              </div>
              <div class="stat-item">
                <span class="label">项目完成率</span>
                <span class="value info">{{ projectCompletionRate }}%</span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="stat-detail-card">
            <h4>资金使用情况</h4>
            <div class="stat-items">
              <div class="stat-item">
                <span class="label">已筹集资金</span>
                <span class="value warning">{{ formatCurrency(stats.totalRaised) }}万</span>
              </div>
              <div class="stat-item">
                <span class="label">已使用资金</span>
                <span class="value success">{{ formatCurrency(stats.totalDonated) }}万</span>
              </div>
              <div class="stat-item">
                <span class="label">资金使用率</span>
                <span class="value info">{{ fundingUsageRate }}%</span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="stat-detail-card">
            <h4>志愿者参与情况</h4>
            <div class="stat-items">
              <div class="stat-item">
                <span class="label">注册志愿者</span>
                <span class="value primary">{{ stats.totalVolunteers }}</span>
              </div>
              <div class="stat-item">
                <span class="label">活跃志愿者</span>
                <span class="value success">{{ stats.activeVolunteers }}</span>
              </div>
              <div class="stat-item">
                <span class="label">本月活动</span>
                <span class="value warning">{{ stats.thisMonthActivities }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 实时更新信息 -->
    <div class="update-info" v-if="stats?.lastUpdated">
      <el-icon><Clock /></el-icon>
      <span>最后更新时间: {{ formatUpdateTime(stats.lastUpdated) }}</span>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock } from '@element-plus/icons-vue'
import CharityStatsCard from '@/components/common/CharityStatsCard.vue'
import CharityAPI, { type EnhancedCharityStats } from '@/api/charity'

// 响应式数据
const loading = ref(true)
const refreshing = ref(false)
const stats = ref<EnhancedCharityStats | null>(null)
const autoRefreshTimer = ref<NodeJS.Timeout | null>(null)

// 计算属性
const projectCompletionRate = computed(() => {
  if (!stats.value) return 0
  const total = stats.value.totalProjects
  const completed = stats.value.completedProjects
  return total > 0 ? Math.round((completed / total) * 100) : 0
})

const fundingUsageRate = computed(() => {
  if (!stats.value) return 0
  const raised = stats.value.totalRaised
  const donated = stats.value.totalDonated
  return raised > 0 ? Math.round((donated / raised) * 100) : 0
})

// 方法
const formatCurrency = (amount: number): string => {
  return (amount / 10000).toFixed(1)
}

const formatUpdateTime = (time: string): string => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const loadCharityStats = async (showLoading = true) => {
  if (showLoading) {
    loading.value = true
  }
  
  try {
    const response = await CharityAPI.getEnhancedCharityStats()
    stats.value = response.data
    
    if (showLoading) {
      ElMessage.success('统计数据加载成功')
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

const handleRefresh = async () => {
  refreshing.value = true
  try {
    await loadCharityStats(false)
    ElMessage.success('数据已刷新')
  } catch (error) {
    ElMessage.error('刷新数据失败')
  } finally {
    refreshing.value = false
  }
}

const startAutoRefresh = () => {
  // 每30秒自动刷新一次数据
  autoRefreshTimer.value = setInterval(() => {
    loadCharityStats(false)
  }, 30000)
}

const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
  }
}

// 生命周期
onMounted(async () => {
  await loadCharityStats()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})

// 暴露方法给父组件
defineExpose({
  refresh: handleRefresh,
  loadData: loadCharityStats
})
</script>

<style lang="scss" scoped>
.charity-stats-overview {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  .detailed-stats {
    margin-bottom: 24px;

    .stat-detail-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      border: 1px solid #f0f0f0;
      height: 100%;

      h4 {
        margin: 0 0 20px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
        border-bottom: 2px solid #f0f0f0;
        padding-bottom: 12px;
      }

      .stat-items {
        display: flex;
        flex-direction: column;
        gap: 16px;

        .stat-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          background: #fafafa;
          border-radius: 8px;
          transition: all 0.3s ease;

          &:hover {
            background: #f5f7fa;
            transform: translateX(4px);
          }

          .label {
            font-size: 14px;
            color: #606266;
            font-weight: 500;
          }

          .value {
            font-size: 16px;
            font-weight: 700;

            &.primary {
              color: #409EFF;
            }

            &.success {
              color: #67C23A;
            }

            &.warning {
              color: #E6A23C;
            }

            &.info {
              color: #909399;
            }

            &.danger {
              color: #F56C6C;
            }
          }
        }
      }
    }
  }

  .update-info {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
    font-size: 14px;
    color: #606266;

    .el-icon {
      color: #909399;
    }

    .el-button {
      margin-left: auto;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .charity-stats-overview {
    .stats-grid {
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
    }
  }
}

@media (max-width: 768px) {
  .charity-stats-overview {
    .stats-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .detailed-stats {
      .el-col {
        margin-bottom: 16px;
      }

      .stat-detail-card {
        padding: 20px;

        h4 {
          font-size: 15px;
          margin-bottom: 16px;
        }

        .stat-items {
          gap: 12px;

          .stat-item {
            padding: 10px 14px;

            .label {
              font-size: 13px;
            }

            .value {
              font-size: 15px;
            }
          }
        }
      }
    }

    .update-info {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;

      .el-button {
        margin-left: 0;
        align-self: flex-end;
      }
    }
  }
}
</style>