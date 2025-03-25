package uz.fido.universaldigital.ui.fragments.profile.about_bank

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConnectWithBankBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class ConnectWithBankFragment : BaseFragment<FragmentConnectWithBankBinding, MenuProfileViewModel>(
    FragmentConnectWithBankBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.mail.setOnClickListener { openMail() }
        binding.call.setOnClickListener { callToBank() }
        binding.telegram.setOnClickListener { openTelegram() }
        binding.chat.setOnClickListener { goto(R.id.menuChatFragment) }
    }

    private fun openTelegram() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, "https://t.me/myuniversalbank".toUri()
            )
        )
    }

    private fun callToBank() {
        val phone = "tel: +998712001110"
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = phone.toUri()
        startActivity(intent)
    }

    private fun openMail() {
        val email = "universaldigitalbank@gmail.com"
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email)
        emailIntent.data = "mailto:$email".toUri()
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        emailIntent.addFlags(Intent.FLAG_FROM_BACKGROUND)
        startActivity(emailIntent)
    }

}