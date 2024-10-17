package uz.fido.universaldigital.ui.fragments.transfers.over_my_cards

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PInfoResponse
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOverMyCardsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.transfers.confirm_transfer.ConfirmTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.getInfoCommand
import uz.fido.universaldigital.ui.fragments.transfers.utils.getServiceIdInfo
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isNotActive
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.format.Format
import uz.fido.utils.utility.format.Format.Companion.sendFormat
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.amount.AmountSuggestionView
import java.math.BigDecimal

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class OverMyCardsFragment : BaseFragment<FragmentOverMyCardsBinding, OverMyCardsViewModel>(
    FragmentOverMyCardsBinding::inflate, OverMyCardsViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var receiverCard: CardResponse? = null
    private var senderCard: CardResponse? = null
    private var p2PInfoDto: P2PInfoDto? = null
    private var maxAmount = BigDecimal(50000000.0)
    private var minAmount = BigDecimal(1000.0)
    private var fromConfirmPage = false
    private var receiverName = ""
    private var percent = BigDecimal(0.0)
    private var amount = "0"

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setFragmentResult()
        initCardList {
            initSenderCards()
            initReceiverCards()
            if (!fromConfirmPage) {
                try {
                    senderCard = userSumCards.first()
                    receiverCard = userSumCards.last()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            getTransferInfo()
            arguments?.let {
                senderCard = requireArguments().serializable(Const.SENDER_CARD) as CardResponse?
                receiverCard = requireArguments().serializable(Const.RECEIVER_CARD) as CardResponse?
                binding.senderCards.setCurrentItem(userSumCards.indexOf(senderCard), true)
                binding.receiverCards.setCurrentItem(userSumCards.indexOf(receiverCard), true)
            }
        }
        initSetOnClickListeners()
        initSuggestions()
        initAmountTextWatcher()
    }

    private fun setFragmentResult() {
        setFragmentResultListener(ConfirmTransferFragment.NAVIGATION_BACK) { _, bundle ->
            fromConfirmPage = bundle.getBoolean("confirm")
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.sendersEmpty.setOnClickListener {
            goto(
                R.id.addCardFragment,
                bundleOf(Const.ADD_CARD_OPERATION to AddCardFragment.OPERATION_OVER_MY_CARDS)
            )
        }
        binding.receiversEmpty.setOnClickListener {
            goto(
                R.id.addCardFragment,
                bundleOf(Const.ADD_CARD_OPERATION to AddCardFragment.OPERATION_OVER_MY_CARDS)
            )
        }
    }

    private fun initCardList(listener: () -> Unit) {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            cardList.forEach {
                if (it.currency_code == CurrencyConst.CURRENCY_CODE_UZS) {
                    if (senderCard == null || receiverCard == null) {
                        userSumCards.add(it)
                    }
                }
            }
            listener.invoke()
        }
    }

    private fun initSenderCards() {
        if (userSumCards.isEmpty()) {
            binding.sendersEmpty.visibility = View.VISIBLE
        } else {
            val senderCardsAdapter = OverMyCardsAdapter(requireContext(), userSumCards)
            binding.senderCards.adapter = senderCardsAdapter
            binding.senderIndicator.setViewPager(binding.senderCards)
            binding.senderCards.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    vibrateTick(requireContext())
                    senderCard = userSumCards[position]
                    getTransferInfo()
                }
            })
        }
    }

    private fun initReceiverCards() {
        if (userSumCards.isEmpty() || userSumCards.size == 1) {
            binding.receiversEmpty.visibility = View.VISIBLE
        } else {
            val receiverCardsAdapter = OverMyCardsAdapter(requireContext(), userSumCards)
            binding.receiverCards.adapter = receiverCardsAdapter
            binding.receiverIndicator.setViewPager(binding.receiverCards)
            if (!fromConfirmPage) {
                binding.receiverCards.currentItem = userSumCards.size - 1
            }
            binding.receiverCards.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    vibrateTick(requireContext())
                    receiverCard = userSumCards[position]
                    getTransferInfo()
                }
            })
        }
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun getTransferInfo() {
        showCommissionProgress()
        Handler(Looper.getMainLooper()).postDelayed({
            if (senderCard == receiverCard) {
                binding.btnContinue.isEnabled(continueButtonState())
                return@postDelayed
            }
            if (senderCard != null && receiverCard != null && senderCard != receiverCard) {
                viewModel.p2pInfoRequest(
                    getClientToken(), P2PInfoRequest(
                        service_id = getServiceIdInfo(receiverCard!!, senderCard!!),
                        from_object_id = senderCard!!.object_id,
                        expire = senderCard!!.object_expiry,
                        to_object_value = receiverCard!!.object_value,
                        command = getInfoCommand(receiverCard!!),
                        to_object_id = receiverCard!!.object_id
                    )
                ).observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            val p2pInfoResponse = it.data as P2PInfoResponse
                            p2PInfoDto = p2pInfoResponse.mapToDto()
                            minAmount = p2pInfoResponse.min_amount.toBigDecimal().divide(100.toBigDecimal())
                            maxAmount = p2pInfoResponse.max_amount.toBigDecimal().divide(100.toBigDecimal()) ?: 15000000.0.toBigDecimal()
                            receiverName = p2pInfoResponse.empbossed_name.toString()
                            percent = p2pInfoResponse.percent.toBigDecimal()
                            setCommission(percent)
                            binding.btnContinue.isEnabled(continueButtonState())
                        }

                        Status.ERROR -> {
                            setErrorText(it?.message)
                            binding.btnContinue.isEnabled(false)
                        }
                    }
                }
            } else {
                hideCommissionBlock()
                binding.btnContinue.isEnabled(false)
            }
        }, 100)
    }

    private fun setCommission(commission: BigDecimal) {
        binding.commissionProgress.visibility = View.GONE
        binding.tvCommission.visibility = View.VISIBLE
        binding.tvCommission.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.brandBlueColor_50
            )
        )
        val etAmount = binding.etAmount.text.toString().replace(" ", "").ifEmpty { "0" }
        val formattedAmount = etAmount.toBigDecimal()
        binding.tvCommission.text = getString(R.string.commission) + " " +
                commission.toString() + "% (" + Format.formatAmount(
            Format.convertFromTiynDivide(
                (formattedAmount * commission).toString()
            )
        ) + " " + getString(R.string.sum_text) + ") "
    }

    private fun showCommissionProgress() {
        binding.tvCommission.visibility = View.GONE
        binding.commissionProgress.visibility = View.VISIBLE
    }

    private fun hideCommissionBlock() {
        binding.tvCommission.visibility = View.GONE
        binding.commissionProgress.visibility = View.GONE
    }

    private fun setErrorText(msg: String?) {
        binding.tvCommission.visibility = View.VISIBLE
        binding.tvCommission.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.brandRedColor
            )
        )
        binding.commissionProgress.visibility = View.GONE
        binding.tvCommission.text = msg ?: getString(uz.fido.utils.R.string.unkknown_error)
    }

    private fun initAmountTextWatcher() {
        binding.etAmount.doAfterTextChanged {
            setCommission(percent)
            binding.btnContinue.isEnabled(continueButtonState())
        }
    }

    private fun continueButtonClickEvent() {
        if (senderCard != null && receiverCard != null && senderCard != receiverCard) {
            amount = binding.etAmount.editableText.toString()
            gotoWithSlide(
                R.id.confirmTransferFragment, bundleOf(
                    SuccessTransferFragment.TRANSFER_DTO to TransferDto(
                        senderCard = senderCard,
                        receiverCard = CardInfoDto(
                            card_type = receiverCard!!.object_type,
                            card_number = receiverCard!!.object_value,
                            card_owner = receiverName,
                            card_expire = receiverCard!!.object_expiry,
                            card_id = receiverCard!!.object_id
                        ),
                        transferAmount = sendFormat(amount),
                        commission = p2PInfoDto?.percent?.toDouble() ?: 0.0,
                        operation = SuccessTransferFragment.TRANSFER_OVER_MY_CARDS,
                        cardId = p2PInfoDto?.cardId
                    )
                )
            )
        }
    }

    private fun continueButtonState(): Boolean {
        val etAmount = binding.etAmount.editableText.toString().replace(" ", "").ifEmpty { "0" }
        val formattedAmount = etAmount.toBigDecimal()
        val totalAmount = formattedAmount + (formattedAmount.divide(100.toBigDecimal())) * percent
        when {
            senderCard == null || receiverCard == null -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor_50))
                hideCommissionBlock()
                return false
            }

            senderCard == receiverCard -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
                binding.tvMinAmount.text = requireContext().getString(R.string.sender_and_receiver_the_same)
                hideCommissionBlock()
                return false
            }

            senderCard!!.isNotActive() -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
                binding.tvMinAmount.text = requireContext().getString(R.string.sender_card_is_not_active)
                hideCommissionBlock()
                return false
            }

            receiverCard!!.isNotActive() -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
                binding.tvMinAmount.text = requireContext().getString(R.string.receiver_card_is_not_active)
                hideCommissionBlock()
                return false
            }

            formattedAmount < minAmount -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor_50))
                binding.tvMinAmount.text =
                    getString(R.string.min_amount) + " ${Format.formatAmount((minAmount).toString())} ${getString(R.string.sum_text)}"
                return false
            }

            formattedAmount > maxAmount -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
                binding.tvMinAmount.text =
                    getString(R.string.max_amount) + " ${Format.formatAmount((maxAmount).toString())} ${getString(R.string.sum_text)}"
                return false
            }

            totalAmount > senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
                binding.tvMinAmount.text = getString(R.string.insufficient_amount)
                return false
            }

            totalAmount > minAmount && totalAmount < senderCard!!.balance.toBigDecimal().divide(BigDecimal(100)) -> {
                binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor_50))
                binding.tvMinAmount.text =
                    getString(R.string.min_amount) + " ${Format.formatAmount((minAmount).toString())} ${getString(R.string.sum_text)}"
                return true
            }

            else -> return true
        }
    }

}