package uz.fido.universaldigital.ui.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLoginBinding
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.utils.const.Const.USER_LOGGED

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStartDestination()
    }

    private fun setStartDestination() {
        val navController = Navigation.findNavController(this, R.id.nav_host_login)
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_login)

        val bundle = Bundle()
        bundle.putString(PinCodeFragment.PIN_OPERATION, PinCodeFragment.PIN_OPERATION_SIGN_IN)
        navGraph.setStartDestination(startDestination())
        navController.setGraph(navGraph, bundle)
    }

    private fun startDestination(): Int {
        return if (Paper.book().read(USER_LOGGED, false) == false) {
            R.id.chooseLanguageFragment
        } else {
            R.id.pinCodeFragment
        }
    }

}