package uz.fido.universaldigital.ui.fragments.transfers.transfer_history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemPopularTransferBinding
import uz.fido.universaldigital.ui.fragments.transfers.utils.getUserNameFormatted
import uz.fido.universaldigital.ui.utils.extensions.cardLogoByType
import uz.fido.utils.const.CardConst
import uz.fido.utils.utility.format.Format

class FavoriteTransfersAdapter(
    private var isMain: Boolean? = false,
    private var onItemClickListener: (PopularTransfers) -> Unit,
    private var setFavorite: (Boolean, String) -> Unit
) : ListAdapter<PopularTransfers, FavoriteTransfersAdapter.ViewHolder>(MyDiffUtil()) {

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return if (isMain == true || count < 15) count else 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPopularTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPopularTransferBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PopularTransfers) {
            binding.apply {
                setCardNumber(item, binding)
                name.text = getUserNameFormatted(item.empbossed_name)
                item.object_type?.let { cardLogoByType(it) }
                    ?.let { cardTypeLogo.setImageResource(it) }
                binding.btnSaved.setImageResource(if (item.is_favourite == "Y") R.drawable.ic_star else R.drawable.ic_star_unselected)
                itemView.setOnClickListener {
                    onItemClickListener.invoke(item)
                }
                btnSaved.setOnClickListener {
                    setFavorite(item.is_favourite == "Y", item.card_number.toString())
                }
            }
        }
    }

    private fun setCardNumber(item: PopularTransfers, binding: ItemPopularTransferBinding) {
        binding.cardNumber.text =
            if (item.object_type != CardConst.WALLET) Format.formatCardNumber(item.card_number.toString())
            else Format.formatWalletNumber(item.card_number.toString())
    }

    class MyDiffUtil : DiffUtil.ItemCallback<PopularTransfers>() {
        override fun areItemsTheSame(
            oldItem: PopularTransfers,
            newItem: PopularTransfers
        ): Boolean {
            return oldItem.card_number == newItem.card_number
        }

        override fun areContentsTheSame(
            oldItem: PopularTransfers, newItem: PopularTransfers
        ): Boolean {
            return oldItem == newItem
        }
    }

}