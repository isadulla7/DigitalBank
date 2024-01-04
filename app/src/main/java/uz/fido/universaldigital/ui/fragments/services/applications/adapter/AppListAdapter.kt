package uz.fido.universaldigital.ui.fragments.services.applications.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.applications.OrderCardApp
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemApplicationBinding

@SuppressLint("SetTextI18n")
class AppListAdapter(
    private val list: ArrayList<OrderCardApp>,
    private val baseInterface: BaseInterface,
    private val context: Context
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderCardApp) {
            binding.applicationName.text =
                item.module_product.ifEmpty { itemView.context.getString(R.string.application) }
            binding.appId.text = "ID: " +
                    if (item.product == "CREDIT") item.loan_cc_id.toString() else item.application_id.toString()
            when (item.state_id) {
                1 -> {
                    binding.status.text = context.getString(R.string.application_received)
                    binding.status.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.brandBlueColor_60
                        )
                    )
                }

                2 -> {
                    binding.status.text = context.getString(R.string.in_processing)
                    binding.status.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.status_waiting
                        )
                    )
                }

                3 -> {
                    binding.status.text = context.getString(R.string.cancelled)
                    binding.status.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.brandBlueColor
                        )
                    )
                }

                4 -> {
                    binding.status.text = context.getString(R.string.cancelled)
                    binding.status.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.brandBlueColor
                        )
                    )
                }

                5 -> {
                    binding.status.text = context.getString(R.string.completed)
                    binding.status.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.status_done
                        )
                    )
                }
            }
            binding.father.setOnClickListener {
                baseInterface.getApplicationDetails(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}