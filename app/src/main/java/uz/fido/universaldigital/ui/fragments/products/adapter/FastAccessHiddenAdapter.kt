package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ItemFastAccessHiddenBinding
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes

class FastAccessHiddenAdapter(
    private val list: ArrayList<FastAccessOperation>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<FastAccessHiddenAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFastAccessHiddenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FastAccessOperation, position: Int) {
            binding.icon.setImageResource(itemView.context.getDrawableFromRes(item.icon))
            binding.fatherItem.setOnClickListener { listener.invoke(position) }
            binding.itemName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFastAccessHiddenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}