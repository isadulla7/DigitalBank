package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.CurrentActivityHolder
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLoginBinding
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.widgets.currency_rates.retrofit.RatesRepository
import uz.fido.universaldigital.widgets.currency_rates.view.WidgetView
import uz.fido.universaldigital.widgets.currency_rates.widget.RatesWidgetProvider
import uz.fido.universaldigital.widgets.currency_rates.worker.Worker
import uz.fido.utils.const.Const.USER_LOGGED
import uz.fido.utils.security.SecurityCheck
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForDeviceLock()
    }

    override fun onResume() {
        super.onResume()
        CurrentActivityHolder.currentActivity = this
    }

    private fun checkForDeviceLock() {
        if (SecurityCheck.isFromEmulator()) {
            openLockActivity()
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
            }.addOnFailureListener(this) { setStartDestination() }
        } else {
            setStartDestination()
        }
    }

    private fun setStartDestination(bundle: Bundle? = null) {
        val navController = Navigation.findNavController(this, R.id.nav_host_login)
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_login)
        navGraph.setStartDestination(getStartDestination())
        navController.setGraph(navGraph, bundle)
    }

    private fun isUserLogged() = Paper.book().read(USER_LOGGED, false) == false

    private fun getStartDestination(): Int {
        return if (isUserLogged()) R.id.chooseLanguageFragment else R.id.passCodeFragment
    }

    private fun openLockActivity() {
        val intent = Intent(this, LockSetActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (CurrentActivityHolder.currentActivity == this) {
            CurrentActivityHolder.currentActivity = null
        }
    }

}