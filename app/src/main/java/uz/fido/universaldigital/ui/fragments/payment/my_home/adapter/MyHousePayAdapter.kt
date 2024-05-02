package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemMyHousePayBinding
import uz.fido.utils.const.APIServiceConst
import uz.fido.utils.format.Format

class MyHousePayAdapter(
    private val context: Context,
    private val list: ArrayList<Template>
) : RecyclerView.Adapter<MyHousePayAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyHousePayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(templateItem: Template) {
            if (templateItem.icon_name != "")
                Picasso.get().load(APIServiceConst.PAYNET_PHOTO + templateItem.icon_name).error(R.drawable.ic_payments_placeholder).into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)
            binding.text.text = templateItem.name
            binding.amount.text = Format.formatAmount(templateItem.amount) + " UZS"
            if (templateItem.payment_success!!) {
                binding.image.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
            } else {
                binding.image.visibility = View.GONE
                binding.progress.visibility = View.VISIBLE
                if (templateItem.error_text != null) {
                    binding.progress.visibility = View.GONE
                    binding.image.visibility = View.VISIBLE
                    binding.amountText.text = context.getString(R.string.error_occured)
                    binding.amountText.setTextColor(ContextCompat.getColor(context,R.color.brandRedColor))
                    binding.amount.setTextColor(ContextCompat.getColor(context,R.color.brandRedColor))
                    binding.image.setImageResource(R.drawable.ic_payment_error_icon)

                } else {
                    binding.errorColor.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyHousePayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}