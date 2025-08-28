<template>
  <div class="products-page">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-container">
      <div class="container">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
          <el-breadcrumb-item>产品中心</el-breadcrumb-item>
          <el-breadcrumb-item v-if="!showProductList">{{ product.name }}</el-breadcrumb-item>
          <el-breadcrumb-item v-else>产品列表</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 产品列表视图 -->
    <div v-if="showProductList" class="products-list-section">
      <div class="container">
        <div class="section-header">
          <h1 class="section-title">练字机器人产品系列</h1>
          <p class="section-subtitle">选择适合您需求的智能练字解决方案</p>
        </div>
        
        <div class="products-grid">
          <div 
            v-for="productItem in products" 
            :key="productItem.id"
            class="product-card"
            @click="selectProduct(productItem)"
          >
            <div class="product-image">
              <img 
                :src="productItem.coverImageUrl || 'https://via.placeholder.com/300x200'" 
                :alt="productItem.name"
              />
              <div class="product-status" :class="productItem.status">
                {{ productItem.status === 'published' ? '现货' : '预售' }}
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ productItem.name }}</h3>
              <p class="product-model">型号：{{ productItem.model }}</p>
              <p class="product-description">{{ productItem.description }}</p>
              <div class="product-price">
                <span class="current-price">¥{{ Number(productItem.price).toLocaleString() }}</span>
              </div>
              <div class="product-actions">
                <el-button type="primary" size="small">查看详情</el-button>
                <el-button size="small">立即购买</el-button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="list-actions">
          <el-button @click="showProductList = false" v-if="products.length > 0">
            查看详细介绍
          </el-button>
        </div>
      </div>
    </div>

    <!-- 产品详情主体 -->
    <div class="product-main">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="8" animated />
        </div>
        
        <div v-else class="product-layout">
          <!-- 左侧图片区域 -->
          <div class="product-gallery">
            <div class="main-image-container">
              <img :src="selectedImage" :alt="product.name" class="main-image" @click="zoomImage" />
              <div class="image-tools">
                <div class="zoom-btn" @click="zoomImage">
                  <el-icon><ZoomIn /></el-icon>
                </div>
              </div>
              <!-- 新品标签 -->
              <div class="promo-tags">
                <div class="tag new-product">{{ product.badge }}</div>
              </div>
            </div>
            
            <!-- 缩略图 -->
            <div class="thumbnail-list">
              <div 
                class="thumbnail-item" 
                v-for="(image, index) in productImages" 
                :key="index"
                :class="{ active: selectedImage === image }"
                @click="selectImage(image)"
              >
                <img :src="image" :alt="`产品图片${index + 1}`" />
              </div>
            </div>
          </div>

          <!-- 右侧产品信息 -->
          <div class="product-info">
            <!-- 产品标题 -->
            <div class="product-title">
              <h1>{{ product.name }}</h1>
              <div class="product-subtitle">{{ product.model }} | 专为教育机构设计</div>
            </div>

            <!-- 价格区域 -->
            <div class="price-section">
              <div class="price-row">
                <span class="price-label">教育机构专享价</span>
                <div class="price-content">
                  <span class="current-price">¥{{ Number(product.price).toLocaleString() }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ Number(product.originalPrice).toLocaleString() }}</span>
                  <div class="discount-tag" v-if="product.originalPrice">
                    立省¥{{ (Number(product.originalPrice) - Number(product.price)).toLocaleString() }}
                  </div>
                </div>
              </div>
              <div class="price-benefits">
                <div class="benefit-item">
                  <el-icon class="benefit-icon"><Discount /></el-icon>
                  <span>教育机构批量优惠</span>
                </div>
                <div class="benefit-item">
                  <el-icon class="benefit-icon"><Van /></el-icon>
                  <span>免费上门安装</span>
                </div>
                <div class="benefit-item">
                  <el-icon class="benefit-icon"><Lock /></el-icon>
                  <span>3年超长质保</span>
                </div>
                <div class="benefit-item">
                  <el-icon class="benefit-icon"><Tools /></el-icon>
                  <span>专业培训支持</span>
                </div>
              </div>
            </div>

            <!-- 产品亮点 -->
            <div class="product-highlights">
              <div class="highlight-title">产品亮点</div>
              <div class="highlight-list">
                <div class="highlight-item" v-for="(highlight, index) in product.highlights" :key="index">
                  <el-icon class="highlight-icon"><Check /></el-icon>
                  <span>{{ highlight }}</span>
                </div>
              </div>
            </div>

            <!-- 服务保障 -->
            <div class="service-guarantee">
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>30天无理由退换</span>
              </div>
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>全国联保服务</span>
              </div>
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>正品保证</span>
              </div>
              <div class="service-item">
                <el-icon><CircleCheck /></el-icon>
                <span>7×24小时技术支持</span>
              </div>
            </div>

            <!-- 购买按钮 -->
            <div class="purchase-actions">
              <el-button type="primary" size="large" class="buy-now-btn" @click="handleBuyNow">
                立即购买
              </el-button>
              <el-button size="large" class="rental-btn" @click="handleRental">
                <div class="rental-btn-content">
                  <span class="rental-text">租赁体验</span>
                  <span class="rental-price">¥{{ rentalPricing.monthlyRate }}/月起</span>
                </div>
              </el-button>
              <el-button size="large" class="consult-btn" @click="handleConsult">
                咨询详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 产品详细功能介绍 -->
    <section class="features-detail-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">核心功能详解</h2>
          <p class="section-subtitle">深入了解教育版练字机器人的强大功能</p>
        </div>
        
        <div class="features-grid">
          <div class="feature-detail-card" v-for="(feature, index) in productFeatures" :key="index">
            <div class="feature-icon">
              <el-icon><component :is="featureIcons[index]" /></el-icon>
            </div>
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 技术规格 -->
    <section class="specifications-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">技术规格参数</h2>
          <p class="section-subtitle">详细的产品技术参数和配置信息</p>
        </div>
        
        <div class="specs-table">
          <div class="spec-row" v-for="(value, key) in product.specifications" :key="key">
            <div class="spec-label">{{ getSpecLabel(key) }}</div>
            <div class="spec-value">{{ value }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 产品细节照片展示 -->
    <section class="product-details-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">产品细节展示</h2>
          <p class="section-subtitle">每一个细节都体现专业品质</p>
        </div>
        
        <div class="details-gallery-large">
          <div class="detail-large-item" v-for="(photo, index) in productDetailPhotos" :key="index">
            <div class="detail-image-container">
              <img :src="photo" :alt="`产品细节 ${index + 1}`" @click="zoomImage(photo)" />
            </div>
            <div class="detail-content">
              <h3 class="detail-title">{{ getDetailPhotoTitle(index) }}</h3>
              <p class="detail-description">{{ getDetailPhotoDescription(index) }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 使用场景照片展示 -->
    <section class="usage-scenes-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">真实使用场景</h2>
          <p class="section-subtitle">看看用户是如何使用我们的产品的</p>
        </div>
        
        <div class="usage-gallery">
          <div class="usage-photo-card" v-for="(photo, index) in usageScenePhotos" :key="index">
            <div class="usage-image">
              <img :src="photo" :alt="`使用场景 ${index + 1}`" />
              <div class="usage-badge">
                {{ getUsageSceneLabel(index) }}
              </div>
            </div>
            <div class="usage-content">
              <h4>{{ getUsageSceneTitle(index) }}</h4>
              <p>{{ getUsageSceneDescription(index) }}</p>
              <div class="usage-stats">
                <span class="stat-item">
                  <strong>{{ getUsageStats(index).users }}</strong> 用户
                </span>
                <span class="stat-item">
                  <strong>{{ getUsageStats(index).satisfaction }}</strong> 满意度
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 用户评价 -->
    <section class="testimonials-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">用户真实评价</h2>
          <p class="section-subtitle">来自教育机构的真实使用反馈</p>
        </div>
        
        <!-- 统计数据展示 -->
        <div class="testimonials-stats">
          <div class="stat-item">
            <div class="stat-number">98.5%</div>
            <div class="stat-label">用户满意度</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">500+</div>
            <div class="stat-label">合作学校</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">50000+</div>
            <div class="stat-label">受益学生</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">95%</div>
            <div class="stat-label">续费率</div>
          </div>
        </div>

        <div class="testimonials-grid">
          <div class="testimonial-card" v-for="(testimonial, index) in enhancedTestimonials" :key="testimonial.id">
            <!-- 机构标识 -->
            <div class="institution-badge">
              <div class="institution-type">{{ testimonial.institutionType }}</div>
              <div class="institution-location">{{ testimonial.location }}</div>
            </div>
            
            <!-- 口碑照片展示 -->
            <div class="testimonial-photo" v-if="testimonial.photo">
              <img :src="testimonial.photo" :alt="`${testimonial.institutionName}使用场景`" />
              <div class="photo-badge">{{ testimonial.photoBadge }}</div>
            </div>
            
            <div class="testimonial-content">
              <div class="rating-section">
                <div class="rating">
                  <el-icon v-for="i in testimonial.rating" :key="i" class="star filled">
                    <Star />
                  </el-icon>
                  <el-icon v-for="i in (5 - testimonial.rating)" :key="i + testimonial.rating" class="star">
                    <Star />
                  </el-icon>
                </div>
                <div class="rating-text">{{ testimonial.rating }}.0 分</div>
              </div>
              
              <h4 class="testimonial-title">{{ testimonial.reviewTitle }}</h4>
              <p class="testimonial-text">"{{ testimonial.content }}"</p>
              
              <!-- 使用效果数据 -->
              <div class="usage-metrics" v-if="testimonial.metrics">
                <div class="metric-item" v-for="metric in testimonial.metrics" :key="metric.label">
                  <span class="metric-label">{{ metric.label }}</span>
                  <span class="metric-value">{{ metric.value }}</span>
                </div>
              </div>
            </div>
            
            <div class="testimonial-footer">
              <div class="testimonial-author">
                <img :src="testimonial.avatar" :alt="testimonial.name" class="author-avatar" />
                <div class="author-info">
                  <div class="author-name">
                    {{ testimonial.name }}
                    <span class="verified-badge" v-if="testimonial.verified">
                      <el-icon><CircleCheck /></el-icon>
                    </span>
                  </div>
                  <div class="author-title">{{ testimonial.title }}</div>
                  <div class="institution-name">{{ testimonial.institutionName }}</div>
                </div>
              </div>
              
              <div class="testimonial-date">
                使用时长：{{ testimonial.usageDuration }}
              </div>
            </div>
          </div>
        </div>

        <!-- 更多评价按钮 -->
        <div class="more-testimonials">
          <el-button size="large" @click="showMoreTestimonials">
            查看更多评价 ({{ totalTestimonials }}+)
          </el-button>
        </div>
      </div>
    </section>

    <!-- 购买咨询 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2 class="cta-title">开始您的智能教学之旅</h2>
          <p class="cta-subtitle">立即体验教育版练字机器人，让科技助力书法教学</p>
          <div class="cta-actions">
            <el-button type="primary" size="large" class="cta-primary-btn" @click="handleBuyNow">
              立即购买
            </el-button>
            <el-button size="large" class="cta-green-btn" @click="handleRental">
              <div class="rental-btn-content">
                <span class="rental-text">租赁体验</span>
                <span class="rental-price">¥{{ rentalPricing.monthlyRate }}/月起</span>
              </div>
            </el-button>
            <el-button text size="large" class="cta-text-btn" @click="handleConsult">
              联系咨询
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 全屏图片预览 -->
    <div v-if="showImagePreview" class="image-preview-overlay" @click="closeImagePreview">
      <div class="image-preview-container">
        <img :src="selectedImage" :alt="product.name" class="preview-image" @click.stop />
        <div class="preview-controls">
          <div class="close-btn" @click="closeImagePreview">
            <el-icon><Close /></el-icon>
          </div>
          <div class="image-nav">
            <div class="nav-btn prev-btn" @click.stop="prevImage" v-if="productImages.length > 1">
              <el-icon><ArrowLeft /></el-icon>
            </div>
            <div class="nav-btn next-btn" @click.stop="nextImage" v-if="productImages.length > 1">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Check, 
  ZoomIn,
  Discount,
  Van,
  Lock,
  Tools,
  CircleCheck,
  Star,
  Cpu,
  DataAnalysis,
  School,
  Lightning,
  Monitor,
  Close,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import { mockApiData, productDetailImages } from '@/utils/mockData'
import { PlatformService, type PlatformLink, type PlatformRecommendation } from '@/services/platformService'
import { websiteApi } from '@/api/website'

// 产品数据 - 从API获取
const product = ref({
  id: 0,
  name: '加载中...',
  model: '',
  description: '',
  price: 0,
  originalPrice: 0,
  coverImageUrl: '',
  status: '',
  highlights: [],
  specifications: {},
  badge: '新品上市'
})

// 产品列表数据
const products = ref([])
const loading = ref(true)
const showProductList = ref(true) // 默认显示产品列表

// 选择产品
const selectProduct = (selectedProduct) => {
  product.value = {
    ...selectedProduct,
    originalPrice: selectedProduct.price * 1.2, // 模拟原价
    highlights: [
      'AI智能识别技术，精准纠错',
      '多种字体支持，满足不同需求',
      '实时学习进度跟踪',
      '专业教学模式设计',
      '护眼显示屏，保护视力'
    ],
    specifications: {
      dimensions: '45cm × 35cm × 25cm',
      weight: '3.2kg',
      display: '10.1英寸电子墨水屏',
      connectivity: 'Wi-Fi、蓝牙、USB',
      teaching: '智能引导、实时纠错',
      recognition: 'AI深度学习算法',
      courses: '内置1000+练字课程',
      management: '云端数据同步'
    },
    badge: '热销产品'
  }
  showProductList.value = false
}

// 租赁价格数据
const rentalPricing = ref({
  dailyRate: 15,
  monthlyRate: 299,
  currency: 'CNY'
})

// 产品图片集合
const productImages = ref(productDetailImages.main)
const productDetailPhotos = ref(productDetailImages.details)
const usageScenePhotos = ref(productDetailImages.usage)
const testimonialPhotos = ref(productDetailImages.testimonial)

// 选中的图片
const selectedImage = ref(productImages.value[0])

// 图片预览状态
const showImagePreview = ref(false)

// 功能图标
const featureIcons = ref([Cpu, DataAnalysis, School, Lightning, Monitor])

// 用户评价
const testimonials = ref(mockApiData.testimonials)

// 增强的用户评价数据
const enhancedTestimonials = ref([
  {
    id: 1,
    name: '张明华',
    title: '书法教研组组长',
    institutionName: '北京市第一实验小学',
    institutionType: '公立小学',
    location: '北京市海淀区',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
    rating: 5,
    reviewTitle: '显著提升了学生的书写兴趣和水平',
    content: '使用练字机器人一学期以来，我们班级的书写水平有了质的飞跃。孩子们从最初的抗拒到现在主动要求练字，这种转变让我们非常惊喜。机器人的智能纠错功能特别实用，能够及时发现并纠正学生的书写问题，比传统教学效率提高了3倍以上。',
    photo: 'https://images.unsplash.com/photo-1497486751825-1233686d5d80?w=600&h=400&fit=crop',
    photoBadge: '课堂教学',
    verified: true,
    usageDuration: '8个月',
    metrics: [
      { label: '书写正确率提升', value: '85%' },
      { label: '学生参与度', value: '96%' },
      { label: '教学效率提升', value: '300%' }
    ]
  },
  {
    id: 2,
    name: '李文静',
    title: '语文教师',
    institutionName: '上海市浦东新区明珠小学',
    institutionType: '公立小学',
    location: '上海市浦东新区',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&h=100&fit=crop&crop=face',
    rating: 5,
    reviewTitle: '家长和学生都非常满意',
    content: '这台练字机器人真的改变了我们的书法教学。以前一个班40个学生，我很难做到一对一指导，现在机器人可以同时为多个学生提供个性化指导。家长们看到孩子的进步都很惊喜，纷纷询问是否可以购买家用版本。',
    photo: 'https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=600&h=400&fit=crop',
    photoBadge: '学生作品',
    verified: true,
    usageDuration: '1年2个月',
    metrics: [
      { label: '家长满意度', value: '98%' },
      { label: '作业完成质量', value: '提升90%' },
      { label: '课堂互动性', value: '显著提高' }
    ]
  },
  {
    id: 3,
    name: '王建国',
    title: '校长',
    institutionName: '深圳市南山区实验学校',
    institutionType: '九年一贯制学校',
    location: '深圳市南山区',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
    rating: 5,
    reviewTitle: '投资回报率超出预期',
    content: '作为学校管理者，我最关心的是教学效果和成本控制。这套练字机器人系统不仅提升了教学质量，还为学校节省了大量师资成本。我们计划在全校推广使用，并已向教育局推荐这一创新教学工具。',
    photo: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&h=400&fit=crop',
    photoBadge: '校园环境',
    verified: true,
    usageDuration: '2年',
    metrics: [
      { label: '教学成本节省', value: '40%' },
      { label: '教学覆盖率', value: '100%' },
      { label: '学生成绩提升', value: '平均15分' }
    ]
  },
  {
    id: 4,
    name: '陈雅琴',
    title: '书法专业教师',
    institutionName: '杭州市西湖区青少年宫',
    institutionType: '培训机构',
    location: '杭州市西湖区',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
    rating: 4,
    reviewTitle: '专业书法教学的得力助手',
    content: '作为专业的书法老师，我对教学工具的要求很高。这台机器人的笔画识别精度让我印象深刻，能够准确识别细微的笔画差异。虽然不能完全替代人工指导，但作为辅助教学工具确实非常优秀，特别适合基础教学阶段。',
    photo: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=600&h=400&fit=crop',
    photoBadge: '专业指导',
    verified: true,
    usageDuration: '1年5个月',
    metrics: [
      { label: '基础教学效率', value: '提升200%' },
      { label: '学员续课率', value: '92%' },
      { label: '教学标准化', value: '显著改善' }
    ]
  },
  {
    id: 5,
    name: '刘德华',
    title: '信息技术主任',
    institutionName: '广州市天河区华阳小学',
    institutionType: '公立小学',
    location: '广州市天河区',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
    rating: 5,
    reviewTitle: '技术先进，操作简便',
    content: '从技术角度来看，这套系统的AI识别算法非常先进，响应速度快，准确率高。更重要的是操作界面设计得很人性化，老师们很快就能上手使用。系统稳定性也很好，使用一年多来几乎没有出现过故障。',
    photo: 'https://images.unsplash.com/photo-1497486751825-1233686d5d80?w=600&h=400&fit=crop',
    photoBadge: '技术应用',
    verified: true,
    usageDuration: '1年3个月',
    metrics: [
      { label: '系统稳定性', value: '99.8%' },
      { label: '识别准确率', value: '96%' },
      { label: '教师满意度', value: '95%' }
    ]
  },
  {
    id: 6,
    name: '赵美丽',
    title: '特殊教育教师',
    institutionName: '成都市特殊教育学校',
    institutionType: '特殊教育学校',
    location: '成都市锦江区',
    avatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&h=100&fit=crop&crop=face',
    rating: 5,
    reviewTitle: '为特殊儿童带来了希望',
    content: '我们学校的孩子在书写方面有特殊困难，传统教学方法效果有限。练字机器人的耐心和重复性正好适合这些孩子的学习特点。它不会因为孩子的进步缓慢而失去耐心，反而能够根据每个孩子的情况调整教学节奏。看到孩子们的进步，我们都很感动。',
    photo: 'https://images.unsplash.com/photo-1509062522246-3755977927d7?w=600&h=400&fit=crop',
    photoBadge: '特殊教育',
    verified: true,
    usageDuration: '10个月',
    metrics: [
      { label: '学习兴趣提升', value: '显著' },
      { label: '书写能力改善', value: '80%学生' },
      { label: '自信心建立', value: '明显提升' }
    ]
  }
])

// 总评价数量
const totalTestimonials = ref(1200)

// 平台推荐数据
const purchaseRecommendation = ref<PlatformRecommendation | null>(null)
const rentalRecommendation = ref<PlatformRecommendation | null>(null)
const isLoadingPlatforms = ref(false)

// 获取产品数据
const fetchProducts = async () => {
  try {
    loading.value = true
    console.log('开始获取产品数据...')
    
    const response = await websiteApi.getProducts({ 
      page: 1, 
      size: 20,
      status: 'published' // 只获取已发布的产品
    })
    
    console.log('API响应:', response)
    
    if (response.code === 200 && response.data.list.length > 0) {
      products.value = response.data.list
      console.log('产品数据获取成功:', products.value)
    } else {
      console.warn('暂无产品数据')
      ElMessage.warning('暂无产品数据')
    }
  } catch (error) {
    console.error('获取产品数据失败:', error)
    ElMessage.error('获取产品数据失败，请稍后重试')
    
    // 使用fallback数据
    product.value = mockApiData.products[0]
  } finally {
    loading.value = false
  }
}



// 组件挂载时获取数据
onMounted(async () => {
  console.log('组件挂载，开始获取数据...')
  
  try {
    isLoadingPlatforms.value = true
    
    // 先获取产品数据
    await fetchProducts()
    
    // 并行获取平台推荐
    const [purchaseRec, rentalRec] = await Promise.all([
      PlatformService.getPurchasePlatformRecommendation(),
      PlatformService.getRentalPlatformRecommendation()
    ])
    
    purchaseRecommendation.value = purchaseRec
    rentalRecommendation.value = rentalRec
    
    console.log('平台推荐获取成功:', {
      purchase: purchaseRec,
      rental: rentalRec
    })
  } catch (error) {
    console.error('获取平台推荐失败:', error)
    ElMessage.error('获取平台信息失败，请稍后重试')
  } finally {
    isLoadingPlatforms.value = false
  }
})

// 选择图片
const selectImage = (image: string) => {
  selectedImage.value = image
}

// 放大图片
const zoomImage = (imageUrl?: string) => {
  if (imageUrl) {
    selectedImage.value = imageUrl
  }
  showImagePreview.value = true
  document.body.style.overflow = 'hidden' // 禁止背景滚动
}

// 关闭图片预览
const closeImagePreview = () => {
  showImagePreview.value = false
  document.body.style.overflow = '' // 恢复滚动
}

// 上一张图片
const prevImage = () => {
  const currentIndex = productImages.value.indexOf(selectedImage.value)
  const prevIndex = currentIndex > 0 ? currentIndex - 1 : productImages.value.length - 1
  selectedImage.value = productImages.value[prevIndex]
}

// 下一张图片
const nextImage = () => {
  const currentIndex = productImages.value.indexOf(selectedImage.value)
  const nextIndex = currentIndex < productImages.value.length - 1 ? currentIndex + 1 : 0
  selectedImage.value = productImages.value[nextIndex]
}

// 立即购买 - 智能平台推荐
const handleBuyNow = async () => {
  if (!purchaseRecommendation.value) {
    ElMessage.warning('正在获取购买平台信息，请稍候...')
    return
  }

  const { recommended, alternatives, userPreference } = purchaseRecommendation.value
  
  if (recommended.length === 0) {
    ElMessage.error('暂无可用的购买平台')
    return
  }

  // 如果只有一个推荐平台，直接跳转
  if (recommended.length === 1) {
    await redirectToPlatform(recommended[0], 'purchase')
    return
  }

  // 多个推荐平台时，显示选择对话框
  const platformOptions = recommended.map(platform => ({
    label: `${platform.platformName} - ${PlatformService.formatPrice(platform.price || 0, platform.currency)}`,
    value: platform.id,
    platform
  }))

  try {
    const { value: selectedPlatformId } = await ElMessageBox.prompt(
      `根据您的地理位置(${userPreference.region})和语言偏好(${userPreference.language})，我们为您推荐以下购买平台：`,
      '选择购买平台',
      {
        confirmButtonText: '前往购买',
        cancelButtonText: '取消',
        inputType: 'select',
        inputOptions: platformOptions,
        inputValue: platformOptions[0].value,
        customClass: 'platform-selection-dialog'
      }
    )

    const selectedPlatform = recommended.find(p => p.id === Number(selectedPlatformId))
    if (selectedPlatform) {
      await redirectToPlatform(selectedPlatform, 'purchase')
    }
  } catch (error) {
    // 用户取消选择
    console.log('用户取消购买')
  }
}

// 租赁体验 - 智能平台推荐
const handleRental = async () => {
  if (!rentalRecommendation.value) {
    ElMessage.warning('正在获取租赁平台信息，请稍候...')
    return
  }

  const { recommended, alternatives, userPreference } = rentalRecommendation.value
  
  if (recommended.length === 0) {
    ElMessage.error('暂无可用的租赁平台')
    return
  }

  // 如果只有一个推荐平台，直接跳转
  if (recommended.length === 1) {
    await redirectToPlatform(recommended[0], 'rental')
    return
  }

  // 多个推荐平台时，显示选择对话框
  const platformOptions = recommended.map(platform => ({
    label: `${platform.platformName} - ${PlatformService.formatRentalPrice(platform.dailyRate || 0, platform.monthlyRate || 0, platform.currency)}`,
    value: platform.id,
    platform
  }))

  try {
    const { value: selectedPlatformId } = await ElMessageBox.prompt(
      `根据您的地理位置(${userPreference.region})和语言偏好(${userPreference.language})，我们为您推荐以下租赁平台：`,
      '选择租赁平台',
      {
        confirmButtonText: '前往租赁',
        cancelButtonText: '取消',
        inputType: 'select',
        inputOptions: platformOptions,
        inputValue: platformOptions[0].value,
        customClass: 'platform-selection-dialog'
      }
    )

    const selectedPlatform = recommended.find(p => p.id === Number(selectedPlatformId))
    if (selectedPlatform) {
      await redirectToPlatform(selectedPlatform, 'rental')
    }
  } catch (error) {
    // 用户取消选择
    console.log('用户取消租赁')
  }
}

// 跳转到平台
const redirectToPlatform = async (platform: PlatformLink, type: 'purchase' | 'rental') => {
  try {
    ElMessage.loading({
      message: '正在验证平台链接...',
      duration: 0
    })

    // 验证平台链接有效性
    const isValid = await PlatformService.validatePlatformLink(platform.platformUrl)
    
    ElMessage.closeAll()

    if (!isValid) {
      ElMessage.error(`${platform.platformName} 链接暂时不可用，请稍后重试`)
      return
    }

    // 显示跳转确认
    const actionText = type === 'purchase' ? '购买' : '租赁'
    const priceText = type === 'purchase' 
      ? PlatformService.formatPrice(platform.price || 0, platform.currency)
      : PlatformService.formatRentalPrice(platform.dailyRate || 0, platform.monthlyRate || 0, platform.currency)

    await ElMessageBox.confirm(
      `即将跳转到 ${platform.platformName} 进行${actionText}，价格：${priceText}`,
      `确认${actionText}`,
      {
        confirmButtonText: `前往${actionText}`,
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 跳转到平台
    await PlatformService.redirectToPlatform(platform)
    
    ElMessage.success(`已为您打开 ${platform.platformName} ${actionText}页面`)
  } catch (error) {
    ElMessage.closeAll()
    if (error !== 'cancel') {
      console.error('平台跳转失败:', error)
      ElMessage.error('跳转失败，请稍后重试')
    }
  }
}

// 咨询详情
const handleConsult = () => {
  ElMessageBox.alert(
    '您可以通过以下方式联系我们：\n\n' +
    '📞 客服热线：400-123-4567\n' +
    '📧 邮箱：service@yxrobot.com\n' +
    '💬 在线客服：点击右下角客服图标\n' +
    '🏢 地址：北京市朝阳区科技园区\n\n' +
    '工作时间：周一至周五 9:00-18:00',
    '联系我们',
    {
      confirmButtonText: '知道了',
      type: 'info'
    }
  )
}

// 产品功能数据
const productFeatures = ref([
  {
    title: 'AI智能识别',
    description: '采用先进的人工智能技术，精准识别笔画轨迹，实时纠正书写错误，提供个性化指导建议。'
  },
  {
    title: '数据分析系统',
    description: '全面记录学习数据，生成详细的学习报告，帮助教师了解学生进度，制定针对性教学计划。'
  },
  {
    title: '教育专用设计',
    description: '专为教育机构设计，支持多人同时使用，配备教师管理端，方便课堂教学和学生管理。'
  },
  {
    title: '智能教学模式',
    description: '内置多种教学模式，从基础笔画到复杂汉字，循序渐进，适应不同年龄段学生的学习需求。'
  },
  {
    title: '护眼显示技术',
    description: '采用电子墨水屏技术，无蓝光伤害，长时间使用不疲劳，保护学生视力健康。'
  }
])

// 获取产品细节照片标题
const getDetailPhotoTitle = (index: number): string => {
  const titles = [
    '电子墨水屏显示',
    '智能机械臂',
    '控制面板',
    '专用笔架',
    '稳固底座',
    '多接口设计'
  ]
  return titles[index] || '产品细节'
}

// 获取产品细节照片描述
const getDetailPhotoDescription = (index: number): string => {
  const descriptions = [
    '护眼电子墨水屏，清晰显示书写内容，无蓝光伤害',
    '精密机械臂，模拟真人手写动作，手把手教学',
    '简洁直观的控制面板，操作简单，功能齐全',
    '人性化笔架设计，方便取放，保持桌面整洁',
    '重心稳定的底座设计，确保设备使用安全',
    '丰富的接口配置，支持多种连接方式'
  ]
  return descriptions[index] || '产品细节展示'
}

// 获取使用场景标签
const getUsageSceneLabel = (index: number): string => {
  const labels = ['小学教学', '中学课堂', '教师指导', '课堂互动', '家庭学习', '培训机构']
  return labels[index] || '使用场景'
}

// 获取使用场景标题
const getUsageSceneTitle = (index: number): string => {
  const titles = [
    '小学生书法启蒙',
    '中学生规范练习',
    '教师专业指导',
    '课堂互动教学',
    '家庭辅助学习',
    '专业培训教学'
  ]
  return titles[index] || '使用场景'
}

// 获取使用场景描述
const getUsageSceneDescription = (index: number): string => {
  const descriptions = [
    '帮助小学生建立正确的书写习惯，从基础笔画开始，循序渐进地学习汉字书写。',
    '配合中学语文教学，提供规范的汉字书写训练，提升学生的书写水平和文化素养。',
    '为教师提供专业的教学工具，实时监控学生学习进度，提供个性化指导建议。',
    '支持多人同时使用，营造良好的课堂学习氛围，提高教学效率和学习兴趣。',
    '延伸课堂教学到家庭，让学生在家也能得到专业的书法指导和练习。',
    '为专业书法培训机构提供系统化的教学解决方案，提升培训质量和效果。'
  ]
  return descriptions[index] || '真实使用场景展示'
}

// 获取使用场景统计数据
const getUsageStats = (index: number) => {
  const stats = [
    { users: '5000+', satisfaction: '98%' },
    { users: '3200+', satisfaction: '96%' },
    { users: '1800+', satisfaction: '99%' },
    { users: '4500+', satisfaction: '97%' },
    { users: '2100+', satisfaction: '95%' },
    { users: '800+', satisfaction: '99%' }
  ]
  return stats[index] || { users: '1000+', satisfaction: '95%' }
}

// 获取规格标签
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

// 查看更多评价
const showMoreTestimonials = () => {
  ElMessageBox.alert(
    '我们收集了来自全国各地教育机构的真实反馈，包括：\n\n' +
    '• 500+ 合作学校的使用体验\n' +
    '• 1200+ 教师的专业评价\n' +
    '• 50000+ 学生的学习效果反馈\n' +
    '• 详细的使用数据和效果分析\n\n' +
    '如需查看完整评价报告，请联系我们的客服团队。',
    '更多用户评价',
    {
      confirmButtonText: '联系客服',
      type: 'info'
    }
  ).then(() => {
    handleConsult()
  })
}
</script>

<style lang="scss" scoped>
.products-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

// 加载状态
.loading-container {
  padding: 40px 0;
  max-width: 1200px;
  margin: 0 auto;
}

// 产品列表样式
.products-list-section {
  padding: 40px 0;
  background: white;
  
  .section-header {
    text-align: center;
    margin-bottom: 40px;
    
    .section-title {
      font-size: 2.5rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 16px;
    }
    
    .section-subtitle {
      font-size: 1.2rem;
      color: var(--text-secondary);
      max-width: 600px;
      margin: 0 auto;
    }
  }
  
  .products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 30px;
    margin-bottom: 40px;
  }
  
  .product-card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: all 0.3s ease;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    }
    
    .product-image {
      position: relative;
      height: 200px;
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
      
      .product-status {
        position: absolute;
        top: 12px;
        right: 12px;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 0.8rem;
        font-weight: 600;
        
        &.published {
          background: #10b981;
          color: white;
        }
        
        &.draft {
          background: #f59e0b;
          color: white;
        }
      }
    }
    
    .product-info {
      padding: 24px;
      
      .product-name {
        font-size: 1.4rem;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      .product-model {
        font-size: 0.9rem;
        color: var(--text-secondary);
        margin-bottom: 12px;
      }
      
      .product-description {
        font-size: 1rem;
        color: var(--text-secondary);
        line-height: 1.5;
        margin-bottom: 16px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .product-price {
        margin-bottom: 20px;
        
        .current-price {
          font-size: 1.8rem;
          font-weight: 700;
          color: #e11d48;
        }
      }
      
      .product-actions {
        display: flex;
        gap: 12px;
        
        .el-button {
          flex: 1;
        }
      }
    }
  }
  
  .list-actions {
    text-align: center;
    
    .el-button {
      padding: 12px 32px;
      font-size: 1.1rem;
    }
  }
}

// 面包屑导航
.breadcrumb-container {
  background: white;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }
}

// 产品主体
.product-main {
  padding: 40px 0;
  background: white;
  
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

// 产品图片区域
.product-gallery {
  .main-image-container {
    position: relative;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    margin-bottom: 20px;
    
    .main-image {
      width: 100%;
      height: 400px;
      object-fit: cover;
      display: block;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.02);
      }
    }
    
    .image-tools {
      position: absolute;
      top: 16px;
      right: 16px;
      
      .zoom-btn {
        width: 40px;
        height: 40px;
        background: rgba(0, 0, 0, 0.6);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(0, 0, 0, 0.8);
          transform: scale(1.1);
        }
      }
    }
    
    .promo-tags {
      position: absolute;
      top: 16px;
      left: 16px;
      
      .tag {
        padding: 6px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
        color: white;
        
        &.new-product {
          background: var(--primary-gradient);
        }
      }
    }
  }
  
  .thumbnail-list {
    display: flex;
    gap: 12px;
    
    .thumbnail-item {
      width: 80px;
      height: 80px;
      border-radius: var(--radius-md);
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.3s ease;
      
      &.active {
        border-color: var(--primary-color);
      }
      
      &:hover {
        border-color: var(--primary-light);
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

// 产品信息区域
.product-info {
  .product-title {
    margin-bottom: 24px;
    
    h1 {
      font-size: 32px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 8px;
      line-height: 1.2;
    }
    
    .product-subtitle {
      font-size: 16px;
      color: var(--text-secondary);
      font-weight: 500;
    }
  }
  
  .price-section {
    margin-bottom: 32px;
    padding: 24px;
    background: var(--bg-secondary);
    border-radius: var(--radius-lg);
    
    .price-row {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      
      .price-label {
        font-size: 14px;
        color: var(--text-secondary);
        margin-right: 16px;
        min-width: 120px;
      }
      
      .price-content {
        display: flex;
        align-items: center;
        gap: 12px;
        
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
        
        .discount-tag {
          background: #10B981;
          color: white;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 600;
        }
      }
    }
    
    .price-benefits {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      
      .benefit-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        color: var(--text-secondary);
        
        .benefit-icon {
          color: var(--primary-color);
          font-size: 16px;
        }
      }
    }
  }
  
  .product-highlights {
    margin-bottom: 32px;
    
    .highlight-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
    }
    
    .highlight-list {
      .highlight-item {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        font-size: 14px;
        color: var(--text-secondary);
        
        .highlight-icon {
          color: var(--primary-color);
          font-size: 16px;
        }
      }
    }
  }
  
  .service-guarantee {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 32px;
    
    .service-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: var(--text-secondary);
      
      .el-icon {
        color: var(--primary-color);
        font-size: 16px;
      }
    }
  }
  
  .purchase-actions {
    .el-button {
      margin-right: 12px;
      margin-bottom: 12px;
    }
    
    .buy-now-btn {
      background: var(--primary-gradient);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-lg);
      }
    }
    
    .rental-btn {
      border: 2px solid #10B981 !important;
      color: #10B981 !important;
      background: transparent !important;
      
      &:hover {
        background: #10B981 !important;
        color: white !important;
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
      }
      
      .rental-btn-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;
        
        .rental-text {
          font-size: 16px;
          font-weight: 600;
        }
        
        .rental-price {
          font-size: 12px;
          opacity: 0.8;
          font-weight: 500;
        }
      }
    }
    
    .consult-btn {
      border: 2px solid var(--primary-color);
      color: var(--primary-color);
      background: transparent;
      
      &:hover {
        background: var(--primary-color);
        color: white;
        transform: translateY(-2px);
        box-shadow: var(--shadow-md);
      }
    }
  }
}

