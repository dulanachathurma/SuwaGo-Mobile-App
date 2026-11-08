package com.qualityapplied.vitacare.data

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int?,
    val heightCm: Double?,
    val weightKg: Double?,
    val avatarUrl: String,
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("email", email)
        put("age", age ?: JSONObject.NULL)
        put("heightCm", heightCm ?: JSONObject.NULL)
        put("weightKg", weightKg ?: JSONObject.NULL)
        put("avatarUrl", avatarUrl)
    }

    companion object {
        fun fromJson(o: JSONObject): User = User(
            id = o.optString("id"),
            name = o.optString("name"),
            email = o.optString("email"),
            age = o.optNullableDouble("age")?.toInt(),
            heightCm = o.optNullableDouble("heightCm"),
            weightKg = o.optNullableDouble("weightKg"),
            avatarUrl = if (o.isNull("avatarUrl")) "" else o.optString("avatarUrl", ""),
        )
    }
}

data class HealthLog(
    val id: String,
    val weightKg: Double,
    val heightCm: Double,
    val bmi: Double,
    val category: String,
    val createdAt: Date,
) {
    companion object {
        fun fromJson(o: JSONObject) = HealthLog(
            id = o.optString("_id"),
            weightKg = o.optDouble("weightKg"),
            heightCm = o.optDouble("heightCm"),
            bmi = o.optDouble("bmi"),
            category = o.optString("category", "normal"),
            createdAt = parseIsoDate(o.optString("createdAt")),
        )
    }
}

data class Metric(
    val id: String,
    val type: String,
    val value: Double,
    val value2: Double?,
    val status: String,
    val urgent: Boolean,
    val note: String,
    val measuredAt: Date,
) {
    companion object {
        fun fromJson(o: JSONObject) = Metric(
            id = o.optString("_id"),
            type = o.optString("type"),
            value = o.optDouble("value"),
            value2 = o.optNullableDouble("value2"),
            status = o.optString("status", "normal"),
            urgent = o.optBoolean("urgent", false),
            note = o.optString("note", ""),
            measuredAt = parseIsoDate(o.optString("measuredAt")),
        )
    }
}

/** One chat bubble of the AI assistant. [offline] = answered by the built-in fallback. */
data class ChatMsg(val role: String, val text: String, val offline: Boolean = false)

/** A clinic / hospital / doctor found on OpenStreetMap near the user. */
data class NearbyPlace(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val distanceM: Double,
    val kind: String,
    val match: Boolean,
)

data class AuthResult(val token: String, val user: User)

data class AddMetricResult(val metric: Metric, val specialty: String?)

/** Reads a number that may be missing or JSON null. */
fun JSONObject.optNullableDouble(key: String): Double? =
    if (!has(key) || isNull(key)) null else optDouble(key).takeUnless { it.isNaN() }

fun parseIsoDate(value: String): Date {
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'")
    for (p in patterns) {
        try {
            val f = SimpleDateFormat(p, Locale.US)
            f.timeZone = TimeZone.getTimeZone("UTC")
            val d = f.parse(value)
            if (d != null) return d
        } catch (_: Exception) {
        }
    }
    return Date()
}

/** 120.0 -> "120", 36.6 -> "36.6" */
fun Double.clean(): String =
    if (this % 1.0 == 0.0) this.toLong().toString() else this.toString()
