package uz.fido.universaldigital.ui.fragments.payment.init_payment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentResponse
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.payment.qr.QrCode
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.PaymentFragmentBinding
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.payment.MenuPaymentViewModel
import uz.fido.universaldigital.ui.fragments.payment.abc_adapter.PopularPaymentsAdapter
import uz.fido.universaldigital.ui.fragments.payment.abc_confirm.ConfirmPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.CreateAutoPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPaymentInterface
import uz.fido.universaldigital.ui.fragments.payment.init_payment.second_step.PaymentSecondStepFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.hideProgress
import uz.fido.universaldigital.ui.utils.extensions.isInternetConnected
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.extensions.showProgress
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PaymentFragment : BasePaymentFragment(), DownloadPaymentInterface {

    private var qrCodeValuesList = ArrayList<QrCode>()
    private var loanId = ""
    private var templateName: String = ""
    private var qrId: String = ""

    private val monitoringViewModel: LocalMonitoringViewModel by viewModels()
    private val menuPaymentsViewModel: MenuPaymentViewModel by viewModels()

    private var popularPaymentsAdapter: PopularPaymentsAdapter? = null
    private var popularPaymentsList = ArrayList<LocalMonitoring>()
    private var continueBtnClicked = false

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)

    enum class TemplateGroups(groupId: Int) {
        DEFAULT_TEMPLATES(1);

        var groupId: Int = 0

        init {
            this.groupId = groupId
        }

        override fun toString(): String {
            return groupId.toString()
        }
    }

    companion object {
        const val PAYMENT_ACCOUNT = "payment_account"
        const val PAYMENT_SERVICE = "payment_service"
        const val PAYMENT_OPERATION = "operation"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"

        const val PAYMENT_OPERATION_PAYMENT = 0
        const val PAYMENT_OPERATION_TEMPLATE = 1
        const val PAYMENT_OPERATION_SAVE_TEMPLATE = 2
        const val PAYMENT_OPERATION_EDIT_TEMPLATE = 3
        const val PAYMENT_OPERATION_MOBILE_WIDGET = 4
        const val PAYMENT_OPERATION_QR = 5
        const val PAYMENT_OPERATION_MIB = 6
        const val PAYMENT_OPERATION_AUTO_PAYMENT_ADD = 7

        const val PAYMENT_TEMPLATE_KEY_VALUE_LIST = "templateKeyValueList"
        const val PAYMENT_TEMPLATE_ITEM = "template_item"
        const val TEMPLATE_NAME_TAG = "TEMPLATE_NAME"
        const val PAYMENT_HOME_ID = "home_id"
        const val PAYMENT_HOME_NAME = "home_name"
        const val PAYMENT_ARGUMENT_1 = "argument_1"
        const val PAYMENT_ARGUMENT_2 = "argument_2"

        const val PAYMENT_SERVICE_DEFAULT_VALUE = "payment_service_default_value"
        const val PAYMENT_SERVICE_ID = "payment_service_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
        setPaymentStatusListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetOnClickListeners()
        checkForPaymentDownload()
        init()
    }

    private fun initArguments() {
        arguments?.let {
            operation = it.getInt(PAYMENT_OPERATION)
            paymentService = (it.serializable(PAYMENT_SERVICE) ?: PaymentService())
            homeId = it.getString(PAYMENT_HOME_ID)
            homeName = it.getString(PAYMENT_HOME_NAME)
            Logger.writeErrorLog(paymentService.toString())
            templateKeyValueList = (it.serializable(PAYMENT_TEMPLATE_KEY_VALUE_LIST) ?: ArrayList())

            when (operation) {
                PAYMENT_OPERATION_QR -> {
                    paymentService!!.min_amount = "500"
                    paymentService!!.max_amount = "5000000"
                    paymentService!!.nameIndex = getString(R.string.qr_payment)
                    paymentService!!.service_id = 204
                    paymentService!!.payment_type = "munis"
                    paymentService!!.icon_name = ""
                    paymentService!!.payment_detail_code = "MUNIS_9998"
                    qrCodeValuesList =
                        it.serializable<ArrayList<QrCode>>("qr_list") as ArrayList<QrCode>
                }

                PAYMENT_OPERATION_MOBILE_WIDGET -> {
                    paymentService = it.serializable(PAYMENT_SERVICE) as PaymentService?
                }

                PAYMENT_OPERATION_EDIT_TEMPLATE -> {
                    templateItem = it.serializable(PAYMENT_TEMPLATE_ITEM) as Template?
                }

                PAYMENT_OPERATION_MIB -> accountId = it.getString(PAYMENT_ACCOUNT)
            }
            if (it.getString(MOBILE_NUMBER) != null) {
                mobileNumber = it.getString(MOBILE_NUMBER).toString()
            }
        }
    }

    override fun downloadPaymentSuccess() {
        view?.let {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                init()
            }
        }
    }

    override fun downloadPaymentFailure() {
        view?.let {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                init()
            }
        }
    }

    private fun init() {
        arguments?.getString(PAYMENT_SERVICE_ID)?.let {
            paymentService = databaseHelper?.getServiceByContractId(it)
        }
        binding.appBar.setTitle(paymentService?.nameIndex.toString())
        if (paymentService?.icon_name != "") Picasso.get()
            .load(PAYNET_PHOTO + paymentService?.icon_name)
            .error(R.drawable.ic_payments_placeholder)
            .into(binding.imageViewAvatar)
        else binding.imageViewAvatar.setImageResource(R.drawable.ic_payments_placeholder)
        if (operation == PAYMENT_OPERATION_QR) {
            binding.imageViewAvatar.setImageResource(R.drawable.ic_qr_payment)
        }
        paymentHashMap = HashMap()
        binding.recyclerHistories.apply {
            popularPaymentsAdapter =
                PopularPaymentsAdapter(popularPaymentsList, this@PaymentFragment)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = popularPaymentsAdapter
        }
        checkForDefaultValues()
        if (popularPaymentsList.size == 0) {
            fetchPopularPayments()
        }
    }

    private fun checkForDefaultValues() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            if (paymentParamsArrayList.size == 0) {
                fetchMinMaxAmount()
                checkForValues(getPaymentDetails())

            }
            withContext(Dispatchers.Main) {
                if (operation == PAYMENT_OPERATION_QR) {
                    prepareQRPayment()
                } else {
                    drawPaymentFields()
                }
                if (operation == PAYMENT_OPERATION_MOBILE_WIDGET) {
                    operation = PAYMENT_OPERATION_PAYMENT
                    binding.btnContinue.performClick()
                }
            }
        }
    }

    private suspend fun initPopularPayment() {
        checkForPopularValues(getPaymentDetails())
        withContext(Dispatchers.Main) {
            drawPaymentFields()
        }
    }

    private fun fetchPopularPayments() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        calendarStart[Calendar.DAY_OF_YEAR] = 1
        val dateBegin = dateFormat.format(calendarStart.time)
        val dateEnd = dateFormat.format(calendarEnd.time)
        val model = LocalMonitoringRequest(
            page_item_size = "20",
            page_number = "1",
            start_date = dateBegin,
            end_date = dateEnd,
            service_id = paymentService!!.service_id.toString()
        )
        monitoringViewModel.getLocalMonitoring(getClientToken(), model)
            .observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    val list = it.data?.local_transactions!!
                    list.forEach { localMonitoring ->
                        var exist = false
                        popularPaymentsList.forEach { popularTransfer ->
                            if (localMonitoring.partner_obj == popularTransfer.partner_obj) {
                                exist = true
                            }
                        }
                        if (!exist && localMonitoring.partner_obj.isNotEmpty()) {
                            popularPaymentsList.add(localMonitoring)
                        }
                    }
                    popularPaymentsAdapter?.setList(popularPaymentsList)
                }
            }
    }

    override fun setPaymentParams(position: Int) {
        getOperationParams(popularPaymentsList[position])
    }

    private fun getOperationParams(localeMonitoring: LocalMonitoring) {
        binding.btnContinue.setProgress(true)
        menuPaymentsViewModel.getOperationParams(
            getClientToken(),
            GetOperationInfoRequest(request_id = localeMonitoring.request_id)
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    val inParams = it.data
                    val templateKeyValue = ArrayList<TemplateKeyValue>()
                    if (inParams?.params != null) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                            inParams.params!!.forEach { params ->
                                templateKeyValue.add(
                                    if (params.key == "AMOUNT")
                                        TemplateKeyValue(
                                            code = params.key,
                                            value = Format.formatAmountFromTiynToInteger(params.value)
                                        )
                                    else TemplateKeyValue(
                                        code = params.key,
                                        value = params.value
                                    )
                                )
                            }
                            paymentParamsArrayList = ArrayList()
                            templateKeyValueList = templateKeyValue
                            initPopularPayment()
                        }
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.operation_could_not_be_performed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener { onContinueClicked() }
        binding.appBar.setOnAdditionalBtnClickListener {
            goto(R.id.paymentHistoryFragment, bundleOf("item" to paymentService))
        }
    }

    override fun setToEditText(allServiceLists: AllServiceLists, tag: String) {
        (requireView().findViewWithTag<View>(editTextTag) as MaskEditText).setText(allServiceLists.name)
        paymentHashMap[editTextTag!!] = allServiceLists.code!!
        allServiceLists.paymentParams?.apply {
            def_value = allServiceLists.code.toString()
        }
        if (regionSelected) {
            regionSelected = false
            regionCode = allServiceLists.code!!
            regionCode = regionCode!!.substring(0, regionCode!!.length.coerceAtMost(2))
            if (divisionTag != null)
                (requireView().findViewWithTag<View>(divisionTag) as MaskEditText).setText("")
        } else {
            editTextList.forEach { editText ->
                if (editText.tag != null) {
                    val editTextTag = editText.tag.toString()
                    if (editTextTag.contains(",") && editTextTag.contains(allServiceLists.code.toString())) {
                        makeVisible(editTextTag)
                    } else {
                        if (editTextTag.contains(allServiceLists.code.toString())) {
                            makeVisible(editTextTag)
                        } else if (editText.tag != "-1" && !editTextTag.contains("PINFL") && !editTextTag.contains(
                                "CODE_OBJ"
                            )
                        ) {
                            makeInvisible(editTextTag)
                        }
                    }
                }
            }
        }
        referenceDialog.dismiss()
        binding.btnContinue.isEnabled(checkForButton())
    }

    private fun makeVisible(tag: String) {
        if (binding.mainLayout.findViewWithTag<MaskEditText>(tag) != null) {
            val frameLayout: FrameLayout = binding.mainLayout.findViewWithTag(tag + "_layout")
            frameLayout.visibility = View.VISIBLE
            binding.mainLayout.findViewWithTag<MaskEditText>(tag).visibility = View.VISIBLE
        }
    }

    private fun makeInvisible(tag: String) {
        if (binding.mainLayout.findViewWithTag<FrameLayout>(tag + "_layout") != null) {
            binding.mainLayout.findViewWithTag<MaskEditText>(tag).visibility = View.GONE
            binding.mainLayout.findViewWithTag<FrameLayout>(tag + "_layout").visibility = View.GONE
        }
    }

    private fun openConfirmPayment(serviceDetails: ArrayList<PaymentParams>) {
        when (operation) {
            PAYMENT_OPERATION_SAVE_TEMPLATE -> {
                saveTemplate(false)
            }

            PAYMENT_OPERATION_EDIT_TEMPLATE -> {
                saveTemplate(true)
            }

            PAYMENT_OPERATION_AUTO_PAYMENT_ADD -> {
                continueBtnClicked = false
                val model = SaveAutoPaymentModel(
                    payment_service_id = paymentService?.service_id.toString(),
                    device_type = "A",
                    device_code = requireContext().getDeviceIds(),
                    device_name = getDeviceName(),
                    payment_details = keyValueList,
                    payment_type = paymentService?.nameIndex,
                    name = paymentService?.nameIndex
                )
                gotoWithSlide(
                    R.id.createAutoPaymentFragment,
                    bundleOf(CreateAutoPaymentFragment.SAVE_AUTO_PAYMENT_MODEL to model)
                )
            }

            else -> {
                continueBtnClicked = false
                val bundle = Bundle()
                bundle.putSerializable("list", serviceDetails)
                bundle.putSerializable("paymentService", paymentService)
                bundle.putSerializable("templateKeyValues", templateKeyValueList)
                bundle.putSerializable(
                    ConfirmPaymentFragment.CONFIRM_PAYMENT_OPERATION,
                    ConfirmPaymentFragment.OPERATION_PAYMENT
                )
                bundle.putInt(ConfirmPaymentFragment.PAYMENT_OPERATION, operation ?: 0)
                bundle.putSerializable(PaymentSecondStepFragment.PAYMENT_KEY_VALUES, keyValueList)
                gotoWithSlide(R.id.confirmPaymentFragment, bundle)
            }
        }
    }

    private fun openSecondStep(serviceDetails: ArrayList<PaymentParams>) {
        if (operation == PAYMENT_OPERATION_SAVE_TEMPLATE || operation == PAYMENT_OPERATION_EDIT_TEMPLATE) {
            templateName =
                binding.mainLayout.findViewWithTag<MaskEditText>(TEMPLATE_NAME_TAG).text.toString()
        }
        val bundle = bundleOf(
            PAYMENT_SERVICE to paymentService,
            PaymentSecondStepFragment.PAYMENT_PARAMS_LIST to serviceDetails,
            PAYMENT_OPERATION to operation,
            PaymentSecondStepFragment.PAYMENT_TEMPLATE_NAME to templateName,
            PAYMENT_TEMPLATE_ITEM to templateItem,
            PaymentSecondStepFragment.PAYMENT_HOME_ID to homeId,
            PaymentSecondStepFragment.PAYMENT_HOME_NAME to homeName,

            PaymentSecondStepFragment.PAYMENT_TEMPLATE_KEY_VALUES to templateKeyValueList,
            PaymentSecondStepFragment.PAYMENT_KEY_VALUES to keyValueList
        )
        gotoWithSlide(R.id.paymentSecondStepFragment, bundle)
    }

    private fun saveTemplate(isEdit: Boolean) {
        binding.btnContinue.setProgress(true)
        templateName =
            binding.mainLayout.findViewWithTag<MaskEditText>(TEMPLATE_NAME_TAG).text.toString()
        val model = CreateTemplateRequest(
            name = templateName,
            template_type = if (homeId != null) "H" else "D",
            service_type = if (isEdit) templateItem?.service_type.toString() else requireArguments().getString(
                "payment_group_name"
            ).toString(),
            service_id = paymentService!!.service_id.toString(),
            template_group_id = if (homeId != null) homeId!! else TemplateGroups.DEFAULT_TEMPLATES.toString(),
            payment_details = keyValueList,
            template_id = if (isEdit) templateItem!!.template_id else null
        )
        if (templateName!=""){
        menuPaymentsViewModel.createTemplate(getClientToken(), model).observe(viewLifecycleOwner) {
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
                    //clearAmount()
                    showSnackbar(it.message.toString())
                }
            }
        }
        }else{
            binding.btnContinue.setProgress(false)
            showSnackbar("Имя шаблона пусто")
        }
    }

