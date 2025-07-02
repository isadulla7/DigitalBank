package uz.fido.universaldigital.ui.fragments.profile.about_bank

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
        binding.rateWithBank.setOnClickListener { openPlaymarket() }
        binding.appShare.setOnClickListener { shareAppLink() }
    }

    private fun shareAppLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Mening Ilovam")
            putExtra(Intent.EXTRA_TEXT, "Mana ilovam: https://play.google.com/store/apps/details?id=${requireContext().packageName}")
        }

        requireContext().startActivity(Intent.createChooser(shareIntent, "Ulashish uchun tanlang"))
    }

    private fun openPlaymarket() {
        val appPackageName = requireContext().packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.setPackage("com.android.vending")
            requireContext().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            requireContext().startActivity(intent)
        }
    }

}