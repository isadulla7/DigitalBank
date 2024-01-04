package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.annotation.SuppressLint
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentEditProfileBinding
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class EditProfileFragment : BaseSimpleFragment<FragmentEditProfileBinding>(
    FragmentEditProfileBinding::inflate
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUserDetails()
        initSetOnClickListeners()
    }

    private fun initUserDetails() {
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

}