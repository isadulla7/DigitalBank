package uz.fido.universaldigital.ui.fragments.payment.abc_success

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.Cheque
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.PrintChequeResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCheckInfoPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_adapter.ChequeAdapter
import uz.fido.universaldigital.ui.fragments.payment.abc_dialog.BottomQRcodeDialog
import uz.fido.universaldigital.ui.fragments.payment.abc_dialog.BottomReceiptsDialog
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CheckInfoPaymentFragment : BaseFragment<FragmentCheckInfoPaymentBinding, SuccessPaymentViewModel>(
    FragmentCheckInfoPaymentBinding::inflate, SuccessPaymentViewModel::class.java
) {

    companion object {
        var OPERATION_P2P = "P2P"
        var OPERATION_PAYMENT = "OPERATION_PAYMENT"
    }

    private lateinit var printChequeResponse: PrintChequeResponse
    private lateinit var dialogReceipt: BottomReceiptsDialog
    private lateinit var dialogQrcode: BottomQRcodeDialog
    private lateinit var currentDate: String
    private lateinit var operation: String

    private val chequeAdapter by lazy { ChequeAdapter() }
    private val pdfFile: ArrayList<Cheque> = arrayListOf()
    private val filteredList = ArrayList<Cheque>()
    private var transactId: String = ""
    private var qrCode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.adapter = chequeAdapter
        if (arguments != null) {
            transactId = requireArguments().getString("transactId", "0")
            operation = requireArguments().getString("operation").toString()
        }
        setonClick()
        if (filteredList.isEmpty()) {
            getCheque()
        } else initList(filteredList)

    }

    private fun setonClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.save.setOnClickListener {
            share()
        }
        binding.buttonReceipt.setOnClickListener {
            if (this::printChequeResponse.isInitialized) {
                dialogReceipt = BottomReceiptsDialog(
                    printChequeResponse.html.toString(),
                    printChequeResponse.monitoring_info?.name.toString()
                )
                dialogReceipt.show(childFragmentManager, "TAG")
            }
        }

        binding.fiscalCheck.setOnClickListener {
            dialogQrcode = BottomQRcodeDialog(qrCode)
            dialogQrcode.show(childFragmentManager, "TAG")
        }
    }

    private fun share() {
        pdfDocument()
//        binding.chequeBlock.takeScreenShot(requireActivity()) { bitmap ->
//            bitmap?.let {
//                val path = FileUtils.saveImageToGallery(requireContext(), it, "MKB Cheque")
//                val shareIntent: Intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    val uri =
//                        FileProvider.getUriForFile(
//                            requireActivity(),
//                            requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider",
//                            File(path)
//                        )
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    type = "image/*"
//                }
//                startActivity(Intent.createChooser(shareIntent, "Send to"))
//            }
//        }
    }

    private fun pdfDocument() {
        lifecycleScope.launch {
            try {
                val pdfDocument = PdfDocument()
                val paint = Paint().apply {
                    textSize = 14f
                }

                val pageWidth = 595
                val pageHeight = 842
                var y = 40f
                var pageNumber = 1

                fun createNewPage(): PdfDocument.Page {
                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = pdfDocument.startPage(pageInfo)
                    y = 40f
                    pageNumber++
                    return page
                }

                var page = createNewPage()
                val canvas = page.canvas

                pdfFile.forEach { item ->
                    if (y + 60f > pageHeight) {
                        pdfDocument.finishPage(page)
                        page = createNewPage()
                    }
                    page.canvas.drawText(item.key_description.ifEmpty { item.key }, 40f, y, paint)
                    y += 25f
                    page.canvas.drawText(item.value, 40f, y, paint)
                    y += 35f
                }

                pdfDocument.finishPage(page)
                val file = File(requireContext().cacheDir, "output.pdf")
                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()
                sharePdf(file)
            } catch (e: Exception) {
                toast("Yuklashni iloji bo‘lmadi")
            }

        }

    }

    private fun sharePdf(file: File) {
        lifecycleScope.launch {
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.my.package.name.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Send PDF"))
        }
    }

    private fun getCheque() {
        showProgress()
        viewModel.printCheque(getClientToken(), PrintChequeRequest(transactId))
            .observe(viewLifecycleOwner) { resource ->
                hideProgress()
                when (resource.status) {
                    Status.SUCCESS -> {
                        val response = resource.data?.details
                        printChequeResponse = resource.data!!
                        setTextResponse(response)
                        if (printChequeResponse.html != null && printChequeResponse.html!!.isNotEmpty()) {
                            binding.buttonReceipt.visibility = View.VISIBLE
                        }
                        initList(filteredList)
                    }

                    Status.ERROR -> {
                        showSnackbar(resource.message.toString())
                    }
                }
            }
    }

    private fun initList(list: ArrayList<Cheque>) {
        chequeAdapter.submitList(list)
        initItem()
    }

    private fun initItem() {
        currentDate = getCurrentDateNumber() + " " + getCurrentTime2()
        binding.dateTime.text = currentDate
        binding.amount.text = requireArguments().getString("amount")
        when (operation) {
            OPERATION_P2P -> {
                binding.commission.text = requireArguments().getString("commission") + " UZS" + "(${requireArguments().getString("percent")} %)"
                binding.paymentName.text = getString(R.string.transfer)
                binding.buttonReceipt.visibility = View.GONE
            }

            OPERATION_PAYMENT -> {
                binding.senderCardLayout.visibility = View.GONE
                binding.commissionLayout.visibility = View.GONE
                binding.paymentName.text = getString(R.string.payment)
            }
        }
    }

    private fun getCurrentDateNumber(): String {
        val df = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return df.format(Calendar.getInstance().time)
    }

    private fun getCurrentTime2(): String {
        //Time format 14:29
        val dfTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        return dfTime.format(Calendar.getInstance().time)
    }

    @SuppressLint("SetTextI18n")
    private fun setTextResponse(response: java.util.ArrayList<Cheque>?) {
        response?.forEach {
            if (it.key.isNotEmpty() && it.value.isNotEmpty()) {
                pdfFile.add(it)
                filteredList.add(it)
            }
            if (it.key == "PROVIDER_NAME") {
                binding.paymentName.text = it.value
                pdfFile.remove(it)
                pdfFile.add(Cheque(key = getString(R.string.name), value = it.value, key_description = it.key_description, order = it.order))
                filteredList.remove(it)
            }
            if (it.key == "AMOUNT") {
                binding.amount.text = it.value + " UZS"
                filteredList.remove(it)
            }
            if (it.key == "OFDQRCODE") {
                binding.fiscalCheck.visibility = View.VISIBLE
                qrCode = it.value
                filteredList.remove(it)
            } else {
                binding.fiscalCheck.visibility = View.GONE
            }
            if (it.key == "CHEQUE_HTML_WIDTH" || it.key == "CHEQUE_HTML_HEIGHT" || it.key == "CHEQUE_BARCODE" || it.key == "AGENT_COMMISSION" ||
                it.key == "OFD_CHEQUE_ID" || it.key == "OFD_TERMINAL_ID" || it.key == "OFD_COMMISIONINFO_TIN0" || it.key == "OFD_AMOUNT0" ||
                it.key == "OFD_PRODUCTNAME0" || it.key == "OFD_PRODUCTCODE0" || it.key == "OFD_VAT_AMOUNT0" || it.key == "OFD_RECEIVEDCASH" ||
                it.key == "BANK_NAME" || it.key == "RS" || it.key == "MFO" || it.key == "FILIAL_INN" || it.key == "TIME" || it.key == "HUMO_PAYMENT_ID" ||
                it.key == "OFD_FISCAL_SIGN" || it.key == "PURCHASED_AMOUNT" || it.key == "OFD_RECEIVPRICE0"
            ) {
                filteredList.remove(it)
            }
        }
    }
}