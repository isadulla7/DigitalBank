package uz.fido.universaldigital.ui.fragments.monitoring.one_card_monitoring

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
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.UzcardItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentUzcardFirstMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.uzcard.UzCardMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.monitoring.uzcard.UzcardMonitoringAdapter
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
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap

@AndroidEntryPoint
class OneUzCardMonitoringFragment : BaseFragment<FragmentUzcardFirstMonitoringBinding, LocalMonitoringViewModel>(
    FragmentUzcardFirstMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
), (UzcardMonitoringItem) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var dialogInfo: UzCardMonitoringDetailsDialog
    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var choose: Int = 2
    private var timeType = ""
    private var filter: Boolean = false
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val uzcardMonitoringAdapter by lazy {
        UzcardMonitoringAdapter(
            totalList,
            this
        )
    }
    private var linearLayoutManager: LinearLayoutManager? = null
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val df = SimpleDateFormat("yyyyMMdd", Locale.US)
    private var cardList = arrayListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageFirst()
        linearLayoutManager = LinearLayoutManager(requireContext())
        totalList.clear()
        getCardList()
        setTime()
        initScrollListener()
        createMonitoringAdapter()
        checkSaveItem()
        //checkFilter()
        onClickView()

    }

    private fun setImageFirst() {
        binding.appBar.setAdditionalIcon(R.drawable.ic_filter_frame)
    }

    private fun checkSaveItem() {
        getUzCardMonitoringList(1, operationType)
    }

    private fun getFilterUzCardMonitoringList(page: Int) {
        var skeletonScreen: SkeletonScreen? = null
        if (page == 1) {
            binding.rec.visibility = View.GONE
            binding.shimmerView.visibility = View.VISIBLE
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
        val type = choose
        val model = UzcardMonitoringRequest(
            startDate = dateBegin,
            endDate = dateEnd,
            pageNumber = page.toString(),
            pageItemSize = LocalMonitoringFragment.PAGE_SIZE,
            selectedCards = cardList
        )

        viewModel.getUzcardMonitoringOld(getClientToken(), model).observe(viewLifecycleOwner) {
            if (page == 1) {
                skeletonScreen!!.hide()
                binding.shimmerView.visibility = View.GONE
                binding.rec.visibility = View.VISIBLE
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

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnClickListener {
            val filterDialog = MonitoringSimpleFilterDialog(
                choose, dateBegin, dateEnd, timeType,
                onClickItem = { newChoose, startDate, endDate, type ->
                    choose = newChoose
                    dateBegin = startDate
                    dateEnd = endDate
                    timeType = type
                    filter = true
                    getFilterUzCardMonitoringList(1)
                    binding.appBar.setAdditionalIcon(R.drawable.ic_filter_yes)
                },
                clear = {
                    choose = 2
                    dateBegin = ""
                    dateEnd = ""
                    timeType = ""
                    filter = false
                    setTime()
                    totalList.clear()
                    setAdapter(totalList)
                    binding.shimmerView.visibility = View.VISIBLE
                    checkSaveItem()
                    setImageFirst()
                }
            )
            filterDialog.show(childFragmentManager, "")
        }
        binding.gotoMainPage.setOnClickListener {
            totalList = ArrayList()
            binding.consError.visibility = View.GONE
            binding.shimmerView.visibility = View.VISIBLE
            getUzCardMonitoringList(page = 1, operationType)

        }
    }

    private fun getCardList() {
        val card = arguments?.serializable<CardResponse>(Const.CARD)
        cardList.add(card?.object_id ?: "")
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (saveViewModel.uzCardFilter) getFilterUzCardMonitoringList(page)
                else getUzCardMonitoringListScroll(page, operationType)
            }
        }
    }

    private fun setTime() {
        val calendarEnd = Calendar.getInstance()
        dateBegin = ""
        dateEnd = df.format(calendarEnd.time)
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = uzcardMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(uzcardMonitoringAdapter))
        }
    }

    private fun getUzCardMonitoringList(page: Int, operationType: Int) {
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
                    pageNumber = page.toString(),
                    pageItemSize = "2",
                    selectedCards = cardList
                )
            ).observe(viewLifecycleOwner) { resources ->
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                binding.rec.visibility = View.VISIBLE
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
                    binding.shimmerView.visibility = View.GONE
                    binding.rec.visibility = View.GONE
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
                if (dateItem.date != Format.newDateFormat((totalList.last() as UzcardItem).uzcardMonitoringItem!!.transactionDate)
                        .substring(
                            0,
                            10
                        )
                ) totalList.add(dateItem)
            }
            for (svMonitoringItem in sortedMap[date]!!) {
                val uzcardItem = UzcardItem()
                uzcardItem.uzcardMonitoringItem = svMonitoringItem
                totalList.add(uzcardItem)
            }
        }
        setAdapter(totalList)
    }

    private fun setAdapter(totalList: ArrayList<ListItem>) {
        uzcardMonitoringAdapter.setListAdapter(totalList)
        binding.rec.scheduleLayoutAnimation()
        emptyView()

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

    private fun emptyView() {
        binding.layoutEmpty.isVisible = totalList.isEmpty()
    }

    override fun invoke(item: UzcardMonitoringItem) {
        dialogInfo = UzCardMonitoringDetailsDialog(item, object : BaseInterface {
            override fun uzCardInfo(uzcardMonitoringItem: UzcardMonitoringItem) {
                super.uzCardInfo(uzcardMonitoringItem)
                dialogInfo.dismiss()
                gotoWithSlide(
                    R.id.checkInfoPaymentFragment,
                    bundleOf("uzcard" to item, "operation" to "uzcard")
                )

            }
        })
        dialogInfo.show(childFragmentManager, "")
    }


}