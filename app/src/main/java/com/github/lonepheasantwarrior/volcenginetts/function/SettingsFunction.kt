package com.github.lonepheasantwarrior.volcenginetts.function

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.github.lonepheasantwarrior.volcenginetts.common.ApiCredentialManager
import com.github.lonepheasantwarrior.volcenginetts.common.LogTag
import com.github.lonepheasantwarrior.volcenginetts.common.SettingsData
import com.github.lonepheasantwarrior.volcenginetts.ui.theme.ThemeMode

/**
 * 设置相关功能
 */
class SettingsFunction(private val context: Context) {
    // SharedPreferences文件名
    private val prefsName = "VolcengineTTS_prefs"

    // SharedPreferences键名（appId和token已迁移到加密存储）
    private val appId = "app_id"  // 仅用于数据迁移
    private val token = "token"   // 仅用于数据迁移
    private val speakerId = "selected_speaker_id"
    private val serviceCluster = "service_cluster"
    private val isEmotional = "is_emotional"
    private val speedRatio = "speed_ratio"
    private val loudnessRatio = "loudness_ratio"
    private val encoding = "encoding"
    private val sampleRate = "sample_rate"
    private val emotion = "emotion"
    private val emotionScale = "emotion_scale"
    private val explicitLanguage = "explicit_language"
    private val showWelcomeDialog = "show_welcome_dialog"
    private val themeMode = "theme_mode"
    private val useDynamicColor = "use_dynamic_color"
    private val enableLogging = "enable_logging"
    private val credentialsMigrated = "credentials_migrated"  // 迁移标记

    private val mainHandler = Handler(Looper.getMainLooper())

    // 加密凭据管理器
    private val credentialManager = ApiCredentialManager.getInstance(context)

    init {
        // 执行数据迁移（从明文存储迁移到加密存储）
        migrateCredentialsIfNeeded()
    }
    
