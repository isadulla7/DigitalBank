package uz.fido.universaldigital.ui.fragments.monitoring.chart

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(manager: FragmentManager, private var months: ArrayList<String>) :
    FragmentPagerAdapter(manager) {
    override fun getCount(): Int {
        return months.size
    }
    override fun getItem(position: Int): Fragment {
        val monthsFragment = MonthsFragment()
        monthsFragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return monthsFragment
    }
    override fun getPageTitle(position: Int): CharSequence {
        return months[position]
    }
}
const val ARG_OBJECT = "object"
