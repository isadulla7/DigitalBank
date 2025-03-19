package uz.fido.universaldigital.ui.fragments.monitoring.local.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.databinding.FragmentMonitoringChartsBinding
import uz.fido.universaldigital.ui.fragments.transfers.utils.capitalizeWord
import uz.fido.utils.R
import java.text.DateFormatSymbols
import java.util.Calendar

@AndroidEntryPoint
class MonitoringChartFragment : DialogFragment() {

    private lateinit var binding: FragmentMonitoringChartsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonitoringChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager(binding.viewpager)
        onPageListenerTabLayout()
    }

    private fun onPageListenerTabLayout() {
        binding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.appBar.setOnBackButtonClickListener { this.dismiss() }
    }

    private fun setupViewPager(viewpager: ViewPager) {
        val monthNameList = getMonthNames()
        val adapter = PagerAdapter(childFragmentManager, monthNameList as ArrayList<String>)
        viewpager.adapter = adapter
        viewpager.currentItem = monthNameList.size
        viewpager.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(viewpager)
    }

    private fun getMonthNames(): List<String> {
        val calendar = Calendar.getInstance()
        val monthNames = DateFormatSymbols().months.map { it.capitalizeWord() }
        val monthNamesList = mutableListOf<String>()
        for (i in 0 until 24) {
            monthNamesList.add("${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}")
            calendar.add(Calendar.MONTH, -1)
        }
        return monthNamesList.reversed()
    }

}
