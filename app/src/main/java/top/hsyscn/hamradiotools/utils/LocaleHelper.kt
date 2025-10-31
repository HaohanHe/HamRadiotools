package top.hsyscn.hamradiotools.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import top.hsyscn.hamradiotools.data.AppLanguage
import java.util.*

/**
 * 语言环境辅助类
 * 用于处理应用的多语言切换和智能语言检测
 */
object LocaleHelper {
    
    /**
     * 根据AppLanguage设置应用语言环境
     */
    fun setLocale(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.FOLLOW_SYSTEM -> getSystemLocale()
            AppLanguage.CHINESE -> Locale.CHINESE
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.JAPANESE -> Locale.JAPANESE
        }
        
        return updateResources(context, locale)
    }
    
    /**
     * 为Activity设置语言环境并重新创建Activity
     */
    fun setLocaleForActivity(activity: android.app.Activity, language: AppLanguage) {
        val locale = when (language) {
            AppLanguage.FOLLOW_SYSTEM -> getSystemLocale()
            AppLanguage.CHINESE -> Locale.CHINESE
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.JAPANESE -> Locale.JAPANESE
        }
        
        Locale.setDefault(locale)
        val configuration = Configuration(activity.resources.configuration)
        configuration.setLocale(locale)
        
        activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
        
        // 重新创建Activity以应用新的语言设置
        activity.recreate()
    }
    
    /**
     * 获取系统默认语言环境
     */
    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            Resources.getSystem().configuration.locale
        }
    }
    
    /**
     * 更新Context的语言资源
     */
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }
    
    /**
     * 智能检测系统语言并返回对应的AppLanguage
     * 如果系统语言不在支持列表中，返回英语作为默认语言
     */
    fun detectSystemLanguage(): AppLanguage {
        val systemLocale = getSystemLocale()
        val language = systemLocale.language
        
        return when (language) {
            "zh" -> AppLanguage.CHINESE
            "ja" -> AppLanguage.JAPANESE
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.ENGLISH // 默认使用英语
        }
    }
    
    /**
     * 获取语言对应的Locale
     */
    fun getLocaleForLanguage(language: AppLanguage): Locale {
        return when (language) {
            AppLanguage.FOLLOW_SYSTEM -> getSystemLocale()
            AppLanguage.CHINESE -> Locale.CHINESE
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.JAPANESE -> Locale.JAPANESE
        }
    }
    
    /**
     * 检查当前语言是否为从右到左的语言
     */
    fun isRTL(language: AppLanguage): Boolean {
        val locale = getLocaleForLanguage(language)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.text.TextUtils.getLayoutDirectionFromLocale(locale) == android.view.View.LAYOUT_DIRECTION_RTL
        } else {
            false
        }
    }
}