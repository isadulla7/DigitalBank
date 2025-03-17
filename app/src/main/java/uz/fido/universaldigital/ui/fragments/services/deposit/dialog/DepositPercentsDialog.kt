package uz.fido.universaldigital.ui.fragments.services.deposit.dialog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.DialogDepositPercentBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit.ClientDepositFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit.ClientDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.AccountHistoriesAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DepositPercentsDialog : BaseFragment<DialogDepositPercentBinding, ClientDepositViewModel>(
    DialogDepositPercentBinding::inflate, ClientDepositViewModel::class.java
) {

    private lateinit var clientDeposit: ClientDeposit
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var accountHistoryAdapter: AccountHistoriesAdapter? = null
    private var list = ArrayList<AccountHistory>()
    private var currentDate: String? = null
    private var dateBegin: String? = null
    private var dateEnd: String? = null
    private var dateSortList = ArrayList<AccountHistory>()
    private var skeletonScreen: SkeletonScreen? = null
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private val newDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { pop() }
        clientDeposit = arguments?.serializable<ClientDeposit>("client_deposit") as ClientDeposit
        setDate()
        initDate()
        recyclerView()
    }

    private fun setDate() {
        val dicimal = BigDecimal("100")

        binding.etAmount.setText(Format.formatAmount((((clientDeposit.sumDep ?: "0").toBigDecimal() - (clientDeposit.amount ?: "0").toBigDecimal()) / dicimal).toString()))
        binding.btnEnter.setOnClickListener {
            if (clientDeposit.withdrawInterest == "Y") {
                val bundle = Bundle()
                bundle.putString(Const.OPERATION, ClientDepositFragment.WITH_DRAW_PERCENT)
                bundle.putSerializable("deposit", clientDeposit)
                bundle.putString("card_type", clientDeposit.currencyCode)
                goto(R.id.depositFillingFragment, bundle)
            }
        }

    }

    private fun recyclerView() {
        binding.list.apply {
            setHasFixedSize(true)
            val linerLayoutManager = LinearLayoutManager(context)
            layoutManager = linerLayoutManager
            scrollListener = object : EndlessRecyclerViewScrollListener(linerLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    fetchHistories(page)
                }
            }
            accountHistoryAdapter = AccountHistoriesAdapter(Const.TYPE_DEPOSIT, list, clientDeposit) { accountHistory, s ->

            }
            adapter = accountHistoryAdapter
            addOnScrollListener(scrollListener)
        }
        if (list.size == 0) {
            fetchHistories(1)
        }
    }

    private fun initDate() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        currentDate = df.format(calendarEnd.time)
        calendarStart.add(Calendar.DAY_OF_MONTH, -30)
        dateBegin = df.format(calendarStart.time)
        dateEnd = df.format(calendarEnd.time)
    }

    private fun fetchHistories(page: Int) {
        val model = AccountHistoriesRequest(
            pageNumber = page.toString(),
            pageSize = "20",
            type = "3",
            account = clientDeposit.accountPercent,
            codeFilial = clientDeposit.filialCode,
            dateClose = dateEnd.toString(),
            dateBegin = dateBegin.toString()
        )
        if (page == 1) {
            skeletonScreen = showSkeleton(binding.list, accountHistoryAdapter!!, R.layout.shimmer_item_account_history)
        }
        viewModel.getAccountHistories(getClientToken(), model).observe(viewLifecycleOwner) {
            if (page == 1)
                skeletonScreen!!.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    list.addAll(it.data!!.response)
                    emptyView()
                    if (it.data!!.response.isNotEmpty())
                        addDateView(it.data!!.response)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }


    private fun addDateView(list: ArrayList<AccountHistory>) {
        list.forEach {
            if (dateSortList.isEmpty()) {
                val accountHistory = AccountHistory("", "", "", "", "", "", it.dateExecute, "", "", "", "",  "", "", "", "", "", 1)
                dateSortList.add(accountHistory)
            }

            val newlistDate = newDateFormat.format(simpleDateFormat.parse(dateSortList[dateSortList.size - 1].dateExecute).time)
            val listDate = newDateFormat.format(simpleDateFormat.parse(it.dateExecute).time)

            if (newlistDate != listDate) {
                val accountHistory = AccountHistory("", "", "", "", "", "", it.dateExecute, "", "", "", "", "", "", "", "", "", 1)
                dateSortList.add(accountHistory)
            }
            dateSortList.add(it)

        }

        accountHistoryAdapter!!.setNewList(dateSortList)
    }


    private fun emptyView() {
        if (list.isEmpty()) {
            binding.empty.visibility = View.VISIBLE
            binding.amountLin.visibility = View.GONE
        }
    }

}