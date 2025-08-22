package uz.fido.universaldigital.ui.fragments.transfers

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMenuTransfersBinding
import uz.fido.universaldigital.ui.fragments.transfers.adapter.MenuTransfersAdapter
import uz.fido.universaldigital.ui.utils.extensions.getTransferTypes
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide

@AndroidEntryPoint
class MenuTransfersFragment : BaseSimpleFragment<FragmentMenuTransfersBinding>(FragmentMenuTransfersBinding::inflate) {

    private val transferTypesAdapter by lazy {
        MenuTransfersAdapter(getTransferTypes()) { transferType ->
            initSetOnClickListeners(transferType)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTransferTypes()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.search.setOnClickListener {
            gotoWithSlide(R.id.searchEveryWhereFragment)
        }
    }

    private fun setupTransferTypes() {
        binding.transferTypes.adapter = transferTypesAdapter
    }

    private fun initSetOnClickListeners(transferTypeId: Int) {
        when (transferTypeId) {
            100 -> goto(R.id.transferToCardFragment)
            200 -> goto(R.id.overMyCardsFragment)
            300 -> goto(R.id.transferByPhoneFragment)
            400 -> goto(R.id.transferByWalletFragment)
            500 -> goto(R.id.transferToAccountFragment)
            600 -> goto(R.id.requestMoneyFragment)
            700 -> goto(R.id.newConversionFragment)
        }
    }

}