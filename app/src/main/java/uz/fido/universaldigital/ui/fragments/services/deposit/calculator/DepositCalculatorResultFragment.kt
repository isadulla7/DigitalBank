package uz.fido.universaldigital.ui.fragments.services.deposit.calculator

import android.os.Bundle
import android.os.Handler
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.CalculateDepositAuto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCalculatorResultDepositBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.adapter.DepositCalculateAdapter
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class DepositCalculatorResultFragment : BaseFragment<FragmentCalculatorResultDepositBinding, MainDepositViewModel>(
    FragmentCalculatorResultDepositBinding::inflate, MainDepositViewModel::class.java
) {

    private lateinit var depositCalculateAdapter: DepositCalculateAdapter
    private var depId = 0
    private var amount = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            depId = it.getInt("dep_id", 0)
            amount = it.getString("amount").toString()
        }
        onClickView()
        recyclerView()

    }

    private fun recyclerView() {
        creteRecyclerView()
        getListCalculator()
    }

    private fun getListCalculator() {
        val skeleton = showSkeleton(binding.recCalculator, depositCalculateAdapter, R.layout.shimmer_item_history, 5)
        viewModel.calculateDepositAuto(
            getClientToken(),
            CalculateDepositAuto(depId.toString(), amount)
        ).observe(viewLifecycleOwner) { resource ->
            Handler().postDelayed({ skeleton.hide() }, 500)
            when (resource.status) {
                Status.SUCCESS -> {
                    var count = 0
                    resource.data?.data?.forEach {
                        count++
                        it.count = count
                    }
                    depositCalculateAdapter.submitList(resource.data?.data)
                }

                Status.ERROR -> {
                    showSnackbar(resource.message.toString())
                }
            }
        }
    }

    private fun creteRecyclerView() {
        depositCalculateAdapter = DepositCalculateAdapter()
        binding.recCalculator.apply {
            adapter = depositCalculateAdapter
        }
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }

    }


}