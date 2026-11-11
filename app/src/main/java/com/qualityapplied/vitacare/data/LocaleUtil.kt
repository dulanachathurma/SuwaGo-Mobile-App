package com.qualityapplied.vitacare.data

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Manages in-app locale state and persistence without triggering Activity recreate().
 * Recomposition in Jetpack Compose instantly updates all translated strings smoothly.
 */
object LocaleManager {
    private const val PREF_NAME = "vitacare_locale_prefs"
    private const val KEY_LANG = "app_language"

    var currentLocale by mutableStateOf("en")
        private set

    fun init(context: Context) {
        currentLocale = getSavedLocale(context)
    }

    fun getSavedLocale(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LANG, null)
        if (!saved.isNullOrBlank()) return saved

        val sysLang = Locale.getDefault().language
        return if (sysLang == "si" || sysLang == "ta") sysLang else "en"
    }

    fun setLocale(context: Context, langCode: String) {
        currentLocale = langCode
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, langCode).apply()
    }

    fun getLocalizedContext(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}

/**
 * Returns a Context that uses the language chosen inside the app (EN / SI / TA).
 * Needed by background code (alarms, ViewModel) where the Activity context is not available.
 */
fun localizedContext(ctx: Context): Context {
    val lang = LocaleManager.getSavedLocale(ctx)
    return LocaleManager.getLocalizedContext(ctx, lang)
}

/** Looks up a string resource by name, "" when missing. */
fun stringByName(ctx: Context, name: String): String {
    val localized = localizedContext(ctx)
    val id = localized.resources.getIdentifier(name, "string", localized.packageName)
    return if (id != 0) localized.getString(id) else ""
}
