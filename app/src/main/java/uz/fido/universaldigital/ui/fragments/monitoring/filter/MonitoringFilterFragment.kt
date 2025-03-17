package uz.fido.universaldigital.ui.fragments.monitoring.filter

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.network.domain.model.monitoring.filter.MonitoringFilter
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMonitoringFilterBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.FilterLocalCardMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringFilterAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.ServiceAllMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringAmountDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringCardDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringChooseDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringDateDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringServiceFilterDialog
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MonitoringFilterFragment : BaseFragment<FragmentMonitoringFilterBinding, MonitoringFilterViewModel>(
    FragmentMonitoringFilterBinding::inflate, MonitoringFilterViewModel::class.java
), View.OnClickListener, (MonitoringFilter) -> Unit {

    private val cardAdapter by lazy {
        FilterLocalCardMonitoringAdapter(
            arrayListOf(),
            this
        )
    }
    private val serviceAdapter by lazy {
        ServiceAllMonitoringAdapter(
            requireContext(),
            arrayListOf(),
            this
        )
    }
    private val monitoringFilterAdapter by lazy {
        MonitoringFilterAdapter(
            arrayListOf(),
            this
        )
    }
    private var filterSaveVh: FilterSaveVh? = null
    private lateinit var monitoringDateDialog: MonitoringDateDialog
    private lateinit var cardDialog: MonitoringCardDialog
    private lateinit var monitoringAmountDialog: MonitoringAmountDialog
    private lateinit var monitoringChooseDialog: MonitoringChooseDialog
    private lateinit var monitoringServiceFilterDialog: MonitoringServiceFilterDialog
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var allOperationFilter = arrayListOf<MonitoringFilter>()
    private var cardList = arrayListOf<FilterCard>()
    private var serviceList = arrayListOf<UserPayedService>()

    private var startDate = ""
    private var endDate = ""
    private var minAmount = ""
    private var maxAmount = ""
    private var choose = ""
    private var dateCurrent = false
    private var chooseCurrent = false
    private var amountCurrent = false
    private var cardResponseError = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardRecyclerView()
        doneFilter()
        filterRecyclerView()
        serviceRecyclerView()
        onCLickView()
        editTextView()
    }

    private fun doneFilter() {
        if (saveViewModel.localFilter) {
            lifecycleScope.launch {
                saveViewModel.localMonitoringFilter.collect {
                    if (allOperationFilter.isEmpty()) {
                        filterSaveVh = it
                        cardResponseError = true
                        localFilterDone()
                        cardSave()
                        getCardFilterList()
                        getSaveFilter()
                    }
                }
            }
        } else {
            getCardList()

        }
    }

    private fun getSaveFilter() {
        val layoutFlexBox = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        binding.recService.apply {
            layoutManager = layoutFlexBox
            adapter = serviceAdapter
        }
        serviceList = filterSaveVh?.serviceList ?: arrayListOf()
        serviceList.forEach { service ->
            if (service.service_current) {
                service.list.forEach {
                    allOperationFilter.add(
                        MonitoringFilter(
                            it.partner_obj,
                            it.service_id,
                            current = false
                        )
                    )
                }
            }
        }
        serviceAdapter.setList(serviceList)
        monitoringFilterAdapter.setList(allOperationFilter)
        filterVisibility()

    }

    private fun cardSave() {
        if (!filterSaveVh?.cardNumber.isNullOrEmpty()) {
            if (filterSaveVh?.cardNumber!!.startsWith("998")) {
                binding.etCardNumber.setMask("#### ## ### ## ##")
                binding.etCardNumber.setMaxLength(17)
                binding.cardNumberLayout.hint = getString(R.string.phone_number)
                binding.etCardNumber.setText("+${filterSaveVh?.cardNumber ?: ""}")
            } else {
                binding.etCardNumber.setMask("#### #### #### ####")
                binding.etCardNumber.setMaxLength(19)
                binding.etCardNumber.setText(filterSaveVh?.cardNumber ?: "")
            }
        }
    }

    private fun getCardFilterList() {
        cardList = filterSaveVh!!.cardList
        successCardList(cardList)
    }

    private fun localFilterDone() {
        if (filterSaveVh!!.startDate != "") {
            startDate = filterSaveVh!!.startDate
            endDate = filterSaveVh!!.endDate
            addFilterList(
                "date",
                "${filterSaveVh!!.startDate} - ${filterSaveVh!!.endDate}",
                false
            )
            dateCurrent = true
            binding.time.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
            buttonClickVisibility()
        }

        if (filterSaveVh!!.maxAmount != "") {
            maxAmount = filterSaveVh!!.maxAmount
            minAmount = filterSaveVh!!.minAmount
            addFilterList(
                "amount",
                "${filterSaveVh!!.minAmount} - ${filterSaveVh!!.maxAmount}",
                false
            )
            amountCurrent = true
            binding.amount.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.amount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.whiteColor
                )
            )
            buttonClickVisibility()
        }
        if (filterSaveVh!!.operationType != "") {
            choose = filterSaveVh!!.operationType
            addFilterList("choose", filterSaveVh!!.operationType, false)
            chooseCurrent = true
            binding.minPlus.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.minPlus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.whiteColor
                )
            )
            buttonClickVisibility()
        }

