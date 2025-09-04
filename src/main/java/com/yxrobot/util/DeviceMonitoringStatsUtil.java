package com.yxrobot.util;

import com.yxrobot.dto.DeviceMonitoringStatsDTO;
import com.yxrobot.entity.DeviceStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 设备监控统计工具类
 * 提供统计计算的辅助方法
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMonitoringStatsUtil {
    
    /**
     * 计算设备状态百分比
     * 
     * @param count 特定状态的设备数量
     * @param total 设备总数
     * @return 百分比（保留2位小数）
     */
    public static BigDecimal calculatePercentage(Integer count, Integer total) {
        if (total == null || total == 0 || count == null) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算设备健康度评分
     * 基于在线率、故障率等指标综合计算
     * 
     * @param stats 统计数据
     * @return 健康度评分（0-100）
     */
    public static BigDecimal calculateHealthScore(DeviceMonitoringStatsDTO stats) {
        if (stats == null || stats.getTotal() == 0) {
            return BigDecimal.ZERO;
        }
        
        // 在线率权重：50%
        BigDecimal onlineRate = calculatePercentage(stats.getOnline(), stats.getTotal());
        BigDecimal onlineScore = onlineRate.multiply(BigDecimal.valueOf(0.5));
        
        // 故障率权重：30%（故障率越低分数越高）
        BigDecimal errorRate = calculatePercentage(stats.getError(), stats.getTotal());
        BigDecimal errorScore = BigDecimal.valueOf(100).subtract(errorRate).multiply(BigDecimal.valueOf(0.3));
        
        // 平均性能权重：20%
        BigDecimal performanceScore = stats.getAvgPerformance().multiply(BigDecimal.valueOf(0.2));
        
        return onlineScore.add(errorScore).add(performanceScore)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 判断趋势方向
     * 
     * @param trend 趋势值
     * @return 趋势方向（up、down、stable）
     */
    public static String getTrendDirection(Integer trend) {
        if (trend == null || trend == 0) {
            return "stable";
        } else if (trend > 0) {
            return "up";
        } else {
            return "down";
        }
    }
    
    /**
     * 获取趋势描述
     * 
     * @param trend 趋势值
     * @param metricName 指标名称
     * @return 趋势描述
     */
    public static String getTrendDescription(Integer trend, String metricName) {
        if (trend == null || trend == 0) {
            return metricName + "保持稳定";
        } else if (trend > 0) {
            return metricName + "增加了" + trend;
        } else {
            return metricName + "减少了" + Math.abs(trend);
        }
    }
    
    /**
     * 计算设备状态分布
     * 
     * @param stats 统计数据
     * @return 状态分布Map
     */
    public static Map<String, BigDecimal> calculateStatusDistribution(DeviceMonitoringStatsDTO stats) {
        if (stats == null || stats.getTotal() == 0) {
            return Map.of(
                "online", BigDecimal.ZERO,
                "offline", BigDecimal.ZERO,
                "error", BigDecimal.ZERO,
                "maintenance", BigDecimal.ZERO
            );
        }
        
        return Map.of(
            "online", calculatePercentage(stats.getOnline(), stats.getTotal()),
            "offline", calculatePercentage(stats.getOffline(), stats.getTotal()),
            "error", calculatePercentage(stats.getError(), stats.getTotal()),
            "maintenance", calculatePercentage(stats.getMaintenance(), stats.getTotal())
        );
    }
    
    /**
     * 获取设备状态的显示颜色
     * 
     * @param status 设备状态
     * @return 颜色代码
     */
    public static String getStatusColor(DeviceStatus status) {
        if (status == null) {
            return "#gray";
        }
        
        switch (status) {
            case ONLINE:
                return "#52c41a"; // 绿色
            case OFFLINE:
                return "#faad14"; // 橙色
            case ERROR:
                return "#f5222d"; // 红色
            case MAINTENANCE:
                return "#1890ff"; // 蓝色
            default:
                return "#d9d9d9"; // 灰色
        }
    }
    
    /**
     * 获取设备状态的显示文本
     * 
     * @param status 设备状态
     * @return 显示文本
     */
    public static String getStatusText(DeviceStatus status) {
        if (status == null) {
            return "未知";
        }
        
        switch (status) {
            case ONLINE:
                return "在线";
            case OFFLINE:
                return "离线";
            case ERROR:
                return "故障";
            case MAINTENANCE:
                return "维护中";
            default:
                return "未知";
        }
    }
    
    /**
     * 计算统计数据的变化率
     * 
     * @param current 当前值
     * @param previous 历史值
     * @return 变化率百分比
     */
    public static BigDecimal calculateChangeRate(Integer current, Integer previous) {
        if (previous == null || previous == 0 || current == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal change = BigDecimal.valueOf(current - previous);
        return change.divide(BigDecimal.valueOf(previous), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 验证统计数据的一致性
     * 
     * @param stats 统计数据
     * @return 是否一致
     */
    public static boolean validateStatsConsistency(DeviceMonitoringStatsDTO stats) {
        if (stats == null) {
            return false;
        }
        
        // 检查各状态数量之和是否等于总数
        int calculatedTotal = stats.getOnline() + stats.getOffline() + 
                             stats.getError() + stats.getMaintenance();
        
        return calculatedTotal == stats.getTotal();
    }
    
    /**
     * 格式化大数字显示
     * 
     * @param number 数字
     * @return 格式化后的字符串
     */
    public static String formatLargeNumber(Integer number) {
        if (number == null) {
            return "0";
        }
        
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return number.toString();
        }
    }
    
    /**
     * 计算多个统计数据的平均值
     * 
     * @param statsList 统计数据列表
     * @return 平均统计数据
     */
    public static DeviceMonitoringStatsDTO calculateAverage(List<DeviceMonitoringStatsDTO> statsList) {
        if (statsList == null || statsList.isEmpty()) {
            return new DeviceMonitoringStatsDTO();
        }
        
        int size = statsList.size();
        DeviceMonitoringStatsDTO avgStats = new DeviceMonitoringStatsDTO();
        
        // 计算各项指标的平均值
        int totalOnline = statsList.stream().mapToInt(DeviceMonitoringStatsDTO::getOnline).sum();
        int totalOffline = statsList.stream().mapToInt(DeviceMonitoringStatsDTO::getOffline).sum();
        int totalError = statsList.stream().mapToInt(DeviceMonitoringStatsDTO::getError).sum();
        int totalMaintenance = statsList.stream().mapToInt(DeviceMonitoringStatsDTO::getMaintenance).sum();
        
        avgStats.setOnline(totalOnline / size);
        avgStats.setOffline(totalOffline / size);
        avgStats.setError(totalError / size);
        avgStats.setMaintenance(totalMaintenance / size);
        avgStats.setTotal(avgStats.getOnline() + avgStats.getOffline() + 
                         avgStats.getError() + avgStats.getMaintenance());
        
        // 计算平均性能
        BigDecimal totalPerformance = statsList.stream()
                .map(DeviceMonitoringStatsDTO::getAvgPerformance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        avgStats.setAvgPerformance(totalPerformance.divide(BigDecimal.valueOf(size), 2, RoundingMode.HALF_UP));
        
        return avgStats;
    }
}