package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLoginBinding
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
        setWidgetUpdateInterval()
    }

    private fun checkForDeviceLock() {
        if (SecurityCheck.isFromEmulator()) {
            openLockActivity()
            return
        } else {
            setStartDestination()
        }
    }


    private fun setStartDestination() {
        val navController = Navigation.findNavController(this, R.id.nav_host_login)
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_login)
        navGraph.setStartDestination(getStartDestination())
        navController.setGraph(navGraph, null)
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


    override fun onResume() {
        super.onResume()
        updateRatesWidget()
    }

    private fun updateRatesWidget() {
        RatesWidgetProvider().doUpdate(this, RatesRepository(), WidgetView())
    }

    private fun setWidgetUpdateInterval() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workRequest =
            PeriodicWorkRequestBuilder<Worker>(3, TimeUnit.HOURS).setConstraints(constraints)
                .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

}