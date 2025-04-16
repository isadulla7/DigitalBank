package uz.fido.universaldigital.ui.fragments.payment.abc_success

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
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
import uz.fido.universaldigital.ui.utils.extensions.takeScreenShot
import uz.fido.universaldigital.ui.utils.file.FileUtils
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CheckInfoPaymentFragment :
    BaseFragment<FragmentCheckInfoPaymentBinding, SuccessPaymentViewModel>(
        FragmentCheckInfoPaymentBinding::inflate, SuccessPaymentViewModel::class.java
    ) {

    companion object {
        var OPERATION_P2P = "P2P"
        var OPERATION_PAYMENT = "OPERATION_PAYMENT"
    }

    private lateinit var printChequeResponse: PrintChequeResponse
    private lateinit var dialogReceipt: BottomReceiptsDialog
    private lateinit var dialogQrcode: BottomQRcodeDialog
    private lateinit var chequeAdapter: ChequeAdapter
    private lateinit var currentDate: String
    private lateinit var operation: String

    private val filteredList = ArrayList<Cheque>()
    private var transactId: String = ""
    private var qr_code: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            transactId = requireArguments().getString("transactId", "0")
            operation = requireArguments().getString("operation").toString()
        }
        chequeAdapter = ChequeAdapter()
        setonClick()
        getCheque()
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
            dialogQrcode = BottomQRcodeDialog(qr_code)
            dialogQrcode.show(childFragmentManager, "TAG")
        }
    }

    private fun share() {
        binding.chequeBlock.takeScreenShot(requireActivity()) { bitmap ->
            bitmap?.let {
                val path = FileUtils.saveImageToGallery(requireContext(), it, "MKB Cheque")
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val uri =
                        FileProvider.getUriForFile(
                            requireActivity(),
                            requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider",
                            File(path)
                        )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Send to"))
            }
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
        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            chequeAdapter.submitList(list)
            adapter = chequeAdapter

        }
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
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
                filteredList.add(it)
            }
            if (it.key == "PROVIDER_NAME") {
                binding.paymentName.text = it.value
                filteredList.remove(it)
            }
            if (it.key == "AMOUNT") {
                binding.amount.text = it.value + " UZS"
                filteredList.remove(it)
            }
            if (it.key == "OFDQRCODE") {
                binding.fiscalCheck.visibility = View.VISIBLE
                qr_code = it.value
                filteredList.remove(it)
            } else {
                binding.fiscalCheck.visibility = View.GONE
            }
        }
    }
}