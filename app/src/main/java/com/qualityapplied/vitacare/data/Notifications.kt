package com.qualityapplied.vitacare.data

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.qualityapplied.vitacare.MainActivity
import com.qualityapplied.vitacare.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class AppNotification(
    val id: Long,
    val title: String,
    val body: String,
    val time: Long,
    val read: Boolean,
)

/**
 * In-app notification list + phone notifications + the daily health reminder.
 *  - Daily reminder: AlarmManager (works after the app is closed, re-scheduled after reboot)
 *  - Health alert: shown right after saving a high / dangerous reading
 */
object NotificationCenter {
    private const val PREFS = "vitacare_notifications"
    private const val K_ITEMS = "items"
    private const val K_DAILY = "daily_enabled"
    private const val K_HOUR = "daily_hour"
    private const val K_MIN = "daily_min"
    private const val K_ALERTS = "alerts_enabled"
    private const val K_WELCOMED = "welcomed"
    private const val MAX_ITEMS = 50

    const val CH_REMINDER = "vitacare_reminder"
    const val CH_ALERT = "vitacare_alert"

    /** Notification list shown in the app (Compose reads this, so the bell badge updates itself). */
    var items by mutableStateOf<List<AppNotification>>(emptyList())
        private set

    val unread: Int get() = items.count { !it.read }

    private fun prefs(ctx: Context) =
        ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ---------- list ----------

    fun load(ctx: Context) {
        items = readItems(ctx)
    }

    private fun readItems(ctx: Context): List<AppNotification> {
        val raw = prefs(ctx).getString(K_ITEMS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                val title = o.optString("title").replace("VitaCare", "Suwago")
                val body = o.optString("body").replace("VitaCare", "Suwago")
                AppNotification(
                    id = o.optLong("id"),
                    title = title,
                    body = body,
                    time = o.optLong("time"),
                    read = o.optBoolean("read", false),
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeItems(ctx: Context, list: List<AppNotification>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject().put("id", it.id).put("title", it.title).put("body", it.body)
                    .put("time", it.time).put("read", it.read),
            )
        }
        prefs(ctx).edit().putString(K_ITEMS, arr.toString()).apply()
        items = list
    }

    fun markAllRead(ctx: Context) {
        writeItems(ctx, readItems(ctx).map { it.copy(read = true) })
    }

    fun clearAll(ctx: Context) {
        writeItems(ctx, emptyList())
    }

    /** Adds an entry to the in-app list and (optionally) shows a phone notification. */
    fun add(ctx: Context, title: String, body: String, showOnPhone: Boolean, channel: String = CH_ALERT) {
        val now = System.currentTimeMillis()
        val list = (listOf(AppNotification(now, title, body, now, false)) + readItems(ctx)).take(MAX_ITEMS)
        writeItems(ctx, list)
        if (showOnPhone) showOnPhone(ctx, title, body, channel, (now % 1_000_000L).toInt())
    }

    // ---------- settings ----------

    fun dailyEnabled(ctx: Context) = prefs(ctx).getBoolean(K_DAILY, false)
    fun dailyHour(ctx: Context) = prefs(ctx).getInt(K_HOUR, 20)
    fun dailyMinute(ctx: Context) = prefs(ctx).getInt(K_MIN, 0)
    fun alertsEnabled(ctx: Context) = prefs(ctx).getBoolean(K_ALERTS, true)

    fun setDaily(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(K_DAILY, enabled).apply()
        scheduleDaily(ctx)
    }

    fun setDailyTime(ctx: Context, hour: Int, minute: Int) {
        prefs(ctx).edit().putInt(K_HOUR, hour).putInt(K_MIN, minute).apply()
        scheduleDaily(ctx)
    }

    fun setAlerts(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(K_ALERTS, enabled).apply()
    }

    // ---------- phone notifications ----------

    fun canNotify(ctx: Context): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()

    private fun ensureChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val lc = localizedContext(ctx)
        nm.createNotificationChannel(
            NotificationChannel(CH_REMINDER, lc.getString(R.string.notif_daily), NotificationManager.IMPORTANCE_DEFAULT),
        )
        nm.createNotificationChannel(
            NotificationChannel(CH_ALERT, lc.getString(R.string.notif_urgent), NotificationManager.IMPORTANCE_HIGH),
        )
    }

