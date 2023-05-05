package uz.fido.universaldigital.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import uz.fido.universaldigital.ui.activities.VpnErrorActivity
import uz.fido.utils.const.AlertType
import uz.fido.utils.const.ServerMessages.ERROR_CODE_VPN
import uz.fido.utils.const.ServerMessages.getMeaningFulMessage
import uz.fido.utils.log.Log.d
import uz.fido.utils.view.custom_snackbar.CustomSnackbar

abstract class BaseFragment<VB : ViewBinding, VM : AbstractViewModel>(
    inflate: Inflate<VB>,
    viewModelClass: Class<VM>
) : AbstractFragment<VB, VM>(inflate, viewModelClass) {

    protected val viewModel: VM get() = abstractViewModel
    val currentActivity get() = activity as BaseActivity

    fun showProgress(progressText: String? = null) =
        (activity as BaseActivity).showProgress(progressText)

    fun hideProgress() = (activity as BaseActivity).hideProgress()

    fun isCurrentThemeDark(): Boolean {
        return (activity as BaseActivity).isCurrentThemeDark()
    }

    fun showSnackbar(snackbarText: String, alertType: AlertType = AlertType.ERROR) {
        hideProgress()
        var message = snackbarText
        if (message == ERROR_CODE_VPN) {
            openVpnErrorActivity()
        } else {
            message = getMeaningFulMessage(message)
            if (message.isNotEmpty() && view != null) {
                CustomSnackbar.make(requireView(), message, alertType)
                    .setDuration(3000)
                    .show()
            }
        }
    }

    private fun openVpnErrorActivity() {
        startActivity(Intent(requireActivity(), VpnErrorActivity::class.java))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.d("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        context?.d("onStart")
    }

    override fun onPause() {
        super.onPause()
        context?.d("onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.d("onDestroyView")
    }

}