package uz.fido.universaldigital.ui.fragments.profile.identification

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.network.domain.model.my_id.MyIdMeResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentVerificationInfoUserBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.profile.identification.adapters.CodeAndNameAdapter
import uz.fido.universaldigital.ui.utils.extensions.pendingTransition
import uz.fido.utils.const.Const
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class VerificationForSignInFragment : BaseFragment<FragmentVerificationInfoUserBinding, IdentificationViewModel>(
    FragmentVerificationInfoUserBinding::inflate, IdentificationViewModel::class.java
) {

    private var myIdMe: MyIdMeResponse? = null
    private var fio: String = ""
    private var isPin: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            isPin = requireArguments().getBoolean(Const.IS_PIN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setupListeners()
        getAccessToken()
        setTermsOfUseColor()
    }

    private fun setupListeners() {
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.identificationBtn.isEnabled = isChecked
        }
        binding.identificationBtn.setOnClickListener {
            if (binding.checkBox.isChecked) {
                if (myIdMe != null) {
                    checkForIdentification()
                } else {
                    getAccessToken()
                }
            } else {
                showSnackbar(getString(R.string.please_accept_privacy))
            }
        }
        binding.skipBtn.setOnClickListener {
            pop()
        }
    }

    private fun checkForIdentification() {
        showProgress()
        myIdMe?.let { myIdMeResponse ->
            val checkIdentification = CheckIdentification(
                doc_serial = myIdMeResponse.profile.doc_data.pass_data.substring(0, 2),
                doc_number = myIdMeResponse.profile.doc_data.pass_data.substring(2),
                birthday = myIdMeResponse.profile.common_data.birth_date,
                doc_type = myIdMeResponse.profile.doc_data.doc_type_id,
                pnfl = myIdMeResponse.profile.common_data.pinfl
            )
            viewModel.identification(getClientToken(), checkIdentification).observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        if (isPin == true) {
                            openMainActivity()
                        } else {
                            val bundle = bundleOf(PinCodeFragment.PIN_OPERATION to PinCodeFragment.PIN_OPERATION_SET_PIN)
                            gotoWithSlide(R.id.action_verificationForSignInFragment_to_pinCodeFragment, bundle)
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun getAccessToken() {
        showProgress()
        viewModel.checkPassport(MyIdGetAccessTokenRequest(code = requireArguments().getString("code").toString())).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { myIdMeResponse ->
                        if (myIdMeResponse.profile != null) {
                            myIdMe = myIdMeResponse
                            initList()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                            pop()
                        }
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initList() {
        myIdMe?.let {
            val commonData = it.profile.common_data
            val docData = it.profile.doc_data
            val fio = "${commonData.first_name} ${commonData.last_name} ${commonData.middle_name}".replace("?", "‘")
            this.fio = fio

            val details = mapOf(
                getString(R.string.fio) to fio,
                getString(R.string.birth_date) to commonData.birth_date,
                getString(R.string.citizenship) to commonData.citizenship,
                getString(R.string.passport_no) to docData.pass_data,
                getString(R.string.date_of_expire) to (docData.expiry_date ?: "")
            )
            saveUserDetails(it)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = CodeAndNameAdapter(details)
            }
        }
    }

    private fun saveUserDetails(myIdResponse: MyIdMeResponse?) {
        myIdResponse?.let { response ->
            val commonData = response.profile.common_data
            val docData = response.profile.doc_data
            saveToSecureStore(Const.FIRST_NAME, commonData.first_name)
            saveToSecureStore(Const.LAST_NAME, commonData.last_name)
            saveToSecureStore(Const.PATRONYMIC, commonData.middle_name)
            saveToSecureStore(Const.USER_BIRTHDAY, commonData.birth_date)
            saveToSecureStore(Const.USER_PASSWORD_DATA, docData.pass_data)
            saveToSecureStore(Const.USER_PASS_GIVEN_DATE, docData.issued_date)
        }
    }

    private fun setTermsOfUseColor() {
        binding.textSingUpTerms.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setLinkTextColor(
                ContextCompat.getColor(requireContext(), R.color.brandRedColor)
            )
        }
    }

    private fun openMainActivity() {
        CoroutineScope(Dispatchers.Default).launch {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().pendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            requireActivity().finish()
        }
    }

}