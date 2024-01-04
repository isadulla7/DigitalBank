package uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.money_transfer.receive.Country
import uz.fido.universaldigital.databinding.ItemCountryBinding

class ChooseCountryAdapter(
    private val context: Context,
    private val list: ArrayList<Country>,
    private val countryName: (Country) -> Unit
) :
    RecyclerView.Adapter<ChooseCountryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCountryBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setData(country: Country) {
            binding.countryName.text = country.name
            binding.countryName.setOnClickListener {
                countryName.invoke(country)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }
}