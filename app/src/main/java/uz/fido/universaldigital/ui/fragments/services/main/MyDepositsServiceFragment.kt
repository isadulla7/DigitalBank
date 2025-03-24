package uz.fido.universaldigital.ui.fragments.services.main

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyDepositsServiceBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeDepositsAdapter
import uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit.ClientDepositFragment
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MyDepositsServiceFragment : BaseFragment<FragmentMyDepositsServiceBinding, MenuProductsViewModel>(
    FragmentMyDepositsServiceBinding::inflate, MenuProductsViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private lateinit var homeDepositsAdapter: HomeDepositsAdapter
    private var depositShimmer: SkeletonScreen? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initClientDepositsRv()

    }

    private fun initClientDepositsRv() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.rvClientDeposits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            homeDepositsAdapter =
                HomeDepositsAdapter(this@MyDepositsServiceFragment, ArrayList(), false)
            adapter = homeDepositsAdapter
        }
        menuProductsViewModel.clientDeposit.observe(viewLifecycleOwner) {
            homeDepositsAdapter.setList(it as ArrayList<ClientDeposit>)
            binding.emptyView.isVisible = it.isEmpty()
        }
        if (menuProductsViewModel.clientDeposit.value == null || menuProductsViewModel.clientDeposit.value!!.isEmpty()) {
            depositShimmer = showSkeleton(
                binding.rvClientDeposits,
                homeDepositsAdapter,
                R.layout.shimmer_item_home_credits,
                5
            )
            getDeposits()
        }
    }

    private fun getDeposits() {
        menuProductsViewModel.getClientDepositList(getClientToken()).observe(viewLifecycleOwner) {
            depositShimmer?.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.data != null) {
                        menuProductsViewModel.updateClientDepositList(it.data!!.data)
                    }
                }

                Status.ERROR -> {

                }
            }
        }
    }

    override fun openDepositDetails(item: ClientDeposit) {
        super.openDepositDetails(item)
        goto(
            R.id.clientDepositFragment,
            bundleOf(ClientDepositFragment.CLIENT_DEPOSIT_MODEL to item)
        )
    }

}