package uz.fido.network.domain.model.money_transfer.receive

import java.io.Serializable

data class RemittanceType(
    val id: Int? = null,
    val nameTransfer: String,
    var foreignCode: Int,
    val descript: String,
    val modeType: String,
    var country_code: String? = null,
    var city: String? = null,
    var first_name: String? = null,
    var second_name: String? = null,
    var patronymic: String? = null,
    var control_number: String? = null,
    var country_name: String? = null,
    var phone: String? = null,
    var order: Int? = 0
) : Serializable