package uz.fido.universaldigital.ui.fragments.payment.abc_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.Cheque
import uz.fido.universaldigital.databinding.ItemCheckBinding


class ChequeAdapter : ListAdapter<Cheque, ChequeAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(private val itemCheckBinding: ItemCheckBinding) : RecyclerView.ViewHolder(itemCheckBinding.root) {
        fun onBind(item: Cheque) {
            itemCheckBinding.apply {
                if (item.key_description.isNotEmpty()) code.text = item.key_description
                else code.text = item.key
                value.text = item.value
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Cheque>() {
        override fun areItemsTheSame(oldItem: Cheque, newItem: Cheque): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Cheque, newItem: Cheque): Boolean {
            return oldItem == newItem
        }
    }
}