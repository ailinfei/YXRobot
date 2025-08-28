<template>
  <div class="news-page">
    <!-- 页面头部 -->
    <section class="news-hero">
      <div class="section-container">
        <div class="hero-content">
          <h1 class="hero-title">在线新闻</h1>
          <p class="hero-subtitle">了解练字机器人的最新动态、技术更新和行业资讯</p>
        </div>
      </div>
    </section>

    <!-- 新闻分类导航 -->
    <section class="news-categories">
      <div class="section-container">
        <div class="category-tabs">
          <button 
            v-for="category in categories" 
            :key="category.id"
            class="category-tab"
            :class="{ active: activeCategory === category.id }"
            @click="setActiveCategory(category.id)"
          >
            {{ category.name }}
          </button>
        </div>
      </div>
    </section>

    <!-- 新闻列表 -->
    <section class="news-content">
      <div class="section-container">
        <div class="news-grid">
          <!-- 特色新闻 -->
          <div class="featured-news" v-if="featuredNews">
            <div class="news-card featured">
              <div class="news-image">
                <img :src="featuredNews.image" :alt="featuredNews.title" />
                <div class="news-badge featured-badge">重要</div>
              </div>
              <div class="news-content-area">
                <div class="news-meta">
                  <span class="news-category">{{ featuredNews.category }}</span>
                  <span class="news-date">{{ formatDate(featuredNews.date) }}</span>
                </div>
                <h2 class="news-title">{{ featuredNews.title }}</h2>
                <p class="news-excerpt">{{ featuredNews.excerpt }}</p>
                <div class="news-actions">
                  <el-button type="primary" @click="goToDetail(featuredNews.id)">
                    阅读全文
                  </el-button>
                  <div class="news-stats">
                    <span><el-icon><View /></el-icon> {{ featuredNews.views }}</span>
                    <span><el-icon><ChatRound /></el-icon> {{ featuredNews.comments }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 新闻列表 -->
          <div class="news-list">
            <div 
              v-for="news in filteredNews" 
              :key="news.id"
              class="news-card"
              @click="goToDetail(news.id)"
            >
              <div class="news-image">
                <img :src="news.image" :alt="news.title" />
                <div class="news-badge" :class="getBadgeClass(news.category)">
                  {{ news.category }}
                </div>
              </div>
              <div class="news-content-area">
                <div class="news-meta">
                  <span class="news-date">{{ formatDate(news.date) }}</span>
                  <span class="news-author">{{ news.author }}</span>
                </div>
                <h3 class="news-title">{{ news.title }}</h3>
                <p class="news-excerpt">{{ news.excerpt }}</p>
                <div class="news-stats">
                  <span><el-icon><View /></el-icon> {{ news.views }}</span>
                  <span><el-icon><ChatRound /></el-icon> {{ news.comments }}</span>
                  <span><el-icon><Star /></el-icon> {{ news.likes }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[6, 12, 18, 24]"
            :total="totalNews"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </section>

    <!-- 订阅新闻 -->
    <section class="news-subscribe">
      <div class="section-container">
        <div class="subscribe-content">
          <div class="subscribe-text">
            <h3>订阅我们的新闻</h3>
            <p>第一时间获取练字机器人的最新动态和技术资讯</p>
          </div>
          <div class="subscribe-form">
            <el-input
              v-model="subscribeEmail"
              placeholder="请输入您的邮箱地址"
              class="subscribe-input"
            >
              <template #append>
                <el-button type="primary" @click="handleSubscribe">
                  订阅
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  View,
  ChatRound,
  Star
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const activeCategory = ref('all')
const currentPage = ref(1)
const pageSize = ref(6)
const subscribeEmail = ref('')

// 新闻分类
const categories = ref([
  { id: 'all', name: '全部' },
  { id: 'company', name: '公司新闻' },
  { id: 'technology', name: '技术更新' },
  { id: 'cooperation', name: '合作动态' },
  { id: 'industry', name: '行业资讯' },
  { id: 'charity', name: '公益活动' }
])

// 特色新闻
const featuredNews = ref({
  id: 1,
  title: '艺学练字机器人荣获"2024年度教育科技创新奖"',
  excerpt: '在刚刚结束的全国教育科技创新大会上，我们的AI练字机器人凭借其卓越的技术创新和教育应用效果，荣获"2024年度教育科技创新奖"。这一荣誉不仅是对我们技术实力的认可，更是对我们致力于传承中华书法文化使命的肯定。',
  category: '公司新闻',
  date: '2024-12-15',
  image: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600&h=400&fit=crop',
  views: 1520,
  comments: 45,
  likes: 128,
  author: '新闻编辑部'
})

// 新闻列表
const newsList = ref([
  {
    id: 2,
    title: '新版AI算法上线，识别准确率提升至99.5%',
    excerpt: '经过半年的技术攻关，我们的新一代AI识别算法正式上线，笔画识别准确率从99.2%提升至99.5%，响应速度提升30%，为用户带来更加精准和流畅的练字体验。',
    category: '技术更新',
    date: '2024-12-10',
    image: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=400&h=300&fit=crop',
    views: 890,
    comments: 23,
    likes: 67,
    author: '技术团队'
  },
  {
    id: 3,
    title: '与北京师范大学达成战略合作协议',
    excerpt: '双方将在书法教育研究、AI技术应用、师资培训等方面开展深度合作，共同推动书法教育的数字化转型和创新发展。',
    category: '合作动态',
    date: '2024-12-05',
    image: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400&h=300&fit=crop',
    views: 756,
    comments: 18,
    likes: 89,
    author: '商务合作部'
  },
  {
    id: 4,
    title: '全国中小学书法教育论坛成功举办',
    excerpt: '来自全国各地的300多位书法教育专家和一线教师参加了本次论坛，共同探讨AI技术在书法教育中的应用前景和发展方向。',
    category: '行业资讯',
    date: '2024-11-28',
    image: 'https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=400&h=300&fit=crop',
    views: 1234,
    comments: 56,
    likes: 145,
    author: '行业观察'
  },
  {
    id: 5,
    title: '公益项目"书法进乡村"惠及千名学生',
    excerpt: '我们的公益项目已为偏远地区的1000多名学生提供了免费的书法学习机会，通过AI练字机器人让更多孩子感受到书法艺术的魅力。',
    category: '公益活动',
    date: '2024-11-20',
    image: 'https://images.unsplash.com/photo-1497486751825-1233686d5d80?w=400&h=300&fit=crop',
    views: 2100,
    comments: 78,
    likes: 234,
    author: '公益项目组'
  },
  {
    id: 6,
    title: '练字机器人2.0版本正式发布',
    excerpt: '全新的2.0版本在硬件性能、软件功能、用户体验等方面都有显著提升，新增了多种字体风格和个性化学习模式。',
    category: '公司新闻',
    date: '2024-11-15',
    image: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=400&h=300&fit=crop',
    views: 1876,
    comments: 92,
    likes: 187,
    author: '产品团队'
  },
  {
    id: 7,
    title: 'AI书法教学系统获得国家专利认证',
    excerpt: '我们自主研发的AI书法教学系统正式获得国家知识产权局颁发的发明专利证书，标志着我们在技术创新方面的重要突破。',
    category: '技术更新',
    date: '2024-11-08',
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=300&fit=crop',
    views: 1456,
    comments: 34,
    likes: 98,
    author: '法务部'
  },
  {
    id: 8,
    title: '国际书法文化交流大会圆满落幕',
    excerpt: '来自20多个国家和地区的书法爱好者齐聚一堂，共同体验AI练字机器人带来的创新书法学习方式，促进了国际文化交流。',
    category: '行业资讯',
    date: '2024-10-30',
    image: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=400&h=300&fit=crop',
    views: 987,
    comments: 41,
    likes: 156,
    author: '国际事务部'
  }
])

// 计算属性
const filteredNews = computed(() => {
  if (activeCategory.value === 'all') {
    return newsList.value
  }
  return newsList.value.filter(news => {
    const categoryMap: Record<string, string> = {
      'company': '公司新闻',
      'technology': '技术更新',
      'cooperation': '合作动态',
      'industry': '行业资讯',
      'charity': '公益活动'
    }
    return news.category === categoryMap[activeCategory.value]
  })
})

const totalNews = computed(() => filteredNews.value.length)

// 方法
const setActiveCategory = (categoryId: string) => {
  activeCategory.value = categoryId
  currentPage.value = 1
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getBadgeClass = (category: string) => {
  const classMap: Record<string, string> = {
    '公司新闻': 'badge-company',
    '技术更新': 'badge-technology',
    '合作动态': 'badge-cooperation',
    '行业资讯': 'badge-industry',
    '公益活动': 'badge-charity'
  }
  return classMap[category] || 'badge-default'
}

const goToDetail = (newsId: number) => {
  router.push(`/website/news/${newsId}`)
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSubscribe = () => {
  if (!subscribeEmail.value) {
    ElMessage.warning('请输入邮箱地址')
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(subscribeEmail.value)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }
  
  ElMessage.success('订阅成功！我们会将最新资讯发送到您的邮箱')
  subscribeEmail.value = ''
}

onMounted(() => {
  // 页面加载时的初始化操作
})
</script>

<style lang="scss" scoped>
.news-page {
  .section-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }
}

// 页面头部
.news-hero {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
  
  .hero-title {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 16px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  }
  
  .hero-subtitle {
    font-size: 18px;
    opacity: 0.9;
    max-width: 600px;
    margin: 0 auto;
    line-height: 1.6;
  }
}

// 分类导航
.news-categories {
  background: white;
  padding: 30px 0;
  border-bottom: 1px solid var(--border-color);
  
  .category-tabs {
    display: flex;
    justify-content: center;
    gap: 8px;
    flex-wrap: wrap;
    
    .category-tab {
      padding: 10px 20px;
      border: 2px solid var(--border-color);
      background: transparent;
      color: var(--text-secondary);
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
      
      &:hover {
        border-color: var(--primary-color);
        color: var(--primary-color);
      }
      
      &.active {
        background: var(--primary-color);
        border-color: var(--primary-color);
        color: white;
      }
    }
  }
}

// 新闻内容
.news-content {
  padding: 60px 0;
  background: var(--bg-secondary);
  
  .news-grid {
    display: grid;
    gap: 40px;
    
    .featured-news {
      .news-card.featured {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 40px;
        background: white;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
        
        .news-image {
          position: relative;
          
          img {
            width: 100%;
            height: 300px;
            object-fit: cover;
          }
          
          .featured-badge {
            position: absolute;
            top: 20px;
            left: 20px;
            background: var(--primary-color);
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
          }
        }
        
        .news-content-area {
          padding: 40px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          
          .news-meta {
            display: flex;
            gap: 16px;
            margin-bottom: 16px;
            
            .news-category {
              background: var(--primary-color);
              color: white;
              padding: 4px 12px;
              border-radius: 12px;
              font-size: 12px;
              font-weight: 600;
            }
            
            .news-date {
              color: var(--text-secondary);
              font-size: 14px;
            }
          }
          
          .news-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 16px;
            line-height: 1.3;
          }
          
          .news-excerpt {
            color: var(--text-secondary);
            line-height: 1.6;
            margin-bottom: 24px;
            flex: 1;
          }
          
          .news-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            .news-stats {
              display: flex;
              gap: 16px;
              color: var(--text-light);
              font-size: 14px;
              
              span {
                display: flex;
                align-items: center;
                gap: 4px;
              }
            }
          }
        }
      }
    }
    
    .news-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
      gap: 30px;
      
      .news-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        transition: all 0.3s ease;
        cursor: pointer;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
        }
        
        .news-image {
          position: relative;
          
          img {
            width: 100%;
            height: 200px;
            object-fit: cover;
          }
          
          .news-badge {
            position: absolute;
            top: 12px;
            right: 12px;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
            color: white;
            
            &.badge-company {
              background: var(--primary-color);
            }
            
            &.badge-technology {
              background: #3b82f6;
            }
            
            &.badge-cooperation {
              background: #10b981;
            }
            
            &.badge-industry {
              background: #f59e0b;
            }
            
            &.badge-charity {
              background: #8b5cf6;
            }
          }
        }
        
        .news-content-area {
          padding: 24px;
          
          .news-meta {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 12px;
            color: var(--text-secondary);
          }
          
          .news-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 12px;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .news-excerpt {
            color: var(--text-secondary);
            font-size: 14px;
            line-height: 1.5;
            margin-bottom: 16px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .news-stats {
            display: flex;
            gap: 16px;
            color: var(--text-light);
            font-size: 12px;
            
            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

// 订阅新闻
.news-subscribe {
  background: var(--primary-gradient);
  color: white;
  padding: 60px 0;
  
  .subscribe-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 40px;
    
    .subscribe-text {
      h3 {
        font-size: 24px;
        font-weight: 700;
        margin-bottom: 8px;
      }
      
      p {
        opacity: 0.9;
        font-size: 16px;
      }
    }
    
    .subscribe-form {
      .subscribe-input {
        width: 400px;
        
        :deep(.el-input__wrapper) {
          border-radius: 25px;
        }
        
        :deep(.el-input-group__append) {
          border-radius: 0 25px 25px 0;
          
          .el-button {
            border-radius: 0 25px 25px 0;
            padding: 0 24px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .news-content {
    .news-grid {
      .featured-news {
        .news-card.featured {
          grid-template-columns: 1fr;
          
          .news-content-area {
            padding: 30px;
          }
        }
      }
      
      .news-list {
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      }
    }
  }
  
  .news-subscribe {
    .subscribe-content {
      flex-direction: column;
      text-align: center;
      
      .subscribe-form {
        .subscribe-input {
          width: 100%;
          max-width: 400px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .news-hero {
    padding: 60px 0;
    
    .hero-title {
      font-size: 36px;
    }
    
    .hero-subtitle {
      font-size: 16px;
    }
  }
  
  .news-categories {
    .category-tabs {
      .category-tab {
        padding: 8px 16px;
        font-size: 14px;
      }
    }
  }
  
  .news-content {
    padding: 40px 0;
    
    .news-grid {
      .news-list {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>