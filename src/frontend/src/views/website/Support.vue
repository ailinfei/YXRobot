<template>
  <div class="support-page">
    <!-- 页面头部 -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">技术支持</h1>
          <p class="hero-subtitle">为您提供专业的技术支持和帮助，让您的使用体验更加顺畅</p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">7×24</div>
              <div class="stat-label">小时技术支持</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">98%</div>
              <div class="stat-label">问题解决率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">5分钟</div>
              <div class="stat-label">平均响应时间</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 快速导航 -->
    <section class="quick-nav-section">
      <div class="container">
        <div class="quick-nav-grid">
          <div class="nav-card" @click="scrollToSection('guide')">
            <div class="nav-icon">
              <el-icon><Document /></el-icon>
            </div>
            <h3>使用指南</h3>
            <p>详细的操作说明和使用教程</p>
          </div>
          <div class="nav-card" @click="scrollToSection('faq')">
            <div class="nav-icon">
              <el-icon><QuestionFilled /></el-icon>
            </div>
            <h3>常见问题</h3>
            <p>快速找到问题的解决方案</p>
          </div>
          <div class="nav-card" @click="scrollToSection('downloads')">
            <div class="nav-icon">
              <el-icon><Download /></el-icon>
            </div>
            <h3>资料下载</h3>
            <p>技术文档和软件下载</p>
          </div>
          <div class="nav-card" @click="scrollToSection('feedback')">
            <div class="nav-icon">
              <el-icon><ChatRound /></el-icon>
            </div>
            <h3>问题反馈</h3>
            <p>提交您的问题和建议</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 使用指南 -->
    <section id="guide" class="guide-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">使用指南</h2>
          <p class="section-subtitle">图文并茂的操作说明，帮助您快速上手</p>
        </div>
        
        <div class="guide-tabs">
          <div class="tab-nav">
            <div 
              class="tab-item" 
              v-for="tab in guideTabs" 
              :key="tab.id"
              :class="{ active: activeGuideTab === tab.id }"
              @click="activeGuideTab = tab.id"
            >
              {{ tab.title }}
            </div>
          </div>
          
          <div class="tab-content">
            <div class="guide-content" v-for="tab in guideTabs" :key="tab.id" v-show="activeGuideTab === tab.id">
              <div class="guide-steps">
                <div class="step-item" v-for="(step, index) in tab.steps" :key="index">
                  <div class="step-number">{{ index + 1 }}</div>
                  <div class="step-content">
                    <h4 class="step-title">{{ step.title }}</h4>
                    <p class="step-description">{{ step.description }}</p>
                    <div class="step-image" v-if="step.image">
                      <img :src="step.image" :alt="step.title" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 常见问题FAQ -->
    <section id="faq" class="faq-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">常见问题</h2>
          <p class="section-subtitle">快速找到您需要的答案</p>
        </div>
        
        <!-- FAQ搜索 -->
        <div class="faq-search">
          <el-input
            v-model="searchQuery"
            placeholder="搜索问题..."
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <!-- FAQ分类 -->
        <div class="faq-categories">
          <div 
            class="category-item" 
            v-for="category in faqCategories" 
            :key="category.id"
            :class="{ active: activeFaqCategory === category.id }"
            @click="activeFaqCategory = category.id"
          >
            {{ category.name }}
          </div>
        </div>
        
        <!-- FAQ列表 -->
        <div class="faq-list">
          <div class="faq-item" v-for="faq in filteredFaqs" :key="faq.id">
            <div class="faq-question" @click="toggleFaq(faq.id)">
              <h4>{{ faq.question }}</h4>
              <el-icon class="expand-icon" :class="{ expanded: expandedFaqs.includes(faq.id) }">
                <ArrowDown />
              </el-icon>
            </div>
            <div class="faq-answer" v-show="expandedFaqs.includes(faq.id)">
              <p>{{ faq.answer }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 资料下载 -->
    <section id="downloads" class="downloads-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">资料下载</h2>
          <p class="section-subtitle">下载技术文档、软件和相关资料</p>
        </div>
        
        <div class="downloads-grid">
          <div class="download-card" v-for="item in downloadItems" :key="item.id">
            <div class="download-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="download-info">
              <h4 class="download-title">{{ item.title }}</h4>
              <p class="download-description">{{ item.description }}</p>
              <div class="download-meta">
                <span class="file-size">{{ item.size }}</span>
                <span class="file-type">{{ item.type }}</span>
                <span class="update-date">{{ item.updateDate }}</span>
              </div>
            </div>
            <div class="download-action">
              <el-button type="primary" @click="downloadFile(item)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 问题反馈 -->
    <section id="feedback" class="feedback-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">问题反馈</h2>
          <p class="section-subtitle">遇到问题？请告诉我们，我们会尽快为您解决</p>
        </div>
        
        <div class="feedback-layout">
          <div class="feedback-form">
            <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="100px">
              <el-form-item label="问题类型" prop="type">
                <el-select v-model="feedbackForm.type" placeholder="请选择问题类型" style="width: 100%">
                  <el-option label="设备故障" value="device" />
                  <el-option label="软件问题" value="software" />
                  <el-option label="使用咨询" value="usage" />
                  <el-option label="功能建议" value="suggestion" />
                  <el-option label="其他问题" value="other" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="联系姓名" prop="name">
                <el-input v-model="feedbackForm.name" placeholder="请输入您的姓名" />
              </el-form-item>
              
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="feedbackForm.phone" placeholder="请输入您的联系电话" />
              </el-form-item>
              
              <el-form-item label="邮箱地址" prop="email">
                <el-input v-model="feedbackForm.email" placeholder="请输入您的邮箱地址" />
              </el-form-item>
              
              <el-form-item label="问题描述" prop="description">
                <el-input 
                  v-model="feedbackForm.description" 
                  type="textarea" 
                  :rows="6"
                  placeholder="请详细描述您遇到的问题或建议"
                />
              </el-form-item>
              
              <el-form-item label="附件上传">
                <el-upload
                  class="upload-demo"
                  drag
                  action="#"
                  multiple
                  :auto-upload="false"
                  :file-list="feedbackForm.files"
                >
                  <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                  <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                  </div>
                  <template #tip>
                    <div class="el-upload__tip">
                      支持jpg/png/pdf文件，且不超过10MB
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" size="large" @click="submitFeedback" :loading="submitting">
                  提交反馈
                </el-button>
                <el-button size="large" @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <div class="contact-info">
            <div class="contact-card">
              <h4>联系方式</h4>
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <div>
                  <div class="contact-label">技术支持热线</div>
                  <div class="contact-value">400-123-4567</div>
                </div>
              </div>
              <div class="contact-item">
                <el-icon><Message /></el-icon>
                <div>
                  <div class="contact-label">技术支持邮箱</div>
                  <div class="contact-value">support@yxrobot.com</div>
                </div>
              </div>
              <div class="contact-item">
                <el-icon><Clock /></el-icon>
                <div>
                  <div class="contact-label">服务时间</div>
                  <div class="contact-value">7×24小时</div>
                </div>
              </div>
            </div>
            
            <div class="response-time">
              <h4>响应时间承诺</h4>
              <ul>
                <li>紧急问题：1小时内响应</li>
                <li>一般问题：4小时内响应</li>
                <li>咨询建议：24小时内响应</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  Document, 
  QuestionFilled, 
  Download, 
  ChatRound,
  Search,
  ArrowDown,
  Phone,
  Message,
  Clock,
  UploadFilled,
  Folder,
  VideoPlay,
  Setting
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const activeGuideTab = ref('setup')
const activeFaqCategory = ref('all')
const searchQuery = ref('')
const expandedFaqs = ref<number[]>([])
const submitting = ref(false)

// 使用指南标签页
const guideTabs = ref([
  {
    id: 'setup',
    title: '设备安装',
    steps: [
      {
        title: '开箱检查',
        description: '检查包装内容是否完整，包括主机、电源适配器、说明书等',
        image: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop'
      },
      {
        title: '设备连接',
        description: '将电源适配器连接到设备，确保电源指示灯正常亮起',
        image: 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=300&fit=crop'
      },
      {
        title: '网络配置',
        description: '通过WiFi或蓝牙连接设备到网络，确保网络连接稳定',
        image: 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=400&h=300&fit=crop'
      }
    ]
  },
  {
    id: 'usage',
    title: '基本使用',
    steps: [
      {
        title: '开机启动',
        description: '长按电源键3秒开机，等待系统启动完成',
        image: 'https://images.unsplash.com/photo-1551650975-87deedd944c3?w=400&h=300&fit=crop'
      },
      {
        title: '选择课程',
        description: '在主界面选择适合的练字课程，可按年级或字体分类选择',
        image: 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=300&fit=crop'
      },
      {
        title: '开始练习',
        description: '跟随机械臂的指导，进行书写练习，系统会实时给出反馈',
        image: 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400&h=300&fit=crop'
      }
    ]
  },
  {
    id: 'maintenance',
    title: '维护保养',
    steps: [
      {
        title: '日常清洁',
        description: '使用干净的软布擦拭屏幕和外壳，避免使用化学清洁剂',
        image: 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=400&h=300&fit=crop'
      },
      {
        title: '定期校准',
        description: '每月进行一次设备校准，确保识别精度和机械臂准确性',
        image: 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=400&h=300&fit=crop'
      },
      {
        title: '软件更新',
        description: '定期检查并安装软件更新，获得最新功能和性能优化',
        image: 'https://images.unsplash.com/photo-1551650975-87deedd944c3?w=400&h=300&fit=crop'
      }
    ]
  }
])

// FAQ分类
const faqCategories = ref([
  { id: 'all', name: '全部' },
  { id: 'device', name: '设备问题' },
  { id: 'software', name: '软件问题' },
  { id: 'usage', name: '使用问题' },
  { id: 'maintenance', name: '维护保养' }
])

// FAQ数据
const faqs = ref([
  {
    id: 1,
    category: 'device',
    question: '设备无法开机怎么办？',
    answer: '请检查电源连接是否正常，确认电源适配器指示灯是否亮起。如果电源正常，请长按电源键10秒进行强制重启。如问题仍未解决，请联系技术支持。'
  },
  {
    id: 2,
    category: 'device',
    question: '机械臂动作不准确如何处理？',
    answer: '这可能是校准问题。请进入设置菜单，选择"设备校准"，按照提示完成校准过程。建议每月进行一次校准以保持最佳性能。'
  },
  {
    id: 3,
    category: 'software',
    question: '如何更新系统软件？',
    answer: '设备连接网络后，系统会自动检查更新。您也可以在设置中手动检查更新。更新过程中请勿断电，更新完成后设备会自动重启。'
  },
  {
    id: 4,
    category: 'usage',
    question: '如何添加自定义练字内容？',
    answer: '在教师管理界面中，选择"课程管理"，点击"添加课程"，可以输入自定义的汉字内容。系统支持批量导入，也可以逐个添加。'
  },
  {
    id: 5,
    category: 'usage',
    question: '学生数据如何导出？',
    answer: '在教师端管理系统中，选择"数据管理"，可以按学生、班级或时间范围导出学习数据。支持Excel和PDF格式导出。'
  },
  {
    id: 6,
    category: 'maintenance',
    question: '屏幕出现划痕如何处理？',
    answer: '轻微划痕可以使用专用的屏幕修复剂处理。严重划痕建议联系售后服务更换屏幕。日常使用中请避免使用尖锐物品接触屏幕。'
  }
])

// 下载资料
const downloadItems = ref([
  {
    id: 1,
    title: '用户操作手册',
    description: '详细的设备操作指南和功能说明',
    size: '15.2MB',
    type: 'PDF',
    updateDate: '2024-01-15',
    icon: Document
  },
  {
    id: 2,
    title: '教师管理系统指南',
    description: '教师端管理系统的使用说明',
    size: '8.7MB',
    type: 'PDF',
    updateDate: '2024-01-10',
    icon: Folder
  },
  {
    id: 3,
    title: '设备驱动程序',
    description: '最新版本的设备驱动程序',
    size: '45.3MB',
    type: 'EXE',
    updateDate: '2024-01-20',
    icon: Setting
  },
  {
    id: 4,
    title: '教学视频合集',
    description: '设备使用和教学方法视频教程',
    size: '256MB',
    type: 'MP4',
    updateDate: '2024-01-08',
    icon: VideoPlay
  }
])

// 反馈表单
const feedbackForm = ref({
  type: '',
  name: '',
  phone: '',
  email: '',
  description: '',
  files: []
})

// 表单验证规则
const feedbackRules = {
  type: [{ required: true, message: '请选择问题类型', trigger: 'change' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  description: [{ required: true, message: '请描述您的问题', trigger: 'blur' }]
}

const feedbackFormRef = ref()

// 计算属性
const filteredFaqs = computed(() => {
  let filtered = faqs.value

  // 按分类筛选
  if (activeFaqCategory.value !== 'all') {
    filtered = filtered.filter(faq => faq.category === activeFaqCategory.value)
  }

  // 按搜索关键词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(faq => 
      faq.question.toLowerCase().includes(query) || 
      faq.answer.toLowerCase().includes(query)
    )
  }

  return filtered
})

// 方法
const scrollToSection = (sectionId: string) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const toggleFaq = (faqId: number) => {
  const index = expandedFaqs.value.indexOf(faqId)
  if (index > -1) {
    expandedFaqs.value.splice(index, 1)
  } else {
    expandedFaqs.value.push(faqId)
  }
}

const downloadFile = (item: any) => {
  console.log('下载文件:', item.title)
  ElMessage.success(`开始下载 ${item.title}`)
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  try {
    await feedbackFormRef.value.validate()
    submitting.value = true
    
    // 模拟提交
    setTimeout(() => {
      submitting.value = false
      ElMessage.success('反馈提交成功！我们会尽快与您联系。')
      resetForm()
    }, 2000)
  } catch (error) {
    console.log('表单验证失败:', error)
  }
}

const resetForm = () => {
  if (feedbackFormRef.value) {
    feedbackFormRef.value.resetFields()
  }
  feedbackForm.value.files = []
}
</script>

<style lang="scss" scoped>
.support-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

// 通用容器
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

// 英雄区域
.hero-section {
  background: var(--primary-gradient);
  padding: 100px 0;
  color: white;
  
  .hero-content {
    text-align: center;
    
    .hero-title {
      font-size: 48px;
      font-weight: 800;
      margin-bottom: 24px;
      line-height: 1.2;
    }
    
    .hero-subtitle {
      font-size: 20px;
      opacity: 0.9;
      margin-bottom: 48px;
      max-width: 600px;
      margin-left: auto;
      margin-right: auto;
      line-height: 1.6;
    }
    
    .hero-stats {
      display: flex;
      justify-content: center;
      gap: 60px;
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          font-size: 36px;
          font-weight: 700;
          margin-bottom: 8px;
          color: white;
          text-shadow: 0 2px 4px rgba(16, 185, 129, 0.3);
        }
        
        .stat-label {
          font-size: 16px;
          opacity: 0.9;
        }
      }
    }
  }
}

