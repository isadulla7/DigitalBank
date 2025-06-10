package uz.fido.universaldigital.ui.fragments.transfers.by_wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferByWalletBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferViewModel
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.P2PHistoryAdapter
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.TransferHistoriesFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.capitalizeWord
import uz.fido.universaldigital.ui.fragments.transfers.utils.setMinMaxAmount
import uz.fido.universaldigital.ui.fragments.transfers.utils.showTransferSkeleton
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.activity.observe
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.view.amount.AmountSuggestionView

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class TransferByWalletFragment : BaseFragment<FragmentTransferByWalletBinding, TransferViewModel>(
    FragmentTransferByWalletBinding::inflate, TransferViewModel::class.java
), PermissionInterface {

    private lateinit var popularTransfersAdapter: P2PHistoryAdapter

    private val cardsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var skeletonScreen: SkeletonScreen? = null
    private var cardInfoDto: CardInfoDto? = null
    private var senderCard: CardResponse? = null
    private var p2PInfoDto: P2PInfoDto? = null
    private var senderWallet: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getHistoriesByWallet()
        popularTransfersAdapter =
            P2PHistoryAdapter(onItemClickListener = ::popularTransferClickEvent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSavedReceivers()
        initSetOnClickListeners()
        initTextWatchers()
        initSuggestions()
        getBundle()
        initCardList {
            initSenderCards()
        }
        setInputType()

        observe(viewModel.historiesByWalletNumber, ::popularTransferLoaded)
        observe(viewModel.popularTransfersLoader, ::showLoader)
        observe(viewModel.cardInfo, ::cardInfoLoaded)
        observe(viewModel.p2pInfo, ::p2pInfoLoaded)


        setFragmentResultListener(TransferHistoriesFragment.REQUEST_KEY) { _, bundle ->
            val walletNumber = bundle.getString(TransferHistoriesFragment.DATA)
            binding.etWalletNumber.setText(walletNumber)
        }
    }

    private fun initCardList(listener: () -> Unit) {
        cardsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            val filterList= cardList.filter { it.is_Dv!="Y" }
            userSumCards.clear()
            filterList.forEach {
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

    private fun getBundle() {
        arguments?.let {
            val cardNumber = it.getString(Const.CARD_NUMBER).toString()
            if (cardNumber.isNotEmpty()) {
                binding.etWalletNumber.setText(cardNumber)
                viewModel.getCardInfo(cardNumber)
            }
            if (it.getString("amount") != null) {
                binding.etAmount.setText(it.getString("amount").toString())
            }
        }
    }

    private fun setWalletNumberMask(number: String) {
        when (number.length) {
            2, 11, 12 -> {
                if (number.startsWith("DV")) binding.etWalletNumber.setMask("## #########")
                if (number.startsWith("AUZ")) binding.etWalletNumber.setMask("### ########")
            }
        }
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun setInputType() {
        binding.etWalletNumber.filters += InputFilter.AllCaps()
    }

    private fun initTextWatchers() {
        binding.etWalletNumber.doOnTextChanged { text, start, before, count ->
            val inputText = text.toString()
            setWalletNumberMask(inputText)
        }
        binding.etWalletNumber.doAfterTextChanged { editable ->
            editable?.let { s ->
                if (s.length == 11 || s.length == 12) {
                    if (s.startsWith("DV") && s.length == 11) {
                        viewModel.getWalletInfo(s.toString().replace(" ", ""))
                        senderWallet = s.toString().replace(" ", "")
                    } else if (s.length == 12) {
                        viewModel.getWalletInfo(s.toString().replace(" ", ""))
                        senderWallet = s.toString().replace(" ", "")
                    }
                } else {
                    binding.btnContinue.isEnabled = false
                    binding.ownerName.visibility = View.GONE
                }
            }
        }
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

    private fun showLoader(isVisible: Boolean) {
        if (isVisible) {
            skeletonScreen = showTransferSkeleton(popularTransfersAdapter, binding.savedReceivers)
        } else {
            skeletonScreen?.hide()
        }
    }

    private fun cardInfoLoaded(cardInfo: CardInfoDto) {
        if (cardInfo.card_owner.isNullOrEmpty()) {
            setCardNumberError()
            cardInfoDto = null
        } else {
            binding.apply {
                progressView.visibility = View.GONE
                ownerName.visibility = View.VISIBLE
                val name =
                    try {
                        cardInfo.card_owner!!.split(" ")[0].capitalizeWord() + " " + cardInfo.card_owner!!.split(
                            " "
                        )[1].capitalizeWord()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cardInfo.card_owner
                    }
                ownerName.text = name.toString()
                ownerName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.brandBlueColor
                    )
                )
                cardInfoDto = cardInfo
                cardInfoDto!!.card_number = senderWallet
                viewModel.getTransferInfo(senderCard, cardInfoDto)
                btnContinue.isEnabled(etAmount.text.toString() == "" || etAmount.text.toString() == "0")
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
            ) && (binding.etAmount.text.toString() != "" && binding.etAmount.text.toString() != "0")
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
            ownerName.text = getString(R.string.wallet_not_found)
            ownerName.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
        }
    }

    private fun popularTransferLoaded(list: ArrayList<CardByPhone>) {
        popularTransfersAdapter.submitList(list)
        binding.emptyView.isVisible = list.isEmpty()
    }

    private fun initSavedReceivers() {
        binding.savedReceivers.layoutManager = LinearLayoutManager(requireContext())
        binding.savedReceivers.adapter = popularTransfersAdapter
    }

    private fun popularTransferClickEvent(item: CardByPhone) {
        if (binding.etWalletNumber.text.toString().replace(" ", "") != item.card_number) {
            if (item.card_number.startsWith("DV")) {
                binding.etWalletNumber.setMask("## #########")
            } else {
                binding.etWalletNumber.setMask("### ########")
            }
            binding.etWalletNumber.setText(item.card_number)
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.appBar.setOnAdditionalBtnClickListener {
            goto(
                R.id.fragmentTransferHistories,
                bundleOf(Const.OPERATION to TransferHistoriesFragment.TransferOperation.BY_WALLET)
            )
        }
        binding.emptyCards.setOnClickListener {
            goto(
                R.id.addCardFragment,
                bundleOf(Const.ADD_CARD_OPERATION to AddCardFragment.OPERATION_BY_WALLET)
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
                    operation = SuccessTransferFragment.TRANSFER_BY_WALLET
                )
            )
        )
    }

}