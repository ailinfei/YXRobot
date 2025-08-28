# 产品创建API文档

## 接口概述

创建新的产品记录，支持两种创建方式：JSON格式（不含文件）和表单格式（支持文件上传）。

## 接口信息

- **接口路径**: `/api/v1/products`
- **请求方法**: `POST`
- **接口描述**: 创建新产品，支持基本信息录入和封面图片上传

## 请求方式

### 方式一：JSON格式（不含文件）

**Content-Type**: `application/json`

#### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| name | String | 是 | 产品名称，最大200字符 | "家用练字机器人" |
| model | String | 是 | 产品型号，最大100字符 | "YX-HOME-001" |
| description | String | 否 | 产品描述，最大5000字符 | "适合家庭使用的智能练字机器人" |
| price | BigDecimal | 是 | 产品价格，必须≥0 | 2999.99 |
| status | String | 是 | 产品状态：draft/published/archived | "draft" |
| coverImageUrl | String | 否 | 封面图片URL | "https://example.com/image.jpg" |

#### 请求示例

```http
POST /api/v1/products HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "name": "家用练字机器人",
  "model": "YX-HOME-001",
  "description": "适合家庭使用的智能练字机器人，支持多种字体和练习模式",
  "price": 2999.99,
  "status": "draft"
}
```

### 方式二：表单格式（支持文件上传）

**Content-Type**: `multipart/form-data`

#### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 限制 |
|--------|------|------|------|------|
| name | String | 是 | 产品名称 | 最大200字符 |
| model | String | 是 | 产品型号 | 最大100字符 |
| description | String | 否 | 产品描述 | 最大5000字符 |
| price | String | 是 | 产品价格 | 数字格式，≥0 |
| status | String | 是 | 产品状态 | draft/published/archived |
| coverFiles | File[] | 否 | 封面图片文件 | 图片格式，最大10MB |

#### 文件上传限制

**支持的图片格式**:
- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- WebP (.webp)

**文件大小限制**:
- 图片文件：最大 10MB
- 单次上传：支持多个文件，但只使用第一个作为封面

#### 请求示例

```http
POST /api/v1/products HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="name"

家用练字机器人
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="model"

YX-HOME-001
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="description"

适合家庭使用的智能练字机器人，支持多种字体和练习模式
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="price"

2999.99
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="status"

published
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="coverFiles"; filename="cover.jpg"
Content-Type: image/jpeg

[图片二进制数据]
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

## 响应结果

### 成功响应 (200)

```json
{
  "code": 200,
  "message": "产品创建成功",
  "data": {
    "id": 1,
    "name": "家用练字机器人",
    "model": "YX-HOME-001",
    "description": "适合家庭使用的智能练字机器人，支持多种字体和练习模式",
    "price": 2999.99,
    "coverImageUrl": "/api/v1/files/products/covers/2024/12/19/abc123def456.jpg",
    "status": "published",
    "createdAt": "2024-12-19 10:30:00",
    "updatedAt": "2024-12-19 10:30:00"
  },
  "timestamp": "2024-12-19T10:30:00Z"
}
```

### 参数验证错误 (400)

```json
{
  "code": 400,
  "message": "产品名称不能为空",
  "data": null,
  "timestamp": "2024-12-19T10:30:00Z"
}
```

### 业务逻辑错误 (400)

```json
{
  "code": 400,
  "message": "产品型号已存在: YX-HOME-001",
  "data": null,
  "timestamp": "2024-12-19T10:30:00Z"
}
```

### 文件上传错误 (400)

```json
{
  "code": 400,
  "message": "图片文件大小不能超过 10 MB",
  "data": null,
  "timestamp": "2024-12-19T10:30:00Z"
}
```

### 系统错误 (500)

```json
{
  "code": 500,
  "message": "创建产品失败",
  "data": null,
  "timestamp": "2024-12-19T10:30:00Z"
}
```

## 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 创建的产品数据 |
| data.id | Long | 产品ID（系统生成） |
| data.name | String | 产品名称 |
| data.model | String | 产品型号 |
| data.description | String | 产品描述 |
| data.price | BigDecimal | 产品价格 |
| data.coverImageUrl | String | 封面图片URL（如果上传了文件） |
| data.status | String | 产品状态 |
| data.createdAt | DateTime | 创建时间 |
| data.updatedAt | DateTime | 更新时间 |
| timestamp | DateTime | 响应时间戳 |

## 业务规则

### 数据验证规则

1. **产品名称**: 必填，1-200字符
2. **产品型号**: 必填，1-100字符，系统内唯一
3. **产品价格**: 必填，数值≥0，最多8位整数2位小数
4. **产品状态**: 必填，只能是 draft、published、archived
5. **产品描述**: 可选，最多5000字符

### 文件上传规则

1. **文件格式**: 只支持图片格式（JPEG、PNG、GIF、WebP）
2. **文件大小**: 单个图片文件最大10MB
3. **文件数量**: 支持多文件上传，但只使用第一个作为封面
4. **文件存储**: 自动生成唯一文件名，按日期分目录存储
5. **失败回滚**: 如果数据库操作失败，会自动删除已上传的文件

### 业务逻辑规则

1. **型号唯一性**: 产品型号在系统中必须唯一
2. **状态管理**: 新创建的产品默认为草稿状态
3. **时间戳**: 系统自动设置创建时间和更新时间
4. **软删除**: 产品支持软删除机制

## 错误处理

### 参数验证错误

- 必填字段为空
- 字段长度超限
- 数据格式不正确
- 枚举值不在允许范围内

### 业务逻辑错误

- 产品型号重复
- 文件格式不支持
- 文件大小超限

### 系统错误

- 数据库连接异常
- 文件存储异常
- 网络超时

## 使用示例

### cURL 示例

#### JSON格式创建

```bash
curl -X POST "http://localhost:8080/api/v1/products" \
     -H "Content-Type: application/json" \
     -d '{
       "name": "家用练字机器人",
       "model": "YX-HOME-001",
       "description": "适合家庭使用的智能练字机器人",
       "price": 2999.99,
       "status": "draft"
     }'
