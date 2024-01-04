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
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.DeleteCardRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentOtherCardsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardsListAdapter
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.CloseWalletDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.ShareCardNumberDialog
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.WalletOperationsDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getLayoutManager
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getSpanCount
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class WalletsFragment : BaseSimpleFragment<FragmentOtherCardsBinding>(
    FragmentOtherCardsBinding::inflate
), BaseInterface, View.OnClickListener {

    private val menuProductsViewModel: MenuProductsViewModel by viewModels()
    private var cardsAdapter: CardsListAdapter? = null
    private var walletList = ArrayList<CardResponse>()

    private lateinit var walletOperationsDialog: WalletOperationsDialog
    private lateinit var selectedWallet: CardResponse

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
        walletList.clear()
        cardList.forEach {
            if (it.object_type == "KL") {
                walletList.add(it)
            }
        }
        cardsAdapter?.submitList(walletList)
        if (walletList.isEmpty()) showEmptyView(walletList)
    }

    private fun initCardsRv(cardLayoutManager: GridLayoutManager) {
        cardsAdapter = CardsListAdapter(this@WalletsFragment, cardLayoutManager)
        binding.cardList.apply {
            layoutManager = cardLayoutManager
            adapter = cardsAdapter
        }
    }

    private fun refreshCards() {
        try {
            cardsAdapter = CardsListAdapter(this@WalletsFragment, getLayoutManager())
            (binding.cardList.layoutManager as GridLayoutManager).spanCount = getSpanCount()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun switchList() {
        refreshCards()
    }

    private fun showEmptyView(list: ArrayList<CardResponse>) {
        binding.emptyView.isVisible = list.isEmpty()
    }

    override fun selectedWallet(item: CardResponse) {
        walletOperationsDialog = WalletOperationsDialog(this@WalletsFragment)
        walletOperationsDialog.show(childFragmentManager, "TAG")
        selectedWallet = item
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
            R.id.delete_wallet -> {
                walletOperationsDialog.dismiss()
                CloseWalletDialog {
                    closeWallet()
                }.show(childFragmentManager, "")
            }

            R.id.rename_wallet -> {
                walletOperationsDialog.dismiss()
                goto(R.id.editCardFragment, bundleOf(Const.CARD to selectedWallet))
            }

            R.id.wallet_requisites -> {
                walletOperationsDialog.dismiss()
                goto(R.id.aboutCardFragment, bundleOf(Const.CARD to selectedWallet))
            }

            R.id.top_up -> {
                walletOperationsDialog.dismiss()
                goto(R.id.overMyCardsFragment, bundleOf(Const.RECEIVER_CARD to selectedWallet))
            }

            R.id.take_off -> {
                walletOperationsDialog.dismiss()
                goto(R.id.overMyCardsFragment, bundleOf(Const.SENDER_CARD to selectedWallet))
            }

            R.id.wallet_monitoring -> {
                walletOperationsDialog.dismiss()
                goto(R.id.menuMonitoringFragment, bundleOf(Const.CARD to selectedWallet))
            }
        }
    }

    private fun closeWallet() {
        showProgress()
        menuProductsViewModel.deleteWallet(
            getClientToken(), DeleteWalletRequest(getClientToken(), selectedWallet.object_id)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    walletList.remove(selectedWallet)
                    cardsAdapter?.notifyItemRemoved(walletList.indexOf(selectedWallet))
                    getCardList()
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun deleteWallet() {
        showProgress()
        menuProductsViewModel.deleteCardRequest(
            getClientToken(), DeleteCardRequest(selectedWallet.object_id)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    walletList.remove(selectedWallet)
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

}