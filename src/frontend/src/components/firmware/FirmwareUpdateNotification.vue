<!--
  固件更新提醒组件
  功能：显示更新提醒、版本差异、更新推荐
-->
<template>
  <div class="firmware-update-notification">
    <!-- 更新提醒横幅 -->
    <el-alert
      v-if="hasUpdates"
      :title="alertTitle"
      :type="alertType"
      :description="alertDescription"
      show-icon
      :closable="false"
      class="update-alert"
    >
      <template #default>
        <div class="alert-content">
          <div class="alert-text">
            <div class="alert-title">{{ alertTitle }}</div>
            <div class="alert-description">{{ alertDescription }}</div>
          </div>
          <div class="alert-actions">
            <el-button size="small" @click="showUpdateDetails">
              查看详情
            </el-button>
            <el-button type="primary" size="small" @click="startBatchUpdate">
              立即更新
            </el-button>
          </div>
        </div>
      </template>
    </el-alert>

    <!-- 更新详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="固件更新详情"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="update-details" v-loading="detailLoading">
        <!-- 更新概览 -->
        <div class="update-overview">
          <div class="overview-stats">
            <div class="stat-item">
              <div class="stat-value">{{ updateResult?.totalDevices || 0 }}</div>
              <div class="stat-label">总设备数</div>
            </div>
            <div class="stat-item highlight">
              <div class="stat-value">{{ updateResult?.devicesNeedingUpdate || 0 }}</div>
              <div class="stat-label">需要更新</div>
            </div>
            <div class="stat-item critical">
              <div class="stat-value">{{ updateResult?.criticalUpdates || 0 }}</div>
              <div class="stat-label">紧急更新</div>
            </div>
          </div>
          <div class="overview-actions">
            <el-button @click="refreshUpdateCheck" :loading="detailLoading">
              <el-icon><Refresh /></el-icon>
              重新检查
            </el-button>
            <el-button type="primary" @click="selectAllForUpdate">
              <el-icon><Select /></el-icon>
              全选更新
            </el-button>
          </div>
        </div>

        <!-- 更新推荐列表 -->
        <div class="recommendations-section">
          <div class="section-header">
            <h3>更新推荐</h3>
            <div class="header-filters">
              <el-select v-model="priorityFilter" placeholder="优先级" size="small" @change="filterRecommendations">
                <el-option label="全部" value="" />
                <el-option label="高优先级" value="high" />
                <el-option label="中优先级" value="medium" />
                <el-option label="低优先级" value="low" />
              </el-select>
            </div>
          </div>

          <div class="recommendations-list">
            <div
              v-for="recommendation in filteredRecommendations"
              :key="recommendation.deviceId"
              class="recommendation-item"
              :class="{ 
                'high-priority': recommendation.priority === 'high',
                'medium-priority': recommendation.priority === 'medium',
                'low-priority': recommendation.priority === 'low',
                'selected': selectedDevices.includes(recommendation.deviceId)
              }"
            >
              <div class="recommendation-header">
                <el-checkbox
                  :model-value="selectedDevices.includes(recommendation.deviceId)"
                  @change="toggleDeviceSelection(recommendation.deviceId)"
                />
                <div class="device-info">
                  <div class="device-name">设备 {{ recommendation.deviceId }}</div>
                  <div class="version-info">
                    {{ recommendation.currentVersion }} → {{ recommendation.recommendedVersion }}
                  </div>
                </div>
                <el-tag :type="getPriorityTagType(recommendation.priority)" size="small">
                  {{ getPriorityText(recommendation.priority) }}
                </el-tag>
              </div>

              <div class="recommendation-content">
                <div class="recommendation-reason">
                  <el-icon><InfoFilled /></el-icon>
                  <span>{{ recommendation.reason }}</span>
                </div>

                <div class="recommendation-details">
                  <!-- 更新收益 -->
                  <div class="detail-section" v-if="recommendation.benefits.length > 0">
                    <div class="detail-title">
                      <el-icon><CircleCheckFilled /></el-icon>
                      <span>更新收益</span>
                    </div>
                    <div class="detail-list">
                      <el-tag
                        v-for="benefit in recommendation.benefits"
                        :key="benefit"
                        type="success"
                        size="small"
                        class="detail-tag"
                      >
                        {{ benefit }}
                      </el-tag>
                    </div>
                  </div>

                  <!-- 潜在风险 -->
                  <div class="detail-section" v-if="recommendation.risks.length > 0">
                    <div class="detail-title">
                      <el-icon><WarningFilled /></el-icon>
                      <span>潜在风险</span>
                    </div>
                    <div class="detail-list">
                      <el-tag
                        v-for="risk in recommendation.risks"
                        :key="risk"
                        type="warning"
                        size="small"
                        class="detail-tag"
                      >
                        {{ risk }}
                      </el-tag>
                    </div>
                  </div>

                  <!-- 兼容性状态 -->
                  <div class="detail-section">
                    <div class="detail-title">
                      <el-icon><Monitor /></el-icon>
                      <span>兼容性</span>
                    </div>
                    <el-tag :type="recommendation.compatibility ? 'success' : 'danger'" size="small">
                      {{ recommendation.compatibility ? '完全兼容' : '存在兼容性问题' }}
                    </el-tag>
                  </div>
                </div>

                <div class="recommendation-actions">
                  <el-button size="small" @click="viewVersionDifference(recommendation.deviceId)">
                    查看差异
                  </el-button>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="updateSingleDevice(recommendation.deviceId)"
                    :disabled="!recommendation.compatibility"
                  >
                    立即更新
                  </el-button>
                </div>
              </div>
            </div>

            <div v-if="filteredRecommendations.length === 0" class="no-recommendations">
              <el-empty description="暂无更新推荐" />
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="startSelectedUpdates"
            :disabled="selectedDevices.length === 0"
          >
            更新选中设备 ({{ selectedDevices.length }})
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 版本差异对话框 -->
    <FirmwareVersionDifference
      v-model="differenceDialogVisible"
      :device-id="selectedDeviceId"
      @update-device="handleUpdateDevice"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  Refresh,
  Select,
  InfoFilled,
  CircleCheckFilled,
  WarningFilled,
  Monitor
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

