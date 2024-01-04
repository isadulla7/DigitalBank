package uz.fido.universaldigital.ui.fragments.chat

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IChatRepository
import uz.fido.network.domain.datasource.interfaces.ISocketRepository
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.ForwardMessageRequest
import uz.fido.network.domain.model.chat.GetUserFromContactRequest
import uz.fido.network.domain.model.chat.MessageOperationRequest
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.create_room.CreateRoomRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuChatViewModel @Inject constructor(
    application: Application,
    private val chatRepository: IChatRepository,
    private val socketRepository: ISocketRepository
) : AbstractViewModel(application) {

    fun fetchUserContacts(token: String) = liveData(Dispatchers.IO) {
        emit(chatRepository.fetchUserContacts(token))
    }

    fun fetchRoomList(token: String) = liveData(Dispatchers.IO) {
        emit(chatRepository.fetchChatRoomList(token))
    }

    fun fetchUnreadMsgCount(token: String) = liveData(Dispatchers.IO) {
        emit(chatRepository.fetchCountUnreadMessages(token))
    }

    fun addNewChat(token: String, getUserFromContactRequest: GetUserFromContactRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(chatRepository.getUserFromContact(token, getUserFromContactRequest))
    }

    fun receiveMessages(token: String, receiveMessagesRequest: ReceiveMessagesRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(chatRepository.fetchChatReceiveMessages(token, receiveMessagesRequest))
    }

    fun sendMessage(token: String, sendMessageRequest: SendMessageRequest) =
        liveData(Dispatchers.IO) {
            emit(chatRepository.sendMessage(token, sendMessageRequest))
        }

    fun messageOperation(token: String, messageOperationRequest: MessageOperationRequest) =
        liveData(
            Dispatchers.IO
        ) {
            emit(chatRepository.messageOperation(token, messageOperationRequest))
        }

    fun createPersonalChatRoom(token: String, createRoomRequest: CreateRoomRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(chatRepository.createPersonalRoom(token, createRoomRequest))
    }

    fun editMessage(token: String, editMessageRequest: EditMessageRequest) =
        liveData(Dispatchers.IO) {
            emit(chatRepository.editMessage(token, editMessageRequest))
        }

    fun removeMessage(token: String, editMessageRequest: EditMessageRequest) =
        liveData(Dispatchers.IO) {
            emit(chatRepository.removeMessage(token, editMessageRequest))
        }

    fun clearRoomHistory(token: String, editMessageRequest: EditMessageRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(chatRepository.clearRoomHistory(token, editMessageRequest))
    }

    fun messageTyping(token: String, editMessageRequest: EditMessageRequest) =
        liveData(Dispatchers.IO) {
            emit(chatRepository.messageTyping(token, editMessageRequest))
        }

    fun forwardMessage(token: String, forwardMessageRequest: ForwardMessageRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(chatRepository.forwardMessage(token, forwardMessageRequest))
    }

    fun testSocket(token: String, deviceId: String) = liveData(Dispatchers.IO) {
        emit(socketRepository.testSocket(token, deviceId))
    }

}