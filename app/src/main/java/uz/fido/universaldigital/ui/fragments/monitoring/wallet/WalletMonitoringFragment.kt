package uz.fido.universaldigital.ui.fragments.monitoring.wallet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.android.synthetic.main.log_out_dialog.view.title
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.WalletHistoryItem
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentWalletMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.MenuMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.WalletMonitoringAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.WalletMonitoringDetailsDialog
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format.newDateFormat
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class WalletMonitoringFragment :
    BaseFragment<FragmentWalletMonitoringBinding, LocalMonitoringViewModel>(
        FragmentWalletMonitoringBinding::inflate, LocalMonitoringViewModel::class.java
    ), (AccountHistory) -> Unit {

    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var operationType = 0
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    private var dateBegin: String = ""
    private var dateEnd: String = ""
    private var walletList = arrayListOf<String>()
    private var linearLayoutManager: LinearLayoutManager? = null
    private lateinit var walletMonitoringDetailsDialog: WalletMonitoringDetailsDialog

    private val menuMonitoringViewModel by activityViewModels<MenuMonitoringViewModel>()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private val walletMonitoringAdapter by lazy {
        WalletMonitoringAdapter(
            requireContext(),
            totalList,
            this
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        recylerViewScroll()
        getCardList()
        setTime()
        createMonitoringAdapter()
        checkFilterWallet()

        onClickView()
    }

    private fun checkFilterWallet() {
        if (!menuMonitoringViewModel.walletFilter)
            getWalletList(1, operationType)
        else getFilterWalletList()
    }

    private fun getFilterWalletList() {
        try {
            menuMonitoringViewModel.walletMonitoringFilter.observe(viewLifecycleOwner) { filterSaveVh ->
                val skeletonScreen = showSkeleton(
                    binding.shimmerView,
                    MibDetailsAdapter(requireContext(), this),
                    R.layout.shimmer_item_monitoring,
                    1
                )
                val newList = arrayListOf<CardResponse>()
                menuProductsViewModel.cards.observe(viewLifecycleOwner) { card ->
                    card.forEach {
                        if (it.object_type == CardConst.WALLET) {
                            newList.add(it)
                        }
                    }
                }
                val card = arrayListOf<Int>()
                filterSaveVh.cardList.forEach { if (!it.is_selected_monitoring) card.add(it.object_id) }
                val checkList = newList.filter { it.object_id == card[0].toString() }
                walletList = arrayListOf()
                checkList.forEach {
                    walletList.add(it.account_code)
                }
                totalList = arrayListOf()
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                if (filterSaveVh.startDate != "") {
                    dateEnd = df.format(format.parse(filterSaveVh.endDate)?.time ?: "")
                    dateBegin = df.format(format.parse(filterSaveVh.startDate)?.time ?: "")
                } else setTime()
                val type = when (filterSaveVh.plusMinus) {
                    getString(R.string.enrollments) -> 2
                    getString(R.string.write_offs) -> 1
                    else -> 0
                }

                val info = Paper.book().read<SignInResponse>(Const.PAPER_CLIENT_INFO)
                val model = AccountHistoriesRequest(
                    pageNumber = "1",
                    pageSize = "20",
                    type = operationType.toString(),
                    account = walletList[0],
                    codeFilial = info?.filial_code,
                    dateClose = dateEnd,
                    dateBegin = dateBegin
                )
                viewModel.getAccountHistories(getClientToken(), model).observe(viewLifecycleOwner) {
                    skeletonScreen.hide()
                    binding.shimmerView.visibility = View.GONE
                    when (it.status) {
                        Status.SUCCESS -> {
                            val response = it.data!!.response
                            successMonitoringList(response, type)
                            if (response.size < 1) {
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
        } catch (e: Exception) {
            recordException(e)
        }
    }

    private fun onClickView() {
        binding.gotoMainPage.setOnClickListener {
            binding.consError.visibility = View.GONE
            if (!menuMonitoringViewModel.walletFilter)
                getWalletList(1, operationType)
            else getFilterWalletList()

        }
    }

    private fun getWalletList(page: Int, operationType: Int) {
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
                        successMonitoringList(response, operationType)
                        if (response.size < 1) {
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
            binding.layoutEmpty.title.text = getString(R.string.card_list_no)
        }
    }

    private fun successMonitoringList(
        response: java.util.ArrayList<AccountHistory>,
        operationType: Int
    ) {
        val sortedResponse = java.util.ArrayList<AccountHistory>()
        val groupedHashMap: HashMap<String, MutableList<AccountHistory>> = when (operationType) {
            2 -> {
                response.forEach {
                    if (it.debit == "0") {
                        sortedResponse.add(it)
                    }
                }
                groupDataIntoHashMap(sortedResponse)
            }

            1 -> {
                response.forEach {
                    if (it.credit == "0") {
                        sortedResponse.add(it)
                    }
                }
                groupDataIntoHashMap(sortedResponse)
            }

            else -> groupDataIntoHashMap(response)
        }
        val sortedMap = groupedHashMap.toSortedMap(compareByDescending { it })
        for (date in sortedMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            if (totalList.isEmpty()) {
                totalList.add(dateItem)
            } else {
                if (dateItem.date != (totalList.last() as WalletHistoryItem).walletItem!!.dateExecute?.let { it1 ->
                        newDateFormat(it1).substring(
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
                visaHistory.dateExecute?.substring(0, 10)?.let { newDateFormat(it) }.toString()
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
        val info = Paper.book().read<SignInResponse>(Const.PAPER_CLIENT_INFO)
        val model = AccountHistoriesRequest(
            pageNumber = page.toString(),
            pageSize = "20",
            type = operationType.toString(),
            account = walletList[0],
            codeFilial = info?.filial_code,
            dateClose = dateEnd,
            dateBegin = dateBegin
        )
        return model
    }

    private fun getCardList() {
        walletList = menuMonitoringViewModel.walledList.value ?: arrayListOf()
    }

    private fun emptyView() {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        } else binding.layoutEmpty.visibility = View.GONE
    }

    private fun recylerViewScroll() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                fetchWalletMonitoring(page, operationType)
            }
        }
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
            adapter = walletMonitoringAdapter
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
            addItemDecoration(StickyHeaderDecoration(walletMonitoringAdapter))

        }
    }

    fun fetchWalletMonitoring(page: Int, operationType: Int) {

    }

    override fun invoke(item: AccountHistory) {
        walletMonitoringDetailsDialog =
            WalletMonitoringDetailsDialog(item, object : BaseInterface {})
        walletMonitoringDetailsDialog.show(childFragmentManager, "")
    }


}