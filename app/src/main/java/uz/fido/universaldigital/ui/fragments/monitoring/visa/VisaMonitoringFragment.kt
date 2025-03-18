package uz.fido.universaldigital.ui.fragments.monitoring.visa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.VisaItem
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringItem
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentVisaMonitoringBinding
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
class VisaMonitoringFragment :
    BaseFragment<FragmentVisaMonitoringBinding, LocalMonitoringViewModel>(
        FragmentVisaMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
    ), (CurrencyCardMonitoringItem) -> Unit {
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var operationType = 2
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private lateinit var visaMonitoringDetailsDialog: VisaMonitoringDetailsDialog
    private var currencyList = arrayListOf<String>()
    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var totalList: ArrayList<ListItem> = ArrayList()

    private val visaMonitoringAdapter by lazy {
        VisaMonitoringAdapter(
            requireContext(),
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCardList()
        setTime()
        createMonitoringAdapter()
        checkFilterVisa()
    }

    private fun checkFilterVisa() {
        if (menuMonitoringViewModel.visaFilter)
            getFilterVisaList()
        else getVisaList()
    }

    private fun getFilterVisaList() {
        menuMonitoringViewModel.visaMonitoringFilter.observe(viewLifecycleOwner) { it ->
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            val card = arrayListOf<String>()
            it.cardList.forEach { if (!it.is_selected_monitoring) card.add(it.object_value) }
            currencyList = card
            totalList = arrayListOf()
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            if (it.startDate != "") {
                dateEnd = df.format(format.parse(it.endDate).time)
                dateBegin = df.format(format.parse(it.startDate).time)
            } else setTime()
            val type = when (it.operationType) {
                getString(R.string.enrollments) -> 0
                getString(R.string.write_offs) -> 1
                else -> 2
            }


            viewModel.getCurrencyCardMonitoring(
                getClientToken(), CurrencyCardMonitoringRequest(
                    object_value = currencyList[0],
                    start_date = dateBegin,
                    end_date = dateEnd
                )
            ).observe(viewLifecycleOwner) {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data?.transactions ?: ArrayList()
                        successMonitoringList(response, type)
                    }

                    Status.ERROR -> {
                        visaMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun getVisaList() {
        if (currencyList.isNotEmpty()) {
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )

            viewModel.getCurrencyCardMonitoring(
                getClientToken(),
                CurrencyCardMonitoringRequest(
                    object_value = currencyList[0],
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
                        visaMonitoringAdapter.setListAdapter(arrayListOf())
                        emptyView()

                    }
                }

            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                //  skeletonScreen.hide()
                if (isVisible) {
                    //     skeletonScreen.hide()
                    binding.shimmerView.visibility = View.GONE
                    binding.rec.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.layoutEmpty.findViewById<TextViewMedium>(R.id.title).text = getString(R.string.card_list_no)
                }
            }, 500)
        }

    }

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
    }

    private fun getCardList() {
        currencyList = menuMonitoringViewModel.currencyList.value ?: arrayListOf()
    }

    private fun setTime() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        calendarStart.add(Calendar.DAY_OF_MONTH, -60)
        dateBegin = df.format(calendarStart.time)
        dateEnd = df.format(calendarEnd.time)
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = visaMonitoringAdapter
            addItemDecoration(StickyHeaderDecoration(visaMonitoringAdapter))

        }
    }

    private fun successMonitoringList(
        response: java.util.ArrayList<CurrencyCardMonitoringItem>?,
        operationType: Int
    ) {
        val sortedResponse = java.util.ArrayList<CurrencyCardMonitoringItem>()
        val groupedHashMap: HashMap<String, MutableList<CurrencyCardMonitoringItem>> =
            when (operationType) {
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


    private fun addDateMonitoringList(sortedMap: SortedMap<String, MutableList<CurrencyCardMonitoringItem>>) {
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != Format.newDateFormat((totalList.last() as VisaItem).visaMonitoringItem!!.tran_date)
                        .substring(
                            0,
                            10
                        )
                ) totalList.add(dateItem)
            }
            for (visaMonitoringItem in sortedMap[date]!!) {
                val generalItem = VisaItem()
                generalItem.visaMonitoringItem = visaMonitoringItem
                totalList.add(generalItem)
            }
        }
        emptyView()
        setAdapter(totalList)

    }

    private fun setAdapter(totalList: java.util.ArrayList<ListItem>) {
        visaMonitoringAdapter.setListAdapter(totalList)

    }

    private fun groupDataIntoHashMap(glMonitoringList: List<CurrencyCardMonitoringItem>): HashMap<String, MutableList<CurrencyCardMonitoringItem>> {
        glMonitoringList.sortedBy { it.tran_date }
        val groupedHashMap: HashMap<String, MutableList<CurrencyCardMonitoringItem>> = HashMap()
        for (humoMonitoringItem in glMonitoringList) {
            val hashMapKey: String =
                Format.newDateFormat(humoMonitoringItem.tran_date.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(humoMonitoringItem)
            } else {
                val list: MutableList<CurrencyCardMonitoringItem> = java.util.ArrayList()
                list.add(humoMonitoringItem)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    override fun invoke(item: CurrencyCardMonitoringItem) {
        visaMonitoringDetailsDialog = VisaMonitoringDetailsDialog(item, object : BaseInterface {
            override fun visaInfoPaymentMonitoring(item: CurrencyCardMonitoringItem) {
                super.visaInfoPaymentMonitoring(item)
                visaMonitoringDetailsDialog.dismiss()
                gotoWithSlide(
                    R.id.checkInfoPaymentFragment,
                    bundleOf("visa" to item, "operation" to "visa")
                )
            }
        })
        visaMonitoringDetailsDialog.show(childFragmentManager, "")
    }

}