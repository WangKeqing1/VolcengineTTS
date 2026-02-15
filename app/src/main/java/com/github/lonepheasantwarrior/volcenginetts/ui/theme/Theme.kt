package com.github.lonepheasantwarrior.volcenginetts.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * 主题模式枚举
 */
enum class ThemeMode {
    FOLLOW_SYSTEM,  // 跟随系统
    LIGHT,          // 浅色模式
    DARK,           // 深色模式
    AMOLED          // AMOLED纯黑模式
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryVariant,
    onPrimaryContainer = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = DarkOnSurface,
    tertiary = Pink80,
    onTertiary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkOnSurface,
)

private val AmoledColorScheme = darkColorScheme(
    primary = AmoledPrimary,
    onPrimary = AmoledOnPrimary,
    primaryContainer = AmoledPrimaryVariant,
    onPrimaryContainer = AmoledOnPrimary,
    secondary = AmoledSecondary,
    onSecondary = AmoledOnSecondary,
    secondaryContainer = AmoledSurface,
    onSecondaryContainer = AmoledOnSurface,
    tertiary = Pink80,
    onTertiary = AmoledOnPrimary,
    background = AmoledBackground,
    onBackground = AmoledOnBackground,
    surface = AmoledSurface,
    onSurface = AmoledOnSurface,
    surfaceVariant = AmoledSurface,
    onSurfaceVariant = AmoledOnSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryVariant,
    onPrimaryContainer = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSurface,
    onSecondaryContainer = LightOnSurface,
    tertiary = Pink40,
    onTertiary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightOnSurface,
)

@Composable
fun VolcengineTTSTheme(
    themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()

    // 判断是否支持动态颜色（Android 12+ / API 31+）
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // 根据主题模式确定是否使用深色主题
    val useDarkTheme = when (themeMode) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AMOLED -> true
    }

    // 选择颜色方案
    val colorScheme = when {
        // AMOLED模式：使用纯黑主题（不使用动态颜色）
        themeMode == ThemeMode.AMOLED -> AmoledColorScheme

        // 动态颜色模式（支持三星One UI的多色彩方案）
        useDynamicColor && supportsDynamicColor -> {
            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        // 静态颜色模式
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}