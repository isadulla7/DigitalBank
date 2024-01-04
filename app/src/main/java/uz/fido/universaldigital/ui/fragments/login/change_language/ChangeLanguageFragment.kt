package uz.fido.universaldigital.ui.fragments.login.change_language

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentChangeLanguageBinding
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.utility.activity.tintSystemBars
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.language.LocaleHelper

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
        binding.selectedUz.visibility = View.VISIBLE
        binding.selectedRus.visibility = View.GONE
        binding.selectedEng.visibility = View.GONE
    }

    private fun selectRussian() {
        binding.selectedRus.visibility = View.VISIBLE
        binding.selectedEng.visibility = View.GONE
        binding.selectedUz.visibility = View.GONE
    }

    private fun selectEnglish() {
        binding.selectedEng.visibility = View.VISIBLE
        binding.selectedRus.visibility = View.GONE
        binding.selectedUz.visibility = View.GONE
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