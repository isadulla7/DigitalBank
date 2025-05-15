package uz.fido.universaldigital.ui.fragments.monitoring.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.monitoring.filter.MonitoringFilter
import uz.fido.universaldigital.databinding.ItemMonitoringFilteredBinding
import uz.fido.utils.utility.format.Format

class MonitoringFilteredAdapter(
    private var list: ArrayList<MonitoringFilter>,
    private val filterOnClick: (MonitoringFilter) -> Unit
) : RecyclerView.Adapter<MonitoringFilteredAdapter.VhFilter>() {

    inner class VhFilter(val binding: ItemMonitoringFilteredBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(monitoringFilter: MonitoringFilter) {
            binding.textName.text = if (checkCard(monitoringFilter.name, monitoringFilter.type)) Format.formatCardNumber(monitoringFilter.name) else monitoringFilter.name
            binding.father.setOnClickListener {
                filterOnClick.invoke(monitoringFilter)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhFilter {
        return VhFilter(ItemMonitoringFilteredBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VhFilter, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(allOperationFilter: ArrayList<MonitoringFilter>) {
        list = allOperationFilter
        notifyDataSetChanged()
    }

    fun checkCard(text: String, type: String): Boolean {
        return text.length == 16 && type != "amount" && type != "choose" && type != "date"
    }

}

