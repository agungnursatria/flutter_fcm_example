package com.example.flutter_fcm.fcm

import android.content.Context
import android.util.Log
import com.example.flutter_fcm.Application
import com.example.flutter_fcm.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.example.flutter_fcm.helper.Helper
import com.example.flutter_fcm.helper.RxBus
import com.example.flutter_fcm.helper.RxEvent
import com.example.flutter_fcm.helper.SharePref
import com.example.flutter_fcm.model.Notification as NotificationModel


class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        token?.let {
            try {
                SharePref.instance().saveString("token", it)
            } catch (e: RuntimeException) {
                Log.w(TAG, e.message)
            }
        } ?: Log.w(TAG, "FCM token is not found")

        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let {
            remoteMessage.notification?.let { notif ->
                setNotifConsole(this, notif)
            } ?: setNotifData(this, it)
        }
    }

    private fun setNotifConsole(context: Context, remoteNotif: RemoteMessage.Notification) {
        val notif = NotificationModel(remoteNotif.title, remoteNotif.body)
        Helper.sendNotification(context, notif)
    }


    private fun setNotifData(context: Context, remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        data?.let { d ->

            Log.d(TAG, d.toString())

            // Show notification from data if needed
            val valNotif = d["notification"]
            if (valNotif != null) {
                val notif = Gson().fromJson(valNotif, NotificationModel::class.java)

                notif.title?.let { title ->
                    notif.body?.let { body ->
                        if (title.isNotEmpty() && body.isNotEmpty()) {
                            Helper.sendNotification(context, notif)
                        }
                    }
                }
            }

            d["code"]?.let {
                if (application is Application){
                    if ((application as Application).isMainActivityVisible){
                        RxBus.publish(RxEvent.EventInitCode(it))
                    } else {
                        Log.d(TAG, "Start activity: ${d["code"]}")
                        startActivity(MainActivity.getIntent(context, it))
                    }
                }
            }
        }
    }
}
