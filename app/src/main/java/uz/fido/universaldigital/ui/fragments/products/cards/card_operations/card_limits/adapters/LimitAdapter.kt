package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_limits.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.limits.SvLimit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemLimitBinding
import uz.fido.utils.utility.format.Format

class LimitAdapter(
    private val list: ArrayList<SvLimit>,
    private var baseInterface: BaseInterface
) : RecyclerView.Adapter<LimitAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLimitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SvLimit) {
            binding.limitType.text = item.dsc
            binding.limitAmount.text = Format.formatAmount(item.limitAmount.toBigDecimal().divide(100.toBigDecimal()).toString()) + " UZS"
            binding.limitDate.text = itemView.context.getString(R.string.used_amount) + " : " + item.usedAmount + " UZS"
            binding.father.setOnClickListener {
                baseInterface.openLimitItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLimitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}