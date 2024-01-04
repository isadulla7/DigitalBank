package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSettingsBinding
import uz.fido.universaldigital.databinding.FragmentSettingsChangeLangBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.LocaleHelper

@AndroidEntryPoint
class ChangeLanguageFragment :
    BaseFragment<FragmentSettingsChangeLangBinding, MenuProfileViewModel>(
        FragmentSettingsChangeLangBinding::inflate, MenuProfileViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        getSelectedLang()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.english.setOnClickListener { setLocale(LanguageConst.ENGLISH) }
        binding.uzbek.setOnClickListener { setLocale(LanguageConst.UZBEK) }
        binding.russian.setOnClickListener { setLocale(LanguageConst.RUSSIAN) }
    }

    private fun getSelectedLang() {
        when (LocaleHelper.getSelectedLang(requireContext())) {
            0 -> {
                selectRussian()
            }

            1, 2 -> {
                selectUzbek()
            }

            3 -> {
                selectEnglish()
            }
        }
    }

    private fun selectUzbek() {
        binding.switchUzbek.isChecked = true
        binding.switchEnglish.isChecked = false
        binding.switchRussian.isChecked = false
    }

    private fun selectRussian() {
        binding.switchRussian.isChecked = true
        binding.switchEnglish.isChecked = false
        binding.switchUzbek.isChecked = false
    }

    private fun selectEnglish() {
        binding.switchEnglish.isChecked = true
        binding.switchRussian.isChecked = false
        binding.switchUzbek.isChecked = false
    }

    private fun setLocale(lang: String) {
        Paper.book().write(Const.UPDATE_MAIN_WIDGETS, true)
        LocaleHelper.setLocale(requireContext(), lang)
        requireActivity().recreate()
        getSelectedLang()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.appBar.setTitle(getString(R.string.choose_language))
    }
}