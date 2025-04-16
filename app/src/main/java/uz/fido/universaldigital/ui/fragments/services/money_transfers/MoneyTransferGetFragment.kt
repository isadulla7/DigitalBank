package uz.fido.universaldigital.ui.fragments.services.money_transfers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.money_transfer.receive.Country
import uz.fido.network.domain.model.money_transfer.receive.MoneyTransferParamsResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMoneyTransferGetBinding
import uz.fido.universaldigital.ui.fragments.services.money_transfers.dialogs.ChooseCountryDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber

@AndroidEntryPoint
class MoneyTransferGetFragment :
    BaseFragment<FragmentMoneyTransferGetBinding, MoneyTransferViewModel>(
        FragmentMoneyTransferGetBinding::inflate, MoneyTransferViewModel::class.java
    ) {

    private var moneyTransferParamsResponse: MoneyTransferParamsResponse? = null
    private var countries = ArrayList<Country>()

    private lateinit var dialog: ChooseCountryDialog
    private var countryCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            moneyTransferParamsResponse = it.serializable<MoneyTransferParamsResponse>("params_model")
            countries = moneyTransferParamsResponse!!.countries
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.appBar.setTitle(moneyTransferParamsResponse?.remittance_type?.nameTransfer!!)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (checkError()) {
                gotoWithSlide(
                    R.id.confirmMoneyTransferFragment,
                    bundleOf("model" to moneyTransferParamsResponse)
                )
            }
        }

        binding.senderCountry.setOnClickListener {
            dialog = ChooseCountryDialog(countries) {
                selectedCountry(it)
            }
            dialog.show(childFragmentManager, "")
        }
        binding.phoneNumber.setText("+" + getClientPhoneNumber())
        binding.phoneNumber.setOnKeyListener { _, _, event ->
            event.keyCode == KeyEvent.KEYCODE_DEL && binding.phoneNumber.text.toString().length == 4
        }
        binding.senderPhoneNumber.setText("+")
        binding.senderPhoneNumber.setOnKeyListener { _, _, event ->
            event.keyCode == KeyEvent.KEYCODE_DEL && binding.senderPhoneNumber.text.toString().length == 1
        }
        binding.etAmount.addTextChangedListener { binding.btnContinue.isEnabled(checkError()) }
    }

    @SuppressLint("SetTextI18n")
    private fun selectedCountry(country: Country) {
        dialog.dismiss()
        binding.senderCountry.setText(country.name)
        countryCode = country.code
        moneyTransferParamsResponse?.remittance_type?.country_name = country.name.trim()
    }

    private fun checkError(): Boolean {
        if (countryCode == null) {
            binding.senderCountryLayout.error = getString(R.string.necessary_area)
        } else {
            binding.senderCountryLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.country_code = countryCode!!
        }
        if (binding.city.text.toString().isEmpty()) {
            binding.cityLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.cityLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.city = binding.city.text.toString()
        }
//        if (binding.senderPhoneNumber.text.toString().isEmpty()) {
//            binding.senderPhoneNumberLayout.error = getString(R.string.necessary_area)
//            return false
//        } else {
//            binding.senderPhoneNumberLayout.error = null
//        moneyTransferParamsResponse?.remittance_type?.phone = binding.senderPhoneNumber.text.toString().replace("+", "").replace(" ", "")
//        }
        if (binding.phoneNumber.text.toString().isEmpty()) {
            binding.phoneNumberLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.phoneNumberLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.phone =
                binding.phoneNumber.text.toString().replace("+", "").replace(" ", "")
        }
        if (binding.nameSender.text.toString().isEmpty()) {
            binding.nameSenderLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.nameSenderLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.first_name =
                binding.nameSender.text.toString()
        }
        if (binding.secondName.text.toString().isEmpty()) {
            binding.secondNameLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.secondNameLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.second_name =
                binding.secondName.text.toString()
        }
        if (binding.fatherName.text.toString().isEmpty()) {
            binding.fatherNameLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.fatherNameLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.patronymic =
                binding.fatherName.text.toString()
        }
//        moneyTransferParamsResponse?.remittance_type?.phone = binding.senderPhoneNumber.text.toString().replace("+", "").replace(" ", "")

        if (binding.controlNumberTransfer.text.toString().isEmpty()) {
            binding.controlNumberTransferLayout.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.controlNumberTransferLayout.error = null
            moneyTransferParamsResponse?.remittance_type?.control_number =
                binding.controlNumberTransfer.text.toString()
        }
        if (binding.etAmount.text.toString().isEmpty() && binding.etAmount.text.toString()
                .startsWith("0")
        ) {
            binding.etAmount.error = getString(R.string.necessary_area)
            return false
        } else {
            binding.etAmount.error = null
            moneyTransferParamsResponse?.remittance_type?.amount =
                binding.etAmount.text.toString()
        }
        return true
    }

}