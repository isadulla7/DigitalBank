package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogShareCardNumberBinding
import uz.fido.utils.const.CardConst.WALLET

class ShareCardNumberDialog(
    private var item: CardResponse, private val baseInterface: BaseInterface
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogShareCardNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogShareCardNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (item.object_type == WALLET) {
            binding.tvCopy.text = getString(R.string.copy_wallet_number)
        }
        binding.layoutCopyCardNumber.setOnClickListener {
            baseInterface.copyCardNumber()
            dismiss()
        }
        binding.layoutShare.setOnClickListener {
            baseInterface.shareCardNumber()
            dismiss()
        }
    }
}