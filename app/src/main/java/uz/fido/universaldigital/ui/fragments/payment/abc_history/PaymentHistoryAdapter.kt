package uz.fido.universaldigital.ui.fragments.payment.abc_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemRequisitesHistoryBinding
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO
import uz.fido.utils.const.ServiceId
import uz.fido.utils.format.Format

@SuppressLint("SetTextI18n")
class PaymentHistoryAdapter(
    private val onClickListener: (Int) -> Unit
) : ListAdapter<LocalMonitoring, PaymentHistoryAdapter.ViewHolder>(MyDiffUtil()) {

    inner class ViewHolder(private val binding: ItemRequisitesHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocalMonitoring) {
            binding.apply {
                accountCode.text = item.name
                serviceName.text = item.created_date
                paymentAmount.text =
                    Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + " UZS"
                paymentName.text = setAccountCode(item)
                setIcon(this, item)
                father.setOnClickListener {
                    onClickListener.invoke(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRequisitesHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun setAccountCode(item: LocalMonitoring): String {
        return when (item.service_id) {
            ServiceId.SERVICE_ID__4 -> {
                item.searchData?.params?.get("RECEIVER_ACCOUNT") ?: ""
            }

            else -> item.partner_obj
        }
    }

    private fun setIcon(binding: ItemRequisitesHistoryBinding, item: LocalMonitoring) {
        Picasso.get().load(PAYNET_PHOTO + item.icon_name)
            .error(R.drawable.ic_outcome_icon).into(binding.icon)
    }

    class MyDiffUtil : DiffUtil.ItemCallback<LocalMonitoring>() {
        override fun areItemsTheSame(oldItem: LocalMonitoring, newItem: LocalMonitoring): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LocalMonitoring, newItem: LocalMonitoring
        ): Boolean {
            return oldItem == newItem
        }
    }

}