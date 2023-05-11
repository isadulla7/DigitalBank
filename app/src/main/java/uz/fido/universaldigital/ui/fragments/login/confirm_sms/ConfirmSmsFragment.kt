package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmSmsBinding
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class ConfirmSmsFragment : BaseFragment<FragmentConfirmSmsBinding, ConfirmSmsViewModel>(
    FragmentConfirmSmsBinding::inflate, ConfirmSmsViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}