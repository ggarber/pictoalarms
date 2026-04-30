/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.ggarber.pictoalarms.presentation

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ggarber.pictoalarms.R
import com.ggarber.pictoalarms.data.ApiWorker
import com.ggarber.pictoalarms.presentation.theme.PictoAlarmsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    PictoAlarmsTheme {
        var deviceId by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }
        var nextAlarmTime by remember { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val context = LocalContext.current
        val vibrator = remember { context.getSystemService(Vibrator::class.java) }
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        val workManager = remember { WorkManager.getInstance(context) }
        var workId by remember { mutableStateOf<java.util.UUID?>(null) }
        val workInfo by (workId?.let { workManager.getWorkInfoByIdLiveData(it).asFlow() } ?: kotlinx.coroutines.flow.flowOf(null)).collectAsState(null)

        LaunchedEffect(workInfo) {
            when (workInfo?.state) {
                androidx.work.WorkInfo.State.SUCCEEDED -> {
                    nextAlarmTime = workInfo?.outputData?.getString("nextAlarm")
                    errorMessage = null
                }
                androidx.work.WorkInfo.State.FAILED -> {
                    errorMessage = "Invalid Device ID. Please try again."
                    submitted = false
                }
                else -> {}
            }
        }

        LaunchedEffect(submitted) {
            if (submitted) {
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                focusRequester.requestFocus()
            }
        }

        fun handleSubmit() {
            val deviceIdToSubmit = deviceId.trim()
            if (deviceIdToSubmit.isNotBlank()) {
                Log.d("PictoAlarms", "handleSubmit called, id: '$deviceIdToSubmit', length: ${deviceIdToSubmit.length}")
                focusManager.clearFocus()
                errorMessage = null
                
                // Save deviceId to SharedPreferences
                context.getSharedPreferences("picto_alarms", Context.MODE_PRIVATE)
                    .edit()
                    .putString("deviceId", deviceIdToSubmit)
                    .apply()

                val workRequest = OneTimeWorkRequestBuilder<ApiWorker>()
                    .setInputData(workDataOf("deviceId" to deviceIdToSubmit))
                    .build()
                workManager.enqueue(workRequest)
                workId = workRequest.id

                submitted = true
            }
        }

        if (submitted) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (workInfo?.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                    Text(
                        text = if (nextAlarmTime != null) 
                            "Configuration Ready,\nNext Alarm at $nextAlarmTime"
                        else 
                            "Configuration Ready,\nNo alarms scheduled",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Configuring...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Button(
                    onClick = {
                        submitted = false
                        deviceId = ""
                        nextAlarmTime = null
                    },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                ) {
                    Text("Hide")
                }
            }
        } else {
            AppScaffold {
                val listState = rememberTransformingLazyColumnState()
                val transformationSpec = rememberTransformationSpec()
                ScreenScaffold(
                    scrollState = listState,
                ) { contentPadding ->
                    TransformingLazyColumn(
                        contentPadding = contentPadding,
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ListHeader(
                                modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) {
                                Text("Device ID Login")
                            }
                        }
                        if (errorMessage != null) {
                            item {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp).transformedHeight(this, transformationSpec),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .transformedHeight(this, transformationSpec)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (deviceId.isEmpty()) {
                                    Text(
                                        text = "Enter Device ID",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                                BasicTextField(
                                    value = deviceId,
                                    onValueChange = { 
                                        deviceId = it
                                        Log.d("PictoAlarms", "deviceId changed: '$it'")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester)
                                        .onKeyEvent {
                                            if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                                                if (deviceId.isNotBlank()) {
                                                    handleSubmit()
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        },
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { 
                                            if (deviceId.isNotBlank()) {
                                                handleSubmit()
                                            }
                                        }
                                    ),
                                    cursorBrush = SolidColor(Color.White)
                                )
                            }
                        }
                        item {
                            Button(
                                onClick = { handleSubmit() },
                                enabled = deviceId.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}