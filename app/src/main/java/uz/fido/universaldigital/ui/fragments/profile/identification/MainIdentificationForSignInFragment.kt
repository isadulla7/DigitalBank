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
import uz.fido.universaldigital.databinding.FragmentMainIdentificationForSignInBinding
import uz.fido.universaldigital.ui.activities.FaceIdActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.UnableGetPassportDataDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainIdentificationForSignInFragment : BaseFragment<FragmentMainIdentificationForSignInBinding, IdentificationViewModel>(
    FragmentMainIdentificationForSignInBinding::inflate, IdentificationViewModel::class.java
) {

    private var passportData: String? = ""
    private var dateOfBirth: String? = ""
    private var isPin: Boolean? = false

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        initClickListener()
    }

    private fun initClickListener() {
        binding.identificationBtn.setOnClickListener { openFaceIdActivity(passportData, dateOfBirth) }
        binding.skipBtn.setOnClickListener { pop() }
    }

    private fun initDetails() {
        binding.illustration.load(R.drawable.ic_illustration_identification)
        arguments?.let {
            isPin = it.getBoolean(Const.IS_PIN)
            passportData = it.getString(Const.PASSPORT_DATA)
            dateOfBirth = it.getString(Const.DATE_OF_BIRTH)
        }
    }

    private val faceIdActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val myIdResultCode = it.data?.getStringExtra("code")
            gotoWithSlide(
                R.id.verificationForSignInFragment,
                bundleOf("code" to myIdResultCode, Const.IS_PIN to isPin)
            )
        } else {
            val myIdResultCode = it.data?.getStringExtra("code")
            if (myIdResultCode == FaceIdActivity.ERROR_CODE_WRONG_PASSPORT_DATA) {
                UnableGetPassportDataDialog {
                    openFaceIdActivity()
                }.show(childFragmentManager, "")
            }
        }
    }

    private fun openFaceIdActivity(passportData: String? = null, dateOfBirth: String? = null) {
        val intent = Intent(requireActivity(), FaceIdActivity::class.java)
        intent.putExtra(FaceIdActivity.MODE, FaceIdActivity.STRONG)
        intent.putExtra(FaceIdActivity.CLIENT_PASSPORT, passportData.orEmpty())
        intent.putExtra(FaceIdActivity.CLIENT_DATE_OF_BIRTH, dateOfBirth.orEmpty())
        faceIdActivityResult.launch(intent)
    }

}