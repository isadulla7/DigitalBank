package uz.fido.universaldigital.ui.fragments.profile.identification.adapters

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemCodeAndNameBinding

class CodeAndNameAdapter(
    private val details: Map<String, String>
) : RecyclerView.Adapter<CodeAndNameAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCodeAndNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>) {
            binding.layout.hint = item.first
            binding.editText.setText(item.second)
            binding.copy.isVisible = item.first == itemView.context.getString(R.string.pnfl)
            binding.copy.setOnClickListener {
                copyValue(itemView.context, item.second)
            }
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

    private fun copyValue(context: Context, value: String) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), value)
        clipboard.setPrimaryClip(clip)
    }

}