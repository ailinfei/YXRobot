# 客户管理模块 - 关联表查询示例

## 🚨 重要说明

本文档展示如何遵循项目数据库设计规范，通过关联表实现复杂查询，**严格禁止使用外键约束**。

## 📋 项目数据库设计规范（来自README）

### 核心原则
1. **禁止使用外键约束**: 所有表与表之间的关联必须通过关联表实现
2. **降低耦合度**: 通过关联表解耦主表之间的直接依赖关系
3. **提高扩展性**: 关联表设计便于后续功能扩展和数据迁移

### 关联表命名规范
```sql
-- 格式: {主表名}_{关联表名}_relation
customer_device_relation    -- 客户设备关联表
customer_order_relation     -- 客户订单关联表
customer_service_relation   -- 客户服务关联表
device_service_relation     -- 设备服务关联表
```

## 🔍 关联查询示例

### 1. 客户设备关联查询

#### 查询客户的所有设备
```sql
-- 查询客户ID为1的所有设备
SELECT 
    d.device_id,
    d.device_name,
    d.device_model,
    d.device_status,
    cdr.relation_type,
    cdr.start_date,
    cdr.end_date
FROM devices d
INNER JOIN customer_device_relation cdr ON d.id = cdr.device_id
WHERE cdr.customer_id = 1 
AND cdr.status = 1 
AND d.is_deleted = 0;
```

#### 统计客户设备数量
```sql
-- 统计客户的设备数量（按类型分组）
SELECT 
    c.customer_name,
    COUNT(*) as total_devices,
    COUNT(CASE WHEN cdr.relation_type = 'purchased' THEN 1 END) as purchased_devices,
    COUNT(CASE WHEN cdr.relation_type = 'rental' THEN 1 END) as rental_devices
FROM customers c
INNER JOIN customer_device_relation cdr ON c.id = cdr.customer_id
INNER JOIN devices d ON cdr.device_id = d.id
WHERE c.is_deleted = 0 
AND cdr.status = 1 
AND d.is_deleted = 0
GROUP BY c.id, c.customer_name;
```

### 2. 客户订单关联查询

#### 查询客户的所有订单
```sql
-- 查询客户的订单历史
SELECT 
    o.order_number,
    o.order_type,
    o.order_status,
    o.total_amount,
    o.order_date,
    cor.customer_role
FROM orders o
INNER JOIN customer_order_relation cor ON o.id = cor.order_id
WHERE cor.customer_id = 1 
AND cor.status = 1 
AND o.is_deleted = 0
ORDER BY o.order_date DESC;
```

#### 客户消费统计
```sql
-- 计算客户的总消费金额
SELECT 
    c.customer_name,
    COALESCE(SUM(o.total_amount), 0) as total_spent,
    COUNT(o.id) as total_orders
FROM customers c
LEFT JOIN customer_order_relation cor ON c.id = cor.customer_id AND cor.status = 1
LEFT JOIN orders o ON cor.order_id = o.id AND o.is_deleted = 0
WHERE c.is_deleted = 0
GROUP BY c.id, c.customer_name;
```

### 3. 客户服务关联查询

#### 查询客户的服务记录
```sql
-- 查询客户的服务历史
SELECT 
    sr.service_number,
    sr.service_type,
    sr.service_title,
    sr.service_status,
    sr.service_date,
    csr.customer_role
FROM service_records sr
INNER JOIN customer_service_relation csr ON sr.id = csr.service_id
WHERE csr.customer_id = 1 
AND csr.status = 1 
AND sr.is_deleted = 0
ORDER BY sr.service_date DESC;
```

### 4. 复杂多表关联查询

