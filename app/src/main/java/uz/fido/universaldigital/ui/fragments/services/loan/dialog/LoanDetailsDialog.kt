package uz.fido.universaldigital.ui.fragments.services.loan.dialog

import android.app.Dialog
import android.content.Context
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
import uz.fido.universaldigital.databinding.DialogInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format

class LoanDetailsDialog(
    private val item: AccountHistory,
    private val type: String
) : BottomSheetDialogFragment() {

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
    private lateinit var binding:DialogInfoMonitoringBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DialogInfoMonitoringBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         binding.allInfo.visibility=View.GONE
        binding.repeat.visibility=View.GONE
        initViews()
    }

    private fun initViews() {
        addView(
            getString(R.string.purpose),
            (if (type == Const.TYPE_LOAN) setCreditPurpose(requireContext(), item.lnType) else item.purpose).toString()
        )
        addView(getString(R.string.transaction_number), item.NumberTrans)
        addView(getString(R.string.date_time), item.dateExecute.toString())
        addView(
            getString(R.string.amount),
            if (item.debit == "0") Format.formatAmount((item.credit!!.toDouble() / 100).toString()) + " UZS" else Format.formatAmount((item.debit.toDouble() / 100).toString()) + " UZS"
        )
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun setCreditPurpose(context: Context, lnType: String): String {
        return when (lnType) {
            "1" -> context.getString(R.string.purpose_1)
            "2", "" -> context.getString(R.string.top_up_account_for_loan)
            "3" -> context.getString(R.string.purpose_3)
            "5" -> context.getString(R.string.purpose_5)
            "7" -> context.getString(R.string.purpose_7)
            "22" -> context.getString(R.string.purpose_22)
            "46" -> context.getString(R.string.purpose_46)
            else -> ""
        }
    }
}