<template>
  <div class="products-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>产品管理</h2>
        <p class="header-subtitle">333管理官网展示的产品信息、媒体文件和状态</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增产品
        </el-button>
      </div>
    </div>



    <!-- 产品列表表格 -->
    <DataTable
      :data="productList"
      :columns="tableColumns"
      :loading="tableLoading"
      show-selection
      :show-edit="false"
      :show-delete="false"
      @batch-delete="handleBatchDelete"
      @refresh="loadProducts"
    >
      <!-- 产品封面图片 -->
      <template #cover_image_url="{ row }">
        <el-image
          v-if="row.cover_image_url"
          :src="row.cover_image_url"
          :preview-src-list="[row.cover_image_url]"
          fit="cover"
          class="product-cover"
        />
        <span v-else class="no-image">暂无图片</span>
      </template>

      <!-- 产品状态 -->
      <template #status="{ row }">
        <StatusTag :status="row.status" />
      </template>

      <!-- 价格显示 -->
      <template #price="{ row }">
        <span class="price-text">¥{{ formatPrice(row.price) }}</span>
      </template>

      <!-- 操作列 -->
      <template #actions="{ row }">
        <el-button type="primary" size="small" text @click="handleEdit(row)">
          编辑
        </el-button>
        <el-button type="info" size="small" text @click="handleManageMedia(row)">
          媒体管理
        </el-button>
        <el-button type="danger" size="small" text @click="handleDelete(row)">
          删除
        </el-button>
   
      </template>
    </DataTable>

    <!-- 产品编辑对话框 -->
    <CommonDialog
      v-model="editDialogVisible"
      :title="editMode === 'add' ? '新增产品' : '编辑产品'"
      width="800px"
      @confirm="handleSaveProduct"
      @cancel="handleCancelEdit"
      :confirm-loading="saveLoading"
    >
      <FormValidator
        ref="productFormRef"
        v-model="productForm"
        :rules="productRules"
        label-width="100px"
        :show-actions="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="productForm.name" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品型号" prop="model">
              <el-input v-model="productForm.model" placeholder="请输入产品型号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品价格" prop="price">
              <el-input-number
                v-model="productForm.price"
                :min="0"
                :precision="2"
                placeholder="请输入产品价格"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品状态" prop="status">
              <el-select v-model="productForm.status" placeholder="请选择产品状态">
                <el-option label="草稿" value="draft" />
                <el-option label="已发布" value="published" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入产品描述"
          />
        </el-form-item>

        <el-form-item label="产品封面">
          <FileUpload
            v-model="productForm.cover_files"
            :limit="1"
            accept="image/*"
            list-type="picture-card"
            :max-size="5"
            upload-text="上传封面"
            action="/api/v1/upload/product/cover"
            :auto-upload="true"
            @success="handleCoverUploadSuccess"
            @error="handleCoverUploadError"
          />
        </el-form-item>
      </FormValidator>
    </CommonDialog>

    <!-- 媒体管理对话框 -->
    <CommonDialog
      v-model="mediaDialogVisible"
      title="产品媒体管理"
      width="1000px"
      :show-footer="false"
    >
      <div class="media-management">
        <div class="media-tabs">
          <el-tabs v-model="activeMediaTab">
            <el-tab-pane label="产品图片" name="images">
              <div class="media-section">
                <div class="section-header">
                  <h4>产品图片</h4>
                  <el-button type="primary" size="small" @click="handleAddMedia('image')">
                    添加图片
                  </el-button>
                </div>
                <div class="media-grid">
                  <div
                    v-for="(media, index) in productImages"
                    :key="media.id"
                    class="media-item"
                  >
                    <el-image
                      :src="media.media_url"
                      fit="cover"
                      class="media-preview"
                      :preview-src-list="[media.media_url]"
                    />
                    <div class="media-actions">
                      <el-button size="small" type="primary" text @click="handleEditMedia(media)">
                        编辑
                      </el-button>
                      <el-button size="small" type="danger" text @click="handleDeleteMedia(media)">
                        删除
                      </el-button>
                    </div>
                    <div class="media-sort">
                      <span>排序: {{ media.sort_order }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="产品视频" name="videos">
              <div class="media-section">
                <div class="section-header">
                  <h4>产品视频</h4>
                  <el-button type="primary" size="small" @click="handleAddMedia('video')">
                    添加视频
                  </el-button>
                </div>
                <div class="media-grid">
                  <div
                    v-for="(media, index) in productVideos"
                    :key="media.id"
                    class="media-item video-item"
                  >
                    <video
                      :src="media.media_url"
                      controls
                      class="media-preview"
                    />
                    <div class="media-actions">
                      <el-button size="small" type="primary" text @click="handleEditMedia(media)">
                        编辑
                      </el-button>
                      <el-button size="small" type="danger" text @click="handleDeleteMedia(media)">
                        删除
                      </el-button>
                    </div>
                    <div class="media-sort">
                      <span>排序: {{ media.sort_order }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </CommonDialog>

    <!-- 媒体上传对话框 -->
    <CommonDialog
      v-model="mediaUploadDialogVisible"
      :title="`上传${currentMediaType === 'image' ? '图片' : '视频'}`"
      width="600px"
      @confirm="handleSaveMedia"
      @cancel="handleCancelMediaUpload"
      :confirm-loading="mediaUploadLoading"
    >
      <div class="media-upload-form">
        <el-form-item label="媒体文件">
          <FileUpload
            v-model="mediaUploadFiles"
            :limit="1"
            :accept="currentMediaType === 'image' ? 'image/*' : 'video/*'"
            :list-type="currentMediaType === 'image' ? 'picture-card' : 'text'"
            :max-size="currentMediaType === 'image' ? 10 : 100"
            :upload-text="`选择${currentMediaType === 'image' ? '图片' : '视频'}`"
            action="/api/v1/upload/product"
            :data="{ mediaType: currentMediaType }"
            :auto-upload="true"
            @success="handleMediaUploadSuccess"
            @error="handleMediaUploadError"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="mediaForm.sort_order"
            :min="0"
            placeholder="请输入排序值"
            style="width: 100%"
          />
        </el-form-item>
      </div>
    </CommonDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus
} from '@element-plus/icons-vue'
import { DataTable, CommonDialog, FormValidator, StatusTag, FileUpload } from '@/components/common'
import type { TableColumn } from '@/components/common'
import { productApi } from '@/api/product'
import type { Product, CreateProductData, UpdateProductData } from '@/types/product'

// 响应式数据
const tableLoading = ref(false)
const editDialogVisible = ref(false)
const mediaDialogVisible = ref(false)
const mediaUploadDialogVisible = ref(false)
const saveLoading = ref(false)
const mediaUploadLoading = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentProduct = ref<any>(null)
const currentMediaType = ref<'image' | 'video'>('image')
const activeMediaTab = ref('images')

// 表单引用
const productFormRef = ref()

// 产品列表数据
const productList = ref<Product[]>([])
const pagination = ref({
  page: 1,
  size: 20,
  total: 0
})

// 产品媒体数据
const productImages = ref([
  {
    id: 1,
    product_id: 1,
    media_type: 'image',
    media_url: 'https://via.placeholder.com/300x200',
    sort_order: 1
  },
  {
    id: 2,
    product_id: 1,
    media_type: 'image',
    media_url: 'https://via.placeholder.com/300x200',
    sort_order: 2
  }
])

const productVideos = ref([
  {
    id: 3,
    product_id: 1,
    media_type: 'video',
    media_url: 'https://www.w3schools.com/html/mov_bbb.mp4',
    sort_order: 1
  }
])

// 表单数据
const productForm = ref({
  name: '',
  model: '',
  description: '',
  price: 0,
  status: 'draft',
  cover_files: []
})

const mediaForm = reactive({
  sort_order: 1
})

const mediaUploadFiles = ref([])

// 表单验证规则
const productRules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请输入产品型号', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入产品价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择产品状态', trigger: 'change' }
  ]
}

