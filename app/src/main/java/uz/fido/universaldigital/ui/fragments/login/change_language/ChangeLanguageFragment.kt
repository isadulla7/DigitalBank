package uz.fido.universaldigital.ui.fragments.login.change_language

import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.isVisible
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentChangeLanguageBinding
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.activity.tintSystemBars
import uz.fido.utils.utility.fragment.goto
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper

class ChangeLanguageFragment : BaseSimpleFragment<FragmentChangeLanguageBinding>(
    FragmentChangeLanguageBinding::inflate
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().tintSystemBars(R.color.brandRedColor, R.color.brandRedColor)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().tintSystemBars(R.color.brandRedColor, R.color.brandRedColor)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        getSelectedLang()
        initSetOnClickListeners()
        DiffieHellman.clearDiffieHellman()
    }

    private fun initSetOnClickListeners() {
        binding.english.setOnClickListener { setLocale(LanguageConst.ENGLISH) }
        binding.uzbek.setOnClickListener { setLocale(LanguageConst.UZBEK) }
        binding.russian.setOnClickListener { setLocale(LanguageConst.RUSSIAN) }
        binding.btnContinue.setOnClickListener { goto(R.id.signInFragment) }
    }

    private fun getSelectedLang() {
        when (LocaleHelper.getSelectedLang(requireContext())) {
            0 -> {
                selectLanguage(Languages.RUSSIAN)
            }

            1, 2 -> {
                selectLanguage(Languages.UZBEK)
            }

            3 -> {
                selectLanguage(Languages.ENGLISH)
            }
        }
    }

    private fun selectLanguage(languages: Languages) {
        binding.selectedUz.isVisible = languages == Languages.UZBEK
        binding.selectedRus.isVisible = languages == Languages.RUSSIAN
        binding.selectedEng.isVisible = languages == Languages.ENGLISH
    }

    private fun setLocale(lang: String) {
        LocaleHelper.setLocale(requireContext(), lang)
        requireActivity().recreate()
        getSelectedLang()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.title.text = getString(R.string.choose_language)
        binding.subtitle.text = getString(R.string.choose_language_description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().tintSystemBars(R.color.whiteColor, R.color.brandRedColor)
    }

}

enum class Languages {
    UZBEK, RUSSIAN, ENGLISH
}