// 快速导航
.quick-nav-section {
  padding: 80px 0;
  background: white;
  
  .quick-nav-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 30px;
  }
  
  .nav-card {
    text-align: center;
    padding: 40px 24px;
    border-radius: var(--radius-xl);
    background: var(--bg-secondary);
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
      background: white;
      
      .nav-icon {
        transform: scale(1.1);
        box-shadow: 0 8px 16px rgba(255, 90, 95, 0.2);
      }
    }
    
    .nav-icon {
      width: 80px;
      height: 80px;
      background: var(--primary-gradient);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 24px;
      font-size: 32px;
      color: white;
      transition: all 0.3s ease;
    }
    
    h3 {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 12px;
    }
    
    p {
      color: var(--text-secondary);
      line-height: 1.5;
    }
  }
}

// 使用指南
.guide-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .guide-tabs {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    
    .tab-nav {
      display: flex;
      background: var(--bg-tertiary);
      
      .tab-item {
        flex: 1;
        padding: 20px;
        text-align: center;
        font-weight: 600;
        color: var(--text-secondary);
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          color: var(--primary-color);
          background: rgba(255, 90, 95, 0.05);
        }
        
        &.active {
          color: var(--primary-color);
          background: white;
          box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
    
    .tab-content {
      padding: 40px;
      
      .guide-steps {
        .step-item {
          display: flex;
          gap: 24px;
          margin-bottom: 40px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .step-number {
            width: 40px;
            height: 40px;
            background: var(--primary-gradient);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            flex-shrink: 0;
          }
          
          .step-content {
            flex: 1;
            
            .step-title {
              font-size: 18px;
              font-weight: 600;
              color: var(--text-primary);
              margin-bottom: 8px;
            }
            
            .step-description {
              color: var(--text-secondary);
              line-height: 1.6;
              margin-bottom: 16px;
            }
            
            .step-image {
              border-radius: var(--radius-lg);
              overflow: hidden;
              max-width: 400px;
              
              img {
                width: 100%;
                height: 200px;
                object-fit: cover;
              }
            }
          }
        }
      }
    }
  }
}

// FAQ区域
.faq-section {
  padding: 100px 0;
  background: white;
  
  .faq-search {
    max-width: 600px;
    margin: 0 auto 40px;
  }
  
  .faq-categories {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-bottom: 40px;
    flex-wrap: wrap;
    
    .category-item {
      padding: 8px 20px;
      border-radius: var(--radius-md);
      background: var(--bg-tertiary);
      color: var(--text-secondary);
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        color: var(--primary-color);
        background: rgba(255, 90, 95, 0.1);
      }
      
      &.active {
        color: white;
        background: var(--primary-gradient);
      }
    }
  }
  
  .faq-list {
    max-width: 800px;
    margin: 0 auto;
    
    .faq-item {
      border: 1px solid var(--border-color);
      border-radius: var(--radius-lg);
      margin-bottom: 16px;
      overflow: hidden;
      
      .faq-question {
        padding: 20px;
        cursor: pointer;
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: all 0.3s ease;
        
        &:hover {
          background: var(--bg-secondary);
        }
        
        h4 {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
        
        .expand-icon {
          transition: transform 0.3s ease;
          color: var(--text-secondary);
          
          &.expanded {
            transform: rotate(180deg);
          }
        }
      }
      
      .faq-answer {
        padding: 0 20px 20px;
        border-top: 1px solid var(--border-color);
        background: var(--bg-secondary);
        
        p {
          color: var(--text-secondary);
          line-height: 1.6;
          margin: 16px 0 0 0;
        }
      }
    }
  }
}

// 下载区域
.downloads-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .downloads-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 30px;
  }
  
  .download-card {
    background: white;
    padding: 30px;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-sm);
    display: flex;
    gap: 20px;
    align-items: center;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-lg);
    }
    
    .download-icon {
      width: 60px;
      height: 60px;
      background: var(--primary-gradient);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 24px;
      flex-shrink: 0;
    }
    
    .download-info {
      flex: 1;
      
      .download-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      .download-description {
        color: var(--text-secondary);
        line-height: 1.5;
        margin-bottom: 12px;
      }
      
      .download-meta {
        display: flex;
        gap: 16px;
        font-size: 12px;
        color: var(--text-light);
        
        span {
          &:not(:last-child)::after {
            content: '|';
            margin-left: 8px;
            color: var(--border-color);
          }
        }
      }
    }
    
    .download-action {
      flex-shrink: 0;
      
      .el-button {
        background: var(--primary-gradient);
        border: none;
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: var(--shadow-md);
        }
      }
    }
  }
}

