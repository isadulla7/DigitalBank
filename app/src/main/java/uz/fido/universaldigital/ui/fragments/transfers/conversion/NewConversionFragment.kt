package uz.fido.universaldigital.ui.fragments.transfers.conversion

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConversionBinding
import uz.fido.universaldigital.ui.dialogs.ChooseCardDialog
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getCommand
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isNotActive
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isUniversalCardAndVisa
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
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
import java.math.BigDecimal
import java.math.RoundingMode

class NewConversionFragment: BaseFragment<FragmentConversionBinding, ConversionViewModel>(
    FragmentConversionBinding::inflate, ConversionViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private var currencyCardList = ArrayList<CardResponse>()
    private var sumCardList = ArrayList<CardResponse>()
    private var allCardList = ArrayList<CardResponse>()
    private var receiverCard: CardResponse? = null
    private var senderCard: CardResponse? = null
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
        getArgument()
        initCurrencyTabs()
        initCards()
        initCurrencyRate()
        initSetOnClickListeners()   
        initAmountTextWatcher()


    }

    private fun getArgument() {
        arguments?.let {
            senderCard= it.getSerializable(Const.SENDER_CARD) as CardResponse
            receiverCard= it.getSerializable(Const.RECEIVER_CARD) as CardResponse
            val amount = it.getString("amount")?:""
            if (!amount.isNullOrEmpty()){
            binding.etAmount.setText(amount.replace(" ","").toBigDecimal().divide(BigDecimal(100)).toString())
            binding.btnContinue.isEnabled(checkForContinueButton())
            setTotalAmount(amount)
            }
        }

    }


    private fun initCurrencyTabs() {
        currencyCode=if (binding.appSwitch.isChecked) CURRENCY_CODE_UZS else CURRENCY_CODE_USD
        binding.appSwitch.setOnCheckedChangeListener { _, isEnable ->
            currencyCode=if (isEnable) CURRENCY_CODE_UZS else CURRENCY_CODE_USD
            binding.btnContinue.isEnabled(checkForContinueButton())
            setTotalAmount(binding.etAmount.editableText.toString())
        }
    }
    private fun initAmountTextWatcher() {
        binding.etAmount.doAfterTextChanged { editable ->
            editable?.let {
                binding.btnContinue.isEnabled(checkForContinueButton())
                val formattedString = if (it.toString() == " ") "" else it
                setTotalAmount(formattedString.toString())
            }
        }
    }

    internal fun setTotalAmount(amount: String){
        if (amount != "" && !amount.startsWith(" ")){
            checkErrorWatcher(amount)
            detailsCalculate(amount.replace(" ",""))
        }else{
            val curr= if (senderCard?.currency_code=="840") buyingRate else sellingRate
            binding.tvCurrency.text = "1$ = ${Format.formatAmount(curr.toString())} UZS"
            binding.tvDebit.text="0.00 UZS"
            binding.tvCalculate.text="0.00 USD"
            binding.btnContinue.isEnabled(false)
        }
    }

    private fun checkErrorWatcher(amount: String) {
        var amountUzs:BigDecimal=BigDecimal.ZERO
        var amountUsd:BigDecimal=BigDecimal.ZERO
        val calculateAmount= if (senderCard?.currency_code==CURRENCY_CODE_USD) buyingRate else sellingRate
        if (senderCard != null && receiverCard != null){
            val amount = binding.etAmount.editableText.toString().replace(" ","")
            if (senderCard!!.isNotActive() || receiverCard!!.isNotActive() || amount.isEmpty()){
                binding.amountLayout.error = getString(R.string.empty_amount_and_no_activ)
            }else{
                if (currencyCode == CURRENCY_CODE_USD){
                    amountUzs=calculateRounded(amount.toBigDecimal(),calculateAmount.toBigDecimal())
                    amountUsd = calculateRoundedDivision(amountUzs,calculateAmount.toBigDecimal())
                    if (senderCard!!.currency_code==CURRENCY_CODE_USD){
                        binding.amountLayout.error=  when{
                            amountUsd>senderCard!!.balance.toBigDecimal().divide(BigDecimal(100))->getString(R.string.not_enough_money)
                            amountUsd < BigDecimal.ONE->"${getString(R.string.min_amount_conversion)} 1 USD"
                            else ->null
                        }
                    }else{
                        binding.amountLayout.error=  when{
                            amountUzs>senderCard!!.balance.toBigDecimal().divide(BigDecimal(100))->getString(R.string.not_enough_money)
                            amountUzs < calculateAmount.toBigDecimal()->"${getString(R.string.min_amount_conversion)}  1 USD"
                            else ->null
                        }
                    }


                }else{
                    amountUsd=calculateRoundedDivision(amount.toBigDecimal(),calculateAmount.toBigDecimal())
                    amountUzs=calculateRounded(amountUsd,calculateAmount.toBigDecimal())
                    if (senderCard!!.currency_code==CURRENCY_CODE_USD){
                        binding.amountLayout.error=  when{
                            amountUsd>senderCard!!.balance.toBigDecimal().divide(BigDecimal(100))->getString(R.string.not_enough_money)
                            amountUsd < BigDecimal.ONE->"${getString(R.string.min_amount_conversion)}   ${calculateAmount} UZS"
                            else ->null
                        }
                    }else{
                        binding.amountLayout.error=  when{
                            amountUzs>senderCard!!.balance.toBigDecimal().divide(BigDecimal(100))->getString(R.string.not_enough_money)
                            amountUzs < calculateAmount.toBigDecimal()->"${getString(R.string.min_amount_conversion)}  ${calculateAmount} UZS"
                            else ->null
                        }
                    }

                }
            }
        }else{
            binding.amountLayout.error="Karta aniqlanmadi"
        }
    }

    private fun detailsCalculate(amount:String) {
        val calculateAmount= if (senderCard?.currency_code==CURRENCY_CODE_USD) buyingRate else sellingRate
        binding.tvCurrency.text = "1$ = ${Format.formatAmount(calculateAmount.toString())} UZS"
        if (currencyCode==CURRENCY_CODE_USD){
            val amountUzs=calculateRounded(amount.toBigDecimal(),calculateAmount.toBigDecimal())
            changeCurrencyText(calculateRoundedDivision(amountUzs,calculateAmount.toBigDecimal()).toString(),amountUzs.toString())
           // binding.tvDebit.text= Format.formatAmount(amountUzs.toString())+" UZS"
          //  binding.tvCalculate.text=Format.formatAmount(calculateRoundedDivision(amountUzs,calculateAmount.toBigDecimal()).toString())+" USD"

        }else{
            val amountUsd=calculateRoundedDivision(amount.toBigDecimal(),calculateAmount.toBigDecimal())
            changeCurrencyText(amountUsd.toString(),calculateRounded(amountUsd,calculateAmount.toBigDecimal()).toString())
         //   binding.tvCalculate.text=Format.formatAmount(amountUsd.toString())+" USD"
         //   binding.tvDebit.text= Format.formatAmount(calculateRounded(amountUsd,calculateAmount.toBigDecimal()).toString())+" UZS"
        }
    }

    private fun changeCurrencyText(usdAmount:String,uzsAmount:String){
        if (senderCard!=null){
            if (senderCard!!.currency_code==CURRENCY_CODE_USD){
                binding.tvCalculate.text= Format.formatAmount(uzsAmount)+" UZS"
                binding.tvDebit.text=Format.formatAmount(usdAmount)+" USD"
            }else{
                binding.tvDebit.text= Format.formatAmount(uzsAmount)+" UZS"
                binding.tvCalculate.text=Format.formatAmount(usdAmount)+" USD"
            }
        }else{

        }
    }



    internal fun checkForContinueButton(): Boolean {
        var amountUzs:BigDecimal=BigDecimal.ZERO
        var amountUsd:BigDecimal=BigDecimal.ZERO
        val calculateAmount= if (senderCard?.currency_code==CURRENCY_CODE_USD) buyingRate else sellingRate
        return if (senderCard != null && receiverCard != null) {
            val amount = binding.etAmount.editableText.toString().replace(" ","")
            if (senderCard!!.isNotActive() || receiverCard!!.isNotActive() || amount.isEmpty()) return false
             if (currencyCode == CURRENCY_CODE_USD){
                 amountUzs=calculateRounded(amount.toBigDecimal(),calculateAmount.toBigDecimal())
                 amountUsd = calculateRoundedDivision(amountUzs,calculateAmount.toBigDecimal())
                 if (senderCard!!.currency_code==CURRENCY_CODE_USD){
                     amountUsd<=senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) && amountUsd >= BigDecimal.ONE
                 }else{
                     amountUzs<=senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) && amountUzs >= calculateAmount.toBigDecimal()
                 }


            }else{
                   amountUsd=calculateRoundedDivision(amount.toBigDecimal(),calculateAmount.toBigDecimal())
                   amountUzs=calculateRounded(amountUsd,calculateAmount.toBigDecimal())
                 if (senderCard!!.currency_code==CURRENCY_CODE_USD){
                     amountUsd<=senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) && amountUsd >= BigDecimal.ONE
                 }else{
                     amountUzs<=senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) && amountUzs >= calculateAmount.toBigDecimal()
                 }

            }

        } else false
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { it ->
            sumCardList= arrayListOf();
            currencyCardList= arrayListOf()
            val cardList = it.filter { it.isUniversalCardAndVisa() && it.object_type!= WALLET }
            allCardList=cardList.toCollection(ArrayList());
            cardList.forEach {
                if (it.object_type == UZCARD || it.object_type == HUMO_CARD || (it.object_type == WALLET && it.currency_code == CURRENCY_CODE_UZS)) {
                    sumCardList.add(it)
                } else if (it.currency_code == CURRENCY_CODE_USD) {
                    currencyCardList.add(it)
                }
            }
            if (senderCard==null || receiverCard==null)
             choiceCard()
            else{
                setSenderCardDetails(senderCard)
                setReceiverCardDetails(receiverCard)
                sumToCurrency=true
            }
            setTotalAmount(binding.etAmount.text.toString())
        }
    }

    private fun choiceCard() {
        if (sumCardList.isNotEmpty()){
            senderCard = sumCardList[0]
            setSenderCardDetails(senderCard)
        }
        if (currencyCardList.isNotEmpty()){
            receiverCard=currencyCardList[0]
            setReceiverCardDetails(receiverCard)
        }

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
            }
        }
        if (cardResponse==null){
            binding.tvChooseCurrencyCard.visibility = View.VISIBLE
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
            }
        }
        if (cardResponse==null){
            binding.tvChooseSumCard.visibility = View.VISIBLE
        }
    }


    private fun initCurrencyRate() {
        utilsViewModel.currencyRates.observe(viewLifecycleOwner) {
            val rates = it ?: ArrayList()
            rates.forEach { courseItem ->
                if (courseItem.currencyCode == CURRENCY_CODE_USD) {
                         sellingRate = courseItem.sellingRate
                         buyingRate = courseItem.buyingRate
                }
                if (senderCard!=null && senderCard!!.currency_code==CURRENCY_CODE_USD){
                    binding.tvCurrency.text = "1$ = ${Format.formatAmount(buyingRate.toString())} UZS"
                }else
                binding.tvCurrency.text = "1$ = ${Format.formatAmount(sellingRate.toString())} UZS"

            }

        }
    }


    private fun initSetOnClickListeners() {
        binding.sumCardBlock.setOnClickListener {
                setSumCard()
        }
        binding.currencyCardBlock.setOnClickListener {
                setCurrencyCard()
        }
        binding.conversionChanger.setOnClickListener {

            if (senderCard != null && receiverCard != null) {
                vibrateTick(requireContext())
                currencyChangeButtonClickEvent()
            }
        }
        binding.btnContinue.setOnClickListener { gotoSecondStep() }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }


    private fun gotoSecondStep() {
        val amountUsd=getUsdAmount()
        val amountUzs= getUzsAmount()
        val request = ConversionRequest(
            command = getCommand(senderCard!!, receiverCard!!),
            from_object_id = senderCard!!.object_id,
            to_object_value = receiverCard!!.object_value,
            to_object_id = receiverCard!!.object_id,
            to_object_expire = senderCard!!.object_expiry,
            amount = amountUsd,
            from_object_value= senderCard!!.object_value,
            amount_equivalent = amountUzs,
            currency_code = senderCard!!.currency_code,
            selling_rate = sellingRate.toString(),
            buying_rate = buyingRate.toString(),
            service_id = ServiceId.SERVICE_ID__9
        )
        val bundle = Bundle()
        bundle.putString(
            ConfirmConversionFragment.TOTAL_AMOUNT, if (currencyCode==CURRENCY_CODE_USD) amountUsd else amountUzs
        )
        bundle.putSerializable(ConfirmConversionFragment.CONVERSION_REQUEST, request)
        bundle.putString(ConfirmConversionFragment.CURRENCY_CODE, currencyCode)
        bundle.putString(
            ConfirmConversionFragment.CURRENT_RATE, binding.tvCurrency.text.toString()
        )
        bundle.putSerializable(
            ConfirmConversionFragment.SENDER_CARD, senderCard
        )
        bundle.putSerializable(
            ConfirmConversionFragment.RECEIVER_CARD, receiverCard
        )

        gotoWithSlide(R.id.confirmConversionFragment, bundle)
    }

    private fun getUzsAmount(): String {
        return if (senderCard!!.currency_code==CURRENCY_CODE_USD){
            binding.tvCalculate.text.toString().replace(" ","").replace("UZS","").toBigDecimal().multiply(BigDecimal(100)).setScale(0,RoundingMode.DOWN).toString()
        }else{
            binding.tvDebit.text.toString().replace(" ","").replace("UZS","").toBigDecimal().multiply(BigDecimal(100)).setScale(0,RoundingMode.DOWN).toString()
        }
    }

    private fun getUsdAmount(): String {
        return  if (senderCard!!.currency_code==CURRENCY_CODE_USD){
            binding.tvDebit.text.toString().replace(" ","").replace("USD","").toBigDecimal().multiply(BigDecimal(100)).setScale(0,RoundingMode.DOWN).toString()
        } else
            binding.tvCalculate.text.toString().replace(" ","").replace("USD","").toBigDecimal().multiply(BigDecimal(100)).setScale(0,RoundingMode.DOWN).toString()

    }

    private fun currencyChangeButtonClickEvent() {
        sumCardX = binding.sumCardBlock.x
        sumCardY = binding.sumCardBlock.y
        currencyCardX = binding.currencyCardBlock.x
        currencyCardY = binding.currencyCardBlock.y

        if (sumToCurrency) {
            animateBlocks()
            sumToCurrency = false


        } else {
            animateBlocks()
            sumToCurrency = true
         setTotalAmount(binding.etAmount.editableText.toString())
        }
        changeCard(sumToCurrency)

    }

    private fun changeCard(change: Boolean) {
        var oldSenderCard= senderCard
        var oldReceiverCard= receiverCard
        if (!change){
            senderCard=oldReceiverCard
            receiverCard=oldSenderCard
        }else{
            senderCard=oldReceiverCard
            receiverCard=oldSenderCard
        }
        setTotalAmount(binding.etAmount.text.toString())
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


    private fun setSumCard() {
        var list= if (sumToCurrency){
            allCardList
        } else{
            if (senderCard?.currency_code==CURRENCY_CODE_USD) sumCardList else currencyCardList
        }
        chooseCardDialog = ChooseCardDialog(list) {
            chooseCardDialog.dismiss()
            it?.let {
                if (sumToCurrency){
                if (it.currency_code==CURRENCY_CODE_USD || it.object_id == receiverCard?.object_id || it.currency_code==receiverCard?.currency_code){
                    senderCard = it
                    receiverCard=null
                    setSenderCardDetails(senderCard)
                    setReceiverCardDetails(receiverCard)
                } else {
                    senderCard = it
                    setSenderCardDetails(senderCard)
                }
                }else{
                    receiverCard = it
                    setSenderCardDetails(receiverCard)
                }
                binding.btnContinue.isEnabled(checkForContinueButton())
            }
        }
        chooseCardDialog.show(childFragmentManager, "")
    }

    private fun setCurrencyCard() {
        var list= if (sumToCurrency){
         if (senderCard?.currency_code==CURRENCY_CODE_USD) sumCardList else currencyCardList
        } else{
          allCardList
        }
        chooseCardDialog = ChooseCardDialog(list) {
            chooseCardDialog.dismiss()
                it?.let {
                    if (sumToCurrency){
                        receiverCard = it
                        setReceiverCardDetails(receiverCard)
                    }else{
                        if (it.currency_code==CURRENCY_CODE_USD || it.object_id == receiverCard?.object_id || it.currency_code==receiverCard?.currency_code){
                            senderCard=it
                            receiverCard=null
                            setSenderCardDetails(receiverCard)
                            setReceiverCardDetails(senderCard)
                        }else{
                        senderCard = it
                        setReceiverCardDetails(senderCard)
                        }
                    }
                    binding.btnContinue.isEnabled(checkForContinueButton())
                }


        }
        chooseCardDialog.show(childFragmentManager, "")
    }


   // calculate

    private fun calculateRoundedDivision(amount: BigDecimal, calculateAmount: BigDecimal): BigDecimal {
        val result = amount.divide(calculateAmount, 10, RoundingMode.DOWN)
        val thirdDigit = result.multiply(BigDecimal(1000))
            .toBigInteger() % BigDecimal.TEN.toBigInteger()

        return if (thirdDigit.toInt() > 0) {
            result.setScale(2, RoundingMode.DOWN)
        } else {
            result.setScale(2, RoundingMode.DOWN)
        }
    }

    private fun calculateRounded(amount: BigDecimal, calculateAmount: BigDecimal): BigDecimal {
        val result = amount.multiply(calculateAmount)
        return result.setScale(2, RoundingMode.DOWN)
    }
}