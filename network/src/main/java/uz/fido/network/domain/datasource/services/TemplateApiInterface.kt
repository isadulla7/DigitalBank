package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.template.CreateTemplateGroupRequest
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.CreateTemplateResponse
import uz.fido.network.domain.model.template.DeleteTemplateRequest
import uz.fido.network.domain.model.template.EditTemplateGroupRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateListResponse
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateResponse
import uz.fido.network.domain.model.template.SetTemplateOrderRequest
import uz.fido.network.domain.model.template.TemplateGroupResponse

interface TemplateApiInterface {
    @POST("CREATE_TEMPLATE")
    suspend fun createTemplate(
        @Header("Authorization") token: String,
        @Body createTemplateRequest: CreateTemplateRequest
    ): CreateTemplateResponse

    @POST("CREATE_TEMPLATE_GROUP")
    suspend fun createTemplateGroup(
        @Header("Authorization") token: String,
        @Body createTemplateGroupRequest: CreateTemplateGroupRequest
    ): TemplateGroupResponse

    @POST("EDIT_TEMPLATE_GROUP")
    suspend fun editTemplateGroup(
        @Header("Authorization") token: String,
        @Body editTemplateGroupRequest: EditTemplateGroupRequest
    ): BaseResponse

    @GET("GET_TEMPLATE_GROUPS")
    suspend fun getTemplateGroup(
        @Header("Authorization") token: String
    ): TemplateGroupResponse

    @POST("GET_TEMPLATE_LIST")
    suspend fun getTemplateList(
        @Header("Authorization") token: String,
        @Body getTemplateListRequest: GetTemplateListRequest
    ): GetTemplateListResponse

    @POST("GET_TEMPLATE")
    suspend fun getTemplate(
        @Header("Authorization") token: String,
        @Body getTemplateRequest: GetTemplateRequest
    ): GetTemplateResponse

    @POST("SET_TEMPLATE_ORDERS")
    suspend fun setTemplateOrder(
        @Header("Authorization") token: String,
        @Body setTemplateOrderRequest: SetTemplateOrderRequest
    ): BaseResponse

    @POST("DELETE_TEMPLATE")
    suspend fun deleteTemplate(
        @Header("Authorization") token: String,
        @Body deleteTemplateRequest: DeleteTemplateRequest
    ): BaseResponse

    @POST("DELETE_GROUP_TEMPLATE")
    suspend fun deleteTemplateGroup(
        @Header("Authorization") token: String,
        @Body getTemplateListRequest: GetTemplateListRequest
    ): BaseResponse
}