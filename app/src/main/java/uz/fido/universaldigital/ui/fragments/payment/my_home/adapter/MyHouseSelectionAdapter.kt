package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyHouseSelectionBinding
import uz.fido.utils.const.APIServiceConst


class MyHouseSelectionAdapter(
    private val list: ArrayList<Template>,
) : RecyclerView.Adapter<MyHouseSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyHouseSelectionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(templateItem: Template) {
            if (templateItem.icon_name != "")
                Picasso.get().load(APIServiceConst.PAYNET_PHOTO + templateItem.icon_name).error(R.drawable.ic_payments_placeholder).into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)
            binding.text.text = templateItem.name
            binding.person.text = templateItem.account
            binding.personText.text = templateItem.account_text + ":"
            binding.amount.text = uz.fido.utils.utility.format.Format.formatAmount(templateItem.amount) + " UZS"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyHouseSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}