package uz.fido.universaldigital.ui.fragments.monitoring.cheque

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.network.domain.model.payment.PrintChequeResponse
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentCheckInfoBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_dialog.BottomReceiptsDialog
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.format.Format.takeScreenShot
import uz.fido.utils.format.FormatUtilsKt
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import java.io.File

class CheckInfoPaymentFragment : BaseSimpleFragment<FragmentCheckInfoBinding>(FragmentCheckInfoBinding::inflate) {

    private lateinit var operation: String
    private lateinit var printChequeResponse: PrintChequeResponse
    private lateinit var uzcardMonitoringItem: UzcardMonitoringItem
    private lateinit var humoMonitoringItem: HumoMonitoringItem
    private lateinit var visaMonitoringItem: CurrencyCardMonitoringItem
    private lateinit var dialogReceipt: BottomReceiptsDialog

    private var searchDataResponse: SearchDataResponse? = null
    private var transactId = ""
    private var command = ""
    private var paymentName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            try {
                paymentName = it.getString("name").toString()
                operation = it.getString("operation").toString()
                command = it.getString("command").toString()
                when (operation) {
                    "local" -> {
                        printChequeResponse = it.serializable<PrintChequeResponse>("details") as PrintChequeResponse
                        searchDataResponse = it.serializable<SearchDataResponse>("data")
                    }

                    "uzcard" -> uzcardMonitoringItem = it.serializable<UzcardMonitoringItem>("uzcard") as UzcardMonitoringItem
                    "humo" -> humoMonitoringItem = it.serializable<HumoMonitoringItem>("humo") as HumoMonitoringItem
                    "visa" -> visaMonitoringItem = it.serializable<CurrencyCardMonitoringItem>("visa") as CurrencyCardMonitoringItem
                    else -> return
                }
            } catch (e: Exception) {
                recordException(e, ::onViewCreated.name)
            }
        }
        checkTip()
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.apply {
            appBar.setOnBackButtonClickListener { pop() }
            save.setOnClickListener {
                Toast.makeText(requireContext(), R.string.successfully_saved, Toast.LENGTH_SHORT).show()
                save(linAdd)
            }
            buttonReceipt.setOnClickListener {
                dialogReceipt = BottomReceiptsDialog(
                    printChequeResponse.html.toString(), printChequeResponse.monitoring_info?.name.toString()
                ) {
                    goto(R.id.receiptFullFragment, bundle = bundleOf("html" to printChequeResponse.html.toString()))
                    dialogReceipt.dismiss()
                }
                dialogReceipt.show(childFragmentManager, "TAG")
            }
        }
    }

    private fun initUzCard() {
        addView(getString(R.string.name), uzcardMonitoringItem.merchantName)
        addView(getString(R.string.date_time), uzcardMonitoringItem.transactionDate)
        addView(getString(R.string.terminal_id), uzcardMonitoringItem.terminalId)
        addView(getString(R.string.card_number), uzcardMonitoringItem.cardNumber)
        if (uzcardMonitoringItem.address.isNotEmpty() && uzcardMonitoringItem.address != "0") addView(getString(R.string.address), uzcardMonitoringItem.address)
        addView(
            getString(R.string.operation_type), if (uzcardMonitoringItem.transactionType == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(
            getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(uzcardMonitoringItem.transactionAmount)) + " UZS"
        )
    }

    private fun save(linAdd: View) {
        share(linAdd)
    }

    private fun share(view: View) {
        view.takeScreenShot(requireActivity()) { bitmap ->
            bitmap?.let {
                val path = FormatUtilsKt.saveImageToGallery(requireContext(), it, "Monitoring cheque")
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val uri = FileProvider.getUriForFile(
                        requireActivity(), requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", File(path)
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Send to"))
            }
        }
    }

    private fun checkTip() {
        when (operation) {
            "local" -> initLocal()
            "uzcard" -> initUzCard()
            "humo" -> initHumo()
            "visa" -> initVisa()
        }
    }

    private fun initVisa() {
        addView(getString(R.string.name), visaMonitoringItem.merchant_name)
        addView(getString(R.string.date_time), visaMonitoringItem.tran_date)
        //addView(getString(R.string.terminal_id), visaMonitoringItem.terminal_id)
        addView(getString(R.string.card_number), Format.formatCardNumber(visaMonitoringItem.card_num))
        if (visaMonitoringItem.address.isNotEmpty()) addView(getString(R.string.address), visaMonitoringItem.address)
        addView(
            getString(R.string.operation_type), if (visaMonitoringItem.tran_type == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(
            getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(visaMonitoringItem.tran_amount)) + " " + visaMonitoringItem.currency
        )
    }

    private fun initLocal() {
        val item = printChequeResponse.monitoring_info!!
        transactId = item.requestId
        addView(getString(R.string.service), paymentName)
        searchDataResponse?.params?.get("FIO")?.let { addView(getString(R.string.fio), it) }
        searchDataResponse?.params?.get("FIO_ABONENT")?.let { addView(getString(R.string.fio), it) }
        searchDataResponse?.params?.get("ADDRESS")?.let { addView(getString(R.string.address), it) }
        searchDataResponse?.params?.get("clientName")?.let { addView(getString(R.string.fio), it) }
        searchDataResponse?.params?.get("mfo")?.let { addView(getString(R.string.mfo), it) }
        addView(getString(R.string.date_time), item.createdDate)
        if (searchDataResponse?.service_id == "-4") {
            printChequeResponse.details.forEach {
                if (checkList(it.key)) {
                    addView(it.key_description, it.value)
                }
            }
        }
        if (item.terminalId.isNotEmpty()) addView(getString(R.string.terminal_id), item.terminalId)
        addView(getString(R.string.transaction_number), item.requestId)

        if (item.partnerObj.isNotEmpty()) {
            if (item.receiverCardName.isNotEmpty()) {
                if (item.partnerObj.startsWith("AUZ")) {
                    addView(
                        getString(R.string.wallet_number), if (item.senderCard.length == 16) Format.formatCardNumber(item.partnerObj) else item.partnerObj
                    )
                } else {
                    addView(
                        item.receiverCardName, if (item.senderCard.length == 16) Format.formatCardNumber(item.partnerObj) else item.partnerObj
                    )
                }
            } else {
                addView(
                    getString(R.string.personal_account), if (item.senderCard.length == 16) Format.formatCardNumber(item.partnerObj) else item.partnerObj
                )
            }
        }
        if (item.senderCard.isNotEmpty() && item.partnerObj.length != 16 && !item.partnerObj.startsWith("AUZ")) {
            addView(
                getString(R.string.choose_card_text), if (item.senderCard.length == 16) Format.formatCardNumber(item.senderCard) else Format.formatWalletNumber(
                    item.senderCard
                )
            )
        }
        if (searchDataResponse != null) {
            if (searchDataResponse?.operation_code.orEmpty().startsWith("P2P")) {
                if (!searchDataResponse?.to_embossed_name.isNullOrEmpty()) {
                    addView(getString(R.string.receiver_name), searchDataResponse?.to_embossed_name.orEmpty())
                }
            }
        }
        val state = if (item.stateId == "1") {
            getString(R.string.successfully)
        } else {
            getString(R.string.waiting)
        }
        if (item.feeAmount.isNotEmpty() && item.feePercent.isNotEmpty()) {
            addView(getString(R.string.commission), "${item.feeAmount.toDouble() / 100.toDouble()} UZS (${item.feePercent}%)")
        }
        addView(getString(R.string.status), state)
        addView(
            getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + when (item.currencyCode) {
                "000" -> " UZS"
                "840" -> " $"
                "978" -> " EUR"
                else -> " RUB"
            }
        )

        if (printChequeResponse.html != null && printChequeResponse.html!!.isNotEmpty()) {
            binding.buttonReceipt.visibility = View.VISIBLE
        }


    }

    private fun checkList(key: String): Boolean {
        return key != "TERMINAL_ID" && key != "AMOUNT"
    }

    private fun initHumo() {
        addView(getString(R.string.name), humoMonitoringItem.merchantName)
        addView(getString(R.string.date_time), humoMonitoringItem.transactionDate)
        addView(getString(R.string.terminal_id), humoMonitoringItem.terminalId)
        addView(getString(R.string.terminal_id), humoMonitoringItem.terminalId)
        addView(getString(R.string.card_number), Format.formatCardNumber(humoMonitoringItem.cardNumber))
        if (humoMonitoringItem.address.isNotEmpty()) addView(getString(R.string.address), humoMonitoringItem.address)
        addView(
            getString(R.string.operation_type), if (humoMonitoringItem.transactionType == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(humoMonitoringItem.transactionAmount)) + " UZS")
    }

    private fun addView(name: String, value: String) {
        val itemBinding = ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        itemBinding.name.text = name
        itemBinding.value.text = value
        binding.linAdd.addView(itemBinding.root)
    }

}