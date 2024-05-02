package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMonitoringFilterCardBinding
import uz.fido.utils.format.Format

class FilterCardMonitoringAdapter(private val context: Context, private val baseInterface: BaseInterface) :
    ListAdapter<FilterCard,
            FilterCardMonitoringAdapter.VH>(FilterCallback()) {


    inner class VH(private val binding: ItemMonitoringFilterCardBinding) :
        ViewHolder(binding.root) {
        fun onBind(itemId: FilterCard) {
            binding.cardName.text =
                if (itemId.object_value.length == 16) Format.formatCardNumberMonitoring(
                    context,
                    itemId.object_value
                ) else itemId.object_value

            binding.status.text = if (itemId.state == "A")
                "${context.getString(R.string.status)}: ${context.getString(R.string.activ)}"
            else
                "${context.getString(R.string.status)}: ${context.getString(R.string.no_activ)}"

            when (itemId.object_type) {
                "SV" -> binding.icon.setImageResource(R.drawable.uzcard_monitoring)
                "GL" -> binding.icon.setImageResource(R.drawable.humo_monitoring)
                "TET" -> binding.icon.setImageResource(R.drawable.master_card)
            }

            if (!itemId.is_selected_monitoring) {
                binding.option.setImageResource(R.drawable.check_construktor)

            } else binding.option.setImageResource(R.drawable.check_box_color)
            binding.father.setOnClickListener {
                baseInterface.monitoringFilterCard(filterCard = itemId)
            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH =
        VH(
            ItemMonitoringFilterCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position))
    }
}

class FilterCallback : DiffUtil.ItemCallback<FilterCard>() {
    override fun areItemsTheSame(oldItem: FilterCard, newItem: FilterCard) = oldItem == newItem

    override fun areContentsTheSame(oldItem: FilterCard, newItem: FilterCard) = oldItem.object_id == newItem.object_id
}


