package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ItemFastAccessVisibleBinding
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.utils.utility.view.recycler_view_drag.ItemTouchHelperAdapter
import java.util.Collections

class FastAccessVisibleAdapter(
    private val list: ArrayList<FastAccessOperation>,
    private val listener: (Int) -> Unit,
) : RecyclerView.Adapter<FastAccessVisibleAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    inner class ViewHolder(private val binding: ItemFastAccessVisibleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FastAccessOperation, position: Int) {
            binding.icon.setImageResource(itemView.context.getDrawableFromRes(item.icon))
            binding.fatherItem.setOnClickListener { listener.invoke(position) }
            binding.itemName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFastAccessVisibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (list[fromPosition].id != 0) {
            var headerPosition = 0
            for (i in 0 until list.size) {
                if (list[i].id == 0) {
                    headerPosition = i
                }
            }
            if (fromPosition < list.size && toPosition < list.size) {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(list, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(list, i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
            }
            return true
        } else {
            return false
        }
    }

    override fun onItemDismiss(position: Int) {
    }
}