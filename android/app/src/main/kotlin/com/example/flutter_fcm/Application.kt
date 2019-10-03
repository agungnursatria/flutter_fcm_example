package com.example.flutter_fcm

import android.content.Context
import androidx.multidex.MultiDex
import com.example.flutter_fcm.helper.SharePref
import com.example.flutter_fcm.model.ActivityStatus
import io.flutter.app.FlutterApplication

class Application: FlutterApplication() {

    var mainActivityStatus = ActivityStatus.DESTROYED

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        SharePref.init(this)
    }
}