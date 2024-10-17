package uz.fido.universaldigital.ui.fragments.profile.saved_cheques

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogSavedChequeOperationBinding
import java.io.File

class SavedChequeOperationDialog(
    private val file: File,
    private val onDeleteClicked: (File) -> Unit,
    private val onShareClicked: (File) -> Unit,
    private val onViewClicked: (File) -> Unit
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

    private lateinit var binding: DialogSavedChequeOperationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSavedChequeOperationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textName.text = file.name
        binding.open.setOnClickListener {
            onViewClicked.invoke(file)
            dismiss()
        }
        binding.delete.setOnClickListener {
            onDeleteClicked.invoke(file)
            dismiss()
        }
        binding.share.setOnClickListener {
            onShareClicked.invoke(file)
            dismiss()
        }
    }


}