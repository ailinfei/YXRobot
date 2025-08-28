<template>
  <el-dialog
    v-model="visible"
    title="产品预览"
    width="90%"
    :before-close="handleClose"
    class="product-preview-dialog"
  >
    <div class="preview-container">
      <!-- 预览工具栏 -->
      <div class="preview-toolbar">
        <div class="toolbar-left">
          <el-button-group>
            <el-button 
              :type="viewMode === 'desktop' ? 'primary' : ''"
              @click="viewMode = 'desktop'"
              :icon="Monitor"
            >
              桌面版
            </el-button>
            <el-button 
              :type="viewMode === 'tablet' ? 'primary' : ''"
              @click="viewMode = 'tablet'"
              :icon="Iphone"
            >
              平板版
            </el-button>
            <el-button 
              :type="viewMode === 'mobile' ? 'primary' : ''"
              @click="viewMode = 'mobile'"
              :icon="Cellphone"
            >
              手机版
            </el-button>
          </el-button-group>
        </div>
        
        <div class="toolbar-right">
          <el-button @click="refreshPreview">
            刷新预览
          </el-button>
          <el-button type="primary" @click="openInNewTab" :icon="View">
            新窗口打开
          </el-button>
        </div>
      </div>

      <!-- 预览内容区域 -->
      <div class="preview-content" :class="`view-${viewMode}`">
        <div class="preview-frame">
          <!-- 模拟官网产品页面 -->
          <div class="simulated-website">
            <!-- 导航栏 -->
            <div class="site-header">
              <div class="container">
                <div class="logo">YXRobot</div>
                <nav class="nav-menu">
                  <span>首页</span>
                  <span class="active">产品中心</span>
                  <span>解决方案</span>
                  <span>关于我们</span>
                </nav>
              </div>
            </div>

            <!-- 面包屑 -->
            <div class="breadcrumb">
              <div class="container">
                首页 / 产品中心 / {{ product.name }}
              </div>
            </div>

            <!-- 产品主体内容 -->
            <div class="product-main">
              <div class="container">
                <div class="product-layout">
                  <!-- 左侧图片 -->
                  <div class="product-gallery">
                    <div class="main-image">
                      <img :src="mainImage?.url" :alt="product.name" />
                      <div v-if="product.badge" class="product-badge">
                        {{ product.badge }}
                      </div>
                    </div>
                    <div class="thumbnail-list">
                      <div 
                        v-for="image in product.images.slice(0, 4)" 
                        :key="image.id"
                        class="thumbnail"
                      >
                        <img :src="image.url" :alt="image.alt" />
                      </div>
                    </div>
                  </div>

                  <!-- 右侧产品信息 -->
                  <div class="product-info">
                    <h1 class="product-title">{{ product.name }}</h1>
                    <div class="product-subtitle">{{ product.model }} | 专为教育机构设计</div>
                    
                    <!-- 价格 -->
                    <div class="price-section">
                      <div class="current-price">¥{{ product.price?.toLocaleString() }}</div>
                      <div v-if="product.originalPrice" class="original-price">
                        ¥{{ product.originalPrice.toLocaleString() }}
                      </div>
                      <div v-if="product.originalPrice" class="discount">
                        立省¥{{ (product.originalPrice - product.price).toLocaleString() }}
                      </div>
                    </div>

                    <!-- 产品亮点 -->
                    <div class="highlights">
                      <h3>产品亮点</h3>
                      <ul>
                        <li v-for="highlight in product.highlights" :key="highlight">
                          {{ highlight }}
                        </li>
                      </ul>
                    </div>

                    <!-- 购买按钮 -->
                    <div class="purchase-actions">
                      <button class="btn-primary">立即购买</button>
                      <button class="btn-secondary">租赁体验</button>
                      <button class="btn-outline">咨询详情</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 产品功能介绍 -->
            <div class="features-section">
              <div class="container">
                <h2 class="section-title">核心功能</h2>
                <div class="features-grid">
                  <div 
                    v-for="feature in product.productFeatures" 
                    :key="feature.title"
                    class="feature-card"
                  >
                    <h3>{{ feature.title }}</h3>
                    <p>{{ feature.description }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 技术规格 -->
            <div class="specs-section">
              <div class="container">
                <h2 class="section-title">技术规格</h2>
                <div class="specs-table">
                  <div 
                    v-for="(value, key) in product.specifications" 
                    :key="key"
                    class="spec-row"
                  >
                    <div class="spec-label">{{ getSpecLabel(key) }}</div>
                    <div class="spec-value">{{ value }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Monitor, Iphone, Cellphone, View } from '@element-plus/icons-vue'
import type { Product } from '@/types/product'

interface Props {
  modelValue: boolean
  product: Product
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'refresh'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const viewMode = ref<'desktop' | 'tablet' | 'mobile'>('desktop')

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const mainImage = computed(() => {
  return props.product.images.find(img => img.type === 'main') || props.product.images[0]
})

// 方法
const handleClose = () => {
  visible.value = false
}

const refreshPreview = () => {
  emit('refresh')
}

const openInNewTab = () => {
  // 这里可以实现在新窗口打开预览的逻辑
  const previewUrl = `/preview/product/${props.product.id}`
  window.open(previewUrl, '_blank')
}

const getSpecLabel = (key: string): string => {
  const labels: Record<string, string> = {
    dimensions: '产品尺寸',
    weight: '产品重量',
    display: '显示屏幕',
    connectivity: '连接方式',
    teaching: '教学方式',
    recognition: '识别技术',
    courses: '课程内容',
    management: '管理系统'
  }
  return labels[key] || key
}
</script>

<style lang="scss" scoped>
.product-preview-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.preview-container {
  height: 80vh;
  display: flex;
  flex-direction: column;
}

.preview-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
}

.preview-content {
  flex: 1;
  overflow: auto;
  background: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;

  &.view-desktop .preview-frame {
    width: 100%;
    max-width: 1200px;
  }

  &.view-tablet .preview-frame {
    width: 768px;
  }

  &.view-mobile .preview-frame {
    width: 375px;
  }
}

.preview-frame {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

// 模拟官网样式
.simulated-website {
  .site-header {
    background: white;
    border-bottom: 1px solid #eee;
    padding: 16px 0;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo {
      font-size: 24px;
      font-weight: bold;
      color: var(--primary-color);
    }

    .nav-menu {
      display: flex;
      gap: 32px;

      span {
        cursor: pointer;
        color: var(--text-secondary);
        transition: color 0.3s ease;

        &.active,
        &:hover {
          color: var(--primary-color);
        }
      }
    }
  }

  .breadcrumb {
    background: #f8f9fa;
    padding: 12px 0;
    font-size: 14px;
    color: var(--text-secondary);

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }
  }

  .product-main {
    padding: 40px 0;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .product-layout {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 60px;
      align-items: start;
    }
  }

  .product-gallery {
    .main-image {
      position: relative;
      border-radius: 12px;
      overflow: hidden;
      margin-bottom: 16px;
      aspect-ratio: 4/3;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .product-badge {
        position: absolute;
        top: 16px;
        left: 16px;
        background: var(--primary-color);
        color: white;
        padding: 6px 12px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 600;
      }
    }

    .thumbnail-list {
      display: flex;
      gap: 12px;

      .thumbnail {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        overflow: hidden;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }
  }

  .product-info {
    .product-title {
      font-size: 32px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 8px;
    }

    .product-subtitle {
      font-size: 16px;
      color: var(--text-secondary);
      margin-bottom: 24px;
    }

    .price-section {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 32px;

      .current-price {
        font-size: 36px;
        font-weight: 700;
        color: var(--primary-color);
      }

      .original-price {
        font-size: 18px;
        color: var(--text-light);
        text-decoration: line-through;
      }

      .discount {
        background: #10B981;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
      }
    }

    .highlights {
      margin-bottom: 32px;

      h3 {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 16px;
        color: var(--text-primary);
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          padding: 8px 0;
          color: var(--text-secondary);
          position: relative;
          padding-left: 20px;

          &::before {
            content: '✓';
            position: absolute;
            left: 0;
            color: var(--primary-color);
            font-weight: bold;
          }
        }
      }
    }

    .purchase-actions {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;

      button {
        padding: 12px 24px;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        border: none;

        &.btn-primary {
          background: var(--primary-color);
          color: white;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(var(--primary-color-rgb), 0.3);
          }
        }

        &.btn-secondary {
          background: #10B981;
          color: white;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
          }
        }

        &.btn-outline {
          background: transparent;
          color: var(--primary-color);
          border: 2px solid var(--primary-color);

          &:hover {
            background: var(--primary-color);
            color: white;
          }
        }
      }
    }
  }

  .features-section,
  .specs-section {
    padding: 60px 0;
    background: #f8f9fa;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .section-title {
      text-align: center;
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 48px;
      color: var(--text-primary);
    }
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 32px;

    .feature-card {
      background: white;
      padding: 32px;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      h3 {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 16px;
        color: var(--text-primary);
      }

      p {
        color: var(--text-secondary);
        line-height: 1.6;
      }
    }
  }

  .specs-table {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .spec-row {
      display: flex;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .spec-label {
        flex: 1;
        padding: 20px 24px;
        background: #f8f9fa;
        font-weight: 600;
        color: var(--text-primary);
      }

      .spec-value {
        flex: 2;
        padding: 20px 24px;
        color: var(--text-secondary);
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .preview-content {
    &.view-desktop .preview-frame,
    &.view-tablet .preview-frame {
      width: 100%;
    }
  }

  .simulated-website {
    .product-main .product-layout {
      grid-template-columns: 1fr;
      gap: 32px;
    }

    .features-grid {
      grid-template-columns: 1fr;
    }

    .specs-table .spec-row {
      flex-direction: column;

      .spec-label {
        background: var(--bg-secondary);
      }
    }
  }
}
</style>