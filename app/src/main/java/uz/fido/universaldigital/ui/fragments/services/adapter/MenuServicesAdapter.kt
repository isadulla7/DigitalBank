package uz.fido.universaldigital.ui.fragments.services.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ItemMenuServiceBankProductsBinding
import uz.fido.universaldigital.databinding.ItemMenuServiceBinding
import uz.fido.universaldigital.databinding.ItemMenuServiceHeaderBinding
import uz.fido.universaldigital.ui.utils.recyclerview.MenuServiceItem
import uz.fido.utils.view.bottom_menu_anim.isVisible

class MenuServicesAdapter(
    var onItemClickListener: (Int) -> Unit
) : ListAdapter<MenuServiceItem, RecyclerView.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> HeaderViewHolder(
                ItemMenuServiceHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ITEM_TYPE_SERVICE -> ServiceViewHolder(
                ItemMenuServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> BankProductViewHolder(
                ItemMenuServiceBankProductsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 5 -> ITEM_TYPE_HEADER
            1, 2, 3, 4 -> ITEM_TYPE_BANK_PRODUCT
            else -> ITEM_TYPE_SERVICE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ServiceViewHolder -> {
                holder.bind(getItem(position))
            }

            is HeaderViewHolder -> {
                holder.bind(getItem(position))
            }

            is BankProductViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemMenuServiceHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuServiceItem) {
            binding.apply {
                tvHeader.text = item.serviceName
            }
        }
    }

    inner class BankProductViewHolder(private val binding: ItemMenuServiceBankProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuServiceItem) {
            binding.apply {
                tvProductName.text = item.serviceName
//                tvProductDescription.text = item.serviceDescription
                icProductIcon.setImageResource(item.icon!!)
                itemBankProduct.setOnClickListener {
                    onItemClickListener.invoke(item.serviceId)
                }
                binding.tvSoon.isVisible = item.serviceId == 3
                binding.disableBg.isVisible = item.serviceId == 3
            }
        }
    }

    inner class ServiceViewHolder(private val binding: ItemMenuServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuServiceItem) {
            binding.apply {
                itemName.text = item.serviceName
                icon.setImageResource(item.icon!!)
                fatherItemMenuService.setOnClickListener {
                    onItemClickListener.invoke(item.serviceId)
                }
                binding.tvSoon.isVisible =
                    item.serviceId == 3 || item.serviceId == 200 || item.serviceId == 800 || item.serviceId == 801 || item.serviceId == 700
                binding.disableBg.isVisible =
                    item.serviceId == 3 || item.serviceId == 200 || item.serviceId == 800 || item.serviceId == 801 || item.serviceId == 700
            }
        }
    }

    companion object {
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_BANK_PRODUCT = 2
        const val ITEM_TYPE_SERVICE = 3
    }

    class MyDiffUtil : DiffUtil.ItemCallback<MenuServiceItem>() {
        override fun areItemsTheSame(oldItem: MenuServiceItem, newItem: MenuServiceItem): Boolean {
            return oldItem.serviceId == newItem.serviceId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MenuServiceItem, newItem: MenuServiceItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
