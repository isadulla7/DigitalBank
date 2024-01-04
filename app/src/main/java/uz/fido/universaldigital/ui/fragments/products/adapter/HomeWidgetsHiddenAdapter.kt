package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemWidgetVisibleBinding

class HomeWidgetsHiddenAdapter(
    private val list: ArrayList<MainWidget>,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<HomeWidgetsHiddenAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWidgetVisibleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MainWidget) {
            binding.icon.setImageResource(R.drawable.ic_plus_green)
            binding.move.visibility = View.GONE
            binding.icon.setOnClickListener { baseInterface.addToVisibleWidgets(adapterPosition) }
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
}