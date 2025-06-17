package uz.fido.universaldigital.ui.fragments.transfers.confirm_transfer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmSmsBinding
import uz.fido.universaldigital.services.SMSBroadcastReceiver
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsViewModel
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.getServiceIdInfo
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.user.getFormattedClientPhone
import java.text.DecimalFormat
import java.text.NumberFormat

@AndroidEntryPoint
@SuppressLint("UnspecifiedRegisterReceiverFlag")
class ConfirmSmsForTransfer : BaseFragment<FragmentConfirmSmsBinding, ConfirmSmsViewModel>(
    FragmentConfirmSmsBinding::inflate, ConfirmSmsViewModel::class.java
) {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var p2pRequest: P2PRequest
    private lateinit var transferDto: TransferDto

    private var operation: String = ""
    private var stringLine: String = ""

    companion object {
        const val TRANSFER_REQUEST = "transfer_request"
        const val STRING_LINE = "string_line"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            transferDto = requireArguments().serializable<TransferDto>(SuccessTransferFragment.TRANSFER_DTO) as TransferDto
            operation = transferDto.operation.toString()
            p2pRequest = requireArguments().serializable<P2PRequest>(TRANSFER_REQUEST) as P2PRequest
            stringLine = requireArguments().getString(STRING_LINE).toString()
        } catch (e: Exception) {
            recordException(e, ::onCreate.name)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        startResendTimer()
        setSubTitle()
        registerSMSReceiver()
        initTextChangeListener()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.resendButton.setOnClickListener {
            when (operation) {
                SuccessTransferFragment.TRANSFER_BY_CARD,
                SuccessTransferFragment.TRANSFER_OVER_MY_CARDS,
                SuccessTransferFragment.TRANSFER_BY_PHONE,
                SuccessTransferFragment.TRANSFER_BY_WALLET -> {
                    checkSms()
                }
            }
        }
    }

    private fun checkSms() {
        checkForSms(
            card = transferDto.senderCard!!,
            amount = transferDto.transferAmount!!,
            serviceId = getServiceIdInfo(transferDto.receiverCard?.card_number!!, transferDto.senderCard!!.object_value),
        ) { _, stringLine ->
            this.stringLine = stringLine
            resendButtonClickEvent()
        }
    }

    private fun initTextChangeListener() {
        binding.etSms.addTextChangedListener {
            binding.btnContinue.isEnabled(it.toString().length == 8)
        }
    }

    private fun continueButtonClickEvent() {
        when (operation) {
            SuccessTransferFragment.TRANSFER_BY_CARD,
            SuccessTransferFragment.TRANSFER_OVER_MY_CARDS,
            SuccessTransferFragment.TRANSFER_BY_PHONE,
            SuccessTransferFragment.TRANSFER_BY_WALLET -> {
                transferRequest()
            }
        }
    }

    private fun transferRequest() {
        binding.btnContinue.setProgress(true)
        p2pRequest.string_line = CryptoUtil.encryptWithoutSalt(
            stringLine,
            binding.etSms.editableText.toString()
        )
        viewModel.p2pRequest(
            getClientToken(), p2pRequest
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data?.createdDocuments
                    try {
                        if (!response.isNullOrEmpty()) {
                            transferDto.requestId = response.first().transactionId.toString()
                        } else transferDto.requestId = it.data?.request_id.orEmpty()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    gotoWithSlide(
                        R.id.action_confirmSmsForTransfer_to_successTransferFragment,
                        bundleOf(
                            SuccessTransferFragment.TRANSFER_OPERATION to requireArguments().getString(SuccessTransferFragment.TRANSFER_OPERATION,""),
                            SuccessTransferFragment.TRANSFER_DTO to transferDto)
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setSubTitle() {
        binding.appBar.setSubtitle(
            getString(
                R.string.sent_to_phone,
                getFormattedClientPhone()
            )
        )
    }

    private fun startResendTimer() {
        countDownTimer = object : CountDownTimer(60000L, 1000) {
            override fun onFinish() {
                binding.tvResendAfter.visibility = View.GONE
                binding.resendButton.visibility = View.VISIBLE
                countDownTimer.cancel()
            }

            override fun onTick(p0: Long) {
                updateResendTime(p0)
            }
        }
        countDownTimer.start()
    }

    private fun updateResendTime(timeInMilliSeconds: Long) {
        if (context != null && this.isAdded) {
            val minute = (timeInMilliSeconds / 1000) / 60
            val seconds = (timeInMilliSeconds / 1000) % 60
            val f: NumberFormat = DecimalFormat("00")
            binding.tvResendAfter.text =
                getString(R.string.left_time, "${f.format(minute)}:${f.format(seconds)}")
        }
    }

    private fun resendButtonClickEvent() {
        binding.resendButton.visibility = View.GONE
        binding.tvResendAfter.visibility = View.VISIBLE
        startResendTimer()
    }

    private val smsBroadcastReceiver: SMSBroadcastReceiver = object : SMSBroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val otpCode = intent.getStringExtra("otp")
            if (!otpCode.isNullOrEmpty()) {
                binding.etSms.setText(otpCode)
            }
        }
    }

    private fun registerSMSReceiver() {
        try {
            val task: Task<Void> = SmsRetriever.getClient(activity ?: return).startSmsRetriever()
            task.addOnSuccessListener {
                activity?.let { a ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        a.registerReceiver(
                            smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_EXPORTED
                        )
                    } else {
                        a.registerReceiver(
                            smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                        )
                    }
                }
            }
            task.addOnFailureListener {}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        try {
            countDownTimer.cancel()
            requireActivity().unregisterReceiver(smsBroadcastReceiver)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        super.onDestroyView()
    }

}