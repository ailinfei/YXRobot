<template>
  <div class="image-management">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-upload
          ref="uploadRef"
          :action="uploadAction"
          :headers="uploadHeaders"
          :data="uploadData"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          multiple
          accept="image/*"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            批量上传
          </el-button>
        </el-upload>
        
        <el-button @click="handleCreateFolder">
          <el-icon><FolderAdd /></el-icon>
          新建文件夹
        </el-button>
        
        <el-button 
          :disabled="selectedImages.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索图片"
          clearable
          @input="handleSearch"
          style="width: 200px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="currentCategory"
          placeholder="选择分类"
          @change="handleCategoryChange"
          style="width: 150px"
        >
          <el-option label="全部分类" value="" />
          <el-option label="教学点图片" value="teaching" />
          <el-option label="活动照片" value="activity" />
          <el-option label="设备图片" value="equipment" />
          <el-option label="人物照片" value="people" />
          <el-option label="其他" value="other" />
        </el-select>
        
        <el-button-group>
          <el-button 
            :type="viewMode === 'grid' ? 'primary' : ''"
            @click="viewMode = 'grid'"
          >
            <el-icon><Grid /></el-icon>
          </el-button>
          <el-button 
            :type="viewMode === 'list' ? 'primary' : ''"
            @click="viewMode = 'list'"
          >
            <el-icon><List /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb" v-if="currentPath.length > 0">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item @click="navigateToRoot">根目录</el-breadcrumb-item>
        <el-breadcrumb-item
          v-for="(folder, index) in currentPath"
          :key="index"
          @click="navigateToFolder(index)"
        >
          {{ folder }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 图片网格视图 -->
    <div v-if="viewMode === 'grid'" class="image-grid" v-loading="loading">
      <!-- 文件夹 -->
      <div
        v-for="folder in folders"
        :key="folder.id"
        class="folder-item"
        @click="enterFolder(folder)"
        @contextmenu.prevent="showFolderContextMenu($event, folder)"
      >
        <div class="folder-icon">
          <el-icon><Folder /></el-icon>
        </div>
        <div class="folder-name">{{ folder.name }}</div>
        <div class="folder-count">{{ folder.imageCount }}张图片</div>
      </div>

      <!-- 图片 -->
      <div
        v-for="image in filteredImages"
        :key="image.id"
        class="image-item"
        :class="{ selected: selectedImages.includes(image.id) }"
        @click="handleImageClick(image)"
        @contextmenu.prevent="showImageContextMenu($event, image)"
      >
        <div class="image-wrapper">
          <el-image
            :src="image.thumbnail || image.url"
            :alt="image.name"
            fit="cover"
            class="image"
            :preview-src-list="[image.url]"
            :initial-index="0"
            preview-teleported
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          
          <!-- 选择框 -->
          <div class="image-checkbox">
            <el-checkbox
              :model-value="selectedImages.includes(image.id)"
              @change="handleImageSelect(image.id, $event)"
              @click.stop
            />
          </div>
          
          <!-- 图片信息 -->
          <div class="image-overlay">
            <div class="image-name">{{ image.name }}</div>
            <div class="image-size">{{ formatFileSize(image.size) }}</div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && filteredImages.length === 0 && folders.length === 0" description="暂无图片" />
    </div>

    <!-- 图片列表视图 -->
    <div v-else class="image-list" v-loading="loading">
      <el-table
        :data="filteredImages"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <el-image
              :src="row.thumbnail || row.url"
              :alt="row.name"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px;"
              :preview-src-list="[row.url]"
              preview-teleported
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="name" label="文件名" min-width="200" />
        
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryText(row.category) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="size" label="文件大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="uploadTime" label="上传时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.uploadTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handlePreview(row)">预览</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="filteredImages.length > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[20, 40, 60, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 右键菜单 -->
    <el-dropdown
      ref="contextMenuRef"
      trigger="contextmenu"
      :virtual-ref="contextMenuTarget"
      virtual-triggering
    >
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleContextAction('preview')">预览</el-dropdown-item>
          <el-dropdown-item @click="handleContextAction('edit')">编辑</el-dropdown-item>
          <el-dropdown-item @click="handleContextAction('copy')">复制链接</el-dropdown-item>
          <el-dropdown-item @click="handleContextAction('move')">移动到</el-dropdown-item>
          <el-dropdown-item @click="handleContextAction('delete')" divided>删除</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  FolderAdd,
  Delete,
  Search,
  Grid,
  List,
  Folder,
  Picture
} from '@element-plus/icons-vue'

// 图片接口
interface ImageItem {
  id: string
  name: string
  url: string
  thumbnail?: string
  size: number
  category: string
  uploadTime: string
  folderId?: string
}

// 文件夹接口
interface FolderItem {
  id: string
  name: string
  imageCount: number
  parentId?: string
}

// 响应式数据
const loading = ref(false)
const viewMode = ref<'grid' | 'list'>('grid')
const searchKeyword = ref('')
const currentCategory = ref('')
const currentPath = ref<string[]>([])
const selectedImages = ref<string[]>([])
const contextMenuTarget = ref()
const contextMenuRef = ref()
const uploadRef = ref()

// 图片和文件夹数据
const images = ref<ImageItem[]>([])
const folders = ref<FolderItem[]>([])

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 上传配置
const uploadAction = '/api/charity/upload-images'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}
const uploadData = {
  category: currentCategory.value || 'other'
}

