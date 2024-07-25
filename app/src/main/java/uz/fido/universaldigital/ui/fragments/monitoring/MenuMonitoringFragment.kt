package uz.fido.universaldigital.ui.fragments.monitoring

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMenuMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringPagerAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.chart.MonitoringChartFragment
import uz.fido.universaldigital.ui.fragments.monitoring.humo.HumoMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.uzcard.UzcardMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.visa.VisaMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.wallet.WalletMonitoringFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide

@AndroidEntryPoint
class MenuMonitoringFragment : BaseFragment<FragmentMenuMonitoringBinding, MenuMonitoringViewModel>
    (FragmentMenuMonitoringBinding::inflate, MenuMonitoringViewModel::class.java) {

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
        position = arguments?.getInt("position", 0) ?: 0
        selectedCard = arguments?.serializable(Const.CARD)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initMonitoringViewPager()
        filterIconCheck(position)
        getCardList()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.filter.setOnClickListener {
            when (position) {
                1 -> if (cardUzCardList.isNotEmpty()) {
                    gotoWithSlide(R.id.monitoringUzCardFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                2 -> if (cardHumoList.isNotEmpty()) {
                    gotoWithSlide(R.id.monitoringHumoFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                3 -> if (cardCurrencyList.isNotEmpty()) {
                    gotoWithSlide(R.id.monitoringVisaFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                4 -> if (cardWalledList.isNotEmpty()) {
                    gotoWithSlide(R.id.monitoringWalletFilterFragment)
                    menuMonitoringViewModel.isFilterWindows = true
                }

                else -> if (isFilter) gotoWithSlide(R.id.monitoringFilterFragment)
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
            menuMonitoringViewModel.allCardList.value = isFilter
            menuMonitoringViewModel.uzcardList.value = cardUzCardList
            menuMonitoringViewModel.humoList.value = cardHumoList
            menuMonitoringViewModel.currencyList.value = cardCurrencyList
            menuMonitoringViewModel.walledList.value = cardWalledList
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
        adapter.addFragment(VisaMonitoringFragment())
        adapter.addFragment(WalletMonitoringFragment())
        binding.viewPager.apply {
            offscreenPageLimit = 1
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
                    filterIconCheck(position)
//                    binding.chart.isVisible = position == 0
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun filterIconCheck(position: Int) {
        when (position) {
            0 -> {
                if (menuMonitoringViewModel.localFilter)
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                else binding.filter.setImageResource(R.drawable.ic_filter_frame)
            }

            1 -> {
                if (menuMonitoringViewModel.uzCardFilter)
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                else binding.filter.setImageResource(R.drawable.ic_filter_frame)
            }

            2 -> {
                if (menuMonitoringViewModel.humoFilter)
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                else binding.filter.setImageResource(R.drawable.ic_filter_frame)
            }

            3 -> {
                if (menuMonitoringViewModel.visaFilter)
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                else binding.filter.setImageResource(R.drawable.ic_filter_frame)
            }

            4 -> {
                if (menuMonitoringViewModel.walletFilter)
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                else binding.filter.setImageResource(R.drawable.ic_filter_frame)
            }
        }
    }

}