package uz.fido.universaldigital.ui.fragments.payment.init_payment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.contains
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentReference
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.PaymentFragmentBinding
import uz.fido.universaldigital.databinding.ViewPaymentAmountBinding
import uz.fido.universaldigital.databinding.ViewPaymentDateBinding
import uz.fido.universaldigital.databinding.ViewPaymentNavigationBinding
import uz.fido.universaldigital.databinding.ViewPaymentPhoneNumberBinding
import uz.fido.universaldigital.databinding.ViewPaymentSimpleInputBinding
import uz.fido.universaldigital.ui.dialogs.OpenSettingsDialog
import uz.fido.universaldigital.ui.dialogs.ReferenceDialog
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPayment
import uz.fido.universaldigital.ui.utils.extensions.getFormattedContact
import uz.fido.universaldigital.ui.utils.extensions.hideSoftKeyboard
import uz.fido.universaldigital.ui.utils.extensions.setHtmlHint
import uz.fido.universaldigital.ui.utils.extensions.setPaymentHint
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.view.custom_edit_text.amount_edit_text.AmountEditText
import uz.fido.utils.view.custom_edit_text.mask_edit_text.ClipBoardListener
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("SetTextI18n")
abstract class BasePaymentFragment : DownloadPayment(), ClipBoardListener, BaseInterface, PermissionInterface {

    private lateinit var phoneViewBinding: ViewPaymentPhoneNumberBinding
    private lateinit var myCalendar: Calendar
    private var permissionInterface: PermissionInterface? = null
    private var navigationList: ArrayList<AllServiceLists>? = null
    private var refParamList = ArrayList<PaymentReference>()
    private var pastedToView: Boolean? = null
    private var dateCode: String? = null
    private var isPhone = false

    lateinit var paymentHashMap: HashMap<String, String>
    lateinit var referenceDialog: ReferenceDialog
    lateinit var binding: PaymentFragmentBinding

    var paymentParamsArrayList = ArrayList<PaymentParams>()
    var templateKeyValueList = ArrayList<TemplateKeyValue>()
    var editTextList: ArrayList<MaskEditText> = ArrayList()
    var keyValueList = HashMap<String, String>()

    var paymentService: PaymentService? = null
    var operation: Int? = null
    var mobileNumber: String = ""
    var mobileNumberUpdate=false
    private var paymentAmount = 0.0
    var minAmount = 0.0
    var maxAmount = 0.0

    //navigation view
    var regionCode: String? = null
    var divisionTag: String? = null
    var editTextTag: String? = null
    var regionSelected = false

    //template
    var templateItem: Template? = null
    var accountId: String? = null
    var homeName: String? = null
    var homeId: String? = null

