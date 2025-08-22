package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.AddCardRequest
import uz.fido.network.domain.model.cards.CheckCardRequest
import uz.fido.nfccardreaderlib.ScanNfcCardActivity
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAddCardBinding
import uz.fido.universaldigital.ui.dialogs.ChooseScanCardOptionDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.adapter.CardBackgroundAdapter
import uz.fido.universaldigital.ui.fragments.transfers.utils.checkCardNumber
import uz.fido.universaldigital.ui.utils.extensions.getCardBackgroundList
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.context.AppSignatureHelper
import uz.fido.utils.utility.context.checkForExpireDate
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.scan_card.cardscan.ScanActivity

@AndroidEntryPoint
class AddCardFragment : BaseFragment<FragmentAddCardBinding, MenuProductsViewModel>(
    FragmentAddCardBinding::inflate, MenuProductsViewModel::class.java
), PermissionInterface {

    companion object {
        const val OPERATION_OVER_MY_CARDS = "over_my_cards"
        const val OPERATION_CARD_TO_CARD = "card_to_card"
        const val OPERATION_BY_WALLET = "by_wallet"
        const val OPERATION_BY_PHONE = "by_phone"
    }

    private var isMain = "N"
    private lateinit var addCardOperation: String
    private var cardDate: Boolean = false
    private var cardNumberAndName: Boolean = false

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        arguments?.let {
            if (it.getString(Const.ADD_CARD_OPERATION) != null) {
                addCardOperation = it.getString(Const.ADD_CARD_OPERATION, "")
            }
        }
        init()
        checkEditTexts()
        initCardBgViewPager()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.addCardBtn.setOnClickListener {
            if (isValid(binding.cardNumber.editableText.toString().replace(" ", ""))) {
                checkCardRequest()
            } else {
                showSnackbar(getString(R.string.error_card_number))
            }
        }
        binding.imageScanner.setOnClickListener {
            val dialog = ChooseScanCardOptionDialog(onCameraClickListener = {
                if (checkForCameraPermission(this@AddCardFragment)) {
                    openCameraForCardRead()
                }
            }, onNFCClickListener = {
                val intent = Intent(requireActivity(), ScanNfcCardActivity::class.java)
                activityNfcLauncher.launch(intent)
            })
            dialog.show(childFragmentManager, "")
        }
    }

    private fun init() {
        cardDateMask()
        binding.cardNumber.addTextChangedListener { checkEditTexts() }
        binding.makeMain.setOnCheckedChangeListener { _, isChecked ->
            isMain = if (isChecked) "Y" else "N"
        }

    }

    private fun cardDateMask() {
        binding.cardExpire.addTextChangedListener(object : TextWatcher {
            var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                s?.let {
                    if (it.isNotEmpty()) {
                        requireContext().checkForExpireDate(
                            binding.cardExpire.text.toString(),
                            binding.cardExpire,
                            binding.cardExpireLayout
                        )
                    }
                    var text = it.toString()
                    text = text.replace(Regex("[^0-9/]"), "")

                    if (text.isNotEmpty() && text[0] == '/') {
                        text = text.substring(1)
                    }
                    if (text.length in 2..3 && text[2 - 1] == '/' && text.length < 3) {
                        text = text.replace("/", "")
                    }

                    if (text.length > 2 && text[2] != '/') {
                        text = text.substring(0, 2) + "/" + text.substring(2)
                    }

                    if (text.length > 5) {
                        text = text.substring(0, 5)
                    }

                    if (text != it.toString()) {
                        binding.cardExpire.setText(text)
                        binding.cardExpire.setSelection(text.length)
                    }
                }
                cardDate = if (binding.cardExpireLayout.error == null && binding.cardExpire.text.length == 5) {
                    true
                } else {
                    false
                }
                binding.addCardBtn.isEnabled(
                    cardDate && cardNumberAndName
                )

                isEditing = false
            }
        })

    }

    private fun checkEditTexts() {
        val editTexts = listOf(
            binding.cardNumber,
            binding.cardName,
        )
        for (editText in editTexts) {
            editText.doAfterTextChanged {
                var isTrueCard = true
                val et1 = binding.cardNumber.text.toString().trim().replace(" ", "")
                val et3 = binding.cardName.text.toString()

                if (binding.cardExpire.text.toString().isEmpty()) {
                    isTrueCard = false
                }
                if (binding.cardNumberLayout.error != null) {
                    isTrueCard = false
                }
                if (et1.isEmpty()) {
                    binding.cardNumberLayout.error = null
                }
                cardNumberAndName = binding.cardNumber.text.toString().replace(" ", "").length == 16
                        && binding.cardName.text.toString().isNotEmpty()
                binding.addCardBtn.isEnabled(
                    et1.length == 16 && et3.isNotEmpty() && isTrueCard && cardDate
                )
            }
        }
    }

    private fun checkCardRequest() {
        val cardNumber = binding.cardNumber.editableText.toString().replace(" ", "")
        val expireDate =
            Format.sentExpireDate(binding.cardExpire.editableText.toString().replace("/", ""))
        val cardName = binding.cardName.editableText.toString().trim()
        if (cardName.isEmpty()) return
        binding.addCardBtn.setProgress(true)
        viewModel.checkCardRequest(
            getClientToken(), CheckCardRequest(
                expireDate,
                cardNumber,
                getFromSecureStore(Const.PAPER_CLIENT_PHONE),
                AppSignatureHelper(requireContext()).appKeyHash,
                requireContext().getDeviceIds()
            )
        ).observe(viewLifecycleOwner) {
            it?.let {
                binding.addCardBtn.setProgress(false)
                when (it.status) {
                    Status.SUCCESS -> {
                        val addCardRequest = AddCardRequest(
                            object_value = cardNumber,
                            object_expiry = expireDate,
                            phone_number = getFromSecureStore(Const.PAPER_CLIENT_PHONE),
                            object_name = cardName,
                            sms_code = "",
                            is_main = isMain,
                            bg_icon_name = "bg_1",
                            otp_id = it.data?.otp_id ?: "",
                            string_line = "",
                            application_id = it.data?.application_id ?: ""
                        )
                        val bundle = Bundle()
                        bundle.putString(Const.OPERATION, ConfirmSmsFragment.ADD_CARD)
                        if (this::addCardOperation.isInitialized && addCardOperation.isNotEmpty()) {
                            bundle.putString(Const.ADD_CARD_OPERATION, addCardOperation)
                        }
                        bundle.putSerializable("data", addCardRequest)
                        bundle.putInt(
                            ConfirmSmsFragment.SMS_MAX_LENGTH,
                            it.data?.sms_length ?: 8
                        )
                        gotoWithSlide(R.id.confirmSmsFragment, bundle)
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
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
            val expireDate = scanResult?.expiryForDisplay()
            if (result != null) {
                binding.cardNumber.setText(result)
                if (!checkCardNumber(result)) {
                    binding.cardNumberLayout.error = getString(R.string.invalid_card_number)
                }
                if (expireDate != null) {
                    if (expireDate.length != 5 && !expireDate.toString().contains("/")) {
                        binding.cardExpire.setText(expireDate)
                        binding.cardExpireLayout.error =
                            getString(R.string.wrong_format)
                    } else if (!expireDate.toString()
                            .contains("//") && expireDate.length == 5
                    ) {
                        binding.cardExpireLayout.error = null
                        binding.cardExpire.setText(expireDate)
                    }
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
            binding.cardExpire.setText(result.data?.extras?.getString("card_expire"))
        }
    }

    private fun initCardBgViewPager() {
        val cardBgAdapter = CardBackgroundAdapter(requireContext(), getCardBackgroundList(), this)
        binding.viewPager.adapter = cardBgAdapter
        binding.dotsIndicator.setViewPager(binding.viewPager)
    }

    private fun isValid(cardNumber: String): Boolean {
        var s1 = 0
        var s2 = 0
        val reverse = StringBuffer(cardNumber).reverse().toString()
        for (i in reverse.indices) {
            val digit = Character.digit(reverse[i], 10)
            when {
                i % 2 == 0 -> s1 += digit
                else -> {
                    s2 += 2 * digit
                    when {
                        digit >= 5 -> s2 -= 9
                    }
                }
            }
        }
        return (s1 + s2) % 10 == 0
    }

}




