package uz.fido.universaldigital.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.DEVICE_CODE
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.language.LocaleHelper
import uz.fido.utils.utility.language.LocaleHelper.getLanguage

@HiltAndroidApp
class UniversalApplication : Application() {

    companion object {

        private lateinit var instance: UniversalApplication

        fun getContext(): Context {
            return instance.applicationContext
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Paper.init(this)
        initTheme()
        DiffieHellman.getDiffieHellman()
        Paper.book().write(DEVICE_CODE, this.getDeviceIds())
        initLocale()

    }

    private fun initLocale() {
        LocaleHelper.setLocale(applicationContext, getLanguage(applicationContext))
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(
            Paper.book().read(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        )
    }

}