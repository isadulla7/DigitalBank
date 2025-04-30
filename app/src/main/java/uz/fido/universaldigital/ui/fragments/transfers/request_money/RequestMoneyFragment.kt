package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateRequestMoneyBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
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
                CurrencyConst.CURRENCY_CHAR_UZS,
                addCard = {
                    goto(R.id.addCardFragment)
                }
            ) { cardResponse ->
                cardResponse?.let { card ->
                    receiverCard = card
                }
            }
        }
    }

    private fun initTextWatchers() {
        binding.etAmount.doAfterTextChanged {
            binding.btnContinue.isEnabled(checkForButton() && receiverCard != null)
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
            val url = "https://universaldigitalbank.page.link/qrcard?cardNumber=${receiverCard?.object_value}&amount=${
                Format.sendFormat(binding.etAmount.text.toString())
            }&objectId=${receiverCard?.object_id}"
            Firebase.dynamicLinks.shortLinkAsync {
                link = url.toUri()
                domainUriPrefix = "https://universaldigitalbank.page.link"
                androidParameters {
                    minimumVersion = 24
                }
                val uri = "https://firebasestorage.googleapis.com/v0/b/universal-mobile-digital.appspot.com/o/Uploads%2Fsocial_media.png?alt=media&token=0bb1942b-946a-4712-aff7-a77318b17fb6".toUri()
                socialMetaTagParameters {
                    title = requireContext().getString(R.string.request_money)
                    description = requireContext().getString(R.string.request_money_desc)
                    imageUrl = uri
                }
            }.addOnSuccessListener { result ->
                binding.btnContinue.setProgress(false)
                val shortLink = result.shortLink
                goto(R.id.requestMoneySuccessFragment, bundleOf("url" to shortLink.toString()))
            }.addOnFailureListener {
                toast(it.localizedMessage.orEmpty())
                binding.btnContinue.setProgress(false)
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

}