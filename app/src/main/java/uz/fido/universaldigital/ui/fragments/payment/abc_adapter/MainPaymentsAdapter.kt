package uz.fido.universaldigital.ui.fragments.payment.abc_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.universaldigital.databinding.ItemPaymentGroupBinding
import uz.fido.universaldigital.ui.utils.extensions.loadPaymentIcon

class MainPaymentsAdapter(private var itemClickListener: (PaymentGroup) -> Unit) :
    ListAdapter<PaymentGroup, MainPaymentsAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val binding = ItemPaymentGroupBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.onBind(getItem(i))
    }

    inner class ViewHolder(private val binding: ItemPaymentGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: PaymentGroup) {
            binding.apply {
                itemName.text = item.name.toString()
                icon.loadPaymentIcon(item.group_code.toString())
                father.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<PaymentGroup>() {
        override fun areItemsTheSame(oldItem: PaymentGroup, newItem: PaymentGroup): Boolean {
            return oldItem.group_code == newItem.group_code
        }

        override fun areContentsTheSame(
            oldItem: PaymentGroup, newItem: PaymentGroup
        ): Boolean {
            return oldItem.group_code == newItem.group_code
        }
    }

}