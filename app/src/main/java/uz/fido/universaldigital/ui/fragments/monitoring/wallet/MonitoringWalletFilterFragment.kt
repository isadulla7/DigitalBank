package uz.fido.universaldigital.ui.fragments.monitoring.wallet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.network.domain.model.monitoring.filter.MonitoringFilter
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMonitoringUzcardFilterBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.FilterCardMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringFilterAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.filter.MonitoringAmountDialog
import uz.fido.universaldigital.ui.fragments.monitoring.filter.MonitoringDateDialog
import uz.fido.universaldigital.ui.fragments.monitoring.filter.TransactionTypeDialog
import uz.fido.universaldigital.ui.fragments.monitoring.filter.MonitoringFilterViewModel
import uz.fido.utils.const.CardConst
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MonitoringWalletFilterFragment : BaseFragment<FragmentMonitoringUzcardFilterBinding, MonitoringFilterViewModel>
    (FragmentMonitoringUzcardFilterBinding::inflate, MonitoringFilterViewModel::class.java),
    View.OnClickListener, (MonitoringFilter) -> Unit {

    private val cardAdapter by lazy { FilterCardMonitoringAdapter(this) }
    private val monitoringFilterAdapter by lazy { MonitoringFilterAdapter(arrayListOf(), this) }
    private var filterSaveVh: FilterSaveVh? = null

    private lateinit var monitoringDateDialog: MonitoringDateDialog
    private lateinit var monitoringAmountDialog: MonitoringAmountDialog
    private lateinit var transactionTypeDialog: TransactionTypeDialog

    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var allOperationFilter = arrayListOf<MonitoringFilter>()
    private var cardList = arrayListOf<FilterCard>()

    private var startDate = ""
    private var endDate = ""
    private var minAmount = ""
    private var maxAmount = ""
    private var choose = ""
    private var dateCurrent = false
    private var chooseCurrent = false
    private var amountCurrent = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkFilter()
        filterRecyclerView()
        onCLickView()
    }

    private fun checkFilter() {
        if (saveViewModel.walletFilter) {
            saveViewModel.walletMonitoringFilter.observe(viewLifecycleOwner) {
                filterSaveVh = it
                createRecyclerView()
                localFilterDone()


            }
        } else {
            cardRecyclerView()

        }
    }

    private fun localFilterDone() {
        if (filterSaveVh!!.startDate != "") {
            startDate = filterSaveVh!!.startDate
            endDate = filterSaveVh!!.endDate
            addFilterList("date", "${filterSaveVh!!.startDate} - ${filterSaveVh!!.endDate}", false)
            dateCurrent = true
            binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
        }

        if (filterSaveVh!!.maxAmount != "") {
            maxAmount = filterSaveVh!!.maxAmount
            minAmount = filterSaveVh!!.minAmount
            addFilterList("amount", "${filterSaveVh!!.minAmount} - ${filterSaveVh!!.maxAmount}", false)
            amountCurrent = true
            binding.amount.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
        }
        if (filterSaveVh!!.operationType != "") {
            choose = filterSaveVh!!.operationType
            addFilterList("choose", filterSaveVh!!.operationType, false)
            chooseCurrent = true
            binding.minPlus.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.minPlus.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
        }
    }

    private fun cardRecyclerView() {
        createRecyclerView()
        getCardList()
    }

    private fun createRecyclerView() {
        binding.recCard.apply {
            adapter = cardAdapter
        }
        if (filterSaveVh != null) {
            cardList = filterSaveVh!!.cardList
            successCardList(cardList)
        }
    }

    private fun getCardList() {
        val skeletonScreen = showSkeleton(binding.shimmerView, cardAdapter, R.layout.shimmer_item_card, 3)
        viewModel.getLocalMonitoringCardList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            Handler(Looper.getMainLooper()).postDelayed({
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
            }, 500)
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource.data?.user_objects ?: ArrayList()
                    val newList = arrayListOf<FilterCard>()
                    response.forEach { filterCard ->
                        if (filterCard.object_type == CardConst.WALLET) {
                            if (!newList.map { it.object_value }.contains(filterCard.object_value)) {
                                newList.add(filterCard)
                            }
                        }
                    }
                    if (newList.isNotEmpty()) newList.forEachIndexed { index, card ->
                        if (index != 0) card.is_selected_monitoring = true
                    }
                    cardList = newList
                    successCardList(cardList)
                }

                Status.ERROR -> {
                    showSnackbar(resource.message.toString())
                }
            }
        }
    }

    private fun successCardList(response: java.util.ArrayList<FilterCard>) {
        cardAdapter.submitList(response)
    }

    private fun filterRecyclerView() {
        createFilterRecycler()
    }

    private fun createFilterRecycler() {
        binding.recFilter.apply {
            adapter = monitoringFilterAdapter
        }
    }

    private fun onCLickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.time.setOnClickListener(this)
        binding.amount.setOnClickListener(this)
        binding.minPlus.setOnClickListener(this)
        binding.card.setOnClickListener(this)
        binding.btnEnter.setOnClickListener(this)
        binding.btnCansel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.time -> {
                showStartEndDate()
            }

            R.id.amount -> {
                showAmountFilter()
            }

            R.id.min_plus -> {
                showChoose()
            }

            R.id.card -> {
                if (binding.expandableCards.isExpanded) {
                    binding.expandableCards.collapse()
                } else {
                    binding.expandableCards.expand()
                }
            }

            R.id.btn_enter -> {
                filterChooseSave()
            }

            R.id.btn_cansel -> {
                filterBackType()
            }
        }
    }

    private fun filterBackType() {
        saveViewModel.walletFilter = false
        pop()
    }

    private fun filterChooseSave() {
        var isCurrent = false
        cardList.forEach { if (!it.is_selected_monitoring) isCurrent = true }
        if (isCurrent) {
            val filter = FilterSaveVh(startDate, endDate, maxAmount, minAmount, choose, "", cardList, arrayListOf())
            saveViewModel.setWalletMonitoringFilter(filter)
            saveViewModel.walletFilter = true
            pop()
        } else Toast.makeText(requireContext(), requireContext().getString(R.string.select_card), Toast.LENGTH_SHORT).show()
    }

    private fun showAmountFilter() {
        if (!amountCurrent) {
            monitoringAmountDialog = MonitoringAmountDialog { min_amount, max_amount ->
                minAmount = min_amount
                maxAmount = max_amount
                amountCurrent = true
                binding.amount.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                addFilterList("amount", "$min_amount - $max_amount", false)

                monitoringAmountDialog.dismiss()

            }
            monitoringAmountDialog.show(childFragmentManager, "")
        } else {
            minAmount = ""
            maxAmount = ""
            amountCurrent = false
            binding.amount.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            removeList("amount")
        }
    }

    private fun addFilterList(type: String, name: String, current: Boolean) {
        val monitoringFilter = MonitoringFilter(name, type, current)
        allOperationFilter.add(0, monitoringFilter)
        setFilterAdapter(allOperationFilter)
    }

    private fun setFilterAdapter(allOperationFilter: ArrayList<MonitoringFilter>) {
        monitoringFilterAdapter.setList(allOperationFilter)
        filterVisibility()
    }

    private fun removeList(type: String) {
        val filter = allOperationFilter.find { it.type == type }
        allOperationFilter.remove(filter)
        setFilterAdapter(allOperationFilter)
    }

    private fun showStartEndDate() {
        if (!dateCurrent) {
            monitoringDateDialog = MonitoringDateDialog { start, end ->
                startDate = start
                endDate = end
                addFilterList("date", "$start - $end", false)
                dateCurrent = true
                binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                monitoringDateDialog.dismiss()

            }
            monitoringDateDialog.show(childFragmentManager, "")
        } else {
            startDate = ""
            endDate = ""
            dateCurrent = false
            binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            removeList("date")
        }
    }

    private fun showChoose() {
        if (!chooseCurrent) {
            transactionTypeDialog = TransactionTypeDialog { choose ->
                this.choose = choose
                addFilterList("choose", choose, false)
                chooseCurrent = true
                binding.minPlus.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.minPlus.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                transactionTypeDialog.dismiss()

            }
            transactionTypeDialog.show(childFragmentManager, "")
        } else {
            this.choose = ""
            chooseCurrent = false
            binding.minPlus.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.minPlus.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            removeList("choose")
        }
    }

    override fun invoke(monitoringFilter: MonitoringFilter) {
        operationFilter(monitoringFilter.type)
        removeList(monitoringFilter.type)
    }

    private fun operationFilter(type: String) {
        when (type) {
            "amount" -> {
                amountCurrent = false
                binding.amount.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }

            "date" -> {
                dateCurrent = false
                binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }

            "choose" -> {
                chooseCurrent = false
                binding.minPlus.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.minPlus.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }


        }
    }

    override fun monitoringFilterCard(filterCard: FilterCard) {
        super.monitoringFilterCard(filterCard)
        cardList.forEach {
            if (it.object_value == filterCard.object_value) {
                it.is_selected_monitoring = !it.is_selected_monitoring
            } else {
                it.is_selected_monitoring = true
            }
        }
        cardAdapter.submitList(cardList)
        cardAdapter.notifyDataSetChanged()
    }

    private fun filterVisibility() {
        if (allOperationFilter.isNotEmpty()) {
            binding.filter.visibility = View.VISIBLE
            binding.recFilter.visibility = View.VISIBLE
        } else {
            binding.filter.visibility = View.GONE
            binding.recFilter.visibility = View.GONE

        }
    }

}