    fun checkForPopularValues(inputParams: List<PaymentParams>) {
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
            sampleParam.regular_exp_mask = paymentParams.regular_exp_mask
            sampleParam.settlement = paymentParams.settlement
            sampleParam.field_mask = paymentParams.field_mask
            sampleParam.prefix = paymentParams.prefix
            for (templateKeyValue in templateKeyValueList) {
                if (paymentParams.code == templateKeyValue.code) {
                    sampleParam.def_value = templateKeyValue.value.toString()
                    sampleParam.code = templateKeyValue.code!!
                }
            }
            paymentParamsArrayList.add(sampleParam)
        }
    }

    fun checkForValues(inputParams: List<PaymentParams>) {
        if (operation == PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE || operation == PaymentFragment.PAYMENT_OPERATION_TEMPLATE || operation == PaymentFragment.PAYMENT_OPERATION_MIB) {
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
                for (templateKeyValue in templateKeyValueList) {
                    if (paymentParams.code == templateKeyValue.code) {
                        sampleParam.def_value = templateKeyValue.value.toString()
                        sampleParam.code = templateKeyValue.code!!
                    }
                }
                paymentParamsArrayList.add(sampleParam)
            }
        }
    }

    fun getPaymentDetails(): ArrayList<PaymentParams> {
        var inputParams = ArrayList<PaymentParams>()
        paymentParamsArrayList = ArrayList()
        try {
            inputParams =
                databaseHelper!!.getPaymentDetails(paymentService!!.payment_detail_code!!)
            paymentParamsArrayList = inputParams
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return inputParams
    }

    fun fetchMinMaxAmount() {
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
            max = "1000"
        }
        minAmount = min.toDouble()
        maxAmount = max.toDouble()
    }

    private fun getAmount(transferAmount: String) {
        val amount = transferAmount.trim()
        paymentAmount =
            if (amount.isEmpty() || amount == "" || amount[0] == '\u0000' || amount == "." || amount == ",") {
                0.0
            } else {
                java.lang.Double.parseDouble(Format.noSpace(amount))
            }
    }

    private fun checkForAmount(amount: Double, textInputLayout: TextInputLayout) {
        if (minAmount > amount || amount > maxAmount) {
            binding.btnContinue.isEnabled(false)
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = getString(
                R.string.amount_range,
                Format.formatAmount(minAmount.toString()),
                Format.formatAmount(maxAmount.toString())
            )
        } else {
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
            binding.btnContinue.isEnabled(checkForButton())
        }
    }

    override fun onTextPaste() {
        pastedToView = true
        val phoneNumberEditText = if (paymentHashMap["PHONE_NUMBER"] != null) {
            view?.findViewWithTag("PHONE_NUMBER")
        } else {
            view?.findViewWithTag<MaskEditText>("CLIENTID")
        }
        phoneNumberEditText?.let {
            var str = Format.phoneNumberFormat(it.text.toString())
            str = str.replace("+", "")
            val reverse = StringBuilder()
            for (i in str.length - 1 downTo 0) {
                reverse.append(str[i])
            }
            val clearString = StringBuilder()
            for (i in reverse.length - 1 downTo 0) {
                clearString.append(reverse[i])
            }
            var clearPhone = clearString.toString()
            if (clearPhone.contains("998")) {
                clearPhone = clearPhone.substring(3)
            }
            if (!clearPhone.contains("998")) {
                clearPhone = "+998$clearPhone"
            }

            it.setMask("+998## ### ## ##")
            it.setText(clearPhone)
            it.setMaxLength(16)
        }
    }

    fun checkForButton(): Boolean {
        for (maskEditText in editTextList) {
            if (maskEditText.visibility == View.VISIBLE) {
                if (maskEditText.text.toString().isEmpty()) {
                    return false
                }
                if (maskEditText.error != null) {
                    return false
                }
            }
        }
        if (isPhone) {
            val code = "phone"
            val editText = binding.mainLayout.findViewWithTag<MaskEditText>(code)
            val editTextHome = binding.mainLayout.findViewWithTag<MaskEditText>(code + "_HOME")
            val tiLayout = binding.mainLayout.findViewWithTag<TextInputLayout>("${code}_LAYOUT")
            if (editTextHome == null) {
                if (tiLayout == null || tiLayout.error != null) {
                    return false
                }
                if (editText == null) return false
            }
        }
        if (paymentHashMap["AMOUNT"] != null) {
            val editText = binding.mainLayout.findViewWithTag<AmountEditText>("AMOUNT")
            if (homeId == null) {
                if (editText != null) {
                    if (editText.text.toString().trim().isEmpty()) {
                        return false
                    } else {
                        getAmount(editText.text.toString().trim())
                        if (minAmount > paymentAmount || paymentAmount > maxAmount) {
                            return false
                        }
                    }
                } else {
                    return false
                }
            }
        }
        return true
    }

    private val activityForContacts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { intent ->
                    val contactUri = intent.data as Uri
                    val contactQuery = requireActivity().contentResolver.query(
                        contactUri, null, null, null, null
                    ) as Cursor
                    pickPhoneNumberFromContact(contactQuery)
                }
            }
        }

    private fun pickPhoneNumberFromContact(contactQuery: Cursor?) {
        try {
            val phoneNumber: String
            if (contactQuery != null && contactQuery.moveToFirst()) {
                val numberIndex: Int =
                    contactQuery.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                phoneNumber = contactQuery.getString(numberIndex)
                if (getFormattedContact(phoneNumber).isNotEmpty()) {
                    phoneViewBinding.editTextPhone.setText(getFormattedContact(phoneNumber))
                } else {
                    wrongFormatSms()
                }
            } else {
                wrongFormatSms()
            }
        } catch (exception: Exception) {
            contactQuery?.close()
            wrongFormatSms()
        } finally {
            contactQuery?.close()
        }
    }

    private fun wrongFormatSms() {
        Toast.makeText(
            requireContext(), getString(uz.fido.utils.R.string.wrong_format), Toast.LENGTH_SHORT
        ).show()
    }

    override fun contactsPermissionGranted() {
        fetchPhoneNo()
    }

    override fun cameraPermissionGranted() {
        goto(R.id.qrPaymentFragment, bundleOf("scan" to ""))
    }

    private fun fetchPhoneNo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            ContactsContract.Contacts.CONTENT_URI,
            ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        )
        activityForContacts.launch(intent)
    }

    private fun drawDateView(paymentParams: PaymentParams) {
        ViewPaymentDateBinding.inflate(
            LayoutInflater.from(requireContext()), binding.mainLayout, false
        ).apply {

            textInputLayout.setPaymentHint(paymentParams)
            editTextDate.tag = paymentParams.code
            textInputLayout.tag = paymentParams.code

            editTextDate.setOnClickListener { edittext ->
                dateCode = editTextDate.tag as String
                DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                        (edittext as MaskEditText).setText(dateFormat.format(myCalendar.time))
                        paymentHashMap[dateCode!!] = dateFormat.format(myCalendar.time)
                        binding.btnContinue.isEnabled(checkForButton())
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            editTextDate.addTextChangedListener {
                paymentParams.def_value = it.toString()
            }

            if (paymentParams.is_read_only != "Y") editTextList.add(editTextDate)
            if (!paymentParams.is_visible.equals("Y")) {
                root.visibility = View.GONE
                editTextDate.visibility = View.GONE
            } else {
                if (!paymentParams.settlement.isNullOrEmpty()) {
                    var tag = paymentParams.settlement + textInputLayout.tag
                    if (paymentParams.settlement == "-1") {
                        tag = "-1"
                    }
                    root.tag = tag + "_layout"
                }
            }
            addViewToMainLayout(root)
        }
    }

    private fun drawNavigationView(paymentParams: PaymentParams) {
        ViewPaymentNavigationBinding.inflate(
            LayoutInflater.from(requireContext()), binding.mainLayout, false
        ).apply {

            val paymentCode = paymentParams.code
            editTextNavigation.tag = paymentCode
            textInputLayout.setPaymentHint(paymentParams)

            if (paymentParams.def_value.isNotEmpty()) {
                try {
                    val defValue = paymentParams.def_value
                    refParamList =
                        databaseHelper!!.getRefParamList(paymentParams.ref_code.toString(), null)
                    paymentHashMap[paymentCode] = defValue
                    for (i in refParamList.indices) {
                        if (defValue == refParamList[i].code!!) {
                            editTextNavigation.setText(refParamList[i].name!!)
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

            if (paymentCode == "DIVISIONS" || paymentCode == "CODE_GP" || paymentCode == "GNI" || paymentCode == "SOATO") {
                editTextNavigation.setOnClickListener {
                    view?.let { view ->
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
                            try {
                                refParamList = when (editTextTag) {
                                    "CODE_GP" -> databaseHelper!!.getRefParamList(
                                        "318", regionCode
                                    )

                                    "GNI" -> databaseHelper!!.getRefParamList("319", regionCode)
                                    "SOATO" -> databaseHelper!!.getRefParamList("317", regionCode)
                                    else -> databaseHelper!!.getRefParamList("D0001", regionCode)
                                }
                                navigationList = ArrayList()
                                for (i in refParamList.indices) {
                                    val allServiceLists = AllServiceLists()
                                    allServiceLists.name = refParamList[i].name!!
                                    allServiceLists.code = refParamList[i].code!!
                                    allServiceLists.paymentParams = paymentParams
                                    navigationList!!.add(allServiceLists)
                                }
                                referenceDialog = ReferenceDialog(
                                    this@BasePaymentFragment, navigationList!!, editTextTag!!
                                )
                                referenceDialog.show(childFragmentManager, "reference_dialog")
                            } catch (e: SQLException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } else {
                editTextNavigation.setOnClickListener {
                    hideSoftKeyboard()
                    editTextTag = editTextNavigation.tag.toString()
                    regionSelected = editTextTag == "REGIONS"
                    refParamList = ArrayList()
                    try {
                        refParamList = databaseHelper!!.getRefParamList(
                            paymentParams.ref_code.toString(), null
                        )
                        navigationList = ArrayList()
                        for (i in refParamList.indices) {
                            val allServiceLists = AllServiceLists()
                            allServiceLists.name = refParamList[i].name!!
                            allServiceLists.code = refParamList[i].code!!
                            allServiceLists.paymentParams = paymentParams
                            navigationList!!.add(allServiceLists)
                        }
                        referenceDialog = ReferenceDialog(
                            this@BasePaymentFragment, navigationList!!, editTextTag!!
                        )
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                    referenceDialog.show(childFragmentManager, "references_dialog")
                }
            }

            if (paymentParams.is_visible!! != "Y") {
                editTextNavigation.visibility = View.GONE
                root.visibility = View.GONE
            }

            if (paymentParams.is_read_only == "Y") {
                editTextNavigation.isEnabled = false
            } else {
                editTextList.add(editTextNavigation)
            }
            addViewToMainLayout(root)
        }
    }

    private fun drawAmountView(paymentParams: PaymentParams) {
        ViewPaymentAmountBinding.inflate(
            LayoutInflater.from(requireContext()), view?.parent as? ViewGroup, false
        ).apply {
            textInputLayout.setPaymentHint(paymentParams)
            editTextAmount.tag = "AMOUNT"
            if (paymentService?.group_code != "7") {
                if (paymentService?.payment_type?.lowercase(Locale.getDefault()) == "paynet") {
                    editTextAmount.keyListener =
                        DigitsKeyListener.getInstance("0123456789 ")
                }
            }

            if (paymentParams.def_value.isNotEmpty()) {
                editTextAmount.setText(paymentParams.def_value)
                paymentHashMap[paymentParams.code] = paymentParams.def_value
            } else {
                paymentHashMap["AMOUNT"] = ""
            }
            editTextAmount.addTextChangedListener { s ->
                if (s.toString().isNotEmpty()) {
                    paymentParams.def_value = s.toString()
                    editTextAmount.setSelection(editTextAmount.length())
                    val transferAmount = editTextAmount.text.toString()
                    paymentHashMap["AMOUNT"] = transferAmount
                    getAmount(transferAmount)
                    checkForAmount(paymentAmount, textInputLayout)
                    binding.btnContinue.isEnabled(checkForButton())
                } else {
                    binding.btnContinue.isEnabled(false)
                    textInputLayout.error = null
                    textInputLayout.isErrorEnabled = false
                    paymentAmount = 0.0
                }
            }
            textViewMinAmount.text =
                getString(R.string.min_amount) + " " + Format().formatAmount(minAmount.toString()) + " ${
                    getString(
                        R.string.sum_text
                    )
                }"
            if (operation == PaymentFragment.PAYMENT_OPERATION_MOBILE_WIDGET) {
                editTextAmount.setText(arguments?.getString(PaymentFragment.PAYMENT_ARGUMENT_2))
            }
            addViewToMainLayout(root)
        }
    }

    private fun drawPhoneView(paymentParams: PaymentParams) {
        phoneViewBinding = ViewPaymentPhoneNumberBinding.inflate(
            LayoutInflater.from(requireContext()), binding.mainLayout, false
        )
        phoneViewBinding.apply {
            val phoneTag = "phone"
            editTextPhone.tag = phoneTag
            val phoneTextInputLayout = textInputLayout
            phoneTextInputLayout.tag = "phone_LAYOUT"
            editTextPhone.setTextInputLayout(phoneTextInputLayout)

            val prefixes = listOf(paymentParams.prefix!!.split(","))
            if (prefixes.isNotEmpty() && prefixes[0].size == 1) {
                editTextPhone.setText(paymentParams.prefix)
                editTextPhone.setOnKeyListener { _, _, event -> event.keyCode == KeyEvent.KEYCODE_DEL && editTextPhone.text.toString().length == paymentParams.prefix!!.length }
            } else {
                editTextPhone.setText("+998")
                editTextPhone.setOnKeyListener { _, _, event -> event.keyCode == KeyEvent.KEYCODE_DEL && editTextPhone.text.toString().length == 4 }
            }
//            editTextPhone.setPrefix(paymentParams.prefix)
            editTextPhone.addTextChangedListener { s ->
                if (s.toString().length > 4) {
                    if (pastedToView == null) {
                        pastedToView = true
                        editTextPhone.setMask("+998## ### ## ##")
                    }
                }
                paymentHashMap[paymentParams.code] = s.toString()
                if (phoneTextInputLayout.error == null) {
                    binding.btnContinue.isEnabled(checkForButton())
                }
                paymentParams.def_value = s.toString()
            }
            editTextPhone.addClipBoardListener(this@BasePaymentFragment)
            if (paymentParams.payment_detail_code.equals("TELEPHONY") || paymentParams.payment_detail_code.equals(
                    "PAYNET_GTC"
                )
            ) {
                editTextPhone.tag = "phone" + "_HOME"
                editTextPhone.setMask("## ### ## ##")
            } else {
                editTextPhone.setMaxLength(30)
                editTextPhone.setMask("+998## ### ## ##")
                if (paymentParams.def_value.isNotEmpty()) {
                    var defValue = paymentParams.def_value
                    if (!defValue.contains("+998")) {
                        defValue = "+998${defValue}"
                    }
                    editTextPhone.setText(defValue)
                }
                imageViewContacts.setOnClickListener {
                    if (checkForContactsPermission(this@BasePaymentFragment)) {
                        fetchPhoneNo()
                    }
                }
                if (operation == PaymentFragment.PAYMENT_OPERATION_MOBILE_WIDGET) {
                    editTextPhone.setText(arguments?.getString(PaymentFragment.PAYMENT_ARGUMENT_1))
                }
            }
            if (mobileNumber.isNotEmpty() && !mobileNumberUpdate){
                editTextPhone.setText(mobileNumber)
            }
            addViewToMainLayout(root)
        }
    }

    private fun drawMainBlock(paymentParams: PaymentParams, index: Int) {
        if (isDetached || context == null || view == null) return
        val mainBlockBinding = ViewPaymentSimpleInputBinding.inflate(
            LayoutInflater.from(requireContext()), view?.parent as? ViewGroup, false
        )
        val editText = mainBlockBinding.editTextMainBlock
        if (paymentParams.param_type.equals("T")) editText.inputType = InputType.TYPE_CLASS_TEXT
        if (paymentParams.param_type.equals("N")) {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.keyListener = DigitsKeyListener.getInstance("0123456789-")
        }
        if (paymentParams.param_type.equals("F")) {
            editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.keyListener = DigitsKeyListener.getInstance("0123456789-")
        }
        if (paymentParams.param_type.equals("I")) editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.imeOptions =
            if (index == paymentParamsArrayList.size - 1) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT

        if (paymentParams.hint.toString().isNotEmpty()) {
            mainBlockBinding.textInputLayout.setHtmlHint(paymentParams.hint.toString())
        } else if (paymentParams.name.toString().isNotEmpty()) {
            mainBlockBinding.textInputLayout.setHtmlHint(paymentParams.name.toString())
        }
        if (paymentParams.is_read_only.equals("Y")) {
            editText.isEnabled = false
            if (paymentParams.is_required.equals("N") && paymentParams.def_value.trim().isEmpty()) {
                mainBlockBinding.root.visibility = View.GONE
                editText.visibility = View.GONE
            }
        }
        if (!paymentParams.prefix.isNullOrEmpty()) {
            val prefixes = listOf(
                paymentParams.prefix!!.split(",".toRegex()).toTypedArray().toString()
            ).toTypedArray()
            if (prefixes.size == 1) {
                editText.setText(paymentParams.prefix)
                editText.setOnKeyListener { _, _, event -> event.keyCode == KeyEvent.KEYCODE_DEL && editText.text.toString().length == paymentParams.prefix!!.length }
            }
            editText.setPrefix(paymentParams.prefix)
        }
        editText.setTextInputLayout(mainBlockBinding.textInputLayout)

        if (paymentParams.def_value.isNotEmpty()) {
            editText.setText(paymentParams.def_value)
            paymentHashMap[paymentParams.code] = paymentParams.def_value
        }
        if (paymentParams.param_length.toString().isNotEmpty()) {
            editText.setMaxLength(paymentParams.param_length!!.toInt())
        }
        when (paymentParams.code) {
            "AMOUNT" -> {
                if (homeId == null) {
                    paymentParams.def_value = paymentParams.def_value.replace(",", ".")
                    drawAmountView(paymentParams)
                } else {
                    paymentHashMap["AMOUNT"] = "0"
                }
            }

            else -> {
                if (paymentParams.regular_exp_mask == "phone") {
                    isPhone = true
                    drawPhoneView(paymentParams)
                } else {
                    mainBlockBinding.textInputLayout.tag = paymentParams.code
                    editText.tag = "${paymentParams.code}_edit"
                    editText.addTextChangedListener { s ->
                        paymentParams.def_value = s.toString()
                        paymentHashMap[paymentParams.code] = s.toString()
                        binding.btnContinue.isEnabled(checkForButton())
                    }
                    if (!paymentParams.is_visible.equals("Y")) {
                        mainBlockBinding.root.visibility = View.GONE
                        editText.visibility = View.GONE
                    } else {
                        if (!paymentParams.settlement.isNullOrEmpty()) {
                            var tag = paymentParams.settlement + mainBlockBinding.textInputLayout.tag
                            if (paymentParams.settlement == "-1") {
                                tag = "-1"
                            }
                            editText.tag = tag
                            mainBlockBinding.root.tag = tag + "_layout"
                        }
                    }
                    if (paymentParams.is_read_only != "Y") editTextList.add(editText)
                    if (paymentParams.payment_detail_code == "MUNIS_0202") {
                        if (paymentParams.code == "BUDGET_ACCOUNT" || paymentParams.code == "BUDGET_INCOME") {
                            editText.visibility = View.GONE
                            mainBlockBinding.root.visibility = View.GONE
                        }
                    }
                    addViewToMainLayout(mainBlockBinding.root)
                }
            }
        }
    }

    fun drawPaymentFields() {
        binding.mainLayout.removeAllViewsInLayout()
        editTextList = ArrayList()
        initTemplateOperations()
        for (i in paymentParamsArrayList.indices) {
            when (paymentParamsArrayList[i].param_type) {
                "T", "N", "F", "I" -> {
                    if (operation == PaymentFragment.PAYMENT_OPERATION_MIB && paymentParamsArrayList[i].code == "WORKNUM") {
                        paymentParamsArrayList[i].def_value = accountId ?: ""
                    }
                    if (paymentParamsArrayList[i].code == "INVOICE") {
                        arguments?.getString(PaymentFragment.PAYMENT_SERVICE_DEFAULT_VALUE)?.let {
                            paymentParamsArrayList[i].def_value = it
                        }
                    }
                    drawMainBlock(paymentParamsArrayList[i], i)
                }

                "D" -> {
                    myCalendar = Calendar.getInstance()
                    drawDateView(paymentParamsArrayList[i])
                }

                "S" -> {
                    drawNavigationView(paymentParamsArrayList[i])
                }
            }
        }
        binding.btnContinue.isEnabled(checkForButton())
    }

    private fun initTemplateOperations() {
        if (operation == PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE ||
            operation == PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE
        ) {
            val mainBlockBinding = ViewPaymentSimpleInputBinding.inflate(
                LayoutInflater.from(requireContext()), binding.mainLayout, false
            )
            mainBlockBinding.editTextMainBlock.apply {
                inputType = InputType.TYPE_CLASS_TEXT
                imeOptions = EditorInfo.IME_ACTION_NEXT
                val hint =
                    if (homeId == null) getString(R.string.template_name) else getString(R.string.name)
                setText(paymentService?.nameIndex)
                mainBlockBinding.textInputLayout.hint = hint
                tag = PaymentFragment.TEMPLATE_NAME_TAG
                if (templateItem != null) {
                    setText(templateItem?.name)
                }
                editTextList.add(this)
                addViewToMainLayout(mainBlockBinding.root)
            }
        }
    }

    private val contactsPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (!it.value) {
                OpenSettingsDialog((getString(R.string.contact_permission_description))) {
                    run {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", activity?.packageName, null)
                        startActivity(intent)
                    }
                }.show(childFragmentManager, "")
                return@registerForActivityResult
            }
        }
        this.permissionInterface?.contactsPermissionGranted()
    }

    private fun checkForContactsPermission(permissionInterface: PermissionInterface?): Boolean {
        val listPermissionsNeeded = ArrayList<String>()
        val writeContact =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS)
        val readContact =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
        if (writeContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS)
        }
        if (readContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            this.permissionInterface = permissionInterface
            contactsPermission.launch(listPermissionsNeeded.toTypedArray())
            return false
        }
        return true
    }

    /*
    *
    * Agar SETTLEMENT -1 bo'lsa SETTLEMENT qiymatidan qat'iy nazar listga qo'shiladi
    * Agar SETTLEMENT -1 dan boshqa bo'lsa SETTLEMENTlar ro'yxati vergul bilan ajratilgan bo'ladi, ulani
    * arrayga split(",") qilinib tanlangan to'lov turini SETTLEMENTi shu arrayda bor bo'lsa ko'rsatiladi aks holda ko'rastilmaydi
    *
    * */

    private fun addViewToMainLayout(view: View) {
        if (!binding.mainLayout.contains(view)) {
            binding.mainLayout.addView(view)
        }
    }

}