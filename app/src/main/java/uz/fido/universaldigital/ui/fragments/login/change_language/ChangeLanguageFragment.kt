package uz.fido.universaldigital.ui.fragments.login.change_language

import android.content.Intent
import android.os.Bundle
import android.view.View
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentChangeLanguageBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.utils.utility.fragment.pop

class ChangeLanguageFragment : BaseSimpleFragment<FragmentChangeLanguageBinding>(
    FragmentChangeLanguageBinding::inflate
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener {
            pop()
        }
        binding.signInButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

}