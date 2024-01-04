package uz.fido.network.domain.model.money_transfer.receive

import java.io.Serializable

data class MoneyTransferParamsResponse(
    val data: ArrayList<RemittanceType> = ArrayList(),
    var request_id: Int,
    val code: Int,
    val countries: ArrayList<Country>,
    val msg: String,
    var remittance_type: RemittanceType? = null
) : Serializable