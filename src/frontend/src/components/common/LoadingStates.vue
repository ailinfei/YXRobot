<template>
  <div class="loading-states">
    <!-- 全屏加载 -->
    <div v-if="type === 'fullscreen'" class="fullscreen-loading">
      <div class="loading-content">
        <el-icon class="loading-icon" :size="40">
          <Loading />
        </el-icon>
        <p class="loading-text">{{ text || '加载中...' }}</p>
        <div v-if="showProgress" class="loading-progress">
          <el-progress :percentage="progress" :show-text="false" />
          <span class="progress-text">{{ progress }}%</span>
        </div>
      </div>
    </div>

    <!-- 页面加载 -->
    <div v-else-if="type === 'page'" class="page-loading">
      <el-skeleton :rows="skeletonRows" animated />
    </div>

    <!-- 卡片加载 -->
    <div v-else-if="type === 'card'" class="card-loading">
      <el-card shadow="hover">
        <el-skeleton :rows="3" animated>
          <template #template>
            <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
            <div style="padding: 14px">
              <el-skeleton-item variant="h3" style="width: 50%" />
              <div style="display: flex; align-items: center; justify-items: space-between; margin-top: 16px; height: 16px">
                <el-skeleton-item variant="text" style="margin-right: 16px" />
                <el-skeleton-item variant="text" style="width: 30%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-card>
    </div>

    <!-- 表格加载 -->
    <div v-else-if="type === 'table'" class="table-loading">
      <el-table :data="[]" style="width: 100%" v-loading="true" element-loading-text="数据加载中...">
        <el-table-column v-for="col in tableColumns" :key="col.prop" :prop="col.prop" :label="col.label" />
      </el-table>
    </div>

    <!-- 列表加载 -->
    <div v-else-if="type === 'list'" class="list-loading">
      <div v-for="i in listItems" :key="i" class="list-item-skeleton">
        <el-skeleton :rows="1" animated>
          <template #template>
            <div style="display: flex; align-items: center">
              <el-skeleton-item variant="circle" style="width: 40px; height: 40px; margin-right: 16px" />
              <div style="flex: 1">
                <el-skeleton-item variant="h3" style="width: 40%; margin-bottom: 8px" />
                <el-skeleton-item variant="text" style="width: 80%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <!-- 图表加载 -->
    <div v-else-if="type === 'chart'" class="chart-loading">
      <div class="chart-skeleton">
        <el-skeleton :rows="0" animated>
          <template #template>
            <div class="chart-placeholder">
              <el-icon class="chart-icon" :size="60">
                <TrendCharts />
              </el-icon>
              <p class="chart-text">图表加载中...</p>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <!-- 按钮加载 -->
    <el-button
      v-else-if="type === 'button'"
      :loading="true"
      :type="buttonType"
      :size="buttonSize"
      :disabled="true"
    >
      {{ text || '处理中...' }}
    </el-button>

    <!-- 默认加载 -->
    <div v-else class="default-loading">
      <el-icon class="loading-icon">
        <Loading />
      </el-icon>
      <span class="loading-text">{{ text || '加载中...' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Loading, TrendCharts } from '@element-plus/icons-vue'

interface LoadingStatesProps {
  type?: 'fullscreen' | 'page' | 'card' | 'table' | 'list' | 'chart' | 'button' | 'default'
  text?: string
  showProgress?: boolean
  progress?: number
  skeletonRows?: number
  tableColumns?: Array<{ prop: string; label: string }>
  listItems?: number
  buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  buttonSize?: 'large' | 'default' | 'small'
}

const props = withDefaults(defineProps<LoadingStatesProps>(), {
  type: 'default',
  showProgress: false,
  progress: 0,
  skeletonRows: 5,
  listItems: 5,
  buttonType: 'primary',
  buttonSize: 'default'
})

// 计算属性
const tableColumns = computed(() => {
  return props.tableColumns || [
    { prop: 'name', label: '名称' },
    { prop: 'status', label: '状态' },
    { prop: 'date', label: '日期' },
    { prop: 'action', label: '操作' }
  ]
})
</script>

<style lang="scss" scoped>
.loading-states {
  .fullscreen-loading {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;

    .loading-content {
      text-align: center;

      .loading-icon {
        color: var(--el-color-primary);
        animation: rotate 2s linear infinite;
        margin-bottom: 16px;
      }

      .loading-text {
        font-size: 16px;
        color: var(--el-text-color-primary);
        margin: 0 0 16px 0;
      }

      .loading-progress {
        width: 200px;
        display: flex;
        align-items: center;
        gap: 12px;

        .progress-text {
          font-size: 14px;
          color: var(--el-text-color-regular);
          min-width: 40px;
        }
      }
    }
  }

  .page-loading {
    padding: 20px;
  }

  .card-loading {
    margin: 16px 0;
  }

  .table-loading {
    margin: 16px 0;
  }

  .list-loading {
    .list-item-skeleton {
      padding: 16px;
      border-bottom: 1px solid var(--el-border-color-lighter);

      &:last-child {
        border-bottom: none;
      }
    }
  }

  .chart-loading {
    .chart-skeleton {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--el-bg-color-page);
      border-radius: var(--el-border-radius-base);

      .chart-placeholder {
        text-align: center;

        .chart-icon {
          color: var(--el-text-color-placeholder);
          margin-bottom: 16px;
        }

        .chart-text {
          color: var(--el-text-color-regular);
          font-size: 14px;
          margin: 0;
        }
      }
    }
  }

  .default-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;

    .loading-icon {
      color: var(--el-color-primary);
      animation: rotate 2s linear infinite;
      margin-right: 8px;
    }

    .loading-text {
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>