#### 客户完整信息查询（设备+订单+服务）
```sql
-- 查询客户的完整业务信息
SELECT 
    c.customer_name,
    c.customer_level,
    c.total_spent,
    device_stats.device_count,
    order_stats.order_count,
    service_stats.service_count
FROM customers c
LEFT JOIN (
    SELECT 
        cdr.customer_id,
        COUNT(*) as device_count
    FROM customer_device_relation cdr
    INNER JOIN devices d ON cdr.device_id = d.id
    WHERE cdr.status = 1 AND d.is_deleted = 0
    GROUP BY cdr.customer_id
) device_stats ON c.id = device_stats.customer_id
LEFT JOIN (
    SELECT 
        cor.customer_id,
        COUNT(*) as order_count
    FROM customer_order_relation cor
    INNER JOIN orders o ON cor.order_id = o.id
    WHERE cor.status = 1 AND o.is_deleted = 0
    GROUP BY cor.customer_id
) order_stats ON c.id = order_stats.customer_id
LEFT JOIN (
    SELECT 
        csr.customer_id,
        COUNT(*) as service_count
    FROM customer_service_relation csr
    INNER JOIN service_records sr ON csr.service_id = sr.id
    WHERE csr.status = 1 AND sr.is_deleted = 0
    GROUP BY csr.customer_id
) service_stats ON c.id = service_stats.customer_id
WHERE c.is_deleted = 0;
```

## 🔧 MyBatis映射器实现

### CustomerMapper.java
```java
@Mapper
public interface CustomerMapper {
    
    // 查询客户基本信息
    Customer selectById(@Param("id") Long id);
    
    // 查询客户的设备列表（通过关联表）
    List<Device> selectCustomerDevices(@Param("customerId") Long customerId);
    
    // 查询客户的订单列表（通过关联表）
    List<Order> selectCustomerOrders(@Param("customerId") Long customerId);
    
    // 查询客户的服务记录（通过关联表）
    List<ServiceRecord> selectCustomerServiceRecords(@Param("customerId") Long customerId);
    
    // 客户统计信息
    CustomerStats selectCustomerStats(@Param("customerId") Long customerId);
}
```

### CustomerMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.yxrobot.mapper.CustomerMapper">

    <!-- 查询客户设备列表 -->
    <select id="selectCustomerDevices" resultType="Device">
        SELECT 
            d.id,
            d.device_id as deviceId,
            d.device_name as deviceName,
            d.device_model as deviceModel,
            d.device_status as deviceStatus,
            cdr.relation_type as relationType,
            cdr.start_date as startDate,
            cdr.end_date as endDate
        FROM devices d
        INNER JOIN customer_device_relation cdr ON d.id = cdr.device_id
        WHERE cdr.customer_id = #{customerId} 
        AND cdr.status = 1 
        AND d.is_deleted = 0
        ORDER BY cdr.created_time DESC
    </select>

    <!-- 查询客户订单列表 -->
    <select id="selectCustomerOrders" resultType="Order">
        SELECT 
            o.id,
            o.order_number as orderNumber,
            o.order_type as orderType,
            o.order_status as orderStatus,
            o.total_amount as totalAmount,
            o.order_date as orderDate,
            cor.customer_role as customerRole
        FROM orders o
        INNER JOIN customer_order_relation cor ON o.id = cor.order_id
        WHERE cor.customer_id = #{customerId} 
        AND cor.status = 1 
        AND o.is_deleted = 0
        ORDER BY o.order_date DESC
    </select>

    <!-- 客户统计信息 -->
    <select id="selectCustomerStats" resultType="CustomerStats">
        SELECT 
            c.id as customerId,
            c.customer_name as customerName,
            COALESCE(device_stats.device_count, 0) as deviceCount,
            COALESCE(device_stats.purchased_count, 0) as purchasedDeviceCount,
            COALESCE(device_stats.rental_count, 0) as rentalDeviceCount,
            COALESCE(order_stats.order_count, 0) as orderCount,
            COALESCE(order_stats.total_amount, 0) as totalSpent,
            COALESCE(service_stats.service_count, 0) as serviceCount
        FROM customers c
        LEFT JOIN (
            SELECT 
                cdr.customer_id,
                COUNT(*) as device_count,
                COUNT(CASE WHEN cdr.relation_type = 'purchased' THEN 1 END) as purchased_count,
                COUNT(CASE WHEN cdr.relation_type = 'rental' THEN 1 END) as rental_count
            FROM customer_device_relation cdr
            INNER JOIN devices d ON cdr.device_id = d.id
            WHERE cdr.status = 1 AND d.is_deleted = 0
            GROUP BY cdr.customer_id
        ) device_stats ON c.id = device_stats.customer_id
        LEFT JOIN (
            SELECT 
                cor.customer_id,
                COUNT(*) as order_count,
                SUM(o.total_amount) as total_amount
            FROM customer_order_relation cor
            INNER JOIN orders o ON cor.order_id = o.id
            WHERE cor.status = 1 AND o.is_deleted = 0
            GROUP BY cor.customer_id
        ) order_stats ON c.id = order_stats.customer_id
        LEFT JOIN (
            SELECT 
                csr.customer_id,
                COUNT(*) as service_count
            FROM customer_service_relation csr
            INNER JOIN service_records sr ON csr.service_id = sr.id
            WHERE csr.status = 1 AND sr.is_deleted = 0
            GROUP BY csr.customer_id
        ) service_stats ON c.id = service_stats.customer_id
        WHERE c.id = #{customerId} AND c.is_deleted = 0
    </select>

