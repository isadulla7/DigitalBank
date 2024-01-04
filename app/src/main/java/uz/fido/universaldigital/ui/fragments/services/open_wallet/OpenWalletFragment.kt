package uz.fido.universaldigital.ui.fragments.services.open_wallet

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOpenWalletBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class OpenWalletFragment : BaseFragment<FragmentOpenWalletBinding, WalletViewModel>(
    FragmentOpenWalletBinding::inflate, WalletViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.openWalletBtn.setOnClickListener {
            openWalletRequest()
        }
        binding.walletName.addTextChangedListener {
            binding.openWalletBtn.isEnabled(it.toString().isNotEmpty())
        }
    }

    private fun openWalletRequest() {
        binding.openWalletBtn.setProgress(true)
        val info = Paper.book().read<SignInResponse>(Const.PAPER_CLIENT_INFO)
        if (info.filial_code!!.isNotEmpty()) viewModel.createWallet(
            getClientToken(),
            CreateWalletRequest(info.filial_code!!, "000", binding.walletName.text.toString())
        ).observe(viewLifecycleOwner) {
            binding.openWalletBtn.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(
                        R.id.basicSuccessFragment,
                        bundleOf(Const.OPERATION to BasicSuccessFragment.OPEN_WALLET_SUCCESS)
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
        else {
            binding.openWalletBtn.setProgress(false)
            showSnackbar(getString(R.string.you_must_get_identification))
        }
    }
}