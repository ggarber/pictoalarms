package com.ggarber.pictoalarms.presentation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.ggarber.pictoalarms.data.AlarmScheduler
import com.ggarber.pictoalarms.presentation.theme.PictoAlarmsTheme

class PictogramActivity : ComponentActivity() {
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val dismissRunnable = Runnable { dismiss() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val pictogram = intent.getStringExtra("pictogram") ?: "Unknown"
        vibrator = getSystemService(Vibrator::class.java)

        setContent {
            PictoAlarmsTheme {
                PictogramScreen(pictogram) {
                    dismiss()
                }
            }
        }

        // Auto-dismiss after 10 minutes
        handler.postDelayed(dismissRunnable, 10 * 60 * 1000)
    }

    private fun dismiss() {
        Log.d("PictogramActivity", "Dismissing pictogram")
        vibrator?.cancel()
        handler.removeCallbacks(dismissRunnable)
        AlarmScheduler.scheduleNextAlarm(this)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator?.cancel()
        handler.removeCallbacks(dismissRunnable)
    }
}

@Composable
fun PictogramScreen(pictogram: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }

    LaunchedEffect(Unit) {
        val timings = longArrayOf(0, 500, 500)
        val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0)
        val effect = VibrationEffect.createWaveform(timings, amplitudes, 1)
        vibrator?.vibrate(effect)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pictogram,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Tap to dismiss",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
        )
    }
}
