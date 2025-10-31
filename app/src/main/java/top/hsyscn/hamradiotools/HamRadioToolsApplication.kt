package top.hsyscn.hamradiotools

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import top.hsyscn.hamradiotools.manager.SettingsManager
import top.hsyscn.hamradiotools.utils.LocaleHelper

/**
 * HamRadiotools应用程序类
 * 负责全局配置和语言设置
 */
class HamRadioToolsApplication : Application() {
    
    private lateinit var settingsManager: SettingsManager
    
    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        
        // 应用保存的语言设置
        val savedLanguage = settingsManager.getLanguage()
        LocaleHelper.setLocale(this, savedLanguage)
    }
    
    override fun attachBaseContext(base: Context?) {
        // 在应用启动时应用语言设置
        val context = base?.let { ctx ->
            val settingsManager = SettingsManager(ctx)
            val savedLanguage = settingsManager.getLanguage()
            LocaleHelper.setLocale(ctx, savedLanguage)
        } ?: base
        
        super.attachBaseContext(context)
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // 当系统配置改变时，重新应用语言设置
        if (::settingsManager.isInitialized) {
            val savedLanguage = settingsManager.getLanguage()
            LocaleHelper.setLocale(this, savedLanguage)
        }
    }
}