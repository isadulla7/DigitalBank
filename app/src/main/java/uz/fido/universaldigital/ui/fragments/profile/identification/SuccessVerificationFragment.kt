package uz.fido.universaldigital.ui.fragments.profile.identification

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSuccessVerificationBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto

@AndroidEntryPoint
class SuccessVerificationFragment :
    BaseFragment<FragmentSuccessVerificationBinding, IdentificationViewModel>(
        FragmentSuccessVerificationBinding::inflate, IdentificationViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadIllustration()
        changeUserStatus()
        binding.btnContinue.isEnabled(true)
        binding.btnContinue.setOnClickListener {
            if (activity is MainActivity) {
                goto(
                    R.id.action_successVerificationFragment2_to_menuProfileFragment,
                    bundleOf(Const.USER_IDENTIFIED to true)
                )
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun changeUserStatus() {
        val signInResponse = Paper.book().read<SignInResponse>(Const.PAPER_CLIENT_INFO)
        signInResponse.user_status_id = "1"
        Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
    }

    private fun loadIllustration() {
        binding.imageView.load(R.drawable.ic_illustration_success)
    }

}