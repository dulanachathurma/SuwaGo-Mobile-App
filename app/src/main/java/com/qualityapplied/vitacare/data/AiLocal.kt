package com.qualityapplied.vitacare.data

import android.content.Context
import com.qualityapplied.vitacare.R
import java.util.Locale

/**
 * Small offline helper used when the AI server cannot be reached.
 * It spots illness words (English keywords or the translated illness names) and suggests the specialist.
 */
object AiLocal {
    private val KEYWORDS = listOf(
        "fever" to "fever", "temperature" to "fever",
        "cold" to "coldFlu", "flu" to "coldFlu", "runny nose" to "coldFlu",
        "dengue" to "dengue", "tired" to "fatigue", "fatigue" to "fatigue", "body pain" to "fatigue",
        "diabetes" to "diabetes", "sugar" to "diabetes", "thyroid" to "thyroid",
        "obes" to "obesity", "overweight" to "obesity", "weight loss" to "obesity",
        "blood pressure" to "hypertension", "hypertension" to "hypertension",
        "cholesterol" to "cholesterol", "chest" to "chestPain",
        "palpitation" to "palpitations", "heartbeat" to "palpitations", "heart rate" to "palpitations",
        "asthma" to "asthma", "cough" to "chronicCough", "breath" to "breathless",
        "acne" to "acne", "pimple" to "acne", "rash" to "skinRash", "itch" to "skinRash",
        "eczema" to "eczema", "fungal" to "fungal", "hair loss" to "hairLoss",
        "blurred" to "blurredVision", "vision" to "blurredVision", "red eye" to "eyeInfection",
        "eye infection" to "eyeInfection", "cataract" to "cataract",
        "tooth" to "toothache", "teeth" to "toothache", "gum" to "gumDisease",
        "earache" to "earache", "ear pain" to "earache", "sinus" to "sinusitis", "throat" to "soreThroat",
        "migraine" to "migraine", "headache" to "migraine", "dizz" to "dizziness",
        "seizure" to "epilepsy", "epilep" to "epilepsy",
        "depress" to "depression", "sad" to "depression", "anxi" to "anxiety", "stress" to "anxiety",
        "sleep" to "insomnia", "insomnia" to "insomnia",
        "gastritis" to "gastritis", "acidity" to "gastritis", "stomach" to "gastritis",
        "diarrh" to "diarrhea", "vomit" to "diarrhea", "liver" to "liver",
        "kidney" to "kidneyDisease", "urine" to "urinaryInfection", "urinary" to "urinaryInfection",
        "back pain" to "backPain", "backache" to "backPain", "joint" to "arthritis", "arthritis" to "arthritis",
        "knee" to "arthritis", "fracture" to "fracture", "broken bone" to "fracture",
        "child" to "childFever", "baby" to "childFever", "pregnan" to "pregnancy",
        "period" to "menstrual", "menstr" to "menstrual",
    )

    fun reply(ctx: Context, text: String, user: User?): String {
        val q = text.lowercase()
        val lines = ArrayList<String>()

        val h = user?.heightCm
        val w = user?.weightKg
        if (q.contains("bmi") && h != null && w != null && h > 0) {
            val m = h / 100.0
            val bmi = w / (m * m)
            val cat = when {
                bmi < 18.5 -> "underweight"
                bmi < 25 -> "normal"
                bmi < 30 -> "overweight"
                else -> "obese"
            }
            lines.add(
                ctx.getString(R.string.ai_bmiIntro) + ": " + String.format(Locale.US, "%.1f", bmi) +
                    " — " + stringByName(ctx, "bmi_$cat"),
            )
        }

        val ids = LinkedHashSet<String>()
        for ((word, id) in KEYWORDS) if (q.contains(word)) ids.add(id)
        for (c in CONDITIONS) {
            val name = stringByName(ctx, "fd_c_" + c.id).lowercase()
            if (name.isNotEmpty() && q.contains(name)) ids.add(c.id)
        }
        val matched = ids.mapNotNull { id -> CONDITIONS.firstOrNull { it.id == id } }.take(3)

        if (matched.isNotEmpty()) {
            lines.add(ctx.getString(R.string.ai_fallbackSee))
            matched.forEach { c ->
                lines.add(c.icon + " " + stringByName(ctx, "fd_c_" + c.id) + " → " + stringByName(ctx, "fd_s_" + c.specialty))
            }
            if (matched.any { it.urgent }) lines.add("🚨 " + ctx.getString(R.string.fd_urgent))
            lines.add(ctx.getString(R.string.ai_fallbackMap))
        }
        if (lines.isEmpty()) lines.add(ctx.getString(R.string.ai_fallbackDefault))
        return lines.joinToString("\n")
    }
}
