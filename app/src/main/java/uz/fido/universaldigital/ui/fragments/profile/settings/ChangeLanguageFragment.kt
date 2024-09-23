package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.network.data.interceptor.tryMakeToast
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSettingsChangeLangBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT_GROUP
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.fillSearchList
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.searchList
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.LocaleHelper
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ChangeLanguageFragment :
    BaseFragment<FragmentSettingsChangeLangBinding, MenuProfileViewModel>(
        FragmentSettingsChangeLangBinding::inflate, MenuProfileViewModel::class.java
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


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
       // Paper.book().write(Const.UPDATE_MAIN_WIDGETS, true)
        Paper.book().write(Const.UPDATE_LANG,true)
        LocaleHelper.setLocale(requireContext(), lang)
        requireActivity().recreate()
        getSelectedLang()


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.appBar.setTitle(getString(R.string.choose_language))
    }
}