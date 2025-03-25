package uz.fido.universaldigital.ui.fragments.profile.security

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyDevicesBinding

@SuppressLint("SetTextI18n")
class DevicesAdapter(
    private val list: ArrayList<UserDevices>,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyDevicesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDevices) {
            binding.apply {
                deviceName.text = if (item.device_name == "") itemView.context.getString(R.string.unknown) else item.device_name
                lastSeen.text = item.last_seen_date
                lastState.text = if (item.status == "A") itemView.context.getString(R.string.activ) else itemView.context.getString(R.string.ne_active)
                if (item.online == "true") {
                    imageView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.monitoring_amount))
                    lastState.setTextColor(ContextCompat.getColor(itemView.context, R.color.monitoring_amount))
                } else {
                    imageView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.hintColor))
                    lastState.setTextColor(ContextCompat.getColor(itemView.context, R.color.hintColor))
                }
                deleteDevice.setOnClickListener {
                    baseInterface.terminateSession(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}