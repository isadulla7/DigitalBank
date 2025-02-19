package uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.RenameDepositRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.DialogClientDepositEditNameBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class DepositEditNameFragment : BaseFragment<DialogClientDepositEditNameBinding, ClientDepositViewModel>(
    DialogClientDepositEditNameBinding::inflate, ClientDepositViewModel::class.java
) {
    private lateinit var deposit: ClientDeposit
    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    companion object {
        const val EDIT_NAME = "edit_name"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkButton()
        binding.appBar.setOnBackButtonClickListener { pop() }
        deposit = requireArguments().serializable<ClientDeposit>(EDIT_NAME) as ClientDeposit
        binding.etName.setText(deposit.depName)
        binding.btnEnter.setOnClickListener {
            val name = binding.etName.editableText.toString()

            showProgress()
            viewModel.renameDeposit(getClientToken(), RenameDepositRequest(name, deposit.savDepId.orEmpty())).observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        getDeposits()
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }

            }
        }
    }

    private fun checkButton() {
        binding.etName.addTextChangedListener {
            binding.btnEnter.isEnabled=it.toString().replace(" ","").isNotEmpty()
        }
    }

    private fun getDeposits() {
        viewModel.getClientDepositList(getClientToken()).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.data != null) {
                        menuProductsViewModel.updateClientDepositList(it.data!!.data)
                        goto(R.id.basicSuccessFragment, bundleOf(Const.OPERATION to BasicSuccessFragment.DEPOSIT_EDIT_NAME))
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }
}