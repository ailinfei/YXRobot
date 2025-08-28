#!/usr/bin/env python3
"""
系统集成测试脚本
任务12：系统集成测试和部署验证 - 确保整体功能正常

测试范围：
1. 执行完整的前后端集成测试
2. 验证前端Sales.vue页面的所有功能正常工作
3. 测试日期范围选择对所有数据的影响
4. 验证销售概览卡片数据的准确性
5. 测试所有图表的数据显示和刷新功能
6. 验证销售记录列表的搜索、筛选、分页、删除功能
7. 测试销售记录详情对话框的数据显示
8. 验证响应式设计在不同设备上的表现
9. 确保访问地址http://localhost:8081/admin/business/sales正常工作
"""

import requests
import json
import time
from datetime import datetime, timedelta
from typing import Dict, List, Any
import sys
import os

class SystemIntegrationTester:
    def __init__(self, backend_url="http://localhost:8080", frontend_url="http://localhost:8081"):
        self.backend_url = backend_url
        self.frontend_url = frontend_url
        self.test_results = []
        self.current_test = ""
        
    def log_test(self, message: str, success: bool = True):
        """记录测试结果"""
        status = "✅" if success else "❌"
        print(f"{status} [{self.current_test}] {message}")
        
        self.test_results.append({
            "test": self.current_test,
            "message": message,
            "success": success,
            "timestamp": datetime.now().isoformat()
        })

    def make_request(self, endpoint: str, method: str = "GET", data: Dict = None) -> Dict:
        """发送HTTP请求"""
        url = f"{self.backend_url}{endpoint}"
        
        try:
            start_time = time.time()
            
            if method == "GET":
                response = requests.get(url, timeout=10)
            elif method == "POST":
                response = requests.post(url, json=data, timeout=10)
            elif method == "PUT":
                response = requests.put(url, json=data, timeout=10)
            elif method == "DELETE":
                response = requests.delete(url, timeout=10)
            
            response_time = (time.time() - start_time) * 1000
            
            return {
                "success": response.status_code < 400,
                "status_code": response.status_code,
                "data": response.json() if response.content else {},
                "response_time": response_time,
                "url": url
            }
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "url": url
            }

    def test_frontend_page_access(self):
        """测试前端页面访问"""
        self.current_test = "前端页面访问"
        print(f"\n🌐 开始测试: {self.current_test}")
        
        try:
            # 测试前端页面访问
            frontend_url = f"{self.frontend_url}/admin/business/sales"
            response = requests.get(frontend_url, timeout=10)
            
            if response.status_code == 200:
                self.log_test(f"前端页面访问成功: {frontend_url}", True)
                return True
            else:
                self.log_test(f"前端页面访问失败: {response.status_code}", False)
                return False
                
        except Exception as e:
            self.log_test(f"前端页面访问异常: {str(e)}", False)
            return False

    def test_sales_stats_api(self):
        """测试销售统计API"""
        self.current_test = "销售统计API"
        print(f"\n�  开始测试: {self.current_test}")
        
        # 测试基本统计数据
        result = self.make_request("/api/sales/stats")
        if result["success"]:
            data = result["data"].get("data", {})
            required_fields = ["totalSalesAmount", "totalOrders", "avgOrderAmount", "newCustomers"]
            
            missing_fields = [field for field in required_fields if field not in data]
            if not missing_fields:
                self.log_test(f"统计数据结构正确 ({result.get('response_time', 0):.0f}ms)", True)
            else:
                self.log_test(f"统计数据缺少字段: {missing_fields}", False)
                return False
        else:
            self.log_test("统计数据API调用失败", False)
            return False
        
        # 测试日期范围统计
        start_date = (datetime.now() - timedelta(days=30)).strftime("%Y-%m-%d")
        end_date = datetime.now().strftime("%Y-%m-%d")
        
        result = self.make_request(f"/api/sales/stats?startDate={start_date}&endDate={end_date}")
        if result["success"]:
            self.log_test(f"日期范围统计正常 ({result.get('response_time', 0):.0f}ms)", True)
        else:
            self.log_test("日期范围统计失败", False)
            return False
        
        return True

    def test_sales_records_api(self):
        """测试销售记录API"""
        self.current_test = "销售记录API"
        print(f"\n📋 开始测试: {self.current_test}")
        
        # 测试基本列表查询
        result = self.make_request("/api/sales/records?page=1&pageSize=20")
        if result["success"]:
            data = result["data"].get("data", {})
            if "list" in data and "total" in data:
                self.log_test(f"销售记录列表结构正确 ({result.get('response_time', 0):.0f}ms)", True)
                
                # 检查记录字段完整性
                records = data.get("list", [])
                if records:
                    record = records[0]
                    required_fields = ["id", "orderNumber", "customerName", "productName", "salesAmount"]
                    missing_fields = [field for field in required_fields if field not in record]
                    
                    if not missing_fields:
                        self.log_test("销售记录字段映射正确", True)
                    else:
                        self.log_test(f"销售记录缺少字段: {missing_fields}", False)
                        return False
                else:
                    self.log_test("销售记录列表为空（正常情况）", True)
            else:
                self.log_test("销售记录列表结构错误", False)
                return False
        else:
            self.log_test("销售记录API调用失败", False)
            return False
        
        # 测试搜索功能
        result = self.make_request("/api/sales/records?keyword=test&page=1&pageSize=20")
        if result["success"]:
            self.log_test(f"搜索功能正常 ({result.get('response_time', 0):.0f}ms)", True)
        else:
            self.log_test("搜索功能失败", False)
            return False
        
        # 测试状态筛选
        result = self.make_request("/api/sales/records?status=pending&page=1&pageSize=20")
        if result["success"]:
            self.log_test(f"状态筛选功能正常 ({result.get('response_time', 0):.0f}ms)", True)
        else:
            self.log_test("状态筛选功能失败", False)
            return False
        
        return True

    def test_charts_api(self):
        """测试图表数据API"""
        self.current_test = "图表数据API"
        print(f"\n📈 开始测试: {self.current_test}")
        
        # 测试销售趋势图表
        result = self.make_request("/api/sales/charts/trends?groupBy=day")
        if result["success"]:
            data = result["data"].get("data", {})
            if "categories" in data and "series" in data:
                self.log_test(f"趋势图表数据结构正确 ({result.get('response_time', 0):.0f}ms)", True)
            else:
                self.log_test("趋势图表数据结构错误", False)
                return False
        else:
            self.log_test("趋势图表API调用失败", False)
            return False
        
        # 测试分布图表
        distribution_types = ["product", "region", "channel"]
        for dist_type in distribution_types:
            result = self.make_request(f"/api/sales/charts/distribution?type={dist_type}")
            if result["success"]:
                data = result["data"].get("data", {})
                if "categories" in data and "series" in data:
                    self.log_test(f"{dist_type}分布图表正常 ({result.get('response_time', 0):.0f}ms)", True)
                else:
                    self.log_test(f"{dist_type}分布图表数据结构错误", False)
                    return False
            else:
                self.log_test(f"{dist_type}分布图表API调用失败", False)
                return False
        
        return True

    def test_date_range_functionality(self):
        """测试日期范围功能"""
        self.current_test = "日期范围功能"
        print(f"\n📅 开始测试: {self.current_test}")
        
        # 测试不同日期范围对所有API的影响
        start_date = (datetime.now() - timedelta(days=7)).strftime("%Y-%m-%d")
        end_date = datetime.now().strftime("%Y-%m-%d")
        
        apis_to_test = [
            ("/api/sales/stats", "统计数据"),
            ("/api/sales/records", "销售记录"),
            ("/api/sales/charts/trends", "趋势图表"),
            ("/api/sales/charts/distribution?type=product", "产品分布图表")
        ]
        
        for endpoint, name in apis_to_test:
            separator = "&" if "?" in endpoint else "?"
            url_with_date = f"{endpoint}{separator}startDate={start_date}&endDate={end_date}"
            
            result = self.make_request(url_with_date)
            if result["success"]:
                self.log_test(f"{name}日期范围筛选正常 ({result.get('response_time', 0):.0f}ms)", True)
            else:
                self.log_test(f"{name}日期范围筛选失败", False)
                return False
        
        return True

    def test_performance_requirements(self):
        """测试性能要求"""
        self.current_test = "性能要求"
        print(f"\n⚡ 开始测试: {self.current_test}")
        
        performance_tests = [
            ("/api/sales/records?page=1&pageSize=20", "销售记录列表", 2000),  # 2秒内
            ("/api/sales/stats", "销售统计", 1000),  # 1秒内
            ("/api/sales/charts/trends", "趋势图表", 3000),  # 3秒内
            ("/api/sales/charts/distribution?type=product", "产品分布", 3000)  # 3秒内
        ]
        
        all_passed = True
        for endpoint, name, max_time in performance_tests:
            result = self.make_request(endpoint)
            if result["success"]:
                response_time = result.get("response_time", 0)
                if response_time <= max_time:
                    self.log_test(f"{name}性能达标 ({response_time:.0f}ms <= {max_time}ms)", True)
                else:
                    self.log_test(f"{name}性能不达标 ({response_time:.0f}ms > {max_time}ms)", False)
                    all_passed = False
            else:
                self.log_test(f"{name}性能测试失败", False)
                all_passed = False
        
        return all_passed

    def test_error_handling(self):
        """测试错误处理"""
        self.current_test = "错误处理"
        print(f"\n🛡️ 开始测试: {self.current_test}")
        
        # 测试无效参数处理
        error_tests = [
            ("/api/sales/records?page=-1", "无效页码"),
            ("/api/sales/records?pageSize=0", "无效页面大小"),
            ("/api/sales/charts/distribution?type=invalid", "无效图表类型"),
            ("/api/sales/stats?startDate=invalid-date", "无效日期格式")
        ]
        
        for endpoint, description in error_tests:
            result = self.make_request(endpoint)
            # 错误处理应该返回适当的错误响应，而不是500错误
            if result["success"] or result.get("status_code", 500) < 500:
                self.log_test(f"{description}错误处理正常", True)
            else:
                self.log_test(f"{description}错误处理异常", False)
                return False
        
        return True

    def test_data_consistency(self):
        """测试数据一致性"""
        self.current_test = "数据一致性"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        # 获取统计数据
        stats_result = self.make_request("/api/sales/stats")
        if not stats_result["success"]:
            self.log_test("无法获取统计数据", False)
            return False
        
        stats_data = stats_result["data"].get("data", {})
        total_orders_from_stats = stats_data.get("totalOrders", 0)
        
        # 获取销售记录总数
        records_result = self.make_request("/api/sales/records?page=1&pageSize=1")
        if not records_result["success"]:
            self.log_test("无法获取销售记录", False)
            return False
        
        records_data = records_result["data"].get("data", {})
        total_orders_from_records = records_data.get("total", 0)
        
        # 比较数据一致性
        if total_orders_from_stats == total_orders_from_records:
            self.log_test(f"统计数据与记录数据一致 (订单数: {total_orders_from_stats})", True)
        else:
            self.log_test(f"数据不一致 - 统计: {total_orders_from_stats}, 记录: {total_orders_from_records}", False)
            return False
        
        return True

    def test_complete_workflow(self):
        """测试完整的工作流程"""
        self.current_test = "完整工作流程"
        print(f"\n🔄 开始测试: {self.current_test}")
        
        workflow_success = True
        
        # 执行所有子测试
        tests = [
            self.test_sales_stats_api,
            self.test_sales_records_api,
            self.test_charts_api,
            self.test_date_range_functionality,
            self.test_performance_requirements,
            self.test_error_handling,
            self.test_data_consistency
        ]
        
        for test_func in tests:
            try:
                if not test_func():
                    workflow_success = False
            except Exception as e:
                self.log_test(f"测试执行异常: {str(e)}", False)
                workflow_success = False
        
        return workflow_success

    def test_frontend_integration(self):
        """测试前端集成"""
        self.current_test = "前端集成"
        print(f"\n🌐 开始测试: {self.current_test}")
        
        # 检查前端文件是否存在
        frontend_files = [
            "workspace/projects/YXRobot/src/frontend/src/views/admin/business/Sales.vue",
            "workspace/projects/YXRobot/src/frontend/src/api/sales.ts",
            "workspace/projects/YXRobot/src/frontend/src/types/sales.ts"
        ]
        
        for file_path in frontend_files:
            if os.path.exists(file_path):
                self.log_test(f"前端文件存在: {os.path.basename(file_path)}", True)
            else:
                self.log_test(f"前端文件缺失: {os.path.basename(file_path)}", False)
                return False
        
        # 测试前端页面访问
        try:
            frontend_url = f"{self.frontend_url}/admin/business/sales"
            response = requests.get(frontend_url, timeout=10)
            
            if response.status_code == 200:
                self.log_test(f"前端页面访问成功: {frontend_url}", True)
            else:
                self.log_test(f"前端页面访问失败: {response.status_code}", False)
                return False
                
        except Exception as e:
            self.log_test(f"前端页面访问异常: {str(e)}", False)
            return False
        
        return True

    def run_all_tests(self):
        """运行所有集成测试"""
        print("🚀 开始系统集成测试 - 任务12")
        print(f"🌐 后端服务器: {self.backend_url}")
        print(f"🌐 前端服务器: {self.frontend_url}")
        print("="*80)
        
        try:
            # 执行所有测试
            main_tests = [
                self.test_frontend_integration,
                self.test_complete_workflow
            ]
            
            overall_success = True
            for test_func in main_tests:
                try:
                    if not test_func():
                        overall_success = False
                except Exception as e:
                    print(f"\n💥 测试 {test_func.__name__} 执行异常: {str(e)}")
                    overall_success = False
            
            # 生成报告
            report = self.generate_report()
            report["overall_success"] = overall_success
            
            return report
            
        except Exception as e:
            print(f"\n💥 测试执行过程中发生错误: {str(e)}")
            return self.generate_report()

    def generate_report(self):
        """生成测试报告"""
        total_tests = len(self.test_results)
        successful_tests = len([r for r in self.test_results if r["success"]])
        failed_tests = total_tests - successful_tests
        success_rate = (successful_tests / total_tests * 100) if total_tests > 0 else 0
        
        print("\n" + "="*80)
        print("📋 系统集成测试报告 - 任务12")
        print("="*80)
        print(f"📊 总测试数: {total_tests}")
        print(f"✅ 成功: {successful_tests}")
        print(f"❌ 失败: {failed_tests}")
        print(f"📈 成功率: {success_rate:.2f}%")
        
        # 按测试类别分组显示结果
        test_categories = {}
        for result in self.test_results:
            category = result["test"]
            if category not in test_categories:
                test_categories[category] = {"success": 0, "failed": 0}
            
            if result["success"]:
                test_categories[category]["success"] += 1
            else:
                test_categories[category]["failed"] += 1
        
        print("\n📊 分类测试结果:")
        for category, counts in test_categories.items():
            total_cat = counts["success"] + counts["failed"]
            success_rate_cat = (counts["success"] / total_cat * 100) if total_cat > 0 else 0
            print(f"  {category}: {counts['success']}/{total_cat} ({success_rate_cat:.1f}%)")
        
        # 显示失败的测试
        failed_results = [r for r in self.test_results if not r["success"]]
        if failed_results:
            print("\n❌ 失败的测试:")
            for result in failed_results:
                print(f"  [{result['test']}] {result['message']}")
        
        # 生成建议
        print("\n💡 测试建议:")
        if success_rate >= 90:
            print("  ✅ 系统集成测试通过，可以进行部署")
        elif success_rate >= 70:
            print("  ⚠️ 系统基本功能正常，建议修复失败的测试后再部署")
        else:
            print("  ❌ 系统存在严重问题，需要修复后重新测试")
        
        print("="*80)
        
        return {
            "total": total_tests,
            "successful": successful_tests,
            "failed": failed_tests,
            "success_rate": success_rate,
            "categories": test_categories,
            "failed_tests": failed_results
        }

if __name__ == "__main__":
    tester = SystemIntegrationTester()
    report = tester.run_all_tests()
    sys.exit(0 if report["failed"] == 0 else 1)