package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理大数据量优化服务
 * 处理大数据量分页查询优化和性能提升
 */
@Service
public class ManagedDeviceLargeDataOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceLargeDataOptimizationService.class);
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    // 大数据量阈值配置
    private static final int LARGE_DATA_THRESHOLD = 10000;  // 大数据量阈值
    private static final int MAX_PAGE_SIZE = 100;           // 最大页面大小
    private static final int OPTIMAL_PAGE_SIZE = 20;        // 最优页面大小
    
    /**
     * 优化分页查询参数
     * 
     * @param criteria 搜索条件
     * @return 优化后的搜索条件
     */
    public ManagedDeviceSearchCriteria optimizePaginationCriteria(ManagedDeviceSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new ManagedDeviceSearchCriteria();
        }
        
        // 优化页面大小
        if (criteria.getPageSize() == null || criteria.getPageSize() <= 0) {
            criteria.setPageSize(OPTIMAL_PAGE_SIZE);
        } else if (criteria.getPageSize() > MAX_PAGE_SIZE) {
            criteria.setPageSize(MAX_PAGE_SIZE);
            logger.warn("页面大小超过最大限制，已调整为: {}", MAX_PAGE_SIZE);
        }
        
        // 优化页码
        if (criteria.getPage() == null || criteria.getPage() <= 0) {
            criteria.setPage(1);
        }
        
        // 计算偏移量
        int offset = (criteria.getPage() - 1) * criteria.getPageSize();
        criteria.setOffset(offset);
        
        return criteria;
    }
    
    /**
     * 检查是否为大数据量查询
     * 
     * @param criteria 搜索条件
     * @return 是否为大数据量查询
     */
    public boolean isLargeDataQuery(ManagedDeviceSearchCriteria criteria) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 获取总记录数
            int totalCount = managedDeviceMapper.countAdvancedSearch(criteria);
            
            long queryTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordDatabaseQueryPerformance("countManagedDevices", queryTime);
            
            return totalCount > LARGE_DATA_THRESHOLD;
        } catch (Exception e) {
            logger.error("检查大数据量查询失败", e);
            return false;
        }
    }
    
    /**
     * 优化大数据量查询
     * 
     * @param criteria 搜索条件
     * @return 优化建议
     */
    public Map<String, Object> optimizeLargeDataQuery(ManagedDeviceSearchCriteria criteria) {
        Map<String, Object> optimization = new HashMap<>();
        
        try {
            // 检查是否为大数据量查询
            boolean isLargeData = isLargeDataQuery(criteria);
            optimization.put("isLargeData", isLargeData);
            
            if (isLargeData) {
                // 大数据量优化建议
                optimization.put("suggestions", generateLargeDataOptimizationSuggestions(criteria));
                
                // 优化查询条件
                ManagedDeviceSearchCriteria optimizedCriteria = applyLargeDataOptimizations(criteria);
                optimization.put("optimizedCriteria", optimizedCriteria);
                
                logger.info("应用大数据量查询优化 - 原页面大小: {}, 优化后页面大小: {}", 
                    criteria.getPageSize(), optimizedCriteria.getPageSize());
            } else {
                optimization.put("suggestions", "当前数据量适中，无需特殊优化");
            }
            
        } catch (Exception e) {
            logger.error("大数据量查询优化失败", e);
            optimization.put("error", e.getMessage());
        }
        
        return optimization;
    }
    
    /**
     * 生成大数据量优化建议
     * 
     * @param criteria 搜索条件
     * @return 优化建议列表
     */
    private String[] generateLargeDataOptimizationSuggestions(ManagedDeviceSearchCriteria criteria) {
        return new String[]{
            "建议使用更具体的搜索条件缩小查询范围",
            "建议减小页面大小以提高查询性能",
            "建议使用日期范围筛选减少数据量",
            "建议使用设备状态或型号筛选",
            "系统已自动优化查询参数"
        };
    }
    
    /**
     * 应用大数据量优化
     * 
     * @param criteria 原始搜索条件
     * @return 优化后的搜索条件
     */
    private ManagedDeviceSearchCriteria applyLargeDataOptimizations(ManagedDeviceSearchCriteria criteria) {
        ManagedDeviceSearchCriteria optimized = new ManagedDeviceSearchCriteria();
        
        // 复制原始条件
        optimized.setKeyword(criteria.getKeyword());
        optimized.setStatus(criteria.getStatus());
        optimized.setModel(criteria.getModel());
        optimized.setCustomerId(criteria.getCustomerId());
        optimized.setCreatedStartDate(criteria.getCreatedStartDate());
        optimized.setCreatedEndDate(criteria.getCreatedEndDate());
        optimized.setSortBy(criteria.getSortBy());
        optimized.setSortOrder(criteria.getSortOrder());
        
        // 应用大数据量优化
        optimized.setPage(criteria.getPage());
        
        // 对于大数据量，强制使用较小的页面大小
        int optimizedPageSize = Math.min(criteria.getPageSize() != null ? criteria.getPageSize() : OPTIMAL_PAGE_SIZE, 50);
        optimized.setPageSize(optimizedPageSize);
        
        // 计算偏移量
        int offset = (optimized.getPage() - 1) * optimized.getPageSize();
        optimized.setOffset(offset);
        
        return optimized;
    }
    
    /**
     * 监控分页查询性能
     * 
     * @param criteria 搜索条件
     * @param queryTime 查询时间
     * @param resultCount 结果数量
     */
    public void monitorPaginationPerformance(ManagedDeviceSearchCriteria criteria, long queryTime, int resultCount) {
        try {
            // 记录分页查询性能
            String queryType = String.format("pagination_page_%d_size_%d", criteria.getPage(), criteria.getPageSize());
            performanceMonitorService.recordDatabaseQueryPerformance(queryType, queryTime);
            
            // 记录业务指标
            performanceMonitorService.recordBusinessMetric("pagination_result_count", resultCount);
            performanceMonitorService.recordBusinessMetric("pagination_page_size", criteria.getPageSize());
            
            // 性能警告
            if (queryTime > 2000) {
                logger.warn("分页查询性能警告 - 页码: {}, 页面大小: {}, 查询时间: {}ms, 结果数量: {}", 
                    criteria.getPage(), criteria.getPageSize(), queryTime, resultCount);
            }
            
        } catch (Exception e) {
            logger.error("监控分页查询性能失败", e);
        }
    }
    
    /**
     * 获取分页性能统计
     * 
     * @return 分页性能统计
     */
    public Map<String, Object> getPaginationPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取不同页面大小的平均查询时间
            stats.put("pageSize10AvgTime", performanceMonitorService.getAverageApiResponseTime("pagination_page_1_size_10"));
            stats.put("pageSize20AvgTime", performanceMonitorService.getAverageApiResponseTime("pagination_page_1_size_20"));
            stats.put("pageSize50AvgTime", performanceMonitorService.getAverageApiResponseTime("pagination_page_1_size_50"));
            stats.put("pageSize100AvgTime", performanceMonitorService.getAverageApiResponseTime("pagination_page_1_size_100"));
            
            // 推荐的最优页面大小
            stats.put("recommendedPageSize", OPTIMAL_PAGE_SIZE);
            stats.put("maxPageSize", MAX_PAGE_SIZE);
            stats.put("largeDataThreshold", LARGE_DATA_THRESHOLD);
            
        } catch (Exception e) {
            logger.error("获取分页性能统计失败", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
}