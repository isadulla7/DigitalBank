package uz.fido.universaldigital.ui.fragments.chat.adapter

import androidx.recyclerview.widget.DiffUtil
import uz.fido.network.domain.model.chat.MessageHistory

class ChatDiffCallback(
    private val mOldChatList: ArrayList<MessageHistory>,
    private val mNewChatList: ArrayList<MessageHistory>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldChatList.size
    }

    override fun getNewListSize(): Int {
        return mNewChatList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldChatList[oldItemPosition].msg_id == mNewChatList[newItemPosition].msg_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChat = mOldChatList[oldItemPosition]
        val newChat = mNewChatList[newItemPosition]
        return oldChat.msg_text.equals(newChat.msg_text)
    }

}