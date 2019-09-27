package com.example.flutter_fcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.flutter_fcm.helper.Constant
import com.example.flutter_fcm.helper.RxBus
import com.example.flutter_fcm.helper.RxEvent
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : FlutterActivity() {

    companion object {
        const val TAG = "MainActivity"
        fun getIntent(context: Context, code: String?): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            code?.let {
                Log.d(TAG, "CODE onIntent: $it")
                putExtra(Constant.EXTRA_CODE, it)
            }
        }
    }

    val disposable = CompositeDisposable()
    var code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        disposable.add(
                RxBus.listen(RxEvent.EventInitCode::class.java)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
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
            (application as Application).isMainActivityVisible = true
        }

        code = intent?.extras?.getString(Constant.EXTRA_CODE, null)
        code?.let {
            Log.d(TAG, "Code: $it")
            MethodChannel(flutterView, "com.example.flutter_fcm/FCMCodeMethodChannel").invokeMethod("sendCode", it)
            code = null
        }
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "Status: onPause")
        if (application is Application) {
            (application as Application).isMainActivityVisible = false
        }
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "Status: onStop")
        super.onStop()
    }

    override fun onRestart() {
        Log.d(TAG, "Status: onRestart")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG, "Status: onDestroy")
        super.onDestroy()
        if (!disposable.isDisposed) disposable.dispose()
    }
}
