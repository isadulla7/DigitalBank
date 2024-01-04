package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.datasource.services.TemplateApiInterface
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
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(private val templateService: TemplateApiInterface) :
    ITemplateRepository {

    override suspend fun createTemplate(
        token: String,
        createTemplateRequest: CreateTemplateRequest
    ): Resource<CreateTemplateResponse> = getResult {
        templateService.createTemplate(token, createTemplateRequest)
    }

    override suspend fun createTemplateGroup(
        token: String,
        createTemplateGroupRequest: CreateTemplateGroupRequest
    ): Resource<TemplateGroupResponse> = getResult {
        templateService.createTemplateGroup(token, createTemplateGroupRequest)
    }


    override suspend fun editTemplateGroup(
        token: String,
        editTemplateGroupRequest: EditTemplateGroupRequest
    ): Resource<BaseResponse> = getResult {
        templateService.editTemplateGroup(token, editTemplateGroupRequest)
    }

    override suspend fun getTemplateGroup(token: String): Resource<TemplateGroupResponse> =
        getResult {
            templateService.getTemplateGroup(token)
        }


    override suspend fun getTemplateList(
        token: String,
        getTemplateListRequest: GetTemplateListRequest
    ): Resource<GetTemplateListResponse> = getResult {
        templateService.getTemplateList(token, getTemplateListRequest)
    }

    override suspend fun getTemplate(
        token: String,
        getTemplateRequest: GetTemplateRequest
    ): Resource<GetTemplateResponse> = getResult {
        templateService.getTemplate(token, getTemplateRequest)
    }

    override suspend fun setTemplateOrder(
        token: String,
        setTemplateOrderRequest: SetTemplateOrderRequest
    ): Resource<BaseResponse> = getResult {
        templateService.setTemplateOrder(token, setTemplateOrderRequest)
    }

    override suspend fun deleteTemplate(
        token: String,
        deleteTemplateRequest: DeleteTemplateRequest
    ): Resource<BaseResponse> = getResult {
        templateService.deleteTemplate(token, deleteTemplateRequest)
    }

    override suspend fun deleteTemplateGroup(
        token: String,
        getTemplateListRequest: GetTemplateListRequest
    ): Resource<BaseResponse> = getResult {
        templateService.deleteTemplateGroup(token, getTemplateListRequest)
    }
}