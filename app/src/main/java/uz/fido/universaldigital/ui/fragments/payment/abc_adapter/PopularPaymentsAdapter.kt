package uz.fido.universaldigital.ui.fragments.payment.abc_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemPopularPaymentBinding

class PopularPaymentsAdapter(
    private var list: ArrayList<LocalMonitoring>,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<PopularPaymentsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemPopularPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocalMonitoring) {
            binding.tvName.text = item.partner_obj
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPopularPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            baseInterface.setPaymentParams(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(popularPaymentsList: ArrayList<LocalMonitoring>) {
        list = popularPaymentsList
        notifyDataSetChanged()
    }

}