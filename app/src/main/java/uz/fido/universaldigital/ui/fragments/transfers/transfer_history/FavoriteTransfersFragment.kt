package uz.fido.universaldigital.ui.fragments.transfers.transfer_history

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentFavoriteTransfersBinding
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferToCardViewModel
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class FavoriteTransfersFragment :
    BaseFragment<FragmentFavoriteTransfersBinding, TransferToCardViewModel>(
        FragmentFavoriteTransfersBinding::inflate, TransferToCardViewModel::class.java
    ) {

    private lateinit var popularTransfersAdapter: FavoriteTransfersAdapter
    private lateinit var nonPopularTransfersAdapter: FavoriteTransfersAdapter

    private var histories = ArrayList<PopularTransfers>()

    companion object {
        const val REQUEST_KEY = "10002"
        const val DATA = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popularTransfersAdapter =
            FavoriteTransfersAdapter(true, ::popularTransferClickEvent, ::setFavorite)
        nonPopularTransfersAdapter =
            FavoriteTransfersAdapter(true, ::popularTransferClickEvent, ::setFavorite)
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
        viewModel.getPopularTransferList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.ERROR -> {
                    binding.emptyView.isVisible = histories.isEmpty()
                }

                Status.SUCCESS -> {
                    val favoriteHistories = ArrayList<PopularTransfers>()
                    val nonFavoriteHistories = ArrayList<PopularTransfers>()
                    resource.data!!.popular_transfers.let { arrayList ->
                        arrayList.forEach {
                            if (!it.empbossed_name.isNullOrEmpty() && !it.card_number.isNullOrEmpty() && it.card_number!!.length == 16) {
                                histories.add(it)
                                if (it.is_favourite == "Y") {
                                    favoriteHistories.add(it)
                                } else {
                                    nonFavoriteHistories.add(it)
                                }
                            }
                        }
                    }
                    binding.emptyView.isVisible = histories.isEmpty()
                    binding.emptyFavorites.isVisible = favoriteHistories.isEmpty()
                    popularTransfersAdapter.submitList(favoriteHistories)
                    nonPopularTransfersAdapter.submitList(nonFavoriteHistories)
                }
            }
        }
    }

    private fun setFavorite(isFavorite: Boolean, cardNumber: String) {
        if (isFavorite) {
            viewModel.setToNonFavoriteTransfer(cardNumber).observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    getPopularTransfers()
                } else {
                    showSnackbar(it.message.toString())
                }
            }
        } else {
            viewModel.setToFavoriteTransfer(cardNumber).observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    getPopularTransfers()
                } else {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun popularTransferClickEvent(item: PopularTransfers) {
        val bundle = Bundle()
        bundle.putString(DATA, item.card_number)
        setFragmentResult(REQUEST_KEY, bundle)
        findNavController().navigateUp()
    }

    private fun initTransferTypes() {
        binding.favoriteTransfers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = popularTransfersAdapter
        }
        binding.nonFavoriteTransfers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = nonPopularTransfersAdapter
        }
    }

}