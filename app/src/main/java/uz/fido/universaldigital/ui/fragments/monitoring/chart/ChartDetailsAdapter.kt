package uz.fido.universaldigital.ui.fragments.monitoring.chart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.local_history.ChartData
import uz.fido.universaldigital.databinding.ItemMonitoringChartDetailsBinding
import uz.fido.utils.utility.format.Format
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("SetTextI18n")
class ChartDetailsAdapter(
    private var list: ArrayList<ChartData>,
    private val setOnClickListener: (ChartData) -> Unit
) : RecyclerView.Adapter<ChartDetailsAdapter.VhService>() {

    inner class VhService(val binding: ItemMonitoringChartDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ChartData, position: Int) {
            val total = list.map { it.amount }.sumOf { it }
            val percent = calculatePercent(item.amount, total)
            binding.cardView.setCardBackgroundColor(getPieChartColors()[position])
            binding.detailName.text = item.paymentService?.nameIndex
            binding.amount.text = Format.formatAmount(item.amount.toString()).replace(".00", "") + " UZS"
            binding.percent.text = percent
            binding.father.setOnClickListener {
                setOnClickListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhService {
        return VhService(
            ItemMonitoringChartDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VhService, position: Int) {
        holder.onBind(list[position], position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(response: ArrayList<ChartData>) {
        list = response
        notifyDataSetChanged()
    }

    private fun calculatePercent(currentAmount: BigDecimal, totalAmount: BigDecimal): String {
        return if (totalAmount != BigDecimal(0)) {
            currentAmount.multiply(BigDecimal(100)).divide(totalAmount, 2, RoundingMode.HALF_UP).toString() + " %"
        } else {
            ""
        }
    }
}