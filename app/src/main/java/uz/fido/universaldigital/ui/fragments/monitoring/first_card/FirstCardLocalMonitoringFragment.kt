package uz.fido.universaldigital.ui.fragments.monitoring.first_card

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.InParamsResponse
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentFirstCardLocalMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.LocalMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.InfoMonitoringDialog
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
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
class FirstCardLocalMonitoringFragment :
    BaseFragment<FragmentFirstCardLocalMonitoringBinding, LocalMonitoringViewModel>
        (FragmentFirstCardLocalMonitoringBinding::inflate, LocalMonitoringViewModel::class.java),
        (LocalMonitoring) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var dialogInfo: InfoMonitoringDialog
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var totalList: ArrayList<ListItem> = ArrayList()
    private var listCard = arrayListOf<String>()
    private val localMonitoringAdapter by lazy {
        LocalMonitoringAdapter(
            requireContext(),
            totalList,
            this
        )
    }
    private var linearLayoutManager: LinearLayoutManager? = null
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val card = arguments?.serializable<CardResponse>(Const.CARD)
        listCard.add(card!!.object_id)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.appBar.setOnBackButtonClickListener { pop() }
        setTime()
        recyclerViewScroll()
        createMonitoringAdapter()
        getLocalMonitoringList(1, 2)

    }

    private fun recyclerViewScroll() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                //  getLocalMonitoringListScroll(page, operationType)
            }
        }
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = localMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(localMonitoringAdapter))
        }
    }

    private fun getLocalMonitoringList(page: Int, operationType: Int) {
        var skeletonScreen: SkeletonScreen? = null
        if (page == 1) {
            totalList = arrayListOf()
            scrollListener.resetState()
            skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
        } else {
            binding.progress.visibility = View.VISIBLE

        }


        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = page.toString(),
                page_item_size = LocalMonitoringFragment.PAGE_SIZE,
                object_ids = listCard
            )
        ).observe(viewLifecycleOwner) { resource ->
            if (page == 1) {
                skeletonScreen!!.hide()
                binding.shimmerView.visibility = View.GONE
            } else {
                binding.progress.visibility = View.GONE
            }
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.consError.visibility = View.GONE
                    val response = resource?.data?.local_transactions ?: arrayListOf()
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
                    if (it.tran_type == LocalMonitoringFragment.MONITORING_CREDIT) {
                        sortedResponse.add(it)
                    }
                }
                groupDataIntoHashMap(sortedResponse)
            }

            1 -> {
                response?.forEach {
                    if (it.tran_type == LocalMonitoringFragment.MONITORING_DEBIT) {
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

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
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

    private fun setTime() {
        val calendarEnd = Calendar.getInstance()
        dateBegin = ""
        dateEnd = df.format(calendarEnd.time)
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
}