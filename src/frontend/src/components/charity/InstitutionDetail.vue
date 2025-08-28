<template>
  <div class="institution-detail" v-if="institution">
    <!-- 机构基本信息 -->
    <div class="detail-section">
      <div class="section-header">
        <div class="institution-title">
          <el-icon class="type-icon" :class="getTypeIconClass(institution.type)">
            <component :is="getTypeIcon(institution.type)" />
          </el-icon>
          <h3>{{ institution.name }}</h3>
          <el-tag :type="getStatusTagType(institution.status)" size="large">
            {{ getStatusText(institution.status) }}
          </el-tag>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑信息
          </el-button>
          <el-button @click="handleContact">
            <el-icon><Phone /></el-icon>
            联系机构
          </el-button>
        </div>
      </div>

      <el-row :gutter="24" class="info-grid">
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">机构类型</div>
            <div class="info-value">
              <el-tag :type="getTypeTagType(institution.type)">
                {{ getTypeText(institution.type) }}
              </el-tag>
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">所在地区</div>
            <div class="info-value">{{ institution.location }}</div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">合作时间</div>
            <div class="info-value">{{ formatDate(institution.cooperationDate) }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 联系信息 -->
    <div class="detail-section">
      <h4 class="section-title">联系信息</h4>
      <el-row :gutter="24">
        <el-col :span="12">
          <div class="contact-info">
            <div class="contact-item">
              <el-icon><User /></el-icon>
              <div class="contact-content">
                <div class="contact-label">联系人</div>
                <div class="contact-value">{{ institution.contactPerson }}</div>
              </div>
            </div>
            
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <div class="contact-content">
                <div class="contact-label">联系电话</div>
                <div class="contact-value">
                  <el-link type="primary" @click="handleCall(institution.contactPhone)">
                    {{ institution.contactPhone }}
                  </el-link>
                </div>
              </div>
            </div>
            
            <div class="contact-item" v-if="institution.email">
              <el-icon><Message /></el-icon>
              <div class="contact-content">
                <div class="contact-label">电子邮箱</div>
                <div class="contact-value">
                  <el-link type="primary" @click="handleEmail(institution.email)">
                    {{ institution.email }}
                  </el-link>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="address-info">
            <div class="address-item">
              <el-icon><Location /></el-icon>
              <div class="address-content">
                <div class="address-label">详细地址</div>
                <div class="address-value">{{ institution.address }}</div>
                <el-button type="primary" text size="small" @click="handleViewMap">
                  <el-icon><Position /></el-icon>
                  查看地图
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 统计数据 -->
    <div class="detail-section">
      <h4 class="section-title">统计数据</h4>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon student">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ institution.studentCount }}</div>
              <div class="stat-label">受益学生</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon device">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ institution.deviceCount }}</div>
              <div class="stat-label">配置设备</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon cooperation">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ cooperationDays }}</div>
              <div class="stat-label">合作天数</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon visit">
              <el-icon><View /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ daysSinceLastVisit }}</div>
              <div class="stat-label">距上次探访</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 合作历史 -->
    <div class="detail-section">
      <h4 class="section-title">合作历史</h4>
      <el-timeline>
        <el-timeline-item
          v-for="(event, index) in cooperationHistory"
          :key="index"
          :timestamp="event.date"
          :type="event.type"
        >
          <div class="timeline-content">
            <h5>{{ event.title }}</h5>
            <p>{{ event.description }}</p>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 备注信息 -->
    <div class="detail-section" v-if="institution.notes">
      <h4 class="section-title">备注信息</h4>
      <div class="notes-content">
        <p>{{ institution.notes }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Edit,
  Phone,
  User,
  Message,
  Location,
  Position,
  Monitor,
  Calendar,
  View,
  School,
  OfficeBuilding,
  House,
  Reading,
  FirstAidKit
} from '@element-plus/icons-vue'
import type { CharityInstitution } from '@/api/mock/charity'

interface InstitutionDetailProps {
  institution: CharityInstitution | null
}

const props = defineProps<InstitutionDetailProps>()

const emit = defineEmits<{
  edit: []
  close: []
}>()

