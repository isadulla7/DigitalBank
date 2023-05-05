package uz.fido.network.domain.model.customer

import java.io.Serializable

data class RatingsResponse(
    val user_rating: ArrayList<UserRating>,
    val curr_user_score: String,
    val totalPages: Int,
    val last: String,
    val request_id: String,
    val code: String,
    val curr_user_position: Int,
    val msg: String,
) : Serializable