package uz.fido.network.domain.model.mib

import java.io.Serializable


data class MibDetails(
    val field_valueSpecified: String? = null,
    val field_keySpecified: String? = null,
    val field_value: String? = null,
    val field_key: String? = null
): Serializable