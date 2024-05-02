package uz.fido.universaldigital.ui.fragments.services.loan.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogCreditOperationBinding

class CreditOperationDialog(
    private var onClick: (String) -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogCreditOperationBinding

    companion object {
        const val INFO_CREDIT = "loan_information"
        const val REQUISITES = "requisites"
        const val REPAYMENT_SCHEDULE = "repayment_schedule"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogCreditOperationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loanInformation.setOnClickListener {
            onClick.invoke(INFO_CREDIT)
        }

        binding.requisites.setOnClickListener {
            onClick.invoke(REQUISITES)
        }

        binding.repaymentSchedule.setOnClickListener {
            onClick.invoke(REPAYMENT_SCHEDULE)
        }
    }

}