package uz.fido.universaldigital.ui.fragments.payment.my_home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.monitoring.home.ItemHomeHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogUzcardInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_USD
import uz.fido.utils.format.Format

class MyHomeHistoryDialog(
    private val item: ItemHomeHistory
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
        binding.allInfo.visibility = View.GONE
        binding.allInfo.setOnClickListener {
        }
    }

    private fun initViews() {
        addView(getString(R.string.date_time), item.create_date.toString())
        addView(getString(R.string.transaction_number), item.request_id)
        addView(getString(R.string.personal_account), item.to_object_value)
        addView(
            getString(R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + if (item.currency_code == CURRENCY_CODE_USD) " $" else " UZS"
        )
        val state = if (item.state_id == "1") {
            getString(R.string.successfully)
        } else {
            getString(R.string.waiting)
        }
        addView(getString(R.string.status), state)
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }
}