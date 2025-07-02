package uz.fido.universaldigital.ui.fragments.transfers.success

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.DialogTemplateSaveBinding
import uz.fido.universaldigital.databinding.FragmentSuccessTransferBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.Companion.OPERATION_P2P
import uz.fido.universaldigital.ui.fragments.transfers.cheque.TransferChequeFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class SuccessTransferFragment :
    BaseFragment<FragmentSuccessTransferBinding, SuccessTransferViewModel>
        (FragmentSuccessTransferBinding::inflate, SuccessTransferViewModel::class.java) {

    private lateinit var transferDto: TransferDto
    private lateinit var now: Date
    private var operation: String = ""


    companion object {
        const val TRANSFER_DTO = "TRANSFER_DTO"
        const val TRANSFER_OPERATION = "TRANSFER_OPERATION"
        const val TRANSFER_BY_CARD = "TRANSFER_BY_CARD"
        const val TRANSFER_BY_PHONE = "TRANSFER_BY_PHONE"
        const val TRANSFER_CARD_BY_PHONE = "TRANSFER_CARD_BY_PHONE"
        const val TRANSFER_BY_WALLET = "TRANSFER_BY_WALLET"
        const val TRANSFER_OVER_MY_CARDS = "TRANSFER_OVER_MY_CARDS"
        const val TRANSFER_OVER_MY_CARDS_BY = "TRANSFER_OVER_MY_CARDS_BY"
        const val TRANSFER_OVER_MY_KL_AND_KL = "TRANSFER_OVER_KL_AND_KL"
        const val CONVERSION = "CONVERSION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferDto = requireArguments().serializable<TransferDto>(TRANSFER_DTO) as TransferDto
        operation = requireArguments().getString(TRANSFER_OPERATION, "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated:${operation} ")
        when (operation) {
            TRANSFER_BY_CARD, TRANSFER_CARD_BY_PHONE, TRANSFER_OVER_MY_CARDS_BY -> {
                binding.repeat.visibility = View.VISIBLE
                binding.tempate.visibility = View.VISIBLE
            }
            TRANSFER_OVER_MY_KL_AND_KL->{
                binding.repeat.visibility = View.VISIBLE
                binding.tempate.visibility = View.GONE
            }

            else -> {
                binding.repeat.visibility = View.GONE
                binding.tempate.visibility = View.GONE
            }
        }
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
        binding.repeat.setOnClickListener {
            repeatTransfer()
        }
        binding.tempate.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        when (operation) {
            TRANSFER_BY_CARD, TRANSFER_CARD_BY_PHONE, TRANSFER_OVER_MY_CARDS_BY -> {
                val templateView = DialogTemplateSaveBinding.inflate(LayoutInflater.from(context), null, false)
              AlertDialog.Builder(requireContext())
                    .setView(templateView.root)
                    .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                        val text = templateView.etNameMin.text.toString()
                        createTemplate(text)
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()



            }
        }
    }

    private fun createTemplate(text: String) {
        showProgress()
        val hashMap = HashMap<String, String>()

        hashMap["CARD_NUMBER"] = transferDto.receiverCard?.card_number.toString()
        hashMap["AMOUNT"] = "${transferDto.transferAmount}"
        val model = CreateTemplateRequest(
            name = text,
            template_type = "C",
            service_type = transferDto.receiverCard?.card_number.toString(),
            payment_details = hashMap,
            template_group_id = "1",
            service_id = "-1"
        )
        viewModel.createTemplate(getClientToken(), model).observe(viewLifecycleOwner) {
            when (Status.SUCCESS) {
                Status.SUCCESS -> {
                    hideProgress()
                    Toast.makeText(requireContext(), getString(R.string.successful), Toast.LENGTH_SHORT).show()
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun repeatTransfer() {
        when (operation) {
            TRANSFER_BY_CARD -> findNavController().popBackStack(R.id.transferToCardFragment, false)
            TRANSFER_CARD_BY_PHONE -> findNavController().popBackStack(R.id.transferByPhoneFragment, false)
            TRANSFER_OVER_MY_CARDS_BY, TRANSFER_OVER_MY_KL_AND_KL -> findNavController().popBackStack(R.id.overMyCardsFragment, false)
        }
        /*   val result = Bundle().apply {
               putString(PopularTransfersFragment.DATA, transferDto.receiverCard?.card_number)
           }
          // parentFragmentManager.setFragmentResult(PopularTransfersFragment.REQUEST_KEY, result)*/

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
                uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.OPERATION to OPERATION_P2P,
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