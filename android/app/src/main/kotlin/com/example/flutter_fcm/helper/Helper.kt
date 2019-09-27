package com.example.flutter_fcm.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.flutter_fcm.MainActivity
import com.example.flutter_fcm.R
import java.util.*
import com.example.flutter_fcm.model.Notification as NotificationModel

object Helper {

    fun sendNotification(context: Context, notif: NotificationModel) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val noti: NotificationCompat.Builder

        val chanelId = "Fazz_id"
        val name = "Fazzcard Notification"
        val description = "Fazzcard notification CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(chanelId, name, importance).apply {
                this.description = description
                enableLights(true)
                lightColor = Color.BLUE
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(mChannel)
        }

        noti = NotificationCompat.Builder(context, chanelId)

        val requestID = Random().nextInt(1000)

        noti.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notif.title ?: "")
                .setContentText(notif.body ?: "")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(notif.body))

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent =
                PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        noti.setContentIntent(pendingIntent)

        notificationManager.notify(requestID, noti.build())
    }
}