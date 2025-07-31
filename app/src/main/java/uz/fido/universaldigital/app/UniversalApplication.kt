package uz.fido.universaldigital.app

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import uz.fido.universaldigital.ui.activities.security.SecurityViolationActivity
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper.getLanguage
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.DEVICE_CODE
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.SecurePrefsManager
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.context.getDeviceIds

@HiltAndroidApp
class UniversalApplication : Application(), ThreatListener.ThreatDetected {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Paper.init(applicationContext)
        initTheme()
        DiffieHellman.getDiffieHellman()
        SecurePrefsManager.init(applicationContext)
        saveToSecureStore(DEVICE_CODE, this.getDeviceIds())
        initLocale()

        val config = TalsecConfig.Builder(PACKAGE_NAME, expectedSigningCertificateHashBase64)
            .watcherMail(MAIL)
            .supportedAlternativeStores(supportedAlternativeStores)
            .prod(IS_PROD)
            .build()

        ThreatListener(this, deviceStateListener).registerListener(this)
        Talsec.start(this, config)
    }

    private fun initLocale() {
        LocaleHelper.setLocale(applicationContext, getLanguage(applicationContext))
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(Paper.book().read(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    //Talsec-Rasp methods

    override fun onRootDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.ROOT_DETECTED)
    }

    override fun onDebuggerDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.DEBUGGER_DETECTED)
    }

    override fun onEmulatorDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.EMULATOR_DETECTED)
    }

    override fun onTamperDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.TAMPER_DETECTED)
    }

    override fun onUntrustedInstallationSourceDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.UNTRUSTED_INSTALLATION)
    }

    override fun onHookDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.HOOK_DETECTED)
    }

    override fun onObfuscationIssuesDetected() {
        openSecurityViolationActivity(SecurityViolationActivity.OTHER_ISSUE)
    }

    override fun onMalwareDetected(p0: MutableList<SuspiciousAppInfo>?) {
        openSecurityViolationActivity(SecurityViolationActivity.MALWARE_DETECTED)
    }

    override fun onScreenshotDetected() {
        println("Screenshot detected")
    }

    override fun onScreenRecordingDetected() {
        println("Screen recording detected")
    }

    override fun onDeviceBindingDetected() {
        println("Device Binding detected")
    }

    private val deviceStateListener = object : ThreatListener.DeviceState {
        override fun onUnlockedDeviceDetected() {
            println("onUnlockedDeviceDetected")
        }

        override fun onHardwareBackedKeystoreNotAvailableDetected() {
            println("onHardwareBackedKeystoreNotAvailableDetected")
        }

        override fun onDeveloperModeDetected() {
            println("onDeveloperModeDetected")
        }

        override fun onADBEnabledDetected() {
            println("onADBEnabledDetected")
        }

        override fun onSystemVPNDetected() {
            openSecurityViolationActivity(SecurityViolationActivity.VPN_DETECTED)
        }
    }

    private fun openSecurityViolationActivity(violationType: String) {
        if (!isLauncherSecurityActivity) {
            val intent = Intent(this, SecurityViolationActivity::class.java).apply {
                val bundle = bundleOf(SecurityViolationActivity.VIOLATION_TYPE to violationType)
                putExtras(bundle)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            isLauncherSecurityActivity = true
        }
    }

    companion object {
        private lateinit var instance: UniversalApplication
        fun getContext(): Context = instance.applicationContext

        private var isLauncherSecurityActivity = false
        private const val PACKAGE_NAME = "uz.fido.universaldigital"
        private const val MAIL = "universaldigitalbank@gmail.com"
        private const val IS_PROD = true
        private val expectedSigningCertificateHashBase64 = arrayOf(
            "8kX5yET/8Bja9wg/PDPgT7ursWbTan+ZDZshjV7JIno=",
            "zFIXURS+8mutcYJWMmVMRx5ETOP64gGN2w5jmOfcuKc="
        )
        private val supportedAlternativeStores = arrayOf("com.sec.android.app.samsungapps", "com.huawei.appmarket", "com.xiaomi.market")
    }

}