package com.qualityapplied.vitacare.data

/**
 * Symptom checker rules (the old DoctorFinder.jsx idea): every ticked symptom gives points to the
 * specialists that treat it. Weights: 3 = main specialist, 2 = often, 1 = sometimes.
 */
data class SymptomRule(val id: String, val urgent: Boolean, val weights: List<Pair<String, Int>>)

data class SpecMatch(val specId: String, val percent: Int)

val SYMPTOMS = listOf(
    SymptomRule("chestPain", true, listOf("cardiologist" to 3, "pulmonologist" to 1, "generalPhysician" to 1)),
    SymptomRule("palpitations", false, listOf("cardiologist" to 3, "endocrinologist" to 1, "psychiatrist" to 1)),
    SymptomRule("shortBreath", true, listOf("pulmonologist" to 3, "cardiologist" to 2)),
    SymptomRule("cough", false, listOf("pulmonologist" to 2, "generalPhysician" to 2, "ent" to 1)),
    SymptomRule("fever", false, listOf("generalPhysician" to 3, "pulmonologist" to 1)),
    SymptomRule("headache", false, listOf("neurologist" to 3, "generalPhysician" to 1, "ophthalmologist" to 1)),
    SymptomRule("dizziness", false, listOf("neurologist" to 2, "cardiologist" to 1, "ent" to 1, "generalPhysician" to 1)),
    SymptomRule("rash", false, listOf("dermatologist" to 3, "generalPhysician" to 1)),
    SymptomRule("itching", false, listOf("dermatologist" to 3)),
    SymptomRule("jointPain", false, listOf("orthopedic" to 3, "generalPhysician" to 1)),
    SymptomRule("backPain", false, listOf("orthopedic" to 3, "neurologist" to 1)),
    SymptomRule("stomachPain", false, listOf("gastroenterologist" to 3, "generalPhysician" to 1)),
    SymptomRule("nausea", false, listOf("gastroenterologist" to 3, "generalPhysician" to 1)),
    SymptomRule("thirst", false, listOf("endocrinologist" to 3, "generalPhysician" to 1)),
    SymptomRule("urination", false, listOf("endocrinologist" to 2, "nephrologist" to 2, "generalPhysician" to 1)),
    SymptomRule("anxiety", false, listOf("psychiatrist" to 3, "cardiologist" to 1)),
    SymptomRule("lowMood", false, listOf("psychiatrist" to 3, "generalPhysician" to 1)),
    SymptomRule("sleep", false, listOf("psychiatrist" to 2, "generalPhysician" to 1)),
    SymptomRule("blurredVision", false, listOf("ophthalmologist" to 3, "endocrinologist" to 1, "neurologist" to 1)),
    SymptomRule("earPain", false, listOf("ent" to 3)),
    SymptomRule("soreThroat", false, listOf("ent" to 3, "generalPhysician" to 1)),
    SymptomRule("toothache", false, listOf("dentist" to 3)),
)

/** Top specialists for the ticked symptom ids, best first (max 3). */
fun recommendSpecialists(selected: Set<String>): List<SpecMatch> {
    if (selected.isEmpty()) return emptyList()
    val score = HashMap<String, Int>()
    SYMPTOMS.filter { it.id in selected }.forEach { s ->
        s.weights.forEach { (spec, w) -> score[spec] = (score[spec] ?: 0) + w }
    }
    val maxPossible = 3 * selected.size
    return score.entries
        .sortedByDescending { it.value }
        .take(3)
        .map { SpecMatch(it.key, minOf(100, Math.round(it.value * 100f / maxPossible))) }
}

fun hasUrgentSymptom(selected: Set<String>): Boolean = SYMPTOMS.any { it.urgent && it.id in selected }
