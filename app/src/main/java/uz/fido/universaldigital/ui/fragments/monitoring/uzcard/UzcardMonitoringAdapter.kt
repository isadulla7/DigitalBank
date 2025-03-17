package uz.fido.universaldigital.ui.fragments.monitoring.uzcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.UzcardItem
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.universaldigital.ui.fragments.monitoring.extensions.setAdditionalInfo
import uz.fido.universaldigital.ui.fragments.monitoring.extensions.setCancelledInfo
import uz.fido.universaldigital.ui.fragments.monitoring.extensions.setMonitoringAmount
import uz.fido.universaldigital.ui.fragments.monitoring.extensions.setMonitoringImage
import uz.fido.universaldigital.ui.fragments.monitoring.extensions.setTextColor
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface

class UzcardMonitoringAdapter(
    private var consolidatedList: ArrayList<ListItem>,
    private val onItemClickListener: (UzcardMonitoringItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListItem.TYPE_DATE) {
            DateViewHolder(ItemHistoriesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            GeneralItemViewHolder(ItemMonitoringBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int = consolidatedList[position].type

    override fun getItemCount(): Int = consolidatedList.size

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
            val monitoringData: UzcardItem = item as UzcardItem
            monitoringData.uzcardMonitoringItem?.let { uzcardMonitoringItem ->
                binding.tvName.text = uzcardMonitoringItem.merchantName
                binding.tvTime.text = uzcardMonitoringItem.transactionDate.substring(10, 15)
                binding.tvAmount.setTextColor(itemView.context, uzcardMonitoringItem)
                binding.tvAmount.setMonitoringAmount(itemView.context, uzcardMonitoringItem)
                binding.icon.setMonitoringImage(uzcardMonitoringItem)
                binding.tvType.setAdditionalInfo(itemView.context, uzcardMonitoringItem)
                binding.tvAmount.setCancelledInfo(itemView.context, binding.icon, binding.tvCancel, uzcardMonitoringItem)
                binding.father.setOnClickListener {
                    onItemClickListener.invoke(uzcardMonitoringItem)
                }
            }
        }
    }

    inner class DateViewHolder(private val binding: ItemHistoriesHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem) {
            val dateItem: DateItem = item as DateItem
            binding.dateView.text = Format.monitoringDate(dateItem.date.toString())
        }
    }

    override fun headerPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        for (i in itemPosition downTo 1) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition
    }

    override fun headerLayout(headerPosition: Int): Int = R.layout.item_histories_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val dateItem: DateItem = consolidatedList[headerPosition] as DateItem
        val date: TextView = header.findViewById(R.id.date_view)
        date.text = Format.monitoringDate(dateItem.date.toString())
    }

    override fun isHeader(itemPosition: Int): Boolean = getItemViewType(itemPosition) == ListItem.TYPE_DATE

    fun removeList() {
        consolidatedList.clear()
        notifyDataSetChanged()
    }

    fun setListAdapter(totalList: ArrayList<ListItem>) {
        consolidatedList = totalList
        notifyDataSetChanged()
    }

}