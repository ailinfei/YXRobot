<template>
  <div class="news-detail-page">
    <!-- 新闻头部 -->
    <section class="news-header">
      <div class="section-container">
        <div class="header-content">
          <!-- 返回按钮 -->
          <div class="back-navigation">
            <el-button @click="goBack" :icon="ArrowLeft" text>
              返回新闻列表
            </el-button>
          </div>
          
          <!-- 新闻标题和元信息 -->
          <div class="news-meta-info">
            <div class="news-category-badge" :class="getBadgeClass(newsDetail.category)">
              {{ newsDetail.category }}
            </div>
            <h1 class="news-title">{{ newsDetail.title }}</h1>
            <div class="news-meta">
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDate(newsDetail.date) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ newsDetail.author }}</span>
              </div>
              <div class="meta-item">
                <el-icon><View /></el-icon>
                <span>{{ newsDetail.views }} 次阅读</span>
              </div>
              <div class="meta-item">
                <el-icon><ChatRound /></el-icon>
                <span>{{ newsDetail.comments }} 条评论</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 新闻内容 -->
    <section class="news-content">
      <div class="section-container">
        <div class="content-layout">
          <!-- 主要内容 -->
          <article class="main-content">
            <!-- 新闻图片 -->
            <div class="news-image">
              <img :src="newsDetail.image" :alt="newsDetail.title" />
            </div>
            
            <!-- 新闻正文 -->
            <div class="news-body" v-html="newsDetail.content"></div>
            
            <!-- 标签 -->
            <div class="news-tags" v-if="newsDetail.tags">
              <span class="tags-label">标签：</span>
              <el-tag 
                v-for="tag in newsDetail.tags" 
                :key="tag"
                class="news-tag"
                type="primary"
              >
                {{ tag }}
              </el-tag>
            </div>
            
            <!-- 分享和操作 -->
            <div class="news-actions">
              <div class="action-buttons">
                <el-button @click="toggleLike" :type="isLiked ? 'primary' : 'default'">
                  <el-icon><Star /></el-icon>
                  {{ isLiked ? '已点赞' : '点赞' }} ({{ newsDetail.likes }})
                </el-button>
                <el-button @click="shareNews">
                  <el-icon><Share /></el-icon>
                  分享
                </el-button>
                <el-button @click="collectNews">
                  <el-icon><Folder /></el-icon>
                  收藏
                </el-button>
              </div>
            </div>
          </article>
          
          <!-- 侧边栏 -->
          <aside class="sidebar">
            <!-- 相关新闻 -->
            <div class="sidebar-section">
              <h3 class="sidebar-title">相关新闻</h3>
              <div class="related-news">
                <div 
                  v-for="news in relatedNews" 
                  :key="news.id"
                  class="related-item"
                  @click="goToNews(news.id)"
                >
                  <img :src="news.image" :alt="news.title" />
                  <div class="related-content">
                    <h4>{{ news.title }}</h4>
                    <span class="related-date">{{ formatDate(news.date) }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 热门新闻 -->
            <div class="sidebar-section">
              <h3 class="sidebar-title">热门新闻</h3>
              <div class="hot-news">
                <div 
                  v-for="(news, index) in hotNews" 
                  :key="news.id"
                  class="hot-item"
                  @click="goToNews(news.id)"
                >
                  <span class="hot-rank">{{ index + 1 }}</span>
                  <div class="hot-content">
                    <h4>{{ news.title }}</h4>
                    <div class="hot-stats">
                      <span><el-icon><View /></el-icon> {{ news.views }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>

    <!-- 评论区域 -->
    <section class="comments-section">
      <div class="section-container">
        <div class="comments-container">
          <h3 class="comments-title">评论 ({{ comments.length }})</h3>
          
          <!-- 发表评论 -->
          <div class="comment-form">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="4"
              placeholder="写下您的评论..."
              class="comment-input"
            />
            <div class="comment-actions">
              <el-button type="primary" @click="submitComment">
                发表评论
              </el-button>
            </div>
          </div>
          
          <!-- 评论列表 -->
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar :src="comment.avatar" :size="40" />
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-date">{{ formatDate(comment.date) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button text size="small" @click="replyComment(comment.id)">
                    回复
                  </el-button>
                  <el-button text size="small" @click="likeComment(comment.id)">
                    <el-icon><Star /></el-icon>
                    {{ comment.likes }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Calendar,
  User,
  View,
  ChatRound,
  Star,
  Share,
  Folder
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const isLiked = ref(false)
const newComment = ref('')

// 新闻详情数据
const newsDetail = ref({
  id: 1,
  title: '艺学练字机器人荣获"2024年度教育科技创新奖"',
  category: '公司新闻',
  date: '2024-12-15',
  author: '新闻编辑部',
  views: 1520,
  comments: 45,
  likes: 128,
  image: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800&h=500&fit=crop',
  tags: ['教育科技', '创新奖', '人工智能', '书法教育'],
  content: `
    <p>在刚刚结束的全国教育科技创新大会上，我们的AI练字机器人凭借其卓越的技术创新和教育应用效果，荣获"2024年度教育科技创新奖"。这一荣誉不仅是对我们技术实力的认可，更是对我们致力于传承中华书法文化使命的肯定。</p>
    
    <h3>技术创新引领行业发展</h3>
    <p>我们的AI练字机器人采用了最先进的计算机视觉技术和深度学习算法，能够实时识别用户的书写动作，提供精准的笔画指导和姿势纠正。经过不断的技术迭代和优化，目前系统的识别准确率已达到99.5%，响应时间缩短至0.1秒以内。</p>
    
    <h3>教育应用成果显著</h3>
    <p>自产品推出以来，我们已与全国500多所学校建立合作关系，为超过10万名学生提供了智能化的书法学习服务。通过AI技术的辅助，学生的书写水平平均提升了40%，学习兴趣和积极性也得到了显著提高。</p>
    
    <h3>公益理念传承文化</h3>
    <p>我们始终坚持"每售出一台机器人，捐赠一节书法课"的公益理念，已为全国1200多个教学点提供了免费的书法课程，让更多偏远地区的孩子也能感受到书法艺术的魅力。</p>
    
    <h3>未来发展展望</h3>
    <p>获得这一殊荣后，我们将继续加大技术研发投入，不断完善产品功能，为用户提供更加优质的学习体验。同时，我们也将扩大公益项目的覆盖范围，让AI技术更好地服务于书法教育事业的发展。</p>
  `
})

// 相关新闻
const relatedNews = ref([
  {
    id: 2,
    title: '新版AI算法上线，识别准确率提升至99.5%',
    date: '2024-12-10',
    image: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=200&h=120&fit=crop'
  },
  {
    id: 3,
    title: '与北京师范大学达成战略合作协议',
    date: '2024-12-05',
    image: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=200&h=120&fit=crop'
  },
  {
    id: 6,
    title: '练字机器人2.0版本正式发布',
    date: '2024-11-15',
    image: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=200&h=120&fit=crop'
  }
])

// 热门新闻
const hotNews = ref([
  {
    id: 5,
    title: '公益项目"书法进乡村"惠及千名学生',
    views: 2100
  },
  {
    id: 6,
    title: '练字机器人2.0版本正式发布',
    views: 1876
  },
  {
    id: 1,
    title: '艺学练字机器人荣获"2024年度教育科技创新奖"',
    views: 1520
  },
  {
    id: 7,
    title: 'AI书法教学系统获得国家专利认证',
    views: 1456
  }
])

// 评论数据
const comments = ref([
  {
    id: 1,
    author: '书法爱好者',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
    date: '2024-12-16',
    content: '恭喜获奖！这个产品确实很棒，我家孩子用了之后书写水平提升很快。',
    likes: 12
  },
  {
    id: 2,
    author: '教育工作者',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&h=100&fit=crop&crop=face',
    date: '2024-12-16',
    content: '作为一名语文老师，我觉得这种AI辅助教学的方式很有前景，希望能在更多学校推广。',
    likes: 8
  },
  {
    id: 3,
    author: '技术专家',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
    date: '2024-12-15',
    content: '技术创新值得肯定，99.5%的识别准确率确实很厉害！',
    likes: 15
  }
])

// 方法
const goBack = () => {
  router.push('/website/news')
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

const toggleLike = () => {
  isLiked.value = !isLiked.value
  if (isLiked.value) {
    newsDetail.value.likes++
    ElMessage.success('点赞成功')
  } else {
    newsDetail.value.likes--
  }
}

const shareNews = () => {
  // 这里可以实现分享功能
  ElMessage.success('分享链接已复制到剪贴板')
}

const collectNews = () => {
  ElMessage.success('收藏成功')
}

const goToNews = (newsId: number) => {
  router.push(`/website/news/${newsId}`)
}

const submitComment = () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  const comment = {
    id: comments.value.length + 1,
    author: '匿名用户',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop&crop=face',
    date: new Date().toISOString().split('T')[0],
    content: newComment.value,
    likes: 0
  }
  
  comments.value.unshift(comment)
  newComment.value = ''
  newsDetail.value.comments++
  ElMessage.success('评论发表成功')
}

const replyComment = (commentId: number) => {
  ElMessage.info('回复功能开发中...')
}

const likeComment = (commentId: number) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likes++
    ElMessage.success('点赞成功')
  }
}

onMounted(() => {
  // 根据路由参数加载对应的新闻详情
  const newsId = route.params.id
  console.log('加载新闻详情:', newsId)
  
  // 这里可以根据newsId从API获取新闻详情
  // 目前使用模拟数据
})
</script>

<style lang="scss" scoped>
.news-detail-page {
  .section-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }
}

