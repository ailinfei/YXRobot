package com.yxrobot.controller;

import com.yxrobot.dto.*;
import com.yxrobot.entity.SalesRecord;
import com.yxrobot.service.CustomerService;
import com.yxrobot.service.SalesProductService;
import com.yxrobot.service.SalesStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 销售管理控制器
 * 完全基于前端Sales.vue页面需求开发
 */
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private SalesProductService salesProductService;
    
    @Autowired
    private SalesStaffService salesStaffService;
    
    /**
     * 获取销售统计数据 - 适配前端概览卡片
     * 对应前端API: salesApi.stats.getStats()
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSalesStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String statType) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模拟销售统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSalesAmount", 1250000.00);
            stats.put("totalOrders", 156);
            stats.put("avgOrderAmount", 8012.82);
            stats.put("totalQuantity", 312);
            stats.put("newCustomers", 23);
            stats.put("activeCustomers", 89);
            stats.put("growthRate", 15.6);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取销售记录列表 - 适配前端数据表格
     * 对应前端API: salesApi.records.getList()
     */
    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getSalesRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模拟销售记录数据
            List<Map<String, Object>> records = createMockSalesRecords();
            
            // 根据关键词筛选
            if (keyword != null && !keyword.trim().isEmpty()) {
                records = records.stream()
                    .filter(record -> 
                        record.get("orderNumber").toString().contains(keyword) ||
                        record.get("customerName").toString().contains(keyword))
                    .toList();
            }
            
            // 根据状态筛选
            if (status != null && !status.trim().isEmpty()) {
                records = records.stream()
                    .filter(record -> status.equals(record.get("status")))
                    .toList();
            }
            
            // 分页处理
            int total = records.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Map<String, Object>> pagedRecords = records.subList(start, end);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedRecords);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPages", (int) Math.ceil((double) total / pageSize));
            data.put("isEmpty", pagedRecords.isEmpty());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除销售记录 - 适配前端删除操作
     * 对应前端API: salesApi.records.delete()
     */
    @DeleteMapping("/records/{id}")
    public ResponseEntity<Map<String, Object>> deleteSalesRecord(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模拟删除操作
            if (id != null && id > 0) {
                response.put("code", 200);
                response.put("message", "删除成功");
                response.put("data", Map.of(
                    "deletedId", id,
                    "deletedAt", LocalDateTime.now()
                ));
            } else {
                response.put("code", 404);
                response.put("message", "销售记录不存在");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取销售趋势图表数据 - 适配前端图表
     * 对应前端API: salesApi.charts.getTrendChartData()
     */
    @GetMapping("/charts/trends")
    public ResponseEntity<Map<String, Object>> getTrendChartData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "day") String groupBy) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 模拟趋势图表数据
            Map<String, Object> chartData = new HashMap<>();
            
            // 生成最近7天的数据
            List<String> categories = new ArrayList<>();
            List<Integer> salesAmountData = new ArrayList<>();
            List<Integer> orderCountData = new ArrayList<>();
            
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                categories.add(date.toString());
                salesAmountData.add((int) (Math.random() * 50000 + 10000));
                orderCountData.add((int) (Math.random() * 20 + 5));
            }
            
            chartData.put("categories", categories);
            
            List<Map<String, Object>> series = new ArrayList<>();
            
            Map<String, Object> salesSeries = new HashMap<>();
            salesSeries.put("name", "销售额");
            salesSeries.put("type", "line");
            salesSeries.put("data", salesAmountData);
            series.add(salesSeries);
            
            Map<String, Object> orderSeries = new HashMap<>();
            orderSeries.put("name", "订单数");
            orderSeries.put("type", "bar");
            orderSeries.put("data", orderCountData);
            series.add(orderSeries);
            
            chartData.put("series", series);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取分布图表数据 - 适配前端分布图表
     * 对应前端API: salesApi.charts.getDistribution()
     */
    @GetMapping("/charts/distribution")
    public ResponseEntity<Map<String, Object>> getDistribution(
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            switch (type) {
                case "product":
                    chartData = createProductDistributionData();
                    break;
                case "region":
                    chartData = createRegionDistributionData();
                    break;
                case "channel":
                    chartData = createChannelDistributionData();
                    break;
                default:
                    chartData = createProductDistributionData();
            }
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取客户列表 - 适配前端下拉选择
     * 对应前端API: salesApi.auxiliary.getCustomers()
     */
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            queryDTO.setKeyword(keyword);
            
            Map<String, Object> result = customerService.getCustomers(queryDTO);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取产品列表 - 适配前端下拉选择
     * 对应前端API: salesApi.auxiliary.getProducts()
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 使用现有的SalesProductService
            List<SalesProductDTO> products = salesProductService.getAllProducts();
            
            // 根据关键词筛选
            if (keyword != null && !keyword.trim().isEmpty()) {
                products = products.stream()
                    .filter(product -> product.getProductName().contains(keyword))
                    .toList();
            }
            
            // 分页处理
            int total = products.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<SalesProductDTO> pagedProducts = products.subList(start, end);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedProducts);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取销售人员列表 - 适配前端下拉选择
     * 对应前端API: salesApi.auxiliary.getStaff()
     */
    @GetMapping("/staff")
    public ResponseEntity<Map<String, Object>> getStaff(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SalesStaffQueryDTO queryDTO = new SalesStaffQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            queryDTO.setKeyword(keyword);
            
            Map<String, Object> result = salesStaffService.getSalesStaff(queryDTO);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量操作 - 适配前端批量操作
     * 对应前端API: salesApi.auxiliary.batchOperation()
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchOperation(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String operation = (String) request.get("operation");
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            
            // 模拟批量操作
            response.put("code", 200);
            response.put("message", "批量操作成功");
            response.put("data", Map.of(
                "operation", operation,
                "processedCount", ids != null ? ids.size() : 0,
                "processedAt", LocalDateTime.now()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 创建模拟销售记录数据
     */
    private List<Map<String, Object>> createMockSalesRecords() {
        List<Map<String, Object>> records = new ArrayList<>();
        
        String[] customers = {"张三", "李四", "王五", "赵六", "钱七"};
        String[] products = {"智能机器人A型", "智能机器人B型", "智能机器人C型"};
        String[] staff = {"销售经理-张经理", "销售代表-李代表", "销售顾问-王顾问"};
        String[] statuses = {"pending", "confirmed", "delivered", "completed"};
        String[] paymentStatuses = {"unpaid", "partial", "paid"};
        
        for (int i = 1; i <= 50; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", (long) i);
            record.put("orderNumber", "ORD" + String.format("%06d", i));
            record.put("customerId", (long) (i % 5 + 1));
            record.put("customerName", customers[i % customers.length]);
            record.put("customerPhone", "1380013800" + (i % 10));
            record.put("productId", (long) (i % 3 + 1));
            record.put("productName", products[i % products.length]);
            record.put("salesStaffId", (long) (i % 3 + 1));
            record.put("staffName", staff[i % staff.length]);
            record.put("quantity", i % 5 + 1);
            record.put("unitPrice", 8000.00 + (i % 10) * 500);
            record.put("salesAmount", (8000.00 + (i % 10) * 500) * (i % 5 + 1));
            record.put("discountAmount", (i % 3) * 100.0);
            record.put("status", statuses[i % statuses.length]);
            record.put("paymentStatus", paymentStatuses[i % paymentStatuses.length]);
            record.put("paymentMethod", i % 2 == 0 ? "银行转账" : "现金");
            record.put("region", i % 3 == 0 ? "北京" : (i % 3 == 1 ? "上海" : "广州"));
            record.put("channel", i % 2 == 0 ? "线上" : "线下");
            record.put("orderDate", LocalDate.now().minusDays(i % 30).toString());
            record.put("notes", i % 4 == 0 ? "重要客户订单" : null);
            record.put("createdAt", LocalDateTime.now().minusDays(i % 30).toString());
            record.put("updatedAt", LocalDateTime.now().toString());
            
            records.add(record);
        }
        
        return records;
    }
    
    /**
     * 创建产品分布数据
     */
    private Map<String, Object> createProductDistributionData() {
        Map<String, Object> data = new HashMap<>();
        
        List<String> categories = Arrays.asList("智能机器人A型", "智能机器人B型", "智能机器人C型", "智能机器人D型");
        List<Map<String, Object>> series = new ArrayList<>();
        
        Map<String, Object> seriesData = new HashMap<>();
        seriesData.put("name", "产品销售");
        seriesData.put("type", "pie");
        seriesData.put("data", Arrays.asList(450000, 320000, 280000, 150000));
        series.add(seriesData);
        
        data.put("categories", categories);
        data.put("series", series);
        
        return data;
    }
    
    /**
     * 创建地区分布数据
     */
    private Map<String, Object> createRegionDistributionData() {
        Map<String, Object> data = new HashMap<>();
        
        List<String> categories = Arrays.asList("北京", "上海", "广州", "深圳", "杭州");
        List<Map<String, Object>> series = new ArrayList<>();
        
        Map<String, Object> seriesData = new HashMap<>();
        seriesData.put("name", "销售额");
        seriesData.put("type", "bar");
        seriesData.put("data", Arrays.asList(380000, 320000, 280000, 220000, 150000));
        series.add(seriesData);
        
        data.put("categories", categories);
        data.put("series", series);
        
        return data;
    }
    
    /**
     * 创建渠道分布数据
     */
    private Map<String, Object> createChannelDistributionData() {
        Map<String, Object> data = new HashMap<>();
        
        List<String> categories = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月");
        List<Map<String, Object>> series = new ArrayList<>();
        
        Map<String, Object> onlineSeries = new HashMap<>();
        onlineSeries.put("name", "线上销售");
        onlineSeries.put("type", "line");
        onlineSeries.put("data", Arrays.asList(120000, 132000, 101000, 134000, 90000, 230000));
        series.add(onlineSeries);
        
        Map<String, Object> offlineSeries = new HashMap<>();
        offlineSeries.put("name", "线下销售");
        offlineSeries.put("type", "line");
        offlineSeries.put("data", Arrays.asList(220000, 182000, 191000, 234000, 290000, 330000));
        series.add(offlineSeries);
        
        data.put("categories", categories);
        data.put("series", series);
        
        return data;
    }
}