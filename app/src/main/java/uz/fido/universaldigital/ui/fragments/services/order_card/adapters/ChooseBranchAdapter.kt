package uz.fido.universaldigital.ui.fragments.services.order_card.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemCountryBinding

class ChooseBranchAdapter(
    private val context: Context,
    private val baseInterface: BaseInterface,
    private val list: ArrayList<Branches>
) :
    RecyclerView.Adapter<ChooseBranchAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setData(country: Branches, baseInterface: BaseInterface) {

            binding.countryName.text = country.name
            binding.countryName.setOnClickListener {
                baseInterface.selectedBranch(country)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position], baseInterface)
    }
}