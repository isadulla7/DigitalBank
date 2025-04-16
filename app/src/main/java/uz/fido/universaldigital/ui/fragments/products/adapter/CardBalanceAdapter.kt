package uz.fido.universaldigital.ui.fragments.products.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemCardBalaceBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNameAndNumber
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes

class CardBalanceAdapter(
    private val context: Context,
    private val list: ArrayList<CardResponse>,
    private var updateTotalBalance: () -> Unit
) : RecyclerView.Adapter<CardBalanceAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemCardBalaceBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun setData(card: CardResponse) {
            binding.mainCardSign.isVisible = card.is_main == "Y"
            binding.switchView.setImageResource(if (card.balance_visibility) R.drawable.btn_radio_on else R.drawable.btn_radio_off)
            binding.cardName.setCardNameAndNumber(card)
            binding.cardBalance.setCardBalance(card)
            binding.cardType.setCardTypeImage(card)
            binding.cardBg.load(itemView.context.getDrawableFromRes(card.bg_icon_name))

            binding.father.setOnClickListener {
                if (card.balance_visibility) {
                    binding.switchView.setImageResource(R.drawable.btn_radio_off)
                    card.balance_visibility = false
                } else {
                    binding.switchView.setImageResource(R.drawable.btn_radio_on)
                    card.balance_visibility = true
                }
                updateTotalBalance.invoke()
            }
            if (card.state == "P") {
                binding.cardBg.alpha = 0.2f
                binding.cardName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_for_disable_card
                    )
                )
                binding.cardBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_for_disable_card
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCardBalaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }
}