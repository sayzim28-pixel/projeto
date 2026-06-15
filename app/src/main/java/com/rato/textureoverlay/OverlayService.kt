package com.rato.textureoverlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isRunning = false
    private val channelId = "overlay_channel"

    companion object {
        const val ACTION_START = "com.rato.textureoverlay.ACTION_START"
        const val ACTION_STOP = "com.rato.textureoverlay.ACTION_STOP"

        // Caminhos de destino
        private const val DEST_BASE_PATH = "Android/data/com.dts.freefireth/files/contentcache/Optional/android"
        private const val FILEINFO_DEST_NAME = "fileinfo"
        private const val SHADERS_DEST_PATH = "Android/data/com.dts.freefireth/files/contentcache/Optional/android/gameassetbundles"
        private const val SHADERS_DEST_FILE = "shaders.F3kBzwdDqkGcpDWPbhf2lNZWvXA~3D"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!isRunning) {
                    startForegroundService()
                    showFloatingButton()
                    isRunning = true
                }
            }
            ACTION_STOP -> {
                hideFloatingButton()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                isRunning = false
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        hideFloatingButton()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificacao do servico de overlay"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Texture Overlay")
            .setContentText("Botao flutuante ativo")
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun showFloatingButton() {
        if (floatingView != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        floatingView = LayoutInflater.from(this)
            .inflate(R.layout.floating_button, null)

        setupTouchListener(layoutParams)

        try {
            windowManager?.addView(floatingView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTouchListener(layoutParams: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isMoved = false

        floatingView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isMoved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()

                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isMoved = true
                    }

                    layoutParams.x = initialX + dx
                    layoutParams.y = initialY + dy
                    windowManager?.updateViewLayout(floatingView, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isMoved) {
                        performFileOperation()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun hideFloatingButton() {
        try {
            floatingView?.let {
                windowManager?.removeView(it)
            }
            floatingView = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun performFileOperation() {
        Thread {
            try {
                val storageDir = Environment.getExternalStorageDirectory()

                // Copia arquivo fileinfo
                val fileinfoDestDir = File(storageDir, DEST_BASE_PATH).apply { mkdirs() }
                copyAssetToExternalStorage(
                    assetName = "fileinfo",
                    destFile = File(fileinfoDestDir, FILEINFO_DEST_NAME)
                )

                // Copia shader
                val shadersDestDir = File(storageDir, SHADERS_DEST_PATH).apply { mkdirs() }
                copyAssetToExternalStorage(
                    assetName = SHADERS_DEST_FILE,
                    destFile = File(shadersDestDir, SHADERS_DEST_FILE)
                )

                showToast("Arquivos copiados com sucesso!")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Erro: ${e.message}")
            }
        }.start()
    }

    private fun copyAssetToExternalStorage(assetName: String, destFile: File) {
        try {
            assets.open(assetName).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            throw IOException("Erro ao copiar $assetName: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
