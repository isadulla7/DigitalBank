package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemServiceChooseFilterBinding
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.format.Format

class ServiceFilterAdapter(
    private var list: ArrayList<LocalMonitoring>,
    private val onClick: (LocalMonitoring) -> Unit
) : RecyclerView.Adapter<ServiceFilterAdapter.VhService>() {

    inner class VhService(val binding: ItemServiceChooseFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: LocalMonitoring) {
            binding.tipName.text = item.name
            binding.status.text = if (item.partnerObj.length == 16) {
                Format.formatCardNumber(item.partnerObj)
            } else item.partnerObj

            if (item.iconName != "")
                Picasso.get()
                    .load(Keys.paynetPhotoUrl() + item.iconName)
                    .error(R.drawable.ic_payments_placeholder)
                    .into(binding.image)
            else binding.image.setImageResource(R.drawable.ic_payments_placeholder)

            if (item.isChecked) {
                binding.option.setImageResource(R.drawable.check_construktor)
            } else binding.option.setImageResource(R.drawable.check_box_color)
            binding.father.setOnClickListener {
                onClick.invoke(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhService {
        return VhService(ItemServiceChooseFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VhService, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(response: ArrayList<LocalMonitoring>) {
        list = response
        notifyDataSetChanged()
    }
}