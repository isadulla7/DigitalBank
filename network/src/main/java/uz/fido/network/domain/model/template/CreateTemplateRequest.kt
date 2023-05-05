package uz.fido.network.domain.model.template

import java.io.Serializable

data class CreateTemplateRequest(
    val name: String,
    val template_type: String,
    val service_type: String,
    val service_id: String,
    val template_group_id: String,
    val payment_details: HashMap<String, String>
) : Serializable