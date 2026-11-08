package com.qualityapplied.vitacare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.Metric
import com.qualityapplied.vitacare.data.clean
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.ChartSeries
import com.qualityapplied.vitacare.ui.components.ChoiceChip
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.LineChart
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SectionCard
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.components.StatusPill
import com.qualityapplied.vitacare.ui.components.statusColors
import com.qualityapplied.vitacare.ui.formatDate
import com.qualityapplied.vitacare.ui.formatShortDate
import com.qualityapplied.vitacare.ui.theme.Amber200
import com.qualityapplied.vitacare.ui.theme.Amber50
import com.qualityapplied.vitacare.ui.theme.Rose200
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Rose600
import com.qualityapplied.vitacare.ui.theme.Rose700
import com.qualityapplied.vitacare.ui.theme.Sky600
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch

private data class MetricType(
    val id: String,
    val icon: String,
    val unit: String,
    val range: String,
    val specialty: String,
)

private val TYPES = listOf(
    MetricType("bloodSugar", "🩸", "mg/dL", "70–99", "endocrinologist"),
    MetricType("cholesterol", "🧪", "mg/dL", "< 200", "cardiologist"),
    MetricType("bloodPressure", "🩺", "mmHg", "90/60 – 119/79", "cardiologist"),
    MetricType("heartRate", "💓", "bpm", "60–100", "cardiologist"),
    MetricType("oxygen", "🫁", "%", "≥ 95", "pulmonologist"),
    MetricType("temperature", "🌡️", "°C", "35.5–37.5", "generalPhysician"),
)

private fun show(m: Metric): String =
    if (m.value2 != null) m.value.clean() + "/" + m.value2.clean() else m.value.clean()

