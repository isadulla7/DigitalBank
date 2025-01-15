package uz.fido.universaldigital.ui.fragments.services.deposit.oferta

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentDepositOfertaBinding
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class DepositOfferFragment : BaseSimpleFragment<FragmentDepositOfertaBinding>
    (FragmentDepositOfertaBinding::inflate) {

    private lateinit var deposit: Deposit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            deposit = it.serializable<Deposit>("deposit") as Deposit
        }
        init()
        onClickView()

    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            gotoWithSlide(
                R.id.openDepositStepFirst, bundleOf(
                    "deposit" to deposit,
                    "operation" to arguments?.getString("operation"),
                    "isSum" to arguments?.getBoolean("isSum")
                )
            )
        }
        binding.offerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnContinue.isEnabled(isChecked)
        }
    }

    private fun init() {
        binding.webView.settings.builtInZoomControls = true
        val website = when (deposit.dep_id) {
            1874 -> "https://ibank.ubank.uz/cib/sarmoya-25.html"
            1674 -> "https://ibank.ubank.uz/cib/qulay_daromad.html"
            1694 -> "https://ibank.ubank.uz/cib/yubiley.html"
            1753 -> "https://ibank.ubank.uz/cib/yuksalish.html"
            1915 -> "https://ibank.ubank.uz/cib/foydali-hamyon18.html"
            1916 -> "https://ibank.ubank.uz/cib/foydali-hamyon24.html"
            else -> "https://ibank.ubank.uz/cib/defaultdig.html"
        }
        binding.webView.loadUrl(website)
    }
}



