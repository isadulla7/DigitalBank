package uz.fido.network.domain.model.payment

data class PaymentTemplate (
        var temp_id: String,
        var client_id: String,
        var contract_id: String,
        var detail_code: String,
        var icon_name: String,
        var name: String,
        var template_type: String,
        var is_personal_account: String,
        var image: Int? = null
)