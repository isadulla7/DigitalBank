package uz.fido.universaldigital.ui.fragments.services.money_transfers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.money_transfer.create.CreateTransferRequest
import uz.fido.network.domain.model.money_transfer.receive.Country
import uz.fido.network.domain.model.money_transfer.receive.MoneyTransferParamsResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmMoneyTransferBinding
import uz.fido.universaldigital.databinding.ItemConfirmPaymentBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConfirmMoneyTransferFragment :
    BaseFragment<FragmentConfirmMoneyTransferBinding, MoneyTransferViewModel>(
        FragmentConfirmMoneyTransferBinding::inflate, MoneyTransferViewModel::class.java
    ) {

    private lateinit var senderCard: CardResponse
    private var moneyTransferParamsResponse: MoneyTransferParamsResponse? = null
    private var countries = ArrayList<Country>()
    private var currency = CurrencyConst.CURRENCY_CHAR_USD
    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            moneyTransferParamsResponse = it.serializable<MoneyTransferParamsResponse>("model")
            countries = moneyTransferParamsResponse!!.countries
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        try {
            initItems(
                getString(R.string.sender_country),
                moneyTransferParamsResponse?.remittance_type?.country_name.orEmpty()
            )
            initItems(
                getString(R.string.transfer_control_number),
                moneyTransferParamsResponse?.remittance_type?.control_number.orEmpty()
            )
            initItems(
                getString(R.string.fio),
                moneyTransferParamsResponse?.remittance_type?.second_name.orEmpty() + " " + moneyTransferParamsResponse?.remittance_type?.first_name.orEmpty() + " " + moneyTransferParamsResponse?.remittance_type?.patronymic.orEmpty()
            )
            initItems(
                getString(R.string.sender_country),
                moneyTransferParamsResponse?.remittance_type?.amount.orEmpty() + " USD"
            )
            initCards()
        } catch (e: Exception) {
            recordException(e, ::initUI.name)
        }
    }

    private fun sendRequest() {
        showProgress()
        val createTransferRequest = CreateTransferRequest(
            operationId = "2",
            cardNumber = senderCard.object_value,
            transferId = moneyTransferParamsResponse?.remittance_type?.id.toString(),
            corFirstName = moneyTransferParamsResponse?.remittance_type?.first_name.toString(),
            corLastName = moneyTransferParamsResponse?.remittance_type?.second_name.toString(),
            corPatronymic = moneyTransferParamsResponse?.remittance_type?.patronymic.toString(),
            phone = getClientPhoneNumber(),
            amount = Format.formatAmountToTiyn(moneyTransferParamsResponse!!.remittance_type!!.amount!!),
            countryCode = moneyTransferParamsResponse?.remittance_type?.country_code.toString(),
            city = moneyTransferParamsResponse?.remittance_type?.city.toString(),
            corPhone = moneyTransferParamsResponse?.remittance_type?.phone.toString(),
            mtcn = moneyTransferParamsResponse?.remittance_type?.control_number.toString(),
            currencyCode = "840"
        )
        viewModel.createMoneyTransfer(getClientToken(), createTransferRequest)
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }

                    Status.SUCCESS -> {
                        val bundle = Bundle()
                        bundle.putString(Const.OPERATION, BasicSuccessFragment.MONEY_TRANSFER)
                        bundle.putString(
                            Const.OPERATION_AMOUNT,
                            moneyTransferParamsResponse!!.remittance_type!!.amount!!
                        )
                        gotoWithSlide(R.id.successPaymentFragment, bundle)
                    }
                }
            }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            sendRequest()
        }
    }

    private fun initItems(name: String, value: String) {
        val mainBlockBinding = ItemConfirmPaymentBinding.inflate(
            LayoutInflater.from(requireContext()), requireView().parent as ViewGroup, false
        )
        mainBlockBinding.textName.text = name
        mainBlockBinding.textValue.text = value
        binding.content.addView(mainBlockBinding.root)
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>,
                moneyTransferParamsResponse!!.remittance_type!!.amount!!,
                currency
            ) { cardResponse ->
                cardResponse?.let { card ->
                    binding.btnContinue.isEnabled(
                        !BaseCardUtils.compareWithBalance(
                            moneyTransferParamsResponse!!.remittance_type!!.amount!!, card
                        )
                    )
                    senderCard = card
                }
            }
        }
    }
}