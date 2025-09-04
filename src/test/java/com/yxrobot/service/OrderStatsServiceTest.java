package test.java.com.yxrobot.service;

import com.yxrobot.dto.OrderStatsDTO;
import com.yxrobot.entity.Order;
import com.yxrobot.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单统计服务测试类
 * 任务16: 编写单元测试 - 测试订单统计功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单统计服务测试")
public class OrderStatsServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderStatsService orderStatsService;

    private Map<String, Object> mockStatsData;

    @BeforeEach
    void setUp() {
        // 准备模拟统计数据
        mockStatsData = new HashMap<>();
        mockStatsData.put("total", 100);
        mockStatsData.put("pending", 10);
        mockStatsData.put("confirmed", 15);
        mockStatsData.put("processing", 20);
        mockStatsData.put("shipped", 25);
        mockStatsData.put("delivered", 20);
        mockStatsData.put("completed", 8);
        mockStatsData.put("cancelled", 2);
        mockStatsData.put("totalRevenue", new BigDecimal("150000.00"));
        mockStatsData.put("averageOrderValue", new BigDecimal("1500.00"));
        mockStatsData.put("salesOrders", 60);
        mockStatsData.put("rentalOrders", 40);
    }

    @Test
    void testGetOrderStats_Success() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getTotal());
        assertEquals(10, result.getPending());
        assertEquals(15, result.getConfirmed());
        assertEquals(20, result.getProcessing());
        assertEquals(25, result.getShipped());
        assertEquals(20, result.getDelivered());
        assertEquals(8, result.getCompleted());
        assertEquals(2, result.getCancelled());
        assertEquals(new BigDecimal("150000.00"), result.getTotalRevenue());
        assertEquals(new BigDecimal("1500.00"), result.getAverageOrderValue());
        assertEquals(60, result.getSalesOrders());
        assertEquals(40, result.getRentalOrders());

        // 验证方法调用
        verify(orderMapper, times(1)).selectOrderStats();
    }

    @Test
    void testGetOrderStats_EmptyData() {
        // 准备测试数据 - 空数据
        when(orderMapper.selectOrderStats()).thenReturn(null);

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStats();

        // 验证结果 - 应该返回零值统计
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getPending());
        assertEquals(0, result.getCompleted());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getAverageOrderValue());
        assertEquals(0, result.getSalesOrders());
        assertEquals(0, result.getRentalOrders());

        // 验证方法调用
        verify(orderMapper, times(1)).selectOrderStats();
    }

    @Test
    void testGetOrderStats_Exception() {
        // 准备测试数据 - 抛出异常
        when(orderMapper.selectOrderStats()).thenThrow(new RuntimeException("Database error"));

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStats();

        // 验证结果 - 异常时应该返回零值统计
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());

        // 验证方法调用
        verify(orderMapper, times(1)).selectOrderStats();
    }

    @Test
    void testGetOrderStatsByDateRange_Success() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(orderMapper.selectOrderStatsByDateRange(startDate, endDate)).thenReturn(mockStatsData);

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStatsByDateRange(startDate, endDate);

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getTotal());
        assertEquals(new BigDecimal("150000.00"), result.getTotalRevenue());

        // 验证方法调用
        verify(orderMapper, times(1)).selectOrderStatsByDateRange(startDate, endDate);
    }

    @Test
    void testGetOrderStatsByDateRange_InvalidDateRange() {
        // 准备测试数据 - 开始日期晚于结束日期
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        when(orderMapper.selectOrderStatsByDateRange(endDate, startDate)).thenReturn(mockStatsData);

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStatsByDateRange(startDate, endDate);

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getTotal());

        // 验证方法调用 - 日期应该被交换
        verify(orderMapper, times(1)).selectOrderStatsByDateRange(endDate, startDate);
    }

    @Test
    void testGetOrderStatusDistribution() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        Map<String, Integer> result = orderStatsService.getOrderStatusDistribution();

        // 验证结果
        assertNotNull(result);
        assertEquals(10, result.get("pending"));
        assertEquals(15, result.get("confirmed"));
        assertEquals(20, result.get("processing"));
        assertEquals(25, result.get("shipped"));
        assertEquals(20, result.get("delivered"));
        assertEquals(8, result.get("completed"));
        assertEquals(2, result.get("cancelled"));
    }

    @Test
    void testGetOrderTypeDistribution() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        Map<String, Integer> result = orderStatsService.getOrderTypeDistribution();

        // 验证结果
        assertNotNull(result);
        assertEquals(60, result.get("sales"));
        assertEquals(40, result.get("rental"));
    }

    @Test
    void testCalculateCompletionRate() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        BigDecimal result = orderStatsService.calculateCompletionRate();

        // 验证结果 - 8/100 = 8%
        assertNotNull(result);
        assertEquals(new BigDecimal("8.0000"), result);
    }

    @Test
    void testCalculateCompletionRate_ZeroTotal() {
        // 准备测试数据 - 总数为0
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("total", 0);
        emptyData.put("completed", 0);
        when(orderMapper.selectOrderStats()).thenReturn(emptyData);

        // 执行测试
        BigDecimal result = orderStatsService.calculateCompletionRate();

        // 验证结果 - 应该返回0
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateCancellationRate() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        BigDecimal result = orderStatsService.calculateCancellationRate();

        // 验证结果 - 2/100 = 2%
        assertNotNull(result);
        assertEquals(new BigDecimal("2.0000"), result);
    }

    @Test
    void testGetPendingOrdersCount() {
        // 准备测试数据
        when(orderMapper.selectOrderStats()).thenReturn(mockStatsData);

        // 执行测试
        Integer result = orderStatsService.getPendingOrdersCount();

        // 验证结果 - pending(10) + confirmed(15) + processing(20) = 45
        assertNotNull(result);
        assertEquals(45, result);
    }

    @Test
    void testDataValidation_NegativeValues() {
        // 准备测试数据 - 包含负值
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("total", -10);
        invalidData.put("pending", -5);
        invalidData.put("completed", -2);
        invalidData.put("totalRevenue", new BigDecimal("-1000.00"));
        invalidData.put("salesOrders", 0);
        invalidData.put("rentalOrders", 0);
        
        when(orderMapper.selectOrderStats()).thenReturn(invalidData);

        // 执行测试
        OrderStatsDTO result = orderStatsService.getOrderStats();

        // 验证结果 - 负值应该被修正为0
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getPending());
        assertEquals(0, result.getCompleted());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
    }
}