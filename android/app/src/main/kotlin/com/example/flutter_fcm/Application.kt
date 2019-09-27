package com.example.flutter_fcm

import android.content.Context
import androidx.multidex.MultiDex
import com.example.flutter_fcm.helper.SharePref
import io.flutter.app.FlutterApplication


/**
 * Created by Christian Nababan on 11/09/19
 */
class Application: FlutterApplication() {

    var isMainActivityVisible = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        SharePref.init(this)
    }
}