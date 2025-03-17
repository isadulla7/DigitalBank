package uz.fido.universaldigital.ui.fragments.monitoring.uzcard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.UzcardItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentUzcardMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.UzCardMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.utils.format.Format
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap

@AndroidEntryPoint
class UzcardMonitoringFragment : BaseFragment<FragmentUzcardMonitoringBinding, LocalMonitoringViewModel>(
    FragmentUzcardMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
), (UzcardMonitoringItem) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var cardList = arrayListOf<String>()
    private val uzcardMonitoringAdapter by lazy {
        UzcardMonitoringAdapter(
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalList.clear()
        initializeUzcardList()
        initMonitoringDate()
        initScrollListener()
        setupMonitoringRecyclerView()
        checkForFilter()
        initSetOnClickListeners()
    }

    private fun initializeUzcardList() {
        cardList = menuMonitoringViewModel.uzcardList.value ?: arrayListOf()
    }

    private fun initMonitoringDate() {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val calendarEnd = Calendar.getInstance()
        dateBegin = ""
        dateEnd = dateFormat.format(calendarEnd.time)
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(LinearLayoutManager(requireContext())) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (menuMonitoringViewModel.uzCardFilter)
                    getFilteredMonitoringList(page)
                else getUzCardMonitoringListScroll(page, operationType)
            }
        }
    }

    private fun setupMonitoringRecyclerView() {
        binding.monitoringList.apply {
            adapter = uzcardMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(uzcardMonitoringAdapter))
        }
    }

    private fun checkForFilter() {
        if (menuMonitoringViewModel.uzCardFilter)
            getFilteredMonitoringList(1)
        else {
            getUzCardMonitoringList(operationType)
        }
    }

    private fun initSetOnClickListeners() {
        binding.refreshButton.setOnClickListener {
            totalList = ArrayList()
            binding.consError.visibility = View.GONE
            binding.shimmerView.visibility = View.VISIBLE
            getUzCardMonitoringList(operationType)
        }
    }

    private fun getFilteredMonitoringList(page: Int) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        menuMonitoringViewModel.uzCardMonitoringFilter.observe(viewLifecycleOwner) { it ->
            val card = it.cardList.filter { !it.is_selected_monitoring }.map { it.object_id.toString() }
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            if (it.startDate != "") {
                dateEnd = dateFormat.format(format.parse(it.endDate)?.time ?: "")
                dateBegin = dateFormat.format(format.parse(it.startDate)?.time ?: "")
            } else initMonitoringDate()
            val type = when (it.plusMinus) {
                getString(R.string.enrollments) -> 0
                getString(R.string.write_offs) -> 1
                else -> 2
            }
            val model = UzcardMonitoringRequest(
                startDate = dateBegin,
                endDate = dateEnd,
                pageNumber = page.toString(),
                pageItemSize = LocalMonitoringFragment.PAGE_SIZE,
                selectedCards = card as ArrayList<String>,
            )
            var skeletonScreen: SkeletonScreen? = null
            if (page == 1) {
                totalList = arrayListOf()
                skeletonScreen = showSkeleton(
                    binding.shimmerView,
                    MibDetailsAdapter(requireContext(), this),
                    R.layout.shimmer_item_monitoring,
                    1
                )
                scrollListener.resetState()
            } else {
                binding.progress.visibility = View.VISIBLE
            }
            viewModel.getUzcardMonitoringOld(getClientToken(), model).observe(viewLifecycleOwner) {
                if (page == 1) {
                    skeletonScreen?.hide()
                } else {
                    binding.progress.visibility = View.GONE
                }
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data?.transactions ?: arrayListOf()
                        successMonitoringList(response, type)
                    }

                    Status.ERROR -> {
                        uzcardMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getUzCardMonitoringList(operationType: Int) {
        if (cardList.isNotEmpty()) {
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            scrollListener.resetState()
            viewModel.getUzcardMonitoringOld(
                token = getClientToken(),
                UzcardMonitoringRequest(
                    startDate = dateBegin,
                    endDate = dateEnd,
                    pageNumber = "1",
                    pageItemSize = "20",
                    selectedCards = cardList
                )
            ).observe(viewLifecycleOwner) { resources ->
                skeletonScreen.hide()
                when (resources.status) {
                    Status.SUCCESS -> {
                        val response = resources?.data?.transactions ?: ArrayList()
                        successMonitoringList(response, operationType)
                    }

                    Status.ERROR -> {
                        uzcardMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isVisible) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.layoutEmpty.findViewById<TextViewMedium>(R.id.title).text = getString(R.string.card_list_no)
                }
            }, 500)
        }
    }

    private fun getUzCardMonitoringListScroll(page: Int, operationType: Int) {
        binding.progress.visibility = View.VISIBLE
        viewModel.getUzcardMonitoringOld(
            token = getClientToken(),
            UzcardMonitoringRequest(
                startDate = dateBegin,
                endDate = dateEnd,
                pageNumber = page.toString(),
                pageItemSize = LocalMonitoringFragment.PAGE_SIZE,
                selectedCards = cardList
            )
        ).observe(viewLifecycleOwner) { resources ->
            binding.progress.visibility = View.GONE
            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources?.data?.transactions
                    successMonitoringList(response, operationType)
                }

                Status.ERROR -> {
                    uzcardMonitoringAdapter.removeList()
                    binding.consError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun successMonitoringList(response: ArrayList<UzcardMonitoringItem>?, operationType: Int) {
        val sortedResponse = ArrayList<UzcardMonitoringItem>()
        val groupedHashMap: HashMap<String, MutableList<UzcardMonitoringItem>> = when (operationType) {
            0 -> {
                response?.forEach {
                    if (it.transactionType == LocalMonitoringFragment.MONITORING_CREDIT) {
                        sortedResponse.add(it)
                    }
                }
                groupDataIntoHashMap(sortedResponse)
            }

            1 -> {
                response?.forEach {
                    if (it.transactionType == LocalMonitoringFragment.MONITORING_DEBIT) {
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

    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<UzcardMonitoringItem>>) {
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != Format.newDateFormat((totalList.last() as UzcardItem).uzcardMonitoringItem!!.transactionDate).substring(0, 10)) totalList.add(dateItem)
            }
            for (svMonitoringItem in sortedMap[date]!!) {
                val uzcardItem = UzcardItem()
                uzcardItem.uzcardMonitoringItem = svMonitoringItem
                totalList.add(uzcardItem)
            }
        }
        updateListItems(totalList)
    }

    private fun updateListItems(totalList: ArrayList<ListItem>) {
        uzcardMonitoringAdapter.setListAdapter(totalList)
        binding.monitoringList.scheduleLayoutAnimation()
        handleEmptyState()
    }

    private fun groupDataIntoHashMap(svMonitoringList: List<UzcardMonitoringItem>): HashMap<String, MutableList<UzcardMonitoringItem>> {
        svMonitoringList.sortedBy { it.transactionDate }
        val groupedHashMap: HashMap<String, MutableList<UzcardMonitoringItem>> = HashMap()
        for (svMonitoring in svMonitoringList) {
            val hashMapKey: String = Format.newDateFormat(svMonitoring.transactionDate.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(svMonitoring)
            } else {
                val list: MutableList<UzcardMonitoringItem> = ArrayList()
                list.add(svMonitoring)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    private fun handleEmptyState() {
        binding.layoutEmpty.isVisible = totalList.isEmpty()
    }

    override fun invoke(item: UzcardMonitoringItem) {
        val dialogInfo = UzCardMonitoringDetailsDialog(item, object : BaseInterface {
            override fun uzCardInfo(uzcardMonitoringItem: UzcardMonitoringItem) {
                super.uzCardInfo(uzcardMonitoringItem)
                gotoWithSlide(
                    R.id.checkInfoPaymentFragment,
                    bundleOf("uzcard" to item, "operation" to "uzcard")
                )
            }
        })
        dialogInfo.show(childFragmentManager, "")
    }

}