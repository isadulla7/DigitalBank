package uz.fido.universaldigital.ui.fragments.transfers.swift_transfer

import android.os.Bundle
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentSwiftTransferBinding
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class SwiftTransferFragment :
    BaseSimpleFragment<FragmentSwiftTransferBinding>(FragmentSwiftTransferBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.swiftUsd.setOnClickListener {
            gotoWithSlide(
                R.id.initTransferDetailsFragment, bundleOf(
                    InitTransferDetailsFragment.BANK_TRANSFER_OPERATION to InitTransferDetailsFragment.BANK_OPERATION_CREATE,
                    InitTransferDetailsFragment.TRANSFER_CURRENCY to InitTransferDetailsFragment.TRANSFER_DOLLAR
                )
            )
        }
        binding.swiftEur.setOnClickListener {
            gotoWithSlide(
                R.id.initTransferDetailsFragment, bundleOf(
                    InitTransferDetailsFragment.BANK_TRANSFER_OPERATION to InitTransferDetailsFragment.BANK_OPERATION_CREATE,
                    InitTransferDetailsFragment.TRANSFER_CURRENCY to InitTransferDetailsFragment.TRANSFER_EURO
                )
            )
        }
    }

}