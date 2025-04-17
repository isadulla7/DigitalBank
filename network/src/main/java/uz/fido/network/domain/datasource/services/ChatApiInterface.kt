package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.chat.EditMessageRequest
import uz.fido.network.domain.model.chat.MessageHistoryResponse
import uz.fido.network.domain.model.chat.ReceiveMessagesRequest
import uz.fido.network.domain.model.chat.RoomListResponse
import uz.fido.network.domain.model.chat.SendMessageRequest
import uz.fido.network.domain.model.chat.SendMessageResponse

interface ChatApiInterface {

    @GET("Get_User_Rooms")
    suspend fun fetchChatRoomList(
        @Header("Authorization") token: String
    ): RoomListResponse

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

    @POST("Clear_Room_History")
    suspend fun clearRoomHistory(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("Delete_Message")
    suspend fun removeMessage(
        @Header("Authorization") token: String,
        @Body editMessageRequest: EditMessageRequest
    ): BaseResponse

    @POST("Send_Message")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body sendMessageRequest: SendMessageRequest
    ): SendMessageResponse

}