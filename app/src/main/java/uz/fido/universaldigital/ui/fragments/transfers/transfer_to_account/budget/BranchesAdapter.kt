package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.databinding.ItemBankMfoBinding

class BranchMfoAdapter(
    private val onClickListener: (String, String) -> Unit
) : ListAdapter<Branches, BranchMfoAdapter.ViewHolder>(BranchesDiffCallback()) {

    var location: LatLng? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBankMfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemBankMfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Branches) {
            binding.bankMfo.text = item.filial_code
            binding.bankName.text = item.name
            binding.father.setOnClickListener {
                onClickListener.invoke(item.filial_code.toString(), item.name.toString())
            }
        }
    }

    private class BranchesDiffCallback : DiffUtil.ItemCallback<Branches>() {
        override fun areItemsTheSame(oldItem: Branches, newItem: Branches): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Branches, newItem: Branches): Boolean =
            oldItem.name == newItem.name
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}