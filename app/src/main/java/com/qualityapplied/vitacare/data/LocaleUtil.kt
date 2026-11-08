package com.qualityapplied.vitacare.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * Returns a Context that uses the language chosen inside the app (EN / SI / TA).
 * Needed by background code (alarms, ViewModel) where the Activity context is not available.
 */
fun localizedContext(ctx: Context): Context {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return ctx
    val first = locales.get(0) ?: return ctx
    val cfg = Configuration(ctx.resources.configuration)
    cfg.setLocale(first)
    return ctx.createConfigurationContext(cfg)
}

/** Looks up a string resource by name, "" when missing. */
fun stringByName(ctx: Context, name: String): String {
    val id = ctx.resources.getIdentifier(name, "string", ctx.packageName)
    return if (id != 0) ctx.getString(id) else ""
}