//    private fun deleteTemplate(templateId: String) {
////        showProgress()
//        menuPaymentsViewModel.deleteTemplate(
//            getClientToken(), DeleteTemplateRequest(
//                templateId
//            )
//        ).observe(viewLifecycleOwner) { resource ->
//            resource?.let { it ->
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        saveTemplate(true)
//                    }
//
//                    Status.ERROR -> {
////                        hideProgress()
//                        showSnackbar(resource.message.toString())
//                    }
//                }
//            }
//        }
//    }

    private fun prepareQRPayment() {
        qrCodeValuesList =
            arguments?.serializable<ArrayList<QrCode>>("qr_list") as ArrayList<QrCode>
        for (qrCode in qrCodeValuesList) {
            if (qrCode.value != null && qrCode.value!!.isNotEmpty()) {
                if (qrCode.name.equals(getString(R.string.unique_qr_of_payment_system))) {
                    qrId = qrCode.value ?: ""
                }
            }
        }
        keyValueList = HashMap()
        keyValueList["SETTLEMENT"] = "01"
        keyValueList["QR_ID"] = qrId
        val model = PreparePaymentRequest(
            "204", "1", "MUNIS_9998", keyValueList, "munis"
        )
        showProgress(requireActivity(), getString(R.string.please_wait))
        menuPaymentsViewModel.preparePaymentRequest(getClientToken(), model)
            .observe(viewLifecycleOwner) {
                it?.let {
                    hideProgress(requireActivity())
                    when (it.status) {
                        Status.SUCCESS -> {
                            val response = it.data as PreparePaymentResponse
                            if (response.level_position == "-1") {
                                openConfirmPayment(response.service_details)
                            } else {
                                paymentParamsArrayList = response.service_details
                                drawPaymentFields()
                            }
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
    }

    private fun preparePayment(
        serviceId: String,
        currentLevelPosition: String,
        paymentDetailCode: String,
        params: HashMap<String, String>,
        paymentType: String
    ) {
        val model = PreparePaymentRequest(
            service_id = serviceId,
            curr_level_position = currentLevelPosition,
            payment_detail_code = paymentDetailCode,
            params = params,
            command = paymentType.lowercase(Locale.getDefault()).trim()
        )
        binding.btnContinue.setProgress(true)
        menuPaymentsViewModel.preparePaymentRequest(getClientToken(), model)
            .observe(viewLifecycleOwner) {
                it?.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            paymentParamsArrayList.forEach { paymentParams ->
                                if (paymentHashMap[paymentParams.code] != null) {
                                    if (paymentParams.code == "AMOUNT") {
                                        val amount =
                                            if (binding.mainLayout.findViewWithTag<TextInputEditText>(
                                                    "AMOUNT"
                                                ) == null
                                            ) "0" else binding.mainLayout.findViewWithTag<TextInputEditText>(
                                                "AMOUNT"
                                            ).text.toString()
                                        paymentParams.def_value = amount
                                    } else {
                                        paymentParams.def_value =
                                            paymentHashMap[paymentParams.code].toString()
                                    }
                                }
                            }
                            val response = it.data as PreparePaymentResponse
                            Const.request_id = response.request_id.toString()
                            if (response.level_position == "-1") {
                                openConfirmPayment(response.service_details)
                            } else {
                                openSecondStep(response.service_details)
                            }
                        }

                        Status.ERROR -> {
                            clearAmount()
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
    }

    private fun clearAmount() {
        paymentParamsArrayList.forEach { paymentParams ->
            if (paymentHashMap[paymentParams.code] != null) {
                if (paymentParams.code == "AMOUNT") {
                    val amount =
                        if (binding.mainLayout.findViewWithTag<TextInputEditText>("AMOUNT") == null) "0" else binding.mainLayout.findViewWithTag<TextInputEditText>(
                            "AMOUNT"
                        ).text.toString()
                    paymentParams.def_value = amount
                } else {
                    paymentParams.def_value = paymentHashMap[paymentParams.code].toString()
                }
            }
        }
    }

    private fun collectDataForPayment(): String {
        for (param in paymentParamsArrayList) {
            val templateKeyValue = TemplateKeyValue()
            if (param.code == "AMOUNT" && !continueBtnClicked) {
                continueBtnClicked = true
                param.def_value = paymentHashMap[param.code].toString()
                if (param.def_value.isNotEmpty() && param.def_value != "0") paymentHashMap[param.code] =
                    Format.formatAmountToTiyn(param.def_value)
            }
            if (param.code == "LOANS_ID") {
                loanId = param.def_value
            }
            if (param.code == "PHONE_NUMBER" || param.code == "CLIENTID") {
                var phone = ""
                paymentHashMap[param.code]?.let {
                    phone = it
                }
                phone = phone.replace(" ", "").replace("+998", "")
                paymentHashMap[param.code] = phone
            }
            if (!param.is_required.equals("N") && param.code.isNotEmpty()) {
                try {
                    keyValueList[param.code] = paymentHashMap[param.code]!!
                } catch (e: Exception) {
                    keyValueList[param.code] = ""
                }
            }
            if (param.code.isNotEmpty()) {
                templateKeyValue.code = param.code
                templateKeyValue.level_position = param.level_position
                try {
                    templateKeyValue.value = paymentHashMap[param.code]
                } catch (e: Exception) {
                    templateKeyValue.value = ""
                }
                templateKeyValueList.add(templateKeyValue)
            }
        }
        val levelPosition: String = if (paymentParamsArrayList.isNotEmpty()) {
            if (paymentParamsArrayList[0].level_position.equals("-1")) {
                "2"
            } else {
                paymentParamsArrayList[0].level_position!!
            }
        } else "1"
        if (paymentService!!.service_id == -2 && loanId.isNotEmpty() && levelPosition == "2") {
            keyValueList["LOANS_ID"] = loanId
        }
        return levelPosition
    }

    private fun onContinueClicked() {
        keyValueList = HashMap()
        templateKeyValueList = ArrayList()
        val levelPosition = collectDataForPayment()
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var vi = requireActivity().currentFocus
        if (vi == null) {
            vi = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(vi.windowToken, 0)
        if (isInternetConnected(requireContext())) {
            preparePayment(
                paymentService!!.service_id.toString(),
                levelPosition,
                paymentService!!.payment_detail_code.toString(),
                keyValueList,
                paymentService!!.payment_type.toString()
            )
        }
    }

}