// 通用区域样式
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
  
  .section-title {
    font-size: 36px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 16px;
    line-height: 1.2;
  }
  
  .section-subtitle {
    font-size: 18px;
    color: var(--text-secondary);
    max-width: 600px;
    margin: 0 auto;
    line-height: 1.6;
  }
}

// 功能详解区域
.features-detail-section {
  padding: 100px 0;
  background: white;
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 40px;
  }
  
  .feature-detail-card {
    display: flex;
    gap: 20px;
    padding: 32px;
    background: var(--bg-secondary);
    border-radius: var(--radius-xl);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .feature-icon {
      width: 60px;
      height: 60px;
      background: var(--primary-gradient);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 24px;
      flex-shrink: 0;
    }
    
    .feature-content {
      .feature-title {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      .feature-description {
        color: var(--text-secondary);
        line-height: 1.6;
      }
    }
  }
}

// 技术规格区域
.specifications-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .specs-table {
    background: white;
    border-radius: var(--radius-xl);
    padding: 40px;
    box-shadow: var(--shadow-sm);
    
    .spec-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 0;
      border-bottom: 1px solid var(--border-color);
      
      &:last-child {
        border-bottom: none;
      }
      
      .spec-label {
        font-weight: 600;
        color: var(--text-primary);
        font-size: 16px;
      }
      
      .spec-value {
        color: var(--text-secondary);
        font-size: 16px;
        text-align: right;
      }
    }
  }
}

