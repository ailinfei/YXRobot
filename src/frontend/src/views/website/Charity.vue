<template>
  <div class="charity-page">
    <!-- 页面头部 -->
    <section class="page-hero">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">公益项目</h1>
          <p class="hero-subtitle">每售出一台机器人，我们就向公益机构捐赠一节书法课</p>
          <div class="hero-stats">
            <div class="stat-card">
              <div class="stat-number">{{ charityStats.totalDonatedCourses.toLocaleString() }}</div>
              <div class="stat-label">已捐赠课程</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ charityStats.totalBeneficiaries.toLocaleString() }}</div>
              <div class="stat-label">受益人数</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ charityStats.totalInstitutions }}</div>
              <div class="stat-label">合作机构</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 公益理念 -->
    <section class="concept-section">
      <div class="section-container">
        <div class="concept-content">
          <div class="concept-text">
            <h2 class="concept-title">我们的公益理念</h2>
            <p class="concept-description">
              书法是中华文化的瑰宝，我们相信每个人都应该有机会学习和传承这门艺术。
              通过"一台一课"公益计划，我们致力于让更多的孩子和老人能够接触到专业的书法教育，
              感受传统文化的魅力，培养文化自信。
            </p>
            <div class="concept-features">
              <div class="feature-item">
                <el-icon class="feature-icon"><StarFilled /></el-icon>
                <div class="feature-content">
                  <h3>传承文化</h3>
                  <p>弘扬中华传统书法艺术</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon"><Avatar /></el-icon>
                <div class="feature-content">
                  <h3>惠及大众</h3>
                  <p>让更多人享受书法教育</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon"><Star /></el-icon>
                <div class="feature-content">
                  <h3>品质保证</h3>
                  <p>提供专业的教学服务</p>
                </div>
              </div>
            </div>
          </div>
          <div class="concept-image">
            <img src="https://picsum.photos/500/400?random=51" alt="公益理念" />
          </div>
        </div>
      </div>
    </section>

    <!-- 统计数据可视化 -->
    <section class="statistics-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">公益成果展示</h2>
          <p class="section-subtitle">用数据见证我们的公益足迹</p>
        </div>
        
        <div class="stats-grid">
          <div class="stats-card">
            <div class="stats-icon schools">
              <el-icon><School /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ charityStats.schoolsCount }}</div>
              <div class="stats-label">合作学校</div>
              <div class="stats-description">覆盖全国各地中小学</div>
            </div>
          </div>
          
          <div class="stats-card">
            <div class="stats-icon communities">
              <el-icon><House /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ charityStats.communityCentersCount }}</div>
              <div class="stats-label">社区中心</div>
              <div class="stats-description">服务社区居民书法学习</div>
            </div>
          </div>
          
          <div class="stats-card">
            <div class="stats-icon care">
              <el-icon><StarFilled /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ charityStats.careCentersCount }}</div>
              <div class="stats-label">福利院</div>
              <div class="stats-description">关爱特殊群体文化需求</div>
            </div>
          </div>
          
          <div class="stats-card">
            <div class="stats-icon elderly">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ charityStats.elderlyUniversitiesCount }}</div>
              <div class="stats-label">老年大学</div>
              <div class="stats-description">丰富老年人精神生活</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 教学点地图展示 -->
    <section class="map-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">教学点分布</h2>
          <p class="section-subtitle">我们的公益足迹遍布全国</p>
        </div>
        
        <div class="map-container">
          <div class="map-placeholder">
            <img src="https://picsum.photos/800/500?random=52" alt="教学点分布地图" />
            <div class="map-overlay">
              <div class="map-legend">
                <div class="legend-item">
                  <div class="legend-dot schools"></div>
                  <span>学校</span>
                </div>
                <div class="legend-item">
                  <div class="legend-dot communities"></div>
                  <span>社区中心</span>
                </div>
                <div class="legend-item">
                  <div class="legend-dot care"></div>
                  <span>福利院</span>
                </div>
                <div class="legend-item">
                  <div class="legend-dot elderly"></div>
                  <span>老年大学</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="locations-grid">
          <div class="location-card" v-for="location in charityLocations" :key="location.id">
            <div class="location-image">
              <img :src="location.imageUrls[0]" :alt="location.name" />
              <div class="location-type" :class="location.locationType">
                {{ getLocationTypeName(location.locationType) }}
              </div>
              <div class="established-date">
                成立于 {{ formatDate(location.establishedDate) }}
              </div>
            </div>
            <div class="location-content">
              <div class="location-header">
                <h3 class="location-name">{{ location.name }}</h3>
                <div class="location-status" :class="{ active: location.isActive }">
                  {{ location.isActive ? '运营中' : '筹备中' }}
                </div>
              </div>
              
              <p class="location-address">{{ location.address }}</p>
              <p class="location-description">{{ location.description }}</p>
              
              <div class="location-stats-grid">
                <div class="stat-item">
                  <span class="stat-label">受益人数</span>
                  <span class="stat-value">{{ location.beneficiaryCount }}人</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">课程数量</span>
                  <span class="stat-value">{{ location.coursesCount }}节</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">师资力量</span>
                  <span class="stat-value">{{ location.teacherCount }}名</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">班级数量</span>
                  <span class="stat-value">{{ location.classCount }}个</span>
                </div>
              </div>
              
              <div class="location-achievements" v-if="location.achievements && location.achievements.length > 0">
                <div class="achievements-label">荣誉成就：</div>
                <div class="achievements-list">
                  <span v-for="achievement in location.achievements" :key="achievement" class="achievement-badge">
                    {{ achievement }}
                  </span>
                </div>
              </div>
              
              <div class="location-programs" v-if="location.specialPrograms && location.specialPrograms.length > 0">
                <div class="programs-label">特色项目：</div>
                <div class="programs-list">
                  <span v-for="program in location.specialPrograms" :key="program" class="program-tag">
                    {{ program }}
                  </span>
                </div>
              </div>
              
              <div class="location-footer">
                <div class="location-contact">
                  <span>联系人：{{ location.contactPerson }}</span>
                  <span class="weekly-hours">每周 {{ location.weeklyHours }} 课时</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section> 

    <!-- 学员作品展示 -->
    <section class="works-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">学员作品展示</h2>
          <p class="section-subtitle">见证每一份进步与成长</p>
        </div>
        
        <div class="works-gallery">
          <div class="work-card" v-for="work in studentWorks" :key="work.id">
            <div class="work-image" @click="viewWork(work)">
              <img :src="work.workImageUrl" :alt="work.workTitle" />
              <div class="work-overlay">
                <el-icon class="view-icon"><View /></el-icon>
                <div class="work-style-badge">{{ work.style }}</div>
              </div>
              <div class="improvement-score">
                <span class="score-label">进步指数</span>
                <span class="score-value">{{ work.improvementScore }}%</span>
              </div>
            </div>
            <div class="work-info">
              <div class="work-header">
                <h3 class="work-title">{{ work.workTitle }}</h3>
                <div class="work-grade">{{ work.grade }}</div>
              </div>
              <p class="work-description">{{ work.workDescription }}</p>
              
              <div class="student-info">
                <div class="student-basic">
                  <span class="student-name">{{ work.studentName }}</span>
                  <span class="student-age">{{ work.studentAge }}岁</span>
                  <span class="student-school">{{ work.school }}</span>
                </div>
                <div class="practice-time">练习时长：{{ work.practiceTime }}</div>
              </div>
              
              <div class="work-achievements" v-if="work.awards && work.awards.length > 0">
                <div class="achievement-label">获奖情况：</div>
                <div class="awards-list">
                  <span v-for="award in work.awards" :key="award" class="award-badge">
                    {{ award }}
                  </span>
                </div>
              </div>
              
              <div class="teacher-comment" v-if="work.teacherComment">
                <div class="comment-label">教师点评：</div>
                <p class="comment-text">"{{ work.teacherComment }}"</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 志愿者活动 -->
    <section class="volunteer-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">志愿者活动</h2>
          <p class="section-subtitle">携手传递书法之美</p>
        </div>
        
        <div class="volunteer-grid">
          <div class="volunteer-card" v-for="activity in volunteerActivitiesData" :key="activity.id">
            <div class="volunteer-image">
              <img :src="activity.imageUrl" :alt="activity.title" />
              <div class="activity-type-badge" :class="activity.type">
                {{ getActivityTypeName(activity.type) }}
              </div>
              <div class="activity-status" :class="activity.status">
                {{ activity.status === 'completed' ? '已完成' : '进行中' }}
              </div>
            </div>
            <div class="volunteer-content">
              <div class="volunteer-header">
                <h3 class="volunteer-title">{{ activity.title }}</h3>
                <div class="participant-count">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.participants }}人参与</span>
                </div>
              </div>
              
              <p class="volunteer-description">{{ activity.description }}</p>
              
              <div class="activity-details">
                <div class="detail-row">
                  <span class="detail-label">活动时间：</span>
                  <span class="detail-value">{{ formatActivityDate(activity.date) }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">活动地点：</span>
                  <span class="detail-value">{{ activity.location }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">活动时长：</span>
                  <span class="detail-value">{{ activity.duration }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">志愿者：</span>
                  <span class="detail-value">{{ activity.volunteers }}名</span>
                </div>
              </div>
              
              <div class="activity-highlights" v-if="activity.highlights && activity.highlights.length > 0">
                <div class="highlights-label">活动亮点：</div>
                <div class="highlights-list">
                  <span v-for="highlight in activity.highlights" :key="highlight" class="highlight-tag">
                    {{ highlight }}
                  </span>
                </div>
              </div>
              
              <div class="activity-impact">
                <div class="impact-label">活动成果：</div>
                <p class="impact-text">{{ activity.impact }}</p>
              </div>
              
              <div class="activity-feedback" v-if="activity.feedback">
                <div class="feedback-label">参与反馈：</div>
                <p class="feedback-text">"{{ activity.feedback }}"</p>
              </div>
              
              <div class="activity-footer">
                <div class="organizer">
                  <span class="organizer-label">主办方：</span>
                  <span class="organizer-name">{{ activity.organizer }}</span>
                </div>
                <div class="next-activity" v-if="activity.nextActivity">
                  <span class="next-label">下次活动：</span>
                  <span class="next-date">{{ formatActivityDate(activity.nextActivity) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 参与公益 -->
    <section class="join-section">
      <div class="section-container">
        <div class="join-content">
          <h2 class="join-title">加入我们的公益行动</h2>
          <p class="join-description">
            您的每一次购买，都是对公益事业的支持。
            让我们一起传承书法文化，让更多人受益于传统艺术的魅力。
          </p>
          <div class="join-actions">
            <el-button type="primary" size="large" class="join-btn" @click="handleJoinCharity">
              <el-icon><StarFilled /></el-icon>
              参与公益
            </el-button>
            <el-button size="large" class="contact-btn" @click="handleContact">
              <el-icon><Phone /></el-icon>
              联系我们
            </el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { 
  Star, 
  User, 
  StarFilled, 
  School, 
  House, 
  Avatar, 
  View, 
  Phone 
} from '@element-plus/icons-vue'
import { mockApiData, volunteerActivities } from '@/utils/mockData'

// 公益统计数据
const charityStats = ref(mockApiData.charityStats)

// 公益教学点数据
const charityLocations = ref(mockApiData.charityLocations)

// 学员作品数据
const studentWorks = ref(mockApiData.studentWorks)

// 志愿者活动详细数据
const volunteerActivitiesData = ref(mockApiData.volunteerActivitiesData)

// 获取教学点类型名称
const getLocationTypeName = (type: string) => {
  const typeMap: { [key: string]: string } = {
    'school': '学校',
    'community_center': '社区中心',
    'care_center': '福利院',
    'elderly_university': '老年大学'
  }
  return typeMap[type] || '其他'
}

// 获取活动标题
const getActivityTitle = (index: number) => {
  const titles = ['志愿者教学活动', '书法比赛颁奖', '作品展示会', '公益庆典']
  return titles[index] || '志愿者活动'
}

// 获取活动描述
const getActivityDescription = (index: number) => {
  const descriptions = [
    '志愿者老师为孩子们提供专业的书法指导',
    '为优秀学员颁发奖状，鼓励继续学习',
    '展示学员们的优秀书法作品',
    '庆祝公益项目取得的丰硕成果'
  ]
  return descriptions[index] || '志愿者活动描述'
}

// 获取活动日期
const getActivityDate = (index: number) => {
  const dates = ['2024年3月15日', '2024年2月28日', '2024年1月20日', '2023年12月10日']
  return dates[index] || '2024年'
}

// 获取活动地点
const getActivityLocation = (index: number) => {
  const locations = ['北京市第一小学', '上海浦东社区中心', '广州市文化中心', '深圳市福利院']
  return locations[index] || '各地教学点'
}

// 查看作品
const viewWork = (work: any) => {
  console.log('查看作品:', work)
  // 这里可以实现作品放大预览功能
}

// 参与公益
const handleJoinCharity = () => {
  console.log('参与公益')
  // 这里可以跳转到购买页面或公益参与页面
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
}

// 格式化活动日期
const formatActivityDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

// 获取活动类型名称
const getActivityTypeName = (type: string) => {
  const typeMap: { [key: string]: string } = {
    'teaching': '教学活动',
    'ceremony': '颁奖典礼',
    'exhibition': '作品展示',
    'celebration': '庆典活动',
    'training': '培训活动',
    'community': '社区服务'
  }
  return typeMap[type] || '志愿活动'
}

// 联系我们
const handleContact = () => {
  console.log('联系我们')
  // 这里可以跳转到联系页面
}
</script>

<style lang="scss" scoped>
.charity-page {
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
}

// 页面头部
.page-hero {
  background: var(--primary-gradient);
  padding: 100px 0;
  color: white;
  
  .hero-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    text-align: center;
  }
  
  .hero-title {
    font-size: 48px;
    font-weight: 800;
    margin-bottom: 16px;
  }
  
  .hero-subtitle {
    font-size: 20px;
    opacity: 0.9;
    margin-bottom: 40px;
  }
  
  .hero-stats {
    display: flex;
    justify-content: center;
    gap: 60px;
    
    .stat-card {
      text-align: center;
      
      .stat-number {
        font-size: 36px;
        font-weight: 700;
        margin-bottom: 8px;
        color: white;
      }
      
      .stat-label {
        font-size: 16px;
        opacity: 0.8;
      }
    }
  }
}

// 公益理念
.concept-section {
  padding: 100px 0;
  
  .concept-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
  }
  
  .concept-text {
    .concept-title {
      font-size: 36px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 24px;
    }
    
    .concept-description {
      font-size: 18px;
      line-height: 1.6;
      color: var(--text-secondary);
      margin-bottom: 40px;
    }
    
    .concept-features {
      display: flex;
      flex-direction: column;
      gap: 24px;
      
      .feature-item {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .feature-icon {
          width: 48px;
          height: 48px;
          background: var(--primary-gradient);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 20px;
        }
        
        .feature-content {
          h3 {
            font-size: 18px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 4px;
          }
          
          p {
            font-size: 14px;
            color: var(--text-secondary);
          }
        }
      }
    }
  }
  
  .concept-image {
    img {
      width: 100%;
      height: auto;
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-lg);
    }
  }
}

// 统计数据
.statistics-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
  }
  
  .stats-card {
    background: white;
    padding: 40px 24px;
    border-radius: var(--radius-xl);
    text-align: center;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .stats-icon {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 24px;
      font-size: 32px;
      color: white;
      
      &.schools { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
      &.communities { background: linear-gradient(135deg, #10b981, #047857); }
      &.care { background: linear-gradient(135deg, #f59e0b, #d97706); }
      &.elderly { background: linear-gradient(135deg, #8b5cf6, #6d28d9); }
    }
    
    .stats-content {
      .stats-number {
        font-size: 32px;
        font-weight: 700;
        color: var(--primary-color);
        margin-bottom: 8px;
      }
      
      .stats-label {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      .stats-description {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

// 地图展示
.map-section {
  padding: 100px 0;
  
  .map-container {
    margin-bottom: 60px;
    
    .map-placeholder {
      position: relative;
      border-radius: var(--radius-xl);
      overflow: hidden;
      box-shadow: var(--shadow-lg);
      
      img {
        width: 100%;
        height: 500px;
        object-fit: cover;
      }
      
      .map-overlay {
        position: absolute;
        top: 20px;
        right: 20px;
        background: rgba(255, 255, 255, 0.9);
        padding: 20px;
        border-radius: var(--radius-lg);
        
        .map-legend {
          display: flex;
          flex-direction: column;
          gap: 12px;
          
          .legend-item {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .legend-dot {
              width: 12px;
              height: 12px;
              border-radius: 50%;
              
              &.schools { background: #3b82f6; }
              &.communities { background: #10b981; }
              &.care { background: #f59e0b; }
              &.elderly { background: #8b5cf6; }
            }
            
            span {
              font-size: 14px;
              color: var(--text-primary);
            }
          }
        }
      }
    }
  }
  
  .locations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 40px;
  }
  
  .location-card {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .location-image {
      position: relative;
      
      img {
        width: 100%;
        height: 200px;
        object-fit: cover;
      }
      
      .location-type {
        position: absolute;
        top: 16px;
        left: 16px;
        padding: 6px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
        color: white;
        
        &.school { background: #3b82f6; }
        &.community_center { background: #10b981; }
        &.care_center { background: #f59e0b; }
        &.elderly_university { background: #8b5cf6; }
      }
      
      .established-date {
        position: absolute;
        bottom: 16px;
        right: 16px;
        background: rgba(0, 0, 0, 0.7);
        color: white;
        padding: 4px 8px;
        border-radius: var(--radius-sm);
        font-size: 11px;
      }
    }
    
    .location-content {
      padding: 24px;
      
      .location-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .location-name {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
        }
        
        .location-status {
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 600;
          background: #f3f4f6;
          color: #6b7280;
          
          &.active {
            background: #dcfce7;
            color: #16a34a;
          }
        }
      }
      
      .location-address {
        font-size: 14px;
        color: var(--text-secondary);
        margin-bottom: 8px;
      }
      
      .location-description {
        font-size: 13px;
        color: var(--text-secondary);
        line-height: 1.4;
        margin-bottom: 16px;
      }
      
      .location-stats-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 12px;
        margin-bottom: 16px;
        
        .stat-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 8px;
          background: var(--bg-secondary);
          border-radius: var(--radius-md);
          
          .stat-label {
            font-size: 11px;
            color: var(--text-light);
            margin-bottom: 4px;
          }
          
          .stat-value {
            font-size: 14px;
            font-weight: 600;
            color: var(--primary-color);
          }
        }
      }
      
      .location-achievements {
        margin-bottom: 16px;
        
        .achievements-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .achievements-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          
          .achievement-badge {
            padding: 3px 8px;
            background: linear-gradient(135deg, #fbbf24, #f59e0b);
            color: white;
            border-radius: var(--radius-sm);
            font-size: 11px;
            font-weight: 500;
          }
        }
      }
      
      .location-programs {
        margin-bottom: 16px;
        
        .programs-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .programs-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          
          .program-tag {
            padding: 3px 8px;
            background: var(--primary-gradient);
            color: white;
            border-radius: var(--radius-sm);
            font-size: 11px;
            font-weight: 500;
          }
        }
      }
      
      .location-footer {
        .location-contact {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          color: var(--text-secondary);
          
          .weekly-hours {
            color: var(--primary-color);
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 学员作品
.works-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .works-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 40px;
  }
  
  .work-card {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .work-image {
      position: relative;
      cursor: pointer;
      
      img {
        width: 100%;
        height: 250px;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .work-overlay {
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
        
        .view-icon {
          font-size: 32px;
          color: white;
        }
        
        .work-style-badge {
          position: absolute;
          top: 16px;
          left: 16px;
          background: var(--primary-gradient);
          color: white;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          font-size: 12px;
          font-weight: 600;
        }
      }
      
      .improvement-score {
        position: absolute;
        top: 16px;
        right: 16px;
        background: rgba(16, 185, 129, 0.9);
        color: white;
        padding: 6px 10px;
        border-radius: var(--radius-md);
        text-align: center;
        
        .score-label {
          display: block;
          font-size: 10px;
          opacity: 0.9;
          margin-bottom: 2px;
        }
        
        .score-value {
          display: block;
          font-size: 14px;
          font-weight: 700;
        }
      }
      
      &:hover {
        img {
          transform: scale(1.05);
        }
        
        .work-overlay {
          opacity: 1;
        }
      }
    }
    
    .work-info {
      padding: 24px;
      
      .work-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .work-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
        }
        
        .work-grade {
          padding: 3px 8px;
          background: var(--primary-gradient);
          color: white;
          border-radius: var(--radius-sm);
          font-size: 11px;
          font-weight: 600;
        }
      }
      
      .work-description {
        font-size: 14px;
        color: var(--text-secondary);
        margin-bottom: 16px;
        line-height: 1.5;
      }
      
      .student-info {
        margin-bottom: 16px;
        
        .student-basic {
          display: flex;
          gap: 12px;
          align-items: center;
          margin-bottom: 8px;
          
          .student-name {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
          }
          
          .student-age {
            font-size: 12px;
            color: var(--text-light);
            background: var(--bg-secondary);
            padding: 2px 6px;
            border-radius: var(--radius-sm);
          }
          
          .student-school {
            font-size: 12px;
            color: var(--text-secondary);
            flex: 1;
            text-align: right;
          }
        }
        
        .practice-time {
          font-size: 12px;
          color: var(--primary-color);
          font-weight: 500;
        }
      }
      
      .work-achievements {
        margin-bottom: 16px;
        
        .achievement-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .awards-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          
          .award-badge {
            padding: 3px 8px;
            background: linear-gradient(135deg, #fbbf24, #f59e0b);
            color: white;
            border-radius: var(--radius-sm);
            font-size: 10px;
            font-weight: 500;
            line-height: 1.2;
          }
        }
      }
      
      .teacher-comment {
        .comment-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .comment-text {
          font-size: 13px;
          color: var(--text-secondary);
          font-style: italic;
          line-height: 1.4;
          background: var(--bg-secondary);
          padding: 12px;
          border-radius: var(--radius-md);
          border-left: 3px solid var(--primary-color);
        }
      }
    }
  }
}

// 志愿者活动
.volunteer-section {
  padding: 100px 0;
  
  .volunteer-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 40px;
  }
  
  .volunteer-card {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
    }
    
    .volunteer-image {
      position: relative;
      height: 220px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
      
      .activity-type-badge {
        position: absolute;
        top: 16px;
        left: 16px;
        padding: 6px 12px;
        border-radius: var(--radius-md);
        font-size: 12px;
        font-weight: 600;
        color: white;
        
        &.teaching { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
        &.ceremony { background: linear-gradient(135deg, #f59e0b, #d97706); }
        &.exhibition { background: linear-gradient(135deg, #8b5cf6, #6d28d9); }
        &.celebration { background: linear-gradient(135deg, #ef4444, #dc2626); }
        &.training { background: linear-gradient(135deg, #10b981, #047857); }
        &.community { background: linear-gradient(135deg, #06b6d4, #0891b2); }
      }
      
      .activity-status {
        position: absolute;
        top: 16px;
        right: 16px;
        padding: 4px 8px;
        border-radius: var(--radius-sm);
        font-size: 11px;
        font-weight: 600;
        
        &.completed {
          background: #dcfce7;
          color: #16a34a;
        }
        
        &.ongoing {
          background: #fef3c7;
          color: #d97706;
        }
      }
      
      &:hover img {
        transform: scale(1.05);
      }
    }
    
    .volunteer-content {
      padding: 24px;
      
      .volunteer-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .volunteer-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          flex: 1;
          margin-right: 12px;
        }
        
        .participant-count {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: var(--primary-color);
          background: var(--bg-secondary);
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          white-space: nowrap;
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
      
      .volunteer-description {
        font-size: 14px;
        color: var(--text-secondary);
        margin-bottom: 20px;
        line-height: 1.5;
      }
      
      .activity-details {
        margin-bottom: 20px;
        
        .detail-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          font-size: 13px;
          
          .detail-label {
            color: var(--text-secondary);
            font-weight: 500;
          }
          
          .detail-value {
            color: var(--text-primary);
            font-weight: 600;
          }
        }
      }
      
      .activity-highlights {
        margin-bottom: 20px;
        
        .highlights-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .highlights-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          
          .highlight-tag {
            padding: 3px 8px;
            background: var(--primary-gradient);
            color: white;
            border-radius: var(--radius-sm);
            font-size: 11px;
            font-weight: 500;
          }
        }
      }
      
      .activity-impact {
        margin-bottom: 16px;
        
        .impact-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .impact-text {
          font-size: 13px;
          color: var(--text-primary);
          background: var(--bg-secondary);
          padding: 12px;
          border-radius: var(--radius-md);
          border-left: 3px solid var(--primary-color);
          line-height: 1.4;
        }
      }
      
      .activity-feedback {
        margin-bottom: 20px;
        
        .feedback-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          font-weight: 500;
        }
        
        .feedback-text {
          font-size: 13px;
          color: var(--text-secondary);
          font-style: italic;
          background: #f8fafc;
          padding: 12px;
          border-radius: var(--radius-md);
          border-left: 3px solid #10b981;
          line-height: 1.4;
        }
      }
      
      .activity-footer {
        border-top: 1px solid var(--border-light);
        padding-top: 16px;
        
        .organizer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .organizer-label {
            font-size: 12px;
            color: var(--text-secondary);
          }
          
          .organizer-name {
            font-size: 12px;
            color: var(--primary-color);
            font-weight: 600;
          }
        }
        
        .next-activity {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .next-label {
            font-size: 12px;
            color: var(--text-secondary);
          }
          
          .next-date {
            font-size: 12px;
            color: white;
            font-weight: 600;
          }
        }
      }
    }
  }
}

// 参与公益
.join-section {
  padding: 100px 0;
  background: var(--primary-gradient);
  color: white;
  
  .join-content {
    text-align: center;
    max-width: 800px;
    margin: 0 auto;
    
    .join-title {
      font-size: 36px;
      font-weight: 700;
      margin-bottom: 24px;
    }
    
    .join-description {
      font-size: 18px;
      line-height: 1.6;
      opacity: 0.9;
      margin-bottom: 40px;
    }
    
    .join-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
      
      .join-btn {
        background: white;
        color: var(--primary-color);
        border: none;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-lg);
        }
      }
      
      .contact-btn {
        border: 2px solid white;
        color: white;
        background: transparent;
        
        &:hover {
          background: white;
          color: var(--primary-color);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-stats {
    flex-direction: column;
    gap: 24px !important;
  }
  
  .concept-content {
    grid-template-columns: 1fr !important;
    gap: 40px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .locations-grid,
  .works-gallery,
  .volunteer-grid {
    grid-template-columns: 1fr;
  }
  
  .join-actions {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 100%;
      max-width: 300px;
    }
  }
}
</style>