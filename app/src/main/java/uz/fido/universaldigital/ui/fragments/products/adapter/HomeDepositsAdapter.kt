package uz.fido.universaldigital.ui.fragments.products.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemHomeDepositsBinding
import uz.fido.utils.utility.format.Format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeDepositsAdapter(
    private val baseInterface: BaseInterface,
    private var list: ArrayList<ClientDeposit>,
    private var isHomeDeposit: Boolean? = true
) : RecyclerView.Adapter<HomeDepositsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeDepositsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (list.size > 2) {
            if (isHomeDeposit == true) {
                return 2
            } else return list.size
        } else list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private val binding: ItemHomeDepositsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClientDeposit) {
            binding.apply {
                tvDepositName.text = item.depName
                tvDepositBalance.text = Format.formatAmountWithAppend(
                    item.sumDep, item.currencyChar
                )
                tvDepositPercentage.text =
                    itemView.context.getString(R.string.yield_per_year, item.percent, "%")
                tvDepositDuration.text = item.depTemp
                val realPercentage =
                    if (calculatePercentage(item) > 0) calculatePercentage(item).toInt() else 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressView.setProgress(realPercentage, true)
                } else {
                    progressView.progress = realPercentage
                }
            }
            itemView.setOnClickListener {
                baseInterface.openDepositDetails(item)
            }
        }
    }

    private fun calculatePercentage(clientDeposit: ClientDeposit): Double {
        return (1 - (calculateDays(clientDeposit.closingDate.orEmpty()).toDouble() / (totalDays(
            clientDeposit.openDate.orEmpty(),
            clientDeposit.closingDate.orEmpty()
        )))) * 100
    }

    private fun totalDays(startDate: String, endDate: String): Int {
        try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val millionSeconds = sdf.parse(endDate).time - sdf.parse(startDate).time
            return TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
        } catch (e: Exception) {
            return 0
        }
    }

    private fun calculateDays(stringDate: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val millionSeconds = sdf.parse(stringDate).time - Calendar.getInstance().timeInMillis
            TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun setList(popularTransfers: ArrayList<ClientDeposit>) {
        list = popularTransfers
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

}