package top.hsyscn.hamradiotools.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import top.hsyscn.hamradiotools.data.AppTheme

// 标准主题配色方案
private val StandardLightColorScheme = lightColorScheme(
    primary = StandardPrimary,
    background = StandardBackground,
    surface = StandardBackground,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val StandardDarkColorScheme = darkColorScheme(
    primary = Color(0xFF6B73FF),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// 鲜艳绿主题配色方案
private val VibrantGreenLightColorScheme = lightColorScheme(
    primary = VibrantGreenPrimary,
    background = VibrantGreenBackground,
    surface = VibrantGreenBackground,
    secondary = Color(0xFF2E8B7A),
    tertiary = Color(0xFF4DD0C7)
)

private val VibrantGreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DD0C7),
    background = Color(0xFF0D1F1C),
    surface = Color(0xFF1A2E2A),
    secondary = Color(0xFF2E8B7A),
    tertiary = Color(0xFF4DD0C7)
)

// 少女粉主题配色方案
private val GirlPinkLightColorScheme = lightColorScheme(
    primary = GirlPinkPrimary,
    background = GirlPinkBackground,
    surface = GirlPinkBackground,
    secondary = Color(0xFFE6527A),
    tertiary = Color(0xFFFF99BB)
)

private val GirlPinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF99BB),
    background = Color(0xFF1F0D14),
    surface = Color(0xFF2E1A21),
    secondary = Color(0xFFE6527A),
    tertiary = Color(0xFFFF99BB)
)

@Composable
fun HamRadioToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.STANDARD,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // 禁用动态颜色以使用自定义主题
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (appTheme) {
                AppTheme.STANDARD -> if (darkTheme) StandardDarkColorScheme else StandardLightColorScheme
                AppTheme.VIBRANT_GREEN -> if (darkTheme) VibrantGreenDarkColorScheme else VibrantGreenLightColorScheme
                AppTheme.GIRL_PINK -> if (darkTheme) GirlPinkDarkColorScheme else GirlPinkLightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}