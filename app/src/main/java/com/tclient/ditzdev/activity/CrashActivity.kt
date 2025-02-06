package com.tclient.ditzdev.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import com.tclient.ditzdev.R

class CrashActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CRASH_INFO = "extra_crash_info"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        val crashInfo = intent.getStringExtra(EXTRA_CRASH_INFO) ?: "Unknown error occurred"
        
        findViewById<TextView>(R.id.tvErrorMessage).text = crashInfo
        
        findViewById<Button>(R.id.btnRestartApp).setOnClickListener {
            restartApp()
        }
        
        findViewById<Button>(R.id.btnShareLog).setOnClickListener {
            shareCrashLog()
        }
    }

    private fun restartApp() {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        finish()
    }

    private fun shareCrashLog() {
        try {
            val lastCrashFile = getSharedPreferences("crash_prefs", MODE_PRIVATE)
                .getString("last_crash_file", null)
            
            lastCrashFile?.let { filePath ->
                val crashFile = File(filePath)
                if (crashFile.exists()) {
                    val fileUri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        crashFile
                    )

                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(this, "Share Crash Log"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}