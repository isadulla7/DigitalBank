package uz.fido.universaldigital.ui.fragments.services.deposit.constructor

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
import uz.fido.network.domain.model.deposits.constructor.DepositConstructor
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDepositConstructorConfirmBinding
import uz.fido.universaldigital.databinding.ViewDepositCreateBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
open class DepositConstructorConfirmFragment :
    BaseFragment<FragmentDepositConstructorConfirmBinding, ConstructorViewModel>
        (FragmentDepositConstructorConfirmBinding::inflate, ConstructorViewModel::class.java),
        (String, String) -> Unit {


    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()
    private lateinit var deposit: DepositConstructor
    private lateinit var cardResponse: CardResponse
    private var stringLine = ""
    private var smsCode = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            deposit = it.serializable<DepositConstructor>("model") as DepositConstructor
        }

        initCards()
        addViewItem()
        initOffer()
        onCLickView()
        getSmsKey()
    }

    private fun getSmsKey() {
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            smsCode = bundle.getString("sms_code").toString()
            stringLine = bundle.getString("string_line").toString()
            createDeposit()
        }
    }

    private fun onCLickView() {
        binding.btnContinue.setOnClickListener {
            getSms()
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getSms() {
        if (!checkForPaymentSms(
                card = cardResponse,
                smsControlLimit = "-1",
                amount = Format.formatAmountToTiyn(deposit.amount)
            )
        ) {
            createDeposit()
        } else {
            checkForSms(cardResponse, deposit.amount, "-5", this)
        }
    }


    private fun createDeposit() {
        binding.btnContinue.setProgress(true)
        val createCreditRequest = CreateCreditRequest(
            command = if (cardResponse.object_type == WALLET) "purse&dep" else "card&dep",
            amount = Format.formatAmountToTiyn(deposit.amount),
            from_object_id = cardResponse.object_id,
            depId = deposit.dep_id,
            service_id = "-5",
            depType = deposit.dep_type,
            pay_to_card = deposit.pay_to_card,
            pay_to_card_number = deposit.pay_to_object_value,
            dep_construct = "Y",
            dcParam100 = "000",
            dcParam102 = Format.formatAmountToTiyn(deposit.amount),
            dcParam103 = deposit.dcParam103,
            dcParam104 = deposit.dcParam104,
            dcParam105 = deposit.date,
            dcParam200 = deposit.replenishment,
            dcParam201 = deposit.capitalization,
            dcParam202 = "1M",
            dcParam203 = "Y",
            dcParam204 = deposit.partial_withdrawal,
            dep_name = deposit.dep_name,
            string_line = stringLine,
            sms_code = smsCode
        )

        viewModel.createDeposit(getClientToken(), createCreditRequest).observe(
            viewLifecycleOwner
        ) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }

                Status.SUCCESS -> {
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, BasicSuccessFragment.DEPOSIT_OPEN)
                    gotoWithSlide(R.id.basicSuccessFragment, bundle)
                }
            }
        }
    }


    private fun addViewItem() {
        addView(getString(R.string.deposit_amount), Format.formatAmount(deposit.amount) + " UZS")
        addView(getString(R.string.deposit_percent), "${deposit.percent} %")
        addView(getString(R.string.deposit_deadline_until), deposit.date)
        addView(
            getString(R.string.interest_paid),
            if (deposit.pay_to_card == "Y") getString(R.string.to_deposit) else getString(R.string.to_card)
        )
        addView(
            getString(R.string.replenishment),
            if (deposit.replenishment == "Y") getString(R.string.possible) else getString(R.string.impossible)
        )
        addView(
            getString(R.string.capitalization),
            if (deposit.capitalization == "Y") getString(R.string.yes) else getString(R.string.no)
        )
        addView(
            getString(R.string.withdrawal),
            if (deposit.partial_withdrawal == "Y") getString(R.string.granted) else getString(R.string.only_percents)
        )
        if (deposit.interest_payment.isNotEmpty()) {
            addView(getString(R.string.period_interest_payment), deposit.interest_payment)
        }
    }


    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ViewDepositCreateBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, deposit.amount, "UZS"
            ) { cardResponse ->
                cardResponse?.let { card ->
                    if (card.balance.toBigDecimal().divide(100.toBigDecimal())
                            .compareTo(deposit.amount.toBigDecimal()) == -1
                    ) {
                        binding.btnContinue.isEnabled(false)
                    } else {
                        this@DepositConstructorConfirmFragment.cardResponse = card
                        binding.btnContinue.isEnabled(true)
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

                val website = Keys.getDepositOfferBaxtliBolalik()
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

    override fun invoke(sms_confirm: String, line_String: String) {

        if (sms_confirm == "Y")
            goto(
                R.id.confirmSmsFragment,
                bundleOf(
                    Const.OPERATION to ConfirmSmsFragment.SMS_DEPOSIT_OPERATION,
                    "string_line" to line_String
                )
            )
        else createDeposit()
    }


}