    @SuppressLint("MissingPermission")
    private fun showOnPhone(ctx: Context, title: String, body: String, channel: String, notifId: Int) {
        if (!canNotify(ctx)) return
        ensureChannels(ctx)
        val open = PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val largeIcon = try {
            BitmapFactory.decodeResource(ctx.resources, R.drawable.vitacare_logo)
        } catch (_: Exception) {
            null
        }
        val n = NotificationCompat.Builder(ctx, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .apply {
                if (largeIcon != null) setLargeIcon(largeIcon)
            }
            .setColor(0xFF0D9488.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(open)
            .setPriority(if (channel == CH_ALERT) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(ctx).notify(notifId, n)
        } catch (_: SecurityException) {
        }
    }

    // ---------- daily reminder (AlarmManager) ----------

    private fun reminderIntent(ctx: Context): PendingIntent = PendingIntent.getBroadcast(
        ctx, 4711, Intent(ctx, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    /** (Re)schedules the next daily reminder, or cancels it when the switch is off. */
    fun scheduleDaily(ctx: Context) {
        val app = ctx.applicationContext
        val am = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!dailyEnabled(app)) {
            am.cancel(reminderIntent(app))
            return
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, dailyHour(app))
            set(Calendar.MINUTE, dailyMinute(app))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, reminderIntent(app))
    }

    /** Schedules an alarm for pill/medicine time. */
    fun schedulePillAlarm(ctx: Context, reminderId: String, medicineName: String, dosage: String, reminderTime: String) {
        try {
            val app = ctx.applicationContext
            val am = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(app, ReminderReceiver::class.java).apply {
                putExtra("pill_med", medicineName)
                putExtra("pill_dosage", dosage)
            }
            val pIntent = PendingIntent.getBroadcast(
                app,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val parts = reminderTime.split(":", " ")
            if (parts.size >= 2) {
                var hour = parts[0].toIntOrNull() ?: 8
                val min = parts[1].toIntOrNull() ?: 0
                val isPm = reminderTime.uppercase().contains("PM")
                val isAm = reminderTime.uppercase().contains("AM")
                if (isPm && hour < 12) hour += 12
                if (isAm && hour == 12) hour = 0

                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, min)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
                }
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pIntent)
            }
        } catch (_: Exception) {}
    }

    // ---------- events ----------

    /** Called after a reading is saved: warns when it is high or dangerous. */
    fun healthAlert(ctx: Context, metric: Metric) {
        if (!alertsEnabled(ctx)) return
        if (!(metric.urgent || metric.status == "high")) return
        val lc = localizedContext(ctx)
        val reading = if (metric.value2 != null) metric.value.clean() + "/" + metric.value2.clean() else metric.value.clean()
        val body = lc.getString(R.string.notif_alertBody) + ": " +
            stringByName(lc, "metric_" + metric.type) + " " + reading +
            " (" + stringByName(lc, "status_" + metric.status) + ")"
        add(ctx, lc.getString(R.string.notif_alertTitle), body, showOnPhone = true, channel = CH_ALERT)
    }

    /** One friendly first message so the list is never empty on a new account. */
    fun welcomeOnce(ctx: Context) {
        val p = prefs(ctx)
        if (p.getBoolean(K_WELCOMED, false)) return
        p.edit().putBoolean(K_WELCOMED, true).apply()
        val lc = localizedContext(ctx)
        add(ctx, lc.getString(R.string.notif_welcomeTitle), lc.getString(R.string.notif_welcomeBody), showOnPhone = false)
    }

    /** Logout: stop the alarm and forget this account's notifications and settings. */
    fun onLogout(ctx: Context) {
        val app = ctx.applicationContext
        val am = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(reminderIntent(app))
        prefs(app).edit().clear().apply()
        items = emptyList()
    }
}

/** Fires every day at the chosen time or for medicine pill alarms. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val med = intent?.getStringExtra("pill_med")
        val dosage = intent?.getStringExtra("pill_dosage")
        if (!med.isNullOrBlank()) {
            NotificationCenter.add(
                context,
                "💊 Pill Time: $med",
                "It is time to take $dosage of $med.",
                showOnPhone = true,
                channel = NotificationCenter.CH_ALERT,
            )
        } else {
            val lc = localizedContext(context)
            NotificationCenter.add(
                context,
                lc.getString(R.string.notif_reminderTitle),
                lc.getString(R.string.notif_reminderBody),
                showOnPhone = true,
                channel = NotificationCenter.CH_REMINDER,
            )
            NotificationCenter.scheduleDaily(context) // next day
        }
    }
}

/** Alarms are cleared when the phone restarts, so schedule again. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) NotificationCenter.scheduleDaily(context)
    }
}
