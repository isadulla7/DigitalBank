package uz.fido.universaldigital.ui.fragments.products.new_design

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuNewHomeBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.NewHomeCardsAdapter
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.home_utils.initRefreshLayout
import uz.fido.universaldigital.ui.utils.home_utils.loadCardsFromPaper
import uz.fido.universaldigital.ui.utils.home_utils.loadProfileImage
import uz.fido.universaldigital.ui.utils.home_utils.setUserDetails
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
@SuppressLint("UseCompatLoadingForDrawables")
class MenuNewHomeFragment : BaseNewHomeFragment(), BaseInterface {

    private var userCards = ArrayList<CardResponse>()
    private var homeCardsAdapter: NewHomeCardsAdapter? = null
    private var balanceUpdateCounter = 0

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuNewHomeBinding.inflate(inflater, container, false)
        this.container = container
        homeCardsAdapter = NewHomeCardsAdapter(this)
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
    }

    private fun initTotalBalance() {
        userCards = loadCardsFromPaper()
        menuProductsViewModel.updateCards(userCards)
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { currentCards ->
            userCards = currentCards as ArrayList<CardResponse>
            binding.addCardLayout.isVisible = currentCards.isEmpty()
            binding.userCardsLayout.isVisible = currentCards.isNotEmpty()
            Paper.book().write(Const.PAPER_CLIENT_CARDS, currentCards)
            initUserCards()
        }
    }

    fun getCardList(refreshLayout: uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshLayout?) {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val cards = it.data?.objects ?: ArrayList()
                    balanceUpdateCounter = 0
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
                    val code = it.errorBody?.code ?: 0
                    if (code == 1024) {
                        val dialog = AlertDialog.Builder(requireContext())
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
        position: Int, cardList: ArrayList<CardResponse>, refreshLayout: uz.fido.utils.libs.smart_refresh.refresh_layout.api.RefreshLayout?
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
        binding.btnShowMore.setOnClickListener { goto(R.id.myCardsListFragment) }
        binding.userAvatar.setOnClickListener { goto(R.id.menuProfileFragment) }
        binding.notifications.setOnClickListener { goto(R.id.notificationsFragment) }
        binding.addCardLayout.setOnClickListener { goto(R.id.addCardFragment) }
    }

    private fun initUserCards() {
        if (userCards.isNotEmpty()) {
            val firstTwoCard = if (userCards.size > 1) {
                userCards.take(2)
            } else userCards
            homeCardsAdapter?.submitList(firstTwoCard)
        }
    }

    private fun initUserCardsRv() {
        binding.userCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeCardsAdapter
            itemAnimator = null
        }
    }

    override fun selectedCard(card: CardResponse) {
        goto(R.id.myCardsListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeCardsAdapter = null
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

}