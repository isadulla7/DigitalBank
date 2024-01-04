package uz.fido.universaldigital.ui.fragments.services.order_card.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import uz.fido.network.domain.model.cards.PriceItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemOrderCardBgBinding

class OrderCardBgAdapter(
    private var context: Context, private var list: ArrayList<PriceItem>,
    private var baseInterface: BaseInterface
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemOrderCardBgBinding.inflate(layoutInflater, container, false)
        val item = list[position]
        binding.cardImage.setImageResource(getDrawable(item.image_name))
        binding.father.setOnClickListener {
            baseInterface.selectedCardBg(item.image_name)
        }
        container.addView(binding.root, 0)
        return binding.root
    }

    fun getDrawable(name: String): Int {
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.bg_1
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, a: Any) {
        container.removeView(a as View)
    }
}