# 任务背景与目标
你现在是一名资深 Android 原生开发专家。我正在使用 Kotlin 开发一款 Android 应用，需要实现一个“一次输入，安全保存，支持系统级自动填充”的凭据管理功能，用于保存第三方接口的 `appid` 和 `api_key`。
交互体验需要类似 Google 密码管理器（利用系统 Autofill Framework 提示保存和填充），底层存储必须是加密的。

# 技术栈与规范
- 语言：Kotlin
- 本地加密存储：Jetpack Security (`androidx.security:security-crypto`) -> `EncryptedSharedPreferences`
- UI 填充支持：Android Autofill Framework

# 执行步骤 (Task List)
请按照以下步骤逐步分析我的项目并执行修改：

### Step 1: 环境分析与依赖添加
1. 分析我的工程，确认我当前使用的是基于 XML 的 View 体系还是 Jetpack Compose 体系。
2. 在 `app/build.gradle.kts` (或 `.gradle`) 中引入 Jetpack Security Crypto 依赖（请使用最新的稳定版或 RC 版，如 `1.1.0-alpha06`）。
3. 同步 Gradle。

### Step 2: 编写安全存储管理类 (Encrypted Storage)
1. 创建一个名为 `ApiCredentialManager` (或类似名称) 的单例类或 Repository。
2. 在内部初始化 `MasterKey` (推荐使用 `MasterKey.Builder` 和 `KeyScheme.AES256_GCM`)。
3. 初始化 `EncryptedSharedPreferences`。
4. 提供三个核心方法：
    - `saveCredentials(appId: String, apiKey: String)`
    - `getCredentials(): Pair<String?, String?>`
    - `clearCredentials()`
5. **异常处理要求**：必须 try-catch 捕获 `GeneralSecurityException` 和 `IOException`，以防部分定制机型由于 Keystore 损坏导致崩溃（发生异常时可降级清理旧数据或返回 null）。

### Step 3: 改造 UI 以支持 Autofill (自动填充框架)
根据你在 Step 1 中确认的 UI 体系（XML 或 Compose），改造输入 `appid` 和 `api_key` 的界面：
1. 为 `appid` 的输入框添加自动填充提示：`AUTOFILL_HINT_USERNAME`。
2. 为 `api_key` 的输入框添加自动填充提示：`AUTOFILL_HINT_PASSWORD`。
3. 确保包含这两个输入框的表单（或容器）能够触发系统的 Autofill 弹窗逻辑（标记 `importantForAutofill="yes"`）。
4. （如果是 Compose，请使用 `autofill` Modifier 及相关的 `AutofillNode` 逻辑）。

### Step 4: 业务逻辑组装 (ViewModel / Activity 改造)
1. 在页面初始化时，先调用 `ApiCredentialManager.getCredentials()`，如果已有数据，则直接静默读取并进入下一步（或预填充到输入框）。
2. 当用户手动输入完凭据并点击“保存/确认”按钮时，调用 `ApiCredentialManager.saveCredentials()` 持久化到本地。

# 约束条件
- 绝对禁止使用明文的 `SharedPreferences` 或 `DataStore` 保存这两个敏感字段。
- 代码需要符合 Kotlin 协程最佳实践（如果涉及到异步读取）。
- 完成后，请向我输出一段简短的验收说明，告诉我应该如何在真机/模拟器上测试这个自动填充功能。