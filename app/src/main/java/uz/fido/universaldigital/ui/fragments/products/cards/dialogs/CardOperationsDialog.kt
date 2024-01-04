package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogCardOperationBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isBankCard

class CardOperationsDialog(
    private var card: CardResponse,
    private var listener: View.OnClickListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogCardOperationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogCardOperationBinding.inflate(inflater, container, false)
        init()
        setOnClickListeners()
        return binding.root
    }

    private fun init() {
//        binding.safeMode.isVisible = card.object_type != "TET"
//        binding.setLimits.isVisible = card.object_type != "TET"
        binding.transferToCard.isVisible = card.object_type != "TET"
        binding.blockCard.isVisible = isBankCard(card)
        if (card.state == "P") {
            binding.transferToCard.visibility = View.GONE
            binding.setLimits.visibility = View.GONE
            binding.monitoring.visibility = View.GONE
            binding.cardSettings.visibility = View.GONE
            binding.blockCard.visibility = View.GONE
            binding.blockCard.visibility = View.GONE
        }
    }

    private fun setOnClickListeners() {
        binding.transferToCard.setOnClickListener(listener)
        binding.monitoring.setOnClickListener(listener)
        binding.setLimits.setOnClickListener(listener)
        binding.requisites.setOnClickListener(listener)
        binding.safety.setOnClickListener(listener)
        binding.cardSettings.setOnClickListener(listener)
        binding.blockCard.setOnClickListener(listener)
        binding.deleteCard.setOnClickListener(listener)
    }
}