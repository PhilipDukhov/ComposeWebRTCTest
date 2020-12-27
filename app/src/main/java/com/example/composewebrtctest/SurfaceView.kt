package com.example.composewebrtctest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

data class SurfaceViewContext(
    val eglBase: EglBase,
    val videoTrack: VideoTrack,
)

@Composable
fun SurfaceView(
    surfaceViewContext: SurfaceViewContext,
    modifier: Modifier,
) {
    val context = AmbientContext.current
    val customView = remember {
        println("SurfaceView initialize")
        SurfaceViewRenderer(context).apply {
            init(surfaceViewContext.eglBase.eglBaseContext, null)
            setEnableHardwareScaler(true)
            setMirror(true)
        }
    }

    AndroidView(
        { customView },
        modifier = modifier
    ) {
        println("SurfaceView addSink $it")
        surfaceViewContext.videoTrack.setEnabled(true)
        surfaceViewContext.videoTrack.addSink(it)
    }
    onDispose {
        println("SurfaceView removeSink $customView")
//        surfaceViewContext.videoTrack.setEnabled(false)
        surfaceViewContext.videoTrack.removeSink(customView)
    }
}