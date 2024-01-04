package uz.fido.universaldigital.ui.fragments.products.cards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import uz.fido.universaldigital.databinding.ItemOperationsBinding
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes

class CardBgAdapter(
    private var context: Context,
    private val imagesNames: ArrayList<String>
) : PagerAdapter() {

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemOperationsBinding.inflate(LayoutInflater.from(context), container, false)
        binding.cardImage.setImageResource(context.getDrawableFromRes(imagesNames[position]))
        container.addView(binding.root, 0)
        return binding.root
    }

    override fun getCount() = imagesNames.size

    override fun getItemPosition(`object`: Any) = POSITION_NONE

    override fun destroyItem(container: ViewGroup, position: Int, a: Any) {
        container.removeView(a as View)
    }

}