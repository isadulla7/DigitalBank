package uz.fido.universaldigital.ui.fragments.login.sign_up

import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignInBinding

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignInBinding, SignUpViewModel>(
    FragmentSignInBinding::inflate, SignUpViewModel::class.java
) {

}