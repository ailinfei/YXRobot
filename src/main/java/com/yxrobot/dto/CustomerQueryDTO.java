package com.yxrobot.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 客户查询数据传输对象
 * 用于前端查询客户时的请求参数
 */
public class CustomerQueryDTO {
    
    // 分页参数
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 20;
    
    // 搜索关键词
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;
    
    // 筛选条件
    @Pattern(regexp = "^(REGULAR|VIP|PREMIUM)$", message = "客户等级必须为REGULAR、VIP或PREMIUM")
    private String customerLevel;
    
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "客户状态必须为ACTIVE、INACTIVE或SUSPENDED")
    private String customerStatus;
    
    private String deviceType;      // 设备类型筛选
    private String region;          // 地区筛选
    private String industry;        // 行业筛选
    
    // 排序参数
    @Pattern(regexp = "^(name|level|totalSpent|customerValue|lastActiveAt|createdAt)$", 
             message = "排序字段必须为name、level、totalSpent、customerValue、lastActiveAt或createdAt")
    private String sortBy;
    
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向必须为ASC或DESC")
    private String sortOrder = "DESC";
    
    // 时间范围筛选
    private String startDate;       // 开始日期
    private String endDate;         // 结束日期
    
    // 金额范围筛选
    private String minSpent;        // 最小消费金额
    private String maxSpent;        // 最大消费金额
    
    // 客户价值范围筛选
    private String minCustomerValue;    // 最小客户价值
    private String maxCustomerValue;    // 最大客户价值
    
    // 活跃状态筛选
    private Boolean isActive;           // 是否活跃
    
    // 标签筛选
    private String tags;                // 客户标签（逗号分隔）
    
    // 信用等级筛选
    private String creditLevel;         // 信用等级
    
    // 注册时间范围筛选
    private String registeredStartDate; // 注册开始日期
    private String registeredEndDate;   // 注册结束日期
    
    // 最后活跃时间范围筛选
    private String lastActiveStartDate; // 最后活跃开始日期
    private String lastActiveEndDate;   // 最后活跃结束日期
    
    // 高级筛选选项
    private Boolean hasDevices;         // 是否有设备
    private Boolean hasOrders;          // 是否有订单
    private Boolean hasServiceRecords;  // 是否有服务记录
    
    // 排序字段扩展
    private String secondarySortBy;     // 次要排序字段
    private String secondarySortOrder;  // 次要排序方向
    
    // 分页偏移量（内部计算）
    private Integer offset;
    
    // 构造函数
    public CustomerQueryDTO() {}
    
    /**
     * 创建Builder实例
     */
    public static CustomerQueryDTOBuilder builder() {
        return new CustomerQueryDTOBuilder();
    }
    
    /**
     * Builder类
     */
    public static class CustomerQueryDTOBuilder {
        private CustomerQueryDTO dto = new CustomerQueryDTO();
        
        public CustomerQueryDTOBuilder page(Integer page) {
            dto.setPage(page);
            return this;
        }
        
        public CustomerQueryDTOBuilder pageSize(Integer pageSize) {
            dto.setPageSize(pageSize);
            return this;
        }
        
        public CustomerQueryDTOBuilder keyword(String keyword) {
            dto.setKeyword(keyword);
            return this;
        }
        
        public CustomerQueryDTOBuilder customerLevel(String customerLevel) {
            dto.setCustomerLevel(customerLevel);
            return this;
        }
        
        public CustomerQueryDTOBuilder level(String level) {
            dto.setLevel(level);
            return this;
        }
        
        public CustomerQueryDTOBuilder customerStatus(String customerStatus) {
            dto.setCustomerStatus(customerStatus);
            return this;
        }
        
        public CustomerQueryDTOBuilder status(String status) {
            dto.setStatus(status);
            return this;
        }
        
        public CustomerQueryDTOBuilder region(String region) {
            dto.setRegion(region);
            return this;
        }
        
        public CustomerQueryDTOBuilder industry(String industry) {
            dto.setIndustry(industry);
            return this;
        }
        
        public CustomerQueryDTOBuilder sortBy(String sortBy) {
            dto.setSortBy(sortBy);
            return this;
        }
        
        public CustomerQueryDTOBuilder sortOrder(String sortOrder) {
            dto.setSortOrder(sortOrder);
            return this;
        }
        
        public CustomerQueryDTOBuilder isActive(Boolean isActive) {
            dto.setIsActive(isActive);
            return this;
        }
        
        public CustomerQueryDTOBuilder startDate(String startDate) {
            dto.setStartDate(startDate);
            return this;
        }
        
        public CustomerQueryDTOBuilder endDate(String endDate) {
            dto.setEndDate(endDate);
            return this;
        }
        
        public CustomerQueryDTO build() {
            return dto;
        }
    }
    
    // Getter和Setter方法
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getCustomerLevel() {
        return customerLevel;
    }
    
    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }
    
    public String getCustomerStatus() {
        return customerStatus;
    }
    
    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getMinSpent() {
        return minSpent;
    }
    
    public void setMinSpent(String minSpent) {
        this.minSpent = minSpent;
    }
    
    public String getMaxSpent() {
        return maxSpent;
    }
    
    public void setMaxSpent(String maxSpent) {
        this.maxSpent = maxSpent;
    }
    
    public String getMinCustomerValue() {
        return minCustomerValue;
    }
    
    public void setMinCustomerValue(String minCustomerValue) {
        this.minCustomerValue = minCustomerValue;
    }
    
    public String getMaxCustomerValue() {
        return maxCustomerValue;
    }
    
    public void setMaxCustomerValue(String maxCustomerValue) {
        this.maxCustomerValue = maxCustomerValue;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getCreditLevel() {
        return creditLevel;
    }
    
    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }
    
    public String getRegisteredStartDate() {
        return registeredStartDate;
    }
    
    public void setRegisteredStartDate(String registeredStartDate) {
        this.registeredStartDate = registeredStartDate;
    }
    
    public String getRegisteredEndDate() {
        return registeredEndDate;
    }
    
    public void setRegisteredEndDate(String registeredEndDate) {
        this.registeredEndDate = registeredEndDate;
    }
    
    public String getLastActiveStartDate() {
        return lastActiveStartDate;
    }
    
    public void setLastActiveStartDate(String lastActiveStartDate) {
        this.lastActiveStartDate = lastActiveStartDate;
    }
    
    public String getLastActiveEndDate() {
        return lastActiveEndDate;
    }
    
    public void setLastActiveEndDate(String lastActiveEndDate) {
        this.lastActiveEndDate = lastActiveEndDate;
    }
    
    public Boolean getHasDevices() {
        return hasDevices;
    }
    
    public void setHasDevices(Boolean hasDevices) {
        this.hasDevices = hasDevices;
    }
    
    public Boolean getHasOrders() {
        return hasOrders;
    }
    
    public void setHasOrders(Boolean hasOrders) {
        this.hasOrders = hasOrders;
    }
    
    public Boolean getHasServiceRecords() {
        return hasServiceRecords;
    }
    
    public void setHasServiceRecords(Boolean hasServiceRecords) {
        this.hasServiceRecords = hasServiceRecords;
    }
    
    public String getSecondarySortBy() {
        return secondarySortBy;
    }
    
    public void setSecondarySortBy(String secondarySortBy) {
        this.secondarySortBy = secondarySortBy;
    }
    
    public String getSecondarySortOrder() {
        return secondarySortOrder;
    }
    
    public void setSecondarySortOrder(String secondarySortOrder) {
        this.secondarySortOrder = secondarySortOrder;
    }
    
    /**
     * 计算偏移量
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
    
    /**
     * 设置偏移量（兼容方法）
     */
    public void setOffset(Integer offset) {
        if (offset != null) {
            // 根据偏移量反推页码
            this.page = (offset / pageSize) + 1;
        }
    }
    
    /**
     * 设置偏移量（兼容方法 - int类型）
     */
    public void setOffset(int offset) {
        // 根据偏移量反推页码
        this.page = (offset / pageSize) + 1;
    }
    
    /**
     * 设置是否活跃（兼容方法）
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        this.customerStatus = isActive ? "ACTIVE" : "INACTIVE";
    }
    
    /**
     * 获取客户等级（兼容方法）
     */
    public String getLevel() {
        return customerLevel;
    }
    
    /**
     * 设置客户等级（兼容方法）
     */
    public void setLevel(String level) {
        this.customerLevel = level;
    }
    
    /**
     * 获取客户状态（兼容方法）
     */
    public String getStatus() {
        return customerStatus;
    }
    
    /**
     * 设置客户状态（兼容方法）
     */
    public void setStatus(String status) {
        this.customerStatus = status;
    }
    
    /**
     * 获取排序SQL片段
     */
    public String getSortSql() {
        StringBuilder sortSql = new StringBuilder();
        
        // 主要排序字段
        if (sortBy != null) {
            String column = getSortColumn(sortBy);
            sortSql.append(column).append(" ").append(sortOrder != null ? sortOrder : "DESC");
        } else {
            sortSql.append("created_at DESC");
        }
        
        // 次要排序字段
        if (secondarySortBy != null && !secondarySortBy.equals(sortBy)) {
            String secondaryColumn = getSortColumn(secondarySortBy);
            sortSql.append(", ").append(secondaryColumn).append(" ")
                   .append(secondarySortOrder != null ? secondarySortOrder : "DESC");
        }
        
        return sortSql.toString();
    }
    
    /**
     * 获取排序字段对应的数据库列名
     */
    private String getSortColumn(String field) {
        switch (field) {
            case "name":
            case "customerName":
                return "customer_name";
            case "level":
            case "customerLevel":
                return "customer_level";
            case "totalSpent":
                return "total_spent";
            case "customerValue":
                return "customer_value";
            case "lastActiveAt":
                return "last_active_at";
            case "registeredAt":
                return "registered_at";
            case "createdAt":
                return "created_at";
            case "updatedAt":
                return "updated_at";
            default:
                return "created_at";
        }
    }
    
    /**
     * 检查是否有高级筛选条件
     */
    public boolean hasAdvancedFilters() {
        return (minCustomerValue != null || maxCustomerValue != null ||
                tags != null || creditLevel != null ||
                registeredStartDate != null || registeredEndDate != null ||
                lastActiveStartDate != null || lastActiveEndDate != null ||
                hasDevices != null || hasOrders != null || hasServiceRecords != null);
    }
    
    /**
     * 检查是否有时间范围筛选
     */
    public boolean hasDateRangeFilters() {
        return (startDate != null || endDate != null ||
                registeredStartDate != null || registeredEndDate != null ||
                lastActiveStartDate != null || lastActiveEndDate != null);
    }
    
    /**
     * 检查是否有金额范围筛选
     */
    public boolean hasAmountRangeFilters() {
        return (minSpent != null || maxSpent != null ||
                minCustomerValue != null || maxCustomerValue != null);
    }
    
    @Override
    public String toString() {
        return "CustomerQueryDTO{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", keyword='" + keyword + '\'' +
                ", customerLevel='" + customerLevel + '\'' +
                ", customerStatus='" + customerStatus + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", region='" + region + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}