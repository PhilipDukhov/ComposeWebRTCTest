package com.example.composewebrtctest

import android.Manifest.permission.CAMERA
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import com.example.composewebrtctest.ui.ComposeWebRTCTestTheme
import kotlinx.coroutines.*
import org.webrtc.*

var counter = 0

class MainActivity : AppCompatActivity() {

    private var webRTCManager: WebRTCManager? = null
    private val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            webRTCManager = WebRTCManager(this)
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    MainScope().launch {
                        updateContent()
                    }
                    delay(2000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateContent()
        permissionLauncher.launch(CAMERA)
    }

    private fun updateContent() {
        setContent {
            ComposeWebRTCTestTheme {
                Surface(color = MaterialTheme.colors.background) {
                    counter += 1
                    TestView(
                            webRTCManager?.getLocalViewContext()
                    )
                }
            }
        }
    }
}

@Composable
fun TestView(context: SurfaceViewContext?) =
        Column(
                modifier = Modifier
                        .fillMaxWidth()
        ) {
            context?.let {
                SurfaceView(
                        it,
                        modifier = Modifier
                                .weight(1F)
                )
            }
            Box(
                    modifier = Modifier
                            .size(40.dp)
                            .background(Color.Red)
            )
            Text("hello",
                    color = Color.White,
                    modifier = Modifier
            )
            Box(
                    modifier = Modifier
                            .background(Color.Red)
                            .aspectRatio(1080F/1920)
                            .align(Alignment.End)
                            .padding(10.dp)
                            .weight(1F)
            ) {
                context?.let {
                    if (counter % 2 == 0) {
                        SurfaceView(
                                it,
                                modifier = Modifier
                                        .fillMaxSize()
                        )
                    }
                }
            }
            Text(
                    "hello again",
                    color = Color.White,
                    modifier = Modifier
                            .align(Alignment.End)
            )
        }