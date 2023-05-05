package uz.fido.network.domain.model.template

import java.io.Serializable

data class GetTemplateListResponse(
    val code: Int,
    val msg: String,
    val templates: ArrayList<Template>
) : Serializable