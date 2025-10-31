package top.hsyscn.hamradiotools.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.hsyscn.hamradiotools.data.AppLanguage
import top.hsyscn.hamradiotools.data.AppSettings
import top.hsyscn.hamradiotools.data.AppTheme
import top.hsyscn.hamradiotools.data.DefaultSettings
import top.hsyscn.hamradiotools.data.SettingsKeys
import java.util.Locale

/**
 * 设置管理器
 * 负责应用设置的存储、读取和管理
 */
class SettingsManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    
    // 当前设置状态
    private val _currentSettings = MutableStateFlow(loadSettings())
    val currentSettings: StateFlow<AppSettings> = _currentSettings.asStateFlow()
    
    // 当前主题状态
    private val _currentTheme = MutableStateFlow(getTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()
    
    // 当前语言状态
    private val _currentLanguage = MutableStateFlow(getLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()
    
    /**
     * 加载设置
     */
    private fun loadSettings(): AppSettings {
        val theme = getTheme()
        val language = getLanguage()
        val version = getAppVersion()
        
        return AppSettings(
            theme = theme,
            language = language,
            appVersion = version
        )
    }
    
    /**
     * 获取当前主题
     */
    fun getTheme(): AppTheme {
        val themeName = sharedPreferences.getString(
            SettingsKeys.THEME_PREFERENCE, 
            DefaultSettings.DEFAULT_THEME
        ) ?: DefaultSettings.DEFAULT_THEME
        
        return try {
            AppTheme.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.STANDARD
        }
    }
    
    /**
     * 设置主题
     */
    fun setTheme(theme: AppTheme) {
        sharedPreferences.edit()
            .putString(SettingsKeys.THEME_PREFERENCE, theme.name)
            .putLong(SettingsKeys.LAST_UPDATED, System.currentTimeMillis())
            .apply()
        
        _currentTheme.value = theme
        _currentSettings.value = _currentSettings.value.copy(theme = theme)
    }
    
    /**
     * 获取当前语言
     */
    fun getLanguage(): AppLanguage {
        val languageName = sharedPreferences.getString(
            SettingsKeys.LANGUAGE_PREFERENCE,
            DefaultSettings.DEFAULT_LANGUAGE
        ) ?: DefaultSettings.DEFAULT_LANGUAGE
        
        return try {
            AppLanguage.valueOf(languageName)
        } catch (e: IllegalArgumentException) {
            AppLanguage.FOLLOW_SYSTEM
        }
    }
    
    /**
     * 设置语言
     */
    fun setLanguage(language: AppLanguage) {
        sharedPreferences.edit()
            .putString(SettingsKeys.LANGUAGE_PREFERENCE, language.name)
            .putLong(SettingsKeys.LAST_UPDATED, System.currentTimeMillis())
            .apply()
        
        _currentLanguage.value = language
        _currentSettings.value = _currentSettings.value.copy(language = language)
    }
    
    /**
     * 获取应用版本
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.1.0"
        } catch (e: Exception) {
            "1.1.0"
        }
    }
    
    /**
     * 检测系统语言并返回对应的应用语言
     */
    fun detectSystemLanguage(): AppLanguage {
        val systemLocale = Locale.getDefault().language
        return when (systemLocale) {
            "zh" -> AppLanguage.CHINESE
            "ja" -> AppLanguage.JAPANESE
            else -> AppLanguage.ENGLISH
        }
    }
    
    /**
     * 获取实际应用的语言（考虑跟随系统的情况）
     */
    fun getEffectiveLanguage(): AppLanguage {
        val currentLang = getLanguage()
        return if (currentLang == AppLanguage.FOLLOW_SYSTEM) {
            detectSystemLanguage()
        } else {
            currentLang
        }
    }
    
    /**
     * 是否是首次启动
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(
            SettingsKeys.FIRST_LAUNCH,
            DefaultSettings.DEFAULT_FIRST_LAUNCH
        )
    }
    
    /**
     * 设置首次启动标记
     */
    fun setFirstLaunch(isFirst: Boolean) {
        sharedPreferences.edit()
            .putBoolean(SettingsKeys.FIRST_LAUNCH, isFirst)
            .apply()
    }
    
    /**
     * 重置所有设置到默认值
     */
    fun resetToDefaults() {
        sharedPreferences.edit().clear().apply()
        _currentSettings.value = loadSettings()
        _currentTheme.value = AppTheme.STANDARD
        _currentLanguage.value = AppLanguage.FOLLOW_SYSTEM
    }
}