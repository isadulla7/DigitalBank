package uz.fido.universaldigital.ui.fragments.products.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mckrpk.animatedprogressbar.dpToPx
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemNewHomeFastAccessLayoutBinding
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getDrawable
import kotlin.math.roundToInt

class NewFastAccessOperationAdapter(
    val context: Context,
    val baseInterface: BaseInterface,
    val list: ArrayList<FastAccessOperation>
) : RecyclerView.Adapter<NewFastAccessOperationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewHomeFastAccessLayoutBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemNewHomeFastAccessLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FastAccessOperation) {
            binding.apply {
                if (item.id == 20) {
                    icon.layoutParams.width = dpToPx(56, itemView.context).roundToInt()
                    icon.layoutParams.height = dpToPx(28, itemView.context).roundToInt()
                    icon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.status_done))
                } else {
                    icon.layoutParams.height = dpToPx(28, itemView.context).roundToInt()
                    icon.layoutParams.width = dpToPx(28, itemView.context).roundToInt()
                    icon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.brandRedColor))
                }
                itemName.text = item.name
                icon.setImageResource(context.getDrawable(item.icon))
                itemFastAccessLayout.setOnClickListener {
                    baseInterface.openHomeOperation(item.id)
                }
            }
        }
    }

}

