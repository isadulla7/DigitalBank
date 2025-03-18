package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogMonitoringCardBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.FilterCardMonitoringAdapter


class MonitoringCardDialog(
    val cardList: ArrayList<FilterCard>,
    private val sortList: (ArrayList<FilterCard>) -> Unit = {}
) : DialogFragment(), BaseInterface {

    private lateinit var binding: DialogMonitoringCardBinding

    private val cardAdapter by lazy { FilterCardMonitoringAdapter(this) }
    private var sortCardList = arrayListOf<FilterCard>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMonitoringCardBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        onClick()

    }

    private fun onClick() {
        binding.btnContinue.setOnClickListener {
            sortCardList = arrayListOf()
            cardList.forEach {
                if (it.is_selected_monitoring) {
                    sortCardList.add(it)
                }
            }
            if (sortCardList.isNotEmpty()) {
                dismiss()
                sortList.invoke(cardList)
            } else {
                Toast.makeText(requireContext(), "Не выбирать номер карты", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createRecyclerView() {
        binding.recCard.apply {
            adapter = cardAdapter
        }
        successCardList(cardList)
    }

    private fun successCardList(response: ArrayList<FilterCard>) {
        cardAdapter.submitList(response)
    }

    override fun monitoringFilterCard(filterCard: FilterCard) {
        super.monitoringFilterCard(filterCard)

        var newFilter: FilterCard? = null
        var position = 0
        cardList.forEachIndexed { intex, it ->
            if (filterCard == it) {
                position = intex
                newFilter = FilterCard(it.object_id, object_name = it.object_name, object_type = it.object_type, object_value = it.object_value, state = it.state, !it.is_selected_monitoring)
            }
        }
        cardList.remove(filterCard)
        if (newFilter != null) {
            cardList.add(position, newFilter!!)
        }
        cardAdapter.submitList(cardList)
        cardAdapter.notifyDataSetChanged()
    }


}