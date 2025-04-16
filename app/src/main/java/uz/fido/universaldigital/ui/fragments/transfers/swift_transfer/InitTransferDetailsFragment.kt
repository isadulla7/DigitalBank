package uz.fido.universaldigital.ui.fragments.transfers.swift_transfer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.swift.CreateSwiftAppRequest
import uz.fido.network.domain.model.swift.SwiftRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentInitTransferDetailsBinding
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.amount.AmountSuggestionView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class InitTransferDetailsFragment : BaseFragment<FragmentInitTransferDetailsBinding, SwiftTransferViewModel>(
    FragmentInitTransferDetailsBinding::inflate, SwiftTransferViewModel::class.java
) {

    companion object {
        const val BANK_TRANSFER_OPERATION = "BANK_TRANSFER_OPERATION"
        const val BANK_OPERATION_CREATE = 1
        const val BANK_OPERATION_CHANGE = 2
        const val TRANSFER_EURO = "EUR"
        const val TRANSFER_DOLLAR = "USD"
        const val TRANSFER_CURRENCY = "TRANSFER_CURRENCY"
    }

    private var selectedCard: CardResponse? = null
    private var requestModel: CreateSwiftAppRequest? = null
    private val myFormat = "dd.MM.yy"
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    private var editTextList = ArrayList<TextInputEditText>()
    private var bicChecked = false
    private var operation: Int = 1
    private var amount = 0L
    private var bic = ""
    private var currency = ""
    private var tempId = ""

    private val params = HashMap<String, String>()

    private var bankTransferModel: ArrayList<TemplateKeyValue>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currency = it.getString(TRANSFER_CURRENCY).toString()
            operation = it.getInt(BANK_TRANSFER_OPERATION)
            if (it.serializable<ArrayList<TemplateKeyValue>>("details") != null) {
                bankTransferModel = it.serializable<ArrayList<TemplateKeyValue>>("details") as ArrayList<TemplateKeyValue>
                if (operation == BANK_OPERATION_CHANGE) tempId = it.getString("temp_id").toString()
            }
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        inputAllCaps()
        initSetOnClickListeners()
        initOperation()
        addTextChangeListeners()
        collectEditTexts()
        initSuggestions()
        setCurrentTime()
        initEditText()
        setFragmentResultListener(BANK_TRANSFER_OPERATION) { _, bundle ->
            operation = bundle.getInt(BANK_TRANSFER_OPERATION)
        }

    }

    private fun inputAllCaps() {
        binding.apply {
            etCustomerCountry.filters += InputFilter.AllCaps()
            passportSerial.filters += InputFilter.AllCaps()
            passportNumber.filters += InputFilter.AllCaps()
            etBeneficiaryBankAccountBi.filters += InputFilter.AllCaps()
            etBeneficiaryAccountNameFio.filters += InputFilter.AllCaps()
            etBeneficiaryAccountNameAddress.filters += InputFilter.AllCaps()
            bicAccountOnly59.filters += InputFilter.AllCaps()
            etBicNameA59.filters += InputFilter.AllCaps()
            etBic56a.filters += InputFilter.AllCaps()
            etBic57a.filters += InputFilter.AllCaps()
            etBicName57a.filters += InputFilter.AllCaps()
            etRemittanceInfo.filters += InputFilter.AllCaps()
            etDetailsOfCharges.filters += InputFilter.AllCaps()
        }
    }

    private fun initSetOnClickListeners() {
        binding.etCustomerName.setText(
            "${getFromSecureStore(Const.FIRST_NAME)} ${getFromSecureStore(Const.LAST_NAME)}"
        )
        binding.btnContinue.setOnClickListener { nextBtnClicked() }
        binding.etDateValue.setOnClickListener { datePicker(it) }
        binding.imageDate.setOnClickListener { datePicker(it) }
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener { goto(R.id.SWIFTHistoryFragment) }
    }

    private fun initOperation() {
        if (operation != BANK_OPERATION_CHANGE) binding.appBar.setAdditionalBtnVisibility(true)
        binding.appBar.setTitle(
            if (currency == TRANSFER_DOLLAR) getString(R.string.create_bank_transfer) else getString(
                R.string.create_bank_transfer_euro
            )
        )
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_PAYMENT, binding.editTextAmount, currency
        )
    }

    private fun addTextChangeListeners() {
        binding.editTextAmount.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                amount = it.toString().replace(" ", "").toLong() * 100
                binding.btnContinue.isEnabled(checkForError())
            }

        }

        binding.etBicName57a.addTextChangedListener { bicCode ->
            if (bicCode.toString().length > 10) {
                checkingBic(bicCode.toString())
            } else {
                binding.bicOrganisationName.visibility = View.GONE
                binding.tiBeneficiaryBankAccountBi.isErrorEnabled = false
            }
        }
    }

    private fun checkingBic(bicCode: String) {
        binding.progressBar.visibility = View.VISIBLE
        val bicRequest = SwiftRequest(
            bic = bicCode
        )
        viewModel.getSwiftBic(getClientToken(), bicRequest).observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            when (it.status) {
                Status.SUCCESS -> {
                    bic = ""
                    it.data!!.data.forEach { data ->
                        if (data.bic == binding.etBicName57a.text.toString()) {
                            bic = data.bic
                            bicChecked = true
                            binding.bicOrganisationName.text = data.orgname
                            binding.bicOrganisationName.visibility = View.VISIBLE
                        }
                    }
                    if (!bicChecked || bic == "") {
                        bicChecked = false
                        binding.bicOrganisationName.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    bicChecked = false
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun checkForError(): Boolean {
        editTextList.forEach {
            if (it.text.toString().isEmpty()) {
                it.error = getString(R.string.fill_the_gaps)
                return false
            }
        }
//        if (operation == BANK_OPERATION_CREATE) selectedCard?.let {
//            val balanceTiyn = it.balance.toBigDecimal()
//            val amountTiyn = ((amount + commissionUsd)).toBigDecimal()
//            if (amountTiyn > balanceTiyn) {
//                showSnackbar(
//                    getString(R.string.insufficient_amount)
//                )
//                return false
//            }
//        }
        return true
    }

    private fun datePicker(view: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(
            requireContext(), { _, Year, monthOfYear, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, Year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                when (view.id) {
                    R.id.et_date_value -> {
                        binding.etDateValue.setText(sdf.format(cal.time))
                    }

                    R.id.image_date -> {
                        binding.receiveDatePassport.setText(sdf.format(cal.time))
                    }
                }
            }, year, month, day
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    private fun nextBtnClicked() {
        if (operation == BANK_OPERATION_CREATE) {
            if (checkForError()) {
                collectData()
                gotoWithSlide(
                    R.id.confirmSWIFTTransfer, bundleOf(
                        "model" to requestModel,
                        "currency_char" to currency,
                        "details" to params
                    )
                )
            }
        } else {
//            if (checkForError()) saveTemplate()
        }
    }

    private fun collectData() {
        requestModel = CreateSwiftAppRequest(
            request_code = "CREATE_IBS_SWIFT_APP",
            language = "RU",
            device_type = "A",
            command = "card&abs",
            amount = amount.toString(),
            service_id = "-22",
            client_id = getClientId(),
            currency_code = if (currency == TRANSFER_DOLLAR) "840" else "978",
            nameandaddress_50k = arrayListOf(
                binding.etCustomerName.text.toString(),
                binding.passportSerial.text.toString() + binding.passportNumber.text.toString() + " " + binding.receiveDatePassport.text.toString(),
                binding.etCustomerCountry.text.toString()
            ),
            bicorbei_59a = bic,
            code_71a = "OUR",
            narrative_70 = arrayListOf(
                if (binding.etRemittanceInfo.text.toString().length >= 35) binding.etRemittanceInfo.text.toString()
                    .substring(0, 35)
                else binding.etRemittanceInfo.editableText.toString(),
                if (binding.etRemittanceInfo.text.toString().length >= 70) binding.etRemittanceInfo.text.toString()
                    .substring(35, 70)
                else " ",
                if (binding.etRemittanceInfo.text.toString().length >= 105) binding.etRemittanceInfo.text.toString()
                    .substring(70, 105)
                else " ",
                if (binding.etRemittanceInfo.text.toString().length >= 135) binding.etRemittanceInfo.text.toString()
                    .substring(105, 135)
                else " "
            ),
            account_56a = "",
            account_59a = bic,
            account_59 = binding.bicAccountOnly59.text.toString(),
            bicorbei_56a = binding.etBic56a.text.toString(),
            nameandaddress_59 = arrayListOf(
                binding.etBeneficiaryAccountNameFio.text.toString(),
                binding.etBeneficiaryAccountNameAddress.text.toString()
            ),
            bicorbei_57a = binding.etBicName57a.text.toString()
        )
    }

    private fun collectEditTexts() {
        editTextList = ArrayList()
        editTextList.add(binding.etBeneficiaryAccountNameFio)
        editTextList.add(binding.etBeneficiaryAccountNameAddress)
        editTextList.add(binding.editTextAmount)
        editTextList.add(binding.passportNumber)
        editTextList.add(binding.passportSerial)
        editTextList.add(binding.receiveDatePassport)
        editTextList.add(binding.etCustomerName)
        editTextList.add(binding.etCustomerCountry)
        editTextList.add(binding.bicAccountOnly59)
        editTextList.add(binding.etBicName57a)
        editTextList.add(binding.etRemittanceInfo)
    }

    private fun setCurrentTime() {
        val currentTime = Calendar.getInstance().time
        binding.etDateValue.setText(sdf.format(currentTime))
    }

    private fun initEditText() {
        if (bankTransferModel != null) bankTransferModel?.forEach { item ->
            when (item.code) {
                "AMOUNT" -> binding.editTextAmount.setText((item.value!!.toInt() / 100).toString())
                "CUSTOMER_NAME" -> binding.etCustomerName.setText(item.value)
                "CARD_NUMBER" -> selectedCard?.object_value = item.value.toString()
                "PASS_SERIAL" -> binding.passportSerial.setText(item.value)
                "PASS_NUMBER" -> binding.passportNumber.setText(item.value)
                "PASS_RECEIVE_DATE" -> binding.receiveDatePassport.setText(item.value)
                "CUSTOMER_COUNTRY" -> binding.etCustomerCountry.setText(item.value)
                "BIC_CODE" -> binding.etBicName57a.setText(item.value)
                "REMITTANCE_INFO" -> binding.etRemittanceInfo.setText(item.value)
                "ACCOUNT_59" -> binding.bicAccountOnly59.setText(item.value)
                "BICORBEI_56A" -> binding.etBic56a.setText(item.value)
                "BENEFICIARY_NAME" -> binding.etBeneficiaryAccountNameFio.setText(item.value)
                "BENEFICIARY_ADDRESS" -> binding.etBeneficiaryAccountNameAddress.setText(item.value)
                "BICORBEI_57A" -> binding.etBicName57a.setText(item.value)
            }
        }
    }


}