package uz.fido.network.domain.model.payment.local_history

data class LocalMonitoringResponse(
    val msg: String,
    val code: Int,
    val totalPages: Int,
    val last: String,
    val local_transactions: ArrayList<LocalMonitoring>
)