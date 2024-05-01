package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMyDetailsBinding
import uz.fido.universaldigital.ui.fragments.profile.identification.adapters.CodeAndNameAdapter
import uz.fido.universaldigital.ui.utils.extensions.fixQuestionMarks
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
            getString(R.string.name) to Paper.book().read(Const.FIRST_NAME, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.surname) to Paper.book().read(Const.LAST_NAME, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.patronic) to Paper.book().read(Const.PATRONYMIC, getString(R.string.unknown)).fixQuestionMarks(),
            getString(R.string.birth_date) to Paper.book().read(Const.USER_BIRTHDAY, getString(R.string.unknown)),
            getString(R.string.passport_no) to Paper.book().read(Const.USER_PASSWORD_DATA, getString(R.string.unknown)),
            getString(R.string.given_date) to Paper.book().read(Const.USER_PASS_GIVEN_DATE, getString(R.string.unknown)),
            getString(R.string.address_mail) to Paper.book().read(Const.EMAIL, getString(R.string.unknown))
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