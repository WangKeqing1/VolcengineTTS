package com.github.lonepheasantwarrior.volcenginetts.common

import com.github.lonepheasantwarrior.volcenginetts.ui.theme.ThemeMode

/**
 * 设置数据类
 * @param appId 应用ID
 * @param token 令牌
 * @param selectedSpeakerId 选中的声音ID
 * @param serviceCluster 接口服务簇
 * @param isEmotional 是否启用情感朗读
 * @param speedRatio 语速比例 (0.1-2.0，默认1.0)
 * @param loudnessRatio 音量比例 (0.5-2.0，默认1.0)
 * @param encoding 音频编码格式 (pcm/wav/mp3/ogg_opus，默认pcm)
 * @param sampleRate 采样率 (8000/16000/24000，默认16000)
 * @param emotion 情感类型 (happy/sad/angry等，默认空)
 * @param emotionScale 情感强度 (1-5，默认3)
 * @param explicitLanguage 明确语言 (zh-CN/en-US等，默认空)
 * @param themeMode 主题模式
 * @param useDynamicColor 是否使用动态颜色（莫奈取色）
 * @param enableLogging 是否启用日志记录
 */
data class SettingsData(
    val appId: String,
    val token: String,
    val selectedSpeakerId: String,
    val serviceCluster: String,
    val isEmotional: Boolean,
    val speedRatio: Float = 1.0f,
    val loudnessRatio: Float = 1.0f,
    val encoding: String = "pcm",
    val sampleRate: Int = 16000,
    val emotion: String = "",
    val emotionScale: Int = 3,
    val explicitLanguage: String = "",
    val themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = true,
    val enableLogging: Boolean = true
)