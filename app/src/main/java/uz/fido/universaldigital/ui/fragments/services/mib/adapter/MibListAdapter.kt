package uz.fido.universaldigital.ui.fragments.services.mib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.mib.Mib
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemListMibBinding

class MibListAdapter(
    private val context: Context,
    private val baseInterface: BaseInterface
) : ListAdapter<Mib, MibListAdapter.MibVh>(MibItemCallback()) {

    inner class MibVh(private val binding: ItemListMibBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(mib: Mib) {
            if (mib.isCurrent) {
                binding.tvImage.visibility = View.VISIBLE
                binding.father.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.home_bank_operation_anim
                    )
                )
            } else {
                binding.tvImage.visibility = View.GONE
            }
            binding.tvMibPassport.text = mib.doc_value
            binding.tvMibType.text = if (mib.client_type == "fiz")
                context.getText(R.string.fiz_mib)
            else
                context.getText(R.string.you_mib)

            binding.father.setOnClickListener {
                baseInterface.openMibInfo(mib, adapterPosition)
            }
            binding.father.setOnLongClickListener {
                baseInterface.openMibInfoLongClick(mib, adapterPosition)
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MibVh =
        MibVh(ItemListMibBinding.inflate(LayoutInflater.from(context), parent, false))


    override fun onBindViewHolder(holder: MibVh, position: Int) {
        holder.onBind(getItem(position))
    }
}


class MibItemCallback : DiffUtil.ItemCallback<Mib>() {
    override fun areItemsTheSame(oldItem: Mib, newItem: Mib) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Mib, newItem: Mib) =
        oldItem.doc_value == newItem.doc_value
}