<template>
  <div class="product-image-carousel">
    <!-- 主图显示区域 -->
    <div class="main-image-container">
      <div class="image-wrapper">
        <img 
          :src="selectedImage?.url || ''" 
          :alt="selectedImage?.alt || '产品图片'" 
          class="main-image"
          @error="handleImageError"
        />
        
        <!-- 图片工具栏 -->
        <div class="image-tools">
          <el-button 
            circle 
            size="small" 
            @click="previewImage"
            :icon="ZoomIn"
            title="预览图片"
          />
          <el-button 
            v-if="!readonly && selectedImage"
            circle 
            size="small" 
            type="danger"
            @click="deleteImage(selectedImage.id)"
            :icon="Delete"
            title="删除图片"
          />
        </div>

        <!-- 主图标识 -->
        <div v-if="selectedImage?.type === 'main'" class="main-badge">
          主图
        </div>
      </div>
    </div>

    <!-- 缩略图列表 -->
    <div class="thumbnail-container">
      <div class="thumbnail-list" ref="thumbnailListRef">
        <!-- 上传按钮 -->
        <div v-if="!readonly" class="upload-thumbnail">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
            accept="image/*"
            class="upload-wrapper"
          >
            <div class="upload-area">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span class="upload-text">添加图片</span>
            </div>
          </el-upload>
        </div>

        <!-- 图片缩略图 -->
        <draggable
          v-model="sortableImages"
          :disabled="readonly"
          item-key="id"
          class="thumbnail-draggable"
          @end="handleDragEnd"
        >
          <template #item="{ element: image, index }">
            <div 
              class="thumbnail-item"
              :class="{ 
                active: selectedImage?.id === image.id,
                'is-main': image.type === 'main'
              }"
              @click="selectImage(image)"
            >
              <img :src="image.url" :alt="image.alt" />
              
              <!-- 主图标识 -->
              <div v-if="image.type === 'main'" class="main-indicator">
                <el-icon><Star /></el-icon>
              </div>
              
              <!-- 操作按钮 -->
              <div v-if="!readonly" class="thumbnail-actions">
                <el-button
                  v-if="image.type !== 'main'"
                  size="small"
                  type="primary"
                  @click.stop="setMainImage(image.id)"
                  title="设为主图"
                >
                  <el-icon><Star /></el-icon>
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click.stop="deleteImage(image.id)"
                  title="删除图片"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>

              <!-- 拖拽手柄 -->
              <div v-if="!readonly" class="drag-handle">
                <el-icon><Rank /></el-icon>
              </div>
            </div>
          </template>
        </draggable>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="80%"
      center
    >
      <div class="preview-container">
        <img 
          :src="previewImageUrl" 
          :alt="selectedImage?.alt || '预览图片'"
          class="preview-image"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ZoomIn, Delete, Star, Rank } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import type { ProductImage } from '@/types/product'

interface Props {
  images: ProductImage[]
  readonly?: boolean
  maxCount?: number
  maxSize?: number // MB
}

interface Emits {
  (e: 'update:images', images: ProductImage[]): void
  (e: 'upload', file: File): void
  (e: 'delete', imageId: string): void
  (e: 'set-main', imageId: string): void
  (e: 'reorder', imageOrders: Array<{ id: string; order: number }>): void
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false,
  maxCount: 10,
  maxSize: 5
})

const emit = defineEmits<Emits>()

// 响应式数据
const selectedImage = ref<ProductImage | null>(null)
const previewVisible = ref(false)
const previewImageUrl = ref('')
const thumbnailListRef = ref<HTMLElement>()

// 可排序的图片列表
const sortableImages = computed({
  get: () => [...props.images].sort((a, b) => a.order - b.order),
  set: (newImages: ProductImage[]) => {
    // 更新排序
    const updatedImages = newImages.map((img, index) => ({
      ...img,
      order: index + 1
    }))
    emit('update:images', updatedImages)
  }
})

// 监听图片列表变化，自动选择第一张图片
watch(
  () => props.images,
  (newImages) => {
    if (newImages.length > 0 && !selectedImage.value) {
      const mainImage = newImages.find(img => img.type === 'main')
      selectedImage.value = mainImage || newImages[0]
    } else if (newImages.length === 0) {
      selectedImage.value = null
    }
  },
  { immediate: true }
)

// 选择图片
const selectImage = (image: ProductImage) => {
  selectedImage.value = image
}

