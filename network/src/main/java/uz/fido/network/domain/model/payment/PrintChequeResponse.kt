package uz.fido.network.domain.model.payment

import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import java.io.Serializable

data class PrintChequeResponse(
    val details: ArrayList<Cheque>,
    val code: Int,
    val msg: String,
    var monitoring_info: LocalMonitoring? = null,
    var html: String? = null
) : Serializable