package uz.fido.universaldigital.ui.fragments.transfers.transfer_history

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTransferHistoryBinding
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferToCardViewModel
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.util.Locale

@AndroidEntryPoint
class PopularTransfersFragment : BaseFragment<FragmentTransferHistoryBinding, TransferToCardViewModel>(
    FragmentTransferHistoryBinding::inflate, TransferToCardViewModel::class.java
) {

    private lateinit var popularTransfersAdapter: PopularTransfersAdapter
    private var histories = ArrayList<PopularTransfers>()

    companion object {
        const val REQUEST_KEY = "10002"
        const val DATA = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popularTransfersAdapter = PopularTransfersAdapter(true) { popularTransfer ->
            if (arguments != null) {
                if (requireArguments().getString("path") == "home") {
                    val bundle = Bundle()
                    bundle.putString(DATA, popularTransfer.card_number)
                    goto(R.id.transferToCardFragment, bundle)
                }
            } else {
                val bundle = Bundle()
                bundle.putString(DATA, popularTransfer.card_number)
                setFragmentResult(REQUEST_KEY, bundle)
                findNavController().navigateUp()
            }
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
        binding.etReceiverName.doAfterTextChanged {
            it?.let {
                passSearch(it.toString())
            }
        }
    }

    private fun getPopularTransfers() {
        val skeletonScreen = showSkeleton(
            binding.transferHistories, popularTransfersAdapter, R.layout.shimmer_item_history, 10
        )
        viewModel.getPopularTransferList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.ERROR -> {
                    skeletonScreen.hide()
                    binding.emptyView.isVisible = histories.isEmpty()
                }

                Status.SUCCESS -> {
                    skeletonScreen.hide()
                    histories.clear()
                    resource.data!!.popular_transfers.let { arrayList ->
                        arrayList.forEach {
                            if (!it.empbossed_name.isNullOrEmpty() && !it.card_number.isNullOrEmpty() && it.card_number!!.length == 16) {
                                histories.add(it)
                            }
                        }
                    }
                    binding.emptyView.isVisible = histories.isEmpty()
                    popularTransfersAdapter.submitList(histories)
                }
            }
        }
    }

    private fun passSearch(str: String) {
        binding.transferHistories.setHasFixedSize(true)
        binding.transferHistories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val filteredList = ArrayList<PopularTransfers>()
        if (str.isEmpty()) {
            binding.transferHistories.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = popularTransfersAdapter
                popularTransfersAdapter.submitList(histories)
            }
        } else {
            for (i in 0 until histories.size) {
                histories[i].empbossed_name?.let {
                    if (it.lowercase(Locale.getDefault())
                            .contains(str.lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(histories[i])
                    }
                }
            }
            binding.transferHistories.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = popularTransfersAdapter
                popularTransfersAdapter.submitList(filteredList)
            }
        }
    }

    private fun initTransferTypes() {
        binding.transferHistories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = popularTransfersAdapter
        }
    }

}