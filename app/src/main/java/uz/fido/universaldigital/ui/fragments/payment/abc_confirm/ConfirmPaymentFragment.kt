package uz.fido.universaldigital.ui.fragments.payment.abc_confirm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.lang3.math.NumberUtils
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.DefaultReferenceResponse
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentConfirmPaymentBinding
import uz.fido.universaldigital.databinding.ItemConfirmPaymentBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.init_payment.second_step.PaymentSecondStepFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils
import uz.fido.universaldigital.ui.utils.extensions.checkForFingerPrintConfirmation
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.format.Format.formatAmount
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class ConfirmPaymentFragment : BaseSimpleFragment<FragmentConfirmPaymentBinding>(
    FragmentConfirmPaymentBinding::inflate
), BaseInterface {

    private lateinit var paymentParamsArrayList: ArrayList<PaymentParams>
    private lateinit var params: HashMap<String, String>

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private val paymentViewModel: ConfirmPaymentViewModel by activityViewModels()
    private var templateKeyValues = ArrayList<TemplateKeyValue>()
    private var paymentService: PaymentService? = null
    private var senderCard: CardResponse? = null
    private var currency = "000"

    private var paymentOperation: Int? = null
    private var operation: String? = null
    private var percentForOthers = 0.00
    private var percentForAsia = 0.00
    private var totalAmount = 0.00
    private var amount = 0.00
    private var stringLine = ""

    companion object {
        const val CONFIRM_PAYMENT_OPERATION = "operation"
        const val OPERATION_PAYMENT_SECOND = "payment_2"
        const val OPERATION_REQUISITES = "requisites"
        const val PAYMENT_OPERATION = "payment_operation"
        const val OPERATION_PAYMENT = "payment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operation = it.getString(CONFIRM_PAYMENT_OPERATION)
            paymentOperation = it.getInt(PAYMENT_OPERATION)
            paymentParamsArrayList = it.serializable<ArrayList<PaymentParams>>("list")!!
            paymentService = it.serializable<PaymentService>("paymentService") as PaymentService
            if (it.serializable<ArrayList<TemplateKeyValue>>("templateKeyValues") != null) templateKeyValues =
                it.serializable<ArrayList<TemplateKeyValue>>("templateKeyValues") as ArrayList<TemplateKeyValue>
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            stringLine = bundle.getString("string_line").orEmpty()
            createPayment()
        }
        setFragmentResultListener(PinCodeFragment.PIN_OPERATION_PAYMENT) { _, _ ->
            checkForSmsBeforePayment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawView()
        setCommission()
        initCards()
    }

    private fun initDetails() {
        binding.paymentName.text = paymentService?.nameIndex
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.continueButton.setOnClickListener {
            params = HashMap()
            for (i in paymentParamsArrayList.indices) {
                params[paymentParamsArrayList[i].code] = paymentParamsArrayList[i].def_value
            }
            val keyValue = requireArguments().serializable<HashMap<String, String>>(PaymentSecondStepFragment.PAYMENT_KEY_VALUES) as HashMap<String, String>
            keyValue.forEach {
                if (it.key == "REGIONS") {
                    params[it.key] = it.value
                }
            }
            val hashMap = HashMap<String, Any>()
            hashMap["contract_id"] = paymentService?.service_id!!
            hashMap["key_value"] = params
            if (senderCard != null) {
                if (!checkForFingerPrintConfirmation()) {
                    checkForSmsBeforePayment()
                } else {
                    fingerPrintAuth()
                }
            }
        }

    }

    private fun initCards() {
        if (paymentService?.service_id == 788) {
            menuProductsViewModel.cards.observe(viewLifecycleOwner) {
                val filterList= it.filter { it.is_Dv!="Y" }
                binding.chooseCardLayout.initCardsOnly(
                    filterList as ArrayList<CardResponse>, (totalAmount).toString(), if (currency == "000") CurrencyConst.CURRENCY_CHAR_UZS else CurrencyConst.CURRENCY_CHAR_USD
                ) { cardResponse ->
                    cardResponse?.let { card ->
                        senderCard = card
                        binding.continueButton.isEnabled(
                            !BaseCardUtils.compareWithBalance(
                                totalAmount.toString(), card
                            )
                        )
                    }
                }
            }
        } else {
            menuProductsViewModel.cards.observe(viewLifecycleOwner) {
                binding.chooseCardLayout.initCards(
                    it as ArrayList<CardResponse>, (totalAmount).toString(), if (currency == "000") CurrencyConst.CURRENCY_CHAR_UZS else CurrencyConst.CURRENCY_CHAR_USD
                ) { cardResponse ->
                    cardResponse?.let { card ->
                        senderCard = card
                        binding.continueButton.isEnabled(
                            !BaseCardUtils.compareWithBalance(
                                totalAmount.toString(), card
                            )
                        )
                    }
                }
            }
        }
    }

    private fun drawView() {
        if (operation != null) {
            when (operation) {
                OPERATION_REQUISITES -> {
                    percentForAsia = requireArguments().getDouble("percent")
                    percentForOthers = requireArguments().getDouble("percent_other")
                    if (requireArguments().getString("CURRENCY") != null) {
                        currency = requireArguments().getString("CURRENCY").toString()
                    }
                }
            }
        }
        for (paymentParams in paymentParamsArrayList) {
            val linearLayout = LinearLayout(context)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.VERTICAL

            val valueView = drawParamValueView()

            when (paymentParams.code) {
                "FIO" -> {
                    val fio = paymentParams.def_value
                    val cleanText = fio.replace("(\\p{Ll})(\\p{Lu})".toRegex(), "$1 $2")
                    valueView.text = cleanText
                }

                "AMOUNT", "RESULT_AMOUNT" -> {
                    if (paymentParams.def_value.isNotEmpty()) {
                        amount = when (paymentParams.payment_detail_code) {
                            else -> paymentParams.def_value.replace(" ", "").toDouble() / 100
                        }
                    }
                    valueView.text = formatAmount(
                        if (amount.toString().trim().isNotEmpty()) amount.toString() else "0"
                    ) + if (currency == "000") " ${getString(R.string.sum_text)}" else " $"
                }

                else -> {
                    valueView.text = if (paymentParams.def_value.trim().isNotEmpty()) {
                        paymentParams.def_value.replace(" ", "")
                    } else ""
                }
            }
            if (paymentParams.payment_detail_code == "LOAN_REPAYMENT") {
                if (paymentParams.def_value.isNotEmpty() && NumberUtils.isParsable(paymentParams.def_value)) {
                    valueView.text = formatAmount(Format.formatAmountFromTiynToInteger(paymentParams.def_value)) + " UZS"
                }
            }
            if (paymentParams.code == "SELECT") {
                val reference = Gson().fromJson(paymentParams.hint, DefaultReferenceResponse::class.java)
                var defaultModel = AllServiceLists()
                reference.options.forEach {
                    if (it.code == paymentParams.def_value) {
                        defaultModel = it
                    }
                }
                addViews("ГНИ", defaultModel.name.toString())
                addViews("Баланс", defaultModel.price.toString())
            }
            if (paymentParams.code == "AAB_COMMISSION") {
                percentForAsia = paymentParams.def_value.toDouble()
                return
            }
            if (paymentParams.is_required.equals("N") && paymentParams.def_value.trim().isEmpty()) {
                linearLayout.visibility = View.GONE
            }
            if (paymentParams.is_visible.equals("Y") && paymentParams.def_value.isNotEmpty()) {
                linearLayout.addView(drawParamNameView(paymentParams))
                linearLayout.addView(valueView)
                binding.content.addView(linearLayout)
            }
        }
    }

    private fun drawParamNameView(paymentParams: PaymentParams): TextViewRegular {
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        linearLayoutParams.setMargins(0, dpToPx(12), 0, 0)
        val myTextView = TextViewRegular(requireContext())
        myTextView.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.brandBlueColor_40
            )
        )
        myTextView.textSize = 14f
        myTextView.layoutParams = linearLayoutParams
        myTextView.text = if (paymentParams.name!!.trim().isNotEmpty()) paymentParams.name else ""
        return myTextView
    }

    private fun drawParamValueView(): TextViewMedium {
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayoutParams.setMargins(0, dpToPx(4), 0, 0)
        val valueView = TextViewMedium(requireContext())
        valueView.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor))
        valueView.layoutParams = linearLayoutParams
        valueView.textSize = 16F
        return valueView
    }

    private fun addViews(name: String, value: String) {
        val mainBlockBinding = ItemConfirmPaymentBinding.inflate(
            LayoutInflater.from(requireContext()), requireView().parent as ViewGroup, false
        )
        mainBlockBinding.textName.text = name
        mainBlockBinding.textValue.text = value
        binding.content.addView(mainBlockBinding.root)
    }

    private fun setCommission() {
        if (operation == OPERATION_REQUISITES) {
            binding.commission.text = "${formatAmount(percentForAsia.toString())} %"
            totalAmount = amount * percentForAsia / 100 + amount
        } else {
            binding.commission.text = "${formatAmount(percentForAsia.toString())} %"
            totalAmount = amount * percentForAsia / 100 + amount
        }
        binding.total.text = formatAmount(
            if (totalAmount.toString().trim().isNotEmpty()) totalAmount.toString() else "0"
        ) + if (currency == "000") " ${getString(R.string.sum_text)}" else "$"
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    private fun checkForSmsBeforePayment() {
        if (!this.checkForPaymentSms(
                card = senderCard!!, paymentService = paymentService, amount = params["AMOUNT"].toString()
            )
        ) {
            createPayment()
        }
    }

    private fun createPayment() {
        try {
            binding.continueButton.setProgress(true)
            val command = if (senderCard!!.object_type == WALLET) "purse&${paymentService?.payment_type.toString().lowercase(Locale.getDefault()).trim()}" else "card&${
                paymentService?.payment_type.toString().lowercase(Locale.getDefault()).trim()
            }"
            val model = CreatePaymentRequest(
                service_id = paymentService?.service_id.toString(),
                params = params,
                from_object_id = senderCard?.object_id.toString(),
                amount = params["AMOUNT"].toString(),
                command = command,
                string_line = stringLine,
                i_request_id = Const.request_id
            )
            val path = if (paymentService?.pay_request_method.isNullOrEmpty()) "CREATE_PAYMENT" else paymentService?.pay_request_method.toString()
            paymentViewModel.createPaymentRequest(getClientToken(), model, path).observe(viewLifecycleOwner) {
                it?.let {
                    binding.continueButton.setProgress(false)
                    when (it.status) {
                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }

                        Status.SUCCESS -> {
                            val bundle = bundleOf(
                                SuccessPaymentFragment.CONFIRM_PAYMENT_OPERATION to SuccessPaymentFragment.PAYMENT,
                                CONFIRM_PAYMENT_OPERATION to operation,
                                Const.OPERATION_AMOUNT to Format.formatMoney(amount.toString()) + if (currency == "000") " ${
                                    getString(
                                        R.string.sum_text
                                    )
                                }" else " USD",
                                Const.OPERATION_CURRENCY to if (currency == "000") " ${getString(R.string.sum_text)}" else " USD",
                                Const.SENDER_CARD to senderCard,
                                Const.PAYMENT_SERVICE to Gson().toJson(paymentService),
                                "EXTRA_PAYMENT_PARAMS" to Gson().toJson(paymentParamsArrayList),
                                SuccessPaymentFragment.PAYMENT_KEY_VALUES to requireArguments().serializable<HashMap<String, String>>(PaymentSecondStepFragment.PAYMENT_KEY_VALUES)
                            )
                            bundle.putString("transactId", it.data?.request_id!!.toString())
                            if (operation != null) {
                                when (operation) {
                                    OPERATION_REQUISITES -> {
                                        gotoWithSlide(R.id.successPaymentFragment, bundle)
                                    }

                                    OPERATION_PAYMENT -> {
                                        gotoWithSlide(R.id.successPaymentFragment, bundle)
                                    }

                                    OPERATION_PAYMENT_SECOND -> {
                                        gotoWithSlide(R.id.successPaymentFragment, bundle)
                                    }
                                }
                            } else {
                                gotoWithSlide(R.id.successPaymentFragment, bundle)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            showSnackbar(getString(uz.fido.utils.R.string.unkknown_error))
            recordException(e, ::createPayment.name)
        }
    }

    private fun fingerPrintAuth() {
        if (hasBiometrics()) {
            fingerPrintDialogBuilder(getBiometricPrompt())
        }
    }

    private fun getBiometricPrompt(): BiometricPrompt {
        return BiometricPrompt(requireActivity(), Executors.newSingleThreadExecutor(), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int, errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    (activity as MainActivity).runOnUiThread {
                        setFingerPrintState(false)
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                (activity as MainActivity).runOnUiThread {
                    setFingerPrintState(true)
                }
            }
        })
    }

    private fun fingerPrintDialogBuilder(biometricPrompt: BiometricPrompt) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.confirm_payment)).setDescription(getString(R.string.for_confirm_payment_touch_sensor))
            .setNegativeButtonText(getString(R.string.cancel)).build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun setFingerPrintState(state: Boolean) {
        if (state) checkForSmsBeforePayment()
    }

    private fun hasBiometrics(): Boolean {
        return when (BiometricManager.from(requireContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                false
            }

            else -> {
                true
            }
        }
    }
}