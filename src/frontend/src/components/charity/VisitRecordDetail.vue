<template>
  <div class="visit-record-detail" v-if="record">
    <!-- 记录头部信息 -->
    <div class="record-header">
      <div class="header-left">
        <h3>{{ record.title }}</h3>
        <el-tag :type="getRecordTagType(record.type)" size="large">
          {{ getRecordTypeText(record.type) }}
        </el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑记录
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
              <div class="info-label">探访时间</div>
              <div class="info-value">{{ formatDateTime(record.visitDate) }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">探访时长</div>
              <div class="info-value">{{ record.duration }}小时</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">参与人数</div>
              <div class="info-value">{{ record.participantCount }}人</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="info-card">
            <div class="info-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">探访人员</div>
              <div class="info-value">{{ record.visitors.length }}人</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 探访人员 -->
    <div class="detail-section">
      <h4 class="section-title">探访人员</h4>
      <div class="visitors-list">
        <el-tag
          v-for="visitor in record.visitors"
          :key="visitor"
          type="primary"
          size="large"
          class="visitor-tag"
        >
          <el-icon><User /></el-icon>
          {{ visitor }}
        </el-tag>
      </div>
    </div>

    <!-- 探访描述 -->
    <div class="detail-section">
      <h4 class="section-title">探访描述</h4>
      <div class="description-content">
        <p>{{ record.description }}</p>
      </div>
    </div>

    <!-- 探访照片 -->
    <div class="detail-section" v-if="record.photos && record.photos.length > 0">
      <h4 class="section-title">探访照片</h4>
      <div class="photos-gallery">
        <div
          v-for="(photo, index) in record.photos"
          :key="index"
          class="photo-item"
          @click="handlePreviewPhoto(record.photos, index)"
        >
          <img :src="photo" :alt="`探访照片${index + 1}`" />
          <div class="photo-overlay">
            <el-icon><ZoomIn /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 反馈意见 -->
    <div class="detail-section" v-if="record.feedback">
      <h4 class="section-title">反馈意见</h4>
      <div class="feedback-content">
        <div class="feedback-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="feedback-text">
          <p>{{ record.feedback }}</p>
        </div>
      </div>
    </div>

    <!-- 记录信息 -->
    <div class="detail-section">
      <h4 class="section-title">记录信息</h4>
      <div class="record-meta">
        <div class="meta-item">
          <span class="meta-label">创建时间：</span>
          <span class="meta-value">{{ formatDateTime(record.createdAt) }}</span>
        </div>
        <div class="meta-item" v-if="record.updatedAt !== record.createdAt">
          <span class="meta-label">更新时间：</span>
          <span class="meta-value">{{ formatDateTime(record.updatedAt) }}</span>
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
import { ref } from 'vue'
import {
  Edit,
  Calendar,
  Clock,
  UserFilled,
  User,
  ZoomIn,
  ChatDotRound
} from '@element-plus/icons-vue'

interface VisitRecord {
  id: number
  title: string
  type: string
  visitDate: string
  visitors: string[]
  duration: number
  participantCount: number
  description: string
  photos?: string[]
  feedback?: string
  createdAt: string
  updatedAt: string
}

interface VisitRecordDetailProps {
  record: VisitRecord | null
}

const props = defineProps<VisitRecordDetailProps>()

const emit = defineEmits<{
  edit: []
  close: []
}>()

// 响应式数据
const previewVisible = ref(false)
const previewImages = ref<string[]>([])
const previewIndex = ref(0)

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
const getRecordTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: '常规探访',
    special: '特殊活动',
    emergency: '紧急探访',
    evaluation: '评估探访'
  }
  return typeMap[type] || type
}

const getRecordTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: 'primary',
    special: 'success',
    emergency: 'danger',
    evaluation: 'warning'
  }
  return typeMap[type] || ''
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
.visit-record-detail {
  .record-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      h3 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
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
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }

    .visitors-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;

      .visitor-tag {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        font-size: 14px;
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

    .record-meta {
      display: flex;
      flex-direction: column;
      gap: 8px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;

      .meta-item {
        display: flex;
        align-items: center;
        font-size: 14px;

        .meta-label {
          color: #909399;
          margin-right: 8px;
        }

        .meta-value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .visit-record-detail {
    .record-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .detail-section {
      .photos-gallery {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      }
    }
  }
}

@media (max-width: 768px) {
  .visit-record-detail {
    .record-header {
      .header-left {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;

        h3 {
          font-size: 18px;
        }
      }

      .header-right {
        .el-button {
          width: 100%;
        }
      }
    }

    .detail-section {
      .info-card {
        padding: 12px;

        .info-icon {
          width: 32px;
          height: 32px;
          font-size: 16px;
        }

        .info-content {
          .info-value {
            font-size: 14px;
          }
        }
      }

      .visitors-list {
        .visitor-tag {
          padding: 6px 12px;
          font-size: 13px;
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