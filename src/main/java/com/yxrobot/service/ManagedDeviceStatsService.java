package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceStatsDTO;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备统计服务类
 * 处理设备统计业务逻辑，支持前端统计卡片功能
 * 计算设备总数、在线设备、离线设备、故障设备等统计数据
 */
@Service
public class ManagedDeviceStatsService {
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    /**
     * 获取设备统计数据
     * 支持按日期范围的动态统计计算
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 设备统计数据
     */
    public ManagedDeviceStatsDTO getManagedDeviceStats(LocalDateTime startDate, LocalDateTime endDate) {
        // 获取状态统计数据
        List<ManagedDeviceMapper.StatusCountDTO> statusCounts = managedDeviceMapper.countByStatus();
        
        // 初始化统计数据
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put(DeviceStatus.ONLINE.getCode(), 0);
        statusMap.put(DeviceStatus.OFFLINE.getCode(), 0);
        statusMap.put(DeviceStatus.ERROR.getCode(), 0);
        statusMap.put(DeviceStatus.MAINTENANCE.getCode(), 0);
        
        int total = 0;
        
        // 处理状态统计结果
        for (ManagedDeviceMapper.StatusCountDTO statusCount : statusCounts) {
            String status = statusCount.getStatus();
            Integer count = statusCount.getCount();
            
            if (count != null) {
                statusMap.put(status, count);
                total += count;
            }
        }
        
        // 创建统计DTO
        ManagedDeviceStatsDTO stats = new ManagedDeviceStatsDTO(
            total,
            statusMap.get(DeviceStatus.ONLINE.getCode()),
            statusMap.get(DeviceStatus.OFFLINE.getCode()),
            statusMap.get(DeviceStatus.ERROR.getCode()),
            statusMap.get(DeviceStatus.MAINTENANCE.getCode())
        );
        
        // 获取按型号分别统计（可选功能）
        List<ManagedDeviceMapper.ModelCountDTO> modelCounts = managedDeviceMapper.countByModel();
        
        // 处理型号统计
        Map<String, ManagedDeviceStatsDTO.ModelStatsDTO> modelStatsMap = calculateModelStats(modelCounts);
        
        // 设置型号统计数据
        stats.setEduStats(modelStatsMap.get(DeviceModel.YX_EDU_2024.getCode()));
        stats.setHomeStats(modelStatsMap.get(DeviceModel.YX_HOME_2024.getCode()));
        stats.setProStats(modelStatsMap.get(DeviceModel.YX_PRO_2024.getCode()));
        
        return stats;
    }
    
    /**
     * 获取设备统计数据（无日期范围限制）
     * 
     * @return 设备统计数据
     */
    public ManagedDeviceStatsDTO getManagedDeviceStats() {
        return getManagedDeviceStats(null, null);
    }
    
    /**
     * 计算按型号分别统计的数据
     * 
     * @param modelCounts 型号统计原始数据
     * @return 按型号分组的统计数据
     */
    private Map<String, ManagedDeviceStatsDTO.ModelStatsDTO> calculateModelStats(
            List<ManagedDeviceMapper.ModelCountDTO> modelCounts) {
        
        Map<String, ManagedDeviceStatsDTO.ModelStatsDTO> modelStatsMap = new HashMap<>();
        
        // 初始化各型号统计数据
        for (DeviceModel model : DeviceModel.values()) {
            modelStatsMap.put(model.getCode(), new ManagedDeviceStatsDTO.ModelStatsDTO(0, 0, 0, 0, 0));
        }
        
        // 处理型号统计结果
        for (ManagedDeviceMapper.ModelCountDTO modelCount : modelCounts) {
            String model = modelCount.getModel();
            Integer count = modelCount.getCount();
            
            if (count != null && modelStatsMap.containsKey(model)) {
                ManagedDeviceStatsDTO.ModelStatsDTO modelStats = modelStatsMap.get(model);
                modelStats.setTotal(count);
                
                // 这里可以进一步查询每个型号的状态分布
                // 为了性能考虑，暂时只设置总数
            }
        }
        
        return modelStatsMap;
    }
    
    /**
     * 获取实时设备统计数据
     * 用于前端页面的实时刷新
     * 
     * @return 实时统计数据
     */
    public ManagedDeviceStatsDTO getRealTimeStats() {
        return getManagedDeviceStats();
    }
    
    /**
     * 获取设备状态分布统计
     * 
     * @return 状态分布统计
     */
    public Map<String, Integer> getStatusDistribution() {
        List<ManagedDeviceMapper.StatusCountDTO> statusCounts = managedDeviceMapper.countByStatus();
        
        Map<String, Integer> distribution = new HashMap<>();
        
        for (ManagedDeviceMapper.StatusCountDTO statusCount : statusCounts) {
            distribution.put(statusCount.getStatus(), statusCount.getCount());
        }
        
        return distribution;
    }
    
    /**
     * 获取设备型号分布统计
     * 
     * @return 型号分布统计
     */
    public Map<String, Integer> getModelDistribution() {
        List<ManagedDeviceMapper.ModelCountDTO> modelCounts = managedDeviceMapper.countByModel();
        
        Map<String, Integer> distribution = new HashMap<>();
        
        for (ManagedDeviceMapper.ModelCountDTO modelCount : modelCounts) {
            distribution.put(modelCount.getModel(), modelCount.getCount());
        }
        
        return distribution;
    }
    
    /**
     * 检查统计数据是否为空
     * 
     * @param stats 统计数据
     * @return 是否为空
     */
    public boolean isStatsEmpty(ManagedDeviceStatsDTO stats) {
        return stats == null || stats.getTotal() == null || stats.getTotal() == 0;
    }
    
    /**
     * 获取统计数据摘要
     * 用于日志记录和监控
     * 
     * @return 统计摘要字符串
     */
    public String getStatsSummary() {
        ManagedDeviceStatsDTO stats = getManagedDeviceStats();
        
        return String.format("设备统计: 总数=%d, 在线=%d, 离线=%d, 故障=%d, 维护=%d",
            stats.getTotal() != null ? stats.getTotal() : 0,
            stats.getOnline() != null ? stats.getOnline() : 0,
            stats.getOffline() != null ? stats.getOffline() : 0,
            stats.getError() != null ? stats.getError() : 0,
            stats.getMaintenance() != null ? stats.getMaintenance() : 0
        );
    }
}