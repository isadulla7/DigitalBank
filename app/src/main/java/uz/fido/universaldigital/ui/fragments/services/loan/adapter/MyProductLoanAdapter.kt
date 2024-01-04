package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyProductCreditBinding

class MyProductLoanAdapter(
    private var list: ArrayList<CreditProduct>,
    private var context: Context,
    private val baseInterface: BaseInterface
):RecyclerView.Adapter<MyProductLoanAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemMyProductCreditBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(creditProduct: CreditProduct) {

            itemView.setOnClickListener {
                if (creditProduct.is_reacted==0){
                     baseInterface.openClientCreditPage(creditProduct)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyProductCreditBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun getItemCount()=list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.onBind(list[position])
    }

    fun setList(data: ArrayList<CreditProduct>) {
        list.clear()
        list=data
        notifyDataSetChanged()
    }

}