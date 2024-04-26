package uz.fido.universaldigital.ui.fragments.products

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuHomeBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeCardsAdapter
import uz.fido.universaldigital.ui.fragments.products.widgets.balance.MainBalanceDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isValidSumCard
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isValidVisaCard
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNameAndNumber
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.doTransferOperationByType
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.extensions.setCardState
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.universaldigital.ui.utils.home_utils.getCardsWithBalanceVisibility
import uz.fido.universaldigital.ui.utils.home_utils.initBalanceWidget
import uz.fido.universaldigital.ui.utils.home_utils.initRefreshLayout
import uz.fido.universaldigital.ui.utils.home_utils.loadCardsFromPaper
import uz.fido.universaldigital.ui.utils.home_utils.loadProfileImage
import uz.fido.universaldigital.ui.utils.home_utils.setUpTickerView
import uz.fido.universaldigital.ui.utils.home_utils.setUserDetails
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.view.recycler_view_drag.EditItemTouchHelperCallback

@AndroidEntryPoint
@SuppressLint("UseCompatLoadingForDrawables")
class MenuHomeFragment : BaseHomeFragment(), BaseInterface {

    private lateinit var selectedCards: ArrayList<CardResponse>
    private lateinit var userCards: ArrayList<CardResponse>

    private var homeCardsAdapter: HomeCardsAdapter? = null
    private var moreButtonClicked = false
    private var totalBalance = 0.0
    private var currency = "UZS"
    private var balanceUpdateCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuHomeBinding.inflate(inflater, container, false)
        this.container = container
        homeCardsAdapter = HomeCardsAdapter(this)
        setUpTickerView()
        initTotalBalance()
        getCardList(null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDefaultStates()
        initWidgets()
        initSetOnClickListeners()
        initUserCardsRv()
        initUserCards()
        initRefreshLayout()
    }

    private fun initDefaultStates() {
        loadProfileImage()
        setUserDetails()
        getNotification()
    }

