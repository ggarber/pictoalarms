package com.ggarber.pictoalarms.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import org.json.JSONArray
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlarmScheduler {
    private const val ACTION_ALARM = "com.ggarber.pictoalarms.ACTION_ALARM"

    fun scheduleNextAlarm(context: Context) {
        val json = readSavedJson(context) ?: return
        val nextAlarm = findNextAlarm(json)

        if (nextAlarm != null) {
            val (time, pictogram) = nextAlarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM
                putExtra("pictogram", pictogram)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val now = LocalDateTime.now()
            var alarmDateTime = LocalDateTime.of(now.toLocalDate(), time)
            
            if (alarmDateTime.isBefore(now)) {
                alarmDateTime = alarmDateTime.plusDays(1)
            }

            val triggerTime = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Scheduled alarm for $pictogram at $alarmDateTime")
        } else {
            Log.d("AlarmScheduler", "No alarms to schedule")
        }
    }

    private fun readSavedJson(context: Context): String? {
        val file = File(context.filesDir, "api_data.json")
        return if (file.exists()) file.readText() else null
    }

    private fun findNextAlarm(json: String): Pair<LocalTime, String>? {
        try {
            val array = JSONArray(json)
            val now = LocalTime.now()
            var nextAlarmTime: LocalTime? = null
            var nextPictogram: String? = null
            val formatter = DateTimeFormatter.ofPattern("[H][HH]:mm[:ss]")

            for (i in 0 until array.length()) {
                try {
                    val obj = array.getJSONObject(i)
                    val timeStr = obj.getString("time")
                    val pictogram = obj.getString("pictogram")
                    val alarmTime = LocalTime.parse(timeStr, formatter)
                    
                    if (alarmTime.isAfter(now)) {
                        if (nextAlarmTime == null || alarmTime.isBefore(nextAlarmTime)) {
                            nextAlarmTime = alarmTime
                            nextPictogram = pictogram
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AlarmScheduler", "Error parsing alarm item at index $i", e)
                }
            }
            
            // If no alarm is after now today, find the first one tomorrow
            if (nextAlarmTime == null && array.length() > 0) {
                for (i in 0 until array.length()) {
                    try {
                        val obj = array.getJSONObject(i)
                        val timeStr = obj.getString("time")
                        val pictogram = obj.getString("pictogram")
                        val alarmTime = LocalTime.parse(timeStr, formatter)
                        if (nextAlarmTime == null || alarmTime.isBefore(nextAlarmTime)) {
                            nextAlarmTime = alarmTime
                            nextPictogram = pictogram
                        }
                    } catch (e: Exception) {
                    }
                }
            }

            if (nextAlarmTime != null && nextPictogram != null) {
                return Pair(nextAlarmTime, nextPictogram)
            }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error parsing JSON array", e)
        }
        return null
    }
}
