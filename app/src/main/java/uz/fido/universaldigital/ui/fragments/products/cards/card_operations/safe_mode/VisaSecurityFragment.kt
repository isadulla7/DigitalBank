package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.safe_mode

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.Secure3DOperations
import uz.fido.network.domain.model.cards.Secure3DRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSafeModeBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken


@AndroidEntryPoint
class VisaSecurityFragment : BaseFragment<FragmentSafeModeBinding, MenuProductsViewModel>(
    FragmentSafeModeBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse
    private var currentStatus: String = ""
    private var errorsCount = 0

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setTitle(getString(R.string.secure_3d))
        binding.safeModeText.text = getString(R.string.pay_with_3d_secure)
        binding.switchSafeMode.setOnCheckedChangeListener { _, isChecked ->
            if (binding.switchSafeMode.isChecked) {
                if (currentStatus != "Active") secure3DAction(Secure3DOperations.INSERT.operation_type)
            } else {
                if (currentStatus == "Active") secure3DAction(Secure3DOperations.DELETE.operation_type)
            }
        }
        binding.saveButton.setOnClickListener { pop() }
    }

    private fun secure3DAction(operation: String) {
        errorsCount++
        viewModel.secure3DAction(
            getClientToken(), Secure3DRequest(
                from_object_id = card.object_value, operation_type = operation
            )
        ).observe(viewLifecycleOwner) { response ->
            response?.let {
                when (response.status) {
                    Status.SUCCESS -> {
                        when (operation) {
                            Secure3DOperations.STATUS_CHECK.operation_type -> {
                                binding.switchSafeMode.isChecked =
                                    response.data!!.status == "Active"
                                currentStatus = response.data!!.status
                            }

                            Secure3DOperations.INSERT.operation_type -> {
                                toast(getString(R.string.turned_on))
                                binding.switchSafeMode.isChecked = true
                                currentStatus = "Active"
                            }

                            else -> {
                                toast(getString(R.string.turned_off))
                                binding.switchSafeMode.isChecked = false
                                currentStatus = "Deleted"
                            }
                        }
                    }

                    Status.ERROR -> {
                        if (errorsCount == 1) {
                            secure3DAction(Secure3DOperations.DELETE.operation_type)
                        }
                    }
                }
            }
        }
    }


}