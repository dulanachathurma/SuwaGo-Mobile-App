package com.qualityapplied.vitacare.data

import android.content.Context
import org.json.JSONObject

/**
 * Keeps the login (JWT + user) on the phone so the app opens straight on the dashboard.
 * The session is only removed when the user presses Logout (or the server says the token is invalid).
 */
class SessionStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("vitacare_session", Context.MODE_PRIVATE)

    val token: String?
        get() = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    fun loadUser(): User? {
        if (token == null) return null
        val raw = prefs.getString(KEY_USER, null) ?: return null
        return try {
            User.fromJson(JSONObject(raw))
        } catch (_: Exception) {
            null
        }
    }

    fun save(token: String, user: User) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER, user.toJson().toString())
            .apply()
    }

    fun saveUser(user: User) {
        prefs.edit().putString(KEY_USER, user.toJson().toString()).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER).apply()
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER = "user"
    }
}
