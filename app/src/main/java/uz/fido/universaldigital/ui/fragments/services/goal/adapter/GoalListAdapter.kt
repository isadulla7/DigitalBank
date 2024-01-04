package uz.fido.universaldigital.ui.fragments.services.goal.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemGoalBinding
import uz.fido.utils.const.CardConst.STATE_PASSIVE
import uz.fido.utils.utility.format.Format
import java.math.RoundingMode

@SuppressLint("SetTextI18n")
class GoalListAdapter(
    private var list: ArrayList<GoalModel>,
    private val onClick:(GoalModel)->Unit
) : RecyclerView.Adapter<GoalListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GoalModel) {
            binding.tvName.text = item.aim_desc
            binding.tvLeftAmount.text = itemView.context.getString(
                R.string.from_amount_credit,
                Format.formatAmount(Format.formatAmountFromTiynToInteger(item.target_amount)) + " UZS"
            )
            binding.tvBalance.text =
                Format.formatAmount(Format.formatAmountFromTiynToInteger(item.current_amount)) + " UZS"

            val perc = calculatePercentage(item)
            binding.progressView.max = 100
            binding.progressView.progress = if (perc > 0) perc else 1
            binding.status.visibility = if (item.state == STATE_PASSIVE) View.VISIBLE else View.GONE
            binding.father.setOnClickListener { onClick.invoke(item) }
        }
    }

    private fun calculatePercentage(item: GoalModel): Int {
        return item.current_amount.toBigDecimal().multiply(100.toBigDecimal())
            .divide(item.target_amount.toBigDecimal(), 2, RoundingMode.HALF_UP).toInt()

    }

    fun setList(goalList: ArrayList<GoalModel>) {
        list = goalList
        notifyDataSetChanged()
    }
}