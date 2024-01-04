package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMenuProfileBinding
import uz.fido.universaldigital.databinding.FragmentTransferToAccountBinding
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class TransferToAccountFragment :
    BaseSimpleFragment<FragmentTransferToAccountBinding>(FragmentTransferToAccountBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.uzsAccount.setOnClickListener { gotoWithSlide(R.id.transferToUzsAccountFragment) }
        binding.usdAccount.setOnClickListener { gotoWithSlide(R.id.transferToUsdAccountFragment) }
        binding.budget.setOnClickListener { gotoWithSlide(R.id.transferToBudgetFragment) }
    }

}