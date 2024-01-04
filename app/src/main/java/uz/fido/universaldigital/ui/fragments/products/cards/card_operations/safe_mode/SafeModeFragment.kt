package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.safe_mode

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.EditCardRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSafeModeBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class SafeModeFragment : BaseFragment<FragmentSafeModeBinding, MenuProductsViewModel>(
    FragmentSafeModeBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            card = this.serializable<CardResponse>(Const.CARD) as CardResponse
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.switchSafeMode.isChecked = card.safe_mode == "Y"
        binding.switchSafeMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) card.safe_mode = "Y" else card.safe_mode = "N"
            editCard(isMain = isChecked)
        }
        binding.saveButton.setOnClickListener { pop() }
    }

    private fun editCard(isMain: Boolean) {
        viewModel.editCardRequest(
            getClientToken(), EditCardRequest(
                card.object_name,
                card.is_main,
                card.object_id,
                card.bg_icon_name,
                if (isMain) "Y" else "N"
            )
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        viewModel.shouldUpdate = true
//                        val text =
//                            if (binding.switchSafeMode.isChecked) getString(R.string.safe_mode_on) else getString(
//                                R.string.safe_mode_off
//                            )
//                        showSnackbar(text)
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }
}