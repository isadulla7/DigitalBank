package uz.fido.universaldigital.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import uz.fido.utils.const.Const.DEVICE_CODE
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceId

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
        DiffieHellman.getDiffieHellman()
        Paper.book().write(DEVICE_CODE, this.getDeviceId())
    }

}