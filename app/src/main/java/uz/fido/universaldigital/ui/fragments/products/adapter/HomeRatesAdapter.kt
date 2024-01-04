package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.universaldigital.databinding.ItemHomeCurrencyRatesBinding
import uz.fido.universaldigital.ui.utils.extensions.setCurrencyFlag
import uz.fido.utils.utility.format.Format

class HomeRatesAdapter(private var list: ArrayList<CourseItem>) :
    RecyclerView.Adapter<HomeRatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeCurrencyRatesBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemHomeCurrencyRatesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseItem) {
            binding.currencyName.text = item.currencyChar
            binding.currencyIcon.setCurrencyFlag(item.currencyCode)
            binding.buy.text = Format.formatAmountToTiyn((item.buyingRate / 100).toString())
            binding.sell.text = Format.formatAmountToTiyn((item.sellingRate / 100).toString())
        }
    }
}