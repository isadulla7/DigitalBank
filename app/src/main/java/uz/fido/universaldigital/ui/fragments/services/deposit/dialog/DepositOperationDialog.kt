package uz.fido.universaldigital.ui.fragments.services.deposit.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogDepositOperationBinding

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
        binding.withDrawPercent.setOnClickListener { onClickView.invoke("with_draw_percent") }
        binding.withDraw.setOnClickListener { onClickView.invoke("with_draw") }
        binding.edit.setOnClickListener { onClickView.invoke("edit") }
        binding.cansel.setOnClickListener { onClickView.invoke("cansel") }
        binding.delete.setOnClickListener { onClickView.invoke("delete") }
        binding.info.setOnClickListener { onClickView.invoke("info") }

    }
}