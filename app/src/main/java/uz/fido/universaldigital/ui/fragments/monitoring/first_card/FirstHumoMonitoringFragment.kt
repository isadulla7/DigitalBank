package uz.fido.universaldigital.ui.fragments.monitoring.first_card

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.HumoItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentHumoFirstMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.humo.HumoMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.humo.HumoMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringAllCardDialog
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
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
class FirstHumoMonitoringFragment :
    BaseFragment<FragmentHumoFirstMonitoringBinding, LocalMonitoringViewModel>(
        FragmentHumoFirstMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
    ), (HumoMonitoringItem) -> Unit {

    private lateinit var humoMonitoringDetailsDialog: HumoMonitoringDetailsDialog
    private lateinit var cardList: ArrayList<String>
    private var totalList: ArrayList<ListItem> = ArrayList()
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var operationType = 2
    private var choose: Int = 2
    private var timeType: String = ""

    private lateinit var filterDialog: MonitoringAllCardDialog
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var filter: Boolean = false
    private val humoMonitoringAdapter by lazy {
        HumoMonitoringAdapter(
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageFirst()
        setTime()
        initRecyclerView()
        checkFilter()
        onClickView()
    }

    private fun setImageFirst() {
        binding.appBar.setAdditionalIcon(R.drawable.ic_filter_frame)
    }

    private fun checkFilter() {
        totalList = arrayListOf()
        if (!filter) {
            checkHumoMonitoringSave()
        } else {
            getFilterHumoMonitoring()
        }
    }

    private fun checkHumoMonitoringSave() {
        getHumoMonitoringList(operationType)
    }

    private fun getFilterHumoMonitoring() {
        try {
            totalList = arrayListOf()
            binding.shimmerView.visibility = View.VISIBLE
            binding.rec.visibility = View.GONE
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formatStartDate = inputFormat.parse(dateBegin)
            val formatEndDate = inputFormat.parse(dateEnd)
            dateBegin = format.format(formatStartDate)
            dateEnd = format.format(formatEndDate)

            val type = choose
            viewModel.getHumoMonitoring(
                getClientToken(), HumoMonitoringRequest(
                    from_object_id = cardList[0],
                    start_date = dateBegin,
                    end_date = dateEnd
                )
            ).observe(viewLifecycleOwner) {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                binding.rec.visibility = View.VISIBLE
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data?.transactions ?: arrayListOf()
                        successMonitoringList(response, type)
                    }

                    Status.ERROR -> {
                        humoMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            recordException(e, ::getFilterHumoMonitoring.name)
        }
    }

    private fun onClickView() {
        binding.gotoMainPage.setOnClickListener {
            binding.consError.visibility = View.GONE
            getHumoMonitoringList(operationType)
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnClickListener {
            if (dateBegin != "" && dateBegin.contains(".")) {
                val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formatStartDate = format.parse(dateBegin)
                val formatEndDate = format.parse(dateEnd)
                dateBegin = inputFormat.format(formatStartDate)
                dateEnd = inputFormat.format(formatEndDate)
            }
            filterDialog = MonitoringAllCardDialog(choose, dateBegin, dateEnd, timeType,
                onClickItem = { choose, startDate, endDate, type ->
                    this.choose = choose
                    dateBegin = startDate
                    dateEnd = endDate
                    timeType = type
                    filter = true
                    binding.appBar.setAdditionalIcon(R.drawable.ic_filter_yes)
                    getFilterHumoMonitoring()
                    filterDialog.dismiss()
                },
                clear = {
                    this.choose = 2
                    dateBegin = ""
                    dateEnd = ""
                    timeType = ""
                    totalList.clear()
                    filter = false
                    setImageFirst()
                    setTime()
                    getHumoMonitoringList(operationType)
                    filterDialog.dismiss()
                })
            filterDialog.show(childFragmentManager, "")
        }
    }

    private fun getCardList() {
        val card = arguments?.serializable<CardResponse>(Const.CARD)
        cardList = arrayListOf()
        cardList.add(card?.object_id ?: "")

    }

    private fun getHumoMonitoringList(operationType: Int) {
        getCardList()
        if (cardList.isNotEmpty()) {
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            viewModel.getHumoMonitoring(
                getClientToken(),
                HumoMonitoringRequest(
                    from_object_id = if (cardList.isNotEmpty()) cardList[0] else "",
                    start_date = dateBegin,
                    end_date = dateEnd
                )
            ).observe(viewLifecycleOwner) { resource ->
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                when (resource.status) {
                    Status.SUCCESS -> {
                        val response = resource.data!!.transactions
                        successMonitoringList(response, operationType)
                    }

                    Status.ERROR -> {
                        humoMonitoringAdapter.removeList()
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

    private fun setTime() {
        val calendarEnd = Calendar.getInstance()
        dateEnd = dateFormat.format(calendarEnd.time)
        calendarEnd.add(Calendar.MONTH, -1)
        dateBegin = dateFormat.format(calendarEnd.time)
    }

    private fun initRecyclerView() {
        binding.rec.apply {
            adapter = humoMonitoringAdapter
            addItemDecoration(StickyHeaderDecoration(humoMonitoringAdapter))
        }
    }

    private fun successMonitoringList(
        response: ArrayList<HumoMonitoringItem>?,
        operationType: Int
    ) {
        val sortedResponse = ArrayList<HumoMonitoringItem>()
        val groupedHashMap: HashMap<String, MutableList<HumoMonitoringItem>> =
            when (operationType) {
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

    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<HumoMonitoringItem>>) {
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != Format.newDateFormat((totalList.last() as HumoItem).humoMonitoringItem!!.transactionDate)
                        .substring(
                            0,
                            10
                        )
                ) totalList.add(dateItem)
            }
            for (humoMonitoringItem in sortedMap[date]!!) {
                val generalItem = HumoItem()
                generalItem.humoMonitoringItem = humoMonitoringItem
                totalList.add(generalItem)
            }
        }
        emptyView()
        setAdapter(totalList)
    }

    private fun setAdapter(totalList: ArrayList<ListItem>) {
        humoMonitoringAdapter.setListAdapter(totalList)
    }

    private fun groupDataIntoHashMap(glMonitoringList: List<HumoMonitoringItem>): HashMap<String, MutableList<HumoMonitoringItem>> {
        glMonitoringList.sortedBy { it.transactionDate }
        val groupedHashMap: HashMap<String, MutableList<HumoMonitoringItem>> = HashMap()
        for (humoMonitoringItem in glMonitoringList) {
            val hashMapKey: String =
                Format.newDateFormat(humoMonitoringItem.transactionDate.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(humoMonitoringItem)
            } else {
                val list: MutableList<HumoMonitoringItem> = ArrayList()
                list.add(humoMonitoringItem)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    private fun emptyView() {
        binding.layoutEmpty.isVisible = totalList.isEmpty()
    }

    override fun invoke(item: HumoMonitoringItem) {
        humoMonitoringDetailsDialog = HumoMonitoringDetailsDialog(item, object : BaseInterface {
            override fun humoInfoPaymentMonitoring(item: HumoMonitoringItem) {
                super.humoInfoPaymentMonitoring(item)
                humoMonitoringDetailsDialog.dismiss()
                gotoWithSlide(
                    R.id.checkInfoPaymentFragment,
                    bundleOf("humo" to item, "operation" to "humo")
                )

            }
        })
        humoMonitoringDetailsDialog.show(childFragmentManager, "")
    }

}