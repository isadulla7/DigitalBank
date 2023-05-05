package uz.fido.network.domain.model.template

import uz.fido.network.domain.model.payment.TemplateKeyValue
import java.io.Serializable

data class GetTemplateResponse(
    val code: Int,
    val msg: String,
    val template_details: ArrayList<TemplateKeyValue>
) : Serializable