import FirmwareVersionDifference from './FirmwareVersionDifference.vue'

// 版本推荐接口
interface VersionRecommendation {
  deviceId: string
  currentVersion: string
  recommendedVersion: string
  reason: string
  priority: 'high' | 'medium' | 'low'
  benefits: string[]
  risks: string[]
  compatibility: boolean
}

// 响应式数据
const detailDialogVisible = ref(false)
const differenceDialogVisible = ref(false)
const detailLoading = ref(false)
const priorityFilter = ref('')
const selectedDevices = ref<string[]>([])
const selectedDeviceId = ref('')

// 更新检测结果
const updateResult = ref<{
  totalDevices: number
  devicesNeedingUpdate: number
  criticalUpdates: number
  recommendations: VersionRecommendation[]
  lastCheckTime: string
  nextCheckTime: string
} | null>(null)

// 计算属性
const hasUpdates = computed(() => {
  return updateResult.value && updateResult.value.devicesNeedingUpdate > 0
})

const alertType = computed(() => {
  if (!updateResult.value) return 'info'
  if (updateResult.value.criticalUpdates > 0) return 'error'
  if (updateResult.value.devicesNeedingUpdate > 5) return 'warning'
  return 'info'
})

const alertTitle = computed(() => {
  if (!updateResult.value) return '检查更新中...'
  
  const { devicesNeedingUpdate, criticalUpdates } = updateResult.value
  
  if (criticalUpdates > 0) {
    return `发现 ${criticalUpdates} 个紧急更新`
  }
  
  return `发现 ${devicesNeedingUpdate} 个可用更新`
})

const alertDescription = computed(() => {
  if (!updateResult.value) return '正在检查设备固件更新...'
  
  const { devicesNeedingUpdate, criticalUpdates } = updateResult.value
  
  if (criticalUpdates > 0) {
    return `${criticalUpdates} 个设备需要紧急更新，${devicesNeedingUpdate - criticalUpdates} 个设备有常规更新可用`
  }
  
  return `${devicesNeedingUpdate} 个设备有新的固件版本可用，建议及时更新以获得最佳性能`
})

const filteredRecommendations = computed(() => {
  if (!updateResult.value) return []
  
  let recommendations = updateResult.value.recommendations
  
  if (priorityFilter.value) {
    recommendations = recommendations.filter(r => r.priority === priorityFilter.value)
  }
  
  // 按优先级排序
  return recommendations.sort((a, b) => {
    const priorityOrder = { high: 3, medium: 2, low: 1 }
    return priorityOrder[b.priority] - priorityOrder[a.priority]
  })
})

// 方法
const loadUpdateStatus = async () => {
  try {
    // Mock data for demonstration
    updateResult.value = {
      totalDevices: 25,
      devicesNeedingUpdate: 8,
      criticalUpdates: 2,
      recommendations: [
        {
          deviceId: 'device-001',
          currentVersion: '1.2.0',
          recommendedVersion: '1.3.0',
          reason: '安全补丁和性能优化',
          priority: 'high',
          benefits: ['安全修复', '性能提升'],
          risks: [],
          compatibility: true
        }
      ],
      lastCheckTime: new Date().toISOString(),
      nextCheckTime: new Date(Date.now() + 30000).toISOString()
    }
  } catch (error) {
    console.error('加载更新状态失败:', error)
  }
}

const showUpdateDetails = () => {
  detailDialogVisible.value = true
}

const refreshUpdateCheck = async () => {
  detailLoading.value = true
  try {
    // Mock refresh check - simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    await loadUpdateStatus()
    ElMessage.success('更新检查完成')
  } catch (error) {
    console.error('更新检查失败:', error)
    ElMessage.error('更新检查失败')
  } finally {
    detailLoading.value = false
  }
}

const filterRecommendations = () => {
  // 筛选逻辑已在计算属性中实现
}

const toggleDeviceSelection = (deviceId: string) => {
  const index = selectedDevices.value.indexOf(deviceId)
  if (index > -1) {
    selectedDevices.value.splice(index, 1)
  } else {
    selectedDevices.value.push(deviceId)
  }
}

