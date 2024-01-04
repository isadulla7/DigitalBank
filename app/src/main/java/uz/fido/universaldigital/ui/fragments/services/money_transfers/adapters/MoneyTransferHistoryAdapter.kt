package uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.money_transfer.list.MoneyTransferHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMoneyTransferHistoryBinding
import uz.fido.utils.utility.format.Format

class MoneyTransferHistoryAdapter(
    private var list: ArrayList<MoneyTransferHistory>, private val baseInterface: BaseInterface
) : RecyclerView.Adapter<MoneyTransferHistoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemMoneyTransferHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MoneyTransferHistory) {
            binding.amount.text =
                Format.formatAmount(Format.convertFromTiynDivide(item.amount)) + " ${item.currencyCode}"
            binding.country
            binding.date.text = item.dateCreated
            binding.status.text = item.statusName

            binding.imageLogo.setImageResource(
                when (item.transferId) {
                    7 -> R.drawable.ic_moneygram
                    8 -> R.drawable.asia_express
                    9 -> R.drawable.logo_unistream
                    10 -> R.drawable.logo_western_union
                    11 -> R.drawable.logo_contact
                    18 -> R.drawable.logo_zolotaya_korona
                    else -> R.drawable.bank_transfers
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoneyTransferHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: ArrayList<MoneyTransferHistory>) {
        this.list = list
        notifyDataSetChanged()
    }
}