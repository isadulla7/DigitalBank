package uz.fido.universaldigital.ui.fragments.monitoring.first_card

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.log_out_dialog.view.title
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.UzcardItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentUzcardFirstMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.SvMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringAllCardDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.UzCardMonitoringDetailsDialog
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
class FirstUzCardMonitoringFragment:BaseFragment<FragmentUzcardFirstMonitoringBinding, LocalMonitoringViewModel>(
    FragmentUzcardFirstMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
), (SVMonitoringItem) -> Unit {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var dialogInfo: UzCardMonitoringDetailsDialog
    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var choose:Int=2
    private var timeType=""
    private var filter:Boolean=false
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val svMonitoringAdapter by lazy {
        SvMonitoringAdapter(
            requireContext(),
            totalList,
            this
        )
    }
    private var linearLayoutManager: LinearLayoutManager? = null
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val df = SimpleDateFormat("yyyyMMdd", Locale.US)
    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var cardList = arrayListOf<String>()
    private lateinit var filterDialog:MonitoringAllCardDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageFirst()
        linearLayoutManager = LinearLayoutManager(requireContext())
        totalList.clear()
        getCardList()
        setTime()
        recylerViewScroll()
        createMonitoringAdapter()
        checkSaveItem()
        //checkFilter()
        onClickView()

    }

    private fun setImageFirst() {
        binding.appBar.setAdditionalIcon(R.drawable.ic_filter_frame)
    }


   /* private fun checkFilter() {
        if (filter)
            getFilterUzCardMonitoringList(1, operationType)
        else {
            checkSaveItem()
        }

    }*/

    private fun checkSaveItem() {
        getUzCardMonitoringList(1, operationType)
    }


    private fun getFilterUzCardMonitoringList(page: Int, operationType: Int) {
        var skeletonScreen: SkeletonScreen? = null
        if (page == 1) {
            binding.rec.visibility=View.GONE
            binding.shimmerView.visibility=View.VISIBLE
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
        val model = SVMonitoringRequest(
            start_date = dateBegin,
            end_date = dateEnd,
            page_number = page.toString(),
            page_item_size = LocalMonitoringFragment.PAGE_SIZE,
            from_object_ids = cardList
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
                    svMonitoringAdapter.removeList()
                    binding.consError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnClickListener {
            filterDialog=MonitoringAllCardDialog(choose,dateBegin,dateEnd,timeType,
                onClickItem = {newChoose,startDate,endDate,type->
                    choose=newChoose
                    dateBegin=startDate
                    dateEnd=endDate
                    timeType=type
                    filter=true
                    getFilterUzCardMonitoringList(1, operationType)
                    binding.appBar.setAdditionalIcon(R.drawable.ic_filter_yes)
                    filterDialog.dismiss()
                },
                clear = {
                    choose=2
                    dateBegin=""
                    dateEnd=""
                    timeType=""
                    filter=false
                    setTime()
                    totalList.clear()
                    setAdapter(totalList)
                    binding.shimmerView.visibility=View.VISIBLE
                    checkSaveItem()
                    setImageFirst()
                    filterDialog.dismiss()
                }
            )
            filterDialog.show(childFragmentManager,"")
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
        cardList.add(card?.object_id?:"")

    }

    private fun recylerViewScroll() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (saveViewModel.uzCardFilter)
                    getFilterUzCardMonitoringList(page, operationType)
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
            adapter = svMonitoringAdapter
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(svMonitoringAdapter))

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
                SVMonitoringRequest(
                    start_date = dateBegin,
                    end_date = dateEnd,
                    page_number = page.toString(),
                    page_item_size = "2",
                    from_object_ids = cardList
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
                        svMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isVisible) {
                    // skeletonScreen.hide()
                    binding.shimmerView.visibility = View.GONE
                    binding.rec.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.layoutEmpty.title.text = getString(R.string.card_list_no)

                }
            }, 500)
        }
    }

    private fun getUzCardMonitoringListScroll(page: Int, operationType: Int) {
        binding.progress.visibility = View.VISIBLE
        viewModel.getUzcardMonitoringOld(
            token = getClientToken(),
            SVMonitoringRequest(
                start_date = dateBegin,
                end_date = dateEnd,
                page_number = page.toString(),
                page_item_size = LocalMonitoringFragment.PAGE_SIZE,
                from_object_ids = cardList
            )
        ).observe(viewLifecycleOwner) { resources ->
            binding.progress.visibility = View.GONE

            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources?.data?.transactions
                    successMonitoringList(response, operationType)
                }

                Status.ERROR -> {
                    svMonitoringAdapter.removeList()
                    binding.consError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun successMonitoringList(response: ArrayList<SVMonitoringItem>?, operationType: Int) {
        val sortedResponse = ArrayList<SVMonitoringItem>()
        Log.d("TAG", "successMonitoringList:${operationType} ")
        val groupedHashMap: HashMap<String, MutableList<SVMonitoringItem>> = when (operationType) {
            0 -> {
                response?.forEach {
                    Log.d("TAG", "successMonitoringList:${it.tran_type} ")
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

    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<SVMonitoringItem>>) {
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != Format.newDateFormat((totalList.last() as UzcardItem).svMonitoringItem!!.tran_date)
                        .substring(
                            0,
                            10
                        )
                ) totalList.add(dateItem)
            }
            for (svMonitoringItem in sortedMap[date]!!) {
                val uzcardItem = UzcardItem()
                uzcardItem.svMonitoringItem = svMonitoringItem
                totalList.add(uzcardItem)
            }
        }
        setAdapter(totalList)

    }

    private fun setAdapter(totalList: ArrayList<ListItem>) {
        svMonitoringAdapter.setListAdapter(totalList)
        binding.rec.scheduleLayoutAnimation()
        emptyView()

    }

    private fun groupDataIntoHashMap(svMonitoringList: List<SVMonitoringItem>): HashMap<String, MutableList<SVMonitoringItem>> {
        svMonitoringList.sortedBy { it.tran_date }
        val groupedHashMap: HashMap<String, MutableList<SVMonitoringItem>> = HashMap()
        for (svMonitoring in svMonitoringList) {
            val hashMapKey: String = Format.newDateFormat(svMonitoring.tran_date.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(svMonitoring)
            } else {
                val list: MutableList<SVMonitoringItem> = ArrayList()
                list.add(svMonitoring)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
    }

    override fun invoke(item: SVMonitoringItem) {
        dialogInfo = UzCardMonitoringDetailsDialog(item, object : BaseInterface {
            override fun uzCardInfo(svMonitoringItem: SVMonitoringItem) {
                super.uzCardInfo(svMonitoringItem)
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