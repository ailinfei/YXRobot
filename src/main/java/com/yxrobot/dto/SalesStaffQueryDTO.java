package com.yxrobot.dto;

/**
 * 销售人员查询条件DTO
 */
public class SalesStaffQueryDTO {
    
    private Integer page;
    private Integer pageSize;
    private String keyword;
    private String department;
    private String position;
    private Boolean active;
    
    // 构造函数
    public SalesStaffQueryDTO() {}
    
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
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
}