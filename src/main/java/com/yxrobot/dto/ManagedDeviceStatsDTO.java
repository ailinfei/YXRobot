package com.yxrobot.dto;

/**
 * 设备管理统计DTO类
 * 适配前端DeviceManagement.vue页面的统计卡片需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 */
public class ManagedDeviceStatsDTO {
    
    private Integer total;              // 设备总数
    private Integer online;             // 在线设备数
    private Integer offline;            // 离线设备数
    private Integer error;              // 故障设备数
    private Integer maintenance;        // 维护中设备数
    
    // 按型号分别统计（可选）
    private ModelStatsDTO eduStats;     // 教育版统计
    private ModelStatsDTO homeStats;    // 家庭版统计
    private ModelStatsDTO proStats;     // 专业版统计
    
    // 构造函数
    public ManagedDeviceStatsDTO() {}
    
    public ManagedDeviceStatsDTO(Integer total, Integer online, Integer offline, Integer error, Integer maintenance) {
        this.total = total;
        this.online = online;
        this.offline = offline;
        this.error = error;
        this.maintenance = maintenance;
    }
    
    // 内部类：型号统计DTO
    public static class ModelStatsDTO {
        private Integer total;
        private Integer online;
        private Integer offline;
        private Integer error;
        private Integer maintenance;
        
        public ModelStatsDTO() {}
        
        public ModelStatsDTO(Integer total, Integer online, Integer offline, Integer error, Integer maintenance) {
            this.total = total;
            this.online = online;
            this.offline = offline;
            this.error = error;
            this.maintenance = maintenance;
        }
        
        // Getter和Setter方法
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        
        public Integer getOnline() { return online; }
        public void setOnline(Integer online) { this.online = online; }
        
        public Integer getOffline() { return offline; }
        public void setOffline(Integer offline) { this.offline = offline; }
        
        public Integer getError() { return error; }
        public void setError(Integer error) { this.error = error; }
        
        public Integer getMaintenance() { return maintenance; }
        public void setMaintenance(Integer maintenance) { this.maintenance = maintenance; }
    }
    
    // Getter和Setter方法
    public Integer getTotal() {
        return total;
    }
    
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public Integer getOnline() {
        return online;
    }
    
    public void setOnline(Integer online) {
        this.online = online;
    }
    
    public Integer getOffline() {
        return offline;
    }
    
    public void setOffline(Integer offline) {
        this.offline = offline;
    }
    
    public Integer getError() {
        return error;
    }
    
    public void setError(Integer error) {
        this.error = error;
    }
    
    public Integer getMaintenance() {
        return maintenance;
    }
    
    public void setMaintenance(Integer maintenance) {
        this.maintenance = maintenance;
    }
    
    public ModelStatsDTO getEduStats() {
        return eduStats;
    }
    
    public void setEduStats(ModelStatsDTO eduStats) {
        this.eduStats = eduStats;
    }
    
    public ModelStatsDTO getHomeStats() {
        return homeStats;
    }
    
    public void setHomeStats(ModelStatsDTO homeStats) {
        this.homeStats = homeStats;
    }
    
    public ModelStatsDTO getProStats() {
        return proStats;
    }
    
    public void setProStats(ModelStatsDTO proStats) {
        this.proStats = proStats;
    }
}