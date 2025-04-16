package uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.SpannedString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.deposits.CreateCreditRequest
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOpenDepositTwoStepBinding
import uz.fido.universaldigital.databinding.ViewDepositCreateBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isUniversalCard
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.util.Calendar

@AndroidEntryPoint
class OpenDepositStepTwoFragment : BaseFragment<FragmentOpenDepositTwoStepBinding, MainDepositViewModel>(FragmentOpenDepositTwoStepBinding::inflate, MainDepositViewModel::class.java),
        (String, String) -> Unit {

    private lateinit var deposit: Deposit
    private var amount = ""
    private var card: CardResponse? = null
    private var smsCode: String = ""
    private var isCard = false
    private var isSum = true
    private var type = CurrencyConst.CURRENCY_CHAR_UZS
    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            deposit = it.serializable<Deposit>("deposit") as Deposit
            amount = it.getString("amount", "")
            isSum = it.getBoolean("isSum")
        }
        init()
        cardListTip()
        initCards()
        initOffer()
        onClickView()
        getSmsKey()
        percentCurrent()

    }

    private fun percentCurrent() {
        if (deposit.percent == "0") {
            binding.chooseCardLayout.visibility = View.GONE
        }
    }

    private fun cardListTip() {
        type = if (!isSum) {
            CurrencyConst.CURRENCY_CHAR_USD
        } else {
            CurrencyConst.CURRENCY_CHAR_UZS
        }
    }

    private fun getSmsKey() {
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            smsCode = bundle.getString("sms_code").toString()
            createDeposit()
        }
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            forSmsCheck()
        }
    }

    private fun forSmsCheck() {
        if (deposit.percent == "0") {
            createDeposit()
        } else if (isCard) {
            card?.let { selectedCard ->
                if (!checkForPaymentSms(
                        card = selectedCard, smsControlLimit = "-1", amount = Format.formatAmountToTiyn(amount)
                    )
                ) {
                    createDeposit()
                } else {
                    checkForSms(selectedCard, amount, "-5", this)
                }
            }
        }
    }

    private fun createDeposit() {
        card?.let { selectedCard ->
            val createCreditRequest = CreateCreditRequest(
                command = if (deposit.percent != "0") {
                    if (selectedCard.object_type == WALLET) "purse&dep" else "card&dep"
                } else "dep",
                amount = Format.formatAmountToTiyn(amount),
                from_object_id = if (deposit.percent != "0") selectedCard.object_id else null,
                depId = deposit.dep_id.toString(),
                service_id = "-5",
                depType = deposit.dep_type.toString(),
                pay_to_card = deposit.pay_to_card,
                pay_to_card_number = deposit.pay_to_card_number,
                sms_code = smsCode,
                bxm_code = requireArguments().getString("bxm_code")
            )
            binding.btnContinue.setProgress(true)
            viewModel.createDeposit(getClientToken(), createCreditRequest).observe(viewLifecycleOwner) { resources ->
                binding.btnContinue.setProgress(false)
                when (resources.status) {
                    Status.SUCCESS -> {
                        goto(
                            R.id.basicSuccessFragment, bundleOf(
                                Const.OPERATION to BasicSuccessFragment.DEPOSIT_OPEN, "amount" to amount
                            )
                        )
                    }

                    Status.ERROR -> {
                        showSnackbar(resources.message.toString())
                    }
                }
            }
        }
    }

    private fun init() {
        val cal: Calendar = Calendar.getInstance()
        if (deposit.keeping_time.isNotEmpty()) {
            when (deposit.keeping_time.last()) {
                'D' -> {
                    cal.add(Calendar.DAY_OF_YEAR, deposit.keeping_time.dropLast(1).toInt())
                }

                'M' -> {
                    cal.add(Calendar.MONTH, deposit.keeping_time.dropLast(1).toInt())
                }

                'Y' -> {
                    cal.add(Calendar.YEAR, deposit.keeping_time.dropLast(1).toInt())
                }
            }
        }
        binding.appBar.setTitle(getString(R.string.confirming))
        addView(getString(R.string.name_deposit), deposit.dep_name)
        addView(
            getString(R.string.deposit_amount), amount + " " + Format().getCurrencyChar(deposit.currency_code)
        )
        addView(getString(R.string.deposit_percent), deposit.percent + " %")
        addView(
            getString(R.string.shelf_life), Format().formattedDepositExpire(requireContext(), deposit.keeping_time)
        )
        addView(
            getString(R.string.maybe_deposit), if (deposit.replenishment == "Y") getString(R.string.maybe_dep) else getString(R.string.possible)
        )
        addView(getString(R.string.interest_rate_type), deposit.type_percent)
        addView(getString(R.string.with_drawal), deposit.type_dep)

    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding = ViewDepositCreateBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { cardResponseList ->
            val filteredCards = cardResponseList.filter { it.isUniversalCard() }
            binding.chooseCardLayout.initCards(
                filteredCards as ArrayList<CardResponse>, amount, type
            ) { cardResponse ->
                cardResponse?.let { card ->
                    if (card.balance.toBigDecimal().divide(100.toBigDecimal()).compareTo(amount.toBigDecimal()) == -1) {
                        isCard = false
                        this.card = card
                        binding.btnContinue.isEnabled(false)
                    } else {
                        binding.btnContinue.isEnabled(true)
                        this.card = card
                        isCard = true
                    }
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
                val website = when (deposit.dep_id) {
                    1874 -> "https://ibank.ubank.uz/cib/sarmoya-25.html"
                    1674 -> "https://ibank.ubank.uz/cib/qulay_daromad.html"
                    1694 -> "https://ibank.ubank.uz/cib/yubiley.html"
                    1753 -> "https://ibank.ubank.uz/cib/qulay_daromad.html"
                    else -> "https://universalbank.uz/juristic"
                }
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
            spannableString.setSpans(it, clickableSpan, fullText, requireContext())
        }

        binding.textPrivacy.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun invoke(smsCofirm: String, lineString: String) {
        if (smsCofirm == "Y") {
            goto(
                R.id.confirmSmsFragment, bundleOf(
                    Const.OPERATION to ConfirmSmsFragment.SMS_DEPOSIT_OPERATION, ConfirmSmsFragment.STRING_LINE to lineString
                )
            )
        } else createDeposit()
    }

}