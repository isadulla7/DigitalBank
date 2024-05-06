package uz.fido.universaldigital.ui.fragments.payment.my_home.add_service

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentResponse
import uz.fido.network.domain.model.template.GetTemplateResponse
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseSinglePaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_UZS
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.sql.SQLException
import java.util.Locale

@AndroidEntryPoint
class MyHouseSinglePaymentFragment :
    BaseFragment<FragmentMyHouseSinglePaymentBinding, MyHomeViewModel>(
        FragmentMyHouseSinglePaymentBinding::inflate,
        MyHomeViewModel::class.java
    ) {

    companion object {
        const val MY_HOUSE_ITEM = "my_house"
        const val MY_HOUSE_TEMPLATE = "template"
        const val MY_HOUSE_TEMPLATE_RESPONSE = "template_response"
    }

    private lateinit var databaseHelper: DatabaseHelper
    private var paymentParamsArrayList = ArrayList<PaymentParams>()
    private var templateItem: Template? = null
    private var templateResponse: GetTemplateResponse? = null
    private var paymentService: PaymentService? = null
    private var amount = BigDecimal(0)
    var minAmount = "0"
    var maxAmount = "0"
    private var selectedCard: CardResponse? = null
    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            templateItem = it.serializable(MY_HOUSE_TEMPLATE) as Template?
            templateResponse = it.serializable(MY_HOUSE_TEMPLATE_RESPONSE) as GetTemplateResponse?
        }
        databaseHelper = DatabaseHelper(requireContext())
        paymentService = databaseHelper.getServiceByContractId(templateItem!!.service_id.toString())
        fetchMinMaxAmount()
        checkForValues(getPaymentDetails())
        initCards()
        setTemplateData()
        onclickView()
        textWatchers()
    }

    private fun textWatchers() {
        binding.etAmount.addTextChangedListener {
            if (!it.isNullOrEmpty() && selectedCard != null) {
                checkAmount(it.toString())
            } else binding.btnContinue.isEnabled(false)
        }
    }

    private fun checkAmount(it: String) {
        try {
            amount = it.replace(" ", "").toBigDecimal()
            val amount = it.replace(" ", "").toBigDecimal()
            if ((amount * BigDecimal("100")) < selectedCard?.balance.toString()
                    .toBigDecimal() && selectedCard?.state == "0" &&
                amount >= minAmount.toBigDecimal() && amount <= maxAmount.toBigDecimal()
            ) {
                binding.btnContinue.isEnabled(true)
            } else binding.btnContinue.isEnabled(false)
        } catch (e: Exception) {
            binding.btnContinue.isEnabled(false)
        }

    }

    private fun onclickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (binding.etAmount.editableText.toString().trim()
                    .isNotEmpty() && selectedCard != null
            ) {
                binding.btnContinue.setProgress(true)
                if (selectedCard!!.balance.toBigDecimal() > binding.etAmount.text.toString()
                        .replace(" ", "").toBigDecimal()
                ) preparePayment()

            }
        }
    }

    private fun preparePayment() {
        binding.btnContinue.isEnabled(true)
        val params = HashMap<String, String>()
        paymentParamsArrayList.forEach {
            if (it.is_required.equals("Y") && it.code.isNotEmpty()) {
                params[it.code] = it.def_value
            }
            if (it.code == "AMOUNT") {
                params[it.code] = Format.formatAmountToTiyn(amount.toString().replace(" ", ""))
            }
        }
        val levelPosition: String = if (paymentParamsArrayList[0].level_position.equals("-1")) {
            "2"
        } else {
            paymentParamsArrayList[0].level_position!!
        }
        val request = PreparePaymentRequest(
            service_id = templateItem?.service_id.toString(),
            payment_detail_code = templateItem?.service_group_code.toString(),
            command = paymentService!!.payment_type!!.lowercase(Locale.getDefault()).trim(),
            curr_level_position = levelPosition,
            params = params
        )
        viewModel.preparePaymentRequest(getClientToken(), request).observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled(false)
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data as PreparePaymentResponse
                    paymentParamsArrayList = response.service_details
                    if (response.level_position == "-1") {
                        Const.request_id = response.request_id.toString()
                        createPayment()
                    } else {
                        preparePayment()
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun createPayment() {
        val params = HashMap<String, String>()
        paymentParamsArrayList.forEach {
            if (it.code != "CARD_NUMBER") {
                params[it.code] = it.def_value
            }
        }
        val paymentType = paymentService!!.payment_type!!.lowercase(Locale.getDefault()).trim()
        val model = CreatePaymentRequest(
            service_id = paymentService!!.service_id.toString(),
            params,
            from_object_id = selectedCard?.object_id.toString(),
            params["AMOUNT"].toString(),
            if (selectedCard!!.object_type == "KL") "purse&${paymentType}" else "card&${paymentType}",
            i_request_id = Const.request_id
        )
        val path =
            if (paymentService!!.pay_request_method.isNullOrEmpty()) "CREATE_PAYMENT/" else paymentService!!.pay_request_method.toString()
        viewModel.createPaymentRequest(path, getClientToken(), model).observe(viewLifecycleOwner) {
            it?.let {
                binding.btnContinue.isEnabled(false)
                when (it.status) {
                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }

                    Status.SUCCESS -> {
                        gotoWithSlide(
                            R.id.successPaymentFragment, bundleOf(
                                "transactId" to it.data?.request_id,
                                Const.OPERATION to SuccessPaymentFragment.OPERATION_HOME_PAYMENT,
                                SuccessPaymentFragment.PAYMENT_KEY_VALUES to params,
                                Const.OPERATION_AMOUNT to Format.formatMoney(
                                    binding.etAmount.text.toString().replace(" ", "")
                                ) + if (selectedCard!!.currency_code == "000") " ${
                                    getString(
                                        R.string.sum_text
                                    )
                                }" else " USD",
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setTemplateData() {
        binding.text.text = templateItem!!.name
        if (templateItem!!.icon_name != "") Picasso.get()
            .load(Keys.paynetPhotoUrl() + templateItem!!.icon_name)
            .error(R.drawable.ic_payments_placeholder).into(binding.icon)
        else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)

        if (!templateItem!!.balance.isNullOrEmpty() && !templateItem!!.balance.toString()
                .startsWith("0")
        ) {
            var balance = templateItem!!.balance

            if (templateItem!!.balance!!.startsWith("-")) {
                balance = balance!!.replace("-", "")
                binding.amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.brandRedColor
                    )
                )
                binding.amount.text = "-${Format.formatAmount(balance)} UZS"
            } else {
                balance = balance!!.replace("+", "")
                binding.amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.mainTextColor
                    )
                )
                templateItem!!.amount = "0"
                binding.amount.text = "${Format.formatAmount(balance)} UZS"
            }
        } else {
            templateItem!!.amount = "0"
            binding.amount.text = "----"
        }

        binding.personText.text = templateItem!!.account_text
        binding.person.text = templateItem!!.account


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
        if (min.isNullOrEmpty()) {
            min = "500"
        }
        if (max.isNullOrEmpty()) {
            max = "50000000"
        }
        minAmount = min
        maxAmount = max
    }


    private fun checkForValues(inputParams: ArrayList<PaymentParams>) {
        paymentParamsArrayList = ArrayList()
        for (paymentParams in inputParams) {
            val sampleParam = PaymentParams()
            sampleParam.payment_detail_code = paymentParams.payment_detail_code
            sampleParam.is_visible = paymentParams.is_visible
            sampleParam.param_type = paymentParams.param_type
            sampleParam.ord = paymentParams.ord
            sampleParam.param_length = paymentParams.param_length
            sampleParam.is_required = paymentParams.is_required
            sampleParam.is_read_only = paymentParams.is_read_only
            sampleParam.code = paymentParams.code
            sampleParam.mondatory = paymentParams.mondatory
            sampleParam.group_ord = paymentParams.group_ord
            sampleParam.def_value = paymentParams.def_value
            sampleParam.icon_name = paymentParams.icon_name
            sampleParam.level_position = paymentParams.level_position
            sampleParam.name = paymentParams.name
            sampleParam.hint = paymentParams.hint
            sampleParam.ref_code = paymentParams.ref_code
            for (templateKeyValue in templateResponse?.template_details!!) {
                if (paymentParams.code == templateKeyValue.code) {
                    sampleParam.def_value = templateKeyValue.value.toString()
                    sampleParam.code = templateKeyValue.code!!
                }
            }
            paymentParamsArrayList.add(sampleParam)
        }
    }

    private fun getPaymentDetails(): ArrayList<PaymentParams> {
        var inputParams = ArrayList<PaymentParams>()
        paymentParamsArrayList = ArrayList()
        try {
            inputParams = databaseHelper.getPaymentDetails(paymentService!!.payment_detail_code!!)
            val hashSet = HashSet<PaymentParams>()
            hashSet.addAll(inputParams)
            inputParams.clear()
            inputParams.addAll(hashSet)
            if (inputParams.size > 0) {
                inputParams[0].ord?.let {
                    inputParams.sortWith { o1, o2 -> o1.ord!!.compareTo(o2.ord!!) }
                }
            }
            paymentParamsArrayList = inputParams
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return inputParams
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, "500", CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    val amount = binding.etAmount.text.toString().replace(" ", "")
                    if (amount.isNotEmpty()) {
                        checkAmount(amount)
                    }
                }
            }
        }
    }


}