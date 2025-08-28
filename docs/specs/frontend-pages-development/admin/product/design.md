# Admin Content - 产品管理页面设计文档

## 概述

产品管理页面是 YXRobot 管理后台内容管理模块的核心功能，采用现代化的前后端分离架构，为管理员提供练字机器人产品信息的管理。该页面已完成前端开发，包含产品列表展示、产品编辑、媒体管理等功能，需要开发对应的后端API支持。

## 架构

### 系统架构
```
┌─────────────────────────────────────────────────────────────┐
│                    前端展示层 (Vue.js 3)                      │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │   产品列表页面    │ │   产品编辑页面    │ │   分类管理页面    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    API接口层 (RESTful)                       │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │   产品管理API    │ │   分类管理API    │ │   文件上传API    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 业务逻辑层 (Spring MVC)                       │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │  ProductService │ │ CategoryService │ │ InventoryService│ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 数据访问层 (MyBatis)                          │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │  ProductMapper  │ │ CategoryMapper  │ │InventoryMapper  │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 数据存储层 (MySQL 9.3)                        │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │   products表     │ │  categories表   │ │  inventory表    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈选择

**前端技术栈:**
- **框架**: Vue.js 3 + TypeScript - 提供现代化的响应式开发体验
- **UI组件库**: Element Plus - 统一的设计语言和丰富的表单组件
- **富文本编辑器**: Quill.js - 强大的产品描述编辑功能
- **图片处理**: Vue-Cropper - 产品图片裁剪和处理
- **文件上传**: Element Plus Upload - 多文件上传和进度管理
- **状态管理**: Pinia - 轻量级的状态管理方案
- **路由管理**: Vue Router 4 - 页面路由和权限控制

**后端技术栈:**
- **框架**: Spring MVC + MyBatis - 成熟稳定的企业级框架
- **文件存储**: 本地存储 + 云存储 - 支持多种存储方案
- **图片处理**: ImageIO + Thumbnailator - 图片压缩和缩略图生成
- **数据验证**: Hibernate Validator - 数据完整性验证
- **缓存**: Redis - 产品信息和分类数据缓存
- **搜索**: MySQL 全文索引 - 产品搜索功能

## 组件和接口

### 前端组件架构（基于实际实现）
```
Products.vue (产品管理主页面)
├── 页面头部 (page-header)
│   ├── 标题和描述 (header-left)
│   └── 新增产品按钮 (header-right)
├── DataTable (数据表格组件)
│   ├── 产品封面图片 (cover_image_url slot)
│   ├── StatusTag (产品状态组件)
│   ├── 价格显示 (price slot)
│   ├── 操作列 (actions slot)
│   │   ├── 编辑按钮
│   │   ├── 媒体管理按钮
│   │   └── 删除按钮
│   ├── 批量选择功能 (show-selection)
│   └── 分页功能 (show-pagination)
├── CommonDialog (产品编辑对话框)
│   └── FormValidator (表单验证组件)
│       ├── 产品名称输入框 (el-input)
│       ├── 产品型号输入框 (el-input)
│       ├── 产品价格输入框 (el-input-number)
│       ├── 产品状态选择器 (el-select)
│       ├── 产品描述文本域 (el-input textarea)
│       └── FileUpload (封面上传组件)
├── CommonDialog (媒体管理对话框)
│   └── 媒体标签页 (el-tabs)
│       ├── 产品图片标签页 (images)
│       │   ├── 图片网格展示 (media-grid)
│       │   ├── 图片预览 (el-image)
│       │   ├── 编辑删除按钮 (media-actions)
│       │   └── 排序显示 (media-sort)
│       └── 产品视频标签页 (videos)
│           ├── 视频网格展示 (media-grid)
│           ├── 视频预览 (video)
│           ├── 编辑删除按钮 (media-actions)
│           └── 排序显示 (media-sort)
└── CommonDialog (媒体上传对话框)
    └── FileUpload (文件上传组件)
        ├── 图片上传 (image/*)
        ├── 视频上传 (video/*)
        └── 排序设置 (el-input-number)
```

### API接口设计（基于前端实现）

#### 产品管理API

**1. 获取产品列表**
```http
GET /api/admin/products
Authorization: Bearer {token}
Query Parameters:
- page: int (页码，默认1)
- size: int (每页数量，默认20)
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "家用练字机器人",
        "model": "YX-HOME-001",
        "description": "适合家庭使用的智能练字机器人，支持多种字体和练习模式",
        "price": 2999.00,
        "cover_image_url": "https://via.placeholder.com/150x150",
        "status": "published",
        "created_at": "2024-01-15 10:30:00",
        "updated_at": "2024-01-20 14:20:00"
      }
    ],
    "total": 156,
    "page": 1,
    "size": 20
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

**2. 获取产品详情**
```http
GET /api/admin/products/{id}
Authorization: Bearer {token}
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "家用练字机器人",
    "model": "YX-HOME-001",
    "description": "适合家庭使用的智能练字机器人，支持多种字体和练习模式",
    "price": 2999.00,
    "cover_image_url": "https://via.placeholder.com/150x150",
    "status": "published",
    "created_at": "2024-01-15 10:30:00",
    "updated_at": "2024-01-20 14:20:00"
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

**3. 创建产品**
```http
POST /api/admin/products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "产品名称",
  "model": "产品型号",
  "description": "产品描述",
  "price": 2999.00,
  "status": "draft",
  "cover_files": []
}
```

**4. 更新产品**
```http
PUT /api/admin/products/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "产品名称",
  "model": "产品型号", 
  "description": "产品描述",
  "price": 2999.00,
  "status": "published",
  "cover_files": []
}
```

**5. 删除产品**
```http
DELETE /api/admin/products/{id}
Authorization: Bearer {token}
```

**6. 批量删除产品**
```http
POST /api/admin/products/batch-delete
Authorization: Bearer {token}
Content-Type: application/json

{
  "productIds": [1, 2, 3]
}
```

#### 产品媒体管理API

**1. 获取产品媒体列表**
```http
GET /api/admin/products/{productId}/media
Authorization: Bearer {token}
Query Parameters:
- type: string (媒体类型: image,video)
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "images": [
      {
        "id": 1,
        "product_id": 1,
        "media_type": "image",
        "media_url": "https://via.placeholder.com/300x200",
        "sort_order": 1
      }
    ],
    "videos": [
      {
        "id": 3,
        "product_id": 1,
        "media_type": "video",
        "media_url": "https://www.w3schools.com/html/mov_bbb.mp4",
        "sort_order": 1
      }
    ]
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

**2. 上传产品媒体**
```http
POST /api/admin/products/{productId}/media
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: File (媒体文件)
media_type: string (image 或 video)
sort_order: int (排序值)
```

**3. 更新媒体信息**
```http
PUT /api/admin/products/media/{mediaId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "sort_order": 2
}
```

**4. 删除产品媒体**
```http
DELETE /api/admin/products/media/{mediaId}
Authorization: Bearer {token}
```

#### 分类管理API

**1. 获取分类树**
```http
GET /api/categories/tree
Authorization: Bearer {token}
```

**2. 创建分类**
```http
POST /api/categories
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "分类名称",
  "parentId": 0,
  "description": "分类描述",
  "sortOrder": 1,
  "isEnabled": true
}
```

#### 文件上传API

**1. 上传产品图片**
```http
POST /api/upload/product-images
Authorization: Bearer {token}
Content-Type: multipart/form-data

files: File[] (支持多文件上传)
```

**2. 上传产品文档**
```http
POST /api/upload/product-documents
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

### 后端服务架构

**控制器层 (Controller)**
```java
/**
 * 产品管理控制器
 * 负责处理产品相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/admin/products")
@Validated
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 获取产品列表
     * 支持分页查询、搜索和筛选
     * 
     * @param queryDTO 查询条件
     * @return Result<PageResult<ProductDTO>> 产品列表响应
     */
    @GetMapping
    public Result<PageResult<ProductDTO>> getProducts(@Valid ProductQueryDTO queryDTO) {
        PageResult<ProductDTO> products = productService.getProducts(queryDTO);
        return Result.success(products);
    }
    
    /**
     * 获取产品详情
     * 包含完整的产品信息和关联数据
     * 
     * @param id 产品ID
     * @return Result<ProductDetailDTO> 产品详情响应
     */
    @GetMapping("/{id}")
    public Result<ProductDetailDTO> getProduct(@PathVariable Long id) {
        ProductDetailDTO product = productService.getProductDetail(id);
        return Result.success(product);
    }
    
    /**
     * 创建产品
     * 创建新的产品记录
     * 
     * @param createDTO 产品创建数据
     * @return Result<ProductDTO> 创建结果响应
     */
    @PostMapping
    public Result<ProductDTO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductDTO product = productService.createProduct(createDTO);
        return Result.success(product);
    }
    
    /**
     * 更新产品
     * 更新现有产品信息
     * 
     * @param id 产品ID
     * @param updateDTO 产品更新数据
     * @return Result<ProductDTO> 更新结果响应
     */
    @PutMapping("/{id}")
    public Result<ProductDTO> updateProduct(@PathVariable Long id, 
                                          @Valid @RequestBody ProductUpdateDTO updateDTO) {
        ProductDTO product = productService.updateProduct(id, updateDTO);
        return Result.success(product);
    }
    
    /**
     * 删除产品
     * 软删除产品记录
     * 
     * @param id 产品ID
     * @return Result<Void> 删除结果响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }
    
    /**
     * 批量操作产品
     * 支持批量状态更新、删除等操作
     * 
     * @param batchDTO 批量操作数据
     * @return Result<BatchOperationResultDTO> 批量操作结果
     */
    @PostMapping("/batch")
    public Result<BatchOperationResultDTO> batchOperation(@Valid @RequestBody ProductBatchDTO batchDTO) {
        BatchOperationResultDTO result = productService.batchOperation(batchDTO);
        return Result.success(result);
    }
}
```

**服务层 (Service)**
```java
/**
 * 产品业务服务类
 * 负责处理产品相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 获取产品列表
     * 支持分页查询、搜索和多条件筛选
     * 
     * @param queryDTO 查询条件
     * @return PageResult<ProductDTO> 分页产品列表
     */
    @Transactional(readOnly = true)
    public PageResult<ProductDTO> getProducts(ProductQueryDTO queryDTO) {
        // 构建查询条件
        ProductQuery query = buildProductQuery(queryDTO);
        
        // 执行分页查询
        List<Product> products = productMapper.selectByQuery(query);
        Long total = productMapper.countByQuery(query);
        
        // 转换为DTO
        List<ProductDTO> productDTOs = products.stream()
            .map(this::convertToProductDTO)
            .collect(Collectors.toList());
        
        return PageResult.<ProductDTO>builder()
            .list(productDTOs)
            .total(total)
            .page(queryDTO.getPage())
            .size(queryDTO.getSize())
            .totalPages((int) Math.ceil((double) total / queryDTO.getSize()))
            .build();
    }
    
    /**
     * 获取产品详情
     * 包含产品基本信息、图片、库存、销售统计等
     * 
     * @param id 产品ID
     * @return ProductDetailDTO 产品详情
     */
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetail(Long id) {
        // 从缓存获取产品信息
        String cacheKey = "product:detail:" + id;
        ProductDetailDTO cached = (ProductDetailDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 查询产品基本信息
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 查询关联数据
        List<ProductImage> images = productImageMapper.selectByProductId(id);
        Inventory inventory = inventoryMapper.selectByProductId(id);
        Category category = categoryMapper.selectById(product.getCategoryId());
        
        // 构建详情DTO
        ProductDetailDTO detailDTO = convertToProductDetailDTO(product, images, inventory, category);
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, detailDTO, Duration.ofMinutes(30));
        
        return detailDTO;
    }
    
    /**
     * 创建产品
     * 创建新产品并处理关联数据
     * 
     * @param createDTO 产品创建数据
     * @return ProductDTO 创建的产品信息
     */
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        // 验证分类是否存在
        Category category = categoryMapper.selectById(createDTO.getCategoryId());
        if (category == null) {
            throw new BusinessException("产品分类不存在");
        }
        
        // 验证SKU唯一性
        if (productMapper.existsBySku(createDTO.getSku())) {
            throw new BusinessException("产品SKU已存在");
        }
        
        // 创建产品记录
        Product product = convertToProduct(createDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        
        // 处理产品图片
        if (createDTO.getImages() != null && !createDTO.getImages().isEmpty()) {
            saveProductImages(product.getId(), createDTO.getImages());
        }
        
        // 创建库存记录
        if (createDTO.getStock() != null && createDTO.getStock() > 0) {
            createInventoryRecord(product.getId(), createDTO.getStock());
        }
        
        // 记录操作日志
        logProductOperation("CREATE", product.getId(), "创建产品: " + product.getName());
        
        return convertToProductDTO(product);
    }
    
    /**
     * 更新产品
     * 更新产品信息和关联数据
     * 
     * @param id 产品ID
     * @param updateDTO 产品更新数据
     * @return ProductDTO 更新后的产品信息
     */
    public ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO) {
        // 查询现有产品
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 验证SKU唯一性（排除当前产品）
        if (!existingProduct.getSku().equals(updateDTO.getSku()) && 
            productMapper.existsBySku(updateDTO.getSku())) {
            throw new BusinessException("产品SKU已存在");
        }
        
        // 更新产品信息
        Product product = convertToProduct(updateDTO);
        product.setId(id);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        
        // 更新产品图片
        if (updateDTO.getImages() != null) {
            updateProductImages(id, updateDTO.getImages());
        }
        
        // 更新库存
        if (updateDTO.getStock() != null) {
            updateInventory(id, updateDTO.getStock(), "管理员调整");
        }
        
        // 清除缓存
        clearProductCache(id);
        
        // 记录操作日志
        logProductOperation("UPDATE", id, "更新产品: " + product.getName());
        
        return convertToProductDTO(product);
    }
    
    /**
     * 删除产品
     * 软删除产品记录
     * 
     * @param id 产品ID
     */
    public void deleteProduct(Long id) {
        // 检查产品是否存在
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 检查是否有关联订单
        if (hasRelatedOrders(id)) {
            throw new BusinessException("产品存在关联订单，无法删除");
        }
        
        // 软删除产品
        product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        
        // 清除缓存
        clearProductCache(id);
        
        // 记录操作日志
        logProductOperation("DELETE", id, "删除产品: " + product.getName());
    }
    
    // 私有方法...
    private ProductQuery buildProductQuery(ProductQueryDTO queryDTO) {
        // 构建查询条件逻辑
    }
    
    private ProductDTO convertToProductDTO(Product product) {
        // 产品实体转DTO逻辑
    }
    
    private void saveProductImages(Long productId, List<String> images) {
        // 保存产品图片逻辑
    }
    
    private void clearProductCache(Long productId) {
        // 清除产品缓存逻辑
    }
    
    private void logProductOperation(String operation, Long productId, String description) {
        // 记录操作日志逻辑
    }
}
```

## 数据模型

### 核心数据表设计

**1. 产品表 (products)**
```sql
-- 产品表
-- 用于存储产品基本信息
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `sku` VARCHAR(100) NOT NULL COMMENT '产品SKU，唯一标识',
  `category_id` BIGINT NOT NULL COMMENT '分类ID，外键关联categories表',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格，单位：元',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价，单位：元',
  `cost_price` DECIMAL(10,2) DEFAULT NULL COMMENT '成本价，单位：元',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '产品状态：draft-草稿，published-已发布，archived-已下架',
  `short_description` TEXT COMMENT '产品简短描述',
  `description` LONGTEXT COMMENT '产品详细描述，支持HTML',
  `specifications` JSON COMMENT '产品规格参数，JSON格式存储',
  `weight` DECIMAL(8,3) DEFAULT NULL COMMENT '产品重量，单位：kg',
  `dimensions` VARCHAR(100) DEFAULT NULL COMMENT '产品尺寸，格式：长x宽x高',
  `brand` VARCHAR(100) DEFAULT NULL COMMENT '品牌',
  `model` VARCHAR(100) DEFAULT NULL COMMENT '型号',
  `warranty_period` INT DEFAULT NULL COMMENT '保修期，单位：月',
  `is_featured` TINYINT DEFAULT 0 COMMENT '是否推荐产品：0-否，1-是',
  `is_digital` TINYINT DEFAULT 0 COMMENT '是否数字产品：0-否，1-是',
  `requires_shipping` TINYINT DEFAULT 1 COMMENT '是否需要物流：0-否，1-是',
  `meta_title` VARCHAR(200) DEFAULT NULL COMMENT 'SEO标题',
  `meta_description` VARCHAR(500) DEFAULT NULL COMMENT 'SEO描述',
  `meta_keywords` VARCHAR(300) DEFAULT NULL COMMENT 'SEO关键词',
  `url_alias` VARCHAR(200) DEFAULT NULL COMMENT 'URL别名',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `sales_count` INT DEFAULT 0 COMMENT '销售数量',
  `rating_average` DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
  `rating_count` INT DEFAULT 0 COMMENT '评分次数',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku` (`sku`),
  UNIQUE KEY `uk_url_alias` (`url_alias`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_sales_count` (`sales_count`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_search` (`name`, `sku`, `status`),
  FULLTEXT KEY `ft_search` (`name`, `short_description`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';
```

**2. 产品分类表 (categories)**
```sql
-- 产品分类表
-- 用于存储产品分类信息，支持多级分类
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `level` TINYINT DEFAULT 1 COMMENT '分类层级，从1开始',
  `path` VARCHAR(500) DEFAULT NULL COMMENT '分类路径，用/分隔',
  `description` TEXT COMMENT '分类描述',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '分类图片URL',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `product_count` INT DEFAULT 0 COMMENT '产品数量',
  `meta_title` VARCHAR(200) DEFAULT NULL COMMENT 'SEO标题',
  `meta_description` VARCHAR(500) DEFAULT NULL COMMENT 'SEO描述',
  `meta_keywords` VARCHAR(300) DEFAULT NULL COMMENT 'SEO关键词',
  `url_alias` VARCHAR(200) DEFAULT NULL COMMENT 'URL别名',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_enabled` (`is_enabled`),
  UNIQUE KEY `uk_url_alias` (`url_alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表';
```

**3. 产品图片表 (product_images)**
```sql
-- 产品图片表
-- 用于存储产品相关的图片信息
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `product_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
  `alt_text` VARCHAR(200) DEFAULT NULL COMMENT '图片替代文本',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '图片标题',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `is_primary` TINYINT DEFAULT 0 COMMENT '是否主图：0-否，1-是',
  `file_size` INT DEFAULT NULL COMMENT '文件大小，单位：字节',
  `width` INT DEFAULT NULL COMMENT '图片宽度，单位：像素',
  `height` INT DEFAULT NULL COMMENT '图片高度，单位：像素',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品图片表';
```

**4. 产品库存表 (inventory)**
```sql
-- 产品库存表
-- 用于存储产品库存信息和库存变动记录
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `current_stock` INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
  `available_stock` INT NOT NULL DEFAULT 0 COMMENT '可用库存数量',
  `reserved_stock` INT NOT NULL DEFAULT 0 COMMENT '预留库存数量',
  `min_stock_level` INT DEFAULT 10 COMMENT '最低库存预警值',
  `max_stock_level` INT DEFAULT NULL COMMENT '最高库存限制',
  `reorder_point` INT DEFAULT NULL COMMENT '补货点',
  `reorder_quantity` INT DEFAULT NULL COMMENT '补货数量',
  `last_stock_in` DATETIME DEFAULT NULL COMMENT '最后入库时间',
  `last_stock_out` DATETIME DEFAULT NULL COMMENT '最后出库时间',
  `stock_status` VARCHAR(20) DEFAULT 'in_stock' COMMENT '库存状态：in_stock-有库存，low_stock-库存不足，out_of_stock-缺货',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_current_stock` (`current_stock`),
  KEY `idx_stock_status` (`stock_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品库存表';
```

**5. 库存变动记录表 (inventory_transactions)**
```sql
-- 库存变动记录表
-- 用于记录所有库存变动的详细信息
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `inventory_transactions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '变动记录ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `transaction_type` VARCHAR(20) NOT NULL COMMENT '变动类型：in-入库，out-出库，adjust-调整',
  `quantity` INT NOT NULL COMMENT '变动数量，正数表示增加，负数表示减少',
  `before_quantity` INT NOT NULL COMMENT '变动前数量',
  `after_quantity` INT NOT NULL COMMENT '变动后数量',
  `reason` VARCHAR(100) DEFAULT NULL COMMENT '变动原因',
  `reference_type` VARCHAR(50) DEFAULT NULL COMMENT '关联类型：order-订单，purchase-采购，adjust-调整',
  `reference_id` BIGINT DEFAULT NULL COMMENT '关联ID',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
  `notes` TEXT COMMENT '备注信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_transaction_type` (`transaction_type`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_reference` (`reference_type`, `reference_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';
```

**6. 产品销售统计表 (product_sales_stats)**
```sql
-- 产品销售统计表
-- 用于存储产品销售统计数据
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员
CREATE TABLE `product_sales_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `sales_quantity` INT DEFAULT 0 COMMENT '销售数量',
  `sales_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '销售金额，单位：元',
  `order_count` INT DEFAULT 0 COMMENT '订单数量',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `conversion_rate` DECIMAL(5,4) DEFAULT 0.0000 COMMENT '转化率',
  `return_quantity` INT DEFAULT 0 COMMENT '退货数量',
  `return_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '退货金额，单位：元',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_date` (`product_id`, `stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_sales_amount` (`sales_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品销售统计表';
```

### 数据传输对象 (DTO)

**产品查询DTO**
```java
/**
 * 产品查询条件DTO
 * 用于封装产品列表查询的各种条件
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductQueryDTO {
    private Integer page = 1;                    // 页码，默认第1页
    private Integer size = 20;                   // 每页数量，默认20条
    private String keyword;                      // 搜索关键词
    private Long categoryId;                     // 分类ID
    private String status;                       // 产品状态
    private BigDecimal minPrice;                 // 最低价格
    private BigDecimal maxPrice;                 // 最高价格
    private String sortBy = "createdAt";         // 排序字段
    private String sortOrder = "desc";           // 排序方向
    private Boolean isFeatured;                  // 是否推荐产品
    private String stockStatus;                  // 库存状态
    
    // getters and setters...
}
```

**产品详情DTO**
```java
/**
 * 产品详情DTO
 * 包含产品的完整信息
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductDetailDTO {
    private Long id;                             // 产品ID
    private String name;                         // 产品名称
    private String sku;                          // 产品SKU
    private CategoryDTO category;                // 产品分类
    private BigDecimal price;                    // 销售价格
    private BigDecimal originalPrice;            // 原价
    private String status;                       // 产品状态
    private String shortDescription;             // 简短描述
    private String description;                  // 详细描述
    private Map<String, Object> specifications;  // 产品规格
    private List<ProductImageDTO> images;        // 产品图片
    private InventoryDTO inventory;              // 库存信息
    private ProductSalesStatsDTO salesStats;     // 销售统计
    private ProductSEODTO seo;                   // SEO信息
    private LocalDateTime createdAt;             // 创建时间
    private LocalDateTime updatedAt;             // 更新时间
    
    // getters and setters...
}
```

## 错误处理

### 业务异常定义
```java
/**
 * 产品管理相关业务异常
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductException extends BusinessException {
    
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String SKU_ALREADY_EXISTS = "SKU_ALREADY_EXISTS";
    public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    public static final String PRODUCT_HAS_ORDERS = "PRODUCT_HAS_ORDERS";
    
    public ProductException(String code, String message) {
        super(code, message);
    }
    
    public static ProductException productNotFound() {
        return new ProductException(PRODUCT_NOT_FOUND, "产品不存在");
    }
    
    public static ProductException skuAlreadyExists() {
        return new ProductException(SKU_ALREADY_EXISTS, "产品SKU已存在");
    }
    
    public static ProductException categoryNotFound() {
        return new ProductException(CATEGORY_NOT_FOUND, "产品分类不存在");
    }
    
    public static ProductException insufficientStock() {
        return new ProductException(INSUFFICIENT_STOCK, "库存不足");
    }
    
    public static ProductException productHasOrders() {
        return new ProductException(PRODUCT_HAS_ORDERS, "产品存在关联订单，无法删除");
    }
}
```

### 统一异常处理
```java
/**
 * 产品管理异常处理器
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@ControllerAdvice
public class ProductExceptionHandler {
    
    @ExceptionHandler(ProductException.class)
    public Result<Void> handleProductException(ProductException e) {
        return Result.error(400, e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return Result.error(400, "参数验证失败: " + message);
    }
}
```

## 测试策略

### 单元测试
- **控制器测试**: 使用 MockMvc 测试 API 接口的请求响应
- **服务层测试**: 使用 Mockito 模拟依赖，测试业务逻辑
- **数据访问层测试**: 使用 H2 内存数据库测试 SQL 查询
- **工具类测试**: 测试数据转换、验证等工具方法

### 集成测试
- **API 集成测试**: 测试完整的产品管理流程
- **数据库集成测试**: 测试数据的一致性和完整性
- **文件上传测试**: 测试图片和文档上传功能
- **缓存集成测试**: 测试 Redis 缓存的有效性

### 性能测试
- **列表查询性能**: 测试大数据量下的查询性能
- **图片上传性能**: 测试多文件并发上传
- **批量操作性能**: 测试批量更新和删除操作
- **缓存性能**: 测试缓存命中率和响应时间

---

**设计文档版本**: v1.0  
**创建日期**: 2024-12-19  
**设计负责人**: 系统架构师  
**审核人**: 技术负责人