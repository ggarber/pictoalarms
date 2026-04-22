/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.ggarber.pictoalarms.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.ggarber.pictoalarms.R
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
        var userId by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }
        var randomImageRes by remember { mutableStateOf(0) }

        if (submitted) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = randomImageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Button(
                    onClick = { submitted = false },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                ) {
                    Text("Back")
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
                                Text("User Login")
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .transformedHeight(this, transformationSpec),
                            ) {
                                if (userId.isEmpty()) {
                                    Text("Enter User ID", color = Color.Gray)
                                }
                                BasicTextField(
                                    value = userId,
                                    onValueChange = { userId = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                )
                            }
                        }
                        item {
                            Button(
                                onClick = {
                                    if (userId.isNotBlank()) {
                                        val images = listOf(R.drawable.img_1, R.drawable.img_2, R.drawable.img_3)
                                        randomImageRes = images.random()
                                        submitted = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
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