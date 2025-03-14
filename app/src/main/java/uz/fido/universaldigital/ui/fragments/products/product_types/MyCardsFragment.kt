package uz.fido.universaldigital.ui.fragments.products.product_types

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.BlockCardRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.DeleteCardRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentAllCardsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardsListAdapter
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.AddCardDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.BlockCardDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.CardOperationsDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.CloseWalletDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.DeleteCardDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.ShareCardNumberDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.WalletOperationsDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getLayoutManager
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getSpanCount
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MyCardsFragment : BaseSimpleFragment<FragmentAllCardsBinding>(
    FragmentAllCardsBinding::inflate
), BaseInterface, View.OnClickListener {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var clientAllCardList = ArrayList<CardResponse>()
    private var cardsAdapter: CardsListAdapter? = null
    private lateinit var walletOperationsDialog: WalletOperationsDialog
    private lateinit var cardOperationsDialog: CardOperationsDialog
    private lateinit var selectedCard: CardResponse

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initCardsRv(getLayoutManager())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCards()
        binding.addCardBtn.setOnClickListener {
            AddCardDialog {
                if (it == Const.ORDER_CARD) {
                    goto(R.id.orderCardListFragment)
                } else {
                    goto(R.id.addCardFragment)
                }
            }.show(parentFragmentManager, "")
        }
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { list ->
            clientAllCardList = list as ArrayList<CardResponse>
            cardsAdapter?.submitList(clientAllCardList)
            showEmptyView(clientAllCardList)
        }
    }


    private fun initCardsRv(cardLayoutManager: GridLayoutManager) {
        cardsAdapter = CardsListAdapter(this@MyCardsFragment, cardLayoutManager)
        binding.cardList.apply {
            setHasFixedSize(true)
            layoutManager = cardLayoutManager
            adapter = cardsAdapter
        }
    }

    override fun switchList() {
        refreshCards()
    }


    private fun refreshCards() {
        try {
            cardsAdapter = CardsListAdapter(this@MyCardsFragment, getLayoutManager())
            (binding.cardList.layoutManager as GridLayoutManager).spanCount = getSpanCount()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEmptyView(list: ArrayList<CardResponse>) {
        binding.emptyView.isVisible = list.isEmpty()
    }

    override fun selectedCard(card: CardResponse) {
        cardOperationsDialog = CardOperationsDialog(card, this@MyCardsFragment)
        cardOperationsDialog.show(childFragmentManager, "TAG")
        selectedCard = card
    }

    override fun selectedWallet(item: CardResponse) {
        walletOperationsDialog = WalletOperationsDialog(this)
        walletOperationsDialog.show(childFragmentManager, "TAG")
        selectedCard = item
    }

    override fun shareCardNumberDialog(item: CardResponse) {
        val dialog = ShareCardNumberDialog(item, object : BaseInterface {
            override fun copyCardNumber() {
                val clipboard: ClipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(android.R.attr.label.toString(), item.object_value)
                clipboard.setPrimaryClip(clip)
            }

            override fun shareCardNumber() {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, item.object_value)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        })
        dialog.show(childFragmentManager, "")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.transfer_to_card -> {
                cardOperationsDialog.dismiss()
                if (selectedCard.object_type != CardConst.CURRENCY_CARD) goto(
                    R.id.overMyCardsFragment, bundleOf(Const.RECEIVER_CARD to selectedCard)
                ) else goto(R.id.conversionFragment, bundleOf(Const.SENDER_CARD to selectedCard))
            }

            R.id.card_settings -> {
                cardOperationsDialog.dismiss()
                goto(R.id.editCardFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.block_card -> {
                cardOperationsDialog.dismiss()
                BlockCardDialog {
                    blockCardRequest()
                }.show(childFragmentManager, "")
            }

            R.id.delete_card -> {
                cardOperationsDialog.dismiss()
                DeleteCardDialog {
                    deleteCard()
                }.show(childFragmentManager, "")
            }

            R.id.requisites -> {
                cardOperationsDialog.dismiss()
                goto(R.id.aboutCardFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.monitoring -> {
                cardOperationsDialog.dismiss()
                Log.d("TAG", "onClick:${selectedCard.object_id} ")
                when (selectedCard.object_type) {
                    CardConst.UZCARD -> goto(R.id.uzCardMonitoringFragment, bundleOf(Const.CARD to selectedCard))
                    CardConst.HUMO_CARD -> goto(R.id.humoMonitoringFragment, bundleOf(Const.CARD to selectedCard))
                    CardConst.WALLET -> goto(R.id.walletMonitoringFragment, bundleOf(Const.CARD to selectedCard))
                    //else->goto(R.id.visaMonitoringFragment, bundleOf(Const.CARD to selectedCard))

                }
            }

            R.id.safety -> {
                cardOperationsDialog.dismiss()
                goto(R.id.cardSafetyFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.delete_wallet -> {
                if (checkWalletBalance(selectedCard.balance)) {
                    showSnackbar(title = getString(R.string.wallet), snackbarText = getString(R.string.wallet_be_closed))
                } else {
                    walletOperationsDialog.dismiss()
                    CloseWalletDialog {
                        closeWallet()
                    }.show(childFragmentManager, "")
                }

            }

            R.id.rename_wallet -> {
                walletOperationsDialog.dismiss()
                goto(R.id.editCardFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.wallet_requisites -> {
                walletOperationsDialog.dismiss()
                goto(R.id.aboutCardFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.top_up -> {
                walletOperationsDialog.dismiss()
                goto(R.id.overMyCardsFragment, bundleOf(Const.RECEIVER_CARD to selectedCard))
            }

            R.id.take_off -> {
                walletOperationsDialog.dismiss()
                goto(R.id.overMyCardsFragment, bundleOf(Const.SENDER_CARD to selectedCard))
            }

            R.id.wallet_monitoring -> {
                walletOperationsDialog.dismiss()
                goto(R.id.walletMonitoringFragment, bundleOf(Const.CARD to selectedCard))
            }
        }
    }

    private fun getCardList() {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        it.data?.let { cardListResponse ->
                            cardListResponse.objects?.let { it1 ->
                                binding.cardList.itemAnimator = null
                                menuProductsViewModel.updateCards(it1)
                                initCardsRv(getLayoutManager())
                                initCards()
                            }
                        }
                    }

                    Status.ERROR -> {
                        hideProgress()
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun checkWalletBalance(balance: String): Boolean {
        Log.d("TAG", "checkWalletBalance:${balance} ")
        val doubleBalance = balance.toDouble()
        return doubleBalance > 0
    }

    private fun deleteCard() {
        showProgress()
        menuProductsViewModel.deleteCardRequest(
            getClientToken(), DeleteCardRequest(selectedCard.object_id)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.cardList.itemAnimator = null
                    getCardList()
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun closeWallet() {
        showProgress()
        menuProductsViewModel.deleteWallet(
            getClientToken(), DeleteWalletRequest(getClientToken(), selectedCard.object_id)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    getCardList()
                }

                Status.ERROR -> {
                    showProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun blockCardRequest() {
        menuProductsViewModel.blockCardRequest(
            getClientToken(), BlockCardRequest(
                from_object_id = selectedCard.object_id,
                status_id = selectedCard.state,
                text = "block",
                command = "card"
            )
        ).observe(viewLifecycleOwner) {
            it?.let {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        menuProductsViewModel.shouldUpdate = true
                        cardsAdapter?.notifyItemChanged(clientAllCardList.indexOf(selectedCard))
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cardsAdapter = null
    }
}