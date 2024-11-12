package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentNewDesignOnboardBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const

@AndroidEntryPoint
class NewDesignOnboardingPage : BaseSimpleFragment<FragmentNewDesignOnboardBinding>(FragmentNewDesignOnboardBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.switchNewDesign.setOnClickListener {
            if (getFromPaper(Const.NEW_DESIGN, "N") == "Y") {
                saveToPaper(Const.NEW_DESIGN, "N")
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(Const.NEW_DESIGN, false)
                requireActivity().finishAffinity()
                startActivity(intent)
            } else {
                saveToPaper(Const.NEW_DESIGN, "Y")
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(Const.NEW_DESIGN, true)
                requireActivity().finishAffinity()
                startActivity(intent)
            }
        }
    }

}