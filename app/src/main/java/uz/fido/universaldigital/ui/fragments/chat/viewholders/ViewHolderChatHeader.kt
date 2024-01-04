package uz.fido.universaldigital.ui.fragments.chat.viewholders

import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.chat.MessageHistory
import uz.fido.universaldigital.databinding.ItemMessageHeaderBinding

class ViewHolderChatHeader(private val binding: ItemMessageHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(history: MessageHistory) {
        binding.textHeader.text = history.created_date
    }
}