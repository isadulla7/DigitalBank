package uz.fido.network.domain.model.monitoring.currency_card

data class CurrencyCardMonitoringResponse(
    val tranCount: String,
    val totalPages: String,
    val page_number: String,
    val last: String,
    val request_id: String,
    val code: Int,
    val numberOfElements: Int,
    val transactions: ArrayList<CurrencyCardMonitoringItem>,
    val msg: String
)