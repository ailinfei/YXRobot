package com.yxrobot.controller;

import com.yxrobot.entity.Product;
import com.yxrobot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 产品管理控制器
 * 适配前端Products.vue页面的需求
 * 遵循API路径规范，支持版本控制
 */
@RestController
@RequestMapping("/api/admin/products")
// 移除@CrossOrigin注解，使用WebConfig中的全局CORS配置
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 简单测试端点
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "测试成功");
        response.put("data", "ProductController正常工作");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取产品列表（适配前端Products.vue）
     * @param page 页码
     * @param size 每页大小
     * @param status 产品状态
     * @param keyword 关键词
     * @return 产品列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        // 处理size和pageSize参数兼容性
        int actualSize = 20; // 默认值
        if (size != null) {
            actualSize = size;
        } else if (pageSize != null) {
            actualSize = pageSize;
        }
        
        try {
            List<Product> products;
            int total;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 搜索产品
                products = productService.searchProducts(keyword, keyword, status, null, null);
                total = productService.getSearchResultCount(keyword, keyword, status, null, null);
            } else if (status != null && !status.trim().isEmpty()) {
                // 按状态查询
                products = productService.getByStatus(status);
                total = productService.getCountByStatus(status);
            } else {
                // 分页查询所有产品
                products = productService.getWithPagination(page, actualSize, null);
                total = productService.getTotalCount();
            }
            
            // 转换为前端期望的格式
            List<Map<String, Object>> productList = products.stream().map(this::convertToFrontendFormat).toList();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", productList);
            data.put("total", total);
            data.put("page", page);
            data.put("size", actualSize);
            data.put("totalPages", (int) Math.ceil((double) total / actualSize));
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 根据ID查询产品
     * @param id 产品ID
     * @return 产品信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = productService.getById(id);
            if (product != null) {
                response.put("code", 200);
                response.put("data", convertToFrontendFormat(product));
                response.put("message", "查询成功");
            } else {
                response.put("code", 404);
                response.put("message", "产品不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 创建产品
     * @param productData 产品数据
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = convertFromFrontendFormat(productData);
            Product createdProduct = productService.create(product);
            
            response.put("code", 200);
            response.put("data", convertToFrontendFormat(createdProduct));
            response.put("message", "创建成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 更新产品
     * @param id 产品ID
     * @param productData 产品数据
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @Valid @RequestBody Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = convertFromFrontendFormat(productData);
            product.setId(id);
            Product updatedProduct = productService.update(product);
            
            response.put("code", 200);
            response.put("data", convertToFrontendFormat(updatedProduct));
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 删除产品
     * @param id 产品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            productService.delete(id);
            // 如果没有抛出异常，说明删除成功
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 发布产品
     * @param id 产品ID
     * @return 发布结果
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            productService.publish(id);
            // 如果没有抛出异常，说明发布成功
            response.put("code", 200);
            response.put("message", "发布成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "发布失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 预览产品
     * @param id 产品ID
     * @return 产品信息
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<Map<String, Object>> previewProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = productService.getById(id);
            if (product != null) {
                response.put("code", 200);
                response.put("data", convertToFrontendFormat(product));
                response.put("message", "查询成功");
            } else {
                response.put("code", 404);
                response.put("message", "产品不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 创建产品（使用已上传的文件URL）
     * @param productData 产品数据（包含cover_image_url）
     * @return 创建结果
     */
    @PostMapping("/with-urls")
    public ResponseEntity<Map<String, Object>> createProductWithUrls(@Valid @RequestBody Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = convertFromFrontendFormat(productData);
            Product createdProduct = productService.create(product);
            
            response.put("code", 200);
            response.put("data", convertToFrontendFormat(createdProduct));
            response.put("message", "创建成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 更新产品（使用已上传的文件URL）
     * @param id 产品ID
     * @param productData 产品数据（包含cover_image_url）
     * @return 更新结果
     */
    @PutMapping("/{id}/with-urls")
    public ResponseEntity<Map<String, Object>> updateProductWithUrls(@PathVariable Long id, @Valid @RequestBody Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = convertFromFrontendFormat(productData);
            product.setId(id);
            Product updatedProduct = productService.update(product);
            
            response.put("code", 200);
            response.put("data", convertToFrontendFormat(updatedProduct));
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 上传产品图片
     * @param file 图片文件
     * @param productId 产品ID（可选）
     * @return 上传结果
     */
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "productId", required = false) String productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 这里应该实现文件上传逻辑，暂时返回模拟数据
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("id", System.currentTimeMillis());
            imageData.put("url", "/uploads/products/covers/" + file.getOriginalFilename());
            imageData.put("type", "image");
            imageData.put("size", file.getSize());
            imageData.put("originalName", file.getOriginalFilename());
            
            response.put("code", 200);
            response.put("data", imageData);
            response.put("message", "上传成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 更新图片顺序
     * @param productId 产品ID
     * @param requestData 图片顺序数据
     * @return 更新结果
     */
    @PutMapping("/{productId}/image-order")
    public ResponseEntity<Map<String, Object>> updateImageOrder(
            @PathVariable String productId, 
            @RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回成功，实际应该更新图片顺序
            response.put("code", 200);
            response.put("message", "图片顺序更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 设置主图
     * @param productId 产品ID
     * @param requestData 主图数据
     * @return 设置结果
     */
    @PutMapping("/{productId}/main-image")
    public ResponseEntity<Map<String, Object>> setMainImage(
            @PathVariable String productId, 
            @RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回成功，实际应该设置主图
            response.put("code", 200);
            response.put("message", "主图设置成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "设置失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 删除产品图片
     * @param productId 产品ID
     * @param imageId 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteProductImage(
            @PathVariable String productId, 
            @PathVariable String imageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回成功，实际应该删除图片
            response.put("code", 200);
            response.put("message", "图片删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 获取产品媒体列表
     * @param productId 产品ID
     * @return 媒体列表
     */
    @GetMapping("/{productId}/media")
    public ResponseEntity<Map<String, Object>> getProductMedia(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回空列表，实际应该查询产品媒体
            List<Map<String, Object>> mediaList = new ArrayList<>();
            
            response.put("code", 200);
            response.put("data", mediaList);
            response.put("message", "查询成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 上传产品媒体
     * @param productId 产品ID
     * @param mediaType 媒体类型
     * @param sortOrder 排序
     * @param mediaFile 媒体文件
     * @return 上传结果
     */
    @PostMapping("/{productId}/media")
    public ResponseEntity<Map<String, Object>> uploadProductMedia(
            @PathVariable String productId,
            @RequestParam("mediaType") String mediaType,
            @RequestParam("sortOrder") String sortOrder,
            @RequestParam("mediaFile") MultipartFile mediaFile) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模拟媒体上传结果
            Map<String, Object> mediaData = new HashMap<>();
            mediaData.put("id", System.currentTimeMillis());
            mediaData.put("productId", productId);
            mediaData.put("mediaType", mediaType);
            mediaData.put("url", "/uploads/products/media/" + mediaFile.getOriginalFilename());
            mediaData.put("sortOrder", Integer.parseInt(sortOrder));
            mediaData.put("originalName", mediaFile.getOriginalFilename());
            mediaData.put("size", mediaFile.getSize());
            
            response.put("code", 200);
            response.put("data", mediaData);
            response.put("message", "媒体上传成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 更新产品媒体信息
     * @param mediaId 媒体ID
     * @param requestData 更新数据
     * @return 更新结果
     */
    @PutMapping("/media/{mediaId}")
    public ResponseEntity<Map<String, Object>> updateProductMedia(
            @PathVariable String mediaId, 
            @RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回成功，实际应该更新媒体信息
            response.put("code", 200);
            response.put("message", "媒体信息更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 删除产品媒体
     * @param mediaId 媒体ID
     * @return 删除结果
     */
    @DeleteMapping("/media/{mediaId}")
    public ResponseEntity<Map<String, Object>> deleteProductMedia(@PathVariable String mediaId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 暂时返回成功，实际应该删除媒体
            response.put("code", 200);
            response.put("message", "媒体删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 将Product实体转换为前端期望的格式
     */
    private Map<String, Object> convertToFrontendFormat(Product product) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("name", product.getName());
        result.put("model", product.getModel());
        result.put("description", product.getDescription());
        result.put("price", product.getPrice());
        result.put("cover_image_url", product.getCoverImageUrl());
        result.put("coverImageUrl", product.getCoverImageUrl());
        result.put("status", product.getStatus());
        result.put("created_at", product.getCreatedAt());
        result.put("updated_at", product.getUpdatedAt());
        result.put("createdAt", product.getCreatedAt());
        result.put("updatedAt", product.getUpdatedAt());
        
        // 模拟前端期望的images数组
        if (product.getCoverImageUrl() != null) {
            Map<String, Object> mainImage = new HashMap<>();
            mainImage.put("id", "1");
            mainImage.put("url", product.getCoverImageUrl());
            mainImage.put("type", "main");
            mainImage.put("alt", product.getName());
            mainImage.put("order", 1);
            result.put("images", List.of(mainImage));
        } else {
            result.put("images", List.of());
        }
        
        // 模拟产品特性（可以从description中提取或设置默认值）
        result.put("features", List.of("智能识别", "高精度", "易操作"));
        
        // 其他前端期望的字段
        result.put("badge", ""); // 产品标签
        result.put("originalPrice", null); // 原价
        
        return result;
    }
    
    /**
     * 将前端格式转换为Product实体
     */
    private Product convertFromFrontendFormat(Map<String, Object> data) {
        Product product = new Product();
        
        if (data.get("name") != null) {
            product.setName(data.get("name").toString());
        }
        if (data.get("model") != null) {
            product.setModel(data.get("model").toString());
        }
        if (data.get("description") != null) {
            product.setDescription(data.get("description").toString());
        }
        if (data.get("price") != null) {
            if (data.get("price") instanceof Number) {
                product.setPrice(new BigDecimal(data.get("price").toString()));
            }
        }
        if (data.get("cover_image_url") != null) {
            product.setCoverImageUrl(data.get("cover_image_url").toString());
        }
        if (data.get("coverImageUrl") != null) {
            product.setCoverImageUrl(data.get("coverImageUrl").toString());
        }
        if (data.get("status") != null) {
            product.setStatus(data.get("status").toString());
        }
        
        return product;
    }
}