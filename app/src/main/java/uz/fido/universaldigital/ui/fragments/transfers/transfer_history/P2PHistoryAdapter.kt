package uz.fido.universaldigital.ui.fragments.transfers.transfer_history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.universaldigital.databinding.ItemTransferHistoryBinding
import uz.fido.universaldigital.ui.fragments.transfers.utils.getShortNameFormatted
import uz.fido.universaldigital.ui.fragments.transfers.utils.getUserNameFormatted
import uz.fido.utils.const.CardConst
import uz.fido.utils.utility.format.Format

class P2PHistoryAdapter(
    private var isMain: Boolean? = false,
    private var isByPhone: Boolean? = null,
    private var onItemClickListener: (CardByPhone) -> Unit
) : ListAdapter<CardByPhone, P2PHistoryAdapter.ViewHolder>(MyDiffUtil()) {

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return if (isMain == true || count < 15) count else 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTransferHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemTransferHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardByPhone) {
            binding.apply {
                setCardNumber(item, binding)
                setUserName(item, binding)
                itemView.setOnClickListener {
                    onItemClickListener.invoke(item)
                }
                btnSaved.setOnClickListener {
                }
            }
        }
    }

    internal fun setUserName(item: CardByPhone, binding: ItemTransferHistoryBinding) {
        try {
            binding.name.text = getUserNameFormatted(item.empbossed_name)
            binding.shortName.text = getShortNameFormatted(item.empbossed_name)
            binding.cardTypeLogo.isVisible = item.card_type == CardConst.WALLET
        } catch (i: IndexOutOfBoundsException) {
            Log.e("===", i.toString())
        }
    }

    internal fun setCardNumber(item: CardByPhone, binding: ItemTransferHistoryBinding) {
        binding.cardNumber.text =
            if (item.card_type != CardConst.WALLET) Format.formatCardNumber(item.card_number)
            else Format.formatWalletNumber(item.card_number)
        if (isByPhone != null) {
            if (isByPhone as Boolean) {
                binding.cardNumber.text = Format.toPhoneFormat(item.phone_number)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MyDiffUtil : DiffUtil.ItemCallback<CardByPhone>() {
        override fun areItemsTheSame(oldItem: CardByPhone, newItem: CardByPhone): Boolean {
            return oldItem.phone_number == newItem.phone_number
        }

        override fun areContentsTheSame(
            oldItem: CardByPhone, newItem: CardByPhone
        ): Boolean {
            return oldItem == newItem
        }
    }

}