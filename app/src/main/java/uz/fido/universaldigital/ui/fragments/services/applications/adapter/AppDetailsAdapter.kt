package uz.fido.universaldigital.ui.fragments.services.applications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.applications.ApplicationStatus
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemApplicationStatusBinding
import uz.fido.utils.utility.format.Format

class AppDetailsAdapter(
    private val list: ArrayList<ApplicationStatus>, private val addButton: () -> Unit
) : RecyclerView.Adapter<AppDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemApplicationStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ApplicationStatus, position: Int) {
            binding.applicationName.text = item.state_name
            binding.date.text = item.create_date
            if (item.isEnable) {
                binding.applicationName.setTextColor(itemView.context.getColor(R.color.brandRedColor))
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.brandRedColor
                    )
                )
                binding.cardView.strokeColor = ContextCompat.getColor(
                    itemView.context, R.color.brandRedColor
                )
            } else {
                binding.applicationName.setTextColor(itemView.context.getColor(R.color.mainTextColor))
                binding.date.setTextColor(itemView.context.getColor(R.color.hintColor))
            }
            if (item.state_id == -100) {
                binding.cardView.setCardBackgroundColor(itemView.context.getColor(R.color.brandRedColor))
//                binding.errorText.visibility = View.VISIBLE
                binding.status.visibility = View.VISIBLE
//                binding.errorText.text = item.err_msg
                binding.status.text = item.status
                binding.applicationName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.brandRedColor
                    )
                )
            }
            if (item.cardNumber != null && item.cardExpiry != null) if (item.cardNumber!!.isNotEmpty() && item.cardExpiry!!.isNotEmpty()) {
                binding.cardNumberLayout.visibility = View.VISIBLE
                binding.cardExpireLayout.visibility = View.VISIBLE
                binding.cardNumber.text = Format.formatCardNumberVisible(item.cardNumber!!)
                binding.cardExpire.text = if (item.cardNumber!!.startsWith("5614")) Format.showExpireDate2(item.cardExpiry!!) else Format.expireDate(item.cardExpiry!!)
                binding.addButton.isVisible = item.cardNumber!!.startsWith("5614")
                binding.addButton.setOnClickListener {
                    addButton.invoke()
                }
            }

            if (item.state_id == 100) {
                binding.status.visibility = View.VISIBLE
                binding.status.text = item.status
                binding.status.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.monitoring_amount
                    )
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplicationStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}