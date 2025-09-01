package com.yxrobot.service;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.DeviceStatusStatsDTO;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.mapper.RentalDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备利用率管理服务类
 * 处理设备利用率业务逻辑，支持前端表格功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Service
public class DeviceUtilizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceUtilizationService.class);
    
    @Autowired
    private RentalDeviceMapper rentalDeviceMapper;
    
    /**
     * 获取设备利用率数据列表（使用参数Map）
     * 支持前端表格的分页、搜索、筛选功能
     * 
     * @param params 查询参数Map
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationData(Map<String, Object> params) {
        logger.info("开始获取设备利用率数据列表，参数：{}", params);
        
        try {
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationList(params);
            
            logger.info("设备利用率数据列表获取成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("获取设备利用率数据列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取设备利用率数据总数
     * 支持前端表格的分页功能
     * 
     * @param params 查询参数Map
     * @return 数据总数
     */
    public Long getDeviceUtilizationCount(Map<String, Object> params) {
        logger.info("开始获取设备利用率数据总数，参数：{}", params);
        
        try {
            Long count = rentalDeviceMapper.selectDeviceUtilizationCount(params);
            
            logger.info("设备利用率数据总数获取成功：{}", count);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            logger.error("获取设备利用率数据总数失败", e);
            return 0L;
        }
    }
    
    /**
     * 获取设备利用率数据列表
     * 支持前端表格的分页、搜索、筛选功能
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param deviceModel 设备型号筛选
     * @param currentStatus 状态筛选
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    public Map<String, Object> getDeviceUtilizationData(Integer page, Integer pageSize, 
                                                        String deviceModel, String currentStatus, 
                                                        String keyword) {
        logger.info("开始获取设备利用率数据，页码：{}, 每页大小：{}, 设备型号：{}, 状态：{}, 关键词：{}", 
                   page, pageSize, deviceModel, currentStatus, keyword);
        
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            
            // 分页参数
            if (page != null && pageSize != null) {
                params.put("page", page);
                params.put("pageSize", pageSize);
                params.put("offset", (page - 1) * pageSize);
            }
            
            // 筛选参数
            if (deviceModel != null && !deviceModel.trim().isEmpty()) {
                params.put("deviceModel", deviceModel.trim());
            }
            
            if (currentStatus != null && !currentStatus.trim().isEmpty()) {
                params.put("currentStatus", currentStatus.trim());
            }
            
            // 搜索参数
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            // 查询设备利用率数据
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationList(params);
            
            // 查询总数
            Long totalCount = rentalDeviceMapper.selectDeviceUtilizationCount(params);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", deviceList);
            result.put("total", totalCount);
            result.put("page", page != null ? page : 1);
            result.put("pageSize", pageSize != null ? pageSize : 20);
            result.put("isEmpty", deviceList == null || deviceList.isEmpty());
            
            logger.info("设备利用率数据获取成功，总数：{}, 当前页数据量：{}", totalCount, 
                       deviceList != null ? deviceList.size() : 0);
            
            return result;
            
        } catch (Exception e) {
            logger.error("获取设备利用率数据失败", e);
            
            // 返回空结果，避免前端报错
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("list", List.of());
            emptyResult.put("total", 0L);
            emptyResult.put("page", page != null ? page : 1);
            emptyResult.put("pageSize", pageSize != null ? pageSize : 20);
            emptyResult.put("isEmpty", true);
            
            return emptyResult;
        }
    }
    
    /**
     * 获取设备状态统计数据
     * 支持前端右侧面板的设备状态分布功能
     * 
     * @return 设备状态统计Map
     */
    public Map<String, Object> getDeviceStatusStats() {
        logger.info("开始获取设备状态统计数据");
        
        try {
            Map<String, Object> statusData = rentalDeviceMapper.selectDeviceStatusStats();
            
            if (statusData != null) {
                logger.info("设备状态统计数据获取成功：运行中={}, 空闲={}, 维护中={}", 
                           statusData.get("active"), statusData.get("idle"), statusData.get("maintenance"));
                return statusData;
            } else {
                // 返回默认值，避免前端报错
                Map<String, Object> defaultStats = new HashMap<>();
                defaultStats.put("active", 0);
                defaultStats.put("idle", 0);
                defaultStats.put("maintenance", 0);
                return defaultStats;
            }
            
        } catch (Exception e) {
            logger.error("获取设备状态统计数据失败", e);
            // 返回默认值，避免前端报错
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("active", 0);
            defaultStats.put("idle", 0);
            defaultStats.put("maintenance", 0);
            return defaultStats;
        }
    }
    
    /**
     * 获取设备状态统计数据DTO
     * 支持前端右侧面板的设备状态分布功能
     * 
     * @return 设备状态统计DTO
     */
    public DeviceStatusStatsDTO getDeviceStatusStatsDTO() {
        logger.info("开始获取设备状态统计数据DTO");
        
        try {
            Map<String, Object> statusData = rentalDeviceMapper.selectDeviceStatusStats();
            
            DeviceStatusStatsDTO statusStats = new DeviceStatusStatsDTO();
            
            if (statusData != null) {
                statusStats.setActive(getIntegerValue(statusData, "active"));
                statusStats.setIdle(getIntegerValue(statusData, "idle"));
                statusStats.setMaintenance(getIntegerValue(statusData, "maintenance"));
            }
            
            logger.info("设备状态统计数据DTO获取成功：运行中={}, 空闲={}, 维护中={}", 
                       statusStats.getActive(), statusStats.getIdle(), statusStats.getMaintenance());
            
            return statusStats;
            
        } catch (Exception e) {
            logger.error("获取设备状态统计数据DTO失败", e);
            // 返回默认值，避免前端报错
            return new DeviceStatusStatsDTO();
        }
    }
    
    /**
     * 获取利用率TOP设备列表
     * 支持前端右侧面板的利用率TOP5功能
     * 
     * @param limit 返回数量限制
     * @return TOP设备列表
     */
    public List<DeviceUtilizationDTO> getTopDevicesByUtilization(Integer limit) {
        logger.info("开始获取利用率TOP设备列表，限制数量：{}", limit);
        
        try {
            if (limit == null || limit <= 0) {
                limit = 5; // 默认TOP5
            }
            
            List<DeviceUtilizationDTO> topDevices = rentalDeviceMapper.selectTopDevicesByUtilization(limit);
            
            logger.info("利用率TOP设备列表获取成功，数量：{}", 
                       topDevices != null ? topDevices.size() : 0);
            
            return topDevices != null ? topDevices : List.of();
            
        } catch (Exception e) {
            logger.error("获取利用率TOP设备列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取设备型号分布数据
     * 支持前端图表分析功能
     * 
     * @return 设备型号分布数据
     */
    public List<Map<String, Object>> getDeviceModelDistribution() {
        logger.info("开始获取设备型号分布数据");
        
        try {
            List<Map<String, Object>> distribution = rentalDeviceMapper.selectDeviceModelDistribution();
            
            logger.info("设备型号分布数据获取成功，型号数量：{}", 
                       distribution != null ? distribution.size() : 0);
            
            return distribution != null ? distribution : List.of();
            
        } catch (Exception e) {
            logger.error("获取设备型号分布数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取设备利用率排行数据
     * 支持前端图表分析功能
     * 
     * @param limit 返回数量限制
     * @return 利用率排行数据
     */
    public List<Map<String, Object>> getUtilizationRanking(Integer limit) {
        logger.info("开始获取设备利用率排行数据，限制数量：{}", limit);
        
        try {
            if (limit == null || limit <= 0) {
                limit = 12; // 默认TOP12
            }
            
            List<Map<String, Object>> ranking = rentalDeviceMapper.selectUtilizationRanking(limit);
            
            logger.info("设备利用率排行数据获取成功，数量：{}", 
                       ranking != null ? ranking.size() : 0);
            
            return ranking != null ? ranking : List.of();
            
        } catch (Exception e) {
            logger.error("获取设备利用率排行数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 根据设备ID获取设备详情
     * 支持前端设备详情弹窗功能
     * 
     * @param deviceId 设备ID
     * @return 设备详情
     */
    public RentalDevice getDeviceById(Long deviceId) {
        logger.info("开始获取设备详情，设备ID：{}", deviceId);
        
        try {
            if (deviceId == null) {
                logger.warn("设备ID为空，无法查询设备详情");
                return null;
            }
            
            RentalDevice device = rentalDeviceMapper.selectById(deviceId);
            
            if (device != null) {
                logger.info("设备详情获取成功：设备编号={}, 型号={}, 利用率={}%", 
                           device.getDeviceId(), device.getDeviceModel(), device.getUtilizationRate());
            } else {
                logger.warn("未找到设备详情，设备ID：{}", deviceId);
            }
            
            return device;
            
        } catch (Exception e) {
            logger.error("获取设备详情失败，设备ID：{}", deviceId, e);
            return null;
        }
    }
    
    /**
     * 根据设备编号获取设备详情
     * 支持前端设备详情弹窗功能
     * 
     * @param deviceId 设备编号
     * @return 设备详情
     */
    public RentalDevice getDeviceByDeviceId(String deviceId) {
        logger.info("开始获取设备详情，设备编号：{}", deviceId);
        
        try {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                logger.warn("设备编号为空，无法查询设备详情");
                return null;
            }
            
            RentalDevice device = rentalDeviceMapper.selectByDeviceId(deviceId.trim());
            
            if (device != null) {
                logger.info("设备详情获取成功：设备编号={}, 型号={}, 利用率={}%", 
                           device.getDeviceId(), device.getDeviceModel(), device.getUtilizationRate());
            } else {
                logger.warn("未找到设备详情，设备编号：{}", deviceId);
            }
            
            return device;
            
        } catch (Exception e) {
            logger.error("获取设备详情失败，设备编号：{}", deviceId, e);
            return null;
        }
    }
    
    /**
     * 获取所有设备型号列表
     * 支持前端筛选功能
     * 
     * @return 设备型号列表
     */
    public List<String> getAllDeviceModels() {
        logger.info("开始获取所有设备型号列表");
        
        try {
            List<String> models = rentalDeviceMapper.selectAllDeviceModels();
            
            logger.info("设备型号列表获取成功，数量：{}", 
                       models != null ? models.size() : 0);
            
            return models != null ? models : List.of();
            
        } catch (Exception e) {
            logger.error("获取设备型号列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取所有地区列表
     * 支持前端筛选功能
     * 
     * @return 地区列表
     */
    public List<String> getAllRegions() {
        logger.info("开始获取所有地区列表");
        
        try {
            List<String> regions = rentalDeviceMapper.selectAllRegions();
            
            logger.info("地区列表获取成功，数量：{}", 
                       regions != null ? regions.size() : 0);
            
            return regions != null ? regions : List.of();
            
        } catch (Exception e) {
            logger.error("获取地区列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 更新设备状态
     * 支持批量操作功能
     * 
     * @param deviceId 设备编号
     * @param newStatus 新状态
     * @return 更新结果
     */
    public boolean updateDeviceStatus(String deviceId, String newStatus) {
        logger.info("开始更新设备状态，设备编号：{}，新状态：{}", deviceId, newStatus);
        
        try {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                logger.warn("设备编号为空，无法更新状态");
                return false;
            }
            
            if (newStatus == null || newStatus.trim().isEmpty()) {
                logger.warn("新状态为空，无法更新");
                return false;
            }
            
            int result = rentalDeviceMapper.updateDeviceStatus(deviceId.trim(), newStatus.trim());
            
            if (result > 0) {
                logger.info("设备状态更新成功，设备编号：{}，新状态：{}", deviceId, newStatus);
                return true;
            } else {
                logger.warn("设备状态更新失败，设备编号：{}", deviceId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新设备状态失败，设备编号：{}", deviceId, e);
            return false;
        }
    }
    
    /**
     * 更新设备维护状态
     * 支持批量操作功能
     * 
     * @param deviceId 设备编号
     * @param maintenanceStatus 维护状态
     * @return 更新结果
     */
    public boolean updateMaintenanceStatus(String deviceId, String maintenanceStatus) {
        logger.info("开始更新设备维护状态，设备编号：{}，维护状态：{}", deviceId, maintenanceStatus);
        
        try {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                logger.warn("设备编号为空，无法更新维护状态");
                return false;
            }
            
            if (maintenanceStatus == null || maintenanceStatus.trim().isEmpty()) {
                logger.warn("维护状态为空，无法更新");
                return false;
            }
            
            int result = rentalDeviceMapper.updateMaintenanceStatus(deviceId.trim(), maintenanceStatus.trim());
            
            if (result > 0) {
                logger.info("设备维护状态更新成功，设备编号：{}，维护状态：{}", deviceId, maintenanceStatus);
                return true;
            } else {
                logger.warn("设备维护状态更新失败，设备编号：{}", deviceId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新设备维护状态失败，设备编号：{}", deviceId, e);
            return false;
        }
    }
    
    /**
     * 软删除设备
     * 支持批量操作功能
     * 
     * @param deviceId 设备编号
     * @return 删除结果
     */
    public boolean softDeleteDevice(String deviceId) {
        logger.info("开始软删除设备，设备编号：{}", deviceId);
        
        try {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                logger.warn("设备编号为空，无法删除");
                return false;
            }
            
            int result = rentalDeviceMapper.softDeleteByDeviceId(deviceId.trim());
            
            if (result > 0) {
                logger.info("设备软删除成功，设备编号：{}", deviceId);
                return true;
            } else {
                logger.warn("设备软删除失败，设备编号：{}", deviceId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("软删除设备失败，设备编号：{}", deviceId, e);
            return false;
        }
    }
    
    /**
     * 批量更新设备状态
     * 
     * @param deviceIds 设备编号列表
     * @param newStatus 新状态
     * @return 更新成功的数量
     */
    public int batchUpdateDeviceStatus(List<String> deviceIds, String newStatus) {
        logger.info("开始批量更新设备状态，设备数量：{}，新状态：{}", deviceIds.size(), newStatus);
        
        int successCount = 0;
        
        for (String deviceId : deviceIds) {
            if (updateDeviceStatus(deviceId, newStatus)) {
                successCount++;
            }
        }
        
        logger.info("批量更新设备状态完成，成功数量：{}", successCount);
        return successCount;
    }
    
    /**
     * 批量更新设备维护状态
     * 
     * @param deviceIds 设备编号列表
     * @param maintenanceStatus 维护状态
     * @return 更新成功的数量
     */
    public int batchUpdateMaintenanceStatus(List<String> deviceIds, String maintenanceStatus) {
        logger.info("开始批量更新设备维护状态，设备数量：{}，维护状态：{}", deviceIds.size(), maintenanceStatus);
        
        int successCount = 0;
        
        for (String deviceId : deviceIds) {
            if (updateMaintenanceStatus(deviceId, maintenanceStatus)) {
                successCount++;
            }
        }
        
        logger.info("批量更新设备维护状态完成，成功数量：{}", successCount);
        return successCount;
    }
    
    /**
     * 批量软删除设备
     * 
     * @param deviceIds 设备编号列表
     * @return 删除成功的数量
     */
    public int batchSoftDeleteDevices(List<String> deviceIds) {
        logger.info("开始批量软删除设备，设备数量：{}", deviceIds.size());
        
        int successCount = 0;
        
        for (String deviceId : deviceIds) {
            if (softDeleteDevice(deviceId)) {
                successCount++;
            }
        }
        
        logger.info("批量软删除设备完成，成功数量：{}", successCount);
        return successCount;
    }
    
    /**
     * 高级搜索设备利用率数据
     * 支持多条件搜索和筛选
     * 
     * @param params 搜索参数
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationWithAdvancedSearch(Map<String, Object> params) {
        logger.info("开始高级搜索设备利用率数据，参数：{}", params);
        
        try {
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationWithAdvancedSearch(params);
            
            logger.info("高级搜索设备利用率数据成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("高级搜索设备利用率数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取高级搜索设备利用率数据总数
     * 
     * @param params 搜索参数
     * @return 数据总数
     */
    public Long getDeviceUtilizationCountWithAdvancedSearch(Map<String, Object> params) {
        logger.info("开始获取高级搜索设备利用率数据总数，参数：{}", params);
        
        try {
            Long count = rentalDeviceMapper.selectDeviceUtilizationCountWithAdvancedSearch(params);
            
            logger.info("高级搜索设备利用率数据总数获取成功：{}", count);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            logger.error("获取高级搜索设备利用率数据总数失败", e);
            return 0L;
        }
    }
    
    /**
     * 按时间范围搜索设备利用率数据
     * 
     * @param params 搜索参数（包含startDate、endDate等）
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationByDateRange(Map<String, Object> params) {
        logger.info("开始按时间范围搜索设备利用率数据，参数：{}", params);
        
        try {
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationByDateRange(params);
            
            logger.info("按时间范围搜索设备利用率数据成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("按时间范围搜索设备利用率数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 按多个设备型号筛选设备利用率数据
     * 
     * @param deviceModels 设备型号列表
     * @param params 其他搜索参数
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationByModels(List<String> deviceModels, Map<String, Object> params) {
        logger.info("开始按设备型号筛选设备利用率数据，型号：{}，参数：{}", deviceModels, params);
        
        try {
            if (deviceModels == null || deviceModels.isEmpty()) {
                return List.of();
            }
            
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationByModels(deviceModels, params);
            
            logger.info("按设备型号筛选设备利用率数据成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("按设备型号筛选设备利用率数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 按多个状态筛选设备利用率数据
     * 
     * @param statuses 状态列表
     * @param params 其他搜索参数
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationByStatuses(List<String> statuses, Map<String, Object> params) {
        logger.info("开始按状态筛选设备利用率数据，状态：{}，参数：{}", statuses, params);
        
        try {
            if (statuses == null || statuses.isEmpty()) {
                return List.of();
            }
            
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationByStatuses(statuses, params);
            
            logger.info("按状态筛选设备利用率数据成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("按状态筛选设备利用率数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 按利用率范围筛选设备利用率数据
     * 
     * @param minUtilization 最小利用率
     * @param maxUtilization 最大利用率
     * @param params 其他搜索参数
     * @return 设备利用率数据列表
     */
    public List<DeviceUtilizationDTO> getDeviceUtilizationByUtilizationRange(Double minUtilization, Double maxUtilization, Map<String, Object> params) {
        logger.info("开始按利用率范围筛选设备利用率数据，范围：{}-{}，参数：{}", minUtilization, maxUtilization, params);
        
        try {
            List<DeviceUtilizationDTO> deviceList = rentalDeviceMapper.selectDeviceUtilizationByUtilizationRange(minUtilization, maxUtilization, params);
            
            logger.info("按利用率范围筛选设备利用率数据成功，数量：{}", 
                       deviceList != null ? deviceList.size() : 0);
            
            return deviceList != null ? deviceList : List.of();
            
        } catch (Exception e) {
            logger.error("按利用率范围筛选设备利用率数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取搜索建议（自动完成）
     * 
     * @param keyword 搜索关键词
     * @param type 搜索类型（deviceId、deviceModel、region等）
     * @return 搜索建议列表
     */
    public List<String> getSearchSuggestions(String keyword, String type) {
        logger.info("开始获取搜索建议，关键词：{}，类型：{}", keyword, type);
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return List.of();
            }
            
            List<String> suggestions = rentalDeviceMapper.selectSearchSuggestions(keyword.trim(), type);
            
            logger.info("搜索建议获取成功，数量：{}", 
                       suggestions != null ? suggestions.size() : 0);
            
            return suggestions != null ? suggestions : List.of();
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取筛选选项统计
     * 返回各个筛选维度的选项及其对应的数量
     * 
     * @return 筛选选项统计数据
     */
    public Map<String, Object> getFilterOptionsStats() {
        logger.info("开始获取筛选选项统计");
        
        try {
            Map<String, Object> stats = rentalDeviceMapper.selectFilterOptionsStats();
            
            logger.info("筛选选项统计获取成功");
            
            return stats != null ? stats : new HashMap<>();
            
        } catch (Exception e) {
            logger.error("获取筛选选项统计失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 构建高级搜索参数
     * 
     * @param searchRequest 搜索请求参数
     * @return 构建后的参数Map
     */
    public Map<String, Object> buildAdvancedSearchParams(Map<String, Object> searchRequest) {
        Map<String, Object> params = new HashMap<>();
        
        // 基础分页参数
        if (searchRequest.containsKey("page")) {
            Integer page = (Integer) searchRequest.get("page");
            Integer pageSize = (Integer) searchRequest.getOrDefault("pageSize", 20);
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
        }
        
        // 搜索关键词
        if (searchRequest.containsKey("keyword")) {
            params.put("keyword", searchRequest.get("keyword"));
        }
        
        // 设备型号筛选（支持单个和多个）
        if (searchRequest.containsKey("deviceModels")) {
            params.put("deviceModels", searchRequest.get("deviceModels"));
        } else if (searchRequest.containsKey("deviceModel")) {
            params.put("deviceModel", searchRequest.get("deviceModel"));
        }
        
        // 状态筛选（支持单个和多个）
        if (searchRequest.containsKey("statuses")) {
            params.put("statuses", searchRequest.get("statuses"));
        } else if (searchRequest.containsKey("currentStatus")) {
            params.put("currentStatus", searchRequest.get("currentStatus"));
        }
        
        // 地区筛选（支持单个和多个）
        if (searchRequest.containsKey("regions")) {
            params.put("regions", searchRequest.get("regions"));
        } else if (searchRequest.containsKey("region")) {
            params.put("region", searchRequest.get("region"));
        }
        
        // 利用率范围
        if (searchRequest.containsKey("minUtilization")) {
            params.put("minUtilization", searchRequest.get("minUtilization"));
        }
        if (searchRequest.containsKey("maxUtilization")) {
            params.put("maxUtilization", searchRequest.get("maxUtilization"));
        }
        
        // 租赁天数范围
        if (searchRequest.containsKey("minRentalDays")) {
            params.put("minRentalDays", searchRequest.get("minRentalDays"));
        }
        if (searchRequest.containsKey("maxRentalDays")) {
            params.put("maxRentalDays", searchRequest.get("maxRentalDays"));
        }
        
        // 时间范围
        if (searchRequest.containsKey("startDate")) {
            params.put("startDate", searchRequest.get("startDate"));
        }
        if (searchRequest.containsKey("endDate")) {
            params.put("endDate", searchRequest.get("endDate"));
        }
        
        // 维护状态
        if (searchRequest.containsKey("maintenanceStatus")) {
            params.put("maintenanceStatus", searchRequest.get("maintenanceStatus"));
        }
        
        // 性能评分范围
        if (searchRequest.containsKey("minPerformanceScore")) {
            params.put("minPerformanceScore", searchRequest.get("minPerformanceScore"));
        }
        if (searchRequest.containsKey("maxPerformanceScore")) {
            params.put("maxPerformanceScore", searchRequest.get("maxPerformanceScore"));
        }
        
        // 信号强度范围
        if (searchRequest.containsKey("minSignalStrength")) {
            params.put("minSignalStrength", searchRequest.get("minSignalStrength"));
        }
        if (searchRequest.containsKey("maxSignalStrength")) {
            params.put("maxSignalStrength", searchRequest.get("maxSignalStrength"));
        }
        
        // 排序参数
        if (searchRequest.containsKey("sortBy")) {
            params.put("sortBy", searchRequest.get("sortBy"));
        }
        if (searchRequest.containsKey("sortOrder")) {
            params.put("sortOrder", searchRequest.get("sortOrder"));
        }
        
        // 是否活跃
        if (searchRequest.containsKey("isActive")) {
            params.put("isActive", searchRequest.get("isActive"));
        }
        
        return params;
    }
    
    /**
     * 从Map中安全获取Integer值
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("无法转换为Integer: {} = {}", key, value);
            return 0;
        }
    }
}