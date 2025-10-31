package top.hsyscn.hamradiotools.data

import androidx.compose.ui.graphics.Color

/**
 * 应用主题枚举
 */
enum class AppTheme {
    STANDARD,      // 标准主题
    VIBRANT_GREEN, // 鲜艳绿主题
    GIRL_PINK      // 少女粉主题
}

/**
 * 应用语言枚举
 */
enum class AppLanguage {
    FOLLOW_SYSTEM, // 跟随系统
    CHINESE,       // 中文
    ENGLISH,       // English
    JAPANESE       // 日本語
}



val AppTheme.getPrimaryColor: Color
    get() = when (this) {
        AppTheme.STANDARD -> Color(0xFF6750A4)
        AppTheme.VIBRANT_GREEN -> Color(0xFF00C853)
        AppTheme.GIRL_PINK -> Color(0xFFE91E63)
    }



/**
 * 应用设置数据类
 */
data class AppSettings(
    val theme: AppTheme = AppTheme.STANDARD,
    val language: AppLanguage = AppLanguage.FOLLOW_SYSTEM,
    val appVersion: String = "1.1.0"
)

/**
 * SharedPreferences键值常量
 */
object SettingsKeys {
    const val THEME_PREFERENCE = "theme_preference"
    const val LANGUAGE_PREFERENCE = "language_preference"
    const val FIRST_LAUNCH = "first_launch"
    const val LAST_UPDATED = "last_updated"
}

/**
 * 默认设置值
 */
object DefaultSettings {
    const val DEFAULT_THEME = "STANDARD"
    const val DEFAULT_LANGUAGE = "FOLLOW_SYSTEM"
    const val DEFAULT_FIRST_LAUNCH = true
}