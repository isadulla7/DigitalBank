package uz.fido.universaldigital.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityMainBinding
import uz.fido.universaldigital.services.AudioModeService
import uz.fido.universaldigital.ui.activities.seasons.Season
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
import uz.fido.universaldigital.ui.fragments.profile.user_details.EditProfileFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.transfers.by_phone.TransferByPhoneFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const
import uz.fido.utils.internet_checker.InternetConnectionChecker
import uz.fido.utils.internet_checker.NoConnectionDialog
import uz.fido.utils.update_checker.UpdateChecker
import uz.fido.utils.utility.activity.tintSystemBars
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.view.bottom_menu_anim.hideAnimWithSlideDown
import uz.fido.utils.view.bottom_menu_anim.showAnimWithSlideUp
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var updateChecker: UpdateChecker
    private var noConnectionDialog: NoConnectionDialog? = null
    private var isStop = false
    private lateinit var database: DatabaseReference

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_OPEN_ACTIVITY" -> {
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
        database = FirebaseDatabase.getInstance().getReference("season")
        setContentView(binding.root)
        initBottomNavigationMenuItems()
        initBottomNavigationMenu()
        checkUpdate()
        askNotificationPermission()
        checkForDeepLink()
        bottomNavSheet()
        listenForSeasonChanges()
    }

    private fun listenForSeasonChanges() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                saveToPaper(Const.CURRENT_SEASON, snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                saveToPaper(Const.CURRENT_SEASON, Season.DEFAULT)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(broadcastReceiver)
            stopService(Intent(this, AudioModeService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bottomNavSheet() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = imeInsets.bottom
            }
            insets
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        internetListener()
        try {
            startService(Intent(this, AudioModeService::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    broadcastReceiver, IntentFilter("ACTION_OPEN_ACTIVITY"), Context.RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(
                    broadcastReceiver, IntentFilter("ACTION_OPEN_ACTIVITY")
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
                    noConnectionDialog?.dismiss()
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

    private fun checkUpdate() {
        updateChecker = UpdateChecker(this)
        updateChecker.checkUpdate()
    }

    private fun checkForDeepLink() {
        if (!intent.getStringExtra(PassCodeFragment.DEEP_LINK_OBJECT_VALUE).isNullOrEmpty()) {
            openPage(
                R.id.requestMoneyPaymentFragment,
                bundleOf(
                    PassCodeFragment.DEEP_LINK_OBJECT_VALUE to intent.getStringExtra(
                        PassCodeFragment.DEEP_LINK_OBJECT_VALUE
                    ),
                    PassCodeFragment.DEEP_LINK_OBJECT_ID to intent.getStringExtra(
                        PassCodeFragment.DEEP_LINK_OBJECT_ID
                    ),
                    PassCodeFragment.DEEP_LINK_AMOUNT to intent.getStringExtra(
                        PassCodeFragment.DEEP_LINK_AMOUNT
                    ),
                    PassCodeFragment.DEEP_LINK_COMMENT to intent.getStringExtra(
                        PassCodeFragment.DEEP_LINK_COMMENT
                    )
                )
            )
        }
    }

    private fun internetListener() {
        InternetConnectionChecker(this).observeForever { isConnected ->
            try {
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
            } catch (e: Exception) {
                recordException(e)
            }
        }
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
                binding.divider.showAnimWithSlideUp()
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
                binding.divider.hideAnimWithSlideDown()
            }
        }
    }

    private fun getStartDestination(): Int {
        return if (getFromPaper(Const.NEW_DESIGN, "N") == "Y") R.id.menuNewHomeFragment else R.id.productsFragment
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private fun isNewDesign() = getFromPaper(Const.NEW_DESIGN, "N") == "Y"

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