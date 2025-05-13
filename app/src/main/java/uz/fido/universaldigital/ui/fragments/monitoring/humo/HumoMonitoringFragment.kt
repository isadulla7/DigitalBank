package uz.fido.universaldigital.ui.fragments.monitoring.humo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.HumoItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringRequest
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentHumoMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringFragment
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.utils.format.Format
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
class HumoMonitoringFragment :
    BaseFragment<FragmentHumoMonitoringBinding, LocalMonitoringViewModel>(
        FragmentHumoMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
    ), (HumoMonitoringItem) -> Unit {

    private lateinit var humoMonitoringDetailsDialog: HumoMonitoringDetailsDialog
    private lateinit var cardList: ArrayList<String>
    private var totalList: ArrayList<ListItem> = ArrayList()
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var operationType = 2
    private var maxAmount:String=""
    private var minAmount:String=""
    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val saveViewModel by activityViewModels<MenuMonitoringViewModel>()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private val humoMonitoringAdapter by lazy {
        HumoMonitoringAdapter(
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTime()
        initRecyclerView()
        checkFilter()
        onClickView()
    }

    private fun checkFilter() {
        totalList = arrayListOf()
        if (!saveViewModel.humoFilter) {
            checkHumoMonitoringSave()
        } else {
            getFilterHumoMonitoring()
        }
    }

    private fun checkHumoMonitoringSave() {
        getHumoMonitoringList(operationType)
    }

    private fun getFilterHumoMonitoring() {
        saveViewModel.humoMonitoringFilter.observe(viewLifecycleOwner) { filterSaveVh ->
            val card = arrayListOf<String>()
            filterSaveVh.cardList.forEach { if (!it.is_selected_monitoring) card.add(it.object_id.toString()) }
            totalList = arrayListOf()
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            if (filterSaveVh.maxAmount.isNotEmpty()){
                maxAmount=filterSaveVh.maxAmount.replace(" ","")+"00"
                minAmount=filterSaveVh.minAmount.replace(" ","")+"00"
            }
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            if (filterSaveVh.startDate != "") {
                dateEnd = dateFormat.format(format.parse(filterSaveVh.endDate).time)
                dateBegin = dateFormat.format(format.parse(filterSaveVh.startDate).time)
            } else setTime()
            val type = when (filterSaveVh.operationType) {
                getString(R.string.enrollments) -> 0
                getString(R.string.write_offs) -> 1
                else -> 2
            }
            viewModel.getHumoMonitoring(
                getClientToken(), HumoMonitoringRequest(
                    from_object_id = if (card.isNotEmpty()) card[0] else "",
                    start_date = dateBegin,
                    end_date = dateEnd
                )
            ).observe(viewLifecycleOwner) {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data?.transactions ?: arrayListOf()
                        val item = arrayListOf<HumoMonitoringItem>()
                        try {
                            response.forEach {
                                if ((it.transactionAmount.toDoubleOrNull() ?: 0.0) < maxAmount.toDouble()
                                    && (it.transactionAmount.toDoubleOrNull() ?: 0.0)>minAmount.toDouble()) {
                                    item.add(it)
                                }
                            }
                            successMonitoringList(item, type)
                        }catch (e:Exception){
                            successMonitoringList(response,type)
                        }
                    }

                    Status.ERROR -> {
                        humoMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun onClickView() {
        binding.gotoMainPage.setOnClickListener {
            binding.consError.visibility = View.GONE
            getHumoMonitoringList(operationType)
        }
    }

    private fun getCardList() {
        cardList = arrayListOf()
        cardList = menuMonitoringViewModel.humoList.value ?: arrayListOf()

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
            if (cardList.isNotEmpty()) {
                saveViewModel.filterHumoCard(cardList[0])
            }
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
        val filteredHashMap = groupedHashMap.mapValues { it.value.sortedByDescending { it.transactionDate }.toMutableList() }
        val sortedMap = filteredHashMap.toSortedMap(compareByDescending { it })
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