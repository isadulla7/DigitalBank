package uz.fido.network.domain.model.template

import java.io.Serializable

data class DeleteTemplateRequest(
    val template_id: String
) : Serializable