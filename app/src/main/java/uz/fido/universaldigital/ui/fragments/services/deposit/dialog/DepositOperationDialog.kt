package uz.fido.universaldigital.ui.fragments.services.deposit.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogDepositOperationBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit.ClientDepositFragment

class DepositOperationDialog(
    private val onClickView: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDepositOperationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDepositOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.withDraw.setOnClickListener { onClickView.invoke(ClientDepositFragment.WITH_DRAW_PERCENT) }
        binding.edit.setOnClickListener { onClickView.invoke(ClientDepositFragment.RENAME_DEPOSIT) }
        binding.close.setOnClickListener { onClickView.invoke(ClientDepositFragment.CLOSE_DEPOSIT) }
        binding.ealryClose.setOnClickListener { onClickView.invoke(ClientDepositFragment.EARLY_CLOSE_DEPOSIT) }
        binding.info.setOnClickListener { onClickView.invoke(ClientDepositFragment.DEPOSIT_INFO) }

    }
}