package uz.fido.network.domain.model.payment

import java.io.Serializable

data class TemplateKeyValue(
        var code: String? = null,
        var name: String? = null,
        var value: String? = null,
        var level_position: String? = null,
        val is_visible: String? = null
) : Serializable
