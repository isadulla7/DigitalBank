package uz.fido.universaldigital.ui.fragments.profile.identification.adapters

import  android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.my_id.CodeAndName
import uz.fido.universaldigital.databinding.ItemCodeAndNameBinding

class CodeAndNameAdapter(
    private val list: ArrayList<CodeAndName>
) : RecyclerView.Adapter<CodeAndNameAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCodeAndNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CodeAndName) {
            binding.layout.hint = item.name
            binding.editText.setText(item.value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCodeAndNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}