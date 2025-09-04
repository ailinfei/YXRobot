package com.yxrobot.service;

import com.yxrobot.dto.OrderStatusUpdateDTO;
import com.yxrobot.entity.*;
import com.yxrobot.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单状态管理服务类
 * 支持状态流转功能
 */
@Service
public class OrderStatusService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    /**
     * 更新单个订单状态
     * 
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @param operator 操作人
     * @param notes 操作备注
     * @return 更新结果
     */
    @Transactional
    public boolean updateOrderStatus(Long orderId, String newStatus, String operator, String notes) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证状态流转是否合法
        OrderStatus currentStatus = order.getStatus();
        OrderStatus targetStatus = OrderStatus.fromCode(newStatus);
        
        if (!isValidStatusTransition(currentStatus, targetStatus)) {
            throw new RuntimeException("不允许的状态流转：从 " + currentStatus.getDescription() + " 到 " + targetStatus.getDescription());
        }

        // 更新订单状态
        int result = orderMapper.updateStatus(orderId, newStatus);
        if (result > 0) {
            // 记录操作日志
            logStatusChange(orderId, currentStatus.getCode(), newStatus, operator, notes);
            return true;
        }
        return false;
    }

    /**
     * 更新单个订单状态（使用DTO）
     * 
     * @param orderId 订单ID
     * @param statusUpdateDTO 状态更新信息
     * @return 更新结果
     */
    @Transactional
    public boolean updateOrderStatus(Long orderId, OrderStatusUpdateDTO statusUpdateDTO) {
        return updateOrderStatus(orderId, statusUpdateDTO.getStatus(), statusUpdateDTO.getOperator(), statusUpdateDTO.getNotes());
    }

    /**
     * 批量更新订单状态
     * 
     * @param orderIds 订单ID列表
     * @param newStatus 新状态
     * @param operator 操作人
     * @param notes 操作备注
     * @return 批量操作结果
     */
    @Transactional
    public BatchUpdateResult batchUpdateOrderStatus(List<Long> orderIds, String newStatus, String operator, String notes) {
        BatchUpdateResult result = new BatchUpdateResult();
        result.setTotalCount(orderIds.size());
        
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        List<String> failedReasons = new ArrayList<>();

        OrderStatus targetStatus = OrderStatus.fromCode(newStatus);

        for (Long orderId : orderIds) {
            try {
                // 查询订单
                Order order = orderMapper.selectById(orderId);
                if (order == null) {
                    failedIds.add(orderId);
                    failedReasons.add("订单不存在");
                    continue;
                }

                // 验证状态流转是否合法
                OrderStatus currentStatus = order.getStatus();
                if (!isValidStatusTransition(currentStatus, targetStatus)) {
                    failedIds.add(orderId);
                    failedReasons.add("不允许的状态流转：从 " + currentStatus.getDescription() + " 到 " + targetStatus.getDescription());
                    continue;
                }

                // 更新订单状态
                int updateResult = orderMapper.updateStatus(orderId, newStatus);
                if (updateResult > 0) {
                    successIds.add(orderId);
                    // 记录操作日志
                    logStatusChange(orderId, currentStatus.getCode(), newStatus, operator, notes);
                } else {
                    failedIds.add(orderId);
                    failedReasons.add("数据库更新失败");
                }
            } catch (Exception e) {
                failedIds.add(orderId);
                failedReasons.add(e.getMessage());
            }
        }

        result.setSuccessCount(successIds.size());
        result.setFailedCount(failedIds.size());
        result.setSuccessIds(successIds);
        result.setFailedIds(failedIds);
        result.setFailedReasons(failedReasons);

        return result;
    }

    /**
     * 批量更新订单状态（使用DTO）
     * 
     * @param orderIds 订单ID列表
     * @param statusUpdateDTO 状态更新信息
     * @return 批量操作结果
     */
    @Transactional
    public BatchUpdateResult batchUpdateOrderStatus(List<Long> orderIds, OrderStatusUpdateDTO statusUpdateDTO) {
        return batchUpdateOrderStatus(orderIds, statusUpdateDTO.getStatus(), statusUpdateDTO.getOperator(), statusUpdateDTO.getNotes());
    }

    /**
     * 验证状态流转是否合法
     * 
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        return currentStatus.canTransitionTo(targetStatus);
    }

    /**
     * 记录状态变更日志
     * 
     * @param orderId 订单ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param operator 操作人
     * @param notes 操作备注
     */
    private void logStatusChange(Long orderId, String oldStatus, String newStatus, String operator, String notes) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setAction("状态变更");
        log.setOperator(operator);
        
        String logNotes = String.format("订单状态从 %s 变更为 %s", 
            OrderStatus.fromCode(oldStatus).getDescription(),
            OrderStatus.fromCode(newStatus).getDescription());
        
        if (notes != null && !notes.trim().isEmpty()) {
            logNotes += "，备注：" + notes;
        }
        
        log.setNotes(logNotes);
        log.setCreatedAt(LocalDateTime.now());
        
        orderLogMapper.insert(log);
    }

    /**
     * 获取订单可用的状态流转选项
     * 
     * @param orderId 订单ID
     * @return 可用的状态流转选项
     */
    public List<OrderStatus> getAvailableStatusTransitions(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        OrderStatus currentStatus = order.getStatus();
        List<OrderStatus> availableTransitions = new ArrayList<>();

        // 根据当前状态获取可用的流转选项
        for (OrderStatus status : OrderStatus.values()) {
            if (currentStatus.canTransitionTo(status)) {
                availableTransitions.add(status);
            }
        }

        return availableTransitions;
    }

    /**
     * 验证订单状态变更权限
     * 
     * @param orderId 订单ID
     * @param operator 操作人
     * @param targetStatus 目标状态
     * @return 是否有权限
     */
    public boolean hasStatusChangePermission(Long orderId, String operator, String targetStatus) {
        // 这里可以根据业务需求实现权限验证逻辑
        // 例如：某些状态只能由特定角色的用户变更
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }

        OrderStatus currentStatus = order.getStatus();
        OrderStatus target = OrderStatus.fromCode(targetStatus);

        // 基本的状态流转验证
        if (!isValidStatusTransition(currentStatus, target)) {
            return false;
        }

        // 这里可以添加更多的权限验证逻辑
        // 例如：
        // - 只有订单创建人或管理员可以取消订单
        // - 只有仓库人员可以标记为已发货
        // - 只有客服人员可以标记为已完成
        
        return true;
    }

    /**
     * 批量操作结果类
     */
    public static class BatchUpdateResult {
        private int totalCount;
        private int successCount;
        private int failedCount;
        private List<Long> successIds;
        private List<Long> failedIds;
        private List<String> failedReasons;

        public BatchUpdateResult() {
            this.successIds = new ArrayList<>();
            this.failedIds = new ArrayList<>();
            this.failedReasons = new ArrayList<>();
        }

        // Getter和Setter方法
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public List<Long> getSuccessIds() {
            return successIds;
        }

        public void setSuccessIds(List<Long> successIds) {
            this.successIds = successIds;
        }

        public List<Long> getFailedIds() {
            return failedIds;
        }

        public void setFailedIds(List<Long> failedIds) {
            this.failedIds = failedIds;
        }

        public List<String> getFailedReasons() {
            return failedReasons;
        }

        public void setFailedReasons(List<String> failedReasons) {
            this.failedReasons = failedReasons;
        }
    }
}