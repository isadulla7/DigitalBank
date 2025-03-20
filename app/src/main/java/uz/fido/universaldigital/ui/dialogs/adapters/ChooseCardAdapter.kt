package uz.fido.universaldigital.ui.dialogs.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemChooseCardDialogBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.extensions.cardLogoByType
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.utility.format.Format

@SuppressLint("SetTextI18n")
class ChooseCardAdapter(
    private val list: ArrayList<CardResponse>,
    private val amount: String? = null,
    private val onClickListener: (CardResponse) -> Unit
) : RecyclerView.Adapter<ChooseCardAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemChooseCardDialogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(card: CardResponse) {
            binding.cardName.text = getCardName(card)
            binding.cardType.setImageResource(cardLogoByType(card))
            binding.cardBalance.setCardBalance(card)
            binding.mainCardSign.isVisible = card.is_main == "Y"

            if (amount != null) {
                if (compareAmount(amount, card)) {
                    binding.cardStatus.visibility = View.VISIBLE
                    binding.father.setOnClickListener {
                        Toast.makeText(
                            itemView.context,
                            R.string.not_enough_money,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    binding.cardStatus.visibility = View.GONE
                    binding.father.setOnClickListener {
                        onClickListener.invoke(card)
                    }
                }
            } else {
                binding.father.setOnClickListener {
                    onClickListener.invoke(card)
                }
            }

            if (card.processing_server_status == "0") {
                binding.cardStatus.visibility = View.GONE
            } else {
                binding.cardStatus.visibility = View.VISIBLE
                binding.cardStatusText.text = card.stateName
                binding.father.setOnClickListener {
                    Toast.makeText(itemView.context, card.stateName, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getCardName(card: CardResponse): String {
        return Format.formatCardName(card.object_name) + " " + if (card.object_type != WALLET) Format.formatCardNumberNew(
            card.object_value
        ) else Format.formatWalletNumber(card.object_value)
    }

    private fun compareAmount(amount: String, card: CardResponse): Boolean {
        return card.balance.toBigDecimal().divide(100.toBigDecimal())
            .compareTo(amount.toBigDecimal()) == -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChooseCardDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }
}