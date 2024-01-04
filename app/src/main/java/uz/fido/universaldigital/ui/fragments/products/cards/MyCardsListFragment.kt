package uz.fido.universaldigital.ui.fragments.products.cards

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMyCardsListBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardPagerAdapter
import uz.fido.universaldigital.ui.fragments.products.cards.card_types.AllCardsFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_types.CurrencyCardsFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_types.HumoCardsFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_types.UzCardFragment
import uz.fido.universaldigital.ui.fragments.products.cards.card_types.WalletsFragment
import uz.fido.universaldigital.ui.fragments.products.cards.dialogs.AddCardDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MyCardsListFragment : BaseSimpleFragment<FragmentMyCardsListBinding>(
    FragmentMyCardsListBinding::inflate
), BaseInterface {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    private lateinit var adapter: CardPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCardTypes()
        setAdditionIcon()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            setCardListState()
            setAdditionIcon()
            (adapter.getItem(binding.viewPager.currentItem) as BaseInterface).switchList()
            menuProductsViewModel.updateCardState.postValue(true)
        }
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

    private fun setAdditionIcon() {
        if (Paper.book().read(Const.LAYOUT_MANAGER_GRID, true)
        ) binding.appBar.setAdditionalIcon(R.drawable.grid_list2)
        else binding.appBar.setAdditionalIcon(R.drawable.grid_icon)
    }

    private fun setCardListState() {
        val currentState = Paper.book().read(Const.LAYOUT_MANAGER_GRID, true)
        Paper.book().write(Const.LAYOUT_MANAGER_GRID, !currentState)
    }

    private fun initCardTypes() {
        adapter = CardPagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(AllCardsFragment())
        adapter.addFragment(UzCardFragment())
        adapter.addFragment(HumoCardsFragment())
        adapter.addFragment(CurrencyCardsFragment())
        adapter.addFragment(WalletsFragment())
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}