package uz.fido.universaldigital.ui.fragments.transfers.success

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentSuccessTransferBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.transfers.cheque.TransferChequeFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.goto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class SuccessTransferFragment :
    BaseSimpleFragment<FragmentSuccessTransferBinding>(FragmentSuccessTransferBinding::inflate) {

    private lateinit var transferDto: TransferDto
    private lateinit var now: Date

    companion object {
        const val TRANSFER_DTO = "TRANSFER_DTO"
        const val TRANSFER_OPERATION = "TRANSFER_OPERATION"
        const val TRANSFER_BY_CARD = "TRANSFER_BY_CARD"
        const val TRANSFER_BY_PHONE = "TRANSFER_BY_PHONE"
        const val TRANSFER_BY_WALLET = "TRANSFER_BY_WALLET"
        const val TRANSFER_OVER_MY_CARDS = "TRANSFER_OVER_MY_CARDS"
        const val CONVERSION = "CONVERSION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferDto = requireArguments().serializable<TransferDto>(TRANSFER_DTO) as TransferDto
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initPageDetails()
        initSetOnClickListeners()
        onBackPressCallback()
    }

    private fun initPageDetails() {
        binding.successTitle.text = getString(R.string.transfer_successfully)
        binding.amount.text =
            Format.formatAmount(
                (transferDto.transferAmount?.toDouble()?.div(100)).toString()
            ) + " " + getString(R.string.sum_text)
        setOperationTime()
    }

    private fun initSetOnClickListeners() {
        binding.gotoMainPage.setOnClickListener {
            gotoMainPage()
        }
        binding.cheque.setOnClickListener {
            gotoChequePage()
        }
    }

    private fun gotoMainPage() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun gotoChequePage() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
        val operationDate = dateFormat.format(now)
        goto(
            R.id.transferChequeFragment2, bundleOf(
                TRANSFER_DTO to transferDto,
                TransferChequeFragment.OPERATION_DATE to operationDate
            )
        )
    }

    private fun onBackPressCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gotoMainPage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setOperationTime() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.US)
        now = Calendar.getInstance().time
        binding.date.text = dateFormat.format(now)
    }

}