package uz.fido.universaldigital.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityMainBinding
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_confirm.ConfirmPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileFragment
import uz.fido.universaldigital.ui.fragments.profile.about_bank.branches.MainBranchesFragment
import uz.fido.universaldigital.ui.fragments.profile.identification.MainIdentificationFragment
import uz.fido.universaldigital.ui.fragments.profile.identification.VerificationInfoUserFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.transfers.by_phone.TransferByPhoneFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.utils.const.Const
import uz.fido.utils.internet_checker.InternetConnectionChecker
import uz.fido.utils.internet_checker.NoConnectionDialog
import uz.fido.utils.update_checker.UpdateChecker
import uz.fido.utils.utility.activity.tintSystemBars
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.utility.fragment.getNavOptions
import uz.fido.utils.view.bottom_menu_anim.hideAnimWithSlideDown
import uz.fido.utils.view.bottom_menu_anim.showAnimWithSlideUp
import java.util.Calendar


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var updateChecker: UpdateChecker

    private var noConnectionDialog: NoConnectionDialog? = null
    private var isStop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigationMenu()
        checkUpdate()
        askNotificationPermission()
        initSearchList()
    }

    override fun onResume() {
        super.onResume()
        internetListener()
        isStop = false
        if (!showPinCode) {
            showPinCode = true
            return
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        if (currentFragment is PassCodeFragment ||
            currentFragment is PinCodeFragment ||
            currentFragment is ConfirmPaymentFragment ||
            currentFragment is SuccessPaymentFragment ||
            currentFragment is MainBranchesFragment ||
            currentFragment is TransferFragment ||
            currentFragment is TransferByPhoneFragment ||
            currentFragment is MenuProfileFragment ||
            currentFragment is MainIdentificationFragment ||
            currentFragment is AddCardFragment ||
            currentFragment is SuccessTransferFragment ||
            currentFragment is BasicSuccessFragment ||
            currentFragment is PaymentFragment ||
            currentFragment is VerificationInfoUserFragment
        ) {
            pausedMillis = 0L
        } else {
            if (pausedMillis != 0L) {
                pausedMillis = Calendar.getInstance().timeInMillis - pausedMillis
            }
            if (pausedMillis != 0L && pausedMillis > 5000) {
                if (pausedMillis > 180000) {
                    pausedMillis = 0
                    noConnectionDialog?.dismiss()
                    startActivityWithClearTask(LoginActivity::class.java)
                } else {
                    openPage(
                        R.id.passCodeFragment2,
                        bundleOf(Const.OPERATION to PassCodeFragment.PASS_OPERATION_POP)
                    )
                    pausedMillis = 0
                }
            }
        }
    }

    private fun openPage(id: Int, bundle: Bundle? = null, isSlide: Boolean? = null) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(id, bundle, if (isSlide == true) getNavOptions() else null)
    }

    override fun onStop() {
        super.onStop()
        pausedMillis = Calendar.getInstance().timeInMillis
        isStop = true
    }

    override fun onDestroy() {
        super.onDestroy()
        pausedMillis = 0
    }

    private fun checkUpdate() {
        updateChecker = UpdateChecker(this)
        updateChecker.checkUpdate()
    }

    private fun internetListener() {
        InternetConnectionChecker(this).observeForever { isConnected ->
            if (isConnected) {
                if (!isDestroyed && !isFinishing) {
                    if (noConnectionDialog != null) {
                        noConnectionDialog?.dismiss()
                        noConnectionDialog = null
                    }
                }
            } else if (!this@MainActivity.isStop && !isDestroyed && !isFinishing) {
                noConnectionDialog = NoConnectionDialog()
                noConnectionDialog?.show(supportFragmentManager, "")
            }
        }
    }

    private fun initBottomNavigationMenu() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.productsFragment ||
                destination.id == R.id.menuTransfersFragment ||
                destination.id == R.id.basePaymentFragment ||
                destination.id == R.id.menuServicesFragment ||
                destination.id == R.id.menuMonitoringFragment
            ) {
                binding.bottomNavigation.showAnimWithSlideUp()
                binding.divider.showAnimWithSlideUp()
                if (destination.id == R.id.productsFragment) {
                    tintSystemBars(R.color.brandRedColor, R.color.backgroundColor)
                } else {
                    tintSystemBars(R.color.backgroundColor, R.color.backgroundColor)
                }
            } else {
                tintSystemBars(R.color.whiteColor)
                binding.bottomNavigation.hideAnimWithSlideDown()
                binding.divider.hideAnimWithSlideDown()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initSearchList() {
        lifecycleScope.launch(Dispatchers.Default) {
            if (SearchList.getSearchList(this@MainActivity).isNotEmpty()) {
                SearchList.searchList.clear()
                SearchList.searchList = SearchList.getSearchList(this@MainActivity)
            } else {
                SearchList.fillSearchList(this@MainActivity)
                SearchList.saveSearchList(this@MainActivity)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UpdateChecker.UPDATE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateChecker.appUpdateManager.registerListener(updateChecker.updateListener)
            }
        }
    }

    companion object {
        var pausedMillis: Long = 0L
        var showPinCode = true
    }

}