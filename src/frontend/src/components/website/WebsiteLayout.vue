<template>
  <div class="website-layout">
    <!-- 顶部导航栏 -->
    <header class="website-header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo">
          <div class="logo-icon">
            <el-icon><Edit /></el-icon>
          </div>
          <span class="logo-text">练字机器人</span>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link 
            v-for="item in menuItems" 
            :key="item.path"
            :to="item.path" 
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            {{ item.title }}
          </router-link>
        </nav>

        <!-- 右侧操作区 -->
        <div class="header-actions">
          <!-- 语言切换 -->
          <el-dropdown @command="handleLanguageChange">
            <el-button text class="lang-btn">
              <el-icon><Operation /></el-icon>
              <span>{{ currentLanguage.name }}</span>
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item 
                  v-for="lang in languages" 
                  :key="lang.code"
                  :command="lang.code"
                  :class="{ active: currentLanguage.code === lang.code }"
                >
                  {{ lang.name }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 购买/租赁按钮 -->
          <el-button type="primary" class="cta-btn" @click="scrollToProducts">
            立即体验
          </el-button>

          <!-- 移动端菜单按钮 -->
          <el-button 
            class="mobile-menu-btn"
            @click="toggleMobileMenu"
            :icon="Menu"
            text
          />
        </div>
      </div>

      <!-- 移动端菜单 -->
      <div class="mobile-menu" :class="{ open: mobileMenuOpen }">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path" 
          class="mobile-nav-item"
          @click="closeMobileMenu"
        >
          {{ item.title }}
        </router-link>
        <div class="mobile-actions">
          <el-button type="primary" size="large" @click="scrollToProducts">
            立即体验
          </el-button>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="website-main">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="website-footer">
      <div class="footer-container">
        <div class="footer-content">
          <!-- 公司信息 -->
          <div class="footer-section">
            <div class="footer-logo">
              <div class="logo-icon">
                <el-icon><Edit /></el-icon>
              </div>
              <span class="logo-text">练字机器人</span>
            </div>
            <p class="footer-desc">
              专业的AI练字机器人，让每个人都能写出一手好字。
              每售出一台机器人，我们就向公益机构捐赠一节书法课。
            </p>
            <div class="social-links">
              <el-button circle :icon="ChatDotRound" />
              <el-button circle :icon="Share" />
              <el-button circle :icon="Phone" />
            </div>
          </div>

          <!-- 产品链接 -->
          <div class="footer-section">
            <h4>产品服务</h4>
            <ul class="footer-links">
              <li><a href="#products">产品介绍</a></li>
              <li><a href="#features">功能特色</a></li>
              <li><a href="#pricing">价格方案</a></li>
              <li><a href="#rental">租赁服务</a></li>
            </ul>
          </div>

          <!-- 公益项目 -->
          <div class="footer-section">
            <h4>公益项目</h4>
            <ul class="footer-links">
              <li><a href="#charity">公益理念</a></li>
              <li><a href="#locations">教学点分布</a></li>
              <li><a href="#impact">社会影响</a></li>
              <li><a href="#volunteer">志愿者招募</a></li>
            </ul>
          </div>

          <!-- 支持帮助 -->
          <div class="footer-section">
            <h4>支持帮助</h4>
            <ul class="footer-links">
              <li><a href="#support">技术支持</a></li>
              <li><a href="#faq">常见问题</a></li>
              <li><a href="#contact">联系我们</a></li>
              <li><a href="#feedback">意见反馈</a></li>
            </ul>
          </div>
        </div>

        <div class="footer-bottom">
          <p>&copy; 2024 练字机器人科技有限公司. 保留所有权利.</p>
          <div class="footer-bottom-links">
            <a href="#privacy">隐私政策</a>
            <a href="#terms">服务条款</a>
            <a href="#legal">法律声明</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Edit,
  Operation,
  ArrowDown,
  Menu,
  ChatDotRound,
  Share,
  Phone
} from '@element-plus/icons-vue'

const route = useRoute()
const mobileMenuOpen = ref(false)

// 导航菜单项
const menuItems = [
  { path: '/website/home', title: '首页' },
  { path: '/website/products', title: '产品介绍' },
  { path: '/website/charity', title: '公益项目' },
  { path: '/website/news', title: '在线新闻' },
  { path: '/website/support', title: '技术支持' },
  { path: '/website/contact', title: '联系我们' }
]

// 语言选项
const languages = [
  { code: 'zh', name: '中文' },
  { code: 'en', name: 'English' },
  { code: 'ja', name: '日本語' },
  { code: 'ko', name: '한국어' },
  { code: 'es', name: 'Español' }
]

const currentLanguageCode = ref('zh')
const currentLanguage = computed(() => 
  languages.find(lang => lang.code === currentLanguageCode.value) || languages[0]
)