// 产品细节照片展示区域
.product-details-section {
  padding: 100px 0;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #f8fafc 100%);
  
  .details-gallery-large {
    display: flex;
    flex-direction: column;
    gap: 80px;
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .detail-large-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    position: relative;
    
    &:nth-child(even) {
      .detail-image-container {
        order: 2;
      }
      .detail-content {
        order: 1;
        margin-bottom: 40px;
        margin-top: 0;
      }
    }
    
    // 添加分隔线
    &:not(:last-child)::after {
      content: '';
      position: absolute;
      bottom: -40px;
      left: 50%;
      transform: translateX(-50%);
      width: 100px;
      height: 2px;
      background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
    }
    
    .detail-image-container {
      width: 100%;
      max-width: 800px;
      border-radius: var(--radius-xl);
      overflow: hidden;
      box-shadow: var(--shadow-lg);
      transition: all 0.3s ease;
      cursor: pointer;
      position: relative;
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: var(--shadow-xl);
        
        &::before {
          opacity: 1;
        }
      }
      
      // 放大镜提示
      &::before {
        content: '🔍';
        position: absolute;
        top: 20px;
        right: 20px;
        width: 50px;
        height: 50px;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        opacity: 0;
        transition: opacity 0.3s ease;
        z-index: 1;
      }
      
      img {
        width: 100%;
        height: auto;
        min-height: 400px;
        object-fit: cover;
        display: block;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
        }
      }
    }
    
    .detail-content {
      margin-top: 40px;
      max-width: 600px;
      
      .detail-title {
        font-size: 28px;
        font-weight: 700;
        color: var(--text-primary);
        margin-bottom: 16px;
        line-height: 1.3;
      }
      
      .detail-description {
        font-size: 18px;
        color: var(--text-secondary);
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}

// 使用场景照片展示区域
.usage-scenes-section {
  padding: 100px 0;
  background: white;
  
  .usage-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 40px;
  }
  
  .usage-photo-card {
    display: flex;
    gap: 20px;
    background: var(--bg-secondary);
    border-radius: var(--radius-xl);
    overflow: hidden;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .usage-image {
      position: relative;
      width: 180px;
      height: 140px;
      flex-shrink: 0;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .usage-badge {
        position: absolute;
        top: 8px;
        left: 8px;
        background: var(--primary-gradient);
        color: white;
        padding: 4px 8px;
        border-radius: var(--radius-sm);
        font-size: 12px;
        font-weight: 600;
      }
      
      &:hover img {
        transform: scale(1.05);
      }
    }
    
    .usage-content {
      padding: 20px;
      flex: 1;
      
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      p {
        color: var(--text-secondary);
        line-height: 1.5;
        margin-bottom: 12px;
        font-size: 14px;
      }
      
      .usage-stats {
        display: flex;
        gap: 16px;
        
        .stat-item {
          font-size: 12px;
          color: var(--text-light);
          
          strong {
            color: var(--primary-color);
            font-weight: 600;
          }
        }
      }
    }
  }
}

