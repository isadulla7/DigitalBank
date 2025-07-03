package uz.fido.universaldigital.ui.fragments.monitoring.local

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.databinding.DialogInfoMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.format.Format
import java.io.File
import java.io.FileOutputStream

class LocalMonitoringDetailsDialog(
    private val localMonitoring: LocalMonitoring,
    private val searchDateResponse: SearchDataResponse? = null,
    private val fullInfo: ((localeMonitoring: LocalMonitoring) -> Unit)? = null,
    private val repeatPayment: ((localeMonitoring: LocalMonitoring) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogInfoMonitoringBinding
    private val pdfInfoList: ArrayList<PdfInfo> = arrayListOf()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForButton()
        initSetOnClickListeners()
        init()
    }

    private fun initSetOnClickListeners() {
        binding.repeat.setOnClickListener {
            dismiss()
            repeatPayment?.invoke(localMonitoring)
        }
        binding.allInfo.setOnClickListener {
            dismiss()
            fullInfo?.invoke(localMonitoring)
        }
    }

    private fun pdfDocument() {
        lifecycleScope.launch {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            var y = 50f
            pdfInfoList.forEach { item ->
                canvas.drawText(item.name.toString(), 40f, y, paint)
                y += 30f
                canvas.drawText(item.info.toString(), 40f, y, paint)
                y += 40f // Keyingi item uchun bo‘sh joy
            }

            pdfDocument.finishPage(page)
            val file = File(requireContext().cacheDir, "output.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            share(file)
        }

    }

    private fun share(file: File) {
        lifecycleScope.launch{
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.my.package.name.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Send PDF"))
        }
    }


    private fun checkForButton() {
        binding.repeat.isVisible = repeatPayment != null
        binding.allInfo.isVisible = fullInfo != null
        if (isBadServiceIds(localMonitoring)) {
            binding.repeat.visibility = View.GONE
        }
    }

    private fun init() {
        try {
            addView(getString(uz.fido.universaldigital.R.string.service), localMonitoring.name)
            if (searchDateResponse != null) {
                when (searchDateResponse.request_code) {
                    "P2P", "CONVERSION" -> {
                        if (searchDateResponse.from_object_value != null) {
                            addView(getString(uz.fido.universaldigital.R.string.sender_card), Format.formatCardNumberForCheque(searchDateResponse.from_object_value.orEmpty()))
                            if (!searchDateResponse.from_embossed_name.isNullOrEmpty()) {
                                addView(getString(uz.fido.universaldigital.R.string.sender_name), searchDateResponse.from_embossed_name.orEmpty())
                            }
                        }
                        if (searchDateResponse.to_object_value != null) {
                            addView(getString(uz.fido.universaldigital.R.string.receiver_card), Format.formatCardNumberForCheque(searchDateResponse.to_object_value.orEmpty()))
                            if (!searchDateResponse.to_embossed_name.isNullOrEmpty()) {
                                addView(getString(uz.fido.universaldigital.R.string.receiver_name), searchDateResponse.to_embossed_name.orEmpty())
                            }
                        }
                        initViews(isRequired = false, isPayment = false)
                    }

                    "CREATE_PAYMENT" -> {
                        initViews(isRequired = true, isPayment = true)
                    }

                    else -> initViews(isRequired = true, isPayment = true)
                }
            } else initViews(isRequired = true, isPayment = true)
        } catch (e: Exception) {
            recordException(e, ::init.name)
        }
    }

    private fun initViews(isRequired: Boolean, isPayment: Boolean) {
        searchDateResponse?.params?.get("FIO")?.let { addView(getString(uz.fido.universaldigital.R.string.fio), it) }
        searchDateResponse?.params?.get("FIO_ABONENT")?.let { addView(getString(uz.fido.universaldigital.R.string.fio), it) }
        searchDateResponse?.params?.get("clientName")?.let { addView(getString(uz.fido.universaldigital.R.string.fio), it) }
        searchDateResponse?.params?.get("mfo")?.let { addView(getString(uz.fido.universaldigital.R.string.mfo), it) }
        searchDateResponse?.params?.get("ADDRESS")?.let { addView(getString(uz.fido.universaldigital.R.string.address), it) }
        addView(getString(uz.fido.universaldigital.R.string.date_time), localMonitoring.createdDate)
     //   addView(getString(uz.fido.universaldigital.R.string.transaction_number), localMonitoring.requestId)
        if (isRequired && localMonitoring.partnerObj.isNotEmpty()) {
            if (localMonitoring.receiverCardName.isNotEmpty()) {
                if (localMonitoring.partnerObj.startsWith("AUZ")) {
                    addView(
                        getString(uz.fido.universaldigital.R.string.wallet_number),
                        if (localMonitoring.senderCard.length == 16) Format.formatCardNumber(localMonitoring.partnerObj) else localMonitoring.partnerObj
                    )
                } else {
                    addView(
                        localMonitoring.receiverCardName, if (localMonitoring.senderCard.length == 16) Format.formatCardNumber(localMonitoring.partnerObj) else localMonitoring.partnerObj
                    )
                }
            } else {
                addView(
                    getString(uz.fido.universaldigital.R.string.personal_account),
                    if (localMonitoring.senderCard.length == 16) Format.formatCardNumber(localMonitoring.partnerObj) else localMonitoring.partnerObj
                )
            }
        }
        if (isPayment && localMonitoring.senderCard.isNotEmpty() && !localMonitoring.partnerObj.startsWith("AUZ")) {
            addView(
                getString(uz.fido.universaldigital.R.string.choose_card_text),
                if (localMonitoring.senderCard.length == 16) Format.formatCardNumber(localMonitoring.senderCard) else Format.formatWalletNumber(
                    localMonitoring.senderCard
                )
            )
        }
        addView(
            getString(uz.fido.universaldigital.R.string.amount),
            Format.formatAmount(Format.convertFromTiynDivide(localMonitoring.amount)) + " ${Format.currencyCode(localMonitoring.currencyCode)}"
        )
        val state = if (localMonitoring.stateId == "1") {
            getString(uz.fido.universaldigital.R.string.successfully)
        } else {
            getString(uz.fido.universaldigital.R.string.waiting)
        }
        if (localMonitoring.feeAmount.isNotEmpty() && localMonitoring.feePercent.isNotEmpty()) {
            addView(getString(uz.fido.universaldigital.R.string.commission), "${localMonitoring.feeAmount.toDouble() / 100.toDouble()} UZS (${localMonitoring.feePercent}%)")
        }
        addView(getString(uz.fido.universaldigital.R.string.status), state)
    }

    private fun addView(name: String, value: String) {
        pdfInfoList.add(PdfInfo(name, value))
        val viewDepositCreateBinding = ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun isBadServiceIds(localMonitoring: LocalMonitoring): Boolean {
        return when (localMonitoring.serviceId) {
            "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10", "-11", "-19", "-12", "-1" -> true
            else -> false
        }
    }
}