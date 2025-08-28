<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'create' ? '添加产品' : '编辑产品'"
    width="90%"
    :before-close="handleClose"
    class="product-edit-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="product-form"
    >
      <el-tabs v-model="activeTab" class="form-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="form-section">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="产品名称" prop="name">
                  <el-input
                    v-model="formData.name"
                    placeholder="请输入产品名称"
                    maxlength="50"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="产品型号" prop="model">
                  <el-input
                    v-model="formData.model"
                    placeholder="请输入产品型号"
                    maxlength="30"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="产品标签" prop="badge">
                  <el-input
                    v-model="formData.badge"
                    placeholder="如：教育专版、家庭首选等"
                    maxlength="20"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="产品状态" prop="status">
                  <el-select v-model="formData.status" placeholder="请选择状态">
                    <el-option label="草稿" value="draft" />
                    <el-option label="已发布" value="published" />
                    <el-option label="已归档" value="archived" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="产品描述" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="4"
                placeholder="请输入产品描述"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 价格设置 -->
        <el-tab-pane label="价格设置" name="price">
          <div class="form-section">
            <!-- 基础价格设置 -->
            <div class="basic-price-section">
              <h4>基础价格</h4>
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="当前价格" prop="price">
                    <el-input-number
                      v-model="formData.price"
                      :min="0"
                      :precision="2"
                      placeholder="请输入当前价格"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="原价">
                    <el-input-number
                      v-model="formData.originalPrice"
                      :min="0"
                      :precision="2"
                      placeholder="请输入原价（可选）"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <div class="price-preview" v-if="formData.price">
                <h4>价格预览</h4>
                <div class="price-display">
                  <span class="current-price">¥{{ formData.price.toLocaleString() }}</span>
                  <span v-if="formData.originalPrice" class="original-price">
                    ¥{{ formData.originalPrice.toLocaleString() }}
                  </span>
                  <span v-if="formData.originalPrice && formData.originalPrice > formData.price" class="discount">
                    立省¥{{ (formData.originalPrice - formData.price).toLocaleString() }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 多币种价格配置 -->
            <div class="multi-currency-section">
              <MultiCurrencyPrice v-model="formData.prices" />
            </div>
          </div>
        </el-tab-pane>

        <!-- 产品特性 -->
        <el-tab-pane label="产品特性" name="features">
          <div class="form-section">
            <el-form-item label="基础特性">
              <div class="features-editor">
                <div 
                  v-for="(feature, index) in formData.features" 
                  :key="index"
                  class="feature-item"
                >
                  <el-input
                    v-model="formData.features[index]"
                    placeholder="请输入特性描述"
                    maxlength="50"
                  />
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    size="small"
                    @click="removeFeature(index)"
                  />
                </div>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addFeature"
                  class="add-feature-btn"
                >
                  添加特性
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="产品亮点">
              <div class="highlights-editor">
                <div 
                  v-for="(highlight, index) in formData.highlights" 
                  :key="index"
                  class="highlight-item"
                >
                  <el-input
                    v-model="formData.highlights[index]"
                    placeholder="请输入产品亮点"
                    maxlength="100"
                  />
                  <el-button
                    type="danger"
                    :icon="Delete"
                    circle
                    size="small"
                    @click="removeHighlight(index)"
                  />
                </div>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addHighlight"
                  class="add-highlight-btn"
                >
                  添加亮点
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="详细功能">
              <div class="product-features-editor">
                <div 
                  v-for="(feature, index) in formData.productFeatures" 
                  :key="index"
                  class="product-feature-item"
                >
                  <el-card class="feature-card">
                    <div class="feature-header">
                      <el-input
                        v-model="feature.title"
                        placeholder="功能标题"
                        maxlength="30"
                        class="feature-title"
                      />
                      <el-button
                        type="danger"
                        :icon="Delete"
                        circle
                        size="small"
                        @click="removeProductFeature(index)"
                      />
                    </div>
                    <el-input
                      v-model="feature.description"
                      type="textarea"
                      :rows="3"
                      placeholder="功能描述"
                      maxlength="200"
                      show-word-limit
                    />
                  </el-card>
                </div>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addProductFeature"
                  class="add-product-feature-btn"
                >
                  添加功能
                </el-button>
              </div>
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 技术规格 -->
        <el-tab-pane label="技术规格" name="specs">
          <div class="form-section">
            <div class="specs-editor">
              <div 
                v-for="(value, key) in formData.specifications" 
                :key="key"
                class="spec-item"
              >
                <el-input
                  :value="getSpecLabel(key)"
                  disabled
                  class="spec-label"
                />
                <el-input
                  v-model="formData.specifications[key]"
                  placeholder="请输入规格参数"
                  class="spec-value"
                />
                <el-button
                  type="danger"
                  :icon="Delete"
                  circle
                  size="small"
                  @click="removeSpec(key)"
                />
              </div>
              
              <div class="add-spec">
                <el-select
                  v-model="newSpecKey"
                  placeholder="选择规格类型"
                  style="width: 200px; margin-right: 12px;"
                >
                  <el-option
                    v-for="spec in availableSpecs"
                    :key="spec.key"
                    :label="spec.label"
                    :value="spec.key"
                  />
                </el-select>
                <el-button
                  type="primary"
                  :icon="Plus"
                  @click="addSpec"
                  :disabled="!newSpecKey"
                >
                  添加规格
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 产品图片 -->
        <el-tab-pane label="产品图片" name="images">
          <div class="form-section">
            <ProductImageCarousel
              :images="formData.images"
              :readonly="false"
              :max-count="10"
              :max-size="5"
              @upload="handleImageUpload"
              @delete="handleImageDelete"
              @set-main="handleSetMainImage"
              @reorder="handleImageReorder"
            />
          </div>
        </el-tab-pane>

        <!-- 状态工作流 -->
        <el-tab-pane label="状态工作流" name="workflow" v-if="mode === 'edit'">
          <div class="form-section">
            <ProductWorkflow
              :current-status="formData.status || 'draft'"
              :product-id="product?.id"
              :readonly="false"
              @status-change="handleStatusChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <!-- 自动保存状态指示 -->
          <div v-if="mode === 'edit'" class="auto-save-status">
            <el-icon v-if="autoSaving" class="loading"><Loading /></el-icon>
            <span class="status-text">
              {{ autoSaving ? '自动保存中...' : '已自动保存' }}
            </span>
          </div>
        </div>
        
        <div class="footer-right">
          <el-button @click="handleClose">取消</el-button>
          <el-button @click="handlePreview" :disabled="!isFormValid">
            预览
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSave"
            :loading="saving"
            :disabled="!isFormValid"
          >
            {{ mode === 'create' ? '创建产品' : '保存修改' }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Loading } from '@element-plus/icons-vue'
import ProductImageCarousel from './ProductImageCarousel.vue'
import MultiCurrencyPrice from './MultiCurrencyPrice.vue'
import ProductWorkflow from './ProductWorkflow.vue'
import type { Product, CreateProductData, UpdateProductData, ProductFeature, ProductImage, ProductPrice } from '@/types/product'
import { productApi } from '@/api/product'

interface Props {
  modelValue: boolean
  product: Product | null
  mode: 'create' | 'edit'
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref()
const activeTab = ref('basic')
const saving = ref(false)
const autoSaving = ref(false)
const newSpecKey = ref('')
const autoSaveTimer = ref<NodeJS.Timeout | null>(null)

// 表单数据
const formData = ref<CreateProductData & { images: ProductImage[]; prices: ProductPrice[]; status?: string }>({
  name: '',
  model: '',
  description: '',
  badge: '',
  features: [],
  productFeatures: [],
  specifications: {},
  price: 0,
  originalPrice: undefined,
  highlights: [],
  images: [],
  prices: [],
  status: 'draft'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '产品名称长度在2-50个字符', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请输入产品型号', trigger: 'blur' },
    { min: 2, max: 30, message: '产品型号长度在2-30个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入产品描述', trigger: 'blur' },
    { min: 10, max: 500, message: '产品描述长度在10-500个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入产品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于0', trigger: 'blur' }
  ]
}

// 可用的规格选项
const availableSpecs = [
  { key: 'dimensions', label: '产品尺寸' },
  { key: 'weight', label: '产品重量' },
  { key: 'display', label: '显示屏幕' },
  { key: 'connectivity', label: '连接方式' },
  { key: 'teaching', label: '教学方式' },
  { key: 'recognition', label: '识别技术' },
  { key: 'courses', label: '课程内容' },
  { key: 'management', label: '管理系统' },
  { key: 'battery', label: '电池续航' },
  { key: 'storage', label: '存储容量' },
  { key: 'material', label: '材质工艺' },
  { key: 'warranty', label: '质保期限' }
]

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isFormValid = computed(() => {
  return formData.value.name && 
         formData.value.model && 
         formData.value.description && 
         formData.value.price > 0
})

// 监听产品数据变化
watch(
  () => props.product,
  (newProduct) => {
    if (newProduct && props.mode === 'edit') {
      initFormData(newProduct)
    } else if (props.mode === 'create') {
      resetFormData()
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(visible, (newVisible) => {
  if (newVisible) {
    activeTab.value = 'basic'
    if (props.mode === 'create') {
      resetFormData()
    } else if (props.product) {
      initFormData(props.product)
    }
  }
})

// 方法
const initFormData = (product: Product) => {
  formData.value = {
    name: product.name,
    model: product.model,
    description: product.description,
    badge: product.badge || '',
    features: [...product.features],
    productFeatures: [...product.productFeatures],
    specifications: { ...product.specifications },
    price: product.price,
    originalPrice: product.originalPrice,
    highlights: [...product.highlights],
    images: [...product.images],
    prices: product.prices ? [...product.prices] : [],
    status: product.status || 'draft'
  }
}

const resetFormData = () => {
  formData.value = {
    name: '',
    model: '',
    description: '',
    badge: '',
    features: ['AI智能笔迹识别', '实时书写指导'],
    productFeatures: [
      {
        title: 'AI智能识别',
        description: '采用先进的人工智能技术，精准识别笔画轨迹，实时纠正书写错误。'
      }
    ],
    specifications: {
      dimensions: '',
      weight: '',
      display: '',
      connectivity: ''
    },
    price: 0,
    originalPrice: undefined,
    highlights: ['AI智能识别技术', '专业书法教学'],
    images: [],
    prices: [
      {
        currency: 'CNY',
        amount: 0,
        originalAmount: undefined,
        discount: undefined
      }
    ],
    status: 'draft'
  }
}

const getSpecLabel = (key: string): string => {
  const spec = availableSpecs.find(s => s.key === key)
  return spec?.label || key
}

// 特性管理
const addFeature = () => {
  formData.value.features.push('')
}

const removeFeature = (index: number) => {
  formData.value.features.splice(index, 1)
}

// 亮点管理
const addHighlight = () => {
  formData.value.highlights.push('')
}

const removeHighlight = (index: number) => {
  formData.value.highlights.splice(index, 1)
}

// 产品功能管理
const addProductFeature = () => {
  formData.value.productFeatures.push({
    title: '',
    description: ''
  })
}

const removeProductFeature = (index: number) => {
  formData.value.productFeatures.splice(index, 1)
}

// 规格管理
const addSpec = () => {
  if (newSpecKey.value && !formData.value.specifications[newSpecKey.value]) {
    formData.value.specifications[newSpecKey.value] = ''
    newSpecKey.value = ''
  }
}

const removeSpec = (key: string) => {
  delete formData.value.specifications[key]
}

// 图片管理
const handleImageUpload = async (file: File) => {
  try {
    // 验证文件类型和大小
    if (!file.type.startsWith('image/')) {
      ElMessage.error('只能上传图片文件')
      return
    }
    
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过5MB')
      return
    }
    
    // 检查图片数量限制
    if (formData.value.images.length >= 10) {
      ElMessage.error('最多只能上传10张图片')
      return
    }
    
    // 这里应该调用实际的上传API
    // const response = await productApi.uploadProductImage(props.product?.id || 'temp', file)
    
    // 模拟上传成功
    const newImage: ProductImage = {
      id: Date.now().toString(),
      url: URL.createObjectURL(file),
      alt: file.name,
      order: formData.value.images.length + 1,
      type: formData.value.images.length === 0 ? 'main' : 'gallery'
    }
    
    formData.value.images.push(newImage)
    ElMessage.success('图片上传成功')
    
    // 触发自动保存
    autoSave()
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

const handleImageDelete = (imageId: string) => {
  const index = formData.value.images.findIndex(img => img.id === imageId)
  if (index > -1) {
    formData.value.images.splice(index, 1)
    ElMessage.success('图片删除成功')
  }
}

const handleSetMainImage = (imageId: string) => {
  formData.value.images.forEach(img => {
    img.type = img.id === imageId ? 'main' : 'gallery'
  })
  ElMessage.success('主图设置成功')
}

const handleImageReorder = (imageOrders: Array<{ id: string; order: number }>) => {
  imageOrders.forEach(({ id, order }) => {
    const image = formData.value.images.find(img => img.id === id)
    if (image) {
      image.order = order
    }
  })
}

// 对话框操作
const handleClose = () => {
  visible.value = false
}

const handlePreview = () => {
  // 创建预览数据
  const previewProduct: Product = {
    id: props.product?.id || 'preview',
    name: formData.value.name,
    model: formData.value.model,
    description: formData.value.description,
    badge: formData.value.badge,
    features: formData.value.features.filter(f => f.trim()),
    productFeatures: formData.value.productFeatures.filter(f => f.title.trim() && f.description.trim()),
    specifications: formData.value.specifications,
    price: formData.value.price,
    originalPrice: formData.value.originalPrice,
    prices: formData.value.prices,
    images: formData.value.images,
    highlights: formData.value.highlights.filter(h => h.trim()),
    status: formData.value.status || 'draft',
    createdAt: props.product?.createdAt || new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
  
  // 在新窗口中打开预览
  const previewWindow = window.open('', '_blank', 'width=1200,height=800')
  if (previewWindow) {
    previewWindow.document.write(generatePreviewHTML(previewProduct))
    previewWindow.document.close()
  } else {
    ElMessage.error('无法打开预览窗口，请检查浏览器弹窗设置')
  }
}

const generatePreviewHTML = (product: Product): string => {
  const mainImage = product.images.find(img => img.type === 'main') || product.images[0]
  
  return `
    <!DOCTYPE html>
    <html lang="zh-CN">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>${product.name} - 产品预览</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
        
        .site-header { background: white; border-bottom: 1px solid #eee; padding: 16px 0; }
        .header-content { display: flex; justify-content: space-between; align-items: center; }
        .logo { font-size: 24px; font-weight: bold; color: #3B82F6; }
        .nav-menu { display: flex; gap: 32px; }
        .nav-menu span { cursor: pointer; color: #6B7280; }
        .nav-menu .active { color: #3B82F6; }
        
        .breadcrumb { background: #f8f9fa; padding: 12px 0; font-size: 14px; color: #6B7280; }
        
        .product-main { padding: 40px 0; }
        .product-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: start; }
        
        .product-gallery .main-image { position: relative; border-radius: 12px; overflow: hidden; margin-bottom: 16px; aspect-ratio: 4/3; }
        .product-gallery img { width: 100%; height: 100%; object-fit: cover; }
        .product-badge { position: absolute; top: 16px; left: 16px; background: #3B82F6; color: white; padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 600; }
        
        .thumbnail-list { display: flex; gap: 12px; }
        .thumbnail { width: 80px; height: 80px; border-radius: 8px; overflow: hidden; cursor: pointer; }
        
        .product-title { font-size: 32px; font-weight: 700; color: #111827; margin-bottom: 8px; }
        .product-subtitle { font-size: 16px; color: #6B7280; margin-bottom: 24px; }
        
        .price-section { display: flex; align-items: center; gap: 16px; margin-bottom: 32px; }
        .current-price { font-size: 36px; font-weight: 700; color: #3B82F6; }
        .original-price { font-size: 18px; color: #9CA3AF; text-decoration: line-through; }
        .discount { background: #10B981; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
        
        .highlights { margin-bottom: 32px; }
        .highlights h3 { font-size: 18px; font-weight: 600; margin-bottom: 16px; color: #111827; }
        .highlights ul { list-style: none; }
        .highlights li { padding: 8px 0; color: #6B7280; position: relative; padding-left: 20px; }
        .highlights li::before { content: '✓'; position: absolute; left: 0; color: #3B82F6; font-weight: bold; }
        
        .purchase-actions { display: flex; gap: 12px; flex-wrap: wrap; }
        .btn { padding: 12px 24px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.3s ease; border: none; }
        .btn-primary { background: #3B82F6; color: white; }
        .btn-secondary { background: #10B981; color: white; }
        .btn-outline { background: transparent; color: #3B82F6; border: 2px solid #3B82F6; }
        
        .features-section, .specs-section { padding: 60px 0; background: #f8f9fa; }
        .section-title { text-align: center; font-size: 32px; font-weight: 700; margin-bottom: 48px; color: #111827; }
        
        .features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 32px; }
        .feature-card { background: white; padding: 32px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
        .feature-card h3 { font-size: 20px; font-weight: 600; margin-bottom: 16px; color: #111827; }
        .feature-card p { color: #6B7280; }
        
        .specs-table { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
        .spec-row { display: flex; border-bottom: 1px solid #eee; }
        .spec-row:last-child { border-bottom: none; }
        .spec-label { flex: 1; padding: 20px 24px; background: #f8f9fa; font-weight: 600; color: #111827; }
        .spec-value { flex: 2; padding: 20px 24px; color: #6B7280; }
        
        @media (max-width: 768px) {
          .product-layout { grid-template-columns: 1fr; gap: 32px; }
          .features-grid { grid-template-columns: 1fr; }
          .spec-row { flex-direction: column; }
        }
      </style>
    </head>
    <body>
      <div class="site-header">
        <div class="container">
          <div class="header-content">
            <div class="logo">YXRobot</div>
            <nav class="nav-menu">
              <span>首页</span>
              <span class="active">产品中心</span>
              <span>解决方案</span>
              <span>关于我们</span>
            </nav>
          </div>
        </div>
      </div>

      <div class="breadcrumb">
        <div class="container">
          首页 / 产品中心 / ${product.name}
        </div>
      </div>

      <div class="product-main">
        <div class="container">
          <div class="product-layout">
            <div class="product-gallery">
              <div class="main-image">
                <img src="${mainImage?.url || ''}" alt="${product.name}" />
                ${product.badge ? `<div class="product-badge">${product.badge}</div>` : ''}
              </div>
              <div class="thumbnail-list">
                ${product.images.slice(0, 4).map(img => `
                  <div class="thumbnail">
                    <img src="${img.url}" alt="${img.alt}" />
                  </div>
                `).join('')}
              </div>
            </div>

            <div class="product-info">
              <h1 class="product-title">${product.name}</h1>
              <div class="product-subtitle">${product.model} | 专为教育机构设计</div>
              
              <div class="price-section">
                <div class="current-price">¥${product.price?.toLocaleString()}</div>
                ${product.originalPrice ? `<div class="original-price">¥${product.originalPrice.toLocaleString()}</div>` : ''}
                ${product.originalPrice ? `<div class="discount">立省¥${(product.originalPrice - product.price).toLocaleString()}</div>` : ''}
              </div>

              <div class="highlights">
                <h3>产品亮点</h3>
                <ul>
                  ${product.highlights.map(highlight => `<li>${highlight}</li>`).join('')}
                </ul>
              </div>

              <div class="purchase-actions">
                <button class="btn btn-primary">立即购买</button>
                <button class="btn btn-secondary">租赁体验</button>
                <button class="btn btn-outline">咨询详情</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="features-section">
        <div class="container">
          <h2 class="section-title">核心功能</h2>
          <div class="features-grid">
            ${product.productFeatures.map(feature => `
              <div class="feature-card">
                <h3>${feature.title}</h3>
                <p>${feature.description}</p>
              </div>
            `).join('')}
          </div>
        </div>
      </div>

      <div class="specs-section">
        <div class="container">
          <h2 class="section-title">技术规格</h2>
          <div class="specs-table">
            ${Object.entries(product.specifications).map(([key, value]) => `
              <div class="spec-row">
                <div class="spec-label">${getSpecLabel(key)}</div>
                <div class="spec-value">${value}</div>
              </div>
            `).join('')}
          </div>
        </div>
      </div>
    </body>
    </html>
  `
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
    management: '管理系统',
    battery: '电池续航',
    storage: '存储容量',
    material: '材质工艺',
    warranty: '质保期限'
  }
  return labels[key] || key
}

// 状态变更处理
const handleStatusChange = (newStatus: string, comment?: string) => {
  formData.value.status = newStatus
  ElMessage.success(`产品状态已变更为${getStatusText(newStatus)}`)
}

const getStatusText = (status: string): string => {
  const statusMap = {
    draft: '草稿',
    review: '审核中',
    published: '已发布',
    archived: '已归档'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

// 自动保存功能
const autoSave = async () => {
  if (props.mode === 'create' || !props.product) return
  
  // 清除之前的定时器
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
  
  // 设置新的定时器
  autoSaveTimer.value = setTimeout(async () => {
    try {
      autoSaving.value = true
      
      // 准备自动保存数据
      const saveData: UpdateProductData = {
        name: formData.value.name,
        model: formData.value.model,
        description: formData.value.description,
        badge: formData.value.badge || undefined,
        features: formData.value.features.filter(f => f.trim()),
        productFeatures: formData.value.productFeatures.filter(f => f.title.trim() && f.description.trim()),
        specifications: formData.value.specifications,
        price: formData.value.price,
        originalPrice: formData.value.originalPrice,
        highlights: formData.value.highlights.filter(h => h.trim())
      }
      
      // await productApi.updateProduct(props.product.id, saveData)
      console.log('自动保存成功')
    } catch (error) {
      console.error('自动保存失败:', error)
    } finally {
      autoSaving.value = false
    }
  }, 3000) // 3秒后自动保存
}

// 监听表单数据变化，触发自动保存
watch(
  formData,
  () => {
    if (props.mode === 'edit' && props.product) {
      autoSave()
    }
  },
  { deep: true }
)

const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate()
    
    saving.value = true
    
    // 准备保存数据
    const saveData: CreateProductData | UpdateProductData = {
      name: formData.value.name,
      model: formData.value.model,
      description: formData.value.description,
      badge: formData.value.badge || undefined,
      features: formData.value.features.filter(f => f.trim()),
      productFeatures: formData.value.productFeatures.filter(f => f.title.trim() && f.description.trim()),
      specifications: formData.value.specifications,
      price: formData.value.price,
      originalPrice: formData.value.originalPrice,
      highlights: formData.value.highlights.filter(h => h.trim())
    }
    
    if (props.mode === 'create') {
      // await productApi.createProduct(saveData as CreateProductData)
      ElMessage.success('产品创建成功')
    } else if (props.product) {
      // await productApi.updateProduct(props.product.id, saveData as UpdateProductData)
      ElMessage.success('产品更新成功')
    }
    
    // 清除自动保存定时器
    if (autoSaveTimer.value) {
      clearTimeout(autoSaveTimer.value)
      autoSaveTimer.value = null
    }
    
    emit('success')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请检查表单信息')
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.product-edit-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.product-form {
  .form-tabs {
    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 24px;
      background: var(--bg-secondary);
      border-bottom: 1px solid var(--border-color);
    }
    
    :deep(.el-tabs__content) {
      padding: 24px;
    }
  }
  
  .form-section {
    .el-form-item {
      margin-bottom: 24px;
    }
  }
}

.price-preview {
  margin-top: 24px;
  padding: 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  
  h4 {
    margin: 0 0 16px 0;
    color: var(--text-primary);
  }
  
  .price-display {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .current-price {
      font-size: 32px;
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
      border-radius: var(--radius-sm);
      font-size: 12px;
      font-weight: 600;
    }
  }
}

.features-editor,
.highlights-editor {
  .feature-item,
  .highlight-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
    
    .el-input {
      flex: 1;
    }
  }
  
  .add-feature-btn,
  .add-highlight-btn {
    margin-top: 8px;
  }
}

.product-features-editor {
  .product-feature-item {
    margin-bottom: 16px;
    
    .feature-card {
      .feature-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        
        .feature-title {
          flex: 1;
        }
      }
    }
  }
  
  .add-product-feature-btn {
    margin-top: 8px;
  }
}

.specs-editor {
  .spec-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
    
    .spec-label {
      width: 150px;
      flex-shrink: 0;
    }
    
    .spec-value {
      flex: 1;
    }
  }
  
  .add-spec {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid var(--border-color);
    display: flex;
    align-items: center;
  }
}

.basic-price-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  
  h4 {
    margin: 0 0 20px 0;
    color: var(--text-primary);
    font-weight: 600;
  }
}

.multi-currency-section {
  margin-top: 24px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-secondary);
  
  .footer-left {
    .auto-save-status {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: var(--text-secondary);
      
      .loading {
        animation: spin 1s linear infinite;
      }
      
      .status-text {
        font-size: 12px;
      }
    }
  }
  
  .footer-right {
    display: flex;
    gap: 12px;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>