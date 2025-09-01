package com.yxrobot.service;

import com.yxrobot.dto.RentalTrendDTO;
import com.yxrobot.dto.ChartDataDTO;
import com.yxrobot.mapper.RentalRecordMapper;
import com.yxrobot.mapper.RentalDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 租赁分析服务类
 * 处理图表数据业务逻辑，支持前端图表功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Service
public class RentalAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalAnalysisService.class);
    
    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    
    @Autowired
    private RentalDeviceMapper rentalDeviceMapper;
    
    /**
     * 获取租赁趋势图表数据
     * 支持前端租赁趋势分析图表（收入+订单数+利用率）
     * 
     * @param period 时间周期（daily、weekly、monthly、quarterly）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return ECharts格式的图表数据
     */
    public Map<String, Object> getTrendChartData(String period, LocalDate startDate, LocalDate endDate) {
        logger.info("开始获取租赁趋势图表数据，周期：{}, 时间范围：{} 到 {}", period, startDate, endDate);
        
        try {
            // 设置默认时间范围
            if (startDate == null || endDate == null) {
                endDate = LocalDate.now();
                switch (period) {
                    case "daily":
                        startDate = endDate.minusDays(6); // 最近7天
                        break;
                    case "weekly":
                        startDate = endDate.minusWeeks(3); // 最近4周
                        break;
                    case "monthly":
                        startDate = endDate.minusMonths(11); // 最近12月
                        break;
                    case "quarterly":
                        startDate = endDate.minusMonths(11); // 最近4季度
                        break;
                    default:
                        startDate = endDate.minusDays(29); // 默认最近30天
                        break;
                }
            }
            
            // 查询趋势数据
            List<RentalTrendDTO> trendData = rentalRecordMapper.selectRentalTrends(startDate, endDate, period);
            
            // 构建ECharts格式数据
            Map<String, Object> chartData = new HashMap<>();
            
            if (trendData != null && !trendData.isEmpty()) {
                // 提取categories（X轴数据）
                List<String> categories = trendData.stream()
                    .map(RentalTrendDTO::getDate)
                    .toList();
                
                // 提取series数据
                List<Map<String, Object>> series = List.of(
                    Map.of(
                        "name", "租赁收入",
                        "data", trendData.stream().map(RentalTrendDTO::getRevenue).toList()
                    ),
                    Map.of(
                        "name", "订单数量",
                        "data", trendData.stream().map(RentalTrendDTO::getOrderCount).toList()
                    ),
                    Map.of(
                        "name", "设备利用率",
                        "data", trendData.stream().map(RentalTrendDTO::getUtilizationRate).toList()
                    )
                );
                
                chartData.put("categories", categories);
                chartData.put("series", series);
            } else {
                // 空数据处理
                chartData.put("categories", List.of());
                chartData.put("series", List.of());
            }
            
            logger.info("租赁趋势图表数据获取成功，数据点数量：{}", 
                       trendData != null ? trendData.size() : 0);
            
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取租赁趋势图表数据失败", e);
            
            // 返回空图表数据
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("categories", List.of());
            emptyData.put("series", List.of());
            return emptyData;
        }
    }
    
    /**
     * 获取分布图表数据
     * 支持地区分布、设备型号分析等图表
     * 
     * @param type 分布类型（region、device-model、utilization-ranking）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return ECharts格式的图表数据
     */
    public Map<String, Object> getDistributionData(String type, LocalDate startDate, LocalDate endDate) {
        logger.info("开始获取分布图表数据，类型：{}, 时间范围：{} 到 {}", type, startDate, endDate);
        
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            switch (type) {
                case "region":
                    chartData = getRegionDistributionData(startDate, endDate);
                    break;
                case "device-model":
                    chartData = getDeviceModelDistributionData();
                    break;
                case "utilization-ranking":
                    chartData = getUtilizationRankingData();
                    break;
                default:
                    logger.warn("未知的分布类型：{}", type);
                    chartData.put("categories", List.of());
                    chartData.put("series", List.of());
                    break;
            }
            
            logger.info("分布图表数据获取成功，类型：{}", type);
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取分布图表数据失败，类型：{}", type, e);
            
            // 返回空图表数据
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("categories", List.of());
            emptyData.put("series", List.of());
            return emptyData;
        }
    }
    
    /**
     * 获取地区分布图表数据
     */
    private Map<String, Object> getRegionDistributionData(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> regionData = rentalRecordMapper.selectRegionDistribution(startDate, endDate);
        
        Map<String, Object> chartData = new HashMap<>();
        
        if (regionData != null && !regionData.isEmpty()) {
            List<String> categories = regionData.stream()
                .map(item -> (String) item.get("region"))
                .toList();
            
            List<Map<String, Object>> series = List.of(
                Map.of(
                    "name", "租赁收入",
                    "data", regionData.stream().map(item -> item.get("revenue")).toList()
                )
            );
            
            chartData.put("categories", categories);
            chartData.put("series", series);
        } else {
            chartData.put("categories", List.of());
            chartData.put("series", List.of());
        }
        
        return chartData;
    }
    
    /**
     * 获取设备型号分布图表数据
     */
    private Map<String, Object> getDeviceModelDistributionData() {
        List<Map<String, Object>> modelData = rentalDeviceMapper.selectDeviceModelDistribution();
        
        Map<String, Object> chartData = new HashMap<>();
        
        if (modelData != null && !modelData.isEmpty()) {
            List<String> categories = modelData.stream()
                .map(item -> (String) item.get("name"))
                .toList();
            
            List<Map<String, Object>> series = List.of(
                Map.of(
                    "name", "设备数量",
                    "data", modelData.stream().map(item -> item.get("value")).toList()
                ),
                Map.of(
                    "name", "平均利用率",
                    "data", modelData.stream().map(item -> item.get("avgUtilization")).toList()
                )
            );
            
            chartData.put("categories", categories);
            chartData.put("series", series);
        } else {
            chartData.put("categories", List.of());
            chartData.put("series", List.of());
        }
        
        return chartData;
    }
    
    /**
     * 获取设备利用率排行图表数据
     */
    private Map<String, Object> getUtilizationRankingData() {
        List<Map<String, Object>> rankingData = rentalDeviceMapper.selectUtilizationRanking(12);
        
        Map<String, Object> chartData = new HashMap<>();
        
        if (rankingData != null && !rankingData.isEmpty()) {
            List<String> categories = rankingData.stream()
                .map(item -> (String) item.get("name"))
                .toList();
            
            List<Map<String, Object>> series = List.of(
                Map.of(
                    "name", "利用率",
                    "data", rankingData.stream().map(item -> item.get("value")).toList()
                )
            );
            
            chartData.put("categories", categories);
            chartData.put("series", series);
        } else {
            chartData.put("categories", List.of());
            chartData.put("series", List.of());
        }
        
        return chartData;
    }
    
    /**
     * 获取设备利用率排行图表数据（公共方法）
     * 支持前端设备利用率排行图表
     * 
     * @param limit 返回数据条数限制
     * @return ECharts格式的图表数据
     */
    public Map<String, Object> getUtilizationRankingData(Integer limit) {
        logger.info("开始获取设备利用率排行图表数据，限制条数：{}", limit);
        
        try {
            if (limit == null || limit <= 0) {
                limit = 12; // 默认返回12条数据
            }
            
            List<Map<String, Object>> rankingData = rentalDeviceMapper.selectUtilizationRanking(limit);
            
            Map<String, Object> chartData = new HashMap<>();
            
            if (rankingData != null && !rankingData.isEmpty()) {
                List<String> categories = rankingData.stream()
                    .map(item -> (String) item.get("name"))
                    .toList();
                
                List<Map<String, Object>> series = List.of(
                    Map.of(
                        "name", "利用率",
                        "data", rankingData.stream().map(item -> item.get("value")).toList()
                    )
                );
                
                chartData.put("categories", categories);
                chartData.put("series", series);
            } else {
                chartData.put("categories", List.of());
                chartData.put("series", List.of());
            }
            
            logger.info("设备利用率排行图表数据获取成功，数据条数：{}", 
                       rankingData != null ? rankingData.size() : 0);
            
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取设备利用率排行图表数据失败", e);
            
            // 返回空图表数据
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("categories", List.of());
            emptyData.put("series", List.of());
            return emptyData;
        }
    }
    
    /**
     * 获取所有图表数据
     * 支持前端一次性加载所有图表数据
     * 
     * @param period 时间周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含所有图表数据的Map
     */
    public Map<String, Object> getAllChartsData(String period, LocalDate startDate, LocalDate endDate) {
        logger.info("开始获取所有图表数据，周期：{}, 时间范围：{} 到 {}", period, startDate, endDate);
        
        Map<String, Object> allChartsData = new HashMap<>();
        
        try {
            // 获取租赁趋势数据
            Map<String, Object> trendData = getTrendChartData(period, startDate, endDate);
            allChartsData.put("trendChart", trendData);
            
            // 获取地区分布数据
            Map<String, Object> regionData = getDistributionData("region", startDate, endDate);
            allChartsData.put("regionChart", regionData);
            
            // 获取设备型号分布数据
            Map<String, Object> deviceModelData = getDistributionData("device-model", startDate, endDate);
            allChartsData.put("deviceModelChart", deviceModelData);
            
            // 获取设备利用率排行数据
            Map<String, Object> utilizationRankingData = getUtilizationRankingData(12);
            allChartsData.put("utilizationRankingChart", utilizationRankingData);
            
            logger.info("所有图表数据获取成功");
            
        } catch (Exception e) {
            logger.error("获取所有图表数据失败", e);
            
            // 返回空数据结构
            Map<String, Object> emptyChartData = new HashMap<>();
            emptyChartData.put("categories", List.of());
            emptyChartData.put("series", List.of());
            
            allChartsData.put("trendChart", emptyChartData);
            allChartsData.put("regionChart", emptyChartData);
            allChartsData.put("deviceModelChart", emptyChartData);
            allChartsData.put("utilizationRankingChart", emptyChartData);
        }
        
        return allChartsData;
    }
    
    /**
     * 获取租赁趋势图表数据（使用ChartDataDTO）
     * 
     * @param period 时间周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return ChartDataDTO格式的图表数据
     */
    public ChartDataDTO getTrendChartDataDTO(String period, LocalDate startDate, LocalDate endDate) {
        logger.info("开始获取租赁趋势图表数据（DTO格式），周期：{}, 时间范围：{} 到 {}", period, startDate, endDate);
        
        try {
            // 设置默认时间范围
            if (startDate == null || endDate == null) {
                endDate = LocalDate.now();
                switch (period) {
                    case "daily":
                        startDate = endDate.minusDays(6);
                        break;
                    case "weekly":
                        startDate = endDate.minusWeeks(3);
                        break;
                    case "monthly":
                        startDate = endDate.minusMonths(11);
                        break;
                    case "quarterly":
                        startDate = endDate.minusMonths(11);
                        break;
                    default:
                        startDate = endDate.minusDays(29);
                        break;
                }
            }
            
            // 查询趋势数据
            List<RentalTrendDTO> trendData = rentalRecordMapper.selectRentalTrends(startDate, endDate, period);
            
            ChartDataDTO chartData = new ChartDataDTO();
            chartData.setTitle("租赁趋势分析");
            chartData.setSubtitle("租赁收入和订单数量趋势");
            
            if (trendData != null && !trendData.isEmpty()) {
                // 设置categories（X轴数据）
                List<String> categories = trendData.stream()
                    .map(RentalTrendDTO::getDate)
                    .toList();
                chartData.setCategories(categories);
                
                // 设置series数据
                List<ChartDataDTO.SeriesData> series = new ArrayList<>();
                
                // 租赁收入系列
                ChartDataDTO.SeriesData revenueSeries = new ChartDataDTO.SeriesData();
                revenueSeries.setName("租赁收入");
                revenueSeries.setType("line");
                revenueSeries.setYAxisIndex(0);
                revenueSeries.setData(trendData.stream().map(item -> (Object) item.getRevenue()).toList());
                series.add(revenueSeries);
                
                // 订单数量系列
                ChartDataDTO.SeriesData orderSeries = new ChartDataDTO.SeriesData();
                orderSeries.setName("订单数量");
                orderSeries.setType("bar");
                orderSeries.setYAxisIndex(1);
                orderSeries.setData(trendData.stream().map(item -> (Object) item.getOrderCount()).toList());
                series.add(orderSeries);
                
                // 设备利用率系列
                ChartDataDTO.SeriesData utilizationSeries = new ChartDataDTO.SeriesData();
                utilizationSeries.setName("设备利用率");
                utilizationSeries.setType("line");
                utilizationSeries.setYAxisIndex(1);
                utilizationSeries.setData(trendData.stream().map(item -> (Object) item.getUtilizationRate()).toList());
                series.add(utilizationSeries);
                
                chartData.setSeries(series);
            } else {
                // 空数据处理
                chartData.setCategories(List.of());
                chartData.setSeries(List.of());
            }
            
            logger.info("租赁趋势图表数据（DTO格式）获取成功，数据点数量：{}", 
                       trendData != null ? trendData.size() : 0);
            
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取租赁趋势图表数据（DTO格式）失败", e);
            
            // 返回空图表数据
            ChartDataDTO emptyData = new ChartDataDTO();
            emptyData.setCategories(List.of());
            emptyData.setSeries(List.of());
            return emptyData;
        }
    }
}