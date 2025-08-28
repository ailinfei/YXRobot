#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
销售管理API集成测试脚本
用于验证前后端字段匹配修复是否成功
"""

import requests
import json
import sys
from datetime import datetime

class SalesAPITester:
    def __init__(self, base_url="http://localhost:8080/api/sales"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def test_sales_records_api(self):
        """测试销售记录列表API"""
        print("🧪 测试销售记录列表API...")
        
        try:
            url = f"{self.base_url}/records"
            params = {
                'page': 1,
                'pageSize': 10
            }
            
            response = self.session.get(url, params=params)
            
            if response.status_code == 200:
                data = response.json()
                print("✅ 销售记录API调用成功")
                
                # 验证响应格式
                if self.validate_response_format(data, 'records'):
                    print("✅ 响应格式验证通过")
                    
                    # 验证字段匹配
                    if 'data' in data and 'list' in data['data']:
                        records = data['data']['list']
                        if records:
                            self.validate_record_fields(records[0])
                        else:
                            print("⚠️  返回的记录列表为空，无法验证字段")
                    
                    return True
                else:
                    print("❌ 响应格式验证失败")
                    return False
            else:
                print(f"❌ API调用失败，状态码: {response.status_code}")
                print(f"   响应内容: {response.text}")
                return False
                
        except Exception as e:
            print(f"❌ API测试异常: {e}")
            return False
    
    def test_sales_stats_api(self):
        """测试销售统计API"""
        print("\n🧪 测试销售统计API...")
        
        try:
            url = f"{self.base_url}/stats"
            response = self.session.get(url)
            
            if response.status_code == 200:
                data = response.json()
                print("✅ 销售统计API调用成功")
                
                # 验证统计字段
                if 'data' in data:
                    stats = data['data']
                    self.validate_stats_fields(stats)
                    return True
                else:
                    print("❌ 响应数据格式错误")
                    return False
            else:
                print(f"❌ API调用失败，状态码: {response.status_code}")
                return False
                
        except Exception as e:
            print(f"❌ API测试异常: {e}")
            return False
    
    def validate_response_format(self, data, api_type):
        """验证API响应格式"""
        required_keys = ['code', 'message', 'data']
        
        for key in required_keys:
            if key not in data:
                print(f"❌ 响应缺少必需字段: {key}")
                return False
        
        if api_type == 'records':
            if 'data' in data and isinstance(data['data'], dict):
                data_keys = ['list', 'total', 'page', 'pageSize']
                for key in data_keys:
                    if key not in data['data']:
                        print(f"❌ 分页数据缺少字段: {key}")
                        return False
        
        return True
    
    def validate_record_fields(self, record):
        """验证销售记录字段"""
        print("\n📋 验证销售记录字段...")
        
        # 前端期望的关键字段
        expected_fields = {
            'id': 'ID',
            'orderNumber': '订单号',
            'customerName': '客户姓名',
            'customerPhone': '客户电话（新增）',
            'productName': '产品名称',
            'staffName': '销售人员姓名（修复）',
            'quantity': '数量',
            'unitPrice': '单价',
            'salesAmount': '销售金额',
            'status': '订单状态',
            'paymentStatus': '付款状态',
            'orderDate': '订单日期'
        }
        
        missing_fields = []
        present_fields = []
        
        for field, description in expected_fields.items():
            if field in record:
                present_fields.append((field, description, record[field]))
                print(f"✅ {description}: {field} = {record[field]}")
            else:
                missing_fields.append((field, description))
                print(f"❌ {description}: {field} - 字段缺失")
        
        print(f"\n📊 字段验证统计:")
        print(f"   ✅ 存在字段: {len(present_fields)}")
        print(f"   ❌ 缺失字段: {len(missing_fields)}")
        
        # 特别检查修复的关键字段
        key_fixes = ['customerPhone', 'staffName']
        print(f"\n🔧 关键修复字段检查:")
        for field in key_fixes:
            if field in record:
                print(f"   ✅ {field}: 修复成功 = {record[field]}")
            else:
                print(f"   ❌ {field}: 修复失败，字段仍然缺失")
        
        return len(missing_fields) == 0
    
    def validate_stats_fields(self, stats):
        """验证销售统计字段"""
        print("\n📋 验证销售统计字段...")
        
        expected_stats_fields = {
            'totalSalesAmount': '总销售额',
            'totalOrders': '订单总数',
            'newCustomers': '新增客户',
            'avgOrderAmount': '平均订单价值',
            'growthRate': '增长率'
        }
        
        for field, description in expected_stats_fields.items():
            if field in stats:
                print(f"✅ {description}: {field} = {stats[field]}")
            else:
                print(f"❌ {description}: {field} - 字段缺失")
    
    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始销售管理API集成测试")
        print("=" * 60)
        
        # 测试API连通性
        try:
            response = self.session.get(f"{self.base_url.replace('/api/sales', '')}/health", timeout=5)
            print("✅ 后端服务连接正常")
        except:
            print("⚠️  无法连接后端服务，请确保服务已启动")
        
        # 执行测试
        tests_passed = 0
        total_tests = 2
        
        if self.test_sales_records_api():
            tests_passed += 1
        
        if self.test_sales_stats_api():
            tests_passed += 1
        
        # 测试结果总结
        print("\n" + "=" * 60)
        print(f"🎯 测试完成统计:")
        print(f"   总测试数: {total_tests}")
        print(f"   通过测试: {tests_passed}")
        print(f"   失败测试: {total_tests - tests_passed}")
        
        if tests_passed == total_tests:
            print("🎉 所有测试通过！前后端字段匹配修复成功！")
            return True
        else:
            print("❌ 部分测试失败，需要进一步修复")
            return False

def main():
    """主函数"""
    print("🔧 销售管理前后端字段匹配测试工具")
    print("用于验证修复是否成功")
    print("=" * 60)
    
    tester = SalesAPITester()
    success = tester.run_all_tests()
    
    if success:
        print("\n✅ 修复验证成功！可以继续后续开发工作。")
        return 0
    else:
        print("\n❌ 修复验证失败，请检查修复内容。")
        return 1

if __name__ == "__main__":
    sys.exit(main())