    /**
     * 获取SharedPreferences实例
     */
    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    /**
     * 数据迁移：将旧的明文appId和token迁移到加密存储
     */
    private fun migrateCredentialsIfNeeded() {
        val prefs = getPreferences()

        // 检查是否已经迁移过
        if (prefs.getBoolean(credentialsMigrated, false)) {
            return
        }

        try {
            // 读取旧的明文数据
            val oldAppId = prefs.getString(appId, null)
            val oldToken = prefs.getString(token, null)

            // 如果存在旧数据，迁移到加密存储
            if (!oldAppId.isNullOrBlank() || !oldToken.isNullOrBlank()) {
                credentialManager.saveCredentials(
                    oldAppId ?: "",
                    oldToken ?: ""
                )
                Log.d(LogTag.INFO, "凭据已从明文存储迁移到加密存储")
            }

            // 清除旧的明文数据
            prefs.edit {
                remove(appId)
                remove(token)
                putBoolean(credentialsMigrated, true)
            }
            Log.d(LogTag.INFO, "旧的明文凭据已清除")
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "凭据迁移失败: ${e.message}")
        }
    }
    
    /**
     * 保存设置信息
     * @param appId 应用ID
     * @param token 令牌
     * @param selectedSpeakerId 选中的声音ID
     * @param serviceCluster 接口服务簇
     * @param isEmotional 是否开启情感朗读
     * @param speedRatio 语速比例
     * @param loudnessRatio 音量比例
     * @param encoding 音频编码格式
     * @param sampleRate 采样率
     * @param emotion 情感类型
     * @param emotionScale 情感强度
     * @param explicitLanguage 明确语言
     */
    fun saveSettings(
        appId: String,
        token: String,
        selectedSpeakerId: String,
        serviceCluster: String,
        isEmotional: Boolean = false,
        speedRatio: Float = 1.0f,
        loudnessRatio: Float = 1.0f,
        encoding: String = "pcm",
        sampleRate: Int = 16000,
        emotion: String = "",
        emotionScale: Int = 3,
        explicitLanguage: String = ""
    ) {
        // 使用加密存储保存敏感凭据
        credentialManager.saveCredentials(appId, token)

        // 其他非敏感配置仍使用普通SharedPreferences
        getPreferences().edit {
            putString(speakerId, selectedSpeakerId)
            putString(this@SettingsFunction.serviceCluster, serviceCluster)
            putBoolean(this@SettingsFunction.isEmotional, isEmotional)
            putFloat(this@SettingsFunction.speedRatio, speedRatio)
            putFloat(this@SettingsFunction.loudnessRatio, loudnessRatio)
            putString(this@SettingsFunction.encoding, encoding)
            putInt(this@SettingsFunction.sampleRate, sampleRate)
            putString(this@SettingsFunction.emotion, emotion)
            putInt(this@SettingsFunction.emotionScale, emotionScale)
            putString(this@SettingsFunction.explicitLanguage, explicitLanguage)
        }
        Log.d(LogTag.INFO, "配置已保存（凭据已加密）")
        mainHandler.post {
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 获取设置信息
     * @return 包含所有配置参数的SettingsData对象
     */
    fun getSettings(): SettingsData {
        val prefs = getPreferences()

        // 从加密存储读取敏感凭据
        val (appId, token) = credentialManager.getCredentials()

        // 从普通SharedPreferences读取其他配置
        val selectedSpeakerId = prefs.getString(speakerId, "") ?: ""
        val serviceCluster = prefs.getString(serviceCluster, "") ?: ""
        val isEmotional = prefs.getBoolean(isEmotional, false)
        val speedRatio = prefs.getFloat(speedRatio, 1.0f)
        val loudnessRatio = prefs.getFloat(loudnessRatio, 1.0f)
        val encoding = prefs.getString(encoding, "pcm") ?: "pcm"
        val sampleRate = prefs.getInt(sampleRate, 16000)
        val emotion = prefs.getString(emotion, "") ?: ""
        val emotionScale = prefs.getInt(emotionScale, 3)
        val explicitLanguage = prefs.getString(explicitLanguage, "") ?: ""
        val themeModeStr = prefs.getString(themeMode, ThemeMode.FOLLOW_SYSTEM.name) ?: ThemeMode.FOLLOW_SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeModeStr)
        } catch (e: IllegalArgumentException) {
            ThemeMode.FOLLOW_SYSTEM
        }
        val useDynamicColor = prefs.getBoolean(useDynamicColor, true)
        val enableLogging = prefs.getBoolean(enableLogging, true)

        Log.d(LogTag.INFO, "读取设置 - appId: ${if (appId.isNullOrBlank()) "空" else "已设置(${appId.length}字符)"}, " +
                "token: ${if (token.isNullOrBlank()) "空" else "已设置(${token.length}字符)"}, " +
                "speakerId: ${if (selectedSpeakerId.isBlank()) "空" else selectedSpeakerId}, " +
                "serviceCluster: ${if (serviceCluster.isBlank()) "空" else serviceCluster}, " +
                "isEmotional: $isEmotional, speedRatio: $speedRatio, loudnessRatio: $loudnessRatio, " +
                "encoding: $encoding, sampleRate: $sampleRate, emotion: $emotion, emotionScale: $emotionScale, " +
                "explicitLanguage: $explicitLanguage, themeMode: $themeMode, useDynamicColor: $useDynamicColor, enableLogging: $enableLogging")

        return SettingsData(
            appId ?: "",
            token ?: "",
            selectedSpeakerId,
            serviceCluster,
            isEmotional,
            speedRatio,
            loudnessRatio,
            encoding,
            sampleRate,
            emotion,
            emotionScale,
            explicitLanguage,
            themeMode,
            useDynamicColor,
            enableLogging
        )
    }

    /**
     * 保存主题设置
     * @param themeMode 主题模式
     * @param useDynamicColor 是否使用动态颜色
     */
    fun saveThemeSettings(themeMode: ThemeMode, useDynamicColor: Boolean) {
        getPreferences().edit {
            putString(this@SettingsFunction.themeMode, themeMode.name)
            putBoolean(this@SettingsFunction.useDynamicColor, useDynamicColor)
        }
        Log.d(LogTag.INFO, "主题设置已保存: $themeMode, 动态颜色: $useDynamicColor")
    }

    /**
     * 获取主题模式
     * @return 主题模式
     */
    fun getThemeMode(): ThemeMode {
        val prefs = getPreferences()
        val themeModeStr = prefs.getString(themeMode, ThemeMode.FOLLOW_SYSTEM.name) ?: ThemeMode.FOLLOW_SYSTEM.name
        return try {
            ThemeMode.valueOf(themeModeStr)
        } catch (e: IllegalArgumentException) {
            ThemeMode.FOLLOW_SYSTEM
        }
    }

    /**
     * 获取是否使用动态颜色
     * @return 是否使用动态颜色
     */
    fun getUseDynamicColor(): Boolean {
        return getPreferences().getBoolean(useDynamicColor, true)
    }

    /**
     * 保存日志设置
     * @param enabled 是否启用日志记录
     */
    fun saveLoggingSettings(enabled: Boolean) {
        getPreferences().edit {
            putBoolean(enableLogging, enabled)
        }
        Log.d(LogTag.INFO, "日志设置已保存: $enabled")
    }

    /**
     * 获取是否启用日志记录
     * @return 是否启用日志记录
     */
    fun getEnableLogging(): Boolean {
        return getPreferences().getBoolean(enableLogging, true)
    }
    
    /**
     * 检查是否需要显示欢迎弹窗
     * @return 如果需要显示返回true，否则返回false
     */
    fun shouldShowWelcomeDialog(): Boolean {
        val prefs = getPreferences()
        return prefs.getBoolean(showWelcomeDialog, true)
    }
    
    /**
     * 设置是否显示欢迎弹窗
     * @param show 是否显示
     */
    fun setShowWelcomeDialog(show: Boolean) {
        getPreferences().edit {
            putBoolean(showWelcomeDialog, show)
        }
        Log.d(LogTag.INFO, "欢迎弹窗显示状态已更新: $show")
    }
}