package uz.fido.network.domain.model.template

import java.io.Serializable

data class CreateTemplateResponse(
    val code: Int,
    val msg: String,
    val template_id: String
) : Serializable