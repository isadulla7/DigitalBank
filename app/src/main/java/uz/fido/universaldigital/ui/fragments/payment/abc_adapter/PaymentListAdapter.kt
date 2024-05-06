package uz.fido.universaldigital.ui.fragments.payment.abc_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemPaymentListBinding
import uz.fido.universaldigital.ui.utils.extensions.setHtmlText
import uz.fido.universaldigital.ui.utils.keys.Keys

class PaymentListAdapter(
    private var itemClickListener: (PaymentService) -> Unit
) : ListAdapter<PaymentService, PaymentListAdapter.ViewHolder>(
    MyDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPaymentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(private var binding: ItemPaymentListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: PaymentService) {
            binding.apply {
                itemName.setHtmlText(item.nameIndex.toString())
                Picasso.get()
                    .load(Keys.paynetPhotoUrl() + item.icon_name)
                    .error(R.drawable.ic_payments_placeholder).into(icon)
                father.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<PaymentService>() {
        override fun areItemsTheSame(oldItem: PaymentService, newItem: PaymentService): Boolean {
            return oldItem.group_code == newItem.group_code
        }

        override fun areContentsTheSame(
            oldItem: PaymentService, newItem: PaymentService
        ): Boolean {
            return oldItem.service_id == newItem.service_id
        }
    }

}