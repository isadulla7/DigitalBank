package uz.fido.network.domain.model.limits

data class SvSetCardLimitRequest(
    val cycle_length: String = "1",
    val cycle_type: String,
    val end_date: String,
    val limit_amount: String,
    val limit_id: String,
    val main_object_value: String,
    val object_value: String
)