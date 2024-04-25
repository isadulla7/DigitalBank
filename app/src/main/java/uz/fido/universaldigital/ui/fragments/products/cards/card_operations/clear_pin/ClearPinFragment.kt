package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.clear_pin

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.ResetPinCountCheck
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.PinClearFragmentBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ClearPinFragment : BaseFragment<PinClearFragmentBinding, UtilsViewModel>(
    PinClearFragmentBinding::inflate, UtilsViewModel::class.java
) {

    private lateinit var card: CardResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.serializable<CardResponse>(Const.CARD) as CardResponse
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.addButton.setOnClickListener {
            val resetPin = ResetPinCountCheck(
                "card",
                card.object_id,
                getClientPhoneNumber(),
                AppSignatureHelper(requireContext()).appKeyHash,
                requireContext().getDeviceIds()
            )
            viewModel.checkResetPinCount(getClientToken(), resetPin).observe(viewLifecycleOwner) {
               showProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                         hideProgress()
                        val line = it.data?.string_line ?: ""
                        gotoWithSlide(
                            R.id.confirmSmsFragment, bundleOf(
                                Const.OPERATION to ConfirmSmsFragment.SMS_RESET_PIN,
                                Const.CARD_NUMBER to card.object_id,
                                "object_data" to card.object_expiry,
                                ConfirmSmsFragment.STRING_LINE to line
                            )
                        )
                    }

                    Status.ERROR -> {
                        hideProgress()
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }


}