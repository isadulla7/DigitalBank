package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.budget

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
import uz.fido.network.domain.model.branches.OneTimeInfoRequest
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferToBudgetBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_history.PaymentHistoryFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.RequisitesViewModel
import uz.fido.universaldigital.ui.utils.extensions.recordException
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
class TransferToBudgetFragment : BaseFragment<FragmentTransferToBudgetBinding, RequisitesViewModel>(
    FragmentTransferToBudgetBinding::inflate, RequisitesViewModel::class.java
), TextWatcher {

    private lateinit var dbHelper: DatabaseHelper
    private var editTextForBank = ArrayList<TextInputEditText>()
    private var currency = CurrencyConst.CURRENCY_CHAR_UZS
    private var maxAmount = BigDecimal(50000000)
    private var minAmount = BigDecimal(500)
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
                "RECEIVER_ACCOUNT","BUDGET_ACCOUNT" -> binding.etReceiverAccount.setText(it.value)
                "PAY_PURPOSE" -> binding.etPurpose.setText(it.value)
                "AMOUNT" -> binding.etBankAmount.setText(Format.convertFromTiynDivide(it.value.toString()))
            }
        }
        binding.btnContinue.isEnabled = checkForError()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            gotoWithSlide(
                R.id.paymentHistoryFragment,
                bundleOf(
                    PaymentHistoryFragment.SERVICE_ID to ServiceId.SERVICE_ID_15
                )
            )
        }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            preparePaymentBank()
        }
    }

    private fun initAccountCodeTextWatcher() {
        binding.etReceiverAccount.doAfterTextChanged {
            if (it.toString().length == 25 || it.toString().length == 27) {
                oneTimeInfo(it.toString())
            }
        }
    }

    private fun initTextWatchers() {
        editTextForBank = ArrayList()
        editTextForBank.add(binding.etReceiverAccount)
        editTextForBank.add(binding.etBankAmount)
        editTextForBank.add(binding.etPurpose)
        binding.etReceiverAccount.addTextChangedListener(this)
        binding.etPurpose.addTextChangedListener(this)
        binding.etBankAmount.addTextChangedListener(this)
    }

    private fun checkForError(): Boolean {
        try {
            if (binding.etBankAmount.text.toString().isEmpty()) {
                return false
            }
            val amount = binding.etBankAmount.text.toString().replace(" ", "").toBigDecimal()
            if (amount < minAmount) {
                binding.layoutBankAmount.isErrorEnabled = true
                binding.layoutBankAmount.error = getString(R.string.min_amount_500)
                return false
            }
            if (amount > maxAmount) {
                binding.layoutBankAmount.isErrorEnabled = true
                binding.layoutBankAmount.error = getString(R.string.max_amount_50_000_000)
                return false
            }
            if (amount > minAmount || amount == minAmount || amount < maxAmount || amount == maxAmount) {
                binding.layoutBankAmount.isErrorEnabled = false
            }
            return (binding.etReceiverAccount.editableText.toString().length == 27 || binding.etReceiverAccount.editableText.toString().length == 25) &&
                    binding.etPurpose.editableText.toString().isNotEmpty() &&
                    binding.etBankAmount.editableText.toString().isNotEmpty()
        } catch (e: Exception) {
            return false
        }
    }

    private fun oneTimeInfo(accountCode: String) {
        binding.commissionProgressBar.visibility = View.VISIBLE
        viewModel.oneTimeInfo(getClientToken(), OneTimeInfoRequest(accountCode, "")).observe(viewLifecycleOwner) {
            binding.commissionProgressBar.visibility = View.GONE
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data
                    percent = response!!.fee_percent.toDouble()
                    if (response.payment_purpose.isNotEmpty()) {
                        binding.etPurpose.setText(response.payment_purpose)
                    }
                    binding.textPercent.text = getString(R.string.commission_with_dots) + " " + response.fee_percent + "%"
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun preparePaymentBank() {
        try {
            val params = HashMap<String, String>()
            val templateKeyValueList = ArrayList<TemplateKeyValue>()
            params["AMOUNT"] =
                Format.formatAmountToTiyn(binding.etBankAmount.text.toString().replace(" ", ""))
            params["PAY_PURPOSE"] = binding.etPurpose.text.toString()
            params["BUDGET_ACCOUNT"] =
                if (binding.etReceiverAccount.editableText.length == 27) binding.etReceiverAccount.editableText.toString() else ""
            params["BUDGET_INCOME"] =
                if (binding.etReceiverAccount.editableText.length == 25) binding.etReceiverAccount.editableText.toString() else ""
            params["SETTLEMENT"] =
                if (binding.etReceiverAccount.editableText.length == 25) "02" else "01"

            params.forEach {
                val templateKeyValue = TemplateKeyValue()
                templateKeyValue.code = it.key
                templateKeyValue.level_position = "1"
                templateKeyValue.value = it.value
                templateKeyValueList.add(templateKeyValue)
            }

            val request = PreparePaymentRequest(
                service_id = ServiceId.SERVICE_ID_15,
                payment_detail_code = "MUNIS_0202",
                command = Command.MUNIS,
                curr_level_position = "1",
                params = params
            )
            val paymentService: PaymentService =
                dbHelper.getServiceByContractId(ServiceId.SERVICE_ID_15)!!
            val amount = binding.etBankAmount.editableText.toString().replace(" ", "").trim()
            viewModel.preparePaymentRequest(getClientToken(), request).observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val bundle = Bundle()
                        bundle.putSerializable("list", it.data?.service_details)
                        bundle.putSerializable("paymentService", paymentService)
                        bundle.putSerializable("templateKeyValues", templateKeyValueList)
                        bundle.putSerializable(SuccessPaymentFragment.PAYMENT_KEY_VALUES, params)
                        bundle.putSerializable("operation", "budget")
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
        } catch (e: Exception) {
            recordException(e, ::preparePaymentBank.name)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        binding.btnContinue.isEnabled(checkForError())
    }

}