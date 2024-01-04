package fido.aab_mobile.ui.ib.fragments.menu.menu_services.bank_transfers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.swift.SWIFTList
import uz.fido.universaldigital.databinding.ItemTransferListBinding
import uz.fido.utils.utility.format.Format

class BankTransferListAdapter(
    private val list: ArrayList<SWIFTList>
) : RecyclerView.Adapter<BankTransferListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTransferListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SWIFTList) {
            val receiver = item.nameandaddress_59.replace("\n", "")
            binding.textReceiver.text = receiver
            binding.textAmount.text = Format.formatAmount(item.amount_32a)
            binding.textBankAccountBis.text = item.bicorbei_57a
            binding.textBeneficiaryAccount.text = item.account_59
            binding.textDetails.text = item.narrative_70
            binding.textStatus.text = item.status
//            itemView.setOnClickListener {
//                baseInterface.openNewPage(bindingAdapter!!.itemCount)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTransferListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}