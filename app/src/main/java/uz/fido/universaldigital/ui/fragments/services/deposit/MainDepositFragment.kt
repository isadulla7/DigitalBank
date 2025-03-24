package uz.fido.universaldigital.ui.fragments.services.deposit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainDepositBinding
import uz.fido.universaldigital.ui.utils.base.ViewPagerAdapter
import uz.fido.universaldigital.ui.fragments.services.deposit.usd_deposit.UsdDepositFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.uzs_deposit.UzsDepositFragment
import uz.fido.utils.utility.fragment.pop

class MainDepositFragment : BaseFragment<FragmentMainDepositBinding, MainDepositViewModel>
    (FragmentMainDepositBinding::inflate, MainDepositViewModel::class.java) {
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createViewPager()
        onClickView()

    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun createViewPager() {
        viewPagerAdapter = ViewPagerAdapter(requireActivity(), listFragment())
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.summ)
                1 -> tab.text = getString(R.string.currency)
            }
        }.attach()
    }

    private fun listFragment() = ArrayList<Fragment>().apply {
        this.add(UzsDepositFragment())
        this.add(UsdDepositFragment())

    }

}