package com.qualityapplied.vitacare.data

import java.util.Locale

data class PharmacyItem(
    val id: String,
    val nameEn: String,
    val nameSi: String,
    val nameTa: String,
    val addressEn: String,
    val addressSi: String,
    val addressTa: String,
    val districtKey: String,
    val provinceKey: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String = "0771234567",
    val isOpenNow: Boolean = true,
    var distanceKm: Double = 0.0,
) {
    fun localizedName(lang: String): String = when (lang) {
        "si" -> nameSi
        "ta" -> nameTa
        else -> nameEn
    }

    fun localizedAddress(lang: String): String = when (lang) {
        "si" -> addressSi
        "ta" -> addressTa
        else -> addressEn
    }
}

data class PharmacyUiState(
    val selectedProvinceKey: String = "",
    val selectedDistrictKey: String = "",
    val isLocationEnabled: Boolean = false,
    val userLocation: Pair<Double, Double>? = null,
    val pharmacyList: List<PharmacyItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isExpandedView: Boolean = false,
)

data class ProvinceInfo(
    val key: String,
    val nameEn: String,
    val nameSi: String,
    val nameTa: String,
    val districts: List<DistrictInfo>,
) {
    fun localizedName(lang: String): String = when (lang) {
        "si" -> nameSi
        "ta" -> nameTa
        else -> nameEn
    }
}

data class DistrictInfo(
    val key: String,
    val nameEn: String,
    val nameSi: String,
    val nameTa: String,
    val centerLat: Double,
    val centerLon: Double,
) {
    fun localizedName(lang: String): String = when (lang) {
        "si" -> nameSi
        "ta" -> nameTa
        else -> nameEn
    }
}

object SriLankaLocations {
    val PROVINCES = listOf(
        ProvinceInfo(
            key = "southern",
            nameEn = "Southern Province",
            nameSi = "දකුණු පළාත",
            nameTa = "தென் மாகாணம்",
            districts = listOf(
                DistrictInfo("hambantota", "Hambantota", "හම්බන්තොට", "அம்பாந்தோட்டை", 6.1246, 81.1185),
                DistrictInfo("matara", "Matara", "මාතර", "மாத்தறை", 5.9496, 80.5469),
                DistrictInfo("galle", "Galle", "ගාල්ල", "காலி", 6.0535, 80.2210),
            ),
        ),
        ProvinceInfo(
            key = "western",
            nameEn = "Western Province",
            nameSi = "බස්නාහිර පළාත",
            nameTa = "மேல் மாகாணம்",
            districts = listOf(
                DistrictInfo("colombo", "Colombo", "කොළඹ", "கொழும்பு", 6.9271, 79.8612),
                DistrictInfo("gampaha", "Gampaha", "ගම්පහ", "கம்பஹா", 7.0840, 79.9925),
                DistrictInfo("kalutara", "Kalutara", "කළුතර", "களுத்துறை", 6.5854, 79.9607),
            ),
        ),
        ProvinceInfo(
            key = "central",
            nameEn = "Central Province",
            nameSi = "මධ්‍යම පළාත",
            nameTa = "மத்திய மாகாணம்",
            districts = listOf(
                DistrictInfo("kandy", "Kandy", "මහනුවර", "கண்டி", 7.2906, 80.6337),
                DistrictInfo("matale", "Matale", "මාතලේ", "மாத்தளை", 7.4675, 80.6234),
                DistrictInfo("nuwaraeliya", "Nuwara Eliya", "නුවරඑළිය", "நுவரெலியா", 6.9497, 80.7891),
            ),
        ),
        ProvinceInfo(
            key = "northern",
            nameEn = "Northern Province",
            nameSi = "උතුරු පළාත",
            nameTa = "வட மாகாணம்",
            districts = listOf(
                DistrictInfo("jaffna", "Jaffna", "යාපනය", "யாழ்ப்பாணம்", 9.6615, 80.0255),
                DistrictInfo("kilinochchi", "Kilinochchi", "කිලිනොච්චිය", "கிளிநொச்சி", 9.3803, 80.3970),
                DistrictInfo("mannar", "Mannar", "මන්නාරම", "மன்னார்", 8.9810, 79.9044),
                DistrictInfo("vavuniya", "Vavuniya", "වවුනියාව", "வவுனியா", 8.7542, 80.4982),
                DistrictInfo("mullaitivu", "Mullaitivu", "මුලතිව්", "முல்லைத்தீவு", 9.2671, 80.8142),
            ),
        ),
        ProvinceInfo(
            key = "eastern",
            nameEn = "Eastern Province",
            nameSi = "නැගෙනහිර පළාත",
            nameTa = "கிழக்கு மாகாணம்",
            districts = listOf(
                DistrictInfo("trincomalee", "Trincomalee", "ත්‍රිකුණාමලය", "திருகோணமலை", 8.5874, 81.2152),
                DistrictInfo("batticaloa", "Batticaloa", "මඩකලපුව", "மட்டக்களப்பு", 7.7310, 81.6747),
                DistrictInfo("ampara", "Ampara", "අම්පාර", "அம்பாறை", 7.2912, 81.6724),
            ),
        ),
        ProvinceInfo(
            key = "northwestern",
            nameEn = "North Western Province",
            nameSi = "වයඹ පළාත",
            nameTa = "வடமேல் மாகாணம்",
            districts = listOf(
                DistrictInfo("kurunegala", "Kurunegala", "කුරුණෑගල", "குருநாகல்", 7.4863, 80.3647),
                DistrictInfo("puttalam", "Puttalam", "පුත්තලම", "புத்தளம்", 8.0362, 79.8283),
            ),
        ),
        ProvinceInfo(
            key = "northcentral",
            nameEn = "North Central Province",
            nameSi = "උතුරු මැද පළාත",
            nameTa = "வட மத்திய மாகாணம்",
            districts = listOf(
                DistrictInfo("anuradhapura", "Anuradhapura", "අනුරාධපුරය", "அனுராதபுரம்", 8.3114, 80.4037),
                DistrictInfo("polonnaruwa", "Polonnaruwa", "පොළොන්නරුව", "பொலன்னறுவை", 7.9403, 81.0188),
            ),
        ),
        ProvinceInfo(
            key = "uva",
            nameEn = "Uva Province",
            nameSi = "ඌව පළාත",
            nameTa = "ஊவா மாகாணம்",
            districts = listOf(
                DistrictInfo("badulla", "Badulla", "බදුල්ල", "பதுளை", 6.9934, 81.0550),
                DistrictInfo("moneragala", "Moneragala", "මොණරාගල", "மொணராகலை", 6.8724, 81.3507),
            ),
        ),
        ProvinceInfo(
            key = "sabaragamuwa",
            nameEn = "Sabaragamuwa Province",
            nameSi = "සබරගමුව පළාත",
            nameTa = "சபரகமுவ மாகாணம்",
            districts = listOf(
                DistrictInfo("ratnapura", "Ratnapura", "රත්නපුරය", "இரத்தினபுரி", 6.6828, 80.4014),
                DistrictInfo("kegalle", "Kegalle", "කෑගල්ල", "கேகாலை", 7.2513, 80.3464),
            ),
        ),
    )

