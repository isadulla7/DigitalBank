package uz.fido.universaldigital.ui.fragments.services.money_transfers

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.money_transfer.list.MoneyTransferHistory
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMoneyTransferHistoryBinding
import uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters.MoneyTransferHistoryAdapter
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MoneyTransferHistoryFragment :
    BaseFragment<FragmentMoneyTransferHistoryBinding, MoneyTransferViewModel>(
        FragmentMoneyTransferHistoryBinding::inflate, MoneyTransferViewModel::class.java
    ) {

    private var moneyTransferHistoryAdapter: MoneyTransferHistoryAdapter? = null
    private var list = ArrayList<MoneyTransferHistory>()

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.recyclerView.apply {
            list = ArrayList()
            moneyTransferHistoryAdapter =
                MoneyTransferHistoryAdapter(list)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moneyTransferHistoryAdapter
        }
        binding.swipeRefresh.setOnRefreshListener {
            fetchList()
        }
        fetchList()
    }

    private fun fetchList() {
        binding.swipeRefresh.isRefreshing = true
        list = ArrayList()
        moneyTransferHistoryAdapter?.setList(list)
        viewModel.fetchTransfersHistory(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                    list = it.data?.data ?: ArrayList()
                    moneyTransferHistoryAdapter?.setList(list)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

}