package uz.fido.universaldigital.ui.fragments.profile.identification.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.databinding.ItemCodeAndNameBinding

class CodeAndNameAdapter(
    private val details: Map<String, String>
) : RecyclerView.Adapter<CodeAndNameAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCodeAndNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>) {
            binding.layout.hint = item.first
            binding.editText.setText(item.second)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCodeAndNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(details.toList()[position])
    }

    override fun getItemCount(): Int {
        return details.size
    }
}