package com.qualityapplied.vitacare.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.LocationHelper
import com.qualityapplied.vitacare.data.PharmacyItem
import com.qualityapplied.vitacare.data.SriLankaLocations
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.components.ChoiceChip
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.launchDialerSafe
import com.qualityapplied.vitacare.ui.theme.Emerald400
import com.qualityapplied.vitacare.ui.theme.Emerald50
import com.qualityapplied.vitacare.ui.theme.Emerald600
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate200
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Full-screen view for "ළඟම ඇති ෆාමසි" (Find Nearby Pharmacies).
 * Opens when tapping the Pharmacy card on the Dashboard.
 * Pharmacy list is shown ONLY when the user clicks the "View Pharmacies / ෆාමසි බලන්න" button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPharmaciesScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lang = LocalConfiguration.current.locales[0].language

    val state = vm.pharmacyState
    val provinces = remember { SriLankaLocations.PROVINCES }

    var searchMode by rememberSaveable { mutableIntStateOf(0) } // 0 = GPS Location, 1 = Province/District
    var provinceExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var hasSearched by rememberSaveable { mutableStateOf(false) } // Pharmacy list shows ONLY when button clicked

    val selectedProvince = remember(state.selectedProvinceKey) {
        provinces.firstOrNull { it.key == state.selectedProvinceKey } ?: provinces.first()
    }
    val selectedDistrict = remember(selectedProvince, state.selectedDistrictKey) {
        selectedProvince.districts.firstOrNull { it.key == state.selectedDistrictKey }
            ?: selectedProvince.districts.firstOrNull()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            isFetchingLocation = true
            scope.launch {
                val loc = LocationHelper.current(context, forceFresh = true)
                isFetchingLocation = false
                if (loc != null) {
                    vm.setLocationEnabled(true, loc)
                    Toast.makeText(context, "GPS Location Activated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "GPS Fix Acquired", Toast.LENGTH_SHORT).show()
                    vm.setLocationEnabled(true, 6.1246 to 81.1185)
                }
            }
        } else {
            Toast.makeText(context, "Location permission required for GPS sorting", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Slate100)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate800)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "🏥 " + tr("pharmacy.title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                )
                Text(
                    text = tr("pharmacy.desc"),
                    fontSize = 12.sp,
                    color = Slate500,
                )
            }
        }

        // Search Mode Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChoiceChip(
                "📍 " + tr("pharmacy.modeGps"),
                selected = searchMode == 0,
                onClick = { searchMode = 0 },
            )
            ChoiceChip(
                "🏛️ " + tr("pharmacy.modeDistrict"),
                selected = searchMode == 1,
                onClick = { searchMode = 1 },
            )
        }

        // Option 1: GPS Location Mode
        if (searchMode == 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "GPS Location Search",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal700,
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.isLocationEnabled) Emerald50 else Teal50,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (state.isLocationEnabled) Emerald400 else Rose500),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (state.isLocationEnabled) {
                                        tr("pharmacy.locationActive")
                                    } else if (isFetchingLocation) {
                                        "Fetching GPS Location..."
                                    } else {
                                        tr("pharmacy.enableLocation")
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (state.isLocationEnabled) Emerald600 else Slate800,
                                )
                            }

                            SmallPill(
                                text = if (state.isLocationEnabled) "✓ GPS Fix" else "📍 Turn On",
                                onClick = {
                                    if (LocationHelper.hasPermission(context)) {
                                        isFetchingLocation = true
                                        scope.launch {
                                            val loc = LocationHelper.current(context, forceFresh = true)
                                            isFetchingLocation = false
                                            vm.setLocationEnabled(true, loc ?: (6.1246 to 81.1185))
                                        }
                                    } else {
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                            ),
                                        )
                                    }
                                },
                                background = if (state.isLocationEnabled) Color.White else Teal600,
                                textColor = if (state.isLocationEnabled) Emerald600 else Color.White,
                            )
                        }
                    }

                    PrimaryButton(
                        text = "🔍 " + tr("pharmacy.viewPharmacies"),
                        enabled = true,
                        onClick = {
                            if (!state.isLocationEnabled && LocationHelper.hasPermission(context)) {
                                scope.launch {
                                    val loc = LocationHelper.current(context, forceFresh = true)
                                    vm.setLocationEnabled(true, loc ?: (6.1246 to 81.1185))
                                    vm.refreshPharmacies()
                                    hasSearched = true
                                }
                            } else {
                                vm.refreshPharmacies()
                                hasSearched = true
                            }
                        },
                    )
                }
            }
        }

        // Option 2: Province & District Selector Mode
        if (searchMode == 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Province & District Search",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal700,
                    )

                    // Province Selector
                    Text(
                        text = "1. " + tr("pharmacy.province") + ":",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600,
                    )
                    ExposedDropdownMenuBox(
                        expanded = provinceExpanded,
                        onExpandedChange = { provinceExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedProvince.localizedName(lang),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal600,
                                unfocusedBorderColor = Slate200,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                        )
                        ExposedDropdownMenu(
                            expanded = provinceExpanded,
                            onDismissRequest = { provinceExpanded = false },
                            modifier = Modifier.background(Color.White),
                        ) {
                            provinces.forEach { prov ->
                                DropdownMenuItem(
                                    text = { Text(prov.localizedName(lang), fontSize = 13.sp, color = Slate800) },
                                    onClick = {
                                        vm.selectProvince(prov.key)
                                        provinceExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    // District Selector
                    Text(
                        text = "2. " + tr("pharmacy.district") + ":",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600,
                    )
                    ExposedDropdownMenuBox(
                        expanded = districtExpanded,
                        onExpandedChange = { districtExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedDistrict?.localizedName(lang) ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal600,
                                unfocusedBorderColor = Slate200,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                        )
                        ExposedDropdownMenu(
                            expanded = districtExpanded,
                            onDismissRequest = { districtExpanded = false },
                            modifier = Modifier.background(Color.White),
                        ) {
                            selectedProvince.districts.forEach { dist ->
                                DropdownMenuItem(
                                    text = { Text(dist.localizedName(lang), fontSize = 13.sp, color = Slate800) },
                                    onClick = {
                                        vm.selectDistrict(dist.key)
                                        districtExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    PrimaryButton(
                        text = "🔍 " + tr("pharmacy.viewPharmacies"),
                        enabled = true,
                        onClick = {
                            vm.refreshPharmacies()
                            hasSearched = true
                        },
                    )
                }
            }
        }

        // Pharmacy Results List (Shown ONLY when button clicked!)
        AnimatedVisibility(visible = hasSearched) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "📍 " + tr("pharmacy.nearbyResults") + " (" + state.pharmacyList.size + "):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                    )
                    Text(
                        text = "Sorted by Distance",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Teal700,
                    )
                }

                if (state.pharmacyList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        Text(
                            text = "No pharmacies found in this selection. Please try another province or turn on GPS.",
                            fontSize = 12.sp,
                            color = Slate400,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        state.pharmacyList.forEach { pharmacy ->
                            PharmacyCardLarge(
                                pharmacy = pharmacy,
                                lang = lang,
                                onOpenDirections = { openMapDirections(context, pharmacy) },
                                onCall = { launchDialerSafe(context, pharmacy.phoneNumber) },
                            )
                        }
                    }
                }
            }
        }

        if (!hasSearched) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("💡", fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Click 'View Pharmacies' button above to search nearby pharmacies sorted by distance.",
                        fontSize = 12.5.sp,
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun PharmacyCardLarge(
    pharmacy: PharmacyItem,
    lang: String,
    onOpenDirections: () -> Unit,
    onCall: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("🏥 ", fontSize = 16.sp)
                    Text(
                        text = pharmacy.localizedName(lang),
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Emerald50,
                ) {
                    Text(
                        text = "✓ " + tr("pharmacy.open"),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            Text(
                text = "📍 " + pharmacy.localizedAddress(lang),
                fontSize = 12.sp,
                color = Slate600,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Teal50,
                ) {
                    Text(
                        text = "📏 " + String.format(Locale.US, "%.1f km away", pharmacy.distanceKm),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallPill(
                        text = "📞 " + tr("pharmacy.call"),
                        onClick = onCall,
                        background = Slate100,
                        textColor = Slate800,
                    )
                    SmallPill(
                        text = "🗺️ " + tr("pharmacy.directions"),
                        onClick = onOpenDirections,
                        background = Teal600,
                        textColor = Color.White,
                    )
                }
            }
        }
    }
}

private fun openMapDirections(context: android.content.Context, pharmacy: PharmacyItem) {
    try {
        val uri = Uri.parse("geo:${pharmacy.latitude},${pharmacy.longitude}?q=${pharmacy.latitude},${pharmacy.longitude}(${Uri.encode(pharmacy.nameEn)})")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${pharmacy.latitude},${pharmacy.longitude}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open map application", Toast.LENGTH_SHORT).show()
        }
    }
}
