<!--
  固件版本差异展示组件
  功能：显示当前版本与最新版本的差异对比
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="版本差异对比"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="version-difference" v-loading="loading">
      <div v-if="differenceData" class="difference-content">
        <!-- 版本信息对比 -->
        <div class="version-comparison">
          <div class="version-item current">
            <div class="version-header">
              <h3>当前版本</h3>
              <el-tag type="info" size="large">{{ differenceData.currentVersion }}</el-tag>
            </div>
            <div class="version-status">
              <el-icon><Clock /></el-icon>
              <span>正在使用</span>
            </div>
          </div>

          <div class="version-arrow">
            <el-icon><Right /></el-icon>
          </div>

          <div class="version-item latest">
            <div class="version-header">
              <h3>最新版本</h3>
              <el-tag type="success" size="large">{{ differenceData.latestVersion }}</el-tag>
            </div>
            <div class="version-status">
              <el-icon><Star /></el-icon>
              <span>推荐更新</span>
            </div>
          </div>
        </div>

        <!-- 更新收益 -->
        <div class="benefits-section" v-if="differenceData.benefits.length > 0">
          <div class="section-header">
            <h3>
              <el-icon><CircleCheckFilled /></el-icon>
              更新收益
            </h3>
          </div>
          <div class="benefits-list">
            <div
              v-for="(benefit, index) in differenceData.benefits"
              :key="index"
              class="benefit-item"
            >
              <el-icon class="benefit-icon"><CircleCheckFilled /></el-icon>
              <span class="benefit-text">{{ benefit }}</span>
            </div>
          </div>
        </div>

        <!-- 更新日志 -->
        <div class="changelog-section">
          <div class="section-header">
            <h3>
              <el-icon><DocumentCopy /></el-icon>
              更新日志
            </h3>
          </div>
          <div class="changelog-list">
            <div
              v-for="(item, index) in differenceData.changelog"
              :key="index"
              class="changelog-item"
            >
              <el-icon class="changelog-icon"><Plus /></el-icon>
              <span class="changelog-text">{{ item }}</span>
            </div>
          </div>
        </div>

        <!-- 潜在风险 -->
        <div class="risks-section" v-if="differenceData.risks.length > 0">
          <div class="section-header">
            <h3>
              <el-icon><WarningFilled /></el-icon>
              注意事项
            </h3>
          </div>
          <div class="risks-list">
            <el-alert
              v-for="(risk, index) in differenceData.risks"
              :key="index"
              :title="risk"
              type="warning"
              show-icon
              :closable="false"
              class="risk-alert"
            />
          </div>
        </div>

        <!-- 更新建议 -->
        <div class="recommendation-section">
          <div class="section-header">
            <h3>
              <el-icon><Star /></el-icon>
              更新建议
            </h3>
          </div>
          <div class="recommendation-content">
            <div class="recommendation-card">
              <div class="card-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">推荐立即更新</div>
                <div class="card-description">
                  新版本包含重要的性能优化和安全修复，建议尽快更新以获得最佳体验。
                </div>
              </div>
            </div>

            <div class="recommendation-tips">
              <h4>更新前准备：</h4>
              <ul class="tips-list">
                <li>确保设备电量充足（建议 > 50%）</li>
                <li>确保网络连接稳定</li>
                <li>更新过程中请勿断开设备连接</li>
                <li>更新完成后设备将自动重启</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 更新时间估算 -->
        <div class="time-estimation">
          <div class="estimation-item">
            <el-icon><Timer /></el-icon>
            <span class="estimation-label">预计更新时间：</span>
            <span class="estimation-value">{{ estimatedTime }}</span>
          </div>
          <div class="estimation-item">
            <el-icon><Download /></el-icon>
            <span class="estimation-label">下载大小：</span>
            <span class="estimation-value">{{ estimatedSize }}</span>
          </div>
        </div>
      </div>

      <div v-else class="no-data">
        <el-empty description="暂无版本差异数据" />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmUpdate" :disabled="!differenceData">
          <el-icon><Upload /></el-icon>
          确认更新
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  Clock,
  Right,
  Star,
  CircleCheckFilled,
  DocumentCopy,
  Plus,
  WarningFilled,
  BulbFilled,
  TrendCharts,
  Timer,
  Download,
  Upload
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { firmwareUpdateService } from '@/services/firmwareUpdateService'