// 用户评价区域
.testimonials-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .testimonials-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 30px;
    margin-bottom: 80px;
    
    .stat-item {
      text-align: center;
      padding: 30px 20px;
      background: white;
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-sm);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: var(--shadow-lg);
      }
      
      .stat-number {
        font-size: 36px;
        font-weight: 700;
        color: var(--primary-color);
        margin-bottom: 8px;
        line-height: 1;
      }
      
      .stat-label {
        font-size: 16px;
        color: var(--text-secondary);
        font-weight: 500;
      }
    }
  }
  
  .testimonials-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 40px;
    margin-bottom: 60px;
  }
  
  .testimonial-card {
    background: white;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-sm);
    overflow: hidden;
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .institution-badge {
      position: absolute;
      top: 16px;
      left: 16px;
      z-index: 2;
      
      .institution-type {
        background: var(--primary-gradient);
        color: white;
        padding: 4px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .institution-location {
        background: rgba(0, 0, 0, 0.7);
        color: white;
        padding: 2px 8px;
        border-radius: var(--radius-sm);
        font-size: 11px;
      }
    }
    
    .testimonial-photo {
      position: relative;
      height: 200px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .photo-badge {
        position: absolute;
        bottom: 12px;
        right: 12px;
        background: rgba(255, 255, 255, 0.9);
        color: var(--text-primary);
        padding: 4px 8px;
        border-radius: var(--radius-sm);
        font-size: 12px;
        font-weight: 600;
        backdrop-filter: blur(10px);
      }
      
      &:hover img {
        transform: scale(1.05);
      }
    }
    
    .testimonial-content {
      padding: 24px;
      
      .rating-section {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 16px;
        
        .rating {
          display: flex;
          gap: 2px;
          
          .star {
            font-size: 16px;
            
            &.filled {
              color: #FFD700;
            }
            
            &:not(.filled) {
              color: #E5E7EB;
            }
          }
        }
        
        .rating-text {
          font-size: 14px;
          color: var(--text-secondary);
          font-weight: 600;
        }
      }
      
      .testimonial-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
        line-height: 1.3;
      }
      
      .testimonial-text {
        color: var(--text-secondary);
        line-height: 1.6;
        font-style: italic;
        margin-bottom: 20px;
        font-size: 15px;
      }
      
      .usage-metrics {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
        gap: 12px;
        margin-bottom: 20px;
        
        .metric-item {
          background: var(--bg-secondary);
          padding: 8px 12px;
          border-radius: var(--radius-md);
          text-align: center;
          
          .metric-label {
            display: block;
            font-size: 12px;
            color: var(--text-light);
            margin-bottom: 4px;
          }
          
          .metric-value {
            font-size: 14px;
            font-weight: 600;
            color: var(--primary-color);
          }
        }
      }
    }
    
    .testimonial-footer {
      padding: 0 24px 24px;
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      
      .testimonial-author {
        display: flex;
        align-items: center;
        gap: 12px;
        flex: 1;
        
        .author-avatar {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          object-fit: cover;
          border: 2px solid var(--border-color);
        }
        
        .author-info {
          .author-name {
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 2px;
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 15px;
            
            .verified-badge {
              color: #10B981;
              font-size: 16px;
            }
          }
          
          .author-title {
            font-size: 13px;
            color: var(--text-secondary);
            margin-bottom: 2px;
          }
          
          .institution-name {
            font-size: 12px;
            color: var(--text-light);
            font-weight: 500;
          }
        }
      }
      
      .testimonial-date {
        font-size: 12px;
        color: var(--text-light);
        text-align: right;
        white-space: nowrap;
      }
    }
  }
  
  .more-testimonials {
    text-align: center;
    
    .el-button {
      padding: 16px 32px;
      font-size: 16px;
      border-radius: var(--radius-lg);
      border: 2px solid var(--primary-color);
      color: var(--primary-color);
      background: transparent;
      transition: all 0.3s ease;
      
      &:hover {
        background: var(--primary-color);
        color: white;
        transform: translateY(-2px);
        box-shadow: var(--shadow-md);
      }
    }
  }
}

