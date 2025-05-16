package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_safety

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCardSafetyBinding
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop


@AndroidEntryPoint
class CardSafetyFragment : BaseFragment<FragmentCardSafetyBinding, UtilsViewModel>(
    FragmentCardSafetyBinding::inflate, UtilsViewModel::class.java
) {

    private lateinit var card: CardResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.serializable<CardResponse>(Const.CARD) as CardResponse
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
        initSetOnClickListeners()
    }

    private fun init() {
        if (card.object_type == CURRENCY_CARD) {
            binding.changePinLayout.visibility = View.GONE
            binding.safeModeLayout.visibility = View.GONE
            binding.limitLayout.visibility = View.GONE
        }
        if (card.object_type == HUMO_CARD || (card.is_our_bank == "Y" && card.object_type == UZCARD)) {
            binding.limitLayout.visibility = View.VISIBLE
        }
        binding.visaSecureLayout.isVisible = card.object_value.startsWith("46")
    }

    private fun initSetOnClickListeners() {
        binding.apply {
            appBar.setOnClickListener { pop() }
            changePinLayout.setOnClickListener {
                if (card.object_type == HUMO_CARD) {
                    goto(R.id.clearPinFragment, bundleOf(Const.CARD to card))
                }
            }
            safeModeLayout.setOnClickListener {
                goto(R.id.safeModeFragment, bundleOf(Const.CARD to card))
            }
            visaSecureLayout.setOnClickListener {
                goto(R.id.visaSecurityFragment, bundleOf(Const.CARD to card))
            }
            limitLayout.setOnClickListener {
                if (card.object_type == HUMO_CARD) {
                    goto(R.id.HUMOLimitsFragment, bundleOf(Const.CARD to card))
                } else {
                    goto(R.id.mainLimitsFragment, bundleOf(Const.CARD to card))
                }
            }
        }
    }

}