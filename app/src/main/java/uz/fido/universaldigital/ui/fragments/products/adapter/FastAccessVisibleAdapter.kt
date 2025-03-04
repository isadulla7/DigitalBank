package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mckrpk.animatedprogressbar.dpToPx
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemFastAccessVisibleBinding
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import kotlin.math.roundToInt

class FastAccessVisibleAdapter(
    private val list: ArrayList<FastAccessOperation>,
    private val listener: (Int) -> Unit,
) : RecyclerView.Adapter<FastAccessVisibleAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFastAccessVisibleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FastAccessOperation, position: Int) {
            if (item.id == 20) {
                binding.icon.layoutParams.width = dpToPx(56, itemView.context).roundToInt()
                binding.icon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.status_done))
            } else {
                binding.icon.layoutParams.width = dpToPx(28, itemView.context).roundToInt()
                binding.icon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.brandRedColor))
            }
            binding.icon.setImageResource(itemView.context.getDrawableFromRes(item.icon))
            binding.fatherItem.setOnClickListener { listener.invoke(position) }
            binding.itemName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFastAccessVisibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}