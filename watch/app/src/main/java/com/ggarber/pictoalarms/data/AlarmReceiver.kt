package com.ggarber.pictoalarms.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ggarber.pictoalarms.presentation.PictogramActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received: ${intent.action}")
        
        when (intent.action) {
            "com.ggarber.pictoalarms.ACTION_ALARM" -> {
                val pictogram = intent.getStringExtra("pictogram") ?: "Unknown"
                Log.d("AlarmReceiver", "Starting PictogramActivity for: $pictogram")
                
                val activityIntent = Intent(context, PictogramActivity::class.java).apply {
                    putExtra("pictogram", pictogram)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(activityIntent)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("AlarmReceiver", "Boot completed, rescheduling alarms")
                AlarmScheduler.scheduleNextAlarm(context)
            }
        }
    }
}
