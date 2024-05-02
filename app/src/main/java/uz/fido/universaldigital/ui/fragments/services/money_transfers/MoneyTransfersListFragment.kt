package uz.fido.universaldigital.ui.fragments.services.money_transfers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.money_transfer.receive.MoneyTransferParamsResponse
import uz.fido.network.domain.model.money_transfer.receive.RemittanceType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMoneyTransfersListBinding
import uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters.MoneyTransferListAdapter
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.hasUserCard
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MoneyTransfersListFragment :
    BaseFragment<FragmentMoneyTransfersListBinding, MoneyTransferViewModel>(
        FragmentMoneyTransfersListBinding::inflate, MoneyTransferViewModel::class.java
    ) {

    private lateinit var moneyTransferAdapter: MoneyTransferListAdapter

    private var moneyTransferParamsResponse: MoneyTransferParamsResponse? = null
    private var list = ArrayList<RemittanceType>()


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUI()
        if (list.isEmpty()) fetchTransferParams()
    }

    private fun initUI() {
        binding.appBar.setAdditionalBtnVisibility(true)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener { gotoWithSlide(R.id.moneyTransferHistoryFragment) }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            moneyTransferAdapter = MoneyTransferListAdapter(list) {
                openMoneyTransfer(it)
            }
            adapter = moneyTransferAdapter
        }
    }


    private fun openMoneyTransfer(item: RemittanceType) {
        val cards = Paper.book().read(Const.PAPER_CLIENT_CARDS, ArrayList<CardResponse>())
        if (hasUserCard()) {
            moneyTransferParamsResponse?.remittance_type = item
            when (item.foreignCode) {
                42202 -> {
                    for (account in cards) {
                        if (account.currency_char == "USD") {
                            val bundle = Bundle()
                            bundle.putSerializable("params_model", moneyTransferParamsResponse)
                            bundle.putString("privacy", "wu_terms.txt")
                            return
                        }
                    }
                }
                else -> {
                    val bundle = Bundle()
                    bundle.putSerializable("params_model", moneyTransferParamsResponse)
                    gotoWithSlide(R.id.moneyTransferGetFragment, bundle)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTransferParams() {
        val skeletonScreen = showSkeleton(
            binding.recyclerView,
            moneyTransferAdapter,
            R.layout.shimmer_item_money_transfer,
            4
        )
        viewModel.fetchTransferParams(getClientToken()).observe(viewLifecycleOwner) {
            skeletonScreen.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    moneyTransferParamsResponse = it.data as MoneyTransferParamsResponse
                    moneyTransferParamsResponse?.let { response ->
                        response.data.forEach { model ->
                            if (model.modeType == "ONLINE") {
                                list.add(model)
                            }
                        }
                        list.sortBy { model -> model.order ?: 0 }
                        moneyTransferAdapter.notifyDataSetChanged()
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

}