    private fun getNotification() {
        binding.notificationHide.setOnClickListener {
            binding.consNotification.visibility=View.GONE
        }
        menuProductsViewModel.getNotifications(getClientToken(), GetNotificationsRequest(
            page_number = "0", page_item_size = "20"
        )
        ).observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    val notificationList=it.data?.notifications?.filter { it.is_read=="N" }?: arrayListOf()
                    if (notificationList.isNotEmpty()){
                        binding.notificationItem.visibility=View.VISIBLE
                        binding.consNotification.visibility=View.VISIBLE
                        binding.notificationItem.text=notificationList.size.toString()
                        binding.notificationTitle.text=notificationList[0].title
                        binding.notificationText.text=notificationList[0].text
                    }else{
                        binding.notificationItem.visibility=View.GONE
                        binding.consNotification.visibility=View.GONE
                    }
                }
                Status.ERROR -> {}
            }
        }
    }

    private fun initTotalBalance() {
        userCards = loadCardsFromPaper()
        menuProductsViewModel.updateCards(userCards)
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { currentCards ->
            userCards = currentCards as ArrayList<CardResponse>
            binding.addCardLayout.isVisible = currentCards.isEmpty()
            binding.userCardsLayout.isVisible = currentCards.isNotEmpty()
            val savedCardsWithBalanceVisibility = getCardsWithBalanceVisibility()
            currentCards.forEach {
                it.balance_visibility = true
            }
            if (savedCardsWithBalanceVisibility.isNotEmpty()) {
                for (savedCards in savedCardsWithBalanceVisibility) {
                    for (currentCard in currentCards) {
                        if (currentCard.object_value == savedCards.object_value) {
                            savedCards.balance = currentCard.balance
                        }
                    }
                }
            }
            selectedCards = if (savedCardsWithBalanceVisibility.isNotEmpty() &&
                (currentCards.size == savedCardsWithBalanceVisibility.size)
            ) savedCardsWithBalanceVisibility else currentCards
            checkForBalanceOfCards()
            Paper.book().write(Const.PAPER_CLIENT_CARDS, currentCards)
            setTotalBalance()
            initBalanceVisibility()
            initUserCards()
        }
    }

    fun getCardList(refreshLayout: RefreshLayout?) {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val cards = it.data?.objects ?: ArrayList()
                    if (cards.isNotEmpty()) {
                        for (i in 0 until cards.size) {
                            getCardInfo(i, cards, refreshLayout)
                        }
                        userCards = cards
                    } else {
                        refreshLayout?.finishRefresh()
                        menuProductsViewModel.updateCards(ArrayList())
                    }
                }

                Status.ERROR -> {
                    val code=it.errorBody?.code?:0
                    if (code==1024){
                        val dialog=AlertDialog.Builder(requireContext())
                            .setTitle("Play Market")
                            .setMessage("Play marketda yangi versiya mavjud")
                            .setCancelable(false)
                            .setPositiveButton("Ok") { _, _ ->
                                requireActivity().openPlayMarket()
                            }
                        dialog.create()
                        dialog.show()
                    }
                  //  deviceCheck(requireActivity(),it.errorBody)
                    refreshLayout?.finishRefresh()
                }
            }
        }
    }

    private fun getCardInfo(
        position: Int, cardList: ArrayList<CardResponse>, refreshLayout: RefreshLayout?
    ) {
        val ids = arrayListOf(cardList[position].object_id)
        val cardInfoRequest = CardInfoRequest(ids)
        menuProductsViewModel.getCardInfoRequest(getClientToken(), cardInfoRequest)
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        balanceUpdateCounter++
                        val response = resource.data?.objects ?: ArrayList()
                        if (response.isNotEmpty()) {
                            cardList[position].apply {
                                balance = response[0].balance
                                processing_server_status = response[0].state
                                stateName = response[0].state_name
                                owerdraft_limit = response[0].overdraft_limit
                                pin_counter = response[0].pin_counter
                                overdraft_limit = response[0].overdraft_limit
                                object_status = response[0].object_status
                            }
                            userCards = cardList
                            if (balanceUpdateCounter == cardList.size)
                                menuProductsViewModel.updateCards(cardList)
                        }
                        refreshLayout?.finishRefresh()
                    }

                    Status.ERROR -> {
                        balanceUpdateCounter++
                        cardList[position].processing_server_status = "-100"
                        userCards = cardList
                        if (balanceUpdateCounter == cardList.size)
                            menuProductsViewModel.updateCards(userCards)
                        refreshLayout?.finishRefresh()
                    }
                }
            }
    }

    private fun initSetOnClickListeners() {
        binding.chat.setOnClickListener { goto(R.id.menuChatFragment) }
        binding.userName.setOnClickListener { goto(R.id.menuProfileFragment) }
        binding.tvAddCard.setOnClickListener { goto(R.id.addCardFragment) }
        binding.searchView.setOnClickListener {
            goto(R.id.searchEveryWhereFragment)
        }
        binding.tvAllCards.setOnClickListener { goto(R.id.myCardsListFragment) }
        binding.userAvatar.setOnClickListener { goto(R.id.menuProfileFragment) }
        binding.hideBalance.setOnClickListener { changeBalanceVisibility() }
        binding.btnShowMore.setOnClickListener {
            showMoreButtonClickEvent()
        }
        binding.notifications.setOnClickListener { goto(R.id.notificationsFragment) }
        binding.addCardLayout.setOnClickListener { goto(R.id.addCardFragment) }
        binding.balanceSettings.setOnClickListener {
            openBalanceSettingsDialog()
        }
        binding.itemMainCard.setOnClickListener { goto(R.id.myCardsListFragment) }
        binding.refreshButton.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
            binding.refreshLayout.autoRefresh()
        }
    }

    private fun openBalanceSettingsDialog() {
        val mainBalanceDialog = MainBalanceDialog()
        mainBalanceDialog.setListener(object : BaseInterface {
            override fun dialogDismiss() {
                checkForBalanceOfCards()
                Paper.book().write(Const.PAPER_CARDS_WITH_BALANCE_VIS, selectedCards)
                setTotalBalance()
            }
        }, selectedCards)
        if (selectedCards.isNotEmpty() && !mainBalanceDialog.isVisible && !mainBalanceDialog.isAdded) {
            mainBalanceDialog.show(childFragmentManager, "TAG")
        }
    }

    private fun initBalanceVisibility() {
        if (Paper.book().read<Boolean>(Const.BALANCE_VISIBILITY) != false) {
            binding.hideBalance.setImageResource(R.drawable.ic_eye)
            binding.balanceSettings.isClickable = true
            setTotalBalance()
        } else {
            binding.balance.visibility = View.GONE
            binding.balanceHidden.visibility = View.VISIBLE
            binding.hideBalance.setImageResource(R.drawable.ic_eye_close)
            binding.balanceSettings.isClickable = false
        }
    }

    private fun changeBalanceVisibility() {
        if (Paper.book().read<Boolean>(Const.BALANCE_VISIBILITY) != false) {
            binding.apply {
                hideBalance.setImageResource(R.drawable.ic_eye_close)
                balanceHidden.visibility = View.VISIBLE
                balance.visibility = View.GONE
                balance.isClickable = false
                balanceSettings.isClickable = false
            }
            Paper.book().write(Const.BALANCE_VISIBILITY, false)
        } else {
            setTotalBalance()
            binding.apply {
                hideBalance.setImageResource(R.drawable.ic_eye)
                balanceHidden.visibility = View.INVISIBLE
                balance.visibility = View.VISIBLE
                balance.isClickable = true
                balanceSettings.isClickable = true
            }
            Paper.book().write(Const.BALANCE_VISIBILITY, true)
        }
        homeCardsAdapter?.notifyDataSetChanged()
        initHomeMainCardInfo(userCards)
    }

    internal fun checkForBalanceOfCards() {
        if (Paper.book().read(Const.TOTAL_BALANCE_TYPE, 0) == 0) {
            totalBalance = 0.0
            currency = "UZS"
            for (card in selectedCards) {
                if (card.isValidSumCard()) {
                    totalBalance += card.balance.toDouble()
                }
            }
        } else {
            totalBalance = 0.0
            currency = "USD"
            for (card in selectedCards) {
                if (card.isValidVisaCard()) {
                    totalBalance += card.balance.toDouble()
                }
            }
        }
    }

    internal fun setTotalBalance() {
        val totalBalanceLast =
            Format.formatAmount(Format.convertFromTiynDivide(totalBalance.toString()))
        binding.balance.text = "$totalBalanceLast $currency"
        initBalanceWidget(totalBalanceLast)
    }

    private fun initUserCards() {
        if (userCards.isNotEmpty()) {
            initHomeMainCardInfo(userCards)
            binding.expandableLayout.collapse()
            binding.btnShowMore.text = getString(R.string.show_more)
            moreButtonClicked = false
            binding.btnShowMore.setCompoundDrawablesWithIntrinsicBounds(
                null, null, requireActivity().getDrawable(
                    R.drawable.ic_arrow_down
                ), null
            )
            if (userCards.size > 1) {
                binding.btnShowMore.visibility = View.VISIBLE
            } else {
                homeCardsAdapter?.submitList(this@MenuHomeFragment.userCards)
                binding.btnShowMore.visibility = View.GONE
                binding.expandableLayout.expand(true)
            }
        }
    }

    private fun initUserCardsRv() {
        binding.userCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeCardsAdapter
            val callback = EditItemTouchHelperCallback(homeCardsAdapter!!, requireContext())
            val mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper.attachToRecyclerView(this)
            itemAnimator = null
        }
    }

    private fun showMoreButtonClickEvent() {
        if (moreButtonClicked) {
            binding.apply {
                expandableLayout.collapse()
                btnShowMore.text = getString(R.string.show_more)
                btnShowMore.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, requireActivity().getDrawable(
                        R.drawable.ic_arrow_down
                    ), null
                )
            }
        } else {
            binding.allCardsProgress.visibility = View.VISIBLE
            binding.apply {
                homeCardsAdapter?.submitList(this@MenuHomeFragment.userCards)
                expandableLayout.expand()
                btnShowMore.text = getString(R.string.show_less)
                btnShowMore.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, requireActivity().getDrawable(
                        R.drawable.arrow_up_24dp
                    ), null
                )
                binding.allCardsProgress.visibility = View.GONE
            }
        }
        moreButtonClicked = !moreButtonClicked
    }

    private fun initHomeMainCardInfo(userCards: ArrayList<CardResponse>) {
        if (userCards.isNotEmpty()) {
            val mainCard = userCards.first()
            binding.apply {
                mainCardName.setCardNameAndNumber(mainCard)
                mainCardBalance.setCardBalance(mainCard)
                mainCardType.setCardTypeImage(mainCard)
                mainCardBg.setImageResource(requireContext().getDrawableFromRes(mainCard.bg_icon_name))
                setCardState(mainCard, cardStateName, requireContext())
                if (cardStateName.isVisible) {
                    binding.mainCardBg.alpha = 0.2f
                    binding.mainCardName.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_for_disable_card
                        )
                    )
                    binding.mainCardBalance.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_for_disable_card
                        )
                    )
                }
            }
        }
    }

    override fun updateWidgetList(list: ArrayList<MainWidget>) {
        super<BaseHomeFragment>.updateWidgetList(list)
        mainWidgetsList = ArrayList()
        binding.widgetsLayout.removeAllViews()
        initWidgets()
    }

    override fun makeDragAndDropOperation(fromPosition: Int, toPosition: Int) {
        super<BaseHomeFragment>.makeDragAndDropOperation(fromPosition, toPosition)
        try {
            this.doTransferOperationByType(userCards[fromPosition], userCards[toPosition])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectedCard(card: CardResponse) {
        goto(R.id.myCardsListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeCardsAdapter = null
    }

    companion object {
        var sellingRate = 1.0
        var buyingRate = 1.0
        var sellingRateRub = 1.0
        var sellingRateEur = 1.0
        var buyingRateDollar = 1.0
    }
}