</mapper>
```

## 🎯 业务逻辑层实现

### CustomerService.java
```java
@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerDeviceRelationMapper customerDeviceRelationMapper;
    
    /**
     * 获取客户完整信息（包含关联数据）
     */
    public CustomerDetailDTO getCustomerDetail(Long customerId) {
        // 1. 获取客户基本信息
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("客户不存在");
        }
        
        // 2. 通过关联表获取设备列表
        List<Device> devices = customerMapper.selectCustomerDevices(customerId);
        
        // 3. 通过关联表获取订单列表
        List<Order> orders = customerMapper.selectCustomerOrders(customerId);
        
        // 4. 通过关联表获取服务记录
        List<ServiceRecord> serviceRecords = customerMapper.selectCustomerServiceRecords(customerId);
        
        // 5. 获取统计信息
        CustomerStats stats = customerMapper.selectCustomerStats(customerId);
        
        // 6. 组装返回数据
        CustomerDetailDTO result = new CustomerDetailDTO();
        result.setCustomer(customer);
        result.setDevices(devices);
        result.setOrders(orders);
        result.setServiceRecords(serviceRecords);
        result.setStats(stats);
        
        return result;
    }
    
    /**
     * 为客户添加设备（通过关联表）
     */
    public void addDeviceToCustomer(Long customerId, Long deviceId, String relationType) {
        // 检查客户是否存在
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("客户不存在");
        }
        
        // 检查设备是否存在
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new DeviceNotFoundException("设备不存在");
        }
        
        // 创建关联关系
        CustomerDeviceRelation relation = new CustomerDeviceRelation();
        relation.setCustomerId(customerId);
        relation.setDeviceId(deviceId);
        relation.setRelationType(relationType);
        relation.setStartDate(LocalDate.now());
        relation.setStatus(1);
        
        customerDeviceRelationMapper.insert(relation);
    }
}
```

## 🚀 优势总结

### 1. 遵循项目规范
- ✅ 严格禁止外键约束
- ✅ 通过关联表实现表间关系
- ✅ 降低表间耦合度

### 2. 提高扩展性
- ✅ 易于添加新的关联类型
- ✅ 支持复杂的多对多关系
- ✅ 便于数据迁移和重构

### 3. 优化查询性能
- ✅ 合理的索引策略
- ✅ 避免复杂的外键约束检查
- ✅ 支持灵活的查询优化

### 4. 数据完整性保障
- ✅ 应用层控制数据一致性
- ✅ 事务处理确保操作原子性
- ✅ 软删除保留数据历史

这种设计完全符合项目的数据库设计规范，确保了系统的可扩展性和维护性！