// 预览图片
const previewImage = () => {
  if (selectedImage.value) {
    previewImageUrl.value = selectedImage.value.url
    previewVisible.value = true
  }
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTgiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIiBmaWxsPSIjOTk5Ij7lm77niYfliqDovb3lpLHotKU8L3RleHQ+PC9zdmc+'
}

// 上传前验证
const beforeUpload = (file: File) => {
  // 检查文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }

  // 检查文件大小
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize
  if (!isLtMaxSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`)
    return false
  }

  // 检查图片数量
  if (props.images.length >= props.maxCount) {
    ElMessage.error(`最多只能上传 ${props.maxCount} 张图片!`)
    return false
  }

  return true
}

// 处理上传
const handleUpload = (options: any) => {
  emit('upload', options.file)
}

// 删除图片
const deleteImage = async (imageId: string) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这张图片吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    emit('delete', imageId)
    
    // 如果删除的是当前选中的图片，选择下一张
    if (selectedImage.value?.id === imageId) {
      const remainingImages = props.images.filter(img => img.id !== imageId)
      selectedImage.value = remainingImages.length > 0 ? remainingImages[0] : null
    }
  } catch {
    // 用户取消删除
  }
}

// 设置主图
const setMainImage = async (imageId: string) => {
  try {
    await ElMessageBox.confirm(
      '确定要将此图片设为主图吗？',
      '设置主图',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    emit('set-main', imageId)
  } catch {
    // 用户取消操作
  }
}

// 拖拽结束处理
const handleDragEnd = () => {
  const imageOrders = sortableImages.value.map((img, index) => ({
    id: img.id,
    order: index + 1
  }))
  emit('reorder', imageOrders)
}
</script>

<style lang="scss" scoped>
.product-image-carousel {
  .main-image-container {
    margin-bottom: 16px;
    
    .image-wrapper {
      position: relative;
      border-radius: var(--radius-lg);
      overflow: hidden;
      background: var(--bg-secondary);
      aspect-ratio: 4/3;
      
      .main-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
      }
      
      .image-tools {
        position: absolute;
        top: 12px;
        right: 12px;
        display: flex;
        gap: 8px;
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      .main-badge {
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
      
      &:hover .image-tools {
        opacity: 1;
      }
    }
  }
  
  .thumbnail-container {
    .thumbnail-list {
      display: flex;
      gap: 12px;
      overflow-x: auto;
      padding: 4px;
      
      &::-webkit-scrollbar {
        height: 4px;
      }
      
      &::-webkit-scrollbar-track {
        background: var(--bg-secondary);
        border-radius: 2px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: var(--border-color);
        border-radius: 2px;
        
        &:hover {
          background: var(--text-light);
        }
      }
    }
    
    .upload-thumbnail {
      flex-shrink: 0;
      
      .upload-wrapper {
        .upload-area {
          width: 80px;
          height: 80px;
          border: 2px dashed var(--border-color);
          border-radius: var(--radius-md);
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          background: var(--bg-secondary);
          
          &:hover {
            border-color: var(--primary-color);
            background: var(--primary-light);
          }
          
          .upload-icon {
            font-size: 20px;
            color: var(--text-light);
            margin-bottom: 4px;
          }
          
          .upload-text {
            font-size: 10px;
            color: var(--text-light);
            text-align: center;
            line-height: 1.2;
          }
        }
      }
    }
    
    .thumbnail-draggable {
      display: flex;
      gap: 12px;
    }
    
    .thumbnail-item {
      position: relative;
      width: 80px;
      height: 80px;
      border-radius: var(--radius-md);
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.3s ease;
      flex-shrink: 0;
      
      &.active {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 2px rgba(var(--primary-color-rgb), 0.2);
      }
      
      &.is-main {
        border-color: var(--warning-color);
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-md);
        
        .thumbnail-actions {
          opacity: 1;
        }
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .main-indicator {
        position: absolute;
        top: 4px;
        left: 4px;
        background: var(--warning-color);
        color: white;
        border-radius: 50%;
        width: 20px;
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 10px;
      }
      
      .thumbnail-actions {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        .el-button {
          padding: 4px;
          min-height: auto;
          
          .el-icon {
            font-size: 12px;
          }
        }
      }
      
      .drag-handle {
        position: absolute;
        bottom: 4px;
        right: 4px;
        background: rgba(0, 0, 0, 0.6);
        color: white;
        border-radius: 4px;
        padding: 2px;
        cursor: move;
        font-size: 10px;
      }
    }
  }
  
  .preview-container {
    text-align: center;
    
    .preview-image {
      max-width: 100%;
      max-height: 70vh;
      object-fit: contain;
    }
  }
}
</style>