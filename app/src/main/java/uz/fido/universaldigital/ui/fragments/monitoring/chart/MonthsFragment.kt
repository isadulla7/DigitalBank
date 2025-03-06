package uz.fido.universaldigital.ui.fragments.monitoring.chart

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.local_history.ChartData
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMonthsBinding
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class MonthsFragment : BaseSimpleFragment<FragmentMonthsBinding>(FragmentMonthsBinding::inflate) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private val viewModel by activityViewModels<LocalMonitoringViewModel>()

    private lateinit var mobileDBHelper: DatabaseHelper
    private lateinit var chartDetailsAdapter: ChartDetailsAdapter
    private lateinit var period: Pair<String, String>

    companion object {
        private const val PAGE_SIZE = "1000"
        private const val PAGE_NUMBER = "1"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        getCurrentPeriod()
        mobileDBHelper = DatabaseHelper(requireContext())
        chartDetailsAdapter = ChartDetailsAdapter(arrayListOf()) {
            val paymentHistoryDialog = PaymentHistoryDialog(it.serviceId, period)
            paymentHistoryDialog.show(childFragmentManager, "")
        }
        initChartDetails()
        getLocalMonitoring(period)
    }

    private fun drawMonthlyChart(list: ArrayList<ChartData>) {
        val total = list.map { it.amount }.sumOf { it }
        list.sortByDescending { it.amount }
        binding.apply {
            pieChart.invalidate()
            val histories: ArrayList<PieEntry> = ArrayList()
            histories.clear()
            for (i in list.indices) {
                histories.add(
                    PieEntry(
                        list[i].amount.toFloat(),
                        calculatePercent(list[i].amount, total)
                    )
                )
            }
            pieChart.legend.isEnabled = false
            pieChart.setDrawEntryLabels(false)
            pieChart.setEntryLabelTextSize(12f)
            pieChart.setDrawRoundedSlices(true)
            pieChart.isRotationEnabled = false
            val pieDataSet = PieDataSet(histories, "")
            pieDataSet.setAutomaticallyDisableSliceSpacing(false)
            if (histories.isNotEmpty())
                pieDataSet.colors = getPieChartColors()
            pieDataSet.setDrawValues(false)
            pieDataSet.valueTextSize = 12f
            pieDataSet.valueTextColor = Color.WHITE
            val pieData = PieData(pieDataSet)
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.centerText = getString(R.string.total_amount) + "\n" + Format.formatAmount(total.toString()).replace(".00", "") + " UZS"
            pieChart.setCenterTextTypeface(
                Typeface.createFromAsset(
                    requireContext().resources.assets, "fonts/Inter-Medium.ttf"
                )
            )
            pieChart.isDrawHoleEnabled = true
            pieChart.setDrawSlicesUnderHole(true)
            pieChart.holeRadius = 48f
            pieChart.transparentCircleRadius = 60f
            pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                }

                override fun onNothingSelected() {
                }
            })
            pieChart.animateX(1000)
            if (total == 0.toBigDecimal()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
            chartDetailsAdapter.setList(list)
        }
    }

    private fun getCurrentPeriod() {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val position = getInt(ARG_OBJECT)
            period = if (position == 23) {
                getCurrentMonth()
            } else {
                getSelectedMonth(position)
            }
        }
    }

    private fun getLocalMonitoring(startEndDate: Pair<String, String>) {
        binding.progressView.visibility = View.VISIBLE
        viewModel.getLocalMonitoring(
            getClientToken(),
            LocalMonitoringRequest(
                start_date = startEndDate.first,
                end_date = startEndDate.second,
                page_number = PAGE_NUMBER,
                page_item_size = PAGE_SIZE,
                object_ids = arrayListOf()
            )
        ).observe(viewLifecycleOwner) { resource ->
            hideProgressView()
            if (resource.status == Status.SUCCESS) {
                val response = resource?.data?.local_transactions ?: arrayListOf()
                val groupList = mobileDBHelper.getGroupList()
                val items = response.groupBy { it.service_id }.map { (serviceId, items) ->
                    val group = groupList.find { paymentGroup ->
                        val serviceIdList = paymentGroup.service_list?.map { it.service_id.toString() }
                        serviceIdList?.contains(serviceId) ?: false
                    }
                    ChartData(
                        paymentGroup = group,
                        paymentService = mobileDBHelper.getServiceByContractId(serviceId),
                        serviceId = serviceId,
                        amount = items.sumOf { BigDecimal(it.amount).divide(100.toBigDecimal()) }
                    )
                } as ArrayList<ChartData>
                drawMonthlyChart(items)
            }
        }
    }

    private fun hideProgressView() {
        try {
            if (context != null) {
                binding.progressView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSelectedMonth(position: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, position - 23)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = dateFormat.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDay = dateFormat.format(calendar.time)
        return Pair(firstDay, lastDay)
    }

    private fun getCurrentMonth(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val lastDay = dateFormat.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = dateFormat.format(calendar.time)
        return Pair(firstDay, lastDay)
    }

    private fun initChartDetails() {
        binding.chartDetails.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chartDetailsAdapter
        }
    }

    private fun calculatePercent(currentAmount: BigDecimal, totalAmount: BigDecimal): String {
        return if (totalAmount != BigDecimal(0)) {
            currentAmount.divide(totalAmount, 2, RoundingMode.HALF_UP).multiply(BigDecimal(100)).toString() + " %"
        } else {
            ""
        }
    }
}