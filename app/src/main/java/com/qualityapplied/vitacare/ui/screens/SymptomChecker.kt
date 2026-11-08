package com.qualityapplied.vitacare.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.SPECIALTIES
import com.qualityapplied.vitacare.data.SYMPTOMS
import com.qualityapplied.vitacare.data.hasUrgentSymptom
import com.qualityapplied.vitacare.data.recommendSpecialists
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Rose200
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose600
import com.qualityapplied.vitacare.ui.theme.Rose700
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate200
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal200
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr

/** Tick your symptoms -> best matching specialists (the old DoctorFinder idea, now inside the app). */
@Composable
fun SymptomChecker(onFindOnMap: (String) -> Unit) {
    // Saved as one comma separated text so it survives rotation
    var selectedCsv by rememberSaveable { mutableStateOf("") }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var needOne by rememberSaveable { mutableStateOf(false) }

    val selected: Set<String> = selectedCsv.split(",").filter { it.isNotBlank() }.toSet()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(tr("doctor.intro"), fontSize = 12.sp, color = Slate500)

        val cardShape = RoundedCornerShape(16.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SYMPTOMS.forEach { s ->
                val on = s.id in selected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val next = if (on) selected - s.id else selected + s.id
                            selectedCsv = next.joinToString(",")
                            showResult = false
                            needOne = false
                        },
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = if (on) Teal50 else Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, if (on) Teal600 else Slate100),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = on,
                            onCheckedChange = null,
                            modifier = Modifier.padding(6.dp),
                            colors = CheckboxDefaults.colors(checkedColor = Teal600, uncheckedColor = Slate400),
                        )
                        Text(
                            tr("symptom." + s.id),
                            fontSize = 14.sp,
                            fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                            color = if (on) Teal700 else Slate800,
                        )
                    }
                }
            }
        }

        if (needOne) {
            Text(
                tr("doctor.selectOne"),
                color = Rose600,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Rose50).padding(12.dp),
            )
        }

        PrimaryButton(tr("doctor.find"), onClick = {
            if (selected.isEmpty()) needOne = true else showResult = true
        })

        if (showResult && selected.isNotEmpty()) {
            val matches = recommendSpecialists(selected)
            val cardShape = RoundedCornerShape(16.dp)

            if (hasUrgentSymptom(selected)) {
                Text(
                    "🚨 " + tr("doctor.urgent"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rose700,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(Rose50)
                        .border(1.dp, Rose200, cardShape)
                        .padding(14.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(Teal50)
                    .border(1.dp, Teal200, cardShape)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(tr("doctor.result"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate800)
                matches.forEach { m ->
                    val spec = SPECIALTIES.firstOrNull { it.id == m.specId } ?: return@forEach
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                spec.icon + " " + tr("fd.s." + spec.id),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800,
                                modifier = Modifier.weight(1f),
                            )
                            Text("${m.percent}% " + tr("doctor.match"), fontSize = 12.sp, color = Slate600)
                        }
                        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Slate200)) {
                            Box(
                                Modifier
                                    .fillMaxWidth(m.percent / 100f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Teal600),
                            )
                        }
                        Row {
                            SmallPill("📍 " + tr("fd.findNearby"), onClick = { onFindOnMap(spec.id) })
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row {
                    SmallPill(tr("doctor.reset"), onClick = {
                        selectedCsv = ""
                        showResult = false
                    }, background = Slate100, textColor = Slate500)
                }
            }
            Text(tr("doctor.disclaimer"), fontSize = 11.sp, color = Slate400, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}
