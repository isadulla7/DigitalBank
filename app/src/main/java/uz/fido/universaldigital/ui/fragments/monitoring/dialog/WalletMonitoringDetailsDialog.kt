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
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogUzcardInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.format.Format

class WalletMonitoringDetailsDialog(
    private val item: AccountHistory,
    private val baseInterface: BaseInterface)
    : BottomSheetDialogFragment() {

      private lateinit var binding:DialogUzcardInfoMonitoringBinding
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
        binding= DialogUzcardInfoMonitoringBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allInfo.visibility=View.GONE
        initViews()
        onClickView()
    }

    private fun onClickView() {
        binding.allInfo.setOnClickListener {
            baseInterface.walletInfoPaymentMonitoring(item)
        }
    }

    private fun initViews() {

        addView(getString(R.string.name), if (item.debitAmount == "0") item.creditAccountName else item.debitAccountName)
        addView(getString(R.string.purpose), item.purpose.toString())
        addView(getString(R.string.date_time), item.dateExecute.toString())
        addView(getString(R.string.account), (if (item.debitAmount == "0") item.creditAccount else item.debitAccount).toString())
        addView(getString(R.string.operation_type), if (item.debitAmount == "0") getString(R.string.income) else getString(R.string.outcome))
        val amount =
            if (item.debitAmount == "0") {
                "${Format.formatAmount(item.creditAmount.toString())} ${
                    when (item.creditAccount.substring(5, 8)) {
                        "000" -> {
                            "UZS"
                        }
                        "643" -> {
                            "RUB"
                        }
                        else -> {
                            "USD"
                        }
                    }
                }"

            } else {
                "${Format.formatAmount(item.debitAmount)} ${
                    when (item.creditAccount.substring(5, 8)) {
                        "000" -> {
                            "UZS"
                        }
                        "643" -> {
                            "RUB"
                        }
                        else -> {
                            "USD"
                        }
                    }
                }"
            }
        addView(getString(R.string.amount), amount)
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }
}