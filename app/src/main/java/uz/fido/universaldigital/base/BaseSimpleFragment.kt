package uz.fido.universaldigital.base

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.security.VpnErrorActivity
import uz.fido.universaldigital.ui.dialogs.BaseInfoDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsViewModel
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.ServerMessages.ERROR_CODE_VPN
import uz.fido.utils.const.ServerMessages.getMeaningFulMessage
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.user.getClientToken

abstract class BaseSimpleFragment<VB : ViewBinding>(
    inflate: Inflate<VB>,
) : SimpleAbstractFragment<VB>(inflate) {

    private val smsViewModel: ConfirmSmsViewModel by viewModels()

    fun showProgress(progressText: String? = null) = (activity as BaseActivity).showProgress(progressText)

    fun hideProgress() = (activity as BaseActivity).hideProgress()

    fun functionInProgress() {
        showSnackbar(getString(R.string.service_under_development), title = getString(R.string.info))
    }

    fun showSnackbar(
        snackbarText: String,
        title: String? = null,
        buttonText: String? = null,
        okClickListener: (() -> Unit)? = null
    ) {
        hideProgress()
        var message = snackbarText
        if (message == ERROR_CODE_VPN) {
            openVpnErrorActivity()
        } else {
            message = getMeaningFulMessage(message)
            if (message.isNotEmpty() && view != null) {
                showBaseInfoDialog(title, buttonText, message, okClickListener)
            }
        }
    }

    private fun showBaseInfoDialog(
        title: String?, buttonText: String?, message: String, okClickListener: (() -> Unit)?
    ) {
        val dialog = BaseInfoDialog(title ?: getString(R.string.error), message, buttonText, okClickListener)
        dialog.show(childFragmentManager, "")
    }

    private fun openVpnErrorActivity() {
        startActivity(Intent(requireActivity(), VpnErrorActivity::class.java))
    }

    companion object {
        @JvmStatic
        protected fun checkForPaymentSms(
            card: CardResponse, smsControlLimit: String, amount: String
        ): Boolean {
            return if (card.safe_mode == "Y" || card.pay_with_sms == "Y") {
                true
            } else smsControlLimit != "-1" && Format.convertFromStringToBigDecimal(amount) > Format.convertFromStringToBigDecimal(
                smsControlLimit
            )
        }
    }

    protected fun checkForSms(
        card: CardResponse, amount: String, serviceId: String, listener: (String, String) -> Unit
    ) {
        val model = CheckSmsForPayment(
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
            from_object_id = card.object_id,
            amount = amount,
            service_id = serviceId,
            device_code = requireContext().getDeviceIds()
        )
        smsViewModel.checkForSmsPaymentRequest(getClientToken(), model).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    listener.invoke(
                        it.data?.is_sms_confirm.toString(), it.data?.string_line.toString()
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

}