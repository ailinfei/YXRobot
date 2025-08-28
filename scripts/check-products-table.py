#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import sys

def check_products_table():
    """检查products表结构"""
    try:
        # 数据库连接配置
        connection = pymysql.connect(
            host='yun.finiot.cn',
            port=3306,
            user='YXRobot',
            password='2200548qq',
            database='YXRobot',
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        
        print("✅ 数据库连接成功")
        
        with connection.cursor() as cursor:
            # 检查products表是否存在
            cursor.execute("SHOW TABLES LIKE 'products'")
            table_exists = cursor.fetchone()
            
            if table_exists:
                print("✅ products表存在")
                
                # 获取表结构
                cursor.execute("DESCRIBE products")
                columns = cursor.fetchall()
                
                print("\n📋 products表结构:")
                print("-" * 80)
                print(f"{'字段名':<20} {'类型':<20} {'是否为空':<10} {'键':<10} {'默认值':<15} {'额外':<15}")
                print("-" * 80)
                
                for column in columns:
                    field = column['Field']
                    type_info = column['Type']
                    null = column['Null']
                    key = column['Key']
                    default = str(column['Default']) if column['Default'] is not None else 'NULL'
                    extra = column['Extra']
                    
                    print(f"{field:<20} {type_info:<20} {null:<10} {key:<10} {default:<15} {extra:<15}")
                
                print("-" * 80)
                
                # 获取表的创建语句
                cursor.execute("SHOW CREATE TABLE products")
                create_table = cursor.fetchone()
                print("\n📝 表创建语句:")
                print(create_table['Create Table'])
                
            else:
                print("❌ products表不存在")
                
                # 查看所有表
                cursor.execute("SHOW TABLES")
                tables = cursor.fetchall()
                print("\n📋 数据库中的所有表:")
                for table in tables:
                    table_name = list(table.values())[0]
                    print(f"  - {table_name}")
        
        connection.close()
        print("\n✅ 数据库连接已关闭")
        
    except Exception as e:
        print(f"❌ 数据库连接失败: {e}")
        return False
    
    return True

if __name__ == "__main__":
    check_products_table()