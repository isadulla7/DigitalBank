package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.loans.loan_groups.CreditGroup
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemLoanGroupBinding
import uz.fido.utils.utility.format.Format

class LoanGroupAdapter(private val context: Context,
                       private val onClick:(CreditGroup)->Unit) : ListAdapter<CreditGroup, LoanGroupAdapter.LoanVh>(LoanCallback()) {


    inner class LoanVh(private val binding: ItemLoanGroupBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(item: CreditGroup) {
            binding.tvName.text = item.name
            binding.loanAmount.text= "${Format.formatAmount(Format.convertFromTiynDivide(item.maxSumma))}  ${context.getString(R.string.summa)}"
            binding.loanPercent.text = "${item.percentMin}%"
            binding.loanTime.text="${item.time_max} ${context.getString(uz.fido.utils.R.string.month)}"
            binding.father.setOnClickListener {
                onClick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanVh {
        return LoanVh(ItemLoanGroupBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: LoanVh, position: Int) {
        holder.onBind(getItem(position))
    }
}
class LoanCallback:DiffUtil.ItemCallback<CreditGroup>(){
    override fun areItemsTheSame(oldItem: CreditGroup, newItem: CreditGroup): Boolean=newItem==oldItem

    override fun areContentsTheSame(oldItem: CreditGroup, newItem: CreditGroup)=oldItem.groupId==newItem.groupId

}