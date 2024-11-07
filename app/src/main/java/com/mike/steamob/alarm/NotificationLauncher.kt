package com.mike.steamob.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import androidx.annotation.DrawableRes
import com.mike.steamob.MainActivity
import com.mike.steamob.R

object NotificationLauncher {
    fun post(
        context: Context,
        title: String,
        msg: String,
        @DrawableRes icon: Int = R.mipmap.ic_launcher,
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        val pendingIntent = PendingIntent.getActivity(
            context, 0, MainActivity.intent(context),
            PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        channel.setShowBadge(true)
        notificationManager?.createNotificationChannel(channel)
        val notification = Notification.Builder(context)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setVibrate(longArrayOf())
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setSmallIcon(icon).build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private const val NOTIFICATION_CHANNEL_ID = "default-channel"
    private const val NOTIFICATION_CHANNEL_NAME = "Default notifications"
    private const val NOTIFICATION_ID = 1010
}
