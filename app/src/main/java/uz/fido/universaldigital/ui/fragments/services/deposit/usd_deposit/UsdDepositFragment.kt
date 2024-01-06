package uz.fido.universaldigital.ui.fragments.services.deposit.usd_deposit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainDepositBinding
import uz.fido.universaldigital.databinding.FragmentUsdDepositBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.adapter.DepositAdapter
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.uzs_deposit.DepositSaveViewModel
import uz.fido.utils.utility.fragment.goto

@AndroidEntryPoint
class UsdDepositFragment : BaseFragment<FragmentUsdDepositBinding, MainDepositViewModel>
    (FragmentUsdDepositBinding::inflate, MainDepositViewModel::class.java), (Deposit) -> Unit {

    private val saveDepositViewModel by activityViewModels<DepositSaveViewModel>()
    private var allDeposits = ArrayList<Deposit>()
    private val depositAdapter by lazy { DepositAdapter(this) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView()
    }

    private fun recyclerView() {
        createRecyclerView()
        getDepositList()
    }

    private fun getDepositList() {
        saveDepositViewModel.depositList.observe(viewLifecycleOwner) {
            allDeposits = arrayListOf()
            it.forEach { if (it.currency_code != "000") allDeposits.add(it) }
            if (allDeposits.isEmpty()) binding.layoutEmpty.visibility = View.VISIBLE
            depositAdapter.submitList(allDeposits)
        }
    }

    private fun createRecyclerView() {
        binding.deposits.apply {
            adapter = depositAdapter

        }
    }

    override fun invoke(deposit: Deposit) {
        goto(
            R.id.openDepositStepFirst, bundleOf(
                "deposit" to deposit,
                "operation" to "deposit",
                "isSum" to false
            )
        )

    }
}