package com.qualityapplied.vitacare.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Error returned by the API. [code] is the machine-readable code (translated in the UI). */
class ApiException(val code: String, val status: Int) : Exception(code)

object ApiClient {
    /** Your deployed backend on Vercel with trailing slash. */
    const val BASE_URL = "https://suwago.vercel.app/api/"

    /** Supplies the saved JWT for every authenticated request. */
    @Volatile
    var tokenProvider: () -> String? = { null }

    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()

    // Vercel serverless functions can be slow on a cold start, so timeouts are generous.
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** Shared HTTP client (used by the nearby-places search too). */
    val httpClient: OkHttpClient get() = client

    private suspend fun call(
        method: String,
        path: String,
        body: JSONObject? = null,
        auth: Boolean = true,
    ): JSONObject = withContext(Dispatchers.IO) {
        // Remove leading slash if path starts with slash to ensure clean URL concatenation
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        val fullUrl = BASE_URL + cleanPath

        val builder = Request.Builder()
            .url(fullUrl)
            .header("Accept", "application/json")

        if (auth) tokenProvider()?.let { builder.header("Authorization", "Bearer $it") }

        val payload = (body ?: JSONObject()).toString().toRequestBody(JSON_TYPE)
        when (method) {
            "GET" -> builder.get()
            "POST" -> builder.post(payload)
            "PUT" -> builder.put(payload)
            "DELETE" -> builder.delete()
        }

        try {
            client.newCall(builder.build()).execute().use { resp ->
                val text = resp.body?.string().orEmpty()
                val json = try {
                    JSONObject(text)
                } catch (_: Exception) {
                    JSONObject()
                }
                if (!resp.isSuccessful) {
                    val codeFromJson = json.optString("code")
                    val errorFromJson = json.optString("error")
                    val messageFromJson = json.optString("message")
                    val msgFromJson = json.optString("msg")

                    val errCode = when {
                        messageFromJson.isNotBlank() && (codeFromJson.isBlank() || codeFromJson == "SERVER_ERROR") -> messageFromJson
                        errorFromJson.isNotBlank() && (codeFromJson.isBlank() || codeFromJson == "SERVER_ERROR") -> errorFromJson
                        codeFromJson.isNotBlank() -> codeFromJson
                        errorFromJson.isNotBlank() -> errorFromJson
                        messageFromJson.isNotBlank() -> messageFromJson
                        msgFromJson.isNotBlank() -> msgFromJson
                        else -> when (resp.code) {
                            400 -> "INVALID_INPUT"
                            401 -> "INVALID_CREDENTIALS"
                            409 -> "EMAIL_EXISTS"
                            else -> "SERVER_ERROR"
                        }
                    }
                    throw ApiException(errCode, resp.code)
                }
                json
            }
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw ApiException("NETWORK_ERROR", 0)
        }
    }

    // ---------- Auth ----------

    suspend fun register(name: String, email: String, password: String): AuthResult {
        val res = call(
            "POST", "auth/register",
            JSONObject().put("name", name.trim()).put("email", email.trim()).put("password", password),
            auth = false,
        )
        return AuthResult(res.getString("token"), User.fromJson(res.getJSONObject("user")))
    }

    suspend fun login(email: String, password: String): AuthResult {
        val res = call(
            "POST", "auth/login",
            JSONObject().put("email", email.trim()).put("password", password),
            auth = false,
        )
        return AuthResult(res.getString("token"), User.fromJson(res.getJSONObject("user")))
    }

    // ---------- Profile ----------

    suspend fun getProfile(): User =
        User.fromJson(call("GET", "users/profile").getJSONObject("user"))

    suspend fun updateProfile(
        name: String,
        age: String,
        heightCm: String,
        weightKg: String,
        avatarUrl: String,
    ): User {
        val body = JSONObject()
            .put("name", name.trim())
            .put("age", age.trim())
            .put("heightCm", heightCm.trim())
            .put("weightKg", weightKg.trim())
            .put("avatarUrl", avatarUrl)
        return User.fromJson(call("PUT", "users/profile", body).getJSONObject("user"))
    }

    // ---------- BMI logs ----------

    suspend fun getLogs(): List<HealthLog> {
        val arr = call("GET", "health/logs").getJSONArray("logs")
        return (0 until arr.length()).map { HealthLog.fromJson(arr.getJSONObject(it)) }
    }

    /** Returns the updated user (the server keeps the profile in sync with the latest entry). */
    suspend fun addLog(weightKg: String, heightCm: String): User {
        val body = JSONObject().put("weightKg", weightKg.trim()).put("heightCm", heightCm.trim())
        return User.fromJson(call("POST", "health/logs", body).getJSONObject("user"))
    }

    suspend fun deleteLog(id: String) {
        call("DELETE", "health/logs/$id")
    }

    // ---------- Health metrics ----------

    suspend fun getMetrics(): List<Metric> {
        val arr = call("GET", "metrics").getJSONArray("metrics")
        return (0 until arr.length()).map { Metric.fromJson(arr.getJSONObject(it)) }
    }

    suspend fun addMetric(type: String, value: String, value2: String, note: String): AddMetricResult {
        val body = JSONObject().put("type", type).put("value", value.trim()).put("note", note.trim())
        if (type == "bloodPressure") body.put("value2", value2.trim())
        val res = call("POST", "metrics", body)
        val specialty = if (res.isNull("specialty")) null else res.optString("specialty")
        return AddMetricResult(Metric.fromJson(res.getJSONObject("metric")), specialty)
    }

    suspend fun deleteMetric(id: String) {
        call("DELETE", "metrics/$id")
    }

    // ---------- Password ----------

