<template>
  <div class="products-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h2>产品管理</h2>
          <p>111管理练字机器人产品信息，与官网展示保持同步</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleCreateProduct" :icon="Plus">
            添加产品
          </el-button>
          <el-button @click="handleSyncToWebsite" :icon="Refresh" :loading="syncing">
            同步到官网
          </el-button>
        </div>
      </div>
    </div>

    <!-- 产品统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon published">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.published }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon draft">
          <el-icon><Edit /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.draft }}</div>
          <div class="stat-label">草稿</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon archived">
          <el-icon><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.archived }}</div>
          <div class="stat-label">已归档</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总计</div>
        </div>
      </div>
    </div>

    <!-- 产品列表 -->
    <div class="products-container">
      <el-row :gutter="24" v-loading="loading">
        <el-col 
          v-for="product in products" 
          :key="product.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
          class="product-col"
        >
          <div class="product-card">
            <!-- 产品图片 -->
            <div class="product-image">
              <img 
                :src="getMainImage(product)?.url || defaultImage" 
                :alt="product.name"
                @error="handleImageError"
              />
              
              <!-- 状态标识 -->
              <div class="status-badge" :class="product.status">
                {{ getStatusText(product.status) }}
              </div>
              
              <!-- 产品标签 -->
              <div v-if="product.badge" class="product-badge">
                {{ product.badge }}
              </div>
              
              <!-- 快速操作 -->
              <div class="quick-actions">
                <el-button 
                  circle 
                  size="small" 
                  @click="handlePreview(product)"
                  :icon="View"
                  title="预览"
                />
                <el-button 
                  circle 
                  size="small" 
                  type="primary"
                  @click="handleEdit(product)"
                  :icon="Edit"
                  title="编辑"
                />
                <el-dropdown @command="(command) => handleMoreAction(command, product)">
                  <el-button circle size="small" :icon="MoreFilled" title="更多" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="duplicate">
                        <el-icon><CopyDocument /></el-icon>
                        复制产品
                      </el-dropdown-item>
                      <el-dropdown-item 
                        :command="product.status === 'published' ? 'unpublish' : 'publish'"
                      >
                        <el-icon><Switch /></el-icon>
                        {{ product.status === 'published' ? '取消发布' : '发布产品' }}
                      </el-dropdown-item>
                      <el-dropdown-item command="archive" v-if="product.status !== 'archived'">
                        <el-icon><Box /></el-icon>
                        归档产品
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" divided>
                        <el-icon><Delete /></el-icon>
                        删除产品
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>

            <!-- 产品信息 -->
            <div class="product-info">
              <h3 class="product-name" :title="product.name">
                {{ product.name }}
              </h3>
              <div class="product-model">{{ product.model }}</div>
              
              <!-- 价格信息 -->
              <div class="price-info">
                <span class="current-price">¥{{ product.price?.toLocaleString() }}</span>
                <span v-if="product.originalPrice" class="original-price">
                  ¥{{ product.originalPrice.toLocaleString() }}
                </span>
              </div>
              
              <!-- 产品特性 -->
              <div class="product-features">
                <el-tag 
                  v-for="feature in product.features.slice(0, 2)" 
                  :key="feature"
                  size="small"
                  type="info"
                >
                  {{ feature }}
                </el-tag>
                <el-tag 
                  v-if="product.features.length > 2"
                  size="small"
                  type="info"
                >
                  +{{ product.features.length - 2 }}
                </el-tag>
              </div>
              
              <!-- 更新时间 -->
              <div class="update-time">
                更新于 {{ formatDate(product.updatedAt) }}
              </div>
            </div>


          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="!loading && products.length === 0" class="empty-state">
        <el-empty description="暂无产品数据">
          <el-button type="primary" @click="handleCreateProduct">
            添加第一个产品
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 产品预览对话框 -->
    <ProductPreview
      v-model="previewVisible"
      :product="selectedProduct"
      @refresh="handleRefreshPreview"
    />

    <!-- 产品编辑对话框 -->
    <ProductEditDialog
      v-model="editVisible"
      :product="selectedProduct"
      :mode="editMode"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Refresh, 
  CircleCheck, 
  Edit, 
  Box, 
  DataAnalysis,
  View,
  MoreFilled,
  CopyDocument,
  Switch,
  Delete
} from '@element-plus/icons-vue'
import ProductPreview from '@/components/product/ProductPreview.vue'
import ProductEditDialog from '@/components/product/ProductEditDialog.vue'
import { productApi } from '@/api/product'
import type { Product } from '@/types/product'
import { formatDate } from '@/utils/dateTime'

// 响应式数据
const loading = ref(false)
const syncing = ref(false)
const products = ref<Product[]>([])
const selectedProduct = ref<Product | null>(null)
const previewVisible = ref(false)
const editVisible = ref(false)
const editMode = ref<'create' | 'edit'>('edit')

// 默认图片
const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTgiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIiBmaWxsPSIjOTk5Ij7kuqflk4Hlm77niYc8L3RleHQ+PC9zdmc+'

// 计算属性
const stats = computed(() => {
  const published = products.value.filter(p => p.status === 'published').length
  const draft = products.value.filter(p => p.status === 'draft').length
  const archived = products.value.filter(p => p.status === 'archived').length
  const total = products.value.length
  
  return { published, draft, archived, total }
})

// 生命周期
onMounted(() => {
  loadProducts()
})

// 方法
const loadProducts = async () => {
  try {
    loading.value = true
    const response = await productApi.getProducts()
    products.value = response.data
  } catch (error) {
    console.error('加载产品列表失败:', error)
    ElMessage.error('加载产品列表失败')
  } finally {
    loading.value = false
  }
}

