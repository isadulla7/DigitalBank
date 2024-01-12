package uz.fido.universaldigital.ui.fragments.products.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemHomeCreditsBinding
import uz.fido.utils.utility.format.Format

class HomeCreditsAdapter(
    private val baseInterface: BaseInterface,
    private var list: ArrayList<CreditProduct>,
    private var isHomeCredit: Boolean? = true
) : RecyclerView.Adapter<HomeCreditsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeCreditsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (list.size > 2) {
            if (isHomeCredit == true) {
                return 2
            } else return list.size
        } else list.size
    }

    inner class ViewHolder(private val binding: ItemHomeCreditsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CreditProduct) {
            binding.apply {
                tvCreditName.text = item.productName
                tvCreditBalance.text = Format.formatAmountWithAppend(
                    item.amount, "UZS"
                )
                val paidAmount = item.amount.toBigDecimal() - item.totalDebt.toBigDecimal()
                val percent = (paidAmount * 100.toBigDecimal() / item.amount.toBigDecimal()).toInt()
                progressView.progress = percent
                tvCreditLeftAmount.text = itemView.context.getString(R.string.paid) + " " +
                        Format.formatAmountWithAppend(
                            paidAmount.toString(),
                            "UZS"
                        )


                if (item.is_reacted == 1) {
                    binding.progressView.visibility = View.GONE
                    binding.tvCreditText.visibility = View.VISIBLE
                    binding.contentButton.visibility = View.VISIBLE
                    binding.tvCreditLeftAmount.text =
                        itemView.context.getString(R.string.application_approved)
                    itemView.setOnClickListener {
                        baseInterface.confirmTakeLoan(item)
                    }

                } else {
                    itemView.setOnClickListener {
                        baseInterface.openCreditDetails(item)
                    }
                    binding.progressView.visibility = View.VISIBLE
                    binding.tvCreditText.visibility = View.GONE
                    binding.contentButton.visibility = View.GONE
                }
            }
        }
    }

    fun setList(creditProduct: ArrayList<CreditProduct>) {
        list = creditProduct
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

}