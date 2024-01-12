package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentRequestMoneyPaymentBinding
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferViewModel
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.PopularTransfersFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.getUserNameFormatted
import uz.fido.universaldigital.ui.fragments.transfers.utils.setMinMaxAmount
import uz.fido.universaldigital.ui.utils.extensions.cardMiniLogoByType
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.utility.activity.observe
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.math.BigDecimal

@AndroidEntryPoint
class RequestMoneyPaymentFragment :
    BaseFragment<FragmentRequestMoneyPaymentBinding, TransferViewModel>(
        FragmentRequestMoneyPaymentBinding::inflate, TransferViewModel::class.java
    ), PermissionInterface {

    private val cardsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var cardInfoDto: CardInfoDto? = null
    private var p2PInfoDto: P2PInfoDto? = null
    private var senderCard: CardResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPopularTransfers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetOnClickListeners()
        initTextWatchers()
        initDetails()
        initCardList {
            initSenderCards()
        }

        observe(viewModel.cardInfo, ::cardInfoLoaded)
        observe(viewModel.p2pInfo, ::p2pInfoLoaded)
        setFragmentResultListener(PopularTransfersFragment.REQUEST_KEY) { _, bundle ->
            val cardNumber = bundle.getString(PopularTransfersFragment.DATA)
            binding.etCardNumber.setText(cardNumber)
        }
    }

    private fun initCardList(listener: () -> Unit) {
        cardsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            userSumCards.clear()
            cardList.forEach {
                if (it.currency_code == CurrencyConst.CURRENCY_CODE_UZS) {
                    userSumCards.add(it)
                }
            }
            binding.llCards.visibility =
                if (userSumCards.isEmpty()) View.INVISIBLE else View.VISIBLE
            binding.emptyCards.isVisible = userSumCards.isEmpty()
            if (userSumCards.isNotEmpty()) {
                senderCard = userSumCards[binding.senderCards.currentItem]
                viewModel.getTransferInfo(senderCard, cardInfoDto)
            }
            listener.invoke()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userSumCards.isNotEmpty()) {
            senderCard = userSumCards[binding.senderCards.currentItem]
        }
    }

    private fun initSenderCards() {
        val senderCardsAdapter = OverMyCardsAdapter(requireContext(), userSumCards)
        binding.senderCards.adapter = senderCardsAdapter
        binding.senderIndicator.setViewPager(binding.senderCards)
        binding.senderIndicator.isVisible = userSumCards.size != 1
        binding.senderCards.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                vibrateTick(requireContext())
                senderCard = userSumCards[position]
                viewModel.getTransferInfo(senderCard, cardInfoDto)
                if (senderCard?.object_value == cardInfoDto?.card_number) {
                    binding.tvMinAmount.visibility = View.VISIBLE
                    binding.tvMinAmount.text = getString(R.string.sender_and_receiver_the_same)
                    binding.btnContinue.isEnabled(false)
                }
            }
        })
    }

    private fun initDetails() {
        arguments?.let {
            val objectValue = it.getString(PassCodeFragment.DEEP_LINK_OBJECT_VALUE, "")
            val objectId = it.getString(PassCodeFragment.DEEP_LINK_OBJECT_ID, "")
            val amount = it.getString(PassCodeFragment.DEEP_LINK_AMOUNT, "")
            val comment = it.getString(PassCodeFragment.DEEP_LINK_COMMENT, "")
            if (!objectValue.isNullOrEmpty()) {
                binding.etCardNumber.setText(objectValue)
                viewModel.getCardInfo(cardNumber = objectValue, objectId = objectId)
            }
            if (!comment.isNullOrEmpty()) {
                binding.etComment.setText(comment)
            } else {
                binding.commentLayout.visibility = View.GONE
            }
            if (!amount.isNullOrEmpty()) {
                binding.etAmount.setText(amount.toBigDecimal().divide(BigDecimal(100)).toString())
            }
        }
    }

    private fun initTextWatchers() {
        binding.etAmount.doAfterTextChanged {
            binding.btnContinue.isEnabled(
                binding.tvMinAmount.setMinMaxAmount(
                    senderCard,
                    cardInfoDto?.card_number,
                    binding.etAmount,
                    p2PInfoDto,
                    requireContext()
                )
            )
        }
    }

    private fun cardInfoLoaded(cardInfo: CardInfoDto) {
        if (cardInfo.card_number.isNullOrEmpty()) {
            setCardNumberError()
            cardInfoDto = null
        } else {
            binding.apply {
                progressView.visibility = View.GONE
                ownerName.visibility = View.VISIBLE
                ownerName.text = getUserNameFormatted(cardInfo.card_owner)
                ownerName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.brandBlueColor
                    )
                )
                ownerName.setCompoundDrawablesWithIntrinsicBounds(
                    cardMiniLogoByType(cardInfo.card_type!!), 0, 0, 0
                )
                cardInfoDto = cardInfo
                viewModel.getTransferInfo(senderCard, cardInfoDto)
                if (senderCard?.object_value == cardInfoDto?.card_number) {
                    binding.tvMinAmount.visibility = View.VISIBLE
                    binding.tvMinAmount.text = getString(R.string.sender_and_receiver_the_same)
                    binding.btnContinue.isEnabled(false)
                }
            }
        }
    }

    private fun p2pInfoLoaded(p2PInfo: P2PInfoDto) {
        p2PInfoDto = p2PInfo
        binding.tvMinAmount.visibility = View.VISIBLE
        binding.btnContinue.isEnabled(
            binding.tvMinAmount.setMinMaxAmount(
                senderCard,
                cardInfoDto?.card_number,
                binding.etAmount,
                p2PInfoDto,
                requireContext()
            )
        )
    }

    private fun setCardNumberError() {
        binding.apply {
            ownerName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.brandRedColor
                )
            )
            progressView.visibility = View.GONE
            ownerName.visibility = View.VISIBLE
            ownerName.text = getString(R.string.card_not_found)
            ownerName.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.appBar.setOnAdditionalBtnClickListener {
            goto(R.id.fragmentPopularTransfers)
        }
        binding.emptyCards.setOnClickListener {
            goto(
                R.id.addCardFragment,
                bundleOf(Const.ADD_CARD_OPERATION to AddCardFragment.OPERATION_CARD_TO_CARD)
            )
        }
    }

    private fun continueButtonClickEvent() {
        val amount = binding.etAmount.editableText.toString()
        gotoWithSlide(
            R.id.confirmTransferFragment, bundleOf(
                SuccessTransferFragment.TRANSFER_DTO to TransferDto(
                    senderCard = senderCard,
                    receiverCard = cardInfoDto,
                    transferAmount = Format.sendFormat(amount),
                    commission = p2PInfoDto?.percent?.toDouble() ?: 0.0,
                    operation = SuccessTransferFragment.TRANSFER_BY_CARD,
                    requestId = p2PInfoDto?.requestId
                )
            )
        )
    }

}