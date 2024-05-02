package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.filter.MonitoringFilter
import uz.fido.universaldigital.databinding.ItemMonitoringFilterBinding
import uz.fido.utils.utility.format.Format

class MonitoringFilterAdapter(
    private var list: ArrayList<MonitoringFilter>,
    private val filterOnClick: (MonitoringFilter) -> Unit
) : RecyclerView.Adapter<MonitoringFilterAdapter.VhFilter>() {

    inner class VhFilter(val binding: ItemMonitoringFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(monitoringFilter: MonitoringFilter) {
//            binding.father.startAnimation(
//                AnimationUtils.loadAnimation(
//                    context,
//                    R.anim.home_bank_operation_anim
//                )
//            )
            binding.textName.text = if (monitoringFilter.name.length == 16) Format.formatCardNumber(monitoringFilter.name) else monitoringFilter.name
            binding.father.setOnClickListener {
                filterOnClick.invoke(monitoringFilter)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhFilter {
        return VhFilter(ItemMonitoringFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VhFilter, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(allOperationFilter: ArrayList<MonitoringFilter>) {
        list = allOperationFilter
        notifyDataSetChanged()
    }

}

