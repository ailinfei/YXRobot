<template>
  <div class="home-page">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">
              艺学，
              让每个人写好汉字。
            </h1>
            <p class="hero-subtitle">
           专业的AI练字机器人，结合传统书法艺术与现代科技，
              为您提供个性化的练字体验。每售出一台机器人，
              我们就向公益机构捐赠一节书法课。
            </p>
            <div class="hero-actions">
              <el-button type="primary" size="large" class="cta-primary" @click="scrollToFeatures">
                <el-icon><VideoPlay /></el-icon>
                观看演示
              </el-button>
       
            </div>
            <div class="hero-stats">
              <div class="stat-item">
                <div class="stat-number">50,000+</div>
                <div class="stat-label">用户选择</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">1,200+</div>
                <div class="stat-label">公益教学点</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">98%</div>
                <div class="stat-label">满意度</div>
              </div>
            </div>
          </div>
          <div class="hero-image">
            <div class="product-showcase">
              <img :src="productImages.hero" alt="练字机器人产品展示" class="product-img" />
              <div class="floating-card card-1">
                <el-icon><Edit /></el-icon>
                <span>AI智能识别</span>
              </div>
              <div class="floating-card card-2">
                <el-icon><Star /></el-icon>
                <span>个性化指导</span>
              </div>
              <div class="floating-card card-3">
                <el-icon><Trophy /></el-icon>
                <span>进步可见</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 产品特色 -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">为什么选择我们的练字机器人</h2>
          <p class="section-subtitle">融合传统书法艺术与现代AI技术，为您带来前所未有的练字体验</p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card" v-for="feature in features" :key="feature.id">
            <div class="feature-icon" :class="feature.iconClass">
              <el-icon><component :is="feature.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>



    <!-- 公益理念 -->
    <section class="charity-section">
      <div class="section-container">
        <div class="charity-content">
          <div class="charity-text">
            <div class="charity-badge">公益理念</div>
            <h2 class="charity-title">每售出一台机器人<br>捐赠一节书法课</h2>
            <p class="charity-desc">
              我们相信书法艺术应该惠及每一个人。通过"一台一课"公益计划，
              我们已经为全国1,200多个教学点提供了免费的书法课程，
              让更多孩子和老人能够感受到书法的魅力。
            </p>
            <div class="charity-stats">
              <div class="charity-stat">
                <div class="stat-number">{{ charityStats.donatedCourses.toLocaleString() }}</div>
                <div class="stat-label">已捐赠课程</div>
              </div>
              <div class="charity-stat">
                <div class="stat-number">{{ charityStats.beneficiaries.toLocaleString() }}</div>
                <div class="stat-label">受益人数</div>
              </div>
              <div class="charity-stat">
                <div class="stat-number">{{ charityStats.institutions }}</div>
                <div class="stat-label">合作机构</div>
              </div>
            </div>
            <el-button type="primary" size="large" @click="$router.push('/website/charity')">
              了解公益项目
            </el-button>
          </div>
          <div class="charity-image">
            <img :src="productImages.charity" alt="公益教学现场" />
          </div>
        </div>
      </div>
    </section>

    <!-- 技术优势 -->
    <section class="technology-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">核心技术优势</h2>
          <p class="section-subtitle">领先的AI技术与传统书法艺术的完美结合</p>
        </div>
        
        <!-- 技术展示图片 - 放在上面 -->
        <div class="tech-image-top">
          <img :src="productImages.technology" alt="AI技术展示" />
          <div class="tech-overlay-top">
            <div class="tech-stats-top">
              <div class="tech-stat-top">
                <div class="stat-number">99.2%</div>
                <div class="stat-label">识别准确率</div>
              </div>
              <div class="tech-stat-top">
                <div class="stat-number">0.1s</div>
                <div class="stat-label">响应时间</div>
              </div>
              <div class="tech-stat-top">
                <div class="stat-number">50+</div>
                <div class="stat-label">字体样式</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 技术特性卡片 - 左右排列在下面 -->
        <div class="tech-features-grid">
          <div class="tech-feature-card" v-for="tech in technologyFeatures" :key="tech.id">
            <div class="tech-icon-card">
              <el-icon><component :is="tech.icon" /></el-icon>
            </div>
            <div class="tech-content-card">
              <h4>{{ tech.title }}</h4>
              <p>{{ tech.description }}</p>
              <div class="tech-metrics-card">
                <span v-for="metric in tech.metrics" :key="metric" class="metric-tag-card">
                  {{ metric }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 应用场景 -->
    <section class="scenarios-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">广泛的应用场景</h2>
          <p class="section-subtitle">从学校教育到家庭学习，满足不同需求</p>
        </div>
        
        <div class="scenarios-grid-compact">
          <div class="scenario-card-compact" v-for="scenario in applicationScenarios" :key="scenario.id">
            <div class="scenario-image-compact">
              <img :src="scenario.image" :alt="scenario.title" />
              <div class="scenario-overlay-compact">
                <div class="scenario-badge-compact">{{ scenario.category }}</div>
                <div class="scenario-stats-compact">
                  <div class="stat-compact">
                    <span class="stat-number">{{ scenario.users }}</span>
                  </div>
                  <div class="stat-compact">
                    <span class="stat-number">{{ scenario.satisfaction }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="scenario-info-compact">
              <h4>{{ scenario.title }}</h4>
              <p>{{ scenario.description }}</p>
              <div class="scenario-features-compact">
                <span v-for="feature in scenario.features.slice(0, 3)" :key="feature" class="feature-tag-compact">
                  {{ feature }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 新闻动态 -->
    <section class="news-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">最新动态</h2>
          <p class="section-subtitle">了解我们的最新产品发布和行业资讯</p>
        </div>
        
        <div class="news-grid">
          <div class="news-card featured" v-if="featuredNews">
            <div class="news-image">
              <img :src="featuredNews.image" :alt="featuredNews.title" />
              <div class="news-badge featured-badge">重要</div>
            </div>
            <div class="news-content">
              <div class="news-meta">
                <span class="news-category">{{ featuredNews.category }}</span>
                <span class="news-date">{{ featuredNews.date }}</span>
              </div>
              <h3 class="news-title">{{ featuredNews.title }}</h3>
              <p class="news-excerpt">{{ featuredNews.excerpt }}</p>
              <el-button type="primary" text @click="readMore(featuredNews)">
                阅读全文 →
              </el-button>
            </div>
          </div>
          
          <div class="news-list">
            <div class="news-item" v-for="news in recentNews" :key="news.id">
              <div class="news-thumb">
                <img :src="news.image" :alt="news.title" />
              </div>
              <div class="news-info">
                <div class="news-meta">
                  <span class="news-category">{{ news.category }}</span>
                  <span class="news-date">{{ news.date }}</span>
                </div>
                <h4 class="news-title">{{ news.title }}</h4>
                <p class="news-excerpt">{{ news.excerpt }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <div class="news-more">
          <el-button size="large" @click="$router.push('/website/news')">
            查看更多新闻
          </el-button>
        </div>
      </div>
    </section>

    <!-- 用户评价 -->
    <section class="testimonials-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">用户真实评价</h2>
          <p class="section-subtitle">听听用户们的使用感受</p>
        </div>
        
        <!-- 评价统计 -->
        <div class="testimonials-stats">
          <div class="stat-card">
            <div class="stat-number">4.9</div>
            <div class="stat-label">平均评分</div>
            <div class="rating-stars">
              <el-icon v-for="i in 5" :key="i" class="star">
                <Star />
              </el-icon>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-number">12,000+</div>
            <div class="stat-label">用户评价</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">98.5%</div>
            <div class="stat-label">推荐率</div>
          </div>
        </div>
        
        <div class="testimonials-grid">
          <div class="testimonial-card" v-for="testimonial in enhancedTestimonials" :key="testimonial.id">
            <div class="testimonial-header">
              <div class="rating">
                <el-icon v-for="i in testimonial.rating" :key="i" class="star filled">
                  <Star />
                </el-icon>
                <el-icon v-for="i in (5 - testimonial.rating)" :key="i + testimonial.rating" class="star">
                  <Star />
                </el-icon>
              </div>
              <div class="testimonial-date">{{ testimonial.date }}</div>
            </div>
            <div class="testimonial-content">
              <p class="testimonial-text">"{{ testimonial.content }}"</p>
              <div class="testimonial-tags" v-if="testimonial.tags">
                <span v-for="tag in testimonial.tags" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </div>
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
                <div class="author-location">{{ testimonial.location }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="testimonials-more">
          <el-button size="large" @click="$router.push('/website/products')">
            查看更多评价
          </el-button>
        </div>
      </div>
    </section>

     <!-- 合作伙伴 -->
    <section class="partners-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">合作伙伴</h2>
          <p class="section-subtitle">与知名教育机构和技术公司携手合作</p>
        </div>
        
        <div class="partners-grid-compact">
          <!-- 教育机构合作伙伴 -->
          <div class="partner-card-compact" v-for="partner in educationPartners" :key="'edu-' + partner.id">
            <div class="partner-logo-compact">
              <img :src="partner.logo" :alt="partner.name" />
            </div>
            <div class="partner-content-compact">
              <h4 class="partner-name-compact">{{ partner.name }}</h4>
              <p class="partner-desc-compact">{{ partner.description }}</p>
              <div class="partner-type-compact education">教育机构</div>
            </div>
          </div>
          
          <!-- 技术合作伙伴 -->
          <div class="partner-card-compact" v-for="partner in technologyPartners" :key="'tech-' + partner.id">
            <div class="partner-logo-compact">
              <img :src="partner.logo" :alt="partner.name" />
            </div>
            <div class="partner-content-compact">
              <h4 class="partner-name-compact">{{ partner.name }}</h4>
              <p class="partner-desc-compact">{{ partner.description }}</p>
              <div class="partner-type-compact technology">技术合作</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区域 -->
    <section class="cta-section">
      <div class="section-container">
        <div class="cta-content">
          <h2 class="cta-title">开始您的练字之旅</h2>
          <p class="cta-subtitle">立即体验AI练字机器人，让科技助力您写出一手好字</p>
          <div class="cta-actions">
            <el-button type="primary" size="large" class="cta-primary-btn" @click="handlePurchase()">
              立即购买
            </el-button>
            <el-button size="large" class="cta-green-btn" @click="handleRental()">
              租赁体验
            </el-button>
            <el-button text size="large" class="cta-text-btn" @click="$router.push('/website/contact')">
              联系咨询
            </el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  VideoPlay,
  Edit,
  Star,
  Trophy,
  Cpu,
  DataAnalysis,
  School,
  ChatRound,
  Lightning,
  Lock,
  CircleCheck,
  Monitor,
  Camera,
  Upload,
  Setting,
  Check,
  ShoppingCart,
  Clock
} from '@element-plus/icons-vue'
import { mockApiData, productImages } from '@/utils/mockData'
import { useImages } from '@/composables/useImages'

// 使用图片管理
const { getImageUrl, preloadImages } = useImages()

// 预加载重要图片
const preloadImportantImages = async () => {
  const importantImages = [
    productImages.hero,
    productImages.charity,
    productImages.technology
  ]
  await preloadImages(importantImages)
}

// 组件挂载时预加载图片
onMounted(() => {
  preloadImportantImages()
})

// 产品特色数据
const features = ref([
  {
    id: 1,
    icon: Cpu,
    iconClass: 'icon-red',
    title: 'AI智能指导',
    description: '先进的计算机视觉技术，实时引导书写笔画，提供精准的姿势和笔法指导'
  },
  {
    id: 2,
    icon: DataAnalysis,
    iconClass: 'icon-green',
    title: '全球首款',
    description: '十年书法一线教学沉淀，循序渐进式教学，理论与实践结合，帮助千万学员写好汉字'
  },
  {
    id: 3,
    icon: School,
    iconClass: 'icon-red',
    title: '名师字体库',
    description: '收录古今优秀书法家的字体样本，支持楷书、行书、隶书等多种字体风格学习'
  },
  {
    id: 4,
    icon: Lightning,
    iconClass: 'icon-red',
    title: '标准发音',
    description: '提供多语言支持，标准汉字发音，让您学习最正宗的汉字发音，学习更轻松'
  },
  {
    id: 5,
    icon: ChatRound,
    iconClass: 'icon-green',
    title: '公益理念',
    description: '每售出一台机器人，向公益机构捐赠一节书法课，让更多人受益于书法艺术'
  },
  {
    id: 6,
    icon: Lock,
    iconClass: 'icon-red',
    title: '品质保障',
    description: '严格的质量控制体系，提供完善的售后服务，让您的使用体验无忧'
  }
])

// 产品系列数据
const products = ref(mockApiData.products.map(product => ({
  ...product,
  image: product.coverImage
})))

// 公益统计数据
const charityStats = ref({
  donatedCourses: mockApiData.charityStats.totalDonatedCourses,
  beneficiaries: mockApiData.charityStats.totalBeneficiaries,
  institutions: mockApiData.charityStats.totalInstitutions
})

// 用户评价数据
const testimonials = ref(mockApiData.testimonials)

// 技术特性数据
const technologyFeatures = ref([
  {
    id: 1,
    icon: Camera,
    title: '计算机视觉技术',
    description: '采用先进的深度学习算法，实时识别笔画轨迹和书写姿势，准确率高达99.2%',
    metrics: ['99.2%准确率', '0.1s响应', '多角度识别']
  },
  {
    id: 2,
    icon: Cpu,
    title: '智能分析引擎',
    description: '内置专业的书法分析算法，能够精准评估笔画质量、字形结构和整体美观度',
    metrics: ['AI评分', '实时反馈', '个性化建议']
  },
  {
    id: 3,
    icon: Upload,
    title: '云端字库系统',
    description: '拥有海量名家字帖资源，支持楷书、行书、隶书等多种字体，持续更新扩充',
    metrics: ['50+字体', '10万+字样', '名家真迹']
  },
  {
    id: 4,
    icon: Setting,
    title: '自适应学习',
    description: '根据用户的学习进度和能力水平，智能调整教学内容和难度，提供个性化学习路径',
    metrics: ['智能推荐', '进度跟踪', '能力评估']
  }
])

// 应用场景数据
const applicationScenarios = ref([
  {
    id: 1,
    title: '学校教育',
    category: '教育机构',
    description: '为中小学书法课程提供专业的教学辅助，提升教学效率和学生学习兴趣',
    image: 'https://via.placeholder.com/400x300/e8f5e8/2e7d32?text=Teaching+Scene',
    features: ['课堂教学', '作业批改', '进度跟踪', '成绩分析'],
    users: '500+学校',
    satisfaction: '98%'
  },
  {
    id: 2,
    title: '培训机构',
    category: '商业培训',
    description: '书法培训班的得力助手，标准化教学流程，提升培训质量和学员满意度',
    image: 'https://via.placeholder.com/400x300/fff3e0/f57c00?text=Activity+Scene',
    features: ['专业指导', '学员管理', '课程规划', '效果评估'],
    users: '200+机构',
    satisfaction: '96%'
  },
  {
    id: 3,
    title: '家庭学习',
    category: '个人用户',
    description: '在家也能享受专业的书法指导，培养孩子的书写习惯和文化素养',
    image: 'https://via.placeholder.com/400x300/fce4ec/c2185b?text=Equipment+Scene',
    features: ['亲子互动', '趣味学习', '习惯养成', '文化传承'],
    users: '10000+家庭',
    satisfaction: '95%'
  },
  {
    id: 4,
    title: '企业培训',
    category: '企业服务',
    description: '为企业员工提供书法修身课程，提升文化素养和团队凝聚力',
    image: 'https://via.placeholder.com/400x300/e0f2f1/00695c?text=People+Scene',
    features: ['团建活动', '文化培训', '压力释放', '技能提升'],
    users: '100+企业',
    satisfaction: '94%'
  }
])

// 新闻动态数据
const featuredNews = ref({
  id: 1,
  title: '艺学练字机器人荣获"2024年度教育科技创新奖"',
  excerpt: '在刚刚结束的全国教育科技创新大会上，我们的AI练字机器人凭借其卓越的技术创新和教育应用效果，荣获"2024年度教育科技创新奖"。这一荣誉不仅是对我们技术实力的认可，更是对我们致力于传承中华书法文化使命的肯定。',
  category: '公司新闻',
  date: '2024-12-15',
  image: 'https://via.placeholder.com/600x400/f8f9fa/495057?text=YXRobot+Main'
})

const recentNews = ref([
  {
    id: 2,
    title: '新版AI算法上线，识别准确率提升至99.5%',
    excerpt: '经过半年的技术攻关，我们的新一代AI识别算法正式上线，笔画识别准确率从99.2%提升至99.5%...',
    category: '技术更新',
    date: '2024-12-10',
    image: 'https://via.placeholder.com/500x350/e3f2fd/1976d2?text=Robot+Detail+1'
  },
  {
    id: 3,
    title: '与北京师范大学达成战略合作协议',
    excerpt: '双方将在书法教育研究、AI技术应用、师资培训等方面开展深度合作...',
    category: '合作动态',
    date: '2024-12-05',
    image: 'https://via.placeholder.com/400x300/e8f5e8/2e7d32?text=Teaching+Scene'
  },
  {
    id: 4,
    title: '全国中小学书法教育论坛成功举办',
    excerpt: '来自全国各地的300多位书法教育专家和一线教师参加了本次论坛...',
    category: '行业资讯',
    date: '2024-11-28',
    image: 'https://via.placeholder.com/400x300/fff3e0/f57c00?text=Activity+Scene'
  },
  {
    id: 5,
    title: '公益项目"书法进乡村"惠及千名学生',
    excerpt: '我们的公益项目已为偏远地区的1000多名学生提供了免费的书法学习机会...',
    category: '公益活动',
    date: '2024-11-20',
    image: 'https://via.placeholder.com/400x300/e0f2f1/00695c?text=People+Scene'
  }
])

// 合作伙伴数据
const educationPartners = ref([
  {
    id: 1,
    name: '北京师范大学',
    description: '书法教育研究合作',
    logo: 'https://images.unsplash.com/photo-1562774053-701939374585?w=120&h=80&fit=crop'
  },
  {
    id: 2,
    name: '华东师范大学',
    description: 'AI教育应用研究',
    logo: 'https://images.unsplash.com/photo-1562774053-701939374585?w=120&h=80&fit=crop'
  },
  {
    id: 3,
    name: '中国书法家协会',
    description: '书法标准制定',
    logo: 'https://images.unsplash.com/photo-1562774053-701939374585?w=120&h=80&fit=crop'
  },
  {
    id: 4,
    name: '全国中小学书法教育联盟',
    description: '教学资源共享',
    logo: 'https://images.unsplash.com/photo-1562774053-701939374585?w=120&h=80&fit=crop'
  }
])

const technologyPartners = ref([
  {
    id: 1,
    name: '百度AI',
    description: '计算机视觉技术',
    logo: 'https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=120&h=80&fit=crop'
  },
  {
    id: 2,
    name: '腾讯云',
    description: '云计算服务',
    logo: 'https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=120&h=80&fit=crop'
  },
  {
    id: 3,
    name: '华为鲲鹏',
    description: '硬件平台支持',
    logo: 'https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=120&h=80&fit=crop'
  },
  {
    id: 4,
    name: '商汤科技',
    description: 'AI算法优化',
    logo: 'https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=120&h=80&fit=crop'
  }
])

// 增强的用户评价数据
const enhancedTestimonials = ref([
  {
    id: 1,
    name: '张老师',
    title: '小学语文教师',
    location: '北京市海淀区',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
    content: '使用练字机器人后，学生们的书写兴趣明显提高了。AI指导很专业，比我手把手教学还要细致。',
    rating: 5,
    date: '2024-11-15',
    verified: true,
    tags: ['教学效果好', '学生喜欢', '操作简单']
  },
  {
    id: 2,
    name: '李妈妈',
    title: '家长',
    location: '上海市浦东新区',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&h=100&fit=crop&crop=face',
    content: '孩子每天都主动要求练字，字写得越来越好看。机器人很有耐心，不会像我一样发脾气。',
    rating: 5,
    date: '2024-11-10',
    verified: true,
    tags: ['孩子喜欢', '进步明显', '家长省心']
  },
  {
    id: 3,
    name: '王校长',
    title: '培训机构负责人',
    location: '广州市天河区',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
    content: '引入练字机器人后，我们的教学质量和效率都有了显著提升。家长们的满意度也大大提高。',
    rating: 5,
    date: '2024-11-05',
    verified: true,
    tags: ['提升效率', '家长满意', '投资回报高']
  },
  {
    id: 4,
    name: '陈先生',
    title: '企业高管',
    location: '深圳市南山区',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
    content: '工作压力大，练字成了我放松的方式。机器人的指导很专业，让我重新找回了对书法的热爱。',
    rating: 4,
    date: '2024-10-28',
    verified: true,
    tags: ['压力释放', '专业指导', '重拾兴趣']
  }
])

// 滚动到指定区域
const scrollToProducts = () => {
  const element = document.getElementById('products')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const scrollToFeatures = () => {
  const element = document.getElementById('features')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// 处理购买
const handlePurchase = (product?: any) => {
  console.log('购买产品:', product)
  // 这里可以跳转到购买页面或打开购买弹窗
}

// 处理租赁
const handleRental = (product?: any) => {
  console.log('租赁产品:', product)
  // 这里可以跳转到租赁页面或打开租赁弹窗
}

// 获取规格标签的中文名称
const getSpecLabel = (key: string): string => {
  const labels: Record<string, string> = {
    dimensions: '产品尺寸',
    weight: '产品重量',
    battery: '电池续航',
    connectivity: '连接方式',
    display: '显示屏幕',
    storage: '存储容量',
    users: '用户支持',
    courses: '课程内容'
  }
  return labels[key] || key
}

// 阅读更多新闻
const readMore = (news: any) => {
  console.log('阅读新闻:', news)
  // 这里可以跳转到新闻详情页面
}
</script>

<style lang="scss" scoped>
.home-page {
  overflow-x: hidden;
  
  .section-container {
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
  
  // 确保所有section都有合适的间距
  section {
    position: relative;
    
    &:not(:last-child) {
      margin-bottom: 0;
    }
  }
}

// 英雄区域
.hero-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 50%, var(--primary-color) 100%);
  padding: 120px 0;
  color: white;
  min-height: 85vh;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  
  // 添加背景装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="20" cy="80" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
    pointer-events: none;
  }
  
  .hero-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }
  
  .hero-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
  }
  
  .hero-title {
    font-size: 52px;
    font-weight: 800;
    color: white;
    line-height: 1.1;
    margin-bottom: 32px;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    animation: fadeInUp 1s ease-out;
    
    .highlight {
      background: linear-gradient(135deg, #FFE5E6 0%, #FFFFFF 50%, #FFE5E6 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      position: relative;
      display: inline-block;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.8) 50%, transparent 100%);
        border-radius: 2px;
        animation: slideIn 1.5s ease-out 0.5s both;
      }
    }
  }
  
  .hero-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.95);
    line-height: 1.7;
    margin-bottom: 40px;
    max-width: 500px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    animation: fadeInUp 1s ease-out 0.3s both;
  }
  
  .hero-actions {
    display: flex;
    gap: 20px;
    margin-bottom: 60px;
    animation: fadeInUp 1s ease-out 0.6s both;
    
    .el-button {
      padding: 16px 32px;
      font-size: 18px;
      font-weight: 600;
      border-radius: 50px;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
        transition: left 0.6s;
      }
      
      &:hover::before {
        left: 100%;
      }
    }
    
    .cta-primary {
      background: rgba(255, 255, 255, 0.15);
      border: 2px solid rgba(255, 255, 255, 0.3);
      color: white;
      backdrop-filter: blur(10px);
      
      &:hover {
        background: rgba(255, 255, 255, 0.25);
        border-color: rgba(255, 255, 255, 0.5);
        transform: translateY(-3px);
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
      }
    }
  }
  
  .hero-stats {
    display: flex;
    gap: 50px;
    animation: fadeInUp 1s ease-out 0.9s both;
    
    .stat-item {
      text-align: center;
      padding: 20px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 16px;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.15);
        transform: translateY(-5px);
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      }
      
      .stat-number {
        font-size: 36px;
        font-weight: 800;
        color: white;
        margin-bottom: 8px;
        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
        counter-reset: number;
        animation: countUp 2s ease-out 1.2s both;
      }
      
      .stat-label {
        font-size: 16px;
        color: rgba(255, 255, 255, 0.9);
        font-weight: 500;
      }
    }
  }
  
  .product-showcase {
    position: relative;
    
    .product-img {
      width: 100%;
      height: auto;
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-lg);
    }
    
    .floating-card {
      position: absolute;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      padding: 16px 20px;
      border-radius: 16px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
      border: 1px solid rgba(255, 255, 255, 0.2);
      z-index: 10;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
      }
      
      .el-icon {
        font-size: 20px;
        padding: 8px;
        border-radius: 50%;
        background: rgba(255, 90, 95, 0.1);
        color: var(--primary-color);
      }
      
      &.card-1 {
        top: 30px;
        right: 30px;
        animation: float 4s ease-in-out infinite;
        
        .el-icon {
          background: rgba(255, 90, 95, 0.15);
          color: var(--primary-color);
        }
      }
      
      &.card-2 {
        bottom: 120px;
        left: 30px;
        animation: float 4s ease-in-out infinite 1.5s;
        
        .el-icon {
          background: rgba(16, 185, 129, 0.15);
          color: #10B981;
        }
      }
      
      &.card-3 {
        bottom: 30px;
        right: 100px;
        animation: float 4s ease-in-out infinite 3s;
        
        .el-icon {
          background: rgba(245, 158, 11, 0.15);
          color: var(--accent-yellow);
        }
      }
    }
  }
}