@Composable
fun MetricsScreen(vm: AppViewModel, onFindDoctor: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val appContext = androidx.compose.ui.platform.LocalContext.current

    var type by rememberSaveable { mutableStateOf("bloodSugar") }
    var all by remember { mutableStateOf<List<Metric>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var value by rememberSaveable { mutableStateOf("") }
    var value2 by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Metric?>(null) }

    suspend fun loadMetrics() {
        try {
            all = ApiClient.getMetrics()
        } catch (e: Exception) {
            error = vm.handleError(e)
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadMetrics() }

    val current = TYPES.first { it.id == type }
    val isBp = type == "bloodPressure"
    val metrics = all.filter { it.type == type }
    val latest = metrics.lastOrNull()

    fun selectType(id: String) {
        type = id
        value = ""
        value2 = ""
        note = ""
        error = ""
        result = null
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(tr("metrics.title"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
            Text(tr("metrics.intro"), fontSize = 12.sp, color = Slate500)
        }

        // Metric selector
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TYPES.forEach { item ->
                ChoiceChip(item.icon + " " + tr("cond." + item.id), selected = type == item.id, onClick = { selectType(item.id) })
            }
        }

        // Latest reading
        SectionCard {
            Text(tr("metric.$type").uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate400)
            if (loading) {
                Text(tr("common.loading"), fontSize = 14.sp, color = Slate400, modifier = Modifier.padding(top = 8.dp))
            } else if (latest != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(show(latest), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Slate800)
                        Text(" " + current.unit, fontSize = 14.sp, color = Slate400, modifier = Modifier.padding(bottom = 4.dp))
                    }
                    StatusPill(tr("status." + latest.status), latest.status)
                }
            } else {
                Text(tr("metrics.none"), fontSize = 14.sp, color = Slate500, modifier = Modifier.padding(top = 8.dp))
            }
            Text(
                tr("metrics.normalRange") + ": " + current.range + " " + current.unit,
                fontSize = 11.sp,
                color = Slate400,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        // Doctor suggestion when the latest reading is outside the normal range
        if (latest != null && latest.status != "normal") {
            val severe = latest.urgent || latest.status == "high"
            val shape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(if (severe) Rose50 else Amber50)
                    .border(1.dp, if (severe) Rose200 else Amber200, shape)
                    .padding(16.dp),
            ) {
                if (latest.urgent) {
                    Text("🚨 " + tr("metrics.urgent"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Rose700)
                    Spacer(Modifier.height(8.dp))
                }
                Text(tr("metrics.seeSpecialist"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                Text("🩺 " + tr("spec." + current.specialty), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text(tr("metrics.advice." + latest.status), fontSize = 12.sp, color = Slate600, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(12.dp))
                Row { SmallPill(tr("metrics.findDoctor"), onClick = { onFindDoctor(current.specialty) }) }
            }
        }

        // Trend
        if (metrics.isNotEmpty()) {
            SectionCard {
                Text(tr("metrics.trend"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                Spacer(Modifier.height(8.dp))
                val labels = metrics.map { formatShortDate(it.measuredAt) }
                val series = if (isBp) {
                    listOf(
                        ChartSeries(tr("metrics.systolic"), metrics.map { it.value.toFloat() }, Rose600),
                        ChartSeries(tr("metrics.diastolic"), metrics.map { (it.value2 ?: it.value).toFloat() }, Sky600),
                    )
                } else {
                    listOf(ChartSeries(tr("metric.$type"), metrics.map { it.value.toFloat() }, Teal600))
                }
                LineChart(labels = labels, series = series, height = 190.dp)
            }
        }

        // Add reading
        SectionCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isBp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            AppTextField(value, { value = it }, tr("metrics.systolic"), keyboardType = KeyboardType.Decimal)
                        }
                        Box(Modifier.weight(1f)) {
                            AppTextField(value2, { value2 = it }, tr("metrics.diastolic"), keyboardType = KeyboardType.Number)
                        }
                    }
                } else {
                    AppTextField(
                        value, { value = it },
                        tr("metrics.value") + " (" + current.unit + ")",
                        keyboardType = KeyboardType.Decimal,
                    )
                }
                AppTextField(note, { if (it.length <= 200) note = it }, tr("metrics.note"))

                ErrorText(error)

                result?.let { m ->
                    val (bg, fg) = statusColors(m.status)
                    Text(
                        show(m) + " " + current.unit + " — " + tr("status." + m.status),
                        color = fg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(bg).padding(12.dp),
                    )
                }

                PrimaryButton(tr("metrics.add"), onClick = {
                    if (value.isBlank() || (isBp && value2.isBlank())) {
                        error = "INVALID_INPUT"
                        return@PrimaryButton
                    }
                    error = ""
                    result = null
                    scope.launch {
                        try {
                            val r = ApiClient.addMetric(type, value, value2, note)
                            result = r.metric
                            // High / dangerous reading -> health alert notification
                            com.qualityapplied.vitacare.data.NotificationCenter.healthAlert(appContext, r.metric)
                            value = ""
                            value2 = ""
                            note = ""
                            loadMetrics()
                        } catch (e: Exception) {
                            error = vm.handleError(e)
                        }
                    }
                })
            }
        }

        // History
        if (metrics.isNotEmpty()) {
            SectionCard {
                Text(tr("metrics.history"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                metrics.reversed().take(6).forEach { m ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(formatDate(m.measuredAt), fontSize = 12.sp, color = Slate600)
                        Text(show(m) + " " + current.unit, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate600)
                        StatusPill(tr("status." + m.status), m.status)
                        Text(
                            "✕",
                            fontSize = 14.sp,
                            color = Rose500,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    try {
                                        ApiClient.deleteMetric(m.id)
                                        all = all.filter { it.id != m.id }
                                    } catch (e: Exception) {
                                        error = vm.handleError(e)
                                    }
                                }
                            }.padding(6.dp),
                        )
                    }
                }
            }
        }

        Text(tr("metrics.disclaimer"), fontSize = 11.sp, color = Slate400)
        Spacer(Modifier.height(4.dp))
    }
}
