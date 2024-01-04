package uz.fido.network.domain.model.applications

import java.io.Serializable

data class ProductDetailsResponse(
    val price: String,
    val app_detail: ApplicationDetails,
    val status_list: ArrayList<ApplicationStatus>,
    val percent: String,
    val request_id: String,
    val code: String,
    val msg: String,
    val app_create_date: String,
) : Serializable