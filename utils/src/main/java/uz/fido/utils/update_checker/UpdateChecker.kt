package uz.fido.utils.update_checker

import android.app.Activity
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import uz.fido.utils.BuildConfig

/**
 * Created by Husniddin Muhammad Amin on 17.01.2023
 * Tashkent, Uzbekistan.
 */

open class UpdateChecker(private var activity: AppCompatActivity) {

    lateinit var appUpdateManager: AppUpdateManager

    companion object {
        const val UPDATE_CODE = 456
    }

    val updateListener = InstallStateUpdatedListener { state: InstallState ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        }
    }

    fun checkUpdate() {
        if (!BuildConfig.DEBUG) {
            appUpdateManager = AppUpdateManagerFactory.create(activity)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            activity,
                            UPDATE_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
            appUpdateInfoTask.addOnFailureListener {

            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        UpdateDownloadedDialog {
            appUpdateManager.completeUpdate()
        }.show(activity.supportFragmentManager, "")
    }

}