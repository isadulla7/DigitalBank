package uz.fido.universaldigital.ui.fragments.profile.identification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentIdentificationPrivacyBinding

@AndroidEntryPoint
class IdentificationPrivacyFragment :
    BaseFragment<FragmentIdentificationPrivacyBinding, IdentificationViewModel>(
        FragmentIdentificationPrivacyBinding::inflate, IdentificationViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)


    }

}