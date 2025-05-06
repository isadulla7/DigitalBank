package uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemMySubscriptionBinding
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.CardConst.STATE_ACTIVE
import uz.fido.utils.format.Format

@SuppressLint("SetTextI18n")
class AutoPaymentsAdapter(
    private var list: ArrayList<AutoPayment>,
    private val context: Context,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<AutoPaymentsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMySubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AutoPayment) {
            binding.paymentAmount.text =
                Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + " " +
                        itemView.context.getString(uz.fido.utils.R.string.sum)
            binding.textDate.text =
                if (item.state == STATE_ACTIVE) itemView.context.getString(R.string.active) else itemView.context.getString(
                    R.string.inactive
                )

            if (item.state == STATE_ACTIVE) {
                binding.textDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.color_auto_activ
                    )
                )
            } else {
                binding.textDate.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.color_auto_no_activ
                    )
                )
            }


            if (item.account.isNotEmpty()) binding.paymentType.text =
                item.account else if (item.type == "D") binding.paymentType.text =
                context.getString(R.string.daily) else binding.paymentType.text =
                context.getString(R.string.monthly_2)

            binding.paymentName.text = item.name
            if (item.icon_name != "") Picasso.get()
                .load(Keys.paynetPhotoUrl() + item.icon_name)
                .error(R.drawable.ic_payments_placeholder).into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)
            binding.imageMore.setOnClickListener {
                onClick.invoke(adapterPosition, "more")
            }
            itemView.setOnClickListener {
                onClick.invoke(adapterPosition, "active")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMySubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: ArrayList<AutoPayment>) {
        this.list = list
        notifyDataSetChanged()
    }
}