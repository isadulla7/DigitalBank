package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.ChangeNotifStateRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSettingsBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.const.CardConst.STATE_ACTIVE
import uz.fido.utils.const.CardConst.STATE_PASSIVE
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, MenuProfileViewModel>(
    FragmentSettingsBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.switchNotification.isChecked = getNotificationState()
        initSetOnClickListeners()
    }

    private fun getNotificationState(): Boolean {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(NOTIFICATION_STATE, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(STATE, true)
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.changeLanguage.setOnClickListener { gotoWithSlide(R.id.changeLanguageFragmentSettings) }
        binding.appTheme.setOnClickListener {
            val navController=  requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
            navController?.navigate(R.id.appThemeFragment)
          //  gotoWithSlide(R.id.appThemeFragment)

        }
        binding.actions.setOnClickListener { gotoWithSlide(R.id.actionsFragment) }
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) changeNotificationState(STATE_PASSIVE) else changeNotificationState(
                STATE_ACTIVE
            )
        }
    }

    private fun changeNotificationState(state: String) {
        showNotificationStateProgress()
        viewModel.changeNotificationState(
            getClientToken(), ChangeNotifStateRequest(getClientToken(), state)
        ).observe(viewLifecycleOwner) {
            hideNotificationStateProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    changeNotificationStateOnUI(state)
                    saveNotificationState(state)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun changeNotificationStateOnUI(state: String) {
        binding.switchNotification.isChecked = state == STATE_ACTIVE
    }

    private fun saveNotificationState(state: String) {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(NOTIFICATION_STATE, Context.MODE_PRIVATE)
        sharedPref.edit {
            putBoolean(STATE, state == STATE_ACTIVE)
        }
    }

    private fun showNotificationStateProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideNotificationStateProgress() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isVisible) {
                binding.progress.visibility = View.GONE
            }
        }, 500)
    }

    companion object {
        const val NOTIFICATION_STATE = "NOTIFICATION_STATE"
        const val STATE = "STATE"
    }
}