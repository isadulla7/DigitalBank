package uz.fido.universaldigital.ui.fragments.services.goal.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.target.GoalHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemAccountHistoryBinding
import uz.fido.universaldigital.databinding.ItemGoalHistoryBinding
import uz.fido.utils.utility.format.Format

class GoalHistoryAdapter(
    private var list: ArrayList<GoalHistory>
) : RecyclerView.Adapter<GoalHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemGoalHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: GoalHistory) {
            binding.amount.text = Format.formatAmount(Format.formatAmountFromTiynToInteger(item.amount)) + " UZS"
            binding.time.text =if (item.create_date.length>16) item.create_date.substring(0,16) else item.create_date
            if (item.amount.startsWith("-")) {
                binding.icon.setImageResource(R.drawable.outcome)
                binding.name.text = itemView.context.getString(R.string.withdrawal)
                binding.amount.setTextColor(ContextCompat.getColor(itemView.context, R.color.mainTextColor))
            } else {
                binding.icon.setImageResource(R.drawable.income)
                binding.name.text = itemView.context.getString(R.string.replenishment)
                binding.amount.setTextColor(ContextCompat.getColor(itemView.context, R.color.monitoring_amount))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setNewList(newList: ArrayList<GoalHistory>) {
        list = newList
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}