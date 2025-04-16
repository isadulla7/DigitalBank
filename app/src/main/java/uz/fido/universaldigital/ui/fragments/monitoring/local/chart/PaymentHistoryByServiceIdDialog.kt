package uz.fido.universaldigital.ui.fragments.monitoring.local.chart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentRequisitesHistoryBinding
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.hideProgress
import uz.fido.universaldigital.ui.utils.extensions.showProgress
import uz.fido.utils.format.Format
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.user.getClientToken
import java.util.SortedMap

@AndroidEntryPoint
class PaymentHistoryByServiceIdDialog(private val paymentServiceId: String, private val period: Pair<String, String>) : DialogFragment(), BaseInterface {

    private lateinit var binding: FragmentRequisitesHistoryBinding
    private val viewModel by activityViewModels<LocalMonitoringViewModel>()

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var localMonitoringAdapter: LocalMonitoringAdapter
    private lateinit var dialogInfo: LocalMonitoringDetailsDialog
    private var totalList: ArrayList<ListItem> = ArrayList()
    private lateinit var skeleton: SkeletonScreen

    companion object {
        const val PAGE_SIZE = "100"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, uz.fido.utils.R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequisitesHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEndlessScrollListener()
        initHistoriesRv()
        getLocalMonitoringList(1)
        initSetOnClickListeners()
    }

    private fun initEndlessScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(LinearLayoutManager(requireContext())) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getLocalMonitoringList(page)
            }
        }
    }

    private fun initHistoriesRv() {
        localMonitoringAdapter = LocalMonitoringAdapter(totalList) {
            getSearchItem(it)
        }
        binding.histories.apply {
            adapter = localMonitoringAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(localMonitoringAdapter))
        }
    }

    private fun getLocalMonitoringList(page: Int) {
        if (page == 1) {
            totalList.clear()
            skeleton = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            scrollListener.resetState()
        }
        viewModel.getLocalMonitoring(
            getClientToken(), LocalMonitoringRequest(
                start_date = period.first,
                end_date = period.second,
                page_number = page.toString(),
                page_item_size = PAGE_SIZE,
                object_ids = ArrayList(),
                service_id = paymentServiceId
            )
        ).observe(viewLifecycleOwner) { resource ->
            hideShimmerWithDelay(skeleton)
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
                if (dateItem.date != Format.newDateFormat((totalList.last() as GeneralItem).localMonitoringItem!!.createdDate)
                        .substring(
                            0, 10
                        )
                ) totalList.add(dateItem)
            }
            for (svMonitoringItem in sortedMap[date]!!) {
                val generalItem = GeneralItem()
                generalItem.localMonitoringItem = svMonitoringItem
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
        svMonitoringList.sortedBy { it.createdDate }
        val groupedHashMap: HashMap<String, MutableList<LocalMonitoring>> = HashMap()
        for (svMonitoring in svMonitoringList) {
            val hashMapKey: String =
                Format.newDateFormat(svMonitoring.createdDate.substring(0, 10))
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
        binding.appBar.setOnBackButtonClickListener { dismiss() }
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
        showProgress(requireActivity())
        viewModel.getSearchData(getClientToken(), GetInfoRequest(localMonitoring.requestId)).observe(viewLifecycleOwner) {
            hideProgress(requireActivity())
            when (it.status) {
                Status.SUCCESS -> {
                    dialogInfo = LocalMonitoringDetailsDialog(localMonitoring, it.data)
                    dialogInfo.show(childFragmentManager, "")
                }

                Status.ERROR -> {
                    dialogInfo = LocalMonitoringDetailsDialog(localMonitoring)
                    dialogInfo.show(childFragmentManager, "")
                }
            }
        }
    }

}




