package uz.fido.universaldigital.ui.fragments.services.loan

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.loan_groups.CreditGroup
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentLoanGroupListBinding
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.LoanGroupAdapter
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class LoanGroupListFragment : BaseFragment<FragmentLoanGroupListBinding, LoanViewModel>(
    FragmentLoanGroupListBinding::inflate, LoanViewModel::class.java
), (CreditGroup) -> Unit {

    companion object {
        const val CREATE_LOAN = "create_loan"
    }

    private val loanAdapter by lazy { LoanGroupAdapter(requireContext(), this) }
    private var loanList = mutableListOf<CreditGroup>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()
        onClickView()
        emptyListCheck()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener {
            pop()
        }
    }

    private fun emptyListCheck() {
        binding.layoutEmpty.isVisible = loanList.isEmpty()

    }

    private fun recyclerView() {
        createRecycler()
        isCheckSaveCreditGroupList()
    }

    private fun isCheckSaveCreditGroupList() {
        if (loanList.isEmpty())
//            getLoanGroupList()
        else setAdapter(loanList)
    }

    private fun getLoanGroupList() {
        val skeletonScreen =
            showSkeleton(binding.recLoan, loanAdapter, R.layout.shimmer_item_deposit, 10)
        viewModel.getCreditGroups(getClientToken()).observe(viewLifecycleOwner) { resource ->
            if (isVisible) {
                skeletonScreen.hide()
            }
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource.data?.credit_products ?: mutableListOf()
                    setAdapter(response)
                    emptyListCheck()
                }

                Status.ERROR -> {
                    emptyListCheck()
                    showSnackbar(resource.message.toString())
                }

            }
        }

    }

    private fun setAdapter(response: MutableList<CreditGroup>) {
        loanAdapter.submitList(response)
        loanList = response
    }

    private fun createRecycler() {
        binding.recLoan.apply {
            adapter = loanAdapter
        }
    }

    override fun invoke(creditGroup: CreditGroup) {
        gotoWithSlide(R.id.createLoanFragment, bundleOf(CREATE_LOAN to creditGroup))
    }
}