// 反馈区域
.feedback-section {
  padding: 100px 0;
  background: white;
  
  .feedback-layout {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 60px;
    align-items: start;
  }
  
  .feedback-form {
    background: var(--bg-secondary);
    padding: 40px;
    border-radius: var(--radius-xl);
    
    .el-button--primary {
      background: var(--primary-gradient);
      border: none;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: var(--shadow-md);
      }
    }
  }
  
  .contact-info {
    .contact-card {
      background: var(--bg-secondary);
      padding: 30px;
      border-radius: var(--radius-xl);
      margin-bottom: 30px;
      
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 20px;
      }
      
      .contact-item {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 20px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .el-icon {
          width: 40px;
          height: 40px;
          background: var(--primary-gradient);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 18px;
        }
        
        .contact-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 4px;
        }
        
        .contact-value {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
        }
      }
    }
    
    .response-time {
      background: var(--bg-secondary);
      padding: 30px;
      border-radius: var(--radius-xl);
      
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }
      
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
          padding: 8px 0;
          color: var(--text-secondary);
          position: relative;
          padding-left: 20px;
          
          &::before {
            content: '•';
            position: absolute;
            left: 0;
            color: var(--primary-color);
            font-weight: bold;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-stats {
    flex-direction: column;
    gap: 30px !important;
  }
  
  .quick-nav-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
  
  .guide-tabs .tab-nav {
    flex-direction: column;
  }
  
  .faq-categories {
    justify-content: flex-start;
  }
  
  .downloads-grid {
    grid-template-columns: 1fr;
  }
  
  .download-card {
    flex-direction: column;
    text-align: center;
  }
  
  .feedback-layout {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .step-item {
    flex-direction: column;
    
    .step-number {
      align-self: flex-start;
    }
  }
}
</style>