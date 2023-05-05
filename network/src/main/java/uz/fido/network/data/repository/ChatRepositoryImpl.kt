package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.repositories.IChatRepository
import uz.fido.network.domain.datasource.services.ChatApiInterface
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
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatApiService: ChatApiInterface) :
    IChatRepository {

    override suspend fun fetchCountUnreadMessages(token: String): Resource<UnreadMessagesResponse> =
        getResult {
            chatApiService.fetchCountUnreadMessages(token)
        }

    override suspend fun fetchUserContacts(token: String): Resource<ChatUserResponse> = getResult {
        chatApiService.fetchUserContacts(token)
    }

    override suspend fun fetchChatRoomList(token: String): Resource<RoomListResponse> = getResult {
        chatApiService.fetchChatRoomList(token)
    }

    override suspend fun createPersonalRoom(
        token: String,
        createRoomRequest: CreateRoomRequest
    ): Resource<CreatePersonalRoomResponse> =
        getResult { chatApiService.createPersonalRoom(token, createRoomRequest) }

    override suspend fun fetchChatReceiveMessages(
        token: String,
        receiveMessagesRequest: ReceiveMessagesRequest
    ): Resource<MessageHistoryResponse> =
        getResult { chatApiService.fetchChatReceiveMessages(token, receiveMessagesRequest) }


    override suspend fun editMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> = getResult { chatApiService.editMessage(token, editMessageRequest) }

    override suspend fun forwardMessage(
        token: String,
        forwardMessageRequest: ForwardMessageRequest
    ): Resource<ForwardMessageResponse> =
        getResult { chatApiService.forwardMessage(token, forwardMessageRequest) }

    override suspend fun clearRoomHistory(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> =
        getResult { chatApiService.clearRoomHistory(token, editMessageRequest) }

    override suspend fun messageTyping(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> =
        getResult { chatApiService.messageTyping(token, editMessageRequest) }

    override suspend fun removeMessage(
        token: String,
        editMessageRequest: EditMessageRequest
    ): Resource<BaseResponse> =
        getResult { chatApiService.removeMessage(token, editMessageRequest) }

    override suspend fun getUserFromContact(
        token: String,
        getUserFromContactRequest: GetUserFromContactRequest
    ): Resource<AddNewRoomResponse> =
        getResult { chatApiService.getUserFromContact(token, getUserFromContactRequest) }

    override suspend fun sendMessage(
        token: String,
        sendMessageRequest: SendMessageRequest
    ): Resource<SendMessageResponse> =
        getResult { chatApiService.sendMessage(token, sendMessageRequest) }

    override suspend fun messageOperation(
        token: String,
        messageOperationRequest: MessageOperationRequest
    ): Resource<BaseResponse> = getResult {
        chatApiService.messageOperation(token, messageOperationRequest)
    }
}