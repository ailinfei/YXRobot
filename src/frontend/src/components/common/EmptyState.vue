<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon :size="64">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="empty-title">{{ title }}</div>
    <div class="empty-description" v-if="description">{{ description }}</div>
    <div class="empty-actions" v-if="$slots.actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Box } from '@element-plus/icons-vue'

interface Props {
  icon?: any
  title: string
  description?: string
}

withDefaults(defineProps<Props>(), {
  icon: Box,
  title: '暂无数据',
  description: ''
})
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;

  .empty-icon {
    margin-bottom: 16px;
    color: #c0c4cc;
  }

  .empty-title {
    font-size: 16px;
    font-weight: 500;
    color: #606266;
    margin-bottom: 8px;
  }

  .empty-description {
    font-size: 14px;
    color: #909399;
    margin-bottom: 24px;
    line-height: 1.5;
  }

  .empty-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}
</style>