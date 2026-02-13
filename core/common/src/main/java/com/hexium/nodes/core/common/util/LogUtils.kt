package com.hexium.nodes.core.common.util

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun captureLogs(): String {
        return try {
            // clear logcat buffer first? No, we want history.
            // Exec logcat -d -v threadtime to get logs
            val process = Runtime.getRuntime().exec("logcat -d -v threadtime")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val sb = StringBuilder()
            var line: String? = null
            val pid = android.os.Process.myPid().toString()

            sb.append("=== Hexium Nodes Log Capture ===\n")
            sb.append("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
            sb.append("PID: $pid\n\n")

            // Read lines and append if they contain PID
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    if (it.contains(pid)) {
                        sb.append(it).append("\n")
                    }
                }
            }
            sb.toString()
        } catch (e: Exception) {
            "Error capturing logs: ${e.message}"
        }
    }

    fun saveLogsToFile(context: Context): String? {
        val logs = captureLogs()
        return try {
            val fileName = "hexium_logs_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.txt"
            val file = File(context.getExternalFilesDir(null), fileName)
            val writer = FileWriter(file)
            writer.write(logs)
            writer.close()
            file.absolutePath
        } catch (e: Exception) {
            e(tag = "LogUtils", message = "Failed to save logs", throwable = e)
            null
        }
    }
}
