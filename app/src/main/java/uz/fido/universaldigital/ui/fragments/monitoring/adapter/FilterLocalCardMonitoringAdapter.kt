package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemFilterMonitoringCardBinding
import uz.fido.universaldigital.databinding.ItemMonitoringFilterCardBinding
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.format.Format

class FilterLocalCardMonitoringAdapter(
    var list: ArrayList<FilterCard>,
    private val context: Context,
    private val baseInterface: BaseInterface
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            VH(ItemMonitoringFilterCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            VHText(ItemFilterMonitoringCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].type == 0) {
            (holder as VH).onBind(list[position])
        } else {
            (holder as VHText).onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    fun setListItem(response: ArrayList<FilterCard>) {
        list = response
        notifyDataSetChanged()
    }


    inner class VHText(private val binding: ItemFilterMonitoringCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(filterCard: FilterCard) {
            binding.cardText.text = filterCard.object_name
        }
    }

    inner class VH(private val binding: ItemMonitoringFilterCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(itemId: FilterCard) {
            binding.cardName.text = itemId.object_value
            binding.status.text = itemId.object_name

            when (itemId.object_type) {
                UZCARD -> binding.icon.setImageResource(R.drawable.uzcard_monitoring)
                HUMO_CARD -> binding.icon.setImageResource(R.drawable.humo_monitoring)
                CURRENCY_CARD -> binding.icon.setImageResource(R.drawable.master_card)
                else -> {}
            }

            if (!itemId.is_selected_monitoring) {
                binding.option.setImageResource(R.drawable.check_construktor)
            } else binding.option.setImageResource(R.drawable.check_box_color)
            binding.father.setOnClickListener {
                baseInterface.monitoringFilterCard(filterCard = itemId)
            }
        }
    }

}