package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.currency

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.branches.OneTimeInfoRequest
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferToUsdAccountBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_history.PaymentHistoryFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.BankBranchesDialog
import uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.RequisitesViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Command
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.const.ServiceId
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class TransferToUsdAccountFragment :
    BaseFragment<FragmentTransferToUsdAccountBinding, RequisitesViewModel>(
        FragmentTransferToUsdAccountBinding::inflate, RequisitesViewModel::class.java
    ), TextWatcher {

    private lateinit var dbHelper: DatabaseHelper
    private var editTextForBank = ArrayList<TextInputEditText>()
    private var currency = CurrencyConst.CURRENCY_CHAR_USD
    private var maxAmount = BigDecimal(1000000)
    private var minAmount = BigDecimal(1)
    private var percent = 0.0
    private var templateDetails: ArrayList<TemplateKeyValue>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        arguments?.let {
            templateDetails =
                it.serializable<ArrayList<TemplateKeyValue>>(PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST) as ArrayList<TemplateKeyValue>
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initAccountCodeTextWatcher()
        initTextWatchers()
        if (templateDetails != null) {
            setData()
        }
    }

    private fun setData() {
        templateDetails!!.forEach {
            when (it.code) {
                "RECEIVER_ACCOUNT" -> binding.etReceiverAccount.setText(it.value)
                "RECEIVER_FILLIAL_CODE" -> binding.etReceiverMfo.setText(it.value)
                "PAYMENT_PURPOSE" -> binding.etPurpose.setText(it.value)
                "AMOUNT" -> binding.etBankAmount.setText(Format.convertFromTiynDivide(it.value.toString()))
                "RECEIVER_NAME" -> binding.etReceiverName.setText(it.value)
            }
        }
        oneTimeInfo(binding.etReceiverAccount.editableText.toString(), binding.etReceiverMfo.editableText.toString())
        binding.btnContinue.isEnabled = checkForError()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            gotoWithSlide(
                R.id.paymentHistoryFragment,
                bundleOf(
                    PaymentHistoryFragment.SERVICE_ID to ServiceId.SERVICE_ID__4
                )
            )
        }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            preparePaymentBank()
        }
        binding.etReceiverMfo.setOnClickListener {
            getBranches()
        }
    }

    private fun initAccountCodeTextWatcher() {
        binding.etReceiverAccount.doAfterTextChanged {
            if (binding.etReceiverAccount.editableText.toString().length > 7) {
                if (binding.etReceiverAccount.editableText.toString()
                        .substring(5, 8) != CurrencyConst.CURRENCY_CODE_USD
                ) {
                    binding.layoutReceiverAccount.error = getString(R.string.wrong_account_code)
                    binding.btnContinue.isEnabled(false)
                } else {
                    binding.layoutReceiverAccount.isErrorEnabled = false
                }
            } else {
                binding.layoutReceiverAccount.isErrorEnabled = false
            }
            if (binding.etReceiverAccount.editableText.toString().length == 20 &&
                binding.etReceiverMfo.editableText.toString().length == 5 &&
                binding.layoutReceiverAccount.error.isNullOrEmpty()
            ) {
                oneTimeInfo(
                    binding.etReceiverAccount.editableText.toString(),
                    binding.etReceiverMfo.editableText.toString()
                )
            } else {
                binding.etReceiverName.setText("")
                binding.etPurpose.setText("")
                binding.textPercent.text = ""
            }
        }
    }

    private fun initTextWatchers() {
        editTextForBank = ArrayList()
        editTextForBank.add(binding.etReceiverName)
        editTextForBank.add(binding.etReceiverAccount)
        editTextForBank.add(binding.etReceiverMfo)
        editTextForBank.add(binding.etBankAmount)
        editTextForBank.add(binding.etPurpose)
        binding.etReceiverName.addTextChangedListener(this)
        binding.etReceiverAccount.addTextChangedListener(this)
        binding.etReceiverMfo.addTextChangedListener(this)
        binding.etPurpose.addTextChangedListener(this)
        binding.etBankAmount.addTextChangedListener(this)
    }

    private fun checkForError(): Boolean {
        editTextForBank.forEach {
            if (it.text.toString().isEmpty()) {
                return false
            }
        }
        if (binding.etBankAmount.text.toString().isEmpty()) {
            return false
        }
        val amount = binding.etBankAmount.text.toString().replace(" ", "").toBigDecimal()
        return !(amount < minAmount || amount > maxAmount)
    }

    private fun oneTimeInfo(accountCode: String, bankCode: String) {
        binding.commissionProgressBar.visibility = View.VISIBLE
        viewModel.oneTimeInfo(getClientToken(), OneTimeInfoRequest(accountCode, bankCode))
            .observe(viewLifecycleOwner) {
                binding.commissionProgressBar.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data
                        percent = response!!.fee_percent.toDouble()
                        if (bankCode != "") {
                            if (response.client_name.isNotEmpty()) binding.etReceiverName.setText(
                                response.client_name
                            )
                            if (response.payment_purpose.isNotEmpty()) binding.etPurpose.setText(
                                response.payment_purpose
                            )
                            binding.textPercent.text =
                                getString(R.string.commission_with_dots) + " " + response.fee_percent + "%"
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun preparePaymentBank() {
        val params = HashMap<String, String>()
        val templateKeyValueList = ArrayList<TemplateKeyValue>()
        params["AMOUNT"] =
            Format.formatAmountToTiyn(binding.etBankAmount.text.toString().replace(" ", ""))
        params["RECEIVER_ACCOUNT"] = binding.etReceiverAccount.text.toString().trim()
        params["RECEIVER_NAME"] = binding.etReceiverName.text.toString()
        params["RECEIVER_FILLIAL_CODE"] = binding.etReceiverMfo.text.toString()
        params["RECEIVER_INN"] = ""
        params["PAYMENT_PURPOSE"] = binding.etPurpose.text.toString()
        params["PAYMENT_PURPOSE_CODE"] = "00667"

        params.forEach {
            val templateKeyValue = TemplateKeyValue()
            templateKeyValue.code = it.key
            templateKeyValue.level_position = "1"
            templateKeyValue.value = it.value
            templateKeyValueList.add(templateKeyValue)
        }

        val request = PreparePaymentRequest(
            service_id = ServiceId.SERVICE_ID__4,
            payment_detail_code = "PAYMENT_ONE_TIME",
            command = Command.ABS,
            curr_level_position = "1",
            params = params
        )
        val paymentService: PaymentService =
            dbHelper.getServiceByContractId(ServiceId.SERVICE_ID__4)!!
        val amount = binding.etBankAmount.editableText.toString().replace(" ", "").trim()
        viewModel.preparePaymentRequest(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val bundle = Bundle()
                    bundle.putSerializable("list", it.data?.service_details)
                    bundle.putSerializable("paymentService", paymentService)
                    bundle.putSerializable("templateKeyValues", templateKeyValueList)
                    bundle.putSerializable(SuccessPaymentFragment.PAYMENT_KEY_VALUES, params)
                    bundle.putString("currency", currency)
                    bundle.putDouble("percent", percent)
                    bundle.putString("amount", amount)
                    gotoWithSlide(R.id.confirmRequisitesPayment, bundle)
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        binding.btnContinue.isEnabled(checkForError())
    }

    private fun getBranches() {
        showProgress(getString(R.string.please_wait))
        viewModel.getBranches(getClientToken(), GetBranchListRequest("info", ""))
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    hideProgress()
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val filteredList = ArrayList<Branches>()
                            val branches = resource.data!!.filials
                            branches.forEach {
                                if (it.filial_type == "F") {
                                    filteredList.add(it)
                                }
                            }
                            BankBranchesDialog(filteredList) { mfo, name ->
                                binding.etReceiverMfo.setText(mfo)
                                binding.textBranchName.visibility = View.VISIBLE
                                binding.textBranchName.text = name
                            }.show(childFragmentManager, "")
                        }

                        Status.ERROR -> {
                            showSnackbar(resource.message.toString())
                        }
                    }
                }
            }
    }

}