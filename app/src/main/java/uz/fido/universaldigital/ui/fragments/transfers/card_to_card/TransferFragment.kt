package uz.fido.universaldigital.ui.fragments.transfers.card_to_card

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.nfccardreaderlib.ScanNfcCardActivity
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferToCardBinding
import uz.fido.universaldigital.ui.dialogs.ChooseScanCardOptionDialog
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.PopularTransfersAdapter
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.PopularTransfersFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.checkCardAvailability
import uz.fido.universaldigital.ui.fragments.transfers.utils.checkCardNumber
import uz.fido.universaldigital.ui.fragments.transfers.utils.formatErrorMessage
import uz.fido.universaldigital.ui.fragments.transfers.utils.setMinMaxAmount
import uz.fido.universaldigital.ui.fragments.transfers.utils.showTransferSkeleton
import uz.fido.universaldigital.ui.utils.extensions.cardMiniLogoByType
import uz.fido.universaldigital.ui.utils.extensions.recordException
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
import uz.scan_card.cardscan.ScanActivity

@AndroidEntryPoint
class TransferFragment : BaseFragment<FragmentTransferToCardBinding, TransferViewModel>(
    FragmentTransferToCardBinding::inflate, TransferViewModel::class.java
), PermissionInterface {

    private lateinit var popularTransfersAdapter: PopularTransfersAdapter
    private lateinit var clipboardManager: ClipboardManager

    private val cardsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var skeletonScreen: SkeletonScreen? = null
    private var cardInfoDto: CardInfoDto? = null
    private var p2PInfoDto: P2PInfoDto? = null
    private var senderCard: CardResponse? = null
    private var deepLinkObjId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popularTransfersAdapter = PopularTransfersAdapter(false, ::popularTransferClickEvent)
        clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
//        tryToGetClipboardData()
        viewModel.getFavoriteTransfers()
        observe(viewModel.popularTransfers, ::popularTransferLoaded)
        observe(viewModel.popularTransfersLoader, ::showLoader)
        observe(viewModel.cardInfo, ::cardInfoLoaded)
        observe(viewModel.p2pInfo, ::p2pInfoLoaded)
        if (arguments != null) {
            if (requireArguments().getString(PopularTransfersFragment.DATA) != null) {
                binding.etCardNumber.setText(requireArguments().getString(PopularTransfersFragment.DATA))
            }
            getDateFromDeepLink(arguments)
        }
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

    private fun getBundle() {
        arguments?.let {
            val cardNumber = it.getString(Const.CARD_NUMBER).toString()
            if (cardNumber.isNotEmpty()) {
                binding.etCardNumber.setText(cardNumber)
                viewModel.getCardInfo(cardNumber)
            }
            if (it.getString("amount") != null) {
                binding.etAmount.setText(it.getString("amount").toString())
            }
        }
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun initTextWatchers() {
        binding.etCardNumber.doAfterTextChanged { editable ->
            editable?.let {
                if (it.toString().length == 19) {
                    if (checkCardAvailability(it.toString().replace(" ", ""))) {
                        binding.progressView.visibility = View.VISIBLE
                        binding.btnScan.visibility = View.GONE
                        viewModel.getCardInfo(it.toString().replace(" ", ""))
                    } else {
                        setCardNumberError()
                    }
                } else {
                    binding.ownerName.visibility = View.GONE
                    cardInfoDto = null
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
        if (isVisible && binding.savedReceivers.size == 0) {
            skeletonScreen = showTransferSkeleton(popularTransfersAdapter, binding.savedReceivers)
        } else {
            skeletonScreen?.hide()
        }
    }

    private fun cardInfoLoaded(cardInfo: CardInfoDto) {
        if (cardInfo.card_number.isNullOrEmpty()) {
            setCardNumberError(cardInfo.message)
            cardInfoDto = null
        } else {
            binding.apply {
                btnScan.visibility = View.VISIBLE
                progressView.visibility = View.GONE
                ownerName.visibility = View.VISIBLE
                ownerName.text = cardInfo.card_owner
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
        if (p2PInfoDto?.isSuccess == true) {
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
        } else {
            binding.tvMinAmount.visibility = View.VISIBLE
            binding.tvMinAmount.text = p2PInfoDto?.errorMessage.orEmpty()
            binding.btnContinue.isEnabled(false)
        }
    }

    private fun setCardNumberError(msg: String? = null) {
        binding.apply {
            ownerName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.brandRedColor
                )
            )
            progressView.visibility = View.GONE
            btnScan.visibility = View.VISIBLE
            ownerName.visibility = View.VISIBLE
            ownerName.text = requireContext().formatErrorMessage(msg)
            ownerName.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
        }
    }

    private fun popularTransferLoaded(list: ArrayList<PopularTransfers>) {
        popularTransfersAdapter.submitList(list)
        binding.emptyView.isVisible = list.isEmpty()
    }

    private fun initSavedReceivers() {
        binding.savedReceivers.layoutManager = LinearLayoutManager(requireContext())
        binding.savedReceivers.adapter = popularTransfersAdapter
    }

    private fun popularTransferClickEvent(item: PopularTransfers) {
        if (binding.etCardNumber.text.toString().replace(" ", "") != item.card_number) {
            binding.etCardNumber.setText(item.card_number)
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.btnScan.setOnClickListener {
            val dialog = ChooseScanCardOptionDialog(onCameraClickListener = {
                if (checkForCameraPermission(this@TransferFragment)) {
                    openCameraForCardRead()
                }
            }, onNFCClickListener = {
                val intent = Intent(requireActivity(), ScanNfcCardActivity::class.java)
                activityNfcLauncher.launch(intent)
            })
            dialog.show(childFragmentManager, "")
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
        binding.icAddButton.setOnClickListener { goto(R.id.favoriteTransfersFragment) }
    }

    private fun continueButtonClickEvent() {
        println(senderCard.toString())
        val amount = binding.etAmount.editableText.toString()
        if (p2PInfoDto?.isSuccess == true) {
            gotoWithSlide(
                R.id.confirmTransferFragment, bundleOf(
                    SuccessTransferFragment.TRANSFER_DTO to TransferDto(
                        senderCard = senderCard,
                        receiverCard = cardInfoDto,
                        transferAmount = Format.sendFormat(amount),
                        commission = p2PInfoDto?.percent?.toDouble() ?: 0.0,
                        operation = SuccessTransferFragment.TRANSFER_BY_CARD,
                        requestId = p2PInfoDto?.requestId,
                        cardId = p2PInfoDto?.cardId
                    )
                )
            )
        } else showSnackbar(p2PInfoDto?.errorMessage ?: "")
    }

    private fun openCameraForCardRead() {
        val intent = ScanActivity.buildIntent(
            requireActivity(), true, null, R.string.card_scan_position_card, null, null
        )
        intent.putExtra("requestCode", ScanActivity.SCAN_REQUEST_CODE)
        getActivityResult.launch(intent)
    }

    override fun cameraPermissionGranted() {
        openCameraForCardRead()
    }

    private val getActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val scanResult = ScanActivity.creditCardFromResult(it.data)
                val result = scanResult?.number
                if (result != null) {
                    binding.etCardNumber.setText(result)
                    if (!checkCardNumber(result)) {
                        binding.cardNumberLayout.error = getString(R.string.invalid_card_number)
                    }
                }
            }
        }

    private val activityNfcLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.etCardNumber.setText(result.data?.extras?.getString("card_number"))
            }
        }

    private fun tryToGetClipboardData() {
        if (this::clipboardManager.isInitialized) {
            try {
                val clipData = clipboardManager.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val text = clipData.getItemAt(0).text
                    if (checkCardAvailability(text.toString())) {
                        if (cardsViewModel.isCardPasted.value == false) {
                            binding.etCardNumber.setText(text)
                            cardsViewModel.isCardPasted.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                recordException(e, ::tryToGetClipboardData.name)
            }
        }
    }

    private fun getDateFromDeepLink(arguments: Bundle? = null) {
        if (arguments == null) return
        val cardNumber = arguments.getString(PassCodeFragment.DEEP_LINK_OBJECT_VALUE)
        val amount = arguments.getString(PassCodeFragment.DEEP_LINK_AMOUNT)
        deepLinkObjId = requireArguments().getString(PassCodeFragment.DEEP_LINK_OBJECT_ID)
        cardNumber?.let {
            binding.etCardNumber.setText(it)
            viewModel.getCardInfo(it.replace(" ", ""), deepLinkObjId)
        }
        amount?.let {
            binding.etAmount.setText(Format.formatAmountFromTiynToInteger(it))
        }
    }
}