package com.mikeapp.steamob.permission

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object AndroidPermissionHelper {
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    fun hasNotificationPermission(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Automatically granted on lower versions
        }
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Checks if the app can schedule exact alarms.
     * Returns true if allowed or API < 33.
     */
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Shows a dialog to the user explaining why exact alarm permission is needed,
     * then directs user to the system settings screen to grant it.
     */
    fun promptExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlertDialog.Builder(activity)
                .setTitle("Allow Exact Alarm Permission")
                .setMessage("To ensure alarms work precisely even when the app is in the background, please allow exact alarms in settings.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        // This opens your app's specific permission screen directly
                        intent.data = "package:${activity.packageName}".toUri()
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        // Robust fallback: open full app settings directly
                        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = "package:${activity.packageName}".toUri()
                        }
                        activity.startActivity(fallbackIntent)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}