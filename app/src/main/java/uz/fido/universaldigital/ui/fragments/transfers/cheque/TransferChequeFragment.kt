package uz.fido.universaldigital.ui.fragments.transfers.cheque

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.FileProvider
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferChequeBinding
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferViewModel
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.extensions.takeScreenShot
import uz.fido.universaldigital.ui.utils.file.FileUtils
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.pop
import java.io.File

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class TransferChequeFragment : BaseFragment<FragmentTransferChequeBinding, TransferViewModel>(
    FragmentTransferChequeBinding::inflate, TransferViewModel::class.java
) {

    private lateinit var transferDto: TransferDto

    companion object {
        const val OPERATION_DATE = "operation_date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferDto = requireArguments().serializable<TransferDto>(SuccessTransferFragment.TRANSFER_DTO) as TransferDto
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        binding.btnShare.setOnClickListener {
            shareCheque()
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun initDetails() {
        val percent = transferDto.commission ?: 0.0
        val commissionAmount = percent * transferDto.transferAmount?.toDouble()!! / 10000
        binding.apply {
            operationName.text = getOperationName()
            senderCard.text = Format.formatCardNumber(transferDto.senderCard?.object_value ?: "")
            senderName.text = transferDto.senderCard?.embossed_name
            receiverCard.text = Format.formatCardNumber(transferDto.receiverCard?.card_number ?: "")
            receiverName.text = transferDto.receiverCard?.card_owner
            operationTime.text = requireArguments().getString(OPERATION_DATE)
            commission.text = "$percent % (" + Format.formatAmount(commissionAmount.toString()) + " " + getString(
                R.string.sum_text
            ) + ")"
            totalAmount.text = Format.formatAmount((transferDto.transferAmount?.toDouble()?.div(100)).toString()) + " " + getString(
                R.string.sum_text
            )
        }
    }

    private fun getOperationName(): String {
        return when (transferDto.operation) {
            SuccessTransferFragment.TRANSFER_BY_CARD -> getString(R.string.transfer_to_card)
            SuccessTransferFragment.TRANSFER_BY_PHONE -> getString(R.string.by_phone_number)
            SuccessTransferFragment.TRANSFER_BY_WALLET -> getString(R.string.by_wallet_number)
            SuccessTransferFragment.TRANSFER_OVER_MY_CARDS -> getString(R.string.over_my_cards)
            else -> getString(R.string.transfer)
        }
    }

    private fun shareCheque() {
        binding.infoBlock.takeScreenShot(requireActivity()) { bitmap ->
            bitmap?.let {
                val path = FileUtils.saveImageToGallery(requireContext(), it, "Universal Digital")
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val uri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", File(path))
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Send to"))
            }
        }
    }

}