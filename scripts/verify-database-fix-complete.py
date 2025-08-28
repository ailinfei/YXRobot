#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据库字段修复完整性验证脚本
验证所有必要的字段是否已正确添加，并检查数据完整性
"""

import mysql.connector
import json
import sys
from datetime import datetime

def verify_database_fix():
    """验证数据库修复的完整性"""
    
    # 数据库连接配置
    config = {
        'host': 'yun.finiot.cn',
        'port': 3306,
        'user': 'YXRobot',
        'password': '2200548qq',
        'database': 'YXRobot',
        'charset': 'utf8mb4'
    }
    
    print("🔍 开始验证数据库字段修复完整性")
    print("=" * 60)
    
    try:
        # 连接数据库
        connection = mysql.connector.connect(**config)
        cursor = connection.cursor(dictionary=True)
        
        print(f"✅ 数据库连接成功: {config['host']}:{config['port']}")
        
        # 验证结果
        verification_results = {
            'timestamp': datetime.now().isoformat(),
            'database_connection': True,
            'field_verification': {},
            'data_integrity': {},
            'api_readiness': {},
            'overall_status': 'unknown'
        }
        
        # 1. 验证sales_records表字段
        print("\n📋 1. 验证sales_records表字段...")
        required_sales_fields = [
            'shipping_address', 'shipping_fee', 'subtotal', 'total_amount', 
            'tracking_number', 'shipping_company'
        ]
        
        cursor.execute("""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT, COLUMN_DEFAULT
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'sales_records'
            ORDER BY ORDINAL_POSITION
        """)
        
        sales_columns = cursor.fetchall()
        existing_sales_fields = [col['COLUMN_NAME'] for col in sales_columns]
        
        sales_field_status = {}
        for field in required_sales_fields:
            exists = field in existing_sales_fields
            sales_field_status[field] = exists
            print(f"   {'✅' if exists else '❌'} {field}: {'存在' if exists else '缺失'}")
        
        verification_results['field_verification']['sales_records'] = sales_field_status
        
        # 2. 验证customers表phone字段
        print("\n📋 2. 验证customers表phone字段...")
        cursor.execute("""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'customers'
              AND COLUMN_NAME = 'phone'
        """)
        
        phone_field = cursor.fetchone()
        phone_exists = phone_field is not None
        verification_results['field_verification']['customers_phone'] = phone_exists
        print(f"   {'✅' if phone_exists else '❌'} phone字段: {'存在' if phone_exists else '缺失'}")
        
        # 3. 验证sales_products表product_image字段
        print("\n📋 3. 验证sales_products表product_image字段...")
        cursor.execute("""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'sales_products'
              AND COLUMN_NAME = 'product_image'
        """)
        
        image_field = cursor.fetchone()
        image_exists = image_field is not None
        verification_results['field_verification']['products_image'] = image_exists
        print(f"   {'✅' if image_exists else '❌'} product_image字段: {'存在' if image_exists else '缺失'}")
        
        # 4. 验证基础数据完整性
        print("\n📋 4. 验证基础数据完整性...")
        
        # 检查客户数据
        cursor.execute("SELECT COUNT(*) as count FROM customers WHERE is_deleted = 0")
        customer_count = cursor.fetchone()['count']
        
        # 检查产品数据
        cursor.execute("SELECT COUNT(*) as count FROM sales_products WHERE is_deleted = 0")
        product_count = cursor.fetchone()['count']
        
        # 检查销售人员数据
        cursor.execute("SELECT COUNT(*) as count FROM sales_staff WHERE is_deleted = 0")
        staff_count = cursor.fetchone()['count']
        
        # 检查销售记录数据
        cursor.execute("SELECT COUNT(*) as count FROM sales_records WHERE is_deleted = 0")
        sales_count = cursor.fetchone()['count']
        
        data_counts = {
            'customers': customer_count,
            'products': product_count,
            'staff': staff_count,
            'sales_records': sales_count
        }
        
        verification_results['data_integrity'] = data_counts
        
        print(f"   📊 数据统计:")
        print(f"      - 客户数量: {customer_count}")
        print(f"      - 产品数量: {product_count}")
        print(f"      - 销售人员数量: {staff_count}")
        print(f"      - 销售记录数量: {sales_count}")
        
        # 5. 验证关联查询能力
        print("\n📋 5. 验证关联查询能力...")
        
        try:
            cursor.execute("""
                SELECT 
                    sr.id,
                    sr.order_number,
                    c.customer_name,
                    c.phone as customer_phone,
                    p.product_name,
                    ss.staff_name,
                    sr.sales_amount,
                    sr.order_date
                FROM sales_records sr
                LEFT JOIN customers c ON sr.customer_id = c.id
                LEFT JOIN sales_products p ON sr.product_id = p.id
                LEFT JOIN sales_staff ss ON sr.sales_staff_id = ss.id
                WHERE sr.is_deleted = 0
                LIMIT 1
            """)
            
            sample_record = cursor.fetchone()
            join_query_success = True
            
            if sample_record:
                print("   ✅ 关联查询成功，示例记录:")
                for key, value in sample_record.items():
                    print(f"      {key}: {value}")
            else:
                print("   ✅ 关联查询语法正确，但暂无销售记录数据")
                
        except Exception as e:
            join_query_success = False
            print(f"   ❌ 关联查询失败: {e}")
        
        verification_results['api_readiness']['join_query'] = join_query_success
        
        # 6. 验证字段映射兼容性
        print("\n📋 6. 验证字段映射兼容性...")
        
        # 检查关键字段是否存在于各表中
        field_mapping_checks = {
            'customers.customer_name': check_field_exists(cursor, 'customers', 'customer_name'),
            'customers.phone': check_field_exists(cursor, 'customers', 'phone'),
            'sales_products.product_name': check_field_exists(cursor, 'sales_products', 'product_name'),
            'sales_staff.staff_name': check_field_exists(cursor, 'sales_staff', 'staff_name'),
            'sales_records.order_number': check_field_exists(cursor, 'sales_records', 'order_number'),
            'sales_records.sales_amount': check_field_exists(cursor, 'sales_records', 'sales_amount'),
            'sales_records.total_amount': check_field_exists(cursor, 'sales_records', 'total_amount')
        }
        
        verification_results['api_readiness']['field_mapping'] = field_mapping_checks
        
        mapping_success_count = sum(field_mapping_checks.values())
        mapping_total_count = len(field_mapping_checks)
        
        print(f"   📊 字段映射检查: {mapping_success_count}/{mapping_total_count}")
        for field, exists in field_mapping_checks.items():
            print(f"      {'✅' if exists else '❌'} {field}")
        
        # 7. 计算总体状态
        all_sales_fields_exist = all(sales_field_status.values())
        basic_data_sufficient = customer_count > 0 and product_count > 0 and staff_count > 0
        mapping_complete = mapping_success_count == mapping_total_count
        
        if all_sales_fields_exist and phone_exists and image_exists and join_query_success and mapping_complete:
            overall_status = 'success'
            status_message = '✅ 数据库字段修复完全成功'
        elif all_sales_fields_exist and phone_exists and mapping_complete:
            overall_status = 'mostly_success'
            status_message = '⚠️ 数据库字段修复基本成功，有少量非关键问题'
        else:
            overall_status = 'partial_success'
            status_message = '❌ 数据库字段修复部分成功，需要进一步修复'
        
        verification_results['overall_status'] = overall_status
        
        # 8. 输出验证报告
        print("\n" + "=" * 60)
        print("📊 验证报告总结")
        print("=" * 60)
        print(status_message)
        print(f"\n🔍 详细状态:")
        print(f"   - sales_records新字段: {'✅' if all_sales_fields_exist else '❌'}")
        print(f"   - customers.phone字段: {'✅' if phone_exists else '❌'}")
        print(f"   - products.product_image字段: {'✅' if image_exists else '❌'}")
        print(f"   - 关联查询能力: {'✅' if join_query_success else '❌'}")
        print(f"   - 字段映射完整性: {'✅' if mapping_complete else '❌'}")
        print(f"   - 基础数据充足: {'✅' if basic_data_sufficient else '❌'}")
        
        # 9. 生成建议
        print(f"\n💡 建议:")
        if overall_status == 'success':
            print("   1. ✅ 数据库修复完成，可以开始测试API接口")
            print("   2. ✅ 可以启动后端服务并测试前端页面")
            print("   3. ✅ 建议运行API测试验证数据流")
        elif overall_status == 'mostly_success':
            print("   1. ⚠️ 主要字段修复完成，可以进行基础测试")
            print("   2. ⚠️ 建议修复剩余的非关键问题")
            print("   3. ✅ 可以开始API接口测试")
        else:
            print("   1. ❌ 需要重新运行数据库修复脚本")
            print("   2. ❌ 检查数据库连接和权限")
            print("   3. ❌ 修复缺失的关键字段")
        
        # 10. 保存验证结果到文件
        report_file = 'database-fix-verification-report.json'
        with open(report_file, 'w', encoding='utf-8') as f:
            json.dump(verification_results, f, indent=2, ensure_ascii=False)
        
        print(f"\n📄 详细验证报告已保存到: {report_file}")
        
        return overall_status == 'success' or overall_status == 'mostly_success'
        
    except mysql.connector.Error as e:
        print(f"❌ 数据库连接失败: {e}")
        return False
        
    except Exception as e:
        print(f"❌ 验证过程失败: {e}")
        return False
        
    finally:
        if 'connection' in locals() and connection.is_connected():
            cursor.close()
            connection.close()
            print("\n📡 数据库连接已关闭")

def check_field_exists(cursor, table_name, field_name):
    """检查指定表中是否存在指定字段"""
    try:
        cursor.execute("""
            SELECT COUNT(*) as count
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = %s
              AND COLUMN_NAME = %s
        """, (table_name, field_name))
        
        result = cursor.fetchone()
        return result['count'] > 0
    except:
        return False

def main():
    """主函数"""
    print("🔍 数据库字段修复完整性验证工具")
    print("验证所有必要的字段是否已正确添加")
    print("=" * 60)
    
    success = verify_database_fix()
    
    if success:
        print("\n🎉 验证完成！数据库修复状态良好")
        print("📋 接下来可以:")
        print("   1. 启动后端服务 (YXRobotApplication)")
        print("   2. 打开测试页面验证API接口")
        print("   3. 访问前端销售管理页面测试功能")
        print("   4. 验证前后端数据流是否正常")
        return 0
    else:
        print("\n❌ 验证失败！需要进一步修复")
        print("📋 建议:")
        print("   1. 检查数据库连接")
        print("   2. 重新运行数据库修复脚本")
        print("   3. 检查错误日志")
        return 1

if __name__ == "__main__":
    sys.exit(main())