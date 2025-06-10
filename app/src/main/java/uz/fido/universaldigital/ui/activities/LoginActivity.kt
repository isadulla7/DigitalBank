package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation.findNavController
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLoginBinding
import uz.fido.universaldigital.ui.activities.app_icon_changer.AppIcons
import uz.fido.universaldigital.ui.activities.app_icon_changer.AppIconsViewModel
import uz.fido.universaldigital.ui.activities.app_icon_changer.changeAppIcon
import uz.fido.universaldigital.ui.activities.security.LockSetActivity
import uz.fido.universaldigital.ui.activities.security.RootedDeviceActivity
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.utils.extensions.getLoginStartDestination
import uz.fido.utils.const.Const
import uz.fido.utils.security.SecurityCheck
import uz.fido.utils.security.SecurityCheck.isPhoneRooted
import uz.fido.utils.security.SecurityCheck.isRunningOnEmulator
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AppIconsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForDeviceLock()
        listenAppIconChanges()
    }

    private fun listenAppIconChanges() {
        viewModel.appIconLiveData.observe(this) {
            val currentIcon = this.getFromSecureStore(Const.CURRENT_APP_ICON, AppIcons.APP_ICON_DEFAULT)
            if (currentIcon != it) {
                saveToSecureStore(Const.CURRENT_APP_ICON, it)
                changeAppIcon()
            }
        }
    }

    private fun checkForDeviceLock() {
        this.isRunningOnEmulator()
        if (SecurityCheck.isFromEmulator()) {
            openLockActivity()
            return
        } else if (this.isPhoneRooted()) {
            openRootedDeviceWarning()
            return
        } else {
            checkForDeepLink()
        }
    }

    private fun checkForDeepLink() {
        if (intent.data != null) {
            FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(this) { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let {
                    val objectValue = it.getQueryParameter("cardNumber")
                    val objectId = it.getQueryParameter("objectId")
                    val amount = it.getQueryParameter("amount")
                    val comment = it.getQueryParameter("comment")
                    setStartDestination(
                        bundleOf(
                            PassCodeFragment.DEEP_LINK_OBJECT_VALUE to objectValue,
                            PassCodeFragment.DEEP_LINK_OBJECT_ID to objectId,
                            PassCodeFragment.DEEP_LINK_AMOUNT to amount,
                            PassCodeFragment.DEEP_LINK_COMMENT to comment
                        )
                    )
                }
            }.addOnFailureListener(this) {
                setStartDestination()
            }
        } else {
            checkNotification()
        }
    }

    private fun checkNotification() {
        val notification = intent.getStringExtra(PassCodeFragment.NOTIFICATION_OPERATION)
        val bundle = bundleOf(PassCodeFragment.NOTIFICATION_OPERATION to notification)
        setStartDestination(bundle)
    }

    private fun setStartDestination(bundle: Bundle? = null) {
        val navController = findNavController(this, R.id.nav_host_login)
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_login)
        navGraph.setStartDestination(getLoginStartDestination())
        navController.setGraph(navGraph, bundle)
    }

    private fun openLockActivity() {
        val intent = Intent(this, LockSetActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openRootedDeviceWarning() {
        val intent = Intent(this, RootedDeviceActivity::class.java)
        startActivity(intent)
        finish()
    }

}