// CTA区域
.cta-section {
  padding: 100px 0;
  background: var(--primary-gradient);
  color: white;
  text-align: center;
  
  .cta-content {
    max-width: 800px;
    margin: 0 auto;
    
    .cta-title {
      font-size: 42px;
      font-weight: 700;
      margin-bottom: 16px;
      line-height: 1.2;
    }
    
    .cta-subtitle {
      font-size: 20px;
      margin-bottom: 40px;
      opacity: 0.9;
      line-height: 1.6;
    }
    
    .cta-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
      flex-wrap: wrap;
      
      .el-button {
        padding: 16px 32px;
        font-size: 16px;
        font-weight: 600;
        border-radius: var(--radius-lg);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
        }
      }
      
      .cta-primary-btn {
        background: white;
        color: var(--primary-color);
        border: none;
        
        &:hover {
          background: var(--bg-secondary);
        }
      }
      
      .cta-green-btn {
        border: 2px solid #10B981;
        color: #10B981;
        background: transparent;
        
        &:hover {
          background: #10B981;
          color: white;
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
        }
        
        .rental-btn-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 2px;
          
          .rental-text {
            font-size: 16px;
            font-weight: 600;
          }
          
          .rental-price {
            font-size: 12px;
            opacity: 0.8;
            font-weight: 500;
          }
        }
      }
      
      .cta-text-btn {
        color: white;
        border: 2px solid white;
        background: transparent;
        
        &:hover {
          background: white;
          color: var(--primary-color);
        }
      }
    }
  }
}

