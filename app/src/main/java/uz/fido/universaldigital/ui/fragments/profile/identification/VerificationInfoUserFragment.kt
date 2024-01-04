package uz.fido.universaldigital.ui.fragments.profile.identification

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.CodeAndName
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.network.domain.model.my_id.MyIdMeResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentVerificationInfoUserBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.profile.identification.adapters.CodeAndNameAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class VerificationInfoUserFragment :
    BaseFragment<FragmentVerificationInfoUserBinding, IdentificationViewModel>(
        FragmentVerificationInfoUserBinding::inflate, IdentificationViewModel::class.java
    ) {

    private var myIdMe: MyIdMeResponse? = null
    private var fio: String = ""

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUI()
        getAccessToken()
    }

    private fun initUI() {
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
        val checkIdentification = CheckIdentification(
            doc_serial = myIdMe!!.profile.doc_data.pass_data.substring(0, 2),
            doc_number = myIdMe!!.profile.doc_data.pass_data.substring(2),
            birthday = myIdMe!!.profile.common_data.birth_date,
            doc_type = myIdMe!!.profile.doc_data.doc_type_id
        )
        viewModel.identification(getClientToken(), checkIdentification)
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        if (activity is MainActivity) {
                            gotoWithSlide(
                                R.id.successVerificationFragment2, bundleOf("fio" to fio)
                            )
                        } else {
                            gotoWithSlide(
                                R.id.successVerificationFragment, bundleOf("fio" to fio)
                            )
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun getAccessToken() {
        showProgress()
        viewModel.checkPassport(
            MyIdGetAccessTokenRequest(
                code = requireArguments().getString("code").toString()
            )
        ).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { myIdMeResponse ->
                        if (myIdMeResponse.profile != null) {
                            myIdMe = myIdMeResponse
                            initList()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_occured),
                                Toast.LENGTH_SHORT
                            ).show()
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
            val fio = "${commonData.first_name} ${commonData.last_name} ${commonData.middle_name}"
            this.fio = fio

            val list = ArrayList<CodeAndName>()
            list.add(CodeAndName(name = getString(R.string.fio), value = fio))
            list.add(
                CodeAndName(
                    name = getString(R.string.birth_date), value = commonData.birth_date
                )
            )
            list.add(
                CodeAndName(
                    name = getString(R.string.citizenship), value = commonData.citizenship
                )
            )
            list.add(CodeAndName(name = getString(R.string.passport_no), value = docData.pass_data))
            list.add(
                CodeAndName(
                    name = getString(R.string.date_of_expire), value = docData.expiry_date
                )
            )
            saveUserDetails(it)
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = CodeAndNameAdapter(list)
            }
        }
    }

    private fun saveUserDetails(myIdResponse: MyIdMeResponse?) {
        myIdResponse?.let { response ->
            val commonData = response.profile.common_data
            val docData = response.profile.doc_data
            val userName = "${commonData.first_name} ${commonData.last_name}"
            val fullName =
                "${commonData.first_name} ${commonData.last_name} ${commonData.middle_name}"
            val birthday = commonData.birth_date
            val citizenship = commonData.citizenship
            val passwordDetails = docData.pass_data
            val passExpireDate = docData.expiry_date
            val issuedDate = docData.issued_date
            val pinfl = commonData.pinfl

            Paper.book().write(Const.USER_NAME, userName)
            Paper.book().write(Const.USER_FULL_NAME, fullName)
            Paper.book().write(Const.USER_BIRTHDAY, birthday)
            Paper.book().write(Const.USER_CITIZENSHIP, citizenship)
            Paper.book().write(Const.USER_PASSWORD_DATA, passwordDetails)
            Paper.book().write(Const.USER_PASS_GIVEN_DATE, issuedDate)
            Paper.book().write(Const.USER_PASS_EXPIRE_DATE, passExpireDate)
            Paper.book().write(Const.USER_PINFL, pinfl)
        }
    }
}