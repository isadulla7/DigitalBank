package uz.fido.universaldigital.ui.fragments.chat.viewholders

import android.os.Handler
import android.os.Looper
import android.text.Html
import android.transition.TransitionManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.chat.MessageHistory
import uz.fido.universaldigital.databinding.ItemMessageSenderBinding
import uz.fido.universaldigital.ui.utils.extensions.setTextSpanned
import uz.fido.utils.utility.format.Format
import java.util.*

class ViewHolderMine(
    private val binding: ItemMessageSenderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(history: MessageHistory) {
        if (history.state_id.toString().lowercase(Locale.getDefault()) == "e") {
            binding.textState.visibility = View.VISIBLE
        } else {
            binding.textState.visibility = View.GONE
        }
        binding.textTime.text = Format.formatChatDate(history.created_date!!)
        binding.textMessage.setTextSpanned((Html.fromHtml(history.msg_text)))
        if (history.animate) {
            TransitionManager.beginDelayedTransition(binding.layoutRoot)
            Handler(Looper.getMainLooper()).postDelayed({
                itemView.setBackgroundResource(0)
                TransitionManager.beginDelayedTransition(binding.layoutRoot)
            }, 1000)
            history.animate = false
        }
        itemView.setOnLongClickListener {
            return@setOnLongClickListener true
        }
    }
}