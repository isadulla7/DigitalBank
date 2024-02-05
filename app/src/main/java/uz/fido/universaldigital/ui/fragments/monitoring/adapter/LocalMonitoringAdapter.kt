package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.GeneralItem
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface
import java.math.BigDecimal

class LocalMonitoringAdapter(
    private var context: Context,
    private var consolidatedList: ArrayList<ListItem>,
    private val onClick: (LocalMonitoring) -> Unit
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

    override fun getItemCount(): Int = consolidatedList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = consolidatedList[position]
        if (holder is DateViewHolder) {
            holder.bind(item)
        } else {
            (holder as GeneralItemViewHolder).bind(item)
        }
    }

    inner class GeneralItemViewHolder(private val binding: ItemMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem) {
            val svMonitoringItem: GeneralItem = item as GeneralItem
            val monitoringItem = svMonitoringItem.svMonitoringItem

            binding.father.setOnClickListener {
                onClick.invoke(monitoringItem!!)
            }

            val name = Format.firstLetterUpperCase(monitoringItem!!.name)
            val newName =
                if (name.isNotEmpty()) name.substring(
                    0,
                    1
                ) + name.substring(2) else context.getString(R.string.no_name)
            binding.tvName.text = newName
            binding.tvTime.text = if (monitoringItem.created_date.length == 19)
                monitoringItem.created_date.substring(10, 16)
            else monitoringItem.created_date

            val symbol = if (monitoringItem.tran_type == "credit") {
                binding.tvAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.monitoring_amount
                    )
                )
                "+"
            } else {
                binding.tvAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.mainTextColor
                    )
                )
                "-"
            }

            if (monitoringItem.service_id == "-1")
                if (monitoringItem.tran_type == "credit") {
                    binding.tvType.text =
                        if (monitoringItem.partner_obj.length == 16) Format.formatCardNumberMonitoring(
                            context,
                            monitoringItem.partner_obj
                        ) else monitoringItem.partner_obj
                } else {
                    binding.tvType.text =
                        if (monitoringItem.object_value.length == 16) Format.formatCardNumberObjectMonitoring(
                            context,
                            monitoringItem.object_value
                        ) else monitoringItem.object_value
                }
            else binding.tvType.text = context.getText(R.string.payment)

//            if (monitoringItem.service_id=="-1")
//                if (monitoringItem.partner_obj.isEmpty()) binding.tvType.text =
//                    Format.formatCardNumberMonitoring(context,monitoringItem.object_value)
//            Picasso.get().load(PAYNET_PHOTO + monitoringItem.icon_name)
//                .error(R.drawable.ic_payments_placeholder)
//                .into(binding.icon)

            val sum = BigDecimal(100)
            binding.tvAmount.text =
                "$symbol ${
                    Format.formatAmount((monitoringItem.amount.toBigDecimal() / sum).toString())
                        .replace(".0", "")
                } ${Format.currencyCode(monitoringItem.currency_code)}"

        }

    }

    inner class DateViewHolder(private val binding: ItemHistoriesHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem) {
            val dateItem: DateItem = item as DateItem
            val date = dateItem.date
            binding.dateView.text = date?.let { Format.monitoringDate(it) }
        }

    }

    override fun getItemViewType(position: Int): Int = consolidatedList[position].type

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