package uz.fido.universaldigital.ui.fragments.transfers.swift_transfer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.swift.CreateSwiftAppRequest
import uz.fido.network.domain.model.swift.GetSwiftCommissionRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmSwiftTransferBinding
import uz.fido.universaldigital.databinding.ItemConfirmPaymentBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ConfirmSWIFTTransferFragment :
    BaseFragment<FragmentConfirmSwiftTransferBinding, SwiftTransferViewModel>(
        FragmentConfirmSwiftTransferBinding::inflate, SwiftTransferViewModel::class.java
    ) {

    private var selectedCard: CardResponse? = null
    private var requestModel: CreateSwiftAppRequest? = null
    private val productsViewModel: MenuProductsViewModel by activityViewModels()
    private var userCardList = ArrayList<CardResponse>()
    private val myFormat = "dd.MM.yy"
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    private var commissionAmount = ""
    private var currency = ""
    private var commissionUsd = "0"
    private var totalAmount = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        fetchBankTransferParams()
        initCards()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.continueButton.setOnClickListener {
            createSwiftApp()
        }
        initValues()
    }

    private fun initValues() {
        addViews(getString(R.string.date_value), getCurrentTime())
        addViews(
            getString(R.string.fio), requestModel!!.nameandaddress_50k!![0]
        )
        addViews(
            getString(R.string.address_sender), requestModel!!.nameandaddress_50k!![2]
        )

        addViews(
            getString(R.string.document_serial), requestModel!!.nameandaddress_50k!![1]
        )

//        addViews(
//            getString(R.string.passport_get_date),
//            requestModel!!.nameandaddress_50k!![3]
//        )

        addViews(
            getString(R.string.name_receiver), requestModel!!.nameandaddress_59!![0]
        )

        addViews(
            getString(R.string.beneficiary_account_name_address),
            requestModel!!.nameandaddress_59!![1]
        )

        addViews(getString(R.string.account_59), requestModel!!.account_59)
        addViews(
            getString(R.string.beneficiary_bank_account_bi), requestModel!!.bicorbei_56a
        )

        addViews(getString(R.string.name_57a_bic), requestModel!!.bicorbei_57a)
        addViews(
            getString(R.string.detail_transfer),
            requestModel!!.narrative_70!![0] + requestModel!!.narrative_70!![1] + requestModel!!.narrative_70!![2] + requestModel!!.narrative_70!![3]
        )

    }

    private fun createSwiftApp() {
        requestModel!!.from_object_id = selectedCard!!.object_value
        binding.continueButton.setProgress(true)
        viewModel.createSwiftApp(getClientToken(), requestModel!!).observe(viewLifecycleOwner) {
            binding.continueButton.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(
                        R.id.basicSuccessFragment, bundleOf(
                            Const.OPERATION to BasicSuccessFragment.SWIFT_SUCCESS,
                            Const.OPERATION_AMOUNT to requestModel!!.amount,
                            "params" to requireArguments().getSerializable("details") as HashMap<String, String>
                        )
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun addViews(name: String, value: String) {
        val mainBlockBinding = ItemConfirmPaymentBinding.inflate(
            LayoutInflater.from(requireContext()), requireView().parent as ViewGroup, false
        )
        mainBlockBinding.textName.text = name
        mainBlockBinding.textValue.text = value
        binding.content.addView(mainBlockBinding.root)
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        return sdf.format(currentTime)
    }


    private fun getArgs() {
        arguments?.let {
            requestModel = it.getSerializable("model") as CreateSwiftAppRequest
            currency = it.getString("currency_char").toString()
        }
    }


    private fun initCards() {
//        calculateTotalAmount()
        productsViewModel.cards.observe(viewLifecycleOwner) { it ->
            userCardList.clear()
            it.forEach {
                if (!it.object_value.startsWith("DV")) {
                    userCardList.add(it)
                }
            }
            binding.chooseCardLayout.initCards(
                userCardList,
                (totalAmount).toString(),
                if (currency == "USD") CurrencyConst.CURRENCY_CHAR_USD else CurrencyConst.CURRENCY_CHAR_EUR
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    binding.continueButton.isEnabled(
                        !BaseCardUtils.compareWithBalance(
                            totalAmount.toString(), card
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotalAmount() {
        totalAmount = requestModel!!.amount.toDouble() / 100.00 + commissionUsd.toDouble() / 100.00
        binding.total.text = "$totalAmount $currency"
    }

    private fun fetchBankTransferParams() {
        viewModel.getSwiftCommission(
            getClientToken(), GetSwiftCommissionRequest(
                currency_code = requestModel!!.currency_code,
                from_object_currency = requestModel!!.currency_code
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    commissionAmount = it.data!!.commis_amount.toString()
                    commissionUsd = it.data!!.usd_comission.toString()
                    binding.commission.text = Format.convertFromTiynDivide(commissionUsd)
                    calculateTotalAmount()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }
}



