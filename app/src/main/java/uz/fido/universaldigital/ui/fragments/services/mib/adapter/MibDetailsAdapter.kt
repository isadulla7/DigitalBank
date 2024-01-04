package uz.fido.universaldigital.ui.fragments.services.mib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.mib.MibDetail
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMibDetailsBinding
import uz.fido.utils.utility.format.Format

class MibDetailsAdapter(val context: Context,private val baseInterface: BaseInterface)
    :ListAdapter<MibDetail, MibDetailsAdapter.MibDetailVh>(MibDetailItemCallback()) {


    inner class MibDetailVh(private val binding:ItemMibDetailsBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun onBind(item: MibDetail) {
            var fio = ""
            var purpose = ""
            item.features?.forEach {
                when (it.field_key) {
                    "fio" -> {
                        fio = it.field_value.toString()
                    }
                    "purpose" -> {
                        purpose = it.field_value.toString()
                    }
                }
            }
            binding.father.setOnClickListener {
                baseInterface.openInfoMib(mibDetail = item)
            }
            binding.tvFio.text=fio
            binding.tvType.text=purpose
            binding.tvAmount.text= Format.formatAmount(item.debet_summa) + " UZS"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MibDetailVh {
       return MibDetailVh(ItemMibDetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MibDetailVh, position: Int) {
        holder.onBind(getItem(position))
    }

}


class MibDetailItemCallback:DiffUtil.ItemCallback<MibDetail>(){
    override fun areItemsTheSame(oldItem: MibDetail, newItem: MibDetail): Boolean =oldItem==newItem

    override fun areContentsTheSame(oldItem: MibDetail, newItem: MibDetail): Boolean =oldItem.id==newItem.id
}