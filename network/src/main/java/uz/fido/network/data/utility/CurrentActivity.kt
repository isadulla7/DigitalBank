package uz.fido.network.data.utility

import android.annotation.SuppressLint
import android.app.Activity

object CurrentActivityHolder {
    @SuppressLint("StaticFieldLeak")
    var currentActivity: Activity? = null
}