    val PHARMACIES = listOf(
        // Southern - Hambantota & Beliatta
        PharmacyItem("p1", "Beliatta Health Pharmacy", "බෙලියත්ත හෙල්ත් ෆාමසිය", "பெலியாத்த ஹெல்த் மருந்தகம்", "Main Street, Beliatta", "ප්‍රධාන පාර, බෙලියත්ත", "பிரதான வீதி, பெலியாத்த", "hambantota", "southern", 6.1472, 80.7411, "0472251120"),
        PharmacyItem("p2", "Healthguard Pharmacy - Tangalle", "හෙල්ත්ගාඩ් ෆාමසිය - තංගල්ල", "ஹெல்த்கார்ட் மருந்தகம் - தங்கalle", "Matara Road, Tangalle", "මාතර පාර, තංගල්ල", "மாத்தறை வீதி, தங்கalle", "hambantota", "southern", 6.0245, 80.7938, "0472240500"),
        PharmacyItem("p3", "Hambantota City Pharmacy", "හම්බන්තොට සිටි ෆාමසිය", "அம்பாந்தோட்டை சிட்டி மருந்தகம்", "Galle Road, Hambantota", "ගාල්ල පාර, හම්බන්තොට", "காலி வீதி, அம்பாந்தோட்டை", "hambantota", "southern", 6.1246, 81.1185, "0472220100"),
        PharmacyItem("p4", "Ambalantota Medicare Pharmacy", "අම්බලන්තොට මෙඩිකෙයා ෆාමසිය", "அம்பலாந்தோட்டை மெடிகேர் மருந்தகம்", "Hospital Road, Ambalantota", "රෝහල පාර, අම්බලන්තොට", "ஆஸ்பத்திரி வீதி, அம்பலாந்தோட்டை", "hambantota", "southern", 6.1221, 81.0254, "0472223400"),

        // Southern - Matara & Galle
        PharmacyItem("p5", "Matara Suwa Arana Pharmacy", "මාතර සුව අරණ ෆාමසිය", "மாத்தறை சுக அரண மருந்தகம்", "Anagarika Dharmapala Mawatha, Matara", "අනගාරික ධර්මපාල මාවත, මාතර", "அனகாரிக தர்மபால மாவத்தை, மாத்தறை", "matara", "southern", 5.9496, 80.5469, "0412222333"),
        PharmacyItem("p6", "Asiri Hospital Pharmacy - Matara", "ආසිරි රෝහල් ෆාමසිය - මාතර", "ஆசிரி மருத்துவமனை மருந்தகம் - மாத்தறை", "Broadway, Matara", "බ්‍රෝඩ්වේ, මාතර", "புரோட்வே, மாத்தறை", "matara", "southern", 5.9481, 80.5432, "0414390900"),
        PharmacyItem("p7", "Galle Fort Medi Care Pharmacy", "ගාල්ල කොටුව මෙඩි කෙයා ෆාමසිය", "காலி கோட்டை மெடி கேர் மருந்தகம்", "Church Street, Galle Fort", "චර්ච් වීදිය, ගාල්ල කොටුව", "சர்ச் வீதி, காலி கோட்டை", "galle", "southern", 6.0267, 80.2170, "0912234567"),

        // Western - Colombo, Gampaha, Kalutara
        PharmacyItem("p8", "Union Chemists - Colombo 03", "යුනියන් කෙමිස්ට්ස් - කොළඹ 03", "யூனியன் கெமிஸ்ட்ஸ் - கொழும்பு 03", "Galle Road, Kollupitiya, Colombo", "ගාල්ල පාර, කොල්ලුපිටිය, කොළඹ", "காலி வீதி, கொள்ளுப்பிட்டி, கொழும்பு", "colombo", "western", 6.9147, 79.8517, "0112573686"),
        PharmacyItem("p9", "Healthguard Pharmacy - Rajagiriya", "හෙල්ත්ගාඩ් ෆාමසිය - රාජගිරිය", "ஹெல்த்கார்ட் மருந்தகம் - ராஜகிரிய", "Cotta Road, Rajagiriya", "කොට්ටේ පාර, රාජගිරිය", "கொட்டே வீதி, ராஜகிரிய", "colombo", "western", 6.9083, 79.8972, "0112874500"),
        PharmacyItem("p10", "Gampaha Central Pharmacy", "ගම්පහ සෙන්ට්‍රල් ෆාමසිය", "கம்பஹா சென்ட்ரல் மருந்தகம்", "Colombo Road, Gampaha", "කොළඹ පාර, ගම්පහ", "கொழும்பு வீதி, கம்பஹா", "gampaha", "western", 7.0840, 79.9925, "0332223456"),
        PharmacyItem("p11", "Kalutara Medi Help Pharmacy", "කළුතර මෙඩි හෙල්ප් ෆාමසිය", "களுத்துறை மெடி ஹெல்ப் மருந்தகம்", "Nagoda Road, Kalutara", "නාගොඩ පාර, කළුතර", "நாகொட வீதி, களுத்துறை", "kalutara", "western", 6.5854, 79.9607, "0342221234"),

        // Central - Kandy & Matale
        PharmacyItem("p12", "Kandy Royal Pharmacy", "මහනුවර රෝයල් ෆාමසිය", "கண்டி ராயல் மருந்தகம்", "Dalada Veediya, Kandy", "දළදා වීදිය, මහනුවර", "தளதா வீதி, கண்டி", "kandy", "central", 7.2906, 80.6337, "0812234111"),
        PharmacyItem("p13", "Peradeniya Teaching Hospital Pharmacy", "පේරාදෙණිය ශික්ෂණ රෝහල් ෆාමසිය", "பேராதனை போதனா மருத்துவமனை மருந்தகம்", "Gatembe, Peradeniya", "ගැටඹේ, පේරාදෙණිය", "கெடம்பே, பேராதனை", "kandy", "central", 7.2655, 80.5960, "0812388000"),
    )

