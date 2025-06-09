package uz.fido.universaldigital.ui.fragments.monitoring.one_card_monitoring

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.WalletHistoryItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentWalletFirstMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.local.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.wallet.WalletMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.wallet.WalletMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class OneFirstMonitoringFragment : BaseFragment<FragmentWalletFirstMonitoringBinding, LocalMonitoringViewModel>(
    FragmentWalletFirstMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
), (AccountHistory) -> Unit {

    private lateinit var walletMonitoringDetailsDialog: WalletMonitoringDetailsDialog
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var filterDialog: MonitoringSimpleFilterDialog

    private var operationType = 0
    private var choose: Int = 2
    private var timeType: String = ""
    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var walletList = arrayListOf<String>()
    private var linearLayoutManager: LinearLayoutManager? = null
    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val walletMonitoringAdapter by lazy {
        WalletMonitoringAdapter(
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        setImageFirst()
        recyclerViewScroll()
        getCardList()
        setTime()
        createMonitoringAdapter()
        getWalletList(1)
        onClickView()
    }

    private fun setImageFirst() {
        binding.appBar.setAdditionalIcon(R.drawable.ic_filter_frame)
    }

    private fun getFilterWalletList() {
        showSkeleton(
            binding.shimmerView,
            MibDetailsAdapter(requireContext(), this),
            R.layout.shimmer_item_monitoring,
            1
        )
        totalList = arrayListOf()
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formatStartDate = inputFormat.parse(dateBegin)
        val formatEndDate = inputFormat.parse(dateEnd)
        dateBegin = format.format(formatStartDate)
        dateEnd = format.format(formatEndDate)
        val filialCode = getFromSecureStore(Const.PAPER_CLIENT_FILIAL_CODE)
        val model = AccountHistoriesRequest(
            pageNumber = "1",
            pageSize = "20",
            type = operationType.toString(),
            account = walletList[0],
            codeFilial = filialCode,
            dateClose = dateEnd,
            dateBegin = dateBegin
        )
        viewModel.getAccountHistories(getClientToken(), model).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!.response
                    successMonitoringList(response)
                    if (response.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    walletMonitoringAdapter.removeList()
                    binding.consError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onClickView() {
        binding.gotoMainPage.setOnClickListener {
            binding.consError.visibility = View.GONE
            if (!menuMonitoringViewModel.walletFilter)
                getWalletList(1)
            else getFilterWalletList()

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
            filterDialog = MonitoringSimpleFilterDialog(
                choose, dateBegin, dateEnd, timeType,
                onClickItem = { choose, startDate, endDate, type ->
                    this.choose=choose
                    totalList.clear()
                    operationType= if (choose==0) 2 else if (choose==1) 1 else 0
                    dateBegin = startDate
                    dateEnd = endDate
                    timeType = type
                    totalList.clear()
                    binding.appBar.setAdditionalIcon(R.drawable.ic_filter_yes)
                    getFilterWalletList()
                    filterDialog.dismiss()
                },
                clear = {
                    operationType=0
                    this.choose = 2
                    dateBegin = ""
                    dateEnd = ""
                    timeType = ""
                    totalList.clear()
                    setImageFirst()
                    setTime()
                    getWalletList(1)
                    filterDialog.dismiss()
                })
            filterDialog.show(childFragmentManager, "")

        }
    }

    private fun getWalletList(page: Int) {
        if (walletList.isNotEmpty()) {
            val skeletonScreen = showSkeleton(
                binding.shimmerView,
                MibDetailsAdapter(requireContext(), this),
                R.layout.shimmer_item_monitoring,
                1
            )
            val model = createModel(page)
            viewModel.getAccountHistories(getClientToken(), model).observe(viewLifecycleOwner) {
                skeletonScreen.hide()
                binding.shimmerView.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        totalList = arrayListOf()
                        val response = it.data!!.response
                        successMonitoringList(response)
                        if (response.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                        }
                    }

                    Status.ERROR -> {
                        walletMonitoringAdapter.removeList()
                        binding.consError.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            binding.shimmerView.visibility = View.GONE
            binding.rec.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutEmpty.findViewById<TextViewMedium>(R.id.title).text = getString(R.string.card_list_no)
        }
    }

    private fun successMonitoringList(
        response: java.util.ArrayList<AccountHistory>
    ) {
        val groupedHashMap: HashMap<String, MutableList<AccountHistory>> = groupDataIntoHashMap(response)
        val sortedMap = groupedHashMap.toSortedMap(compareByDescending { it })
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != (totalList.last() as WalletHistoryItem).walletItem!!.dateExecute?.let { it1 ->
                        Format.newDateFormat(it1).substring(
                            0,
                            10
                        )
                    }
                ) totalList.add(dateItem)
            }

            for (visaMonitoringItem in groupedHashMap[date]!!) {
                val generalItem = WalletHistoryItem()
                generalItem.walletItem = visaMonitoringItem
                totalList.add(generalItem)
            }
        }
        setListAdapter(totalList)
        emptyView()
    }

    private fun setListAdapter(totalList: ArrayList<ListItem>) {
        walletMonitoringAdapter.setListAdapter(totalList)
    }

    private fun groupDataIntoHashMap(visaHistories: java.util.ArrayList<AccountHistory>): HashMap<String, MutableList<AccountHistory>> {
        val groupedHashMap: HashMap<String, MutableList<AccountHistory>> = HashMap()
        for (visaHistory in visaHistories) {
            val hashMapKey: String =
                visaHistory.dateExecute?.substring(0, 10)?.let { Format.newDateFormat(it) }.toString()
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(visaHistory)
            } else {
                val list: MutableList<AccountHistory> = java.util.ArrayList()
                list.add(visaHistory)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    private fun createModel(page: Int): AccountHistoriesRequest {
        val filialCode = getFromSecureStore(Const.PAPER_CLIENT_FILIAL_CODE)
        val model = AccountHistoriesRequest(
            pageNumber = page.toString(),
            pageSize = "20",
            type = operationType.toString(),
            account = walletList[0],
            codeFilial = filialCode,
            dateClose = dateEnd,
            dateBegin = dateBegin
        )
        return model
    }

    private fun getCardList() {
        val card = arguments?.serializable<CardResponse>(Const.CARD)
        walletList = arrayListOf()
        walletList.add(card?.account_code ?: "")
    }

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
    }

    private fun recyclerViewScroll() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            }
        }
    }

    private fun setTime() {
        val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        calendarStart.add(Calendar.DAY_OF_MONTH, -60)
        dateBegin = df.format(calendarStart.time)
        dateEnd = df.format(calendarEnd.time)
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = walletMonitoringAdapter
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(walletMonitoringAdapter))
        }
    }

    override fun invoke(item: AccountHistory) {
        walletMonitoringDetailsDialog =
            WalletMonitoringDetailsDialog(item, object : BaseInterface {})
        walletMonitoringDetailsDialog.show(childFragmentManager, "")
    }


}