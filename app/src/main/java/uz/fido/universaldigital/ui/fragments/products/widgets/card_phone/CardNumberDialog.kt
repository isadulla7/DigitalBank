package uz.fido.universaldigital.ui.fragments.products.widgets.card_phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogCardNumberBinding
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferViewModel
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.PopularTransfersAdapter
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar

class CardNumberDialog(val onClick:(String)->Unit): DialogFragment() {

    private lateinit var binding: DialogCardNumberBinding
    private lateinit var popularTransfersAdapter: PopularTransfersAdapter
    private val cardsViewModel: TransferViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogCardNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popularTransfersAdapter = PopularTransfersAdapter(false, ::popularTransferClickEvent)
        binding.appBar.setOnClickListener {
            dismiss()
        }
        initSavedReceivers()
        getPopularList()
    }

    private fun getPopularList() {
        cardsViewModel.getHomePopularTransfers().observe(viewLifecycleOwner){resource ->
            when(resource.status){
                Status.SUCCESS->{
                    val list= resource.data?.popular_transfers
                    popularTransfersAdapter.submitList(list)
                }
                Status.ERROR->{
                    showSnackbar(resource.message.toString())
                }

            }
        }
    }

    private fun initSavedReceivers() {
        binding.recCard.layoutManager = LinearLayoutManager(requireContext())
        binding.recCard.adapter = popularTransfersAdapter
    }
    private fun popularTransferClickEvent(item: PopularTransfers) {
        onClick(item.card_number.toString().replace(" ",""))
    }
}