    /** Needs PUT /api/users/password on the backend (see BACKEND_ADDITIONS.md). */
    suspend fun changePassword(current: String, newPassword: String) {
        try {
            call(
                "PUT", "users/password",
                JSONObject().put("currentPassword", current).put("newPassword", newPassword),
            )
        } catch (e: ApiException) {
            if (e.status == 404 || e.status == 405) throw ApiException("NOT_SUPPORTED", e.status)
            throw e
        }
    }

    // ---------- AI assistant ----------

    /** Needs POST /api/ai/chat on the backend (see BACKEND_ADDITIONS.md). Returns plain text. */
    suspend fun chat(messages: List<ChatMsg>, language: String, profile: JSONObject?): String {
        val arr = JSONArray()
        messages.forEach { arr.put(JSONObject().put("role", it.role).put("content", it.text)) }
        val body = JSONObject().put("messages", arr).put("language", language)
        if (profile != null) body.put("profile", profile)
        val res = call("POST", "ai/chat", body)
        val reply = res.optString("reply", "")
        if (reply.isBlank()) throw ApiException("SERVER_ERROR", 500)
        return reply
    }

    // ---------- Images ----------

    /** Downloads an avatar that is stored as a normal http(s) link. */
    suspend fun fetchBytes(url: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            client.newCall(Request.Builder().url(url).build()).execute().use { r ->
                if (r.isSuccessful) r.body?.bytes() else null
            }
        } catch (_: Exception) {
            null
        }
    }

    // ---------- Bookings ----------

    suspend fun getBookings(): List<BookingItem> {
        val arr = call("GET", "bookings").getJSONArray("bookings")
        return (0 until arr.length()).map { BookingItem.fromJson(arr.getJSONObject(it)) }
    }

    suspend fun createBooking(
        doctorId: String,
        doctorName: String,
        doctorSpecialty: String,
        appointmentDate: String,
        timeSlot: String,
        notes: String = "",
    ): BookingItem {
        val body = JSONObject()
            .put("doctorId", doctorId)
            .put("doctorName", doctorName)
            .put("doctorSpecialty", doctorSpecialty)
            .put("appointmentDate", appointmentDate)
            .put("timeSlot", timeSlot)
            .put("notes", notes)
        val res = call("POST", "bookings", body)
        return BookingItem.fromJson(res.getJSONObject("booking"))
    }

    suspend fun updateBookingStatus(id: String, status: String): BookingItem {
        val body = JSONObject().put("status", status)
        val res = call("PATCH", "bookings/$id/status", body)
        return BookingItem.fromJson(res.getJSONObject("booking"))
    }

    suspend fun deleteBooking(id: String) {
        call("DELETE", "bookings/$id")
    }

    // ---------- Reminders ----------

    suspend fun getReminders(): List<ReminderItem> {
        val arr = call("GET", "reminders").getJSONArray("reminders")
        return (0 until arr.length()).map { ReminderItem.fromJson(arr.getJSONObject(it)) }
    }

    suspend fun createReminder(
        medicineName: String,
        dosage: String,
        reminderTime: String,
        repeatDays: List<String> = listOf("Daily"),
    ): ReminderItem {
        val arr = JSONArray()
        repeatDays.forEach { arr.put(it) }
        val body = JSONObject()
            .put("medicineName", medicineName.trim())
            .put("dosage", dosage.trim())
            .put("reminderTime", reminderTime.trim())
            .put("repeatDays", arr)
        val res = call("POST", "reminders", body)
        return ReminderItem.fromJson(res.getJSONObject("reminder"))
    }

    suspend fun toggleReminder(id: String, isActive: Boolean? = null): ReminderItem {
        val body = JSONObject()
        if (isActive != null) body.put("isActive", isActive)
        val res = call("PUT", "reminders/$id", body)
        return ReminderItem.fromJson(res.getJSONObject("reminder"))
    }

    suspend fun deleteReminder(id: String) {
        call("DELETE", "reminders/$id")
    }

    // ---------- Lab Reports ----------

    suspend fun getReports(): List<LabReportItem> {
        val arr = call("GET", "reports").getJSONArray("reports")
        return (0 until arr.length()).map { LabReportItem.fromJson(arr.getJSONObject(it)) }
    }

    suspend fun uploadReport(
        reportTitle: String,
        fileUrl: String,
        category: String = "General",
        notes: String = "",
    ): LabReportItem {
        val body = JSONObject()
            .put("reportTitle", reportTitle.trim())
            .put("fileUrl", fileUrl)
            .put("category", category)
            .put("notes", notes.trim())
        val res = call("POST", "reports", body)
        return LabReportItem.fromJson(res.getJSONObject("report"))
    }

    suspend fun deleteReport(id: String) {
        call("DELETE", "reports/$id")
    }

    // ---------- Doctor Reviews ----------

    suspend fun getDoctorReviews(doctorId: String): DoctorReviewsResult {
        val res = call("GET", "reviews/doctor/$doctorId", auth = false)
        val arr = res.getJSONArray("reviews")
        val reviews = (0 until arr.length()).map { DoctorReview.fromJson(arr.getJSONObject(it)) }
        val avgRating = res.optDouble("avgRating", 0.0)
        val count = res.optInt("count", reviews.size)
        return DoctorReviewsResult(reviews, avgRating, count)
    }

    suspend fun createReview(doctorId: String, rating: Double, reviewText: String): DoctorReview {
        val body = JSONObject()
            .put("doctorId", doctorId)
            .put("rating", rating)
            .put("reviewText", reviewText.trim())
        val res = call("POST", "reviews", body)
        return DoctorReview.fromJson(res.getJSONObject("review"))
    }
}