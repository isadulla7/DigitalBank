package uz.fido.universaldigital.ui.fragments.monitoring.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.WalletHistoryItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface

class WalletMonitoringAdapter(
    private var consolidatedList: ArrayList<ListItem>,
    private val itemClickListener: (AccountHistory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListItem.TYPE_DATE) {
            DateViewHolder(ItemHistoriesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            GeneralItemViewHolder(ItemMonitoringBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int = consolidatedList[position].type

    override fun getItemCount() = consolidatedList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = consolidatedList[position]
        if (holder is DateViewHolder) {
            holder.bind(item)
        } else {
            (holder as GeneralItemViewHolder).bind(item)
        }
    }

    inner class GeneralItemViewHolder(private val binding: ItemMonitoringBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem) {
            val walletItem: WalletHistoryItem = item as WalletHistoryItem
            walletItem.walletItem?.let { walletMonitoringItem ->
                binding.tvName.setTransactionName(walletMonitoringItem)
                binding.tvTime.setTransactionTime(walletMonitoringItem)
                binding.tvAmount.setTextColor(itemView.context, walletMonitoringItem)
                binding.tvAmount.setTransactionAmount(walletMonitoringItem)
                binding.icon.setMonitoringImage(walletMonitoringItem)
                binding.father.setOnClickListener {
                    itemClickListener.invoke(walletMonitoringItem)
                }
            }
        }
    }

    inner class DateViewHolder(private val binding: ItemHistoriesHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem) {
            val dateItem: DateItem = item as DateItem
            val date = dateItem.date
            binding.dateView.text = Format.monitoringDate(date.toString())
        }
    }


    override fun headerPositionForItem(itemPosition: Int): Int {
        for (i in itemPosition downTo 1) {
            if (isHeader(i)) {
                return i
            }
        }
        return 0
    }

    override fun headerLayout(headerPosition: Int): Int = R.layout.item_histories_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val dateItem: DateItem = consolidatedList[headerPosition] as DateItem
        val date: TextView = header.findViewById(R.id.date_view)
        date.text = Format.monitoringDate(dateItem.date.toString())
    }

    override fun isHeader(itemPosition: Int): Boolean = getItemViewType(itemPosition) == ListItem.TYPE_DATE

    fun setListAdapter(totalList: ArrayList<ListItem>) {
        consolidatedList = totalList
        notifyDataSetChanged()
    }

    fun removeList() {
        consolidatedList.clear()
        notifyDataSetChanged()
    }
}