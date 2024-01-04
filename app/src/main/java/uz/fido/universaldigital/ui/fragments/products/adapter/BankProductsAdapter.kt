package uz.fido.universaldigital.ui.fragments.products.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemBankProductsBinding
import uz.fido.universaldigital.ui.fragments.products.model.BankProducts
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes

class BankProductsAdapter(
    val context: Context,
    val baseInterface: BaseInterface,
    val list: ArrayList<BankProducts>
) : RecyclerView.Adapter<BankProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBankProductsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ItemBankProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BankProducts) {
            binding.apply {
                name.text = item.name
                icon.load(itemView.context.getDrawableFromRes(item.icon))
                fatherBankProduct.setOnClickListener {
                    baseInterface.openBankProduct(item.id)
                }
            }
        }
    }

}

