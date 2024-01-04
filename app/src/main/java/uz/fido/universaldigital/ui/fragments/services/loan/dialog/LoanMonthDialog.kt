package uz.fido.universaldigital.ui.fragments.services.loan.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.LoanMonthBottomSheetBinding
import uz.fido.universaldigital.ui.fragments.services.loan.adapter.LoanMonthAdapter
import uz.fido.universaldigital.ui.fragments.services.loan.modul.LoanMonth
import java.text.SimpleDateFormat
import java.util.Calendar

class LoanMonthDialog(
    private var onClick: (Int, String) -> Unit,
    private val size: String,
    private val type: String,
    private val selectDate: Int,
    private val context: Context
) : BottomSheetDialogFragment() {

    private lateinit var binding: LoanMonthBottomSheetBinding
    private var adapter: LoanMonthAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LoanMonthBottomSheetBinding.inflate(inflater, container, false)
        val list = currentTime()
        adapter = LoanMonthAdapter(onClick, list = list, context, type)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        return binding.root
    }

    private fun currentTime() = when (type) {
        "month" -> loanTimeList(size)
        "day" -> loanDayList()
        "year" -> yearList()
        else -> loanDayList()
    }

    private fun yearList(): java.util.ArrayList<LoanMonth> {
        val monthList = arrayListOf<LoanMonth>()
        val month_date = SimpleDateFormat("yyyy")

        for (i in 1 until 6) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, i)
            monthList.add(
                LoanMonth(
                    i,
                    "${context.getString(R.string.until)} ${month_date.format(calendar.time)} ${
                        context.getString(uz.fido.utils.R.string.year)
                    }"
                )
            )
        }
        return monthList
    }


    private fun loanDayList(): ArrayList<LoanMonth> {
        val monthList = arrayListOf<LoanMonth>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1 until lastDay + 1) {
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.MONTH, selectDate)
            calendar2.add(Calendar.DATE, i)
            val day = calendar2[Calendar.DAY_OF_WEEK]
            val week = checkWeek(day)
            monthList.add(LoanMonth(i, week))
        }
        return monthList
    }

    private fun checkWeek(day: Int): String {
        return when (day) {
            Calendar.MONDAY -> getString(R.string.monday)
            Calendar.TUESDAY -> getString(R.string.tuesday)
            Calendar.WEDNESDAY -> getString(R.string.wednesday)
            Calendar.THURSDAY -> getString(R.string.thursday)
            Calendar.FRIDAY -> getString(R.string.friday)
            Calendar.SATURDAY -> getString(R.string.saturday)
            Calendar.SUNDAY -> getString(R.string.sunday)
            else -> getString(R.string.monday)
        }
    }


    private fun loanTimeList(timeMax: String): ArrayList<LoanMonth> {
        val monthList = arrayListOf<LoanMonth>()
        val month_date = SimpleDateFormat("MMMM yyyy")
        for (i in 3 until timeMax.toInt() + 1) {
            val cal: Calendar = Calendar.getInstance()
            cal.add(Calendar.MONTH, i)
            monthList.add(LoanMonth(i, month_date.format(cal.time)))
        }
        return monthList
    }

}