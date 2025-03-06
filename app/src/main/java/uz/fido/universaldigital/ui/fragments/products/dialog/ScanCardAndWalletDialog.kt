package uz.fido.universaldigital.ui.fragments.products.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogScanCardWalletBinding

class ScanCardAndWalletDialog(
    private var scanCardClick: () -> Unit,
    private var contactClick: () -> Unit,
    private var walletClick: () -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogScanCardWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogScanCardWalletBinding.inflate(inflater, container, false)
         binding.scanCard.setOnClickListener { scanCardClick() }
         binding.contact.setOnClickListener { contactClick() }
         binding.wallet.setOnClickListener { walletClick() }
        return binding.root
    }
}