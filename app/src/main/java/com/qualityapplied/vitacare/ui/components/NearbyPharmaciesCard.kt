package com.qualityapplied.vitacare.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.R
import com.qualityapplied.vitacare.data.LocationHelper
import com.qualityapplied.vitacare.data.PharmacyItem
import com.qualityapplied.vitacare.data.SriLankaLocations
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.theme.Emerald400
import com.qualityapplied.vitacare.ui.theme.Emerald50
import com.qualityapplied.vitacare.ui.theme.Emerald600
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate200
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 3D Quick Card replacing the Vitals card in Home Dashboard.
 * Displays feature_vitals.png image card and launches the interactive Find Nearby Pharmacies dialog.
 */
@Composable
fun NearbyPharmaciesCard(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .aspectRatio(0.55f)
            .clickable {
                showDialog = true
                vm.togglePharmacyExpandedView(true)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.feature_vitals),
            contentDescription = tr("pharmacy.title"),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }

    if (showDialog) {
        NearbyPharmaciesDialog(
            vm = vm,
            onDismiss = {
                showDialog = false
                vm.togglePharmacyExpandedView(false)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPharmaciesDialog(
    vm: AppViewModel,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lang = LocalConfiguration.current.locales[0].language

    val state = vm.pharmacyState
    val provinces = remember { SriLankaLocations.PROVINCES }

    val selectedProvince = remember(state.selectedProvinceKey) {
        provinces.firstOrNull { it.key == state.selectedProvinceKey } ?: provinces.first()
    }
    val selectedDistrict = remember(selectedProvince, state.selectedDistrictKey) {
        selectedProvince.districts.firstOrNull { it.key == state.selectedDistrictKey }
            ?: selectedProvince.districts.firstOrNull()
    }

    var provinceExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var showResultsList by remember { mutableStateOf(false) }

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
                    Toast.makeText(context, "GPS Location acquired", Toast.LENGTH_SHORT).show()
                    vm.setLocationEnabled(true, 6.1246 to 81.1185)
                }
            }
        } else {
            Toast.makeText(context, "Location permission required for distance sorting", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏥 ", fontSize = 20.sp)
                    Column {
                        Text(
                            text = tr("pharmacy.title"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800,
                        )
                        Text(
                            text = tr("pharmacy.desc"),
                            fontSize = 11.sp,
                            color = Teal700,
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Location Permission / Active Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.isLocationEnabled) Emerald50 else Teal50,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                                    "Getting GPS Location..."
                                } else {
                                    tr("pharmacy.enableLocation")
                                },
                                fontSize = 11.5.sp,
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

                // Province Selector (ExposedDropdownMenuBox)
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

                // District Selector (ExposedDropdownMenuBox)
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

                // "View Pharmacies / ෆාමසි බලන්න" Primary Button
                PrimaryButton(
                    text = tr("pharmacy.viewPharmacies"),
                    enabled = true,
                    onClick = {
                        vm.refreshPharmacies()
                        showResultsList = true
                    },
                )

                // List of Nearby Pharmacies
                AnimatedVisibility(visible = showResultsList || state.pharmacyList.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    ) {
                        Text(
                            text = "📍 " + tr("pharmacy.nearbyResults") + " (" + state.pharmacyList.size + "):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Teal700,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        if (state.pharmacyList.isEmpty()) {
                            Text(
                                text = "No pharmacies found in this district. Select another location.",
                                fontSize = 12.sp,
                                color = Slate400,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(state.pharmacyList) { pharmacy ->
                                    PharmacyItemCard(
                                        pharmacy = pharmacy,
                                        lang = lang,
                                        onOpenDirections = {
                                            openMapDirections(context, pharmacy)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(tr("common.cancel"), fontSize = 13.sp, color = Slate600)
            }
        },
    )
}

@Composable
fun PharmacyItemCard(
    pharmacy: PharmacyItem,
    lang: String,
    onOpenDirections: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pharmacy.localizedName(lang),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                    )
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Emerald50,
                    ) {
                        Text(
                            text = "✓ " + tr("pharmacy.open"),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald600,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = pharmacy.localizedAddress(lang),
                    fontSize = 10.5.sp,
                    color = Slate600,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "📏 " + String.format(Locale.US, "%.1f km away", pharmacy.distanceKm),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal700,
                )
            }

            Spacer(Modifier.width(8.dp))

            SmallPill(
                text = "🗺️ " + tr("pharmacy.directions"),
                onClick = onOpenDirections,
                background = Teal600,
                textColor = Color.White,
            )
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