// 全屏图片预览样式
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  
  .image-preview-container {
    position: relative;
    max-width: 90vw;
    max-height: 90vh;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .preview-image {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      border-radius: var(--radius-lg);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
    }
    
    .preview-controls {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      
      .close-btn {
        position: absolute;
        top: 20px;
        right: 20px;
        width: 50px;
        height: 50px;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        cursor: pointer;
        pointer-events: auto;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(0, 0, 0, 0.9);
          transform: scale(1.1);
        }
        
        .el-icon {
          font-size: 24px;
        }
      }
      
      .image-nav {
        position: absolute;
        top: 50%;
        left: 0;
        right: 0;
        transform: translateY(-50%);
        display: flex;
        justify-content: space-between;
        padding: 0 20px;
        pointer-events: none;
        
        .nav-btn {
          width: 60px;
          height: 60px;
          background: rgba(0, 0, 0, 0.7);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          cursor: pointer;
          pointer-events: auto;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(0, 0, 0, 0.9);
            transform: scale(1.1);
          }
          
          .el-icon {
            font-size: 28px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-layout {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
  }
  
  .product-info {
    .product-title h1 {
      font-size: 24px !important;
    }
    
    .price-section .price-content .current-price {
      font-size: 28px !important;
    }
    
    .purchase-actions {
      .el-button {
        width: 100%;
        margin-right: 0 !important;
      }
    }
  }
  
  .usage-photo-card {
    flex-direction: column !important;
    
    .usage-image {
      width: 100% !important;
      height: 200px !important;
    }
  }
  
  .cta-content {
    .cta-title {
      font-size: 32px !important;
    }
    
    .cta-subtitle {
      font-size: 18px !important;
    }
    
    .cta-actions {
      flex-direction: column !important;
      align-items: center;
      
      .el-button {
        width: 100%;
        max-width: 300px;
      }
    }
  }
  
  // 产品细节展示响应式
  .product-details-section {
    .details-gallery-large {
      gap: 60px;
      
      .detail-large-item {
        .detail-image-container {
          img {
            min-height: 250px;
          }
        }
        
        .detail-content {
          margin-top: 30px;
          
          .detail-title {
            font-size: 22px !important;
          }
          
          .detail-description {
            font-size: 16px !important;
          }
        }
        
        &:nth-child(even) {
          .detail-content {
            margin-bottom: 30px;
          }
        }
      }
    }
  }
  
  // 用户评价响应式
  .testimonials-section {
    .testimonials-stats {
      grid-template-columns: repeat(2, 1fr) !important;
      gap: 20px !important;
      margin-bottom: 60px !important;
      
      .stat-item {
        padding: 20px 15px !important;
        
        .stat-number {
          font-size: 28px !important;
        }
        
        .stat-label {
          font-size: 14px !important;
        }
      }
    }
    
    .testimonials-grid {
      grid-template-columns: 1fr !important;
      gap: 30px !important;
    }
    
    .testimonial-card {
      .testimonial-content {
        padding: 20px !important;
        
        .testimonial-title {
          font-size: 16px !important;
        }
        
        .testimonial-text {
          font-size: 14px !important;
        }
        
        .usage-metrics {
          grid-template-columns: repeat(2, 1fr) !important;
          gap: 8px !important;
          
          .metric-item {
            padding: 6px 8px !important;
            
            .metric-label {
              font-size: 11px !important;
            }
            
            .metric-value {
              font-size: 12px !important;
            }
          }
        }
      }
      
      .testimonial-footer {
        padding: 0 20px 20px !important;
        flex-direction: column !important;
        align-items: flex-start !important;
        gap: 12px !important;
        
        .testimonial-author {
          .author-avatar {
            width: 40px !important;
            height: 40px !important;
          }
          
          .author-info {
            .author-name {
              font-size: 14px !important;
            }
            
            .author-title {
              font-size: 12px !important;
            }
            
            .institution-name {
              font-size: 11px !important;
            }
          }
        }
        
        .testimonial-date {
          font-size: 11px !important;
        }
      }
    }
  }
  
  .image-preview-overlay {
    .image-preview-container {
      max-width: 95vw;
      max-height: 95vh;
      
      .preview-controls {
        .close-btn {
          top: 10px;
          right: 10px;
          width: 40px;
          height: 40px;
          
          .el-icon {
            font-size: 20px;
          }
        }
        
        .image-nav {
          padding: 0 10px;
          
          .nav-btn {
            width: 50px;
            height: 50px;
            
            .el-icon {
              font-size: 24px;
            }
          }
        }
      }
    }
  }
}
</style>