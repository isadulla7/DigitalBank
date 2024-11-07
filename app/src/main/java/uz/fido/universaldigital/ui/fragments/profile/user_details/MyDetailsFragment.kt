package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMyDetailsBinding
import uz.fido.universaldigital.ui.fragments.profile.identification.adapters.CodeAndNameAdapter
import uz.fido.universaldigital.ui.utils.extensions.fixQuestionMarks
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MyDetailsFragment : BaseSimpleFragment<FragmentMyDetailsBinding>(FragmentMyDetailsBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUserDetails()
        initSetOnClickListeners()
    }

    private fun initUserDetails() {
        val details = mapOf(
            getString(R.string.name) to getFromPaper(Const.FIRST_NAME, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.surname) to getFromPaper(Const.LAST_NAME, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.patronymic) to getFromPaper(Const.PATRONYMIC, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.birth_date) to getFromPaper(Const.USER_BIRTHDAY, getString(R.string.unknown)),
            getString(R.string.passport_no) to getFromPaper(Const.USER_PASSWORD_DATA, getString(R.string.unknown)),
            getString(R.string.given_date) to getFromPaper(Const.USER_PASS_GIVEN_DATE, getString(R.string.unknown)),
            getString(R.string.address_mail) to getFromPaper(Const.EMAIL, getString(R.string.unknown))
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CodeAndNameAdapter(details)
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            gotoWithSlide(R.id.editProfileFragment)
        }
    }

}