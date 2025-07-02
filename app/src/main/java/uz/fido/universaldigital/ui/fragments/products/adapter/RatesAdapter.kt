package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.universaldigital.databinding.ItemHomeCurrencyRatesNewBinding
import uz.fido.universaldigital.ui.utils.extensions.setCurrencyFlag
import uz.fido.utils.utility.format.Format
import uz.fido.universaldigital.R


class RatesAdapter(private var list: ArrayList<CourseItem>,val checkBoxClick:(CourseItem)->Unit) :
    RecyclerView.Adapter<RatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeCurrencyRatesNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(homeCurrencyRates: ArrayList<CourseItem>) {
        list = homeCurrencyRates
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHomeCurrencyRatesNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseItem) {
            binding.imageCheckBox.setOnClickListener {
                checkBoxClick.invoke(item)
            }
             binding.imageCheckBox.setImageResource(if (item.checkBox) R.drawable.check_box_checked else R.drawable.check_box_color)
            binding.currencyName.text = item.currencyChar
            binding.currencyIcon.setCurrencyFlag(item.currencyCode)
            binding.buyingRate.text = Format.formatAmount((item.buyingRate).toString())
            binding.sellRate.text = Format.formatAmount((item.sellingRate).toString())
            binding.centralBank.text = Format.formatAmount((item.sbCourse).toString())
        }
    }
}