<template>
  <div class="visual-progress-monitor">
    <!-- 总体进度概览 -->
    <div class="progress-overview">
      <div class="progress-header">
        <h3>{{ $t('fontPackage.progress.title') }}</h3>
        <div class="status-indicators">
          <div class="connection-status" :class="connectionStatus">
            <el-icon><CircleCheck v-if="connectionStatus === 'connected'" /><Warning v-else /></el-icon>
            <span>{{ connectionStatus === 'connected' ? '已连接' : connectionStatus === 'connecting' ? '连接中' : '未连接' }}</span>
          </div>
          <div class="health-indicator" :class="healthStatus.status">
            <el-icon><CircleCheck v-if="healthStatus.status === 'healthy'" /><Warning v-else /></el-icon>
            <span>{{ getStatusText(healthStatus.status) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 主进度条 -->
      <div class="main-progress">
        <el-progress 
          :percentage="overallProgress.percentage" 
          :status="getProgressStatus()"
          :stroke-width="12"
          :show-text="false"
        />
        <div class="progress-info">
          <span class="percentage">{{ overallProgress.percentage }}%</span>
          <span class="phase">{{ overallProgress.currentPhase.name }}</span>
          <span class="eta" v-if="overallProgress.estimatedTimeRemaining > 0">
            {{ formatTime(overallProgress.estimatedTimeRemaining) }} {{ $t('fontPackage.progress.remaining') }}
          </span>
        </div>
      </div>
    </div>

    <!-- 详细进度信息 -->
    <div class="progress-details">
      <el-row :gutter="20">
        <!-- 字符进度 -->
        <el-col :span="12">
          <div class="detail-card">
            <h4>{{ $t('fontPackage.progress.characterProgress') }}</h4>
            <div class="character-stats">
              <div class="stat-item">
                <span class="label">{{ $t('fontPackage.progress.completed') }}</span>
                <span class="value">{{ overallProgress.charactersCompleted }}</span>
              </div>
              <div class="stat-item">
                <span class="label">{{ $t('fontPackage.progress.total') }}</span>
                <span class="value">{{ overallProgress.charactersTotal }}</span>
              </div>
              <div class="stat-item">
                <span class="label">{{ $t('fontPackage.progress.current') }}</span>
                <span class="value current-char">{{ overallProgress.currentCharacter }}</span>
              </div>
            </div>
            
            <!-- 字符进度网格 -->
            <div class="character-grid" v-if="Object.keys(characterProgress).length > 0">
              <div 
                v-for="(progress, character) in characterProgress" 
                :key="character"
                class="character-item"
                :class="progress.status"
                :title="`${character}: ${progress.progress}%`"
              >
                <span class="character">{{ character }}</span>
                <div class="mini-progress">
                  <div 
                    class="progress-bar" 
                    :style="{ width: progress.progress + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 性能指标 -->
        <el-col :span="12">
          <div class="detail-card">
            <h4>{{ $t('fontPackage.progress.performance') }}</h4>
            <div class="performance-metrics">
              <div class="metric-item">
                <el-icon><Timer /></el-icon>
                <div class="metric-info">
                  <span class="label">{{ $t('fontPackage.progress.trainingSpeed') }}</span>
                  <span class="value">{{ performanceMetrics.trainingSpeed }} chars/min</span>
                </div>
              </div>
              <div class="metric-item">
                <el-icon><Monitor /></el-icon>
                <div class="metric-info">
                  <span class="label">{{ $t('fontPackage.progress.memoryUsage') }}</span>
                  <span class="value">{{ performanceMetrics.memoryUsage }}%</span>
                </div>
              </div>
              <div class="metric-item">
                <el-icon><VideoPlay /></el-icon>
                <div class="metric-info">
                  <span class="label">{{ $t('fontPackage.progress.gpuUtilization') }}</span>
                  <span class="value">{{ performanceMetrics.gpuUtilization }}%</span>
                </div>
              </div>
              <div class="metric-item">
                <el-icon><Clock /></el-icon>
                <div class="metric-info">
                  <span class="label">{{ $t('fontPackage.progress.estimatedCompletion') }}</span>
                  <span class="value">{{ performanceMetrics.estimatedCompletion }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 异常告警 -->
    <div class="anomaly-alerts" v-if="anomalies.length > 0">
      <h4>{{ $t('fontPackage.progress.alerts') }}</h4>
      <div class="alert-list">
        <el-alert
          v-for="anomaly in anomalies"
          :key="anomaly.timestamp"
          :type="getAlertType(anomaly.severity)"
          :title="anomaly.message"
          :closable="true"
          @close="resolveAnomaly(anomaly)"
          show-icon
        >
          <template #default>
            <div class="alert-details">
              <span class="timestamp">{{ formatTimestamp(anomaly.timestamp) }}</span>
              <span class="type">{{ $t(`fontPackage.progress.anomalyTypes.${anomaly.type}`) }}</span>
            </div>
          </template>
        </el-alert>
      </div>
    </div>

    <!-- 训练阶段详情 -->
    <div class="phase-details" v-if="overallProgress.currentPhase">
      <h4>{{ $t('fontPackage.progress.currentPhase') }}</h4>
      <div class="phase-info">
        <div class="phase-header">
          <span class="phase-name">{{ overallProgress.currentPhase.name }}</span>
          <span class="phase-status" :class="overallProgress.currentPhase.status">
            {{ $t(`fontPackage.progress.phaseStatus.${overallProgress.currentPhase.status}`) }}
          </span>
        </div>
        <p class="phase-description">{{ overallProgress.currentPhase.description }}</p>
        <el-progress 
          :percentage="overallProgress.currentPhase.progress" 
          :stroke-width="6"
          :show-text="true"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElProgress, ElRow, ElCol, ElAlert, ElIcon } from 'element-plus'
import { CircleCheck, Warning, Timer, Monitor, VideoPlay, Clock } from '@element-plus/icons-vue'
import { useProgressMonitoring } from '@/composables/useProgressMonitoring'
import type { 
  ProgressData, 
  CharacterProgressMap, 
  PerformanceMetrics, 
  AnomalyAlert, 
  SystemHealth 
} from '@/types/fontPackage'

interface Props {
  packageId?: number
  autoStart?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  autoStart: true
})

const emit = defineEmits<{
  anomalyResolved: [anomaly: AnomalyAlert]
  monitoringStarted: [packageId: number]
  monitoringStopped: []
}>()

// 使用进度监控 composable
const {
  isMonitoring,
  isConnected,
  overallProgress,
  characterProgress,
  performanceMetrics,
  anomalies,
  healthStatus,
  startMonitoring: startProgressMonitoring,
  stopMonitoring: stopProgressMonitoring,
  resolveAnomaly: resolveProgressAnomaly,
  getProgressSnapshot
} = useProgressMonitoring()

// 计算属性
const getProgressStatus = computed(() => {
  if (overallProgress.value.percentage === 100) return 'success'
  if (anomalies.value.some(a => a.severity === 'high')) return 'exception'
  return undefined
})

// 方法
const getStatusText = (status: string) => {
  const statusMap = {
    healthy: '正常',
    warning: '警告',
    error: '错误'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getAlertType = (severity: string) => {
  const typeMap = {
    low: 'info',
    medium: 'warning',
    high: 'error'
  }
  return typeMap[severity as keyof typeof typeMap] || 'info'
}

const formatTime = (seconds: number) => {
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟`
  return `${Math.floor(seconds / 3600)}小时${Math.floor((seconds % 3600) / 60)}分钟`
}

const formatTimestamp = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString()
}

const resolveAnomaly = (anomaly: AnomalyAlert) => {
  const resolved = resolveProgressAnomaly(anomaly.timestamp)
  if (resolved) {
    emit('anomalyResolved', resolved)
  }
}

// 连接状态指示器
const connectionStatus = computed(() => {
  if (!isMonitoring.value) return 'disconnected'
  return isConnected.value ? 'connected' : 'connecting'
})

// 生命周期
onMounted(async () => {
  if (props.autoStart && props.packageId) {
    await startMonitoring(props.packageId)
  }
})

onUnmounted(async () => {
  await stopMonitoring()
})

// 公开方法
const startMonitoring = async (packageId: number) => {
  try {
    await startProgressMonitoring(packageId)
    emit('monitoringStarted', packageId)
  } catch (error) {
    console.error('启动监控失败:', error)
  }
}

const stopMonitoring = async () => {
  try {
    await stopProgressMonitoring()
    emit('monitoringStopped')
  } catch (error) {
    console.error('停止监控失败:', error)
  }
}

// 暴露给父组件的方法
defineExpose({
  startMonitoring,
  stopMonitoring,
  getProgressSnapshot
})
</script>

<style scoped lang="scss">
.visual-progress-monitor {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .progress-overview {
    margin-bottom: 24px;

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .status-indicators {
        display: flex;
        gap: 12px;

        .connection-status,
        .health-indicator {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 4px 12px;
          border-radius: 16px;
          font-size: 12px;
          font-weight: 500;
        }

        .connection-status {
          &.connected {
            background: #f0f9ff;
            color: #67c23a;
          }

          &.connecting {
            background: #fdf6ec;
            color: #e6a23c;
          }

          &.disconnected {
            background: #fef0f0;
            color: #f56c6c;
          }
        }

        .health-indicator {
          &.healthy {
            background: #f0f9ff;
            color: #67c23a;
          }

          &.warning {
            background: #fdf6ec;
            color: #e6a23c;
          }

          &.error {
            background: #fef0f0;
            color: #f56c6c;
          }
        }
      }
    }

    .main-progress {
      .progress-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;
        font-size: 14px;

        .percentage {
          font-size: 24px;
          font-weight: 600;
          color: #409eff;
        }

        .phase {
          color: #606266;
        }

        .eta {
          color: #909399;
        }
      }
    }
  }

  .progress-details {
    margin-bottom: 24px;

    .detail-card {
      padding: 16px;
      background: #f8f9fa;
      border-radius: 6px;
      height: 100%;

      h4 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 14px;
        font-weight: 600;
      }

      .character-stats {
        display: flex;
        gap: 16px;
        margin-bottom: 16px;

        .stat-item {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .label {
            font-size: 12px;
            color: #909399;
          }

          .value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;

            &.current-char {
              color: #409eff;
              font-size: 20px;
            }
          }
        }
      }

      .character-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
        gap: 8px;

        .character-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 8px;
          background: #fff;
          border-radius: 4px;
          border: 1px solid #e4e7ed;

          &.pending {
            opacity: 0.6;
          }

          &.training {
            border-color: #409eff;
            background: #ecf5ff;
          }

          &.completed {
            border-color: #67c23a;
            background: #f0f9ff;
          }

          &.failed {
            border-color: #f56c6c;
            background: #fef0f0;
          }

          .character {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 4px;
          }

          .mini-progress {
            width: 100%;
            height: 3px;
            background: #e4e7ed;
            border-radius: 2px;
            overflow: hidden;

            .progress-bar {
              height: 100%;
              background: #409eff;
              transition: width 0.3s ease;
            }
          }
        }
      }

      .performance-metrics {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .metric-item {
          display: flex;
          align-items: center;
          gap: 8px;

          .el-icon {
            color: #409eff;
          }

          .metric-info {
            display: flex;
            flex-direction: column;
            gap: 2px;

            .label {
              font-size: 12px;
              color: #909399;
            }

            .value {
              font-size: 14px;
              font-weight: 600;
              color: #303133;
            }
          }
        }
      }
    }
  }

  .anomaly-alerts {
    margin-bottom: 24px;

    h4 {
      margin: 0 0 12px 0;
      color: #303133;
      font-size: 14px;
      font-weight: 600;
    }

    .alert-list {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .alert-details {
        display: flex;
        gap: 12px;
        font-size: 12px;

        .timestamp {
          color: #909399;
        }

        .type {
          color: #606266;
        }
      }
    }
  }

  .phase-details {
    h4 {
      margin: 0 0 12px 0;
      color: #303133;
      font-size: 14px;
      font-weight: 600;
    }

    .phase-info {
      .phase-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .phase-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .phase-status {
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: 500;

          &.pending {
            background: #f4f4f5;
            color: #909399;
          }

          &.running {
            background: #ecf5ff;
            color: #409eff;
          }

          &.completed {
            background: #f0f9ff;
            color: #67c23a;
          }

          &.failed {
            background: #fef0f0;
            color: #f56c6c;
          }
        }
      }

      .phase-description {
        margin: 0 0 12px 0;
        color: #606266;
        font-size: 14px;
      }
    }
  }
}
</style>