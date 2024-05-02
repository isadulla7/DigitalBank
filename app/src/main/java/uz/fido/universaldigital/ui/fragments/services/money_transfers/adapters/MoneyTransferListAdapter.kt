package uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.money_transfer.receive.RemittanceType
import uz.fido.universaldigital.R
import uz.fido.utils.view.custom_text_view.TextViewMedium

class MoneyTransferListAdapter(
    private val list: ArrayList<RemittanceType>,
    private val openMoneyTransfer: (RemittanceType) -> Unit,
) : RecyclerView.Adapter<MoneyTransferListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_money_transfer, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.nameTransfer
        holder.image.setImageResource(
            when (item.id) {
                7 -> R.drawable.ic_moneygram
                8 -> R.drawable.asia_express
                9 -> R.drawable.logo_unistream
                10 -> R.drawable.logo_western_union
                11 -> R.drawable.logo_contact
                18 -> R.drawable.logo_zolotaya_korona
                31 -> R.drawable.ic_sberbank_online
                else -> R.drawable.ic_payments_placeholder
            }
        )
        holder.father.setOnClickListener {
            openMoneyTransfer.invoke(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextViewMedium = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val father: LinearLayoutCompat = view.findViewById(R.id.father)
    }

}