// Props
interface Props {
  modelValue: boolean
  deviceId?: string
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'update-device': [deviceId: string]
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const loading = ref(false)
const differenceData = ref<{
  currentVersion: string
  latestVersion: string
  changelog: string[]
  benefits: string[]
  risks: string[]
} | null>(null)

// 计算属性
const estimatedTime = computed(() => {
  if (!differenceData.value) return '未知'
  
  // 根据版本差异估算更新时间
  const versionDiff = differenceData.value.latestVersion.localeCompare(differenceData.value.currentVersion)
  
  if (versionDiff > 0) {
    // 主版本更新
    if (differenceData.value.latestVersion.split('.')[0] !== differenceData.value.currentVersion.split('.')[0]) {
      return '8-12 分钟'
    }
    // 次版本更新
    if (differenceData.value.latestVersion.split('.')[1] !== differenceData.value.currentVersion.split('.')[1]) {
      return '5-8 分钟'
    }
    // 补丁更新
    return '2-5 分钟'
  }
  
  return '3-6 分钟'
})

const estimatedSize = computed(() => {
  if (!differenceData.value) return '未知'
  
  // 根据版本差异估算下载大小
  const versionDiff = differenceData.value.latestVersion.localeCompare(differenceData.value.currentVersion)
  
  if (versionDiff > 0) {
    // 主版本更新
    if (differenceData.value.latestVersion.split('.')[0] !== differenceData.value.currentVersion.split('.')[0]) {
      return '45-60 MB'
    }
    // 次版本更新
    if (differenceData.value.latestVersion.split('.')[1] !== differenceData.value.currentVersion.split('.')[1]) {
      return '25-40 MB'
    }
    // 补丁更新
    return '8-15 MB'
  }
  
  return '20-35 MB'
})

// 方法
const loadVersionDifference = async () => {
  if (!props.deviceId) return

  loading.value = true
  try {
    const data = await firmwareUpdateService.getVersionDifference(props.deviceId)
    differenceData.value = data
    
    if (!data) {
      ElMessage.info('该设备已是最新版本')
    }
  } catch (error) {
    console.error('加载版本差异失败:', error)
    ElMessage.error('加载版本差异失败')
  } finally {
    loading.value = false
  }
}

const confirmUpdate = () => {
  if (!props.deviceId) return
  
  emit('update-device', props.deviceId)
  ElMessage.success('已开始更新设备')
}

const handleClose = () => {
  emit('update:modelValue', false)
  differenceData.value = null
}

// 监听设备ID变化
watch(() => props.deviceId, (newId) => {
  if (newId && dialogVisible.value) {
    loadVersionDifference()
  }
})

// 监听对话框显示状态
watch(dialogVisible, (visible) => {
  if (visible && props.deviceId) {
    loadVersionDifference()
  }
})
</script>

<style lang="scss" scoped>
.version-difference {
  .difference-content {
    .version-comparison {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 32px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;

      .version-item {
        flex: 1;
        text-align: center;

        .version-header {
          margin-bottom: 12px;

          h3 {
            margin: 0 0 8px 0;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
          }
        }

        .version-status {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 6px;
          font-size: 14px;
          color: #606266;

          .el-icon {
            font-size: 16px;
          }
        }

        &.current .version-status {
          color: #909399;
        }

        &.latest .version-status {
          color: #67c23a;
        }
      }

      .version-arrow {
        margin: 0 20px;
        font-size: 24px;
        color: #409eff;
      }
    }

    .benefits-section,
    .changelog-section,
    .risks-section,
    .recommendation-section {
      margin-bottom: 24px;

      .section-header {
        margin-bottom: 16px;

        h3 {
          display: flex;
          align-items: center;
          gap: 8px;
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;

          .el-icon {
            font-size: 18px;
          }
        }
      }
    }

    .benefits-section {
      .section-header h3 {
        color: #67c23a;
      }

      .benefits-list {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .benefit-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 12px;
          background: #f0f9ff;
          border-radius: 4px;

          .benefit-icon {
            color: #67c23a;
            flex-shrink: 0;
          }

          .benefit-text {
            font-size: 14px;
            color: #606266;
          }
        }
      }
    }

    .changelog-section {
      .section-header h3 {
        color: #409eff;
      }

      .changelog-list {
        display: flex;
        flex-direction: column;
        gap: 6px;

        .changelog-item {
          display: flex;
          align-items: flex-start;
          gap: 8px;
          padding: 6px 0;
          font-size: 14px;
          line-height: 1.5;
          color: #606266;

          .changelog-icon {
            color: #409eff;
            margin-top: 2px;
            flex-shrink: 0;
          }

          .changelog-text {
            flex: 1;
          }
        }
      }
    }

    .risks-section {
      .section-header h3 {
        color: #e6a23c;
      }

      .risks-list {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .risk-alert {
          margin: 0;
        }
      }
    }

    .recommendation-section {
      .section-header h3 {
        color: #409eff;
      }

      .recommendation-content {
        .recommendation-card {
          display: flex;
          align-items: flex-start;
          gap: 16px;
          padding: 16px;
          background: #f0f9ff;
          border-radius: 8px;
          border-left: 4px solid #409eff;
          margin-bottom: 16px;

          .card-icon {
            width: 40px;
            height: 40px;
            background: #409eff;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 18px;
            flex-shrink: 0;
          }

          .card-content {
            flex: 1;

            .card-title {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              margin-bottom: 6px;
            }

            .card-description {
              font-size: 14px;
              color: #606266;
              line-height: 1.5;
            }
          }
        }

        .recommendation-tips {
          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #303133;
          }

          .tips-list {
            margin: 0;
            padding-left: 20px;
            color: #606266;

            li {
              font-size: 14px;
              line-height: 1.6;
              margin-bottom: 4px;
            }
          }
        }
      }
    }

    .time-estimation {
      display: flex;
      justify-content: space-around;
      padding: 16px;
      background: #fafafa;
      border-radius: 6px;
      border: 1px solid #e4e7ed;

      .estimation-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;

        .el-icon {
          color: #409eff;
        }

        .estimation-label {
          color: #606266;
        }

        .estimation-value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }

  .no-data {
    text-align: center;
    padding: 40px;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .version-difference {
    .difference-content {
      .version-comparison {
        flex-direction: column;
        gap: 16px;

        .version-arrow {
          transform: rotate(90deg);
          margin: 0;
        }
      }

      .time-estimation {
        flex-direction: column;
        gap: 12px;
        align-items: center;
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