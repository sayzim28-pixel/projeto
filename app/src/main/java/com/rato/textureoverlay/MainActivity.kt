package com.rato.textureoverlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        startButton.setOnClickListener {
            if (checkOverlayPermission()) {
                startOverlayService()
            } else {
                requestOverlayPermission()
            }
        }

        stopButton.setOnClickListener {
            stopOverlayService()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (checkOverlayPermission()) {
                startOverlayService()
                Toast.makeText(this, "Permissao concedida!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissao negada. O overlay nao funcionara.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_START
        }
        startService(intent)
        statusText.text = "Overlay ativo - Botao flutuante visivel"
        Toast.makeText(this, "Overlay iniciado!", Toast.LENGTH_SHORT).show()
    }

    private fun stopOverlayService() {
        val intent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_STOP
        }
        startService(intent)
        statusText.text = "Overlay inativo"
        Toast.makeText(this, "Overlay parado!", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus() {
        statusText.text = if (checkOverlayPermission()) {
            "Permissao OK - Pronto para iniciar"
        } else {
            "Permissao de overlay necessaria"
        }
    }
}
