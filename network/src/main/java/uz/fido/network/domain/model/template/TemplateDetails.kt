package uz.fido.network.domain.model.template

import java.io.Serializable

data class TemplateDetails(
    var code: String,
    var level_position: String,
    var name: String,
    var value: String,
    var values: ArrayList<String>
) : Serializable