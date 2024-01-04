package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.HumoItem
import uz.fido.network.domain.model.monitoring.VisaItem
import uz.fido.network.domain.model.monitoring.WalletHistoryItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface

class WalletMonitoringAdapter(
    private var context: Context,
    private var consolidatedList: ArrayList<ListItem>,
    private val onClickView:(AccountHistory)->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListItem.TYPE_DATE) {
            DateViewHolder(
                ItemHistoriesHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            GeneralItemViewHolder(
                ItemMonitoringBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int = consolidatedList[position].type

    override fun getItemCount() = consolidatedList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = consolidatedList[position]
        if (holder is WalletMonitoringAdapter.DateViewHolder) {
            holder.bind(item)
        } else {
            (holder as WalletMonitoringAdapter.GeneralItemViewHolder).bind(item)
        }
    }

    inner class GeneralItemViewHolder(private val binding: ItemMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem) {
            val walletItem: WalletHistoryItem = item as WalletHistoryItem
            val monitoringItem = walletItem.walletItem
            binding.tvName.text =  monitoringItem?.coAccName
            monitoringItem?.dateExecute?.let {
                binding.tvTime.text = it.substring(10, 16)
            }
            val currencyChar = when (walletItem.walletItem?.dtAcc?.substring(5, 8)) {
                "000" -> {
                    "UZS"
                }
                "643" -> {
                    "RUB"
                }
                else -> {
                    "USD"
                }
            }

            if (monitoringItem?.debit == "0") {
                binding.icon.setImageResource(R.drawable.ic_monitoring_plus)
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.monitoring_amount))
                binding.tvAmount.text =
                    "+ ${Format.formatAmount(monitoringItem.credit.toString())} $currencyChar"
            }else {
                binding.icon.setImageResource(R.drawable.icon_monitoring)
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                binding.tvAmount.text =
                    "- ${Format.formatAmount(monitoringItem?.debit)} $currencyChar"
            }

            binding.father.setOnClickListener {
                onClickView.invoke(monitoringItem!!)
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


    fun setListAdapter(totalList: ArrayList<ListItem>) {
        consolidatedList = totalList
        notifyDataSetChanged()
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

    override fun isHeader(itemPosition: Int): Boolean =
        getItemViewType(itemPosition) == ListItem.TYPE_DATE

    fun removeList() {
        consolidatedList.clear()
        notifyDataSetChanged()
    }
}