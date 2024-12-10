package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAppThemeBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.universaldigital.ui.utils.extensions.delayOnLifecycle
import uz.fido.utils.const.Const
import uz.fido.utils.libs.circular_reveal_switch.ext.setDayNightModeSwitcher
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class AppThemeFragment : BaseFragment<FragmentAppThemeBinding, MenuProfileViewModel>(
    FragmentAppThemeBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initDefaultState()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.dayMode.setDayNightModeSwitcher(toNightMode = false) {
            selectLight()
        }
        binding.nightMode.setDayNightModeSwitcher(toNightMode = true) {
            selectDark()
        }
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val toNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        binding.automatic.setDayNightModeSwitcher(toNightMode = toNightMode) {
            selectDefault()
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
        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        selectDefault()
    }

    private fun setThemeDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_YES)
        selectDark()
    }

    private fun setThemeLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        recreateActivity()
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_NO)
        selectLight()
    }

    private fun recreateActivity() {
        binding.dayMode.delayOnLifecycle(10, Dispatchers.Main) {
            requireActivity().recreate()
        }
    }

    private fun selectDark() {
        binding.switchDarkmode.setImageResource(R.drawable.ic_check_enable)
        binding.switchDaymode.setImageResource(R.drawable.ic_check_disable)
        binding.switchAuto.setImageResource(R.drawable.ic_check_disable)
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun selectLight() {
        binding.switchDaymode.setImageResource(R.drawable.ic_check_enable)
        binding.switchDarkmode.setImageResource(R.drawable.ic_check_disable)
        binding.switchAuto.setImageResource(R.drawable.ic_check_disable)
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun selectDefault() {
        binding.switchAuto.setImageResource(R.drawable.ic_check_enable)
        binding.switchDaymode.setImageResource(R.drawable.ic_check_disable)
        binding.switchDarkmode.setImageResource(R.drawable.ic_check_disable)
        Paper.book().write(Const.APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}