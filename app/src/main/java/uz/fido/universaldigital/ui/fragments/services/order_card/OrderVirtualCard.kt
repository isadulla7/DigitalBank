package uz.fido.universaldigital.ui.fragments.services.order_card

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.SpannedString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.OrderCardTypeResponse
import uz.fido.network.domain.model.cards.OrderVirtualCardRequest
import uz.fido.network.domain.model.cards.ProductType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentOrderVirtualCardBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class OrderVirtualCard : BaseFragment<FragmentOrderVirtualCardBinding, OrderCardViewModel>(
    FragmentOrderVirtualCardBinding::inflate, OrderCardViewModel::class.java
), BaseInterface {

    private lateinit var cardOrderResponse: OrderCardTypeResponse
    private lateinit var priceItem: ProductType

    private var cardType: Int = 0
    private var productCode = ""

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        gettingDetails()
        init()
        initOffer()
    }

    private fun gettingDetails() {
        binding.btnContinue.setOnClickListener {
            if (!binding.checkBox.isChecked) {
                showSnackbar(getString(R.string.please_accept_privacy))
            } else if (binding.etSecretWord.editableText.toString().isNotEmpty()) {
                orderVirtualCard(binding.etSecretWord.editableText.toString())
            } else {
                if (priceItem.code != "GL_VIRTUAL_CARD") showSnackbar(getString(R.string.input_secret_word))
                else orderVirtualCard(binding.etSecretWord.editableText.toString())
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        arguments?.let {
            priceItem = Gson().fromJson(
                it.getString("priceItem"), ProductType::class.java
            )
            cardOrderResponse = Gson().fromJson(
                it.getString("response"), OrderCardTypeResponse::class.java
            )
            productCode = requireArguments().getString("code", "")
            cardType = requireArguments().getInt("type", 0)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.paymentSystem.text = when (cardType) {
            1 -> "UzCard"
            2 -> "Humo"
            3 -> "Mastercard"
            else -> "Visa"
        }
        binding.cardImage.setImageResource(
            when (priceItem.code) {
                "GL_VIRTUAL_CARD" -> {
                    R.drawable.virtual_vard_bg
                }

                "TET_VIRTUAL_CARD" -> {
                    R.drawable.virtual_vard_bg
                }

                else -> {
                    R.drawable.virtual_vard_bg
                }
            }
        )
        binding.issueCost.text = Format.formatAmount((priceItem.price.toDouble() / 100).toString()) + " UZS"
        when (priceItem.code) {
            "GL_VIRTUAL_CARD" -> {
                binding.layoutP2pPercent.visibility = View.GONE
                binding.etSecretWord.visibility = View.GONE
                binding.secretWordDesc.visibility = View.GONE
                binding.securityCodeTxt.visibility = View.GONE
                binding.p2pPercent.text = priceItem.transact_process_perc + " %"
                binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
                    binding.btnContinue.isEnabled(b)
                }
            }

            "VISA_VIRTUAL_CARD" -> {
                binding.etSecretWord.addTextChangedListener {
                    binding.btnContinue.isEnabled(
                        it.toString().isNotEmpty() && binding.checkBox.isChecked
                    )
                }
                binding.p2pPercent.text = priceItem.transact_process_perc + " %"
            }

            "SV_DUO_VIRTUAL_CARD" -> {
                binding.etSecretWord.addTextChangedListener {
                    binding.btnContinue.isEnabled(
                        it.toString().length in 5..10 && binding.checkBox.isChecked
                    )
                }
                binding.layoutP2pPercent.visibility = View.GONE
            }
        }
        binding.expire.text = priceItem.card_validity_period + " ${requireContext().getString(R.string.let)}"
        binding.orderCardType.text = priceItem.name
    }

    private fun orderVirtualCard(secretWord: String) {
        binding.btnContinue.setProgress(true)
        viewModel.orderVirtualCard(
            getClientToken(), OrderVirtualCardRequest(
                orderType = productCode,
                cardType = cardType.toString().replace("4", "3"),
                contact = Paper.book().read(Const.PAPER_CLIENT_PHONE),
                service_id = "-11",
                smsMobilePhone = Paper.book().read(Const.PAPER_CLIENT_PHONE),
                virtual = "Y",
                secretWord = secretWord
            )
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(
                        R.id.basicSuccessFragment, bundleOf(Const.OPERATION to BasicSuccessFragment.ORDER_VIRTUAL_CARD)
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initOffer() {
        val fullText = getText(R.string.accept_deposit_privacy) as SpannedString
        val spannableString = SpannableString(fullText)
        val annotations = fullText.getSpans(0, fullText.length, Annotation::class.java)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val website = "https://ibank.ubank.uz/cib/offertaCard.html"/*if (priceItem.code == "TET_VIRTUAL_CARD") {
                    "https://aab.uz/download.php?f=101ccbefc15f322d168a7e6bdeb04ca7.pdf&i=40425"
                } else "https://aab.uz/upload/iblock/01d/3shax3op5pzox646d243uuv2hywnfb82.pdf"*/
                val webIntent = Intent(Intent.ACTION_VIEW)
                webIntent.data = Uri.parse(website)
                requireActivity().startActivity(webIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        annotations?.find {
            it.value == "help_link"
        }?.let {
            spannableString.setSpansForPrivacy(it, clickableSpan, fullText, requireContext())
        }

        binding.textSingUpTerms.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
        }
    }
}