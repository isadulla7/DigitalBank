package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemServiceChooseFilterBinding
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO
import uz.fido.utils.format.Format

class ServiceFilterAdapter(
    private val context: Context,
    private var list: ArrayList<LocalMonitoring>,
    private val onClick: (LocalMonitoring) -> Unit
) : RecyclerView.Adapter<ServiceFilterAdapter.VhService>() {

    inner class VhService(val binding: ItemServiceChooseFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: LocalMonitoring) {
            binding.tipName.text = item.name
            binding.status.text = if (item.partner_obj == "16") {
                Format.formatCardNumberMonitoring(context, item.partner_obj)
            } else item.partner_obj

            if (item.icon_name != "")
                Picasso.get()
                    .load(PAYNET_PHOTO + item.icon_name)
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
        return VhService(ItemServiceChooseFilterBinding.inflate(LayoutInflater.from(context), parent, false))
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