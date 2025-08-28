<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>系统设置</h2>
      <p>配置系统参数和管理选项</p>
    </div>
    
    <div class="card">
      <div style="padding: 40px; text-align: center; color: var(--text-secondary);">
        <el-icon size="48"><Setting /></el-icon>
        <h3 style="margin: 16px 0 8px 0;">系统设置功能</h3>
        <p>此页面正在开发中...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Setting } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.settings-page {
  .page-header {
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 4px 0;
    }
    
    p {
      color: var(--text-secondary);
      margin: 0;
    }
  }
}
</style>