// 新闻头部
.news-header {
  background: white;
  padding: 40px 0;
  border-bottom: 1px solid var(--border-color);
  
  .back-navigation {
    margin-bottom: 24px;
  }
  
  .news-meta-info {
    .news-category-badge {
      display: inline-block;
      padding: 6px 16px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      color: white;
      margin-bottom: 16px;
      
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
    
    .news-title {
      font-size: 36px;
      font-weight: 700;
      color: var(--text-primary);
      line-height: 1.3;
      margin-bottom: 20px;
    }
    
    .news-meta {
      display: flex;
      gap: 24px;
      flex-wrap: wrap;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: var(--text-secondary);
        font-size: 14px;
        
        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
}

// 新闻内容
.news-content {
  padding: 40px 0;
  background: var(--bg-secondary);
  
  .content-layout {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
    
    .main-content {
      background: white;
      border-radius: 12px;
      padding: 40px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      
      .news-image {
        margin-bottom: 32px;
        
        img {
          width: 100%;
          height: 400px;
          object-fit: cover;
          border-radius: 8px;
        }
      }
      
      .news-body {
        line-height: 1.8;
        color: var(--text-primary);
        
        :deep(h3) {
          font-size: 20px;
          font-weight: 600;
          margin: 32px 0 16px 0;
          color: var(--text-primary);
        }
        
        :deep(p) {
          margin-bottom: 16px;
          text-align: justify;
        }
      }
      
      .news-tags {
        margin: 32px 0;
        padding: 20px 0;
        border-top: 1px solid var(--border-color);
        
        .tags-label {
          color: var(--text-secondary);
          margin-right: 12px;
        }
        
        .news-tag {
          margin-right: 8px;
        }
      }
      
      .news-actions {
        padding: 20px 0;
        border-top: 1px solid var(--border-color);
        
        .action-buttons {
          display: flex;
          gap: 12px;
        }
      }
    }
    
    .sidebar {
      .sidebar-section {
        background: white;
        border-radius: 12px;
        padding: 24px;
        margin-bottom: 24px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        
        .sidebar-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 20px;
          padding-bottom: 12px;
          border-bottom: 2px solid var(--primary-color);
        }
      }
      
      .related-news {
        .related-item {
          display: flex;
          gap: 12px;
          margin-bottom: 16px;
          cursor: pointer;
          padding: 12px;
          border-radius: 8px;
          transition: background-color 0.2s ease;
          
          &:hover {
            background: var(--bg-tertiary);
          }
          
          img {
            width: 80px;
            height: 60px;
            object-fit: cover;
            border-radius: 6px;
            flex-shrink: 0;
          }
          
          .related-content {
            flex: 1;
            
            h4 {
              font-size: 14px;
              font-weight: 500;
              color: var(--text-primary);
              line-height: 1.4;
              margin-bottom: 8px;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
            
            .related-date {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
      }
      
      .hot-news {
        .hot-item {
          display: flex;
          gap: 12px;
          margin-bottom: 16px;
          cursor: pointer;
          padding: 12px;
          border-radius: 8px;
          transition: background-color 0.2s ease;
          
          &:hover {
            background: var(--bg-tertiary);
          }
          
          .hot-rank {
            width: 24px;
            height: 24px;
            background: var(--primary-color);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: 600;
            flex-shrink: 0;
          }
          
          .hot-content {
            flex: 1;
            
            h4 {
              font-size: 14px;
              font-weight: 500;
              color: var(--text-primary);
              line-height: 1.4;
              margin-bottom: 8px;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
            
            .hot-stats {
              font-size: 12px;
              color: var(--text-secondary);
              
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
}

// 评论区域
.comments-section {
  padding: 60px 0;
  background: white;
  
  .comments-container {
    max-width: 800px;
    
    .comments-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 32px;
    }
    
    .comment-form {
      margin-bottom: 40px;
      
      .comment-input {
        margin-bottom: 16px;
      }
      
      .comment-actions {
        display: flex;
        justify-content: flex-end;
      }
    }
    
    .comments-list {
      .comment-item {
        display: flex;
        gap: 16px;
        margin-bottom: 24px;
        padding-bottom: 24px;
        border-bottom: 1px solid var(--border-color);
        
        &:last-child {
          border-bottom: none;
        }
        
        .comment-avatar {
          flex-shrink: 0;
        }
        
        .comment-content {
          flex: 1;
          
          .comment-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            
            .comment-author {
              font-weight: 600;
              color: var(--text-primary);
            }
            
            .comment-date {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
          
          .comment-text {
            color: var(--text-primary);
            line-height: 1.6;
            margin-bottom: 12px;
          }
          
          .comment-actions {
            display: flex;
            gap: 16px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .news-content {
    .content-layout {
      grid-template-columns: 1fr;
      
      .sidebar {
        order: -1;
        
        .sidebar-section {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
          gap: 20px;
          
          .sidebar-title {
            grid-column: 1 / -1;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .news-header {
    padding: 24px 0;
    
    .news-meta-info {
      .news-title {
        font-size: 28px;
      }
      
      .news-meta {
        gap: 16px;
      }
    }
  }
  
  .news-content {
    padding: 24px 0;
    
    .content-layout {
      .main-content {
        padding: 24px;
        
        .news-image {
          img {
            height: 250px;
          }
        }
      }
    }
  }
  
  .comments-section {
    padding: 40px 0;
  }
}
</style>