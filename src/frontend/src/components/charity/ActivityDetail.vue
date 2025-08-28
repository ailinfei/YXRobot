<template>
  <div class="activity-detail" v-if="activity">
    <!-- 活动头部信息 -->
    <div class="detail-header">
      <div class="header-left">
        <h3>{{ activity.title }}</h3>
        <div class="header-tags">
          <el-tag :type="getTypeTagType(activity.type)" size="large">
            {{ getTypeText(activity.type) }}
          </el-tag>
          <el-tag :type="getStatusTagType(activity.status)" size="large">
            {{ getStatusText(activity.status) }}
          </el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑活动
        </el-button>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="detail-section">
      <h4 class="section-title">基本信息</h4>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">活动时间</div>
              <div class="info-value">{{ formatDateTime(activity.date) }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><Location /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">活动地点</div>
              <div class="info-value">{{ activity.location }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">参与人数</div>
              <div class="info-value">{{ activity.participants }}人</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">主办方</div>
              <div class="info-value">{{ activity.organizer }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 活动描述 -->
    <div class="detail-section">
      <h4 class="section-title">活动描述</h4>
      <div class="description-content">
        <p>{{ activity.description }}</p>
      </div>
    </div>

    <!-- 财务信息 -->
    <div class="detail-section">
      <h4 class="section-title">财务信息</h4>
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="finance-card budget">
            <div class="finance-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="finance-content">
              <div class="finance-value">¥{{ activity.budget.toLocaleString() }}</div>
              <div class="finance-label">预算金额</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="finance-card actual">
            <div class="finance-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="finance-content">
              <div class="finance-value">¥{{ activity.actualCost.toLocaleString() }}</div>
              <div class="finance-label">实际花费</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="finance-card balance">
            <div class="finance-icon">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="finance-content">
              <div class="finance-value" :class="balanceClass">
                ¥{{ Math.abs(activity.budget - activity.actualCost).toLocaleString() }}
              </div>
              <div class="finance-label">{{ balanceText }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 活动照片 -->
    <div class="detail-section" v-if="activity.photos && activity.photos.length > 0">
      <h4 class="section-title">活动照片</h4>
      <div class="photos-gallery">
        <div
          v-for="(photo, index) in activity.photos"
          :key="index"
          class="photo-item"
          @click="handlePreviewPhoto(activity.photos, index)"
        >
          <img :src="photo" :alt="`活动照片${index + 1}`" />
          <div class="photo-overlay">
            <el-icon><ZoomIn /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 反馈意见 -->
    <div class="detail-section" v-if="activity.feedback">
      <h4 class="section-title">活动反馈</h4>
      <div class="feedback-content">
        <div class="feedback-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="feedback-text">
          <p>{{ activity.feedback }}</p>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewImages"
      :initial-index="previewIndex"
      @close="previewVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Edit,
  Calendar,
  Location,
  User,
  OfficeBuilding,
  Money,
  Wallet,
  DataLine,
  ZoomIn,
  ChatDotRound
} from '@element-plus/icons-vue'
import type { CharityActivity } from '@/api/mock/charity'

interface ActivityDetailProps {
  activity: CharityActivity | null
}

const props = defineProps<ActivityDetailProps>()

const emit = defineEmits<{
  edit: []
  close: []
}>()

// 响应式数据
const previewVisible = ref(false)
const previewImages = ref<string[]>([])
const previewIndex = ref(0)

// 计算属性
const balanceClass = computed(() => {
  if (!props.activity) return ''
  const balance = props.activity.budget - props.activity.actualCost
  return balance >= 0 ? 'positive' : 'negative'
})

const balanceText = computed(() => {
  if (!props.activity) return ''
  const balance = props.activity.budget - props.activity.actualCost
  return balance >= 0 ? '结余金额' : '超支金额'
})

// 方法
const handleEdit = () => {
  emit('edit')
}

const handlePreviewPhoto = (photos: string[], index: number) => {
  previewImages.value = photos
  previewIndex.value = index
  previewVisible.value = true
}

// 工具方法
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: '实地探访',
    training: '培训活动',
    donation: '捐赠活动',
    event: '特殊活动'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: 'primary',
    training: 'success',
    donation: 'warning',
    event: 'info'
  }
  return typeMap[type] || ''
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    planned: '已计划',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    planned: 'info',
    ongoing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || ''
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style lang="scss" scoped>
.activity-detail {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;

    .header-left {
      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }

      .header-tags {
        display: flex;
        gap: 12px;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .detail-section {
    margin-bottom: 32px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      border-bottom: 2px solid #f0f0f0;
      padding-bottom: 8px;
    }

    .info-card {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;
      height: 100%;

      .info-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        background: #409EFF;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
      }

      .info-content {
        flex: 1;

        .info-label {
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }

        .info-value {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
          word-break: break-all;
        }
      }
    }

    .description-content {
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;
      border-left: 4px solid #409EFF;

      p {
        margin: 0;
        font-size: 14px;
        color: #606266;
        line-height: 1.8;
      }
    }

    .finance-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      }

      .finance-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
      }

      &.budget .finance-icon {
        background: linear-gradient(135deg, #E6A23C, #ELBF73);
        color: white;
      }

      &.actual .finance-icon {
        background: linear-gradient(135deg, #67C23A, #85CE61);
        color: white;
      }

      &.balance .finance-icon {
        background: linear-gradient(135deg, #409EFF, #66B1FF);
        color: white;
      }

      .finance-content {
        .finance-value {
          font-size: 20px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;

          &.positive {
            color: #67C23A;
          }

          &.negative {
            color: #F56C6C;
          }
        }

        .finance-label {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }

    .photos-gallery {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;

      .photo-item {
        position: relative;
        aspect-ratio: 4/3;
        border-radius: 8px;
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.02);

          .photo-overlay {
            opacity: 1;
          }
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .photo-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          opacity: 0;
          transition: opacity 0.3s ease;
        }
      }
    }

    .feedback-content {
      display: flex;
      gap: 16px;
      padding: 20px;
      background: #f0f9ff;
      border-radius: 8px;
      border-left: 4px solid #409EFF;

      .feedback-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #409EFF;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        flex-shrink: 0;
      }

      .feedback-text {
        flex: 1;

        p {
          margin: 0;
          font-size: 14px;
          color: #606266;
          line-height: 1.8;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .activity-detail {
    .detail-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
}

@media (max-width: 768px) {
  .activity-detail {
    .detail-header {
      .header-left {
        h3 {
          font-size: 18px;
        }

        .header-tags {
          flex-wrap: wrap;
        }
      }

      .header-right {
        .el-button {
          width: 100%;
        }
      }
    }

    .detail-section {
      .info-card,
      .finance-card {
        padding: 12px;

        .info-icon,
        .finance-icon {
          width: 32px;
          height: 32px;
          font-size: 16px;
        }

        .info-content,
        .finance-content {
          .info-value,
          .finance-value {
            font-size: 13px;
          }
        }
      }

      .photos-gallery {
        grid-template-columns: repeat(2, 1fr);
      }

      .feedback-content {
        flex-direction: column;
        gap: 12px;

        .feedback-icon {
          align-self: flex-start;
        }
      }
    }
  }
}
</style>