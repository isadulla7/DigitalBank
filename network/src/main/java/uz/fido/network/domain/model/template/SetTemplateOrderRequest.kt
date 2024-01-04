package uz.fido.network.domain.model.template

data class SetTemplateOrderRequest(
    val template_group_id: String,
    val template_ids: ArrayList<Int>
)