//        val list=filterSaveVh!!.cardList.filter { it.is_selected_monitoring }
//        if (list.isNotEmpty()){
//            addFilterList("card",getString(R.string.card),false)
//            binding.newCard.background =
//                ContextCompat.getDrawable(requireContext(),R.drawable.monitoring_filter_item_color_click)
//            binding.newCard.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.whiteColor
//                )
//            )
//            buttonClickVisibility()
//
//        }

    }

    override fun monitoringFilterCard(filterCard: FilterCard) {
        super.monitoringFilterCard(filterCard)
        cardList.first { it.object_id == filterCard.object_id }.is_selected_monitoring =
            !filterCard.is_selected_monitoring
        cardAdapter.notifyDataSetChanged()
        buttonClickVisibility()
    }

    private fun filterRecyclerView() {
        createFilterRecycler()
    }

    private fun createFilterRecycler() {
        val layoutFlexBox = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        binding.recFilter.apply {
            layoutManager = layoutFlexBox
            adapter = monitoringFilterAdapter

        }
    }

    private fun serviceRecyclerView() {
        val layoutFlexBox = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        binding.recService.apply {
            layoutManager = layoutFlexBox
            adapter = serviceAdapter
        }
        if (!saveViewModel.localFilter) {
            getServices()
        }
    }

    private fun getServices() {
        viewModel.getLocalMonitoringServiceList(getClientToken())
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        serviceList = resource.data?.user_payed_services ?: arrayListOf()
                        serviceAdapter.setList(serviceList)
                    }

                    Status.ERROR -> {
                        showSnackbar(resource.message.toString())
                    }
                }
            }
    }

    private fun onCLickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.time.setOnClickListener(this)
        binding.amount.setOnClickListener(this)
        binding.minPlus.setOnClickListener(this)
        binding.card.setOnClickListener(this)
        binding.operationTip.setOnClickListener(this)
        binding.btnEnter.setOnClickListener(this)
        binding.btnCansel.setOnClickListener(this)
        binding.newCard.setOnClickListener(this)
        binding.cardAnimation.setOnClickListener(this)
    }

    private fun cardRecyclerView() {
        createRecyclerView()

    }

    private fun createRecyclerView() {
        binding.recCard.apply {
            adapter = cardAdapter
        }
    }

    private fun getCardList() {
        val skeletonScreen = showSkeleton(binding.shimmerView, cardAdapter, R.layout.shimmer_item_card, 1)
        viewModel.getLocalMonitoringCardList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            skeletonScreen.hide()
            binding.shimmerView.visibility = View.GONE
            when (resource.status) {
                Status.SUCCESS -> {
                    cardResponseError = true
                    val activeList = arrayListOf<FilterCard>()
                    val passiveList = arrayListOf<FilterCard>()
                    val response = resource.data?.user_objects ?: ArrayList()
                    response.forEach { item ->
                        if (item.state == "0") {
                            if (!activeList.map { it.object_value }.contains(item.object_value)) {
                                activeList.add(item)
                            }
                        } else {
                            if (!passiveList.map { it.object_value }.contains(item.object_value)) {
                                passiveList.add(item)
                            }
                        }
                    }
                    if (cardList.isEmpty()) {
                        cardList.addAll(activeList)
                    }
                    successCardList(cardList)
                }

                Status.ERROR -> {
                    cardResponseError = false
                    showSnackbar(resource.message.toString())
                }
            }
        }
    }

    private fun successCardList(response: ArrayList<FilterCard>) {
        cardAdapter.setListItem(response)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.time -> {
                showStartEndDate()
            }

            R.id.card, R.id.card_animation -> {
                if (binding.expandableCards.isExpanded) {
                    binding.cardAnimation.animate().rotation(0f).start()

                    binding.expandableCards.collapse()
                } else {
                    binding.cardAnimation.animate().rotation(180f).start()

                    binding.expandableCards.expand()
                }
            }

            R.id.amount -> {
                showAmountFilter()
            }

            R.id.min_plus -> {
                showChoose()
            }

            R.id.operation_tip -> {
                /*         monitoringFilterDialog = MonitoringFilterDialog(serviceList) {
                             newServiceList = arrayListOf()

                             it.forEach { if (it.service_current) newServiceList.add(it) }
                             serviceAdapter.setList(newServiceList)
                             var monthList: List<MonitoringFilter> =
                                 allOperationFilter.filter { it.type in listOf("date", "amount", "choose") }
                             allOperationFilter = arrayListOf()
                             allOperationFilter.addAll(monthList)
                             newServiceList.forEach { monitoringFilter ->
                                 allOperationFilter.add(
                                     MonitoringFilter(
                                         monitoringFilter.service_name.toString(),
                                         monitoringFilter.service_id.toString(), false
                                     )
                                 )
                             }
                             setFilterAdapter(allOperationFilter)

                             serviceList = it
                             buttonClickVisibility()
                             monitoringFilterDialog.dismiss()
                         }
                         monitoringFilterDialog.show(childFragmentManager, "")*/
            }

            R.id.btn_enter -> {
                filterChooseSave()
            }

            R.id.new_card -> {
                cardDialog = MonitoringCardDialog(cardList) { filterCards ->
                    cardDialog.dismiss()
                    cardList = filterCards
                    checkCard()
                    val firstOperation = allOperationFilter.filter { it.type == "card" }
                    if (firstOperation.isEmpty()) {
                        addFilterList("card", getString(R.string.card), false)
                    }
                    buttonClickVisibility()
                }
                cardDialog.show(childFragmentManager, "")
            }

            R.id.btn_cansel -> {
                filterBackType()
            }
        }
    }

    private fun checkCard() {
        val checkCard = cardList.filter { it.is_selected_monitoring }
        if (checkCard.isNotEmpty()) {
            binding.newCard.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
            binding.newCard.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.whiteColor
                )
            )
        } else {
            binding.newCard.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.newCard.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mainTextColor
                )
            )
        }
    }

    private fun filterBackType() {
        saveViewModel.localFilter = false
        pop()

    }

    private fun filterChooseSave() {
        saveViewModel.localFilter = true
        val carNumber = binding.etCardNumber.text.toString().replace(" ", "").replace("+", "")
        val filter = FilterSaveVh(
            startDate,
            endDate,
            maxAmount,
            minAmount,
            choose,
            carNumber,
            cardList,
            serviceList
        )
        saveViewModel.setLocalMonitoringFilter(filter)
        pop()
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

    private fun showChoose() {
        if (!chooseCurrent) {
            monitoringChooseDialog = MonitoringChooseDialog { choose ->
                this.choose = choose
                addFilterList("choose", choose, false)
                chooseCurrent = true
                binding.minPlus.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.minPlus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.whiteColor
                    )
                )
                buttonClickVisibility()
                monitoringChooseDialog.dismiss()

            }
            monitoringChooseDialog.show(childFragmentManager, "")
        } else {
            this.choose = ""
            chooseCurrent = false
            binding.minPlus.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.minPlus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mainTextColor
                )
            )
            removeList("choose")
        }
    }

    private fun showAmountFilter() {
        if (!amountCurrent) {
            monitoringAmountDialog = MonitoringAmountDialog { min_amount, max_amount ->
                minAmount = min_amount
                maxAmount = max_amount
                amountCurrent = true
                binding.amount.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.whiteColor
                    )
                )
                addFilterList("amount", "$min_amount - $max_amount", false)
                buttonClickVisibility()

                monitoringAmountDialog.dismiss()

            }
            monitoringAmountDialog.show(childFragmentManager, "")
        } else {
            minAmount = ""
            maxAmount = ""
            amountCurrent = false
            binding.amount.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.amount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mainTextColor
                )
            )
            removeList("amount")
        }
    }

    private fun showStartEndDate() {
        if (!dateCurrent) {
            monitoringDateDialog = MonitoringDateDialog { start, end ->
                startDate = start
                endDate = end
                addFilterList("date", "$start - $end", false)
                dateCurrent = true
                binding.time.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.time.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.whiteColor
                    )
                )
                buttonClickVisibility()
                monitoringDateDialog.dismiss()

            }
            monitoringDateDialog.show(childFragmentManager, "")
        } else {
            startDate = ""
            endDate = ""
            dateCurrent = false
            binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
            binding.time.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mainTextColor
                )
            )
            removeList("date")
        }
    }

    private fun removeList(type: String) {
        val filter = allOperationFilter.find { it.type == type }
        allOperationFilter.remove(filter)
        setFilterAdapter(allOperationFilter)
        buttonClickVisibility()
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

    override fun invoke(monitoringFilter: MonitoringFilter) {
        operationFilter(monitoringFilter.type, monitoringFilter)
        if (monitoringFilter.type == "amount" || monitoringFilter.type == "date" || monitoringFilter.type == "choose" || monitoringFilter.type == "card") {
            removeList(monitoringFilter.type)
        }

    }

    private fun operationFilter(type: String, monitoringFilter: MonitoringFilter) {
        when (type) {
            "amount" -> {
                amountCurrent = false
                minAmount = ""
                maxAmount = ""
                binding.amount.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mainTextColor
                    )
                )
                buttonClickVisibility()
            }

            "date" -> {
                startDate = ""
                endDate = ""
                dateCurrent = false
                binding.time.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.time.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mainTextColor
                    )
                )
                buttonClickVisibility()

            }

            "choose" -> {
                choose = ""
                chooseCurrent = false
                binding.minPlus.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.minPlus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mainTextColor
                    )
                )
            }

            "card" -> {
                binding.newCard.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.newCard.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mainTextColor
                    )
                )
                cardList.forEach {
                    it.is_selected_monitoring = false
                }
                buttonClickVisibility()

            }

            else -> {
                val item = serviceList.filter { it.service_id.toString() == type }
                val allServiceItem = allOperationFilter.filter { it.type == type }
                if (allServiceItem.size == 1) {
                    serviceList.first { it.service_id.toString() == type }.list =
                        arrayListOf()
                    serviceList.first { it.service_id.toString() == type }.service_current = false
                    serviceAdapter.setList(serviceList)
                } else {
                    val localMonitoring =
                        item.first().list.find { it.service_id == monitoringFilter.type && it.partner_obj == monitoringFilter.name }
                    item.first().list.remove(localMonitoring)
                }
                val layoutFlexBox = FlexboxLayoutManager(context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    alignItems = AlignItems.STRETCH
                }
                binding.recService.apply {
                    layoutManager = layoutFlexBox
                    adapter = serviceAdapter
                }


                allOperationFilter.remove(monitoringFilter)
                monitoringFilterAdapter.setList(allOperationFilter)
                setFilterAdapter(allOperationFilter)
                buttonClickVisibility()
            }

        }
    }

    override fun monitoringPayed(userPayedService: UserPayedService) {
        super.monitoringPayed(userPayedService)
        monitoringServiceFilterDialog =
            MonitoringServiceFilterDialog(
                userPayedService.service_id.toString(),
                userPayedService
            ) { item ->
                val localMonitoringList = allOperationFilter.filter { it.type == userPayedService.service_id.toString() }
                allOperationFilter.removeAll(localMonitoringList.toSet())
                item.forEach {
                    allOperationFilter.add(
                        MonitoringFilter(
                            name = it.partner_obj,
                            type = it.service_id,
                            false
                        )
                    )
                }
                serviceList.forEach {
                    if (it == userPayedService && item.isNotEmpty()) {
                        it.service_current = true
                    } else if (it == userPayedService && item.isEmpty()) {
                        it.service_current = false
                    }
                }
                val layoutFlexBox = FlexboxLayoutManager(context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    alignItems = AlignItems.STRETCH
                }
                binding.recService.apply {
                    layoutManager = layoutFlexBox
                    adapter = serviceAdapter
                }


                userPayedService.list = item
                serviceAdapter.setList(serviceList)
                setFilterAdapter(allOperationFilter)
                monitoringServiceFilterDialog.dismiss()
                buttonClickVisibility()
            }
        monitoringServiceFilterDialog.show(childFragmentManager, "")

    }

    private fun editTextView() {
        binding.etCardNumber.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                choicePhoneAndCard(it)
            }
        }
    }

    private fun choicePhoneAndCard(it: Editable?) {
        val number = it.toString().replace(" ", "")
        if (number.startsWith("+")) {
            if (number.length < 2) {
                binding.etCardNumber.setMask("#### ## ### ## ##")
                binding.etCardNumber.setMaxLength(17)
                binding.cardNumberLayout.hint = getString(R.string.phone_number)
            }
            phoneNumber(number)
        } else {
            if (number.length < 2) {
                binding.etCardNumber.setMask("#### #### #### ####")
                binding.etCardNumber.setMaxLength(19)
            }
            cardNumber(it)
        }
    }

    private fun phoneNumber(number: String) {
        if (number.isNotEmpty()) {
            if (number.length == 17)
                buttonClickVisibility()
        }
    }

    private fun cardNumber(it: Editable?) {
        if (it.toString().replace(" ", "").length == 16) {
            binding.progress.visibility = View.VISIBLE
            binding.scanner.visibility = View.GONE
            viewModel.getCardInfo(
                getClientToken(), CheckCardRequestP2p(
                    "card",
                    it.toString().replace(" ", "")
                )
            ).observe(viewLifecycleOwner) {
                binding.progress.visibility = View.GONE
                binding.scanner.visibility = View.VISIBLE
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data as CheckCardResponse
                        binding.ownerName.text = response.empbossed_name
                        binding.ownerName.visibility = View.VISIBLE
                        buttonClickVisibility()
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun buttonClickVisibility() {
        val isCard = cardList.filter { !it.is_selected_monitoring }
        val isService = serviceList.filter { it.service_current }
        if ((amountCurrent || chooseCurrent || dateCurrent || isCard.isNotEmpty() || isService.isNotEmpty()) && cardResponseError) {
            binding.btnEnter.isEnabled = true
            binding.btnEnter.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.brandRedColor)
        } else {
            binding.btnEnter.isEnabled = false
            binding.btnEnter.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.buttonEnabled)
        }
    }

}