package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemServiceMonitoringFilterBinding
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO

class ServiceAllMonitoringAdapter(
    private val context: Context,
    private var list: ArrayList<UserPayedService>, private val baseInterface: BaseInterface
) : RecyclerView.Adapter<ServiceAllMonitoringAdapter.VhService>() {

    inner class VhService(val binding: ItemServiceMonitoringFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: UserPayedService) {
            binding.tipName.text = item.service_name

            if (item.service_current) {
                binding.tipName.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                binding.father.background = ContextCompat.getDrawable(context, R.drawable.monitoring_filter_item_color_click)
            } else {
                binding.tipName.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                binding.father.background = ContextCompat.getDrawable(context, R.drawable.monitoring_filter_item_color)

            }
            if (item.icon_name != "")
                Picasso.get()
                    .load(PAYNET_PHOTO + item.icon_name)
                    .error(R.drawable.ic_payments_placeholder)
                    .into(binding.image)
            else binding.image.setImageResource(R.drawable.ic_payments_placeholder)
            binding.father.setOnClickListener {
                baseInterface.monitoringPayed(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhService {
        return VhService(
            ItemServiceMonitoringFilterBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VhService, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(response: ArrayList<UserPayedService>) {
        list = response
        notifyDataSetChanged()
    }
}