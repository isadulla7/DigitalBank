package uz.fido.universaldigital.ui.main_dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ChooseCardDialogBinding
import uz.fido.universaldigital.ui.main_dialogs.adapters.ChooseCardAdapter

class ChooseCardDialog(
    var cards: List<CardResponse>,
    var amount: String? = null,
    private var onClickListener: (CardResponse?) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: ChooseCardDialogBinding
    private var chooseCardAdapter: ChooseCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChooseCardDialogBinding.inflate(inflater, container, false)
        initCardsList()
        return binding.root
    }

    private fun initCardsList() {
        chooseCardAdapter = ChooseCardAdapter(getSortedCards(), amount) {
            if (cards.isNotEmpty())
                onClickListener.invoke(it)
            else onClickListener.invoke(null)
        }
        binding.cardList.apply {
            adapter = chooseCardAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun getSortedCards(): ArrayList<CardResponse> {
        val newList = mutableListOf<CardResponse>()
        newList.addAll(cards)
        newList.sortByDescending { it.balance.toDouble() }
        newList.sortBy { it.state }
        return newList as ArrayList<CardResponse>
    }
}