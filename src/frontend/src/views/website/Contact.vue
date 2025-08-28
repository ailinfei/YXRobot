<template>
  <div class="contact-page">
    <!-- 页面头部 -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">联系我们</h1>
          <p class="hero-subtitle">随时为您提供专业的咨询服务，让我们一起探讨如何让科技助力教育</p>
          <div class="hero-features">
            <div class="feature-item">
              <el-icon><Clock /></el-icon>
              <span>7×24小时服务</span>
            </div>
            <div class="feature-item">
              <el-icon><ChatRound /></el-icon>
              <span>专业咨询团队</span>
            </div>
            <div class="feature-item">
              <el-icon><Lightning /></el-icon>
              <span>快速响应</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 服务统计 -->
    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">24</div>
            <div class="stat-label">小时响应</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">服务客户</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">98%</div>
            <div class="stat-label">满意度</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">365</div>
            <div class="stat-label">天服务</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系方式 -->
    <section class="contact-info-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">多种联系方式</h2>
          <p class="section-subtitle">选择最适合您的联系方式，我们随时为您服务</p>
        </div>
        
        <div class="contact-grid">
          <div class="contact-card">
            <div class="contact-icon">
              <el-icon><Phone /></el-icon>
            </div>
            <h3>电话咨询</h3>
            <div class="contact-details">
              <div class="contact-item">
                <span class="label">销售热线：</span>
                <span class="value">400-123-4567</span>
              </div>
              <div class="contact-item">
                <span class="label">技术支持：</span>
                <span class="value">400-123-4568</span>
              </div>
              <div class="contact-item">
                <span class="label">服务时间：</span>
                <span class="value">周一至周日 9:00-21:00</span>
              </div>
            </div>
            <el-button type="primary" @click="makeCall">立即拨打</el-button>
          </div>
          
          <div class="contact-card">
            <div class="contact-icon">
              <el-icon><Message /></el-icon>
            </div>
            <h3>邮箱联系</h3>
            <div class="contact-details">
              <div class="contact-item">
                <span class="label">商务合作：</span>
                <span class="value">business@yxrobot.com</span>
              </div>
              <div class="contact-item">
                <span class="label">技术支持：</span>
                <span class="value">support@yxrobot.com</span>
              </div>
              <div class="contact-item">
                <span class="label">客户服务：</span>
                <span class="value">service@yxrobot.com</span>
              </div>
            </div>
            <el-button type="primary" @click="sendEmail">发送邮件</el-button>
          </div>
          
          <div class="contact-card">
            <div class="contact-icon">
              <el-icon><Location /></el-icon>
            </div>
            <h3>公司地址</h3>
            <div class="contact-details">
              <div class="contact-item">
                <span class="label">总部地址：</span>
                <span class="value">北京市朝阳区科技园区创新大厦A座15层</span>
              </div>
              <div class="contact-item">
                <span class="label">邮政编码：</span>
                <span class="value">100000</span>
              </div>
              <div class="contact-item">
                <span class="label">交通指南：</span>
                <span class="value">地铁10号线科技园站A出口</span>
              </div>
            </div>
            <el-button type="primary" @click="showMap">查看地图</el-button>
          </div>
          
          <div class="contact-card">
            <div class="contact-icon">
              <el-icon><ChatRound /></el-icon>
            </div>
            <h3>在线客服</h3>
            <div class="contact-details">
              <div class="contact-item">
                <span class="label">微信客服：</span>
                <span class="value">扫码添加客服微信</span>
              </div>
              <div class="contact-item">
                <span class="label">QQ客服：</span>
                <span class="value">123456789</span>
              </div>
              <div class="contact-item">
                <span class="label">在线时间：</span>
                <span class="value">7×24小时在线</span>
              </div>
            </div>
            <el-button type="primary" @click="startChat">开始聊天</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 在线客服聊天窗口 -->
    <section class="chat-section" v-if="showChatWindow">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">在线客服</h2>
          <p class="section-subtitle">专业客服团队为您实时解答问题</p>
        </div>
        
        <div class="chat-container">
          <div class="chat-window">
            <div class="chat-header">
              <div class="agent-info">
                <img src="https://picsum.photos/40/40?random=1" alt="客服头像" class="agent-avatar" />
                <div class="agent-details">
                  <div class="agent-name">小智客服</div>
                  <div class="agent-status">在线</div>
                </div>
              </div>
              <el-button text @click="closeChatWindow">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            
            <div class="chat-messages" ref="chatMessagesRef">
              <div class="message" v-for="message in chatMessages" :key="message.id" :class="message.type">
                <div class="message-avatar" v-if="message.type === 'received'">
                  <img src="https://picsum.photos/32/32?random=1" alt="客服" />
                </div>
                <div class="message-content">
                  <div class="message-bubble">{{ message.content }}</div>
                  <div class="message-time">{{ message.time }}</div>
                </div>
                <div class="message-avatar" v-if="message.type === 'sent'">
                  <img src="https://picsum.photos/32/32?random=2" alt="用户" />
                </div>
              </div>
            </div>
            
            <div class="chat-input">
              <el-input
                v-model="chatInputText"
                placeholder="请输入您的问题..."
                @keyup.enter="sendChatMessage"
              >
                <template #append>
                  <el-button @click="sendChatMessage" :disabled="!chatInputText.trim()">
                    发送
                  </el-button>
                </template>
              </el-input>
            </div>
          </div>
          
          <div class="chat-sidebar">
            <div class="quick-questions">
              <h4>常见问题</h4>
              <div class="question-item" v-for="question in quickQuestions" :key="question.id" @click="selectQuickQuestion(question)">
                {{ question.text }}
              </div>
            </div>
            
            <div class="contact-shortcuts">
              <h4>其他联系方式</h4>
              <el-button size="small" @click="makeCall">
                <el-icon><Phone /></el-icon>
                电话咨询
              </el-button>
              <el-button size="small" @click="sendEmail">
                <el-icon><Message /></el-icon>
                邮件联系
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="faq-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">常见问题</h2>
          <p class="section-subtitle">快速找到您关心问题的答案</p>
        </div>
        
        <div class="faq-grid">
          <div class="faq-category" v-for="category in faqCategories" :key="category.id">
            <h3 class="category-title">{{ category.title }}</h3>
            <div class="faq-list">
              <div class="faq-item" v-for="faq in category.questions" :key="faq.id">
                <div class="faq-question" @click="toggleFaq(faq.id)">
                  <span>{{ faq.question }}</span>
                  <el-icon class="faq-icon" :class="{ active: activeFaq === faq.id }">
                    <ArrowDown />
                  </el-icon>
                </div>
                <div class="faq-answer" v-show="activeFaq === faq.id">
                  <p>{{ faq.answer }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户反馈表单 -->
    <section class="feedback-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">客户反馈</h2>
          <p class="section-subtitle">您的意见和建议是我们持续改进的动力</p>
        </div>
        
        <div class="feedback-layout">
          <div class="feedback-form">
            <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="120px">
              <el-form-item label="反馈类型" prop="type">
                <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型" style="width: 100%">
                  <el-option label="产品咨询" value="product" />
                  <el-option label="商务合作" value="business" />
                  <el-option label="技术支持" value="technical" />
                  <el-option label="投诉建议" value="complaint" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="公司/机构名称" prop="company">
                <el-input v-model="feedbackForm.company" placeholder="请输入公司或机构名称" />
              </el-form-item>
              
              <el-form-item label="联系人姓名" prop="name">
                <el-input v-model="feedbackForm.name" placeholder="请输入联系人姓名" />
              </el-form-item>
              
              <el-form-item label="职位" prop="position">
                <el-input v-model="feedbackForm.position" placeholder="请输入您的职位" />
              </el-form-item>
              
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="feedbackForm.phone" placeholder="请输入联系电话" />
              </el-form-item>
              
              <el-form-item label="邮箱地址" prop="email">
                <el-input v-model="feedbackForm.email" placeholder="请输入邮箱地址" />
              </el-form-item>
              
              <el-form-item label="详细内容" prop="content">
                <el-input 
                  v-model="feedbackForm.content" 
                  type="textarea" 
                  :rows="6"
                  placeholder="请详细描述您的需求、问题或建议"
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
                      支持jpg/png/pdf/doc文件，单个文件不超过10MB
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" size="large" @click="submitFeedback" :loading="submitting">
                  提交反馈
                </el-button>
                <el-button size="large" @click="resetForm">重置表单</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <div class="feedback-info">
            <div class="info-card">
              <h4>为什么选择我们？</h4>
              <ul>
                <li>专业的AI练字技术</li>
                <li>丰富的教育行业经验</li>
                <li>完善的售后服务体系</li>
                <li>持续的产品创新能力</li>
              </ul>
            </div>
            
            <div class="info-card">
              <h4>服务承诺</h4>
              <ul>
                <li>24小时内回复您的咨询</li>
                <li>专业团队跟进服务</li>
                <li>定制化解决方案</li>
                <li>长期技术支持保障</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 地图定位 -->
    <section class="map-section" v-if="showMapSection">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">公司位置</h2>
          <p class="section-subtitle">欢迎您到访我们的办公室进行面对面交流</p>
        </div>
        
        <div class="map-container">
          <div class="map-placeholder">
            <div class="map-content">
              <el-icon size="48"><Location /></el-icon>
              <h3>地图加载中...</h3>
              <p>北京市朝阳区科技园区创新大厦A座15层</p>
              <div class="map-actions">
                <el-button type="primary" @click="openExternalMap">在地图中查看</el-button>
                <el-button @click="copyAddress">复制地址</el-button>
              </div>
            </div>
          </div>
          
          <div class="location-info">
            <h4>交通指南</h4>
            <div class="transport-item">
              <el-icon><CaretRight /></el-icon>
              <div>
                <strong>地铁：</strong>
                <span>10号线科技园站A出口，步行5分钟</span>
              </div>
            </div>
            <div class="transport-item">
              <el-icon><CaretRight /></el-icon>
              <div>
                <strong>公交：</strong>
                <span>123路、456路科技园站下车</span>
              </div>
            </div>
            <div class="transport-item">
              <el-icon><CaretRight /></el-icon>
              <div>
                <strong>自驾：</strong>
                <span>大厦地下停车场，免费停车2小时</span>
              </div>
            </div>
            
            <div class="office-hours">
              <h4>办公时间</h4>
              <p>周一至周五：9:00 - 18:00</p>
              <p>周六：9:00 - 17:00</p>
              <p>周日：预约访问</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { 
  Phone, 
  Message, 
  Location, 
  ChatRound,
  Clock,
  Lightning,
  Close,
  UploadFilled,
  CaretRight,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const showChatWindow = ref(false)
const showMapSection = ref(false)
const chatInputText = ref('')
const chatMessages = ref([
  {
    id: 1,
    type: 'received',
    content: '您好！欢迎咨询练字机器人，我是您的专属客服小智，有什么可以帮助您的吗？',
    time: '14:30'
  }
])
const submitting = ref(false)

// 快速问题
const quickQuestions = ref([
  { id: 1, text: '产品价格是多少？' },
  { id: 2, text: '如何申请试用？' },
  { id: 3, text: '有哪些付款方式？' },
  { id: 4, text: '售后服务如何？' },
  { id: 5, text: '可以定制功能吗？' }
])

// 反馈表单
const feedbackForm = ref({
  type: '',
  company: '',
  name: '',
  position: '',
  phone: '',
  email: '',
  content: '',
  files: []
})

// 表单验证规则
const feedbackRules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  company: [{ required: true, message: '请输入公司或机构名称', trigger: 'blur' }],
  name: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入详细内容', trigger: 'blur' }]
}

const feedbackFormRef = ref()
const chatMessagesRef = ref()

// FAQ相关数据
const activeFaq = ref(null)
const faqCategories = ref([
  {
    id: 1,
    title: '产品相关',
    questions: [
      {
        id: 1,
        question: '练字机器人适合什么年龄段使用？',
        answer: '我们的练字机器人适合5-18岁的学生使用，从幼儿园到高中阶段都有相应的课程内容和难度设置。'
      },
      {
        id: 2,
        question: '机器人的书写效果如何？',
        answer: '采用先进的AI技术和精密机械臂，能够模拟真人手写动作，书写效果自然流畅，完全符合书法标准。'
      },
      {
        id: 3,
        question: '设备需要联网使用吗？',
        answer: '设备支持离线和在线两种模式。基础功能可离线使用，在线模式可获得更多课程内容和实时更新。'
      }
    ]
  },
  {
    id: 2,
    title: '购买服务',
    questions: [
      {
        id: 4,
        question: '如何购买练字机器人？',
        answer: '您可以通过我们的官网、授权经销商或电商平台购买。我们提供多种购买方式和分期付款选项。'
      },
      {
        id: 5,
        question: '是否提供试用服务？',
        answer: '是的，我们为教育机构提供7天免费试用服务，个人用户可预约到店体验或参加体验活动。'
      },
      {
        id: 6,
        question: '保修期是多长时间？',
        answer: '我们提供3年免费保修服务，包括硬件维修和软件更新。保修期外也提供优惠的维修服务。'
      }
    ]
  },
  {
    id: 3,
    title: '技术支持',
    questions: [
      {
        id: 7,
        question: '设备出现故障怎么办？',
        answer: '请先查看用户手册进行基础排查，如无法解决可联系我们的技术支持团队，提供远程协助或上门维修服务。'
      },
      {
        id: 8,
        question: '如何更新课程内容？',
        answer: '设备联网后会自动检查更新，您也可以在设置中手动检查更新。新课程内容会定期推送。'
      },
      {
        id: 9,
        question: '支持多少种字体？',
        answer: '目前支持楷书、行书、隶书等多种字体，包含颜真卿、王羲之等名家字体，总计超过50种字体样式。'
      }
    ]
  }
])

// 方法
const makeCall = () => {
  // 检测设备类型，移动端直接拨打电话
  if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
    window.location.href = 'tel:400-123-4567'
  } else {
    ElMessage.info('请拨打销售热线：400-123-4567')
    // 复制电话号码到剪贴板
    navigator.clipboard.writeText('400-123-4567').then(() => {
      ElMessage.success('电话号码已复制到剪贴板')
    }).catch(() => {
      console.log('复制失败')
    })
  }
}

