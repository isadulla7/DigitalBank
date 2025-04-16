package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemHomePopularTransferBinding
import uz.fido.universaldigital.ui.fragments.transfers.utils.getShortNameFormatted
import uz.fido.universaldigital.ui.fragments.transfers.utils.getUserNameFormatted

class HomePopularTransferAdapter(
    private val baseInterface: BaseInterface,
    private var list: ArrayList<PopularTransfers>
) : RecyclerView.Adapter<HomePopularTransferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomePopularTransferBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private val binding: ItemHomePopularTransferBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PopularTransfers) {
            binding.name.text = getUserNameFormatted(item.empbossed_name)
            binding.shortName.text = getShortNameFormatted(item.empbossed_name)

            itemView.setOnClickListener {
                baseInterface.openTransferList(item)
            }
        }
    }

    fun setList(popularTransfers: ArrayList<PopularTransfers>) {
        list = popularTransfers
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

}