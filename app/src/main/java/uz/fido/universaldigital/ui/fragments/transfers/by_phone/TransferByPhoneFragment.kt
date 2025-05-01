package uz.fido.universaldigital.ui.fragments.transfers.by_phone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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
import uz.fido.universaldigital.databinding.FragmentTransferByPhoneBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferViewModel
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.P2PHistoryAdapter
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.TransferHistoriesFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.setMinMaxAmount
import uz.fido.universaldigital.ui.fragments.transfers.utils.showTransferSkeleton
import uz.fido.universaldigital.ui.utils.extensions.cardMiniLogoByType
import uz.fido.universaldigital.ui.utils.extensions.getFormattedContact
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
class TransferByPhoneFragment : BaseFragment<FragmentTransferByPhoneBinding, TransferViewModel>(
    FragmentTransferByPhoneBinding::inflate, TransferViewModel::class.java
), PermissionInterface {

    private lateinit var popularTransfersAdapter: P2PHistoryAdapter

    private val cardsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var skeletonScreen: SkeletonScreen? = null
    private var cardInfoDto: CardInfoDto? = null
    private var senderCard: CardResponse? = null
    private var p2PInfoDto: P2PInfoDto? = null
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getHistoriesByPhone()
        popularTransfersAdapter =
            P2PHistoryAdapter(isByPhone = true, onItemClickListener = ::popularTransferClickEvent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSavedReceivers()
        initSetOnClickListeners()
        initTextWatchers()
        initSuggestions()
        getBundle()
        setPhonePrefix()
        initCardList {
            initSenderCards()
        }

        observe(viewModel.historiesByPhoneNumber, ::popularTransferLoaded)
        observe(viewModel.popularTransfersLoader, ::showLoader)
        observe(viewModel.cardInfo, ::cardInfoLoaded)
        observe(viewModel.p2pInfo, ::p2pInfoLoaded)


        setFragmentResultListener(TransferHistoriesFragment.REQUEST_KEY) { _, bundle ->
            Log.d("TAG", "onViewCreated:run_---- ")
            val phoneNumber = bundle.getString(TransferHistoriesFragment.REQUEST_KEY)
            binding.etPhoneNumber.setText(phoneNumber)
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
                viewModel.getTransferInfo(senderCard, cardInfoDto, "Y")
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
                viewModel.getTransferInfo(senderCard, cardInfoDto, "Y")
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
                binding.etPhoneNumber.setText(cardNumber)
                viewModel.getCardInfo(cardNumber)
            }
            if (it.getString("amount") != null) {
                binding.etAmount.setText(it.getString("amount").toString())
            }
            if (it.getString("contact") != null) {
                if (checkForContactsPermission(this)) {
                    fetchPhoneNumber()
                }
            }
        }
    }

    private fun setPhonePrefix() {
        binding.etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etPhoneNumber.text.toString()
                    .isEmpty()
            ) binding.etPhoneNumber.setText(getString(R.string.phone_number_prefix))
        }
        binding.etPhoneNumber.setOnKeyListener { _, _, event ->
            event.keyCode == KeyEvent.KEYCODE_DEL && binding.etPhoneNumber.text.toString().length == 4
        }
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun initTextWatchers() {
        binding.etPhoneNumber.doAfterTextChanged { editable ->
            editable?.let {
                if (it.toString().length == 17) {
                    val phoneNumber = it.toString().replace(" ", "").replace("+", "")
                    binding.btnContact.visibility = View.GONE
                    binding.progressView.visibility = View.VISIBLE
                    this.phoneNumber = phoneNumber
                    viewModel.getCardInfoByPhone(phoneNumber)
                } else {
                    phoneNumber = ""
                    binding.ownerName.visibility = View.GONE
                    cardInfoDto = null
                }
            }
        }
        binding.etAmount.doAfterTextChanged {
            if (p2PInfoDto?.isSuccess == true) {
                binding.btnContinue.isEnabled(
                    binding.tvMinAmount.setMinMaxAmount(
                        senderCard,
                        cardInfoDto?.card_number,
                        binding.etAmount,
                        p2PInfoDto,
                        requireContext()
                    )
                )
            } else binding.btnContinue.isEnabled(false)
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
        if (cardInfo.card_number.isNullOrEmpty()) {
            setCardNumberError()
            cardInfoDto = null
        } else {
            binding.apply {
                btnContact.visibility = View.VISIBLE
                progressView.visibility = View.GONE
                ownerName.visibility = View.VISIBLE
                ownerName.text =
                    cardInfo.card_owner + " " + Format.formatCardNumberNew(cardInfo.card_number!!)
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
                viewModel.getTransferInfo(senderCard, cardInfoDto, "Y")
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
        if (!p2PInfo.isSuccess && !p2PInfo.errorMessage.isNullOrEmpty()) {
            binding.tvMinAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
            binding.tvMinAmount.text = p2PInfo.errorMessage
            binding.btnContinue.isEnabled(false)
        } else {
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

    private fun setCardNumberError() {
        binding.apply {
            ownerName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.brandRedColor
                )
            )
            progressView.visibility = View.GONE
            btnContact.visibility = View.VISIBLE
            ownerName.visibility = View.VISIBLE
            ownerName.text = getString(R.string.card_not_found)
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
        if (binding.etPhoneNumber.text.toString().replace(" ", "") != item.phone_number) {
            binding.etPhoneNumber.setText(item.phone_number)
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.btnContact.setOnClickListener {
            if (checkForContactsPermission(this)) {
                fetchPhoneNumber()
            }
        }
        binding.appBar.setOnAdditionalBtnClickListener {
            goto(
                R.id.fragmentTransferHistories,
                bundleOf(Const.OPERATION to TransferHistoriesFragment.TransferOperation.BY_PHONE)
            )
        }
        binding.emptyCards.setOnClickListener {
            goto(
                R.id.addCardFragment,
                bundleOf(Const.ADD_CARD_OPERATION to AddCardFragment.OPERATION_BY_PHONE)
            )
        }
        binding.icAddButton.setOnClickListener { goto(R.id.favoriteTransfersFragment) }
    }

    private fun continueButtonClickEvent() {
        val amount = binding.etAmount.editableText.toString()
        if (p2PInfoDto?.isSuccess == true) {
            if (amount.isNotEmpty()) {
                gotoWithSlide(
                    R.id.confirmTransferFragment, bundleOf(
                        SuccessTransferFragment.TRANSFER_DTO to TransferDto(
                            senderCard = senderCard,
                            receiverCard = cardInfoDto,
                            transferAmount = Format.sendFormat(amount),
                            commission = p2PInfoDto?.percent?.toDouble() ?: 0.0,
                            operation = SuccessTransferFragment.TRANSFER_BY_PHONE,
                            phoneNumber = phoneNumber
                        )
                    )
                )
            }
        } else showSnackbar(p2PInfoDto?.errorMessage ?: "")
    }

    override fun contactsPermissionGranted() {
        fetchPhoneNumber()
    }

    private fun fetchPhoneNumber() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            ContactsContract.Contacts.CONTENT_URI,
            ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        )
        activityForContacts.launch(intent)
    }

    private val activityForContacts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { intent ->
                    val contactUri = intent.data as Uri
                    val contactQuery = requireActivity().contentResolver.query(
                        contactUri, null, null, null, null
                    ) as Cursor
                    pickPhoneNumberFromContact(contactQuery)
                }
            }
        }

    private fun pickPhoneNumberFromContact(contactQuery: Cursor?) {
        try {
            val phoneNumber: String
            if (contactQuery != null && contactQuery.moveToFirst()) {
                val numberIndex: Int =
                    contactQuery.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                phoneNumber = contactQuery.getString(numberIndex)
                if (getFormattedContact(phoneNumber).isNotEmpty()) {
                    binding.etPhoneNumber.setText(getFormattedContact(phoneNumber))
                } else {
                    wrongPhoneNumberFormat()
                }
            } else {
                wrongPhoneNumberFormat()
            }
        } catch (exception: Exception) {
            contactQuery?.close()
            wrongPhoneNumberFormat()
        } finally {
            contactQuery?.close()
        }
    }

    private fun wrongPhoneNumberFormat() {
        Toast.makeText(
            requireContext(), getString(uz.fido.utils.R.string.wrong_format), Toast.LENGTH_SHORT
        ).show()
    }

}