const selectAllForUpdate = () => {
  if (selectedDevices.value.length === filteredRecommendations.value.length) {
    selectedDevices.value = []
  } else {
    selectedDevices.value = filteredRecommendations.value.map(r => r.deviceId)
  }
}

const viewVersionDifference = (deviceId: string) => {
  selectedDeviceId.value = deviceId
  differenceDialogVisible.value = true
}

const updateSingleDevice = (deviceId: string) => {
  ElMessage.info(`开始更新设备: ${deviceId}`)
  // 这里应该调用实际的更新API
}

const startBatchUpdate = () => {
  if (!updateResult.value) return
  
  // 选择所有高优先级设备
  const highPriorityDevices = updateResult.value.recommendations
    .filter(r => r.priority === 'high')
    .map(r => r.deviceId)
  
  if (highPriorityDevices.length > 0) {
    selectedDevices.value = highPriorityDevices
    startSelectedUpdates()
  } else {
    showUpdateDetails()
  }
}

const startSelectedUpdates = () => {
  if (selectedDevices.value.length === 0) {
    ElMessage.warning('请选择要更新的设备')
    return
  }
  
  ElMessage.info(`开始批量更新 ${selectedDevices.value.length} 个设备`)
  // 这里应该调用实际的批量更新API
  
  detailDialogVisible.value = false
}

const handleUpdateDevice = (deviceId: string) => {
  updateSingleDevice(deviceId)
  differenceDialogVisible.value = false
}

// 工具方法
const getPriorityTagType = (priority: string) => {
  const types: Record<string, any> = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const texts: Record<string, string> = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级'
  }
  return texts[priority] || priority
}

// 生命周期
onMounted(() => {
  loadUpdateStatus()
  
  // 监听更新检测结果
  const checkInterval = setInterval(() => {
    loadUpdateStatus()
  }, 30000) // 30秒检查一次
  
  onUnmounted(() => {
    clearInterval(checkInterval)
  })
})
</script>

<style lang="scss" scoped>
.firmware-update-notification {
  .update-alert {
    margin-bottom: 20px;

    .alert-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;

      .alert-text {
        flex: 1;

        .alert-title {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 4px;
        }

        .alert-description {
          font-size: 14px;
          color: #606266;
        }
      }

      .alert-actions {
        display: flex;
        gap: 8px;
      }
    }
  }

  .update-details {
    .update-overview {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;

      .overview-stats {
        display: flex;
        gap: 24px;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #303133;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #606266;
          }

          &.highlight .stat-value {
            color: #e6a23c;
          }

          &.critical .stat-value {
            color: #f56c6c;
          }
        }
      }

      .overview-actions {
        display: flex;
        gap: 8px;
      }
    }

    .recommendations-section {
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .header-filters {
          display: flex;
          gap: 8px;
        }
      }

      .recommendations-list {
        display: flex;
        flex-direction: column;
        gap: 16px;
        max-height: 500px;
        overflow-y: auto;

        .recommendation-item {
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          padding: 16px;
          background: white;
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          &.selected {
            border-color: #409eff;
            background: #f0f9ff;
          }

          &.high-priority {
            border-left: 4px solid #f56c6c;
          }

          &.medium-priority {
            border-left: 4px solid #e6a23c;
          }

          &.low-priority {
            border-left: 4px solid #909399;
          }

          .recommendation-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 12px;

            .device-info {
              flex: 1;

              .device-name {
                font-size: 14px;
                font-weight: 500;
                color: #303133;
                margin-bottom: 2px;
              }

              .version-info {
                font-size: 12px;
                color: #606266;
                font-family: monospace;
              }
            }
          }

          .recommendation-content {
            .recommendation-reason {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-bottom: 12px;
              padding: 8px 12px;
              background: #f0f9ff;
              border-radius: 4px;
              font-size: 14px;
              color: #606266;

              .el-icon {
                color: #409eff;
              }
            }

            .recommendation-details {
              display: flex;
              flex-direction: column;
              gap: 12px;
              margin-bottom: 12px;

              .detail-section {
                .detail-title {
                  display: flex;
                  align-items: center;
                  gap: 6px;
                  font-size: 12px;
                  font-weight: 500;
                  color: #606266;
                  margin-bottom: 6px;
                }

                .detail-list {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 6px;

                  .detail-tag {
                    font-size: 11px;
                  }
                }
              }
            }

            .recommendation-actions {
              display: flex;
              justify-content: flex-end;
              gap: 8px;
            }
          }
        }

        .no-recommendations {
          text-align: center;
          padding: 40px;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .firmware-update-notification {
    .update-alert {
      .alert-content {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;

        .alert-actions {
          justify-content: center;
        }
      }
    }

    .update-details {
      .update-overview {
        flex-direction: column;
        gap: 16px;

        .overview-stats {
          justify-content: center;
        }
      }

      .recommendations-section {
        .section-header {
          flex-direction: column;
          gap: 12px;
          align-items: stretch;
        }
      }
    }

    .dialog-footer {
      flex-direction: column-reverse;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>