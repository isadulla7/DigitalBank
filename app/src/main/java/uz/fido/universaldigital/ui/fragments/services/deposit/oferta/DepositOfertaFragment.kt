package uz.fido.universaldigital.ui.fragments.services.deposit.oferta

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentDepositOfertaBinding
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class DepositOfertaFragment:BaseSimpleFragment<FragmentDepositOfertaBinding>
    (FragmentDepositOfertaBinding::inflate){
    private lateinit var deposit: Deposit
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let{
          deposit=it.getSerializable("deposit") as Deposit
        }
        init()
        onClickView()

    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.isEnabled(true)
        binding.btnContinue.setOnClickListener {
            gotoWithSlide(
                R.id.openDepositStepFirst, bundleOf(
                    "deposit" to deposit,
                    "operation" to arguments?.getString("deposit"),
                    "isSum" to arguments?.getBoolean("isSum")
                )
            )
        }
    }

    private fun init(){
        val website = when (deposit.dep_id) {
            1874 -> "https://ibank.ubank.uz/cib/sarmoya-25.html"
            1674 -> "https://ibank.ubank.uz/cib/qulay_daromad.html"
            1694 -> "https://ibank.ubank.uz/cib/yubiley.html"
            1753 -> "https://ibank.ubank.uz/cib/qulay_daromad.html"
            else -> "https://universalbank.uz/juristic"
        }


        binding.webView.loadUrl(website)
    }
    }



