package uz.fido.universaldigital.ui.fragments.profile.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentAppThemeBinding
import uz.fido.universaldigital.ui.utils.extensions.delayOnLifecycle
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class AppThemeFragment : BaseSimpleFragment<FragmentAppThemeBinding>(
    FragmentAppThemeBinding::inflate
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initDefaultState()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.dayMode.setOnClickListener {
            setThemeLightMode()
        }
        binding.nightMode.setOnClickListener {
            setThemeDarkMode()
        }
        binding.automatic.setOnClickListener {
            setFollowSystem()
        }
    }

    private fun initDefaultState() {
        when (Paper.book().read<Int>(Const.APP_THEME)) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                selectDark()
            }

            AppCompatDelegate.MODE_NIGHT_NO -> {
                selectLight()
            }

            else -> {
                selectDefault()
            }
        }
    }

    private fun setFollowSystem() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        selectDefault()
    }

    private fun setThemeDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_YES)
        selectDark()
    }

    private fun setThemeLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_NO)
        selectLight()
    }

    private fun recreateActivity() {
        binding.dayMode.delayOnLifecycle(10, Dispatchers.Main) {
            requireActivity().recreate()
        }
    }

    private fun selectDark() {
        binding.switchDarkmode.isChecked = true
        binding.switchDaymode.isChecked = false
        binding.switchAuto.isChecked = false
    }

    private fun selectLight() {
        binding.switchDaymode.isChecked = true
        binding.switchDarkmode.isChecked = false
        binding.switchAuto.isChecked = false
    }

    private fun selectDefault() {
        binding.switchAuto.isChecked = true
        binding.switchDaymode.isChecked = false
        binding.switchDarkmode.isChecked = false
    }
}