package uz.fido.network.domain.model.template

import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import java.io.Serializable

data class Template(
    var account: String? = null,
    var account_text: String? = null,
    var amount: String? = null,
    var balance: String? = null,
    var icon_name: String? = null,
    var name: String? = null,
    var service_id: String? = null,
    var service_type: String? = null,
    var template_id: String = "",
    var template_type: String? = null,
    var service_group_name: String? = null,
    var service_group_code: String? = null,
    var service_group_id: String? = null,
    var template_response: GetTemplateResponse? = null,
    var payment_success: Boolean? = false,
    var payment_service: PaymentService? = null,
    var payment_params: ArrayList<PaymentParams>? = null,
    var error_text: String? = null,
    var ord: Int? = 0,
    var service_state: String? = null,
    var min_amount: String? = null,
    var max_amount: String? = null,
    var isCurrent: Boolean = false,
    var check_amount:Boolean=false
) : Serializable