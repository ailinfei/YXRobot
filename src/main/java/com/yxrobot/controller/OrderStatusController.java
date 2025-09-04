package com.yxrobot.controller;

import com.yxrobot.dto.OrderDTO;
import com.yxrobot.service.OrderService;
import com.yxrobot.service.OrderStatusService;
import com.yxrobot.service.OrderValidationService;
import com.yxrobot.exception.OrderException;
import com.yxrobot.util.OrderValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单状态管理控制器
 * 适配前端状态操作，提供状态变更相关的API接口
 */
@RestController
@RequestMapping("/api/admin/orders")
@Validated
public class OrderStatusController {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusController.class);

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderValidationService orderValidationService;

    /**
     * 更新单个订单状态
     * PATCH /api/admin/orders/{id}/status
     * 支持单个订单状态变更
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            logger.info("开始更新订单状态，订单ID: {}, 目标状态: {}", id, request.getStatus());
            
            // 验证订单ID
            if (id == null || id <= 0) {
                throw OrderException.validationFailed("id", "订单ID无效");
            }
            
            // 验证状态格式
            try {
                OrderValidationUtil.validateOrderStatus(request.getStatus());
            } catch (OrderException e) {
                throw OrderException.validationFailed("status", "订单状态无效：" + request.getStatus());
            }
            
            // 获取当前订单信息
            OrderDTO currentOrder = orderService.getOrderById(id);
            
            // 验证状态流转
            OrderValidationService.ValidationResult validationResult = 
                orderValidationService.validateStatusChange(currentOrder.getStatus(), request.getStatus());
            
            if (!validationResult.isValid()) {
                logger.warn("订单状态变更验证失败: {}", validationResult.getErrors());
                StringBuilder errorMsg = new StringBuilder("状态变更验证失败：");
                for (OrderValidationService.ValidationError error : validationResult.getErrors()) {
                    errorMsg.append(error.getMessage()).append("; ");
                }
                throw OrderException.validationFailed("statusTransition", errorMsg.toString());
            }
            
            // 验证权限
            if (!orderStatusService.hasStatusChangePermission(id, request.getOperator(), request.getStatus())) {
                throw OrderException.permissionDenied("状态变更操作");
            }

            // 更新状态
            boolean success = orderStatusService.updateOrderStatus(
                id, 
                request.getStatus(), 
                request.getOperator(), 
                request.getNotes()
            );

            if (success) {
                // 返回更新后的订单信息
                OrderDTO updatedOrder = orderService.getOrderById(id);
                logger.info("订单状态更新成功，订单ID: {}, 新状态: {}", id, updatedOrder.getStatus());
                return ResponseEntity.ok(createSuccessResponse("状态更新成功", updatedOrder));
            } else {
                throw OrderException.businessRuleViolation("状态更新", "状态更新操作失败");
            }
            
        } catch (OrderException e) {
            logger.warn("订单状态更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("订单状态更新运行时异常", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("订单状态更新系统异常", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 批量更新订单状态
     * PATCH /api/admin/orders/batch/status
     * 支持批量状态变更
     */
    @PatchMapping("/batch/status")
    public ResponseEntity<Map<String, Object>> batchUpdateOrderStatus(
            @Valid @RequestBody BatchStatusUpdateRequest request) {
        try {
            logger.info("开始批量更新订单状态，订单数量: {}, 目标状态: {}", 
                request.getOrderIds().size(), request.getStatus());
            
            // 验证批量操作参数
            if (request.getOrderIds() == null || request.getOrderIds().isEmpty()) {
                throw OrderException.validationFailed("orderIds", "订单ID列表不能为空");
            }
            
            if (request.getOrderIds().size() > 100) {
                throw OrderException.validationFailed("orderIds", "批量操作订单数量不能超过100个");
            }
            
            // 验证状态格式
            try {
                OrderValidationUtil.validateOrderStatus(request.getStatus());
            } catch (OrderException e) {
                throw OrderException.validationFailed("status", "订单状态无效：" + request.getStatus());
            }
            
            // 验证每个订单ID
            for (Long orderId : request.getOrderIds()) {
                if (orderId == null || orderId <= 0) {
                    throw OrderException.validationFailed("orderIds", "包含无效的订单ID：" + orderId);
                }
            }
            
            // 执行批量状态更新
            OrderStatusService.BatchUpdateResult result = orderStatusService.batchUpdateOrderStatus(
                request.getOrderIds(),
                request.getStatus(),
                request.getOperator(),
                request.getNotes()
            );

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalCount", result.getTotalCount());
            responseData.put("successCount", result.getSuccessCount());
            responseData.put("failedCount", result.getFailedCount());
            responseData.put("successIds", result.getSuccessIds());
            responseData.put("failedIds", result.getFailedIds());
            responseData.put("failedReasons", result.getFailedReasons());

            String message = String.format("批量操作完成：成功 %d 个，失败 %d 个", 
                result.getSuccessCount(), result.getFailedCount());

            logger.info("批量状态更新完成，成功: {}, 失败: {}", 
                result.getSuccessCount(), result.getFailedCount());
            
            return ResponseEntity.ok(createSuccessResponse(message, responseData));
            
        } catch (OrderException e) {
            logger.warn("批量状态更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("批量状态更新异常", e);
            return ResponseEntity.badRequest().body(createErrorResponse("批量操作失败：" + e.getMessage()));
        }
    }

    /**
     * 批量删除订单
     * DELETE /api/admin/orders/batch
     * 支持批量删除
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteOrders(
            @Valid @RequestBody BatchDeleteRequest request) {
        try {
            int successCount = 0;
            int failedCount = 0;
            List<String> failedReasons = new java.util.ArrayList<>();

            for (Long orderId : request.getOrderIds()) {
                try {
                    orderService.deleteOrder(orderId);
                    successCount++;
                } catch (Exception e) {
                    failedCount++;
                    failedReasons.add("订单ID " + orderId + ": " + e.getMessage());
                }
            }

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalCount", request.getOrderIds().size());
            responseData.put("successCount", successCount);
            responseData.put("failedCount", failedCount);
            responseData.put("failedReasons", failedReasons);

            String message = String.format("批量删除完成：成功 %d 个，失败 %d 个", successCount, failedCount);

            return ResponseEntity.ok(createSuccessResponse(message, responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("批量删除失败：" + e.getMessage()));
        }
    }

    /**
     * 获取订单状态变更历史
     * GET /api/admin/orders/{id}/status/history
     * 支持查看订单状态变更历史
     */
    @GetMapping("/{id}/status/history")
    public ResponseEntity<Map<String, Object>> getOrderStatusHistory(@PathVariable @NotNull Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            
            // 返回订单的操作日志作为状态变更历史
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orderId", id);
            responseData.put("orderNumber", order.getOrderNumber());
            responseData.put("currentStatus", order.getStatus());
            responseData.put("history", order.getOrderLogs());

            return ResponseEntity.ok(createSuccessResponse("查询成功", responseData));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("系统错误：" + e.getMessage()));
        }
    }

    /**
     * 验证状态变更权限
     * GET /api/admin/orders/{id}/status/permission
     * 支持前端按钮权限控制
     */
    @GetMapping("/{id}/status/permission")
    public ResponseEntity<Map<String, Object>> checkStatusChangePermission(
            @PathVariable @NotNull Long id,
            @RequestParam String operator,
            @RequestParam String targetStatus) {
        try {
            boolean hasPermission = orderStatusService.hasStatusChangePermission(id, operator, targetStatus);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orderId", id);
            responseData.put("operator", operator);
            responseData.put("targetStatus", targetStatus);
            responseData.put("hasPermission", hasPermission);

            return ResponseEntity.ok(createSuccessResponse("验证成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("验证失败：" + e.getMessage()));
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

    /**
     * 状态更新请求DTO
     */
    public static class StatusUpdateRequest {
        @NotNull(message = "状态不能为空")
        private String status;

        @NotNull(message = "操作人不能为空")
        private String operator;

        private String notes;

        // Getter和Setter方法
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    /**
     * 批量状态更新请求DTO
     */
    public static class BatchStatusUpdateRequest {
        @NotEmpty(message = "订单ID列表不能为空")
        private List<Long> orderIds;

        @NotNull(message = "状态不能为空")
        private String status;

        @NotNull(message = "操作人不能为空")
        private String operator;

        private String notes;

        // Getter和Setter方法
        public List<Long> getOrderIds() {
            return orderIds;
        }

        public void setOrderIds(List<Long> orderIds) {
            this.orderIds = orderIds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    /**
     * 批量删除请求DTO
     */
    public static class BatchDeleteRequest {
        @NotEmpty(message = "订单ID列表不能为空")
        private List<Long> orderIds;

        @NotNull(message = "操作人不能为空")
        private String operator;

        private String reason;

        // Getter和Setter方法
        public List<Long> getOrderIds() {
            return orderIds;
        }

        public void setOrderIds(List<Long> orderIds) {
            this.orderIds = orderIds;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}