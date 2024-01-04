package uz.fido.universaldigital.ui.fragments.products.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemHomeFastAccessLayoutBinding
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getDrawable

class FastAccessOperationAdapter(
    val context: Context,
    val baseInterface: BaseInterface,
    val list: ArrayList<FastAccessOperation>
) : RecyclerView.Adapter<FastAccessOperationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeFastAccessLayoutBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemHomeFastAccessLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FastAccessOperation) {
            binding.apply {
                itemName.text = item.name
                icon.setImageResource(context.getDrawable(item.icon))
                itemFastAccessLayout.setOnClickListener {
                    baseInterface.openHomeOperation(item.id)
                }
            }
        }
    }

}

