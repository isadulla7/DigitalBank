package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.format.Format

class InfoMonitoringDialog(
    private val localMonitoring: LocalMonitoring,
    private val searchDateResponse: SearchDataResponse? = null,
    private val fullInfo: (localeMonitoring: LocalMonitoring) -> Unit,
    private val repeatPayment: (localeMonitoring: LocalMonitoring) -> Unit,
    private val returnPayment: (localeMonitoring: LocalMonitoring) -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogInfoMonitoringBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForButton()
        initSetOnClickListeners()
        init()
    }

    private fun initSetOnClickListeners() {
        binding.repeat.setOnClickListener {
            if (localMonitoring.tran_type == "credit") {
                if (searchDateResponse != null)
                    if (searchDateResponse.request_code == "P2P") {
                        dismiss()
                        returnPayment.invoke(localMonitoring)
                    } else {
                        return@setOnClickListener
                    }
            } else {
                dismiss()
                repeatPayment.invoke(localMonitoring)
            }
        }
        binding.allInfo.setOnClickListener {
            dismiss()
            fullInfo.invoke(localMonitoring)
        }
    }

    private fun checkForButton() {
        if (isBadServiceIds(localMonitoring)) {
            binding.repeat.visibility = View.GONE
        }
        if (localMonitoring.tran_type == "credit") {
            if (searchDateResponse != null) {
                if (searchDateResponse.request_code == "P2P") {
                    binding.tvRepeat.text = getString(R.string.return_text)
                } else {
                    binding.repeat.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {
        try {
            if (searchDateResponse != null) {
                when (searchDateResponse.request_code) {
                    "P2P", "CONVERSION" -> {
                        if (searchDateResponse.from_object_value != null) {
                            addView(getString(R.string.sender_card), Format.formatCardNumberForCheque(searchDateResponse.from_object_value.orEmpty()))
                            if (!searchDateResponse.from_embossed_name.isNullOrEmpty()) {
                                addView(getString(R.string.sender_name), searchDateResponse.from_embossed_name.orEmpty())
                            }
                        }
                        if (searchDateResponse.to_object_value != null) {
                            addView(getString(R.string.receiver_card), Format.formatCardNumberForCheque(searchDateResponse.to_object_value.orEmpty()))
                            if (!searchDateResponse.to_embossed_name.isNullOrEmpty()) {
                                addView(getString(R.string.receiver_name), searchDateResponse.to_embossed_name.orEmpty())
                            }
                        }
                        initViews(isRequired = false, isPayment = false)
                    }

                    "CREATE_PAYMENT" -> {
                        initViews(isRequired = true, isPayment = true)
                    }

                    else -> initViews(isRequired = true, isPayment = true)
                }
            } else initViews(isRequired = true, isPayment = true)
        } catch (e: Exception) {
            recordException(e, ::init.name)
        }
    }

    private fun initViews(isRequired: Boolean, isPayment: Boolean) {
        addView(getString(R.string.service), localMonitoring.name)
        searchDateResponse?.params?.get("FIO")?.let { addView(getString(R.string.fio), it) }
        searchDateResponse?.params?.get("FIO_ABONENT")?.let { addView(getString(R.string.fio), it) }
        searchDateResponse?.params?.get("ADDRESS")?.let { addView(getString(R.string.address), it) }
        addView(getString(R.string.date_time), localMonitoring.created_date)
        addView(getString(R.string.transaction_number), localMonitoring.request_id)
        if (isRequired && localMonitoring.partner_obj.isNotEmpty()) {
            if (localMonitoring.to_obj_name.isNotEmpty()) {
                if (localMonitoring.partner_obj.startsWith("AUZ")) {
                    addView(
                        getString(R.string.wallet_number),
                        if (localMonitoring.object_value.length == 16) Format.formatCardNumber(localMonitoring.partner_obj) else localMonitoring.partner_obj
                    )
                } else {
                    addView(
                        localMonitoring.to_obj_name, if (localMonitoring.object_value.length == 16) Format.formatCardNumber(localMonitoring.partner_obj) else localMonitoring.partner_obj
                    )
                }
            } else {
                addView(
                    getString(R.string.personal_account),
                    if (localMonitoring.object_value.length == 16) Format.formatCardNumber(localMonitoring.partner_obj) else localMonitoring.partner_obj
                )
            }
        }

        if (isPayment && localMonitoring.object_value.isNotEmpty() && !localMonitoring.partner_obj.startsWith("AUZ")) {
            addView(
                getString(R.string.choose_card_text),
                if (localMonitoring.object_value.length == 16) Format.formatCardNumber(localMonitoring.object_value) else Format.formatWalletNumber(
                    localMonitoring.object_value
                )
            )
        }

        addView(getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(localMonitoring.amount)) + " ${Format.currencyCode(localMonitoring.currency_code)}")
        val state = if (localMonitoring.state_id == "1") {
            getString(R.string.successfully)
        } else {
            getString(R.string.waiting)
        }
        if (localMonitoring.fee_amount.isNotEmpty() && localMonitoring.fee_percent.isNotEmpty())
            addView(getString(R.string.commission), "${localMonitoring.fee_amount.toDouble() / 100.toDouble()} UZS (${localMonitoring.fee_percent}%)")

        addView(getString(R.string.status), state)
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding = ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun isBadServiceIds(localMonitoring: LocalMonitoring): Boolean {
        return when (localMonitoring.service_id) {
            "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10", "-11", "-19" -> true
            else -> false
        }
    }
}