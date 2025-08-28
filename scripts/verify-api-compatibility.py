#!/usr/bin/env python3
"""
API兼容性验证脚本
验证后端API接口与前端TypeScript接口定义的兼容性
检查字段映射、数据类型、响应格式等
"""

import json
import requests
import sys
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional
import re

class ApiCompatibilityVerifier:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.test_results = []
        self.current_test = ""
        
        # 前端TypeScript接口定义（从types/sales.ts提取）
        self.frontend_interfaces = {
            "SalesRecord": {
                "required_fields": [
                    "id", "orderNumber", "customerId", "productId", "salesStaffId",
                    "salesAmount", "quantity", "unitPrice", "orderDate", "status",
                    "paymentStatus", "createdAt", "updatedAt", "customerName",
                    "productName", "staffName"
                ],
                "optional_fields": [
                    "discountAmount", "deliveryDate", "paymentMethod", "region",
                    "channel", "notes", "customerPhone"
                ],
                "field_types": {
                    "id": "number",
                    "salesAmount": "number",
                    "quantity": "number",
                    "unitPrice": "number",
                    "orderNumber": "string",
                    "customerName": "string",
                    "productName": "string",
                    "staffName": "string",
                    "status": "string",
                    "paymentStatus": "string"
                }
            },
            "SalesStats": {
                "required_fields": [
                    "totalSalesAmount", "totalOrders", "avgOrderAmount",
                    "totalQuantity", "newCustomers", "activeCustomers"
                ],
                "optional_fields": ["growthRate"],
                "field_types": {
                    "totalSalesAmount": "number",
                    "totalOrders": "number",
                    "avgOrderAmount": "number",
                    "totalQuantity": "number",
                    "newCustomers": "number",
                    "activeCustomers": "number"
                }
            },
            "SalesChartData": {
                "required_fields": ["categories", "series"],
                "field_types": {
                    "categories": "array",
                    "series": "array"
                }
            },
            "PageResponse": {
                "required_fields": ["list", "total", "page", "pageSize"],
                "optional_fields": ["totalPages", "isEmpty"],
                "field_types": {
                    "list": "array",
                    "total": "number",
                    "page": "number",
                    "pageSize": "number"
                }
            },
            "ApiResponse": {
                "required_fields": ["code", "message", "data"],
                "field_types": {
                    "code": "number",
                    "message": "string"
                }
            }
        }

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
        url = f"{self.base_url}{endpoint}"
        
        try:
            if method == "GET":
                response = requests.get(url, timeout=10)
            elif method == "POST":
                response = requests.post(url, json=data, timeout=10)
            elif method == "PUT":
                response = requests.put(url, json=data, timeout=10)
            elif method == "DELETE":
                response = requests.delete(url, timeout=10)
            else:
                raise ValueError(f"不支持的HTTP方法: {method}")
            
            return {
                "success": response.status_code < 400,
                "status_code": response.status_code,
                "data": response.json() if response.content else {},
                "url": url,
                "method": method
            }
        except requests.exceptions.RequestException as e:
            return {
                "success": False,
                "error": str(e),
                "url": url,
                "method": method
            }
        except json.JSONDecodeError as e:
            return {
                "success": False,
                "error": f"JSON解析错误: {str(e)}",
                "url": url,
                "method": method
            }

    def verify_field_presence(self, data: Dict, interface_name: str) -> bool:
        """验证字段存在性"""
        interface_def = self.frontend_interfaces.get(interface_name)
        if not interface_def:
            self.log_test(f"未找到接口定义: {interface_name}", False)
            return False

        required_fields = interface_def.get("required_fields", [])
        missing_fields = []
        
        for field in required_fields:
            if field not in data:
                missing_fields.append(field)
        
        if missing_fields:
            self.log_test(f"缺少必需字段: {', '.join(missing_fields)}", False)
            return False
        
        self.log_test(f"所有必需字段都存在 ({len(required_fields)}个)", True)
        return True

    def verify_field_types(self, data: Dict, interface_name: str) -> bool:
        """验证字段类型"""
        interface_def = self.frontend_interfaces.get(interface_name)
        if not interface_def:
            return False

        field_types = interface_def.get("field_types", {})
        type_errors = []
        
        for field, expected_type in field_types.items():
            if field not in data:
                continue
                
            value = data[field]
            actual_type = self.get_python_type(value)
            
            if not self.is_type_compatible(actual_type, expected_type):
                type_errors.append(f"{field}: 期望{expected_type}, 实际{actual_type}")
        
        if type_errors:
            self.log_test(f"字段类型错误: {'; '.join(type_errors)}", False)
            return False
        
        self.log_test(f"字段类型验证通过 ({len(field_types)}个字段)", True)
        return True

    def get_python_type(self, value: Any) -> str:
        """获取Python值的类型字符串"""
        if isinstance(value, bool):
            return "boolean"
        elif isinstance(value, int):
            return "number"
        elif isinstance(value, float):
            return "number"
        elif isinstance(value, str):
            return "string"
        elif isinstance(value, list):
            return "array"
        elif isinstance(value, dict):
            return "object"
        elif value is None:
            return "null"
        else:
            return str(type(value).__name__)

    def is_type_compatible(self, actual_type: str, expected_type: str) -> bool:
        """检查类型兼容性"""
        if actual_type == expected_type:
            return True
        
        # 数字类型兼容性
        if expected_type == "number" and actual_type in ["number", "int", "float"]:
            return True
        
        # 允许null值（可选字段）
        if actual_type == "null":
            return True
            
        return False

    def verify_camel_case_naming(self, data: Dict) -> bool:
        """验证camelCase命名规范"""
        snake_case_pattern = re.compile(r'.*_.*')
        snake_case_fields = []
        
        for key in data.keys():
            if snake_case_pattern.match(key):
                snake_case_fields.append(key)
        
        if snake_case_fields:
            self.log_test(f"发现snake_case字段: {', '.join(snake_case_fields)}", False)
            return False
        
        self.log_test("字段命名符合camelCase规范", True)
        return True

    def test_sales_records_api(self):
        """测试销售记录API"""
        self.current_test = "销售记录API"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        # 测试列表接口
        result = self.make_request("/api/sales/records?page=1&pageSize=5")
        
        if not result["success"]:
            self.log_test(f"API请求失败: {result.get('error', '未知错误')}", False)
            return False
        
        response_data = result["data"]
        
        # 验证API响应格式
        if not self.verify_field_presence(response_data, "ApiResponse"):
            return False
        
        if not self.verify_field_types(response_data, "ApiResponse"):
            return False
        
        # 验证分页数据格式
        page_data = response_data.get("data", {})
        if not self.verify_field_presence(page_data, "PageResponse"):
            return False
        
        if not self.verify_field_types(page_data, "PageResponse"):
            return False
        
        # 验证销售记录数据格式
        records = page_data.get("list", [])
        if records:
            record = records[0]
            if not self.verify_field_presence(record, "SalesRecord"):
                return False
            
            if not self.verify_field_types(record, "SalesRecord"):
                return False
            
            if not self.verify_camel_case_naming(record):
                return False
        
        self.log_test("销售记录API验证通过", True)
        return True

    def test_sales_stats_api(self):
        """测试销售统计API"""
        self.current_test = "销售统计API"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        result = self.make_request("/api/sales/stats")
        
        if not result["success"]:
            self.log_test(f"API请求失败: {result.get('error', '未知错误')}", False)
            return False
        
        response_data = result["data"]
        
        # 验证API响应格式
        if not self.verify_field_presence(response_data, "ApiResponse"):
            return False
        
        # 验证统计数据格式
        stats_data = response_data.get("data", {})
        if not self.verify_field_presence(stats_data, "SalesStats"):
            return False
        
        if not self.verify_field_types(stats_data, "SalesStats"):
            return False
        
        if not self.verify_camel_case_naming(stats_data):
            return False
        
        self.log_test("销售统计API验证通过", True)
        return True

    def test_sales_charts_api(self):
        """测试销售图表API"""
        self.current_test = "销售图表API"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        # 测试趋势图表
        result = self.make_request("/api/sales/charts/trends?groupBy=month")
        
        if not result["success"]:
            self.log_test(f"趋势图表API请求失败: {result.get('error', '未知错误')}", False)
            return False
        
        response_data = result["data"]
        
        # 验证API响应格式
        if not self.verify_field_presence(response_data, "ApiResponse"):
            return False
        
        # 验证图表数据格式
        chart_data = response_data.get("data", {})
        if not self.verify_field_presence(chart_data, "SalesChartData"):
            return False
        
        if not self.verify_field_types(chart_data, "SalesChartData"):
            return False
        
        # 验证series格式
        series = chart_data.get("series", [])
        if series:
            series_item = series[0]
            required_series_fields = ["name", "data"]
            for field in required_series_fields:
                if field not in series_item:
                    self.log_test(f"series缺少字段: {field}", False)
                    return False
        
        # 测试分布图表
        distribution_result = self.make_request("/api/sales/charts/distribution?type=product")
        if distribution_result["success"]:
            self.log_test("分布图表API正常", True)
        else:
            self.log_test("分布图表API失败", False)
        
        self.log_test("销售图表API验证通过", True)
        return True

    def test_error_handling(self):
        """测试错误处理"""
        self.current_test = "错误处理"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        # 测试无效ID
        result = self.make_request("/api/sales/records/999999")
        if result["status_code"] == 404:
            self.log_test("无效ID返回404状态码", True)
        else:
            self.log_test(f"无效ID应返回404，实际返回{result['status_code']}", False)
        
        # 测试无效参数
        result = self.make_request("/api/sales/records?page=-1")
        if result["status_code"] == 400:
            self.log_test("无效参数返回400状态码", True)
        else:
            self.log_test(f"无效参数应返回400，实际返回{result.get('status_code', 'ERROR')}", False)
        
        # 测试无效日期格式
        result = self.make_request("/api/sales/records?startDate=invalid-date")
        if result["status_code"] == 400:
            self.log_test("无效日期格式返回400状态码", True)
        else:
            self.log_test(f"无效日期格式应返回400，实际返回{result.get('status_code', 'ERROR')}", False)
        
        return True

    def test_field_mapping_consistency(self):
        """测试字段映射一致性"""
        self.current_test = "字段映射一致性"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        # 获取销售记录数据
        result = self.make_request("/api/sales/records?page=1&pageSize=1")
        
        if not result["success"]:
            self.log_test("无法获取测试数据", False)
            return False
        
        records = result["data"].get("data", {}).get("list", [])
        if not records:
            self.log_test("没有销售记录数据用于测试", False)
            return False
        
        record = records[0]
        
        # 检查关键字段映射
        field_mappings = {
            "customerName": "客户名称字段",
            "productName": "产品名称字段", 
            "staffName": "销售人员姓名字段",
            "salesAmount": "销售金额字段",
            "orderNumber": "订单号字段"
        }
        
        mapping_errors = []
        for field, description in field_mappings.items():
            if field not in record:
                mapping_errors.append(f"缺少{description}({field})")
            elif record[field] is None:
                mapping_errors.append(f"{description}({field})为null")
        
        if mapping_errors:
            self.log_test(f"字段映射错误: {'; '.join(mapping_errors)}", False)
            return False
        
        self.log_test("字段映射一致性验证通过", True)
        return True

    def test_performance_requirements(self):
        """测试性能要求"""
        self.current_test = "性能要求"
        print(f"\n🔍 开始测试: {self.current_test}")
        
        import time
        
        # 测试API响应时间
        start_time = time.time()
        result = self.make_request("/api/sales/records?page=1&pageSize=20")
        end_time = time.time()
        
        response_time = (end_time - start_time) * 1000  # 转换为毫秒
        
        if not result["success"]:
            self.log_test("性能测试API请求失败", False)
            return False
        
        # 响应时间要求：< 2秒
        if response_time < 2000:
            self.log_test(f"API响应时间: {response_time:.2f}ms (符合要求)", True)
        else:
            self.log_test(f"API响应时间: {response_time:.2f}ms (超过2秒限制)", False)
        
        # 测试图表API响应时间
        start_time = time.time()
        chart_result = self.make_request("/api/sales/charts/trends")
        end_time = time.time()
        
        chart_response_time = (end_time - start_time) * 1000
        
        if chart_result["success"]:
            if chart_response_time < 3000:
                self.log_test(f"图表API响应时间: {chart_response_time:.2f}ms (符合要求)", True)
            else:
                self.log_test(f"图表API响应时间: {chart_response_time:.2f}ms (超过3秒限制)", False)
        
        return True

    def generate_report(self):
        """生成测试报告"""
        total_tests = len(self.test_results)
        successful_tests = len([r for r in self.test_results if r["success"]])
        failed_tests = total_tests - successful_tests
        success_rate = (successful_tests / total_tests * 100) if total_tests > 0 else 0
        
        print("\n" + "="*60)
        print("📋 API兼容性验证报告")
        print("="*60)
        print(f"📊 总测试数: {total_tests}")
        print(f"✅ 成功: {successful_tests}")
        print(f"❌ 失败: {failed_tests}")
        print(f"📈 成功率: {success_rate:.2f}%")
        print(f"⏰ 测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        # 按测试分组显示结果
        test_groups = {}
        for result in self.test_results:
            test_name = result["test"]
            if test_name not in test_groups:
                test_groups[test_name] = []
            test_groups[test_name].append(result)
        
        print("\n📝 详细结果:")
        for test_name, results in test_groups.items():
            test_success = len([r for r in results if r["success"]])
            test_total = len(results)
            print(f"\n🎯 {test_name}: {test_success}/{test_total} 成功")
            
            for result in results:
                status = "✅" if result["success"] else "❌"
                print(f"  {status} {result['message']}")
        
        # 生成建议
        print("\n💡 改进建议:")
        if failed_tests == 0:
            print("  🎉 所有测试都通过了！API兼容性良好。")
        else:
            print("  🔧 请根据失败的测试项目修复API接口问题。")
            print("  📚 确保后端DTO字段与前端TypeScript接口完全匹配。")
            print("  🐪 检查字段命名是否符合camelCase规范。")
            print("  ⚡ 优化API响应时间，确保用户体验。")
        
        return {
            "total": total_tests,
            "successful": successful_tests,
            "failed": failed_tests,
            "success_rate": success_rate,
            "details": test_groups
        }

    def run_all_tests(self):
        """运行所有兼容性测试"""
        print("🚀 开始API兼容性验证")
        print(f"🌐 测试服务器: {self.base_url}")
        print("="*60)
        
        try:
            # 执行所有测试
            self.test_sales_records_api()
            self.test_sales_stats_api()
            self.test_sales_charts_api()
            self.test_error_handling()
            self.test_field_mapping_consistency()
            self.test_performance_requirements()
            
            # 生成报告
            report = self.generate_report()
            
            print("\n🏁 API兼容性验证完成!")
            return report
            
        except Exception as e:
            print(f"\n💥 测试执行过程中发生错误: {str(e)}")
            return self.generate_report()

def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description="API兼容性验证脚本")
    parser.add_argument("--url", default="http://localhost:8080", 
                       help="后端服务器URL (默认: http://localhost:8080)")
    
    args = parser.parse_args()
    
    verifier = ApiCompatibilityVerifier(args.url)
    report = verifier.run_all_tests()
    
    # 根据测试结果设置退出码
    exit_code = 0 if report["failed"] == 0 else 1
    sys.exit(exit_code)

if __name__ == "__main__":
    main()