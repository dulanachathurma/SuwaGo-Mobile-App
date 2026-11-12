package com.qualityapplied.vitacare.ui.screens


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.qualityapplied.vitacare.ui.showTimePickerSafe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ApiClient
import com.qualityapplied.vitacare.data.NotificationCenter
import com.qualityapplied.vitacare.data.ReminderItem
import com.qualityapplied.vitacare.ui.components.AppTextField
import com.qualityapplied.vitacare.ui.components.ChoiceChip
import com.qualityapplied.vitacare.ui.components.ErrorText
import com.qualityapplied.vitacare.ui.components.PrimaryButton
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Rose50
import com.qualityapplied.vitacare.ui.theme.Rose700
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Teal200
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal50
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr
import kotlinx.coroutines.launch

@Composable
fun RemindersScreen() {
    val scope = rememberCoroutineScope()
    var reminders by remember { mutableStateOf<List<ReminderItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun loadReminders() {
        scope.launch {
            loading = true
            try {
                reminders = ApiClient.getReminders()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load reminders"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadReminders() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 125.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("💊 Medicine Schedule Manager", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text("Set pill times & get local alarm notifications", fontSize = 12.sp, color = Slate500)
            }
            Spacer(Modifier.width(8.dp))
            SmallPill("+ Add Pill", onClick = { showAddDialog = true }, background = Teal600, textColor = Color.White)
        }

        ErrorText(error)

        if (loading) {
            Text(tr("common.loading"), fontSize = 14.sp, color = Slate500)
        } else if (reminders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("⏰", fontSize = 38.sp)
                    Text("No Pill Reminders Yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.padding(top = 8.dp))
                    Text("Add your prescribed medications to get timely alarm alerts.", fontSize = 12.sp, color = Slate500, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                reminders.forEach { r ->
                    ReminderCard(
                        reminder = r,
                        onToggle = { active ->
                            scope.launch {
                                try {
                                    ApiClient.toggleReminder(r.id, active)
                                    loadReminders()
                                } catch (_: Exception) {}
                            }
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    ApiClient.deleteReminder(r.id)
                                    loadReminders()
                                } catch (_: Exception) {}
                            }
                        },
                    )
                }
            }
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onSaved = {
                    showAddDialog = false
                    loadReminders()
                },
            )
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (reminder.isActive) Teal50 else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, if (reminder.isActive) Teal200 else Slate100),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text("💊", fontSize = 26.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(reminder.medicineName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    Text("Dosage: ${reminder.dosage}", fontSize = 12.sp, color = Teal700, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text("⏰ ${reminder.reminderTime}", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.SemiBold)
                        Text("🔁 ${reminder.repeatDays.joinToString(", ")}", fontSize = 11.sp, color = Slate500)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = reminder.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Teal600),
                )
                Text(
                    "Delete",
                    fontSize = 11.sp,
                    color = Rose700,
                    modifier = Modifier.padding(top = 4.dp).background(Rose50, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp).clickable { onDelete() },
                )
            }
        }
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("08:00 AM") }

    val times = remember { listOf("07:00 AM", "08:00 AM", "12:00 PM", "02:00 PM", "06:00 PM", "08:00 PM", "10:00 PM") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val openCustomTimePicker = {
        val cal = Calendar.getInstance()
        showTimePickerSafe(
            context = context,
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = false,
        ) { hourOfDay, minute ->
            val calendarCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            selectedTime = SimpleDateFormat("hh:mm a", Locale.US).format(calendarCal.time)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Pill Reminder", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(medName, { medName = it }, "Medicine Name (e.g. Paracetamol)")
                AppTextField(dosage, { dosage = it }, "Dosage (e.g. 500 mg, 1 Tablet)")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Select Pill Alarm Time:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(6.dp))
                    SmallPill("⏰ Custom Time", onClick = openCustomTimePicker, background = Teal50, textColor = Teal700)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    times.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { time ->
                                ChoiceChip(time, selected = selectedTime == time, onClick = { selectedTime = time })
                            }
                        }
                    }
                    if (!times.contains(selectedTime)) {
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            ChoiceChip("⏰ $selectedTime (Custom)", selected = true, onClick = openCustomTimePicker)
                        }
                    }
                }

                ErrorText(error)
            }
        },
        confirmButton = {
            PrimaryButton(
                text = if (loading) "Saving..." else "Save Reminder",
                enabled = !loading && medName.isNotBlank() && dosage.isNotBlank(),
                onClick = {
                    loading = true
                    error = ""
                    scope.launch {
                        try {
                            val r = ApiClient.createReminder(
                                medicineName = medName,
                                dosage = dosage,
                                reminderTime = selectedTime,
                                repeatDays = listOf("Daily"),
                            )
                            NotificationCenter.schedulePillAlarm(context, r.id, r.medicineName, r.dosage, r.reminderTime)
                            NotificationCenter.add(
                                context,
                                "💊 Pill Reminder Scheduled: ${r.medicineName}",
                                "Daily alarm set for ${r.dosage} at ${r.reminderTime}.",
                                showOnPhone = true,
                            )
                            onSaved()
                        } catch (e: Exception) {
                            error = e.message ?: "Failed to save reminder"
                        } finally {
                            loading = false
                        }
                    }
                },
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate600) }
        },
    )
}
