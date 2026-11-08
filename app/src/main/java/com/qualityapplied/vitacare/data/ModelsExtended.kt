package com.qualityapplied.vitacare.data

import org.json.JSONObject

data class BookingItem(
    val id: String,
    val userId: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String,
    val appointmentDate: String,
    val timeSlot: String,
    val status: String,
    val notes: String,
    val createdAt: String = "",
) {
    companion object {
        fun fromJson(o: JSONObject) = BookingItem(
            id = o.optString("_id", o.optString("id")),
            userId = o.optString("userId"),
            doctorId = o.optString("doctorId"),
            doctorName = o.optString("doctorName"),
            doctorSpecialty = o.optString("doctorSpecialty"),
            appointmentDate = o.optString("appointmentDate"),
            timeSlot = o.optString("timeSlot"),
            status = o.optString("status", "Confirmed"),
            notes = o.optString("notes", ""),
            createdAt = o.optString("createdAt", ""),
        )
    }
}

data class ReminderItem(
    val id: String,
    val userId: String,
    val medicineName: String,
    val dosage: String,
    val reminderTime: String,
    val repeatDays: List<String>,
    val isActive: Boolean,
) {
    companion object {
        fun fromJson(o: JSONObject): ReminderItem {
            val daysArr = o.optJSONArray("repeatDays")
            val daysList = mutableListOf<String>()
            if (daysArr != null) {
                for (i in 0 until daysArr.length()) {
                    daysList.add(daysArr.getString(i))
                }
            } else {
                daysList.add("Daily")
            }
            return ReminderItem(
                id = o.optString("_id", o.optString("id")),
                userId = o.optString("userId"),
                medicineName = o.optString("medicineName"),
                dosage = o.optString("dosage"),
                reminderTime = o.optString("reminderTime"),
                repeatDays = daysList,
                isActive = o.optBoolean("isActive", true),
            )
        }
    }
}

data class LabReportItem(
    val id: String,
    val userId: String,
    val reportTitle: String,
    val fileUrl: String,
    val category: String,
    val uploadDate: String,
    val notes: String,
) {
    companion object {
        fun fromJson(o: JSONObject) = LabReportItem(
            id = o.optString("_id", o.optString("id")),
            userId = o.optString("userId"),
            reportTitle = o.optString("reportTitle"),
            fileUrl = o.optString("fileUrl"),
            category = o.optString("category", "General"),
            uploadDate = o.optString("uploadDate", o.optString("createdAt", "")),
            notes = o.optString("notes", ""),
        )
    }
}

data class DoctorReview(
    val id: String,
    val userId: String,
    val userName: String,
    val doctorId: String,
    val rating: Double,
    val reviewText: String,
    val createdAt: String = "",
) {
    companion object {
        fun fromJson(o: JSONObject) = DoctorReview(
            id = o.optString("_id", o.optString("id")),
            userId = o.optString("userId"),
            userName = o.optString("userName", "Patient"),
            doctorId = o.optString("doctorId"),
            rating = o.optDouble("rating", 5.0),
            reviewText = o.optString("reviewText"),
            createdAt = o.optString("createdAt", ""),
        )
    }
}

data class DoctorReviewsResult(
    val reviews: List<DoctorReview>,
    val avgRating: Double,
    val count: Int,
)
