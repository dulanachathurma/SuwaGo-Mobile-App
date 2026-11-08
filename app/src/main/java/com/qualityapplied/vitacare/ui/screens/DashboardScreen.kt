package com.qualityapplied.vitacare.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.R
import kotlinx.coroutines.delay
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.HealthLog
import com.qualityapplied.vitacare.data.clean
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.Avatar
import com.qualityapplied.vitacare.ui.components.ChartSeries
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.LineChart
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SectionCard
import com.qualityapplied.vitacare.ui.components.StatusPill
import com.qualityapplied.vitacare.ui.formatDate
import com.qualityapplied.vitacare.ui.formatShortDate
import com.qualityapplied.vitacare.ui.theme.Amber400
import com.qualityapplied.vitacare.ui.theme.Emerald400
import com.qualityapplied.vitacare.ui.theme.Rose400
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Rose600
import com.qualityapplied.vitacare.ui.theme.Sky400
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    vm: AppViewModel,
    onOpenMetrics: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onOpenVault: () -> Unit = {},
) {
    val user = vm.user ?: return
    val scope = rememberCoroutineScope()

    var logs by remember { mutableStateOf<List<HealthLog>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf(user.weightKg?.clean() ?: "") }
    var height by rememberSaveable { mutableStateOf(user.heightCm?.clean() ?: "") }

    suspend fun loadLogs() {
        try {
            logs = ApiClient.getLogs()
        } catch (e: Exception) {
            error = vm.handleError(e)
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadLogs() }

    val latest = logs.lastOrNull()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Greeting
        val firstName = user.name.trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "Dulana"
        val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
        val greetingKey = when {
            hour in 5..11 -> "greeting.morning"
            hour in 12..16 -> "greeting.afternoon"
            hour in 17..20 -> "greeting.evening"
            else -> "greeting.night"
        }
        val dateText = remember {
            try {
                SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
            } catch (_: Exception) {
                ""
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(user.avatarUrl, user.name, 48.dp)
                Spacer(Modifier.width(12.dp))
                Text(
                    tr(greetingKey, firstName),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                )
            }
            if (dateText.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Teal50,
                ) {
                    Text(
                        text = dateText,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        maxLines = 1,
                    )
                }
            }
        }

        // 1990 SOS Emergency Call Button
        SosEmergencyButton()

        // Hero Banner Card Auto Slider (2s interval)
        AutoBannerSlider()

        // 3D Feature Cards (Placed directly below top promo banner slider & above BMI section)
        FeatureCardsSection(onOpenMetrics, onOpenReminders, onOpenVault)

        // BMI card
        SectionCard {
            Text(tr("dashboard.yourBmi").uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate400)
            if (loading) {
                Text(tr("common.loading"), fontSize = 14.sp, color = Slate400, modifier = Modifier.padding(top = 8.dp))
            } else if (latest != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(latest.bmi.clean(), fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = Slate800)
                    StatusPill(tr("bmi." + latest.category), latest.category)
                }
                BmiScale(latest.bmi)
            } else {
                Text(tr("dashboard.noData"), fontSize = 14.sp, color = Slate500, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // Trend chart
        if (logs.isNotEmpty()) {
            SectionCard {
                Text(tr("dashboard.trend"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                Spacer(Modifier.height(8.dp))
                val labels = logs.map { formatShortDate(it.createdAt) }
                LineChart(
                    labels = labels,
                    series = listOf(
                        ChartSeries(tr("dashboard.bmi"), logs.map { it.bmi.toFloat() }, Teal600, fill = true),
                        ChartSeries(tr("dashboard.healthyRange"), logs.map { 24.9f }, Emerald400, dashed = true, showPoints = false),
                        ChartSeries("", logs.map { 18.5f }, Emerald400, dashed = true, showPoints = false),
                    ),
                    minY = 15f,
                    maxY = 35f,
                    height = 210.dp,
                )
            }
        }

        // Add entry
        SectionCard {
            Text(tr("dashboard.addEntry"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    AppTextField(weight, { weight = it }, tr("dashboard.weight"), keyboardType = KeyboardType.Decimal)
                }
                Box(Modifier.weight(1f)) {
                    AppTextField(height, { height = it }, tr("dashboard.height"), keyboardType = KeyboardType.Decimal)
                }
            }
            Spacer(Modifier.height(12.dp))
            ErrorText(error)
            if (error.isNotEmpty()) Spacer(Modifier.height(12.dp))
            PrimaryButton(tr("dashboard.add"), onClick = {
                if (weight.isBlank() || height.isBlank()) {
                    error = "INVALID_INPUT"
                    return@PrimaryButton
                }
                error = ""
                scope.launch {
                    try {
                        vm.updateUser(ApiClient.addLog(weight, height))
                        loadLogs()
                    } catch (e: Exception) {
                        error = vm.handleError(e)
                    }
                }
            })
        }

        // History
        if (logs.isNotEmpty()) {
            SectionCard {
                Text(tr("dashboard.history"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                logs.reversed().take(5).forEach { log ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(formatDate(log.createdAt), fontSize = 12.sp, color = Slate600)
                        Text(log.weightKg.clean() + " kg · BMI " + log.bmi.clean(), fontSize = 12.sp, color = Slate600)
                        Text(
                            tr("dashboard.delete"),
                            fontSize = 12.sp,
                            color = Rose500,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    try {
                                        ApiClient.deleteLog(log.id)
                                        logs = logs.filter { it.id != log.id }
                                    } catch (e: Exception) {
                                        error = vm.handleError(e)
                                    }
                                }
                            }.padding(4.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

/** BMI scale from 15 to 40, split by WHO category boundaries, with a marker at the user's BMI. */
@Composable
private fun BmiScale(bmi: Double) {
    val fraction = (((bmi - 15.0) / 25.0).coerceIn(0.0, 1.0)).toFloat()
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50))) {
            Segment(14f, Sky400)
            Segment(26f, Emerald400)
            Segment(20f, Amber400)
            Segment(40f, Rose400)
        }
        Box(
            modifier = Modifier
                .offset(x = maxWidth * fraction - 2.dp, y = (-4).dp)
                .width(4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Slate800),
        )
    }
}

@Composable
private fun RowScope.Segment(weight: Float, color: Color) {
    Box(Modifier.weight(weight).fillMaxHeight().background(color))
}

@Composable
private fun AutoBannerSlider() {
    val banners = remember { listOf(R.drawable.home_banner, R.drawable.home_banner_2, R.drawable.home_banner_3) }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        tonalElevation = 2.dp,
        color = Color.White,
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
            Crossfade(
                targetState = currentIndex,
                animationSpec = tween(600),
                label = "bannerCrossfade",
            ) { targetIndex ->
                Image(
                    painter = painterResource(banners[targetIndex]),
                    contentDescription = "VitCare Banner",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                banners.indices.forEach { index ->
                    val active = index == currentIndex
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (active) 20.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (active) Teal600 else Color.White.copy(alpha = 0.7f)),
                    )
                }
            }
        }
    }
}

/**
 * Emergency 1990 SOS Call Card Component.
 * Refactored to use wrapContentHeight() with compact 16.dp horizontal and 8.dp vertical internal padding,
 * icon on left, title & subtitle stacked vertically (weight 1f) on the right of icon.
 */
@Composable
private fun SosEmergencyButton() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // Compact wrapContentHeight to prevent excessive vertical real estate
            .clickable {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1990"))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Rose600),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Compact padding: 16.dp horizontal, 8.dp vertical
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Icon on the left
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🚨", fontSize = 18.sp)
            }

            // Title and Subtitle stacked vertically on the right of the icon with weight(1f)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "1990 Emergency / සුව සැරිය",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Tap for immediate Suwa Seriya ambulance call",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Action Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
            ) {
                Text(
                    text = "CALL 1990",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Rose600,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun FeatureCardsSection(
    onOpenMetrics: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenVault: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Feature3DCard(
            title = tr("feature.vitals"),
            imageRes = R.drawable.feature_vitals,
            modifier = Modifier.weight(1f),
            onClick = onOpenMetrics,
        )
        Feature3DCard(
            title = tr("feature.reminders"),
            imageRes = R.drawable.feature_reminders,
            modifier = Modifier.weight(1f),
            onClick = onOpenReminders,
        )
        Feature3DCard(
            title = tr("feature.reports"),
            imageRes = R.drawable.feature_reports,
            modifier = Modifier.weight(1f),
            onClick = onOpenVault,
        )
    }
}

@Composable
private fun Feature3DCard(
    title: String,
    imageRes: Int,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .aspectRatio(0.55f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}
