package com.qualityapplied.vitacare.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.qualityapplied.vitacare.R
import com.qualityapplied.vitacare.data.NotificationCenter
import com.qualityapplied.vitacare.ui.components.SectionCard
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Amber50
import com.qualityapplied.vitacare.ui.theme.Amber600
import com.qualityapplied.vitacare.ui.theme.Rose500
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate200
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate600
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.tr
import java.util.Locale

/** Reminder settings + the list of notifications received. Opened from the 🔔 icon or the Profile. */
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val items = NotificationCenter.items

    var daily by remember { mutableStateOf(NotificationCenter.dailyEnabled(context)) }
    var hour by remember { mutableIntStateOf(NotificationCenter.dailyHour(context)) }
    var minute by remember { mutableIntStateOf(NotificationCenter.dailyMinute(context)) }
    var alerts by remember { mutableStateOf(NotificationCenter.alertsEnabled(context)) }
    var blocked by remember { mutableStateOf(!NotificationCenter.canNotify(context)) }

    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        blocked = !granted
    }

    fun askPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Everything counts as read once the user leaves this screen
    DisposableEffect(Unit) {
        onDispose { NotificationCenter.markAllRead(context) }
    }
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SmallPill("←  " + tr("map.back"), onClick = onBack, background = Slate100, textColor = Slate600)
            Spacer(Modifier.padding(horizontal = 6.dp))
            Text("🔔 " + tr("notif.title"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
        }

        if (blocked && (daily || alerts)) {
            Text(
                tr("notif.allow"),
                fontSize = 12.sp,
                color = Amber600,
                modifier = Modifier.fillMaxWidth().clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)).background(Amber50).padding(12.dp),
            )
        }

        SectionCard {
            Text(tr("notif.settings"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate400)
            Spacer(Modifier.height(8.dp))

            SettingRow(
                title = tr("notif.daily"),
                subtitle = tr("notif.dailyDesc"),
                checked = daily,
                onChange = {
                    daily = it
                    NotificationCenter.setDaily(context, it)
                    if (it) askPermission()
                },
            )
            if (daily) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(tr("notif.time"), fontSize = 13.sp, color = Slate600)
                    SmallPill("⏰ " + String.format(Locale.US, "%02d:%02d", hour, minute), onClick = {
                        TimePickerDialog(context, { _, h, m ->
                            hour = h
                            minute = m
                            NotificationCenter.setDailyTime(context, h, m)
                        }, hour, minute, true).show()
                    })
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Slate100))
            SettingRow(
                title = tr("notif.urgent"),
                subtitle = tr("notif.urgentDesc"),
                checked = alerts,
                onChange = {
                    alerts = it
                    NotificationCenter.setAlerts(context, it)
                    if (it) askPermission()
                },
            )
        }

        if (items.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallPill(tr("notif.markRead"), onClick = { NotificationCenter.markAllRead(context) }, background = Slate100, textColor = Slate600)
                SmallPill(tr("notif.clear"), onClick = { NotificationCenter.clearAll(context) }, background = Slate100, textColor = Slate600)
            }
        }

        if (items.isEmpty()) {
            Text(tr("notif.empty"), fontSize = 13.sp, color = Slate500, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            val now = System.currentTimeMillis()
            items.forEach { n ->
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.vitacare_logo),
                            contentDescription = "Notification Logo",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(n.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.weight(1f))
                                if (!n.read) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Rose500),
                                    )
                                }
                            }
                            Text(n.body, fontSize = 12.sp, color = Slate600, modifier = Modifier.padding(top = 2.dp))
                            Text(
                                DateUtils.getRelativeTimeSpanString(n.time, now, DateUtils.MINUTE_IN_MILLIS).toString(),
                                fontSize = 10.sp,
                                color = Slate400,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
            Text(subtitle, fontSize = 11.sp, color = Slate500)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Teal600, checkedThumbColor = Color.White),
        )
    }
}
