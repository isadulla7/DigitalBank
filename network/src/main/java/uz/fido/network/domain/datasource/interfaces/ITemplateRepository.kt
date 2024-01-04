package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
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

interface ITemplateRepository {
    suspend fun createTemplate(
        token: String, createTemplateRequest: CreateTemplateRequest
    ): Resource<CreateTemplateResponse>

    suspend fun createTemplateGroup(
        token: String, createTemplateGroupRequest: CreateTemplateGroupRequest
    ): Resource<TemplateGroupResponse>

    suspend fun editTemplateGroup(
        token: String, editTemplateGroupRequest: EditTemplateGroupRequest
    ): Resource<BaseResponse>

    suspend fun getTemplateGroup(
        token: String
    ): Resource<TemplateGroupResponse>

    suspend fun getTemplateList(
        token: String, getTemplateListRequest: GetTemplateListRequest
    ): Resource<GetTemplateListResponse>

    suspend fun getTemplate(
        token: String, getTemplateRequest: GetTemplateRequest
    ): Resource<GetTemplateResponse>

    suspend fun setTemplateOrder(
        token: String, setTemplateOrderRequest: SetTemplateOrderRequest
    ): Resource<BaseResponse>

    suspend fun deleteTemplate(
        token: String, deleteTemplateRequest: DeleteTemplateRequest
    ): Resource<BaseResponse>

    suspend fun deleteTemplateGroup(
        token: String, getTemplateListRequest: GetTemplateListRequest
    ): Resource<BaseResponse>
}