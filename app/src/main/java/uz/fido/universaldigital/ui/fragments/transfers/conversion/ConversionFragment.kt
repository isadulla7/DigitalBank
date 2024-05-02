package uz.fido.universaldigital.ui.fragments.transfers.conversion

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.databinding.FragmentConversionBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.main_dialogs.ChooseCardDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getCommand
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isNotActive
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.extensions.setCardState
import uz.fido.universaldigital.ui.utils.extensions.whiteCardLogoByType
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_USD
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_UZS
import uz.fido.utils.const.ServiceId
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ConversionFragment : BaseFragment<FragmentConversionBinding, ConversionViewModel>(
    FragmentConversionBinding::inflate, ConversionViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private var currencyCardList = ArrayList<CardResponse>()
    private var sumCardList = ArrayList<CardResponse>()
    private var currencyCard: CardResponse? = null
    private var sumCard: CardResponse? = null

    private var sumCardX = 0.0f
    private var sumCardY = 0.0f
    private var currencyCardX = 0.0f
    private var currencyCardY = 0.0f
    private var sumToCurrency = true

    private var sellingRate = 1.0
    private var buyingRate = 1.0
    private var currencyCode = CURRENCY_CODE_USD

    private lateinit var chooseCardDialog: ChooseCardDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utilsViewModel.updateRates()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initCards()
        initCurrencyRate()
        initSetOnClickListeners()
        initCurrencyTabs()
        initAmountTextWatcher()
        setSenderCardDetails(if (sumToCurrency) sumCard else currencyCard)
        setReceiverCardDetails(if (sumToCurrency) currencyCard else sumCard)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCardsFromBundle()
    }

    private fun initSetOnClickListeners() {
        binding.sumCardBlock.setOnClickListener {
            setSumCard()
        }
        binding.currencyCardBlock.setOnClickListener {
            setCurrencyCard()
        }
        binding.conversionChanger.setOnClickListener {
            if (sumCard != null && currencyCard != null) {
                vibrateTick(requireContext())
                currencyChangeButtonClickEvent()
            }
        }
        binding.btnContinue.setOnClickListener { gotoSecondStep() }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun setSumCard() {
        chooseCardDialog = ChooseCardDialog(sumCardList) {
            chooseCardDialog.dismiss()
            it?.let {
                sumCard = it
                setSenderCardDetails(if (sumToCurrency) sumCard else currencyCard)
            }
        }
        chooseCardDialog.show(childFragmentManager, "")
    }

    private fun setCurrencyCard() {
        chooseCardDialog = ChooseCardDialog(currencyCardList) {
            chooseCardDialog.dismiss()
            it?.let {
                currencyCard = it
                setReceiverCardDetails(if (sumToCurrency) currencyCard else sumCard)
            }
        }
        chooseCardDialog.show(childFragmentManager, "")
    }

    private fun setReceiverCardDetails(cardResponse: CardResponse?) {
        cardResponse?.let {
            binding.apply {
                tvChooseCurrencyCard.visibility = View.GONE
                receiverCardBalance.setCardBalance(it)
                receiverCardName.text = it.object_name
                receiverCardBackground.load(requireContext().getDrawableFromRes(it.bg_icon_name))
                receiverCardType.setImageResource(whiteCardLogoByType(it))
                receiverCardNumber.text =
                    if (it.object_type != WALLET) Format.formatCardNumberNew(it.object_value) else Format.formatWalletNumber(
                        it.object_value
                    )
                setCardState(it, binding.receiverCardState, requireContext())
                btnContinue.isEnabled(checkForContinueButton())
            }
        }
    }

    private fun setSenderCardDetails(cardResponse: CardResponse?) {
        cardResponse?.let {
            binding.apply {
                tvChooseSumCard.visibility = View.GONE
                senderCardBalance.setCardBalance(it)
                senderCardName.text = it.object_name
                senderCardBg.load(requireContext().getDrawableFromRes(it.bg_icon_name))
                senderCardType.setImageResource(whiteCardLogoByType(it))
                senderCardNumber.text = if (it.object_type != WALLET) Format.formatCardNumberNew(
                    it.object_value
                ) else Format.formatWalletNumber(it.object_value)
                setCardState(it, binding.senderCardState, requireContext())
                btnContinue.isEnabled(checkForContinueButton())
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            cardList.forEach {
                if (it.object_type == UZCARD || it.object_type == HUMO_CARD || (it.object_type == WALLET && it.currency_code == CURRENCY_CODE_UZS)) {
                    sumCardList.add(it)
                } else if (it.currency_code == CURRENCY_CODE_USD) {
                    currencyCardList.add(it)
                }
            }
        }
    }

    private fun currencyChangeButtonClickEvent() {
        sumCardX = binding.sumCardBlock.x
        sumCardY = binding.sumCardBlock.y
        currencyCardX = binding.currencyCardBlock.x
        currencyCardY = binding.currencyCardBlock.y
        if (sumToCurrency) {
            animateBlocks()
            sumToCurrency = false
            binding.currentRate.text = "1$ = $buyingRate UZS"
            binding.tvMinAmount.text = "1$ = $buyingRate UZS"
            setTotalAmount(binding.etAmount.editableText.toString(), currencyCode)
        } else {
            animateBlocks()
            sumToCurrency = true
            binding.currentRate.text = "1$ = $sellingRate UZS"
            binding.tvMinAmount.text = "1$ = $sellingRate UZS"
            setTotalAmount(binding.etAmount.editableText.toString(), currencyCode)
        }
        binding.btnContinue.isEnabled(checkForContinueButton())
    }

    private fun animateBlocks() {
        binding.sumCardBlock.animate().x(currencyCardX).y(currencyCardY).setDuration(300)
            .withEndAction {
                binding.conversionChanger.isClickable = true
            }.withStartAction {
                binding.conversionChanger.isClickable = false
            }.start()
        binding.currencyCardBlock.animate().x(sumCardX).y(sumCardY).setDuration(300).start()
    }

    private fun initCurrencyTabs() {
        binding.tabLayout.apply {
            addTab(newTab().setText("USD"))
            addTab(newTab().setText("UZS"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        if (it.position == 1) {
                            currencyCode = CURRENCY_CODE_UZS
                            setTotalAmount(
                                binding.etAmount.editableText.toString(), currencyCode
                            )
                            binding.btnContinue.isEnabled(checkForContinueButton())
                        } else {
                            currencyCode = CURRENCY_CODE_USD
                            setTotalAmount(
                                binding.etAmount.editableText.toString(), currencyCode
                            )
                            binding.btnContinue.isEnabled(checkForContinueButton())
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    internal fun setTotalAmount(amount: String, currencyCode: String) {
        if (amount != "" && !amount.startsWith(" ")) {
            binding.calculatedAmountLayout.visibility = View.VISIBLE
            val fixedAmount = amount.replace(" ", "").toDouble()
            if (currencyCode == CURRENCY_CODE_USD) {
                if (sumToCurrency) {
                    binding.totalAmount.text =
                        Format.conversionFormat(fixedAmount * sellingRate) + " " + getString(R.string.sum_text)
                } else {
                    binding.totalAmount.text =
                        Format.conversionFormat(fixedAmount * buyingRate) + " " + getString(R.string.sum_text)
                }
            } else {
                if (sumToCurrency) {
                    binding.totalAmount.text =
                        Format.conversionFormat(fixedAmount / sellingRate) + " $"
                } else {
                    binding.totalAmount.text =
                        Format.conversionFormat(fixedAmount / buyingRate) + " $"
                }
            }
        } else {
            binding.totalAmount.text = ""
            binding.calculatedAmountLayout.visibility = View.INVISIBLE
        }
    }

    internal fun checkForContinueButton(): Boolean {
        return if (sumCard != null && currencyCard != null) {
            val amount = binding.etAmount.editableText.toString()
            if (sumCard!!.isNotActive() || currencyCard!!.isNotActive()) return false
            if (amount.isNotEmpty() && !amount.startsWith(" ")) {
                val fixedAmount = amount.replace(" ", "").toDouble()
                if (currencyCode == CURRENCY_CODE_UZS) {
                    if (sumToCurrency) {
                        (fixedAmount >= sellingRate && sumCard!!.balance.toDouble() / 100 >= fixedAmount)
                    } else (fixedAmount >= buyingRate && currencyCard!!.balance.toDouble() / 100 >= fixedAmount / buyingRate)
                } else {
                    if (sumToCurrency) {
                        (fixedAmount >= 1.0 && sumCard!!.balance.toDouble() / 100 >= fixedAmount * sellingRate)
                    } else (fixedAmount >= 1.0 && currencyCard!!.balance.toDouble() / 100 >= fixedAmount)
                }
            } else false
        } else false
    }

    private fun initAmountTextWatcher() {
        binding.etAmount.doAfterTextChanged { editable ->
            editable?.let {
                binding.btnContinue.isEnabled(checkForContinueButton())
                val formattedString = if (it.toString() == " ") "" else it
                setTotalAmount(formattedString.toString(), currencyCode)
                binding.minAmountLayout.isVisible = it.toString().isEmpty()
            }
        }
    }

    private fun initCurrencyRate() {
        binding.currentRate.text = "1$ = $sellingRate UZS"
        utilsViewModel.currencyRates.observe(viewLifecycleOwner) {
            val rates = it ?: ArrayList()
            rates.forEach { courseItem ->
                if (courseItem.currencyCode == CURRENCY_CODE_USD) {
                    sellingRate = courseItem.sellingRate
                    buyingRate = courseItem.buyingRate
                }
                binding.currentRate.text =
                    if (sumToCurrency) "1$ = $sellingRate UZS" else "1$ = $buyingRate UZS"
                binding.tvMinAmount.text =
                    if (sumToCurrency) "1$ = $sellingRate UZS" else "1$ = $buyingRate UZS"
            }

        }
    }

    private fun gotoSecondStep() {
        val amount = binding.etAmount.editableText.toString().replace(" ", "").replace(",", "")
        val request = ConversionRequest(
            command = if (sumToCurrency) {
                getCommand(sumCard!!, currencyCard!!)
            } else getCommand(currencyCard!!, sumCard!!),
            from_object_id = if (!sumToCurrency) currencyCard!!.object_id else sumCard!!.object_id,
            to_object_value = if (!sumToCurrency) sumCard!!.object_value else currencyCard!!.object_value,
            to_object_expire = if (!sumToCurrency) sumCard!!.object_expiry else currencyCard!!.object_expiry,
            amount = Format.conversionFormat((amount.toDouble() * 100)).replace(",", ".")
                .replace(" ", ""),
            currency_code = currencyCode,
            service_id = ServiceId.SERVICE_ID__9
        )
        val bundle = Bundle()
        bundle.putString(
            ConfirmConversionFragment.TOTAL_AMOUNT, binding.totalAmount.text.toString()
        )
        bundle.putSerializable(ConfirmConversionFragment.CONVERSION_REQUEST, request)
        bundle.putString(ConfirmConversionFragment.CURRENCY_CODE, currencyCode)
        bundle.putString(
            ConfirmConversionFragment.CURRENT_RATE, binding.currentRate.text.toString()
        )
        bundle.putSerializable(
            ConfirmConversionFragment.SENDER_CARD, if (!sumToCurrency) currencyCard else sumCard
        )
        bundle.putSerializable(
            ConfirmConversionFragment.RECEIVER_CARD, if (sumToCurrency) currencyCard else sumCard
        )
        gotoWithSlide(R.id.confirmConversionFragment, bundle)
    }

    private fun setCardsFromBundle() {
        arguments?.let {
            val senderCard =
                requireArguments().serializable<CardResponse>(Const.SENDER_CARD)
            val receiverCard =
                requireArguments().serializable<CardResponse>(Const.RECEIVER_CARD)
            if (senderCard != null && receiverCard != null) {
                setSenderCardDetails(senderCard)
                setReceiverCardDetails(receiverCard)
                if (senderCard.currency_code == CURRENCY_CODE_UZS) {
                    sumCard = senderCard
                    currencyCard = receiverCard
                    setSenderCardDetails(senderCard)
                    setReceiverCardDetails(receiverCard)
                    sumToCurrency = true
                } else {
                    sumCard = receiverCard
                    currencyCard = senderCard
                    sumToCurrency = false
                    setSenderCardDetails(receiverCard)
                    setReceiverCardDetails(senderCard)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isVisible) {
                            binding.conversionChanger.performClick()
                        }
                    }, 200)
                }
            }
        }
    }

}