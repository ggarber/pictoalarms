package com.ggarber.pictoalarms.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        // Replace with your actual API URL
        val url = "https://api.example.com/data" 

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body.string()
                    saveJson(json)
                    Result.success()
                } else {
                    Log.e("ApiWorker", "API request failed with code: ${response.code}")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e("ApiWorker", "Error fetching data from API", e)
            Result.retry()
        }
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
