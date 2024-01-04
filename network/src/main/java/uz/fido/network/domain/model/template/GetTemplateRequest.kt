package uz.fido.network.domain.model.template

import java.io.Serializable

data class GetTemplateRequest(
    val template_id: String,
    val is_hashmap: String
) : Serializable