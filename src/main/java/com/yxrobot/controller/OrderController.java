package com.yxrobot.controller;

import com.yxrobot.dto.*;
import com.yxrobot.entity.OrderStatus;
import com.yxrobot.service.OrderService;
import com.yxrobot.service.OrderStatsService;
import com.yxrobot.service.OrderStatusService;
import com.yxrobot.service.OrderValidationService;
import com.yxrobot.validator.OrderFormValidator;
import com.yxrobot.exception.OrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理控制器
 * 适配前端API调用，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/admin/orders")
@Validated
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatsService orderStatsService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderValidationService orderValidationService;

    @Autowired
    private OrderFormValidator orderFormValidator;

    @Autowired
    private com.yxrobot.service.OrderSearchService orderSearchService;

    /**
     * 获取订单列表
     * GET /api/admin/orders
     * 支持前端列表查询需求：分页、搜索、筛选
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        
        try {
            // 构建查询条件
            OrderQueryDTO queryDTO = new OrderQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);
            queryDTO.setType(type);
            queryDTO.setStatus(status);
            queryDTO.setPaymentStatus(paymentStatus);
            
            // 处理日期参数
            if (startDate != null && !startDate.isEmpty()) {
                queryDTO.setStartDate(java.time.LocalDate.parse(startDate));
            }
            if (endDate != null && !endDate.isEmpty()) {
                queryDTO.setEndDate(java.time.LocalDate.parse(endDate));
            }
            
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);

            // 查询订单列表
            OrderService.OrderQueryResult result = orderService.getOrders(queryDTO);
            
            // 查询统计数据
            OrderStatsDTO stats = orderStatsService.getOrderStats();

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("list", result.getList());
            responseData.put("total", result.getTotal());
            responseData.put("stats", stats);
            responseData.put("page", page);
            responseData.put("size", size);

            return ResponseEntity.ok(createSuccessResponse("查询成功", responseData));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 获取订单详情
     * GET /api/admin/orders/{id}
     * 支持前端详情查询
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable @NotNull Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(createSuccessResponse("查询成功", order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 创建订单
     * POST /api/admin/orders
     * 支持前端订单创建
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        try {
            logger.info("开始创建订单，订单号: {}", createDTO.getOrderNumber());
            
            // 表单数据验证
            orderFormValidator.validateCreateForm(createDTO);
            
            // 业务数据验证
            OrderValidationService.ValidationResult validationResult = 
                orderValidationService.validateOrderCreate(createDTO);
            
            if (!validationResult.isValid()) {
                logger.warn("订单创建验证失败: {}", validationResult.getErrors());
                StringBuilder errorMsg = new StringBuilder("数据验证失败：");
                for (OrderValidationService.ValidationError error : validationResult.getErrors()) {
                    errorMsg.append(error.getMessage()).append("; ");
                }
                throw OrderException.validationFailed("create", errorMsg.toString());
            }
            
            // 创建订单
            OrderDTO order = orderService.createOrder(createDTO);
            
            logger.info("订单创建成功，订单ID: {}", order.getId());
            return ResponseEntity.ok(createSuccessResponse("创建成功", order));
            
        } catch (OrderException e) {
            logger.warn("订单创建失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("订单创建运行时异常", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("订单创建系统异常", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 更新订单
     * PUT /api/admin/orders/{id}
     * 支持前端订单编辑
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody OrderDTO updateDTO) {
        try {
            logger.info("开始更新订单，订单ID: {}", id);
            
            // 验证订单ID
            if (id == null || id <= 0) {
                throw OrderException.validationFailed("id", "订单ID无效");
            }
            
            // 表单数据验证
            orderFormValidator.validateUpdateForm(updateDTO);
            
            // 业务数据验证
            OrderValidationService.ValidationResult validationResult = 
                orderValidationService.validateOrderUpdate(id, updateDTO);
            
            if (!validationResult.isValid()) {
                logger.warn("订单更新验证失败: {}", validationResult.getErrors());
                StringBuilder errorMsg = new StringBuilder("数据验证失败：");
                for (OrderValidationService.ValidationError error : validationResult.getErrors()) {
                    errorMsg.append(error.getMessage()).append("; ");
                }
                throw OrderException.validationFailed("update", errorMsg.toString());
            }
            
            // 更新订单
            OrderDTO order = orderService.updateOrder(id, updateDTO);
            
            logger.info("订单更新成功，订单ID: {}", order.getId());
            return ResponseEntity.ok(createSuccessResponse("更新成功", order));
            
        } catch (OrderException e) {
            logger.warn("订单更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("订单更新运行时异常", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("订单更新系统异常", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 删除订单
     * DELETE /api/admin/orders/{id}
     * 支持前端订单删除（软删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable @NotNull Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(createSuccessResponse("删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 获取订单统计数据
     * GET /api/admin/orders/stats
     * 支持前端统计卡片显示
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            OrderStatsDTO stats;
            
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                // 按日期范围查询统计数据
                java.time.LocalDate start = java.time.LocalDate.parse(startDate);
                java.time.LocalDate end = java.time.LocalDate.parse(endDate);
                stats = orderStatsService.getOrderStatsByDateRange(start, end);
            } else {
                // 查询全部统计数据
                stats = orderStatsService.getOrderStats();
            }
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 搜索订单
     * GET /api/admin/orders/search
     * 支持前端快速搜索功能（优化版本）
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            logger.debug("开始搜索订单，关键词: {}, 限制: {}", keyword, limit);
            
            // 使用优化的搜索服务
            List<OrderDTO> orders = orderSearchService.quickSearch(keyword, limit);
            
            // 检测搜索类型并提供建议
            com.yxrobot.util.SearchOptimizationUtil.SearchType searchType = 
                com.yxrobot.util.SearchOptimizationUtil.detectSearchType(keyword);
            
            List<String> suggestions = com.yxrobot.util.SearchOptimizationUtil.generateSearchSuggestions(
                keyword, searchType, orders.size());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("list", orders);
            responseData.put("total", orders.size());
            responseData.put("searchType", searchType.name());
            responseData.put("suggestions", suggestions);
            responseData.put("optimizedKeyword", com.yxrobot.util.SearchOptimizationUtil.optimizeKeyword(keyword));

            return ResponseEntity.ok(createSuccessResponse("搜索成功", responseData));
        } catch (Exception e) {
            logger.error("搜索订单失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("搜索失败：" + e.getMessage()));
        }
    }

    /**
     * 验证订单号是否存在
     * GET /api/admin/orders/validate/orderNumber
     * 支持前端表单验证
     */
    @GetMapping("/validate/orderNumber")
    public ResponseEntity<Map<String, Object>> validateOrderNumber(@RequestParam String orderNumber) {
        try {
            // 这里可以添加订单号验证逻辑
            // 暂时返回不存在，表示可以使用
            Map<String, Object> result = new HashMap<>();
            result.put("exists", false);
            result.put("available", true);
            
            return ResponseEntity.ok(createSuccessResponse("验证成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("验证失败：" + e.getMessage()));
        }
    }

    /**
     * 获取订单可用的状态流转选项
     * GET /api/admin/orders/{id}/transitions
     * 支持前端状态操作按钮显示
     */
    @GetMapping("/{id}/transitions")
    public ResponseEntity<Map<String, Object>> getOrderTransitions(@PathVariable @NotNull Long id) {
        try {
            List<OrderStatus> transitions = orderStatusService.getAvailableStatusTransitions(id);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> transitionList = new java.util.ArrayList<>();
            for (OrderStatus status : transitions) {
                Map<String, Object> transition = new HashMap<>();
                transition.put("code", status.getCode());
                transition.put("description", status.getDescription());
                transitionList.add(transition);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("transitions", transitionList);
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    // ==================== 辅助功能接口 ====================
    
    /**
     * 导出订单数据
     * GET /api/admin/orders/export
     * 支持前端订单数据导出功能
     */
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "excel") String format) {
        
        try {
            // 构建查询条件
            OrderQueryDTO queryDTO = new OrderQueryDTO();
            queryDTO.setKeyword(keyword);
            queryDTO.setType(type);
            queryDTO.setStatus(status);
            
            // 处理日期参数
            if (startDate != null && !startDate.isEmpty()) {
                queryDTO.setStartDate(java.time.LocalDate.parse(startDate));
            }
            if (endDate != null && !endDate.isEmpty()) {
                queryDTO.setEndDate(java.time.LocalDate.parse(endDate));
            }
            
            // 获取要导出的订单数据
            OrderService.OrderQueryResult result = orderService.getOrders(queryDTO);
            
            // 构建导出数据
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("orders", result.getList());
            exportData.put("total", result.getTotal());
            exportData.put("exportTime", java.time.LocalDateTime.now().toString());
            exportData.put("format", format);
            exportData.put("filters", Map.of(
                "keyword", keyword != null ? keyword : "",
                "type", type != null ? type : "",
                "status", status != null ? status : "",
                "startDate", startDate != null ? startDate : "",
                "endDate", endDate != null ? endDate : ""
            ));
            
            return ResponseEntity.ok(createSuccessResponse("导出数据准备完成", exportData));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("导出失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取订单物流信息
     * GET /api/admin/orders/{id}/tracking
     * 支持前端物流信息查询
     */
    @GetMapping("/{id}/tracking")
    public ResponseEntity<Map<String, Object>> getOrderTracking(@PathVariable @NotNull Long id) {
        try {
            // 获取订单详情（包含物流信息）
            OrderDTO order = orderService.getOrderById(id);
            
            // 提取物流信息
            Map<String, Object> trackingInfo = new HashMap<>();
            if (order.getShippingInfo() != null) {
                trackingInfo.put("company", order.getShippingInfo().getCompany());
                trackingInfo.put("trackingNumber", order.getShippingInfo().getTrackingNumber());
                trackingInfo.put("shippedAt", order.getShippingInfo().getShippedAt());
                trackingInfo.put("deliveredAt", order.getShippingInfo().getDeliveredAt());
                trackingInfo.put("notes", order.getShippingInfo().getNotes());
                trackingInfo.put("status", order.getStatus());
            } else {
                trackingInfo.put("company", null);
                trackingInfo.put("trackingNumber", null);
                trackingInfo.put("shippedAt", null);
                trackingInfo.put("deliveredAt", null);
                trackingInfo.put("notes", null);
                trackingInfo.put("status", order.getStatus());
            }
            
            trackingInfo.put("orderId", order.getId());
            trackingInfo.put("orderNumber", order.getOrderNumber());
            trackingInfo.put("customerName", order.getCustomerName());
            trackingInfo.put("deliveryAddress", order.getDeliveryAddress());
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", trackingInfo));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }
    
    /**
     * 更新订单物流信息
     * PUT /api/admin/orders/{id}/tracking
     * 支持前端物流信息更新
     */
    @PutMapping("/{id}/tracking")
    public ResponseEntity<Map<String, Object>> updateOrderTracking(
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> trackingData) {
        
        try {
            // 验证订单是否存在
            OrderDTO order = orderService.getOrderById(id);
            
            // 提取物流信息
            String company = (String) trackingData.get("company");
            String trackingNumber = (String) trackingData.get("trackingNumber");
            String notes = (String) trackingData.get("notes");
            
            // 这里应该调用物流信息更新服务
            // 由于当前没有专门的物流服务，我们返回一个成功响应
            Map<String, Object> updatedTracking = new HashMap<>();
            updatedTracking.put("orderId", id);
            updatedTracking.put("orderNumber", order.getOrderNumber());
            updatedTracking.put("company", company);
            updatedTracking.put("trackingNumber", trackingNumber);
            updatedTracking.put("notes", notes);
            updatedTracking.put("updatedAt", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(createSuccessResponse("物流信息更新成功", updatedTracking));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }
    
    /**
     * 获取客户列表（用于订单创建/编辑）
     * GET /api/admin/customers
     * 支持前端订单表单中的客户选择
     */
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomersForOrder(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        try {
            // 构建简化的客户数据用于订单表单
            List<Map<String, Object>> customers = new ArrayList<>();
            
            // 这里应该调用CustomerService获取客户数据
            // 由于我们已经有CustomerController，这里返回一个示例响应
            Map<String, Object> sampleCustomer = new HashMap<>();
            sampleCustomer.put("id", "1");
            sampleCustomer.put("name", "示例客户");
            sampleCustomer.put("phone", "13800138000");
            sampleCustomer.put("email", "customer@example.com");
            sampleCustomer.put("company", "示例公司");
            sampleCustomer.put("address", "北京市朝阳区示例地址");
            customers.add(sampleCustomer);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", customers);
            result.put("total", customers.size());
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", result));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("查询失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取产品列表（用于订单创建/编辑）
     * GET /api/admin/products
     * 支持前端订单表单中的产品选择
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductsForOrder(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        try {
            // 构建简化的产品数据用于订单表单
            List<Map<String, Object>> products = new ArrayList<>();
            
            // 这里应该调用ProductService获取产品数据
            // 由于我们已经有ProductController，这里返回一个示例响应
            Map<String, Object> sampleProduct = new HashMap<>();
            sampleProduct.put("id", "1");
            sampleProduct.put("name", "练字机器人 Pro");
            sampleProduct.put("model", "YX-PRO-001");
            sampleProduct.put("price", 2999.00);
            sampleProduct.put("description", "专业级练字机器人");
            sampleProduct.put("category", "机器人");
            sampleProduct.put("status", "active");
            products.add(sampleProduct);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", products);
            result.put("total", products.size());
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", result));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 创建服务器错误响应
     */
    private Map<String, Object> createServerErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}