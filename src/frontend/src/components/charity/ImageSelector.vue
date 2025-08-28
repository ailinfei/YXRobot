<template>
  <div class="image-selector">
    <div class="selector-toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索图片"
        clearable
        style="width: 200px;"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="categoryFilter" placeholder="选择分类" style="width: 150px;">
        <el-option label="全部分类" value="" />
        <el-option label="教学点图片" value="teaching" />
        <el-option label="活动照片" value="activity" />
        <el-option label="设备图片" value="equipment" />
        <el-option label="人物照片" value="people" />
      </el-select>
    </div>

    <div class="image-grid" v-loading="loading">
      <div
        v-for="image in filteredImages"
        :key="image.id"
        class="image-item"
        :class="{ selected: selectedImage?.id === image.id }"
        @click="handleImageSelect(image)"
      >
        <el-image
          :src="image.thumbnail || image.url"
          :alt="image.name"
          fit="cover"
          class="image"
        >
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        
        <div class="image-info">
          <div class="image-name">{{ image.name }}</div>
        </div>
      </div>
    </div>

    <div class="selector-footer">
      <div class="selected-info">
        <span v-if="selectedImage">已选择：{{ selectedImage.name }}</span>
        <span v-else>请选择一张图片</span>
      </div>
      
      <div class="footer-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :disabled="!selectedImage" @click="handleConfirm">
          确定
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Picture } from '@element-plus/icons-vue'

// 图片接口
interface ImageItem {
  id: string
  name: string
  url: string
  thumbnail?: string
  category: string
}

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const categoryFilter = ref('')
const selectedImage = ref<ImageItem | null>(null)
const images = ref<ImageItem[]>([])

// 计算属性
const filteredImages = computed(() => {
  let result = images.value

  if (searchKeyword.value) {
    result = result.filter(image => 
      image.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  if (categoryFilter.value) {
    result = result.filter(image => image.category === categoryFilter.value)
  }

  return result
})

// 事件定义
const emit = defineEmits<{
  select: [imageUrl: string]
  cancel: []
}>()

// 方法
const handleImageSelect = (image: ImageItem) => {
  selectedImage.value = image
}

const handleConfirm = () => {
  if (selectedImage.value) {
    emit('select', selectedImage.value.url)
  }
}

const handleCancel = () => {
  emit('cancel')
}

const loadImages = async () => {
  loading.value = true
  try {
    // 模拟加载图片数据
    await new Promise(resolve => setTimeout(resolve, 500))
    
    images.value = [
      {
        id: '1',
        name: '教学点-希望小学-01.jpg',
        url: 'https://picsum.photos/400/300?random=teaching1',
        thumbnail: 'https://picsum.photos/200/150?random=teaching1',
        category: 'teaching'
      },
      {
        id: '2',
        name: '活动照片-书法培训.jpg',
        url: 'https://picsum.photos/400/300?random=activity1',
        thumbnail: 'https://picsum.photos/200/150?random=activity1',
        category: 'activity'
      },
      {
        id: '3',
        name: '设备图片-YX机器人.jpg',
        url: 'https://picsum.photos/400/300?random=equipment1',
        thumbnail: 'https://picsum.photos/200/150?random=equipment1',
        category: 'equipment'
      },
      {
        id: '4',
        name: '志愿者合影.jpg',
        url: 'https://picsum.photos/400/300?random=people1',
        thumbnail: 'https://picsum.photos/200/150?random=people1',
        category: 'people'
      }
    ]
  } catch (error) {
    console.error('加载图片失败:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadImages()
})
</script>

<style lang="scss" scoped>
.image-selector {
  .selector-toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    align-items: center;
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 12px;
    max-height: 400px;
    overflow-y: auto;
    margin-bottom: 16px;

    .image-item {
      border: 2px solid transparent;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #409EFF;
        transform: translateY(-2px);
      }

      &.selected {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }

      .image {
        width: 100%;
        height: 100px;
      }

      .image-error {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100px;
        background: #f5f7fa;
        color: #909399;
        font-size: 24px;
      }

      .image-info {
        padding: 8px;
        background: white;

        .image-name {
          font-size: 12px;
          color: #606266;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }

  .selector-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;

    .selected-info {
      font-size: 14px;
      color: #606266;
    }

    .footer-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>