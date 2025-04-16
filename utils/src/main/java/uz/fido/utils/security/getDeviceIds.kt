package uz.fido.utils.security

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun Context.getDeviceIds(): String {
    return Settings.Secure.getString(
        this.contentResolver, Settings.Secure.ANDROID_ID
    )
}