// 产品特色
.features-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 50%, #f8fafc 100%);
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
    gap: 50px;
  }
  
  .feature-card {
    text-align: center;
    padding: 50px 30px;
    border-radius: 24px;
    background: white;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(0, 0, 0, 0.05);
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: var(--primary-gradient);
      transform: scaleX(0);
      transition: transform 0.4s ease;
    }
    
    &:hover {
      transform: translateY(-12px);
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
      border-color: rgba(255, 90, 95, 0.2);
      
      &::before {
        transform: scaleX(1);
      }
      
      .feature-icon {
        transform: scale(1.15) rotate(5deg);
        box-shadow: 0 15px 30px rgba(255, 90, 95, 0.3);
        
        &.icon-green {
          box-shadow: 0 15px 30px rgba(16, 185, 129, 0.3);
        }
      }
      
      .feature-title {
        color: var(--primary-color);
      }
    }
    
    .feature-icon {
      width: 90px;
      height: 90px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 32px;
      font-size: 36px;
      color: white;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        inset: -4px;
        border-radius: 50%;
        padding: 4px;
        background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
        mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
        mask-composite: xor;
        opacity: 0;
        transition: opacity 0.4s ease;
      }
      
      &:hover::after {
        opacity: 1;
      }
      
      &.icon-red { 
        background: var(--primary-gradient);
        box-shadow: 0 8px 25px rgba(255, 90, 95, 0.4);
      }
      &.icon-green { 
        background: linear-gradient(135deg, #10b981, #059669);
        box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
        color: white;
      }
      &.icon-yellow { 
        background: linear-gradient(135deg, var(--accent-yellow), var(--accent-yellow-dark));
        box-shadow: 0 8px 25px rgba(245, 158, 11, 0.4);
      }
      &.icon-blue { 
        background: linear-gradient(135deg, #3b82f6, #1d4ed8);
        box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
      }
      &.icon-purple { 
        background: linear-gradient(135deg, #8b5cf6, #6d28d9);
        box-shadow: 0 8px 25px rgba(139, 92, 246, 0.4);
      }
    }
    
    .feature-title {
      font-size: 22px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 20px;
      transition: color 0.3s ease;
    }
    
    .feature-desc {
      color: var(--text-secondary);
      line-height: 1.7;
      font-size: 16px;
    }
  }
}

