package uz.fido.universaldigital.ui.fragments.monitoring.all_card

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.SkeletonScreen
import dagger.hilt.android.AndroidEntryPoint
import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem
import kotlinx.android.synthetic.main.log_out_dialog.view.title
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.InParamsResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
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
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentLocalMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.LocalMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.InfoMonitoringDialog
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap

@AndroidEntryPoint
class LocalMonitoringFragment :
    BaseFragment<FragmentLocalMonitoringBinding, LocalMonitoringViewModel>(
        FragmentLocalMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
    ), (LocalMonitoring) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var dialogInfo: InfoMonitoringDialog

    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var newTotalList = arrayListOf<LocalMonitoring>()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private var listCard = arrayListOf<String>()
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val localMonitoringAdapter by lazy {
        LocalMonitoringAdapter(
            requireContext(),
            totalList,
            this
        )
    }
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)

    companion object {
        const val PAGE_SIZE = "20"
        const val MONITORING_CREDIT = "credit"
        const val MONITORING_DEBIT = "debit"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (saveViewModel.allCardList.value != false)
            allOperation()
        else {
            binding.shimmerView.visibility = View.GONE
            binding.rec.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutEmpty.title.text = getString(R.string.card_list_no)
        }

    }

    private fun allOperation() {
        setTime()
        recyclerViewScroll()
        createMonitoringAdapter()
        checkFilter()
        onClickView()
    }

    private fun checkFilter() {
        if (saveViewModel.localFilter) getFilterLocalMonitoringList(0, operationType)
        else checkLocalMonitoringSave()

    }

    private fun checkLocalMonitoringSave() {
        if (saveViewModel.saveLocalMonitoringCurrent) {
            saveViewModel.saveLocalMonitoring.observe(viewLifecycleOwner) {
                localMonitoringAdapter.removeList()
                newTotalList = it
                successMonitoringList(it, 2)
                getNewListMonitoringList()
                binding.shimmerView.visibility = View.GONE
            }
        } else getLocalMonitoringList(page = 1, operationType)

    }

    private fun getNewListMonitoringList() {
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

    private fun getFilterLocalMonitoringList(page: Int, operationType: Int) {
        saveViewModel.localMonitoringFilter.observe(viewLifecycleOwner) {
            if (it.startDate != "") {
                dateEnd = it.endDate
                dateBegin = it.startDate
            } else setTime()
            val type = when (it.plusMinus) {
                getString(R.string.enrollments) -> 0
                getString(R.string.write_offs) -> 1
                else -> 2
            }
            val cardList = arrayListOf<Int>()
            val cardListCheck = it.cardList.filter { it.is_selected_monitoring }
            if (cardListCheck.isNotEmpty()) {
                it.cardList.forEach { if (!it.is_selected_monitoring) cardList.add(it.object_id) }
            } else {
                it.cardList.forEach { cardList.add(it.object_id) }
            }
            val serviceList = arrayListOf<Int>()

            val serviceIdCheck = it.serviceList.filter { it.service_current }
            val listParentObj = arrayListOf<String>()
            if (serviceIdCheck.isEmpty()) {
                it.serviceList.forEach { serviceList.add(it.service_id!!.toInt()) }
            } else {
                it.serviceList.forEach {
                    if (it.service_current) {
                        serviceList.add(it.service_id!!.toInt())
                        it.list.forEach {
                            listParentObj.add(it.partner_obj)
                        }
                    }
                }

            }
            var skeletonScreen: SkeletonScreen? = null
            if (page == 0) {
                totalList = arrayListOf()
                scrollListener.resetState()
                binding.shimmerView.visibility = View.VISIBLE
                binding.rec.visibility = View.GONE
                skeletonScreen = showSkeleton(
                    binding.shimmerView,
                    MibDetailsAdapter(requireContext(), this),
                    R.layout.shimmer_item_monitoring,
                    1
                )
            } else {
                binding.progress.visibility = View.VISIBLE
            }
            val newFilter = NewFilterMonitoringFilterRequest(
                start_date = if (it.startDate.isNotEmpty()) dateBegin else null,
                end_date = if (it.endDate.isNotEmpty()) dateEnd else null,
                page_number = page,
                page_item_size = 20,
                service_ids = serviceList,
                object_ids = cardList,
                to_object_value = listParentObj,
                max_amount = if (it.maxAmount.isNotEmpty()) "${
                    it.maxAmount.replace(
                        " ",
                        ""
                    )
                }00" else null,
                min_amount = if (it.minAmount.isNotEmpty()) "${
                    it.minAmount.replace(
                        " ",
                        ""
                    )
                }00" else null
            )
            viewModel.newFilterLocalMonitoring(getClientToken(), newFilter)
                .observe(viewLifecycleOwner) {
                    if (page == 0) {
                        skeletonScreen!!.hide()
                        binding.shimmerView.visibility = View.GONE
                        binding.rec.visibility = View.VISIBLE
                    } else {
                        binding.progress.visibility = View.GONE
                    }
                    when (it.status) {
                        Status.SUCCESS -> {
                            val response = it.data!!.local_transactions
                            successMonitoringList(response, type)

                        }

                        Status.ERROR -> {
                            localMonitoringAdapter.removeList()
                            binding.consError.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }

    private fun onClickView() {
        binding.gotoMainPage.setOnClickListener {
            totalList = ArrayList()
            binding.consError.visibility = View.GONE
            binding.shimmerView.visibility = View.VISIBLE
            if (saveViewModel.localFilter) getFilterLocalMonitoringList(0, operationType)
            else getLocalMonitoringList(page = 1, operationType)

        }
    }

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
    }

    private fun setTime() {
        val calendarEnd = Calendar.getInstance()
        dateBegin = ""
        dateEnd = df.format(calendarEnd.time)
    }

    private fun recyclerViewScroll() {
        scrollListener =
            object : EndlessRecyclerViewScrollListener(LinearLayoutManager(requireContext())) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    if (saveViewModel.localFilter)
                        getFilterLocalMonitoringList(page, operationType)
                    else
                        getLocalMonitoringListScroll(page, operationType)
                }
            }
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = localMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(localMonitoringAdapter))
        }
    }

    private fun getLocalMonitoringList(page: Int, operationType: Int) {
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
                page_number = page.toString(),
                page_item_size = PAGE_SIZE,
                object_ids = listCard
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

    fun getLocalMonitoringListScroll(page: Int, operationType: Int) {
        binding.progress.visibility = View.VISIBLE
        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = page.toString(),
                page_item_size = PAGE_SIZE,
                object_ids = listCard
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
        binding.rec.scheduleLayoutAnimation()
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
                        dialogInfo = InfoMonitoringDialog(
                            requireContext(),
                            localMonitoring,
                            it.data,
                            object : BaseInterface {
                                override fun repeatPayment(localeMonitoring: LocalMonitoring) {
                                    super.repeatPayment(localeMonitoring)
                                    getOperationParams(1, it, localeMonitoring)
                                }

                                override fun returnPayment(localeMonitoring: LocalMonitoring) {
                                    super.returnPayment(localeMonitoring)
                                    getOperationParams(2, it, localeMonitoring)

                                }

                                override fun fullInfo(localeMonitoring: LocalMonitoring) {
                                    super.fullInfo(localeMonitoring)
                                    dialogInfo.dismiss()
                                    printCheque(localMonitoring, it)

                                }
                            })
                        dialogInfo.show(childFragmentManager, "")
                    }

                    Status.ERROR -> {
                        dialogInfo = InfoMonitoringDialog(
                            requireContext(),
                            localMonitoring,
                            null,
                            object : BaseInterface {

                            })
                        dialogInfo.show(childFragmentManager, "")
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
                        gotoWithSlide(
                            R.id.checkInfoPaymentFragment,
                            bundleOf(
                                "details" to response,
                                "operation" to "local",
                                "command" to resource.data?.command
                            )
                        )
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }

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
                    dialogInfo.dismiss()
                    val response = it.data
                    if (operationType == 1 || operationType == 2)
                        repeat(response!!, operationType)
                    else {
                        //  if (response!!.params != null)
                        //    saveTemplate(inParams = response, localeMonitoring)
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
                                TemplateKeyValue(
                                    code = it.key,
                                    value = Format.formatAmountFromTiynToInteger(it.value)
                                )
                            else TemplateKeyValue(code = it.key, value = it.value)
                        )
                    }
                val bundle = Bundle()
                val service =
                    DatabaseHelper(requireContext()).getServiceByContractId(inParams.service_id.toString())
                bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
                bundle.putSerializable(
                    PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST,
                    templateKeyValue
                )
                bundle.putInt(
                    PaymentFragment.PAYMENT_OPERATION,
                    PaymentFragment.PAYMENT_OPERATION_TEMPLATE
                )
                gotoWithSlide(R.id.paymentFragment, bundle)
            }
        }
    }

}




