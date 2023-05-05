package uz.fido.network.domain.model.template

import java.io.Serializable

data class GetTemplateListRequest(
    val template_group_id: String
) : Serializable