// 产品展示 - 增强版
.products-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 50%, #f8fafc 100%);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 30% 20%, rgba(255, 90, 95, 0.05) 0%, transparent 50%),
                radial-gradient(circle at 70% 80%, rgba(16, 185, 129, 0.05) 0%, transparent 50%);
    pointer-events: none;
  }
  


  // 重新设计的产品展示布局
  .product-showcase-redesigned {
    .product-container-new {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 60px;
      align-items: flex-start;
      max-width: 1200px;
      margin: 0 auto;
      background: white;
      border-radius: 20px;
      padding: 40px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
      border: 1px solid #f1f5f9;
      
      .product-gallery {
        display: flex;
        gap: 20px;
        
        .product-thumbnails {
          display: flex;
          flex-direction: column;
          gap: 12px;
          
          .thumbnail-item {
            width: 80px;
            height: 80px;
            border-radius: 12px;
            overflow: hidden;
            border: 2px solid transparent;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &.active {
              border-color: #ff5a5f;
              box-shadow: 0 4px 15px rgba(255, 90, 95, 0.3);
            }
            
            &:hover {
              border-color: #ff5a5f;
              transform: scale(1.05);
            }
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
        }
        
        .product-main-display {
          flex: 1;
          
          .main-image-wrapper {
            position: relative;
            border-radius: 16px;
            overflow: hidden;
            background: #f8fafc;
            
            .main-product-image {
              width: 100%;
              height: 400px;
              object-fit: cover;
              transition: transform 0.4s ease;
            }
            
            &:hover .main-product-image {
              transform: scale(1.02);
            }
            
            .product-badge-new {
              position: absolute;
              top: 20px;
              left: 20px;
              background: linear-gradient(135deg, #ff5a5f, #ff3b41);
              color: white;
              padding: 8px 16px;
              border-radius: 20px;
              font-size: 14px;
              font-weight: 700;
              box-shadow: 0 4px 15px rgba(255, 90, 95, 0.4);
              
              &.badge-hot {
                background: linear-gradient(135deg, #ff5a5f, #ff3b41);
                animation: pulse 2s infinite;
              }
            }
          }
        }
      }
      
      .product-info-new {
        .product-header-new {
          margin-bottom: 24px;
          
          .product-title-new {
            font-size: 32px;
            font-weight: 800;
            color: #1f2937;
            margin-bottom: 8px;
            line-height: 1.2;
          }
          
          .product-subtitle-new {
            font-size: 16px;
            color: #6b7280;
            font-weight: 500;
          }
        }
        
        .education-price-section {
          background: linear-gradient(135deg, #fef2f2, #fff5f5);
          border: 2px solid #fecaca;
          border-radius: 16px;
          padding: 24px;
          margin-bottom: 24px;
          
          .price-label-new {
            font-size: 14px;
            color: #dc2626;
            font-weight: 600;
            margin-bottom: 8px;
          }
          
          .price-main-new {
            display: flex;
            align-items: baseline;
            gap: 12px;
            flex-wrap: wrap;
            
            .current-price-new {
              font-size: 36px;
              font-weight: 800;
              color: #dc2626;
            }
            
            .original-price-new {
              font-size: 18px;
              color: #9ca3af;
              text-decoration: line-through;
            }
            
            .discount-tag-new {
              background: linear-gradient(135deg, #10b981, #059669);
              color: white;
              padding: 6px 12px;
              border-radius: 20px;
              font-size: 14px;
              font-weight: 700;
              box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
            }
          }
        }
        
        .service-promises {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 12px;
          margin-bottom: 24px;
          
          .promise-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #374151;
            
            .promise-icon {
              color: #10b981;
              font-size: 16px;
              flex-shrink: 0;
            }
          }
        }
        
        .product-highlights-new {
          margin-bottom: 32px;
          
          .highlights-title {
            font-size: 18px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 16px;
          }
          
          .highlights-list {
            display: flex;
            flex-direction: column;
            gap: 8px;
            
            .highlight-row {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 16px;
              
              .highlight-item-new {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 14px;
                color: #374151;
                
                .highlight-icon-new {
                  color: #10b981;
                  font-size: 14px;
                  flex-shrink: 0;
                }
              }
            }
          }
        }
        
        .purchase-buttons-new {
          display: flex;
          flex-direction: column;
          gap: 12px;
          
          .el-button {
            width: 100%;
            padding: 16px 24px;
            font-size: 16px;
            font-weight: 700;
            border-radius: 12px;
            transition: all 0.3s ease;
          }
          
          .btn-buy-now {
            background: linear-gradient(135deg, #ff5a5f, #ff3b41);
            border: none;
            color: white;
            box-shadow: 0 8px 25px rgba(255, 90, 95, 0.4);
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 12px 35px rgba(255, 90, 95, 0.5);
            }
          }
          
          .btn-try-rental {
            background: linear-gradient(135deg, #10b981, #059669);
            border: none;
            color: white;
            position: relative;
            
            .rental-note {
              font-size: 12px;
              opacity: 0.9;
              margin-left: 8px;
            }
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 12px 35px rgba(16, 185, 129, 0.4);
            }
          }
          
          .btn-consult-new {
            border: 2px solid #6b7280;
            color: #6b7280;
            background: transparent;
            
            &:hover {
              background: #6b7280;
              color: white;
              transform: translateY(-2px);
            }
          }
        }
      }
    }
  }

  // 增强的产品展示布局
  .product-showcase-enhanced {
    .product-container {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 80px;
      align-items: center;
      max-width: 1200px;
      margin: 0 auto;
      position: relative;
      z-index: 1;
      
      .product-visual {
        position: relative;
        
        .product-image-wrapper {
          position: relative;
          border-radius: 24px;
          overflow: hidden;
          box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          
          &:hover {
            transform: translateY(-8px);
            box-shadow: 0 30px 80px rgba(0, 0, 0, 0.15);
            
            .image-glow {
              opacity: 1;
            }
            
            .product-floating-tags .floating-tag {
              transform: translateY(-5px);
              box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
            }
          }
          
          .product-main-image {
            width: 100%;
            height: auto;
            display: block;
            transition: transform 0.4s ease;
          }
          
          .product-badge-enhanced {
            position: absolute;
            top: 24px;
            right: 24px;
            color: white;
            padding: 12px 20px;
            border-radius: 50px;
            font-size: 14px;
            font-weight: 700;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            z-index: 10;
            
            // 默认红色背景（热销、专业）
            background: linear-gradient(135deg, #ff5a5f 0%, #ff3b41 100%);
            
            &.badge-recommend {
              background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            }
          }
          
          .image-glow {
            position: absolute;
            inset: -4px;
            background: linear-gradient(135deg, rgba(255, 90, 95, 0.3), rgba(16, 185, 129, 0.3));
            border-radius: 28px;
            opacity: 0;
            transition: opacity 0.4s ease;
            z-index: -1;
            filter: blur(20px);
          }
          
          .product-floating-tags {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            pointer-events: none;
            
            .floating-tag {
              position: absolute;
              background: rgba(255, 255, 255, 0.95);
              backdrop-filter: blur(10px);
              padding: 12px 16px;
              border-radius: 50px;
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 14px;
              font-weight: 600;
              color: #1f2937;
              box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
              border: 1px solid rgba(255, 255, 255, 0.3);
              transition: all 0.3s ease;
              
              .el-icon {
                font-size: 16px;
                padding: 6px;
                border-radius: 50%;
                color: white;
              }
              
              &.tag-ai {
                top: 60px;
                left: 24px;
                animation: float 4s ease-in-out infinite;
                
                .el-icon {
                  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
                }
              }
              
              &.tag-quality {
                bottom: 60px;
                right: 24px;
                animation: float 4s ease-in-out infinite 2s;
                
                .el-icon {
                  background: linear-gradient(135deg, #f59e0b, #d97706);
                }
              }
            }
          }
        }
      }
      
      .product-details {
        .product-header {
          margin-bottom: 32px;
          
          .product-title-section {
            margin-bottom: 16px;
            
            .product-name-enhanced {
              font-size: 36px;
              font-weight: 800;
              color: #1f2937;
              margin-bottom: 8px;
              line-height: 1.2;
              background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
              background-clip: text;
            }
            
            .product-model-enhanced {
              font-size: 18px;
              color: #6b7280;
              font-weight: 600;
              letter-spacing: 0.5px;
            }
          }
          
          .product-rating {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .rating-stars {
              display: flex;
              gap: 2px;
              
              .star {
                color: #fbbf24;
                font-size: 18px;
                filter: drop-shadow(0 1px 2px rgba(251, 191, 36, 0.3));
              }
            }
            
            .rating-text {
              font-size: 16px;
              color: #6b7280;
              font-weight: 500;
            }
          }
        }
        
        .product-desc-enhanced {
          font-size: 18px;
          color: #4b5563;
          line-height: 1.7;
          margin-bottom: 40px;
          padding: 24px;
          background: linear-gradient(135deg, #f9fafb 0%, #ffffff 100%);
          border-radius: 16px;
          border-left: 4px solid #ff5a5f;
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
        }
        
        .product-highlights-enhanced {
          margin-bottom: 40px;
          
          h4 {
            font-size: 20px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 8px;
            
            &::before {
              content: '';
              width: 4px;
              height: 20px;
              background: linear-gradient(135deg, #ff5a5f, #ff3b41);
              border-radius: 2px;
            }
          }
          
          .highlights-grid {
            display: grid;
            gap: 16px;
            
            .highlight-item {
              display: flex;
              align-items: center;
              gap: 12px;
              padding: 16px 20px;
              background: white;
              border-radius: 12px;
              box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
              border: 1px solid #f3f4f6;
              transition: all 0.3s ease;
              
              &:hover {
                transform: translateX(8px);
                box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
                border-color: #ff5a5f;
                
                .highlight-icon {
                  background: #ff5a5f;
                  color: white;
                  transform: scale(1.1);
                }
              }
              
              .highlight-icon {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                background: #f0fdf4;
                color: #10b981;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 16px;
                transition: all 0.3s ease;
                flex-shrink: 0;
              }
              
              span {
                font-size: 16px;
                color: #374151;
                font-weight: 500;
                line-height: 1.5;
              }
            }
          }
        }
        
        .product-features-enhanced {
          margin-bottom: 40px;
          
          h4 {
            font-size: 20px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 8px;
            
            &::before {
              content: '';
              width: 4px;
              height: 20px;
              background: linear-gradient(135deg, #10b981, #059669);
              border-radius: 2px;
            }
          }
          
          .features-tags-enhanced {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            
            .feature-tag-enhanced {
              background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
              color: #0369a1;
              padding: 10px 16px;
              border-radius: 50px;
              font-size: 14px;
              font-weight: 600;
              border: 1px solid #bae6fd;
              transition: all 0.3s ease;
              position: relative;
              overflow: hidden;
              
              &::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
                transition: left 0.5s;
              }
              
              &:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(3, 105, 161, 0.2);
                background: linear-gradient(135deg, #0369a1 0%, #0284c7 100%);
                color: white;
                border-color: #0369a1;
                
                &::before {
                  left: 100%;
                }
              }
            }
          }
        }
        
        .product-purchase-section {
          background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
          padding: 32px;
          border-radius: 20px;
          box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
          border: 1px solid #e5e7eb;
          
          .price-display {
            margin-bottom: 32px;
            
            .price-row {
              display: flex;
              align-items: baseline;
              gap: 16px;
              margin-bottom: 12px;
              
              .original-price-enhanced {
                font-size: 20px;
                color: #9ca3af;
                text-decoration: line-through;
                font-weight: 500;
              }
              
              .current-price-enhanced {
                font-size: 42px;
                font-weight: 800;
                background: linear-gradient(135deg, #ff5a5f 0%, #ff3b41 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                position: relative;
                
                &::after {
                  content: '';
                  position: absolute;
                  bottom: -4px;
                  left: 0;
                  right: 0;
                  height: 3px;
                  background: linear-gradient(90deg, transparent 0%, #ff5a5f 50%, transparent 100%);
                  border-radius: 2px;
                  opacity: 0.3;
                }
              }
            }
            
            .discount-info {
              display: flex;
              gap: 12px;
              align-items: center;
              
              .discount-badge {
                background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                color: white;
                padding: 8px 16px;
                border-radius: 50px;
                font-size: 14px;
                font-weight: 700;
                box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
              }
              
              .discount-percent {
                background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
                color: white;
                padding: 8px 16px;
                border-radius: 50px;
                font-size: 14px;
                font-weight: 700;
                box-shadow: 0 4px 15px rgba(251, 191, 36, 0.3);
              }
            }
          }
          
          .purchase-actions-enhanced {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            
            .el-button {
              padding: 16px 28px;
              font-size: 16px;
              font-weight: 700;
              border-radius: 50px;
              transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
              position: relative;
              overflow: hidden;
              flex: 1;
              min-width: 140px;
              
              &::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                transition: left 0.6s;
              }
              
              &:hover::before {
                left: 100%;
              }
            }
            
            .btn-purchase {
              background: linear-gradient(135deg, #ff5a5f 0%, #ff3b41 100%);
              border: none;
              color: white;
              box-shadow: 0 8px 25px rgba(255, 90, 95, 0.4);
              
              &:hover {
                transform: translateY(-3px);
                box-shadow: 0 15px 35px rgba(255, 90, 95, 0.5);
                background: linear-gradient(135deg, #ff3b41 0%, #e11d48 100%);
              }
            }
            
            .btn-rental {
              border: 2px solid #10b981;
              color: #10b981;
              background: transparent;
              
              &:hover {
                background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                color: white;
                transform: translateY(-3px);
                box-shadow: 0 15px 35px rgba(16, 185, 129, 0.4);
                border-color: #10b981;
              }
            }
            
            .btn-consult {
              border: 2px solid #6b7280;
              color: #6b7280;
              background: transparent;
              
              &:hover {
                background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
                color: white;
                transform: translateY(-3px);
                box-shadow: 0 15px 35px rgba(107, 114, 128, 0.4);
                border-color: #6b7280;
              }
            }
          }
        }
      }
    }
    
    .product-specifications {
      background: white;
      padding: 40px;
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-sm);
      
      h4 {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 32px;
        text-align: center;
      }
      
      .specs-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 24px;
        
        .spec-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 0;
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
  }
  
  .products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 40px;
  }
  
  .product-card {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .product-image {
      position: relative;
      
      img {
        width: 100%;
        height: 240px;
        object-fit: cover;
      }
      
      .product-badge {
        position: absolute;
        top: 16px;
        right: 16px;
        color: white;
        padding: 6px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        
        // 默认红色背景（热销、专业）
        background: var(--primary-gradient);
        
        &.badge-recommend {
          background: var(--accent-green-gradient);
          color: white;
        }
      }
    }
    
    .product-content {
      padding: 32px;
    }
    
    .product-name {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 12px;
    }
    
    .product-desc {
      color: var(--text-secondary);
      line-height: 1.6;
      margin-bottom: 20px;
    }
    
    .product-features {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 24px;
      
      .feature-tag {
        background: var(--bg-tertiary);
        color: var(--text-secondary);
        padding: 4px 8px;
        border-radius: var(--radius-sm);
        font-size: 12px;
      }
    }
    
    .product-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .product-price {
        .price-label {
          font-size: 12px;
          color: var(--text-secondary);
          display: block;
        }
        
        .price-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--primary-color);
          text-shadow: 0 1px 2px rgba(255, 90, 95, 0.1);
        }
      }
      
      .product-actions {
        display: flex;
        gap: 8px;
        
        .el-button {
          font-size: 14px;
        }
        
        .purchase-btn {
          background: var(--primary-gradient);
          border: none;
          
          &:hover {
            transform: translateY(-1px);
            box-shadow: var(--shadow-md);
          }
        }
        
        .rental-btn {
          border: 2px solid #10B981;
          color: #10B981;
          background: transparent;
          
          &:hover {
            background: #10B981;
            color: white;
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(16, 185, 129, 0.2);
          }
        }
      }
    }
  }
}

// 公益理念
.charity-section {
  padding: 100px 0;
  background: var(--primary-gradient);
  color: white;
  
  .charity-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
  }
  
  .charity-badge {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    padding: 8px 16px;
    border-radius: var(--radius-md);
    font-size: 14px;
    font-weight: 600;
    display: inline-block;
    margin-bottom: 24px;
  }
  
  .charity-title {
    font-size: 36px;
    font-weight: 700;
    line-height: 1.2;
    margin-bottom: 24px;
  }
  
  .charity-desc {
    font-size: 18px;
    line-height: 1.6;
    opacity: 0.9;
    margin-bottom: 40px;
  }
  
  .charity-stats {
    display: flex;
    gap: 40px;
    margin-bottom: 40px;
    
    .charity-stat {
      .stat-number {
        font-size: 32px;
        font-weight: 700;
        margin-bottom: 4px;
        color: white;
        text-shadow: 0 2px 4px rgba(255, 255, 255, 0.3);
        background: linear-gradient(135deg, white 0%, #f8f9fa 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, transparent 0%, white 50%, transparent 100%);
          border-radius: 1px;
          opacity: 0.6;
        }
      }
      
      .stat-label {
        font-size: 14px;
        opacity: 0.8;
      }
    }
  }
  
  .charity-image {
    img {
      width: 100%;
      height: auto;
      border-radius: var(--radius-xl);
    }
  }
}

// 用户评价
.testimonials-section {
  padding: 100px 0;
  
  .testimonials-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 40px;
  }
  
  .testimonial-card {
    background: white;
    padding: 32px;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-sm);
    
    .testimonial-content {
      margin-bottom: 24px;
      
      .rating {
        display: flex;
        gap: 4px;
        margin-bottom: 16px;
        
        .star {
          color: var(--primary-color);
          font-size: 16px;
          text-shadow: 0 1px 2px rgba(255, 90, 95, 0.2);
          transition: all 0.2s ease;
          
          &:hover {
            transform: scale(1.1);
            color: var(--primary-dark);
          }
        }
      }
      
      .testimonial-text {
        color: var(--text-secondary);
        line-height: 1.6;
        font-style: italic;
      }
    }
    
    .testimonial-author {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .author-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        object-fit: cover;
      }
      
      .author-name {
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 4px;
        display: flex;
        align-items: center;
        gap: 8px;
        
        .verified-badge {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          width: 16px;
          height: 16px;
          background: var(--accent-green);
          border-radius: 50%;
          color: white;
          font-size: 10px;
          box-shadow: 0 2px 4px rgba(16, 185, 129, 0.3);
          transition: all 0.2s ease;
          
          &:hover {
            background: var(--accent-green-dark);
            transform: scale(1.1);
          }
          
          &::before {
            content: '✓';
            font-weight: bold;
          }
        }
      }
      
      .author-title {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

// CTA区域
.cta-section {
  padding: 120px 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 50%, var(--primary-color) 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
                radial-gradient(circle at 70% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
    pointer-events: none;
  }
  
  .cta-content {
    text-align: center;
    position: relative;
    z-index: 1;
    max-width: 900px;
    margin: 0 auto;
    
    .cta-title {
      font-size: 48px;
      font-weight: 800;
      color: white;
      margin-bottom: 24px;
      text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
      animation: fadeInUp 1s ease-out;
    }
    
    .cta-subtitle {
      font-size: 22px;
      color: rgba(255, 255, 255, 0.95);
      margin-bottom: 50px;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      animation: fadeInUp 1s ease-out 0.3s both;
    }
    
    .cta-actions {
      display: flex;
      justify-content: center;
      gap: 24px;
      flex-wrap: wrap;
      animation: fadeInUp 1s ease-out 0.6s both;
      
      .el-button {
        padding: 18px 36px;
        font-size: 18px;
        font-weight: 600;
        border-radius: 50px;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: left 0.6s;
        }
        
        &:hover::before {
          left: 100%;
        }
      }
      
      .cta-primary-btn {
        background: rgba(255, 255, 255, 0.15);
        border: 2px solid rgba(255, 255, 255, 0.3);
        color: white;
        backdrop-filter: blur(10px);
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          border-color: rgba(255, 255, 255, 0.5);
          transform: translateY(-3px);
          box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
        }
      }
      
      .cta-green-btn {
        border: 2px solid var(--accent-green);
        color: var(--accent-green);
        background: transparent;
        
        &:hover {
          background: var(--accent-green);
          color: white;
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(16, 185, 129, 0.2);
        }
      }
      
      .cta-text-btn {
        color: var(--primary-color);
        
        &:hover {
          color: var(--primary-dark);
        }
      }
    }
  }
}

// 动画
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-section {
    padding: 80px 0;
    min-height: 70vh;
    
    .hero-content {
      grid-template-columns: 1fr;
      gap: 40px;
      text-align: center;
    }
    
    .hero-title {
      font-size: 36px;
      line-height: 1.2;
    }
    
    .hero-subtitle {
      font-size: 18px;
    }
    
    .hero-actions {
      flex-direction: column;
      align-items: center;
      gap: 16px;
      
      .el-button {
        width: 100%;
        max-width: 280px;
        padding: 14px 28px;
        font-size: 16px;
      }
    }
    
    .hero-stats {
      flex-direction: column;
      gap: 20px;
      align-items: center;
      
      .stat-item {
        width: 100%;
        max-width: 200px;
      }
    }
  }
  
  .features-section {
    padding: 80px 0;
    
    .features-grid {
      grid-template-columns: 1fr;
      gap: 30px;
    }
    
    .feature-card {
      padding: 40px 24px;
    }
    
    .feature-icon {
      width: 70px;
      height: 70px;
      font-size: 28px;
    }
  }
  
  .charity-section {
    .charity-content {
      grid-template-columns: 1fr;
      gap: 40px;
      text-align: center;
    }
    
    .charity-stats {
      justify-content: center;
      gap: 20px;
      
      .charity-stat {
        .stat-number {
          font-size: 24px;
          color: var(--accent-green);
        }
      }
    }
  }
  
  .features-section {
    .features-grid {
      grid-template-columns: 1fr;
      gap: 24px;
    }
    
    .feature-card {
      padding: 24px 16px;
      
      .feature-icon {
        width: 60px;
        height: 60px;
        font-size: 24px;
        
        &.icon-green {
          background: var(--accent-green-gradient);
          color: white;
        }
      }
    }
  }
  
  .products-section {
    .single-product-showcase {
      .product-hero {
        grid-template-columns: 1fr;
        gap: 30px;
      }
      
      .product-actions {
        flex-direction: column;
        
        .el-button {
          width: 100%;
        }
        
        .rental-btn {
          border-color: var(--accent-green);
          color: var(--accent-green);
          
          &:hover {
            background: var(--accent-green);
          }
        }
      }
    }
  }
  
  .cta-section {
    .cta-actions {
      flex-direction: column;
      align-items: center;
      
      .el-button {
        width: 100%;
        max-width: 300px;
      }
      
      .cta-green-btn {
        border-color: var(--accent-green);
        color: var(--accent-green);
      }
    }
  }
  
  .section-header {
    .section-title {
      font-size: 28px;
    }
  }
  
  .features-grid,
  .products-grid,
  .testimonials-grid {
    grid-template-columns: 1fr;
  }
}

// 技术优势

.technology-section {
  padding: 100px 0;
  background: white;
  
  // 顶部技术展示图片
  .tech-image-top {
    position: relative;
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    margin-bottom: 60px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
    
    img {
      width: 100%;
      height: 400px;
      object-fit: cover;
      display: block;
    }
    
    .tech-overlay-top {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
      padding: 40px;
      color: white;
      
      .tech-stats-top {
        display: flex;
        justify-content: space-around;
        
        .tech-stat-top {
          text-align: center;
          padding: 20px;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 12px;
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);
          
          .stat-number {
            font-size: 28px;
            font-weight: 800;
            margin-bottom: 8px;
            text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
          }
          
          .stat-label {
            font-size: 14px;
            opacity: 0.9;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  // 底部技术特性卡片网格
  .tech-features-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
    
    .tech-feature-card {
      display: flex;
      gap: 20px;
      padding: 30px;
      background: var(--bg-secondary);
      border-radius: 16px;
      transition: all 0.3s ease;
      border: 1px solid rgba(0, 0, 0, 0.05);
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
        border-color: var(--primary-color);
      }
      
      .tech-icon-card {
        width: 70px;
        height: 70px;
        background: var(--primary-gradient);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 28px;
        flex-shrink: 0;
        box-shadow: 0 8px 20px rgba(255, 90, 95, 0.3);
      }
      
      .tech-content-card {
        flex: 1;
        
        h4 {
          font-size: 20px;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 12px;
          line-height: 1.3;
        }
        
        p {
          color: var(--text-secondary);
          line-height: 1.6;
          margin-bottom: 16px;
          font-size: 15px;
        }
        
        .tech-metrics-card {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
          
          .metric-tag-card {
            background: white;
            color: var(--primary-color);
            padding: 6px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            border: 1px solid rgba(255, 90, 95, 0.2);
          }
        }
      }
    }
  }
  
  .tech-image {
    position: relative;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    
    img {
      width: 100%;
      height: auto;
      display: block;
    }
    
    .tech-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
      padding: 30px;
      color: white;
      
      .tech-stats {
        display: flex;
        justify-content: space-around;
        
        .tech-stat {
          text-align: center;
          
          .stat-number {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 12px;
            opacity: 0.9;
          }
        }
      }
    }
  }
  

}

// 应用场景 - 紧凑图卡样式
.scenarios-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .scenarios-grid-compact {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
  }
  
  .scenario-card-compact {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    }
    
    .scenario-image-compact {
      position: relative;
      height: 160px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .scenario-overlay-compact {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(0, 0, 0, 0.4), transparent);
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        padding: 12px;
        
        .scenario-badge-compact {
          background: var(--primary-gradient);
          color: white;
          padding: 4px 10px;
          border-radius: 12px;
          font-size: 11px;
          font-weight: 600;
        }
        
        .scenario-stats-compact {
          display: flex;
          gap: 8px;
          
          .stat-compact {
            background: rgba(255, 255, 255, 0.9);
            padding: 4px 8px;
            border-radius: 8px;
            
            .stat-number {
              font-size: 12px;
              font-weight: 600;
              color: var(--primary-color);
            }
          }
        }
      }
      
      &:hover img {
        transform: scale(1.1);
      }
    }
    
    .scenario-info-compact {
      padding: 20px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      p {
        color: var(--text-secondary);
        line-height: 1.5;
        margin-bottom: 12px;
        font-size: 14px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .scenario-features-compact {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        
        .feature-tag-compact {
          background: var(--primary-color);
          color: white;
          padding: 2px 8px;
          border-radius: 10px;
          font-size: 11px;
          font-weight: 500;
        }
      }
    }
  }
}

// 新闻动态
.news-section {
  padding: 100px 0;
  background: white;
  
  .news-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
    margin-bottom: 40px;
  }
  
  .news-card.featured {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-xl);
    }
    
    .news-image {
      position: relative;
      height: 300px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .featured-badge {
        position: absolute;
        top: 16px;
        left: 16px;
        background: var(--primary-gradient);
        color: white;
        padding: 6px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
      }
    }
    
    .news-content {
      padding: 32px;
      
      .news-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .news-category {
          background: var(--primary-color);
          color: white;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 500;
        }
        
        .news-date {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .news-title {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
        line-height: 1.3;
      }
      
      .news-excerpt {
        color: var(--text-secondary);
        line-height: 1.6;
        margin-bottom: 20px;
      }
    }
  }
  
  .news-list {
    .news-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      border-radius: var(--radius-lg);
      transition: all 0.3s ease;
      margin-bottom: 16px;
      
      &:hover {
        background: var(--bg-secondary);
        transform: translateX(4px);
      }
      
      .news-thumb {
        width: 80px;
        height: 60px;
        border-radius: var(--radius-md);
        overflow: hidden;
        flex-shrink: 0;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .news-info {
        flex: 1;
        
        .news-meta {
          display: flex;
          gap: 12px;
          margin-bottom: 8px;
          
          .news-category {
            background: var(--bg-tertiary);
            color: var(--text-secondary);
            padding: 2px 6px;
            border-radius: var(--radius-sm);
            font-size: 11px;
          }
          
          .news-date {
            color: var(--text-light);
            font-size: 11px;
          }
        }
        
        .news-title {
          font-size: 14px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 4px;
          line-height: 1.3;
        }
        
        .news-excerpt {
          font-size: 12px;
          color: var(--text-secondary);
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }
  
  .news-more {
    text-align: center;
    
    .el-button {
      padding: 12px 24px;
      border: 2px solid var(--primary-color);
      color: var(--primary-color);
      background: transparent;
      
      &:hover {
        background: var(--primary-color);
        color: white;
      }
    }
  }
}

// 合作伙伴 - 紧凑图卡样式
.partners-section {
  padding: 100px 0;
  background: white;
  
  .partners-grid-compact {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
  }
  
  .partner-card-compact {
    background: white;
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: var(--primary-color);
    }
    
    .partner-logo-compact {
      width: 80px;
      height: 80px;
      margin: 0 auto 16px;
      border-radius: 12px;
      overflow: hidden;
      background: var(--bg-secondary);
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        filter: grayscale(100%);
        transition: filter 0.3s ease;
      }
    }
    
    &:hover .partner-logo-compact img {
      filter: grayscale(0%);
    }
    
    .partner-content-compact {
      .partner-name-compact {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
        line-height: 1.3;
      }
      
      .partner-desc-compact {
        font-size: 13px;
        color: var(--text-secondary);
        margin-bottom: 12px;
        line-height: 1.4;
      }
      
      .partner-type-compact {
        display: inline-block;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 11px;
        font-weight: 600;
        
        &.education {
          background: rgba(16, 185, 129, 0.1);
          color: #10B981;
        }
        
        &.technology {
          background: rgba(59, 130, 246, 0.1);
          color: #3B82F6;
        }
      }
    }
  }
}

// 用户评价增强样式
.testimonials-section {
  .testimonials-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
    margin-bottom: 60px;
    
    .stat-card {
      text-align: center;
      padding: 32px 24px;
      background: var(--bg-secondary);
      border-radius: var(--radius-xl);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: var(--shadow-md);
      }
      
      .stat-number {
        font-size: 36px;
        font-weight: 700;
        color: var(--primary-color);
        margin-bottom: 8px;
      }
      
      .stat-label {
        font-size: 16px;
        color: var(--text-secondary);
        margin-bottom: 12px;
      }
      
      .rating-stars {
        display: flex;
        justify-content: center;
        gap: 4px;
        
        .star {
          color: #FFD700;
          font-size: 18px;
        }
      }
    }
  }
  
  .testimonial-card {
    .testimonial-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
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
      
      .testimonial-date {
        font-size: 12px;
        color: var(--text-light);
      }
    }
    
    .testimonial-content {
      .testimonial-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        margin-top: 12px;
        
        .tag {
          background: var(--primary-color);
          color: white;
          padding: 2px 6px;
          border-radius: var(--radius-sm);
          font-size: 11px;
          font-weight: 500;
        }
      }
    }
    
    .testimonial-author {
      .author-info {
        .author-name {
          .verified-badge {
            color: #10B981;
            font-size: 16px;
          }
        }
        
        .author-location {
          font-size: 12px;
          color: var(--text-light);
          margin-top: 2px;
        }
      }
    }
  }
  
  .testimonials-more {
    text-align: center;
    margin-top: 40px;
    
    .el-button {
      padding: 12px 24px;
      border: 2px solid var(--primary-color);
      color: var(--primary-color);
      background: transparent;
      
      &:hover {
        background: var(--primary-color);
        color: white;
      }
    }
  }
}

