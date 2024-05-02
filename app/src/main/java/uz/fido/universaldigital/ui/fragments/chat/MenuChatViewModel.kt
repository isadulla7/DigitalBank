package uz.fido.universaldigital.ui.fragments.chat

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IChatRepository
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuChatViewModel @Inject constructor(
    application: Application,
    private val chatRepository: IChatRepository
) : AbstractViewModel(application) {

    fun fetchRoomList(token: String) = liveData(Dispatchers.IO) {
        emit(chatRepository.fetchChatRoomList(token))
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

}