// 表格列配置
const tableColumns: TableColumn[] = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'cover_image_url', label: '封面图片', width: 100 },
  { prop: 'name', label: '产品名称', minWidth: 150 },
  { prop: 'model', label: '产品型号', width: 120 },
  { prop: 'price', label: '价格', width: 100 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createdAt', label: '创建时间', width: 160, type: 'date' },
  { prop: 'updatedAt', label: '更新时间', width: 160, type: 'date' }
]



// 方法
const formatPrice = (price: number) => {
  return price.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const loadProducts = async () => {
  console.log('🚀 开始加载产品列表...')
  tableLoading.value = true
  try {
    console.log('📡 调用API:', {
      page: pagination.value.page,
      size: pagination.value.size
    })
    
    const response = await productApi.getProducts({
      page: pagination.value.page,
      size: pagination.value.size
    })
    
    console.log('📥 API响应:', response)
    
    if (response.code === 200) {
      productList.value = response.data.list || []
      pagination.value.total = response.data.total || 0
      
      // 详细检查每个产品的时间字段
      console.log('✅ 产品列表加载成功:', {
        count: productList.value.length,
        total: pagination.value.total,
        firstProduct: productList.value[0],
        timeFields: productList.value.map(p => ({
          id: p.id,
          name: p.name,
          createdAt: p.createdAt,
          updatedAt: p.updatedAt,
          created_at: p.created_at,
          updated_at: p.updated_at
        }))
      })
      // 只在初始加载时显示成功消息，避免删除后重新加载时的干扰
      // ElMessage.success(`成功加载 ${productList.value.length} 个产品`)
    } else {
      console.error('❌ API返回错误:', response)
      ElMessage.error(response.message || '获取产品列表失败')
    }
  } catch (error) {
    console.error('💥 获取产品列表失败:', error)
    ElMessage.error('获取产品列表失败，请稍后重试')
    // 清空产品列表，不使用fallback数据
    productList.value = []
  } finally {
    tableLoading.value = false
  }
}



const handleAdd = () => {
  editMode.value = 'add'
  resetProductForm()
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  editMode.value = 'edit'
  currentProduct.value = row
  Object.assign(productForm.value, {
    name: row.name,
    model: row.model,
    description: row.description,
    price: row.price,
    status: row.status,
    cover_files: []
  })
  editDialogVisible.value = true
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除这个产品吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    console.log('🗑️ 开始删除产品:', row.id, row.name)
    const response = await productApi.deleteProduct(row.id.toString())
    console.log('🗑️ 删除API响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('产品删除成功')
      console.log('🔄 删除成功，清除缓存并重新加载产品列表...')
      
      // 清除产品列表相关的缓存
      const { clearCache } = await import('@/utils/request')
      clearCache('/admin/products')
      
      // 立即重新加载产品列表
      loadProducts()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error)
      ElMessage.error('删除产品失败，请稍后重试')
    }
  }
}

