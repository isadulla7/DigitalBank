package uz.fido.universaldigital.ui.fragments.payment.abc_history

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.InParamsResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentRequisitesHistoryBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.LocalMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.InfoMonitoringDialog
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap

@AndroidEntryPoint
class PaymentHistoryFragment : BaseFragment<FragmentRequisitesHistoryBinding, LocalMonitoringViewModel>(
    FragmentRequisitesHistoryBinding::inflate, LocalMonitoringViewModel::class.java
) {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var localMonitoringAdapter: LocalMonitoringAdapter
    private lateinit var dialogInfo: InfoMonitoringDialog

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var totalList: ArrayList<ListItem> = ArrayList()

    private var paymentService: PaymentService? = null
    private var serviceId: String? = null
    private var dateBegin: String = ""

    companion object {
        const val PAGE_SIZE = "20"
        const val SERVICE_ID = "service_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paymentService = it.serializable("item") as PaymentService?
            serviceId = if (paymentService != null) {
                paymentService?.service_id.toString()
            } else {
                it.getString(SERVICE_ID)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEndlessScrollListener()
        initHistoriesRv()
        getLocalMonitoringList()
        initSetOnClickListeners()
    }

    private fun initEndlessScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(LinearLayoutManager(requireContext())) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getLocalMonitoringListScroll(page)
            }
        }
    }

    private fun initHistoriesRv() {
        localMonitoringAdapter = LocalMonitoringAdapter(requireContext(), totalList) {
            getSearchItem(it)
        }
        binding.histories.apply {
            adapter = localMonitoringAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(localMonitoringAdapter))
        }
    }

    private fun getLocalMonitoringList() {
        totalList.clear()
        val skeletonScreen = showSkeleton(
            binding.shimmerView,
            MibDetailsAdapter(requireContext(), this),
            R.layout.shimmer_item_monitoring,
            1
        )
        scrollListener.resetState()
        viewModel.getLocalMonitoring(
            getClientToken(), LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateFormat.format(Calendar.getInstance().time),
                page_number = "1",
                page_item_size = PAGE_SIZE,
                object_ids = ArrayList(),
                service_id = serviceId
            )
        ).observe(viewLifecycleOwner) { resource ->
            hideShimmerWithDelay(skeletonScreen)
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource?.data?.local_transactions
                    successMonitoringList(response)
                }

                Status.ERROR -> {
                    localMonitoringAdapter.removeList()
                }
            }
        }
    }

    private fun getLocalMonitoringListScroll(page: Int) {
        viewModel.getLocalMonitoring(
            getClientToken(), LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateFormat.format(Calendar.getInstance().time),
                page_number = page.toString(),
                page_item_size = PAGE_SIZE,
                object_ids = ArrayList()
            )
        ).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource?.data?.local_transactions
                    successMonitoringList(response)
                }

                Status.ERROR -> {
                    localMonitoringAdapter.removeList()
                }
            }
        }
    }

    private fun successMonitoringList(response: ArrayList<LocalMonitoring>?) {
        val groupedHashMap: HashMap<String, MutableList<LocalMonitoring>> =
            groupDataIntoHashMap(response!!)
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
                            0, 10
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
        binding.histories.scheduleLayoutAnimation()
        binding.emptyView.isVisible = totalList.isEmpty()
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

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun hideShimmerWithDelay(skeletonScreen: SkeletonScreen) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }, 300)
    }

    private fun getSearchItem(localMonitoring: LocalMonitoring) {
        showProgress()
        viewModel.getSearchData(getClientToken(), GetInfoRequest(localMonitoring.request_id))
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        dialogInfo = InfoMonitoringDialog(
                            localMonitoring,
                            it.data,
                            fullInfo = { localMonitoring ->
                                dialogInfo.dismiss()
                                printCheque(localMonitoring, it)
                            },
                            repeatPayment = { localMonitoring ->
                                getOperationParams(1, localMonitoring)
                            },
                            returnPayment = { localMonitoring ->
                                getOperationParams(2, localMonitoring)
                            })
                        dialogInfo.show(childFragmentManager, "")
                    }

                    Status.ERROR -> {
                        dialogInfo = InfoMonitoringDialog(
                            localMonitoring,
                            null,
                            fullInfo = {}, repeatPayment = {}, returnPayment = {})
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




