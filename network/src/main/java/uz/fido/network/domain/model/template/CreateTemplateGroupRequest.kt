package uz.fido.network.domain.model.template

import java.io.Serializable

data class CreateTemplateGroupRequest(
    val name: String
) : Serializable