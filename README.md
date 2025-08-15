# Spring Boot Unit Testing Learning Journey

> **學習目標**: 從零開始學習 Spring Boot 單元測試，展示完整的測試實踐從 Controller 到 Service 層

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![JUnit](https://img.shields.io/badge/JUnit-5-blue.svg)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-5.17.0-red.svg)](https://mockito.org/)

## 專案概述

這是一個**學習導向**的專案，記錄了我從零開始學習 Spring Boot 單元測試的完整過程。專案展示了如何使用 JUnit 5 和 Mockito 建立有效的測試架構，涵蓋 Controller 和 Service 層的測試實作。

### 學習成果
### 🎯 學習里程碑

|  學習重點 | 完成狀態  | 核心技能 |
|----------|-------|----------|
|基礎單元測試設置 | ✅ 完成  | JUnit 5 基礎、測試概念 |
|Mock 物件使用 | ✅ 完成  | Mockito 框架、依賴隔離 |
|Controller & Service 層測試 | ✅ 完成  | MockMvc、分層測試策略 |
|整合測試 | ❌ 未完成 | @SpringBootTest |
|TestContainers 使用 | ❌ 未完成 | 容器化測試 |
|測試覆蓋率報告 | ❌ 未完成 | JaCoCo 整合 |


## 工具使用

| 類別 | 技術 | 版本 | 用途 |
|------|------|------|------|
| **Framework** | Spring Boot | 3.5.4 | 主要應用框架 |
| **Testing** | JUnit 5 | 5.12.2 | 測試執行框架 |
| **Mocking** | Mockito | 5.17.0 | Mock 物件框架 |
| **Web Testing** | MockMvc | - | HTTP 層測試工具 |
| **Database** | PostgreSQL | 42.7.7 | 資料庫 |
| **Build Tool** | Maven | - | 專案建構工具 |

## 專案架構

```
src/
├── main/java/com/self/demo/
│   ├── controller/
│   │   └── UserController.java       # REST API 控制器
│   ├── service/
│   │   └── UserService.java          # 業務邏輯層
│   ├── dao/
│   │   └── UserInterface.java        # 資料存取層
│   ├── model/
│   │   └── User.java                 # 資料模型
│   └── DemoApplication.java          # Spring Boot 應用程式
│
└── test/java/com/self/demo/
    ├── controller/
    │   └── UserControllerTest.java   # Controller Web 層測試
    └── service/
        └── UserServiceTest.java      # Service 業務邏輯測試
```

## 測試架構設計

### Controller 層 - Web 測試
採用 **MockMvc + Mockito** 的混合策略：

```java
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    
    private MockMvc mvc;                    // HTTP 請求模擬
    
    @Mock
    private UserService userService;       // Mock Service 依賴
    
    @InjectMocks
    private UserController userController; // 被測試的 Controller
    
    @BeforeEach
    void setUp() {
        // 建立 standalone MockMvc，不依賴完整 Spring Context
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
}
```

**測試重點**：
- **HTTP 行為驗證** - 狀態碼、JSON 回應、Content-Type
- **URL 路由測試** - 確保請求正確映射到 Controller 方法
- **例外處理** - 測試各種錯誤情況的 HTTP 回應
- **JSON 序列化** - 驗證物件正確轉換為 JSON

### Service 層 - 業務邏輯測試
採用 **純 Mockito** 策略：

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    UserInterface userDao;              // Mock DAO 依賴
    
    @InjectMocks  
    UserService userService;            // 被測試的 Service
}
```

**測試重點**：
-  **業務邏輯驗證** - 核心業務規則的正確性
-  **依賴互動** - 確保正確呼叫 DAO 方法
-  **例外處理** - 各種業務例外的處理邏輯
-  **資料轉換** - 輸入輸出資料的正確處理

## 測試案例覆蓋

### Controller 測試案例
| 測試方法 | 測試情境 | 驗證重點 |
|----------|----------|----------|
| `get_shouldReturn200_andBody` | 成功取得使用者 | HTTP 200, JSON 完整性, 資料正確性 |
| `get_shouldReturn404_whenServiceThrows` | 使用者不存在 | HTTP 404, 例外處理, 錯誤訊息 |

### Service 測試案例
| 測試方法 | 測試情境 | 驗證重點 |
|----------|----------|----------|
| `getById_should_return_user_when_found` | 成功找到使用者 | 回傳資料正確性, DAO 呼叫驗證 |
| `getById_should_throw_NoSuchElementException_when_not_found` | 使用者不存在 | 例外拋出, 錯誤類型正確 |

## 快速開始

### 環境需求
- ☕ Java 21+
- 📦 Maven 3.8+
- 🐘 PostgreSQL (可選，測試不依賴真實資料庫)

## 💡 核心測試概念展示

### Mock 物件的正確使用
```java
// 1. 建立 Mock 物件
@Mock
private UserService userService;

