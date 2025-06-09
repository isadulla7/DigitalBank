package uz.fido.universaldigital.ui.fragments.services.sms_notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.home.CheckSMSActivateRequest
import uz.fido.nfccardreaderlib.ScanNfcCardActivity
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConnectSmsNotificationBinding
import uz.fido.universaldigital.ui.dialogs.ChooseScanCardOptionDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.checkCardNumber
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.user.getFormattedClientPhone
import uz.scan_card.cardscan.ScanActivity

@AndroidEntryPoint
class ConnectSmsNotificationFragment : BaseFragment<FragmentConnectSmsNotificationBinding, ConnectSmsNotificationViewModel>(
    FragmentConnectSmsNotificationBinding::inflate, ConnectSmsNotificationViewModel::class.java
), PermissionInterface {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initTextChangeListener()
        initSetOnClickListeners()
        initPhoneNumber()
    }

    private fun initPhoneNumber() {
        val phoneNumber = getFormattedClientPhone()
        binding.smsPhone.text = getString(R.string.sms_notification_turned_on_current_phone, phoneNumber)
    }

    private fun initTextChangeListener() {
        binding.cardNumber.addTextChangedListener(createCardNumberTextWatcher())

        binding.cardNumber.doAfterTextChanged {
            it?.let {
                if (it.toString().replace(" ","").length == 16 && it.startsWith("9860")) {
                    binding.btnContinue.isEnabled(true)
                    val cardNumberFormatted = binding.cardNumber.text.toString().replace(" ", "")
                    getObjectInfo(cardNumberFormatted)
                } else {
                    binding.btnContinue.isEnabled(false)
                    binding.ownerName.isVisible = false
                }
            }
        }
    }

    private fun createCardNumberTextWatcher(): TextWatcher {
        var isFormatting = false
        var previousText = ""

        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true
                val digits = s.toString().replace(" ", "")
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    formatted.append(digits[i])
                    if ((i + 1) % 4 == 0 && i != digits.length - 1) {
                        formatted.append(" ")
                    }
                }

                s?.replace(0, s.length, formatted.toString())

                isFormatting = false
                if (s.toString().replace(" ","").length == 16 && s.toString().startsWith("9860")) {
                    binding.btnContinue.isEnabled(true)
                    val cardNumberFormatted = binding.cardNumber.text.toString().replace(" ", "")
                    getObjectInfo(cardNumberFormatted)
                } else {
                    binding.btnContinue.isEnabled(false)
                    binding.ownerName.isVisible = false
                }

            }
        }


    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            val cardNumberFormatted = binding.cardNumber.text.toString().replace(" ", "")
            checkSmsActivate(cardNumberFormatted)
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.imageScanner.setOnClickListener {
            val dialog = ChooseScanCardOptionDialog(onCameraClickListener = {
                if (checkForCameraPermission(this@ConnectSmsNotificationFragment)) {
                    openCameraForCardRead()
                }
            }, onNFCClickListener = {
                val intent = Intent(requireActivity(), ScanNfcCardActivity::class.java)
                activityNfcLauncher.launch(intent)
            })
            dialog.show(childFragmentManager, "")
        }
    }

    private fun getObjectInfo(cardNumber: String) {
        viewModel.getCardInfo(getClientToken(), CheckCardRequestP2p("card", cardNumber)).observe(viewLifecycleOwner) {
            if (it.status == Status.SUCCESS) {
                val response = it.data as CheckCardResponse
                setOwnerName(response.empbossed_name)
            }
        }
    }

    private fun checkSmsActivate(cardNumber: String) {
        binding.btnContinue.setProgress(true)
        viewModel.checkSMSActivate(
            getClientToken(), CheckSMSActivateRequest(
                object_value = cardNumber,
                app_key_hash = AppSignatureHelper(requireContext()).appKeyHash
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val line = it.data?.string_line ?: ""
                    gotoWithSlide(
                        R.id.confirmSmsFragment, bundleOf(
                            Const.OPERATION to ConfirmSmsFragment.SMS_OPERATION_CONNECT_SMS_INFO,
                            Const.PHONE_NUMBER to getClientPhoneNumber(),
                            Const.CARD_NUMBER to cardNumber,
                            ConfirmSmsFragment.STRING_LINE to line
                        )
                    )
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setOwnerName(ownerName: String) {
        binding.ownerName.isVisible = true
        binding.ownerName.text = ownerName
    }

    private fun openCameraForCardRead() {
        val intent = ScanActivity.buildIntent(
            requireActivity(), true, null, R.string.card_scan_position_card, null, null
        )
        getActivityResult.launch(intent)
    }

    private val getActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val scanResult = ScanActivity.creditCardFromResult(it.data)
            val result = scanResult?.number
            if (result != null) {
                binding.cardNumber.setText(result)
                if (!checkCardNumber(result)) {
                    binding.cardNumberLayout.error = getString(R.string.invalid_card_number)
                }
            }
        }
    }

    override fun cameraPermissionGranted() {
        openCameraForCardRead()
    }

    private val activityNfcLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            binding.cardNumber.setText(result.data?.extras?.getString("card_number"))
        }
    }

}