package uz.fido.network.domain.model.paid_services


data class PsServiceResponse(
    val ps_service_list: ArrayList<PaidService>,
    val request_id: String,
    val code: Int,
    val msg: String
)