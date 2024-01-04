package uz.fido.network.domain.model.monitoring.currency_card

import java.io.Serializable

data class CurrencyCardMonitoringItem(
    var merchant_name: String,
    var merchant_id: String,
    var ref_number: String,
    var tran_type: String,
    var terminal_id: String,
    var tran_date: String,
    var bank_code: String,
    var card_num: String,
    var currency: String,
    var tran_amount: String,
    var address: String
) : Serializable