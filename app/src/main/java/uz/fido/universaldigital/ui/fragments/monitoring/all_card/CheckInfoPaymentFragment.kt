package uz.fido.universaldigital.ui.fragments.monitoring.all_card

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.FileUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.fragment_check_info.save
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem
import uz.fido.network.domain.model.payment.PrintChequeResponse
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentCheckInfoBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.format.Format
import uz.fido.utils.format.Format.takeScreenShot
import uz.fido.utils.format.FormatUtil
import uz.fido.utils.format.FormatUtilsKt
import uz.fido.utils.utility.fragment.pop
import java.io.File

class CheckInfoPaymentFragment:BaseSimpleFragment<FragmentCheckInfoBinding>(
    FragmentCheckInfoBinding::inflate
) {
    private var transactId = ""
    private var command = ""
    private lateinit var operation: String
    private lateinit var printChequeResponse: PrintChequeResponse
    private lateinit var svMonitoringItem: SVMonitoringItem
    private lateinit var humoMonitoringItem: HumoMonitoringItem
    private lateinit var visaMonitoringItem: CurrencyCardMonitoringItem
    private var localMonitoring:LocalMonitoring?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            operation = it.getString("operation").toString()
            command = it.getString("command").toString()
            when (operation) {
                "local" -> printChequeResponse = it.getSerializable("details") as PrintChequeResponse
                "uzcard" -> svMonitoringItem = it.getSerializable("uzcard") as SVMonitoringItem
                "humo" -> humoMonitoringItem = it.getSerializable("humo") as HumoMonitoringItem
                "visa" -> visaMonitoringItem = it.getSerializable("visa") as CurrencyCardMonitoringItem
                else -> return
            }
        }
       checkTip()
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.save.setOnClickListener {
            Toast.makeText(requireContext(), R.string.successfully_saved, Toast.LENGTH_SHORT).show()
            save(binding.linAdd, 2)
        }
    }
    private fun initUzCard() {
        addView(getString(R.string.name),svMonitoringItem.merchant_name)
        addView(getString(R.string.date_time), svMonitoringItem.tran_date)
        addView(getString(R.string.terminal_id), svMonitoringItem.terminal_id)
        addView(getString(R.string.card_number), svMonitoringItem.card_num)
        if (svMonitoringItem.address.isNotEmpty() && svMonitoringItem.address != "0") addView(getString(R.string.address), svMonitoringItem.address)
        addView(
            getString(R.string.operation_type),
            if (svMonitoringItem.tran_type == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(
            getString(R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(svMonitoringItem.tran_amount.toString())) + " UZS", true
        )
    }

    private fun save(linAdd: View,operationType: Int) {
        share(linAdd)
//        if (operationType == 1)
//            takeScreenshot(view)
//        else share(view)
    }

    private fun share(view: View) {
        view.takeScreenShot(requireActivity()) { bitmap ->
            bitmap?.let {
                val path = FormatUtilsKt.saveImageToGallery(requireContext(), it, "Monitoring cheque")
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val uri =
                        FileProvider.getUriForFile(
                            requireActivity(),
                            requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", File(path)
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
            getString(R.string.operation_type),
            if (visaMonitoringItem.tran_type == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(
            getString(R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(visaMonitoringItem.tran_amount)) + " " + visaMonitoringItem.currency
        )
    }
    private fun initLocal() {
        val item = printChequeResponse.monitoring_info!!
        transactId = item.request_id
        addView(getString(R.string.date_time), item.created_date)
        if (item.terminal_id.isNotEmpty())
            addView(getString(R.string.terminal_id), item.terminal_id)
        addView(getString(R.string.transaction_number), item.request_id)
        if (item.partner_obj.isNotEmpty()) {
            if (!item.to_obj_name.isNullOrEmpty()) {
                if (item.partner_obj.startsWith("AUZ")) {
                    addView(
                        getString(R.string.wallet_number),
                        if (item.object_value.length == 16) Format.formatCardNumber(item.partner_obj) else item.partner_obj
                    )
                } else {
                    addView(
                        item.to_obj_name, if (item.object_value.length == 16) Format.formatCardNumber(item.partner_obj) else item.partner_obj
                    )
                }
            } else addView(
                getString(R.string.personal_account),
                if (item.object_value.length == 16) Format.formatCardNumber(item.partner_obj) else item.partner_obj
            )
        }
        if (item.object_value.isNotEmpty() && item.partner_obj.length != 16 && !item.partner_obj.startsWith("AUZ")) {
            addView(
                getString(R.string.choose_card_text),
                if (item.object_value.length == 16) Format.formatCardNumber(item.object_value) else Format.formatWalletNumber(
                    item.object_value
                )
            )
        }

        val state = if (item.state_id == "1") {
            getString(R.string.successfully)
        } else {
            getString(R.string.waiting)
        }
          if (!item.fee_amount.isNullOrEmpty() && !item.fee_percent.isNullOrEmpty()){
        addView(getString(R.string.commission), "${item.fee_amount.toDouble() / 100.toDouble()} UZS (${item.fee_percent}%)")
          }
        addView(getString(R.string.status), state, isState = true)
        addView(
            getString(R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + when (item.currency_code) {
                "000" -> " UZS"
                "840" -> " $"
                "978" -> " EUR"
                else -> " RUB"
            }, true
        )

//        if ((command.contains("paynet") || command.contains("munis")) && !item.partner_obj.startsWith("AUZ")) {
//            binding.buttonFiscal.visibility = View.VISIBLE
//        }
    }

    private fun initHumo() {
        addView(getString(R.string.name), humoMonitoringItem.merchant_name)
        addView(getString(R.string.date_time), humoMonitoringItem.tran_date)
        addView(getString(R.string.terminal_id), humoMonitoringItem.terminal_id)
        addView(getString(R.string.terminal_id), humoMonitoringItem.terminal_id)
        addView(getString(R.string.card_number), Format.formatCardNumber(humoMonitoringItem.card_num))
        if (humoMonitoringItem.address.isNotEmpty()) addView(getString(R.string.address), humoMonitoringItem.address)
        addView(
            getString(R.string.operation_type),
            if (humoMonitoringItem.tran_type == "credit") getString(R.string.income) else getString(R.string.outcome)
        )
        addView(getString(R.string.amount), Format.formatAmount(Format.convertFromTiynDivide(humoMonitoringItem.tran_amount)) + " UZS", true)
    }
    private fun addView(name: String, value: String, isAmount: Boolean? = null, isState: Boolean? = null) {
        val itemBinding= ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        itemBinding.name.text = name
        itemBinding.value.text = value
        binding.linAdd.addView(itemBinding.root)
//        if (isAmount == true) {
//            itemBinding.textName.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryBlack))
//            itemBinding.textValue.textSize = 20f
//            itemBinding.textValue.typeface = Typeface.createFromAsset(requireContext().assets, "fonts/navigo_bold.ttf")
//        }
//        if (isState == true) {
//            itemBinding.textValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_identified))
//        }
    }

}