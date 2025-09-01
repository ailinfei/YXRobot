package com.yxrobot.controller;

import com.yxrobot.dto.CustomerDTO;
import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.dto.CustomerDeviceDTO;
import com.yxrobot.dto.CustomerOrderDTO;
import com.yxrobot.dto.ServiceRecordDTO;
import com.yxrobot.entity.Customer;
import com.yxrobot.service.CustomerService;
import com.yxrobot.service.CustomerStatsService;
import com.yxrobot.service.CustomerDeviceService;
import com.yxrobot.service.CustomerOrderService;
import com.yxrobot.service.CustomerServiceRecordService;
import com.yxrobot.service.CustomerValidationService;
import com.yxrobot.exception.CustomerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户管理控制器
 * 完全基于前端Customers.vue页面需求开发
 * 提供与前端页面完全匹配的API接口
 */
@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CustomerStatsService customerStatsService;
    
    @Autowired
    private CustomerDeviceService customerDeviceService;
    
    @Autowired
    private CustomerOrderService customerOrderService;
    
    @Autowired
    private CustomerServiceRecordService customerServiceRecordService;
    
    @Autowired
    private CustomerValidationService customerValidationService;
    
    @Autowired
    private com.yxrobot.service.CustomerApiValidationService apiValidationService;
    
    @Autowired
    private com.yxrobot.service.CustomerQueryOptimizationService queryOptimizationService;
    
    @Autowired
    private com.yxrobot.service.CustomerSearchOptimizationService searchOptimizationService;
    
    // ==================== 客户列表和查询接口 ====================
    
    /**
     * 获取客户列表 - 适配前端客户列表表格
     * 对应前端API: customerApi.getCustomers()
     * 支持分页、搜索、筛选、排序功能
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 构建查询条件
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            queryDTO.setKeyword(keyword);
            queryDTO.setLevel(level);
            queryDTO.setStatus(status);
            queryDTO.setDeviceType(deviceType);
            queryDTO.setRegion(region);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            
            // 1. 验证查询参数
            com.yxrobot.service.CustomerApiValidationService.ValidationResult validationResult = 
                apiValidationService.validateQueryParameters(queryDTO);
            
            if (validationResult.hasErrors()) {
                response.put("code", 400);
                response.put("message", "参数验证失败");
                response.put("data", null);
                response.put("errors", validationResult.getErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            // 2. 搜索查询优化
            com.yxrobot.service.CustomerSearchOptimizationService.SearchOptimizationResult searchOptimization = 
                searchOptimizationService.optimizeSearchQuery(queryDTO);
            
            // 3. 查询优化
            com.yxrobot.service.CustomerQueryOptimizationService.OptimizedQuery optimizedQuery = 
                queryOptimizationService.optimizeCustomerQuery(searchOptimization.getOptimizedQuery());
            
            // 4. 查询客户数据
            Map<String, Object> result = customerService.getCustomers(searchOptimization.getOptimizedQuery());
            
            // 5. 记录查询性能
            long executionTime = System.currentTimeMillis() - startTime;
            int resultCount = result.get("list") != null ? 
                ((List<?>) result.get("list")).size() : 0;
            queryOptimizationService.recordQueryPerformance("customer_list", executionTime, resultCount);
            searchOptimizationService.recordSearchPerformance("customer_list", executionTime, resultCount);
            
            // 5. 构建响应
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            // 添加性能和优化信息（开发环境）
            if (logger.isDebugEnabled()) {
                response.put("performance", Map.of(
                    "executionTime", executionTime + "ms",
                    "resultCount", resultCount,
                    "queryOptimizationTips", optimizedQuery.getOptimizationTips(),
                    "searchOptimizationTips", searchOptimization.getOptimizationTips(),
                    "indexSuggestions", searchOptimization.getIndexSuggestions(),
                    "cacheEnabled", searchOptimization.isUseCache()
                ));
            }
            
            // 添加验证警告（如果有）
            if (validationResult.hasWarnings()) {
                response.put("warnings", validationResult.getWarnings());
            }
            
        } catch (CustomerException e) {
            logger.warn("客户查询参数验证失败: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("客户查询失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取客户详情 - 适配前端客户详情对话框
     * 对应前端API: customerApi.getCustomer(id)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomer(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            
            if (customer == null) {
                response.put("code", 404);
                response.put("message", "客户不存在");
                response.put("data", null);
            } else {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", customer);
            }
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 客户统计接口 ====================
    
    /**
     * 获取客户统计数据 - 适配前端统计卡片
     * 对应前端API: customerApi.getCustomerStats()
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerStatsDTO stats = customerStatsService.getCustomerStats();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 客户CRUD接口 ====================
    
    /**
     * 创建客户 - 适配前端新增客户功能
     * 对应前端API: customerApi.createCustomer(data)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody CustomerCreateDTO createDTO) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("开始创建客户，姓名: {}", createDTO.getName());
            
            // 1. API参数验证
            com.yxrobot.service.CustomerApiValidationService.ValidationResult apiValidation = 
                apiValidationService.validateCreateParameters(createDTO);
            
            if (apiValidation.hasErrors()) {
                response.put("code", 400);
                response.put("message", "参数验证失败");
                response.put("errors", apiValidation.getErrors());
                response.put("warnings", apiValidation.getWarnings());
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 2. 综合数据验证
            CustomerValidationService.ValidationSummary validationSummary = 
                customerValidationService.validateCustomerCreate(createDTO);
            
            if (!validationSummary.isValid()) {
                logger.warn("客户创建验证失败: {}", validationSummary.getErrors());
                response.put("code", 400);
                response.put("message", "数据验证失败");
                response.put("errors", validationSummary.getErrors());
                response.put("warnings", validationSummary.getWarnings());
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 3. 创建客户
            Customer createdCustomer = customerService.createCustomerFromDTO(createDTO);
            CustomerDTO customerDTO = customerService.getCustomerById(createdCustomer.getId());
            
            // 4. 记录性能
            long executionTime = System.currentTimeMillis() - startTime;
            queryOptimizationService.recordQueryPerformance("customer_create", executionTime, 1);
            
            logger.info("客户创建成功，ID: {}, 耗时: {}ms", createdCustomer.getId(), executionTime);
            
            response.put("code", 200);
            response.put("message", "客户创建成功");
            response.put("data", customerDTO);
            
            // 添加验证信息（如果有警告）
            if (validationSummary.hasWarnings() || apiValidation.hasWarnings()) {
                List<Object> allWarnings = new java.util.ArrayList<>();
                if (validationSummary.hasWarnings()) {
                    allWarnings.addAll(validationSummary.getWarnings());
                }
                if (apiValidation.hasWarnings()) {
                    allWarnings.addAll(apiValidation.getWarnings());
                }
                response.put("warnings", allWarnings);
            }
            
            if (validationSummary.hasInfos()) {
                response.put("infos", validationSummary.getInfos());
            }
            
            // 性能信息（开发环境）
            if (logger.isDebugEnabled()) {
                response.put("performance", Map.of(
                    "executionTime", executionTime + "ms",
                    "validationTime", "包含在总时间内"
                ));
            }
            
        } catch (CustomerException e) {
            logger.warn("客户创建业务异常: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("errorCode", e.getErrorCode());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("客户创建系统异常", e);
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新客户 - 适配前端编辑客户功能
     * 对应前端API: customerApi.updateCustomer(id, data)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Long id, 
                                                             @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始更新客户，ID: {}", id);
            
            // 综合数据验证
            CustomerValidationService.ValidationSummary validationSummary = 
                customerValidationService.validateCustomerUpdate(id, updateDTO);
            
            if (!validationSummary.isValid()) {
                logger.warn("客户更新验证失败: {}", validationSummary.getErrors());
                response.put("code", 400);
                response.put("message", "数据验证失败");
                response.put("errors", validationSummary.getErrors());
                response.put("warnings", validationSummary.getWarnings());
                response.put("data", null);
                return ResponseEntity.ok(response);
            }
            
            // 更新客户
            Customer updatedCustomer = customerService.updateCustomerFromDTO(id, updateDTO);
            CustomerDTO customerDTO = customerService.getCustomerById(updatedCustomer.getId());
            
            logger.info("客户更新成功，ID: {}", id);
            
            response.put("code", 200);
            response.put("message", "客户更新成功");
            response.put("data", customerDTO);
            
            // 添加验证信息（如果有警告）
            if (validationSummary.hasWarnings()) {
                response.put("warnings", validationSummary.getWarnings());
            }
            if (validationSummary.hasInfos()) {
                response.put("infos", validationSummary.getInfos());
            }
            
        } catch (CustomerException e) {
            logger.warn("客户更新业务异常: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("errorCode", e.getErrorCode());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("客户更新系统异常", e);
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除客户 - 适配前端删除客户功能
     * 对应前端API: customerApi.deleteCustomer(id)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            customerService.deleteCustomer(id);
            
            response.put("code", 200);
            response.put("message", "客户删除成功");
            response.put("data", null);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 客户关联数据接口 ====================
    
    /**
     * 获取客户设备列表 - 适配前端客户详情页面
     * 对应前端API: customerApi.getCustomerDevices(customerId)
     */
    @GetMapping("/{id}/devices")
    public ResponseEntity<Map<String, Object>> getCustomerDevices(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerDeviceDTO> devices = customerDeviceService.getCustomerDevices(id);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", devices);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取客户订单列表 - 适配前端客户详情页面
     * 对应前端API: customerApi.getCustomerOrders(customerId)
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<Map<String, Object>> getCustomerOrders(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerOrderDTO> orders = customerOrderService.getCustomerOrders(id);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", orders);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取客户服务记录 - 适配前端客户详情页面
     * 对应前端API: customerApi.getCustomerServiceRecords(customerId)
     */
    @GetMapping("/{id}/service-records")
    public ResponseEntity<Map<String, Object>> getCustomerServiceRecords(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ServiceRecordDTO> serviceRecords = 
                customerServiceRecordService.getCustomerServiceRecords(id);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", serviceRecords);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 搜索和筛选接口 ====================
    
    /**
     * 搜索客户 - 适配前端搜索功能
     * 对应前端API: customerApi.searchCustomers(keyword)
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerDTO> customers = customerService.searchCustomers(keyword, limit);
            
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", customers);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "搜索失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取筛选选项 - 适配前端筛选功能
     * 对应前端API: customerApi.getFilterOptions()
     */
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("levels", customerService.getCustomerLevelOptions());
            options.put("regions", customerService.getRegionOptions());
            options.put("industries", customerService.getIndustryOptions());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", options);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 高级搜索 - 支持复杂筛选条件
     * 对应前端API: customerApi.advancedSearch(conditions)
     */
    @PostMapping("/advanced-search")
    public ResponseEntity<Map<String, Object>> advancedSearch(@RequestBody CustomerQueryDTO queryDTO) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("执行高级搜索，条件: {}", queryDTO);
            
            // 1. 验证查询参数
            com.yxrobot.service.CustomerApiValidationService.ValidationResult validationResult = 
                apiValidationService.validateQueryParameters(queryDTO);
            
            if (validationResult.hasErrors()) {
                response.put("code", 400);
                response.put("message", "参数验证失败");
                response.put("data", null);
                response.put("errors", validationResult.getErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            // 2. 执行高级搜索
            Map<String, Object> result = customerService.advancedSearch(queryDTO);
            
            // 3. 记录查询性能
            long executionTime = System.currentTimeMillis() - startTime;
            int resultCount = result.get("list") != null ? 
                ((List<?>) result.get("list")).size() : 0;
            queryOptimizationService.recordQueryPerformance("advanced_search", executionTime, resultCount);
            
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", result);
            
            // 添加性能信息（开发环境）
            if (logger.isDebugEnabled()) {
                response.put("performance", Map.of(
                    "executionTime", executionTime + "ms",
                    "resultCount", resultCount,
                    "hasAdvancedFilters", queryDTO.hasAdvancedFilters()
                ));
            }
            
        } catch (CustomerException e) {
            logger.warn("高级搜索业务异常: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("高级搜索系统异常", e);
            response.put("code", 500);
            response.put("message", "搜索失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 搜索建议 - 支持前端搜索提示
     * 对应前端API: customerApi.getSearchSuggestions(keyword)
     */
    @GetMapping("/search-suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> suggestions = customerService.getSearchSuggestions(keyword, limit);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", suggestions);
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 智能搜索建议 - 基于历史搜索和AI优化
     * 对应前端API: customerApi.getIntelligentSuggestions(keyword)
     */
    @GetMapping("/intelligent-suggestions")
    public ResponseEntity<Map<String, Object>> getIntelligentSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> suggestions = searchOptimizationService.getIntelligentSuggestions(keyword, limit);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", suggestions);
            
        } catch (Exception e) {
            logger.error("获取智能搜索建议失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 热门搜索关键词 - 支持前端热门搜索显示
     * 对应前端API: customerApi.getPopularKeywords()
     */
    @GetMapping("/popular-keywords")
    public ResponseEntity<Map<String, Object>> getPopularKeywords(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> keywords = searchOptimizationService.getPopularKeywords(limit);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", keywords);
            
        } catch (Exception e) {
            logger.error("获取热门搜索关键词失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 筛选统计信息 - 支持前端筛选面板
     * 对应前端API: customerApi.getFilterStatistics()
     */
    @GetMapping("/filter-statistics")
    public ResponseEntity<Map<String, Object>> getFilterStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics = customerService.getFilterStatistics();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
        } catch (Exception e) {
            logger.error("获取筛选统计信息失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 按标签筛选客户 - 支持标签筛选功能
     * 对应前端API: customerApi.getCustomersByTags(tags)
     */
    @GetMapping("/by-tags")
    public ResponseEntity<Map<String, Object>> getCustomersByTags(
            @RequestParam String tags,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            List<CustomerDTO> customers = customerService.getCustomersByTags(tags, queryDTO);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", customers);
            result.put("total", customers.size());
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
        } catch (Exception e) {
            logger.error("按标签筛选客户失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 按时间范围筛选客户 - 支持时间范围筛选功能
     * 对应前端API: customerApi.getCustomersByDateRange(startDate, endDate)
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<Map<String, Object>> getCustomersByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "registered") String dateType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            // 根据日期类型设置筛选条件
            switch (dateType) {
                case "registered":
                    queryDTO.setRegisteredStartDate(startDate);
                    queryDTO.setRegisteredEndDate(endDate);
                    break;
                case "lastActive":
                    queryDTO.setLastActiveStartDate(startDate);
                    queryDTO.setLastActiveEndDate(endDate);
                    break;
                default:
                    queryDTO.setStartDate(startDate);
                    queryDTO.setEndDate(endDate);
            }
            
            Map<String, Object> result = customerService.getCustomers(queryDTO);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
        } catch (Exception e) {
            logger.error("按时间范围筛选客户失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 批量操作接口（暂不实现） ====================
    
    /**
     * 批量操作客户 - 预留接口
     * 对应前端API: customerApi.batchOperation(ids, operation)
     */
    @PutMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchOperation(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("code", 501);
        response.put("message", "批量操作功能暂未实现");
        response.put("data", null);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 导出客户数据 - 预留接口
     * 对应前端API: customerApi.exportCustomers()
     */
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportCustomers() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("code", 501);
        response.put("message", "数据导出功能暂未实现");
        response.put("data", null);
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 性能监控和优化接口 ====================
    
    /**
     * 搜索性能报告 - 管理员监控接口
     * 对应前端API: customerApi.getSearchPerformanceReport()
     */
    @GetMapping("/performance-report")
    public ResponseEntity<Map<String, Object>> getSearchPerformanceReport() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> report = searchOptimizationService.getSearchPerformanceReport();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", report);
            
        } catch (Exception e) {
            logger.error("获取搜索性能报告失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清理搜索缓存 - 管理员操作接口
     * 对应前端API: customerApi.clearSearchCache()
     */
    @PostMapping("/clear-search-cache")
    public ResponseEntity<Map<String, Object>> clearSearchCache() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            searchOptimizationService.clearCache();
            
            response.put("code", 200);
            response.put("message", "搜索缓存清理成功");
            response.put("data", null);
            
        } catch (Exception e) {
            logger.error("清理搜索缓存失败", e);
            response.put("code", 500);
            response.put("message", "清理失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 搜索优化建议 - 管理员优化接口
     * 对应前端API: customerApi.getSearchOptimizationAdvice()
     */
    @GetMapping("/optimization-advice")
    public ResponseEntity<Map<String, Object>> getSearchOptimizationAdvice() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 创建示例查询来获取优化建议
            CustomerQueryDTO sampleQuery = new CustomerQueryDTO();
            sampleQuery.setKeyword("test");
            sampleQuery.setCustomerLevel("vip");
            sampleQuery.setRegion("北京");
            
            com.yxrobot.service.CustomerSearchOptimizationService.SearchOptimizationResult optimization = 
                searchOptimizationService.optimizeSearchQuery(sampleQuery);
            
            Map<String, Object> advice = new HashMap<>();
            advice.put("optimizationTips", optimization.getOptimizationTips());
            advice.put("indexSuggestions", optimization.getIndexSuggestions());
            advice.put("performanceReport", searchOptimizationService.getSearchPerformanceReport());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", advice);
            
        } catch (Exception e) {
            logger.error("获取搜索优化建议失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
}