package com.ggarber.pictoalarms.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.security.ProviderInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: Exception) {
            Log.e("ApiWorker", "Google Play Services not available, SSL might fail", e)
        }

        val sharedPrefs = applicationContext.getSharedPreferences("picto_alarms", Context.MODE_PRIVATE)
        val deviceId = inputData.getString("deviceId") ?: sharedPrefs.getString("deviceId", null)

        if (deviceId.isNullOrBlank()) {
            Log.d("ApiWorker", "No deviceId found, skipping request")
            return@withContext Result.success()
        }

        val client = OkHttpClient()
        val url = "https://pictoalarms.vercel.app/api/alarms?deviceId=$deviceId"
        Log.d("ApiWorker", "Requesting alarms from URL: $url for deviceId: $deviceId")

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body.string()
                    Log.d("ApiWorker", "Response received: $json")
                    saveJson(json)
                    AlarmScheduler.scheduleNextAlarm(applicationContext)
                    val nextAlarm = findNextAlarm(json)
                    Log.d("ApiWorker", "Next alarm found: $nextAlarm")
                    Result.success(workDataOf("nextAlarm" to nextAlarm))
                } else {
                    Log.e("ApiWorker", "API request failed with code: ${response.code}")
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Log.e("ApiWorker", "Error fetching data from API", e)
            Result.failure()
        }
    }

    private fun findNextAlarm(json: String): String? {
        try {
            val array = JSONArray(json)
            val now = LocalTime.now()
            var nextAlarm: LocalTime? = null
            val formatter = DateTimeFormatter.ofPattern("[H][HH]:mm[:ss]")

            for (i in 0 until array.length()) {
                var timeStr: String? = null
                try {
                    val obj = array.getJSONObject(i)
                    timeStr = obj.getString("time")
                    val alarmTime = LocalTime.parse(timeStr, formatter)
                    
                    if (alarmTime.isAfter(now)) {
                        if (nextAlarm == null || alarmTime.isBefore(nextAlarm)) {
                            nextAlarm = alarmTime
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ApiWorker", "Error parsing alarm item at index $i: $timeStr", e)
                }
            }
            
            // If no alarm is after now today, find the first one tomorrow
            if (nextAlarm == null && array.length() > 0) {
                for (i in 0 until array.length()) {
                    try {
                        val obj = array.getJSONObject(i)
                        val timeStr = obj.getString("time")
                        val alarmTime = LocalTime.parse(timeStr, formatter)
                        if (nextAlarm == null || alarmTime.isBefore(nextAlarm)) {
                            nextAlarm = alarmTime
                        }
                    } catch (e: Exception) {
                        Log.v("ApiWorker", "Skipping invalid alarm during second pass: ${e.message}")
                    }
                }
            }

            return nextAlarm?.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            Log.e("ApiWorker", "Error parsing JSON array", e)
        }
        return null
    }

    private fun saveJson(json: String) {
        val fileName = "api_data.json"
        try {
            applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Log.d("ApiWorker", "JSON saved successfully to $fileName")
        } catch (e: Exception) {
            Log.e("ApiWorker", "Error saving JSON to file", e)
        }
    }
}
