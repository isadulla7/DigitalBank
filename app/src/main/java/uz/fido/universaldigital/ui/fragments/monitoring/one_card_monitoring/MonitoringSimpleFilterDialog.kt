package uz.fido.universaldigital.ui.fragments.monitoring.one_card_monitoring

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogAllCardFilterBinding
import uz.fido.universaldigital.ui.utils.extensions.limitRange
import uz.fido.utils.utility.format.Format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonitoringSimpleFilterDialog(
    private val choose: Int,
    private var startDate: String,
    private var endDate: String,
    private var timeType: String,
    private var onClickItem: (Int, String, String, String) -> Unit,
    private val clear: () -> Unit,
) : DialogFragment() {

    private lateinit var binding: DialogAllCardFilterBinding
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private var minPlus: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)
    }

    companion object {
        const val DATE_TYPE_MONTH = "month"
        const val DATE_TYPE_WEEK = "week"
        const val DATE_TYPE_PERIOD = "period"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogAllCardFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setAdditionalBtnVisibility(true)
        minMaxCheck()
        checkTime()
        initSetOnClickListeners()
    }

    private fun minMaxCheck() {
        minPlus = choose
        if (choose == 0) {
            minPlus = 0
            incomeClick()
        }
        if (choose == 1) {
            minPlus = 1
            outComeClick()
        }
    }

    private fun initSetOnClickListeners() {
        binding.btnEnter.setOnClickListener {
            dismiss()
            onClickItem(minPlus, startDate, endDate, timeType)
        }
        binding.btnCansel.setOnClickListener {
            dismiss()
            clear()
        }
        binding.appBar.setOnBackButtonClickListener {
            dismiss()
        }
        binding.month.setOnClickListener {
            showMonthRange()
        }
        binding.week.setOnClickListener {
            showWeekRange()
        }
        binding.time.setOnClickListener {
            showTimeRangeDialog()
        }
        binding.inCome.setOnClickListener {
            incomeClick()
            minPlus = 0
        }
        binding.outCome.setOnClickListener {
            outComeClick()
            minPlus = 1

        }
    }

    private fun showTimeRangeDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setCalendarConstraints(limitRange().build())
        builder.setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            startDate = Format.Companion.getDateFromMilliseconds(it.first!!, "yyyyMMdd")
            endDate = Format.Companion.getDateFromMilliseconds(it.second!!, "yyyyMMdd")
            timeType = DATE_TYPE_PERIOD
            checkTime()
        }
        picker.addOnCancelListener {
            picker.dismiss()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun checkTime() {
        when (timeType) {
            DATE_TYPE_PERIOD -> {
                val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formatStartDate = inputFormat.parse(startDate)
                val formatEndDate = inputFormat.parse(endDate)
                binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                binding.time.text = "${outputFormat.format(formatStartDate)} - ${outputFormat.format(formatEndDate)}"
                binding.week.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.week.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
                binding.month.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.month.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }

            DATE_TYPE_MONTH -> {
                binding.month.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.month.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
                binding.time.text = requireContext().getString(R.string.select_period)
                binding.week.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.week.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }

            DATE_TYPE_WEEK -> {
                binding.week.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
                binding.week.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
                binding.time.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.time.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
                binding.time.text = requireContext().getString(R.string.select_period)
                binding.month.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
                binding.month.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
            }
        }
    }

    private fun showWeekRange() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
        startDate = dateFormat.format(startOfWeek)
        endDate = dateFormat.format(endOfWeek)
        timeType = DATE_TYPE_WEEK
        checkTime()

    }

    private fun showMonthRange() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 0)
        val endOfMonth = calendar.time
        startDate = dateFormat.format(startOfMonth)
        endDate = dateFormat.format(endOfMonth)
        timeType = DATE_TYPE_MONTH
        checkTime()
    }

    private fun outComeClick() {
        binding.outCome.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
        binding.outCome.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
        binding.inCome.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
        binding.inCome.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
    }

    private fun incomeClick() {
        binding.inCome.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color_click)
        binding.inCome.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
        binding.outCome.background = ContextCompat.getDrawable(requireContext(), R.drawable.monitoring_filter_item_color)
        binding.outCome.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainTextColor))
    }


}