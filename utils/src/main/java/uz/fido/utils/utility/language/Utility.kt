package uz.fido.utils.utility.language

import android.content.res.Resources
import android.os.Build
import java.util.Locale

object Utility {

    fun getDeviceLocale(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
    }
}