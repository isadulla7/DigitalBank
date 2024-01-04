package uz.fido.universaldigital.ui.fragments.profile.security

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyDevicesBinding

@SuppressLint("SetTextI18n")
class DevicesAdapter(
    private val list: ArrayList<UserDevices>, private val baseInterface: BaseInterface
) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyDevicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDevices) {
            binding.apply {
                deviceName.text =
                    item.device_name + ", " + if (item.device_type == "A") "Android" else "iOS"
//                icon.setImageResource(if (item.device_type == "A") R.drawable.ic_my_device_android else R.drawable.ic_my_device_ios)
                lastSeen.text = item.last_seen_date
//                if (item.city.isNotEmpty() && item.country.isNotEmpty()) {
//                    textAdditional.text = item.ip + " - " + item.city + ", " + item.country
//                } else {
//                    textAdditional.text = item.ip
//                }
                deleteDevice.setOnClickListener {
                    baseInterface.terminateSession(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMyDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}