package uz.fido.universaldigital.ui.fragments.monitoring.local

import android.os.Bundle
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
import uz.fido.network.domain.model.monitoring.filter.NewFilterMonitoringFilterRequest
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
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.LocalMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.Companion.CHEQUE_MODEL
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeFragment.Companion.OPERATION_MONITORING
import uz.fido.universaldigital.ui.fragments.monitoring.cheque.TransferChequeModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.InfoMonitoringDialog
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
    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var newTotalList = arrayListOf<LocalMonitoring>()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val localMonitoringAdapter by lazy { LocalMonitoringAdapter(requireContext(), totalList, this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserCards()
    }

    private fun checkUserCards() {
        lifecycleScope.launch {
            saveViewModel.userHasCard.collect { hasCard ->
                if (hasCard) initUI()
                else showEmptyState()
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
        dateBegin = endDate.orEmpty()
        dateEnd = startDate ?: dateFormat.format(calendarEnd.time)
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(LinearLayoutManager(requireContext())) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (saveViewModel.localFilter) getFilteredMonitoringList(page)
                else getMonitoringListWhenScrolled(page, operationType)
            }
        }
    }

    private fun initRecyclerView() {
        binding.monitoringList.apply {
            adapter = localMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
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
            else getMonitoringListFirstPage(operationType)
        }
    }

    private fun showEmptyState() {
        binding.shimmerView.visibility = View.GONE
        binding.monitoringList.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.layoutEmpty.findViewById<TextViewMedium>(R.id.title).text = getString(R.string.card_list_no)
    }

    private fun getFilteredMonitoringList(page: Int) {
        lifecycleScope.launch {
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
                val newFilter = NewFilterMonitoringFilterRequest(
                    start_date = if (filter.startDate.isNotEmpty()) dateBegin else null,
                    end_date = if (filter.endDate.isNotEmpty()) dateEnd else null,
                    page_number = page,
                    page_item_size = 20,
                    service_ids = filter.getServiceIdsAndPartnerObj().first,
                    object_ids = filter.getSelectedCards(),
                    to_object_value = filter.getServiceIdsAndPartnerObj().second,
                    max_amount = filter.getMaxAmount(),
                    min_amount = filter.getMinMinAmount()
                )
                viewModel.newFilterLocalMonitoring(getClientToken(), newFilter).observe(viewLifecycleOwner) {
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
        lifecycleScope.launch {
            saveViewModel.saveLocalMonitoring.collect { localMonitoring ->
                if (localMonitoring.isNotEmpty()) {
                    localMonitoringAdapter.removeList()
                    newTotalList = localMonitoring
                    successMonitoringList(localMonitoring, 2)
                    getFreshMonitoringList()
                    binding.shimmerView.visibility = View.GONE
                } else {
                    getMonitoringListFirstPage(operationType)
                }
            }
        }
    }

    private fun getMonitoringListFirstPage(operationType: Int) {
        val skeletonScreen = showSkeleton(
            binding.shimmerView,
            MibDetailsAdapter(requireContext(), this),
            R.layout.shimmer_item_monitoring,
            1
        )
        totalList = arrayListOf()
        scrollListener.resetState()
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
            skeletonScreen.hide()
            binding.shimmerView.visibility = View.GONE
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.consError.visibility = View.GONE
                    val response = resource?.data?.local_transactions ?: arrayListOf()
                    saveViewModel.saveLocalMonitoring(response)
                    saveViewModel.saveLocalMonitoringCurrent = true
                    successMonitoringList(response, operationType)
                }

                Status.ERROR -> {
                    binding.consError.visibility = View.VISIBLE
                    localMonitoringAdapter.removeList()
                }

            }
        }
    }

    private fun getFreshMonitoringList() {
        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = "1",
                page_item_size = PAGE_SIZE,
                object_ids = ArrayList()
            )
        ).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource?.data?.local_transactions ?: arrayListOf()
                    if (!(newTotalList.containsAll(response) && response.containsAll(newTotalList))) {
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

    private fun getMonitoringListWhenScrolled(page: Int, operationType: Int) {
        binding.progress.visibility = View.VISIBLE
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
            binding.progress.visibility = View.GONE
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.consError.visibility = View.GONE
                    val response = resource?.data?.local_transactions
                    successMonitoringList(response, operationType)
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
            val sortedResponse = ArrayList<LocalMonitoring>()
            val groupedHashMap: HashMap<String, MutableList<LocalMonitoring>> = when (operationType) {
                0 -> {
                    response?.forEach {
                        if (it.tran_type == MONITORING_CREDIT) {
                            sortedResponse.add(it)
                        }
                    }
                    groupDataIntoHashMap(sortedResponse)
                }

                1 -> {
                    response?.forEach {
                        if (it.tran_type == MONITORING_DEBIT) {
                            sortedResponse.add(it)
                        }
                    }
                    groupDataIntoHashMap(sortedResponse)
                }

                else -> {
                    groupDataIntoHashMap(response!!)
                }
            }
            val sortedMap = groupedHashMap.toSortedMap(compareByDescending { it })
            addDateMonitoringList(sortedMap)
        } catch (e: Exception) {
            recordException(e, ::successMonitoringList.name)
        }
    }

    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<LocalMonitoring>>) {
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != Format.newDateFormat((totalList.last() as GeneralItem).svMonitoringItem!!.created_date)
                        .substring(
                            0,
                            10
                        )
                ) totalList.add(dateItem)
            }
            for (svMonitoringItem in sortedMap[date]!!) {
                val generalItem = GeneralItem()
                generalItem.svMonitoringItem = svMonitoringItem
                totalList.add(generalItem)
            }
        }
        setAdapter(totalList)
    }

    private fun setAdapter(totalList: ArrayList<ListItem>) {
        localMonitoringAdapter.setListAdapter(totalList)
        binding.monitoringList.scheduleLayoutAnimation()
        emptyView()
    }

    private fun groupDataIntoHashMap(svMonitoringList: List<LocalMonitoring>): HashMap<String, MutableList<LocalMonitoring>> {
        svMonitoringList.sortedBy { it.created_date }
        val groupedHashMap: HashMap<String, MutableList<LocalMonitoring>> = HashMap()
        for (svMonitoring in svMonitoringList) {
            val hashMapKey: String =
                Format.newDateFormat(svMonitoring.created_date.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(svMonitoring)
            } else {
                val list: MutableList<LocalMonitoring> = java.util.ArrayList()
                list.add(svMonitoring)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    override fun invoke(localMonitoring: LocalMonitoring) {
        getSearchItem(localMonitoring)
    }

    private fun getSearchItem(localMonitoring: LocalMonitoring) {
        showProgress()
        viewModel.getSearchData(getClientToken(), GetInfoRequest(localMonitoring.request_id))
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        val dialogInfo = InfoMonitoringDialog(
                            localMonitoring,
                            it.data,
                            fullInfo = { localMonitoring ->
                                printCheque(localMonitoring, it)
                            },
                            repeatPayment = { localMonitoring ->
                                getOperationParams(1, it, localMonitoring)
                            },
                            returnPayment = { localMonitoring ->
                                getOperationParams(2, it, localMonitoring)
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
        viewModel.printCheque(getClientToken(), PrintChequeRequest(localMonitoring.request_id))
            .observe(viewLifecycleOwner) {
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
        val percent = localMonitoring.fee_percent
        val commissionAmount = localMonitoring.fee_amount.toBigDecimal().divide(BigDecimal(100))
        val totalAmount = localMonitoring.amount.toBigDecimal().divide(BigDecimal(100))
        val model = TransferChequeModel(
            transactionDate = localMonitoring.created_date,
            transactionAmount = Format.formatAmount((data.amount?.toDouble()?.div(100)).toString()) + " " + getString(
                R.string.sum_text
            ),
            transactionFee = "$percent % (" + Format.formatAmount(commissionAmount.toString()) + " " + getString(
                R.string.sum_text
            ) + ")",
            transactionNumber = data.request_id.orEmpty(),
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
        resource: Resource<SearchDataResponse>,
        localeMonitoring: LocalMonitoring
    ) {
        viewModel.getOperationParams(
            getClientToken(),
            GetOperationInfoRequest(request_id = localeMonitoring.request_id)
        ).observe(viewLifecycleOwner) {
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

    private fun emptyView() {
        binding.layoutEmpty.isVisible = totalList.isEmpty()
    }

    companion object {
        const val PAGE_SIZE = "20"
        const val MONITORING_CREDIT = "credit"
        const val MONITORING_DEBIT = "debit"
    }

}




