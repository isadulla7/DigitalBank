package uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemAutopaymentDayBinding


class AutoPaymentDaysAdapter(
    private val baseInterface: BaseInterface,
    private var list: ArrayList<AllServiceLists>
) : RecyclerView.Adapter<AutoPaymentDaysAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAutopaymentDayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllServiceLists) {
            binding.textDay.text = item.name
            if (item.isSelected) {
                binding.imageDelete.setImageResource(R.drawable.check_construktor)
            } else {
                binding.imageDelete.setImageResource(R.drawable.check_box_color)
            }
            binding.father.setOnClickListener {
                if (item.isSelected) {
                    binding.imageDelete.setImageResource(R.drawable.check_box_color)
                    item.isSelected = false
                } else {
                    binding.imageDelete.setImageResource(R.drawable.check_construktor)
                    item.isSelected = true
                }
                baseInterface.justOperation()
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutopaymentDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(daysList: ArrayList<AllServiceLists>) {
        list = daysList
        notifyDataSetChanged()
    }
}