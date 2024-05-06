package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemServiceChooseFilterBinding
import uz.fido.universaldigital.ui.utils.keys.Keys

class ServiceAllChooseAdapter(
    private val context: Context,
    private var list: ArrayList<UserPayedService>,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<ServiceAllChooseAdapter.VhService>() {

    inner class VhService(val binding: ItemServiceChooseFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: UserPayedService) {
            binding.tipName.text = item.service_name

            if (item.icon_name != "")
                Picasso.get()
                    .load(Keys.paynetPhotoUrl() + item.icon_name)
                    .error(R.drawable.ic_payments_placeholder)
                    .into(binding.image)
            else binding.image.setImageResource(R.drawable.ic_payments_placeholder)

            if (item.service_current) {
                binding.option.setImageResource(R.drawable.check_construktor)
            } else binding.option.setImageResource(R.drawable.check_box_color)

            binding.father.setOnClickListener {
                baseInterface.monitoringPayed(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhService {
        return VhService(
            ItemServiceChooseFilterBinding.inflate(
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