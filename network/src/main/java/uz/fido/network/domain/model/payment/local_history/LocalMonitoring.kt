package uz.fido.network.domain.model.payment.local_history

import uz.fido.network.domain.model.payment.Cheque
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.search.SearchDataResponse
import java.io.Serializable
import java.math.BigDecimal

data class LocalMonitoring(
    var icon_name: String="",
    var amount: String="",
    var tran_type: String="",
    var fee_amount: String="",
    var fee_percent: String="",
    var service_id: String="",
    var terminal_id: String="",
    var name: String="",
    var request_id: String="",
    var state_id: String? = "",
    var created_date: String="",
    var to_obj_name: String="",
    var object_value: String="",
    var partner_obj: String="",
    var currency_code: String="",
    var to_embossed_name: String? = "",
    var details: ArrayList<Cheque>? = null,
    var isChecked: Boolean = false,
    var searchData: SearchDataResponse? = null
) : Serializable


data class ChartData(
    val paymentGroup: PaymentGroup? = null,
    val paymentService: PaymentService? = null,
    val serviceId: String,
    val amount: BigDecimal
)