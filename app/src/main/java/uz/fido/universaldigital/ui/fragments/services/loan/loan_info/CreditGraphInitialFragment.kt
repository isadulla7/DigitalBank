package uz.fido.universaldigital.ui.fragments.services.loan.loan_info

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.loan_graph.CreditGraph
import uz.fido.network.domain.model.loans.loan_graph.CreditGraphRequest
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreditGraphBinding
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.CreditGraphAdapter
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientCreditFragment
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientLoanViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class CreditGraphInitialFragment : BaseFragment<FragmentCreditGraphBinding, ClientLoanViewModel>(
    FragmentCreditGraphBinding::inflate, ClientLoanViewModel::class.java
) {
    private lateinit var clientProduct: CreditProduct
    private var list = ArrayList<CreditGraph>()
    private var overdueDate = ArrayList<String>()
    private lateinit var creditGraphAdapter: CreditGraphAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            clientProduct =
                it.serializable<CreditProduct>(ClientCreditFragment.CLIENT_CREDIT_MODEL) as CreditProduct
            overdueDate = it.serializable<ArrayList<String>>("status") as ArrayList<String>
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        recyclerView()
        fetchGraph()
    }

    private fun fetchGraph() {
        val skeletonView =
            showSkeleton(binding.rec, creditGraphAdapter, R.layout.shimmer_item_credit_graph, 5)
        viewModel.getCreditGraph(getClientToken(), CreditGraphRequest(clientProduct.loanId))
            .observe(viewLifecycleOwner) {
                skeletonView.hide()
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data?.data ?: arrayListOf()
                        list.addAll(response)
                        list.forEachIndexed { index, creditGraph ->
                            creditGraph.position = index + 1
                        }
                        creditGraphAdapter.notifyDataSetChanged()
                    }

                    Status.ERROR -> {
                        it.message.toString()
                    }
                }
            }
    }

    private fun recyclerView() {
        binding.rec.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            list = ArrayList()
            creditGraphAdapter = CreditGraphAdapter(list, context, overdueDate)
            adapter = creditGraphAdapter
        }
    }
}