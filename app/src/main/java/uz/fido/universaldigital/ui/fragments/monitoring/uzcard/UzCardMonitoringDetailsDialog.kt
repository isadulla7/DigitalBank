package uz.fido.universaldigital.ui.fragments.monitoring.uzcard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogUzcardInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.format.Format

class UzCardMonitoringDetailsDialog(
    private val item: UzcardMonitoringItem,
    private val baseInterface: BaseInterface
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUzcardInfoMonitoringBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
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
        binding = DialogUzcardInfoMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        onClickView()
    }

    private fun onClickView() {
        binding.allInfo.setOnClickListener {
            dismiss()
            baseInterface.uzCardInfo(item)
        }
    }

    private fun initViews() {
        addView(getString(R.string.name), item.merchantName)
        addView(getString(R.string.terminal_id), item.terminalId)
        addView(getString(R.string.card_number), item.cardNumber)
        if (item.address.isNotEmpty() && item.address != "0") addView(getString(R.string.address), item.address)
        addView(getString(R.string.operation_type), if (item.transactionType == "credit") getString(R.string.income) else getString(R.string.outcome))
        addView(getString(R.string.date_time), item.transactionDate.substring(0, 10) + " " + item.transactionDate.substring(10, item.transactionDate.length))
        addView(
            getString(R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(item.transactionAmount)) + " UZS"
        )
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding = ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }
}