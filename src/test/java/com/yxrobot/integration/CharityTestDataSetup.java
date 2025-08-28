package com.yxrobot.integration;

import com.yxrobot.entity.CharityStats;
import com.yxrobot.mapper.CharityStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 简化的公益测试数据设置
 */
@Component
public class CharityTestDataSetup {

    @Autowired
    private CharityStatsMapper charityStatsMapper;

    public void setupTestData() {
        // 创建基础测试数据
        CharityStats stats = new CharityStats();
        stats.setTotalBeneficiaries(25000);
        stats.setTotalInstitutions(300);
        stats.setCooperatingInstitutions(180);
        stats.setTotalVolunteers(250);
        stats.setTotalRaised(new BigDecimal("15000000"));
        stats.setTotalDonated(new BigDecimal("12000000"));
        stats.setTotalProjects(120);
        stats.setActiveProjects(35);
        stats.setCompletedProjects(75);
        stats.setTotalActivities(420);
        stats.setThisMonthActivities(25);

        charityStatsMapper.insert(stats);
    }

    public void cleanupTestData() {
        // 清理测试数据 - 获取最新记录并删除
        CharityStats latest = charityStatsMapper.selectLatest();
        if (latest != null) {
            charityStatsMapper.deleteById(latest.getId());
        }
    }
}