```

#### 文件上传格式创建

```bash
curl -X POST "http://localhost:8080/api/v1/products" \
     -F "name=家用练字机器人" \
     -F "model=YX-HOME-002" \
     -F "description=支持文件上传的产品" \
     -F "price=2999.99" \
     -F "status=published" \
     -F "coverFiles=@/path/to/cover.jpg"
```

### JavaScript 示例

#### 使用 fetch API（JSON格式）

```javascript
async function createProduct(productData) {
  try {
    const response = await fetch('/api/v1/products', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(productData)
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('产品创建成功:', result.data);
      return result.data;
    } else {
      console.error('创建失败:', result.message);
      return null;
    }
  } catch (error) {
    console.error('请求异常:', error);
    return null;
  }
}

// 使用示例
const productData = {
  name: '家用练字机器人',
  model: 'YX-HOME-001',
  description: '适合家庭使用的智能练字机器人',
  price: 2999.99,
  status: 'draft'
};

createProduct(productData);
```

#### 使用 FormData（文件上传）

```javascript
async function createProductWithFile(productData, coverFile) {
  try {
    const formData = new FormData();
    formData.append('name', productData.name);
    formData.append('model', productData.model);
    formData.append('description', productData.description);
    formData.append('price', productData.price.toString());
    formData.append('status', productData.status);
    
    if (coverFile) {
      formData.append('coverFiles', coverFile);
    }
    
    const response = await fetch('/api/v1/products', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('产品创建成功:', result.data);
      return result.data;
    } else {
      console.error('创建失败:', result.message);
      return null;
    }
  } catch (error) {
    console.error('请求异常:', error);
    return null;
  }
}

// 使用示例
const productData = {
  name: '家用练字机器人',
  model: 'YX-HOME-002',
  description: '支持文件上传的产品',
  price: 2999.99,
  status: 'published'
};

const fileInput = document.getElementById('coverFile');
const coverFile = fileInput.files[0];

createProductWithFile(productData, coverFile);
```

### Vue.js 示例

```vue
<template>
  <div class="product-create">
    <form @submit.prevent="submitProduct">
      <div class="form-group">
        <label>产品名称 *</label>
        <input v-model="form.name" type="text" required maxlength="200" />
      </div>
      
      <div class="form-group">
        <label>产品型号 *</label>
        <input v-model="form.model" type="text" required maxlength="100" />
      </div>
      
      <div class="form-group">
        <label>产品描述</label>
        <textarea v-model="form.description" maxlength="5000"></textarea>
      </div>
      
      <div class="form-group">
        <label>产品价格 *</label>
        <input v-model="form.price" type="number" step="0.01" min="0" required />
      </div>
      
      <div class="form-group">
        <label>产品状态 *</label>
        <select v-model="form.status" required>
          <option value="draft">草稿</option>
          <option value="published">已发布</option>
          <option value="archived">已归档</option>
        </select>
      </div>
      
      <div class="form-group">
        <label>封面图片</label>
        <input @change="handleFileChange" type="file" accept="image/*" />
      </div>
      
      <button type="submit" :disabled="loading">
        {{ loading ? '创建中...' : '创建产品' }}
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'ProductCreate',
  data() {
    return {
      form: {
        name: '',
        model: '',
        description: '',
        price: 0,
        status: 'draft'
      },
      coverFile: null,
      loading: false
    };
  },
  methods: {
    handleFileChange(event) {
      this.coverFile = event.target.files[0];
    },
    
    async submitProduct() {
      this.loading = true;
      
      try {
        let result;
        
        if (this.coverFile) {
          // 使用文件上传方式
          result = await this.createProductWithFile();
        } else {
          // 使用JSON方式
          result = await this.createProductJSON();
        }
        
        if (result) {
          this.$message.success('产品创建成功');
          this.$router.push('/products');
        }
      } catch (error) {
        this.$message.error('创建产品失败');
      } finally {
        this.loading = false;
      }
    },
    
    async createProductJSON() {
      const response = await fetch('/api/v1/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(this.form)
      });
      
      const result = await response.json();
      return result.code === 200 ? result.data : null;
    },
    
    async createProductWithFile() {
      const formData = new FormData();
      Object.keys(this.form).forEach(key => {
        formData.append(key, this.form[key]);
      });
      formData.append('coverFiles', this.coverFile);
      
      const response = await fetch('/api/v1/products', {
        method: 'POST',
        body: formData
      });
      
      const result = await response.json();
      return result.code === 200 ? result.data : null;
    }
  }
};
</script>
```

## 性能说明

- **接口响应时间**: < 200ms（不含文件上传）
- **文件上传时间**: 根据文件大小，通常 < 5秒
- **并发支持**: 支持1000+并发请求
- **文件存储**: 使用日期分目录，避免单目录文件过多
- **事务处理**: 确保数据一致性，失败时自动回滚

## 注意事项

1. **型号唯一性**: 产品型号必须在系统中唯一，建议使用有意义的编码规则
2. **文件上传**: 建议在前端进行文件大小和格式预检查
3. **错误处理**: 前端应实现完善的错误处理和用户提示
4. **数据验证**: 前后端都应进行数据验证，确保数据安全
5. **文件清理**: 系统会自动清理上传失败的文件
6. **状态管理**: 新创建的产品建议先设为草稿状态，审核后再发布

## 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2024-12-19 | 初始版本，支持JSON和文件上传两种创建方式 |