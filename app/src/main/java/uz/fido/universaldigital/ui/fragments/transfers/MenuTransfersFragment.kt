package uz.fido.universaldigital.ui.fragments.transfers

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMenuTransfersBinding
import uz.fido.universaldigital.ui.fragments.transfers.adapter.MenuTransfersAdapter
import uz.fido.universaldigital.ui.utils.extensions.getTransferTypes
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide

@AndroidEntryPoint
class MenuTransfersFragment :
    BaseSimpleFragment<FragmentMenuTransfersBinding>(FragmentMenuTransfersBinding::inflate) {

    private lateinit var transferTypesAdapter: MenuTransfersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferTypesAdapter = MenuTransfersAdapter(requireContext(), getTransferTypes()) {
            initSetOnClickListeners(it)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initTransferTypes()
        binding.search.setOnClickListener {
            gotoWithSlide(R.id.searchEveryWhereFragment)
        }
    }

    private fun initSetOnClickListeners(transferTypeId: Int) {
        when (transferTypeId) {
            100 -> goto(R.id.transferToCardFragment)
            200 -> goto(R.id.overMyCardsFragment)
            300 -> goto(R.id.transferByPhoneFragment)
            400 -> goto(R.id.transferByWalletFragment)
            500 -> /*goto(R.id.conversionFragment)*/functionInProgress()
            600 -> /*goto(R.id.requestMoneyFragment)*/functionInProgress()
        }
    }

    private fun initTransferTypes() {
        binding.transferTypes.adapter = transferTypesAdapter
    }

}