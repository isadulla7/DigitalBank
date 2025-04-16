package uz.fido.universaldigital.ui.fragments.services.loan.list

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentClientCreditListBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeCreditsAdapter
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientCreditFragment
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ClientCreditListFragment :
    BaseFragment<FragmentClientCreditListBinding, MenuProductsViewModel>(
        FragmentClientCreditListBinding::inflate, MenuProductsViewModel::class.java
    ) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    private lateinit var homeCreditsAdapter: HomeCreditsAdapter
    private var creditShimmer: SkeletonScreen? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initClientCreditsRv()
        initSetOnClickListeners()
    }

    private fun initClientCreditsRv() {
        binding.rvClientCredits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            homeCreditsAdapter =
                HomeCreditsAdapter(this@ClientCreditListFragment, ArrayList(), false)
            adapter = homeCreditsAdapter
        }
        menuProductsViewModel.creditProduct.observe(viewLifecycleOwner) {
            homeCreditsAdapter.setList(it as ArrayList<CreditProduct>)
            binding.emptyView.isVisible = it.isEmpty()
        }
        if (menuProductsViewModel.creditProduct.value == null || menuProductsViewModel.creditProduct.value!!.isEmpty()) {
            creditShimmer = showSkeleton(
                binding.rvClientCredits,
                homeCreditsAdapter,
                R.layout.shimmer_item_home_credits,
                5
            )
            getCreditProducts()
        }
    }

    private fun getCreditProducts() {
        menuProductsViewModel.getClientProducts(getClientToken())
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data?.data != null) {
                            resource.data!!.data.forEach { credit ->
                                if (credit.saldo1.isNullOrEmpty()) credit.saldo1 = "0"
                                if (credit.saldo118.isNullOrEmpty()) credit.saldo118 = "0"
                                if (credit.saldo2.isNullOrEmpty()) credit.saldo2 = "0"
                                if (credit.saldo22.isNullOrEmpty()) credit.saldo22 = "0"
                                if (credit.saldo3.isNullOrEmpty()) credit.saldo3 = "0"
                                if (credit.saldo46.isNullOrEmpty()) credit.saldo46 = "0"
                                if (credit.saldo5.isNullOrEmpty()) credit.saldo5 = "0"
                                if (credit.saldo7.isNullOrEmpty()) credit.saldo7 = "0"
                            }
                            menuProductsViewModel.updateCreditGroups(resource.data!!.data)
                        }
                        creditShimmer?.hide()
                    }

                    Status.ERROR -> {
                        creditShimmer?.hide()
                    }
                }
            }
    }

    override fun confirmTakeLoan(item: CreditProduct) {
        super.confirmTakeLoan(item)
        goto(R.id.takeCreditFragment, bundleOf(ClientCreditFragment.CLIENT_CREDIT_MODEL to item))
    }

    override fun openCreditDetails(item: CreditProduct) {
        super.openCreditDetails(item)
        goto(R.id.clientCreditFragment, bundleOf(ClientCreditFragment.CLIENT_CREDIT_MODEL to item))
    }

    private fun initSetOnClickListeners() {
        binding.getCredit.setOnClickListener {
//            goto(R.id.loanGroupListFragment)
            functionInProgress()
        }
        binding.appbar.setOnBackButtonClickListener { pop() }
    }
}