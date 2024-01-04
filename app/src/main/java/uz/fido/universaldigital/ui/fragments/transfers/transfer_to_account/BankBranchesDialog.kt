package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.databinding.FragmentBankBranchesBinding
import uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.budget.BranchMfoAdapter

class BankBranchesDialog(
    private val branches: ArrayList<Branches>,
    private val onCLick: (String, String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var bankBranchesAdapter: BranchMfoAdapter

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

    private lateinit var binding: FragmentBankBranchesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBankBranchesBinding.inflate(inflater, container, false)

        initBranchesRv()
        return binding.root
    }

    private fun initBranchesRv() {
        bankBranchesAdapter = BranchMfoAdapter { mfo, name ->
            onCLick.invoke(mfo, name)
            dismiss()
        }
        bankBranchesAdapter.submitList(branches)
        binding.branches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bankBranchesAdapter
        }
    }

}