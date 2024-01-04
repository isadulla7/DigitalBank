package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


import uz.fido.network.domain.model.loans.loan_graph.CreditGraph
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemLoanGraphBinding
import uz.fido.utils.utility.format.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreditGraphAdapter(
    private val list: ArrayList<CreditGraph>,
    private val context: Context,
    private val overdueDate: ArrayList<String>
) : RecyclerView.Adapter<CreditGraphAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLoanGraphBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CreditGraph) {
            if (item.recommendedAmount.isNotEmpty()) {
                binding.amountMain.text = Format.formatAmount(Format.convertFromTiynDivide(item.recommendedAmount)) + " сум"
            } else {
                binding.amountMain.text = Format.formatAmount(Format.convertFromTiynDivide(item.interestOnTermDebt)) + " сум"
            }

            if (item.recommendedAmount.isNotEmpty() && item.interestOnTermDebt.isNotEmpty()){
                binding.principalAmount.text=Format.formatAmount(Format.convertFromTiynDivide(item.amount))+" сум"
            }

            if (item.saldo.isNotEmpty()){
                binding.remainder.text=Format.formatAmount(Format.convertFromTiynDivide(item.saldo)) + " сум"
            }
            if (item.interestOnTermDebt.isNotEmpty()){
                binding.perc.text=Format.formatAmount(Format.convertFromTiynDivide(item.interestOnTermDebt)) + " сум"
            }

            binding.tvCount.text=item.position.toString()
            binding.time.text = item.repaymentDate
            val c: Calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val getCurrentDate: String = sdf.format(c.time)

            if (sdf.parse(getCurrentDate) > sdf.parse(item.repaymentDate)) {
                binding.loanStatus.visibility = View.VISIBLE
                if (overdueDate.contains(item.repaymentDate)) {
                    binding.status.text=context.getString(R.string.no_payed)
                    binding.status.setTextColor(ContextCompat.getColor(context,R.color.status_waiting))
                } else {
                    binding.status.text=context.getString(R.string.payed)
                    binding.status.setTextColor(ContextCompat.getColor(context,R.color.monitoring_amount))
                }
            } else {

                binding.loanStatus.visibility = View.GONE
            }

            itemView.setOnClickListener {
             //   baseInterface.openNewPage(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoanGraphBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}