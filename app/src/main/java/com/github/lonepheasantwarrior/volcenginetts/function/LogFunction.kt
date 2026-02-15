package com.github.lonepheasantwarrior.volcenginetts.function

import android.content.Context
import android.util.Log
import com.github.lonepheasantwarrior.volcenginetts.common.LogTag
import com.github.lonepheasantwarrior.volcenginetts.common.TtsCallLog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日志管理功能
 */
class LogFunction(private val context: Context) {
    private val logFileName = "tts_call_logs.json"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * 获取日志文件
     */
    private fun getLogFile(): File {
        return File(context.filesDir, logFileName)
    }

    /**
     * 添加调用日志
     */
    fun addLog(log: TtsCallLog) {
        try {
            val logs = readLogs().toMutableList()
            logs.add(0, log) // 添加到列表开头（最新的在前面）

            // 转换为JSON并保存
            val jsonArray = JSONArray()
            logs.forEach { logItem ->
                val jsonObject = JSONObject().apply {
                    put("timestamp", logItem.timestamp)
                    put("speakerId", logItem.speakerId)
                    put("speakerName", logItem.speakerName)
                    put("scene", logItem.scene)
                    put("textLength", logItem.textLength)
                    put("isEmotional", logItem.isEmotional)
                    put("success", logItem.success)
                    put("errorMessage", logItem.errorMessage ?: "")
                }
                jsonArray.put(jsonObject)
            }

            getLogFile().writeText(jsonArray.toString())
            Log.d(LogTag.INFO, "日志已保存")
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "保存日志失败: ${e.message}")
        }
    }

    /**
     * 读取所有日志
     */
    fun readLogs(): List<TtsCallLog> {
        try {
            val logFile = getLogFile()
            if (!logFile.exists()) {
                return emptyList()
            }

            val jsonString = logFile.readText()
            if (jsonString.isBlank()) {
                return emptyList()
            }

            val jsonArray = JSONArray(jsonString)
            val logs = mutableListOf<TtsCallLog>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val log = TtsCallLog(
                    timestamp = jsonObject.getLong("timestamp"),
                    speakerId = jsonObject.getString("speakerId"),
                    speakerName = jsonObject.getString("speakerName"),
                    scene = jsonObject.getString("scene"),
                    textLength = jsonObject.getInt("textLength"),
                    isEmotional = jsonObject.getBoolean("isEmotional"),
                    success = jsonObject.getBoolean("success"),
                    errorMessage = jsonObject.optString("errorMessage").takeIf { it.isNotBlank() }
                )
                logs.add(log)
            }

            return logs
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "读取日志失败: ${e.message}")
            return emptyList()
        }
    }

    /**
     * 清除所有日志
     */
    fun clearLogs() {
        try {
            val logFile = getLogFile()
            if (logFile.exists()) {
                logFile.delete()
                Log.d(LogTag.INFO, "日志已清除")
            }
        } catch (e: Exception) {
            Log.e(LogTag.ERROR, "清除日志失败: ${e.message}")
        }
    }

    /**
     * 获取日志统计信息
     */
    fun getLogStatistics(): LogStatistics {
        val logs = readLogs()
        val totalCalls = logs.size
        val successCalls = logs.count { it.success }
        val failedCalls = totalCalls - successCalls
        val totalTextLength = logs.sumOf { it.textLength }

        return LogStatistics(
            totalCalls = totalCalls,
            successCalls = successCalls,
            failedCalls = failedCalls,
            totalTextLength = totalTextLength
        )
    }

    /**
     * 格式化时间戳
     */
    fun formatTimestamp(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
}

/**
 * 日志统计数据类
 */
data class LogStatistics(
    val totalCalls: Int,
    val successCalls: Int,
    val failedCalls: Int,
    val totalTextLength: Int
)
