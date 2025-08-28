# 图片资源说明

本目录包含项目中使用的所有图片资源。

## 目录结构

```
images/
├── avatars/           # 用户头像
│   └── default-avatar.svg
├── charity/           # 公益项目相关图片
│   ├── teaching-01.jpg
│   ├── activity-01.jpg
│   ├── equipment-01.jpg
│   ├── people-01.jpg
│   └── *-thumb.jpg    # 缩略图
├── products/          # 产品相关图片
│   ├── robot-main.jpg
│   ├── robot-detail-1.jpg
│   └── robot-detail-2.jpg
├── states/            # 状态图片
│   ├── no-data.svg
│   ├── loading.svg
│   └── 404.svg
└── visit/             # 访问记录图片
    ├── visit_1_1.jpg
    ├── visit_1_2.jpg
    └── visit_1_3.jpg
```

## 图片规格要求

### 产品图片
- **主图**: 800x600px, JPG格式, 质量85%
- **详情图**: 600x400px, JPG格式, 质量85%
- **缩略图**: 200x150px, JPG格式, 质量80%

### 用户头像
- **默认头像**: 60x60px, SVG格式
- **用户上传**: 最大100x100px, 支持JPG/PNG

### 公益项目图片
- **教学点图片**: 800x600px, JPG格式
- **活动照片**: 600x400px, JPG格式
- **设备图片**: 600x400px, JPG格式
- **人物照片**: 600x400px, JPG格式

### 状态图片
- **无数据**: SVG格式, 200x150px
- **加载中**: SVG格式, 100x100px (带动画)
- **404错误**: SVG格式, 300x200px

## 使用说明

### 1. 在Vue组件中使用
```vue
<template>
  <img :src="getImageUrl('/images/products/robot-main.jpg')" alt="产品图片" />
</template>

<script setup>
import { getImageUrl } from '@/utils/imageUtils'
</script>
```

### 2. 懒加载使用
```vue
<template>
  <img v-lazy="'/images/products/robot-main.jpg'" alt="产品图片" />
</template>
```

### 3. 占位图片
```vue
<template>
  <img :src="generatePlaceholder(400, 300, '产品图片')" alt="占位图" />
</template>

<script setup>
import { generatePlaceholder } from '@/utils/imageUtils'
</script>
```

## 图片优化建议

1. **格式选择**
   - 照片类图片使用JPG格式
   - 图标、Logo使用SVG格式
   - 需要透明背景的使用PNG格式

2. **尺寸优化**
   - 根据实际显示尺寸提供合适的图片
   - 提供2x版本支持高分辨率屏幕
   - 使用响应式图片技术

3. **压缩优化**
   - JPG质量控制在80-90%
   - 使用工具进行无损压缩
   - 考虑使用WebP格式

4. **加载优化**
   - 重要图片进行预加载
   - 非关键图片使用懒加载
   - 提供合适的占位图

## 替换说明

当前目录中的图片文件都是占位文件，实际项目中需要替换为真实的图片：

1. **Logo和品牌图片**: 使用公司实际的Logo和品牌素材
2. **产品图片**: 使用练字机器人的实际产品照片
3. **公益项目图片**: 使用真实的教学点和活动照片
4. **用户头像**: 可以保持默认SVG，或使用更精美的设计

## 版权说明

- 所有图片资源应确保拥有使用权限
- 避免使用有版权争议的图片
- 建议使用自有素材或购买正版图片
- 可以使用免费的图片资源网站（如Unsplash、Pexels等）

## 维护建议

1. 定期检查图片链接的有效性
2. 监控图片加载性能
3. 及时更新过时的图片内容
4. 保持图片风格的一致性