// 计算属性
const filteredImages = computed(() => {
  let result = images.value

  if (searchKeyword.value) {
    result = result.filter(image => 
      image.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  if (currentCategory.value) {
    result = result.filter(image => image.category === currentCategory.value)
  }

  return result
})

// 方法
const getCategoryText = (category: string) => {
  const categoryMap: Record<string, string> = {
    teaching: '教学点',
    activity: '活动',
    equipment: '设备',
    people: '人物',
    other: '其他'
  }
  return categoryMap[category] || category
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
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
        size: 1024 * 500,
        category: 'teaching',
        uploadTime: '2024-12-01 10:30:00'
      },
      {
        id: '2',
        name: '活动照片-书法培训.jpg',
        url: 'https://picsum.photos/400/300?random=activity1',
        thumbnail: 'https://picsum.photos/200/150?random=activity1',
        size: 1024 * 800,
        category: 'activity',
        uploadTime: '2024-12-02 14:20:00'
      },
      {
        id: '3',
        name: '设备图片-YX机器人.jpg',
        url: 'https://picsum.photos/400/300?random=equipment1',
        thumbnail: 'https://picsum.photos/200/150?random=equipment1',
        size: 1024 * 300,
        category: 'equipment',
        uploadTime: '2024-12-03 09:15:00'
      }
    ]
    
    folders.value = [
      {
        id: 'folder-1',
        name: '2024年活动照片',
        imageCount: 25
      },
      {
        id: 'folder-2',
        name: '教学点图片',
        imageCount: 18
      }
    ]
    
    pagination.total = images.value.length
  } catch (error) {
    ElMessage.error('加载图片失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  // 搜索逻辑已在计算属性中处理
}

const handleCategoryChange = () => {
  pagination.page = 1
  // 分类筛选逻辑已在计算属性中处理
}

const handleImageClick = (image: ImageItem) => {
  const index = selectedImages.value.indexOf(image.id)
  if (index > -1) {
    selectedImages.value.splice(index, 1)
  } else {
    selectedImages.value.push(image.id)
  }
}

const handleImageSelect = (imageId: string, selected: boolean) => {
  if (selected) {
    if (!selectedImages.value.includes(imageId)) {
      selectedImages.value.push(imageId)
    }
  } else {
    const index = selectedImages.value.indexOf(imageId)
    if (index > -1) {
      selectedImages.value.splice(index, 1)
    }
  }
}

const handleSelectionChange = (selection: ImageItem[]) => {
  selectedImages.value = selection.map(item => item.id)
}

const handleCreateFolder = () => {
  ElMessage.info('新建文件夹功能开发中...')
}

const handleBatchDelete = async () => {
  if (selectedImages.value.length === 0) {
    ElMessage.warning('请先选择要删除的图片')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedImages.value.length} 张图片吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    selectedImages.value = []
    loadImages()
  } catch (error) {
    // 用户取消删除
  }
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response: any, file: File) => {
  ElMessage.success(`${file.name} 上传成功`)
  loadImages()
}

