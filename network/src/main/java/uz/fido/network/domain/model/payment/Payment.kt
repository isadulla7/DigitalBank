package uz.fido.network.domain.model.payment

import com.google.gson.annotations.Expose

data class Payment(
    @Expose
    var result: String? = null,
    @Expose
    var msg: String? = null,
    @Expose
    var service_groups: ArrayList<PaymentGroup>? = null,
    @Expose
    var service_list: ArrayList<PaymentService>? = null,
    @Expose
    var payment_details: ArrayList<PaymentParams>? = null,
    @Expose
    var references_list: ArrayList<PaymentReference>? = null,
    @Expose
    var curr_version: String? = null,
    var cashback_list: ArrayList<PaymentCashback>? = null
)