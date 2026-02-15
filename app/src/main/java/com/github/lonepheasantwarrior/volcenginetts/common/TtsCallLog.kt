package com.github.lonepheasantwarrior.volcenginetts.common

/**
 * TTS调用日志数据类
 * @param timestamp 调用时间戳
 * @param speakerId 声音ID
 * @param speakerName 声音名称
 * @param scene 场景分类
 * @param textLength 文本长度
 * @param isEmotional 是否情感朗读
 * @param success 是否成功
 * @param errorMessage 错误信息（如果失败）
 */
data class TtsCallLog(
    val timestamp: Long,
    val speakerId: String,
    val speakerName: String,
    val scene: String,
    val textLength: Int,
    val isEmotional: Boolean,
    val success: Boolean,
    val errorMessage: String? = null
)