    /**
     * Calculates distance between two GPS lat/lon points using the Haversine formula.
     * Returns distance in kilometers (km).
     */
    fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Double {
        val r = 6371.0 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    /**
     * Returns pharmacies matching district/province, sorted ascending by distance from user lat/lon.
     */
    fun findPharmaciesSorted(
        userLat: Double?,
        userLon: Double?,
        provinceKey: String,
        districtKey: String,
    ): List<PharmacyItem> {
        val filtered = PHARMACIES.filter { item ->
            (provinceKey.isEmpty() || item.provinceKey == provinceKey) &&
                (districtKey.isEmpty() || item.districtKey == districtKey)
        }

        val refLat = userLat ?: (PROVINCES.firstOrNull { it.key == provinceKey }
            ?.districts?.firstOrNull { it.key == districtKey }?.centerLat ?: 6.1246)
        val refLon = userLon ?: (PROVINCES.firstOrNull { it.key == provinceKey }
            ?.districts?.firstOrNull { it.key == districtKey }?.centerLon ?: 81.1185)

        return filtered.map { pharmacy ->
            val dist = haversineDistance(refLat, refLon, pharmacy.latitude, pharmacy.longitude)
            pharmacy.copy(distanceKm = String.format(Locale.US, "%.1f", dist).toDouble())
        }.sortedBy { it.distanceKm }
    }
}
