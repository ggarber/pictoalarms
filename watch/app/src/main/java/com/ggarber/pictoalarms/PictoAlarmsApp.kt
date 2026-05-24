package com.ggarber.pictoalarms

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ggarber.pictoalarms.data.ApiWorker
import java.util.concurrent.TimeUnit

class PictoAlarmsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("PictoAlarmsApp", "Application onCreate")
        scheduleApiFetch()
        com.ggarber.pictoalarms.data.AlarmScheduler.scheduleNextAlarm(this)
    }

    private fun scheduleApiFetch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val apiWorkRequest = PeriodicWorkRequestBuilder<ApiWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ApiFetchWork",
            ExistingPeriodicWorkPolicy.KEEP,
            apiWorkRequest,
        )
    }
}
