package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogWalletOperationsBinding

class WalletOperationsDialog(private var listener: View.OnClickListener, val cardResponse: CardResponse) :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogWalletOperationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogWalletOperationsBinding.inflate(inflater, container, false)
        setOnClickListeners()
        return binding.root
    }


    private fun setOnClickListeners() {
        binding.deleteWallet.setOnClickListener(listener)
        binding.renameWallet.setOnClickListener(listener)
        binding.topUp.setOnClickListener(listener)
        binding.takeOff.setOnClickListener(listener)
        binding.walletRequisites.setOnClickListener(listener)
        binding.walletMonitoring.setOnClickListener(listener)
    }
}