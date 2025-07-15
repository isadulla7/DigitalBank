package uz.fido.universaldigital.ui.fragments.profile.identification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainIdentificationBinding
import uz.fido.universaldigital.ui.activities.security.FaceIdActivity
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainIdentificationFragment :
    BaseFragment<FragmentMainIdentificationBinding, IdentificationViewModel>(
        FragmentMainIdentificationBinding::inflate, IdentificationViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        initClickListener()
    }

    private fun initClickListener() {
        binding.identificationBtn.setOnClickListener { openFaceIdActivity() }
        binding.skipBtn.setOnClickListener { pop() }
    }

    private fun initDetails() {
        binding.illustration.load(R.drawable.ic_illustration_identification)
        arguments?.let {
            if (it.getBoolean("need_identification", false)) {
                binding.illustration.load(R.drawable.ic_illustration_identification_2)
                binding.tvTitle.text = getString(R.string.pass_identification)
                binding.tvDescription.text = getString(R.string.pass_identification_desc)
            }
        }
    }

    private val faceIdActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (requireActivity() is LoginActivity) {
                gotoWithSlide(
                    R.id.verificationInfoUserFragment,
                    bundleOf("code" to it?.data?.getStringExtra("code"))
                )
            } else {
                gotoWithSlide(
                    R.id.verificationInfoUserFragment2,
                    bundleOf("code" to it?.data?.getStringExtra("code"))
                )
            }
        }
    }


    private fun openFaceIdActivity() {
        val intent = Intent(requireActivity(), FaceIdActivity::class.java)
        intent.putExtra("mode", "strong")
        faceIdActivityResult.launch(intent)
    }

}