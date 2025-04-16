package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogScanCardBinding

class ChooseScanCardOptionDialog(
    private var onCameraClickListener: () -> Unit,
    private var onNFCClickListener: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogScanCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogScanCardBinding.inflate(inflater, container, false)
        binding.btnCamera.setOnClickListener {
            onCameraClickListener.invoke()
            dismiss()
        }
        binding.btnNfc.setOnClickListener {
            onNFCClickListener.invoke()
            dismiss()
        }
        return binding.root
    }
}