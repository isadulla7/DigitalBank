package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IChatRepository
import uz.fido.network.domain.datasource.services.ChatApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.MessageHistoryResponse
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.RoomListResponse
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.SendMessageResponse
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatApiService: ChatApiInterface) :
    IChatRepository {

    override suspend fun fetchChatRoomList(token: String): Resource<RoomListResponse> = getResult {
        chatApiService.fetchChatRoomList(token)
    }

    override suspend fun fetchChatReceiveMessages(
        token: String,
        receiveMessagesRequest: ReceiveMessagesRequest
    ): Resource<MessageHistoryResponse> = getResult { chatApiService.fetchChatReceiveMessages(token, receiveMessagesRequest) }


    override suspend fun editMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> = getResult { chatApiService.editMessage(token, editMessageRequest) }

    override suspend fun clearRoomHistory(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> = getResult { chatApiService.clearRoomHistory(token, editMessageRequest) }

    override suspend fun removeMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> = getResult { chatApiService.removeMessage(token, editMessageRequest) }

    override suspend fun sendMessage(
        token: String,
        sendMessageRequest: SendMessageRequest
    ): Resource<SendMessageResponse> = getResult { chatApiService.sendMessage(token, sendMessageRequest) }

}