// 2. 定義 Mock 行為（Stubbing）
when(userService.findById("u1")).thenReturn(mockUser);

// 3. 執行測試
mvc.perform(get("/api/u1"));

// 4. 驗證互動（Verification）
verify(userService, times(1)).findById("u1");
```

### 測試隔離策略
```java
@BeforeEach
void setUp() {
    // 重置 Mock 狀態，確保測試獨立性
    Mockito.reset(userService);
    
    // 建立測試環境
    mvc = MockMvcBuilders.standaloneSetup(userController).build();
}
```

### 測試
```java
@Test
void getById_should_return_user_when_found() {
    // Arrange（準備）- 設定測試資料和 Mock 行為
    String id = "u1";
    User expectedUser = new User("u1", "Tom", "tom@example.com", "CARD123");
    when(userDao.findById(id)).thenReturn(Optional.of(expectedUser));
    
    // Act（執行）- 呼叫被測試的方法
    User actualUser = userService.findById(id);
    
    // Assert（驗證）- 檢查結果和副作用
    assertEquals("u1", actualUser.getUserId());
    verify(userDao, times(1)).findById(id);
}
```

### 測試設計思維
1. **依賴隔離** - 理解為什麼要使用 Mock 物件
2. **職責分離** - 每層測試專注自己的職責
3. **失敗情境** - 不只測試成功案例，也要測試失敗情況

### 技術技能掌握
- **JUnit 5** - 現代 Java 測試框架的使用
- **Mockito** - Mock 物件框架的深度應用
- **MockMvc** - Spring Web 層測試的標準工具
- **測試策略** - 不同層級測試的設計思路

## 開發環境

### IDE 測試執行
- **IntelliJ IDEA**: 右鍵 → Run 'UserControllerTest'

### 命令列測試
```bash
# Maven 測試命令
mvn test                              # 執行所有測試
mvn test -Dtest=UserControllerTest    # 執行特定測試類別
mvn test -Dtest=*Controller*          # 執行符合模式的測試
mvn clean test                        # 清理後執行測試
```

## 遇到的挑戰與解決方案

### 🐛 Challenge 1: Spring Session 依賴衝突

### 根本原因
當 Spring Boot 應用程式包含 `spring-session-jdbc` 依賴時，Spring Boot 會**自動啟用 Session 管理功能**，但這個功能需要**有效的資料庫連線**才能正常工作。

### 依賴來源
```xml
<!-- pom.xml 中的這個依賴是問題的起源 -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
</dependency>
```

### Spring Boot 自動配置機制
```java
// Spring Boot 看到 spring-session-jdbc 依賴後，會自動載入：
SessionAutoConfiguration               // Session 基本配置
JdbcSessionConfiguration              // JDBC Session 配置  
JdbcSessionDataSourceScriptDatabaseInitializer  // 資料庫初始化器
```

### 錯誤發生的完整流程

#### 步驟 1: Spring Boot 啟動檢測
```
Spring Boot 啟動
    ↓
檢測到 spring-session-jdbc.jar
    ↓  
自動啟用 SessionAutoConfiguration
    ↓
嘗試配置 JDBC Session 功能
```

#### 步驟 2: 資料庫連線需求
```java
// Spring Session 需要建立以下資料表：
SPRING_SESSION         // 儲存 Session 資料
SPRING_SESSION_ATTRIBUTES  // 儲存 Session 屬性
```

#### 步驟 3: 初始化失敗點
```java
// JdbcSessionDataSourceScriptDatabaseInitializer 嘗試：
1. 連接資料庫
2. 檢查資料庫驅動
3. 建立 Session 相關資料表
4. 初始化 Session 功能
```

#### 步驟 4: 具體失敗原因
```java
// 錯誤訊息分析：
java.lang.IllegalArgumentException: jdbcUrl is required with driverClassName.
    at com.zaxxer.hikari.HikariConfig.validate(HikariConfig.java:1080)

// 這表示：
1. HikariCP 無法取得有效的 jdbcUrl
2. 資料庫連線配置有問題
3. Spring Session 無法初始化資料庫連線
```

### 🔧 為什麼會發生這個問題？

#### 1. 配置檔案問題
```yaml
# 我們的 application.yml 可能有以下問題：
spring:
  datasource:
    # 可能的問題：
    name: master
