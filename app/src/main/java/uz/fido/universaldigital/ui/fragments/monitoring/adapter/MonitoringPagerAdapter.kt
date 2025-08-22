package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import uz.fido.universaldigital.R

class MonitoringPagerAdapter(private var context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun clear() {
        mFragmentList.clear()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.local_monitoring)
            1 -> context.getString(R.string.uzcard)
            2 -> context.getString(R.string.humo)
          //  3 -> "Visa"
            3 -> context.getString(R.string.wallet)
            else -> ""
        }
    }

}