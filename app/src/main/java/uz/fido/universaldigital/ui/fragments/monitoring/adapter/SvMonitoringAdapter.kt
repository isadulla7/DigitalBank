package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.UzcardItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface

class SvMonitoringAdapter(
    private var context: Context, private var consolidatedList: ArrayList<ListItem>,
    private val onCLick: (SVMonitoringItem) -> Unit
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
            (holder as GeneralItemViewHolder).bind(item, position)
        }
    }

    inner class GeneralItemViewHolder(private val binding: ItemMonitoringBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem, position: Int) {

            val svMonitoringItem: UzcardItem = item as UzcardItem
            val monitoringItem = svMonitoringItem.svMonitoringItem
            val name = monitoringItem?.merchant_name.toString()

            binding.tvName.text = name


            binding.tvTime.text = monitoringItem?.tran_date?.substring(10, 15)
            val symbol = if (monitoringItem?.tran_type == "credit") {
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.monitoring_amount))
                "+"
            } else {
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                "-"
            }

            binding.tvAmount.text = symbol + Format.formatAmount((monitoringItem!!.tran_amount.toDouble() / 100).toString()).replace(".0", "") + " " + context.getString(R.string.uzs)
            if (monitoringItem.tran_type == "credit") {
                binding.icon.setImageResource(R.drawable.ic_monitoring_plus)
                binding.tvType.text = if (monitoringItem.card_num.length > 4) {
                    "${context.getString(R.string.card)} •••• ${
                        monitoringItem.card_num.substring(
                            monitoringItem.card_num.length - 4,
                            monitoringItem.card_num.length
                        )
                    }"
                } else monitoringItem.card_num
            } else {
                binding.icon.setImageResource(R.drawable.icon_monitoring)
                binding.tvType.text = if (monitoringItem.card_num.length > 4) {
                    "${context.getString(R.string.card)} •••• ${
                        monitoringItem.card_num.substring(
                            monitoringItem.card_num.length - 4,
                            monitoringItem.card_num.length
                        )
                    }"
                } else monitoringItem.card_num
            }

            if (monitoringItem.reversal == "true") {
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                binding.tvAmount.paintFlags = binding.tvType.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvAmount.alpha = 0.5f
                binding.tvCansel.text = context.getString(R.string.canselled)
                binding.icon.setImageResource(R.drawable.icon_cansel_monitoring)
            } else {
                binding.tvAmount.alpha = 1f
                binding.tvCansel.text = ""
                binding.icon.setImageResource(if (monitoringItem.tran_type == "credit") R.drawable.ic_monitoring_plus else R.drawable.icon_monitoring)
                binding.tvAmount.paintFlags = 0
            }
            binding.father.setOnClickListener {
                onCLick.invoke(monitoringItem)

            }

        }

    }

    inner class DateViewHolder(private val binding: ItemHistoriesHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem) {
            val dateItem: DateItem = item as DateItem
            val date = dateItem.date
            binding.dateView.text = Format.monitoringDate(date.toString())
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