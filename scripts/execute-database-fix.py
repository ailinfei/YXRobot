#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
销售数据库结构修复执行脚本
用于修复前端页面需求与数据库设计不匹配的问题
"""

import mysql.connector
import os
import sys
from pathlib import Path

def execute_database_fix():
    """执行数据库结构修复"""
    
    # 数据库连接配置 - 云数据库
    config = {
        'host': 'yun.finiot.cn',
        'port': 3306,
        'user': 'YXRobot',
        'password': '2200548qq',
        'database': 'YXRobot',
        'charset': 'utf8mb4',
        'autocommit': True
    }
    
    # SQL文件路径
    current_dir = Path(__file__).parent
    sql_file = current_dir / 'fix-sales-database-structure.sql'
    
    print("🚀 开始执行销售数据库结构修复")
    print("=" * 50)
    
    try:
        # 连接数据库
        print("📡 连接数据库...")
        connection = mysql.connector.connect(**config)
        cursor = connection.cursor()
        
        print(f"✅ 数据库连接成功: {config['host']}:{config['port']}")
        
        # 读取SQL文件
        print(f"📖 读取SQL文件: {sql_file}")
        if not sql_file.exists():
            raise FileNotFoundError(f"SQL文件不存在: {sql_file}")
            
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql_content = f.read()
        
        # 分割SQL语句（按分号分割，但要处理存储过程中的分号）
        sql_statements = []
        current_statement = ""
        in_delimiter = False
        
        for line in sql_content.split('\n'):
            line = line.strip()
            
            # 跳过注释和空行
            if not line or line.startswith('--'):
                continue
                
            # 处理DELIMITER
            if line.startswith('DELIMITER'):
                in_delimiter = True
                continue
            elif line == 'DELIMITER ;':
                in_delimiter = False
                if current_statement.strip():
                    sql_statements.append(current_statement.strip())
                    current_statement = ""
                continue
            
            current_statement += line + "\n"
            
            # 如果不在存储过程中，遇到分号就分割
            if not in_delimiter and line.endswith(';'):
                if current_statement.strip():
                    sql_statements.append(current_statement.strip())
                    current_statement = ""
        
        # 添加最后一个语句
        if current_statement.strip():
            sql_statements.append(current_statement.strip())
        
        print(f"📝 解析到 {len(sql_statements)} 条SQL语句")
        
        # 执行SQL语句
        success_count = 0
        error_count = 0
        
        for i, statement in enumerate(sql_statements, 1):
            try:
                if statement.strip():
                    print(f"⚡ 执行语句 {i}/{len(sql_statements)}")
                    
                    # 对于多语句，需要分别执行
                    if 'SELECT' in statement.upper() and 'INTO @' in statement.upper():
                        # 处理变量设置语句
                        for sub_stmt in statement.split(';'):
                            sub_stmt = sub_stmt.strip()
                            if sub_stmt:
                                cursor.execute(sub_stmt)
                    else:
                        cursor.execute(statement)
                    
                    success_count += 1
                    
            except mysql.connector.Error as e:
                error_count += 1
                print(f"❌ 语句执行失败 {i}: {e}")
                print(f"   SQL: {statement[:100]}...")
                
                # 对于某些可以忽略的错误，继续执行
                if "Duplicate column name" in str(e) or "already exists" in str(e):
                    print("   ⚠️  字段已存在，跳过...")
                    continue
                elif "Unknown table" in str(e):
                    print("   ⚠️  表不存在，跳过...")
                    continue
        
        print("\n" + "=" * 50)
        print(f"✅ 数据库结构修复完成!")
        print(f"📊 执行统计:")
        print(f"   - 成功: {success_count} 条")
        print(f"   - 失败: {error_count} 条")
        
        # 验证修复结果
        print("\n🔍 验证修复结果...")
        verify_fix(cursor)
        
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
    
    return True

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
        
        # 检查视图
        print("\n📋 检查销售记录扩展视图...")
        cursor.execute("""
            SELECT COUNT(*) as view_exists
            FROM INFORMATION_SCHEMA.VIEWS 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'sales_records_extended'
        """)
        
        view_result = cursor.fetchone()
        if view_result and view_result[0] > 0:
            print("✅ sales_records_extended 视图创建成功")
        else:
            print("⚠️  sales_records_extended 视图未创建")
        
        # 检查订单商品明细表
        print("\n📋 检查订单商品明细表...")
        cursor.execute("""
            SELECT COUNT(*) as table_exists
            FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_SCHEMA = 'YXRobot' 
              AND TABLE_NAME = 'sales_order_items'
        """)
        
        table_result = cursor.fetchone()
        if table_result and table_result[0] > 0:
            print("✅ sales_order_items 表创建成功")
        else:
            print("⚠️  sales_order_items 表未创建")
            
        print("\n🎉 数据库结构修复验证完成!")
        
    except Exception as e:
        print(f"❌ 验证过程出错: {e}")

def main():
    """主函数"""
    print("🚀 销售数据库结构修复工具")
    print("用于修复前端页面需求与数据库设计不匹配的问题")
    print("=" * 60)
    
    # 执行修复
    success = execute_database_fix()
    
    if success:
        print("\n✅ 修复完成! 接下来需要:")
        print("1. 更新后端Entity类以匹配新的数据库结构")
        print("2. 修改DTO类以支持前端需要的数据格式") 
        print("3. 调整Service层逻辑以处理新字段")
        print("4. 更新Mapper XML文件")
        print("5. 测试前后端数据流")
        return 0
    else:
        print("\n❌ 修复失败，请检查错误信息")
        return 1

if __name__ == "__main__":
    sys.exit(main())