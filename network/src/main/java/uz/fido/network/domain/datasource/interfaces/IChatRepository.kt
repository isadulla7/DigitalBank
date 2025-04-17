package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.MessageHistoryResponse
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.RoomListResponse
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.SendMessageResponse

interface IChatRepository {

    suspend fun fetchChatRoomList(token: String): Resource<RoomListResponse>

    suspend fun fetchChatReceiveMessages(
        token: String,
        receiveMessagesRequest: ReceiveMessagesRequest
    ): Resource<MessageHistoryResponse>

    suspend fun editMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun clearRoomHistory(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun removeMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun sendMessage(
        token: String,
        sendMessageRequest: SendMessageRequest
    ): Resource<SendMessageResponse>

}