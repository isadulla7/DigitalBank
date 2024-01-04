package uz.fido.universaldigital.ui.fragments.products.cards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyCardGridBinding
import uz.fido.universaldigital.databinding.ItemMyCardsBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isMainCard
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setBankLogo
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNumber
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardState
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.loadCardBackgroundImage

class CardsListAdapter(
    private var baseInterface: BaseInterface,
    private var layoutManager: GridLayoutManager? = null
) : ListAdapter<CardResponse, RecyclerView.ViewHolder>(MyCardsDiffUtil()) {

    inner class ViewHolder(private val binding: ItemMyCardsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardResponse) {
            binding.apply {
                mainCardSign.isVisible = isMainCard(item)
                cardName.text = item.object_name
                cardBalance.setCardBalance(item)
                cardNumber.setCardNumber(item)
                cardType.setCardTypeImage(item)
                cardBg.loadCardBackgroundImage(item)
                bankLogo.setBankLogo(item)
                setCardState(item, itemView.context, status)
                father.setOnClickListener {
                    if (item.object_type == "KL") baseInterface.selectedWallet(item) else baseInterface.selectedCard(
                        item
                    )
                }
                father.setOnLongClickListener {
                    baseInterface.shareCardNumberDialog(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    inner class SimpleViewHolder(private val binding: ItemMyCardGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardResponse) {
            binding.apply {
                mainCardSign.isVisible = isMainCard(item)
                cardName.text = item.object_name
                cardBalance.setCardBalance(item)
                cardNumber.setCardNumber(item)
                cardType.setCardTypeImage(item)
                cardBg.loadCardBackgroundImage(item)
                bankLogo.setBankLogo(item)
                setCardState(item, itemView.context, status)
                father.setOnClickListener {
                    if (item.object_type == "KL") baseInterface.selectedWallet(item) else baseInterface.selectedCard(
                        item
                    )
                }
                father.setOnLongClickListener {
                    baseInterface.shareCardNumberDialog(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMyCardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingMiniCard =
            ItemMyCardGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == ViewType.DETAILED.ordinal) ViewHolder(binding) else SimpleViewHolder(
            bindingMiniCard
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SimpleViewHolder) {
            holder.bind(getItem(position))
        } else {
            (holder as ViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutManager?.spanCount == 2) ViewType.SMALL.ordinal
        else ViewType.DETAILED.ordinal
    }

    private class MyCardsDiffUtil : DiffUtil.ItemCallback<CardResponse>() {
        override fun areItemsTheSame(oldItem: CardResponse, newItem: CardResponse): Boolean =
            oldItem.balance == newItem.balance &&
                    oldItem.processing_server_status == newItem.processing_server_status &&
                    oldItem.state == newItem.state &&
                    oldItem.stateName == newItem.stateName &&
                    oldItem.object_name == newItem.object_name

        override fun areContentsTheSame(oldItem: CardResponse, newItem: CardResponse): Boolean {
            return oldItem == newItem
        }
    }

    enum class ViewType {
        SMALL, DETAILED
    }

}