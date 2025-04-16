package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ConstructorLoanMonthBinding
import uz.fido.universaldigital.ui.fragments.services.loan.modul.LoanMonth

@SuppressLint("SetTextI18n")
class LoanMonthAdapter(
    private val onClickView: (Int, String) -> Unit,
    private val list: ArrayList<LoanMonth>,
    private val type: String,
) : RecyclerView.Adapter<LoanMonthAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstructorLoanMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(private val view: ConstructorLoanMonthBinding) : RecyclerView.ViewHolder(view.root) {

        fun onBind(loanMonth: LoanMonth) {
            when (type) {
                "month" -> {
                    view.month.text = "${loanMonth.count} ${itemView.context.getString(uz.fido.utils.R.string.month)}"
                    view.time.text = loanMonth.term
                }

                "day" -> {
                    view.month.text = "${loanMonth.count}"
                    view.time.text = loanMonth.term
                }

                else -> {
                    view.month.text = "${loanMonth.count}"
                    view.time.text = loanMonth.term
                }
            }
            view.textView.setOnClickListener {
                onClickView.invoke(loanMonth.count, type)
            }
        }

    }

}