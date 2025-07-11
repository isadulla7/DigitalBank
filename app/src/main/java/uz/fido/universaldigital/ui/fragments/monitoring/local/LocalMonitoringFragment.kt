package uz.fido.universaldigital.ui.fragments.monitoring.local

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.InParamsResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.TransferChequeModel
import uz.fido.network.domain.model.monitoring.filter.LocalMonitoringFilterRequest
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentLocalMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.Companion.CHEQUE_MODEL
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.Companion.OPERATION_MONITORING
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.format.Format
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap

@AndroidEntryPoint
class LocalMonitoringFragment : BaseFragment<FragmentLocalMonitoringBinding, LocalMonitoringViewModel>(
    FragmentLocalMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
), (LocalMonitoring) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var skeletonScreen: SkeletonScreen

    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var newTotalList = arrayListOf<LocalMonitoring>()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val localMonitoringAdapter by lazy { LocalMonitoringAdapter(totalList, this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserCards()
    }

    private fun checkUserCards() {
        viewLifecycleOwner.lifecycleScope.launch {
            saveViewModel.userHasCard.collect {
                if (it) initUI() else showEmptyState()
            }
        }
    }

    private fun initUI() {
        setMonitoringDate()
        initScrollListener()
        initRecyclerView()
        checkForFilter()
        initSetOnClickListeners()
    }

    private fun setMonitoringDate(startDate: String? = null, endDate: String? = null) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
        val calendarEnd = Calendar.getInstance()
        dateBegin = startDate.orEmpty()
        dateEnd = endDate ?: dateFormat.format(calendarEnd.time)
    }

    private fun initScrollListener() {
        layoutManager = LinearLayoutManager(requireContext())
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (saveViewModel.localFilter) getFilteredMonitoringList(page)
                else getMonitoringList(page, operationType)
            }
        }
    }

    private fun initRecyclerView() {
        binding.monitoringList.apply {
            adapter = localMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = this@LocalMonitoringFragment.layoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(localMonitoringAdapter))
        }
    }

    private fun checkForFilter() {
        if (saveViewModel.localFilter) getFilteredMonitoringList(1)
        else checkSavedMonitoringList()
    }

    private fun initSetOnClickListeners() {
        binding.reshreshButton.setOnClickListener {
            totalList = ArrayList()
            binding.consError.visibility = View.GONE
            binding.shimmerView.visibility = View.VISIBLE
            if (saveViewModel.localFilter) getFilteredMonitoringList(0)
            else getMonitoringList(1, operationType)
        }
    }

    private fun showEmptyState() {
        binding.shimmerView.visibility = View.GONE
        binding.monitoringList.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.layoutEmpty.findViewById<TextViewMedium>(R.id.title).text = getString(R.string.card_list_no)
    }

    private fun getFilteredMonitoringList(page: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            saveViewModel.localMonitoringFilter.collectLatest { filter ->
                setMonitoringDate(filter.startDate, filter.endDate)
                var skeletonScreen: SkeletonScreen? = null
                if (page == 1) {
                    totalList = arrayListOf()
                    scrollListener.resetState()
                    binding.shimmerView.visibility = View.VISIBLE
                    binding.monitoringList.visibility = View.GONE
                    skeletonScreen = showSkeleton(binding.shimmerView, MibDetailsAdapter(requireContext(), this@LocalMonitoringFragment), R.layout.shimmer_item_monitoring, 1)
                } else {
                    binding.progress.visibility = View.VISIBLE
                }
                val filterRequest = LocalMonitoringFilterRequest(
                    startDate = if (filter.startDate.isNotEmpty()) dateBegin else null,
                    endDate = if (filter.endDate.isNotEmpty()) dateEnd else null,
                    pageNumber = page,
                    pageItemSize = 20,
                    serviceIds = filter.getServiceIdsAndPartnerObj().first,
                    objectIds = filter.getSelectedCards(),
                    toObjectValue = filter.getServiceIdsAndPartnerObj().second,
                    maxAmount = filter.getMaxAmount(),
                    minAmount = filter.getMinMinAmount()
                )
                viewModel.newFilterLocalMonitoring(getClientToken(), filterRequest).observe(viewLifecycleOwner) {
                    if (page == 1) {
                        skeletonScreen?.hide()
                        binding.shimmerView.visibility = View.GONE
                        binding.monitoringList.visibility = View.VISIBLE
                    } else {
                        binding.progress.visibility = View.GONE
                    }
                    when (it.status) {
                        Status.SUCCESS -> {
                            val response = it.data?.local_transactions ?: arrayListOf()
                            successMonitoringList(response, getOperationType(filter))
                        }

                        Status.ERROR -> {
                            localMonitoringAdapter.removeList()
                            binding.consError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun checkSavedMonitoringList() {
        viewLifecycleOwner.lifecycleScope.launch {
            saveViewModel.saveLocalMonitoring.collect { localMonitoring ->
                if (localMonitoring.isNotEmpty()) {
                    localMonitoringAdapter.removeList()
                    newTotalList = localMonitoring
                    successMonitoringList(localMonitoring, 2)
                    getMonitoringListFirstPage()
                    binding.shimmerView.visibility = View.GONE
                } else {
                    getMonitoringList(1, operationType)
                }
            }
        }
    }

    private fun getMonitoringList(page: Int, operationType: Int) {
        val isFirstPage = page == 1
        if (isFirstPage) {
            skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            totalList = arrayListOf()
            scrollListener.resetState()
        } else {
            binding.progress.visibility = View.VISIBLE
        }
        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = page.toString(),
                page_item_size = PAGE_SIZE,
                object_ids = arrayListOf()
            )
        ).observe(viewLifecycleOwner) { resource ->
            if (isFirstPage && this::skeletonScreen.isInitialized) {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
            } else {
                binding.progress.visibility = View.GONE
            }
            val response = resource?.data?.local_transactions ?: arrayListOf()
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.consError.visibility = View.GONE
                    successMonitoringList(response, operationType)
                    if (isFirstPage) {
                        saveViewModel.saveLocalMonitoring(response)
                        saveViewModel.saveLocalMonitoringCurrent = true
                    }
                }

                Status.ERROR -> {
                    binding.consError.visibility = View.VISIBLE
                    localMonitoringAdapter.removeList()
                }
            }
        }
    }

    private fun getMonitoringListFirstPage() {
        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = "1",
                page_item_size = PAGE_SIZE,
                object_ids = arrayListOf()
            )
        ).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource?.data?.local_transactions ?: arrayListOf()
                    if (newTotalList.toSet() != response.toSet()) {
                        totalList.clear()
                        successMonitoringList(response, operationType)
                    }
                }

                Status.ERROR -> {
                    binding.consError.visibility = View.VISIBLE
                    localMonitoringAdapter.removeList()
                }
            }
        }
    }

    private fun successMonitoringList(response: ArrayList<LocalMonitoring>?, operationType: Int) {
        try {
            val filteredResponse = when (operationType) {
                0 -> response?.filter { it.transactionType == MONITORING_CREDIT }
                1 -> response?.filter { it.transactionType == MONITORING_DEBIT }
                else -> response
            } ?: emptyList()
            val groupedHashMap = groupDataIntoHashMap(filteredResponse)
            val sortedMap = groupedHashMap.toSortedMap(compareByDescending { it })
            addDateMonitoringList(sortedMap)
        } catch (e: Exception) {
            recordException(e, ::successMonitoringList.name)
        }
    }

    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<LocalMonitoring>>) {
        var lastDate: String? = null
        sortedMap.forEach { (date, monitoringItems) ->
            val formattedLastDate = lastDate?.let {
                Format.newDateFormat((totalList.last() as GeneralItem).localMonitoringItem!!.createdDate).substring(0, 10)
            }
            if (formattedLastDate != date) {
                totalList.add(DateItem().apply { this.date = date })
                lastDate = date
            }
            monitoringItems.forEach { svMonitoringItem ->
                totalList.add(GeneralItem().apply { this.localMonitoringItem = svMonitoringItem })
            }
        }
        setAdapter(totalList)
    }

    private fun setAdapter(totalList: ArrayList<ListItem>) {
        localMonitoringAdapter.setListAdapter(totalList)
        binding.monitoringList.scheduleLayoutAnimation()
        handleEmptyState()
    }

    private fun groupDataIntoHashMap(monitoringList: List<LocalMonitoring>): HashMap<String, MutableList<LocalMonitoring>> {
        return monitoringList.sortedByDescending { it.createdDate }
            .groupByTo(HashMap()) { Format.newDateFormat(it.createdDate.substring(0, 10)) }
            .mapValues { it.value.toMutableList() } as HashMap<String, MutableList<LocalMonitoring>>
    }

    override fun invoke(localMonitoring: LocalMonitoring) {
        getSearchItem(localMonitoring)
    }

    private fun getSearchItem(localMonitoring: LocalMonitoring) {
        showProgress()
        viewModel.getSearchData(getClientToken(), GetInfoRequest(localMonitoring.requestId)).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    val dialogInfo = LocalMonitoringDetailsDialog(
                        localMonitoring,
                        it.data,
                        fullInfo = { localMonitoring ->
                            printCheque(localMonitoring, it)
                        },
                        repeatPayment = { localMonitoring ->
                            getOperationParams(1, localMonitoring)
                        }
                    )
                    dialogInfo.show(childFragmentManager, "")
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString(), "Error")
                }
            }
        }
    }

    private fun printCheque(
        localMonitoring: LocalMonitoring,
        resource: Resource<SearchDataResponse>
    ) {
        showProgress()
        viewModel.printCheque(getClientToken(), PrintChequeRequest(localMonitoring.requestId)).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!
                    response.monitoring_info = localMonitoring
                    if (resource.data?.request_code == "P2P") {
                        resource.data?.let { it1 -> drawTransferCheque(localMonitoring, it1) }
                    } else {
                        gotoWithSlide(
                            R.id.checkInfoPaymentFragment,
                            bundleOf(
                                "details" to response,
                                "operation" to "local",
                                "command" to resource.data?.command,
                                "data" to resource.data,
                                "name" to localMonitoring.name
                            )
                        )
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun drawTransferCheque(
        localMonitoring: LocalMonitoring,
        data: SearchDataResponse
    ) {
        val percent = localMonitoring.feePercent
        val commissionAmount = localMonitoring.feeAmount.toBigDecimal().divide(BigDecimal(100))
        val totalAmount = localMonitoring.amount.toBigDecimal().divide(BigDecimal(100))
        val model = TransferChequeModel(
            transactionDate = localMonitoring.createdDate,
            transactionAmount = Format.formatAmount((data.amount?.toDouble()?.div(100)).toString()) + " " + getString(
                R.string.sum_text
            ),
            transactionFee = "$percent % (" + Format.formatAmount(commissionAmount.toString()) + " " + getString(
                R.string.sum_text
            ) + ")",
            transactionNumber ="",
            senderCardNumber = Format.formatCardNumberForCheque(data.from_object_value.orEmpty()),
            senderCardName = data.from_embossed_name.orEmpty(),
            receiverCardName = data.to_embossed_name.orEmpty(),
            receiverCardNumber = Format.formatCardNumberForCheque(data.to_object_value.orEmpty()),
            operationName = "(${getString(R.string.transfer)})",
            totalAmount = "${uz.fido.utils.utility.format.Format.formatAmount(totalAmount.toString())} ${getString(uz.fido.utils.R.string.sum)}"
        )
        goto(
            R.id.transferChequeFragment2, bundleOf(
                uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.OPERATION to OPERATION_MONITORING,
                CHEQUE_MODEL to model
            )
        )
    }

    private fun getOperationParams(
        operationType: Int,
        localeMonitoring: LocalMonitoring
    ) {
        viewModel.getOperationParams(getClientToken(), GetOperationInfoRequest(localeMonitoring.requestId)).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data
                    if (operationType == 1 || operationType == 2) {
                        repeat(response!!, operationType)
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.operation_could_not_be_performed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun repeat(inParams: InParamsResponse, operationType: Int) {
        when (inParams.service_id) {
            "-1", "-12" -> {
                if (inParams.to_object_value!!.startsWith("AUZ") || inParams.to_object_value!!.startsWith(
                        "DV"
                    )
                )
                    gotoWithSlide(
                        R.id.transferByWalletFragment,
                        bundleOf(
                            "card_number" to if (operationType != 2) inParams.to_object_value else inParams.from_object_value,
                            "amount" to Format.formatAmountFromTiynToInteger(inParams.amount.toString())
                        )
                    )
                else gotoWithSlide(
                    R.id.transferToCardFragment,
                    bundleOf(
                        "card_number" to if (operationType != 2) inParams.to_object_value else inParams.from_object_value,
                        "amount" to Format.formatAmountFromTiynToInteger(inParams.amount.toString())
                    )
                )
            }

            else -> {
                val templateKeyValue = java.util.ArrayList<TemplateKeyValue>()
                if (inParams.params != null)
                    inParams.params!!.forEach {
                        templateKeyValue.add(
                            if (it.key == "AMOUNT")
                                TemplateKeyValue(code = it.key, value = Format.formatAmountFromTiynToInteger(it.value))
                            else TemplateKeyValue(code = it.key, value = it.value)
                        )
                    }
                val bundle = Bundle()
                val service = DatabaseHelper(requireContext()).getServiceByContractId(inParams.service_id.toString())
                bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
                bundle.putSerializable(PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST, templateKeyValue)
                bundle.putInt(PaymentFragment.PAYMENT_OPERATION, PaymentFragment.PAYMENT_OPERATION_TEMPLATE)
                gotoWithSlide(R.id.paymentFragment, bundle)
            }
        }
    }

    private fun handleEmptyState() {
        binding.layoutEmpty.isVisible = totalList.isEmpty()
    }

    companion object {
        const val PAGE_SIZE = "20"
        const val MONITORING_CREDIT = "credit"
        const val MONITORING_DEBIT = "debit"
    }

}




