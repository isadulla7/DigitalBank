package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemNewHomeUserCardBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNameAndNumber
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.setCardState
import uz.fido.utils.utility.view.recycler_view_drag.ItemTouchHelperAdapter

class NewHomeCardsAdapter(
    private var baseInterface: BaseInterface
) : ListAdapter<CardResponse, NewHomeCardsAdapter.ViewHolder>(
    MyDiffUtil()
), ItemTouchHelperAdapter {

    inner class ViewHolder(private var binding: ItemNewHomeUserCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CardResponse) {
            binding.apply {
                mainCardSign.isVisible = item.is_main == "Y"
                cardName.setCardNameAndNumber(item)
                cardBalance.setCardBalance(item)
                cardType.setCardTypeImage(item)
                cardBg.load(itemView.context.getDrawableFromRes(item.bg_icon_name))
                setCardState(item, cardStateName, itemView.context)
                if (cardStateName.isVisible) {
                    binding.cardBg.alpha = 0.2f
                    binding.cardName.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_for_disable_card
                        )
                    )
                    binding.cardBalance.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_for_disable_card
                        )
                    )
                } else {
                    binding.cardBg.alpha = 1f
                    binding.cardName.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.home_card_description_color
                        )
                    )
                    binding.cardBalance.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.mainTextColor
                        )
                    )
                }
                father.setOnClickListener {
                    baseInterface.selectedCard(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemNewHomeUserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }


    class MyDiffUtil : DiffUtil.ItemCallback<CardResponse>() {
        override fun areItemsTheSame(oldItem: CardResponse, newItem: CardResponse): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CardResponse, newItem: CardResponse): Boolean {
            return oldItem == newItem
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        baseInterface.makeDragAndDropOperation(fromPosition, toPosition)
        return true

    }

    override fun onItemDismiss(position: Int) {}

}