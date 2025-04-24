package uz.fido.universaldigital.ui.fragments.services.loan.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemDepositHistoryBinding
import uz.fido.universaldigital.databinding.ItemHistoriesHeaderBinding
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.format.Format
import java.text.SimpleDateFormat
import java.util.Locale

class AccountHistoriesAdapter(
    private var type: String,
    private var list: ArrayList<AccountHistory>,
    private val clientDeposit: ClientDeposit? = null,
    private val onClick: (AccountHistory, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDepositHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AccountHistory) {
            if (type == Const.TYPE_LOAN) {
                binding.name.text =
                    when (item.lnType) {
                        "1" -> itemView.context.getString(R.string.main_dept)
                        "2", "" -> if (item.debitAccount!!.startsWith("12503")) itemView.context.getString(R.string.use_of_overdraft_limit) else itemView.context.getString(
                            R.string.top_up_account
                        )

                        "3" -> itemView.context.getString(R.string.accured_interest)
                        "5" -> itemView.context.getString(R.string.overdue_principal)
                        "7" -> itemView.context.getString(R.string.accured_interest_on_overdue)
                        "22" -> itemView.context.getString(R.string.penalty_overdue_accured_interest)
                        else -> itemView.context.getString(R.string.overdue_interest)
                    }
            } else {
                binding.name.text =
                    Format.setDepositOperationTitle(debit = item.debitAmount, context = itemView.context, dtAcc = item.debitAccount.toString(), coAcc = item.creditAccount)
            }

            item.dateExecute?.let {
                binding.time.text = Format.formatDepositDate(it, "dd.MM.yyyy HH:mm:ss", "HH:mm")
            }
            val currencyChar = clientDeposit?.currencyChar ?: CurrencyConst.CURRENCY_CHAR_UZS

            if (item.debitAmount == "0") {
                binding.amount.setTextColor(ContextCompat.getColor(itemView.context, R.color.monitoring_amount))
                binding.amount.text = "+ ${Format.formatAmount((item.creditAmount!!.toDouble()).toString())} $currencyChar"
                binding.icon.setImageResource(R.drawable.ic_monitoring_plus)
                binding.type.text = itemView.context.getText(R.string.maybe_deposit)
            } else {
                binding.icon.setImageResource(R.drawable.icon_monitoring)
                binding.amount.setTextColor(ContextCompat.getColor(itemView.context, R.color.mainTextColor))
                binding.amount.text = "- ${Format.formatAmount((item.debitAmount.toDouble()).toString())} $currencyChar"
                binding.type.text = itemView.context.getText(R.string.write_offs)

            }
            if (item.lnType == "2" || item.lnType == "") {
                if (item.debitAccount!!.startsWith("12503")) {
                    binding.type.text = itemView.context.getText(R.string.maybe_deposit)
                    binding.icon.setImageResource(R.drawable.payment_loan)
                    binding.amount.setTextColor(ContextCompat.getColor(itemView.context, R.color.mainTextColor))
                    binding.amount.text = Format.formatAmount(if (item.debitAmount == "0") item.creditAmount else item.debitAmount) + " $currencyChar"
                }
            }
            itemView.setOnClickListener {
                onClick.invoke(item, type)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].accountType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) ViewHolder(ItemDepositHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else ViewHolderHeaders(ItemHistoriesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].accountType == 0) {
            (holder as ViewHolder).bind(list[position])
        } else (holder as ViewHolderHeaders).onBind(accountHistory = list[position])
    }

    inner class ViewHolderHeaders(private val binding: ItemHistoriesHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(accountHistory: AccountHistory) {
            binding.dateView.text = try {
                val inputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM, EEEE", Locale.getDefault())
                val newFormat = outputFormat.format(inputFormat.parse(accountHistory.dateExecute.toString())?.time ?: "")
                newFormat.toString()
            } catch (e: Exception) {
                ""
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setNewList(newList: ArrayList<AccountHistory>) {
        list = newList
        notifyDataSetChanged()
    }

    fun setList(list: ArrayList<AccountHistory>) {
        list.forEach {
            if (!list.contains(it)) {
                list.add(it)
            }
        }
        notifyDataSetChanged()
    }

}