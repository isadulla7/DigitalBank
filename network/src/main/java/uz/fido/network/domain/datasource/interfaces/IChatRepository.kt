package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.chat.AddNewRoomResponse
import uz.fido.network.domain.model.chat.ChatUserResponse
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.ForwardMessageRequest
import uz.fido.network.domain.model.chat.ForwardMessageResponse
import uz.fido.network.domain.model.chat.GetUserFromContactRequest
import uz.fido.network.domain.model.chat.MessageHistoryResponse
import uz.fido.network.domain.model.chat.MessageOperationRequest
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.RoomListResponse
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.SendMessageResponse
import uz.fido.network.domain.model.chat.UnreadMessagesResponse
import uz.fido.network.domain.model.chat.create_room.CreatePersonalRoomResponse
import uz.fido.network.domain.model.chat.create_room.CreateRoomRequest

interface IChatRepository {

    suspend fun fetchCountUnreadMessages(token: String): Resource<UnreadMessagesResponse>

    suspend fun fetchUserContacts(token: String): Resource<ChatUserResponse>

    suspend fun fetchChatRoomList(token: String): Resource<RoomListResponse>

    suspend fun createPersonalRoom(
        token: String,
        createRoomRequest: CreateRoomRequest
    ): Resource<CreatePersonalRoomResponse>

    suspend fun fetchChatReceiveMessages(
        token: String,
        receiveMessagesRequest: ReceiveMessagesRequest
    ): Resource<MessageHistoryResponse>

    suspend fun editMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun forwardMessage(
        token: String,
        forwardMessageRequest: ForwardMessageRequest
    ): Resource<ForwardMessageResponse>

    suspend fun clearRoomHistory(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun messageTyping(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun removeMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse>

    suspend fun getUserFromContact(
        token: String,
        getUserFromContactRequest: GetUserFromContactRequest
    ): Resource<AddNewRoomResponse>

    suspend fun sendMessage(
        token: String,
        sendMessageRequest: SendMessageRequest
    ): Resource<SendMessageResponse>

    suspend fun messageOperation(
        token: String,
        messageOperationRequest: MessageOperationRequest
    ): Resource<BaseResponse>
}