/**
 * 图片资源配置文件
 * 统一管理项目中使用的图片资源
 */

// Logo相关
export const logoImages = {
  main: 'https://via.placeholder.com/200x60/f5f5f5/333333?text=YX+Robot+Logo',
  text: 'https://via.placeholder.com/200x60/f5f5f5/333333?text=YX+Robot+Logo'
}

// 状态图片
export const stateImages = {
  noData: 'https://via.placeholder.com/200x150/ffebee/f44336?text=No+Data',
  loading: 'https://via.placeholder.com/200x150/e3f2fd/2196f3?text=Loading...',
  error404: 'https://via.placeholder.com/400x300/ffebee/f44336?text=404+Not+Found'
}

// 产品图片
export const productImages = {
  main: 'https://picsum.photos/600/400?random=robot1',
  detail1: 'https://picsum.photos/500/350?random=robot2',
  detail2: 'https://picsum.photos/500/350?random=robot3'
}

// 公益项目图片
export const charityImages = {
  teaching: 'https://picsum.photos/400/300?random=teaching1',
  activity: 'https://picsum.photos/400/300?random=activity1',
  equipment: 'https://picsum.photos/400/300?random=equipment1',
  people: 'https://picsum.photos/400/300?random=people1',
  // 缩略图
  teachingThumb: 'https://picsum.photos/200/150?random=teaching1',
  activityThumb: 'https://picsum.photos/200/150?random=activity1',
  equipmentThumb: 'https://picsum.photos/200/150?random=equipment1',
  peopleThumb: 'https://picsum.photos/200/150?random=people1'
}

// 用户头像
export const avatarImages = {
  default: 'https://ui-avatars.com/api/?name=User&background=e0e0e0&color=999999&size=100'
}

// 访问记录图片
export const visitImages = {
  visit1_1: 'https://picsum.photos/400/300?random=visit1',
  visit1_2: 'https://picsum.photos/400/300?random=visit2',
  visit1_3: 'https://picsum.photos/400/300?random=visit3'
}

// 图片预加载列表
export const preloadImages = [
  logoImages.main,
  stateImages.loading,
  avatarImages.default
]

// 图片懒加载配置
export const lazyLoadConfig = {
  placeholder: stateImages.loading,
  error: stateImages.noData,
  loading: stateImages.loading
}

// 图片尺寸配置
export const imageSizes = {
  avatar: {
    small: '40x40',
    medium: '60x60',
    large: '100x100'
  },
  thumbnail: {
    small: '150x100',
    medium: '200x150',
    large: '300x200'
  },
  product: {
    small: '400x300',
    medium: '600x400',
    large: '800x600'
  }
}

// 支持的图片格式
export const supportedFormats = [
  'jpg',
  'jpeg',
  'png',
  'gif',
  'svg',
  'webp'
]

// 图片上传配置
export const uploadConfig = {
  maxSize: 5 * 1024 * 1024, // 5MB
  acceptedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/svg+xml'],
  quality: 0.8,
  maxWidth: 1920,
  maxHeight: 1080
}

// 图片验证和回退函数
export const validateImageUrl = (url: string): Promise<string> => {
  return new Promise((resolve) => {
    const img = new Image()
    img.onload = () => resolve(url)
    img.onerror = () => resolve(stateImages.noData)
    img.src = url
  })
}

// 获取安全的图片URL
export const getSafeImageUrl = async (url: string, fallback?: string): Promise<string> => {
  try {
    return await validateImageUrl(url)
  } catch {
    return fallback || stateImages.noData
  }
}

// 批量验证图片
export const validateImages = async (urls: string[]): Promise<string[]> => {
  const promises = urls.map(url => validateImageUrl(url))
  return Promise.all(promises)
}