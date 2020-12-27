package com.example.composewebrtctest

import android.content.Context
import org.webrtc.*

class WebRTCManager(private val applicationContext: Context) {
    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory
                .InitializationOptions
                .builder(applicationContext)
                .createInitializationOptions()
        )
    }
    private val rootEglBase = EglBase.create()!!
    private val factory = PeerConnectionFactory
        .builder()
        .setOptions(PeerConnectionFactory.Options())
        .setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
            rootEglBase.eglBaseContext,
            true,
            true
        )
        )
        .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
        .createPeerConnectionFactory()!!
    private val localVideoTrack = factory.createVideoTrack(
        "ARDAMSv0",
        createVideoCapturer()!!.run {
            val videoSource = factory.createVideoSource(isScreencast)
            initialize(
                SurfaceTextureHelper.create("WebRTC", rootEglBase.eglBaseContext),
                applicationContext,
                videoSource.capturerObserver
            )
            startCapture(1280, 720, 30)
            videoSource
        }
    ).apply {
        setEnabled(true)
    }

    private fun createVideoCapturer(): VideoCapturer? =
        if (Camera2Enumerator.isSupported(applicationContext)) {
            Camera2Enumerator(applicationContext)
        } else {
            Camera1Enumerator(true)
        }.let { enumerator ->
            enumerator.deviceNames
                .sortedBy { !enumerator.isFrontFacing(it) }  // first try to find front camera
                .firstMapOrNull {
                    enumerator.createCapturer(it, null)
                }
        }

    fun getLocalViewContext(): SurfaceViewContext =
        SurfaceViewContext(
            rootEglBase,
            localVideoTrack
        )

    inline fun <I, O> Iterable<I>.firstMapOrNull(predicate: (I) -> O?): O? {
        for (element in this) {
            return predicate(element) ?: continue
        }
        return null
    }
}