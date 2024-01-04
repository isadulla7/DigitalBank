package uz.fido.network.domain.model.template

import uz.fido.network.domain.model.my_house.MyHouseGroup
import java.io.Serializable

data class TemplateGroupResponse(
    val code: Int,
    val msg: String,
    val template_groups: ArrayList<MyHouseGroup>,
    val template_group_id: String? = null
) : Serializable