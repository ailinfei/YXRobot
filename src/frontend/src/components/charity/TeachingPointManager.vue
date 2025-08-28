<!--
  教学点图片管理组件
  支持教学点图片的批量上传、分类管理和展示
-->
<template>
  <div class="teaching-point-manager">
    <!-- 操作栏 -->
    <div class="manager-header">
      <div class="header-left">
        <h3>教学点图片管理</h3>
        <p class="header-subtitle">管理各地教学点的图片资料</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showUploadDialog">
          <el-icon><Plus /></el-icon>
          上传图片
        </el-button>
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="categoryFilter" placeholder="选择分类" clearable @change="handleFilter">
        <el-option label="全部分类" value="" />
        <el-option label="学校" value="school" />
        <el-option label="社区中心" value="community" />
        <el-option label="福利院" value="welfare" />
        <el-option label="图书馆" value="library" />
        <el-option label="其他" value="other" />
      </el-select>
      <el-select v-model="regionFilter" placeholder="选择地区" clearable @change="handleFilter">
        <el-option label="全部地区" value="" />
        <el-option label="北京" value="beijing" />
        <el-option label="上海" value="shanghai" />
        <el-option label="广州" value="guangzhou" />
        <el-option label="深圳" value="shenzhen" />
        <el-option label="杭州" value="hangzhou" />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索教学点名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
        style="width: 200px;"
      />
    </div>

    <!-- 图片网格 -->
    <div class="images-grid" v-loading="loading">
      <div
        v-for="point in filteredTeachingPoints"
        :key="point.id"
        class="teaching-point-card"
      >
        <div class="card-header">
          <div class="point-info">
            <h4 class="point-name">{{ point.name }}</h4>
            <div class="point-meta">
              <el-tag :type="getCategoryTagType(point.category)" size="small">
                {{ getCategoryText(point.category) }}
              </el-tag>
              <span class="point-region">{{ point.region }}</span>
            </div>
          </div>
          <el-dropdown @command="(command) => handlePointAction(command, point)" trigger="click">
            <el-button text>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑信息</el-dropdown-item>
                <el-dropdown-item command="upload">上传图片</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除教学点</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="images-container">
          <div
            v-for="(image, index) in point.images"
            :key="index"
            class="image-item"
            @click="previewImage(point, index)"
          >
            <el-image
              :src="image.url"
              :alt="image.description"
              fit="cover"
              class="point-image"
              :preview-src-list="point.images.map(img => img.url)"
              :initial-index="index"
            />
            <div class="image-overlay">
              <div class="image-actions">
                <el-button text @click.stop="editImage(point, index)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button text @click.stop="deleteImage(point, index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="image-info">
              <p class="image-description">{{ image.description }}</p>
              <span class="image-date">{{ formatDate(image.uploadedAt) }}</span>
            </div>
          </div>

          <!-- 添加图片按钮 -->
          <div class="add-image-btn" @click="uploadToPoint(point)">
            <el-icon><Plus /></el-icon>
            <span>添加图片</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="point-stats">
            <span class="stat-item">
              <el-icon><Picture /></el-icon>
              {{ point.images.length }} 张图片
            </span>
            <span class="stat-item">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(point.createdAt) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredTeachingPoints.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无教学点数据">
          <el-button type="primary" @click="showUploadDialog">添加教学点</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 图片上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传教学点图片"
      width="70%"
      :before-close="handleUploadClose"
    >
      <div class="upload-content">
        <!-- 教学点信息 -->
        <div class="point-form" v-if="!selectedPoint">
          <h4>教学点信息</h4>
          <el-form :model="pointForm" :rules="pointRules" ref="pointFormRef" label-width="100px">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="教学点名称" prop="name">
                  <el-input v-model="pointForm.name" placeholder="请输入教学点名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分类" prop="category">
                  <el-select v-model="pointForm.category" placeholder="选择分类" style="width: 100%">
                    <el-option label="学校" value="school" />
                    <el-option label="社区中心" value="community" />
                    <el-option label="福利院" value="welfare" />
                    <el-option label="图书馆" value="library" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="地区" prop="region">
                  <el-select v-model="pointForm.region" placeholder="选择地区" style="width: 100%">
                    <el-option label="北京" value="beijing" />
                    <el-option label="上海" value="shanghai" />
                    <el-option label="广州" value="guangzhou" />
                    <el-option label="深圳" value="shenzhen" />
                    <el-option label="杭州" value="hangzhou" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="地址" prop="address">
                  <el-input v-model="pointForm.address" placeholder="详细地址" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <!-- 图片上传 -->
        <div class="upload-section">
          <h4>上传图片</h4>
          <el-upload
            ref="uploadRef"
            v-model:file-list="uploadFileList"
            :auto-upload="false"
            :multiple="true"
            :limit="10"
            accept="image/*"
            :before-upload="beforeUpload"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
            drag
            class="upload-dragger"
          >
            <el-icon class="el-icon--upload">
              <Upload />
            </el-icon>
            <div class="el-upload__text">
              将图片拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 JPG/PNG 格式，单个文件不超过 5MB，最多上传10张
              </div>
            </template>
          </el-upload>

          <!-- 图片描述 -->
          <div v-if="uploadFileList.length > 0" class="image-descriptions">
            <h5>图片描述</h5>
            <div
              v-for="(file, index) in uploadFileList"
              :key="index"
              class="description-item"
            >
              <div class="file-preview">
                <img :src="getFilePreviewUrl(file)" :alt="file.name" />
                <span class="file-name">{{ file.name }}</span>
              </div>
              <el-input
                v-model="imageDescriptions[index]"
                placeholder="请输入图片描述"
                maxlength="100"
                show-word-limit
              />
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleUploadClose">取消</el-button>
          <el-button type="primary" @click="handleUploadSubmit" :loading="uploadLoading">
            上传
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  Search,
  MoreFilled,
  Edit,
  Delete,
  Picture,
  Calendar,
  Upload
} from '@element-plus/icons-vue'

