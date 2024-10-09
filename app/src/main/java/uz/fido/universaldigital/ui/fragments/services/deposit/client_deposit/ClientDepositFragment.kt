package uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.SkeletonScreen
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentClientDepositBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.dialog.DepositOperationDialog
import uz.fido.universaldigital.ui.fragments.services.deposit.dialog.DialogInfoMonitoring
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.AccountHistoriesAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ClientDepositFragment : BaseFragment<FragmentClientDepositBinding, ClientDepositViewModel>(
    FragmentClientDepositBinding::inflate, ClientDepositViewModel::class.java
), View.OnClickListener {
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dialog: DepositOperationDialog
    private lateinit var clientDeposit: ClientDeposit

    private var accountHistoryAdapter: AccountHistoriesAdapter? = null
    private var skeletonScreen: SkeletonScreen? = null
    private var currentDate: String? = null
    private var dateBegin: String? = null
    private var dateEnd: String? = null

    private var dateSortList = ArrayList<AccountHistory>()
    private var list = ArrayList<AccountHistory>()
    private var serviceId: String = ""

    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private val newDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    companion object {
        const val CLIENT_DEPOSIT_MODEL = "model"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            clientDeposit = it.serializable<ClientDeposit>(CLIENT_DEPOSIT_MODEL) as ClientDeposit
        }
        setDate()
        historyDeposit()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.appBar.setAdditionalBtnVisibility(true)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.linearIncome.setOnClickListener(this)
        binding.linearOut.setOnClickListener(this)
        binding.appBar.setOnAdditionalBtnClickListener {
            dialog = DepositOperationDialog {
                dialog.dismiss()
                when (it) {
                    "edit" -> {
                        goto(
                            R.id.action_clientDepositFragment_to_depositEditNameFragment,
                            bundleOf(DepositEditNameFragment.EDIT_NAME to clientDeposit)
                        )
                    }

                    "delete" -> {
                        goto(
                            R.id.depositFillingFragment, bundleOf(
                                "deposit" to clientDeposit,
                                Const.OPERATION to "delete",
                                "card_type" to clientDeposit.currencyCode
                            )
                        )
                    }

                    "info" -> {
                        val loanDetailsDialog = DialogInfoMonitoring(
                            null,
                            clientDeposit, "info"
                        )
                        loanDetailsDialog.show(childFragmentManager, "")
                    }

                    "with_draw" -> {
                        goto(
                            R.id.depositPercentsDialog,
                            bundleOf("client_deposit" to clientDeposit)
                        )
                    }
                }
            }
            dialog.show(childFragmentManager, "")
        }
    }

    private fun historyDeposit() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        initDate()
        recyclerView()
    }

    private fun recyclerView() {
        dateSortList = arrayListOf()
        binding.rec.apply {
            setHasFixedSize(true)
            scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    fetchHistories(page)
                }
            }
            accountHistoryAdapter = AccountHistoriesAdapter(
                Const.TYPE_DEPOSIT,
                dateSortList,
                clientDeposit
            ) { AccountHistory, type ->
                val loanDetailsDialog =
                    DialogInfoMonitoring(AccountHistory, clientDeposit, type)
                loanDetailsDialog.show(childFragmentManager, "")
            }
            layoutManager = linearLayoutManager
            adapter = accountHistoryAdapter
            addOnScrollListener(scrollListener)
        }
        if (list.size == 0) {
            fetchHistories(1)
        }

    }

    private fun fetchHistories(page: Int) {
        val model = AccountHistoriesRequest(
            pageNumber = page.toString(),
            pageSize = "20",
            type = "3",
            account = clientDeposit.account,
            codeFilial = clientDeposit.filialCode,
            dateClose = dateEnd.toString(),
            dateBegin = dateBegin.toString()
        )
        if (page == 1) {
            skeletonScreen = showSkeleton(
                binding.rec,
                accountHistoryAdapter!!,
                R.layout.shimmer_item_account_history
            )
        }
        viewModel.getAccountHistories(getClientToken(), model).observe(viewLifecycleOwner) {
            if (page == 1) {
                skeletonScreen!!.hide()
            }
            when (it.status) {
                Status.SUCCESS -> {
                    list = it.data!!.response
                    if (list.isNotEmpty())
                        addDateView(list)
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
                val accountHistory = AccountHistory(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    it.dateExecute,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    1
                )
                dateSortList.add(accountHistory)
            }

            val newlistDate =
                newDateFormat.format(simpleDateFormat.parse(dateSortList[dateSortList.size - 1].dateExecute).time)
            val listDate = newDateFormat.format(simpleDateFormat.parse(it.dateExecute).time)

            if (newlistDate != listDate) {
                val accountHistory = AccountHistory(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    it.dateExecute,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    1
                )
                dateSortList.add(accountHistory)
            }
            dateSortList.add(it)
        }

        accountHistoryAdapter!!.setNewList(dateSortList)
    }

    private fun setDate() {
        binding.linearOut.visibility = if (clientDeposit.partialWrite == "Y") View.VISIBLE
        else View.INVISIBLE

        serviceId = clientDeposit.status.orEmpty()
        binding.depositNumber.text = if (clientDeposit.savDepId.orEmpty().length > 4) "•• ${
            clientDeposit.savDepId.orEmpty().substring(
                clientDeposit.savDepId.orEmpty().length - 4,
                clientDeposit.savDepId.orEmpty().length
            )
        }"
        else "•• ${clientDeposit.savDepId}"
        binding.depositName.text = clientDeposit.depName
        binding.appBar.setTitle(clientDeposit.depName.orEmpty())
        binding.amount.text =
            Format.formatAmount(((clientDeposit.sumDep ?: "0").toDouble() / 100).toString()) + " ${clientDeposit.currencyChar}"
        binding.depositPercent.text =
            "${getString(R.string.profit_per_year)} " + clientDeposit.percent + " %"
        binding.depositMonth.text = clientDeposit.depTemp
        binding.progressIndicator.progress =
            if (calculatePercentage() > 0) calculatePercentage().toInt() else 2
        clientDeposit.persSum?.let {
            binding.amountPercent.text =
                "${Format.formatAmount(Format.convertFromTiynDivide(it))} ${clientDeposit.currencyChar}"
        }
        // binding.progressIndicatorPercent.progress = if (calculatePercentage() > 0) calculatePercentage().toInt() else 1
        binding.depositPercentDay.text = clientDeposit.closingDate
    }

    private fun calculatePercentage(): Double {
        return (1 - (calculateDays(clientDeposit.closingDate.orEmpty()).toDouble() / (totalDays(
            clientDeposit.openDate.orEmpty(),
            clientDeposit.closingDate.orEmpty()
        )))) * 100
    }

    private fun totalDays(startDate: String, endDate: String): Int {
        try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val millionSeconds = sdf.parse(endDate).time - sdf.parse(startDate).time
            return TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
        } catch (e: Exception) {
            return 0
        }
    }

    private fun calculateDays(stringDate: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val millionSeconds = sdf.parse(stringDate).time - Calendar.getInstance().timeInMillis
            TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun initDate() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        currentDate = df.format(calendarEnd.time)
        calendarStart.add(Calendar.DAY_OF_MONTH, -360)
        dateBegin = df.format(calendarStart.time)
        dateEnd = df.format(calendarEnd.time)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.linear_income -> {
                goto(
                    R.id.depositFillingFragment, bundleOf(
                        "deposit" to clientDeposit,
                        Const.OPERATION to "top_up",
                        "card_type" to clientDeposit.currencyCode
                    )
                )
            }

            R.id.linear_out -> {
                if (clientDeposit.partialWrite == "Y") {
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, "with_draw")
                    bundle.putSerializable("deposit", clientDeposit)
                    bundle.putString("card_type", clientDeposit.currencyCode)
                    goto(R.id.depositFillingFragment, bundle)
                }
            }

        }
    }
}
