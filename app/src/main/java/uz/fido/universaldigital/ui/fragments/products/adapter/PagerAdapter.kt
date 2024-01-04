package uz.fido.universaldigital.ui.fragments.products.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import uz.fido.universaldigital.R
import java.util.*

class PagerAdapter(private var context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun getFragmentsList(): ArrayList<Fragment> {
        return mFragmentList
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun clear() {
        mFragmentList.clear()
    }


    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.uzs_text)
            1 -> context.getString(R.string.usd_text)
            2 -> context.getString(R.string.rub_text)
            else -> context.getString(R.string.eur_text)
        }
    }
}