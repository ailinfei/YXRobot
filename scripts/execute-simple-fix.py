#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简化的数据库字段修复执行脚本
"""

import mysql.connector
import os
import sys
from pathlib import Path

def execute_simple_fix():
    """执行简化的数据库字段修复"""
    
    # 数据库连接配置
    config = {
        'host': 'yun.finiot.cn',
        'port': 3306,
        'user': 'YXRobot',
        'password': '2200548qq',
        'database': 'YXRobot',
        'charset': 'utf8mb4',
        'autocommit': True
    }
    
    print("🚀 开始执行简化数据库字段修复")
    print("=" * 50)
    
    try:
        # 连接数据库
        print("📡 连接数据库...")
        connection = mysql.connector.connect(**config)
        cursor = connection.cursor()
        
        print(f"✅ 数据库连接成功: {config['host']}:{config['port']}")
        
        # 执行修复SQL
        sql_statements = [
            # 1. 添加customers表phone字段
            """
            ALTER TABLE customers 
            ADD COLUMN phone VARCHAR(20) COMMENT '联系电话' AFTER contact_person
            """,
            
            # 2. 添加sales_records表字段
            """
            ALTER TABLE sales_records 
            ADD COLUMN shipping_address VARCHAR(500) COMMENT '收货地址' AFTER notes
            """,
            """
            ALTER TABLE sales_records 
            ADD COLUMN shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费' AFTER shipping_address
            """,
            """
            ALTER TABLE sales_records 
            ADD COLUMN subtotal DECIMAL(10,2) COMMENT '商品小计金额' AFTER shipping_fee
            """,
            """
            ALTER TABLE sales_records 
            ADD COLUMN total_amount DECIMAL(10,2) COMMENT '订单总金额（含运费）' AFTER subtotal
            """,
            """
            ALTER TABLE sales_records 
            ADD COLUMN tracking_number VARCHAR(100) COMMENT '快递单号' AFTER total_amount
            """,
            """
            ALTER TABLE sales_records 
            ADD COLUMN shipping_company VARCHAR(100) COMMENT '快递公司' AFTER tracking_number
            """,
            
            # 3. 添加产品图片字段
            """
            ALTER TABLE sales_products 
            ADD COLUMN product_image VARCHAR(500) COMMENT '产品图片URL' AFTER description
            """,
            
            # 4. 更新现有数据
            """
            UPDATE sales_records 
            SET 
              subtotal = COALESCE(sales_amount - COALESCE(discount_amount, 0), sales_amount),
              total_amount = COALESCE(sales_amount + COALESCE(shipping_fee, 0), sales_amount)
            WHERE subtotal IS NULL OR total_amount IS NULL
            """,
            
            # 5. 添加索引
            """
            CREATE INDEX idx_sales_records_total_amount ON sales_records (total_amount)
            """,
        ]
        
        # 插入基础数据的SQL
        insert_statements = [
            """
            INSERT IGNORE INTO customers (customer_name, customer_type, phone, email, address, region, industry, credit_level) VALUES
            ('测试客户A', 'individual', '13800138001', 'test1@example.com', '北京市朝阳区测试街道1号', '北京', '教育', 'A'),
            ('测试企业B', 'enterprise', '13800138002', 'test2@example.com', '上海市浦东新区测试路2号', '上海', '科技', 'B'),
            ('练字教育机构', 'enterprise', '13800138003', 'edu@example.com', '广州市天河区教育路3号', '广州', '教育', 'A')
            """,
            """
            INSERT IGNORE INTO sales_products (product_name, product_code, category, brand, model, unit_price, cost_price, stock_quantity, unit, description) VALUES
            ('家用练字机器人', 'ROBOT001', '教育设备', '练字科技', '标准版', 2999.00, 2000.00, 100, '台', '智能练字辅导机器人'),
            ('商用练字机器人', 'ROBOT002', '教育设备', '练字科技', '专业版', 4999.00, 3500.00, 50, '台', '商用级练字辅导机器人'),
            ('练字笔芯套装', 'PEN001', '配件', '练字科技', '通用型', 99.00, 50.00, 500, '套', '专用练字笔芯，10支装')
            """,
            """
            INSERT IGNORE INTO sales_staff (staff_name, staff_code, department, position, phone, email, sales_target, commission_rate) VALUES
            ('张销售', 'SALES001', '销售部', '销售经理', '13900139001', 'zhang@example.com', 100000.00, 0.05),
            ('李销售', 'SALES002', '销售部', '销售专员', '13900139002', 'li@example.com', 80000.00, 0.04),
            ('王销售', 'SALES003', '销售部', '高级销售', '13900139003', 'wang@example.com', 120000.00, 0.06)
            """
        ]
        
        success_count = 0
        error_count = 0
        
        # 执行结构修改SQL
        print("📝 执行数据库结构修改...")
        for i, statement in enumerate(sql_statements, 1):
            try:
                print(f"⚡ 执行语句 {i}/{len(sql_statements)}")
                cursor.execute(statement)
                success_count += 1
                print(f"   ✅ 成功")
                
            except mysql.connector.Error as e:
                error_count += 1
                if "Duplicate column name" in str(e) or "already exists" in str(e):
                    print(f"   ⚠️  字段已存在，跳过")
                    success_count += 1  # 算作成功
                else:
                    print(f"   ❌ 失败: {e}")
        
        # 执行数据插入SQL
        print("\n📝 插入基础数据...")
        for i, statement in enumerate(insert_statements, 1):
            try:
                print(f"⚡ 插入数据 {i}/{len(insert_statements)}")
                cursor.execute(statement)
                print(f"   ✅ 成功")
                
            except mysql.connector.Error as e:
                print(f"   ❌ 失败: {e}")
        
        print("\n" + "=" * 50)
        print(f"✅ 数据库字段修复完成!")
        print(f"📊 执行统计:")
        print(f"   - 成功: {success_count} 条")
        print(f"   - 失败: {error_count} 条")
        
        # 验证修复结果
        print("\n🔍 验证修复结果...")
        verify_fix(cursor)
        
        return True
        
    except mysql.connector.Error as e:
        print(f"❌ 数据库连接失败: {e}")
        return False
        
    except Exception as e:
        print(f"❌ 执行失败: {e}")
        return False
        
    finally:
        if 'connection' in locals() and connection.is_connected():
            cursor.close()
            connection.close()
            print("📡 数据库连接已关闭")

def verify_fix(cursor):
    """验证修复结果"""
    
    try:
        # 检查新增字段
        print("📋 检查sales_records表新增字段...")
        cursor.execute("""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'sales_records'
              AND COLUMN_NAME IN ('shipping_address', 'shipping_fee', 'subtotal', 'total_amount', 'tracking_number', 'shipping_company')
            ORDER BY ORDINAL_POSITION
        """)
        
        new_fields = cursor.fetchall()
        if new_fields:
            print("✅ 新增字段验证成功:")
            for field in new_fields:
                print(f"   - {field[0]}: {field[1]} ({field[3]})")
        else:
            print("⚠️  未找到新增字段")
        
        # 检查customers表phone字段
        print("\n📋 检查customers表phone字段...")
        cursor.execute("""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'customers'
              AND COLUMN_NAME = 'phone'
        """)
        
        phone_field = cursor.fetchone()
        if phone_field:
            print(f"✅ phone字段验证成功: {phone_field[0]} ({phone_field[3]})")
        else:
            print("⚠️  phone字段未找到")
        
        # 检查基础数据
        print("\n📋 检查基础数据...")
        cursor.execute("SELECT COUNT(*) FROM customers")
        customer_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM sales_products")
        product_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM sales_staff")
        staff_count = cursor.fetchone()[0]
        
        print(f"✅ 基础数据统计:")
        print(f"   - 客户数量: {customer_count}")
        print(f"   - 产品数量: {product_count}")
        print(f"   - 销售人员数量: {staff_count}")
        
        print("\n🎉 数据库字段修复验证完成!")
        
    except Exception as e:
        print(f"❌ 验证过程出错: {e}")

def main():
    """主函数"""
    print("🚀 简化数据库字段修复工具")
    print("=" * 50)
    
    success = execute_simple_fix()
    
    if success:
        print("\n✅ 修复完成! 数据库字段已更新")
        print("🎯 接下来可以:")
        print("1. 测试后端API接口")
        print("2. 验证前端页面数据显示")
        print("3. 检查字段映射是否正确")
        return 0
    else:
        print("\n❌ 修复失败，请检查错误信息")
        return 1

if __name__ == "__main__":
    sys.exit(main())