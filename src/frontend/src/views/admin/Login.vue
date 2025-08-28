<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo">
            <div class="logo-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <span class="logo-text">练字机器人</span>
          </div>
          <h1 class="brand-title">管理后台</h1>
          <p class="brand-subtitle">智能练字机器人管理系统</p>
          
          <!-- 特色功能展示 -->
          <div class="features">
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </div>
            <div class="feature-item">
              <el-icon><Monitor /></el-icon>
              <span>设备监控</span>
            </div>
            <div class="feature-item">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-section">
        <div class="login-form-container">
          <div class="login-header">
            <h2>欢迎回来</h2>
            <p>请登录您的管理员账户</p>
          </div>

          <el-form 
            :model="loginForm" 
            :rules="loginRules" 
            ref="loginFormRef"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="用户名或邮箱"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                size="large"
                :prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <el-link type="primary" @click="handleForgotPassword">忘记密码？</el-link>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn"
                :loading="loginLoading"
                @click="handleLogin"
              >
                {{ loginLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>


        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Edit,
  DataAnalysis,
  Monitor,
  User,
  Lock
} from '@element-plus/icons-vue'
import authService from '@/services/authService'

const router = useRouter()
const loginFormRef = ref()
const loginLoading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loginLoading.value = true
    
    // 使用认证服务进行登录
    const response = await authService.login({
      username: loginForm.username,
      password: loginForm.password,
      remember: loginForm.remember
    })
    
    loginLoading.value = false
    
    if (response.success) {
      ElMessage.success(response.message)
      
      // 跳转到管理后台
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    loginLoading.value = false
    console.log('表单验证失败:', error)
  }
}

// 处理忘记密码
const handleForgotPassword = () => {
  ElMessage.info('请联系系统管理员重置密码')
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 900px;
  width: 100%;
  min-height: 600px;
}

.brand-section {
  background: var(--primary-gradient);
  color: white;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
  }
  
  .brand-content {
    position: relative;
    z-index: 1;
    
    .logo {
      display: flex;
      align-items: center;
      margin-bottom: 32px;
      
      .logo-icon {
        width: 48px;
        height: 48px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: var(--radius-lg);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;
      }
      
      .logo-text {
        font-size: 24px;
        font-weight: 700;
      }
    }
    
    .brand-title {
      font-size: 36px;
      font-weight: 800;
      margin-bottom: 16px;
      line-height: 1.2;
    }
    
    .brand-subtitle {
      font-size: 18px;
      opacity: 0.9;
      margin-bottom: 48px;
      line-height: 1.5;
    }
    
    .features {
      display: flex;
      flex-direction: column;
      gap: 20px;
      
      .feature-item {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 16px;
        
        .el-icon {
          font-size: 20px;
          opacity: 0.8;
        }
      }
    }
  }
}

.login-section {
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form-container {
  max-width: 320px;
  margin: 0 auto;
  width: 100%;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 16px;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
    
    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: var(--radius-lg);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        border: 1px solid var(--border-color);
        
        &:hover {
          border-color: var(--primary-color);
        }
        
        &.is-focus {
          border-color: var(--primary-color);
          box-shadow: 0 0 0 2px rgba(255, 90, 95, 0.2);
        }
      }
    }
  }
  
  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  
  .login-btn {
    width: 100%;
    height: 48px;
    background: var(--primary-gradient);
    border: none;
    border-radius: var(--radius-lg);
    font-size: 16px;
    font-weight: 600;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 8px 16px rgba(255, 90, 95, 0.3);
    }
  }
}

.other-login {
  margin-top: 32px;
  
  .divider {
    text-align: center;
    margin-bottom: 24px;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: var(--border-color);
    }
    
    span {
      background: white;
      padding: 0 16px;
      color: var(--text-secondary);
      font-size: 14px;
      position: relative;
    }
  }
  
  .social-login {
    display: flex;
    gap: 12px;
    
    .social-btn {
      flex: 1;
      height: 44px;
      border: 1px solid var(--border-color);
      background: transparent;
      color: var(--text-secondary);
      border-radius: var(--radius-lg);
      
      &:hover {
        border-color: var(--primary-color);
        color: var(--primary-color);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    grid-template-columns: 1fr;
    max-width: 400px;
  }
  
  .brand-section {
    display: none;
  }
  
  .login-section {
    padding: 40px 20px;
  }
}
</style>