const handleUploadError = (error: any, file: File) => {
  ElMessage.error(`${file.name} 上传失败`)
}

const handlePreview = (image: ImageItem) => {
  // 预览功能已通过 el-image 的 preview 实现
}

const handleEdit = (image: ImageItem) => {
  ElMessage.info('编辑图片功能开发中...')
}

const handleDelete = async (image: ImageItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除图片"${image.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    loadImages()
  } catch (error) {
    // 用户取消删除
  }
}

const enterFolder = (folder: FolderItem) => {
  currentPath.value.push(folder.name)
  ElMessage.info(`进入文件夹：${folder.name}`)
}

const navigateToRoot = () => {
  currentPath.value = []
  ElMessage.info('返回根目录')
}

const navigateToFolder = (index: number) => {
  currentPath.value = currentPath.value.slice(0, index + 1)
  ElMessage.info(`导航到：${currentPath.value.join('/')}`)
}

const showImageContextMenu = (event: MouseEvent, image: ImageItem) => {
  contextMenuTarget.value = event.target
  // 右键菜单逻辑
}

const showFolderContextMenu = (event: MouseEvent, folder: FolderItem) => {
  contextMenuTarget.value = event.target
  // 右键菜单逻辑
}

const handleContextAction = (action: string) => {
  ElMessage.info(`执行操作：${action}`)
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadImages()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadImages()
}

// 生命周期
onMounted(() => {
  loadImages()
})

// 暴露方法给父组件
defineExpose({
  refresh: loadImages
})
</script>

<style lang="scss" scoped>
.image-management {
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .toolbar-left {
      display: flex;
      gap: 12px;
    }

    .toolbar-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .breadcrumb {
    margin-bottom: 16px;
    padding: 8px 16px;
    background: #f8f9fa;
    border-radius: 4px;

    :deep(.el-breadcrumb__item) {
      cursor: pointer;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 20px;

    .folder-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .folder-icon {
        font-size: 48px;
        color: #409EFF;
        margin-bottom: 12px;
      }

      .folder-name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .folder-count {
        font-size: 12px;
        color: #909399;
      }
    }

    .image-item {
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.selected {
        border: 2px solid #409EFF;
      }

      .image-wrapper {
        position: relative;
        height: 200px;

        .image {
          width: 100%;
          height: 100%;
        }

        .image-error {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background: #f5f7fa;
          color: #909399;
          font-size: 30px;
        }

        .image-checkbox {
          position: absolute;
          top: 8px;
          left: 8px;
          background: rgba(255, 255, 255, 0.9);
          border-radius: 4px;
          padding: 2px;
        }

        .image-overlay {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
          color: white;
          padding: 12px 8px 8px;

          .image-name {
            font-size: 12px;
            font-weight: 500;
            margin-bottom: 2px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .image-size {
            font-size: 11px;
            opacity: 0.8;
          }
        }
      }
    }
  }

  .image-list {
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .image-management {
    .toolbar {
      flex-direction: column;
      gap: 12px;

      .toolbar-left,
      .toolbar-right {
        width: 100%;
        justify-content: center;
      }
    }

    .image-grid {
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 12px;
    }
  }
}
</style>