package uz.fido.universaldigital.ui.fragments.profile.identification

import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentIdentificationPrivacyBinding

@AndroidEntryPoint
class IdentificationPrivacyFragment : BaseFragment<FragmentIdentificationPrivacyBinding, IdentificationViewModel>(
    FragmentIdentificationPrivacyBinding::inflate, IdentificationViewModel::class.java
)