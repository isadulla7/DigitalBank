package uz.fido.utils.view.amount

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.utils.R
import uz.fido.utils.databinding.ItemAmountSuggestionBinding

@SuppressLint("SetTextI18n")
class AmountSuggestionAdapter(
    private val currency: String, private val onItemClickListener: (String) -> Unit
) : ListAdapter<String, AmountSuggestionAdapter.ViewHolder>(MyDiffUtil()) {

    //for git
    inner class ViewHolder(
        private val binding: ItemAmountSuggestionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(amount: String) {
            binding.tvSuggAmount.text =
                amount + " " + currency.ifEmpty { itemView.context.getString(R.string.sum) }
            binding.tvSuggAmount.setOnClickListener {
                onItemClickListener.invoke(amount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAmountSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getItem(position))
    }

    class MyDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: String, newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}