package uz.fido.universaldigital.ui.fragments.products.cards.card_types

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.BlockCardRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.DeleteCardRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentOtherCardsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardsListAdapter
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.BlockCardDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.CardOperationsDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.DeleteCardDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.ShareCardNumberDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getLayoutManager
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getSpanCount
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class UzCardFragment : BaseSimpleFragment<FragmentOtherCardsBinding>(
    FragmentOtherCardsBinding::inflate
), BaseInterface, View.OnClickListener {

    private lateinit var cardOperationsDialog: CardOperationsDialog
    private lateinit var selectedCard: CardResponse

    private val menuProductsViewModel: MenuProductsViewModel by viewModels()
    private var uzCardList = ArrayList<CardResponse>()
    private var cardsAdapter: CardsListAdapter? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initCardsRv(getLayoutManager())
        initCards()
    }

    override fun onResume() {
        super.onResume()
        refreshCards()
    }

    private fun initCards() {
        val cardList: ArrayList<CardResponse> = try {
            Paper.book().read(Const.PAPER_CLIENT_CARDS, ArrayList())
        } catch (e: Exception) {
            Paper.book().write(Const.PAPER_CLIENT_CARDS, ArrayList<CardResponse>())
            ArrayList()
        }
        uzCardList.clear()
        cardList.forEach {
            if (it.object_type == "SV") {
                uzCardList.add(it)
            }
        }
        cardsAdapter?.submitList(uzCardList)
        if (uzCardList.isEmpty()) showEmptyView(uzCardList)
    }

    private fun initCardsRv(cardLayoutManager: GridLayoutManager) {
        cardsAdapter = CardsListAdapter(this@UzCardFragment, cardLayoutManager)
        binding.cardList.apply {
            layoutManager = cardLayoutManager
            adapter = cardsAdapter
        }
    }

    override fun switchList() {
        refreshCards()
    }

    private fun refreshCards() {
        try {
            cardsAdapter = CardsListAdapter(this@UzCardFragment, getLayoutManager())
            (binding.cardList.layoutManager as GridLayoutManager).spanCount = getSpanCount()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEmptyView(list: ArrayList<CardResponse>) {
        binding.emptyView.isVisible = list.isEmpty()
    }

    override fun selectedCard(card: CardResponse) {
        cardOperationsDialog = CardOperationsDialog(card, this@UzCardFragment)
        cardOperationsDialog.show(childFragmentManager, "TAG")
        selectedCard = card
    }

    override fun shareCardNumberDialog(item: CardResponse) {
        ShareCardNumberDialog(item, object : BaseInterface {
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
        }).show(childFragmentManager, "")
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
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
                goto(R.id.menuMonitoringFragment, bundleOf(Const.CARD to selectedCard))
            }

            R.id.safety -> {
                cardOperationsDialog.dismiss()
                goto(R.id.cardSafetyFragment, bundleOf(Const.CARD to selectedCard))
            }
        }
    }

    private fun getCardList() {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            it?.let {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { cardListResponse ->
                            cardListResponse.objects?.let { it1 ->
                                cardsAdapter?.submitList(it1)
                                cardsAdapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun deleteCard() {
        showProgress()
        menuProductsViewModel.deleteCardRequest(
            getClientToken(), DeleteCardRequest(selectedCard.object_id)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    uzCardList.remove(selectedCard)
                    cardsAdapter?.notifyDataSetChanged()
                    getCardList()
                }

                Status.ERROR -> {
                    hideProgress()
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
                command = "card"
            )
        ).observe(viewLifecycleOwner) {
            it?.let {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        menuProductsViewModel.shouldUpdate = true
                        cardsAdapter?.notifyItemChanged(uzCardList.indexOf(selectedCard))
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

}