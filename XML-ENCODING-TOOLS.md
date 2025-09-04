# XML编码修复工具使用指南

## 工具概述

本项目提供了一套完整的XML编码问题检测和修复工具，用于解决MyBatis映射文件的编码问题。

## 可用脚本

### 1. fix-encoding.bat - 完整修复工具
**功能**: 全面的XML编码检测、备份、修复和验证
**使用场景**: 首次发现编码问题或需要完整修复流程时使用

**执行步骤**:
1. 🔍 检测XML编码问题
2. 📦 备份现有XML文件
3. 🔧 修复编码问题
4. 🧪 验证修复结果
5. 🏗️ 测试编译

**使用方法**:
```bash
fix-encoding.bat
```

### 2. quick-fix-encoding.bat - 快速修复工具
**功能**: 快速修复XML编码问题，跳过检测和验证步骤
**使用场景**: 已知存在编码问题，需要快速修复时使用

**使用方法**:
```bash
quick-fix-encoding.bat
```

### 3. verify-encoding.bat - 验证工具
**功能**: 检查XML文件编码状态和编译情况
**使用场景**: 修复后验证或定期检查编码状态

**使用方法**:
```bash
verify-encoding.bat
```

### 4. restore-from-backup.bat - 恢复工具
**功能**: 从备份恢复XML文件
**使用场景**: 修复出现问题时回滚到原始状态

**使用方法**:
```bash
restore-from-backup.bat
```

## Java工具类

### 扫描和检测
- `XmlEncodingScanner` - 扫描XML文件编码问题
- `ComprehensiveXmlChecker` - 综合检查XML文件状态

### 备份和恢复
- `XmlBackupService` - XML文件备份服务
- `XmlBackupRunner` - 备份执行器

### 修复工具
- `XmlFinalFixer` - 最终修复工具（推荐）
- `XmlFinalFixRunner` - 最终修复执行器
- `XmlEncodingFixerV2` - 改进的编码修复工具
- `XmlReplacementCharFixer` - 替换字符修复工具

### 验证工具
- `XmlValidationTest` - MyBatis XML验证测试
- `XmlEncodingValidator` - 编码验证器

## 使用建议

### 首次使用
1. 运行 `fix-encoding.bat` 进行完整修复
2. 检查修复报告
3. 测试应用启动: `mvn spring-boot:run`

### 日常维护
1. 定期运行 `verify-encoding.bat` 检查状态
2. 发现问题时运行 `quick-fix-encoding.bat` 快速修复

### 问题排查
1. 如果修复失败，检查备份文件是否存在
2. 如果编译失败，运行 `restore-from-backup.bat` 恢复后重新修复
3. 查看详细日志了解具体错误信息

## 备份策略

- 所有修复操作都会自动创建备份
- 备份文件保存在 `xml-backup/` 目录
- 备份文件使用时间戳命名，确保唯一性
- 建议定期清理旧的备份文件

## 技术细节

### 支持的编码问题
- UTF-8声明与实际编码不匹配
- 中文字符替换字符（�）问题
- BOM标记问题
- XML语法错误

### 修复策略
1. **安全优先**: 修复前必须备份
2. **分步处理**: 先恢复编码，再处理字符问题
3. **验证机制**: 每步都进行验证确保正确性

## 故障排除

### 常见问题

**Q: 脚本运行失败，提示找不到类**
A: 确保项目已编译，运行 `mvn compile` 后重试

**Q: 修复后仍有编码问题**
A: 运行 `verify-encoding.bat` 检查具体问题，可能需要手动处理特殊情况

**Q: 备份文件丢失**
A: 检查 `xml-backup/` 目录，如果确实丢失，需要从版本控制系统恢复

**Q: 应用启动失败**
A: 运行 `restore-from-backup.bat` 恢复原始文件，然后重新分析问题

### 联系支持
如遇到无法解决的问题，请提供：
1. 错误日志
2. 修复报告
3. 具体的XML文件内容

---
**工具版本**: v2.0  
**最后更新**: 2025年9月1日  
**兼容性**: Spring Boot 2.7+, MyBatis 3.5+