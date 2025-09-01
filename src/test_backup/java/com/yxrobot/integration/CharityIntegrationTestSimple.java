package com.yxrobot.integration;

import com.yxrobot.dto.CharityStatsDTO;
import com.yxrobot.service.CharityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简化的公益管理集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CharityIntegrationTestSimple {

    @Autowired
    private CharityService charityService;

    @Test
    public void testGetCharityStats() {
        // 测试获取公益统计数据
        CharityStatsDTO stats = charityService.getCharityStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalBeneficiaries() >= 0);
        assertTrue(stats.getTotalInstitutions() >= 0);
    }
}