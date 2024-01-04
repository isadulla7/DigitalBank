package uz.fido.network.domain.model.payment

import java.io.Serializable

class PaymentService : Serializable {
    var nameIndex: String? = ""
    var icon_name: String? = ""
    var local_icon_name: String? = ""
    var name_ru: String? = ""
    var name_uzl: String? = ""
    var name_en: String? = ""
    var order: Int? = 0
    var service_id: Int? = 0
    var payment_detail_code: String? = ""
    var payment_type: String? = ""
    var identification_payment_method: String? = ""
    var name_uzc: String? = ""
    var max_amount: String? = ""
    var min_amount: String? = ""
    var service_group_code: String? = ""
    var pay_request_method: String? = ""
    var sms_control_limit: String? = ""
    var is_sub_group: String = "N"
    var payment_group: PaymentGroup? = null
    var paymentCashback: PaymentCashback? = null
    var group_code: String? = null

    var isSelected = false

    companion object {
        const val TABLE_NAME = "service_types"
        const val INDEX_NAME_RU = "name_ru"
        const val INDEX_NAME_UC = "name_uzc"
        const val INDEX_NAME_UL = "name_uzl"
        const val INDEX_NAME_EN = "name_en"
        const val ICON_NAME = "icon_name"
        const val ORDER = "ord"
        const val SERVICE_GROUP_CODE = "service_group_code"
        const val SERVICE_ID = "service_id"
        const val PAYMENT_DETAIL_CODE = "payment_detail_code"
        const val PAYMENT_TYPE = "payment_type"
        const val MIN_AMOUNT = "min_amount"
        const val MAX_AMOUNT = "max_amount"
        const val PAY_REQUEST_METHOD = "pay_request_method"
        const val COLUMN_IDENTIFICATION = "identification_payment_method"
        const val COLUMN_SMS_CONTROL_LIMIT = "sms_control_limit"
    }

    fun PaymentService(
        icon_name: String,
        service_id: Int,
        payment_detail_code: String,
        payment_type: String,
        order: Int,
        name_index: String,
        min_amount: String,
        max_amount: String,
        identification_payment_method: String,
        pay_request_method: String,
        sms_control_limit: String
    ): PaymentService {
        this.icon_name = icon_name
        this.service_id = service_id
        this.payment_detail_code = payment_detail_code
        this.payment_type = payment_type
        this.order = order
        this.nameIndex = name_index
        this.min_amount = min_amount
        this.max_amount = max_amount
        this.identification_payment_method = identification_payment_method
        this.pay_request_method = pay_request_method
        this.sms_control_limit = sms_control_limit
        return this
    }

    override fun toString(): String {
        return "\n${icon_name}\n" +
                "${service_id}\n" +
                "${payment_detail_code}\n" +
                "${payment_type}\n" +
                "${order}\n" +
                "${nameIndex}\n" +
                "${min_amount}\n" +
                "${max_amount}\n" +
                "${identification_payment_method}\n" +
                "${pay_request_method}\n" +
                "${sms_control_limit}\n" +
                "$service_group_code\n"
    }

}