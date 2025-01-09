package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMainNewsBinding
import uz.fido.universaldigital.ui.fragments.products.widgets.notifications.news.NewsFragment
import uz.fido.universaldigital.ui.fragments.products.widgets.notifications.notifications.NotificationsFragment
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainNewsFragment : BaseSimpleFragment<FragmentMainNewsBinding>(
    FragmentMainNewsBinding::inflate
), BaseInterface {

    private lateinit var adapter: NewsPagerAdapter

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initViewPager()
        binding.appBar.setOnBackButtonClickListener { pop() }
    }


    private fun initViewPager() {
        adapter = NewsPagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(NewsFragment())
        adapter.addFragment(NotificationsFragment())
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

}