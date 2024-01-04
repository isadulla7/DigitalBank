package uz.fido.universaldigital.ui.fragments.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.chat.MessageHistory
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMessageHeaderBinding
import uz.fido.universaldigital.databinding.ItemMessageReceiverBinding
import uz.fido.universaldigital.databinding.ItemMessageSenderBinding
import uz.fido.universaldigital.ui.fragments.chat.MenuChatFragment
import uz.fido.universaldigital.ui.fragments.chat.viewholders.ViewHolderChatHeader
import uz.fido.universaldigital.ui.fragments.chat.viewholders.ViewHolderMine
import uz.fido.universaldigital.ui.fragments.chat.viewholders.ViewHolderReceiver

class ChatAdapter(
    private val baseInterface: BaseInterface,
    private val list: ArrayList<MessageHistory>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0

        //message
        const val TYPE_MESSAGE_MINE = 1
        const val TYPE_MESSAGE_RECEIVER = 11
        const val TYPE_MESSAGE_MINE_REPLY = 12
        const val TYPE_MESSAGE_MINE_FORWARD = 13
        const val TYPE_MESSAGE_RECEIVER_REPLY = 14
        const val TYPE_MESSAGE_RECEIVER_FORWARD = 15

        //image
        const val TYPE_IMAGE_MINE = 2
        const val TYPE_IMAGE_RECEIVER = 21
        const val TYPE_IMAGE_MINE_REPLY = 22
        const val TYPE_IMAGE_MINE_FORWARD = 23
        const val TYPE_IMAGE_RECEIVER_REPLY = 24
        const val TYPE_IMAGE_RECEIVER_FORWARD = 25

    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    fun updateChatListItems(chats: ArrayList<MessageHistory>) {
        val diffCallback = ChatDiffCallback(list, chats)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(chats)
        scope.launch(Dispatchers.Main) {
            diffResult.dispatchUpdatesTo(this@ChatAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemMessageHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderChatHeader(binding)
            }

            TYPE_MESSAGE_MINE -> {
                val binding = ItemMessageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderMine(binding, baseInterface)
            }

            TYPE_MESSAGE_RECEIVER -> {
                val binding = ItemMessageReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderReceiver(binding, baseInterface)
            }

            else -> {
                val binding = ItemMessageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderMine(binding, baseInterface)
            }
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderChatHeader -> {
                holder.bind(list[position])
            }

            is ViewHolderReceiver -> {
                holder.bind(list[position])
            }

            is ViewHolderMine -> {
                holder.bind(list[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (list[position].is_header) {
            TYPE_HEADER
        } else {
            when (list[position].msg_type_id!!) {
                MenuChatFragment.MessageTypes.TYPE_MESSAGE.typeId -> {
                    return if (list[position].is_mine) {
                        if (item.ref_user_id == null || item.ref_user_id == "-1") {
                            if (item.is_forwarded == "Y") {
                                TYPE_MESSAGE_MINE_FORWARD
                            } else {
                                TYPE_MESSAGE_MINE
                            }
                        } else {
                            TYPE_MESSAGE_MINE_REPLY
                        }
                    } else {
                        if (item.ref_user_id == null || item.ref_user_id == "-1") {
                            if (item.is_forwarded == "Y") {
                                TYPE_MESSAGE_RECEIVER_FORWARD
                            } else {
                                TYPE_MESSAGE_RECEIVER
                            }
                        } else {
                            TYPE_MESSAGE_RECEIVER_REPLY
                        }
                    }
                }

                MenuChatFragment.MessageTypes.TYPE_IMAGE.typeId -> {
                    return if (list[position].is_mine) {
                        if (item.ref_user_id == null || item.ref_user_id == "-1") {
                            if (item.is_forwarded == "Y") {
                                TYPE_IMAGE_MINE_FORWARD
                            } else {
                                TYPE_IMAGE_MINE
                            }
                        } else {
                            TYPE_IMAGE_MINE_REPLY
                        }
                    } else {
                        if (item.ref_user_id == null || item.ref_user_id == "-1") {
                            if (item.is_forwarded == "Y") {
                                TYPE_IMAGE_RECEIVER_FORWARD
                            } else {
                                TYPE_IMAGE_RECEIVER
                            }
                        } else {
                            TYPE_IMAGE_RECEIVER_REPLY
                        }
                    }
                }

                else -> {
                    return list[position].msg_type_id!!
                }
            }
        }
    }

}