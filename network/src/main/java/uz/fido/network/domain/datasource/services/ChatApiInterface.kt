package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.chat.*
import uz.fido.network.domain.model.chat.create_room.CreatePersonalRoomResponse
import uz.fido.network.domain.model.chat.create_room.CreateRoomRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse

interface ChatApiInterface {
    @GET("CHAT_GET_COUNT_UNREAD_MESSAGES")
    suspend fun fetchCountUnreadMessages(
        @Header("Authorization") token: String
    ): UnreadMessagesResponse

    @GET("Get_User_Contacts")
    suspend fun fetchUserContacts(
        @Header("Authorization") token: String
    ): ChatUserResponse

    @GET("Get_User_Rooms")
    suspend fun fetchChatRoomList(
        @Header("Authorization") token: String
    ): RoomListResponse

    @POST("Create_Personal_Room")
    suspend fun createPersonalRoom(
        @Header("Authorization") token: String,
        @Body createRoomRequest: CreateRoomRequest
    ): CreatePersonalRoomResponse

    @POST("Get_Users_Room_Messages")
    suspend fun fetchChatReceiveMessages(
        @Header("Authorization") token: String,
        @Body receiveMessagesRequest: ReceiveMessagesRequest
    ): MessageHistoryResponse

    @POST("Edit_Message")
    suspend fun editMessage(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("Forward_Message")
    suspend fun forwardMessage(
        @Header("Authorization") token: String,
        @Body forwardMessageRequest: ForwardMessageRequest
    ): ForwardMessageResponse

    @POST("Clear_Room_History")
    suspend fun clearRoomHistory(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("Typing")
    suspend fun messageTyping(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("Delete_Message")
    suspend fun removeMessage(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("CHAT_GET_USER_FROM_CONTACT")
    suspend fun getUserFromContact(
        @Header("Authorization") token: String,
        @Body getUserFromContactRequest: GetUserFromContactRequest
    ): AddNewRoomResponse

    @POST("Send_Message")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body sendMessageRequest: SendMessageRequest
    ): SendMessageResponse

    @POST("CHAT_EDIT_MESSAGES")
    suspend fun messageOperation(
        @Header("Authorization") token: String,
        @Body messageOperationRequest: MessageOperationRequest
    ): BaseResponse
}