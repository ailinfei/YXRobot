/**
 * 用户认证服务
 * 管理用户登录、登出、权限验证等功能
 */

export interface UserInfo {
  id: string
  username: string
  name: string
  email?: string
  role: string
  avatar: string
  permissions: string[]
  loginTime?: string
  lastActiveTime?: string
}

export interface LoginCredentials {
  username: string
  password: string
  remember?: boolean
}

export interface LoginResponse {
  success: boolean
  message: string
  data?: {
    token: string
    userInfo: UserInfo
  }
}

class AuthService {
  private readonly TOKEN_KEY = 'token'
  private readonly USER_INFO_KEY = 'userInfo'
  private readonly REMEMBER_KEY = 'remember_login'

  /**
   * 用户登录
   */
  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    try {
      // 模拟API调用延迟
      await new Promise(resolve => setTimeout(resolve, 1000))

      // 模拟登录验证
      if (credentials.username === 'admin' && credentials.password === '123456') {
        const userInfo: UserInfo = {
          id: '1',
          username: credentials.username,
          name: '系统管理员',
          email: 'admin@yxrobot.com',
          role: '超级管理员',
          avatar: 'https://ui-avatars.com/api/?name=Admin&background=6366f1&color=ffffff&size=100',
          permissions: ['admin:all', 'dashboard:view', 'content:manage', 'business:manage', 'system:manage'],
          loginTime: new Date().toISOString(),
          lastActiveTime: new Date().toISOString()
        }

        const token = 'mock-jwt-token'

        // 存储认证信息
        this.setToken(token)
        this.setUserInfo(userInfo)

        // 如果选择记住登录，设置记住标记
        if (credentials.remember) {
          localStorage.setItem(this.REMEMBER_KEY, 'true')
        }

        return {
          success: true,
          message: '登录成功',
          data: {
            token,
            userInfo
          }
        }
      } else {
        return {
          success: false,
          message: '用户名或密码错误'
        }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return {
        success: false,
        message: '登录失败，请稍后重试'
      }
    }
  }

  /**
   * 用户登出
   */
  logout(): void {
    // 清除所有认证信息
    localStorage.removeItem(this.TOKEN_KEY)
    localStorage.removeItem(this.USER_INFO_KEY)
    localStorage.removeItem(this.REMEMBER_KEY)
    
    // 可以在这里调用后端登出API
    console.log('用户已登出')
  }

  /**
   * 检查用户是否已登录
   */
  isAuthenticated(): boolean {
    const token = this.getToken()
    const userInfo = this.getUserInfo()
    return !!(token && userInfo)
  }

  /**
   * 验证用户权限
   */
  hasPermission(permission: string): boolean {
    const userInfo = this.getUserInfo()
    if (!userInfo || !userInfo.permissions) {
      return false
    }

    // 超级管理员拥有所有权限
    if (userInfo.permissions.includes('admin:all')) {
      return true
    }

    return userInfo.permissions.includes(permission)
  }

  /**
   * 验证用户角色
   */
  hasRole(role: string): boolean {
    const userInfo = this.getUserInfo()
    return userInfo?.role === role
  }

  /**
   * 获取当前用户信息
   */
  getCurrentUser(): UserInfo | null {
    return this.getUserInfo()
  }

  /**
   * 更新用户信息
   */
  updateUserInfo(userInfo: Partial<UserInfo>): void {
    const currentUser = this.getUserInfo()
    if (currentUser) {
      const updatedUser = { ...currentUser, ...userInfo }
      this.setUserInfo(updatedUser)
    }
  }

  /**
   * 刷新用户最后活跃时间
   */
  refreshLastActiveTime(): void {
    const userInfo = this.getUserInfo()
    if (userInfo) {
      userInfo.lastActiveTime = new Date().toISOString()
      this.setUserInfo(userInfo)
    }
  }

  /**
   * 验证Token是否有效
   */
  validateToken(): boolean {
    const token = this.getToken()
    if (!token) {
      return false
    }

    // 这里可以添加更复杂的token验证逻辑
    // 比如检查token是否过期、格式是否正确等
    if (token === 'mock-jwt-token') {
      return true
    }

    return false
  }

  /**
   * 获取Token
   */
  private getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY)
  }

  /**
   * 设置Token
   */
  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token)
  }

  /**
   * 获取用户信息
   */
  private getUserInfo(): UserInfo | null {
    const userInfoStr = localStorage.getItem(this.USER_INFO_KEY)
    if (!userInfoStr) {
      return null
    }

    try {
      return JSON.parse(userInfoStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      // 清除无效的用户信息
      localStorage.removeItem(this.USER_INFO_KEY)
      return null
    }
  }

  /**
   * 设置用户信息
   */
  private setUserInfo(userInfo: UserInfo): void {
    localStorage.setItem(this.USER_INFO_KEY, JSON.stringify(userInfo))
  }

  /**
   * 检查是否记住登录
   */
  isRememberLogin(): boolean {
    return localStorage.getItem(this.REMEMBER_KEY) === 'true'
  }
}

// 创建单例实例
export const authService = new AuthService()

// 默认导出
export default authService