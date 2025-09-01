package com.yxrobot.service;

import com.yxrobot.entity.SalesStaff;
import com.yxrobot.dto.SalesStaffDTO;
import com.yxrobot.dto.SalesStaffQueryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 销售人员服务类
 * 处理销售人员相关的业务逻辑
 */
@Service
public class SalesStaffService {
    
    /**
     * 获取销售人员列表
     * @param queryDTO 查询条件
     * @return 销售人员列表
     */
    public Map<String, Object> getSalesStaff(SalesStaffQueryDTO queryDTO) {
        // 模拟数据，实际应该从数据库查询
        List<SalesStaffDTO> staff = List.of(
            createMockStaff(1L, "销售经理-张经理", "SALES001", "销售部"),
            createMockStaff(2L, "销售代表-李代表", "SALES002", "销售部"),
            createMockStaff(3L, "销售顾问-王顾问", "SALES003", "销售部")
        );
        
        return Map.of(
            "list", staff,
            "total", staff.size(),
            "page", queryDTO.getPage() != null ? queryDTO.getPage() : 1,
            "pageSize", queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20
        );
    }
    
    /**
     * 获取在职销售人员列表
     * @return 在职销售人员列表
     */
    public List<SalesStaffDTO> getActiveStaff() {
        return List.of(
            createMockStaff(1L, "销售经理-张经理", "SALES001", "销售部"),
            createMockStaff(2L, "销售代表-李代表", "SALES002", "销售部"),
            createMockStaff(3L, "销售顾问-王顾问", "SALES003", "销售部")
        );
    }
    
    /**
     * 根据ID获取销售人员
     * @param id 销售人员ID
     * @return 销售人员信息
     */
    public SalesStaffDTO getStaffById(Long id) {
        return createMockStaff(id, "销售人员" + id, "SALES00" + id, "销售部");
    }
    
    /**
     * 创建模拟销售人员数据
     */
    private SalesStaffDTO createMockStaff(Long id, String name, String code, String department) {
        SalesStaffDTO staff = new SalesStaffDTO();
        staff.setId(id);
        staff.setStaffName(name);
        staff.setStaffCode(code);
        staff.setDepartment(department);
        staff.setPosition("销售");
        staff.setPhone("1380013800" + id);
        staff.setEmail("sales" + id + "@yxrobot.com");
        staff.setIsActive(true);
        staff.setCreatedAt(java.time.LocalDateTime.now().toString());
        staff.setUpdatedAt(java.time.LocalDateTime.now().toString());
        return staff;
    }
}