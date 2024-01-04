package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.amount_requests.RmCreateRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateRequestMoneyBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.amount.AmountSuggestionView

@AndroidEntryPoint
class RequestMoneyFragment : BaseFragment<FragmentCreateRequestMoneyBinding, RequestMoneyViewModel>(
    FragmentCreateRequestMoneyBinding::inflate, RequestMoneyViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var receiverCard: CardResponse? = null
    private var minAmount = 1000

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSuggestions()
        initCards()
        initTextWatchers()
        initSetOnClickListeners()
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>,
                (0).toString(),
                CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    receiverCard = card
                }
            }
        }
    }

    private fun initTextWatchers() {
        binding.etAmount.doAfterTextChanged {
            binding.btnContinue.isEnabled(checkForButton())
        }
    }

    private fun checkForButton(): Boolean {
        val etAmount = binding.etAmount.editableText.toString().replace(" ", "").ifEmpty { "0" }
        val formattedAmount = etAmount.toDouble()
        return when {
            formattedAmount < minAmount -> {
                false
            }

            else -> true
        }
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            val url =
                "https://universaldigitalbank.page.link/qrcard?cardNumber=${receiverCard?.object_value}"
            Firebase.dynamicLinks.shortLinkAsync {
                link = Uri.parse(url)
                domainUriPrefix = "https://universaldigitalbank.page.link"
                androidParameters {
                    minimumVersion = 24
                }
            }.addOnSuccessListener { result ->
                binding.btnContinue.setProgress(false)
                val shortLink = result.shortLink
                goto(R.id.requestMoneySuccessFragment, bundleOf("url" to shortLink.toString()))
            }.addOnFailureListener {
                toast(it.localizedMessage.toString())
                binding.btnContinue.setProgress(false)
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun createRequestMoney() {
        binding.btnContinue.setProgress(true)
        viewModel.createRm(
            getClientToken(), RmCreateRequest(
                requested_sum = Format.formatAmountToTiyn(binding.etAmount.editableText.toString()),
                name = binding.etAmount.editableText.toString(),
                object_id = receiverCard?.object_id.toString(),
                receiver_phone_number = ""
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnContinue.setProgress(false)
                    goto(
                        R.id.requestMoneySuccessFragment, bundleOf(
                            "url" to it.data?.url.toString()
                        )
                    )
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

}