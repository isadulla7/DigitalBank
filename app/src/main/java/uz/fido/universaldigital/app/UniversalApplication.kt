package uz.fido.universaldigital.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper.getLanguage
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.DEVICE_CODE
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceIds

@HiltAndroidApp
class UniversalApplication : Application() {

    companion object {
        private lateinit var instance: UniversalApplication
        fun getContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Paper.init(this)
        initTheme()
        DiffieHellman.getDiffieHellman()
        saveToPaper(DEVICE_CODE, this.getDeviceIds())
        initLocale()
    }

    private fun initLocale() {
        LocaleHelper.setLocale(applicationContext, getLanguage(applicationContext))
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(Paper.book().read(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

}