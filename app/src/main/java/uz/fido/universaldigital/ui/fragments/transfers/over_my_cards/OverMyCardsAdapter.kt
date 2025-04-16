package uz.fido.universaldigital.ui.fragments.transfers.over_my_cards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.databinding.ItemTransferCardBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.setCardState
import uz.fido.universaldigital.ui.utils.extensions.whiteCardLogoByType
import uz.fido.utils.const.CardConst
import uz.fido.utils.utility.format.Format

class OverMyCardsAdapter(
    private var context: Context,
    private val cards: ArrayList<CardResponse>
) : PagerAdapter() {

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemTransferCardBinding.inflate(LayoutInflater.from(context), container, false)
        val item = cards[position]
        binding.senderCardBalance.setCardBalance(item)
        binding.cardBackground.load(context.getDrawableFromRes(item.bg_icon_name))
        binding.senderCardName.text = item.object_name
        binding.senderCardType.setImageResource(whiteCardLogoByType(item))
        binding.senderCardNumber.text =
            if (item.object_type != CardConst.WALLET) Format.formatCardNumberNew(item.object_value) else Format.formatWalletNumber(
                item.object_value
            )
        setCardState(item, binding.cardState, context)
        container.addView(binding.root, 0)
        return binding.root
    }

    override fun getCount(): Int {
        return cards.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, a: Any) {
        container.removeView(a as View)
    }

}