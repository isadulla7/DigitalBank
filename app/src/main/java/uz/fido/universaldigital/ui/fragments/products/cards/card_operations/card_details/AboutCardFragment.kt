package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_details

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.GetCVVRequest
import uz.fido.network.domain.model.cards.GetObjValueRequest
import uz.fido.network.domain.model.cards.GetObjValueResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAboutCardBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class AboutCardFragment : BaseFragment<FragmentAboutCardBinding, MenuProductsViewModel>(
    FragmentAboutCardBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse

    private var securityCode: String = ""
    private var isCvvVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.serializable<CardResponse>("card") as CardResponse

        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        initSetOnClickListeners()
        getSecurityCode()
    }

    private fun initDetails() {
        binding.apply {
            bankCodeLayout.isVisible = card.account_code.isNotEmpty()
            bankNameLayout.isVisible = card.bank_name.isNotEmpty()
            cardRequisitesLayout.isVisible = card.account_code.isNotEmpty()
            expireDateLayout.isVisible = card.object_type != WALLET

            cardNumber.text = Format.formatCardNumberVisible(card.object_value)
            holderName.text = card.embossed_name
            cardExpire.text = Format.expireDate(card.object_expiry)
            bankCode.text = card.bank_code
            bankName.text = card.bank_name
            cardRequisites.text = Format.formatCardAccountNumber(card.account_code)
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.passToggle.setOnClickListener {
            if (isCvvVisible) {
                binding.cvvNumber.text = "******"
                binding.passToggle.setImageResource(R.drawable.ic_eye)
            } else {
                binding.cvvNumber.text = securityCode
                binding.passToggle.setImageResource(R.drawable.ic_eye_close)
            }
            isCvvVisible = !isCvvVisible
        }
        binding.copyCardNumber.setOnClickListener {
            getObjValue()
        }
    }

    private fun getSecurityCode() {
        if (card.object_type == CURRENCY_CARD) {
            viewModel.getCVV(getClientToken(), GetCVVRequest(cardNumber = card.object_value)).observe(viewLifecycleOwner) {
                it?.let {
                    if (it.status == Status.SUCCESS) {
                        if (it.data?.securityCode != null && it.data!!.securityCode.isNotEmpty()) {
                            binding.cvvLayout.visibility = View.VISIBLE
                            binding.cvvNumber.text = it.data!!.securityCode
                            securityCode = it.data!!.securityCode
                        }
                    }
                }
            }
        }
    }

    private fun getObjValue() {
        binding.progressView.visibility = View.VISIBLE
        viewModel.getCardNumberRequest(getClientToken(), GetObjValueRequest(from_object_id = card.object_id)).observe(viewLifecycleOwner) {
            it?.let {
                binding.progressView.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        val result = it.data as GetObjValueResponse
                        try {
                            copyObjValue(CryptoUtil.decryptWithoutSalt(result.object_value, getFromSecureStore(Const.PASSWORD_ENC)))
                        } catch (e: Exception) {
                            toast(e.localizedMessage)
                        }
                    }

                    Status.ERROR -> {

                    }
                }
            }
        }
    }

    private fun copyObjValue(objValue: String) {
        val clipboard: ClipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), objValue)
        clipboard.setPrimaryClip(clip)
    }

}