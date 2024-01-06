package uz.fido.universaldigital.ui.fragments.services.deposit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.databinding.ItemDepositProductBinding
import uz.fido.utils.utility.format.Format

class DepositAdapter(
    private val onClickDeposit: (Deposit) -> Unit
) : ListAdapter<Deposit, DepositAdapter.DepositVh>(CallBackDeposit()) {

    inner class DepositVh(private val binding: ItemDepositProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Deposit) {
            binding.apply {
                depositName.text = item.dep_name
                depositPercent.text = item.percent + "%"
                depositTerm.text =
                    Format().formattedDepositExpire(itemView.context, item.keeping_time)
                depositAmount.text =
                    Format().formattedDepositAmount(itemView.context, item.min_sum.toString())
                itemView.setOnClickListener {
                    onClickDeposit.invoke(item)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositVh {
        return DepositVh(
            ItemDepositProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DepositVh, position: Int) {
        holder.onBind(getItem(position))
    }

}

class CallBackDeposit() : DiffUtil.ItemCallback<Deposit>() {
    override fun areItemsTheSame(oldItem: Deposit, newItem: Deposit) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Deposit, newItem: Deposit) =
        oldItem.dep_id == newItem.dep_id
}
