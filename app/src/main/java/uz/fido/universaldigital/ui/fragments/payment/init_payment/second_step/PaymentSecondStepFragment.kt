package uz.fido.universaldigital.ui.fragments.payment.init_payment.second_step

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.math.NumberUtils
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.DefaultReferenceResponse
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentReference
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentResponse
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentPaymentSecondStepBinding
import uz.fido.universaldigital.databinding.ViewPaymentAmountBinding
import uz.fido.universaldigital.databinding.ViewPaymentNavigationBinding
import uz.fido.universaldigital.databinding.ViewPaymentSecondStepDetailsBinding
import uz.fido.universaldigital.databinding.ViewPaymentSimpleInputNewBinding
import uz.fido.universaldigital.ui.dialogs.ReferenceDialog
import uz.fido.universaldigital.ui.fragments.payment.MenuPaymentViewModel
import uz.fido.universaldigital.ui.fragments.payment.abc_confirm.ConfirmPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.CreateAutoPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.init_payment.utility.ViewElectricityCalculator
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.delayOnLifecycle
import uz.fido.universaldigital.ui.utils.extensions.hideSoftKeyboard
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.SQLException
import java.util.Locale

class PaymentSecondStepFragment :
    BaseSimpleFragment<FragmentPaymentSecondStepBinding>(FragmentPaymentSecondStepBinding::inflate),
    BaseInterface {

    private lateinit var paymentHashMap: HashMap<String, String>
    private val menuPaymentViewModel: MenuPaymentViewModel by activityViewModels()

    private var paymentParamsArrayList = ArrayList<PaymentParams>()
    private var paymentService: PaymentService? = null
    private var mobileDBHelper: DatabaseHelper? = null
    private var accountId = ""

    private var navigationList: ArrayList<AllServiceLists>? = null
    private var paymentParamsForSelect: PaymentParams? = null
    private var refParamList = ArrayList<PaymentReference>()
    private var referenceDialog: ReferenceDialog? = null
    private var editTextTag: String? = null
    private var divisionTag: String? = null
    private var regionCode: String? = null
    private var regionSelected = false

    private var paymentAmount = 0.0
    private var minAmount = 0.0
    private var maxAmount = 0.0

    private var editTextList: ArrayList<MaskEditText> = ArrayList()
    private var amountEditText: TextInputEditText? = null

    private var templateItem: Template? = null
    private var templateName: String = ""
    private var operation: Int? = 0

    private var templateKeyValues: HashMap<String, String>? = null
    private var keyValueList = HashMap<String, String>()
    private var accountBalance: String? = null
    private var homeName: String? = null
    private var homeId: String? = null

    private var paymentParamsForIndicatorFrom: PaymentParams? = null
    private var paymentParamsForIndicatorTo: PaymentParams? = null
    private var range = 0L

    companion object {
        const val PAYMENT_PARAMS_LIST = "params_list"
        const val PAYMENT_TEMPLATE_NAME = "template_name"
        const val PAYMENT_HOME_ID = "home_id"
        const val PAYMENT_HOME_NAME = "home_name"
        const val PAYMENT_KEY_VALUES = "key_values"
        const val PAYMENT_TEMPLATE_KEY_VALUES = "template_key_values"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mobileDBHelper = DatabaseHelper(requireContext())
        arguments?.let {
            paymentParamsArrayList =
                it.serializable<ArrayList<PaymentParams>>(PAYMENT_PARAMS_LIST) as ArrayList<PaymentParams>
            paymentService = it.serializable(PaymentFragment.PAYMENT_SERVICE) as PaymentService?
            operation = it.getInt(PaymentFragment.PAYMENT_OPERATION)
            templateName = it.getString(PAYMENT_TEMPLATE_NAME).toString()
            templateItem = it.serializable(PaymentFragment.PAYMENT_TEMPLATE_ITEM) as Template?
            templateKeyValues = it.serializable(PAYMENT_KEY_VALUES) as HashMap<String, String>?
            homeId = it.getString(PAYMENT_HOME_ID)
            homeName = it.getString(PAYMENT_HOME_NAME)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setTitle(paymentService?.nameIndex.toString())
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            gotoNext()
        }
        drawViews()
        if (homeId != null) {
            binding.btnContinue.isEnabled(checkForButton())
        }
    }

    private fun drawViews() {
        paymentHashMap = HashMap()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            fetchMinMaxAmount()
        }
        binding.mainLayout.removeAllViews()
        binding.infoLayout.removeAllViews()
        editTextList = ArrayList()
        for (i in paymentParamsArrayList.indices) {
            Logger.writeLog(
                "\n${paymentParamsArrayList[i].code}\n${paymentParamsArrayList[i].is_read_only}\n${paymentParamsArrayList[i].is_required}\n" +
                        "${paymentParamsArrayList[i].param_type}"
            )
            if (paymentParamsArrayList[i].is_read_only != "Y" && paymentParamsArrayList[i].is_required == "Y") {
                if (paymentParamsArrayList[i].code == "AMOUNT") {
                    drawAmountView(paymentParamsArrayList[i])
                } else {
                    if (paymentParamsArrayList[i].param_type == "S") {
                        drawViewsForNavigation(paymentParamsArrayList[i])
                    } else {
                        drawEditText(paymentParamsArrayList[i], i)
                    }
                }
            } else {
                if (paymentParamsArrayList[i].regular_exp_mask == "curr_balance") {
                    binding.layoutBalance.visibility = View.VISIBLE
                    if (binding.balance.text.toString().isEmpty() &&
                        paymentParamsArrayList[i].def_value != "null"
                    ) {
                        accountBalance =
                            paymentParamsArrayList[i].def_value.replace(",", " ") + " UZS"
                        paymentHashMap[paymentParamsArrayList[i].code] =
                            paymentParamsArrayList[i].def_value
//                        if (paymentParamsArrayList[i].def_value.startsWith("-")) {
//                            binding.balance.setTextColor(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.brandRedColor
//                                )
//                            )
//                        }
                    }
                    binding.balance.text = accountBalance.toString()
                } else {
                    drawMainBlockViews(paymentParamsArrayList[i], i)
                }
            }
        }

        drawCalculatorView()
        binding.btnContinue.isEnabled(checkForButton())
    }

    private var calculatorTariffAmount = BigDecimal(0)
    private var calculatorView: ViewElectricityCalculator? = null

    private fun drawCalculatorView() {
        if (operation != null && operation == PaymentFragment.PAYMENT_OPERATION_PAYMENT) {
            if (paymentService?.payment_detail_code == "PAYNET_ELECTR" || paymentService?.payment_detail_code == "PAYNET_GAZ" ||
                paymentService?.payment_detail_code == "PAYNET_SUV" || paymentService?.payment_detail_code == "MUNIS_0102"
            ) {
                Log.d("TAG", "drawCalculatorView:$operation ")
                Log.d("TAG", "drawCalculatorView:${paymentService?.payment_detail_code} ")
                calculatorView = ViewElectricityCalculator(requireContext())
                calculatorView?.tag = paymentService?.payment_detail_code
                calculatorView?.setValues(childFragmentManager, object : BaseInterface {
                    override fun sendCalculatorRange(range: Long, from: String, to: String) {
                        this@PaymentSecondStepFragment.range = range
                        paymentHashMap["COUNT_BEFORE"] = from
                        paymentHashMap["COUNT_AFTER"] = to
                        calculateRange()
                    }
                })
                if (paymentParamsForIndicatorFrom != null && paymentParamsForIndicatorTo != null) {
                    calculatorView?.setCounterIndicators(
                        paymentParamsForIndicatorFrom!!,
                        paymentParamsForIndicatorTo!!
                    )
                }
                binding.mainLayout.addView(
                    calculatorView,
                    if (paymentService?.payment_detail_code == "MUNIS_0102") 0 else 1
                )
            }
        }
    }

    private fun checkForCounterIndicator() {
        if (amountEditText?.hasFocus() == true) {
            if (calculatorTariffAmount != BigDecimal.ZERO) {
                val indicator = paymentAmount.toBigDecimal()
                    .divide(calculatorTariffAmount, 0, RoundingMode.HALF_UP)
                if (indicator > BigDecimal(1) || indicator.equals(BigDecimal.ONE)) {
                    calculatorView?.setIndicatorMax(indicator)
                } else {
                    calculatorView?.setIndicatorMax(BigDecimal.ZERO)
                }
            }
        }
    }

    private fun calculateRange() {
        if (calculatorTariffAmount != BigDecimal(0)) {
            amountEditText?.setText(
                range.toBigDecimal().multiply(calculatorTariffAmount).toString()
            )
            val transferAmount = amountEditText?.text.toString()
            paymentHashMap["AMOUNT"] = transferAmount
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawAmountView(paymentParams: PaymentParams) {
        val amountBinding = ViewPaymentAmountBinding.inflate(
            LayoutInflater.from(requireContext()),
            requireView().parent as ViewGroup,
            false
        )
        val hint = if (paymentParams.hint.toString()
                .isNotEmpty()
        ) paymentParams.hint else paymentParams.name
        amountBinding.textInputLayout.hint = hint
        amountEditText = amountBinding.editTextAmount
        if (paymentService?.group_code != "7") {
            if (paymentService?.payment_type?.lowercase(Locale.getDefault()) == "paynet") {
                amountBinding.editTextAmount.keyListener =
                    DigitsKeyListener.getInstance("0123456789 ")
            }
        }
        if (paymentParams.def_value.isNotEmpty()) {
            amountEditText?.setText(paymentParams.def_value)
            paymentHashMap[paymentParams.code] = paymentParams.def_value
        }
        amountEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    amountEditText?.setSelection(amountEditText!!.length())
                    val transferAmount = amountEditText!!.text.toString()
                    paymentHashMap["AMOUNT"] = transferAmount
                    getAmount(transferAmount)
                    checkForAmount(paymentAmount)
                    checkForCounterIndicator()
                } else {
                    amountBinding.textInputLayout.error = null
                    amountBinding.textInputLayout.isErrorEnabled = false
                    paymentAmount = 0.0
                }
            }
        })
        if (operation != null && operation == PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE) {
            if (templateItem?.amount != null) {
                amountEditText!!.setText(Format.formatAmountFromTiynToInteger(templateItem?.amount!!))
            }
        }
        amountBinding.textViewMinAmount.text =
            getString(R.string.min_amount) + " " + Format().formatAmount(minAmount.toString()) + " ${
                getString(
                    R.string.sum_text
                )
            }"
        if (homeId != null) {
            paymentHashMap["AMOUNT"] = "0"
            amountBinding.root.visibility = View.GONE
        }
        binding.mainLayout.addView(amountBinding.root)
    }

    private fun drawEditText(paymentParams: PaymentParams, index: Int) {
        val mainBlockBinding = ViewPaymentSimpleInputNewBinding.inflate(
            LayoutInflater.from(requireContext()),
            requireView().parent as ViewGroup,
            false
        )
        val hint = if (paymentParams.hint.toString()
                .isNotEmpty()
        ) paymentParams.hint else paymentParams.name
        mainBlockBinding.textInputLayout.hint = hint
        Log.d("drawEditText", ": $hint")
        val editText = mainBlockBinding.editTextMainBlock
        editTextList.add(editText)
        editText.imeOptions =
            if (index == paymentParamsArrayList.size - 1) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
        if (paymentParams.param_length.toString().isNotEmpty()) {
            editText.setMaxLength(paymentParams.param_length!!.toInt())
        }
        if (paymentParams.is_read_only.equals("Y")) {
            editText.isEnabled = false
            if (paymentParams.is_required.equals("N") && paymentParams.def_value.trim().isEmpty()) {
                mainBlockBinding.root.visibility = View.GONE
                editText.visibility = View.GONE
            }
        }
        if (paymentParams.param_type.equals("T"))
            editText.inputType = InputType.TYPE_CLASS_TEXT
        if (paymentParams.param_type.equals("N"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        if (paymentParams.param_type.equals("F"))
            editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (paymentParams.param_type.equals("I"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER

        if (paymentParams.def_value.isNotEmpty()) {
            editText.setText(paymentParams.def_value)
            if (paymentParams.payment_detail_code == "LOAN_REPAYMENT") {
                if (NumberUtils.isParsable(paymentParams.def_value)) {
                    editText.setText(
                        Format.formatAmount(
                            Format.formatAmountFromTiynToInteger(
                                paymentParams.def_value
                            )
                        ) + " UZS"
                    )
                }
            }
            paymentHashMap[paymentParams.code] = paymentParams.def_value
        }
        if (!paymentParams.is_visible.equals("Y")) {
            mainBlockBinding.root.visibility = View.GONE
            editText.visibility = View.GONE
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    binding.btnContinue.isEnabled(checkForButton())
                } else {
                    paymentHashMap[paymentParams.code] = s.toString()
                    binding.btnContinue.isEnabled(checkForButton())
                }
            }
        })
        binding.mainLayout.addView(mainBlockBinding.root)
    }

    /*NAVIGATION VIEW*/
    private fun drawViewsForNavigation(paymentParams: PaymentParams) {
        val viewPaymentNavigationBinding =
            ViewPaymentNavigationBinding.inflate(
                LayoutInflater.from(requireContext()),
                requireView().parent as ViewGroup,
                false
            )
        val myEditText = viewPaymentNavigationBinding.editTextNavigation
        val paymentCode = paymentParams.code
        myEditText.tag = paymentCode

        if (paymentParams.is_read_only == "Y") {
            myEditText.isEnabled = false
        } else {
            editTextList.add(myEditText)
        }
        if (paymentCode == "COUNT_BEFORE") {
            paymentParamsForIndicatorFrom = paymentParams
        }
        if (paymentCode == "COUNT_AFTER") {
            paymentParamsForIndicatorTo = paymentParams
        }
        myEditText.hint = paymentParams.hint!!.ifEmpty { paymentParams.name!! }
        if (paymentParams.def_value.isNotEmpty()) {
            if (paymentCode == "SELECT") {
                val defValue =
                    Gson().fromJson(paymentParams.def_value, DefaultReferenceResponse::class.java)
                if (defValue.options.size == 1) {
                    paymentHashMap[paymentCode] = defValue.options[0].code.toString()
                    myEditText.setText(defValue.options[0].name.toString())
                    myEditText.isEnabled = false
                    val defaultModel = defValue.options[0]
                    addViews("ГНИ", defaultModel.name.toString(), paymentParams)
                    addViews("Баланс", defaultModel.price.toString(), paymentParams)
                }
            } else {
                try {
                    val defValue = paymentParams.def_value
                    refParamList =
                        mobileDBHelper!!.getRefParamList(paymentParams.ref_code.toString(), null)
                    paymentHashMap[paymentCode] = defValue
                    for (i in refParamList.indices) {
                        if (defValue == refParamList[i].code!!) {
                            myEditText.setText(refParamList[i].name!!)
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }
        if (paymentCode == "DIVISIONS" || paymentCode == "CODE_GP" || paymentCode == "GNI" || paymentCode == "SOATO") {
            myEditText.setOnClickListener(divisionListener)
        } else {
            myEditText.setOnClickListener {
                hideSoftKeyboard()
                editTextTag = myEditText.tag.toString()
                regionSelected = editTextTag == "REGIONS"
                refParamList = ArrayList()
                if (paymentCode == "SELECT") {
                    navigationList = ArrayList()
                    val defValue = Gson().fromJson(
                        paymentParams.def_value,
                        DefaultReferenceResponse::class.java
                    )
                    paymentParamsForSelect = paymentParams
                    navigationList!!.addAll(defValue.options)
                    referenceDialog = ReferenceDialog(this, navigationList!!, "SELECT")
                } else {
                    try {
                        refParamList =
                            mobileDBHelper!!.getRefParamList(paymentParams.ref_code!!, null)
                        navigationList = ArrayList()
                        for (i in refParamList.indices) {
                            val allServiceLists = AllServiceLists()
                            allServiceLists.name = refParamList[i].name!!
                            allServiceLists.code = refParamList[i].code!!
                            allServiceLists.addition = refParamList[i].flag!!
                            navigationList!!.add(allServiceLists)
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                    referenceDialog = ReferenceDialog(this, navigationList!!, editTextTag!!)
                }
                referenceDialog?.show(childFragmentManager, "references_dialog")
            }
            if (paymentCode == "TARIF_TYPE") {
                editTextTag = "TARIF_TYPE"
                refParamList =
                    mobileDBHelper!!.getRefParamList(paymentParams.ref_code.toString(), null)
                navigationList = ArrayList()
                for (i in refParamList.indices) {
                    val allServiceLists = AllServiceLists()
                    allServiceLists.name = refParamList[i].name!!
                    allServiceLists.code = refParamList[i].code!!
                    allServiceLists.addition = refParamList[i].flag!!
                    navigationList!!.add(allServiceLists)
                }
                if (navigationList != null) {
                    if (navigationList?.size == 1) {
                        viewPaymentNavigationBinding.root.visibility = View.GONE
                        myEditText.visibility = View.GONE
                    }
                    binding.infoLayout.delayOnLifecycle(200, Dispatchers.Main) {
                        if (!navigationList.isNullOrEmpty())
                            setToEditText(navigationList!![0], "TARIF_TYPE")
                    }
                }
            }
            if (paymentCode == "EARLY_CLOSURE") {
                editTextTag = "EARLY_CLOSURE"
                refParamList =
                    mobileDBHelper!!.getRefParamList(paymentParams.ref_code.toString(), null)
                navigationList = ArrayList()
                for (i in refParamList.indices) {
                    val allServiceLists = AllServiceLists()
                    allServiceLists.name = refParamList[i].name!!
                    allServiceLists.code = refParamList[i].code!!
                    allServiceLists.addition = refParamList[i].flag!!
                    navigationList!!.add(allServiceLists)
                }
                if (!navigationList.isNullOrEmpty()) {
                    viewPaymentNavigationBinding.root.findViewWithTag<MaskEditText>("EARLY_CLOSURE")
                        .setText(navigationList!![0].name)
                }
            }
        }
        if (!paymentParams.is_visible.equals("Y")) {
            viewPaymentNavigationBinding.root.visibility = View.GONE
            myEditText.visibility = View.GONE
        }
        binding.mainLayout.addView(viewPaymentNavigationBinding.root)
    }

    private val divisionListener = View.OnClickListener { view ->
        if (regionCode != null) {
            var v: View? = view.findViewWithTag("DIVISIONS")
            if (v == null) {
                v = view.findViewWithTag("CODE_GP")
            }
            if (v == null) {
                v = view.findViewWithTag("SOATO")
            }
            if (v == null) {
                v = view.findViewWithTag("GNI")
            }
            refParamList = ArrayList()
            editTextTag = v!!.tag.toString()
            divisionTag = editTextTag
            Log.d("===P", regionCode!!)
            try {
                refParamList = when (editTextTag) {
                    "CODE_GP" -> mobileDBHelper!!.getRefParamList("318", regionCode)
                    "GNI" -> mobileDBHelper!!.getRefParamList("319", regionCode)
                    "SOATO" -> mobileDBHelper!!.getRefParamList("317", regionCode)
                    else -> mobileDBHelper!!.getRefParamList("D0001", regionCode)
                }
                navigationList = ArrayList()
                for (i in refParamList.indices) {
                    val allServiceLists = AllServiceLists()
                    allServiceLists.name = refParamList[i].name!!
                    allServiceLists.code = refParamList[i].code!!
                    navigationList!!.add(allServiceLists)
                }
                referenceDialog = ReferenceDialog(this, navigationList!!, editTextTag!!)
                referenceDialog?.show(childFragmentManager, "reference_dialog")
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    override fun setToEditText(allServiceLists: AllServiceLists, tag: String) {
        if (tag == "SELECT") {
            if (paymentParamsForSelect != null) {
                val defValue = Gson().fromJson(
                    paymentParamsForSelect?.def_value,
                    DefaultReferenceResponse::class.java
                )
                defValue.options.forEach {
                    if (it.code == allServiceLists.code) {
                        addViews("ГНИ", it.name.toString(), paymentParamsForSelect!!)
                        addViews("Баланс", it.price.toString(), paymentParamsForSelect!!)
                    }
                }
            }
        }
        if (tag == "TARIF_TYPE") {
            binding.infoLayout.findViewWithTag<TextViewMedium>("TARIF_PRICE").text =
                allServiceLists.code
            paymentHashMap["TARIF_PRICE"] = allServiceLists.code.toString()
            calculatorTariffAmount = allServiceLists.code!!.toBigDecimal()
            when (paymentService?.payment_detail_code) {
                "PAYNET_ELECTR" -> {
                    calculateRange()
                }

                "PAYNET_GAZ", "PAYNET_SUV" -> {
                    if (allServiceLists.addition == "P") {
                        amountEditText?.setText("0")
                    } else {
                        calculateRange()
                    }
                    Log.d("TAG", "setToEditText: ${paymentService?.payment_detail_code}")
                    Log.d("TAG", "setToEditText: ${allServiceLists.addition}")

                    if (binding.mainLayout.findViewWithTag<ViewElectricityCalculator>(paymentService?.payment_detail_code) != null && homeId == null)
                        binding.mainLayout.findViewWithTag<ViewElectricityCalculator>(
                            paymentService?.payment_detail_code
                        ).visibility =
                            if (allServiceLists.addition == "P") View.GONE else View.VISIBLE
                }
            }
        }
        (requireView().findViewWithTag<View>(editTextTag) as MaskEditText).setText(allServiceLists.name)
        paymentHashMap[editTextTag!!] = allServiceLists.code!!
        if (regionSelected) {
            regionSelected = false
            regionCode = allServiceLists.code!!
            regionCode = regionCode!!.substring(0, regionCode!!.length.coerceAtMost(2))
            if (divisionTag != null)
                (requireView().findViewWithTag<View>(divisionTag) as MaskEditText).setText("")
        }
        referenceDialog?.dismiss()
        binding.btnContinue.isEnabled(checkForButton())
    }

    @SuppressLint("SetTextI18n")
    private fun drawMainBlockViews(paymentParams: PaymentParams, position: Int) {
        val mainBlockBinding =
            ViewPaymentSecondStepDetailsBinding.inflate(
                LayoutInflater.from(requireContext()),
                requireView().parent as ViewGroup,
                false
            )
        mainBlockBinding.textViewName.text =
            if (paymentParams.hint.isNullOrEmpty()) paymentParams.name else paymentParams.hint
        mainBlockBinding.textViewValue.text = paymentParams.def_value
        mainBlockBinding.textViewValue.tag = paymentParams.code
        if (paymentParams.is_visible != "Y") {
            mainBlockBinding.root.visibility = View.GONE
        }
        if (paymentParams.param_type == "S") {
            if (paymentParams.code == "DIVISIONS" || paymentParams.code == "CODE_GP" || paymentParams.code == "GNI" || paymentParams.code == "SOATO") {
                val refParamList = mobileDBHelper!!.getRefParamList("317", null)
                refParamList.forEach {
                    Log.d(
                        "==",
                        "${it.name.toString()} ${it.code.toString()} ${it.ref_code.toString()}"
                    )
                }
            }
        }
        if (paymentParams.payment_detail_code == "LOAN_REPAYMENT") {
            if (NumberUtils.isParsable(paymentParams.def_value)) {
                mainBlockBinding.textViewValue.text =
                    Format.formatAmount(Format.formatAmountFromTiynToInteger(paymentParams.def_value)) + " UZS"
            }
        }
        if (paymentParams.code == "AMOUNT") {
            mainBlockBinding.textViewValue.text =
                Format.formatAmount(Format.formatAmountFromTiynToInteger(paymentParams.def_value)) + " UZS"
        }

        if (paymentParams.is_required.equals("N") && paymentParams.def_value.trim().isEmpty()) {
            mainBlockBinding.root.visibility = View.GONE
        }
//        if (position == paymentParamsArrayList.size - 1) {
//            mainBlockBinding.viewLine.visibility = View.INVISIBLE
//        }
        paymentHashMap[paymentParams.code] = paymentParams.def_value
        if (paymentParams.code != "TARIF_PRICE" && paymentParams.def_value.isEmpty()) {
            return
        }
        binding.infoLayout.addView(mainBlockBinding.root)
    }

    private fun addViews(name: String, value: String, paymentParams: PaymentParams) {
        val mainBlockBinding =
            ViewPaymentSecondStepDetailsBinding.inflate(
                LayoutInflater.from(requireContext()),
                requireView().parent as ViewGroup,
                false
            )

        val textViewName =
            binding.infoLayout.findViewWithTag<TextViewRegular>(paymentParamsForSelect?.code + "_SELECT_name${name}")
                ?: null
        val textViewValue =
            binding.infoLayout.findViewWithTag<TextViewMedium>(paymentParamsForSelect?.code + "_SELECT_value${name}")
                ?: null
        if (textViewName != null) {
            textViewName.text = name
            textViewValue?.text = value
        } else {
            mainBlockBinding.textViewName.text = name
            mainBlockBinding.textViewValue.text = value
            mainBlockBinding.textViewName.tag = paymentParams.code + "_SELECT_name${name}"
            mainBlockBinding.textViewValue.tag = paymentParams.code + "_SELECT_value${name}"

            binding.infoLayout.addView(mainBlockBinding.root)
        }
    }

    private fun fetchMinMaxAmount() {
        var min = paymentService!!.min_amount
        var max = paymentService!!.max_amount
        if (min != null && min.contains(".")) {
            min = min.substring(0, min.indexOf("."))
        }
        if (max != null && max.contains(".")) {
            max = max.substring(0, max.indexOf("."))
        }
        minAmount = min!!.toDouble()
        maxAmount = max!!.toDouble()
    }

    private fun getAmount(transferAmount: String) {
       try {
           paymentAmount =
               if (transferAmount.isEmpty() || transferAmount == "" || transferAmount[0] == '\u0000' || transferAmount == "." || transferAmount == ",") {
                   0.0
               } else {
                   java.lang.Double.parseDouble(Format.noSpace(transferAmount))
               }
       }catch (e:Exception){
           paymentAmount=0.0
           binding.btnContinue.isEnabled(false)
       }
    }

    private fun checkForAmount(amount: Double) {
        if (minAmount > amount || amount > maxAmount) {
            binding.btnContinue.isEnabled(false)
        } else {
            binding.btnContinue.isEnabled(checkForButton())
        }
    }

    private fun checkForButton(): Boolean {
        Log.d("TAG", "checkForButton: $homeId")
        for (maskEditText in editTextList) {
            if (maskEditText.visibility == View.VISIBLE) {
                if (maskEditText.rawText.isEmpty()) {
                    return false
                }
                if (maskEditText.error != null) {
                    return false
                }
            }
        }
        if (homeId == null) {
            if (amountEditText?.text.toString().isEmpty()) {
                return false
            }
            if (amountEditText?.text.toString().startsWith("0")) {
                return false
            }
        }
        return true
    }

    private fun preparePayment(
        service_id: String,
        curr_level_pos: String,
        payment_detail_code: String,
        params: HashMap<String, String>,
        payment_type: String
    ) {
        val model = PreparePaymentRequest(
            service_id = service_id,
            curr_level_position = curr_level_pos,
            payment_detail_code = payment_detail_code,
            params = params,
            command = payment_type.lowercase(Locale.getDefault()).trim()
        )
        binding.btnContinue.setProgress(true)
        menuPaymentViewModel.preparePaymentRequest(getClientToken(), model)
            .observe(viewLifecycleOwner) {
                it?.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            paymentParamsArrayList.forEach { paymentParams ->
                                if (paymentParams.code == "AMOUNT") {
                                    if (amountEditText != null) {
                                        paymentParams.def_value = amountEditText!!.text.toString()
                                    }
                                } else {
                                    if (paymentParams.code != "SELECT") {
                                        paymentParams.def_value =
                                            paymentHashMap[paymentParams.code].toString()
                                    }
                                }
                            }
                            val response = it.data as PreparePaymentResponse
                            Const.request_id = response.request_id.toString()
                            openConfirmPayment(response.service_details)
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
    }

    private fun gotoNext() {
        keyValueList = HashMap<String, String>()
        templateKeyValues =
            requireArguments().getSerializable(PAYMENT_KEY_VALUES) as HashMap<String, String>

        var loanId = ""
        for (param in paymentParamsArrayList) {
            if (param.code == "ABONENT_ID" || param.code == "PAYER" || param.code == "ABONENTLIC"
                || param.code == "CUSTOMER" || param.code == "ABONENTID" || param.code == "CUSTOMER"
                || param.code == "PROVIDER_ACC"
            ) {
                if (accountId.isEmpty()) {
                    accountId = paymentHashMap[param.code]!!
                }
            }
            if (param.code == "AMOUNT") {
                param.def_value = when (param.payment_detail_code) {
                    "PAYNET_8016" -> paymentHashMap[param.code]!!
                    else -> Format.formatAmountToTiyn(paymentHashMap[param.code])

                }
                paymentHashMap[param.code] = param.def_value
            }
            if (param.code == "LOANS_ID") {
                loanId = param.def_value
            }
            if (param.code == "PHONE_NUMBER") {
                var phone = paymentHashMap[param.code]!!
                phone = phone.replace(" ", "")
                paymentHashMap[param.code] = phone
            }
            if (!param.is_required.equals("N") && param.code.isNotEmpty()) {
                try {
                    keyValueList[param.code] = paymentHashMap[param.code]!!
                } catch (e: Exception) {
                    keyValueList[param.code] = ""
                    templateKeyValues!![param.code] = ""
                }
            }
            if (param.code.isNotEmpty() && paymentHashMap[param.code] != null && templateKeyValues != null) {
                if (templateKeyValues!![param.code] == null) {
                    templateKeyValues!![param.code] = paymentHashMap[param.code]!!
                }
            }
        }
        val levelPosition1 = "2"
        if (paymentService!!.service_id == -2 && loanId.isNotEmpty() && levelPosition1 == "2") {
            keyValueList["LOANS_ID"] = loanId
        }
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var vi = requireActivity().currentFocus
        if (vi == null) {
            vi = View(activity)
        }
        imm.hideSoftInputFromWindow(vi.windowToken, 0)
//        if (checkForInternet()) {
        preparePayment(
            paymentService!!.service_id.toString(),
            levelPosition1,
            paymentService!!.payment_detail_code.toString(),
            keyValueList,
            paymentService!!.payment_type.toString()
        )
//        }
    }

    private fun openConfirmPayment(serviceDetails: ArrayList<PaymentParams>) {
        when (operation) {
            PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE, PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE -> {
                saveTemplate()
            }

            PaymentFragment.PAYMENT_OPERATION_AUTO_PAYMENT_ADD -> {
                val model = SaveAutoPaymentModel(
                    payment_service_id = paymentService?.service_id.toString(),
                    device_type = "A",
                    device_code = requireContext().getDeviceIds(),
                    device_name = getDeviceName(),
                    payment_details = templateKeyValues!!,
                    payment_type = paymentService?.nameIndex,
                    name = paymentService?.nameIndex
                )
                gotoWithSlide(
                    R.id.createAutoPaymentFragment,
                    bundleOf(CreateAutoPaymentFragment.SAVE_AUTO_PAYMENT_MODEL to model)
                )
            }

            else -> {
                val bundle = Bundle()
                bundle.putSerializable("list", serviceDetails)
                bundle.putSerializable("paymentService", paymentService)
                bundle.putSerializable("account", accountId)
                bundle.putSerializable(PAYMENT_KEY_VALUES, templateKeyValues)
                bundle.putSerializable(
                    ConfirmPaymentFragment.CONFIRM_PAYMENT_OPERATION,
                    ConfirmPaymentFragment.OPERATION_PAYMENT_SECOND
                )
                gotoWithSlide(R.id.confirmPaymentFragment, bundle)
            }
        }
    }

    private fun saveTemplate() {
        binding.btnContinue.setProgress(true)
        // templateKeyValues!!["AMOUNT"]="0.0"
        val model = CreateTemplateRequest(
            name = templateName,
            template_type = if (homeId != null) "H" else "D",
            service_type = paymentService!!.payment_detail_code.toString(),
            service_id = paymentService!!.service_id.toString(),
            template_group_id = if (homeId != null) homeId!! else PaymentFragment.TemplateGroups.DEFAULT_TEMPLATES.toString(),
            payment_details = templateKeyValues!!,
            template_id = if (operation == PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE) templateItem!!.template_id else null
        )
        menuPaymentViewModel.createTemplate(getClientToken(), model).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    if (homeId != null) {
                        gotoWithSlide(
                            R.id.basicSuccessFragment,
                            bundleOf(
                                Const.OPERATION to BasicSuccessFragment.SAVE_MY_HOME,
                                BasicSuccessFragment.HOME_ID to homeId,
                                BasicSuccessFragment.HOME_NAME to homeName
                            )
                        )
                    } else {
                        gotoWithSlide(
                            R.id.basicSuccessFragment, bundleOf(
                                Const.OPERATION to BasicSuccessFragment.SAVE_TEMPLATE,
                            )
                        )
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }
}