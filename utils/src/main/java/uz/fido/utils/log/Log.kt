package uz.fido.utils.log

import android.content.Context
import android.util.Log
import uz.fido.utils.BuildConfig

object Log {
    fun Context.i(message: String) {
        i("Info: ${this.javaClass.simpleName}-", message)
    }

    fun i(message: String) {
        i("Info: ", message)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
        crashlyticsLog(tag,message)
    }

    fun i(message: String, throwable: Throwable) {
        i("Info", throwable.localizedMessage)
    }

    fun Context.v(message: String) {
        v("Verbose: ${this.javaClass.simpleName}-", message)
    }

    fun v(message: String, throwable: Throwable) {
        v("Verbose", throwable.localizedMessage)
    }

    fun v(message: String) {
        v("Verbose: ", message)
    }

    fun v(tag: String, message: String) {
        Log.v(tag, message)
        crashlyticsLog(tag,message)
    }

    fun e(message: String, throwable: Throwable) {
        e("Error", throwable.localizedMessage)
    }

    fun e(message: String) {
        e("Error: ", message)
    }

    fun e(tag: String, message: String) {
        Log.e(tag, message)
        crashlyticsLog(tag, message, true)
    }

    fun Context.e(message: String) {
        e("Error: ${this.javaClass.simpleName}-", message)
    }

    fun d(message: String, throwable: Throwable) {
        d("Debug", throwable.localizedMessage)
    }

    fun d(message: String) {
        d("Debug: ", message)
    }

    fun Context.d(message: String) {
        d("Debug: ${this.javaClass.simpleName}-", message)
    }

    fun d(tag: String, message: String) {
        Log.d(tag, message)
        crashlyticsLog(tag, message, true)
    }

    private fun crashlyticsLog(
        tag: String,
        message: String,
        force:Boolean = false
    ) {
        if (debugMode || force) {
//            FirebaseCrashlytics.getInstance().log("${tag}: ${message}")
        }
    }

    private val debugMode: Boolean get() = BuildConfig.DEBUG
}