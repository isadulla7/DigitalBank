package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.AppIcons
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLoginBinding
import uz.fido.universaldigital.ui.activities.app_icon_changer.changeAppIcon
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED
import uz.fido.utils.security.SecurityCheck.isPhoneRooted
import uz.fido.utils.security.SecurityCheck.isRunningOnEmulator

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().getReference(Const.FIREBASE_APP_ICON_NAME)
        setContentView(binding.root)
        checkForDeviceLock()
        listenAppIconChanges()
    }

    private fun listenAppIconChanges() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentIcon = getFromPaper(Const.CURRENT_APP_ICON, AppIcons.APP_ICON_DEFAULT)
                if (currentIcon != snapshot.value.toString()) {
                    saveToPaper(Const.CURRENT_APP_ICON, snapshot.value.toString())
                    changeAppIcon()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                saveToPaper(Const.CURRENT_APP_ICON, AppIcons.APP_ICON_DEFAULT)
            }
        })
    }

    private fun checkForDeviceLock() {
        if (this.isRunningOnEmulator()) {
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
        if (notification != null) {
            val bundle = bundleOf(PassCodeFragment.NOTIFICATION_OPERATION to notification)
            setStartDestination(bundle)
        } else {
            setStartDestination()
        }
    }

    private fun setStartDestination(bundle: Bundle? = null) {
        val navController = findNavController(this, R.id.nav_host_login)
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

    private fun openRootedDeviceWarning() {
        val intent = Intent(this, RootedDeviceActivity::class.java)
        startActivity(intent)
        finish()
    }

}