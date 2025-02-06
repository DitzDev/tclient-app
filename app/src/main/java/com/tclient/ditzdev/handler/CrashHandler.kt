package com.tclient.ditzdev.handler

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import com.tclient.ditzdev.activity.CrashActivity
import kotlin.jvm.Volatile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    
    companion object {
        @Volatile
        private var instance: CrashHandler? = null
        
        fun initialize(application: Application) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = CrashHandler(application)
                        Thread.setDefaultUncaughtExceptionHandler(instance)
                    }
                }
            }
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val crashLog = generateCrashLog(throwable)
            val crashFile = saveCrashToFile(crashLog)
            context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("last_crash_file", crashFile.absolutePath)
                .apply()
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    
                    val intent = Intent(context, CrashActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or 
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra(CrashActivity.EXTRA_CRASH_INFO, crashLog)
                    }
                    
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                    Looper.loop()
                }
            }.start()
            Thread.sleep(1000)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
            
        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun generateCrashLog(throwable: Throwable): String {
        return buildString {
            append("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")
            append("========== DEVICE INFORMATION =========\n")
            append("Brand: ${Build.BRAND}\n")
            append("Device: ${Build.DEVICE}\n")
            append("Model: ${Build.MODEL}\n")
            append("Android Version: ${Build.VERSION.RELEASE}\n")
            append("SDK: ${Build.VERSION.SDK_INT}\n")
            append("========== END OF DEVICE INFORMATION =========\n\n")
            append("========== START STACK TRACE =========\n\n")
            append(Log.getStackTraceString(throwable))
            append("========== END OF STACK TRACE =========\n\n")
        }
    }

    private fun saveCrashToFile(crashLog: String): File {
        val fileName = "crash_${System.currentTimeMillis()}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { 
            it.write(crashLog.toByteArray()) 
        }
        return file
    }
}