// 响应式设计增强
@media (max-width: 768px) {
  .technology-content,
  .charity-content {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
  }
  
  .news-grid {
    grid-template-columns: 1fr !important;
  }
  
  .testimonials-stats {
    grid-template-columns: 1fr !important;
    gap: 20px !important;
  }
  
  .partners-logos {
    grid-template-columns: 1fr !important;
  }
  
  .scenarios-grid {
    grid-template-columns: 1fr !important;
  }
  
  .tech-features .tech-feature {
    flex-direction: column !important;
    text-align: center;
    
    .tech-icon {
      margin: 0 auto 16px;
    }
  }
}

// 公益理念完整样式
.charity-section {
  .charity-title {
    font-size: 36px;
    font-weight: 700;
    color: white;
    margin-bottom: 24px;
    line-height: 1.2;
  }
  
  .charity-desc {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.6;
    margin-bottom: 32px;
  }
  
  .charity-stats {
    display: flex;
    gap: 40px;
    margin-bottom: 32px;
    
    .charity-stat {
      .stat-number {
        font-size: 32px;
        font-weight: 700;
        color: white;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
  
  .charity-image {
    img {
      width: 100%;
      height: auto;
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-lg);
    }
  }
}

// CTA区域完整样式
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

// 动画效果
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    transform: scaleX(0);
  }
  to {
    transform: scaleX(1);
  }
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

// 响应式设计完整版
@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
    text-align: center;
  }
  
  .hero-title {
    font-size: 36px !important;
  }
  
  .hero-stats {
    justify-content: center !important;
    gap: 20px !important;
  }
  
  .technology-content,
  .charity-content {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
  }
  
  .news-grid {
    grid-template-columns: 1fr !important;
  }
  
  .testimonials-stats {
    grid-template-columns: 1fr !important;
    gap: 20px !important;
  }
  
  .partners-logos {
    grid-template-columns: 1fr !important;
  }
  
  .scenarios-grid {
    grid-template-columns: 1fr !important;
  }
  
  .tech-features .tech-feature {
    flex-direction: column !important;
    text-align: center;
    
    .tech-icon {
      margin: 0 auto 16px;
    }
  }
  
  .cta-title {
    font-size: 32px !important;
  }
  
  .cta-actions {
    flex-direction: column !important;
    align-items: center;
    
    .el-button {
      width: 100%;
      max-width: 300px;
    }
  }
  
  .product-hero {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
  }
  
  .product-actions {
    flex-direction: column !important;
    
    .el-button {
      width: 100%;
    }
  }
}
  

  .partners-logos {
    grid-template-columns: 1fr !important;
  }
  
  .scenarios-grid {
    grid-template-columns: 1fr !important;
  }
  
  .tech-features .tech-feature {
    flex-direction: column !important;
    text-align: center;
    
    .tech-icon {
      margin: 0 auto 16px;
    }
  }
  
  .cta-title {
    font-size: 32px !important;
  }
  
  .cta-actions {
    flex-direction: column !important;
    align-items: center;
    
    .el-button {
      width: 100%;
      max-width: 300px;
    }
  }
  
  .product-hero {
    grid-template-columns: 1fr !important;
    gap: 40px !important;
  }
  
  .product-actions {
    flex-direction: column !important;
    
    .el-button {
      width: 100%;
    }
  }

