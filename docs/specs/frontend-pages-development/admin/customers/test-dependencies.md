# 客户管理模块单元测试依赖配置

## Maven依赖配置

在项目的`pom.xml`中需要添加以下测试依赖：

```xml
<dependencies>
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- MyBatis Test -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database for Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers (可选，用于集成测试) -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Maven插件配置

```xml
<build>
    <plugins>
        <!-- Surefire Plugin for Unit Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M9</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                <excludes>
                    <exclude>**/*IntegrationTest.java</exclude>
                </excludes>
                <systemPropertyVariables>
                    <spring.profiles.active>test</spring.profiles.active>
                </systemPropertyVariables>
                <argLine>-Xmx1024m -XX:MaxPermSize=256m</argLine>
            </configuration>
        </plugin>
        
        <!-- Failsafe Plugin for Integration Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>3.0.0-M9</version>
            <configuration>
                <includes>
                    <include>**/*IntegrationTest.java</include>
                    <include>**/*IT.java</include>
                </includes>
                <systemPropertyVariables>
                    <spring.profiles.active>test</spring.profiles.active>
                </systemPropertyVariables>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <!-- JaCoCo Plugin for Code Coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.8</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 测试配置文件

### application-test.yml
已创建在 `src/test/resources/application-test.yml`

### 测试数据库脚本
- `src/test/resources/schema-test.sql` - 测试表结构
- `src/test/resources/data-test.sql` - 测试初始数据

## 测试执行命令

### 运行所有单元测试
```bash
mvn test -Dspring.profiles.active=test
```

### 运行特定测试类
```bash
mvn test -Dtest=CustomerServiceTest -Dspring.profiles.active=test
```

### 运行测试并生成覆盖率报告
```bash
mvn clean test jacoco:report -Dspring.profiles.active=test
```

### 运行测试套件
```bash
mvn test -Dtest=CustomerModuleTestSuite -Dspring.profiles.active=test
```

## 测试报告位置

- **JaCoCo覆盖率报告**: `target/site/jacoco/index.html`
- **Surefire测试报告**: `target/surefire-reports/`
- **自定义测试报告**: `target/test-reports/`

## 测试质量标准

### 覆盖率要求
- **行覆盖率**: ≥80%
- **分支覆盖率**: ≥70%
- **方法覆盖率**: ≥90%
- **类覆盖率**: ≥95%

### 测试用例要求
- **单元测试**: 每个public方法至少1个测试用例
- **边界测试**: 覆盖边界条件和异常情况
- **集成测试**: 验证组件间协作
- **性能测试**: 验证关键功能性能指标

### 测试命名规范
- **测试类**: `{ClassName}Test`
- **测试方法**: `test{MethodName}_{Scenario}`
- **测试数据**: `test{DataType}`
- **Mock对象**: `mock{ObjectName}`

## 持续集成配置

### GitHub Actions配置示例
```yaml
name: Customer Module Tests

on:
  push:
    paths:
      - 'src/main/java/com/yxrobot/service/Customer*'
      - 'src/main/java/com/yxrobot/controller/Customer*'
      - 'src/main/java/com/yxrobot/mapper/Customer*'
      - 'src/test/java/com/yxrobot/**/*Test.java'
  pull_request:
    paths:
      - 'src/main/java/com/yxrobot/service/Customer*'
      - 'src/main/java/com/yxrobot/controller/Customer*'

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        
    - name: Run Customer Module Tests
      run: mvn test -Dtest=CustomerModuleTestSuite -Dspring.profiles.active=test
      
    - name: Generate Coverage Report
      run: mvn jacoco:report
      
    - name: Upload Coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml
```

## 测试最佳实践

### 1. 测试数据管理
- 使用`@BeforeEach`准备测试数据
- 使用`@AfterEach`清理测试数据
- 避免测试间的数据依赖

### 2. Mock使用原则
- Mock外部依赖和复杂对象
- 验证Mock对象的调用
- 使用`@MockBean`进行Spring集成测试

### 3. 断言策略
- 使用具体的断言方法
- 验证返回值和副作用
- 检查异常情况

### 4. 测试组织
- 按功能模块组织测试
- 使用测试套件管理相关测试
- 保持测试的独立性和可重复性