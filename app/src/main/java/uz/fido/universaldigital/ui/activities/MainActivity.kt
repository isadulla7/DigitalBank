package uz.fido.universaldigital.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityMainBinding
import uz.fido.universaldigital.services.AudioModeService
import uz.fido.universaldigital.services.ShakeDetectionService
import uz.fido.universaldigital.ui.activities.seasons.SeasonViewModel
import uz.fido.universaldigital.ui.activities.security.CallSafeActivity
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_confirm.ConfirmPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileFragment
import uz.fido.universaldigital.ui.fragments.profile.about_bank.branches.MainBranchesFragment
import uz.fido.universaldigital.ui.fragments.profile.identification.MainIdentificationFragment
import uz.fido.universaldigital.ui.fragments.profile.identification.VerificationInfoUserFragment
import uz.fido.universaldigital.ui.fragments.profile.settings.ShakeActions
import uz.fido.universaldigital.ui.fragments.profile.user_details.EditProfileFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.transfers.by_phone.TransferByPhoneFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.utils.extensions.adjustBottomNavForKeyboard
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.getStartDestination
import uz.fido.universaldigital.ui.utils.extensions.isActive
import uz.fido.universaldigital.ui.utils.extensions.isNewDesign
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.const.Const
import uz.fido.utils.internet_checker.InternetConnectionChecker
import uz.fido.utils.internet_checker.NoConnectionDialog
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.update_checker.UpdateChecker
import uz.fido.utils.utility.activity.tintSystemBars
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.view.bottom_menu_anim.hideAnimWithSlideDown
import uz.fido.utils.view.bottom_menu_anim.showAnimWithSlideUp
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity(), ShakeDetectionService.OnShakeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var updateChecker: UpdateChecker
    private lateinit var shakeDetectionService: ShakeDetectionService
    private val viewModel: SeasonViewModel by viewModels()

    private var noConnectionDialog: NoConnectionDialog? = null
    private var isStop = false

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                AudioModeService.ACTION_OPEN_ACTIVITY -> {
                    if (!isFinishing) {
                        startActivity(Intent(this@MainActivity, CallSafeActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        shakeDetectionService = ShakeDetectionService(this, this)
        shakeDetectionService.start()

        setContentView(binding.root)
        initBottomNavigationMenuItems()
        initBottomNavigationMenu()
        checkForDeepLink()
        adjustBottomNavForKeyboard(binding.bottomNavigation)
        listenForSeasonChanges()
        checkForAppUpdates()
        askNotificationPermission()
    }

    private fun initBottomNavigationMenuItems() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.navigation_main)
        navGraph.setStartDestination(getStartDestination())
        navHostFragment.navController.setGraph(navGraph, null)
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        binding.bottomNavigation.apply {
            menu.add(0, if (isNewDesign()) R.id.menuNewHomeFragment else R.id.productsFragment, 0, getString(R.string.home)).setIcon(R.drawable.ic_menu_home)
            menu.add(0, R.id.menuTransfersFragment, 1, getString(R.string.transfer)).setIcon(R.drawable.ic_men_transfer)
            menu.add(0, R.id.menuServicesFragment, 2, getString(R.string.services)).setIcon(R.drawable.ic_menu_products)
            menu.add(0, R.id.basePaymentFragment, 3, getString(R.string.payments)).setIcon(R.drawable.ic_menu_payment)
            menu.add(0, R.id.menuMonitoringFragment, 4, getString(R.string.monitoring)).setIcon(R.drawable.ic_menu_monitoring)
        }
    }

    private fun initBottomNavigationMenu() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.productsFragment ||
                destination.id == R.id.menuTransfersFragment ||
                destination.id == R.id.basePaymentFragment ||
                destination.id == R.id.menuServicesFragment ||
                destination.id == R.id.menuMonitoringFragment ||
                destination.id == R.id.menuNewHomeFragment
            ) {
                binding.bottomNavigation.showAnimWithSlideUp()
//                binding.divider.showAnimWithSlideUp()
                when (destination.id) {
                    R.id.productsFragment -> {
                        tintSystemBars(R.color.brandRedColor, R.color.backgroundColor)
                    }

                    R.id.menuNewHomeFragment -> {
                        tintSystemBars(R.color.whiteColor, R.color.whiteColor)
                    }

                    else -> {
                        tintSystemBars(R.color.backgroundColor, R.color.backgroundColor)
                    }
                }
            } else {
                tintSystemBars(R.color.whiteColor)
                binding.bottomNavigation.hideAnimWithSlideDown()
//                binding.divider.hideAnimWithSlideDown()
            }
        }
    }

    private fun listenForSeasonChanges() {
        viewModel.seasonLiveData.observe(this) {
            saveToSecureStore(Const.CURRENT_SEASON, it)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(broadcastReceiver)
            stopService(Intent(this, AudioModeService::class.java))
            shakeDetectionService.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        internetListener()
        try {
            startService(Intent(this, AudioModeService::class.java))
            shakeDetectionService.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    broadcastReceiver, IntentFilter(AudioModeService.ACTION_OPEN_ACTIVITY), RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(
                    broadcastReceiver, IntentFilter(AudioModeService.ACTION_OPEN_ACTIVITY)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isStop = false
        if (!showPinCode) {
            showPinCode = true
            return
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
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
            currentFragment is VerificationInfoUserFragment ||
            currentFragment is EditProfileFragment
        ) {
            pausedMillis = 0L
        } else {
            if (pausedMillis != 0L) {
                pausedMillis = Calendar.getInstance().timeInMillis - pausedMillis
            }
            if (pausedMillis != 0L && pausedMillis > 5000) {
                if (pausedMillis > 180000) {
                    pausedMillis = 0
                    noConnectionDialog?.dismissAllowingStateLoss()
                    startActivityWithClearTask(LoginActivity::class.java)
                } else {
                    openPage(R.id.passCodeFragment2, bundleOf(Const.OPERATION to PassCodeFragment.PASS_OPERATION_POP))
                    pausedMillis = 0
                }
            }
        }
    }

    private fun openPage(id: Int, bundle: Bundle? = null) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(id, bundle, null)
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

    private fun checkForAppUpdates() {
        updateChecker = UpdateChecker(this)
        updateChecker.checkUpdate()
    }

    private fun checkForDeepLink() {
        if (!intent.getStringExtra(PassCodeFragment.DEEP_LINK_OBJECT_VALUE).isNullOrEmpty()) {
            openPage(
                R.id.transferToCardFragment,
                bundleOf(
                    PassCodeFragment.DEEP_LINK_OBJECT_VALUE to intent.getStringExtra(PassCodeFragment.DEEP_LINK_OBJECT_VALUE),
                    PassCodeFragment.DEEP_LINK_OBJECT_ID to intent.getStringExtra(PassCodeFragment.DEEP_LINK_OBJECT_ID),
                    PassCodeFragment.DEEP_LINK_AMOUNT to intent.getStringExtra(PassCodeFragment.DEEP_LINK_AMOUNT),
                    PassCodeFragment.DEEP_LINK_COMMENT to intent.getStringExtra(PassCodeFragment.DEEP_LINK_COMMENT)
                )
            )
        }
        if (!intent.getStringExtra(PassCodeFragment.NOTIFICATION_OPERATION).isNullOrEmpty()) {
            openPage(R.id.mainNewsFragment)
        }
    }

    private fun internetListener() {
        try {
            InternetConnectionChecker(this).observe(this) { isConnected ->
                if (isConnected) {
                    if (isActive()) {
                        if (noConnectionDialog != null) {
                            noConnectionDialog?.dismissAllowingStateLoss()
                            noConnectionDialog = null
                        }
                    }
                } else if (!this@MainActivity.isStop && isActive()) {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        noConnectionDialog = NoConnectionDialog()
                        noConnectionDialog?.show(supportFragmentManager, "")
                    }
                }
            }
        } catch (e: Exception) {
            recordException(e, ::internetListener.name)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UpdateChecker.UPDATE_CODE) {
            if (resultCode == RESULT_OK) {
                updateChecker.appUpdateManager.registerListener(updateChecker.updateListener)
            }
        }
    }

    override fun onShakeDetected() {
        if (this.getFromSecureStore(Const.SHAKING_ACTION_STATE, "N") == "Y") {
            when (Paper.book().read<String>(Const.SELECTED_FRAGMENT)) {
                ShakeActions.ACTION_MY_CARDS -> {
                    openPage(R.id.myCardsServiceFragment)
                }

                ShakeActions.ACTION_MY_CREDITS -> {
                    openPage(R.id.myCreditsServiceFragment)
                }

                ShakeActions.ACTION_MY_DEPOSITS -> {
                    openPage(R.id.myDepositsServiceFragment)
                }

                ShakeActions.ACTION_RATES -> {
                    openPage(R.id.ratesFragment)
                }

                ShakeActions.ACTION_TRANSFER -> {
                    openPage(R.id.transferToCardFragment)
                }
            }
        }
    }

    companion object {
        var pausedMillis: Long = 0L
        var showPinCode = true
    }

}