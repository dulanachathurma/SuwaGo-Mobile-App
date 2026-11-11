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
            nameTa = "மேල් மாகாணம்",
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
        // Southern - Hambantota, Matara, Galle
        PharmacyItem("p1", "Beliatta Health Pharmacy", "බෙලියත්ත හෙල්ත් ෆාමසිය", "பெலியாத்த ஹெல்த் மருந்தகம்", "Main Street, Beliatta", "ප්‍රධාන පාර, බෙලියත්ත", "பிரதான வீதி, பெலியாத்த", "hambantota", "southern", 6.1472, 80.7411, "0472251120"),
        PharmacyItem("p2", "Healthguard Pharmacy - Tangalle", "හෙල්ත්ගාඩ් ෆාමසිය - තංගල්ල", "ஹெல்த்கார்ட் மருந்தகம் - தங்கalle", "Matara Road, Tangalle", "මාතර පාර, තංගල්ල", "மாத்தறை வீதி, தங்கalle", "hambantota", "southern", 6.0245, 80.7938, "0472240500"),
        PharmacyItem("p3", "Hambantota City Pharmacy", "හම්බන්තොට සිටි ෆාමසිය", "அம்பாந்தோட்டை சிட்டி மருந்தகம்", "Galle Road, Hambantota", "ගාල්ල පාර, හම්බන්තොට", "காலி வீதி, அம்பாந்தோட்டை", "hambantota", "southern", 6.1246, 81.1185, "0472220100"),
        PharmacyItem("p4", "Ambalantota Medicare Pharmacy", "අම්බලන්තොට මෙඩිකෙයා ෆාමසිය", "அம்பலாந்தோட்டை மெடிகேர் மருந்தகம்", "Hospital Road, Ambalantota", "රෝහල පාර, අම්බලන්තොට", "ஆஸ்பத்திரி வீதி, அம்பலாந்தோட்டை", "hambantota", "southern", 6.1221, 81.0254, "0472223400"),
        PharmacyItem("p5", "Matara Suwa Arana Pharmacy", "මාතර සුව අරණ ෆාමසිය", "மாத்தறை சுக அரண மருந்தகம்", "Anagarika Dharmapala Mawatha, Matara", "අනගාරික ධර්මපාල මාවත, මාතර", "அனகாரிக தர்மபால மாவத்தை, மாத்தறை", "matara", "southern", 5.9496, 80.5469, "0412222333"),
        PharmacyItem("p6", "Asiri Hospital Pharmacy - Matara", "ආසිරි රෝහල් ෆාමසිය - මාතර", "ஆசிரி மருத்துவமனை மருந்தகம் - மாத்தறை", "Broadway, Matara", "බ්‍රෝඩ්වේ, මාතර", "புரோட்வே, මාத்தறை", "matara", "southern", 5.9481, 80.5432, "0414390900"),
        PharmacyItem("p14", "Matara Central Pharmacy", "මාතර සෙන්ට්‍රල් ෆාමසිය", "மாத்தறை சென்ட்ரல் மருந்தகம்", "Station Road, Matara", "දුම්රියපොළ පාර, මාතර", "ஸ்டேஷன் வீதி, மாத்தறை", "matara", "southern", 5.9510, 80.5400, "0412224500"),
        PharmacyItem("p7", "Galle Fort Medi Care Pharmacy", "ගාල්ල කොටුව මෙඩි කෙයා ෆාමසිය", "காலி கோட்டை மெடி கேர் மருந்தகம்", "Church Street, Galle Fort", "චර්ච් වීදිය, ගාල්ල කොටුව", "சர்ච් வீதி, காலி கோட்டை", "galle", "southern", 6.0267, 80.2170, "0912234567"),
        PharmacyItem("p15", "Asiri Hospital Pharmacy - Galle", "ආසිරි රෝහල් ෆාමසිය - ගාල්ල", "ஆசிரி மருத்துவமனை மருந்தகம் - காலி", "Wakwella Road, Galle", "වක්වැල්ල පාර, ගාල්ල", "வக்வெல்ல வீதி, காலி", "galle", "southern", 6.0400, 80.2200, "0914380400"),

        // Western - Colombo, Gampaha, Kalutara
        PharmacyItem("p8", "Union Chemists - Colombo 03", "යුනියන් කෙමිස්ට්ස් - කොළඹ 03", "யூனியன் கெமிஸ்ட்ஸ் - கொழும்பு 03", "Galle Road, Kollupitiya, Colombo", "ගාල්ල පාර, කොල්ලුපිටිය, කොළඹ", "காலி வீதி, கொள்ளுப்பிட்டி, கொழும்பு", "colombo", "western", 6.9147, 79.8517, "0112573686"),
        PharmacyItem("p9", "Healthguard Pharmacy - Rajagiriya", "හෙල්ත්ගාඩ් ෆාමසිය - රාජගිරිය", "ஹெல்த்கார்ட் மருந்தகம் - ராஜகிரிய", "Cotta Road, Rajagiriya", "කොට්ටේ පාර, රාජගිරිය", "கொட்டே வீதி, ராஜகிரிய", "colombo", "western", 6.9083, 79.8972, "0112874500"),
        PharmacyItem("p16", "Asiri Central Pharmacy - Colombo 10", "ආසිරි සෙන්ට්‍රල් ෆාමසිය - කොළඹ 10", "ஆசிரி சென்ட்ரல் மருந்தகம் - கொழும்பு 10", "Norris Canal Road, Colombo 10", "නෝරිස් කෙනල් පාර, කොළඹ 10", "நோரிஸ் கெனால் வீதி, கொழும்பு 10", "colombo", "western", 6.9240, 79.8680, "0114665500"),
        PharmacyItem("p10", "Gampaha Central Pharmacy", "ගම්පහ සෙන්ට්‍රල් ෆාමසිය", "கம்பஹா சென்ட்ரல் மருந்தகம்", "Colombo Road, Gampaha", "කොළඹ පාර, ගම්පහ", "கொழும்பு வீதி, கம்பஹா", "gampaha", "western", 7.0840, 79.9925, "0332223456"),
        PharmacyItem("p17", "Negombo Suwa Arana Pharmacy", "මීගමුව සුව අරණ ෆාමසිය", "நீர்கொழும்பு சுக அரண மருந்தகம்", "Main Street, Negombo", "ප්‍රධාන වීදිය, මීගමුව", "பிரதான வீதி, நீர்கொழும்பு", "gampaha", "western", 7.2083, 79.8358, "0312224400"),
        PharmacyItem("p11", "Kalutara Medi Help Pharmacy", "කළුතර මෙඩි හෙල්ප් ෆාමසිය", "களுத்துறை மெடி ஹெல்ப் மருந்தகம்", "Nagoda Road, Kalutara", "නාගොඩ පාර, කළුතර", "நாகொட வீதி, களுத்துறை", "kalutara", "western", 6.5854, 79.9607, "0342221234"),
        PharmacyItem("p18", "Panadura Medi Care Pharmacy", "පානදූර මෙඩි කෙයා ෆාමසිය", "பாணந்துறை மெடி கேர் மருந்தகம்", "Galle Road, Panadura", "ගාල්ල පාර, පානදූර", "காலி வீதி, பாணந்துறை", "kalutara", "western", 6.7130, 79.9070, "0382233200"),

        // Central - Kandy, Matale, Nuwara Eliya
        PharmacyItem("p12", "Kandy Royal Pharmacy", "මහනුවර රෝයල් ෆාමසිය", "கண்டி ராயல் மருந்தகம்", "Dalada Veediya, Kandy", "දළදා වීදිය, මහනුවර", "தளதா வீதி, கண்டி", "kandy", "central", 7.2906, 80.6337, "0812234111"),
        PharmacyItem("p13", "Peradeniya Teaching Hospital Pharmacy", "පේරාදෙණිය ශික්ෂණ රෝහල් ෆාමසිය", "பேராதனை போதனா மருத்துவமனை மருந்தகம்", "Gatembe, Peradeniya", "ගැටඹේ, පේරාදෙණිය", "கெடம்பே, பேராதனை", "kandy", "central", 7.2655, 80.5960, "0812388000"),
        PharmacyItem("p19", "Matale City Pharmacy", "මාතලේ සිටි ෆාමසිය", "மாத்தளை சிட்டி மருந்தகம்", "Trincomalee Street, Matale", "ත්‍රිකුණාමල වීදිය, මාතලේ", "திருகோணமலை வீதி, மாத்தளை", "matale", "central", 7.4675, 80.6234, "0662222100"),
        PharmacyItem("p20", "Dambulla Suwa Arana Pharmacy", "දඹුල්ල සුව අරණ ෆාමසිය", "தம்புள்ளை சுக அரண மருந்தகம்", "Kurunegala Road, Dambulla", "කුරුණෑගල පාර, දඹුල්ල", "குருநாகல் வீதி, தம்புள்ளை", "matale", "central", 7.8600, 80.6517, "0662284900"),
        PharmacyItem("p21", "Nuwara Eliya Royal Pharmacy", "නුවරඑළිය රෝයල් ෆාමසිය", "நுவரெலியா ராயல் மருந்தகம்", "Park Road, Nuwara Eliya", "පාක් පාර, නුවරඑළිය", "பார்க் வீதி, நுவரெலியா", "nuwaraeliya", "central", 6.9497, 80.7891, "0522223100"),
        PharmacyItem("p22", "Hatton Medi Help Pharmacy", "හැටන් මෙඩි හෙල්ප් ෆාමසිය", "ஹேட்டன் மெடி ஹெல்ப் மருந்தகம்", "Main Street, Hatton", "ප්‍රධාන වීදිය, හැටන්", "பிரதான வீதி, ஹேட்டன்", "nuwaraeliya", "central", 6.8910, 80.5970, "0512222500"),

        // Northern - Jaffna, Kilinochchi, Mannar, Vavuniya, Mullaitivu
        PharmacyItem("p23", "Jaffna City Pharmacy", "යාපනය සිටි ෆාමසිය", "யாழ்ப்பாணம் சிட்டி மருந்தகம்", "Hospital Road, Jaffna", "රෝහල පාර, යාපනය", "ஆஸ்பத்திரி வீதி, யாழ்ப்பாணம்", "jaffna", "northern", 9.6615, 80.0255, "0212223300"),
        PharmacyItem("p24", "Nallur Medi Care Pharmacy", "නල්ලූර් මෙඩි කෙයා ෆාමසිය", "நல்லூர் மெடி கேர் மருந்தகம்", "Point Pedro Road, Nallur", "පොයින්ට් පේදුරු පාර, නල්ලූර්", "பாயிண்ட் பருத்தி வீதி, நல்லூர்", "jaffna", "northern", 9.6750, 80.0320, "0212224400"),
        PharmacyItem("p25", "Kilinochchi Town Pharmacy", "කිලිනොච්චිය ටවුන් ෆාමසිය", "கிளிநொச்சி டவுன் மருந்தகம்", "A9 Road, Kilinochchi", "A9 පාර, කිලිනොච්චිය", "A9 வீதி, கிளிநொச்சி", "kilinochchi", "northern", 9.3803, 80.3970, "0212285100"),
        PharmacyItem("p26", "Mannar Main Street Pharmacy", "මන්නාරම ප්‍රධාන පාර ෆාමසිය", "மன்னார் பிரதான வீதி மருந்தகம்", "Main Street, Mannar", "ප්‍රධාන පාර, මන්නාරම", "பிரதான வீதி, மன்னார்", "mannar", "northern", 8.9810, 79.9044, "0232222200"),
        PharmacyItem("p27", "Vavuniya Central Pharmacy", "වවුනියාව සෙන්ට්‍රල් ෆාමසිය", "வவுனியா சென்ட்ரல் மருந்தகம்", "Bazaar Street, Vavuniya", "බසාර් වීදිය, වවුනියාව", "பஜார் வீதி, வவுனியா", "vavuniya", "northern", 8.7542, 80.4982, "0242223100"),
        PharmacyItem("p28", "Mullaitivu Coast Pharmacy", "මුලතිව් වෙරළ ෆාමසිය", "முல்லைத்தீவு கடற்கரை மருந்தகம்", "Hospital Road, Mullaitivu", "රෝහල පාර, මුලතිව්", "ஆஸ்பத்திரி வீதி, முல்லைத்தீவு", "mullaitivu", "northern", 9.2671, 80.8142, "0212286000"),

        // Eastern - Trincomalee, Batticaloa, Ampara
        PharmacyItem("p29", "Trincomalee Suwa Arana Pharmacy", "ත්‍රිකුණාමලය සුව අරණ ෆාමසිය", "திருகோணமலை சுக அரண மருந்தகம்", "Dockyard Road, Trincomalee", "ඩොක්යාඩ් පාර, ත්‍රිකුණාමලය", "டாக்கியார்ட் வீதி, திருகோணமலை", "trincomalee", "eastern", 8.5874, 81.2152, "0262222400"),
        PharmacyItem("p30", "Town Pharmacy Trincomalee", "ටවුන් ෆාමසිය ත්‍රිකුණාමලය", "டவுன் மருந்தகம் திருகோணமலை", "Main Street, Trincomalee", "ප්‍රධාන වීදිය, ත්‍රිකුණාමලය", "பிரதான வீதி, திருகோணமலை", "trincomalee", "eastern", 8.5720, 81.2330, "0262223100"),
        PharmacyItem("p31", "Batticaloa City Pharmacy", "මඩකලපුව සිටි ෆාමසිය", "மட்டக்களப்பு சிட்டி மருந்தகம்", "Trincomalee Road, Batticaloa", "ත්‍රිකුණාමල පාර, මඩකලපුව", "திருகோணமலை வீதி, மட்டக்களப்பு", "batticaloa", "eastern", 7.7310, 81.6747, "0652222500"),
        PharmacyItem("p32", "Kattankudy Health Care Pharmacy", "කාත්තන්කුඩි හෙල්ත් කෙයා ෆාමසිය", "காத்தான்குடி ஹெல்த் கேர் மருந்தகம்", "Main Street, Kattankudy", "ප්‍රධාන වීදිය, කාත්තන්කුඩි", "பிரதான வீதி, காத்தான்குடி", "batticaloa", "eastern", 7.6890, 81.7220, "0652246000"),
        PharmacyItem("p33", "Ampara Town Pharmacy", "අම්පාර ටවුන් ෆාමසිය", "அம்பாறை டவுன் மருந்தகம்", "Clock Tower Road, Ampara", "ඔරලෝසු කණුව පාර, අම්පාර", "கடிகார கோபுர வீதி, அம்பாறை", "ampara", "eastern", 7.2912, 81.6724, "0632222100"),
        PharmacyItem("p34", "Kalmunai Medi Care Pharmacy", "කල්මුණේ මෙඩි කෙයා ෆාමසිය", "கல்முனை மெடி கேர் மருந்தகம்", "Main Street, Kalmunai", "ප්‍රධාන වීදිය, කල්මුණේ", "பிரதான வீதி, கல்முனை", "ampara", "eastern", 7.4167, 81.8333, "0672223500"),

        // North Western - Kurunegala, Puttalam
        PharmacyItem("p35", "Kurunegala Royal Pharmacy", "කුරුණෑගල රෝයල් ෆාමසිය", "குருநாகல் ராயல் மருந்தகம்", "Colombo Road, Kurunegala", "කොළඹ පාර, කුරුණෑගල", "கொழும்பு வீதி, குருநாகல்", "kurunegala", "northwestern", 7.4863, 80.3647, "0372223100"),
        PharmacyItem("p36", "Suwa Arana Pharmacy - Kurunegala", "සුව අරණ ෆාමසිය - කුරුණෑගල", "சுக அரண மருந்தகம் - குருநாகல்", "Negombo Road, Kurunegala", "මීගමුව පාර, කුරුණෑගල", "நீர்கொழும்பு வீதி, குருநாகல்", "kurunegala", "northwestern", 7.4800, 80.3600, "0372224500"),
        PharmacyItem("p37", "Puttalam Main Street Pharmacy", "පුත්තලම ප්‍රධාන පාර ෆාමසිය", "புத்தளம் பிரதான வீதி மருந்தகம்", "Main Street, Puttalam", "ප්‍රධාන වීදිය, පුත්තලම", "பிரதான வீதி, புத்தளம்", "puttalam", "northwestern", 8.0362, 79.8283, "0322265100"),
        PharmacyItem("p38", "Chilaw Suwa Arana Pharmacy", "හලාවත සුව අරණ ෆාමසිය", "சிலாபம் சுக அரண மருந்தகம்", "Colombo Road, Chilaw", "කොළඹ පාර, හලාවත", "கொழும்பு வீதி, சிலாபம்", "puttalam", "northwestern", 7.5758, 79.7953, "0322222400"),

        // North Central - Anuradhapura, Polonnaruwa
        PharmacyItem("p39", "Anuradhapura City Pharmacy", "අනුරාධපුර සිටි ෆාමසිය", "அனுராதபுரம் சிட்டி மருந்தகம்", "Maithripala Senanayake Mawatha, Anuradhapura", "මෛත්‍රීපාල සේනානායක මාවත, අනුරාධපුරය", "மைத்ரிபால சேனானாயக்க மாவத்தை, அனுராதபுரம்", "anuradhapura", "northcentral", 8.3114, 80.4037, "0252222300"),
        PharmacyItem("p40", "Sacred City Medi Care Pharmacy", "පූජනීය නගර මෙඩි කෙයා ෆාමසිය", "புனித நகர் மெடி கேர் மருந்தகம்", "New Town, Anuradhapura", "නව නගරය, අනුරාධපුරය", "புது நகர், அனுராதபுரம்", "anuradhapura", "northcentral", 8.3300, 80.4100, "0252224100"),
        PharmacyItem("p41", "Polonnaruwa Ancient City Pharmacy", "පොළොන්නරුව පැරණි නගර ෆාමසිය", "பொலன்னறுவை பண்டைய நகர் மருந்தகம்", "Batticaloa Road, Polonnaruwa", "මඩකලපුව පාර, පොළොන්නරුව", "மட்டக்களப்பு வீதி, பொலன்னறுவை", "polonnaruwa", "northcentral", 7.9403, 81.0188, "0272222100"),
        PharmacyItem("p42", "Kaduruwela Suwa Arana Pharmacy", "කඩුරුවෙල සුව අරණ ෆාමසිය", "கடுருவெல சுக அரண மருந்தகம்", "Main Street, Kaduruwela", "ප්‍රධාන වීදිය, කඩුරුවෙල", "பிரதான வீதி, கடுருவெல", "polonnaruwa", "northcentral", 7.9350, 81.0250, "0272223400"),

        // Uva - Badulla, Moneragala
        PharmacyItem("p43", "Badulla Central Pharmacy", "බදුල්ල සෙන්ට්‍රල් ෆාමසිය", "பதுளை சென்ட்ரல் மருந்தகம்", "Lower Street, Badulla", "පහළ වීදිය, බදුල්ල", "கீழ் வீதி, பதுளை", "badulla", "uva", 6.9934, 81.0550, "0552222300"),
        PharmacyItem("p44", "Bandarawela Suwa Arana Pharmacy", "බණ්ඩාරවෙල සුව අරණ ෆාමසිය", "பண்டாரவளை சுக அரண மருந்தகம்", "Main Street, Bandarawela", "ප්‍රධාන වීදිය, බණ්ඩාරවෙල", "பிரதான வீதி, பண்டாரவளை", "badulla", "uva", 6.8317, 80.9875, "0572222500"),
        PharmacyItem("p45", "Moneragala Town Pharmacy", "මොණරාගල ටවුන් ෆාමසිය", "மொணராகலை டவுன் மருந்தகம்", "Pottuvil Road, Moneragala", "පොතුවිල් පාර, මොණරාගල", "பொத்துவில் வீதி, மொணராகலை", "moneragala", "uva", 6.8724, 81.3507, "0552276100"),
        PharmacyItem("p46", "Wellawaya Medi Help Pharmacy", "වැල්ලවාය මෙඩි හෙල්ප් ෆාමසිය", "வெள்ளவாய மெடி ஹெல்ப் மருந்தகம்", "Ella Road, Wellawaya", "ඇල්ල පාර, වැල්ලවාය", "எல்ல வீதி, வெள்ளவாய", "moneragala", "uva", 6.7380, 81.1030, "0552274200"),

        // Sabaragamuwa - Ratnapura, Kegalle
        PharmacyItem("p47", "Ratnapura Gem City Pharmacy", "රත්නපුර මැණික් නගර ෆාමසිය", "இரத்தினபுரி மாணிக்க நகர் மருந்தகம்", "Main Street, Ratnapura", "ප්‍රධාන වීදිය, රත්නපුරය", "பிரதான வீதி, இரத்தினபுரி", "ratnapura", "sabaragamuwa", 6.6828, 80.4014, "0452222300"),
        PharmacyItem("p48", "Suwa Arana Pharmacy - Ratnapura", "සුව අරණ ෆාමසිය - රත්නපුරය", "சுக அரண மருந்தகம் - இரத்தினபுரி", "Hospital Road, Ratnapura", "රෝහල පාර, රත්නපුරය", "ஆஸ்பத்திரி வீதி, இரத்தினபுரி", "ratnapura", "sabaragamuwa", 6.6870, 80.3980, "0452224100"),
        PharmacyItem("p49", "Kegalle Central Pharmacy", "කෑගල්ල සෙන්ට්‍රල් ෆාමසිය", "கேகாலை சென்ட்ரல் மருந்தகம்", "Main Street, Kegalle", "ප්‍රධාන වීදිය, කෑගල්ල", "பிரதான வீதி, கேகாலை", "kegalle", "sabaragamuwa", 7.2513, 80.3464, "0352222100"),
        PharmacyItem("p50", "Mawanella Suwa Arana Pharmacy", "මාවනැල්ල සුව අරණ ෆාමසිය", "மாவனல்லை சுக அரண மருந்தகம்", "Kandy Road, Mawanella", "මහනුවර පාර, මාවනැල්ල", "கண்டி வீதி, மாவனல்லை", "kegalle", "sabaragamuwa", 7.2530, 80.4460, "0352246200"),
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
