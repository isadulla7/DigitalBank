package uz.fido.universaldigital.ui.fragments.services.mib.addmib

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAddMibBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringViewPagerAdapter
import uz.fido.universaldigital.ui.fragments.services.mib.MibViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.addmib.fiz_mib.MibFizFragment
import uz.fido.universaldigital.ui.fragments.services.mib.addmib.you_mib.MibYouFragment
import uz.fido.utils.utility.fragment.pop

class MibAddFragment : BaseFragment<FragmentAddMibBinding, MibViewModel>
    (FragmentAddMibBinding::inflate, MibViewModel::class.java) {

    private lateinit var viewPagerAdapter: MonitoringViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewPager()
        appBar()
    }

    private fun appBar() {
        binding.appBar.setOnBackButtonClickListener {
            pop()
        }
    }

    private fun createViewPager() {
        viewPagerAdapter = MonitoringViewPagerAdapter(requireActivity(), listFragment())
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.fiz_mib)
                1 -> tab.text = getString(R.string.you_mib)
            }
        }.attach()
    }

    private fun listFragment() = ArrayList<Fragment>().apply {
        this.add(MibFizFragment())
        this.add(MibYouFragment())
    }

}