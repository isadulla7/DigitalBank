package uz.fido.universaldigital.ui.fragments.transfers.transfer_history

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferHistoryBinding
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferToCardViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.Command
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class TransferHistoriesFragment :
    BaseFragment<FragmentTransferHistoryBinding, TransferToCardViewModel>(
        FragmentTransferHistoryBinding::inflate, TransferToCardViewModel::class.java
    ) {

    enum class TransferOperation {
        BY_PHONE,
        BY_WALLET
    }

    companion object {
        const val REQUEST_KEY = "10001"
        const val DATA = "data"
    }

    private lateinit var p2PHistoryAdapter: P2PHistoryAdapter
    private lateinit var operation: TransferOperation
    private var histories = ArrayList<CardByPhone>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operation = requireArguments().serializable<TransferOperation>(Const.OPERATION) as TransferOperation
        p2PHistoryAdapter = P2PHistoryAdapter(true, operation == TransferOperation.BY_PHONE) { cardByPhone ->
            Log.d("TAG", "onCreate:${cardByPhone.phone_number} ")
            val bundle = Bundle()
            if (operation == TransferOperation.BY_PHONE) {
                bundle.putString(DATA, cardByPhone.phone_number)
            } else bundle.putString(DATA, cardByPhone.card_number)
            setFragmentResult(REQUEST_KEY, bundle)
            findNavController().navigateUp()
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initTransferTypes()
        getPopularTransfers()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getPopularTransfers() {
        val skeletonScreen =
            showSkeleton(
                binding.transferHistories,
                p2PHistoryAdapter,
                R.layout.shimmer_item_history,
                10
            )
        viewModel.getP2PHistory(
            getClientToken(),
            P2PHistoryRequest(getClientId(), Keys.getClientId(), Command.INFO, "A")
        ).observe(
            viewLifecycleOwner
        ) { resource ->
            when (resource.status) {
                Status.ERROR -> {
                    skeletonScreen.hide()
                }

                Status.SUCCESS -> {
                    skeletonScreen.hide()
                    histories.addAll(initHistoryByType(resource.data!!.cards))
                    p2PHistoryAdapter.submitList(histories)
                }
            }
        }
    }

    private fun initHistoryByType(list: ArrayList<CardByPhone>): ArrayList<CardByPhone> {
        val histories = ArrayList<CardByPhone>()
        when (operation) {
            TransferOperation.BY_PHONE -> {
                list.forEach {
                    if (it.phone_number.isNotEmpty()) {
                        histories.add(it)
                    }
                }
                binding.emptyView.isVisible = histories.isEmpty()
            }

            else -> {
                list.forEach {
                    if (it.card_type == CardConst.WALLET) {
                        histories.add(it)
                    }
                }
                binding.emptyView.isVisible = histories.isEmpty()
            }
        }
        return histories
    }

    private fun initTransferTypes() {
        binding.transferHistories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = p2PHistoryAdapter
        }
    }

}