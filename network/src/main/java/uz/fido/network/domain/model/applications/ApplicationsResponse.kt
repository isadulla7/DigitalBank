package uz.fido.network.domain.model.applications

import java.io.Serializable

data class ApplicationsResponse(
    val product_list: ArrayList<OrderCardApp>?,
    val request_id: Int,
    val code: Int,
    val msg: String
) : Serializable