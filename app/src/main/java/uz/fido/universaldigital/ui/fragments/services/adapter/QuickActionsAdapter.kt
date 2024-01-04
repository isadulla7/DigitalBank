package uz.fido.universaldigital.ui.fragments.services.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemQuickActionBinding
import uz.fido.universaldigital.ui.utils.recyclerview.MenuServiceItem

class QuickActionsAdapter(
    var onItemClickListener: (Int) -> Unit,
    var addToTopListClickListener: (MenuServiceItem, Int) -> Unit
) :
    ListAdapter<MenuServiceItem, QuickActionsAdapter.ViewHolder>(MyDiffUtil()) {

    inner class ViewHolder(private val binding: ItemQuickActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: MenuServiceItem, position: Int) {
            binding.apply {
                itemName.text = item.serviceName
                icon.setImageResource(getQuickActionIcon(item.serviceId))
                icAddButton.isVisible = item.isEditing == true
                fatherItemQuickAction.setOnClickListener {
                    if (item.isEditing == true) {
                        addToTopListClickListener.invoke(item, position)
                    } else {
                        onItemClickListener.invoke(position)
                    }
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<MenuServiceItem>() {
        override fun areItemsTheSame(oldItem: MenuServiceItem, newItem: MenuServiceItem): Boolean {
            return oldItem.serviceId == newItem.serviceId

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MenuServiceItem,
            newItem: MenuServiceItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemQuickActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }

    private fun getQuickActionIcon(id: Int): Int {
        return when (id) {
            100 -> R.drawable.ic_mini_conversion
            200 -> R.drawable.ic_mini_location
            300 -> R.drawable.ic_mini_qr_payment
            400 -> R.drawable.ic_mini_humopay
            500 -> R.drawable.ic_mini_transfer_to_account
            600 -> R.drawable.ic_mini_connect_sms_notif
            700 -> R.drawable.ic_mini_debts
            else -> R.drawable.ic_mini_money_transfers
        }
    }
}
