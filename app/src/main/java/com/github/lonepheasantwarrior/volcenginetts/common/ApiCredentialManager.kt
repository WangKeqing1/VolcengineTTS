package com.github.lonepheasantwarrior.volcenginetts.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * API凭据管理器 - 使用加密存储保护敏感信息
 *
 * 使用Jetpack Security Crypto库提供的EncryptedSharedPreferences
 * 来安全地存储appId和apiKey（token）
 */
class ApiCredentialManager private constructor(private val context: Context) {

    companion object {
        private const val ENCRYPTED_PREFS_FILE = "encrypted_api_credentials"
        private const val KEY_APP_ID = "app_id"
        private const val KEY_API_KEY = "api_key"

        @Volatile
        private var instance: ApiCredentialManager? = null

        /**
         * 获取单例实例
         */
        fun getInstance(context: Context): ApiCredentialManager {
            return instance ?: synchronized(this) {
                instance ?: ApiCredentialManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private var encryptedPrefs: SharedPreferences? = null

    init {
        initializeEncryptedPreferences()
    }

    /**
     * 初始化加密的SharedPreferences
     */
    private fun initializeEncryptedPreferences() {
        try {
            // 创建或获取MasterKey
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            // 创建EncryptedSharedPreferences
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            Log.d(LogTag.INFO, "加密存储初始化成功")
        } catch (e: GeneralSecurityException) {
            Log.e(LogTag.ERROR, "加密存储初始化失败 (安全异常): ${e.message}")
            handleEncryptionError(e)
        } catch (e: IOException) {
            Log.e(LogTag.ERROR, "加密存储初始化失败 (IO异常): ${e.message}")
            handleEncryptionError(e)
        }
    }

    /**
     * 处理加密错误 - 降级处理
     * 在部分定制机型上，Keystore可能损坏，此时清理旧数据
     */
    private fun handleEncryptionError(e: Exception) {
        try {
            // 尝试删除损坏的加密文件
            context.deleteSharedPreferences(ENCRYPTED_PREFS_FILE)
            Log.d(LogTag.INFO, "已清理损坏的加密存储文件")

            // 重新初始化
            initializeEncryptedPreferences()
        } catch (cleanupError: Exception) {
            Log.e(LogTag.ERROR, "清理加密存储失败: ${cleanupError.message}")
            encryptedPrefs = null
        }
    }

    /**
     * 保存API凭据
     * @param appId 应用ID
     * @param apiKey API密钥（token）
     * @return 是否保存成功
     */
    fun saveCredentials(appId: String, apiKey: String): Boolean {
        return try {
            encryptedPrefs?.edit()?.apply {
                putString(KEY_APP_ID, appId)
                putString(KEY_API_KEY, apiKey)
                apply()
            }
            Log.d(LogTag.INFO, "API凭据已加密保存")
            true
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "保存API凭据失败: ${e.message}")
            false
        }
    }

    /**
     * 获取API凭据
     * @return Pair<appId, apiKey>，如果不存在或读取失败则返回null
     */
    fun getCredentials(): Pair<String?, String?> {
        return try {
            val appId = encryptedPrefs?.getString(KEY_APP_ID, null)
            val apiKey = encryptedPrefs?.getString(KEY_API_KEY, null)

            if (appId != null || apiKey != null) {
                Log.d(LogTag.INFO, "读取加密凭据 - appId: ${if (appId.isNullOrBlank()) "空" else "已设置(${appId.length}字符)"}, " +
                        "apiKey: ${if (apiKey.isNullOrBlank()) "空" else "已设置(${apiKey.length}字符)"}")
            }

            Pair(appId, apiKey)
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "读取API凭据失败: ${e.message}")
            Pair(null, null)
        }
    }

    /**
     * 清除API凭据
     * @return 是否清除成功
     */
    fun clearCredentials(): Boolean {
        return try {
            encryptedPrefs?.edit()?.apply {
                remove(KEY_APP_ID)
                remove(KEY_API_KEY)
                apply()
            }
            Log.d(LogTag.INFO, "API凭据已清除")
            true
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "清除API凭据失败: ${e.message}")
            false
        }
    }

    /**
     * 检查是否已保存凭据
     * @return 是否存在有效凭据
     */
    fun hasCredentials(): Boolean {
        val (appId, apiKey) = getCredentials()
        return !appId.isNullOrBlank() && !apiKey.isNullOrBlank()
    }
}
