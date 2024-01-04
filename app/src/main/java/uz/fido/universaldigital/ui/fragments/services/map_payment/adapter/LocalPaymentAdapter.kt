package uz.fido.universaldigital.ui.fragments.services.map_payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemLocalPaymentBinding

class LocalPaymentAdapter(
    private var list: ArrayList<LocalPayment>,
    private val context: Context,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<LocalPaymentAdapter.LocalPaymentVh>() {

    inner class LocalPaymentVh(private val binding: ItemLocalPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(localPayment: LocalPayment) {
            binding.distance.text = localPayment.distance + " km"
            binding.paymentName.text = localPayment.sv_merchant_name
            binding.address.text = localPayment.address
            binding.father.setOnClickListener {
                baseInterface.openLocalPayment(localPayment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalPaymentVh {
        return LocalPaymentVh(
            ItemLocalPaymentBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LocalPaymentVh, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(it: ArrayList<LocalPayment>) {
        list = it
        notifyDataSetChanged()
    }
}