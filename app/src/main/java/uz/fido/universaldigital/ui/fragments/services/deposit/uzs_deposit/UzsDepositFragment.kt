package uz.fido.universaldigital.ui.fragments.services.deposit.uzs_deposit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.network.domain.model.deposits.DepositListResponse
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentUzsDepositBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.adapter.DepositAdapter
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class UzsDepositFragment : BaseFragment<FragmentUzsDepositBinding, MainDepositViewModel>
    (FragmentUzsDepositBinding::inflate, MainDepositViewModel::class.java), (Deposit) -> Unit {

    private val saveDepositViewModel by activityViewModels<DepositSaveViewModel>()
    private var allDeposits = ArrayList<Deposit>()
    private val depositAdapter by lazy { DepositAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun recyclerView() {
        createRecyclerView()
        checkListDeposit()
    }

    private fun checkListDeposit() {
        if (!saveDepositViewModel.depositCurrent)
            getDepositList()
        else getVmDepositList()
    }

    private fun getVmDepositList() {
        saveDepositViewModel.depositList.observe(viewLifecycleOwner) {
            allDeposits = it
            depositAdapter.submitList(allDeposits)
        }
    }

    private fun getDepositList() {
        val skeletonScreen =
            showSkeleton(binding.deposits, depositAdapter, R.layout.shimmer_item_deposit, 10)
        viewModel.getDeposits(
            getClientToken(),
            GetDepositListRequest("dep")
        ).observe(viewLifecycleOwner) { resource ->
            Handler(Looper.getMainLooper()).postDelayed({ skeletonScreen.hide() }, 500)
            when (resource.status) {
                Status.SUCCESS -> {
                    val list = resource.data as DepositListResponse
                    allDeposits = list.deposit_types
                    saveDepositViewModel.saveDepositList(allDeposits)
                    saveDepositViewModel.depositCurrent = true
                    depositAdapter.submitList(allDeposits)
                }

                Status.ERROR -> {
                    depositAdapter.submitList(arrayListOf())
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun createRecyclerView() {
        binding.deposits.apply {
            adapter = depositAdapter
        }
    }

    override fun invoke(deposit: Deposit) {
        if (deposit.dep_id != 853) {
            gotoWithSlide(
                R.id.openDepositOferta, bundleOf(
                    "deposit" to deposit,
                    "operation" to "deposit",
                    "isSum" to true
                )
            )
        } else {
            gotoWithSlide(
                R.id.openDepositStepFirst, bundleOf(
                    "deposit" to deposit,
                    "operation" to "deposit",
                    "isSum" to true
                )
            )
        }
    }


}