const handleBatchDelete = async (rows: any[]) => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${rows.length} 个产品吗？`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // TODO: 实现批量删除API调用
    // const deletePromises = rows.map(row => productApi.deleteProduct(row.id.toString()))
    // await Promise.all(deletePromises)
    
    ElMessage.success('产品批量删除成功')
    
    // 清除缓存并刷新列表
    const { clearCache } = await import('@/utils/request')
    clearCache('/admin/products')
    loadProducts()
  } catch {
    // 用户取消删除
  }
}

const handleManageMedia = (row: any) => {
  currentProduct.value = row
  mediaDialogVisible.value = true
  // 加载产品媒体数据
  loadProductMedia(row.id)
}

const loadProductMedia = async (productId: number) => {
  console.log('加载产品媒体:', productId)
  try {
    const response = await productApi.getProductMedia(productId.toString())
    if (response.code === 200) {
      // 分离图片和视频
      const images = response.data.filter(media => media.media_type === 'image')
      const videos = response.data.filter(media => media.media_type === 'video')
      
      productImages.value = images
      productVideos.value = videos
      
      console.log('成功加载产品媒体 - 图片:', images.length, '视频:', videos.length)
    } else {
      ElMessage.error(response.message || '获取产品媒体失败')
    }
  } catch (error) {
    console.error('获取产品媒体失败:', error)
    ElMessage.error('获取产品媒体失败，请稍后重试')
  }
}

const handleAddMedia = (type: 'image' | 'video') => {
  currentMediaType.value = type
  mediaForm.sort_order = 1
  mediaUploadFiles.value = []
  mediaUploadDialogVisible.value = true
}

const handleEditMedia = async (media: any) => {
  try {
    const { value: newSortOrder } = await ElMessageBox.prompt('请输入新的排序值', '编辑媒体', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: media.sort_order.toString(),
      inputPattern: /^\d+$/,
      inputErrorMessage: '请输入有效的数字'
    })
    
    const sortOrder = parseInt(newSortOrder)
    const response = await productApi.updateProductMedia(media.id.toString(), sortOrder)
    
    if (response.code === 200) {
      ElMessage.success('媒体信息更新成功')
      
      // 清除缓存并刷新媒体列表
      const { clearCache } = await import('@/utils/request')
      clearCache('/admin/products')
      loadProductMedia(currentProduct.value?.id)
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新媒体信息失败:', error)
      ElMessage.error('更新媒体信息失败，请稍后重试')
    }
  }
}

const handleDeleteMedia = async (media: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个媒体文件吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await productApi.deleteProductMedia(media.id.toString())
    if (response.code === 200) {
      ElMessage.success('媒体文件删除成功')
      
      // 清除缓存并刷新媒体列表
      const { clearCache } = await import('@/utils/request')
      clearCache('/admin/products')
      loadProductMedia(currentProduct.value?.id)
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除媒体文件失败:', error)
      ElMessage.error('删除媒体文件失败，请稍后重试')
    }
  }
}

const handleSaveProduct = async () => {
  const isValid = await productFormRef.value?.validate()
  if (!isValid) return
  
  saveLoading.value = true
  try {
    let response
    
    // 提取封面图片URL
    let coverImageUrl = null
    if (productForm.value.cover_files && productForm.value.cover_files.length > 0) {
      const coverFile = productForm.value.cover_files[0]
      if (coverFile.response && coverFile.response.code === 200) {
        coverImageUrl = coverFile.response.data.url
      } else if (coverFile.url) {
        coverImageUrl = coverFile.url
      }
    }
    
    if (editMode.value === 'add') {
      // 创建产品
      const createData = {
        name: productForm.value.name,
        model: productForm.value.model,
        description: productForm.value.description,
        price: productForm.value.price,
        status: productForm.value.status,
        cover_image_url: coverImageUrl
      }
      
      response = await productApi.createProductWithUrls(createData)
    } else {
      // 更新产品
      const updateData = {
        name: productForm.value.name,
        model: productForm.value.model,
        description: productForm.value.description,
        price: productForm.value.price,
        status: productForm.value.status,
        cover_image_url: coverImageUrl
      }
      
      response = await productApi.updateProductWithUrls(currentProduct.value.id.toString(), updateData)
    }
    
    if (response.code === 200) {
      ElMessage.success(`产品${editMode.value === 'add' ? '创建' : '更新'}成功`)
      editDialogVisible.value = false
      
      // 清除缓存并刷新列表
      const { clearCache } = await import('@/utils/request')
      clearCache('/admin/products')
      loadProducts()
    } else {
      ElMessage.error(response.message || `${editMode.value === 'add' ? '创建' : '更新'}失败`)
    }
  } catch (error) {
    console.error('保存产品失败:', error)
    ElMessage.error(`产品${editMode.value === 'add' ? '创建' : '更新'}失败，请稍后重试`)
  } finally {
    saveLoading.value = false
  }
}

const handleCancelEdit = () => {
  editDialogVisible.value = false
  resetProductForm()
}

const handleSaveMedia = async () => {
  if (mediaUploadFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  
  mediaUploadLoading.value = true
  try {
    const file = mediaUploadFiles.value[0]
    
    // 检查文件是否已经上传成功
    let mediaUrl = null
    if (file.response && file.response.code === 200) {
      mediaUrl = file.response.data.url
    } else if (file.url) {
      mediaUrl = file.url
    } else {
      ElMessage.error('文件上传未完成，请等待上传完成后再保存')
      return
    }
    
    // 调用产品媒体保存API
    const response = await productApi.uploadProductMedia(
      currentProduct.value.id.toString(),
      currentMediaType.value,
      mediaForm.sort_order,
      file.raw // 传递原始文件对象
    )
    
    if (response.code === 200) {
      ElMessage.success('媒体文件保存成功')
      mediaUploadDialogVisible.value = false
      
      // 清除缓存并刷新媒体列表
      const { clearCache } = await import('@/utils/request')
      clearCache('/admin/products')
      loadProductMedia(currentProduct.value?.id)
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存媒体文件失败:', error)
    ElMessage.error('保存媒体文件失败，请稍后重试')
  } finally {
    mediaUploadLoading.value = false
  }
}

const handleCancelMediaUpload = () => {
  mediaUploadDialogVisible.value = false
  mediaUploadFiles.value = []
}

const resetProductForm = () => {
  Object.assign(productForm.value, {
    name: '',
    model: '',
    description: '',
    price: 0,
    status: 'draft',
    cover_files: []
  })
}

// 文件上传处理方法
const handleCoverUploadSuccess = (response: any, file: any) => {
  console.log('封面上传成功:', response)
  if (response.code === 200) {
    ElMessage.success('封面上传成功')
    // 更新文件信息
    file.url = response.data.url
    file.response = response
  } else {
    ElMessage.error(response.message || '封面上传失败')
  }
}

const handleCoverUploadError = (error: any, file: any) => {
  console.error('封面上传失败:', error)
  ElMessage.error('封面上传失败，请稍后重试')
}

const handleMediaUploadSuccess = (response: any, file: any) => {
  console.log('媒体上传成功:', response)
  if (response.code === 200) {
    ElMessage.success('媒体文件上传成功')
    // 更新文件信息
    file.url = response.data.url
    file.response = response
  } else {
    ElMessage.error(response.message || '媒体上传失败')
  }
}

const handleMediaUploadError = (error: any, file: any) => {
  console.error('媒体上传失败:', error)
  ElMessage.error('媒体文件上传失败，请稍后重试')
}

// 生命周期
onMounted(() => {
  loadProducts()
})
</script>

<style lang="scss" scoped>
.products-management {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    
    .header-left {
      h2 {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 4px 0;
      }
      
      .header-subtitle {
        color: var(--text-secondary);
        font-size: 14px;
        margin: 0;
      }
    }
  }
  
  .product-cover {
    width: 60px;
    height: 60px;
    border-radius: var(--radius-sm);
  }
  
  .no-image {
    color: var(--text-light);
    font-size: 12px;
  }
  
  .price-text {
    font-weight: 600;
    color: var(--primary-color);
  }
}

.media-management {
  .media-tabs {
    .media-section {
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        h4 {
          margin: 0;
          color: var(--text-primary);
        }
      }
      
      .media-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 16px;
        
        .media-item {
          border: 1px solid var(--border-color);
          border-radius: var(--radius-md);
          overflow: hidden;
          
          .media-preview {
            width: 100%;
            height: 150px;
            object-fit: cover;
          }
          
          .media-actions {
            display: flex;
            justify-content: center;
            gap: 8px;
            padding: 8px;
            background: var(--bg-secondary);
          }
          
          .media-sort {
            padding: 4px 8px;
            background: var(--bg-tertiary);
            font-size: 12px;
            color: var(--text-secondary);
            text-align: center;
          }
          
          &.video-item {
            .media-preview {
              height: 120px;
            }
          }
        }
      }
    }
  }
}

.media-upload-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .products-management {
    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }
  }
  
  .media-management {
    .media-grid {
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 12px;
    }
  }
}
</style>