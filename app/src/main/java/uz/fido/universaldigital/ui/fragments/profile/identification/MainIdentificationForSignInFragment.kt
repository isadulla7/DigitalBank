package uz.fido.universaldigital.ui.fragments.profile.identification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainIdentificationForSignInBinding
import uz.fido.universaldigital.ui.activities.security.FaceIdActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs.UnableGetPassportDataDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainIdentificationForSignInFragment : BaseFragment<FragmentMainIdentificationForSignInBinding, IdentificationViewModel>(
    FragmentMainIdentificationForSignInBinding::inflate, IdentificationViewModel::class.java
) {

    private var passportData: String? = ""
    private var dateOfBirth: String? = ""
    private var pinfl: String? = ""
    private var isPin: Boolean? = false

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        initClickListener()
    }

    private fun initDetails() {
        binding.illustration.load(R.drawable.ic_user_identification)
        arguments?.let {
            isPin = it.getBoolean(Const.IS_PIN)
            dateOfBirth = it.getString(Const.DATE_OF_BIRTH)
            passportData = it.getString(Const.PASSPORT_DATA)
            pinfl = it.getString(Const.PINFL)
        }
    }

    private fun initClickListener() {
        binding.identificationBtn.setOnClickListener {
            getResidencyType()
        }
        binding.skipBtn.setOnClickListener { pop() }
    }

    private val faceIdActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val myIdResultCode = result.data?.getStringExtra(FaceIdActivity.CODE)
        val myIdExceptionCode = result.data?.getIntExtra(FaceIdActivity.EXCEPTION_CODE, 0)
        when {
            result.resultCode == Activity.RESULT_OK -> navigateToVerification(myIdResultCode)
            result.resultCode == Activity.RESULT_CANCELED && (myIdExceptionCode == FaceIdActivity.ERROR_CODE_WRONG_PASSPORT_DATA || myIdExceptionCode == FaceIdActivity.ERROR_CODE_OLD_PASSPORT_DATA) -> showPassportErrorDialog()
        }
    }

    private fun openFaceIdActivity(passportData: String? = null, dateOfBirth: String? = null, pinfl: String? = null, isResident: String? = null) {
        val intent = Intent(requireActivity(), FaceIdActivity::class.java)
        intent.putExtra(FaceIdActivity.CLIENT_PASSPORT, if (passportData.isNullOrEmpty()) pinfl.orEmpty() else passportData)
        intent.putExtra(FaceIdActivity.CLIENT_DATE_OF_BIRTH, dateOfBirth.orEmpty())
        intent.putExtra(FaceIdActivity.RESIDENCY_TYPE, isResident)
        faceIdActivityResult.launch(intent)
    }

    private fun navigateToVerification(myIdResultCode: String?) {
        gotoWithSlide(R.id.verificationForSignInFragment, bundleOf(FaceIdActivity.CODE to myIdResultCode, Const.IS_PIN to isPin))
    }

    private fun showPassportErrorDialog() {
        UnableGetPassportDataDialog {
            openFaceIdActivity()
        }.show(childFragmentManager, "")
    }

    private fun getResidencyType() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getUserResidency().observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it.status == Status.SUCCESS) {
                val response = it.data?.isResident
                val isResident = response.orEmpty()
                openFaceIdActivity(passportData, dateOfBirth, pinfl, isResident)
            } else {
                openFaceIdActivity(passportData, dateOfBirth, pinfl)
            }
        }
    }

}