```

#### 3. Spring Session 配置嘗試
```yaml
# 我們曾經嘗試這樣解決：
spring:
  session:
    store-type: none  # 試圖停用 Session 功能

# 但這個設定不足以完全阻止 Spring Session 的初始化
# Spring Boot 仍然會嘗試載入 JdbcSessionConfiguration
# 因為 spring-session-jdbc 依賴的存在
```

### 💡 解決方案詳細分析
#### 方案 1: 排除自動配置
```java
@SpringBootApplication(exclude = {
    SessionAutoConfiguration.class  // 完全停用 Session 自動配置
})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
#### 方案 2: 完全註解調 dependency，並移除yml session 設定 (我們使用的方案)
```java
//<dependency>
//    <groupId>org.springframework.session</groupId>
//    <artifactId>spring-session-jdbc</artifactId>
//</dependency>
```
```yaml
spring:
  session:
    store-type: none  # 試圖完全停用 Session 儲存
```
**為什麼這樣有效？**
- 告訴 Spring Boot 不要自動配置 Session 功能
- 避免 Session 相關的資料庫初始化
- 應用程式可以正常啟動，專注於我們的測試學習

### 學習要點
#### 1. Spring Boot 自動配置的雙面性
```java
// 優點：開箱即用，減少配置工作
// 缺點：有時候會自動配置不需要的功能
```

#### 2. 依賴管理的重要性
```xml
<!-- 每個依賴都可能帶來額外的自動配置 -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
    <!-- 這個依賴會自動啟用 Session 功能 -->
</dependency>
```

### 🐛 Challenge 2: MockMvc NullPointerException
**問題**: `Cannot invoke MockMvc.perform() because "this.mvc" is null`
**解決方案**:
```java
private MockMvc mvc;  // 模擬 HTTP 請求並檢查回應

@BeforeEach
void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(userController).build();
}
```
**學習**: @BeforeEach 的重要性和 MockMvc 初始化

### 🐛 Challenge 3: 測試間相互污染，沒有在每個測試前重置 Mock 狀態

```java
@ExtendWith(MockitoExtension.class)
class BadExampleTest {

    @Mock
    private UserService userService;

    // ❌ 沒有 @BeforeEach，沒有 reset()

    @Test
    void test1_setupUser() {
        System.out.println("=== Test 1 開始 ===");

        // 設定 Mock 行為
        when(userService.findById("u1")).thenReturn(new User("u1", "John", "john@test.com"));

        // 測試邏輯
        User result = userService.findById("u1");
        assertEquals("John", result.getName());

        // 驗證呼叫次數
        verify(userService, times(1)).findById("u1");  // ✅ 通過

        System.out.println("Test 1 取得使用者: " + result.getName());
        System.out.println("=== Test 1 結束 ===");
    }

    @Test
    void test2_expectNullUser() {
        System.out.println("=== Test 2 開始 ===");

        // ❌ 這個測試期望 findById("u1") 回傳 null（因為沒有設定）
        // 但實際上會回傳 "John"，因為 Mock 還記得 test1 的設定！

        User result = userService.findById("u1");

        // 這個測試會失敗！
        assertNull(result);  // ❌ 失敗！result 是 "John"，不是 null

        System.out.println("=== Test 2 結束 ===");
    }

    @Test
    void test3_checkCallCount() {
        System.out.println("=== Test 3 開始 ===");

        // 這個測試只呼叫一次，期望 verify 通過
        User result = userService.findById("u1");

        // ❌ 這會失敗！因為呼叫次數是累積的
        // test1 呼叫 1 次 + test2 呼叫 1 次 + test3 呼叫 1 次 = 3 次
        verify(userService, times(1)).findById("u1");  // ❌ 失敗！實際是 3 次

        System.out.println("=== Test 3 結束 ===");
    }
}
```

## 未來可學習的範圍

### 短期目標 
- [ ] **整合測試** - 學習 @SpringBootTest 完整測試
- [ ] **資料庫測試** - @DataJpaTest Repository 層測試
- [ ] **測試覆蓋率** - 報告和覆蓋率門檻

### 中期目標
- [ ] **TestContainers** - 容器化的資料庫測試
- [ ] **效能測試** - 基本的負載測試
- [ ] **API 測試** - 完整的 REST API 測試

### 長期目標
- [ ] **E2E 測試** - 端對端測試框架
- [ ] **測試自動化** - CI/CD 流程整合
- [ ] **測試策略** - 測試金字塔實踐
---


