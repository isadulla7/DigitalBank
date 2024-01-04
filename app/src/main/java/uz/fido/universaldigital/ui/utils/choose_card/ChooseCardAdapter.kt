package uz.fido.universaldigital.ui.utils.choose_card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemChooseCardBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setBankLogo
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNumberFormatted
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage

class ChooseCardAdapter(
    private val list: ArrayList<CardResponse>,
    private val amount: String? = null
) : RecyclerView.Adapter<ChooseCardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemChooseCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(card: CardResponse) {
            binding.apply {
                cardNumber.setCardNumberFormatted(card)
                bankLogo.setBankLogo(card)
                cardBalance.setCardBalance(card)
                cardBackground.load(itemView.context.getDrawableFromRes(card.bg_icon_name))
                cardType.setCardTypeImage(card)
                setCardState(card, this, itemView.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChooseCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }

    private fun setCardState(item: CardResponse, binding: ItemChooseCardBinding, context: Context) {
        when (item.processing_server_status) {
            "0" -> {
                if (amount != null) {
                    if (compareAmount(amount, item)) {
                        isCardStateViewVisible(true, binding)
                        binding.errorText.text =
                            context.getString(R.string.not_enough_money)
                    } else {
                        isCardStateViewVisible(false, binding)
                    }
                }
                return
            }

            "-100" -> {
                isCardStateViewVisible(true, binding)
                binding.errorText.text = context.getString(R.string.can_not_receive_balance)
                return
            }

            else -> {
                isCardStateViewVisible(true, binding)
                binding.errorText.text = item.stateName
            }
        }
    }

    private fun isCardStateViewVisible(isVisible: Boolean, binding: ItemChooseCardBinding) {
        binding.errorView.isVisible = isVisible
        binding.errorText.isVisible = isVisible
    }

    private fun compareAmount(amount: String, card: CardResponse): Boolean {
        return card.balance.toBigDecimal().divide(100.toBigDecimal())
            .compareTo(amount.toBigDecimal()) == -1
    }

    fun Context.getDrawableFromRes(name: String): Int {
        val resId = this.resources.getIdentifier(name, "drawable", this.packageName)
        return if (resId != 0) {
            resId
        } else R.drawable.bg_0
    }
}