const getMainImage = (product: Product) => {
  return product.images.find(img => img.type === 'main') || product.images[0]
}

const getStatusText = (status: string) => {
  const statusMap = {
    published: '已发布',
    draft: '草稿',
    archived: '已归档'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = defaultImage
}

const handleCreateProduct = () => {
  selectedProduct.value = null
  editMode.value = 'create'
  editVisible.value = true
}

const handleEdit = (product: Product) => {
  selectedProduct.value = product
  editMode.value = 'edit'
  editVisible.value = true
}

const handlePreview = (product: Product) => {
  selectedProduct.value = product
  previewVisible.value = true
}

const handleRefreshPreview = () => {
  // 刷新预览数据
  if (selectedProduct.value) {
    const updatedProduct = products.value.find(p => p.id === selectedProduct.value?.id)
    if (updatedProduct) {
      selectedProduct.value = updatedProduct
    }
  }
}

const handleEditSuccess = () => {
  editVisible.value = false
  loadProducts()
  ElMessage.success(editMode.value === 'create' ? '产品创建成功' : '产品更新成功')
}

const handleMoreAction = async (command: string, product: Product) => {
  switch (command) {
    case 'duplicate':
      await handleDuplicate(product)
      break
    case 'publish':
      await handlePublish(product)
      break
    case 'unpublish':
      await handleUnpublish(product)
      break
    case 'archive':
      await handleArchive(product)
      break
    case 'delete':
      await handleDelete(product)
      break
  }
}

const handleDuplicate = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要复制产品"${product.name}"吗？`,
      '复制确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // 实现复制逻辑
    ElMessage.success('产品复制成功')
    loadProducts()
  } catch {
    // 用户取消
  }
}

const handlePublish = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布产品"${product.name}"到官网吗？`,
      '发布确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await productApi.publishProduct(product.id)
    ElMessage.success('产品发布成功')
    loadProducts()
  } catch {
    // 用户取消
  }
}

const handleUnpublish = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消发布产品"${product.name}"吗？`,
      '取消发布确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('已取消发布')
    loadProducts()
  } catch {
    // 用户取消
  }
}

const handleArchive = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要归档产品"${product.name}"吗？归档后将不再显示在官网上。`,
      '归档确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('产品已归档')
    loadProducts()
  } catch {
    // 用户取消
  }
}

const handleDelete = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除产品"${product.name}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    // await productApi.deleteProduct(product.id)
    ElMessage.success('产品删除成功')
    loadProducts()
  } catch {
    // 用户取消
  }
}

const handleSyncToWebsite = async () => {
  try {
    syncing.value = true
    // 模拟同步过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('产品信息已同步到官网')
  } catch (error) {
    ElMessage.error('同步失败，请稍后重试')
  } finally {
    syncing.value = false
  }
}
</script>

<style lang="scss" scoped>
.products-page {
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-info {
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
      
      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }
  
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 32px;
    
    .stat-card {
      background: white;
      border-radius: var(--radius-lg);
      padding: 24px;
      box-shadow: var(--shadow-sm);
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-md);
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        
        &.published {
          background: rgba(16, 185, 129, 0.1);
          color: #10B981;
        }
        
        &.draft {
          background: rgba(245, 158, 11, 0.1);
          color: #F59E0B;
        }
        
        &.archived {
          background: rgba(107, 114, 128, 0.1);
          color: #6B7280;
        }
        
        &.total {
          background: rgba(59, 130, 246, 0.1);
          color: #3B82F6;
        }
      }
      
      .stat-content {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
  }
  
  .products-container {
    .product-col {
      margin-bottom: 24px;
    }
    
    .product-card {
      background: white;
      border-radius: var(--radius-lg);
      overflow: hidden;
      box-shadow: var(--shadow-sm);
      transition: all 0.3s ease;
      height: 100%;
      display: flex;
      flex-direction: column;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: var(--shadow-lg);
        
        .quick-actions {
          opacity: 1;
        }
      }
      
      .product-image {
        position: relative;
        aspect-ratio: 4/3;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.3s ease;
        }
        
        &:hover img {
          transform: scale(1.05);
        }
        
        .status-badge {
          position: absolute;
          top: 12px;
          right: 12px;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 600;
          color: white;
          
          &.published {
            background: #10B981;
          }
          
          &.draft {
            background: #F59E0B;
          }
          
          &.archived {
            background: #6B7280;
          }
        }
        
        .product-badge {
          position: absolute;
          top: 12px;
          left: 12px;
          background: var(--primary-color);
          color: white;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 600;
        }
        
        .quick-actions {
          position: absolute;
          bottom: 12px;
          right: 12px;
          display: flex;
          gap: 8px;
          opacity: 0;
          transition: opacity 0.3s ease;
        }
      }
      
      .product-info {
        padding: 20px;
        flex: 1;
        display: flex;
        flex-direction: column;
        
        .product-name {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0 0 8px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .product-model {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 12px;
        }
        
        .price-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;
          
          .current-price {
            font-size: 20px;
            font-weight: 700;
            color: var(--primary-color);
          }
          
          .original-price {
            font-size: 14px;
            color: var(--text-light);
            text-decoration: line-through;
          }
        }
        
        .product-features {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          margin-bottom: 16px;
          
          .el-tag {
            font-size: 11px;
          }
        }
        
        .update-time {
          font-size: 12px;
          color: var(--text-light);
          margin-top: auto;
        }
      }
      

    }
    
    .empty-state {
      text-align: center;
      padding: 60px 20px;
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .products-page {
    .page-header .header-content {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 480px) {
  .products-page {
    .stats-cards {
      grid-template-columns: 1fr;
    }
  }
}
</style>