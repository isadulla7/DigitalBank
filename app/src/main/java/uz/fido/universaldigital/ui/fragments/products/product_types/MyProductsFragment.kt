package uz.fido.universaldigital.ui.fragments.products.product_types

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMyCardsListBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardPagerAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MyCardsListFragment : BaseSimpleFragment<FragmentMyCardsListBinding>(
    FragmentMyCardsListBinding::inflate
), BaseInterface {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
   private var stateCurrent:Boolean=false
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
    }

    private fun setAdditionIcon() {
        if (Paper.book().read(Const.LAYOUT_MANAGER_GRID, true) == true) binding.appBar.setAdditionalIcon(R.drawable.grid_list2)
        else binding.appBar.setAdditionalIcon(R.drawable.grid_icon)
    }

    private fun setCardListState() {
        val currentState = Paper.book().read(Const.LAYOUT_MANAGER_GRID, true) ?: true
        Paper.book().write(Const.LAYOUT_MANAGER_GRID, !currentState)
    }

    private fun initCardTypes() {
        adapter = CardPagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(MyCardsFragment())
        adapter.addFragment(MyDepositsFragment())
        adapter.addFragment(MyCreditsFragment())
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.appBar.setAdditionalBtnVisibility(tab?.position == 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    override fun onPause() {
        super.onPause()
        stateCurrent=true
    }
    override fun onResume() {
        super.onResume()
        if (stateCurrent){
            initCardTypes()
          //  binding.appBar.setAdditionalBtnVisibility(false)
            stateCurrent=false
        }
    }
}