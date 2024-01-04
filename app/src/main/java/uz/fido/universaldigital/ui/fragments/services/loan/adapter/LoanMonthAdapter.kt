package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.constructor_loan_month.view.textView
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ConstructorLoanMonthBinding
import uz.fido.universaldigital.ui.fragments.services.loan.modul.LoanMonth
import uz.fido.utils.view.custom_text_view.TextViewRegular

@SuppressLint("SetTextI18n")
class LoanMonthAdapter(
    private val onClickView:(Int,String)->Unit,
    private val list: ArrayList<LoanMonth>,
    private val context:Context,
    private val type:String,
) : RecyclerView.Adapter<LoanMonthAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
   return ViewHolder(ConstructorLoanMonthBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }




    inner class ViewHolder(private val view: ConstructorLoanMonthBinding) : RecyclerView.ViewHolder(view.root) {

        fun onBind(loanMonth: LoanMonth) {
             if (type=="month"){
          view.month.text="${loanMonth.count} ${context.getString(uz.fido.utils.R.string.month)}"
          view.time.text="${loanMonth.term}"
             }else if (type=="day"){
                 view.month.text="${loanMonth.count}"
                 view.time.text="${loanMonth.term}"
             }else{
                 view.month.text="${loanMonth.count}"
                 view.time.text="${loanMonth.term}"
             }
           view.textView.setOnClickListener {
               onClickView.invoke(loanMonth.count,type)
           }
        }

    }

}