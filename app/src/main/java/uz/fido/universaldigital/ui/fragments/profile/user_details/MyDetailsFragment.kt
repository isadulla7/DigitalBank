package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.domain.model.my_id.CodeAndName
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMyDetailsBinding
import uz.fido.universaldigital.ui.fragments.profile.identification.adapters.CodeAndNameAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class MyDetailsFragment : BaseSimpleFragment<FragmentMyDetailsBinding>(
    FragmentMyDetailsBinding::inflate
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUserDetails()
        initSetOnClickListeners()
    }

    private fun initUserDetails() {
        val list = ArrayList<CodeAndName>()
        list.add(
            CodeAndName(
                name = getString(R.string.fio),
                value = Paper.book().read(Const.USER_FULL_NAME, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.birth_date),
                value = Paper.book().read(Const.USER_BIRTHDAY, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.citizenship),
                value = Paper.book().read(Const.USER_CITIZENSHIP, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.passport_no),
                value = Paper.book().read(Const.USER_PASSWORD_DATA, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.given_date),
                value = Paper.book().read(Const.USER_PASS_GIVEN_DATE, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.date_of_expire),
                value = Paper.book().read(Const.USER_PASS_EXPIRE_DATE, getString(R.string.unknown))
            )
        )
        list.add(
            CodeAndName(
                name = getString(R.string.address_mail),
                value = Paper.book().read(Const.EMAIL, getString(R.string.unknown))
            )
        )
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CodeAndNameAdapter(list)
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            gotoWithSlide(R.id.editProfileFragment)
        }
    }

}