// 计算属性
const cooperationDays = computed(() => {
  if (!props.institution?.cooperationDate) return 0
  const startDate = new Date(props.institution.cooperationDate)
  const today = new Date()
  const diffTime = Math.abs(today.getTime() - startDate.getTime())
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

const daysSinceLastVisit = computed(() => {
  if (!props.institution?.lastVisitDate) return '未探访'
  const lastVisit = new Date(props.institution.lastVisitDate)
  const today = new Date()
  const diffTime = Math.abs(today.getTime() - lastVisit.getTime())
  const days = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return `${days}天前`
})

const cooperationHistory = computed(() => {
  if (!props.institution) return []
  
  return [
    {
      date: props.institution.cooperationDate,
      title: '开始合作',
      description: `与${props.institution.name}正式建立合作关系`,
      type: 'primary'
    },
    {
      date: props.institution.lastVisitDate || props.institution.cooperationDate,
      title: '实地探访',
      description: '进行实地探访，了解合作进展和需求',
      type: 'success'
    },
    {
      date: new Date().toISOString().slice(0, 10),
      title: '当前状态',
      description: `合作状态：${getStatusText(props.institution.status)}`,
      type: props.institution.status === 'active' ? 'success' : 'warning'
    }
  ]
})

// 方法
const handleEdit = () => {
  emit('edit')
}

const handleContact = () => {
  if (props.institution) {
    ElMessage.info(`联系 ${props.institution.name}：${props.institution.contactPhone}`)
  }
}

const handleCall = (phone: string) => {
  ElMessage.info(`拨打电话：${phone}`)
}

const handleEmail = (email: string) => {
  ElMessage.info(`发送邮件至：${email}`)
}

const handleViewMap = () => {
  if (props.institution) {
    ElMessage.info(`查看 ${props.institution.name} 的地图位置`)
  }
}

// 工具方法
const getTypeIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    school: School,
    community: OfficeBuilding,
    orphanage: House,
    library: Reading,
    hospital: FirstAidKit
  }
  return iconMap[type] || OfficeBuilding
}

const getTypeIconClass = (type: string) => {
  return `type-${type}`
}

const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    school: '学校',
    community: '社区中心',
    orphanage: '福利院',
    library: '图书馆',
    hospital: '医院'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    school: 'primary',
    community: 'success',
    orphanage: 'warning',
    library: 'info',
    hospital: 'danger'
  }
  return typeMap[type] || ''
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    active: '合作中',
    inactive: '暂停合作',
    pending: '待审核'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    active: 'success',
    inactive: 'info',
    pending: 'warning'
  }
  return typeMap[status] || ''
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}
</script>

<style lang="scss" scoped>
.institution-detail {
  .detail-section {
    margin-bottom: 32px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .institution-title {
        display: flex;
        align-items: center;
        gap: 12px;

        .type-icon {
          font-size: 24px;

          &.type-school {
            color: #409EFF;
          }

          &.type-community {
            color: #67C23A;
          }

          &.type-orphanage {
            color: #E6A23C;
          }

          &.type-library {
            color: #909399;
          }

          &.type-hospital {
            color: #F56C6C;
          }
        }

        h3 {
          margin: 0;
          font-size: 20px;
          font-weight: 600;
          color: #303133;
        }
      }

      .action-buttons {
        display: flex;
        gap: 12px;
      }
    }

    .section-title {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      border-bottom: 2px solid #f0f0f0;
      padding-bottom: 8px;
    }

    .info-grid {
      .info-card {
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        text-align: center;

        .info-label {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
        }

        .info-value {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
        }
      }
    }

    .contact-info {
      .contact-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .el-icon {
          font-size: 18px;
          color: #409EFF;
          margin-top: 2px;
        }

        .contact-content {
          flex: 1;

          .contact-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 4px;
          }

          .contact-value {
            font-size: 14px;
            color: #303133;
            font-weight: 500;
          }
        }
      }
    }

    .address-info {
      .address-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;

        .el-icon {
          font-size: 18px;
          color: #67C23A;
          margin-top: 2px;
        }

        .address-content {
          flex: 1;

          .address-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 4px;
          }

          .address-value {
            font-size: 14px;
            color: #303133;
            font-weight: 500;
            margin-bottom: 8px;
            line-height: 1.5;
          }
        }
      }
    }

    .stat-card {
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

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;

        &.student {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
        }

        &.device {
          background: linear-gradient(135deg, #67C23A, #85CE61);
          color: white;
        }

        &.cooperation {
          background: linear-gradient(135deg, #E6A23C, #ELBF73);
          color: white;
        }

        &.visit {
          background: linear-gradient(135deg, #F56C6C, #F78989);
          color: white;
        }
      }

      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }

    .timeline-content {
      h5 {
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }

      p {
        margin: 0;
        font-size: 13px;
        color: #606266;
        line-height: 1.5;
      }
    }

    .notes-content {
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;
      border-left: 4px solid #409EFF;

      p {
        margin: 0;
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .institution-detail {
    .detail-section {
      .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;

        .action-buttons {
          width: 100%;
          justify-content: flex-end;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .institution-detail {
    .detail-section {
      .section-header {
        .institution-title {
          flex-direction: column;
          align-items: flex-start;
          gap: 8px;

          h3 {
            font-size: 18px;
          }
        }

        .action-buttons {
          flex-direction: column;
          width: 100%;

          .el-button {
            width: 100%;
          }
        }
      }

      .info-grid,
      .contact-info,
      .address-info {
        .el-col {
          margin-bottom: 16px;
        }
      }

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