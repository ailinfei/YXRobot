<template>
  <div class="font-package-progress-integration">
    <div class="integration-header">
      <h2>字体包训练进度监控</h2>
      <p>集成了实时进度监控的字体包管理界面</p>
    </div>

    <div class="integration-content">
      <!-- 字体包列表 -->
      <div class="package-list">
        <h3>字体包列表</h3>
        <div class="package-grid">
          <div 
            v-for="pkg in fontPackages" 
            :key="pkg.id"
            class="package-card"
            :class="{ active: selectedPackage?.id === pkg.id }"
            @click="selectPackage(pkg)"
          >
            <div class="package-info">
              <h4>{{ pkg.name }}</h4>
              <p>{{ pkg.description }}</p>
              <div class="package-meta">
                <el-tag :type="getStatusType(pkg.status)" size="small">
                  {{ getStatusText(pkg.status) }}
                </el-tag>
                <span class="package-version">{{ pkg.version }}</span>
              </div>
            </div>
            
            <div class="package-progress" v-if="pkg.trainingProgress">
              <el-progress 
                :percentage="pkg.trainingProgress.percentage" 
                :stroke-width="4"
                :show-text="false"
              />
              <span class="progress-text">{{ pkg.trainingProgress.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 进度监控区域 -->
      <div class="progress-monitor-section" v-if="selectedPackage">
        <div class="monitor-header">
          <h3>{{ selectedPackage.name }} - 训练监控</h3>
          <div class="monitor-actions">
            <el-button 
              v-if="!isMonitoring"
              type="primary"
              @click="startMonitoring"
              :loading="isStartingMonitor"
            >
              <el-icon><VideoPlay /></el-icon>
              开始监控
            </el-button>
            
            <el-button 
              v-else
              type="warning"
              @click="stopMonitoring"
              :loading="isStoppingMonitor"
            >
              <el-icon><VideoPause /></el-icon>
              停止监控
            </el-button>
            
            <el-button 
              type="info"
              @click="refreshPackageList"
              :loading="isRefreshing"
            >
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>

        <!-- 可视化进度监控组件 -->
        <VisualProgressMonitor
          :package-id="selectedPackage.id"
          :auto-start="false"
          @monitoring-started="handleMonitoringStarted"
          @monitoring-stopped="handleMonitoringStopped"
          @anomaly-resolved="handleAnomalyResolved"
        />
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else>
        <el-empty description="请选择一个字体包开始监控">
          <el-button type="primary" @click="createNewPackage">
            <el-icon><Plus /></el-icon>
            创建新字体包
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 创建字体包对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建字体包"
      width="80%"
      :close-on-click-modal="false"
    >
      <FontPackageWizard
        v-model="showCreateDialog"
        @complete="handlePackageCreated"
        @cancel="showCreateDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay, VideoPause, Refresh, Plus } from '@element-plus/icons-vue'
import VisualProgressMonitor from './VisualProgressMonitor.vue'
import FontPackageWizard from './FontPackageWizard.vue'
import { useUserExperienceOptimization } from '@/composables/useUserExperienceOptimization'
import { errorHandler } from '@/services/errorHandlingService'
import { performanceOptimizer } from '@/services/performanceOptimizationService'
import type { FontPackage, AnomalyAlert } from '@/types/fontPackage'

// 使用用户体验优化
const {
  isLoading,
  startLoading,
  stopLoading,
  showError,
  showSuccess,
  showWarning,
  registerShortcut,
  debounce,
  throttle,
  retryOperation
} = useUserExperienceOptimization()

// 响应式数据
const fontPackages = ref<FontPackage[]>([])
const selectedPackage = ref<FontPackage | null>(null)
const isMonitoring = ref(false)
const isStartingMonitor = ref(false)
const isStoppingMonitor = ref(false)
const isRefreshing = ref(false)
const showCreateDialog = ref(false)

// 方法
const getStatusType = (status: string) => {
  const typeMap = {
    draft: 'info',
    training: 'warning',
    completed: 'success',
    failed: 'danger',
    published: 'success'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getStatusText = (status: string) => {
  const textMap = {
    draft: '草稿',
    training: '训练中',
    completed: '已完成',
    failed: '失败',
    published: '已发布'
  }
  return textMap[status as keyof typeof textMap] || status
}

const selectPackage = (pkg: FontPackage) => {
  selectedPackage.value = pkg
  
  // 如果选择的包正在训练，自动开始监控
  if (pkg.status === 'training' && !isMonitoring.value) {
    startMonitoring()
  }
}

const startMonitoring = async () => {
  if (!selectedPackage.value) return
  
  isStartingMonitor.value = true
  startLoading('正在启动监控...')
  
  try {
    await retryOperation(async () => {
      // 这里可以调用实际的监控启动API
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (Math.random() < 0.1) { // 10%概率模拟失败
        throw new Error('监控启动失败')
      }
    })
    
    isMonitoring.value = true
    showSuccess('监控已启动', `正在监控字体包: ${selectedPackage.value.name}`)
  } catch (error) {
    errorHandler.handleError(error as Error, {
      component: 'FontPackageProgressIntegration',
      action: 'startMonitoring'
    })
  } finally {
    isStartingMonitor.value = false
    stopLoading()
  }
}

const stopMonitoring = async () => {
  isStoppingMonitor.value = true
  startLoading('正在停止监控...')
  
  try {
    // 这里可以调用实际的监控停止API
    await new Promise(resolve => setTimeout(resolve, 500))
    
    isMonitoring.value = false
    showSuccess('监控已停止')
  } catch (error) {
    errorHandler.handleError(error as Error, {
      component: 'FontPackageProgressIntegration',
      action: 'stopMonitoring'
    })
  } finally {
    isStoppingMonitor.value = false
    stopLoading()
  }
}

// 使用防抖优化刷新操作
const refreshPackageList = debounce(async () => {
  isRefreshing.value = true
  startLoading('正在刷新列表...')
  
  try {
    await performanceOptimizer.measureRenderTime('packageList', async () => {
      await loadFontPackages()
    })
    showSuccess('列表已刷新')
  } catch (error) {
    errorHandler.handleError(error as Error, {
      component: 'FontPackageProgressIntegration',
      action: 'refreshPackageList'
    })
  } finally {
    isRefreshing.value = false
    stopLoading()
  }
}, 1000)

const createNewPackage = () => {
  showCreateDialog.value = true
}

const handlePackageCreated = (data: any) => {
  showCreateDialog.value = false
  
  // 创建新的字体包记录
  const newPackage: FontPackage = {
    id: Date.now(),
    name: data.basicInfo.name,
    description: data.basicInfo.description,
    version: data.basicInfo.version,
    fontType: data.basicInfo.fontType,
    difficulty: data.basicInfo.difficulty,
    tags: data.basicInfo.tags,
    status: 'training',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    createdBy: 'current-user',
    targetCharacters: data.sampleUpload.targetCharacters,
    trainingProgress: {
      percentage: 0,
      currentPhase: {
        name: '准备中',
        description: '正在初始化训练环境',
        status: 'pending',
        progress: 0
      },
      estimatedTimeRemaining: 0,
      charactersCompleted: 0,
      charactersTotal: data.sampleUpload.targetCharacters.length,
      currentCharacter: '',
      startTime: new Date().toISOString(),
      logs: []
    }
  }
  
  fontPackages.value.unshift(newPackage)
  selectedPackage.value = newPackage
  
  ElMessage.success('字体包创建成功，开始训练')
  
  // 自动开始监控
  startMonitoring()
}

const handleMonitoringStarted = (packageId: number) => {
  console.log(`开始监控字体包 ${packageId}`)
}

const handleMonitoringStopped = () => {
  console.log('停止监控')
}

const handleAnomalyResolved = (anomaly: AnomalyAlert) => {
  showSuccess(`异常已解决: ${anomaly.message}`, '系统运行正常')
}

// 加载字体包列表
const loadFontPackages = async () => {
  // 模拟加载数据
  const mockPackages: FontPackage[] = [
    {
      id: 1,
      name: '楷书字体包',
      description: '传统楷书风格字体包',
      version: 'v1.0.0',
      fontType: 'kaishu',
      difficulty: 3,
      tags: ['楷书', '传统'],
      status: 'training',
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:30:00Z',
      createdBy: 'user1',
      targetCharacters: ['一', '二', '三', '四', '五'],
      trainingProgress: {
        percentage: 45,
        currentPhase: {
          name: '模型训练',
          description: '正在训练字体生成模型',
          status: 'running',
          progress: 45
        },
        estimatedTimeRemaining: 1800,
        charactersCompleted: 2,
        charactersTotal: 5,
        currentCharacter: '三',
        startTime: '2024-01-01T10:00:00Z',
        logs: []
      }
    },
    {
      id: 2,
      name: '行书字体包',
      description: '流畅行书风格字体包',
      version: 'v1.0.0',
      fontType: 'xingshu',
      difficulty: 4,
      tags: ['行书', '流畅'],
      status: 'completed',
      createdAt: '2024-01-01T09:00:00Z',
      updatedAt: '2024-01-01T11:00:00Z',
      createdBy: 'user1',
      targetCharacters: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十'],
      trainingProgress: {
        percentage: 100,
        currentPhase: {
          name: '训练完成',
          description: '字体包训练已完成',
          status: 'completed',
          progress: 100
        },
        estimatedTimeRemaining: 0,
        charactersCompleted: 10,
        charactersTotal: 10,
        currentCharacter: '',
        startTime: '2024-01-01T09:00:00Z',
        logs: []
      }
    },
    {
      id: 3,
      name: '隶书字体包',
      description: '古典隶书风格字体包',
      version: 'v1.0.0',
      fontType: 'lishu',
      difficulty: 5,
      tags: ['隶书', '古典'],
      status: 'draft',
      createdAt: '2024-01-01T08:00:00Z',
      updatedAt: '2024-01-01T08:30:00Z',
      createdBy: 'user1',
      targetCharacters: ['一', '二', '三']
    }
  ]
  
  fontPackages.value = mockPackages
}

// 生命周期
onMounted(() => {
  // 注册快捷键
  registerShortcut('ctrl+r', refreshPackageList, '刷新字体包列表')
  registerShortcut('ctrl+n', createNewPackage, '创建新字体包')
  registerShortcut('space', () => {
    if (selectedPackage.value) {
      if (isMonitoring.value) {
        stopMonitoring()
      } else {
        startMonitoring()
      }
    }
  }, '开始/停止监控')
  
  // 加载数据
  performanceOptimizer.measureRenderTime('initial-load', () => {
    loadFontPackages()
  })
})
</script>

<style scoped lang="scss">
.font-package-progress-integration {
  padding: 20px;

  .integration-header {
    margin-bottom: 24px;
    text-align: center;

    h2 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 24px;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: #606266;
      font-size: 14px;
    }
  }

  .integration-content {
    .package-list {
      margin-bottom: 32px;

      h3 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .package-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 16px;

        .package-card {
          padding: 16px;
          background: #fff;
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            border-color: #409eff;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
          }

          &.active {
            border-color: #409eff;
            background: #ecf5ff;
          }

          .package-info {
            margin-bottom: 12px;

            h4 {
              margin: 0 0 4px 0;
              color: #303133;
              font-size: 16px;
              font-weight: 600;
            }

            p {
              margin: 0 0 8px 0;
              color: #606266;
              font-size: 14px;
            }

            .package-meta {
              display: flex;
              justify-content: space-between;
              align-items: center;

              .package-version {
                color: #909399;
                font-size: 12px;
              }
            }
          }

          .package-progress {
            display: flex;
            align-items: center;
            gap: 8px;

            .progress-text {
              font-size: 12px;
              color: #606266;
              min-width: 35px;
            }
          }
        }
      }
    }

    .progress-monitor-section {
      .monitor-header {
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

        .monitor-actions {
          display: flex;
          gap: 8px;
        }
      }
    }

    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 400px;
    }
  }
}
</style>