const sendEmail = () => {
  const subject = encodeURIComponent('练字机器人咨询')
  const body = encodeURIComponent('您好，我想了解更多关于练字机器人的信息。\n\n我的需求：\n\n联系方式：\n\n')
  window.open(`mailto:business@yxrobot.com?subject=${subject}&body=${body}`)
}

const showMap = () => {
  showMapSection.value = true
  nextTick(() => {
    const element = document.querySelector('.map-section')
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' })
    }
  })
}

const startChat = () => {
  showChatWindow.value = true
  nextTick(() => {
    const element = document.querySelector('.chat-section')
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' })
    }
  })
}

const closeChatWindow = () => {
  showChatWindow.value = false
}

const sendChatMessage = () => {
  if (!chatInputText.value.trim()) return
  
  const userMessage = {
    id: Date.now(),
    type: 'sent',
    content: chatInputText.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  chatMessages.value.push(userMessage)
  chatInputText.value = ''
  
  // 模拟客服回复
  setTimeout(() => {
    const responses = [
      '感谢您的咨询，我来为您详细介绍一下我们的产品。',
      '这是一个很好的问题，让我为您查询相关信息。',
      '根据您的需求，我推荐以下解决方案...',
      '我已经记录了您的问题，稍后会有专业人员与您联系。'
    ]
    
    const botMessage = {
      id: Date.now() + 1,
      type: 'received',
      content: responses[Math.floor(Math.random() * responses.length)],
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    
    chatMessages.value.push(botMessage)
    
    // 滚动到底部
    nextTick(() => {
      if (chatMessagesRef.value) {
        chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
      }
    })
  }, 1000)
}

const selectQuickQuestion = (question: any) => {
  chatInputText.value = question.text
  sendChatMessage()
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  try {
    await feedbackFormRef.value.validate()
    submitting.value = true
    
    // 模拟提交
    setTimeout(() => {
      submitting.value = false
      ElMessage.success('反馈提交成功！我们会在24小时内与您联系。')
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

const openExternalMap = () => {
  const address = '北京市朝阳区科技园区创新大厦A座15层'
  const url = `https://map.baidu.com/search/${encodeURIComponent(address)}`
  window.open(url, '_blank')
}

const copyAddress = () => {
  const address = '北京市朝阳区科技园区创新大厦A座15层'
  navigator.clipboard.writeText(address).then(() => {
    ElMessage.success('地址已复制到剪贴板')
  })
}

// FAQ切换功能
const toggleFaq = (faqId: number) => {
  activeFaq.value = activeFaq.value === faqId ? null : faqId
}
</script>

<style lang="scss" scoped>
.contact-page {
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
    
    .hero-features {
      display: flex;
      justify-content: center;
      gap: 40px;
      
      .feature-item {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 16px;
        
        .el-icon {
          font-size: 20px;
          color: white;
        }
      }
    }
  }
}

// 联系方式区域
.contact-info-section {
  padding: 100px 0;
  background: white;
  
  .contact-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 30px;
  }
  
  .contact-card {
    background: var(--bg-secondary);
    padding: 40px 30px;
    border-radius: var(--radius-xl);
    text-align: center;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: var(--shadow-lg);
      background: white;
      
      .contact-icon {
        transform: scale(1.1);
        box-shadow: 0 8px 16px rgba(255, 90, 95, 0.2);
      }
    }
    
    .contact-icon {
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
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 24px;
    }
    
    .contact-details {
      margin-bottom: 30px;
      
      .contact-item {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;
        text-align: left;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .label {
          font-weight: 500;
          color: var(--text-secondary);
          min-width: 80px;
        }
        
        .value {
          color: var(--text-primary);
          font-weight: 500;
          flex: 1;
        }
      }
    }
    
    .el-button {
      background: var(--primary-gradient);
      border: none;
      width: 100%;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: var(--shadow-md);
      }
    }
  }
}

// 聊天区域
.chat-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .chat-container {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .chat-window {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    height: 500px;
    display: flex;
    flex-direction: column;
    
    .chat-header {
      padding: 20px;
      background: var(--bg-tertiary);
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid var(--border-color);
      
      .agent-info {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .agent-avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          object-fit: cover;
        }
        
        .agent-name {
          font-weight: 600;
          color: var(--text-primary);
        }
        
        .agent-status {
          font-size: 12px;
          color: white;
        }
      }
    }
    
    .chat-messages {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      
      .message {
        display: flex;
        gap: 12px;
        margin-bottom: 20px;
        
        &.sent {
          flex-direction: row-reverse;
          
          .message-content {
            align-items: flex-end;
            
            .message-bubble {
              background: var(--primary-gradient);
              color: white;
            }
          }
        }
        
        .message-avatar {
          img {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
          }
        }
        
        .message-content {
          display: flex;
          flex-direction: column;
          max-width: 70%;
          
          .message-bubble {
            background: var(--bg-secondary);
            padding: 12px 16px;
            border-radius: var(--radius-lg);
            color: var(--text-primary);
            line-height: 1.5;
          }
          
          .message-time {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 4px;
          }
        }
      }
    }
    
    .chat-input {
      padding: 20px;
      border-top: 1px solid var(--border-color);
      
      .el-button {
        background: var(--primary-gradient);
        border: none;
      }
    }
  }
  
  .chat-sidebar {
    .quick-questions {
      background: white;
      padding: 24px;
      border-radius: var(--radius-xl);
      margin-bottom: 20px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }
      
      .question-item {
        padding: 12px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        margin-bottom: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        font-size: 14px;
        color: var(--text-secondary);
        
        &:hover {
          background: var(--primary-color);
          color: white;
          transform: translateX(4px);
        }
      }
    }
    
    .contact-shortcuts {
      background: white;
      padding: 24px;
      border-radius: var(--radius-xl);
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }
      
      .el-button {
        width: 100%;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
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
  
  .feedback-info {
    .info-card {
      background: var(--bg-secondary);
      padding: 30px;
      border-radius: var(--radius-xl);
      margin-bottom: 30px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
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
            content: '✓';
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

// FAQ区域
.faq-section {
  padding: 100px 0;
  background: var(--bg-secondary);
}
  .faq-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 40px;
  }
  
  .faq-category {
    background: white;
    border-radius: var(--radius-xl);
    padding: 30px;
    box-shadow: var(--shadow-sm);
    
    .category-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 24px;
      text-align: center;
      padding-bottom: 16px;
      border-bottom: 2px solid var(--primary-color);
    }
    
    .faq-list {
      .faq-item {
        border-bottom: 1px solid var(--border-color);
        
        &:last-child {
          border-bottom: none;
        }
        
        .faq-question {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 0;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &:hover {
            color: var(--primary-color);
          }
          
          span {
            font-weight: 500;
            color: var(--text-primary);
            flex: 1;
          }
          
          .faq-icon {
            color: var(--primary-color);
            transition: transform 0.3s ease;
            
            &.active {
              transform: rotate(180deg);
            }
          }
        }
        
        .faq-answer {
          padding: 0 0 16px 0;
          color: var(--text-secondary);
          line-height: 1.6;
          
          p {
            margin: 0;
          }
        }
      }
    }
  }

// 地图区域
.map-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .map-container {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
    align-items: start;
  }
  
  .map-placeholder {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .map-content {
      text-align: center;
      color: var(--text-secondary);
      
      .el-icon {
        color: var(--primary-color);
        margin-bottom: 16px;
      }
      
      h3 {
        font-size: 20px;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      p {
        margin-bottom: 24px;
        line-height: 1.6;
      }
      
      .map-actions {
        display: flex;
        gap: 12px;
        justify-content: center;
        
        .el-button--primary {
          background: var(--primary-gradient);
          border: none;
        }
      }
    }
  }
  
  .location-info {
    background: white;
    padding: 30px;
    border-radius: var(--radius-xl);
    
    h4 {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }
    
    .transport-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      margin-bottom: 16px;
      
      .el-icon {
        color: var(--primary-color);
        margin-top: 2px;
      }
      
      strong {
        color: var(--text-primary);
        margin-right: 8px;
      }
      
      span {
        color: var(--text-secondary);
      }
    }
    
    .office-hours {
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid var(--border-color);
      
      h4 {
        margin-bottom: 12px;
      }
      
      p {
        color: var(--text-secondary);
        margin-bottom: 4px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

// 服务统计区域
.stats-section {
  padding: 60px 0;
  background: white;
  border-bottom: 1px solid var(--border-color);
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 40px;
    max-width: 800px;
    margin: 0 auto;
  }
  
  .stat-item {
    text-align: center;
    
    .stat-number {
      font-size: 48px;
      font-weight: 800;
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

// 地图区域
.map-section {
  padding: 100px 0;
  background: var(--bg-secondary);
  
  .map-container {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
    align-items: start;
  }
  
  .map-placeholder {
    background: white;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .map-content {
      text-align: center;
      color: var(--text-secondary);
      
      .el-icon {
        color: var(--primary-color);
        margin-bottom: 16px;
      }
      
      h3 {
        font-size: 20px;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      p {
        margin-bottom: 24px;
        line-height: 1.6;
      }
      
      .map-actions {
        display: flex;
        gap: 12px;
        justify-content: center;
        
        .el-button--primary {
          background: var(--primary-gradient);
          border: none;
        }
      }
    }
  }
  
  .location-info {
    background: white;
    padding: 30px;
    border-radius: var(--radius-xl);
    
    h4 {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }
    
    .transport-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      margin-bottom: 16px;
      
      .el-icon {
        color: var(--primary-color);
        margin-top: 2px;
      }
      
      strong {
        color: var(--text-primary);
        margin-right: 8px;
      }
      
      span {
        color: var(--text-secondary);
      }
    }
    
    .office-hours {
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid var(--border-color);
      
      h4 {
        margin-bottom: 12px;
      }
      
      p {
        color: var(--text-secondary);
        margin-bottom: 4px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-features {
    flex-direction: column;
    gap: 20px !important;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
  
  .faq-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .chat-container {
    grid-template-columns: 1fr;
    
    .chat-window {
      order: 2;
    }
    
    .chat-sidebar {
      order: 1;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }
  }
  
  .feedback-layout {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .chat-container {
    grid-template-columns: 1fr;
    
    .chat-window {
      order: 2;
    }
    
    .chat-sidebar {
      order: 1;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }
  }
  
  .feedback-layout {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .map-container {
    grid-template-columns: 1fr;
  }
  
  .map-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}

</style>