</style>
// 紧
凑布局的响应式设计
@media (max-width: 768px) {
  .partners-grid-compact {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 16px !important;
  }
  
  .scenarios-grid-compact {
    grid-template-columns: 1fr !important;
    gap: 20px !important;
  }
  
  .scenario-card-compact {
    .scenario-image-compact {
      height: 140px !important;
    }
    
    .scenario-info-compact {
      padding: 16px !important;
      
      h4 {
        font-size: 15px !important;
      }
      
      p {
        font-size: 13px !important;
      }
    }
  }
  
  .partner-card-compact {
    padding: 16px !important;
    
    .partner-logo-compact {
      width: 60px !important;
      height: 60px !important;
      margin-bottom: 12px !important;
    }
    
    .partner-content-compact {
      .partner-name-compact {
        font-size: 14px !important;
      }
      
      .partner-desc-compact {
        font-size: 12px !important;
      }
    }
  }
}

@media (max-width: 480px) {
  .partners-grid-compact {
    grid-template-columns: 1fr !important;
  }
}// 技术
优势新布局的响应式设计
@media (max-width: 768px) {
  .technology-section {
    .tech-features-grid {
      grid-template-columns: 1fr !important;
      gap: 20px !important;
      
      .tech-feature-card {
        padding: 24px !important;
        
        .tech-icon-card {
          width: 60px !important;
          height: 60px !important;
          font-size: 24px !important;
        }
        
        .tech-content-card {
          h4 {
            font-size: 18px !important;
          }
          
          p {
            font-size: 14px !important;
          }
        }
      }
    }
    
    .tech-image-top {
      margin-bottom: 40px !important;
      
      img {
        height: 300px !important;
      }
      
      .tech-overlay-top {
        padding: 30px !important;
        
        .tech-stats-top {
          flex-direction: column !important;
          gap: 16px !important;
          
          .tech-stat-top {
            padding: 16px !important;
            
            .stat-number {
              font-size: 24px !important;
            }
            
            .stat-label {
              font-size: 12px !important;
            }
          }
        }
      }
    }
  }
}// 
增强的产品展示样式
.product-showcase-enhanced {
  .product-visual {
    position: relative;
    
    .product-image-wrapper {
      position: relative;
      border-radius: 24px;
      overflow: hidden;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
      transition: all 0.4s ease;
      
      &:hover {
        transform: translateY(-10px);
        box-shadow: 0 30px 80px rgba(0, 0, 0, 0.2);
      }
      
      .product-main-image {
        width: 100%;
        height: 400px;
        object-fit: cover;
        display: block;
        transition: transform 0.4s ease;
      }
      
      &:hover .product-main-image {
        transform: scale(1.05);
      }
      
      .product-badge-enhanced {
        position: absolute;
        top: 20px;
        right: 20px;
        background: var(--primary-gradient);
        color: white;
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 700;
        box-shadow: 0 4px 15px rgba(255, 90, 95, 0.4);
        animation: pulse 2s infinite;
        
        &.badge-recommend {
          background: linear-gradient(135deg, #10b981, #059669);
          box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
        }
      }
      
      .image-glow {
        position: absolute;
        inset: -2px;
        border-radius: 24px;
        background: var(--primary-gradient);
        opacity: 0;
        transition: opacity 0.4s ease;
        z-index: -1;
        filter: blur(20px);
      }
      
      &:hover .image-glow {
        opacity: 0.3;
      }
    }
  }
  
  .product-details {
    .product-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;
      
      .product-title-section {
        .product-name-enhanced {
          font-size: 32px;
          font-weight: 800;
          color: var(--text-primary);
          margin-bottom: 8px;
          line-height: 1.2;
        }
        
        .product-model-enhanced {
          font-size: 16px;
          color: var(--text-secondary);
          font-weight: 600;
          background: var(--bg-secondary);
          padding: 4px 12px;
          border-radius: 12px;
          display: inline-block;
        }
      }
      
      .product-rating {
        text-align: right;
        
        .rating-stars {
          display: flex;
          gap: 2px;
          margin-bottom: 4px;
          
          .star {
            color: #FFD700;
            font-size: 18px;
          }
        }
        
        .rating-text {
          font-size: 14px;
          color: var(--text-secondary);
          font-weight: 500;
        }
      }
    }
    
    .product-desc-enhanced {
      font-size: 18px;
      color: var(--text-secondary);
      line-height: 1.7;
      margin-bottom: 32px;
    }
    
    .product-highlights-enhanced {
      margin-bottom: 32px;
      
      h4 {
        font-size: 20px;
        font-weight: 700;
        color: var(--text-primary);
        margin-bottom: 20px;
      }
      
      .highlights-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        
        .highlight-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 16px;
          background: white;
          border-radius: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;
          
          &:hover {
            transform: translateX(5px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
          }
          
          .highlight-icon {
            width: 24px;
            height: 24px;
            background: var(--primary-gradient);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 12px;
            flex-shrink: 0;
          }
          
          span {
            font-size: 15px;
            color: var(--text-primary);
            font-weight: 500;
          }
        }
      }
    }
    
    .product-features-enhanced {
      margin-bottom: 40px;
      
      h4 {
        font-size: 20px;
        font-weight: 700;
        color: var(--text-primary);
        margin-bottom: 20px;
      }
      
      .features-tags-enhanced {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        
        .feature-tag-enhanced {
          background: var(--primary-color);
          color: white;
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 14px;
          font-weight: 600;
          transition: all 0.3s ease;
          
          &:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 90, 95, 0.3);
          }
        }
      }
    }
    
    .product-purchase-section {
      .price-display {
        background: white;
        padding: 24px;
        border-radius: 16px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        margin-bottom: 24px;
        border: 2px solid var(--primary-color);
        
        .price-row {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 12px;
          
          .original-price-enhanced {
            font-size: 20px;
            color: var(--text-light);
            text-decoration: line-through;
          }
          
          .current-price-enhanced {
            font-size: 42px;
            font-weight: 800;
            color: var(--primary-color);
            text-shadow: 0 2px 4px rgba(255, 90, 95, 0.2);
          }
        }
        
        .discount-info {
          display: flex;
          gap: 12px;
          
          .discount-badge {
            background: #10B981;
            color: white;
            padding: 6px 12px;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
          }
          
          .discount-percent {
            background: var(--primary-color);
            color: white;
            padding: 6px 12px;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
          }
        }
      }
      
      .purchase-actions-enhanced {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;
        
        .el-button {
          flex: 1;
          min-width: 140px;
          padding: 16px 24px;
          font-size: 16px;
          font-weight: 600;
          border-radius: 12px;
          transition: all 0.3s ease;
          
          .el-icon {
            margin-right: 8px;
            font-size: 18px;
          }
        }
        
        .btn-purchase {
          background: var(--primary-gradient);
          border: none;
          color: white;
          
          &:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(255, 90, 95, 0.4);
          }
        }
        
        .btn-rental {
          background: #10B981;
          border: none;
          color: white;
          
          &:hover {
            background: #059669;
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
          }
        }
        
        .btn-consult {
          border: 2px solid var(--primary-color);
          color: var(--primary-color);
          background: transparent;
          
          &:hover {
            background: var(--primary-color);
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(255, 90, 95, 0.3);
          }
        }
      }
    }
  }
}
