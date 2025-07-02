package uz.fido.universaldigital.ui.fragments.monitoring

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMenuMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringPagerAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.local.chart.MonitoringChartFragment
import uz.fido.universaldigital.ui.fragments.monitoring.humo.HumoMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.uzcard.UzcardMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.wallet.WalletMonitoringFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto

@AndroidEntryPoint
class MenuMonitoringFragment : BaseFragment<FragmentMenuMonitoringBinding, MenuMonitoringViewModel>(FragmentMenuMonitoringBinding::inflate, MenuMonitoringViewModel::class.java) {

    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    private val cardCurrencyList = arrayListOf<String>()
    private val cardUzCardList = arrayListOf<String>()
    private val cardWalledList = arrayListOf<String>()
    private val cardHumoList = arrayListOf<String>()
    private var selectedCard: CardResponse? = null

    private var isFilter = false
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            position = arguments?.getInt("position", 0) ?: 0
            selectedCard = arguments?.serializable(Const.CARD)
        } catch (e: Exception) {
            recordException(e, ::onCreate.name)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initMonitoringViewPager()
        updateFilterIcon(position)
        getCardList()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.filter.setOnClickListener {
            when (position) {
                1 -> if (cardUzCardList.isNotEmpty()) {
                    goto(R.id.monitoringUzCardFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                2 -> if (cardHumoList.isNotEmpty()) {
                    goto(R.id.monitoringHumoFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                3 -> if (cardWalledList.isNotEmpty()) {
                    goto(R.id.monitoringWalletFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                else -> if (isFilter) goto(R.id.monitoringFilterFragment)
            }
        }
        binding.chart.setOnClickListener {
            val dialog = MonitoringChartFragment()
            dialog.show(childFragmentManager, "")
        }
    }

    private fun getCardList() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { card ->
            isFilter = card.isNotEmpty()
            card.forEach {
                when (it.object_type) {
                    UZCARD -> {
                        cardUzCardList.add(it.object_id)
                    }

                    HUMO_CARD -> {
                        cardHumoList.add(it.object_id)
                    }

                    CURRENCY_CARD, "TET_SUM" -> {
                        cardCurrencyList.add(it.object_value)
                    }

                    WALLET -> {
                        cardWalledList.add(it.account_code)
                    }
                }
            }
            menuMonitoringViewModel.userHasCard(isFilter)
            menuMonitoringViewModel.uzcardList.value = cardUzCardList
            menuMonitoringViewModel.humoList.value = cardHumoList
            menuMonitoringViewModel.currencyList.value = cardCurrencyList
            menuMonitoringViewModel.walledList.value = cardWalledList
            menuMonitoringViewModel.walledCode.value=if (cardWalledList.isNotEmpty()) card.firstOrNull { it.object_id==cardWalledList[0] }?.object_value?:"" else ""
        }
    }

    override fun onResume() {
        super.onResume()
        if (!menuMonitoringViewModel.isFilterWindows) {
            binding.viewPager.setCurrentItem(0, false)
            menuMonitoringViewModel.isFilterWindows = false
        }
    }

    private fun initMonitoringViewPager() {
        val adapter = MonitoringPagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(LocalMonitoringFragment())
        adapter.addFragment(UzcardMonitoringFragment())
        adapter.addFragment(HumoMonitoringFragment())
        adapter.addFragment(WalletMonitoringFragment())
        binding.viewPager.apply {
            offscreenPageLimit = 3
            this.adapter = adapter
            binding.tabLayout.setupWithViewPager(this)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    this@MenuMonitoringFragment.position = position
                    updateFilterIcon(position)
                    binding.chart.isVisible = position == 0
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun updateFilterIcon(position: Int) {
        val filterStatus = listOf(
            menuMonitoringViewModel.localFilter,
            menuMonitoringViewModel.uzCardFilter,
            menuMonitoringViewModel.humoFilter,
            menuMonitoringViewModel.walletFilter
        )
        binding.filter.setImageResource(
            if (filterStatus.getOrNull(position) == true) R.drawable.ic_filter_yes
            else R.drawable.ic_filter_frame
        )
    }
}