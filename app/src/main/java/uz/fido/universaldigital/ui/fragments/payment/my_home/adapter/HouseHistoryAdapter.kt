package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.home.HomeGeneralItem
import uz.fido.network.domain.model.monitoring.home.ItemHomeHistory
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.universaldigital.databinding.ItemMonitoringBinding
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO
import uz.fido.utils.format.Format
import uz.fido.utils.sticky.StickyHeaderInterface
import uz.fido.utils.view.custom_text_view.TextViewRegular


class HouseHistoryAdapter(
    private var context: Context,
    private var consolidatedList: ArrayList<ListItem>,
    private val template: Template,
    private val onCLickView: (ItemHomeHistory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface {

    inner class GeneralItemViewHolder(private val binding: ItemMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ListItem, position: Int) {
            val svMonitoringItem: HomeGeneralItem = item as HomeGeneralItem
            val monitoringItem = svMonitoringItem.itemHomeHistory
            binding.tvName.text = template.name
            binding.tvTime.text = monitoringItem?.create_date?.substring(10, 16)
            binding.icon.setPadding(0, 0, 0, 0)
            if (template.icon_name != "")
                Picasso.get().load(PAYNET_PHOTO + template.icon_name).error(
                    R.drawable.ic_payments_placeholder
                )
                    .into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)
//            binding.icon.setImageResource(getPaymentGroupIcon(template.service_group_id.toString()))
            binding.tvAmount.text =
                "+ " + Format.formatAmount((monitoringItem!!.amount.toDouble() / 100).toString()) + " " + context.getString(
                    R.string.uzs
                )

            binding.father.setOnClickListener {
                onCLickView.invoke(monitoringItem)
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

    override fun getItemViewType(position: Int) = consolidatedList[position].type

    fun setList(list: ArrayList<ListItem>) {
        list.forEach {
            if (!consolidatedList.contains(it)) {
                consolidatedList.add(it)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = consolidatedList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = consolidatedList[position]
        if (holder is DateViewHolder) {
            holder.bind(item)
        } else {
            (holder as GeneralItemViewHolder).bind(item, position)
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

    override fun headerLayout(headerPosition: Int) = R.layout.item_histories_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val dateItem: DateItem = consolidatedList[headerPosition] as DateItem
        val date: TextViewRegular = header.findViewById(R.id.date_view)
        date.text = Format.monitoringDate(dateItem.date.toString())
    }

    override fun isHeader(itemPosition: Int) = getItemViewType(itemPosition) == ListItem.TYPE_DATE
    fun setListAdapter(totalList: java.util.ArrayList<ListItem>) {
        consolidatedList = totalList
        notifyDataSetChanged()
    }
}