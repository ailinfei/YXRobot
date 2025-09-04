package com.yxrobot.service;

import com.yxrobot.dto.*;
import com.yxrobot.entity.*;
import com.yxrobot.mapper.*;
import com.yxrobot.validator.OrderFormValidator;
import com.yxrobot.validator.DataIntegrityValidator;
import com.yxrobot.exception.OrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理服务类
 * 支持前端列表和操作功能
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShippingInfoMapper shippingInfoMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OrderFormValidator orderFormValidator;

    @Autowired
    private DataIntegrityValidator dataIntegrityValidator;

    /**
     * 分页查询订单列表，支持搜索和筛选
     * 
     * @param queryDTO 查询条件
     * @return 订单分页数据
     */
    public OrderQueryResult getOrders(OrderQueryDTO queryDTO) {
        // 计算分页参数
        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        
        // 查询订单列表
        List<Order> orders = orderMapper.selectOrdersWithPagination(
            queryDTO.getKeyword(),
            queryDTO.getType(),
            queryDTO.getStatus(),
            queryDTO.getStartDate(),
            queryDTO.getEndDate(),
            offset,
            queryDTO.getSize()
        );
        
        // 查询总数
        int total = orderMapper.countOrders(
            queryDTO.getKeyword(),
            queryDTO.getType(),
            queryDTO.getStatus(),
            queryDTO.getStartDate(),
            queryDTO.getEndDate()
        );
        
        // 转换为DTO
        List<OrderDTO> orderDTOs = orders.stream()
            .map(this::convertToOrderDTO)
            .collect(Collectors.toList());
        
        return new OrderQueryResult(orderDTOs, total);
    }

    /**
     * 根据ID查询订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        return convertToOrderDTO(order);
    }

    /**
     * 创建订单
     * 
     * @param createDTO 订单创建数据
     * @return 创建的订单信息
     */
    @Transactional
    public OrderDTO createOrder(OrderCreateDTO createDTO) {
        logger.info("开始创建订单，订单号: {}", createDTO.getOrderNumber());
        
        try {
            // 表单数据验证
            orderFormValidator.validateCreateForm(createDTO);
            
            // 数据完整性验证
            dataIntegrityValidator.validateCreateDataIntegrity(createDTO);
            
            // 生成订单号（如果没有提供）
            String orderNumber = createDTO.getOrderNumber();
            if (orderNumber == null || orderNumber.trim().isEmpty()) {
                orderNumber = generateOrderNumber();
            }
            
                // 创建订单对象
            Order order = new Order();
            order.setOrderNumber(orderNumber);
            order.setType(OrderType.fromCode(createDTO.getType())); // 修复：使用fromCode方法转换
            order.setStatus(OrderStatus.PENDING);
            order.setCustomerId(createDTO.getCustomerId());
            order.setDeliveryAddress(createDTO.getDeliveryAddress());
            order.setSubtotal(createDTO.getSubtotal());
            order.setShippingFee(createDTO.getShippingFee());
            order.setDiscount(createDTO.getDiscount());
            order.setTotalAmount(createDTO.getTotalAmount());
            order.setCurrency(createDTO.getCurrency());
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setPaymentMethod(createDTO.getPaymentMethod());
            order.setExpectedDeliveryDate(createDTO.getExpectedDeliveryDate());
            order.setSalesPerson(createDTO.getSalesPerson());
            order.setNotes(createDTO.getNotes());
            order.setRentalStartDate(createDTO.getRentalStartDate());
            order.setRentalEndDate(createDTO.getRentalEndDate());
            order.setRentalDays(createDTO.getRentalDays());
            order.setRentalNotes(createDTO.getRentalNotes());
            order.setCreatedAt(LocalDateTime.now());
            order.setCreatedBy("admin"); // TODO: 从当前用户获取
            order.setIsDeleted(false);
            
            // 插入订单
            orderMapper.insert(order);
            
            // 创建订单商品
            if (createDTO.getOrderItems() != null && !createDTO.getOrderItems().isEmpty()) {
                for (OrderCreateDTO.OrderItemCreateDTO itemDTO : createDTO.getOrderItems()) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(order.getId());
                    item.setProductId(itemDTO.getProductId());
                    item.setProductName(itemDTO.getProductName());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setUnitPrice(itemDTO.getUnitPrice());
                    item.setTotalPrice(itemDTO.getTotalPrice());
                    item.setCreatedAt(LocalDateTime.now());
                    
                    orderItemMapper.insert(item);
                }
            }
        
            // 记录操作日志
            logOrderAction(order.getId(), "创建订单", "admin", "订单创建成功");
            
            return convertToOrderDTO(order);
        } catch (Exception e) {
            logger.error("创建订单失败，订单号: {}", createDTO.getOrderNumber(), e);
            throw e;
        }
    }

    /**
     * 更新订单
     * 
     * @param orderId 订单ID
     * @param updateDTO 更新数据
     * @return 更新后的订单信息
     */
    @Transactional
    public OrderDTO updateOrder(Long orderId, OrderDTO updateDTO) {
        logger.info("开始更新订单，订单ID: {}", orderId);
        
        try {
            // 验证订单ID
            if (orderId == null || orderId <= 0) {
                throw OrderException.validationFailed("orderId", "订单ID无效");
            }
            
            // 表单数据验证
            orderFormValidator.validateUpdateForm(updateDTO);
            
            // 数据完整性验证
            dataIntegrityValidator.validateUpdateDataIntegrity(orderId, updateDTO);
            
            // 查询现有订单
            Order existingOrder = orderMapper.selectById(orderId);
            if (existingOrder == null) {
                throw OrderException.orderNotFound(orderId.toString());
            }
            
            // 检查订单是否可以修改
            if ("completed".equals(existingOrder.getStatus().getCode()) || 
                "cancelled".equals(existingOrder.getStatus().getCode())) {
                throw OrderException.businessRuleViolation("订单状态", "已完成或已取消的订单不能修改");
            }
            
                // 更新订单信息
            existingOrder.setType(OrderType.fromCode(updateDTO.getType()));
            existingOrder.setCustomerId(updateDTO.getCustomerId());
            existingOrder.setDeliveryAddress(updateDTO.getDeliveryAddress());
            existingOrder.setSubtotal(updateDTO.getSubtotal());
            existingOrder.setShippingFee(updateDTO.getShippingFee());
            existingOrder.setDiscount(updateDTO.getDiscount());
            existingOrder.setTotalAmount(updateDTO.getTotalAmount());
            existingOrder.setCurrency(updateDTO.getCurrency());
            existingOrder.setPaymentMethod(updateDTO.getPaymentMethod());
            existingOrder.setExpectedDeliveryDate(updateDTO.getExpectedDeliveryDate());
            existingOrder.setSalesPerson(updateDTO.getSalesPerson());
            existingOrder.setNotes(updateDTO.getNotes());
            existingOrder.setRentalStartDate(updateDTO.getRentalStartDate());
            existingOrder.setRentalEndDate(updateDTO.getRentalEndDate());
            existingOrder.setRentalDays(updateDTO.getRentalDays());
            existingOrder.setRentalNotes(updateDTO.getRentalNotes());
            existingOrder.setUpdatedAt(LocalDateTime.now());
            
            orderMapper.updateById(existingOrder);
            
            // 删除原有订单商品
            orderItemMapper.deleteByOrderId(orderId);
            
            // 重新创建订单商品
            if (updateDTO.getOrderItems() != null && !updateDTO.getOrderItems().isEmpty()) {
                for (OrderItemDTO itemDTO : updateDTO.getOrderItems()) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(orderId);
                    item.setProductId(itemDTO.getProductId());
                    item.setProductName(itemDTO.getProductName());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setUnitPrice(itemDTO.getUnitPrice());
                    item.setTotalPrice(itemDTO.getTotalPrice());
                    item.setCreatedAt(LocalDateTime.now());
                    
                    orderItemMapper.insert(item);
                }
            }
        
            // 记录操作日志
            logOrderAction(orderId, "更新订单", "admin", "订单信息更新成功");
            
            return convertToOrderDTO(existingOrder);
        } catch (Exception e) {
            logger.error("更新订单失败，订单ID: {}", orderId, e);
            throw e;
        }
    }

    /**
     * 删除订单（软删除）
     * 
     * @param orderId 订单ID
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 软删除订单
        order.setIsDeleted(true);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 记录操作日志
        logOrderAction(orderId, "删除订单", "admin", "订单已删除");
    }

    /**
     * 转换Order实体为OrderDTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setType(order.getType() != null ? order.getType().getCode() : null);
        dto.setStatus(order.getStatus() != null ? order.getStatus().getCode() : null);
        dto.setCustomerId(order.getCustomerId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCurrency(order.getCurrency());
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().getCode() : null);
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentTime(order.getPaymentTime());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setSalesPerson(order.getSalesPerson());
        dto.setNotes(order.getNotes());
        dto.setRentalStartDate(order.getRentalStartDate());
        dto.setRentalEndDate(order.getRentalEndDate());
        dto.setRentalDays(order.getRentalDays());
        dto.setRentalNotes(order.getRentalNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setCreatedBy(order.getCreatedBy());
        
        // 加载关联数据
        loadOrderRelatedData(dto, order.getId());
        
        return dto;
    }

    /**
     * 加载订单关联数据
     */
    private void loadOrderRelatedData(OrderDTO dto, Long orderId) {
        // 加载订单商品
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        List<OrderItemDTO> itemDTOs = items.stream()
            .map(this::convertToOrderItemDTO)
            .collect(Collectors.toList());
        dto.setOrderItems(itemDTOs);
        
        // 加载物流信息
        ShippingInfo shippingInfo = shippingInfoMapper.selectByOrderId(orderId);
        if (shippingInfo != null) {
            dto.setShippingInfo(convertToShippingInfoDTO(shippingInfo));
        }
        
        // 加载操作日志
        List<OrderLog> logs = orderLogMapper.selectByOrderId(orderId);
        List<OrderLogDTO> logDTOs = logs.stream()
            .map(this::convertToOrderLogDTO)
            .collect(Collectors.toList());
        dto.setOrderLogs(logDTOs);
        
        // 加载客户信息
        Order order = orderMapper.selectById(orderId); // 获取订单信息
        if (order != null && order.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(order.getCustomerId());
            if (customer != null) {
                dto.setCustomerName(customer.getName());
                dto.setCustomerPhone(customer.getPhone());
            } else {
                dto.setCustomerName("未知客户");
                dto.setCustomerPhone("");
            }
        } else {
            dto.setCustomerName("未知客户");
            dto.setCustomerPhone("");
        }
    }

    /**
     * 转换OrderItem实体为OrderItemDTO
     */
    private OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrderId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    /**
     * 转换ShippingInfo实体为ShippingInfoDTO
     */
    private ShippingInfoDTO convertToShippingInfoDTO(ShippingInfo shippingInfo) {
        ShippingInfoDTO dto = new ShippingInfoDTO();
        dto.setId(shippingInfo.getId());
        dto.setOrderId(shippingInfo.getOrderId());
        dto.setCompany(shippingInfo.getCompany());
        dto.setTrackingNumber(shippingInfo.getTrackingNumber());
        dto.setShippedAt(shippingInfo.getShippedAt());
        dto.setDeliveredAt(shippingInfo.getDeliveredAt());
        dto.setNotes(shippingInfo.getNotes());
        return dto;
    }

    /**
     * 转换OrderLog实体为OrderLogDTO
     */
    private OrderLogDTO convertToOrderLogDTO(OrderLog log) {
        OrderLogDTO dto = new OrderLogDTO();
        dto.setId(log.getId());
        dto.setOrderId(log.getOrderId());
        dto.setAction(log.getAction());
        dto.setOperator(log.getOperator());
        dto.setNotes(log.getNotes());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        // 生成格式：ORD + 年月日 + 6位随机数
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%06d", (int)(Math.random() * 1000000));
        return "ORD" + date + random;
    }

    /**
     * 记录订单操作日志
     */
    private void logOrderAction(Long orderId, String action, String operator, String notes) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setAction(action);
        log.setOperator(operator);
        log.setNotes(notes);
        log.setCreatedAt(LocalDateTime.now());
        
        orderLogMapper.insert(log);
    }

    /**
     * 订单查询结果类
     */
    public static class OrderQueryResult {
        private List<OrderDTO> list;
        private int total;

        public OrderQueryResult(List<OrderDTO> list, int total) {
            this.list = list;
            this.total = total;
        }

        public List<OrderDTO> getList() {
            return list;
        }

        public void setList(List<OrderDTO> list) {
            this.list = list;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}