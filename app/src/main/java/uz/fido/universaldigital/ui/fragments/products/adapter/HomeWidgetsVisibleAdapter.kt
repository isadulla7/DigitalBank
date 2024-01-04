package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemWidgetVisibleBinding
import uz.fido.utils.utility.view.recycler_view_drag.ItemTouchHelperAdapter
import java.util.Collections

class HomeWidgetsVisibleAdapter(
    private val list: ArrayList<MainWidget>,
    private val baseInterface: BaseInterface,
) : RecyclerView.Adapter<HomeWidgetsVisibleAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    inner class ViewHolder(private val binding: ItemWidgetVisibleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MainWidget) {
            binding.icon.setImageResource(R.drawable.ic_minus_red)
            binding.icon.setOnClickListener { baseInterface.addToHiddenWidgets(adapterPosition) }
            binding.title.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemWidgetVisibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (list[fromPosition].id != 0) {
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

    override fun onItemDismiss(position: Int) {}
}