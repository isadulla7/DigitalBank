package uz.fido.network.domain.model.template

import java.io.Serializable

data class EditTemplateGroupRequest(
    val template_group_id: String,
    val name: String,
    var order: String
) : Serializable