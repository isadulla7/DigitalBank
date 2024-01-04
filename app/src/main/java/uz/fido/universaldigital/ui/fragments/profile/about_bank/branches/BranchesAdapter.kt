package uz.fido.universaldigital.ui.fragments.profile.about_bank.branches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemBranchBinding
import kotlin.math.roundToInt

class BranchesAdapter(
    private val baseInterface: BaseInterface
) : ListAdapter<Branches, BranchesAdapter.ViewHolder>(BranchesDiffCallback()) {

    var location: LatLng? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemBranchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Branches) {
            binding.name.text = item.name.takeIf { !it.isNullOrEmpty() }
            binding.address.text = item.address.takeIf { !it.isNullOrEmpty() }
            binding.workingTime.text = item.working_time.toString().ifEmpty { "--:--" }

            if (item.x_coordinate.toString().isNotEmpty() &&
                item.y_coordinate.toString().isNotEmpty() &&
                !item.x_coordinate!!.contains(",") &&
                !item.y_coordinate!!.contains(",")
            ) {
                val x = java.lang.Double.parseDouble(item.x_coordinate.toString())
                val y = java.lang.Double.parseDouble(item.y_coordinate.toString())
                location = LatLng(x, y)
                if (item.filial_type == "B") {
                    binding.icon.setImageResource(R.drawable.ic_branch_type_atm)
                } else {
                    binding.icon.setImageResource(R.drawable.ic_branch_type_branch)
                }

            }
            try {
                if (location != null && MainBranchesFragment.currentLatLng != null) {
                    val location2 = MainBranchesFragment.currentLatLng
                    val distance =
                        (SphericalUtil.computeDistanceBetween(location2, location) / 1000)
                    val formattedDistance = (distance * 100).roundToInt() / 100.0
                    binding.distance.text = "$formattedDistance km"
                    item.distance = formattedDistance.toString()
                }
            } catch (e: Exception) {
                binding.distance.visibility = View.GONE
            }
            binding.father.setOnClickListener {
                baseInterface.openBranchDetails(item, this)
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