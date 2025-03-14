package uz.fido.universaldigital.ui.fragments.profile.settings

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.base.ShakeActions
import uz.fido.universaldigital.databinding.FragmentAppActionsBinding
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.universaldigital.ui.utils.extensions.setChildrenEnable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class ActionsFragment : BaseSimpleFragment<FragmentAppActionsBinding>(
    FragmentAppActionsBinding::inflate
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        binding.radioGroup.setChildrenEnable(getFromPaper(Const.SHAKING_ACTION_STATE, "N") == "Y")
        binding.switchAction.isChecked = getFromPaper(Const.SHAKING_ACTION_STATE, "N") == "Y"
        binding.radioGroup.check(Paper.book().read<Int>(Const.SELECTED_OPTION, -1) ?: -1)
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.switchAction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveToPaper(Const.SHAKING_ACTION_STATE, "Y")
                binding.radioGroup.setChildrenEnable(true)
            } else {
                binding.radioGroup.clearCheck()
                binding.radioGroup.setChildrenEnable(false)
                saveToPaper(Const.SHAKING_ACTION_STATE, "N")
            }
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Paper.book().write(Const.SELECTED_OPTION, checkedId)
            when (checkedId) {
                binding.option1.id -> {
                    Paper.book().write(Const.SELECTED_FRAGMENT, ShakeActions.ACTION_MY_CARDS)
                }

                binding.option2.id -> {
                    Paper.book().write(Const.SELECTED_FRAGMENT, ShakeActions.ACTION_MY_CREDITS)
                }

                binding.option3.id -> {
                    Paper.book().write(Const.SELECTED_FRAGMENT, ShakeActions.ACTION_MY_DEPOSITS)
                }

                binding.option4.id -> {
                    Paper.book().write(Const.SELECTED_FRAGMENT, ShakeActions.ACTION_RATES)
                }

                binding.option5.id -> {
                    Paper.book().write(Const.SELECTED_FRAGMENT, ShakeActions.ACTION_TRANSFER)
                }
            }
        }
    }

}