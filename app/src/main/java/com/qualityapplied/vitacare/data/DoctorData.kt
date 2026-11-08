package com.qualityapplied.vitacare.data

// Generated from the web app (frontend/src/utils/doctorData.js)

data class Specialty(val id: String, val icon: String, val query: String)

data class Condition(val id: String, val icon: String, val specialty: String, val urgent: Boolean)

val SPECIALTIES = listOf(
    Specialty("generalPhysician", "🩺", "general physician"),
    Specialty("cardiologist", "❤️", "cardiologist"),
    Specialty("endocrinologist", "🍬", "endocrinologist"),
    Specialty("pulmonologist", "🫁", "pulmonologist"),
    Specialty("dermatologist", "🧴", "dermatologist"),
    Specialty("ophthalmologist", "👁️", "eye doctor ophthalmologist"),
    Specialty("dentist", "🦷", "dentist"),
    Specialty("ent", "👂", "ENT specialist"),
    Specialty("neurologist", "🧠", "neurologist"),
    Specialty("psychiatrist", "💭", "psychiatrist"),
    Specialty("gastroenterologist", "🍽️", "gastroenterologist"),
    Specialty("nephrologist", "🫘", "nephrologist kidney"),
    Specialty("orthopedic", "🦴", "orthopaedic surgeon"),
    Specialty("pediatrician", "👶", "pediatrician"),
    Specialty("gynecologist", "🤰", "gynecologist")
)

val CONDITIONS = listOf(
    Condition("fever", "🌡️", "generalPhysician", false),
    Condition("coldFlu", "🤧", "generalPhysician", false),
    Condition("dengue", "🦟", "generalPhysician", false),
    Condition("fatigue", "😴", "generalPhysician", false),
    Condition("diabetes", "🩸", "endocrinologist", false),
    Condition("thyroid", "🦋", "endocrinologist", false),
    Condition("obesity", "⚖️", "endocrinologist", false),
    Condition("hypertension", "💢", "cardiologist", false),
    Condition("cholesterol", "🧪", "cardiologist", false),
    Condition("chestPain", "💔", "cardiologist", true),
    Condition("palpitations", "💓", "cardiologist", false),
    Condition("asthma", "🌬️", "pulmonologist", false),
    Condition("chronicCough", "😮‍💨", "pulmonologist", false),
    Condition("breathless", "😤", "pulmonologist", true),
    Condition("acne", "🧼", "dermatologist", false),
    Condition("skinRash", "🔴", "dermatologist", false),
    Condition("eczema", "🩹", "dermatologist", false),
    Condition("fungal", "🍄", "dermatologist", false),
    Condition("hairLoss", "💇", "dermatologist", false),
    Condition("blurredVision", "👓", "ophthalmologist", false),
    Condition("eyeInfection", "👁️", "ophthalmologist", false),
    Condition("cataract", "☁️", "ophthalmologist", false),
    Condition("toothache", "🦷", "dentist", false),
    Condition("gumDisease", "🪥", "dentist", false),
    Condition("earache", "👂", "ent", false),
    Condition("sinusitis", "👃", "ent", false),
    Condition("soreThroat", "🗣️", "ent", false),
    Condition("migraine", "🤕", "neurologist", false),
    Condition("dizziness", "💫", "neurologist", false),
    Condition("epilepsy", "⚡", "neurologist", false),
    Condition("depression", "😔", "psychiatrist", false),
    Condition("anxiety", "😰", "psychiatrist", false),
    Condition("insomnia", "🌙", "psychiatrist", false),
    Condition("gastritis", "🔥", "gastroenterologist", false),
    Condition("diarrhea", "🚽", "gastroenterologist", false),
    Condition("liver", "🍺", "gastroenterologist", false),
    Condition("kidneyDisease", "🫘", "nephrologist", false),
    Condition("urinaryInfection", "🚻", "nephrologist", false),
    Condition("backPain", "🧍", "orthopedic", false),
    Condition("arthritis", "🦵", "orthopedic", false),
    Condition("fracture", "🩻", "orthopedic", false),
    Condition("childFever", "🧒", "pediatrician", false),
    Condition("childGrowth", "📏", "pediatrician", false),
    Condition("menstrual", "🌸", "gynecologist", false),
    Condition("pregnancy", "🤰", "gynecologist", false)
)
