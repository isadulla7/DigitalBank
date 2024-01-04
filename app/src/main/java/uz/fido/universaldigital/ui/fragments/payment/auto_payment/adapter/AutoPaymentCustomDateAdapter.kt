package uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ItemAutopaymentCustomDateBinding

class AutoPaymentCustomDateAdapter(
    private var list: ArrayList<String>,
    private val onClick:(String)->Unit
) : RecyclerView.Adapter<AutoPaymentCustomDateAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAutopaymentCustomDateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.textDay.text = item
            binding.father.setOnClickListener {
                onClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutopaymentCustomDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(daysList: ArrayList<String>) {
        list = daysList
        notifyDataSetChanged()
    }
}