package uz.fido.universaldigital.ui.fragments.services.deposit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import uz.fido.network.domain.model.deposits.DepositCalculator
import uz.fido.universaldigital.databinding.ItemDepositCalculatorBinding
import uz.fido.utils.utility.format.Format

class DepositCalculateAdapter : ListAdapter<DepositCalculator,DepositCalculateAdapter.ViewHolder>(CalculatorCallback()) {
    var count=0
    inner class ViewHolder(private val binding: ItemDepositCalculatorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DepositCalculator) {
            count++
            binding.principalAmount.text=Format.formatAmount(item.amount) +" сум"
            binding.precentLoan.text=Format.formatAmount(item.receiveSum)+" сум"
            binding.totalPaind.text=Format.formatAmount((item.saldo.toBigDecimal()-item.amount.toBigDecimal()).toString())+" сум"
            binding.tvCount.text=item.count.toString()
            binding.time.text = item.date
            binding.amount.text =" ${Format.formatAmount(item.saldo)} сум"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDepositCalculatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    override fun getItemCount(): Int {
//        return list.size
//    }



}

class CalculatorCallback:DiffUtil.ItemCallback<DepositCalculator>(){
    override fun areItemsTheSame(oldItem: DepositCalculator, newItem: DepositCalculator): Boolean {
        return  oldItem==newItem
    }

    override fun areContentsTheSame(
        oldItem: DepositCalculator,
        newItem: DepositCalculator
    ): Boolean {
        return oldItem.amount==newItem.amount
    }
}