// 接口定义
interface TeachingPointImage {
  url: string
  description: string
  uploadedAt: string
}

interface TeachingPoint {
  id: string
  name: string
  category: string
  region: string
  address: string
  images: TeachingPointImage[]
  createdAt: string
}

// 响应式数据
const loading = ref(false)
const categoryFilter = ref('')
const regionFilter = ref('')
const searchKeyword = ref('')
const uploadDialogVisible = ref(false)
const uploadLoading = ref(false)
const selectedPoint = ref<TeachingPoint | null>(null)

// 教学点数据
const teachingPoints = ref<TeachingPoint[]>([])

// 表单数据
const pointForm = ref({
  name: '',
  category: '',
  region: '',
  address: ''
})

const pointRules = {
  name: [{ required: true, message: '请输入教学点名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  region: [{ required: true, message: '请选择地区', trigger: 'change' }]
}

const pointFormRef = ref()
const uploadRef = ref()
const uploadFileList = ref<any[]>([])
const imageDescriptions = ref<string[]>([])

// 计算属性
const filteredTeachingPoints = computed(() => {
  let filtered = teachingPoints.value

  if (categoryFilter.value) {
    filtered = filtered.filter(point => point.category === categoryFilter.value)
  }

  if (regionFilter.value) {
    filtered = filtered.filter(point => point.region === regionFilter.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(point =>
      point.name.toLowerCase().includes(keyword) ||
      point.address.toLowerCase().includes(keyword)
    )
  }

  return filtered
})

// 方法
const loadTeachingPoints = async () => {
  loading.value = true
  try {
    // 模拟数据
    teachingPoints.value = [
      {
        id: '1',
        name: '北京市第一小学',
        category: 'school',
        region: 'beijing',
        address: '北京市朝阳区建国路88号',
        images: [
          {
            url: 'https://picsum.photos/300/200?random=1',
            description: '学校外观',
            uploadedAt: '2024-01-15T10:00:00Z'
          },
          {
            url: 'https://picsum.photos/300/200?random=2',
            description: '教室内部',
            uploadedAt: '2024-01-15T10:30:00Z'
          }
        ],
        createdAt: '2024-01-15T09:00:00Z'
      },
      {
        id: '2',
        name: '上海浦东社区中心',
        category: 'community',
        region: 'shanghai',
        address: '上海市浦东新区世纪大道1000号',
        images: [
          {
            url: 'https://picsum.photos/300/200?random=3',
            description: '社区中心大厅',
            uploadedAt: '2024-01-20T14:00:00Z'
          }
        ],
        createdAt: '2024-01-20T13:00:00Z'
      }
    ]
  } catch (error) {
    console.error('加载教学点数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadTeachingPoints()
}

const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const showUploadDialog = () => {
  selectedPoint.value = null
  pointForm.value = {
    name: '',
    category: '',
    region: '',
    address: ''
  }
  uploadFileList.value = []
  imageDescriptions.value = []
  uploadDialogVisible.value = true
}

const uploadToPoint = (point: TeachingPoint) => {
  selectedPoint.value = point
  uploadFileList.value = []
  imageDescriptions.value = []
  uploadDialogVisible.value = true
}

const handlePointAction = async (command: string, point: TeachingPoint) => {
  switch (command) {
    case 'edit':
      ElMessage.info('编辑教学点功能开发中')
      break
    case 'upload':
      uploadToPoint(point)
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定要删除这个教学点吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        // 删除逻辑
        ElMessage.success('教学点已删除')
        loadTeachingPoints()
      } catch {
        // 用户取消
      }
      break
  }
}

const previewImage = (point: TeachingPoint, index: number) => {
  // 图片预览逻辑已由 el-image 组件处理
}

const editImage = (point: TeachingPoint, index: number) => {
  ElMessage.info('编辑图片功能开发中')
}

const deleteImage = async (point: TeachingPoint, index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这张图片吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    point.images.splice(index, 1)
    ElMessage.success('图片已删除')
  } catch {
    // 用户取消
  }
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传10张图片')
}

const handleRemove = (file: any, fileList: any[]) => {
  const index = uploadFileList.value.indexOf(file)
  if (index > -1) {
    imageDescriptions.value.splice(index, 1)
  }
}

const getFilePreviewUrl = (file: any) => {
  return URL.createObjectURL(file.raw)
}

const handleUploadSubmit = async () => {
  if (!selectedPoint.value) {
    try {
      await pointFormRef.value?.validate()
    } catch {
      return
    }
  }

  if (uploadFileList.value.length === 0) {
    ElMessage.warning('请选择要上传的图片')
    return
  }

  uploadLoading.value = true
  try {
    // 模拟上传
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const newImages = uploadFileList.value.map((file, index) => ({
      url: getFilePreviewUrl(file),
      description: imageDescriptions.value[index] || file.name,
      uploadedAt: new Date().toISOString()
    }))

    if (selectedPoint.value) {
      // 添加到现有教学点
      selectedPoint.value.images.push(...newImages)
    } else {
      // 创建新教学点
      const newPoint: TeachingPoint = {
        id: Date.now().toString(),
        ...pointForm.value,
        images: newImages,
        createdAt: new Date().toISOString()
      }
      teachingPoints.value.unshift(newPoint)
    }

    ElMessage.success('图片上传成功')
    handleUploadClose()
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.value = false
  }
}

const handleUploadClose = () => {
  uploadDialogVisible.value = false
  uploadFileList.value = []
  imageDescriptions.value = []
  selectedPoint.value = null
}

// 工具方法
const getCategoryTagType = (category: string) => {
  const types: Record<string, any> = {
    school: 'primary',
    community: 'success',
    welfare: 'warning',
    library: 'info',
    other: 'default'
  }
  return types[category] || 'default'
}

const getCategoryText = (category: string) => {
  const texts: Record<string, string> = {
    school: '学校',
    community: '社区中心',
    welfare: '福利院',
    library: '图书馆',
    other: '其他'
  }
  return texts[category] || category
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadTeachingPoints()
})
</script>

<style lang="scss" scoped>
.teaching-point-manager {
  .manager-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      h3 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 18px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
      }
    }
    
    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .filter-bar {
    display: flex;
    gap: 12px;
    align-items: center;
    margin-bottom: 24px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
  }

  .images-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 24px;

    .teaching-point-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;

        .point-info {
          flex: 1;

          .point-name {
            margin: 0 0 8px 0;
            font-size: 16px;
            font-weight: 600;
            color: #262626;
          }

          .point-meta {
            display: flex;
            align-items: center;
            gap: 8px;

            .point-region {
              color: #666;
              font-size: 12px;
            }
          }
        }
      }

      .images-container {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 8px;
        padding: 16px;

        .image-item {
          position: relative;
          aspect-ratio: 1;
          border-radius: 8px;
          overflow: hidden;
          cursor: pointer;

          .point-image {
            width: 100%;
            height: 100%;
          }

          .image-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s ease;

            .image-actions {
              display: flex;
              gap: 8px;

              .el-button {
                color: white;
                border-color: white;

                &:hover {
                  background: rgba(255, 255, 255, 0.2);
                }
              }
            }
          }

          &:hover .image-overlay {
            opacity: 1;
          }

          .image-info {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
            color: white;
            padding: 8px;
            font-size: 12px;

            .image-description {
              margin: 0 0 4px 0;
              font-weight: 500;
            }

            .image-date {
              opacity: 0.8;
            }
          }
        }

        .add-image-btn {
          aspect-ratio: 1;
          border: 2px dashed #d9d9d9;
          border-radius: 8px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          color: #999;
          transition: all 0.3s ease;

          &:hover {
            border-color: #409eff;
            color: #409eff;
          }

          span {
            margin-top: 4px;
            font-size: 12px;
          }
        }
      }

      .card-footer {
        padding: 16px;
        border-top: 1px solid #f0f0f0;
        background: #fafafa;

        .point-stats {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;
            color: #666;
            font-size: 12px;
          }
        }
      }
    }

    .empty-state {
      grid-column: 1 / -1;
      padding: 40px;
    }
  }

  .upload-content {
    .point-form {
      margin-bottom: 24px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;

      h4 {
        margin: 0 0 16px 0;
        color: #262626;
      }
    }

    .upload-section {
      h4 {
        margin: 0 0 16px 0;
        color: #262626;
      }

      .image-descriptions {
        margin-top: 20px;

        h5 {
          margin: 0 0 12px 0;
          color: #262626;
          font-size: 14px;
        }

        .description-item {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

          .file-preview {
            display: flex;
            align-items: center;
            gap: 8px;
            min-width: 150px;

            img {
              width: 40px;
              height: 40px;
              object-fit: cover;
              border-radius: 4px;
            }

            .file-name {
              font-size: 12px;
              color: #666;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .teaching-point-manager {
    .images-grid {
      grid-template-columns: 1fr;
    }

    .filter-bar {
      flex-direction: column;
      align-items: stretch;
      gap: 8px;
    }
  }
}
</style>