// 切换语言
const handleLanguageChange = (langCode: string) => {
  currentLanguageCode.value = langCode
  console.log('切换语言到:', langCode)
  // 这里可以添加实际的语言切换逻辑
}

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

// 关闭移动端菜单
const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}

// 滚动到产品区域
const scrollToProducts = () => {
  const element = document.getElementById('products')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
  closeMobileMenu()
}
</script>

<style lang="scss" scoped>
.website-layout {
  min-height: 100vh;
  background: var(--bg-primary);
}

.website-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 1000;
  
  .header-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .logo {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    text-decoration: none;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-1px);
      
      .logo-icon {
        box-shadow: 0 4px 12px rgba(255, 90, 95, 0.3);
        transform: scale(1.05);
      }
      
      .logo-text {
        color: var(--primary-color);
      }
    }
    
    .logo-icon {
      width: 36px;
      height: 36px;
      background: var(--primary-gradient);
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      color: white;
      font-size: 18px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(255, 90, 95, 0.2);
    }
    
    .logo-text {
      transition: color 0.3s ease;
    }
  }
  
  .nav-menu {
    display: flex;
    align-items: center;
    gap: 32px;
    
    .nav-item {
      color: var(--text-secondary);
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;
      position: relative;
      padding: 8px 16px;
      border-radius: var(--radius-md);
      
      &:hover {
        color: var(--primary-color);
        background: rgba(255, 90, 95, 0.05);
        transform: translateY(-1px);
      }
      
      &.active {
        color: var(--primary-color);
        background: rgba(255, 90, 95, 0.1);
        
        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 50%;
          transform: translateX(-50%);
          width: 30px;
          height: 3px;
          background: var(--primary-gradient);
          border-radius: 2px;
          box-shadow: 0 2px 4px rgba(255, 90, 95, 0.3);
        }
      }
    }
  }
  
  .header-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .lang-btn {
      display: flex;
      align-items: center;
      gap: 6px;
      color: var(--text-secondary);
      
      &:hover {
        color: var(--primary-color);
      }
    }
    
    .cta-btn {
      background: var(--primary-gradient);
      border: none;
      padding: 10px 24px;
      font-weight: 500;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: var(--shadow-md);
      }
    }
    
    .mobile-menu-btn {
      display: none;
      color: var(--text-secondary);
    }
  }
  
  .mobile-menu {
    display: none;
    background: white;
    border-top: 1px solid var(--border-color);
    padding: 20px 24px;
    
    &.open {
      display: block;
    }
    
    .mobile-nav-item {
      display: block;
      padding: 12px 0;
      color: var(--text-secondary);
      text-decoration: none;
      font-weight: 500;
      border-bottom: 1px solid var(--border-color);
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover,
      &.active {
        color: var(--primary-color);
      }
    }
    
    .mobile-actions {
      margin-top: 20px;
      
      .el-button {
        width: 100%;
      }
    }
  }
}

.website-main {
  min-height: calc(100vh - 70px);
}

.website-footer {
  background: var(--text-primary);
  color: white;
  
  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 24px 20px;
  }
  
  .footer-content {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 40px;
    margin-bottom: 40px;
  }
  
  .footer-section {
    h4 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 20px;
      color: white;
    }
  }
  
  .footer-logo {
    display: flex;
    align-items: center;
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 16px;
    
    .logo-icon {
      width: 32px;
      height: 32px;
      background: var(--primary-gradient);
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      font-size: 16px;
    }
  }
  
  .footer-desc {
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.6;
    margin-bottom: 20px;
  }
  
  .social-links {
    display: flex;
    gap: 12px;
    
    .el-button {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: white;
      
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
      }
    }
  }
  
  .footer-links {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      margin-bottom: 12px;
      
      a {
        color: rgba(255, 255, 255, 0.8);
        text-decoration: none;
        transition: color 0.2s ease;
        
        &:hover {
          color: white;
        }
      }
    }
  }
  
  .footer-bottom {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: rgba(255, 255, 255, 0.6);
    
    .footer-bottom-links {
      display: flex;
      gap: 24px;
      
      a {
        color: rgba(255, 255, 255, 0.6);
        text-decoration: none;
        
        &:hover {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .website-header {
    .nav-menu {
      display: none;
    }
    
    .header-actions {
      .mobile-menu-btn {
        display: block;
      }
      
      .cta-btn {
        display: none;
      }
    }
  }
  
  .website-footer {
    .footer-content {
      grid-template-columns: 1fr;
      gap: 30px;
    }
    
    .footer-bottom {
      flex-direction: column;
      gap: 16px;
      text-align: center;
      
      .footer-bottom-links {
        justify-content: center;
      }
    }
  }
}
</style>