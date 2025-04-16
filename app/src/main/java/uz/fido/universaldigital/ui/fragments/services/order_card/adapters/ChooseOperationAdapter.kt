package uz.fido.universaldigital.ui.fragments.services.order_card.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.cards.ProductType
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemOrderCardOperationBinding
import uz.fido.universaldigital.ui.utils.extensions.yearText
import uz.fido.utils.utility.format.Format


class ChooseOperationAdapter(
    private val context: Context,
    private val baseInterface: BaseInterface,
    private val list: ArrayList<ProductType>,
    private val operation: String
) : RecyclerView.Adapter<ChooseOperationAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemOrderCardOperationBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        private val allServiceLists = ArrayList<AllServiceLists>()
        private var infoItems = AllServiceLists()

        @SuppressLint("SetTextI18n")
        fun setData(priceItem: ProductType) {
            binding.productName.text = priceItem.name
            if (priceItem.code == "SV_COBAGING_CARD" || priceItem.code == "SV_COBAGING_CARD_MIR") {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.insurance_deposit)
                infoItems.code =
                    Format.formatAmount((priceItem.insurance_deposit.toDouble() / 100).toString()) + " ${priceItem.currency}"
                allServiceLists.add(infoItems)
            }
            if (priceItem.price != 0) {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.issue_cost)
                infoItems.code =
                    Format.formatAmount((priceItem.price.toDouble() / 100).toString()) + " UZS"
                allServiceLists.add(infoItems)
            }

            if (priceItem.delivery_amount != 0 && priceItem.is_virtual == "N") {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.delivery_cost)
                infoItems.code =
                    Format.formatAmount((priceItem.delivery_amount.toDouble() / 100).toString()) + " UZS"
                allServiceLists.add(infoItems)
            }
            if (priceItem.card_validity_period.isNotEmpty()) {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.card_expire)
                infoItems.code = priceItem.card_validity_period + " ${
                    yearText(
                        itemView.context, priceItem.card_validity_period.toInt()
                    )
                }"
                allServiceLists.add(infoItems)
            }

            if (priceItem.price == 0) {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.issue_cost)
                infoItems.code = context.getString(R.string.free)
                allServiceLists.add(infoItems)
//                    issueCost.text = context.getString(R.string.free)
            }
            if (priceItem.delivery_amount == 0) {
                infoItems = AllServiceLists()
                infoItems.name = context.getString(R.string.delivery_cost)
                infoItems.code = context.getString(R.string.free)
                allServiceLists.add(infoItems)
//                    deliveryCost.text = context.getString(R.string.free)
            }

            binding.continueButton.setOnClickListener {
                baseInterface.selectedCardWithOperation(priceItem, operation)
            }
            if (operation == "1" || operation == "2") {
                binding.layoutInsuranceDeposit.visibility = View.GONE
            }
            binding.recyclerView.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = OrderCardInfoAdapter(allServiceLists)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderCardOperationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }
}