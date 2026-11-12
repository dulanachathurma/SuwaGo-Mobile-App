package com.qualityapplied.vitacare.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.qualityapplied.vitacare.data.LocationHelper
import com.qualityapplied.vitacare.data.NearbyPlace
import com.qualityapplied.vitacare.data.PlacesApi
import com.qualityapplied.vitacare.data.SPECIALTIES
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Amber200
import com.qualityapplied.vitacare.ui.theme.Amber50
import com.qualityapplied.vitacare.ui.theme.Amber600
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

// Colombo - default fallback when GPS is disabled
private const val FALLBACK_LAT = 6.9271
private const val FALLBACK_LON = 79.8612

private fun isValidLocation(lat: Double, lon: Double): Boolean {
    return lat in -90.0..90.0 && lon in -180.0..180.0 && (lat != 0.0 || lon != 0.0)
}

/**
 * Full screen map INSIDE the app (OpenStreetMap, no API key).
 * Finds the user's location, then lists + pins clinics / hospitals near them.
 */
@Composable
fun NearbyMapScreen(specId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val spec = SPECIALTIES.firstOrNull { it.id == specId }

    var phase by remember { mutableStateOf("locating") } // locating | searching | done | error
    var center by remember { mutableStateOf(GeoPoint(FALLBACK_LAT, FALLBACK_LON)) }
    var usedFallback by remember { mutableStateOf(false) }
    var places by remember { mutableStateOf(emptyList<NearbyPlace>()) }
    var focus by remember { mutableStateOf<NearbyPlace?>(null) }
    var attempt by remember { mutableIntStateOf(0) }
    var asked by rememberSaveable { mutableStateOf(false) }

    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        attempt++ // continue after the user answered the permission dialog
    }

    LaunchedEffect(attempt) {
        phase = "locating"
        focus = null
        if (!LocationHelper.hasPermission(context) && !asked) {
            asked = true
            permLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return@LaunchedEffect
        }
        LocationHelper.clearCache()
        PlacesApi.clearCache()
        val loc = LocationHelper.current(context, forceFresh = true)
        val validLoc = loc != null && isValidLocation(loc.first, loc.second)
        usedFallback = !validLoc

        val lat = if (validLoc) loc!!.first else FALLBACK_LAT
        val lon = if (validLoc) loc!!.second else FALLBACK_LON
        center = GeoPoint(lat, lon)
        phase = "searching"
        try {
            places = PlacesApi.nearby(lat, lon, specId)
            phase = "done"
        } catch (_: Exception) {
            phase = "error"
        }
    }

    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Surface(color = Color.White, shadowElevation = 3.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmallPill("←  " + tr("map.back"), onClick = onBack, background = Slate100, textColor = Slate700)
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tr("map.title"),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (spec != null) {
                        Text(spec.icon + " " + tr("fd.s." + spec.id), fontSize = 13.sp, color = Teal700, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        if (usedFallback && phase != "locating") {
            Text(
                tr("map.noLocation"),
                fontSize = 11.sp,
                color = Amber600,
                modifier = Modifier.fillMaxWidth().background(Amber50).padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        // Map
        Box(modifier = Modifier.fillMaxWidth().weight(0.55f)) {
            OsmMap(
                center = center,
                places = places,
                focus = focus,
                youLabel = tr("map.you"),
                unnamed = tr("map.unnamed"),
                onPick = { focus = it },
                modifier = Modifier.fillMaxSize(),
            )
            if (phase == "locating" || phase == "searching") {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(Color.White, androidx.compose.foundation.shape.RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(16.dp).height(16.dp), strokeWidth = 2.dp, color = Teal700)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        tr(if (phase == "locating") "map.locating" else "map.searching"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate700,
                    )
                }
            }
        }

        // Results
        Column(modifier = Modifier.fillMaxWidth().weight(0.45f).background(Color.White)) {
            when (phase) {
                "error" -> Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ErrorText("MAP_FAILED")
                    Row { SmallPill(tr("map.retry"), onClick = { attempt++ }) }
                }
                "done" -> if (places.isEmpty()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(tr("map.none"), fontSize = 13.sp, color = Slate500)
                        Row { SmallPill(tr("map.retry"), onClick = { attempt++ }) }
                    }
                } else {
                    Text(
                        "${places.size} " + tr("map.found"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 4.dp),
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 125.dp),
                    ) {
                        items(places, key = { it.id }) { p ->
                            val unnamedStr = tr("map.unnamed")
                            PlaceRow(
                                p = p,
                                selected = focus?.id == p.id,
                                unnamed = unnamedStr,
                                onClick = { focus = p },
                                onDirections = {
                                    val dest = "${p.lat},${p.lon}"
                                    val label = Uri.encode(p.name.ifBlank { unnamedStr })
                                    val navUri = Uri.parse("google.navigation:q=$dest")
                                    val webDirectionsUrl = "https://www.google.com/maps/dir/?api=1&destination=$dest&travelmode=driving"
                                    val geoUri = Uri.parse("geo:$dest?q=$dest($label)")

                                    try {
                                        val navIntent = Intent(Intent.ACTION_VIEW, navUri).apply {
                                            setPackage("com.google.android.apps.maps")
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(navIntent)
                                    } catch (_: Exception) {
                                        try {
                                            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(mapIntent)
                                        } catch (_: Exception) {
                                            try {
                                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webDirectionsUrl)).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                                context.startActivity(webIntent)
                                            } catch (_: Exception) {
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
                else -> Box(Modifier.weight(1f))
            }
            Text(
                tr("map.credit"),
                fontSize = 9.sp,
                color = Slate400,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun PlaceRow(p: NearbyPlace, selected: Boolean, unnamed: String, onClick: () -> Unit, onDirections: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) Teal50 else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                p.name.ifBlank { unnamed },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate800,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val sub = distanceText(p.distanceM) + if (p.match) "  ·  ⭐ " + tr("map.bestMatch") else ""
            Text(sub, fontSize = 11.sp, color = if (p.match) Teal700 else Slate500)
        }
        Spacer(Modifier.width(8.dp))
        SmallPill("🧭 " + tr("map.directions"), onClick = onDirections)
    }
}

@Composable
private fun distanceText(m: Double): String =
    if (m < 1000) "${m.roundToInt()} m" else String.format(Locale.US, "%.1f", m / 1000.0) + " " + tr("map.km")

/** OpenStreetMap view (osmdroid) wrapped for Compose. */
@Composable
private fun OsmMap(
    center: GeoPoint,
    places: List<NearbyPlace>,
    focus: NearbyPlace?,
    youLabel: String,
    unnamed: String,
    onPick: (NearbyPlace) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = 4.0
            controller.setZoom(14.0)
            controller.setCenter(GeoPoint(FALLBACK_LAT, FALLBACK_LON))
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Move to the user
    LaunchedEffect(center) {
        mapView.controller.setZoom(14.0)
        mapView.controller.animateTo(center)
    }

    // Draw "you" + all places
    LaunchedEffect(center, places) {
        mapView.overlays.clear()
        val you = Marker(mapView).apply {
            position = center
            title = youLabel
            icon = dot(context, 0xFF0284C7.toInt())
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        mapView.overlays.add(you)
        places.forEach { p ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(p.lat, p.lon)
                title = p.name.ifBlank { unnamed }
                snippet = distanceShort(p.distanceM)
                icon = dot(context, if (p.match) 0xFF059669.toInt() else 0xFFE11D48.toInt())
                relatedObject = p
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                setOnMarkerClickListener { m, _ ->
                    m.showInfoWindow()
                    onPick(p)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // Tapping a list row focuses that pin
    LaunchedEffect(focus) {
        val f = focus ?: return@LaunchedEffect
        mapView.controller.animateTo(GeoPoint(f.lat, f.lon))
        mapView.overlays.filterIsInstance<Marker>().firstOrNull { it.relatedObject == f }?.showInfoWindow()
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}

private fun distanceShort(m: Double): String =
    if (m < 1000) "${m.roundToInt()} m" else String.format(Locale.US, "%.1f km", m / 1000.0)

/** Round coloured map pin with a white border. */
private fun dot(context: Context, color: Int): Drawable {
    val d = context.resources.displayMetrics.density
    val size = (26 * d).toInt()
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    paint.color = color
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 3 * d, paint)
    return BitmapDrawable(context.resources, bmp)
}
