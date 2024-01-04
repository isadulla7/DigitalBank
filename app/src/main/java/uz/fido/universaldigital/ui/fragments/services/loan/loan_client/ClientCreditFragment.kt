package uz.fido.universaldigital.ui.fragments.services.loan.loan_client

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.SkeletonScreen
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.bind
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.loan_graph.CreditActualGraphResponse
import uz.fido.network.domain.model.loans.loan_graph.CreditGraphRequest
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistoriesResponse
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentClientLoanBinding
import uz.fido.universaldigital.ui.fragments.monitoring.dialog.MonitoringChooseDialog
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.AccountHistoriesAdapter
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.CreditOperationDialog
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanDetailsDialog
import uz.fido.universaldigital.ui.fragments.services.loan.loan_info.CreditDetailsFragment
import uz.fido.universaldigital.ui.fragments.services.loan.requisites.CreditRequisitesFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.adapter.showSkeletonView
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.RoundingMode
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.Locale

@AndroidEntryPoint
class ClientCreditFragment:BaseFragment<FragmentClientLoanBinding,ClientLoanViewModel>(
    FragmentClientLoanBinding::inflate,ClientLoanViewModel::class.java
), View.OnClickListener {

    private lateinit var clientProduct: CreditProduct
    private var creditActualGraph: CreditActualGraphResponse? = null
    private lateinit var currentDate: String
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private lateinit var dateBegin: String
    private lateinit var dateEnd: String
    private lateinit var loanDetailsDialog: LoanDetailsDialog
    private lateinit var monitoringChooseDialog: MonitoringChooseDialog
    private var accountHistoriesResponse: AccountHistoriesResponse? = null
    private var skeletonScreen: SkeletonScreen? = null
    private var operType=3
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var creditOperationDialog: CreditOperationDialog
    private var overdueDate= arrayListOf<String>()
    private lateinit var accountHistoriesAdapter: AccountHistoriesAdapter
    private var list = ArrayList<AccountHistory>()
    private lateinit var dateSortList:ArrayList<AccountHistory>
    private lateinit var linerLayoutManager:LinearLayoutManager
    private val simpleDateFormat=SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    private val newDateFormat=SimpleDateFormat("dd.MM.yyyy")
    companion object {
        const val CLIENT_CREDIT_MODEL = "model"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linerLayoutManager = LinearLayoutManager(context)
        arguments?.let {
            clientProduct = it.serializable<CreditProduct>(CLIENT_CREDIT_MODEL) as CreditProduct
        }
        dateSortList=ArrayList<AccountHistory>()
        setDate()
        fetchGraph(clientProduct.loanId)
        setMonitoring()
        setonCLickListener()

       }

    private fun setonCLickListener() {
        binding.layoutControl.setOnClickListener(this)
        binding.nextMonthPaymentDetails.setOnClickListener(this)
        binding.pay.setOnClickListener(this)
        binding.filter.setOnClickListener(this)
        binding.layoutCreditSchedule.setOnClickListener(this)
    }

    private fun setMonitoring() {
        accountHistoriesAdapter = AccountHistoriesAdapter(Const.TYPE_LOAN, list, null, ){item,type->
         loanDetailsDialog= LoanDetailsDialog(requireContext(),item,type)
          loanDetailsDialog.show(childFragmentManager,"")
        }
        isFilter()
        scrollMonitoring()
        recyclerView()
        getListMonitoring(1,operType)
    }

    private fun scrollMonitoring() {
        scrollListener = object : EndlessRecyclerViewScrollListener(linerLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getListMonitoring(page, operType)
            }
        }
    }

    private fun recyclerView() {
        binding.rec.apply {

            layoutManager = linerLayoutManager

            adapter=accountHistoriesAdapter
            addOnScrollListener(scrollListener)
        }
    }

    private fun getListMonitoring(page: Int, actionType: Int) {
          if (page==1){
              skeletonScreen = showSkeleton(binding.rec, accountHistoriesAdapter, R.layout.shimmer_item_account_history)
          }
        val model = AccountHistoriesRequest(
            pageNumber = page.toString(),
            pageSize = "20",
            type = actionType.toString(),
            account = clientProduct.loan2,
            codeFilial = clientProduct.codeFilial,
            dateClose = dateEnd,
            dateBegin = dateBegin
        )

        viewModel.getAccountHistories(getClientToken(),model).observe(viewLifecycleOwner){resource->
              if (page==1){
                  skeletonScreen!!.hide()
              }else{

              }

            when(resource.status){
                Status.SUCCESS->{
                    list= arrayListOf()
                    val accountList = arrayListOf(
                        clientProduct.loan1,
                        clientProduct.loan5,
                        clientProduct.loan3,
                        clientProduct.loan7,
                        clientProduct.loan22,
                        clientProduct.loan2,
                        clientProduct.loan46
                    )
                    if (resource.data != null) {
                        resource?.data!!.response.forEach {
                            if (accountList.contains(it.coAcc) && !list.contains(it)) {
                                if (it.lnType != "" || it.dtAcc!!.startsWith("12503")) list.add(it)
                            }
                        }
                    }
                    Collections.sort(list, object : Comparator<AccountHistory> {
                        val format: DateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH)
                        override fun compare(p0: AccountHistory?, p1: AccountHistory?): Int {
                            return try {
                                format.parse(p0?.dateExecute).compareTo(format.parse(p1?.dateExecute))
                            } catch (e: ParseException) {
                                Log.e("exception", e.toString())
                            }
                        }
                    })
                    list.reverse()
                    accountHistoriesResponse = resource.data
                    if (list.isNotEmpty())
                    newDateAddImte(list)
                }
                Status.ERROR->{
                    showSnackbar(resource.message.toString())
                }
            }
        }
    }

    private fun newDateAddImte(list: ArrayList<AccountHistory>) {
        list.forEach {
            if (dateSortList.isEmpty()){
                val accountHistory=AccountHistory("","","","","","",it.dateExecute,"","","","","","","","","","",1)
                dateSortList.add(accountHistory)
            }

            val newlistDate=newDateFormat.format(simpleDateFormat.parse(dateSortList[dateSortList.size-1].dateExecute).time)
            val listDate=newDateFormat.format(simpleDateFormat.parse(it.dateExecute).time)

            if (newlistDate!= listDate){
                val accountHistory=AccountHistory("","","","","","",it.dateExecute,"","","","","","","","","","",1)
                dateSortList.add(accountHistory)
            }
            dateSortList.add(it)

        }

        accountHistoriesAdapter.setNewList(dateSortList)
    }

    private fun isFilter() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        currentDate = df.format(calendarEnd.time)
        calendarStart.add(Calendar.DAY_OF_MONTH, -30)

        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        val contractDate = sdf.parse(clientProduct.contractDate)
        val differenceDays = java.util.concurrent.TimeUnit.DAYS.convert(
            contractDate.time - Calendar.getInstance().timeInMillis, java.util.concurrent.TimeUnit.MILLISECONDS
        )
        dateBegin = if (differenceDays > 30) df.format(calendarStart.time) else df.format(contractDate)
        dateEnd = df.format(calendarEnd.time)
    }



    private fun fetchGraph(loanId: String) {
        val skeletonScreen = showSkeletonView(R.layout.shimmer_view_loan_info, binding.shimmerView)
        viewModel.getCreditGraphSecond(getClientToken(), CreditGraphRequest(loanId)).observe(viewLifecycleOwner){
            skeletonScreen.hide()
            when(it.status){
                Status.SUCCESS->{
                    val response = it.data as CreditActualGraphResponse
                    creditActualGraph = response
                    calculateNextMonthPayment(
                        creditActualGraph!!
                    )
                    val hashMap = HashMap<String, CreditActualGraphResponse>()
                    hashMap[loanId] = response
                    statusSuccess(response)
                }
                Status.ERROR->{
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun statusSuccess(response: CreditActualGraphResponse) {
         response.data.forEach {
             val c: Calendar = Calendar.getInstance()
             val sdf = SimpleDateFormat("dd.MM.yyyy")
             val getCurrentDate: String = sdf.format(c.time)
             if (sdf.parse(getCurrentDate) > sdf.parse(it.redempDate)) {
                 overdueDate.add(it.redempDate)
             }
         }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun calculateNextMonthPayment(creditActualGraph: CreditActualGraphResponse) {
        if (clientProduct.saldo5!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() > 0.toBigDecimal()) {
            binding.nearestPayment.text = getString(R.string.overdue_payment)
            binding.textRecommendedAmount.text = "\n${
                Format.formatAmount(
                    creditActualGraph.nextPayment.toBigDecimal().divide(100.toBigDecimal()).toString()
                )
            } UZS"
        } else {
            binding.textRecommendedAmount.text = "\n${
                Format.formatAmount(
                    creditActualGraph.nextPayment.toBigDecimal().divide(100.toBigDecimal()).toString()
                )
            } UZS"
            binding.nearestPayment.text =
                if (!isOverdraft(clientProduct.creditType)) getString(R.string.nearest_payment) else getString(R.string.nearest_limit_decrease)
        }

        binding.date.text=creditActualGraph.nextPaymentDate.toString()

        if (!isOverdraft(clientProduct.creditType)) {
        } else {
            binding.nextMonthPaymentDetails.isClickable = false
        }

    }


    private fun setDate() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.textCreditName.text = getLoanType(context = requireContext(), clientProduct.creditType)
        binding.textBalance.text = Format.formatAmount(Format.convertFromTiynDivide(clientProduct.amount)) + " UZS"
        binding.totalAmount.text = getString(R.string.the_rest)+" "+Format.formatAmount(Format.convertFromTiynDivide(clientProduct.totalDebt)) + " UZS"
        binding.date.text=clientProduct.arrearDate
        val perc = calculatePercentage()

        binding.progressIndicator.max = 100
        if (!isOverdraft(clientProduct.creditType)) {
            binding.progressIndicator.progress = if (perc > 0) 100-perc else 1
        } else calculateOverdraftPercent()

        if (clientProduct.saldo5!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() > 0.toBigDecimal()) {
            binding.nearestPayment.text = getString(R.string.overdue_payment)
        }


        if (clientProduct.saldo5!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() > 0.toBigDecimal()) {
            binding.nearestPayment.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
            binding.textRecommendedAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
            binding.date.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteColor))
            binding.nextMonthPaymentDetails.background=ContextCompat.getDrawable(requireContext(),R.drawable.credit_background_color)
        }

    }


    fun isOverdraft(creditId: String): Boolean {
        return creditId == "54"
    }

    private fun calculatePercentage(): Int {
        return (clientProduct.saldo1!!.toBigDecimal().multiply(100.toBigDecimal())
            .divide(clientProduct.amount.toBigDecimal(), 2, RoundingMode.HALF_UP)).toInt()
    }

    fun getLoanType(context: Context, loanId: String): String {
        return when (loanId) {
            "24" -> context.getString(R.string.loan_type_1)
            "30" -> context.getString(R.string.loan_type_2)
            "32" -> context.getString(R.string.loan_type_3)
            "34" -> context.getString(R.string.loan_type_4)
            "54" -> context.getString(R.string.loan_type_5)
            "59" -> context.getString(R.string.loan_type_6)
            else -> context.getString(R.string.loan)
        }
    }

    private fun calculateOverdraftPercent() {
        var perc = clientProduct.overdraftLimit.toDouble() / clientProduct.amount.toDouble()
        perc *= 100
        binding.progressIndicator.progress = if (perc.toInt() > 0) 100-perc.toInt() else 1

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.layout_control->{
               creditOperationDialog= CreditOperationDialog {
                   creditOperationDialog.dismiss()
                   when(it){
                       CreditOperationDialog.INFO_CREDIT->{
                         val dialogInfo=CreditDetailsFragment(clientProduct)
                           dialogInfo.show(childFragmentManager,"")
                       // goto(R.id.creditDetailsFragment, bundleOf(CLIENT_CREDIT_MODEL to clientProduct))
                       }
                       CreditOperationDialog.REQUISITES->{
                           val dialog=CreditRequisitesFragment(clientProduct)
                           dialog.show(childFragmentManager,"")
                        // goto(R.id.creditRequisitesFragment,bundleOf(CLIENT_CREDIT_MODEL to clientProduct))
                       }
                       CreditOperationDialog.REPAYMENT_SCHEDULE->{
                        goto(R.id.creditGraphInitialFragment, bundleOf(CLIENT_CREDIT_MODEL to clientProduct,"status" to overdueDate))
                       }
                   }
               }
              creditOperationDialog.show(childFragmentManager,"")
            }
            R.id.nextMonthPaymentDetails,R.id.pay->{
                if (creditActualGraph != null) gotoWithSlide(
                    R.id.loanPaymentFragment, bundleOf(CLIENT_CREDIT_MODEL to clientProduct, "actualGraph" to creditActualGraph!!.data[0],"earlyClosure" to "1")
                )
            }
            R.id.layout_credit_schedule->{
                if (creditActualGraph != null) gotoWithSlide(
                    R.id.loanPaymentFragment, bundleOf(CLIENT_CREDIT_MODEL to clientProduct, "actualGraph" to creditActualGraph!!.data[0],"earlyClosure" to "2")
                )
            }
            R.id.filter->{
                monitoringChooseDialog= MonitoringChooseDialog {
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                    monitoringChooseDialog.dismiss()
                 getFilterList(it)
                }
                monitoringChooseDialog.show(childFragmentManager,"")
            }
        }
    }

    private fun getFilterList(it: String) {
        when(it){
            getString(R.string.write_offs)->{
                operType=1
            }
            getString(R.string.enrollments)->{
                operType=3
            }
        }
        getListMonitoring(1,operType)
    }
}
