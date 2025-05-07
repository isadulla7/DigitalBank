package uz.fido.universaldigital.ui.fragments.services.applications

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainApplicationListBinding
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.services.applications.adapter.AppTypesViewPagerAdapter
import uz.fido.universaldigital.ui.fragments.services.applications.app_types.AllAppFragment
import uz.fido.universaldigital.ui.fragments.services.applications.app_types.FailedAppFragment
import uz.fido.universaldigital.ui.fragments.services.applications.app_types.SuccessAppFragment
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainApplicationListFragment : BaseFragment<FragmentMainApplicationListBinding, UtilsViewModel>(
    FragmentMainApplicationListBinding::inflate, UtilsViewModel::class.java
) {

    private lateinit var adapter: AppTypesViewPagerAdapter

    companion object {
        var OPERATION = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            OPERATION = it.getString(Const.OPERATION, "")
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initAppTypes()
    }

    private fun initAppTypes() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        adapter = AppTypesViewPagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(AllAppFragment())
        adapter.addFragment(SuccessAppFragment())
        adapter.addFragment(FailedAppFragment())
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

}