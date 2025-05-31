package com.mikeapp.steamob.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import androidx.annotation.DrawableRes
import com.mikeapp.steamob.R
import com.mikeapp.steamob.ui.home.MainActivity

object NotificationLauncher {
    fun post(
        context: Context,
        title: String,
        msg: String,
        @DrawableRes icon: Int = R.drawable.ic_notification_g,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        val pendingIntent = PendingIntent.getActivity(
            context, 0, MainActivity.intent(context),
            PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
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
    private const val NOTIFICATION_ID = 1010
}
