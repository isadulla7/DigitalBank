package uz.fido.universaldigital.ui.fragments.profile.saved_cheques

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemSavedChequeBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedChequesAdapter(
    private var list: List<File>,
    private val context: Context,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<SavedChequesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSavedChequeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(file: File) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            binding.fileName.text = file.name
            binding.fileModifiedDate.text = dateFormat.format(Date(file.lastModified()))
            binding.father.setOnClickListener {
                baseInterface.openCheque(file)
            }
            binding.more.setOnClickListener {
                baseInterface.chequeOperations(file)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSavedChequeBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }

}