package com.example.flutter_fcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.flutter_fcm.helper.Constant
import com.example.flutter_fcm.helper.RxBus
import com.example.flutter_fcm.helper.RxEvent
import com.example.flutter_fcm.model.ActivityStatus
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : FlutterActivity() {

    companion object {
        const val TAG = "MainActivity"

        fun getIntentWithCode(context: Context, code: String): Intent = getIntent(context).apply {
            putExtra(Constant.EXTRA_CODE, code)
        }

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private val disposable = CompositeDisposable()
    private var code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        disposable.add(
                RxBus.listen(RxEvent.EventInitCode::class.java)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Log.d(TAG, "Send Code in Rx: ${it.code}")
                            MethodChannel(flutterView, "com.example.flutter_fcm/FCMCodeMethodChannel").invokeMethod("sendCode", it.code)
                        }
        )

        code = intent?.extras?.getString(Constant.EXTRA_CODE, null)
        MethodChannel(flutterView, "com.example.flutter_fcm/FCMCodeMethodChannel").setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                "get_code" -> {
                    code?.let {
                        result.success(it)
                        code = null
                    } ?: result.error(TAG, "Code is not found", null)
                }
                else -> result.error(TAG, "Method ${methodCall.method} is not implemented", null)
            }
        }

        Log.d(TAG, "Status: onCreate")
    }

    override fun onStart() {
        Log.d(TAG, "Status: onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "Status: onResume")
        if (application is Application) {
            (application as Application).mainActivityStatus = ActivityStatus.ACTIVE
        }
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "Status: onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "Status: onStop")
        if (application is Application) {
            (application as Application).mainActivityStatus = ActivityStatus.STOP
        }
        super.onStop()
    }

    override fun onRestart() {
        Log.d(TAG, "Status: onRestart")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG, "Status: onDestroy")
        if (application is Application) {
            (application as Application).mainActivityStatus = ActivityStatus.DESTROYED
        }
        if (!disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }
}
