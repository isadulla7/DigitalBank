package uz.fido.utils.security

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import uz.fido.utils.log.Logger
import java.util.UUID

@SuppressLint("HardwareIds")
fun Context.getDeviceIds(): String {
    Logger.writeLog("uuid:" + UUID.randomUUID().toString())
    Logger.writeLog("serial:" + Build.SERIAL)
    return Settings.Secure.getString(
        this.contentResolver, Settings.Secure.ANDROID_ID
    )
}