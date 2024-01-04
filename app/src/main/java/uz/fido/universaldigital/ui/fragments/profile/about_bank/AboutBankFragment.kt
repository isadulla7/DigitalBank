package uz.fido.universaldigital.ui.fragments.profile.about_bank

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAboutBankBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class AboutBankFragment : BaseFragment<FragmentAboutBankBinding, MenuProfileViewModel>(
    FragmentAboutBankBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.connectWithBank.setOnClickListener { gotoWithSlide(R.id.connectWithBankFragment) }
        binding.publicOffer.setOnClickListener { gotoWithSlide(R.id.publicOfferFragment) }
        binding.atmAndFilials.setOnClickListener { goto(R.id.mainBranchesFragment) }
    }

}